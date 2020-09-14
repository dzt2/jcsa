package com.jcsa.jcmutest.mutant.sec2mutant.lang.cons;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SecConditionConstraint extends SecConstraint {

	public SecConditionConstraint(CirStatement statement, 
			SymExpression condition) throws Exception {
		super(statement, SecKeywords.asserts);
		this.add_child(new SecExpression(condition));
	}
	
	/**
	 * @return the condition being asserted at this point
	 */
	public SecExpression get_condition() { 
		return (SecExpression) this.get_child(2); 
	}

	@Override
	public SymExpression get_sym_condition() throws Exception {
		return this.get_condition().get_expression();
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_condition().generate_code() + ")";
	}

}
