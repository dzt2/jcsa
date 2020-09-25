package com.jcsa.jcmutest.backups;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SecSetReferenceError extends SecReferenceError {

	public SecSetReferenceError(CirStatement statement, CirExpression 
			orig_reference, SymExpression muta_expression) throws Exception {
		super(statement, SecKeywords.set_reference, orig_reference);
		this.add_child(new SecExpression(muta_expression));
		SecType type = this.get_orig_reference().get_type();
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
	
	public SecExpression get_muta_expression() {
		return (SecExpression) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_reference().generate_code() +
				", " + this.get_muta_expression().generate_code() + ")";
	}
	
}
