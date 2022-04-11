package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	It specifies the abstract execution state defined in StateMutation analysis.	<br>
 * 	<code>
 * 		UniAbstractState				[category, location,  l_operand, r_operand]	<br>
 * 		|--	UniConditionState			[category, statement, l_operand, r_operand]	<br>
 * 		|--	|--	UniCoverTimesState		[cov_time, statement, min_times, max_times]	<br>
 * 		|--	|--	UniConstraintState		[eva_cond, statement, condition, must_need]	<br>
 * 		|--	|--	UniSeedMutantState		[sed_muta, statement, mutant_ID, clas_oprt]	<br>
 * 		|--	UniPathErrorState			[category, statement, l_operand, r_operand]	<br>
 * 		|--	|--	UniBlockErrorState		[set_stmt, statement, orig_exec, muta_exec]	<br>
 * 		|--	|--	UniFlowsErrorState		[set_flow, statement, orig_next, muta_next]	<br>
 * 		|--	|--	UniTrapsErrorState		[trp_stmt, statement, orig_exec, exception]	<br>
 * 		|--	UniDataErrorState			[category, location,  l_operand, r_operand]	<br>
 * 		|--	|--	UniValueErrorState		[set_expr, expr|stmt, orig_expr, muta_expr]	<br>
 * 		|--	|--	UniIncreErrorState		[inc_expr, expr|stmt, orig_expr, different]	<br>
 * 		|--	|--	UniBixorErrorState		[xor_expr, expr|stmt, orig_expr, different]	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class UniAbstractState {
	
	/* definitions */
	private	UniAbstractClass	category;
	private	CirNode				location;
	private	SymbolExpression	loperand;
	private	SymbolExpression	roperand;
	protected UniAbstractState(UniAbstractClass category,
			CirNode location, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		if(category == null) {
			throw new IllegalArgumentException("Invalid category: null");
		}
		else if(location == null || location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid: " + location);
		}
		else if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			this.category = category; this.location = location;
			this.loperand = loperand; this.roperand = roperand;
		}
	}
	
	/* getters */
	/**
	 * @return the category of the abstract execution state
	 */
	public 	UniAbstractClass	get_category()	{ return this.category; }
	/**
	 * @return the storage location to preserve the value of state
	 */
	public	CirNode				get_location()	{ return this.location; }
	/**
	 * @return the execution point where the abstract state is defined
	 */
	public	CirExecution		get_execution()	{ return this.location.execution_of(); }
	/**
	 * @return the statement of the execution point of the state point
	 */
	public	CirStatement		get_statement()	{ return this.get_execution().get_statement(); }
	/**
	 * @return the left-operand to describe the abstract state
	 */
	public	SymbolExpression	get_loperand()	{ return this.loperand; }
	/**
	 * @return the right-operand to describe the abstract state
	 */
	public	SymbolExpression	get_roperand()	{ return this.roperand;	}
	@Override
	public String toString() {
		return this.category + "#" + this.location.get_node_id() + "#" + this.loperand + "#" + this.roperand;
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof UniAbstractState) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	
	/* classifier */
	/**
	 * @return whether this state is a conditional state
	 */
	public boolean is_conditional() {
		switch(this.category) {
		case cov_time:
		case eva_cond:
		case sed_muta:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether this state represents a path-related error
	 */
	public boolean is_path_errors() {
		switch(this.category) {
		case set_stmt:
		case trp_stmt:
		case set_flow:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether this state represents a data-related error
	 */
	public boolean is_data_errors() {
		switch(this.category) {
		case set_expr:
		case inc_expr:
		case xor_expr:	return true;
		default:		return false;
		}
	}
	/**
	 * @return UniAbstErrorState
	 */
	public boolean is_abst_errors() {
		return this.is_data_errors() || this.is_path_errors();
	}
	
	/* factory */
	/**
	 * @param statement	the statement being executed as a coverage condition
	 * @param min_times	the minimal times for executing the target statement
	 * @param max_times	the maximal times for executing the target statement
	 * @return			cov_time(statement; min_times, max_times);
	 * @throws Exception
	 */
	public static UniCoverTimesState cov_time(CirStatement statement, 
					int min_times, int max_times) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(min_times < 0 || max_times <= 0 || max_times >= min_times) {
			throw new IllegalArgumentException(min_times + "#" + max_times);
		}
		else {
			return new UniCoverTimesState(statement, min_times, max_times);
		}
	}
	/**
	 * @param statement	the statement where the condition is evaluated
	 * @param condition	the condition being evaluated in the statement
	 * @param must_need	True (always satisfied); False (at least once)
	 * @return			eva_cond(statement; condition, must_need)
	 * @throws Exception
	 */
	public static UniConstraintState eva_cond(CirStatement statement,
				Object condition, boolean must_need) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new UniConstraintState(statement, SymbolFactory.
						sym_condition(condition, true), must_need);
		}
	}
	/**
	 * @param statement	the statement where the mutation is injected
	 * @param mutant	the syntactic mutation injected in statement
	 * @return			sed_muta(statement; mutant_ID, clas_operator)
	 * @throws Exception
	 */
	public static UniSeedMutantState sed_muta(CirStatement statement, Mutant mutant) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant as: null");
		}
		else {
			return new UniSeedMutantState(statement, mutant);
		}
	}
	/**
	 * @param statement	the statement to be executed in original programs
	 * @param muta_exec	True if the statement is executed in the mutation
	 * @return			set_stmt(statement, !muta_exec, muta_exec);
	 * @throws Exception
	 */
	public static UniBlockErrorState set_stmt(CirStatement statement, boolean muta_exec) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else {
			return new UniBlockErrorState(statement, muta_exec);
		}
	}
	/**
	 * @param statement	the statement from which the flow error is arised
	 * @param orig_next	the statement being executed next in the original
	 * @param muta_next	the statement being executed next in the mutation
	 * @return			set_flow(statement; original_next, mutation_next)
	 * @throws Exception
	 */
	public static UniFlowsErrorState set_flow(CirStatement statement, 
			CirExecution orig_next, CirExecution muta_next) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(orig_next == null) {
			throw new IllegalArgumentException("Invalid orig_next: null");
		}
		else if(muta_next == null) {
			throw new IllegalArgumentException("Invalid muta_next: null");
		}
		else {
			return new UniFlowsErrorState(statement, orig_next, muta_next);
		}
	}
	/**
	 * @param statement
	 * @return trp_stmt(statement.exit, true, exception)
	 * @throws Exception
	 */
	public static UniTrapsErrorState trp_stmt(CirStatement statement) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else {
			CirExecution execution = statement.execution_of();
			execution = execution.get_graph().get_exit();
			return new UniTrapsErrorState(execution.get_statement());
		}
	}
	/**
	 * @param expression	the location in which the value-error was injected
	 * @param muta_value	the mutation value of the expression being mutated
	 * @return				set_expr(expr|stmt, orig_value, muta_value)
	 * @throws Exception
	 */
	public static UniValueErrorState set_expr(CirExpression expression, Object value) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid location as null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			CirExpression target;
			if(StateMutations.is_assigned(expression)) {
				CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
				target = statement.get_rvalue();
			}
			else {
				target = expression;
			}
			
			SymbolExpression orig_value, muta_value;
			if(StateMutations.is_boolean(expression)) {
				orig_value = SymbolFactory.sym_condition(target, true);
				muta_value = SymbolFactory.sym_condition(value, true);
			}
			else {
				orig_value = SymbolFactory.sym_expression(target);
				muta_value = SymbolFactory.sym_expression(value);
			}
			return new UniValueErrorState(expression, orig_value, muta_value);
		}
	}
	/**
	 * @param expression	the location in which the value-error was injected
	 * @param muta_value	the mutation value of the expression being mutated
	 * @return				inc_expr(expr|stmt, orig_value, muta_value)
	 * @throws Exception
	 */
	public static UniIncreErrorState inc_expr(CirExpression expression, Object value) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid location as null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			CirExpression target;
			if(StateMutations.is_assigned(expression)) {
				CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
				target = statement.get_rvalue();
			}
			else {
				target = expression;
			}
			
			SymbolExpression orig_value, muta_value;
			if(StateMutations.is_address(target) || StateMutations.is_numeric(target)) {
				orig_value = SymbolFactory.sym_expression(target);
				muta_value = SymbolFactory.sym_expression(value);
			}
			else {
				throw new IllegalArgumentException("Invalid: " + target.getClass().getSimpleName());
			}
			return new UniIncreErrorState(expression, orig_value, muta_value);
		}
	}
	/**
	 * @param expression	the location in which the value-error was injected
	 * @param muta_value	the mutation value of the expression being mutated
	 * @return				xor_expr(expr|stmt, orig_value, muta_value)
	 * @throws Exception
	 */
	public static UniBixorErrorState xor_expr(CirExpression expression, Object value) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid location as null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			CirExpression target;
			if(StateMutations.is_assigned(expression)) {
				CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
				target = statement.get_rvalue();
			}
			else {
				target = expression;
			}
			
			SymbolExpression orig_value, muta_value;
			if(StateMutations.is_integer(target)) {
				orig_value = SymbolFactory.sym_expression(target);
				muta_value = SymbolFactory.sym_expression(value);
			}
			else {
				throw new IllegalArgumentException("Invalid: " + target.getClass().getSimpleName());
			}
			return new UniBixorErrorState(expression, orig_value, muta_value);
		}
	}
	/**
	 * @param statement
	 * @param identifier
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static UniValueErrorState set_expr(CirStatement statement, Object identifier, Object value) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(identifier == null) {
			throw new IllegalArgumentException("Invalid identifier: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			return new UniValueErrorState(statement, 
					SymbolFactory.sym_expression(identifier), 
					SymbolFactory.sym_expression(value));
		}
	}
	/**
	 * @param statement
	 * @param identifier
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static UniIncreErrorState inc_expr(CirStatement statement, Object identifier, Object value) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(identifier == null) {
			throw new IllegalArgumentException("Invalid identifier: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			return new UniIncreErrorState(statement, 
					SymbolFactory.sym_expression(identifier), 
					SymbolFactory.sym_expression(value));
		}
	}
	/**
	 * @param statement
	 * @param identifier
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static UniBixorErrorState xor_expr(CirStatement statement, Object identifier, Object value) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(identifier == null) {
			throw new IllegalArgumentException("Invalid identifier: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			return new UniBixorErrorState(statement, 
					SymbolFactory.sym_expression(identifier), 
					SymbolFactory.sym_expression(value));
		}
	}
	
}
