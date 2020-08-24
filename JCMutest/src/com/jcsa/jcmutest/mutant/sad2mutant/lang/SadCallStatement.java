package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * call expression by argument_list;
 * @author yukimula
 *
 */
public class SadCallStatement extends SadStatement {
	
	protected SadCallStatement(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the function to be called
	 */
	public SadExpression get_function() {
		return (SadExpression) this.get_child(1);
	}
	
	/**
	 * @return the arguments applied on calling function
	 */
	public SadArgumentList get_argument_list() {
		return (SadArgumentList) this.get_child(2);
	}

	@Override
	protected String generate_content() throws Exception {
		return "call " + this.get_function().generate_code() + 
				" by " + this.get_argument_list().generate_code() + ";";
	}

	@Override
	protected SadNode clone_self() {
		return new SadCallStatement(this.get_cir_source());
	}

}
