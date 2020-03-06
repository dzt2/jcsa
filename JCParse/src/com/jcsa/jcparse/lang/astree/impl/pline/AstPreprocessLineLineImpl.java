package com.jcsa.jcparse.lang.astree.impl.pline;

import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.pline.AstDirective;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessLineLine;
import com.jcsa.jcparse.lang.lexical.CDirective;

public class AstPreprocessLineLineImpl extends AstFixedNode implements AstPreprocessLineLine {

	public AstPreprocessLineLineImpl(AstDirective line, AstConstant constant) throws Exception {
		super(2);

		if (line == null || line.get_directive() != CDirective.cdir_include)
			throw new IllegalArgumentException("Invalid #line: null");
		else {
			this.set_child(0, line);
			this.set_child(1, constant);
		}
	}

	public AstPreprocessLineLineImpl(AstDirective line, AstConstant constant, AstLiteral path) throws Exception {
		super(3);

		if (line == null || line.get_directive() != CDirective.cdir_include)
			throw new IllegalArgumentException("Invalid #line: null");
		else {
			this.set_child(0, line);
			this.set_child(1, constant);
			this.set_child(2, path);
		}
	}

	@Override
	public AstDirective get_directive() {
		return (AstDirective) children[0];
	}

	@Override
	public AstConstant get_line_constant() {
		return (AstConstant) children[1];
	}

	@Override
	public boolean has_path_literal() {
		return children.length == 3;
	}

	@Override
	public AstLiteral get_path_literal() {
		if (children.length != 3)
			throw new IllegalArgumentException("Invalid access: no path");
		else
			return (AstLiteral) children[2];
	}

}
