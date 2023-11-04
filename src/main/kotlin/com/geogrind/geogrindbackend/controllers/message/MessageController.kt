package com.geogrind.geogrindbackend.controllers.message

import com.geogrind.geogrindbackend.dto.message.SuccessUserMessageResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Message", description = "Message REST Controller")
@RestController
@RequestMapping(path = ["/geogrind/message/"])
interface MessageController {
    @GetMapping(path = ["/all"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Retrieve all messages",
        operationId = "retrieveAllMessages",
        description = "Retrieve a list of all messages"
    )
    suspend fun getAllMessages(): ResponseEntity<List<SuccessUserMessageResponse>>
}
