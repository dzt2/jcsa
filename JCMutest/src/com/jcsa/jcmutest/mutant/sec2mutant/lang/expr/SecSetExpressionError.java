package com.jcsa.jcmutest.mutant.sec2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecExpression;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SecSetExpressionError extends SecExpressionError {

	public SecSetExpressionError(CirStatement statement, CirExpression 
			orig_expression, SymExpression muta_expression) throws Exception {
		super(statement, SecKeywords.set_expression, orig_expression);
		this.add_child(new SecExpression(muta_expression));
		SecType type = this.get_orig_expression().get_type();
		switch(type.get_vtype()) {
		case cbool:
		case cchar:
		case csign:
		case usign:
		case creal:
		case caddr:	
		case cbody: break;
		default: throw new IllegalArgumentException(type.generate_code());
		}
	}
	
	/**
	 * @return expression to replace the original one
	 */
	public SecExpression get_muta_expression() {
		return (SecExpression) this.get_child(3);
	}
	
	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_expression().generate_code() +
				", " + this.get_muta_expression().generate_code() + ")";
	}
	
}
