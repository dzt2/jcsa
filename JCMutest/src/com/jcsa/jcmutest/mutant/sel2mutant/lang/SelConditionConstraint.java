package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SelConditionConstraint extends SelConstraint {

	protected SelConditionConstraint(CirStatement statement, 
			 SymExpression condition) throws Exception {
		super(statement, SelKeywords.asserts);
		this.add_child(new SelExpression(condition));
	}
	
	public SelExpression get_condition() {
		return (SelExpression) this.get_child(2);
	}

	@Override
	protected String generate_parameters() throws Exception {
		return "(" + this.get_condition().generate_code() + ")";
	}
	
}
