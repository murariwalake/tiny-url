package com.murariwalake.tinyurl.util;

public class Util {

	public static String decimalToBase62(long decimal) {
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		int base = characters.length();
		String result = "";
		while (decimal > 0) {
			result = characters.charAt((int) (decimal % base)) + result;
			decimal = decimal / base;
		}
		return result;
	}

	public static long random() {
		return (long) (Math.random() * 100000L);
	}
}
