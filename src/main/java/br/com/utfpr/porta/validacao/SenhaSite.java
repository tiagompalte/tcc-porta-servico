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
@Pattern(regexp = "/^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%!^&*]).{6,12}$/g")
public @interface SenhaSite {
	
	@OverridesAttribute(constraint = Pattern.class, name = "message")
	String message() default "A senha do site deve conter uma letra maiúscula, um caracter especial(@,#,$,%,!,^,&,*) e um número. Deve conter de 6 a 12 dígitos";
	
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
