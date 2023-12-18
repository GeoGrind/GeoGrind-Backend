package com.geogrind.geogrindbackend.models.messages
//
//import com.fasterxml.jackson.annotation.JsonIgnore
//import com.geogrind.geogrindbackend.models.user_profile.UserProfile
//import jakarta.persistence.*
//import jakarta.validation.constraints.Size
//import org.hibernate.annotations.GenericGenerator
//import org.springframework.data.annotation.CreatedDate
//import org.springframework.data.annotation.LastModifiedDate
//import org.springframework.data.jpa.domain.support.AuditingEntityListener
//import java.util.Date
//import java.util.UUID
//
//@Entity
//@Table(name = "chatroom")
//@EntityListeners(AuditingEntityListener::class)
//data class ChatRoom (
//
//    @Id
//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
//    @Column(name = "chatroom_id", columnDefinition = "uuid", updatable = false, nullable = false, unique = true)
//    val chatRoomId: UUID? = null,
//
//    @Column(name = "chatroom_description", length = 100, unique = false, nullable = false)
//    var chatRoomDescription: String ?= null,
//
//    @OneToMany
//    @JoinColumn(name = "chatroom_id")
//    var members: MutableSet<UserProfile>,
//
//    @OneToMany
//    @JoinColumn(name = "chatroom_id")
//    var messages: MutableSet<Message> = HashSet(),
//
//    @CreatedDate
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "created_at")
//    var createdAt: Date? = Date(),
//
//    @LastModifiedDate
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "updated_at")
//    var updatedAny: Date? = Date(),
//) {
//    @Column(name = "chatroom_name", length = 100, unique = false, nullable = false)
//    @Size(min = 3)
//    lateinit var chatRoomName: String
//        private set
//
//    init {
//        chatRoomName = members.joinToString(", ") { it.username }
//    }
//}
