pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "EcoTrack"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":core:common")
include(":core:design")
include(":core:ui")
include(":core:database")
include(":core:network")
include(":core:ml")
include(":domain")
include(":data")
include(":feature:dashboard")
include(":feature:inventory")
include(":feature:addproduct")
include(":feature:shoppinglist")
include(":feature:productdetail")
include(":feature:settings")
include(":feature:ai")
