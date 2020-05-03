package com.jcsa.jcmuta.mutant.error2mutation.infection;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
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

/**
 * 
 * @author yukimula
 *
 */
public class VBRPInfection extends StateInfection {
	
	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}
	
	private StateConstraints set_true_constraints(CirExpression expression) throws Exception {
		/* 1. getters */
		CirStatement statement = expression.statement_of();
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		SymExpression operand = SymFactory.parse(expression);
		StateConstraints constraints = new StateConstraints(true);
		
		/* (1) operand == false */
		if(CTypeAnalyzer.is_boolean(type)) {
			operand = SymFactory.new_unary_expression(
					CBasicTypeImpl.bool_type, COperator.logic_not, operand);
		}
		/* (2) operand == 0 */
		else if(CTypeAnalyzer.is_number(type)) {
			operand = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.equal_with, operand, SymFactory.new_constant(0L));
		}
		/* (3) operand == null */
		else if(CTypeAnalyzer.is_pointer(type)) {
			operand = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.equal_with, operand, SymFactory.new_address(StateError.NullPointer, type));
		}
		else {
			throw new IllegalArgumentException("Invalid type: " + type);
		}
		
		/* construct symbolic constraints */
		operand = this.derive_sym_constraint(operand);
		constraints.add_constraint(statement, operand);
		return constraints;
	}
	
	private StateConstraints set_false_constraints(CirExpression expression) throws Exception {
		/* 1. getters */
		CirStatement statement = expression.statement_of();
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		SymExpression operand = SymFactory.parse(expression);
		StateConstraints constraints = new StateConstraints(true);
		
		/* (1) operand == true */
		if(CTypeAnalyzer.is_boolean(type)) {
		}
		/* (2) operand != 0 */
		else if(CTypeAnalyzer.is_number(type)) {
			operand = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, operand, SymFactory.new_constant(0L));
		}
		/* (3) operand != null */
		else if(CTypeAnalyzer.is_pointer(type)) {
			operand = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, operand, SymFactory.new_address(StateError.NullPointer, type));
		}
		else {
			throw new IllegalArgumentException("Invalid type: " + type);
		}
		
		/* construct symbolic constraints */
		operand = this.derive_sym_constraint(operand);
		constraints.add_constraint(statement, operand);
		return constraints;
	}
	
	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = this.get_result_of(cir_tree, this.get_location(mutation));
		
		StateConstraints constraints; StateError error;
		switch(mutation.get_mutation_operator()) {
		case set_true:
		{
			constraints = this.set_true_constraints(expression);
			error = graph.get_error_set().set_bool(expression, true);
		}
		break;
		case set_false:
		{
			constraints = this.set_false_constraints(expression);
			error = graph.get_error_set().set_bool(expression, false);
		}
		break;
		default: throw new IllegalArgumentException("Invalid: " + mutation.get_mutation_operator());
		}
		
		output.put(error, constraints);
	}
	
}
