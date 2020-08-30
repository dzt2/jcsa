package com.jcsa.jcmutest.mutant.sed2mutant.lang.stmt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * label : expression := expression;
 * @author yukimula
 *
 */
public class SedAssignStatement extends SedStatement {

	public SedAssignStatement(CirNode cir_source) {
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
		return this.get_lvalue().generate_code() + 
				" := " + this.get_rvalue().generate_code();
	}

	@Override
	protected SedNode clone_self() {
		return new SedAssignStatement(this.get_cir_source());
	}
	
}
