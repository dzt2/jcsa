package com.jcsa.jcmuta.mutant.back2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;

import __backup__.TextMutation;

/**
 * Translate from TextMutation to the AstMutation.
 * 
 * @author yukimula
 *
 */
public interface Text2MutaTranslator {
	
	/**
	 * Translate the TextMutation as the AstMutation
	 * @param mutation
	 * @return null if the translation fails
	 * @throws Exception
	 */
	public AstMutation parse(TextMutation mutation) throws Exception;
	
}
