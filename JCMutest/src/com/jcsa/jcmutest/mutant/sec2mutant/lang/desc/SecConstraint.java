package com.jcsa.jcmutest.mutant.sec2mutant.lang.desc;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymContexts;
import com.jcsa.jcparse.lang.sym.SymEvaluator;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SecConstraint extends SecDescription {

	public SecConstraint(CirStatement statement, 
			SymExpression condition) throws Exception {
		super(statement, SecKeywords.asserton);
		this.add_child(new SecExpression(condition));
	}
	
	public SecExpression get_condition() { 
		return (SecExpression) this.get_child(2); 
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_condition().generate_code() + ")";
	}
	
	@Override
	public boolean is_constraint() {
		return true;
	}
	
	@Override
	public boolean is_state_error() {
		return false;
	}

	
	@Override
	public SecDescription optimize(SymContexts contexts) throws Exception {
		CirStatement statement = this.get_location().get_statement();
		SymExpression condition = this.get_condition().get_expression();
		return new SecConstraint(
				statement, SymEvaluator.evaluate_on(condition, contexts));
	}
	
}
