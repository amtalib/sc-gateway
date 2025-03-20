package bpi.api.gateway.utils.apitoken;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import bpi.api.gateway.utils.DateUtility;

/**
 * Pipes java console logs into a file. 
 * To pipe java console logs into a file, 
 * the line below must be invoked at the very start of a program.
 * 
 *  > System.setOut(new AMTLogger("D:\\output2.log", System.out).getStream() );
 *  
 * @author Abdulhamid M. Talib
 * @version 03-04-2012
 * */
public final class AMTLogger {
	
	public static void logMsgToFile(String path, String msg) {
		
		try {
			if (path != null) {
				File filePath = new File(path);
				BufferedWriter bw = null;
				
				try {
					if ( !filePath.getParentFile().exists() ) {
						filePath.getParentFile().mkdirs();
					}
					
					bw = new BufferedWriter(new FileWriter(filePath, true) );
					bw.append("# INFO [");
					bw.append(DateUtility.getCurrentDate("yyyy-MM-dd HH:mm:ss") );
					bw.append("] ");
					bw.append(msg);
					bw.append('\n');
					bw.flush();
				} catch (Exception e) {}
				finally {
					if (bw != null) {
						try {
							bw.close();	
						} catch (Exception e2) {}
					}
				}
			}
		} catch (Exception e) {}
	} // End method
	
	public static void logMsgToFile(String tranId, String thread, String path, String msg, boolean logToConsole) {
		try {
			if (path != null) {
				File filePath = new File(path);
				BufferedWriter bw = null;
				
				try {
					if (logToConsole == true) {
						logInfo(thread, msg);
					}
					
					if ( !filePath.getParentFile().exists() ) {
						filePath.getParentFile().mkdirs();
					}
					
					bw = new BufferedWriter(new FileWriter(filePath, true) );
					bw.append("# INFO [");
					bw.append(thread);
					bw.append(" | ");
					bw.append(tranId);
					bw.append(" | ");
					bw.append(DateUtility.getCurrentDate("yyyy-MM-dd HH:mm:ss") );
					bw.append("] ");
					bw.append(msg);
					bw.append('\n');
					bw.flush();
				} catch (Exception e) {}
				finally {
					if (bw != null) {
						try {
							bw.close();	
						} catch (Exception e2) {}
					}
				}
			}
		} catch (Exception e) {}
	} // End method
	
    public static void logInfo(String threadName, String str) {
    	System.out.println();
		System.out.println("# INFO [" 
				+ threadName
				+ " | "
				+ DateUtility.getCurrentDate("yyyy-MM-dd HH:mm:ss")
				+ "] " + str);
		
		return;
	} // End method
    
    public static void logInfo(String tranId, String threadName, String str) {
    	System.out.println();
		System.out.println("# INFO [" 
				+ threadName
				+ " | "
				+ tranId
				+ " | "
				+ DateUtility.getCurrentDate("yyyy-MM-dd HH:mm:ss")
				+ "] " + str);
		
		return;
	} // End method

	public static void logErr(String threadName, Throwable e) {
		if (e == null) {
			return;
		}

		StringWriter sw = new StringWriter();

		e.printStackTrace(new PrintWriter(sw));

		System.out.println("\n# ERROR ["
				+ threadName
				+ " | "
				+ DateUtility.getCurrentDate("yyyy-MM-DD HH:mm:ss") 
				+ "] "
				+ sw.toString() + "\n");
		
		return;
	} // End method
	
	public static void logErr(String tranId, String threadName, Throwable e) {
		if (e == null) {
			return;
		}

		StringWriter sw = new StringWriter();

		e.printStackTrace(new PrintWriter(sw));

		System.out.println("\n# ERROR ["
				+ threadName
				+ " | "
				+ tranId
				+ " | "
				+ DateUtility.getCurrentDate("yyyy-MM-DD HH:mm:ss") 
				+ "] "
				+ sw.toString() + "\n");
		
		return;
	} // End method

	public static String getErrMsg(Throwable e) {

		if (e == null) {
			return "";
		}

		StringWriter sw = new StringWriter();

		e.printStackTrace(new PrintWriter(sw));

		return sw.toString();
	} // End method


	public static String getShortErrMessage(Throwable e) {
		if (e == null) {
			return "";
		}

		List<String> causes = new ArrayList<>();
		Throwable throwable = e;
		int limit = 5;
		int count = 0;
		// get Throwable causes until null or depending on a certain limit (whichever comes first)
		while(throwable != null && count < limit) {
			// get message (ex: "Caused by: org.hibernate.exception.SQLGrammarException: could not execute statement");
			causes.add(throwable.getMessage() );
			// get cause of the current Throwable
			throwable = throwable.getCause();
			// increase count
			count++;
		}

		// Append all causes into one message
		String message = String.join("||", causes);

		return message;
	}
    
} // End of class

