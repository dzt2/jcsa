package com.jcsa.jcmutest.mutant.sed2mutant.lang.stmt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedArgumentList;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * label: call function on argument_list;
 * @author yukimula
 *
 */
public class SedCallStatement extends SedStatement {

	public SedCallStatement(CirNode cir_source) {
		super(cir_source);
	}
	
	public SedExpression get_function() {
		return (SedExpression) this.get_child(1);
	}
	public SedArgumentList get_argument_list() {
		return (SedArgumentList) this.get_child(2);
	}

	@Override
	protected String generate_content() throws Exception {
		return "call " + this.get_function().generate_code()
				+ this.get_argument_list().generate_code();
	}

	@Override
	protected SedNode clone_self() {
		return new SedCallStatement(this.get_cir_source());
	}
	
}
