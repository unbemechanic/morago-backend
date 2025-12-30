package morago.logging;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AdminAuditAspect {

    private static final Logger audit = LoggerFactory.getLogger("AUDIT");

    @AfterReturning(
            value = "execution(* morago.service.AdminService.*(..))"
    )
    public void auditAdmin(JoinPoint joinPoint){
        String admin = SecurityContextHolder.getContext().getAuthentication().getName();

        audit.info(
                "ADMIN_ACTION admin={} method={}",
                admin,
                joinPoint.getSignature().getName()
        );
    }
}
