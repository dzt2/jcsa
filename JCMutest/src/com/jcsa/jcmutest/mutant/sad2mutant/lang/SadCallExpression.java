package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * call_expr |-- expression argument_list
 * @author yukimula
 *
 */
public class SadCallExpression extends SadExpression {

	protected SadCallExpression(CirNode source, CType data_type) {
		super(source, data_type);
	}
	
	/**
	 * @return the expression of function to be called
	 */
	public SadExpression get_function() { 
		return (SadExpression) this.get_child(0); 
	}
	
	/**
	 * @return the argument list to be used for calling function
	 */
	public SadArgumentList get_argument_list() {
		return (SadArgumentList) this.get_child(1);
	}

	@Override
	public String generate_code() throws Exception {
		return this.get_function().generate_code() + 
				this.get_argument_list().generate_code();
	}

	@Override
	protected SadNode clone_self() {
		return new SadCallExpression(this.get_cir_source(), this.get_data_type());
	}

}
