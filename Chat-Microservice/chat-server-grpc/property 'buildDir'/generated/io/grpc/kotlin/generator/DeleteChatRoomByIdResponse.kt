// Code generated by Wire protocol buffer compiler, do not edit.
// Source: io.grpc.kotlin.generator.DeleteChatRoomByIdResponse in io/grpc/kotlin/generator/chat.proto
package io.grpc.kotlin.generator

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.ReverseProtoWriter
import com.squareup.wire.Syntax.PROTO_3
import com.squareup.wire.WireField
import com.squareup.wire.`internal`.JvmField
import kotlin.Any
import kotlin.AssertionError
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.DeprecationLevel
import kotlin.Int
import kotlin.Long
import kotlin.Nothing
import kotlin.String
import okio.ByteString

public class DeleteChatRoomByIdResponse(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#BOOL",
    label = WireField.Label.OMIT_IDENTITY,
    schemaIndex = 0,
  )
  public val success: Boolean = false,
  unknownFields: ByteString = ByteString.EMPTY,
) : Message<DeleteChatRoomByIdResponse, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN,
  )
  override fun newBuilder(): Nothing = throw
      AssertionError("Builders are deprecated and only available in a javaInterop build; see https://square.github.io/wire/wire_compiler/#kotlin")

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is DeleteChatRoomByIdResponse) return false
    if (unknownFields != other.unknownFields) return false
    if (success != other.success) return false
    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + success.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    result += """success=$success"""
    return result.joinToString(prefix = "DeleteChatRoomByIdResponse{", separator = ", ", postfix =
        "}")
  }

  public fun copy(success: Boolean = this.success, unknownFields: ByteString = this.unknownFields):
      DeleteChatRoomByIdResponse = DeleteChatRoomByIdResponse(success, unknownFields)

  public companion object {
    @JvmField
    public val ADAPTER: ProtoAdapter<DeleteChatRoomByIdResponse> = object :
        ProtoAdapter<DeleteChatRoomByIdResponse>(
      FieldEncoding.LENGTH_DELIMITED, 
      DeleteChatRoomByIdResponse::class, 
      "type.googleapis.com/io.grpc.kotlin.generator.DeleteChatRoomByIdResponse", 
      PROTO_3, 
      null, 
      "io/grpc/kotlin/generator/chat.proto"
    ) {
      override fun encodedSize(`value`: DeleteChatRoomByIdResponse): Int {
        var size = value.unknownFields.size
        if (value.success != false) size += ProtoAdapter.BOOL.encodedSizeWithTag(1, value.success)
        return size
      }

      override fun encode(writer: ProtoWriter, `value`: DeleteChatRoomByIdResponse) {
        if (value.success != false) ProtoAdapter.BOOL.encodeWithTag(writer, 1, value.success)
        writer.writeBytes(value.unknownFields)
      }

      override fun encode(writer: ReverseProtoWriter, `value`: DeleteChatRoomByIdResponse) {
        writer.writeBytes(value.unknownFields)
        if (value.success != false) ProtoAdapter.BOOL.encodeWithTag(writer, 1, value.success)
      }

      override fun decode(reader: ProtoReader): DeleteChatRoomByIdResponse {
        var success: Boolean = false
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> success = ProtoAdapter.BOOL.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return DeleteChatRoomByIdResponse(
          success = success,
          unknownFields = unknownFields
        )
      }

      override fun redact(`value`: DeleteChatRoomByIdResponse): DeleteChatRoomByIdResponse =
          value.copy(
        unknownFields = ByteString.EMPTY
      )
    }

    private const val serialVersionUID: Long = 0L
  }
}
