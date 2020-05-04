package com.jcsa.jcmuta.mutant.error2mutation.infection;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrors;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

public class VCRPInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}
	
	/**
	 * expression != value
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	private StateConstraints get_constraints_for(CirExpression expression, CConstant value) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		/* expression != value */
		SymExpression constraint;
		if(CTypeAnalyzer.is_number(type)) {
			switch(value.get_type().get_tag()) {
			case c_bool:
			{
				if(value.get_bool().booleanValue())
					constraint = StateEvaluation.not_equals(expression, 1L);
				else 
					constraint = StateEvaluation.not_equals(expression, 0L);
			}
			break;
			case c_char:
			case c_uchar:
			{
				long val = value.get_char().charValue();
				constraint = StateEvaluation.not_equals(expression, val);
			}
			break;
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			{
				long val = value.get_integer().longValue();
				constraint = StateEvaluation.not_equals(expression, val);
			}
			break;
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
			{
				constraint = StateEvaluation.not_equals(expression, value.get_long().longValue());
			}
			break;
			case c_float:
			{
				constraint = StateEvaluation.not_equals(expression, value.get_float().doubleValue());
			}
			break;
			case c_double:
			case c_ldouble:
			{
				constraint = StateEvaluation.not_equals(expression, value.get_double().doubleValue());
			}
			break;
			default: throw new IllegalArgumentException("Invalid constant: " + value);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + type);
		}
		
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		return constraints;
	}
	
	/**
	 * set_numb(expr, value)
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	private StateError get_state_error_of(CirExpression expression, CConstant value, StateErrorGraph graph) throws Exception {
		StateErrors errors = graph.get_error_set();
		switch(value.get_type().get_tag()) {
		case c_bool:
		{
			if(value.get_bool().booleanValue())
				return errors.set_numb(expression, 1L);
			else 
				return errors.set_numb(expression, 0L);
		}
		case c_char:
		case c_uchar:
		{
			long val = value.get_char().charValue();
			return errors.set_numb(expression, val);
		}
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
		{
			long val = value.get_integer().longValue();
			return errors.set_numb(expression, val);
		}
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
		{
			return errors.set_numb(expression, value.get_long().longValue());
		}
		case c_float:
		{
			return errors.set_numb(expression, value.get_float().doubleValue());
		}
		case c_double:
		case c_ldouble:
		{
			return errors.set_numb(expression, value.get_double().doubleValue());
		}
		default: throw new IllegalArgumentException("Invalid constant: " + value);
		}
	}
	
	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = this.get_result_of(
						cir_tree, this.get_location(mutation));
		CConstant value = (CConstant) mutation.get_parameter();
		
		StateConstraints constraints = this.get_constraints_for(expression, value);
		StateError error = this.get_state_error_of(expression, value, graph);
		
		output.put(error, constraints);
	}

}
