package org.springframework.cloud.gateway.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * todo
 *
 * @author huanglb
 * @create 2018/11/12
 */
public class ElapsedFilter implements GatewayFilter, Ordered {

	private static final Log log = LogFactory.getLog(GatewayFilter.class);
	private static final String ELAPSED_TIME_BEGIN = "elapsedTimeBegin";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		exchange.getAttributes().put(ELAPSED_TIME_BEGIN, System.currentTimeMillis());
		return chain.filter(exchange).then(
				Mono.fromRunnable(() -> {
					Long startTime = exchange.getAttribute(ELAPSED_TIME_BEGIN);
					if (startTime != null) {
						log.info(exchange.getRequest().getURI().getRawPath() + ", 耗时: " + (System.currentTimeMillis() - startTime) + "ms");
					}
				})
		);
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
}
