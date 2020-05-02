package com.jcsa.jcmuta.mutant.error2mutation.infection;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymFactory;

public class BTRPInfection extends StateInfection {
	
	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		CirExpression result = this.get_result_of(cir_tree, mutation.get_location());
		if(result != null) {
			return result.statement_of();
		}
		else {
			return null;
		}
	}
	
	private StateConstraints trap_on_true_constraints(CirExpression expression) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		SymExpression condition = SymFactory.parse(expression);
		
		SymExpression constraint;
		if(CTypeAnalyzer.is_boolean(type)) {
			constraint = condition;
		}
		else if(CTypeAnalyzer.is_number(type)) {
			constraint = SymFactory.new_binary_expression(
					CBasicTypeImpl.bool_type, COperator.not_equals,
					condition, SymFactory.new_constant(0L));
		}
		else if(CTypeAnalyzer.is_pointer(type)) {
			constraint = SymFactory.new_binary_expression(
					CBasicTypeImpl.bool_type, COperator.not_equals, 
					condition, SymFactory.new_address(StateError.NullPointer, type));
		}
		else {
			throw new IllegalArgumentException("Invalid data type: " + type);
		}
		
		StateConstraints constraints = new StateConstraints(true);
		constraints.add_constraint(expression.statement_of(), constraint);
		return constraints;
	}
	private StateConstraints trap_on_false_constraints(CirExpression expression) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		SymExpression condition = SymFactory.parse(expression);
		
		SymExpression constraint;
		if(CTypeAnalyzer.is_boolean(type)) {
			constraint = SymFactory.new_unary_expression(
					CBasicTypeImpl.bool_type, COperator.logic_not, condition);
		}
		else if(CTypeAnalyzer.is_number(type)) {
			constraint = SymFactory.new_binary_expression(
					CBasicTypeImpl.bool_type, COperator.not_equals,
					condition, SymFactory.new_constant(0L));
		}
		else if(CTypeAnalyzer.is_pointer(type)) {
			constraint = SymFactory.new_binary_expression(
					CBasicTypeImpl.bool_type, COperator.not_equals, 
					condition, SymFactory.new_address(StateError.NullPointer, type));
		}
		else {
			throw new IllegalArgumentException("Invalid data type: " + type);
		}
		
		StateConstraints constraints = new StateConstraints(true);
		constraints.add_constraint(expression.statement_of(), constraint);
		return constraints;
	}
	
	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		AstExpression ast_location = (AstExpression) mutation.get_location();
		CirExpression expression = this.get_result_of(cir_tree, ast_location);
		
		StateConstraints constraints;
		switch(mutation.get_mutation_operator()) {
		case trap_on_true:	constraints = this.trap_on_true_constraints(expression);	break;
		case trap_on_false:	constraints = this.trap_on_false_constraints(expression);	break;
		default: throw new IllegalArgumentException("Invalid: " + mutation.get_mutation_operator());
		}
		StateError error = graph.get_error_set().failure();
		
		output.put(error, constraints);
	}
	
}
