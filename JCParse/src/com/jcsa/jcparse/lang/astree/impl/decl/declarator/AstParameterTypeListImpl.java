package com.jcsa.jcparse.lang.astree.impl.decl.declarator;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterTypeList;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstParameterTypeListImpl extends AstFixedNode implements AstParameterTypeList {

	public AstParameterTypeListImpl(AstParameterList plist) throws Exception {
		super(1);
		this.set_child(0, plist);
	}

	public AstParameterTypeListImpl(AstParameterList plist, AstPunctuator comma, AstPunctuator ellipsis)
			throws Exception {
		super(3);

		if (comma == null || comma.get_punctuator() != CPunctuator.comma)
			throw new IllegalArgumentException("Invalid comma: null");
		else if (ellipsis == null || ellipsis.get_punctuator() != CPunctuator.ellipsis)
			throw new IllegalArgumentException("Invalid ellipsis: null");
		else {
			this.set_child(0, plist);
			this.set_child(1, comma);
			this.set_child(2, ellipsis);
		}
	}

	@Override
	public AstParameterList get_parameter_list() {
		return (AstParameterList) children[0];
	}

	@Override
	public AstPunctuator get_comma() {
		if (children.length != 3)
			throw new IllegalArgumentException("Invalid access: no-ellipsis");
		else
			return (AstPunctuator) children[1];
	}

	@Override
	public AstPunctuator get_ellipsis() {
		if (children.length != 3)
			throw new IllegalArgumentException("Invalid access: no-ellipsis");
		else
			return (AstPunctuator) children[2];
	}

	@Override
	public boolean has_ellipsis() {
		return children.length == 3;
	}

}
