/**
* COPYRIGHT : yesterday is yesterday, today is today.
*/
package main;

import java.util.Random;

public class Utility {

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	public Utility() {
	}

	public String generateRandomKey() {
		Random r = new Random();
//		return Long.toHexString(r.nextLong()) + Integer.toHexString(r.nextInt()).substring(0, 4);
		return "81cfb457f1101c3b06f6";
	}

	public String XOR(String s1, String s2) {
		s2 = Integer.toString(Integer.parseInt(s2, 16));
		byte[] a = s1.getBytes();
		byte[] b = s2.getBytes();
		byte[] out = new byte[a.length];
		for (int i = 0; i < a.length; i++) {
			out[i] = (byte) (a[i] ^ b[i % b.length]);
		}
		System.out.println(bytesToHex(out));
		return new String(out);
	}
	
	public int XOR(Integer int1, Integer int2) {
		int xored = int2 ^ int1;
		return xored;
	}
	
	public String OR(String s1, String s2) {
		byte[] a = s1.getBytes();
		byte[] b = s2.getBytes();
		byte[] out = new byte[a.length];
		for (int i = 0; i < a.length; i++) {
			out[i] = (byte) (a[i] | b[i % b.length]);
		}
		return new String(out);
	}
	
	public Integer OR(Integer int1, Integer int2) {
		int xored = int2 | int1;
		return xored;
	}
	
	public String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public int hexToInt(String string) {
		return Integer.parseInt(string, 16);
	}
	
	public String intToHex(int num) {
		String str = Integer.toHexString(num);
		while(str.length()<4) {
			str = '0' + str;
		}
		return str;
	}

	public String strToHex(String str) {
		return bytesToHex(str.getBytes());
	}
	
	public int[] intToBit(int num) {
	    int [] bits = new int [4];
	    for (int i = 3; i >= 0; i--) {
	        bits[i] = ((num & (1 << i)) != 0) ? 1 : 0;
	    }
	    
		 return bits;
	}
	
	
}
