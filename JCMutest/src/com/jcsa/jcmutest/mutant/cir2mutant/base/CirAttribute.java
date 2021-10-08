package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * 	It describes an attribute defined in software testing objective.			<br>
 * 																				<br>
 * 	<code>
 * 		CirAttribute			{type, execution, location, parameter}			<br>
 * 		|--	CirConstraint		{condition, execution, statement, expression}	<br>
 * 		|--	CirCoverCount		{cov_count, execution, statement, integer}		<br>
 * 		|--	CirBlockError		{blk_error, execution, statement, true|false}	<br>
 * 		|--	CirFlowsError		{flw_error, source, orig_target, muta_target}	<br>
 * 		|--	CirTrapsError		{trp_error, execution, statement, true}			<br>
 * 		|--	CirDiferError		{dif_error, execution, orig_expr, muta_expr}	<br>
 * 		|--	CirValueError		{val_error, execution, orig_expr, muta_expr}	<br>
 * 		|--	CirReferError		{ref_error, execution, orig_refr, muta_refr}	<br>
 * 		|--	CirStateError		{sta_error, execution, orig_refr, muta_expr}	<br>
 * 	</code>
 *
 * 	@author yukimula
 */
public abstract class CirAttribute {

	/* attributes */
	/** the type of the attribute to define software testing requirement **/
	private CirAttributeType	type;
	/** the execution point where the attribute is evaluated or injected **/
	private CirExecution 		execution;
	/** the C-intermediate code location to refine this attribute's node **/
	private CirNode				location;
	/** the symbolic parameter to refine the definition of the attribute **/
	private SymbolExpression	parameter;

	/* constructor */
	/**
	 * create an attribute to denote test requirement.
	 * @param type
	 * @param execution
	 * @param location
	 * @param parameter
	 * @throws Exception
	 */
	protected CirAttribute(CirAttributeType type, CirExecution execution,
			CirNode location, SymbolExpression parameter) throws IllegalArgumentException {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type as null");
		}
		else if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(parameter == null) {
			throw new IllegalArgumentException("Invalid parameter: null");
		}
		else {
			this.type = type;
			this.execution = execution;
			this.location = location;
			this.parameter = parameter;
		}
	}

	/* getters */
	/**
	 * @return the type of the attribute to define software testing requirement
	 */
	public CirAttributeType get_type() { return this.type; }
	/**
	 * @return the execution point where the attribute is evaluated or injected
	 */
	public CirExecution get_execution() { return this.execution; }
	/**
	 * @return the C-intermediate code location to refine this attribute's node
	 */
	public CirNode get_location() { return this.location; }
	/**
	 * @return the symbolic parameter to refine the definition of the attribute
	 */
	public SymbolExpression get_parameter() { return this.parameter; }

	/* universals */
	@Override
	public String toString() {
		return this.type + "$" + this.execution + "$" +
				this.location.get_node_id() + "$" + this.parameter;
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		else if(obj instanceof CirAttribute) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}

	/* utility methods */
	/**
	 * @return whether it is constraint {condition|cov_count}
	 */
	public boolean is_constraint() {
		switch(this.type) {
		case condition:
		case cov_count:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether it is expression-based error {val|ref|sta_error}
	 */
	public boolean is_expr_error() {
		switch(this.type) {
		case val_error:
		case ref_error:
		case sta_error:	
		case dif_error:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether it is statement-based error {trp|flw|blk_error}
	 */
	public boolean is_stmt_error() {
		switch(this.type) {
		case flw_error:
		case trp_error:
		case blk_error:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether it is error (expression or statement)
	 */
	public boolean is_abst_error() {
		return this.is_expr_error() || this.is_stmt_error();
	}
	/**
	 * @return generate the optimized version of the attribute using non-context
	 * @throws Exception
	 */
	public CirAttribute optimize() throws Exception { return this.optimize(null); }
	/**
	 * @return true(satisfied), false(not-satisfied), null(unknown)
	 * @throws Exception
	 */
	public Boolean evaluate() throws Exception { return this.evaluate(null); }

	/* extension methods */
	/**
	 * @param context
	 * @return generate the optimized version of the attribute under the context
	 * @throws Exception
	 */
	public abstract CirAttribute optimize(SymbolProcess context) throws Exception;
	/**
	 * @param context
	 * @return true(satisfied), false(not-satisfied), null(unknown)
	 * @throws Exception
	 */
	public abstract Boolean evaluate(SymbolProcess context) throws Exception;

	/* factory methods */
	/**
	 * @param execution
	 * @param expression
	 * @param value
	 * @return {condition, execution, statement, expression}
	 * @throws Exception
	 */
	public static CirConstraint new_constraint(CirExecution execution, Object expression, boolean value) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirConstraint(execution, SymbolFactory.sym_condition(expression, value));
		}
	}
	/**
	 * @param execution
	 * @param times
	 * @return {cov_count, execution, statement, integer}
	 * @throws Exception
	 */
	public static CirCoverCount new_cover_count(CirExecution execution, int times) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(times <= 0) {
			throw new IllegalArgumentException("Invalid times: " + times);
		}
		else {
			return new CirCoverCount(execution, SymbolFactory.sym_constant(Integer.valueOf(times)));
		}
	}
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return {dif_error, execution, orig_expr, muta_expr}
	 * @throws Exception
	 */
	public static CirDiferError new_difer_error(CirExpression orig_expression, SymbolExpression muta_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else {
			CirStatement statement = orig_expression.statement_of();
			CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
			return new CirDiferError(execution, orig_expression, muta_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return {val_error, execution, orig_expr, muta_expr}
	 * @throws Exception
	 */
	public static CirValueError new_value_error(CirExpression orig_expression, SymbolExpression muta_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else {
			CirStatement statement = orig_expression.statement_of();
			CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
			return new CirValueError(execution, orig_expression, muta_expression);
		}
	}
	/**
	 * @param orig_reference
	 * @param muta_expression
	 * @return {ref_error, execution, orig_refr, muta_refr}
	 * @throws Exception
	 */
	public static CirReferError new_refer_error(CirExpression orig_reference, SymbolExpression muta_expression) throws Exception {
		if(orig_reference == null) {
			throw new IllegalArgumentException("Invalid orig_reference: null");
		}
		else {
			CirStatement statement = orig_reference.statement_of();
			CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
			return new CirReferError(execution, orig_reference, muta_expression);
		}
	}
	/**
	 * @param orig_reference
	 * @param muta_expression
	 * @return {sta_error, execution, orig_refr, muta_expr}
	 * @throws Exception
	 */
	public static CirStateError new_state_error(CirExpression orig_reference, SymbolExpression muta_expression) throws Exception {
		if(orig_reference == null) {
			throw new IllegalArgumentException("Invalid orig_reference: null");
		}
		else {
			CirStatement statement = orig_reference.statement_of();
			CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
			return new CirStateError(execution, orig_reference, muta_expression);
		}
	}
	/**
	 * @param orig_flow
	 * @param muta_flow
	 * @return {flw_error, source, orig_target, muta_target}
	 * @throws Exception
	 */
	public static CirFlowsError new_flows_error(CirExecutionFlow orig_flow, CirExecutionFlow muta_flow) throws Exception {
		if(orig_flow == null) {
			throw new IllegalArgumentException("Invalid orig_flow: null");
		}
		else if(muta_flow == null) {
			throw new IllegalArgumentException("Invalid muta_flow: null");
		}
		else if(orig_flow.get_source() == muta_flow.get_source()) {
			return new CirFlowsError(orig_flow, SymbolFactory.sym_expression(muta_flow.get_target()));
		}
		else {
			throw new IllegalArgumentException(orig_flow + " --> " + muta_flow);
		}
	}
	/**
	 * @param execution
	 * @param execute
	 * @return {blk_error, execution, statement, true|false}
	 * @throws Exception
	 */
	public static CirBlockError new_block_error(CirExecution execution, boolean execute) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirBlockError(execution, SymbolFactory.sym_constant(Boolean.valueOf(execute)));
		}
	}
	/**
	 * @param execution
	 * @return {trp_error, execution, statement, true}
	 * @throws Exception
	 */
	public static CirTrapsError new_traps_error(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirTrapsError(execution, SymbolFactory.sym_constant(Boolean.TRUE));
		}
	}
	/**
	 * @param mutation
	 * @return kill_muta:execution:statement:literal
	 * @throws Exception 
	 */
	public static CirKillMutant new_kill_mutant(CirMutation mutation) throws Exception {
		return new CirKillMutant(mutation);
	}
	
}
