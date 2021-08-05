package com.jcsa.jcparse.lang.astree.decl.declarator;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>param_type_list |--> param_list (, ...)?</code>
 *
 * @author yukimula
 */
public interface AstParameterTypeList extends AstNode {
	public AstParameterList get_parameter_list();

	public AstPunctuator get_comma();

	public AstPunctuator get_ellipsis();

	public boolean has_ellipsis();
}
