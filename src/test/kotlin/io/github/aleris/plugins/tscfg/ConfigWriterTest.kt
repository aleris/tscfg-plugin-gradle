package io.github.aleris.plugins.tscfg

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class ConfigWriterTest : FunSpec({

  val testFixture = TestFixture()

  test("write") {

    val configSource = testFixture.sourceConfigFileReader.read(testFixture.configFile)
    val configWriter = ConfigWriter(ConfigGenerator(testFixture.generateTscfgTask))

    configWriter.write(configSource)

    val generatedFile = testFixture.testProjectDir.resolve("src/main/resources/application.conf")

    generatedFile.exists() shouldBe true
    generatedFile.readText() shouldContain "uri = \${?API_URI}"
  }
})
