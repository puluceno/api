package br.com.redefood.util;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class RedeFoodUtils {
    
    public static SimpleDateFormat getDateFormat() {
	return new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
    }
    
    public static String formatDate(Date date) {
	return getDateFormat().format(date);
    }
    
    public static SimpleDateFormat getDateOnlyFormat() {
	return new SimpleDateFormat("dd/MM/yyyy");
    }
    
    public static String formatDateOnly(Date date) {
	return getDateOnlyFormat().format(date);
    }
    
    public static final String doHash(String toHash) {
	StringBuffer hexString = new StringBuffer();
	
	try {
	    MessageDigest md = MessageDigest.getInstance(RedeFoodConstants.DEFAULT_HASHING_ALGORITHM);
	    md.update(toHash.getBytes());
	    byte[] digest = md.digest();
	    
	    for (byte b : digest) {
		hexString.append(String.format("%02x", b));
	    }
	    
	} catch (NoSuchAlgorithmException e) {
	    System.out.println("No such algorithm to verify the password.");
	}
	
	return hexString.toString();
    }
    
    public static String formatTime(int elapsed) {
	int ss = elapsed % 60;
	elapsed /= 60;
	int min = elapsed % 60;
	elapsed /= 60;
	int hh = elapsed % 24;
	return strZero(hh) + ":" + strZero(min) + ":" + strZero(ss);
    }
    
    private static String strZero(int n) {
	if (n < 10)
	    return "0" + String.valueOf(n);
	return String.valueOf(n);
    }
    
    /**
     * Method responsible for building an url using a given subdomain name.
     * 
     * @param subdomain
     *            Store subdomain
     * @return String URL
     */
    public static String urlBuilder(String subdomain) {
	return RedeFoodConstants.DEFAULT_URL_PREFIX.concat(subdomain).concat(RedeFoodConstants.DEFAULT_URL_SUFFIX);
    }
    
    /**
     * Method responsible for formatting a Double into pt-BR currency format.
     * 
     * @param money
     *            a {@link Double}
     * @return a formatted {@link String} containing the money symbol from pt-BR
     */
    public static String moneyFormatter(Double money) {
	NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
	return formatoMoeda.format(money);
    }
    
    /**
     * Method responsible for formatting a BigDecimal into pt-BR currency
     * format.
     * 
     * @param money
     *            a {@link BigDecimal}
     * @return a formatted {@link String} containing the money symbol from pt-BR
     */
    public static String moneyFormatter(BigDecimal money) {
	NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
	return formatoMoeda.format(money);
    }
}
