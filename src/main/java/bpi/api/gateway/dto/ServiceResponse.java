package bpi.api.gateway.dto;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/***
 * @author amtalib
 * @since 07-04-2014
 * @version 1.0
 * */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponse<E> {

	/**
	 * 0 : FAIL, -1 : ERROR, 1 : SUCCESS
	 * */
	private E data;
	private String flag = ResponseStatus.SUCCESS.getCode();
	private String message = ResponseStatus.SUCCESS.getMessage();

	private String tranRefNo;

	public String getTranRefNo() {
		return tranRefNo;
	}

	public void setTranRefNo(String tranRefNo) {
		this.tranRefNo = tranRefNo;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	@JsonIgnore
	public void setStatus(ResponseStatus responseStatus) {
		if (responseStatus != null) {
			this.flag = responseStatus.getCode();
			this.setMessage(responseStatus.getMessage() );
		}
	}

	  

	@JsonIgnore
	public void setStatus(JWTResponseFlag rf) {
		if (rf != null) {
			this.flag = rf.name();
			this.setMessage(rf.getDescription() );
		}
	}
	
	@JsonIgnore
	public void setStatus(JWTResponseFlag rf, String msg) {
		if (rf != null) {
			this.flag = rf.name();
			this.setMessage(rf.getDescription() );
			this.appendToMessage(msg);
		}
	}
	
	@JsonIgnore
	public void setStatusToException(JWTResponseFlag rf, String msg, String dataStr) {
		if (rf != null) {
			this.flag = rf.name();
			this.setMessage(rf.getDescription());
			this.appendToMessage("Local Message[" + msg + "]");
			this.appendToMessage("Data[" + dataStr + "]");
		}
	}
	
	public E getData() {
		return data;
	}

	public void setData(E data) {
		this.data = data;

		if (data == null) {
			return;
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@JsonIgnore
	public void appendToMessage(String message) {
		if (this.message != null) {
			this.message += " " + message;
		} else {
			this.message = message;
		}
	} // End method

	@JsonIgnore
	public void appendToMessage(Throwable e) {
		if (e != null) {

			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw) );

			if (this.message != null) {
				this.message += ". " + sw.toString();
			} else {
				this.message = sw.toString();
			}
		}
	} // End method

	@Override
	public String toString() {
		try {
			return new com.fasterxml.jackson.databind.ObjectMapper()
					.writerWithDefaultPrettyPrinter().writeValueAsString(this);
		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	} // End method

}
