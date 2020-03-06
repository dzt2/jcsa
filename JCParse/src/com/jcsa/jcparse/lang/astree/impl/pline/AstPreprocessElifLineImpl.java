package com.jcsa.jcparse.lang.astree.impl.pline;

import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.pline.AstDirective;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessElifLine;
import com.jcsa.jcparse.lang.lexical.CDirective;

public class AstPreprocessElifLineImpl extends AstFixedNode implements AstPreprocessElifLine {

	public AstPreprocessElifLineImpl(AstDirective elif, AstConstExpression expr) throws Exception {
		super(2);

		if (elif == null || elif.get_directive() != CDirective.cdir_elif)
			throw new IllegalArgumentException("Invalid #elif");
		else {
			this.set_child(0, elif);
			this.set_child(1, expr);
		}
	}

	@Override
	public AstDirective get_directive() {
		return (AstDirective) children[0];
	}

	@Override
	public AstConstExpression get_condition() {
		return (AstConstExpression) children[1];
	}

}
