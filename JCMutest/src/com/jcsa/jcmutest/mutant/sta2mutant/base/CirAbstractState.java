package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It defines the abstract execution state describing the set of concrete 
 * states used in mutation execution analysis.
 * <br>
 * <code>
 * 	CirAbstractState			[category, execution, location, parameters]		<br>
 * 	<br>
 * 	|--	CirConditionState		[category, execution, statement, {parameter}]	<br>
 * 	|--	|--	CirCoverTimesState	[cov_time, execution, statement, {int_times}]	<br>
 * 	|--	|--	CirConstraintState	[eva_expr, execution, statement, {condition}]	<br>
 * 	|--	|--	CirTerminatedState	[end_stmt, execution, statement, {exception}]	<br>
 * 	<br>
 * 	|--	CirDataErrorState		[category, execution, expression, {oval, mval}]	<br>
 * 	|--	|--	CirDiferErrorState	[mut_diff, execution, expression, {oval, mval}]	<br>
 * 	|--	|--	CirValueErrorState	[mut_expr, execution, expression, {oval, mval}]	<br>
 * 	|--	|--	CirReferErrorState	[mut_refr, execution, reference,  {oval, mval}]	<br>
 * 	<br>
 * 	|--	CirDifferentState		[category, execution, expression, {difference}]	<br>
 * 	|--	|--	CirIncreErrorState	[inc_expr, execution, expression, {difference}]	<br>
 * 	|--	|--	CirBixorErrorState	[xor_expr, execution, expression, {difference}]	<br>
 * 	|--	|--	CirScopeErrorState	[scp_expr, execution, expression, {difference}]	<br>
 * 	<br>
 * 	|--	CirPathErrorState		[category, execution, statement,  {oexe, mexe}]	<br>
 * 	|--	|--	CirBlockErrorState	[mut_stmt, execution, statement,  {bool, bool}]	<br>
 * 	|--	|--	CirFlowsErrorState	[mut_flow, execution, statement,  {ostm, mstm}]	<br>
 * 	|--	|--	CirBrachErrorState	[mut_brac, execution, statement,  {ocon, mcon}]	<br>
 * </code>
 * <br>
 * @author yukimula
 *
 */
public abstract class CirAbstractState {
	
	/* definitions */
	private CirStateCategory 	category;
	private CirExecution 		execution;
	private CirNode				location;
	private SymbolExpression[]	parameters;
	
	/* constructor */
	/**
	 * construct an abstract state description object
	 * @param category
	 * @param execution
	 * @param location
	 * @param number_of_parameters
	 * @throws Exception
	 */
	protected CirAbstractState(CirStateCategory category,
			CirExecution execution, CirNode location,
			int number_of_parameters) throws Exception {
		if(category == null) {
			throw new IllegalArgumentException("Invalid category as null");
		}
		else if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(number_of_parameters < 0) {
			throw new IllegalArgumentException("Invalid:" + number_of_parameters);
		}
		else {
			this.category = category;
			this.execution = execution;
			this.location = location;
			this.parameters = new SymbolExpression[number_of_parameters];
		}
	}
	/**
	 * set the kth parameter in the body
	 * @param k
	 * @param parameter
	 * @throws Exception
	 */
	protected void set_parameter(int k, SymbolExpression parameter) throws Exception {
		if(k < 0 || k >= this.parameters.length) {
			throw new IndexOutOfBoundsException("Invalid k: " + k + 
						" for length = " + this.parameters.length);
		}
		else if(parameter == null) {
			throw new IllegalArgumentException("Invalid parameter");
		}
		else {
			this.parameters[k] = parameter;
		}
	}
	
	/* getters */
	/**
	 * @return the category keyword of this state
	 */
	public	CirStateCategory	get_category()	{ return this.category; }
	/**
	 * @return the control flow node where this state is defined over
	 */
	public 	CirExecution		get_execution()	{ return this.execution; }
	/**
	 * @return the statement where the state is defined on
	 */
	public	CirStatement		get_statement() { return this.execution.get_statement(); }
	/**
	 * @return the C-intermediate code location on which the state works
	 */
	public	CirNode				get_location()	{ return this.location; }
	/**
	 * @param k
	 * @return the kth symbolic expression as the parameter of the state description
	 * @throws IndexOutOfBoundsException
	 */
	protected	SymbolExpression	get_parameter(int k) throws IndexOutOfBoundsException { return this.parameters[k]; }
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.category.toString());
		buffer.append("#");
		buffer.append(this.execution.toString());
		buffer.append("#" + this.location.get_node_id());
		for(SymbolExpression parameter : this.parameters) {
			buffer.append(":");
			buffer.append(parameter.toString());
		}
		return buffer.toString();
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		else if(obj instanceof CirAbstractState) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	
	/* factory methods */
	/* conditions state */
	/**
	 * @param execution	the statement that needs to be executed on the state
	 * @param int_times	the minimal times the statement needs be executed on
	 * @return 			cov_time(execution, statement, [int_times]);
	 * @throws Exception
	 */
	public static CirCoverTimesState cov_time(CirExecution execution, int int_times) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(int_times <= 0) {
			throw new IllegalArgumentException("Invalid int_times: null");
		}
		else {
			return new CirCoverTimesState(execution, SymbolFactory.sym_constant(int_times));
		}
	}
	/**
	 * @param execution	the statement where the constraint will be evaluated
	 * @param condition	the constraint being evaluated to define the state(s)
	 * @return			eva_expr(execution, statement, [condition]);
	 * @throws Exception
	 */
	public static CirConstraintState eva_expr(CirExecution execution, Object condition) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			SymbolExpression constraint = SymbolFactory.sym_condition(condition, true);
			return new CirConstraintState(execution, constraint);
		}
	}
	/**
	 * @param execution
	 * @return end_stmt(execution, statement, [exception]);
	 * @throws Exception
	 */
	public static CirTerminatedState end_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirTerminatedState(
					execution.get_graph().get_exit(),
					CirStateValuation.trap_value);
		}
	}
	/* path error state */
	/**
	 * @param execution		the statement that will be (NOT) executed in mutated version
	 * @param muta_execute	whether the statement is executed (or not) in mutated program
	 * @return				mut_stmt(execution, statement, [!mval, mval]);
	 * @throws Exception
	 */
	public static CirBlockErrorState mut_stmt(CirExecution execution, boolean muta_execute) throws Exception {
		if(execution == null || execution.get_statement() instanceof CirTagStatement) {
			throw new IllegalArgumentException("Invalid execution: " + execution);
		}
		else {
			return new CirBlockErrorState(execution,
					SymbolFactory.sym_constant(Boolean.valueOf(!muta_execute)),
					SymbolFactory.sym_constant(Boolean.valueOf(muta_execute)));
		}
	}
	/**
	 * @param source		the statement from which the flow error will occur in the specified states
	 * @param orig_target	the next statement being executed from source in original version
	 * @param muta_target	the next statement being executed from source in mutation version
	 * @return				mut_flow(source, statement, [orig_target, muta_target]);
	 * @throws Exception
	 */
	public static CirFlowsErrorState mut_flow(CirExecution source, 
			CirExecution orig_target, CirExecution muta_target) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(orig_target == null) {
			throw new IllegalArgumentException("Invalid orig_target");
		}
		else if(muta_target == null) {
			throw new IllegalArgumentException("Invalid muta_target");
		}
		else {
			return new CirFlowsErrorState(source,
					SymbolFactory.sym_expression(orig_target),
					SymbolFactory.sym_expression(muta_target));
		}
	}
	/**
	 * @param execution			the conditional statement where the branch error will be introduced
	 * @param muta_condition	the mutated condition to replace original predicate in the statement
	 * @return					mut_brac(execution, statement, [orig_condition, muta_condition]);
	 * @throws Exception
	 */
	public static CirBrachErrorState mut_brac(CirExecution execution, Object muta_condition) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			CirStatement statement = execution.get_statement();
			SymbolExpression orig_value, muta_value;
			if(statement instanceof CirIfStatement) {
				orig_value = SymbolFactory.sym_condition(
						((CirIfStatement) statement).get_condition(), true);
			}
			else if(statement instanceof CirCaseStatement) {
				orig_value = SymbolFactory.sym_condition(
						((CirCaseStatement) statement).get_condition(), true);
			}
			else {
				throw new IllegalArgumentException(statement.generate_code(true));
			}
			muta_value = SymbolFactory.sym_condition(muta_condition, true);
			return new CirBrachErrorState(execution, orig_value, muta_value);
		}
	}
	/* data error state */
	/**
	 * @param expression	the (used) expression on which the data error will be seeded in the state
	 * @param muta_value	the mutated value to replace the original use of expression in this state
	 * @return				mut_diff(execution, expression, [orig_value, muta_value]);
	 * @throws Exception
	 */
	public static CirDiferErrorState mut_diff(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.execution_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(StateMutations.is_assigned(expression)) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else {
			SymbolExpression ovalue = SymbolFactory.sym_expression(expression);
			SymbolExpression mvalue = SymbolFactory.sym_expression(muta_value);
			return new CirDiferErrorState(expression, ovalue, mvalue);
		}
	}
	/**
	 * @param expression	the (used) expression on which the data error will be seeded in the state
	 * @param muta_value	the mutated value to replace the original use of expression in this state
	 * @return				mut_expr(execution, expression, [orig_value, muta_value]);
	 * @throws Exception
	 */
	public static CirValueErrorState mut_expr(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.execution_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(StateMutations.is_assigned(expression)) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else {
			SymbolExpression ovalue = SymbolFactory.sym_expression(expression);
			SymbolExpression mvalue = SymbolFactory.sym_expression(muta_value);
			if(StateMutations.is_boolean(expression)) {
				ovalue = SymbolFactory.sym_condition(ovalue, true);
				mvalue = SymbolFactory.sym_condition(mvalue, true);
			}
			return new CirValueErrorState(expression, ovalue, mvalue);
		}
	}
	/**
	 * @param expression	the (defined) reference where the data error is introduced in this state
	 * @param muta_value	the mutated value to replace the original def of expression in the state
	 * @return				mut_refr(execution, expression, [orig_value, muta_value]);
	 * @throws Exception
	 */
	public static CirReferErrorState mut_refr(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.execution_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(StateMutations.is_assigned(expression)) {
			CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
			SymbolExpression ovalue = SymbolFactory.sym_expression(statement.get_rvalue());
			SymbolExpression mvalue = SymbolFactory.sym_expression(muta_value);
			if(StateMutations.is_boolean(expression)) {
				ovalue = SymbolFactory.sym_condition(ovalue, true);
				mvalue = SymbolFactory.sym_condition(mvalue, true);
			}
			return new CirReferErrorState(expression, ovalue, mvalue);
		}
		else {
			SymbolExpression ovalue = SymbolFactory.sym_expression(expression);
			SymbolExpression mvalue = SymbolFactory.sym_expression(muta_value);
			if(StateMutations.is_boolean(expression)) {
				ovalue = SymbolFactory.sym_condition(ovalue, true);
				mvalue = SymbolFactory.sym_condition(mvalue, true);
			}
			return new CirReferErrorState(expression, ovalue, mvalue);
		}
	}
	/* difference state */
	/**
	 * @param expression	the expression into which the difference will be introduced
	 * @param difference	the difference being incremented into the target expression
	 * @return				inc_expr(execution, expression, [difference]);
	 * @throws Exception
	 */
	public static CirIncreErrorState inc_expr(CirExpression expression, Object difference) throws Exception {
		if(expression == null || expression.execution_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(StateMutations.is_assigned(expression)) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(StateMutations.is_numeric(expression) 
				|| StateMutations.is_address(expression)) {
			return new CirIncreErrorState(expression, SymbolFactory.sym_expression(difference));
		}
		else {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
	}
	/**
	 * @param expression	the expression into which the difference will be introduced
	 * @param difference	the difference being incremented into the target expression
	 * @return				xor_expr(execution, expression, [difference]);
	 * @throws Exception
	 */
	public static CirBixorErrorState xor_expr(CirExpression expression, Object difference) throws Exception {
		if(expression == null || expression.execution_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(StateMutations.is_assigned(expression)) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(StateMutations.is_integer(expression)) {
			return new CirBixorErrorState(expression, SymbolFactory.sym_expression(difference));
		}
		else {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
	}
	/**
	 * @param expression	the expression into which the difference will be introduced
	 * @param difference	the difference being incremented into the target expression
	 * @return				scp_expr(execution, expression, [difference]);
	 * @throws Exception
	 */
	public static CirScopeErrorState scp_expr(CirExpression expression, Object difference) throws Exception {
		if(expression == null || expression.execution_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(StateMutations.is_assigned(expression)) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(StateMutations.is_numeric(expression)) {
			return new CirScopeErrorState(expression, SymbolFactory.sym_expression(difference));
		}
		else {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
	}
	
	
	
	
	
}
