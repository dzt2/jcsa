package com.jcsa.jcmutest.mutant.sed2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedOperator;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * SadUnaryExpression				{+,-,~,!,*,&,cast}				
 * @author yukimula
 *
 */
public class SedUnaryExpression extends SedExpression {

	public SedUnaryExpression(CirNode cir_source, 
			CType data_type, COperator operator) {
		super(cir_source, data_type);
		this.add_child(new SedOperator(operator));
	}
	
	/**
	 * @return {+,-,~,!,*,&,cast}	
	 */
	public SedOperator get_operator() {
		return (SedOperator) this.get_child(0);
	}
	/**
	 * @return the unary operand in expression
	 */
	public SedNode get_operand() {
		return this.get_child(1);
	}

	@Override
	protected SedNode clone_self() {
		return new SedUnaryExpression(this.get_cir_source(), this.
				get_data_type(), this.get_operator().get_operator());
	}

	@Override
	public String generate_code() throws Exception {
		return this.get_operator().generate_code() + 
				"(" + this.get_operand().generate_code() + ")";
	}
	
}
