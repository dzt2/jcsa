package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SelExecutionConstraint extends SelConstraint {

	protected SelExecutionConstraint(CirStatement statement, SymExpression times) throws Exception {
		super(statement, SelKeywords.execute);
		this.add_child(new SelExpression(times));
	}
	
	public SelExpression get_loop_times() {
		return (SelExpression) this.get_child(2);
	}

	@Override
	protected String generate_parameters() throws Exception {
		return "(" + this.get_loop_times().generate_code() + ")";
	}
	
}
