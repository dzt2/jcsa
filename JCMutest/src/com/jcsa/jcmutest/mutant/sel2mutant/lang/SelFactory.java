package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SelFactory {
	
	/* constraint */
	private static SymExpression condition_of(SymExpression expression, boolean value) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(CTypeAnalyzer.is_boolean(type)) {
			if(value) {
				return expression;
			}
			else {
				return SymFactory.logic_not(expression);
			}
		}
		else if(CTypeAnalyzer.is_integer(type) || CTypeAnalyzer.is_real(type) || CTypeAnalyzer.is_pointer(type)) {
			if(value) {
				return SymFactory.not_equals(expression, Integer.valueOf(0));
			}
			else {
				return SymFactory.equal_with(expression, Integer.valueOf(0));
			}
		}
		else {
			throw new IllegalArgumentException(type.generate_code());
		}
	}
	public static SelConstraint execution_constraint(
			CirStatement statement, int times) throws Exception {
		return new SelExecutionConstraint(statement,
				SymFactory.parse(Integer.valueOf(times)));
	}
	public static SelConstraint condition_constraint(CirStatement statement, 
			SymExpression condition, boolean assert_value) throws Exception {
		condition = condition_of(condition, assert_value);
		return new SelConditionConstraint(statement, condition);
	}
	
	
	
	
	
	
}
