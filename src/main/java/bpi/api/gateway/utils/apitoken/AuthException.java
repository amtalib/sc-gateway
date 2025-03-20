package bpi.api.gateway.utils.apitoken;

/**
 * @author AMTalib
 * @since 09/10/2019
 * */
public class AuthException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5003197886546978705L;

	private String className;
	private String methodName;
	
	private String uri;
	private String tranRefNo;
	private String field;
	
	public AuthException(Throwable throwable) {
		super(throwable);
	}

	public AuthException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public AuthException(String msg) {
		super(msg);
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getTranRefNo() {
		return tranRefNo;
	}

	public void setTranRefNo(String tranRefNo) {
		this.tranRefNo = tranRefNo;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}



	public static class Builder {
		private String msg;
		private String tranRefNo;
		private String uri;
		private String field;
		private String className;
		private String methodName;

		public Builder(String msg) {
			this.msg = msg;
		}

		public Builder tranRefNo(String tranRefNo) {
			this.tranRefNo = tranRefNo;
			return this;
		}
		
		public Builder className(String className) {
			this.className = className;
			return this;
		}
		
		public Builder methodName(String methodName) {
			this.methodName = methodName;
			return this;
		}

		public Builder uri(String uri) {
			this.uri = uri;
			return this;
		}
		
		public Builder field(String field) {
			this.field = field;
			return this;
		}

		public AuthException build() {
			AuthException ve = new AuthException(this.msg);
			ve.setUri(this.uri);
			ve.setTranRefNo(this.tranRefNo);
			ve.setField(this.field);
			ve.setClassName(className);
			ve.setMethodName(methodName);
			
			return ve;
		}
	} // End builder class

}
