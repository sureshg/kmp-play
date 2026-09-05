package dev.suresh.serde

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.io.encoding.Base64

/**
 * A [Base64] serializer. Use with
 *
 * ```kotlin
 * @Serializable(with = ByteArrayAsBase64Serializer::class)
 * ```
 */
object ByteArrayAsBase64Serializer : KSerializer<ByteArray> {

  private val base64 = Base64.Default

  override val descriptor: SerialDescriptor
    get() = PrimitiveSerialDescriptor("ByteArrayAsBase64Serializer", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder) = base64.decode(decoder.decodeString())

  override fun serialize(encoder: Encoder, value: ByteArray) =
      encoder.encodeString(base64.encode(value))
}
