package io.github.aleris.plugins.tscfg

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class SourceConfigFileReaderTest : FunSpec({

  val testFixture = TestFixture()

  test("read") {
    val configSource = testFixture.sourceConfigFileReader.read(testFixture.configFile)

    configSource.configFile shouldBe testFixture.configFile
    configSource.namespace shouldNotBe null
    configSource.objectType shouldNotBe null
    configSource.path.endsWith("src/tscfg/application.spec.conf") shouldBe true
  }
})
