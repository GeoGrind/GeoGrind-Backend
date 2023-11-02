package com.geogrind.geogrindbackend.controllers.message

import com.geogrind.geogrindbackend.dto.message.TestDto
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
        summary = "Retrieve test message data",
        operationId = "retrieveTestMessageImpl",
        description = "Implementation for retrieving test message data"
    )
    override suspend fun getFunction(): ResponseEntity<TestDto> {
        println("Inside getFunction method")
        return ResponseEntity.ok(TestDto(username = "dummyUser"))
    }
}
