package com.jcsa.jcmutest.mutant.sel2mutant.lang.token;

import com.jcsa.jcparse.lang.lexical.COperator;

public class SelOperator extends SelToken {
	
	/* definition */
	private COperator operator;
	public SelOperator(COperator operator) throws Exception {
		if(operator == null)
			throw new IllegalArgumentException("Invalid operator");
		else
			this.operator = operator;
	}
	
	/**
	 * @return the operator that this node defines
	 */
	public COperator get_operator() { return this.operator; }

	@Override
	public String generate_code() throws Exception {
		return this.operator.toString();
	}
	
}
