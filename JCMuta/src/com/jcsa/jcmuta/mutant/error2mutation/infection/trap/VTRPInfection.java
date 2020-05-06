package com.jcsa.jcmuta.mutant.error2mutation.infection.trap;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

public class VTRPInfection extends StateInfection {
	
	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}
	
	private StateConstraints trap_on_pos_constraints(CirExpression expression) throws Exception {
		/* getters */
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		SymExpression constraint; StateConstraints constraints;
		
		/* (1) operand == true */
		if(CTypeAnalyzer.is_boolean(type)) {
			constraint = StateEvaluation.new_condition(expression, true);
		}
		/* (2) operand > 0 */
		else if(CTypeAnalyzer.is_number(type)) {
			constraint = StateEvaluation.greater_tn(expression, 0L);
		}
		/* (3) operand != null */
		else if(CTypeAnalyzer.is_pointer(type)) {
			constraint = StateEvaluation.not_equals(expression, StateEvaluation.NullPointer);
		}
		else {
			throw new IllegalArgumentException("Invalid " + type);
		}
		
		/* construct symbolic constraints */
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		return constraints;
	}
	
	private StateConstraints trap_on_zro_constraints(CirExpression expression) throws Exception {
		/* getters */
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		SymExpression constraint; StateConstraints constraints;
		
		/* (1) operand == false */
		if(CTypeAnalyzer.is_boolean(type)) {
			constraint = StateEvaluation.new_condition(expression, false);
		}
		/* (2) operand == 0 */
		else if(CTypeAnalyzer.is_number(type)) {
			constraint = StateEvaluation.equal_with(expression, 0L);
		}
		/* (3) operand == null */
		else if(CTypeAnalyzer.is_pointer(type)) {
			constraint = StateEvaluation.equal_with(expression, StateEvaluation.NullPointer);
		}
		else {
			throw new IllegalArgumentException("Invalid " + type);
		}
		
		/* construct symbolic constraints */
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		return constraints;
	}
	
	private StateConstraints trap_on_neg_constraints(CirExpression expression) throws Exception {
		/* getters */
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		SymExpression constraint; StateConstraints constraints;
		
		/* (1) impossible */
		if(CTypeAnalyzer.is_boolean(type)) {
			constraint = StateEvaluation.new_constant(false);
		}
		/* (2) operand < 0 */
		else if(CTypeAnalyzer.is_number(type)) {
			constraint = StateEvaluation.smaller_tn(expression, 0L);
		}
		/* (3) impossible */
		else if(CTypeAnalyzer.is_pointer(type)) {
			constraint = StateEvaluation.new_constant(false);
		}
		else {
			throw new IllegalArgumentException("Invalid " + type);
		}
		
		/* construct symbolic constraints */
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		return constraints;
	}
	
	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = this.get_result_of(cir_tree, this.get_location(mutation));
		
		StateConstraints constraints;
		switch(mutation.get_mutation_operator()) {
		case trap_on_pos: constraints = this.trap_on_pos_constraints(expression); break;
		case trap_on_zro: constraints = this.trap_on_zro_constraints(expression); break;
		case trap_on_neg: constraints = this.trap_on_neg_constraints(expression); break;
		default: throw new IllegalArgumentException("Invalid: " + mutation.get_mutation_operator());
		}
		
		StateError error = graph.get_error_set().failure();
		output.put(error, constraints);
	}

}
