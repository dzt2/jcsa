package com.jcsa.jcparse.lang.astree.impl.expr;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.ctype.CType;

/**
 * Superclass (abstract) for all expression with fixed size of children (except
 * comma_expr)
 *
 * @author yukimula
 *
 */
public abstract class AstFixedExpressionImpl extends AstFixedNode implements AstExpression {

	protected CType type;

	protected AstFixedExpressionImpl(int size) throws Exception {
		super(size);
	}

	@Override
	public CType get_value_type() {
		return type;
	}

	@Override
	public void set_value_type(CType type) {
		this.type = type;
	}

}
