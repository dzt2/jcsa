package com.jcsa.jcparse.lang.astree.impl.expr.base;

import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.lexical.CConstant;

public class AstConstantImpl extends AstBasicExpressionImpl implements AstConstant {

	private CConstant constant;

	public AstConstantImpl(CConstant constant) throws Exception {
		super();
		if (constant == null)
			throw new IllegalArgumentException("Invalid constant: null");
		else
			this.constant = constant;
	}

	@Override
	public CConstant get_constant() {
		return constant;
	}

}
