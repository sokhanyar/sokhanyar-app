package ir.saltech.sokhanyar.data.local.entity.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.File
import java.util.Date

object FileSerializer : KSerializer<File> {
	override val descriptor = PrimitiveSerialDescriptor("File", PrimitiveKind.STRING)

	override fun serialize(encoder: Encoder, value: File) {
		encoder.encodeString(value.path)
	}

	override fun deserialize(decoder: Decoder): File {
		return File(decoder.decodeString())
	}
}

object DateSerializer : KSerializer<Date> {
	override val descriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.LONG)

	override fun serialize(encoder: Encoder, value: Date) {
		encoder.encodeLong(value.time)
	}

	override fun deserialize(decoder: Decoder): Date {
		return Date(decoder.decodeLong())
	}
}
