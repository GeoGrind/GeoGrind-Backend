package com.geogrind.geogrindbackend.controllers.message

import com.geogrind.geogrindbackend.dto.message.MessageResponseDto
import com.geogrind.geogrindbackend.services.message.MessageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Message", description = "Message REST Controller Implementation")
@RestController
@RequestMapping(path = ["/geogrind/message/"])
class MessageControllerImpl @Autowired constructor(
    private val messageService: MessageService
) : MessageController {

    @GetMapping(path = ["/all"], produces = ["application/json"])
    @Operation(
        method = "GET",
        summary = "Retrieve all messages",
        operationId = "retrieveAllMessagesImpl",
        description = "Implementation for retrieving a list of all messages"
    )
    override suspend fun getAllMessages(): ResponseEntity<List<MessageResponseDto>> {
        println("Inside getAllMessages method")

        val messages = messageService.findAllMessages()
        val messageDtos = messages.map { message ->
            MessageResponseDto(
                id = message.id,
                email = message.email,
                createdAt = message.createdAt,
                updatedAt = message.updatedAt
            )
        }

        return ResponseEntity.ok(messageDtos)
    }
}
