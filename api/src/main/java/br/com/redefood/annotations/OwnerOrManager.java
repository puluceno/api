package br.com.redefood.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.redefood.util.RedeFoodConstants;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OwnerOrManager {
	String header() default RedeFoodConstants.DEFAULT_TOKEN_IDENTIFICATOR;
}
