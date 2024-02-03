package io.github.aleris.plugins.tscfg

import org.gradle.testfixtures.ProjectBuilder

class TestFixture {
  val testProjectDir = tempDir()

  val project = ProjectBuilder.builder().withProjectDir(testProjectDir).build()

  val generateTscfgTask: GenerateTscfgTask

  val sourceConfigFileReader = SourceConfigFileReader(project)

  val configFile = project.objects.newInstance(
    ConfigFile::class.java,
    CONFIG_SPEC_PATH,
    project.objects.property(String::class.java).convention(PACKAGE_NAME)
  )

  init {
    project.plugins.apply("io.github.aleris.plugins.tscfg")

    generateTscfgTask = project.tasks.getByName("generateTscfg") as GenerateTscfgTask

    testProjectDir.resolve(CONFIG_SPEC_PATH).also {
      it.parentFile.mkdirs()
      it.writeText("""
      # Configuration for API calls
      api {
        # The URI of the API
        #@envvar API_URI
        uri: "string | http://localhost:8080",
      }
      """.trimIndent())
    }

    configFile.outputConfigFileName.set("src/main/resources/application.conf")
  }

  companion object {
    const val PACKAGE_NAME = "io.github.aleris.plugins.tscfg.example"
    const val PACKAGE_PATH = "io/github/aleris/plugins/tscfg/example"
    const val CONFIG_SPEC_PATH = "src/tscfg/application.spec.conf"
  }
}