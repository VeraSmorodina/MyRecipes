package com.vsmorodina.myrecipes.data.backup

import com.vsmorodina.myrecipes.data.entity.CategoryEntity
import com.vsmorodina.myrecipes.data.entity.CategoryType
import com.vsmorodina.myrecipes.data.entity.RecipeEntity
import com.vsmorodina.myrecipes.domain.entity.BackupSummary
import com.vsmorodina.myrecipes.domain.entity.InvalidBackupException
import com.vsmorodina.myrecipes.domain.entity.InvalidBackupException.Reason
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class BackupArchiveTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var sourceDir: File
    private lateinit var targetDir: File

    @Before
    fun setUp() {
        sourceDir = tempFolder.newFolder("source")
        targetDir = tempFolder.newFolder("target")
    }

    @Test
    fun roundTrip_restoresCategoriesRecipesAndPhotos() {
        val categoryPhoto = photo("image_1.jpg", byteArrayOf(1, 2, 3))
        val recipePhoto = photo("image_2.png", byteArrayOf(4, 5, 6, 7))
        val categories = listOf(
            CategoryEntity(id = 1, name = "Супы", photoUri = "", isDefault = true, type = CategoryType.SOUPS),
            CategoryEntity(id = 5, name = "Моя категория", photoUri = categoryPhoto.absolutePath)
        )
        val recipes = listOf(
            RecipeEntity(
                id = 10, categoryId = 1, name = "Борщ", ingredients = "Свёкла\nКапуста",
                cookingAlgorithm = "Варить \"долго\"", photoUri = recipePhoto.absolutePath, isFavorite = true
            ),
            RecipeEntity(
                id = 11, categoryId = 5, name = "Без фото", ingredients = "",
                cookingAlgorithm = "", photoUri = ""
            )
        )

        val (archive, exportSummary) = export(categories, recipes)
        val imported = BackupArchive.read(ByteArrayInputStream(archive), targetDir)

        assertEquals(BackupSummary(2, 2, 2), exportSummary)
        assertEquals(exportSummary, imported.summary)
        assertEquals(categories.map { it.copy(photoUri = "") }, imported.categories.map { it.copy(photoUri = "") })
        assertEquals(recipes.map { it.copy(photoUri = "") }, imported.recipes.map { it.copy(photoUri = "") })

        assertEquals("", imported.categories[0].photoUri)
        assertPhoto(targetDir, categoryPhoto, imported.categories[1].photoUri)
        assertPhoto(targetDir, recipePhoto, imported.recipes[0].photoUri)
        assertEquals("", imported.recipes[1].photoUri)
        assertTrue(imported.recipes[0].photoUri.endsWith(".png"))
    }

    @Test
    fun sharedPhoto_isStoredOnce() {
        val sharedPhoto = photo("image_1.jpg", byteArrayOf(9))
        val categories = listOf(CategoryEntity(id = 1, name = "К", photoUri = sharedPhoto.absolutePath))
        val recipes = listOf(
            RecipeEntity(id = 1, categoryId = 1, name = "Р", ingredients = "", cookingAlgorithm = "",
                photoUri = sharedPhoto.absolutePath)
        )

        val (archive, summary) = export(categories, recipes)
        val imported = BackupArchive.read(ByteArrayInputStream(archive), targetDir)

        assertEquals(1, summary.photosCount)
        assertEquals(imported.categories[0].photoUri, imported.recipes[0].photoUri)
        assertEquals(1, targetDir.listFiles()!!.size)
    }

    @Test
    fun missingPhotoFile_isExportedWithoutPhoto() {
        val categories = listOf(
            CategoryEntity(id = 1, name = "К", photoUri = File(sourceDir, "deleted.jpg").absolutePath)
        )

        val (archive, summary) = export(categories, emptyList())
        val imported = BackupArchive.read(ByteArrayInputStream(archive), targetDir)

        assertEquals(0, summary.photosCount)
        assertEquals("", imported.categories[0].photoUri)
    }

    @Test
    fun emptyDatabase_roundTrips() {
        val (archive, summary) = export(emptyList(), emptyList())
        val imported = BackupArchive.read(ByteArrayInputStream(archive), targetDir)

        assertEquals(BackupSummary(0, 0, 0), summary)
        assertTrue(imported.categories.isEmpty())
        assertTrue(imported.recipes.isEmpty())
    }

    @Test
    fun notZipFile_isRejected() {
        assertRejected(Reason.NOT_A_BACKUP, "просто текст".toByteArray())
    }

    @Test
    fun zipWithoutData_isRejected() {
        assertRejected(Reason.NOT_A_BACKUP, zipOf("photos/1.jpg" to byteArrayOf(1)))
        assertTrue("скопированные фото должны удаляться", targetDir.listFiles()!!.isEmpty())
    }

    @Test
    fun foreignJson_isRejected() {
        assertRejected(Reason.NOT_A_BACKUP, zipOf("data.json" to """{"hello": "world"}""".toByteArray()))
    }

    @Test
    fun newerFormatVersion_isRejected() {
        assertRejected(Reason.UNSUPPORTED_VERSION, zipOf("data.json" to data(version = 2)))
    }

    @Test
    fun recipeWithUnknownCategory_isRejectedAndPhotosCleanedUp() {
        val json = data(
            categories = """[{"id": 1, "name": "К"}]""",
            recipes = """[{"id": 1, "categoryId": 2, "name": "Р", "photo": "photos/1.jpg"}]"""
        )
        assertRejected(Reason.CORRUPTED, zipOf("photos/1.jpg" to byteArrayOf(1), "data.json" to json))
        assertTrue(targetDir.listFiles()!!.isEmpty())
    }

    @Test
    fun duplicateIds_areRejected() {
        val json = data(categories = """[{"id": 1, "name": "А"}, {"id": 1, "name": "Б"}]""")
        assertRejected(Reason.CORRUPTED, zipOf("data.json" to json))
    }

    @Test
    fun unreferencedAndMissingPhotos_areIgnored() {
        val json = data(
            categories = """[{"id": 1, "name": "К", "photo": "photos/missing.jpg", "type": "UNKNOWN"}]""",
            recipes = """[{"id": 1, "categoryId": 1, "name": "Р", "photo": null}]"""
        )
        val imported = BackupArchive.read(
            ByteArrayInputStream(zipOf("data.json" to json, "photos/extra.jpg" to byteArrayOf(1))),
            targetDir
        )

        assertEquals("", imported.categories[0].photoUri)
        assertEquals(CategoryType.NONE, imported.categories[0].type)
        assertEquals("", imported.recipes[0].photoUri)
        assertEquals(0, imported.summary.photosCount)
        assertTrue(targetDir.listFiles()!!.isEmpty())
    }

    @Test
    fun entryNameWithPathTraversal_doesNotEscapePhotosDir() {
        val json = data(
            categories = """[{"id": 1, "name": "К", "photo": "photos/../../evil.sh"}]"""
        )
        val imported = BackupArchive.read(
            ByteArrayInputStream(zipOf("photos/../../evil.sh" to byteArrayOf(1), "data.json" to json)),
            targetDir
        )

        val photo = File(imported.categories[0].photoUri)
        assertEquals(targetDir.canonicalFile, photo.canonicalFile.parentFile)
        assertTrue(photo.name.startsWith("image_"))
    }

    private fun photo(name: String, content: ByteArray) =
        File(sourceDir, name).apply { writeBytes(content) }

    private fun export(
        categories: List<CategoryEntity>,
        recipes: List<RecipeEntity>
    ): Pair<ByteArray, BackupSummary> {
        val output = ByteArrayOutputStream()
        val summary = BackupArchive.write(output, categories, recipes)
        return output.toByteArray() to summary
    }

    private fun assertPhoto(dir: File, expected: File, actualPath: String) {
        val actual = File(actualPath)
        assertEquals(dir.canonicalFile, actual.canonicalFile.parentFile)
        assertArrayEquals(expected.readBytes(), actual.readBytes())
    }

    private fun assertRejected(reason: Reason, archive: ByteArray) {
        try {
            BackupArchive.read(ByteArrayInputStream(archive), targetDir)
            fail("Ожидалось InvalidBackupException($reason)")
        } catch (e: InvalidBackupException) {
            assertEquals(reason, e.reason)
        }
    }

    private fun data(
        version: Int = BackupArchive.FORMAT_VERSION,
        categories: String = "[]",
        recipes: String = "[]"
    ) = """{"format": "myrecipes-backup", "version": $version, "categories": $categories, "recipes": $recipes}"""
        .toByteArray()

    private fun zipOf(vararg entries: Pair<String, ByteArray>): ByteArray {
        val output = ByteArrayOutputStream()
        ZipOutputStream(output).use { zip ->
            entries.forEach { (name, content) ->
                zip.putNextEntry(ZipEntry(name))
                zip.write(content)
                zip.closeEntry()
            }
        }
        return output.toByteArray()
    }
}
