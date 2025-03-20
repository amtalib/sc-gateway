package bpi.api.gateway.dto;

/***
 * @author amtalib
 * @since 07-16-2014
 * @version 1.0
 * */

public enum ResponseStatus {

	FAIL("0") {
		@Override
		public String getMessage() {
			return "Transaction failed.";
		}

		@Override
		public String getMessage(String processRefNo) {
			return "Process Ref. No. [" + processRefNo + "]. Transaction failed.";
		}
	}, ERROR("0") {
		@Override
		public String getMessage() {
			return "Transaction failed due to server error.";
		}

		@Override
		public String getMessage(String processRefNo) {
			return "Process Ref. No. [" + processRefNo + "]. Transaction failed due to server error.";
		}
	}, SUCCESS("1") {
		@Override
		public String getMessage() {
			return "Transaction successful.";
		}

		@Override
		public String getMessage(String processRefNo) {
			return "Process Ref. No. [" + processRefNo + "]. Transaction successful."; 
		}
	};

	private ResponseStatus(String code) {
		this.code = code;
	}

	private String code;
	public abstract String getMessage();
	public abstract String getMessage(String processRefNo);
	
	public String toString() {
		return this.code;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

}
