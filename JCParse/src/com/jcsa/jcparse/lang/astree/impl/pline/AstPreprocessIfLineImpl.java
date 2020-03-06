package com.jcsa.jcparse.lang.astree.impl.pline;

import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.pline.AstDirective;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessIfLine;
import com.jcsa.jcparse.lang.lexical.CDirective;

public class AstPreprocessIfLineImpl extends AstFixedNode implements AstPreprocessIfLine {

	public AstPreprocessIfLineImpl(AstDirective _if, AstConstExpression expr) throws Exception {
		super(2);

		if (_if == null || _if.get_directive() != CDirective.cdir_if)
			throw new IllegalArgumentException("Invalid #if: null");
		else {
			this.set_child(0, _if);
			this.set_child(1, expr);
		}
	}

	@Override
	public AstDirective get_directive() {
		return (AstDirective) children[0];
	}

	@Override
	public AstConstExpression get_if_condition() {
		return (AstConstExpression) children[1];
	}

}
