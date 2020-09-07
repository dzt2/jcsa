package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import com.jcsa.jcparse.lang.lexical.COperator;

public class SelOperator extends SelToken {
	
	private COperator operator;
	protected SelOperator(COperator operator) throws Exception {
		if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else
			this.operator = operator;
	}
	
	/**
	 * @return the operator that the node describes
	 */
	public COperator get_operator() { return this.operator; }

	@Override
	public String generate_code() throws Exception {
		return this.operator.toString();
	}
	
}
