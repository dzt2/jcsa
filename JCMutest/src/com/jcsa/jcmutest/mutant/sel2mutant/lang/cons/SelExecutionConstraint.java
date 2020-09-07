package com.jcsa.jcmutest.mutant.sel2mutant.lang.cons;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.token.SelExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SelExecutionConstraint extends SelConstraint {

	public SelExecutionConstraint(CirStatement statement, int loop_times) throws Exception {
		super(statement, SelKeywords.execute);
		this.add_child(new SelExpression(SymFactory.parse(Integer.valueOf(loop_times))));
	}
	
	/**
	 * @return the loop-times required for executing the statement
	 */
	public SelExpression get_loop_times() {
		return (SelExpression) this.get_child(2);
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_loop_times().generate_code() + ")";
	}
	
}
