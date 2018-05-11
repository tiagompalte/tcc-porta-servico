package br.com.utfpr.porta.util;

import java.io.UnsupportedEncodingException;

import org.apache.logging.log4j.util.Strings;

public class Util {
	
	private Util() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static boolean verificarStringUTF8(String str) throws UnsupportedEncodingException {
		
		if(Strings.isEmpty(str)) {
			throw new NullPointerException();
		}
		
		byte[] utf8Bytes = str.getBytes("UTF8");
        byte[] defaultBytes = str.getBytes();

        String utf8String = new String(utf8Bytes, "UTF8");
        String defaultString = new String(defaultBytes);
        
        return utf8String.compareTo(defaultString) == 0;			
	}
	
	public static String converterUTF8toISO88591(String str) throws UnsupportedEncodingException {
		
		if(Strings.isEmpty(str)) {
			throw new NullPointerException();
		}
		
		if(!verificarStringUTF8(str)) {
			throw new IllegalArgumentException();
		}
		
		return new String(str.getBytes("UTF-8"), "ISO-8859-1");
	}

}
