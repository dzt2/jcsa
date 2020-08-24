package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * label: expression = expression;
 * 
 * @author yukimula
 *
 */
public class SadAssignStatement extends SadStatement {

	protected SadAssignStatement(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the expression to be defined
	 */
	public SadExpression get_lvalue() { 
		return (SadExpression) this.get_child(1); 
	}
	
	/**
	 * @return the expression to assign the left-operand
	 */
	public SadExpression get_rvalue() { 
		return (SadExpression) this.get_child(2); 
	}
	
	@Override
	protected String generate_content() throws Exception {
		return this.get_lvalue().generate_code() + " := " + 
					this.get_rvalue().generate_code() + ";";
	}

	@Override
	protected SadNode clone_self() {
		return new SadAssignStatement(this.get_cir_source());
	}

}
