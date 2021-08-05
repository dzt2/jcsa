package com.jcsa.jcparse.lang.astree.decl.declarator;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;

/**
 * <code>
 * 		abs_declarator --> 	pointer (abs_declarator)?
 * 						  | (abs_declarator)? dimension
 * 						  | (abs_declarator)? parambody
 * 						  | ( abs_declarator )
 *
 * </code>
 *
 * @author yukimula
 *
 */
public interface AstAbsDeclarator extends AstNode {
	public DeclaratorProduction get_production();

	public AstPointer get_pointer();

	public AstDimension get_dimension();

	public AstParameterBody get_parameter_body();

	public AstPunctuator get_lparanth();

	public AstPunctuator get_rparanth();

	public AstAbsDeclarator get_declarator();
}
