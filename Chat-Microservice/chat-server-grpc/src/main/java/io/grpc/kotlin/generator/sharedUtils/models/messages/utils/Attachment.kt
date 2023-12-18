package com.geogrind.geogrindbackend.models.messages.utils
//
//import com.geogrind.geogrindbackend.models.messages.Message
//import jakarta.persistence.*
//import jakarta.validation.constraints.Size
//import org.hibernate.annotations.GenericGenerator
//import org.springframework.data.annotation.CreatedDate
//import org.springframework.data.annotation.LastModifiedDate
//import org.springframework.data.jpa.domain.support.AuditingEntityListener
//import java.util.*
//
//@Entity
//@Table(name = "attachment")
//@EntityListeners(AuditingEntityListener::class)
//data class Attachment (
//
//    @Id
//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
//    @Column(name = "attachment_id", columnDefinition = "uuid", updatable = false, nullable = false, unique = true)
//    val attachmentId: UUID? = null,
//
//    @Column(name = "url", unique = false, nullable = false)
//    @Size(min = 3)
//    val url: String,
//
//    @Column(name = "attachment_type", unique = false, nullable = false)
//    @Size(min = 3)
//    val attachmentType: String,
//
//    // Many to one relationship with the message entity
//    @ManyToOne
//    @JoinColumn(name = "message_id", insertable = false, updatable = false)
//    var message: Message,
//
//    @CreatedDate
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "created_at")
//    var createdAt: Date? = null,
//
//    @LastModifiedDate
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "updated_at")
//    var updatedAt: Date? = null,
//)