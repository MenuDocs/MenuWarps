package org.menudocs.warps.data.ksx

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Location
import org.bukkit.World

object LocationSerializer : KSerializer<Location> {
  override val descriptor = buildClassSerialDescriptor("Bukkit.Location") {
    element<Double>("x")
    element<Double>("y")
    element<Double>("z")
    element<Float>("pitch")
    element<Float>("yaw")
    element<World?>("world")
  }

  @OptIn(ExperimentalSerializationApi::class)
  override fun deserialize(decoder: Decoder): Location {
    with(decoder.beginStructure(descriptor)) {
      var x: Double? = null
      var y: Double? = null
      var z: Double? = null
      var pitch: Float? = null
      var yaw: Float? = null
      var world: World? = null

      loop@ while (true) when (val index = decodeElementIndex(descriptor)) {
        CompositeDecoder.DECODE_DONE -> break@loop
        Index.X ->
          x = decodeDoubleElement(descriptor, index)
        Index.Y ->
          y = decodeDoubleElement(descriptor, index)
        Index.Z ->
          z = decodeDoubleElement(descriptor, index)
        Index.YAW ->
          yaw = decodeFloatElement(descriptor, index)
        Index.PITCH ->
          pitch = decodeFloatElement(descriptor, index)
        Index.WORLD ->
          world = decodeNullableSerializableElement(descriptor, index, WorldSerializer)
      }

      endStructure(descriptor)
      if (x == null || y == null || z == null) {
        throw SerializationException("Not enough information to construct location")
      }

      return if (yaw != null && pitch != null) Location(world, x, y, z, yaw, pitch) else Location(world, x, y, z)
    }
  }

  @OptIn(ExperimentalSerializationApi::class)
  override fun serialize(encoder: Encoder, value: Location) {
    with(encoder.beginStructure(descriptor)) {
      /* encode shit */
      encodeDoubleElement(descriptor, Index.X, value.x)
      encodeDoubleElement(descriptor, Index.Y, value.y)
      encodeDoubleElement(descriptor, Index.Z, value.z)
      encodeFloatElement(descriptor, Index.YAW, value.yaw)
      encodeFloatElement(descriptor, Index.PITCH, value.pitch)
      encodeNullableSerializableElement(descriptor, Index.WORLD, WorldSerializer, value.world)

      /* end the structure */
      endStructure(descriptor)
    }
  }

  object Index {
    const val X     = 0
    const val Y     = 1
    const val Z     = 2
    const val YAW   = 3
    const val PITCH = 4
    const val WORLD = 5
  }

}