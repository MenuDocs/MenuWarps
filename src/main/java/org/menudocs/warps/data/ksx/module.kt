package org.menudocs.warps.data.ksx

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

val json = Json {
  serializersModule = SerializersModule {
    contextual(LocationSerializer)
    contextual(UUIDSerializer)
  }

  prettyPrint = true
  isLenient = true
  encodeDefaults = true
  ignoreUnknownKeys = true
}