package br.com.utfpr.porta.validacao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Pattern(regexp = "\\b([1-9]|1\\d{0}[0-9]|2\\d{0}[0-9]|3\\d{0}[0-1])\\b")
public @interface DiaMes {
	
	@OverridesAttribute(constraint = Pattern.class, name = "message")
	String message() default "Dia do mês deve estar entre 1 e 31";
	
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
