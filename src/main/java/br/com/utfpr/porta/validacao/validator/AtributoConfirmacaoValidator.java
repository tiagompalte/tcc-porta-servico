package br.com.utfpr.porta.validacao.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.apache.commons.beanutils.BeanUtils;

import br.com.utfpr.porta.validacao.AtributoConfirmacao;

public class AtributoConfirmacaoValidator implements ConstraintValidator<AtributoConfirmacao, Object> {

	private String atributoSite;
	private String atributoConfirmacaoSite;
	
	private String atributoTeclado;
	private String atributoConfirmacaoTeclado;
	
	public void initialize(AtributoConfirmacao constraintAnnotation) {
		this.atributoSite = constraintAnnotation.atributoSite();
		this.atributoConfirmacaoSite = constraintAnnotation.atributoConfirmacaoSite();
		
		this.atributoTeclado = constraintAnnotation.atributoTeclado();
		this.atributoConfirmacaoTeclado = constraintAnnotation.atributoConfirmacaoTeclado();
	}

	public boolean isValid(Object object, ConstraintValidatorContext context) {
		boolean validoSite = false;
		boolean validoTeclado = false;
		try {
			Object valorAtributoSite = BeanUtils.getProperty(object, this.atributoSite);
			Object valorAtributoConfirmacaoSite = BeanUtils.getProperty(object, this.atributoConfirmacaoSite);
			
			Object valorAtributoTeclado = BeanUtils.getProperty(object, this.atributoTeclado);
			Object valorAtributoConfirmacaoTeclado = BeanUtils.getProperty(object, this.atributoConfirmacaoTeclado);
			
			validoSite = ambosSaoNull(valorAtributoSite, valorAtributoConfirmacaoSite) || ambosSaoIguais(valorAtributoSite, valorAtributoConfirmacaoSite);
			
			validoTeclado = ambosSaoNull(valorAtributoTeclado, valorAtributoConfirmacaoTeclado) || ambosSaoIguais(valorAtributoTeclado, valorAtributoConfirmacaoTeclado);
			
		} catch (Exception e) {
			throw new RuntimeException("Erro recuperando valores dos atributos", e);
		}
		
		if (!validoSite || !validoTeclado) {
			context.disableDefaultConstraintViolation();
			String mensagem = context.getDefaultConstraintMessageTemplate();
			ConstraintViolationBuilder violationBuilder = context.buildConstraintViolationWithTemplate(mensagem);
			
			if(!validoSite) {
				violationBuilder.addPropertyNode(atributoConfirmacaoSite).addConstraintViolation();
			}
			if(!validoTeclado) {
				violationBuilder.addPropertyNode(atributoConfirmacaoTeclado).addConstraintViolation();
			}
			
		}
		
		return (validoSite && validoTeclado);
	}

	private boolean ambosSaoIguais(Object valorAtributo, Object valorAtributoConfirmacao) {
		return valorAtributo != null && valorAtributo.equals(valorAtributoConfirmacao);
	}

	private boolean ambosSaoNull(Object valorAtributo, Object valorAtributoConfirmacao) {
		return valorAtributo == null && valorAtributoConfirmacao == null;
	}

}
