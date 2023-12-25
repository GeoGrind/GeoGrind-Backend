// Code generated by Wire protocol buffer compiler, do not edit.
// Source: io.grpc.kotlin.generator.ChatRoom in io/grpc/kotlin/generator/chat.proto
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
import com.squareup.wire.`internal`.immutableCopyOf
import com.squareup.wire.`internal`.redactElements
import com.squareup.wire.`internal`.sanitize
import kotlin.Any
import kotlin.AssertionError
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.DeprecationLevel
import kotlin.Int
import kotlin.Long
import kotlin.Nothing
import kotlin.String
import kotlin.collections.List
import okio.ByteString

public class ChatRoom(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#STRING",
    label = WireField.Label.OMIT_IDENTITY,
    schemaIndex = 0,
  )
  public val chatRoomId: String = "",
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#STRING",
    label = WireField.Label.OMIT_IDENTITY,
    schemaIndex = 1,
  )
  public val chatRoomDescription: String = "",
  chatRoomOwners: List<UserProfile> = emptyList(),
  chatRoomMembers: List<UserProfile> = emptyList(),
  chatRoomMessages: List<Messages> = emptyList(),
  @field:WireField(
    tag = 6,
    adapter = "io.grpc.kotlin.generator.Theme#ADAPTER",
    label = WireField.Label.OMIT_IDENTITY,
    schemaIndex = 5,
  )
  public val chatRoomTheme: Theme? = null,
  @field:WireField(
    tag = 7,
    adapter = "com.squareup.wire.ProtoAdapter#STRING",
    label = WireField.Label.OMIT_IDENTITY,
    schemaIndex = 6,
  )
  public val createdAt: String = "",
  @field:WireField(
    tag = 8,
    adapter = "com.squareup.wire.ProtoAdapter#STRING",
    label = WireField.Label.OMIT_IDENTITY,
    schemaIndex = 7,
  )
  public val updatedAt: String = "",
  @field:WireField(
    tag = 9,
    adapter = "com.squareup.wire.ProtoAdapter#STRING",
    label = WireField.Label.OMIT_IDENTITY,
    schemaIndex = 8,
  )
  public val chatRoomName: String = "",
  unknownFields: ByteString = ByteString.EMPTY,
) : Message<ChatRoom, Nothing>(ADAPTER, unknownFields) {
  @field:WireField(
    tag = 3,
    adapter = "io.grpc.kotlin.generator.UserProfile#ADAPTER",
    label = WireField.Label.REPEATED,
    schemaIndex = 2,
  )
  public val chatRoomOwners: List<UserProfile> = immutableCopyOf("chatRoomOwners", chatRoomOwners)

  @field:WireField(
    tag = 4,
    adapter = "io.grpc.kotlin.generator.UserProfile#ADAPTER",
    label = WireField.Label.REPEATED,
    schemaIndex = 3,
  )
  public val chatRoomMembers: List<UserProfile> = immutableCopyOf("chatRoomMembers",
      chatRoomMembers)

  @field:WireField(
    tag = 5,
    adapter = "io.grpc.kotlin.generator.Messages#ADAPTER",
    label = WireField.Label.REPEATED,
    schemaIndex = 4,
  )
  public val chatRoomMessages: List<Messages> = immutableCopyOf("chatRoomMessages",
      chatRoomMessages)

  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN,
  )
  override fun newBuilder(): Nothing = throw
      AssertionError("Builders are deprecated and only available in a javaInterop build; see https://square.github.io/wire/wire_compiler/#kotlin")

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is ChatRoom) return false
    if (unknownFields != other.unknownFields) return false
    if (chatRoomId != other.chatRoomId) return false
    if (chatRoomDescription != other.chatRoomDescription) return false
    if (chatRoomOwners != other.chatRoomOwners) return false
    if (chatRoomMembers != other.chatRoomMembers) return false
    if (chatRoomMessages != other.chatRoomMessages) return false
    if (chatRoomTheme != other.chatRoomTheme) return false
    if (createdAt != other.createdAt) return false
    if (updatedAt != other.updatedAt) return false
    if (chatRoomName != other.chatRoomName) return false
    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + chatRoomId.hashCode()
      result = result * 37 + chatRoomDescription.hashCode()
      result = result * 37 + chatRoomOwners.hashCode()
      result = result * 37 + chatRoomMembers.hashCode()
      result = result * 37 + chatRoomMessages.hashCode()
      result = result * 37 + (chatRoomTheme?.hashCode() ?: 0)
      result = result * 37 + createdAt.hashCode()
      result = result * 37 + updatedAt.hashCode()
      result = result * 37 + chatRoomName.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    result += """chatRoomId=${sanitize(chatRoomId)}"""
    result += """chatRoomDescription=${sanitize(chatRoomDescription)}"""
    if (chatRoomOwners.isNotEmpty()) result += """chatRoomOwners=$chatRoomOwners"""
    if (chatRoomMembers.isNotEmpty()) result += """chatRoomMembers=$chatRoomMembers"""
    if (chatRoomMessages.isNotEmpty()) result += """chatRoomMessages=$chatRoomMessages"""
    if (chatRoomTheme != null) result += """chatRoomTheme=$chatRoomTheme"""
    result += """createdAt=${sanitize(createdAt)}"""
    result += """updatedAt=${sanitize(updatedAt)}"""
    result += """chatRoomName=${sanitize(chatRoomName)}"""
    return result.joinToString(prefix = "ChatRoom{", separator = ", ", postfix = "}")
  }

  public fun copy(
    chatRoomId: String = this.chatRoomId,
    chatRoomDescription: String = this.chatRoomDescription,
    chatRoomOwners: List<UserProfile> = this.chatRoomOwners,
    chatRoomMembers: List<UserProfile> = this.chatRoomMembers,
    chatRoomMessages: List<Messages> = this.chatRoomMessages,
    chatRoomTheme: Theme? = this.chatRoomTheme,
    createdAt: String = this.createdAt,
    updatedAt: String = this.updatedAt,
    chatRoomName: String = this.chatRoomName,
    unknownFields: ByteString = this.unknownFields,
  ): ChatRoom = ChatRoom(chatRoomId, chatRoomDescription, chatRoomOwners, chatRoomMembers,
      chatRoomMessages, chatRoomTheme, createdAt, updatedAt, chatRoomName, unknownFields)

  public companion object {
    @JvmField
    public val ADAPTER: ProtoAdapter<ChatRoom> = object : ProtoAdapter<ChatRoom>(
      FieldEncoding.LENGTH_DELIMITED, 
      ChatRoom::class, 
      "type.googleapis.com/io.grpc.kotlin.generator.ChatRoom", 
      PROTO_3, 
      null, 
      "io/grpc/kotlin/generator/chat.proto"
    ) {
      override fun encodedSize(`value`: ChatRoom): Int {
        var size = value.unknownFields.size
        if (value.chatRoomId != "") size += ProtoAdapter.STRING.encodedSizeWithTag(1,
            value.chatRoomId)
        if (value.chatRoomDescription != "") size += ProtoAdapter.STRING.encodedSizeWithTag(2,
            value.chatRoomDescription)
        size += UserProfile.ADAPTER.asRepeated().encodedSizeWithTag(3, value.chatRoomOwners)
        size += UserProfile.ADAPTER.asRepeated().encodedSizeWithTag(4, value.chatRoomMembers)
        size += Messages.ADAPTER.asRepeated().encodedSizeWithTag(5, value.chatRoomMessages)
        if (value.chatRoomTheme != null) size += Theme.ADAPTER.encodedSizeWithTag(6,
            value.chatRoomTheme)
        if (value.createdAt != "") size += ProtoAdapter.STRING.encodedSizeWithTag(7,
            value.createdAt)
        if (value.updatedAt != "") size += ProtoAdapter.STRING.encodedSizeWithTag(8,
            value.updatedAt)
        if (value.chatRoomName != "") size += ProtoAdapter.STRING.encodedSizeWithTag(9,
            value.chatRoomName)
        return size
      }

      override fun encode(writer: ProtoWriter, `value`: ChatRoom) {
        if (value.chatRoomId != "") ProtoAdapter.STRING.encodeWithTag(writer, 1, value.chatRoomId)
        if (value.chatRoomDescription != "") ProtoAdapter.STRING.encodeWithTag(writer, 2,
            value.chatRoomDescription)
        UserProfile.ADAPTER.asRepeated().encodeWithTag(writer, 3, value.chatRoomOwners)
        UserProfile.ADAPTER.asRepeated().encodeWithTag(writer, 4, value.chatRoomMembers)
        Messages.ADAPTER.asRepeated().encodeWithTag(writer, 5, value.chatRoomMessages)
        if (value.chatRoomTheme != null) Theme.ADAPTER.encodeWithTag(writer, 6, value.chatRoomTheme)
        if (value.createdAt != "") ProtoAdapter.STRING.encodeWithTag(writer, 7, value.createdAt)
        if (value.updatedAt != "") ProtoAdapter.STRING.encodeWithTag(writer, 8, value.updatedAt)
        if (value.chatRoomName != "") ProtoAdapter.STRING.encodeWithTag(writer, 9,
            value.chatRoomName)
        writer.writeBytes(value.unknownFields)
      }

      override fun encode(writer: ReverseProtoWriter, `value`: ChatRoom) {
        writer.writeBytes(value.unknownFields)
        if (value.chatRoomName != "") ProtoAdapter.STRING.encodeWithTag(writer, 9,
            value.chatRoomName)
        if (value.updatedAt != "") ProtoAdapter.STRING.encodeWithTag(writer, 8, value.updatedAt)
        if (value.createdAt != "") ProtoAdapter.STRING.encodeWithTag(writer, 7, value.createdAt)
        if (value.chatRoomTheme != null) Theme.ADAPTER.encodeWithTag(writer, 6, value.chatRoomTheme)
        Messages.ADAPTER.asRepeated().encodeWithTag(writer, 5, value.chatRoomMessages)
        UserProfile.ADAPTER.asRepeated().encodeWithTag(writer, 4, value.chatRoomMembers)
        UserProfile.ADAPTER.asRepeated().encodeWithTag(writer, 3, value.chatRoomOwners)
        if (value.chatRoomDescription != "") ProtoAdapter.STRING.encodeWithTag(writer, 2,
            value.chatRoomDescription)
        if (value.chatRoomId != "") ProtoAdapter.STRING.encodeWithTag(writer, 1, value.chatRoomId)
      }

      override fun decode(reader: ProtoReader): ChatRoom {
        var chatRoomId: String = ""
        var chatRoomDescription: String = ""
        val chatRoomOwners = mutableListOf<UserProfile>()
        val chatRoomMembers = mutableListOf<UserProfile>()
        val chatRoomMessages = mutableListOf<Messages>()
        var chatRoomTheme: Theme? = null
        var createdAt: String = ""
        var updatedAt: String = ""
        var chatRoomName: String = ""
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> chatRoomId = ProtoAdapter.STRING.decode(reader)
            2 -> chatRoomDescription = ProtoAdapter.STRING.decode(reader)
            3 -> chatRoomOwners.add(UserProfile.ADAPTER.decode(reader))
            4 -> chatRoomMembers.add(UserProfile.ADAPTER.decode(reader))
            5 -> chatRoomMessages.add(Messages.ADAPTER.decode(reader))
            6 -> chatRoomTheme = Theme.ADAPTER.decode(reader)
            7 -> createdAt = ProtoAdapter.STRING.decode(reader)
            8 -> updatedAt = ProtoAdapter.STRING.decode(reader)
            9 -> chatRoomName = ProtoAdapter.STRING.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return ChatRoom(
          chatRoomId = chatRoomId,
          chatRoomDescription = chatRoomDescription,
          chatRoomOwners = chatRoomOwners,
          chatRoomMembers = chatRoomMembers,
          chatRoomMessages = chatRoomMessages,
          chatRoomTheme = chatRoomTheme,
          createdAt = createdAt,
          updatedAt = updatedAt,
          chatRoomName = chatRoomName,
          unknownFields = unknownFields
        )
      }

      override fun redact(`value`: ChatRoom): ChatRoom = value.copy(
        chatRoomOwners = value.chatRoomOwners.redactElements(UserProfile.ADAPTER),
        chatRoomMembers = value.chatRoomMembers.redactElements(UserProfile.ADAPTER),
        chatRoomMessages = value.chatRoomMessages.redactElements(Messages.ADAPTER),
        chatRoomTheme = value.chatRoomTheme?.let(Theme.ADAPTER::redact),
        unknownFields = ByteString.EMPTY
      )
    }

    private const val serialVersionUID: Long = 0L
  }
}
