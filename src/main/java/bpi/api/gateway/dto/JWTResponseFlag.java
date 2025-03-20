package bpi.api.gateway.dto;

/**
 * @author Abdulhamid M. Talib
 * @since 07-16-2014
 * @version 1.0 
 * */
public enum JWTResponseFlag {

	JWT001 {
		@Override
		public String getDescription() {
			return "Transaction successful.";
		}
	}, JWT100 {
		@Override
		public String getDescription() {
			return "Transaction failed. Invalid request.";
		}
	}, JWT101 {
		@Override
		public String getDescription() {
			return "Transaction failed. Invalid username.";
		}
	}, JWT102 {
		@Override
		public String getDescription() {
			return "Transaction failed. Invalid password.";
		}
	}, JWT200 {
		@Override
		public String getDescription() {
			return "Transaction failed. Suspended account.";
		}
	}, JWT996 {
		@Override
		public String getDescription() {
			return "Bad request.";
		}
	}, JWT997 {
		@Override
		public String getDescription() {
			return "Authorization failed.";
		}
	}, JWT998 {
		@Override
		public String getDescription() {
			return "Authentication failed.";
		}
	}, JWT999 {
		@Override
		public String getDescription() {
			return "Internal server error.";
		}
	};       
	
	public abstract String getDescription();
	
}
