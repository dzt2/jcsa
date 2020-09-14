package com.jcsa.jcmutest.mutant.sec2mutant.lang.cons;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecDisjunctConstraints extends SecConstraint {

	public SecDisjunctConstraints(CirStatement statement) throws Exception {
		super(statement, SecKeywords.disjunct);
	}
	
	/**
	 * @return the number of constraints within the conjunction
	 */
	public int number_of_constraints() {
		return this.number_of_children() - 2;
	}
	
	/**
	 * @param k
	 * @return the kth constraint within the conjunction
	 * @throws IndexOutOfBoundsException
	 */
	public SecConstraint get_constraint(int k) throws IndexOutOfBoundsException {
		return (SecConstraint) this.get_child(k + 2);
	}
	
	@Override
	public SymExpression get_sym_condition() throws Exception {
		SymExpression expression = null;
		for(int k = 0; k < this.number_of_constraints(); k++) {
			if(expression == null) {
				expression = this.get_constraint(k).get_sym_condition();
			}
			else {
				expression = SymFactory.logic_ior(expression, 
						this.get_constraint(k).get_sym_condition());
			}
		}
		if(expression == null)
			return SymFactory.new_constant(Boolean.FALSE);
		else
			return expression;
	}
	
	@Override
	protected String generate_content() throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("{");
		for(int k = 0; k < this.number_of_constraints(); k++) {
			buffer.append(" ").append(this.get_constraint(k).generate_code()).append(";");
		}
		buffer.append(" }");
		return buffer.toString();
	}
	
}
