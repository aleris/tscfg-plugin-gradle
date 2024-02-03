package io.github.aleris.plugins.tscfg

import java.nio.file.Path

object UnchangedFileWriter {
  fun write(path: Path, text: String) {
    val file = path.toFile()
    val existing = if (file.exists()) {
      file.readText()
    } else {
      null
    }
    if (text != existing) {
      file.parentFile.mkdirs()
      file.writeText(text)
    }
  }
}
