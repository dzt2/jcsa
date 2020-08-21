package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;

/**
 * set_mutation |-- label::set_value(expression, expression).
 * 
 * @author yukimula
 *
 */
public class SedSetExpression extends SedStateMutation {
	
	/* getters */
	/**
	 * @return the statement in which the mutation expected to occur
	 */
	public SedLabel get_statement() {
		return (SedLabel) this.get_child(0);
	}
	/**
	 * @return the original expression being replaced
	 */
	public SedExpression get_orig_expression() {
		return (SedExpression) this.get_child(1);
	}
	/**
	 * @return the mutated expression to replace it
	 */
	public SedExpression get_muta_expression() {
		return (SedExpression) this.get_child(2);
	}
	
	@Override
	protected SedNode copy_self() {
		return new SedSetExpression();
	}
	@Override
	public String generate_code() throws Exception {
		return get_statement().generate_code() + "::set_value(" + 
				this.get_orig_expression().generate_code() + ", " + 
				this.get_muta_expression().generate_code() + ")"; 
	}
	
}
