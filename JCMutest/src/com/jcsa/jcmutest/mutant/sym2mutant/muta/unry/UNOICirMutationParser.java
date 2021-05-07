package com.jcsa.jcmutest.mutant.sym2mutant.muta.unry;

import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.sym2mutant.util.SymInstanceUtils;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UNOICirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_cir_expression(cir_tree, mutation.get_location()).statement_of();
	}

	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymStateError, SymConstraint> infections) throws Exception {
		CirExpression expression = this.get_cir_expression(cir_tree, mutation.get_location());
		SymConstraint constraint; SymStateError state_error; SymbolExpression condition, muta_value;
		CType data_type = expression.get_data_type();
		if(data_type == null) data_type = CBasicTypeImpl.void_type;
		else data_type = CTypeAnalyzer.get_value_type(data_type);
		
		switch(mutation.get_operator()) {
		case insert_arith_neg:
		{
			condition = SymbolFactory.not_equals(expression, Integer.valueOf(0));
			constraint = SymInstanceUtils.expr_constraint(statement, condition, true);
			muta_value = SymbolFactory.arith_neg(expression);
			state_error = SymInstanceUtils.expr_error(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		case insert_bitws_rsv:
		{
			constraint = SymInstanceUtils.expr_constraint(statement, Boolean.TRUE, true);
			muta_value = SymbolFactory.bitws_rsv(expression);
			state_error = SymInstanceUtils.expr_error(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		case insert_logic_not:
		{
			constraint = SymInstanceUtils.expr_constraint(statement, Boolean.TRUE, true);
			muta_value = SymbolFactory.logic_not(expression);
			state_error = SymInstanceUtils.expr_error(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		case insert_abs_value:
		{
			if(CTypeAnalyzer.is_boolean(data_type)) {
				condition = SymbolFactory.sym_expression(Boolean.FALSE);
			}
			else {
				condition = SymbolFactory.smaller_tn(expression, Integer.valueOf(0));
			}
			constraint = SymInstanceUtils.expr_constraint(statement, condition, true);
			muta_value = SymbolFactory.arith_neg(expression);
			state_error = SymInstanceUtils.expr_error(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		case insert_nabs_value:
		{
			if(CTypeAnalyzer.is_boolean(data_type)) {
				condition = SymbolFactory.sym_expression(Boolean.TRUE);
			}
			else {
				condition = SymbolFactory.greater_tn(expression, Integer.valueOf(0));
			}
			constraint = SymInstanceUtils.expr_constraint(statement, condition, true);
			muta_value = SymbolFactory.arith_neg(expression);
			state_error = SymInstanceUtils.expr_error(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
	}

}
