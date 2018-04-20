package br.com.utfpr.porta.validacao.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.apache.commons.beanutils.BeanUtils;

import br.com.utfpr.porta.validacao.AtributoConfirmacao;

public class AtributoConfirmacaoValidator implements ConstraintValidator<AtributoConfirmacao, Object> {

	private String atributo;
	private String atributoConfirmacao;
		
	public void initialize(AtributoConfirmacao constraintAnnotation) {
		this.atributo = constraintAnnotation.atributo();
		this.atributoConfirmacao = constraintAnnotation.atributoConfirmacao();
	}

	public boolean isValid(Object object, ConstraintValidatorContext context) {
		boolean validoSite = false;
		try {
			Object valorAtributoSite = BeanUtils.getProperty(object, this.atributo);
			Object valorAtributoConfirmacaoSite = BeanUtils.getProperty(object, this.atributoConfirmacao);
						
			validoSite = ambosSaoNull(valorAtributoSite, valorAtributoConfirmacaoSite) || ambosSaoIguais(valorAtributoSite, valorAtributoConfirmacaoSite);
			
		} catch (Exception e) {
			throw new RuntimeException("Erro recuperando valores dos atributos", e);
		}
		
		if (!validoSite) {
			context.disableDefaultConstraintViolation();
			String mensagem = context.getDefaultConstraintMessageTemplate();
			ConstraintViolationBuilder violationBuilder = context.buildConstraintViolationWithTemplate(mensagem);			
			violationBuilder.addPropertyNode(atributoConfirmacao).addConstraintViolation();
		}
		
		return validoSite;
	}

	private boolean ambosSaoIguais(Object valorAtributo, Object valorAtributoConfirmacao) {
		return valorAtributo != null && valorAtributo.equals(valorAtributoConfirmacao);
	}

	private boolean ambosSaoNull(Object valorAtributo, Object valorAtributoConfirmacao) {
		return valorAtributo == null && valorAtributoConfirmacao == null;
	}

}
