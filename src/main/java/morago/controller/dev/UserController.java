package morago.controller.dev;


import lombok.RequiredArgsConstructor;
import morago.dto.admin.client.InterpreterResponse;
import morago.dto.admin.interpreter.AdminIPResponseDto;
import morago.model.User;
import morago.security.CustomUserDetails;
import morago.service.AdminService;
import morago.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-info")
public class UserController {

    private final ClientService clientService;

    @GetMapping("/me")
    public ResponseEntity<User> me(Authentication auth){
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        return ResponseEntity.ok(principal.getUser());

    }

    @GetMapping("/interpreter/list")
    public ResponseEntity<Page<InterpreterResponse>> getAllInterpreterProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InterpreterResponse> interpreters = clientService.findAllInterpreterProfiles(pageable);
        return ResponseEntity.ok(interpreters);
    }

}
