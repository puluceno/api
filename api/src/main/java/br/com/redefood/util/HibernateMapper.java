package br.com.redefood.util;

import java.util.Arrays;

import br.com.redefood.hibernate.Hibernate4Module;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class HibernateMapper {
	private static ObjectMapper mapper = new ObjectMapper();
	private static Hibernate4Module mod = new Hibernate4Module();

	private static ObjectMapper mapperWithoutModule() {
		mod.configure(Hibernate4Module.Feature.FORCE_LAZY_LOADING, false);
		mapper.registerModule(mod);
		return mapper;
	}

	public static ObjectMapper getMapper() {
		return mapperWithoutModule();
	}

	public void verifyException(Throwable e, String... matches) {
		String msg = e.getMessage();
		String lmsg = (msg == null) ? "" : msg.toLowerCase();
		for (String match : matches) {
			String lmatch = match.toLowerCase();
			if (lmsg.indexOf(lmatch) >= 0)
				return;
		}
		System.out.println(("Expected an exception with one of substrings (" + Arrays.asList(matches)
				+ "): got one with message \"" + msg + "\""));
	}
}
