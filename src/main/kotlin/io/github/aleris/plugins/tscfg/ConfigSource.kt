package io.github.aleris.plugins.tscfg

import tscfg.model.ObjectType
import tscfg.ns.NamespaceMan
import java.nio.file.Path
data class ConfigSource(
  val configFile: ConfigFile,
  val path: Path,
  val objectType: ObjectType,
  val namespace: NamespaceMan,
)
