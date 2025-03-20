package bpi.api.gateway.filters.logger;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * @author Abdulhamid Talib
 * @since 2025-03-07
 * 
 *        A custom filter that assigns tranRefNo and logs requests, response, and exception.
 */
@Component
public class LoggingFilter implements GlobalFilter, Ordered {
	
	// TODO Log to File and DB
	private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
	
	public static String HEADER_NAME_TRAN_REF_NO = "X-TranRef-No";
	
	@Value("${logging.request.headers_enabled}")
	private Boolean loggingRequestHeadersEnabled;
	
	@Value("${logging.request.activities_enabled}")
	private Boolean loggingRequestActivitiesEnabled;
	
	@Value("${logging.response.headers-enabled}")
	private Boolean loggingResponseHeadersEnabled;
	
	@Value("${logging.response.activities-enabled}")
	private Boolean loggingResponseActivitiesEnabled;
	
	@Value("${logging.exception-enabled}")
	private Boolean loggingExceptionEnabled;
	
	/*
	 * Assign TranRefNo and logs the request.
	 * */
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		long startTime = System.currentTimeMillis();
		
		// Generate TranRefNo
		String tranRefNo = UUID.randomUUID().toString();
		
		// Attach tranRefNo to the headers. 
		exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.add(HEADER_NAME_TRAN_REF_NO, tranRefNo) );
		
		// Log Request Details
		// TODO Log to File and DB
		if (loggingRequestActivitiesEnabled) {
			LoggingFilter.logger.info("\n\nIncoming Request with assigned TraRefNo [{}] {} {}", tranRefNo, request.getMethod(), request.getURI() );
		}
		
		if (loggingRequestHeadersEnabled) {
			StringBuilder sb = new StringBuilder();
			request.getHeaders().forEach( (name, values) -> sb.append("\n\t" + name + " = " + values) );
			// TODO Log to File and DB			
			LoggingFilter.logger.info("TraRefNo [{}] Request Headers {}", tranRefNo, sb.toString() );
			
		}
		
		return chain.filter(exchange).doOnSuccess(aVoid -> logResponse(exchange, startTime, tranRefNo) )
				.doOnError(throwable -> logException(exchange, throwable, tranRefNo, startTime) );
	} // End method

	/*
	 * Log response.
	 * */
	private void logResponse(ServerWebExchange exchange, long startTime, String tranRefNo) {
		ServerHttpResponse response = exchange.getResponse();
		
		long duration = System.currentTimeMillis() - startTime;

		if (loggingResponseActivitiesEnabled) {
			// TODO Log to File and DB
			LoggingFilter.logger.info("Returning response. TraRefNo[{}] Response Status[{}] Duration[{}]", tranRefNo, response.getStatusCode(), duration);
		}
		
		if (loggingResponseHeadersEnabled) {
			StringBuilder sb = new StringBuilder();
			response.getHeaders().forEach( (name, values) -> sb.append("\n\t" + name + " = " + values) );
			// TODO Log to File and DB			
			LoggingFilter.logger.info("TraRefNo [{}] Response Headers {}", tranRefNo, sb.toString() );
		}
	} // End method

	/*
	 * Log exception.
	 * */
	private void logException(ServerWebExchange exchange, Throwable throwable, String tranRefNo, long startTime) {
		if (loggingExceptionEnabled) {
			long duration = System.currentTimeMillis() - startTime;
			
			// TODO Log to File and DB
			LoggingFilter.logger.error("Returning an exception. TraRefNo [{}] Method[{}] URI[{}] Duration[{}]"
						, tranRefNo
						, exchange.getRequest().getMethod()
						, exchange.getRequest().getURI()
						, duration
						, throwable
					);
		}
	} // End method

	@Override
	public int getOrder() {
		return -3; // Ensures logging happens early
	} // End method

}
