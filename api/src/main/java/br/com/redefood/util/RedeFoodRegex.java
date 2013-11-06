package br.com.redefood.util;
/**
 * 
 * @author pulu
 * Class with methods to verify if a string matches a regular expression.
 */
public class RedeFoodRegex {

	/**
	 * Method to verify if a subdomain only contains alphabetical charactes (letters).
	 * No other characters are allowed.
	 * @param subdomain
	 * @return
	 */
	public static boolean verifySubDomain(String subdomain) {

		if (subdomain.matches("^[a-zA-Z]+$"))
			return true;
		else
			return false;
	}
}
