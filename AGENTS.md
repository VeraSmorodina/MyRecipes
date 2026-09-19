# AGENTS.md — «Мои рецепты» (MyRecipes)

Руководство для AI-агентов и разработчиков. Описывает фактическое состояние кода, а не желаемое.

## Что это за проект

Android-приложение для хранения собственных рецептов. Работает полностью офлайн: без сети, бэкенда и аккаунтов.

Что умеет:
- **категории**: список в сетке 2×N, создание, редактирование, удаление (через меню «⋮» на карточке). При первом запуске добавляются 8 категорий по умолчанию;
- **рецепты**: название, ингредиенты, алгоритм приготовления, фото, категория. Их можно создать, открыть, отредактировать и удалить;
- **избранное**: отметка сердечком на экране рецепта и отдельная вкладка;
- **поиск** рецептов по названию (поле на экране категорий, запуск по IME `Done`);
- **«Поделиться»**: рецепт отправляется текстом через `ACTION_SEND`.

Весь UI-текст на русском. Язык коммитов тоже русский.

## Стек и версии

| Что | Версия / значение |
|---|---|
| Язык | Kotlin 2.1.0, JVM target 1.8 |
| Android Gradle Plugin | 8.8.0 |
| Gradle wrapper | 8.10.2 |
| compileSdk / targetSdk / minSdk | 35 / 35 / 24 |
| UI | XML-макеты, Fragments, Material 3 (`Theme.Material3.DayNight.NoActionBar`), ViewBinding + DataBinding |
| Навигация | Jetpack Navigation 2.8.5 + Safe Args |
| БД | Room 2.6.1 (через KSP) |
| DI | Dagger 2.52 (через KSP), без Hilt |
| Асинхронность | Coroutines, `Flow`, `LiveData` |
| Тесты | JUnit 4, AndroidX Test, Espresso (есть только шаблонные примеры) |

Все версии зависимостей и плагинов лежат в каталоге версий [gradle/libs.versions.toml](gradle/libs.versions.toml). Новые зависимости добавляйте туда, а не строкой в `build.gradle.kts`.

Про процессоры аннотаций: Room и Dagger работают через **KSP**, но плагин `kotlin-kapt` тоже нужен, его использует DataBinding (`ProcessDataBinding`). **Не удаляйте kapt.**

## Сборка, запуск, тесты

Нужен **JDK 17**: подойдёт JBR из Android Studio или любой OpenJDK 17. Путь к Android SDK задаётся в `local.properties` (`sdk.dir`), этот файл не коммитится.

```bash
./gradlew assembleDebug
```

```bash
./gradlew testDebugUnitTest
```

```bash
./gradlew connectedDebugAndroidTest
```

```bash
./gradlew installDebug
```

Если системная Java не 17-й версии, задайте `JAVA_HOME` перед вызовом, например:
`JAVA_HOME=$HOME/Library/Java/JavaVirtualMachines/ms-17.0.17/Contents/Home ./gradlew assembleDebug`.

Проверено: `assembleDebug` и `testDebugUnitTest` проходят. Предупреждения компилятора о deprecated API (`startActivityForResult`, `onActivityResult`, `setHasOptionsMenu`, `onCreateOptionsMenu`) известны и ожидаемы.

Линтера и форматтера (ktlint, detekt, spotless) нет, CI тоже нет. Стиль кода — `kotlin.code.style=official`.

### Версионирование

Версия задаётся одной переменной в [app/build.gradle.kts](app/build.gradle.kts):

```kotlin
val version = 7          // versionCode = 7, versionName = "1.7"
```

Чтобы поднять версию, увеличьте это число на 1 (так сделаны коммиты «version up»). Release-сборка идёт без минификации (`isMinifyEnabled = false`). Конфигурации подписи в репозитории нет.

## Структура проекта

Модуль один — `:app`. Пакет и `applicationId`: `com.vsmorodina.myrecipes`.

```
app/src/main/java/com/vsmorodina/myrecipes/
├── MainActivity.kt              # единственная Activity: Toolbar + NavHost + BottomNavigationView
├── RecipesApplication.kt        # создаёт Dagger-компонент; заполняет категории по умолчанию при первом запуске
├── data/
│   ├── AppDatabase.kt           # Room БД "app_database", version = 2
│   ├── dao/                     # CategoryDao, RecipeDao, RecipePhotoDao
│   ├── entity/                  # CategoryEntity (+ enum CategoryType), RecipeEntity, RecipePhotoEntity
│   └── repository/              # CategoryRepositoryImpl, RecipesRepositoryImpl
├── domain/
│   ├── entity/                  # Category, Recipe — доменные модели
│   ├── repository/              # интерфейсы CategoryRepository, RecipesRepository
│   └── useCase/                 # 13 use case'ов, по одному действию на класс
├── di/
│   ├── ApplicationComponent.kt  # @Singleton компонент, inject(...) для каждого фрагмента
│   ├── AppViewModelFactory.kt   # фабрика ViewModel на основе Dagger multibinding
│   ├── annotation/ViewModelKey.kt
│   └── module/                  # AppModule, DatabaseModule, RepositoryModule, ViewModelModule
└── presentation/
    ├── fragments/               # 9 экранов
    ├── viewModels/              # по одной ViewModel на экран
    ├── adapters/                # ListAdapter'ы и DiffUtil-колбэки для RecyclerView
    └── common/fileloadImage.kt  # BindingAdapter "imageUri" (сейчас нигде не используется)

app/src/main/res/
├── navigation/nav_graph.xml     # граф навигации (единственный)
├── menu/menu_main.xml           # нижняя навигация
├── menu/menu_toolbar.xml        # меню экрана рецепта: Редактировать / Удалить / Поделиться
├── layout/                      # fragment_*.xml (обёрнуты в <layout> для DataBinding), item-макеты
├── drawable/                    # иконки; s*.jpg/webp — картинки категорий по умолчанию; def1.webp, image_def.png — заглушки
└── values/                      # colors, dimens, strings, themes (тёмная серая палитра задана вручную)
```

## Архитектура

Упрощённая Clean Architecture + MVVM. Слои связаны так:

```
Fragment ──(AppViewModelFactory)──▶ ViewModel ──▶ UseCase ──▶ Repository (интерфейс, domain)
                                                                   ▲
                                                   RepositoryImpl (data) ──▶ DAO ──▶ Room
```

Как это устроено:
- **Fragment** сам себя инжектит в `onCreateView` (у `RecipeFragment` в `onCreate`):
  ```kotlin
  (requireActivity().application as RecipesApplication).applicationComponent.inject(this)
  viewModel = ViewModelProvider(this, appViewModelFactory).get(XxxViewModel::class.java)
  ```
- **ViewModel** получает use case'ы через `@Inject constructor`. Аргументы навигации она не получает в конструкторе: их передают методом `init(id)` или `initRecipe(id)`, потом вызывают загрузку (`getRecipe()`, `getCategory()` и т.п.).
- **UseCase** — класс с `@Inject constructor(repository)` и методом `invoke(...)`, объявленным **без** модификатора `operator`. Поэтому вызывать нужно `useCase.invoke(x)`, а не `useCase(x)`.
- **Repository**: интерфейс лежит в `domain/repository`, реализация в `data/repository`, связка — `@Binds` в `RepositoryModule`.
- **Маппинг** Entity → модель делают методы `CategoryEntity.toModel()` и `RecipeEntity.toModel()`.

Слои разделены не строго, это нужно учитывать:
- интерфейсы репозиториев и use case'ы принимают и возвращают **data-сущности** (`CategoryEntity`, `RecipeEntity`), например в `insertCategory`, `recipeInsert`, `getFavouritesRecipe`, `searchRecipes`, `getCategoriesLiveData`;
- `domain.entity.Category` импортирует `data.entity.CategoryType` и `R` (картинки категорий по умолчанию);
- реактивные типы перемешаны: категории списком приходят как `Flow`, для спиннера на экране создания рецепта — как `LiveData`; рецепты по категории — `Flow`, избранное и поиск — `LiveData`, одиночный рецепт — `suspend`.

Когда добавляете новый код, держитесь направления *domain-модели в domain-слое*. Существующие сигнатуры без необходимости не переписывайте.

### UI-паттерны, которые повторяются во всех экранах
- ViewBinding через `_binding` / `binding get() = _binding!!`, в `onDestroyView` обнуляется `_binding = null`.
- Сначала создаётся binding, потом выставляются `binding.lifecycleOwner = viewLifecycleOwner` и `binding.viewModel = viewModel`. Для DataBinding каждый `fragment_*.xml` содержит `<variable name="viewModel">`.
- Расширение `Fragment.observeLiveData(liveData) { ... }` объявлено на верхнем уровне в [CreateCategoryFragment.kt](app/src/main/java/com/vsmorodina/myrecipes/presentation/fragments/CreateCategoryFragment.kt) и используется во всём пакете `fragments`.
- `Flow` собирается так: `lifecycleScope.launch { flow.collectLatest { adapter.submitList(it) } }`.
- Одноразовые события (успешное сохранение) делаются через `MutableLiveData<String?>`: сначала выставляется значение, сразу за ним `null`. Отдельного event-механизма нет.
- `CreateRecipeViewModel` сообщает об ошибках через `sealed interface DisplayMessage` (`ToastMessage` / `AlertDialogMessage`), остальные ViewModel — через `MutableLiveData<String>`.
- Выбор фото на API ≤ 32 делается через `Intent.ACTION_PICK` + `startActivityForResult`. На API 33+ сначала запрашивается `READ_MEDIA_IMAGES`, потом открывается `ACTION_PICK` через `ActivityResultLauncher`. Выбранный файл **копируется** в `context.filesDir/image_<timestamp>.jpg`, в БД записывается **абсолютный путь** к копии. Отображается через `setImageURI(Uri.fromFile(File(path)))`. Эта логика продублирована в 4 фрагментах: `Create/Change` × `Recipe/Category`.
- Пустой `photoUri` означает, что фото нет: для карточек используется заглушка `R.drawable.def1`, для экрана рецепта — `R.drawable.image_def`.

## Модель данных (Room)

БД `app_database`, `version = 2`, `exportSchema = false`, **`fallbackToDestructiveMigration()`**.

**`categories`** (`CategoryEntity`)
| Колонка | Тип | Примечание |
|---|---|---|
| `id` | Long PK autoGenerate | |
| `name` | String | |
| `photo_uri` | String | `""`, если фото нет |
| `is_default` | Boolean | `true` у 8 категорий, созданных при первом запуске |
| `type` | `CategoryType` (enum) | `NONE, SOUPS, SALADS, BAKING, APPETIZERS, MEAT, GARNISH, BEVERAGES, SAUCES`. По `type` выбирается картинка в `Category.getDefaultCategoryImage()` |

**`recipes`** (`RecipeEntity`)
| Колонка | Тип | Примечание |
|---|---|---|
| `id` | Long PK autoGenerate | |
| `category_id` | Long | FK → `categories.id`, **`ON DELETE CASCADE`**, есть индекс |
| `name` | String | |
| `ingredients` | String | свободный текст |
| `cookingAlgorithm` | String | имя колонки в camelCase, так исторически сложилось |
| `photo_url` | String | в Kotlin-поле называется `photoUri`; хранит абсолютный путь к файлу |
| `is_favorite` | Boolean | |

**`recipe_photos`** (`RecipePhotoEntity`, `RecipePhotoDao`): таблица создаётся, но в коде **не используется**, DAO не отдаётся через DI. Судя по всему, это задел под несколько фото на рецепт.

Сохранение (`insert`) в `RecipeDao` и `CategoryDao` работает в режиме `OnConflictStrategy.REPLACE`, поэтому редактирование рецепта — это `insert` с существующим `id`.

Заполнение при первом запуске происходит в `RecipesApplication.onCreate()`: проверяется флаг `isFirstRun` в SharedPreferences `RecipesApplicationPreferences`, после чего делается `insertAll` восьми категорий в `CoroutineScope(Dispatchers.IO)`.

## Навигация

Все экраны описаны в [nav_graph.xml](app/src/main/res/navigation/nav_graph.xml), стартовый — `categoriesFragment`. Аргументы передаются через Safe Args (`XxxFragmentArgs.fromBundle(requireArguments())`, `XxxFragmentDirections.actionYyy(...)`).

| Destination id | Fragment | Аргументы | Куда можно перейти |
|---|---|---|---|
| `categoriesFragment` | `CategoriesFragment` | — | `recipesFragment`, `createCategoryFragment2`, `changeCategoryFragment`, `searchRecipeFragment` |
| `recipesFragment` | `RecipesFragment` | `categoryId: long` | `recipeFragment` |
| `recipeFragment` | `RecipeFragment` | `idArg: long` | `changeRecipeFragment` |
| `changeRecipeFragment` | `ChangeRecipeFragment` | `idRecipeChange: long` | — |
| `createRecipeFragment` | `CreateRecipeFragment` | — | `createCategoryFragment2` |
| `createCategoryFragment2` | `CreateCategoryFragment` | — | — |
| `changeCategoryFragment` | `ChangeCategoryFragment` | `categoryId: long` | — |
| `favoritesFragment` | `FavouritesRecipesFragment` | — | `recipeFragment` |
| `searchRecipeFragment` | `SearchRecipeFragment` | `searchRecipeArg: string` | `recipeFragment` |

Нижняя навигация ([menu_main.xml](app/src/main/res/menu/menu_main.xml)) связана с графом через `setupWithNavController`. Для этого **id пунктов меню должны совпадать с id destination'ов**: `categoriesFragment` (подпись «Поиск»), `favoritesFragment`, `createRecipeFragment`. Если переименовываете destination, меняйте и меню.

Заголовок Toolbar берётся из `android:label` destination'а.

## Как добавить новый экран или функцию (чек-лист)

1. **Data**: если нужен новый запрос, добавьте метод в DAO. Если меняется схема (новая сущность или колонка), поднимите `version` в `AppDatabase` и учтите, что без миграции **данные пользователей будут стёрты** (см. «Подводные камни»).
2. **Domain**: добавьте метод в интерфейс репозитория (`domain/repository`), реализуйте его в `data/repository/*Impl`.
3. **UseCase**: создайте класс `XxxUseCase @Inject constructor(repo)` с методом `invoke(...)` в `domain/useCase/`.
4. **ViewModel**: `class XxxViewModel @Inject constructor(...) : ViewModel()` в `presentation/viewModels/`.
5. **DI**:
   - зарегистрируйте ViewModel в [ViewModelModule.kt](app/src/main/java/com/vsmorodina/myrecipes/di/module/ViewModelModule.kt): `@IntoMap @ViewModelKey(XxxViewModel::class) @Binds abstract fun ...`. Если этого не сделать, приложение упадёт в рантайме при открытии экрана: `AppViewModelFactory` не найдёт провайдер. На этапе компиляции ошибки не будет;
   - добавьте `fun inject(fragment: XxxFragment)` в [ApplicationComponent.kt](app/src/main/java/com/vsmorodina/myrecipes/di/ApplicationComponent.kt);
   - новый репозиторий привяжите через `@Binds` в `RepositoryModule`, новый DAO отдайте через `@Provides` в `DatabaseModule`.
6. **UI**: макет `fragment_xxx.xml` оберните в `<layout>` с `<variable name="viewModel">`, создайте Fragment по паттерну выше и добавьте destination и action в `nav_graph.xml`.
7. Соберите проект `./gradlew assembleDebug`: ошибки Dagger, Safe Args и DataBinding видны только при сборке.

## Конвенции

- Код на Kotlin, стиль official, отступ 4 пробела.
- Имена: `XxxFragment`, `XxxViewModel`, `XxxUseCase`, `XxxRepository` / `XxxRepositoryImpl`, `XxxEntity` (Room), `XxxDao`; адаптеры — `XxxItemAdapter` / `XxxItemsAdapter`, DiffUtil — `XxxDiffItemCallback`.
- Написание плавает: встречаются и `Favourite`, и `Favorite`. В новом коде ориентируйтесь на соседние файлы и не переименовывайте существующее без задачи.
- Комментарии в коде на русском.
- Строки UI почти везде захардкожены по-русски прямо в Kotlin и XML, в `strings.xml` лежит лишь несколько. Новые строки лучше выносить в `strings.xml`.
- Цвета берутся из `values/colors.xml` (`gray`, `gray_light`, `gray_very_light`, `white`…). Тема фактически одна, тёмно-серая, цвета заданы явно в макетах.
- В `CategoryItemsAdapter` параметр `onСlickCategory` содержит **кириллическую «С»**. Если скопировать имя с латинской C, будет ошибка компиляции.
- Коммиты — короткое описание на русском в свободной форме («Добавлена функция поделиться», «Рефакторинг»); подъём версии оформляется отдельным коммитом «version up».

## Тесты

Настоящих тестов нет, есть только шаблоны `ExampleUnitTest` (`src/test`) и `ExampleInstrumentedTest` (`src/androidTest`). Библиотек для моков и для тестирования корутин/`LiveData` не подключено: нет `mockk`, `kotlinx-coroutines-test`, `arch core-testing`. Если пишете тесты, добавьте их в `libs.versions.toml`. Проще всего покрыть unit-тестами use case'ы и ViewModel, подменив интерфейсы репозиториев фейками.

## Подводные камни и известные проблемы

Прежде чем что-то «чинить попутно», учтите: это текущее поведение, и часть его может быть не очевидна пользователю.

1. **Разрушительная миграция.** `fallbackToDestructiveMigration()` + `exportSchema = false`: любое изменение схемы с повышением `version` **удаляет все рецепты пользователя**. Для изменений схемы пишите `Migration`. После потери данных сид категорий не повторится, потому что флаг `isFirstRun` уже `false`.
2. **Два экземпляра БД.** `RecipesApplication` заполняет категории через `AppDatabase.getInstance()`, а весь остальной код работает с отдельным `@Singleton`-экземпляром из `DatabaseModule`. Это два разных объекта `RoomDatabase`, и между ними не работает invalidation tracker: при самом первом запуске список категорий может не обновиться, пока экран не пересоздан. Любой новый код должен брать БД или DAO **только через Dagger**.
3. **Поиск — точное совпадение.** `RecipeDao.searchRecipes` использует `WHERE name LIKE :query`, но `%` нигде не добавляется. Найдётся только рецепт с полностью совпадающим названием (без учёта регистра и только для ASCII; для кириллицы SQLite `LIKE` регистрозависим).
4. **Редактирование рецепта сбрасывает категорию.** `ChangeRecipeViewModel.calculateCategoryIndex()` вызывается раньше, чем загружен рецепт, и читает `.value` у Room-`LiveData` без наблюдателей (там всегда `null`). Поэтому спиннер не выставляется на категорию рецепта, и при сохранении рецепт переезжает в первую категорию списка.
5. **Редактирование категории по умолчанию** создаёт `CategoryEntity` без `isDefault` и `type`. Категория становится обычной и теряет встроенную картинку.
6. **Удаление категории** происходит сразу, без подтверждения, и каскадом удаляет все её рецепты (FK `CASCADE`).
7. **Сообщение об успехе приходит раньше записи.** В `CreateRecipeViewModel` и `ChangeRecipeViewModel` оно отправляется до завершения `insert` (вне `launch`), фрагмент тут же делает `navigateUp()`, а `viewModelScope` отменяется при уничтожении ViewModel.
8. **Файлы фото не удаляются**: ни при замене фото, ни при удалении рецепта или категории. `filesDir` со временем растёт.
9. `SearchRecipeItemAdapter` не обрабатывает пустой `photoUri`, заглушки нет. `SearchRecipeFragment` не обнуляет `_binding` в `onDestroyView`.
10. `RecipeFragment` инжектится дважды (в `onCreate` и в `onCreateView`). Это безвредно, но лишне.
11. Мёртвый код: `RecipePhotoEntity`/`RecipePhotoDao`, BindingAdapter `imageUri` в `presentation/common/fileloadImage.kt`, `SearchRecipeItemCallback`, строка `hello_blank_fragment`, закомментированные старые фабрики ViewModel во фрагментах.
12. В `RecipeViewModel` висит `TODO("Исправить, при выходе из рецепта лайк не сохраняется")`. Текущая логика `updateFavorite()` / `getFavoriteStatus()` пишет в БД, так что TODO может быть устаревшим; проверьте, прежде чем полагаться на него.
13. Плагин Safe Args берёт версию из `navigationFragmentKtx = 2.8.4`, а библиотеки навигации — из `nav_version = 2.8.5`. При обновлении навигации меняйте обе версии.

## Файлы, которые не нужно коммитить или трогать

- `local.properties`: локальный путь к SDK, в `.gitignore`.
- `.idea/`, `.gradle/`, `.kotlin/`, `build/`: артефакты IDE и сборки. `.idea/` сейчас не отслеживается git'ом — не добавляйте её в коммиты.
- `gradle/wrapper/gradle-wrapper.jar` меняется только через `./gradlew wrapper`.
