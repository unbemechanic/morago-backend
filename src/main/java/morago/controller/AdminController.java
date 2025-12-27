package morago.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morago.dto.call.topic.CallTopicDto;
import morago.model.CallTopic;
import morago.model.User;
import morago.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(
            summary = "Returns all users as list",
            description = "Returns all created user list",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Call topic created",
                            content = @Content(schema = @Schema(implementation = CallTopicDto.class))),
                    @ApiResponse(responseCode = "409", description = "Topic with name exists")
            }
    )
    @GetMapping("/users/list")
    public ResponseEntity<List<User>> getUsers(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<User> getAllUsers =  adminService.findAllUsers();
        return ResponseEntity.ok(getAllUsers);
    }

    @Operation(
            summary = "Creating new call topic",
            description = "Creates new call topic with name",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Call topic created",
                            content = @Content(schema = @Schema(implementation = CallTopicDto.class))),
                    @ApiResponse(responseCode = "409", description = "Topic with name exists")
            }
    )
    @PostMapping("/call/topic/add")
    public ResponseEntity<String> addTopic(@Valid @RequestBody CallTopicDto topic) {
        adminService.createNewTopic(topic);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Returns all topics as list",
            description = "Returns all call topics with audit data, topics are created with default status of not active",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Call topic List",
                            content = @Content(schema = @Schema(implementation = CallTopic.class))),
            }
    )
    @GetMapping("/call/topic/list")
    public ResponseEntity<List<CallTopic>> getAllTopics() {
        List<CallTopic> topicList = adminService.getAllTopics();
        return ResponseEntity.status(HttpStatus.OK).body(topicList);
    }

}
