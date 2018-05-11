package br.com.utfpr.porta.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.Test;

public class Conversor {

	@Test
	public void verificarStringUTF8Test() throws UnsupportedEncodingException {		
		String utf8 = new String("áàäãâéèëêíìîïóòôõöúùûüç".getBytes(), "UTF8");
		assertEquals(Util.verificarStringUTF8(utf8), true);
	}
	
}
