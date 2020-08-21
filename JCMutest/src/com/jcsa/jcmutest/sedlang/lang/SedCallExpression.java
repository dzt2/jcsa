package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * call_expression |-- expression argument_list
 * @author yukimula
 *
 */
public class SedCallExpression extends SedExpression {
	
	/* constructor */
	protected SedCallExpression(CirNode source, CType data_type) {
		super(source, data_type);
	}
	
	/* getters */
	/**
	 * @return the function being invoked by the calling expression
	 */
	public SedExpression get_function() { 
		return (SedExpression) this.get_child(0); 
	}
	/**
	 * @return the argument list applied on calling the function
	 */
	public SedArgumentList get_argument_list() { 
		return (SedArgumentList) this.get_child(1); 
	}
	
	/* implementation */
	@Override
	protected SedNode copy_self() {
		return new SedCallExpression(this.get_source(), this.get_data_type());
	}
	@Override
	public String generate_code() throws Exception {
		return this.get_function().generate_code() + 
				this.get_argument_list().generate_code();
	}
	
}
