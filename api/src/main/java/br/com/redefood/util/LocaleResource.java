package br.com.redefood.util;

import java.io.InputStream;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.regex.Pattern;

public class LocaleResource {
	private String locale = "pt_br";

	public LocaleResource(String locale) {
		if (locale != null)
			this.setLocale(locale);
	}

	public Properties produceProperties() {

		Properties props = new Properties();

		try {

			InputStream resource = getClass().getClassLoader().getResourceAsStream(
					"messages/messages_" + locale + ".properties");
			props.load(resource);
		} catch (Exception e) {
			System.err.println("Could not locate message.properties file.");
		}
		return props;
	}

	public static Properties getProperty(String locale) {
		LocaleResource local = new LocaleResource(locale);
		Properties prop = local.produceProperties();
		return prop;
	}

	public static String getString(String locale, String key, Object... params) {
		try {
			LocaleResource local = new LocaleResource(locale);
			Properties prop = local.produceProperties();
			return MessageFormat.format(prop.getProperty(key), params);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public static String deAccent(String str) {
	    String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
	    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	    return pattern.matcher(nfdNormalizedString).replaceAll("");
	}
}
