package com.jcsa.jcmutest.backups;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SecUnyExpressionError extends SecExpressionError {

	public SecUnyExpressionError(CirStatement statement, CirExpression 
			orig_expression, COperator operator) throws Exception {
		super(statement, SecKeywords.uny_expression, orig_expression);
		SecType type = this.get_orig_expression().get_type();
		switch(operator) {
		case negative:
		{
			switch(type.get_vtype()) {
			case cbool:
			case cchar:
			case csign:
			case usign:
			case creal:	break;
			default: throw new IllegalArgumentException(type.generate_code());
			}
			break;
		}
		case bit_not:
		{
			switch(type.get_vtype()) {
			case cbool:
			case cchar:
			case csign:
			case usign: break;
			default: throw new IllegalArgumentException(type.generate_code());
			}
			break;
		}
		case logic_not:
		{
			switch(type.get_vtype()) {
			case cbool:
			case cchar:
			case csign:
			case usign:
			case caddr:
			case creal:	break;
			default: throw new IllegalArgumentException(type.generate_code());
			}
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.add_child(new SecOperator(operator));
	}
	
	/**
	 * @return {-, ~, !}
	 */
	public SecOperator get_operator() { return (SecOperator) this.get_child(3); }
	
	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_expression().generate_code() + 
				", " + this.get_operator().generate_code() + ")";
	}

}