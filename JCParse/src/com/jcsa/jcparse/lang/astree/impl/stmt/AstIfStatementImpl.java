package com.jcsa.jcparse.lang.astree.impl.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstIfStatementImpl extends AstFixedNode implements AstIfStatement {

	public AstIfStatementImpl(AstKeyword _if, AstPunctuator lparanth, AstExpression expr, AstPunctuator rparanth,
			AstStatement tbranch, AstKeyword _else, AstStatement fbranch) throws Exception {
		super(7);

		if (_if == null || _if.get_keyword() != CKeyword.c89_if)
			throw new IllegalArgumentException("Invalid if: null");
		else if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid lparanth: null");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid rparanth: null");
		else if (_else == null || _else.get_keyword() != CKeyword.c89_else)
			throw new IllegalArgumentException("Invalid else: null");
		else {
			this.set_child(0, _if);
			this.set_child(1, lparanth);
			this.set_child(2, expr);
			this.set_child(3, rparanth);
			this.set_child(4, tbranch);
			this.set_child(5, _else);
			this.set_child(6, fbranch);
		}
	}

	public AstIfStatementImpl(AstKeyword _if, AstPunctuator lparanth, AstExpression expr, AstPunctuator rparanth,
			AstStatement tbranch) throws Exception {
		super(5);

		if (_if == null || _if.get_keyword() != CKeyword.c89_if)
			throw new IllegalArgumentException("Invalid if: null");
		else if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid lparanth: null");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid rparanth: null");
		else {
			this.set_child(0, _if);
			this.set_child(1, lparanth);
			this.set_child(2, expr);
			this.set_child(3, rparanth);
			this.set_child(4, tbranch);
		}
	}

	@Override
	public AstKeyword get_if() {
		return (AstKeyword) children[0];
	}

	@Override
	public AstPunctuator get_lparanth() {
		return (AstPunctuator) children[1];
	}

	@Override
	public AstExpression get_condition() {
		return (AstExpression) children[2];
	}

	@Override
	public AstPunctuator get_rparanth() {
		return (AstPunctuator) children[3];
	}

	@Override
	public AstStatement get_true_branch() {
		return (AstStatement) children[4];
	}

	@Override
	public boolean has_else() {
		return children.length == 7;
	}

	@Override
	public AstKeyword get_else() {
		if (children.length != 7)
			throw new IllegalArgumentException("Invalid access: no-else");
		else
			return (AstKeyword) children[5];
	}

	@Override
	public AstStatement get_false_branch() {
		if (children.length != 7)
			throw new IllegalArgumentException("Invalid access: no-else");
		else
			return (AstStatement) children[6];
	}

}
