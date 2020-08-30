package com.jcsa.jcmutest.mutant.sed2mutant.lang.stmt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * label: wait lvalue := rvalue;
 * @author yukimula
 *
 */
public class SedWaitStatement extends SedStatement {

	public SedWaitStatement(CirNode cir_source) {
		super(cir_source);
	}
	
	public SedExpression get_lvalue() {
		return (SedExpression) this.get_child(1);
	}
	public SedExpression get_rvalue() {
		return (SedExpression) this.get_child(2);
	}

	@Override
	protected String generate_content() throws Exception {
		return "wait " + this.get_lvalue().generate_code() + 
				" := " + this.get_rvalue().generate_code();
	}

	@Override
	protected SedNode clone_self() {
		return new SedWaitStatement(this.get_cir_source());
	}
	
}
