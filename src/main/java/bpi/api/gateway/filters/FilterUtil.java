package bpi.api.gateway.filters;

import java.util.List;

import org.springframework.web.server.ServerWebExchange;

public class FilterUtil {

	public static String getHeader(ServerWebExchange exchange, String header) {
		String headerValue = null;
		
		if (exchange != null) {
			List<String> headers = exchange.getRequest().getHeaders().get(header);
			
			if (headers != null && headers.size() > 0) {
				headerValue = headers.get(0);	
			}
		}
		
		return headerValue;
	} // End method
	
}
