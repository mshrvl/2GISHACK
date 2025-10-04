import java.net.URI

include(":feature:profile")


include(":feature:map")


include(":feature:auth")


include(":core")


include(":data")


pluginManagement {
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
        maven {
            url = URI("https://artifactory.2gis.dev/sdk-maven-release")
        }
    }
}

rootProject.name = "My Application"
include(":app")
include(":feature")
