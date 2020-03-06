package com.jcsa.jcparse.lang.astree.decl.declarator;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>declarator |-->  pointer declarator 
 * 						| declarator dimension
 * 						| declarator parambody
 * 						| ( declarator )
 * 						| identifier </code>
 * 
 * @author yukimula
 *
 */
public interface AstDeclarator extends AstNode {
	/**
	 * production of declarator
	 * 
	 * @author yukimula
	 *
	 */
	public static enum DeclaratorProduction {
		pointer_declarator, declarator_dimension, declarator_parambody, lp_declarator_rp, identifier,
	}

	public DeclaratorProduction get_production();

	public AstPointer get_pointer();

	public AstDimension get_dimension();

	public AstParameterBody get_parameter_body();

	public AstPunctuator get_lparanth();

	public AstPunctuator get_rparanth();

	public AstName get_identifier();

	public AstDeclarator get_declarator();
}
