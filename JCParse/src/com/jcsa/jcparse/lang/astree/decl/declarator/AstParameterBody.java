package com.jcsa.jcparse.lang.astree.decl.declarator;

import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>param_body --> ( (param_type_list|identifier_list)? ) </code>
 * 
 * @author yukimula
 */
public interface AstParameterBody extends AstScopeNode {
	public boolean has_parameter_type_list();

	public boolean has_identifier_list();

	public AstPunctuator get_lparanth();

	public AstParameterTypeList get_parameter_type_list();

	public AstIdentifierList get_identifier_list();

	public AstPunctuator get_rparanth();
}
