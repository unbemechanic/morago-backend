package morago.configuration.network;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

@Component
public class QueryCounterInterceptor implements StatementInspector {

    @Override
    public String inspect(String sql){
        QueryCountFilter.increment();
        return sql;
    }
}
