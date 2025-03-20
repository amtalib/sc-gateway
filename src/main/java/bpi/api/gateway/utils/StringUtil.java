package bpi.api.gateway.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;

import java.util.Set;

/***
 * @author Abdulhamid M. Talib
 * @since 02-16-2012
 * @version 1.0
 */
public final class StringUtil {

	private static Map<Character, Character> specialCharMap = new HashMap<Character, Character>();

	static {
		char[] scArr = new char[] { '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '=', '-', '~', '`', '{',
				'[', '}', ']', '|', '\\', ':', ';', '"', '\'', '<', ',', '>', '.', '?', '/' };

		for (char c : scArr) {
			specialCharMap.put(c, c);
		}
	} // End method

	public static boolean hasSpecialCharacter(String str) {

		if (str == null || str.trim().isEmpty()) {
			return false;
		}

		String s = str;

		for (int i = 0; i < s.length(); i++) {

			if (specialCharMap.get(s.charAt(i)) != null) {
				return true;
			}
		}

		return false;
	}
	
	public static boolean hasSpecialCharacterWithExceptions(String str, char[] chars) {
		
		if (str == null || str.trim().isEmpty() ) {
			return false;
		}
		
		String s = str;

		for (int i = 0; i < s.length(); i++) {
			
			boolean found = false;
			for (int j = 0; j < chars.length; j++) {
				if(chars[j] == s.charAt(i)) {
					found = true;
					break;
				}
			}
			
			if(found) {
				continue;
			}
			
			if (specialCharMap.get(s.charAt(i) ) != null) {
				return true;
			}
		}

		return false;
	}

	public static boolean hasLowerCaseLetter(String str) {
		if (str == null || str.trim().isEmpty()) {
			return false;
		}

		String s = str;

		for (int i = 0; i < s.length(); i++) {

			if (Character.isLetter(s.charAt(i)) && Character.isLowerCase(s.charAt(i))) {
				return true;
			}
		}

		return false;
	}

	public static boolean hasUpperCaseLetter(String str) {
		if (str == null || str.trim().isEmpty()) {
			return false;
		}

		String s = str;

		for (int i = 0; i < s.length(); i++) {

			if (Character.isLetter(s.charAt(i)) && Character.isUpperCase(s.charAt(i))) {
				return true;
			}
		}

		return false;
	}

	public static boolean hasLetter(String str) {

		if (str == null || str.trim().isEmpty()) {
			return false;
		}

		String s = str;

		for (int i = 0; i < s.length(); i++) {

			if (Character.isLetter(s.charAt(i))) {
				return true;
			}
		}

		return false;
	}

	public static boolean hasDigit(String str) {

		if (str == null || str.trim().isEmpty()) {
			return false;
		}

		String s = str;

		for (int i = 0; i < s.length(); i++) {

			// If we find a non-digit character we return false.
			if (Character.isDigit(s.charAt(i))) {
				return true;
			}
		}

		return false;
	}
	
	public static String escape(String raw) {
	    String escaped = raw;
	    escaped = escaped.replace("\\", "\\\\");
	    escaped = escaped.replace("\"", "\\\"");
	    escaped = escaped.replace("\b", "\\b");
	    escaped = escaped.replace("\f", "\\f");
	    escaped = escaped.replace("\n", "\\n");
	    escaped = escaped.replace("\r", "\\r");
	    escaped = escaped.replace("\t", "\\t");
	    // TODO: escape other non-printing characters using uXXXX notation
	    return escaped;
	}
	
	public static String formatStringToUpperAndUnderscoreSeparated(String str) {
		if (str == null) {
			return "";
		}
		
		return str.toUpperCase().replace(' ', '_');
	}
	
	public static String formatStringToUpperAndUnderscoreSeparated(String prefix, String str) {
		if (prefix == null) {
			prefix = "";
		}
		
		return prefix + "_" + formatStringToUpperAndUnderscoreSeparated(str);
	}
	
	public static String toCamelCase(String str) {
		if (str == null) {
			return "";
		}
		
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str.replace(' ', '_') );
	}

	public static boolean isFirstLetterOfEveryWordCapitalized(String str) {
		return WordUtils.capitalize(str).equals(str);
	}
	
	private final static int levenshteinDistanceThreshold = 1; 
	
	public static String getClosestStringAmongStrings(String str, Set<String> strSet) {
		Map<Integer, String> sortedStringDistanceEvaluationMap = Maps.newTreeMap();
		
		strSet.forEach(strToCompare -> {
			int evaluatedlevenshteinDistance = StringUtils.getLevenshteinDistance(str.toLowerCase(), strToCompare.toLowerCase() );
			
			if (evaluatedlevenshteinDistance <= levenshteinDistanceThreshold ) {
				sortedStringDistanceEvaluationMap.put(evaluatedlevenshteinDistance, strToCompare);
			}
		});
		
		if (sortedStringDistanceEvaluationMap.size() > 0) {
			return sortedStringDistanceEvaluationMap.entrySet().stream().findFirst().get().getValue();
		} else {
			return null;
		}
	}
	
	public static boolean isDecimal(String str) {
		return str.matches("(\\+|-)?([0-9]+(\\.[0-9]+))");
	}

	public static long stringToLong(String str) {

		if (str == null || str.trim().isEmpty()) {
			return 0;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			// If we find a non-digit character we return false.
			if (Character.isDigit(str.charAt(i))) {
				sb.append(str.charAt(i));
			}
		}

		if (sb.length() > 0) {
			return Long.valueOf(sb.toString());
		}

		return 0;
	}

	public static boolean isSpecialCharsExists(String str) {

		if (str == null || str.isEmpty()) {
			return false;
		}

		for (char c : str.toCharArray()) {

			if (Character.isWhitespace(c) && !Character.isSpaceChar(c)) {
				return true;
			}
		}

		return false;
	}

	public static int countExtendedAsciiChars(String str) {

		if (str == null || str.isEmpty()) {
			return 0;
		}

		int counter = 0;

		for (char c : str.toCharArray()) {
			if (Integer.valueOf(c) >= 128) {
				counter++;
			}
		}

		return counter;
	}

	public static String paddRightZero(int numOfZero, String str) {

		if (str != null) {
			int n = numOfZero - str.length();

			for (int x = 0; x < n; x++) {
				str = str + "0";
			}
		}

		return str;
	}

	public static String paddLeftZero(int numOfZero, String str) {

		if (str == null) {
			str = "";
		}

		int n = numOfZero - str.length();

		for (int x = 0; x < n; x++) {
			str = "0" + str;
		}

		return str;
	}

	public static String paddLeftSpace(int numOfSpace, String str) {

		if (str == null) {
			str = "";
		}

		int n = numOfSpace - str.length();

		for (int x = 0; x < n; x++) {
			str = " " + str;
		}

		return str;
	}

	public static String paddRightSpace(int numOfSpace, String str) {

		if (str == null) {
			str = "";
		}

		int n = numOfSpace - str.length();

		for (int x = 0; x < n; x++) {
			str = str + " ";
		}

		return str;
	}

	public static boolean isNullOrEmpty(String str) {
		if (isNull(str) || isEmpty(str)) {
			return true;
		}

		return false;
	}
	
	public static boolean isNullOrEmptyOrNA(String str) {
		if (isNull(str) || isEmpty(str) || "NA".equalsIgnoreCase(str.trim() ) ) {
			return true;
		}
	
		return false;
	}

	public static boolean isNullOrEmptyOrNAORHasNoLetterOrDigits(String str) {
		if (isNull(str) || isEmpty(str) || "NA".equalsIgnoreCase(str.trim() ) ) {
			return true;
		}
		
		boolean hasAlphanumeric = false;
		for (int i = 0; i < str.length(); i++) {
			if (Character.isLetterOrDigit(str.charAt(i) ) ) {
				hasAlphanumeric = true;
			}
		}
		
		if (hasAlphanumeric == false) {
			return true;
		}

		return false;
	}
	
	
	public static boolean isEmpty(String str) throws NullPointerException {
		if (str.trim().isEmpty()) {
			return true;
		}

		return false;
	}

	public static boolean isNull(String str) {
		if (str == null) {
			return true;
		}

		return false;
	}

	public static boolean isNumeric(String str) {

		if (str == null || str.trim().isEmpty()) {
			return false;
		}

		String s = str;
		int dotCounter = 0;

		for (int i = 0; i < s.length(); i++) {
			// If we find a non-digit character we return false.
			if (s.charAt(i) == '.') {

				if (dotCounter > 0) {
					return false;
				}

				dotCounter++;
				continue;
			}

			if (!Character.isDigit(s.charAt(i))) {
				return false;
			}
		}

		return true;
	} // End method
	
	public static boolean isNumeric2(String str) {
		if (str == null) {
	        return false;
	    }
	    try {
	        Double.parseDouble(str);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	
	public static int countInstance(char x, String str) {
		int instance = 0;

		if (str == null || str.isEmpty()) {
			return instance;
		}

		for (char c : str.toCharArray()) {
			if (c == x) {
				instance++;
			}
		}

		return instance;
	}

	public static String paddRight(int numPadds, String padd, String str) {

		if (str == null) {
			str = "";
		}

		int n = numPadds - str.length();

		for (int x = 0; x < n; x++) {
			str = str + padd;
		}

		return str;
	}

	public static String paddLeft(int numPadds, String padd, String str) {

		if (str == null) {
			str = "";
		}

		int n = numPadds - str.length();

		for (int x = 0; x < n; x++) {
			str = padd + str;
		}

		return str;
	}

	public static String removeExtendedChars(String str) {

		if (str == null) {
			str = "";
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) < 32) {
				continue;
			}

			if (str.charAt(i) > 125) {
				continue;
			}

			sb.append(str.charAt(i));
		}

		return sb.toString().trim();
	} // End method

	public static String removeNonAlphaChars(String str) {

		if (str == null) {
			str = "";
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) < 65) {
				continue;
			}

			if (str.charAt(i) > 90) {
				if (str.charAt(i) < 97) {
					continue;
				}

				if (str.charAt(i) > 122) {
					continue;
				}
			}

			sb.append(str.charAt(i));
		}

		return sb.toString().trim();
	} // End method

	public static String removeNonAlphaNumericChars(String str) {

		if (str == null) {
			str = "";
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) < 65) {
				if (str.charAt(i) < 48) {
					continue;
				}

				if (str.charAt(i) > 57) {
					continue;
				}
			}

			if (str.charAt(i) > 90) {
				if (str.charAt(i) < 97) {
					continue;
				}

				if (str.charAt(i) > 122) {
					continue;
				}
			}

			sb.append(str.charAt(i));
		}

		return sb.toString().trim();
	} // End method

	public static String removeNonPathChars(String str) {

		if (str == null) {
			str = "";
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {

			// Exclude dash and underscore
			if (str.charAt(i) == 45 || str.charAt(i) == 95) {
				sb.append(str.charAt(i));
				continue;
			}

			if (str.charAt(i) < 65) {
				if (str.charAt(i) < 48) {
					continue;
				}

				if (str.charAt(i) > 57) {
					continue;
				}
			}

			if (str.charAt(i) > 90) {
				if (str.charAt(i) < 97) {
					continue;
				}

				if (str.charAt(i) > 122) {
					continue;
				}
			}

			sb.append(str.charAt(i));
		}

		return sb.toString().trim();
	} // End method
	
	public static String removeNonNumericChars(String str) {
		if (str == null) {
			return null;
		}
		
		return str.replaceAll("[^\\d.]", "");
	}

	public static HashMap<String, Object> convertJsonToMap(String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
		};

		HashMap<String, Object> map = mapper.readValue(json, typeRef);

		return map;
	} // End method

	public static String convertMapToJson(HashMap<String, Object> map) throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		String json = "";

		// Convert map to JSON string
		json = mapper.writeValueAsString(map);
		json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);

		return json;
	} // End method

	public static String trimNumeric(String num) {
		if (num == null) {
			return null;
		}

		String result = "";
		boolean start = false;

		for (int i = 0; i < num.length(); i++) {
			if ((num.charAt(i) != '0') && (!start)) {
				start = true;
			}

			if (start) {
				result += num.charAt(i);
			}
		}

		return result;
	}

	public static String mask(char mask, int maskPercentage, String accountNumber) {
		if (accountNumber == null) {
			return null;
		}

		if (maskPercentage <= 0 || maskPercentage > 100) {
			return null;
		}

		int len = accountNumber.length();

		if (len < 2) {
			return accountNumber;
		}

		int percentageInt = Double.valueOf(Math.floor(Double.valueOf(len) * (Double.valueOf(maskPercentage) / 100)))
				.intValue();

		StringBuilder sb = new StringBuilder();

		for (int x = 0; x < percentageInt; x++) {
			sb.append(mask);
		}

		sb.append(accountNumber.substring(percentageInt));

		return sb.toString();
	} // End method

	public static String urlDecode(String str) {
		if (str == null) {
			return null;
		}

		String decodedString = "";

		if (str.trim().isEmpty()) {
			return decodedString;
		}

		try {
			decodedString = URLDecoder.decode(str, StandardCharsets.UTF_8.name());
		} catch (Exception e) {
		}

		return decodedString;
	}

	public static String urlEncode(String str) {
		if (str == null) {
			return null;
		}

		String encodedString = "";

		if (str.trim().isEmpty()) {
			return encodedString;
		}

		try {
			encodedString = URLEncoder.encode(str, StandardCharsets.UTF_8.name());
		} catch (Exception e) {
		}

		return encodedString;
	}

	public static boolean isValidUUID(String str) {

		if (str == null || str.trim().isEmpty() ) {
			return false;
		}

		String s = str;

		for (int i = 0; i < s.length(); i++) {

			if (specialCharMap.get(s.charAt(i)) != null) {
				return false;
			}
		}
		
		return (str.length() == 32);
	}
	
	public static boolean isValidEmail(String str) {
        if (str == null) {
            return false;
        }
        
        String strPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        
        Pattern pattern = Pattern.compile(strPattern);
        
        return pattern.matcher(str).matches();
    }
	
	public static boolean isValidCellNum(String str) {
        if (str == null) {
            return false;
        }
        
        if (StringUtil.isNumeric(str) == false) {
            return false;
        }

        if (str.startsWith("9") == false) {
            
            if (str.startsWith("09") == false) {
                if (str.startsWith("639") == false) {
                    return false;
                    
                } else if (str.length() != 12) {
                    return false;
                }
                
            } else if (str.length() != 11) {
                return false;
            }
            
        } else if (str.length() != 10) {
            return false;
        }
        
        return true;
    }
	
	public static Integer parseStrToNum(String str, String postfix) {
        if (str.isEmpty() || str == null) {
            return null;
        }
       
        Pattern pattern = Pattern.compile("[0-9]+");
        postfix = postfix.toLowerCase();
       
        if (postfix != null) {
            pattern = Pattern.compile("[0-9]+" + postfix);
        }
       
        Matcher matcher = pattern.matcher(str.toLowerCase() );
       
        if (matcher.find() ) {
            String strMatch = matcher.group();
           
            if (isNumeric(strMatch) ) {
                return Integer.parseInt(strMatch);
               
            } else {
                return Integer.parseInt(strMatch.replaceAll(postfix, "") );
            }
        }
       
        return null;
    }
	
	public static String processValOrDot(String val) {
		return StringUtil.isNullOrEmpty(val) ? "." : val;
	}
	
	public static String removeSpecialCharacters(String str) {
		if (str != null) {
			return str.replaceAll("\\W", "");
		}
		
		return null;
	}
	
	public static int getWordCount(String str) {
		String trim = str.trim();
		
		if (trim.isEmpty())
		    return 0;
		
		return trim.split("\\s+").length;
	}
	
	public static BigDecimal getDecimalValue(String str) {
		if (StringUtil.isNullOrEmpty(str) ) {
			return new BigDecimal("0.00");
		}
		
		String trim = str.trim();
		
		if (StringUtil.isNullOrEmpty(trim) ) {
			return new BigDecimal("0.00");
		}
		
		if (trim.indexOf("-") > -1) {
			trim = trim.replace("-", "");
		}
	 
		if (trim.indexOf(",") > -1) {
			trim = trim.replace(",", "");
		}
		
		if (trim.indexOf("'") > -1) {
			trim = trim.replace("'", "");
		}
		
		try {
			return new BigDecimal(trim);
		} catch (Exception e) {}
		
		return new BigDecimal("0.00");
	}
	
	
	public static void main(String[] args) {
		
		X subj = new X();
		subj.setFirstName("KEVIN");
		subj.setLastName("GARCIA");
		subj.setMiddleName("ALFRED");
		
		StringBuilder personSB = new StringBuilder();
		
		personSB.append( (subj.getFirstName() != null ? subj.getFirstName().toUpperCase() : "" )  );
		personSB.append( (subj.getMiddleName() != null ? subj.getMiddleName().toUpperCase() : "" )  );
		personSB.append( (subj.getLastName() != null ? subj.getLastName().toUpperCase() : "" )  );
		 
		Map<String, String> personsToInvestigateSet = new HashMap<String, String>();
		personsToInvestigateSet.put(personSB.toString(), personSB.toString() );
		
		// ---------------------------------------------
		personSB = new StringBuilder();
		subj = new X();
		subj.setFirstName("KEVINs");
		subj.setLastName("GARCIA");
		subj.setMiddleName("ALFRED");
		
		personSB.append( (subj.getFirstName() != null ? subj.getFirstName().toUpperCase() : "" )  );
		personSB.append( (subj.getMiddleName() != null ? subj.getMiddleName().toUpperCase() : "" )  );
		personSB.append( (subj.getLastName() != null ? subj.getLastName().toUpperCase() : "" )  );
		
		if (personsToInvestigateSet.get(personSB.toString() ) != null) {
			System.out.println("Double");
		}
		else {
			System.out.println("Unique");
		}
	}

}

class X {
	private String firstName;
	private String middleName;
	private String lastName;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
}