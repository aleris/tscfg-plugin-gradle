package io.github.aleris.plugins.tscfg

import org.gradle.testfixtures.ProjectBuilder

class TestFixture {
  val testProjectDir = tempDir()

  val project = ProjectBuilder.builder().withProjectDir(testProjectDir).build()

  val tsCfgExtension: TscfgExtension

  val generateTscfgTask: GenerateTscfgTask

  val sourceConfigFileReader = SourceConfigFileReader(project)

  val configFile: ConfigFile

  init {
    project.plugins.apply("io.github.aleris.plugins.tscfg")

    tsCfgExtension = project.extensions.getByType(TscfgExtension::class.java)
    tsCfgExtension.packageName.set(PACKAGE_NAME)

    generateTscfgTask = project.tasks.getByName("generateTscfg") as GenerateTscfgTask

    testProjectDir.resolve(CONFIG_SPEC_PATH).also {
      it.parentFile.mkdirs()
      it.writeText(CONFIG_SPEC)
    }

    configFile = project.objects.newInstance(
      ConfigFile::class.java,
      CONFIG_SPEC_NAME,
      tsCfgExtension,
      project,
    )
    configFile.configFile.set(project.file("src/main/resources/application.conf"))
  }

  companion object {
    const val PACKAGE_NAME = "com.example"
    const val PACKAGE_PATH = "com/example"
    const val CONFIG_SPEC_NAME = "application"
    const val CONFIG_SPEC_PATH = "src/tscfg/application.spec.conf"
    val CONFIG_SPEC = """
      # Configuration for API calls
      api {
        # The URI of the API
        #@envvar API_URI
        uri: "string | http://localhost:8080",
      }
      """.trimIndent()
  }
}