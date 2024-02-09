plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

gradlePlugin {
    plugins.create("updateReadmeVersion") {
        isAutomatedPublishing = false
        id = "io.github.aleris.plugins.update-readme-version"
        displayName = "Update Readme Plugin"
        description = "Keep the version in the README.md file up to date with the project version"
        tags = listOf("readme", "version")
        implementationClass = "io.github.aleris.plugins.readmeversion.ReadmeVersionPlugin"
    }
}
