package org.menudocs.warps.data.ksx

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.World
import org.menudocs.warps.MenuWarps

object WorldSerializer : KSerializer<World?> {
  override val descriptor = PrimitiveSerialDescriptor("Bukkit.World", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): World? {
    val uuid = decoder.decodeSerializableValue(UUIDSerializer)
    return MenuWarps.INSTANCE.server.getWorld(uuid)
  }

  @OptIn(ExperimentalSerializationApi::class)
  override fun serialize(encoder: Encoder, value: World?) {
    return encoder.encodeNullableSerializableValue(UUIDSerializer, value?.uid)
  }

}
