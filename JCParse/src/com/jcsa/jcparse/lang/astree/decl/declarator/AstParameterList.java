package com.jcsa.jcparse.lang.astree.decl.declarator;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>param_list |--> param_decl (, param_decl)*</code>
 * 
 * @author yukimula
 */
public interface AstParameterList extends AstNode {
	public int number_of_parameters();

	public AstParameterDeclaration get_parameter(int k);

	public AstPunctuator get_comma(int k);

	public void append_parameter(AstPunctuator comma, AstParameterDeclaration param) throws Exception;
}
