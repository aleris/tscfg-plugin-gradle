package io.github.aleris.plugins.tscfg

import java.io.File
import java.nio.file.Files

fun tempDir(): File = Files.createTempDirectory("tscfg-plugin-gradle").toFile().also { it.deleteOnExit() }
