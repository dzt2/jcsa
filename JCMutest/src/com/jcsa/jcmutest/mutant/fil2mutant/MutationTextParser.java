package com.jcsa.jcmutest.mutant.fil2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * Parse from AstMutation to TextMutation, in which the latter is used to
 * generate mutation code.
 *
 * @author yukimula
 *
 */
abstract class MutationTextParser {

	/* parsing method */
	/**
	 * @param source
	 * @return the location in which the code is mutated (while the others are not)
	 * @throws Exception
	 */
	protected abstract AstNode get_location(AstMutation source) throws Exception;
	/**
	 * @param source
	 * @param location
	 * @return the code to replace the original code specified in range of location
	 * @throws Exception
	 */
	protected abstract String get_muta_code(AstMutation source, AstNode location) throws Exception;
	/**
	 * @param source
	 * @return parse the source mutation as text-mutation.
	 * @throws Exception
	 */
	protected TextMutation parse(AstMutation source) throws Exception {
		AstNode location = this.get_location(source);
		String muta_code = this.get_muta_code(source, location);
		return new TextMutation(location, muta_code);
	}

}
