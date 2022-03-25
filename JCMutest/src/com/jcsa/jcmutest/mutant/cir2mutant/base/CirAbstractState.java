package com.jcsa.jcmutest.mutant.cir2mutant.base;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolContext;

/**
 * 	It represents an abstract execution state specified at some program location.
 * 	<br>
 * 	<code>
 * 	CirAbstractState		[class, execution] [location, key]; [lvalue, rvalue]<br>
 * 	<br>
 * 	|--	CirConditionState	[class, execution] [stmt, stm_key]; [xxxxxx, xxxxxx]<br>
 * 	|--	|--	CirCoverTimesState	[cov_stmt]; [stmt, skey]; [lest_most, int_times]<br>
 * 	|--	|--	CirConstraintState	[eva_expr]; [stmt, skey]; [must_need, condition]<br>
 * 	|--	|--	CirSyMutationState	[ast_muta]; [stmt, skey]; [muta_id, mu_operator]<br>
 * 	<br>
 * 	|--	CirPathErrorState	[class, execution] [stmt, stm_key]; [xxxxxx, xxxxxx]<br>
 * 	|--	|--	CirBlockErrorState 	[mut_stmt];	[stmt, skey]; [orig_exec, muta_exec]<br>
 * 	|--	|--	CirFlowsErrorState	[mut_flow]; [stmt, skey]; [orig_trgt, muta_trgt]<br>
 * 	|--	|--	CirTrapsErrorState	[trp_stmt]; [stmt, skey]; [exception, exception]<br>
 * 	<br>
 * 	|--	CirDataErrorState	[class, execution] [expr|stmt, key] [lvalue, rvalue]<br>
 * 	|--	|--	CirValueErrorState	[set_expr]; [expr|stmt, key]; [o_value, m_value]<br>
 * 	|--	|--	CirIncreErrorState	[inc_expr];	[expr|stmt, key]; [b_value, d_value]<br>
 * 	|--	|--	CirBixorErrorState	[inc_expr];	[expr|stmt, key]; [b_value, d_value]<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class CirAbstractState {
	
	/* definitions */
	private CirAbstractClass	category;
	private	CirExecution		execution;
	private CirNode				location;
	private SymbolExpression	identifier;
	private SymbolExpression	loperand;
	private SymbolExpression	roperand;
	protected CirAbstractState(CirAbstractClass category, CirExecution execution,
			CirNode location, SymbolExpression identifier,
			SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		if(category == null) {
			throw new IllegalArgumentException("Invalid category: null");
		}
		else if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(identifier == null) {
			throw new IllegalArgumentException("Invalid identifier: null");
		}
		else if(loperand == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			this.category = category;
			this.execution = execution;
			this.location = location;
			this.identifier = identifier;
			//System.out.println("\t\t" + category + "\t" + loperand.generate_simple_code() + "\t" + roperand.generate_simple_code());
			this.loperand = CirMutations.evaluate(loperand);
			this.roperand = CirMutations.evaluate(roperand);
		}
	}
	
	/* getters */
	/**
	 * @return the category of this state
	 */
	public CirAbstractClass	get_category()	 { return this.category; }
	/**
	 * @return the CFG-node where the state is defined
	 */
	public CirExecution		get_execution()	 { return this.execution; }
	/**
	 * @return the C-intermediate representation point to preserve the state
	 */
	public CirNode			get_location()	 { return this.location; }
	/**
	 * @return the identifier to specify of which store in location preserve
	 */
	public SymbolExpression	get_identifier() { return this.identifier; }
	/**
	 * @return the left-operand in description
	 */
	public SymbolExpression get_loperand()	 { return this.loperand; }
	/**
	 * @return the right-operand in description
	 */
	public SymbolExpression	get_roperand()	 { return this.roperand; }
	
	/* classifier */
	/**
	 * @return whether the state is CirConditionState
	 */
	public boolean is_conditional() {
		switch(this.category) {
		case ast_muta:
		case cov_stmt:
		case eva_expr:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether the state is CirPathErrorState or CirDataErrorState
	 */
	public boolean is_abst_error() {
		switch(this.category) {
		case mut_stmt:
		case mut_flow:
		case trp_stmt:
		case set_expr:
		case inc_expr:
		case xor_expr:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether the state is CirDataErrorState
	 */
	public boolean is_data_error() {
		switch(this.category) {
		case set_expr:
		case inc_expr:
		case xor_expr:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether the state is CirPathErrorState
	 */
	public boolean is_path_error() {
		switch(this.category) {
		case mut_stmt:
		case mut_flow:
		case trp_stmt:	return true;
		default:		return false;
		}
	}
	
	/* identify */
	@Override
	public String toString() {
		return String.format("%s:%s(%d:%s, %s, %s)", 
				this.category.toString(), this.execution.toString(), 
				this.location.get_node_id(), this.identifier.toString(), 
				this.loperand.toString(), this.roperand.toString());
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CirAbstractState) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	
	/* factory */
	/**
	 * @param mutant
	 * @param execution
	 * @return ast_muta([statement, stmt_key]; [muta_id, operator])
	 * @throws Exception
	 */
	public static CirSyMutationState ast_muta(CirExecution execution, int mid, String operator) throws Exception {
		if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirSyMutationState(execution, mid, operator);
		}
	}
	/**
	 * @param execution
	 * @param times
	 * @return cov_stmt([statement, stmt_key]; [true, times])
	 * @throws Exception
	 */
	public static CirCoverTimesState cov_time(CirExecution execution, int times) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(times <= 0) {
			throw new IllegalArgumentException("Invalid times: " + times);
		}
		else {
			return new CirCoverTimesState(execution, true, times);
		}
	}
	/**
	 * @param execution
	 * @param times
	 * @return cov_stmt([statement, stmt_key]; [false, times])
	 * @throws Exception
	 */
	public static CirCoverTimesState lim_time(CirExecution execution, int times) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(times < 0) {
			throw new IllegalArgumentException("Invalid times: " + times);
		}
		else {
			return new CirCoverTimesState(execution, false, times);
		}
	}
	/**
	 * @param execution
	 * @param condition
	 * @return eva_expr([statement, stmt_key]; [true, condition])
	 * @throws Exception
	 */
	public static CirConstraintState eva_must(CirExecution execution, Object condition) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new CirConstraintState(execution, true, condition);
		}
	}
	/**
	 * @param execution
	 * @param condition
	 * @return eva_expr([statement, stmt_key]; [false, condition])
	 * @throws Exception
	 */
	public static CirConstraintState eva_need(CirExecution execution, Object condition) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new CirConstraintState(execution, false, condition);
		}
	}
	/**
	 * @param execution
	 * @param muta_exec True if the statement is set to be executed or False to not
	 * @return mut_stmt([statement, stmt_key]; (!muta_exec, muta_exec))
	 * @throws Exception
	 */
	public static CirBlockErrorState mut_stmt(CirExecution execution, boolean muta_exec) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirBlockErrorState(execution, muta_exec);
		}
	}
	/**
	 * @param orig_flow the original flow to be executed
	 * @param muta_flow the mutated flow to replace with original one
	 * @return mut_flow([orig_flow.source]; [orig_flow.target, muta_flow.target])
	 * @throws Exception
	 */
	public static CirFlowsErrorState mut_flow(CirExecutionFlow orig_flow, CirExecutionFlow muta_flow) throws Exception {
		if(orig_flow == null) {
			throw new IllegalArgumentException("Invalid orig_flow: null");
		}
		else if(muta_flow == null) {
			throw new IllegalArgumentException("Invalid muta_flow: null");
		}
		else if(orig_flow.get_source() != muta_flow.get_source()) {
			throw new IllegalArgumentException("Unmatched flows");
		}
		else {
			return new CirFlowsErrorState(orig_flow, muta_flow);
		}
	}
	/**
	 * @param execution
	 * @return trp_stmt([statement, stmt_key]; [exception, exception])
	 * @throws Exception
	 */
	public static CirTrapsErrorState trp_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			execution = execution.get_graph().get_exit();
			return new CirTrapsErrorState(execution);
		}
	}
	/**
	 * @param expression the use expression or left-value to be assigned 
	 * @param muta_value the value to replace the original expression state
	 * @return set_expr([expression, expr_key]; [orig_value, muta_value]
	 * @throws Exception
	 */
	public static CirValueErrorState set_expr(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.execution_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(CirMutations.is_boolean(expression)) {
			CirExecution execution = expression.execution_of();
			SymbolExpression identifier = SymbolFactory.sym_expression(expression);
			SymbolExpression ori_value, mut_value;
			ori_value = SymbolFactory.sym_condition(expression, true);
			mut_value = SymbolFactory.sym_condition(muta_value, true);
			return new CirValueErrorState(execution, expression, identifier, ori_value, mut_value);
		}
		else if(CirMutations.is_assigned(expression)) {
			CirExecution execution = expression.execution_of();
			SymbolExpression identifier = SymbolFactory.sym_expression(expression);
			SymbolExpression ori_value, mut_value;
			CirAssignStatement statement = (CirAssignStatement) execution.get_statement();
			ori_value = SymbolFactory.sym_expression(statement.get_rvalue());
			mut_value = SymbolFactory.sym_expression(muta_value);
			return new CirValueErrorState(execution, expression, identifier, ori_value, mut_value);
		}
		else {
			CirExecution execution = expression.execution_of();
			SymbolExpression identifier = SymbolFactory.sym_expression(expression);
			SymbolExpression ori_value, mut_value;
			ori_value = SymbolFactory.sym_expression(expression);
			mut_value = SymbolFactory.sym_expression(muta_value);
			return new CirValueErrorState(execution, expression, identifier, ori_value, mut_value);
		}
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @return set_expr([statement, expr_key]; [orig_value, muta_value])
	 * @throws Exception
	 */
	public static CirValueErrorState set_vdef(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.execution_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			CirExecution execution = expression.execution_of();
			SymbolExpression identifier = SymbolFactory.sym_expression(expression);
			SymbolExpression ori_value, mut_value;
			if(CirMutations.is_boolean(expression)) {
				ori_value = SymbolFactory.sym_condition(expression, true);
				mut_value = SymbolFactory.sym_condition(muta_value, true);
			}
			else {
				ori_value = SymbolFactory.sym_expression(expression);
				mut_value = SymbolFactory.sym_expression(muta_value);
			}
			return new CirValueErrorState(execution, execution.get_statement(), 
					identifier, ori_value, mut_value);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return inc_expr([expression, expr_key]; [base_value, difference])
	 * @throws Exception
	 */
	public static CirIncreErrorState inc_expr(CirExpression expression, Object difference) throws Exception {
		if(expression == null || expression.execution_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else if(CirMutations.is_assigned(expression)) {
			CirExecution execution = expression.execution_of();
			SymbolExpression identifier = SymbolFactory.sym_expression(expression);
			SymbolExpression ori_value, mut_value;
			CirAssignStatement statement = (CirAssignStatement) execution.get_statement();
			ori_value = SymbolFactory.sym_expression(statement.get_rvalue());
			mut_value = SymbolFactory.sym_expression(difference);
			return new CirIncreErrorState(execution, expression, identifier, ori_value, mut_value);
		}
		else {
			CirExecution execution = expression.execution_of();
			SymbolExpression identifier = SymbolFactory.sym_expression(expression);
			SymbolExpression ori_value, mut_value;
			ori_value = SymbolFactory.sym_expression(expression);
			mut_value = SymbolFactory.sym_expression(difference);
			return new CirIncreErrorState(execution, expression, identifier, ori_value, mut_value);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return xor_expr([expression, expr_key]; [base_value, difference])
	 * @throws Exception
	 */
	public static CirBixorErrorState xor_expr(CirExpression expression, Object difference) throws Exception {
		if(expression == null || expression.execution_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else if(CirMutations.is_assigned(expression)) {
			CirExecution execution = expression.execution_of();
			SymbolExpression identifier = SymbolFactory.sym_expression(expression);
			SymbolExpression ori_value, mut_value;
			CirAssignStatement statement = (CirAssignStatement) execution.get_statement();
			ori_value = SymbolFactory.sym_expression(statement.get_rvalue());
			mut_value = SymbolFactory.sym_expression(difference);
			return new CirBixorErrorState(execution, expression, identifier, ori_value, mut_value);
		}
		else {
			CirExecution execution = expression.execution_of();
			SymbolExpression identifier = SymbolFactory.sym_expression(expression);
			SymbolExpression ori_value, mut_value;
			ori_value = SymbolFactory.sym_expression(expression);
			mut_value = SymbolFactory.sym_expression(difference);
			return new CirBixorErrorState(execution, expression, identifier, ori_value, mut_value);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return inc_expr([statement, expr_key]; [base_value, difference]
	 * @throws Exception
	 */
	public static CirIncreErrorState inc_vdef(CirExpression expression, Object difference) throws Exception {
		if(expression == null || expression.execution_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else {
			CirExecution execution = expression.execution_of();
			SymbolExpression identifier = SymbolFactory.sym_expression(expression);
			SymbolExpression ori_value, mut_value;
			ori_value = SymbolFactory.sym_expression(expression);
			mut_value = SymbolFactory.sym_expression(difference);
			return new CirIncreErrorState(execution, execution.get_statement(),
					identifier, ori_value, mut_value);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return inc_expr([statement, expr_key]; [base_value, difference]
	 * @throws Exception
	 */
	public static CirBixorErrorState xor_vdef(CirExpression expression, Object difference) throws Exception {
		if(expression == null || expression.execution_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else {
			CirExecution execution = expression.execution_of();
			SymbolExpression identifier = SymbolFactory.sym_expression(expression);
			SymbolExpression ori_value, mut_value;
			ori_value = SymbolFactory.sym_expression(expression);
			mut_value = SymbolFactory.sym_expression(difference);
			return new CirBixorErrorState(execution, execution.get_statement(),
					identifier, ori_value, mut_value);
		}
	}
	
	/* normalization, evaluation, subsume, summarization */
	/**
	 * @param context
	 * @return the normalized version of the state under the context
	 * @throws Exception
	 */
	public CirAbstractState normalize(SymbolContext context) throws Exception {
		return CirMutations.normalize(this, context);
	}
	/**
	 * @return the normalized version of the state under any context
	 * @throws Exception
	 */
	public CirAbstractState normalize() throws Exception {
		return CirMutations.normalize(this);
	}
	/**
	 * @param context
	 * @return True {passed}; False {failed}; null {unknown}
	 * @throws Exception
	 */
	public Boolean evaluate(SymbolContext context) throws Exception {
		return CirMutations.evaluate(this, context);
	}
	/**
	 * @return True {passed}; False {failed}; null {unknown}
	 * @throws Exception
	 */
	public Boolean evaluate() throws Exception {
		return CirMutations.evaluate(this);
	}
	/**
	 * @return the set of states directly subsumed by this one locally
	 * @throws Exception
	 */
	public Collection<CirAbstractState> extend_one() throws Exception {
		return CirMutations.extend_one(this);
	}
	/**
	 * @return the set of all states subsumed by this one locally 
	 * @throws Exception
	 */
	public Collection<CirAbstractState> extend_all() throws Exception {
		return CirMutations.extend_all(this);
	}
	/**
	 * @param context
	 * @return the set of states directly subsumed by this state under the context
	 * @throws Exception
	 */
	public Collection<CirAbstractState> subsume(Object context) throws Exception {
		return CirMutations.subsume(this, context);
	}
	
}
