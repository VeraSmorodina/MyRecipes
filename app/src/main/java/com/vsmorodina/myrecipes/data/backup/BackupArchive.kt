package com.vsmorodina.myrecipes.data.backup

import com.vsmorodina.myrecipes.data.entity.CategoryEntity
import com.vsmorodina.myrecipes.data.entity.CategoryType
import com.vsmorodina.myrecipes.data.entity.RecipeEntity
import com.vsmorodina.myrecipes.domain.entity.BackupSummary
import com.vsmorodina.myrecipes.domain.entity.InvalidBackupException
import com.vsmorodina.myrecipes.domain.entity.InvalidBackupException.Reason
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Резервная копия — ZIP-архив:
 * - data.json — категории и рецепты; вместо абсолютного пути к фото хранится имя файла внутри архива;
 * - photos/ — фотографии категорий и рецептов.
 *
 * id категорий и рецептов сохраняются как есть, поэтому связи рецепт → категория не пересчитываются.
 */
object BackupArchive {
    const val FORMAT_VERSION = 1

    private const val FORMAT = "myrecipes-backup"
    private const val DATA_ENTRY = "data.json"
    private const val PHOTOS_DIR = "photos/"
    private const val MAX_DATA_SIZE = 20L * 1024 * 1024

    class ImportedBackup(
        val categories: List<CategoryEntity>,
        val recipes: List<RecipeEntity>,
        // Фото, скопированные в папку приложения; на них ссылаются photoUri категорий и рецептов
        val photoFiles: List<File>
    ) {
        val summary get() = BackupSummary(categories.size, recipes.size, photoFiles.size)
    }

    /** Пишет архив в [output]. Фото, файлов которых уже нет на диске, сохраняются как «без фото». */
    fun write(
        output: OutputStream,
        categories: List<CategoryEntity>,
        recipes: List<RecipeEntity>
    ): BackupSummary {
        val photoEntries = LinkedHashMap<String, String>() // путь к файлу -> имя в архиве

        fun photoEntryFor(path: String): String? {
            if (path.isBlank() || !File(path).isFile) return null
            return photoEntries.getOrPut(path) {
                "$PHOTOS_DIR${photoEntries.size + 1}.${extensionOf(path)}"
            }
        }

        val data = JSONObject()
            .put("format", FORMAT)
            .put("version", FORMAT_VERSION)
            .put("exportedAt", System.currentTimeMillis())
            .put("categories", JSONArray().apply {
                categories.forEach { put(it.toJson(photoEntryFor(it.photoUri))) }
            })
            .put("recipes", JSONArray().apply {
                recipes.forEach { put(it.toJson(photoEntryFor(it.photoUri))) }
            })

        val zip = ZipOutputStream(output.buffered())
        zip.putNextEntry(ZipEntry(DATA_ENTRY))
        zip.write(data.toString(2).toByteArray(Charsets.UTF_8))
        zip.closeEntry()
        photoEntries.forEach { (path, entryName) ->
            zip.putNextEntry(ZipEntry(entryName))
            File(path).inputStream().use { it.copyTo(zip) }
            zip.closeEntry()
        }
        zip.finish()
        zip.flush()

        return BackupSummary(categories.size, recipes.size, photoEntries.size)
    }

    /**
     * Читает архив из [input] и копирует фото в [photosDir].
     * Если архив некорректен, бросает [InvalidBackupException] и удаляет уже скопированные фото.
     */
    fun read(input: InputStream, photosDir: File): ImportedBackup {
        val extractedPhotos = HashMap<String, File>() // имя в архиве -> скопированный файл
        try {
            var dataJson: String? = null
            val zip = ZipInputStream(input.buffered())
            while (true) {
                val entry = zip.nextEntry ?: break
                when {
                    entry.isDirectory -> Unit
                    entry.name == DATA_ENTRY -> dataJson = readText(zip)
                    entry.name.startsWith(PHOTOS_DIR) && entry.name !in extractedPhotos ->
                        extractedPhotos[entry.name] = extractPhoto(zip, entry.name, photosDir)
                }
            }

            val backup = parse(
                dataJson ?: throw InvalidBackupException(Reason.NOT_A_BACKUP),
                extractedPhotos
            )

            // Фото, на которые ничего не ссылается, не оставляем
            val usedPhotos = backup.photoFiles.toSet()
            extractedPhotos.values.filter { it !in usedPhotos }.forEach { it.delete() }
            return backup
        } catch (e: Throwable) {
            extractedPhotos.values.forEach { it.delete() }
            throw when (e) {
                is ZipException -> InvalidBackupException(Reason.CORRUPTED, e)
                else -> e
            }
        }
    }

    private fun parse(dataJson: String, photos: Map<String, File>): ImportedBackup {
        val root = try {
            JSONObject(dataJson)
        } catch (e: JSONException) {
            throw InvalidBackupException(Reason.NOT_A_BACKUP, e)
        }
        if (root.optString("format") != FORMAT) throw InvalidBackupException(Reason.NOT_A_BACKUP)

        val version = root.optInt("version", 0)
        if (version > FORMAT_VERSION) throw InvalidBackupException(Reason.UNSUPPORTED_VERSION)
        if (version < 1) throw InvalidBackupException(Reason.CORRUPTED)

        val usedPhotos = LinkedHashSet<File>()
        fun photoPath(json: JSONObject): String {
            val file = json.optStringOrNull("photo")?.let { photos[it] } ?: return ""
            usedPhotos.add(file)
            return file.absolutePath
        }

        val categories: List<CategoryEntity>
        val recipes: List<RecipeEntity>
        try {
            categories = root.getJSONArray("categories").objects().map {
                CategoryEntity(
                    id = it.getLong("id"),
                    name = it.getString("name"),
                    photoUri = photoPath(it),
                    isDefault = it.optBoolean("isDefault", false),
                    type = CategoryType.entries.firstOrNull { type -> type.name == it.optString("type") }
                        ?: CategoryType.NONE
                )
            }
            recipes = root.getJSONArray("recipes").objects().map {
                RecipeEntity(
                    id = it.getLong("id"),
                    categoryId = it.getLong("categoryId"),
                    name = it.getString("name"),
                    ingredients = it.optString("ingredients", ""),
                    cookingAlgorithm = it.optString("cookingAlgorithm", ""),
                    photoUri = photoPath(it),
                    isFavorite = it.optBoolean("isFavorite", false)
                )
            }
        } catch (e: JSONException) {
            throw InvalidBackupException(Reason.CORRUPTED, e)
        }

        validate(categories, recipes)
        return ImportedBackup(categories, recipes, usedPhotos.toList())
    }

    private fun validate(categories: List<CategoryEntity>, recipes: List<RecipeEntity>) {
        val categoryIds = HashSet<Long>()
        categories.forEach {
            if (it.id <= 0 || !categoryIds.add(it.id)) throw InvalidBackupException(Reason.CORRUPTED)
        }
        val recipeIds = HashSet<Long>()
        recipes.forEach {
            if (it.id <= 0 || !recipeIds.add(it.id) || it.categoryId !in categoryIds) {
                throw InvalidBackupException(Reason.CORRUPTED)
            }
        }
    }

    private fun CategoryEntity.toJson(photoEntry: String?) = JSONObject()
        .put("id", id)
        .put("name", name)
        .put("isDefault", isDefault)
        .put("type", type.name)
        .putOpt("photo", photoEntry)

    private fun RecipeEntity.toJson(photoEntry: String?) = JSONObject()
        .put("id", id)
        .put("categoryId", categoryId)
        .put("name", name)
        .put("ingredients", ingredients)
        .put("cookingAlgorithm", cookingAlgorithm)
        .put("isFavorite", isFavorite)
        .putOpt("photo", photoEntry)

    private fun JSONArray.objects() = (0 until length()).map { getJSONObject(it) }

    // optString у org.json возвращает строку "null" для JSON null, поэтому проверяем явно
    private fun JSONObject.optStringOrNull(name: String) =
        if (has(name) && !isNull(name)) getString(name) else null

    private fun readText(zip: ZipInputStream): String {
        val buffer = ByteArrayOutputStream()
        val chunk = ByteArray(DEFAULT_BUFFER_SIZE)
        var total = 0L
        while (true) {
            val read = zip.read(chunk)
            if (read < 0) break
            total += read
            if (total > MAX_DATA_SIZE) throw InvalidBackupException(Reason.CORRUPTED)
            buffer.write(chunk, 0, read)
        }
        return String(buffer.toByteArray(), Charsets.UTF_8)
    }

    // Имя файла генерируется заново, имя из архива в путь не попадает
    private fun extractPhoto(zip: ZipInputStream, entryName: String, photosDir: File): File {
        val file = File.createTempFile("image_", ".${extensionOf(entryName)}", photosDir)
        file.outputStream().use { zip.copyTo(it) }
        return file
    }

    private fun extensionOf(name: String): String {
        val extension = name.substringAfterLast('/').substringAfterLast('.', "").lowercase()
        val isValid = extension.length in 1..5 && extension.all { it in 'a'..'z' || it in '0'..'9' }
        return if (isValid) extension else "jpg"
    }
}
