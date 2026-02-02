package morago.configuration.network;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class QueryCountFilter extends OncePerRequestFilter {

    private static final ThreadLocal<Integer> queryCount = ThreadLocal.withInitial(() -> 0);

    public static void increment() {
        queryCount.set(queryCount.get() + 1);
    }

    public static int getCount() {
        return queryCount.get();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        queryCount.set(0);

        filterChain.doFilter(request, response);

        log.info("DB Queries for {}: {}", request.getRequestURI(), getCount());
        queryCount.remove();
    }
}
