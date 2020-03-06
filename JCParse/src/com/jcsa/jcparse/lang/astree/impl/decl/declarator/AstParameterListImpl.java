package com.jcsa.jcparse.lang.astree.impl.decl.declarator;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterDeclaration;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterList;
import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstParameterListImpl extends AstVariableNode implements AstParameterList {

	public AstParameterListImpl(AstParameterDeclaration param) throws Exception {
		super();
		this.append_child(param);
	}

	@Override
	public int number_of_parameters() {
		return (children.size() + 1) / 2;
	}

	@Override
	public AstParameterDeclaration get_parameter(int k) {
		if (k < 0 || 2 * k >= children.size())
			throw new IllegalArgumentException("Invalid index: " + k);
		else
			return (AstParameterDeclaration) children.get(2 * k);
	}

	@Override
	public AstPunctuator get_comma(int k) {
		if (k < 0 || 2 * k + 1 >= children.size())
			throw new IllegalArgumentException("Invalid index: " + k);
		else
			return (AstPunctuator) children.get(2 * k + 1);
	}

	@Override
	public void append_parameter(AstPunctuator comma, AstParameterDeclaration param) throws Exception {
		if (comma == null || comma.get_punctuator() != CPunctuator.comma)
			throw new IllegalArgumentException("Invalid comma: not a comma");
		else {
			this.append_child(comma);
			this.append_child(param);
		}
	}

}
