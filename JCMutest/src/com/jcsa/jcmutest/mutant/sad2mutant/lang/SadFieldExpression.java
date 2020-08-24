package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * field_expression |-- expression . field
 * @author yukimula
 *
 */
public class SadFieldExpression extends SadExpression {
	
	protected SadFieldExpression(CirNode source, CType data_type) {
		super(source, data_type);
	}
	
	/**
	 * @return the body of the expression
	 */
	public SadExpression get_body() { 
		return (SadExpression) this.get_child(0); 
	}
	
	/**
	 * @return the field of the expression
	 */
	public SadField get_field() {
		return (SadField) this.get_child(1);
	}
	
	@Override
	public String generate_code() throws Exception {
		return "(" + this.get_body().generate_code() + 
				")." + this.get_field().generate_code();
	}
	
	@Override
	protected SadNode clone_self() {
		return new SadFieldExpression(this.get_cir_source(), this.get_data_type());
	}
	
}
