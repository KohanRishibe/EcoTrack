# EcoTrack

Мобильное приложение для рационального потребления продуктов питания: учёт запасов, сроки годности, список покупок и статистика «использовано / выброшено».

## Технологии

- **Kotlin 2.0+**, **Jetpack Compose**, **Material 3** (Dynamic Color / Material You)
- **Clean Architecture**: модули `core`, `domain`, `data`, `feature`
- **MVVM** + `StateFlow`, **Hilt**, **Room**, **Navigation Compose 2.8+** (type-safe routes)
- **Ktor Client**, **Coil 3**, **CameraX** + **ML Kit Barcode Scanning**
- **Convention plugins** + **Version Catalog** (`gradle/libs.versions.toml`)

## Структура модулей

```
app/                    # Точка входа, навигация, Hilt graph
build-logic/            # Convention plugins
core/
  common/               # Resource, UiState
  design/               # EcoTrackTheme, цвета, типографика
  ui/                   # Общие Compose-компоненты
  database/             # Room entities, DAO
  network/              # Ktor, Open Food Facts API
domain/                 # Модели, репозитории (интерфейсы), Use Cases
data/                   # Реализации репозиториев, Room, DataStore
feature/
  dashboard/            # ui / domain / data (пакеты)
  inventory/
  addproduct/
  shoppinglist/
  productdetail/
  settings/
  ai/                   # ML Kit: фото, чеки; Smart Suggestions
core/ml/                # Image Labeling, Text Recognition, Gemini Nano (заглушка)
```

## AI-функции (опционально)

| Функция | Технология | Описание |
|---------|------------|----------|
| **Распознавание по фото** | ML Kit Image Labeling (+ Gemini Nano при наличии) | Категория продукта и типичный срок хранения → форма «Добавить продукт» |
| **Умные подсказки** | Локальная эвристика по истории (`SmartSuggestionEngine`) | Прогноз «закончится через N дней» → кнопка на главной добавляет в список покупок |
| **Скан чека** | ML Kit Text Recognition | OCR строк чека → выбор позиций → импорт в запасы |

Переключатели в **Настройки → AI-функции**. Точки входа: главная (кнопки «По фото» / «Чек»), форма добавления продукта, настройки.

**Gemini Nano**: в `core:ml` подключена заглушка `NoOpGeminiNanoClassifier`. На устройствах с AICore можно заменить реализацией через [ML Kit GenAI](https://developers.google.com/ml-kit/genai) / Gemini Nano API.

## Требования

- Android Studio Ladybug (2024.2.1) или новее
- JDK 17
- Android SDK 35
- minSdk 26

## Сборка

```bash
# Windows
gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

Release-сборка с R8:

```bash
gradlew.bat assembleRelease
```

Установка на устройство:

```bash
gradlew.bat installDebug
```

## Экраны

| Экран | Описание |
|-------|----------|
| **Dashboard** | Приветствие, сводка запасов, сроки годности, donut-график |
| **Inventory** | Список по категориям (sticky headers), swipe-to-delete, FAB |
| **Add Product** | Форма + сканер штрихкода (CameraX + ML Kit) |
| **Shopping List** | Чекбоксы, шаблоны частых покупок |
| **Product Detail** | Карточка продукта, «использован» / «выброшен» |
| **Settings** | Уведомления, тема, AI-переключатели, экспорт данных |
| **Photo AI** | Камера → категория и срок хранения |
| **Receipt scan** | OCR чека → импорт продуктов |

## Baseline Profiles

В `app/src/main/baseline-prof.txt` — стартовый набор правил. Для полноценной генерации подключите модуль `:benchmark` (Macrobenchmark) и выполните профилирование release-сборки.

## Лицензия

Учебный / демонстрационный проект.
