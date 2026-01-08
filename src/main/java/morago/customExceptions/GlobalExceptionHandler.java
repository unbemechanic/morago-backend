package morago.customExceptions;

import jakarta.servlet.http.HttpServletRequest;
import morago.customExceptions.call.*;
import morago.customExceptions.interpreter.NoInterpreterFoundException;
import morago.customExceptions.interpreter.ProfileExistsException;
import morago.customExceptions.language.InvalidLanguageException;
import morago.customExceptions.language.LanguageExistsException;
import morago.customExceptions.password.*;
import morago.customExceptions.role.InvalidRoleAssigment;
import morago.customExceptions.role.InvalidRoleException;
import morago.customExceptions.role.RoleNotFoundException;
import morago.customExceptions.token.ExpiredJwtTokenException;
import morago.customExceptions.token.RefreshTokenNotFoundException;
import morago.customExceptions.wallet.WalletStateException;
import morago.dto.exceptions.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PhoneNumberAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handlePhoneNumberAlreadyExistsException(
            PhoneNumberAlreadyExistsException ex,
            HttpServletRequest request)
    {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleEnumBindingError(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        String msg = ex.getMessage();

        if (msg != null && msg.contains("Cannot coerce empty String")) {
            return build(HttpStatus.BAD_REQUEST,"Role cannot be empty", request);
        }

        if (msg != null && msg.contains("RoleEnum")) {
            String invalidRole = extractInvalidEnumValue(msg);
            return build(HttpStatus.BAD_REQUEST,
                    "Invalid role: " + invalidRole + " Allowed roles: INTERPRETER, CLIENT",
                    request);
        }
        return build(HttpStatus.BAD_REQUEST, "Malformed request body", request);
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidRole(
            InvalidRoleException ex,
            HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidRoleAssigment.class)
    public ResponseEntity<?> handleInvalidRoleAssigment(
            InvalidRoleAssigment ex,
            HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRoleNotFoundException(
            RoleNotFoundException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        return build(HttpStatus.BAD_REQUEST, message, request);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiErrorResponse> handleUnexpected(
//            Exception ex,
//            HttpServletRequest request
//    ) {
//        return build(
//                HttpStatus.INTERNAL_SERVER_ERROR,
//                "Unexpected server error",
//                request
//        );
//    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.UNAUTHORIZED,
                "Bad credentials, invalid username or password, please try again",
                request);
    }

    @ExceptionHandler(CallTopicExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleCallTopicExistsException(
            CallTopicExistsException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }
    @ExceptionHandler(CallNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCallNotFoundException(
            CallNotFoundException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(CallDurationException.class)
    public ResponseEntity<ApiErrorResponse> handleCallDurationException(
            CallDurationException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }
    @ExceptionHandler(SecurityWebRTCException.class)
    public ResponseEntity<ApiErrorResponse> handleSecurityWebRTCException(
            SecurityWebRTCException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    // Refresh token
    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRefreshTokenNotFoundException(
            HttpServletRequest request,
            RefreshTokenNotFoundException ex
    ){
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(ExpiredJwtTokenException.class)
    public ResponseEntity<ApiErrorResponse> handleExpiredJwtTokenException(
            ExpiredJwtTokenException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // Password reset
    @ExceptionHandler(MissingResetFieldsException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingResetFieldsException(
            MissingResetFieldsException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidResetCodeException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidResetCodeException(
            InvalidResetCodeException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(ResetPasswordTokenMissingException.class)
    public ResponseEntity<ApiErrorResponse> handleResetPasswordTokenMissingException(
            ResetPasswordTokenMissingException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(PasswordRequiredException.class)
    public ResponseEntity<ApiErrorResponse> handlePasswordRequiredException(
            PasswordRequiredException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handlePasswordMismatchException(
            PasswordMismatchException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(WeakPasswordException.class)
    public ResponseEntity<ApiErrorResponse> handleWeakPasswordException(
            WeakPasswordException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // Call topics
    @ExceptionHandler(CallTopicNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCallTopicNotFoundException(
            CallTopicNotFoundException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(NoCallTopicFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoCallTopicFoundException(
            NoCallTopicFoundException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // Language exceptions
    @ExceptionHandler(LanguageExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleLanguageExistsException(
            LanguageExistsException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidLanguageException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidLanguageException(
            InvalidLanguageException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // Interpreter Profile
    @ExceptionHandler(NoInterpreterFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoInterpreterFoundException(
            NoInterpreterFoundException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(ProfileExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleProfileExistsException(
            ProfileExistsException ex,
            HttpServletRequest request
    ){
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(WalletStateException.class)
    public ResponseEntity<ApiErrorResponse> handleWalletStateException(
        WalletStateException ex,
        HttpServletRequest request
    ){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // build helper method
    private ResponseEntity<ApiErrorResponse> build(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(status)
                .body(ApiErrorResponse.of(status, message, request.getRequestURI()));
    }

    // helper method to extract role names from msg
    private String extractInvalidEnumValue(String message) {
        // Matches: from String "TEACHER"
        Pattern pattern = Pattern.compile("from String \"(.*?)\"");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return "UNKNOWN";
    }

}


