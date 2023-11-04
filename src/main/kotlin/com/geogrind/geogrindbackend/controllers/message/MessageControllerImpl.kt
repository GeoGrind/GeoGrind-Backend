package com.geogrind.geogrindbackend.controllers.message

import com.geogrind.geogrindbackend.controllers.registration.UserAccountController
import com.geogrind.geogrindbackend.dto.message.SuccessUserMessageResponse
import com.geogrind.geogrindbackend.services.message.MessageService
import com.geogrind.geogrindbackend.models.message.toSuccessHttpResponseList
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.util.*

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
    override suspend fun getAllMessages(): ResponseEntity<List<SuccessUserMessageResponse>> = withTimeout(timeOutMillis) {
        ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(messageService.findAllMessages().toSuccessHttpResponseList())
            .also { log.info("Successfully retrieved all messages: $it") }
    }

    @GetMapping(path = ["/author/{authorId}"], produces = ["application/json"])
    @Operation(
        method = "GET",
        summary = "Retrieve messages by author",
        operationId = "retrieveMessagesByAuthorImpl",
        description = "Implementation for retrieving a list of messages for a specific author"
    )
    override suspend fun getMessagesByAuthor(@PathVariable authorId: UUID): ResponseEntity<List<SuccessUserMessageResponse>> = withTimeout(timeOutMillis) {
        ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(messageService.findMessagesByAuthor(authorId).toSuccessHttpResponseList())
            .also { log.info("Successfully retrieved messages for author $authorId: $it") }
    }
    companion object {
        private val log = LoggerFactory.getLogger(UserAccountController::class.java)
        private const val timeOutMillis = 5000L
    }
}
