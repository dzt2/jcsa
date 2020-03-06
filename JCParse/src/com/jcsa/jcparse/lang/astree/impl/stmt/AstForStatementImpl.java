package com.jcsa.jcparse.lang.astree.impl.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstForStatementImpl extends AstFixedNode implements AstForStatement {

	public AstForStatementImpl(AstKeyword _for, AstPunctuator lparanth, AstStatement initializer,
			AstExpressionStatement condition, AstExpression increment, AstPunctuator rparanth, AstStatement body)
			throws Exception {
		super(7);

		if (_for == null || _for.get_keyword() != CKeyword.c89_for)
			throw new IllegalArgumentException("Invalid for: null");
		else if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid lparanth: null");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid rparanth: null");
		else if (!(initializer instanceof AstDeclarationStatement) && !(initializer instanceof AstExpressionStatement))
			throw new IllegalArgumentException("Invalid initializer: null");
		else {
			this.set_child(0, _for);
			this.set_child(1, lparanth);
			this.set_child(2, initializer);
			this.set_child(3, condition);
			this.set_child(4, increment);
			this.set_child(5, rparanth);
			this.set_child(6, body);
		}
	}

	public AstForStatementImpl(AstKeyword _for, AstPunctuator lparanth, AstStatement initializer,
			AstExpressionStatement condition, AstPunctuator rparanth, AstStatement body) throws Exception {
		super(6);

		if (_for == null || _for.get_keyword() != CKeyword.c89_for)
			throw new IllegalArgumentException("Invalid for: null");
		else if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid lparanth: null");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid rparanth: null");
		else if (!(initializer instanceof AstDeclarationStatement) && !(initializer instanceof AstExpressionStatement))
			throw new IllegalArgumentException("Invalid initializer: null");
		else {
			this.set_child(0, _for);
			this.set_child(1, lparanth);
			this.set_child(2, initializer);
			this.set_child(3, condition);
			this.set_child(4, rparanth);
			this.set_child(5, body);
		}
	}

	@Override
	public AstKeyword get_for() {
		return (AstKeyword) children[0];
	}

	@Override
	public AstPunctuator get_lparanth() {
		return (AstPunctuator) children[1];
	}

	@Override
	public AstStatement get_initializer() {
		return (AstStatement) children[2];
	}

	@Override
	public AstExpressionStatement get_condition() {
		return (AstExpressionStatement) children[3];
	}

	@Override
	public boolean has_increment() {
		return children.length == 7;
	}

	@Override
	public AstExpression get_increment() {
		if (children.length != 7)
			throw new IllegalArgumentException("Invalid access: no-increment");
		else
			return (AstExpression) children[4];
	}

	@Override
	public AstPunctuator get_rparanth() {
		return (AstPunctuator) children[children.length - 2];
	}

	@Override
	public AstStatement get_body() {
		return (AstStatement) children[children.length - 1];
	}

}
