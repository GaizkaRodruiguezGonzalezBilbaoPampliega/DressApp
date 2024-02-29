pluginManagement {
    repositories {
        google()
        mavenCentral()

        jcenter(){
            content{
                includeModule("com.theartofdev.edmodo","android-image-cropper")
            }
        }
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        jcenter(){
            content{
                includeModule("com.theartofdev.edmodo","android-image-cropper")
            }
        }






    }
}

rootProject.name = "DressApp"
include(":app")
