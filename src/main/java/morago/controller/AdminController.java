package morago.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morago.dto.admin.*;
import morago.dto.admin.client.ClientProfileDto;
import morago.dto.admin.client.CreateClientRequestDto;
import morago.dto.admin.interpreter.AdminIPResponseDto;
import morago.dto.admin.interpreter.AdminInterpreterProfileRequestDto;
import morago.dto.admin.interpreter.SingleInterpreterProfileDto;
import morago.dto.call.topic.CallTopicDto;
import morago.dto.call.topic.UpdateCallTopicRequest;
import morago.dto.language.LanguageRequestDto;
import morago.mapper.UserMapper;
import morago.model.CallTopic;
import morago.model.interpreter.Language;
import morago.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(
        name = "Admin Controller",
        description = "Endpoints for administrative operations such as user management, role assignment, and system-level configurations. Access is restricted to administrators."
)
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserMapper userMapper;

    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT");

    @Operation(
            summary = "Get all users as list",
            description = "Returns all created user list",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Call topic created",
                            content = @Content(schema = @Schema(implementation = CallTopicDto.class))),
                    @ApiResponse(responseCode = "409", description = "Topic with name exists")
            }
    )
    @GetMapping("/users/list")
    public ResponseEntity<Page<UserResponseDto>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        auditLog.info("ADMIN_LIST_USERS page={} size={} sortBy={} direction={}",
                page, size, sortBy, direction);
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserResponseDto> getAllUsers =  adminService.findAllUsers(pageable);
        return ResponseEntity.ok(getAllUsers);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<SingleUserResponseDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userMapper.toSingleUserResponseDto(adminService.findUserById(id)));
    }

    // Interpreter profile CRUD
    @Operation(
            summary = "POST new user with interpreter profile",
            description = "Creates new user with interpreter profile, assigned default role of INTERPRETER",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created",
                            content = @Content(schema = @Schema(implementation = AdminInterpreterProfileRequestDto.class))),
                    @ApiResponse(responseCode = "409", description = "Phone number already exists"),
                    @ApiResponse(responseCode = "400", description = "Malformed request body if values are sent empty")
            }
    )
    @PostMapping("/user/interpreter/add")
    public ResponseEntity<AdminInterpreterProfileRequestDto> createNewInterpreterProfile(@Valid @RequestBody AdminInterpreterProfileRequestDto interpreter){
        auditLog.info("ADMIN_CREATE_INTERPRETER phoneNumber={}", interpreter.getPhoneNumber());
        adminService.createNewInterpreterProfile(interpreter);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Get Interpreter profile by id",
            description = "Returns single interpreter profile data with languages, topics, and basic information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lists all interpreter profile info",
                            content = @Content(schema = @Schema(implementation = CallTopicDto.class))),
                    @ApiResponse(responseCode = "404", description = "No interpreter found with id")
            }
    )
    @GetMapping("/user/interpreter/{id}")
    public ResponseEntity<SingleInterpreterProfileDto> getInterpreterProfile(@PathVariable Long id) {
        auditLog.info("ADMIN_GET_INTERPRETER phoneNumber={}", id);
        SingleInterpreterProfileDto result = adminService.getInterpreterProfileById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/interpreter/list")
    public ResponseEntity<Page<AdminIPResponseDto>> getAllInterpreterProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
            ){
        auditLog.info("ADMIN_LIST_INTERPRETER page={} size={} sortBy={} direction={}",
                page, size, sortBy, direction);
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdminIPResponseDto> interpreters = adminService.findAllInterpreterProfiles(pageable);
        return ResponseEntity.ok(interpreters);
    }

    // Client Profile CRUD
    @PostMapping("/user/client/add")
    public ResponseEntity<CreateClientRequestDto> createClientProfile(@Valid @RequestBody CreateClientRequestDto client){
        auditLog.info("ADMIN_CREATE_CLIENT phoneNumber={}", client.getPhoneNumber());
        adminService.createClientProfile(client);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/user/client/list")
    public ResponseEntity<Page<ClientProfileDto>> getAllClientProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
        auditLog.info("ADMIN_LIST_Client page={} size={} sortBy={} direction={}",
                page, size, sortBy, direction);
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ClientProfileDto> clients = adminService.findAllClientProfiles(pageable);
        return ResponseEntity.ok(clients);
    }
    @Operation(
            summary = "DELETE user by id",
            description = "Removes user together with child instances such as interpreter profile",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "No user found")
            }
    )
    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        auditLog.info("ADMIN_DELETE_USER userId={}", id);
        adminService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/user/update/{id}")
    public ResponseEntity<Void> updateUserStatus(@PathVariable Long id, @RequestBody StatusUpdateDto status){
        auditLog.info("ADMIN_UPDATE_USER userId={}", id);
        adminService.updateUserIsVerified(id, status.getStatus());
        return ResponseEntity.noContent().build();
    }


    // Call topic + Finished
    @Operation(
            summary = "Get paginated list of call topics",
            description = "Returns a paginated list of call topics including audit fields. Newly created topics are inactive by default.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Paginated list of call topics",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class))),
            }
    )
//  GET /call/topic/list?page=0&size=5&sortBy=name&direction=asc
    @GetMapping("/call/topic/list")
    public ResponseEntity<Page<CallTopic>> getAllTopics(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(defaultValue = "topicName") String sortBy,
          @RequestParam(defaultValue = "asc") String direction
    ) {
        auditLog.info("ADMIN_LIST_CALL_TOPIC page={} size={} sortBy={} direction={}",
                page, size, sortBy, direction);
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CallTopic> topicList = adminService.getAllTopics(pageable);
        return ResponseEntity.ok(topicList);
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
    public ResponseEntity<CallTopicDto> addTopic(@Valid @RequestBody CallTopicDto topic) {
        auditLog.info("ADMIN_CREATE_CALL_TOPIC name={}", topic.getName());
        adminService.createNewTopic(topic);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Update call topic partially by id",
            description = "Updates topic name and/or active status. Only provided fields are modified.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Call topic updated successfully",
                            content = @Content(schema = @Schema(implementation = UpdateCallTopicRequest.class))),
                    @ApiResponse(responseCode = "409", description = "Topic name already exists"),
                    @ApiResponse(responseCode = "404", description = "No call topic found with id number")
            }
    )
    @PutMapping("/call/topic/edit/{id}")
    public ResponseEntity<Void> editTopic(
            @RequestBody UpdateCallTopicRequest topic,
            @PathVariable Long id) {
        auditLog.info("ADMIN_UPDATE_CALL_TOPIC id={} name={} active={}",
                id, topic.getName(), topic.getIsActive());
        adminService.updateTopic(id, topic.getName(), topic.getIsActive());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(
            summary = "Delete call topic by id",
            description = "Deletes call topic with id.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Call topic deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "No call topic found with id number")
            }
    )
    @DeleteMapping("/call/topic/delete/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id){
        auditLog.info("ADMIN_DELETE_CALL_TOPIC id={}", id);
        adminService.deleteTopicById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Languages + Finished
    @Operation(
            summary = "Creates new language",
            description = "Creates new language with name",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Language created successfully",
                            content = @Content(schema = @Schema(implementation = LanguageRequestDto.class))),
                    @ApiResponse(responseCode = "409", description = "Language name already exists"),
            }
    )
    @PostMapping("/language/add")
    public ResponseEntity<LanguageRequestDto> addNewLanguage(@Valid @RequestBody LanguageRequestDto language) {
        auditLog.info("ADMIN_CREATE_LANGUAGE name={}", language.getName());
        adminService.createNewLanguage(language);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Returns all languages as list",
            description = "Returns all languages with audit data.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lists all languages",
                            content = @Content(schema = @Schema(implementation = Language.class))),
            }
    )
    @GetMapping("/language/list")
    public ResponseEntity<Page<Language>> getAllLanguages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        auditLog.info("ADMIN_LIST_LANGUAGE page={} size={} sortBy={} direction={}",
                page, size, sortBy, direction);
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Language> languages = adminService.findAllLanguages(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(languages);
    }

    @PutMapping("/language/edit/{id}")
    public ResponseEntity<Void> editLanguage(
            @RequestBody  LanguageRequestDto language,
            @PathVariable Long id) {
        auditLog.info("ADMIN_EDIT_LANGUAGE id={}", id);
        adminService.updateLanguage(id, language.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/language/delete/{id}")
    public ResponseEntity<Void> deleteLanguage(@PathVariable Long id) {
        auditLog.info("ADMIN_DELETE_LANGUAGE id={}", id);
        adminService.deleteLanguage(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
