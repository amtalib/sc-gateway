package bpi.api.gateway.filters.ratelimiter;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import bpi.api.gateway.filters.FilterUtil;
import bpi.api.gateway.filters.headers.HeaderValidationFilter;
import bpi.api.gateway.filters.logger.LoggingFilter;
import reactor.core.publisher.Mono;

/**
 * @author Abdulhamid Talib
 * @since 2025-03-11
 * 
 * A custom resolver for identifying a user.
 * */
@Primary
@Component("rateLimiterResolver")
public class RateLimiterResolver implements KeyResolver, Ordered {

	// TODO Log to File and DB
	private static final Logger logger = LoggerFactory.getLogger(RateLimiterResolver.class);
	
	@Override
	public int getOrder() {
		return -1; // Ensures RateLimiter happens after LoggingFilter and HeaderValidationFilter
	} // End method
	
	/**
	 * Precondition: 
	 * 	1. api-token was already decoded.
	 * 	2. JWT.iss claim was attached in the header.  
	 * */
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
    	try {
	    	String tranRefNo = FilterUtil.getHeader(exchange, LoggingFilter.HEADER_NAME_TRAN_REF_NO);
	    	
	    	// TODO Change to JWT.iss claim
	    	String iss = FilterUtil.getHeader(exchange, HeaderValidationFilter.HEADER_NAME_JWT_ISS);
	    	
	    	if (iss != null && !iss.isBlank() ) {
	    		RateLimiterResolver.logger.info("Identifying KeyResolver for TraRefNo [{}] using the JWT.issuer.", tranRefNo, iss);
	    		
		        return Mono.just(iss);
	    	}
	    	else {
	    		RateLimiterResolver.logger.info("Identifying KeyResolver for TraRefNo [{}] using RemoteIP Address", tranRefNo);
	    		
		        return Optional.ofNullable(exchange.getRequest().getRemoteAddress() )
		            .map(InetSocketAddress::getAddress)
		            .map(InetAddress::getHostAddress)
		            .map(Mono::just)
		            .orElse(Mono.empty()
		           );
	    	} 
    	} catch (Exception e) {
    		// TODO Log to file and db.
			e.printStackTrace();
		} 
     
        return Optional.ofNullable(exchange.getRequest().getRemoteAddress() )
	            .map(InetSocketAddress::getAddress)
	            .map(InetAddress::getHostAddress)
	            .map(Mono::just)
	            .orElse(Mono.empty()
	           );
    } // End method
    
}
