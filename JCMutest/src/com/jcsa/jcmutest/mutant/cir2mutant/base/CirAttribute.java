package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	It denotes a symbolic attribute required in software testing. 			<br>
 * 	<code>
 * 		CirAttribute		{type; execution; location; parameter;}			<br>
 * 		|--	CirConstraint	{condition; execution; statement; expression;}	<br>
 * 		|--	CirCoverCount	{cov_count; execution; statement; integer;}		<br>
 * 		|--	CirValueError	{val_error; execution; expression; value;}		<br>
 * 		|--	CirReferError	{ref_error; execution; expression; value;}		<br>
 * 		|--	CirStateError	{sta_error; execution; reference; value;}		<br>
 * 		|--	CirFlowsError	{flw_error; if_exec; orig_target; muta_target;}	<br>
 * 		|--	CirBlockError	{blk_error; execution; statement; true|false;}	<br>
 * 		|--	CirTrapsError	{trp_error; execution; statement; true;}		<br>
 * 	</code>
 * 
 * @author yukimula
 *
 */
public abstract class CirAttribute {
	
	/* attributes */
	/** the attribute type of the attribute instance **/
	private CirAttributeType	type;
	/** the CFG-node where the attribute is evaluated **/
	private CirExecution 		execution;
	/** the C-intermediate code location to define it **/
	private CirNode		 		location; 
	/** the symbolic expression used as parameter **/
	private SymbolExpression	parameter;
	
	/* constructor */
	/**
	 * create an abstract attribute to denote the requirement in testing
	 * @param type		the attribute type of the attribute instance 
	 * @param execution	the CFG-node where the attribute is evaluated
	 * @param location	the C-intermediate code location to define it
	 * @param parameter	the symbolic expression used as parameter
	 * @throws IllegalArgumentException
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
			throw new IllegalArgumentException("Invalid location as null");
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
	 * @return the attribute type of the attribute instance
	 */
	public CirAttributeType get_type() { return this.type; }
	/**
	 * @return the CFG-node where the attribute is evaluated
	 */
	public CirExecution get_execution() { return this.execution; }
	/**
	 * @return the C-intermediate code location to define it
	 */
	public CirNode get_location() { return this.location; }
	/**
	 * @return the symbolic expression used as parameter
	 */
	public SymbolExpression get_parameter() { return this.parameter; }
	
	/* universal */
	@Override
	public String toString() {
		return this.type + "$" + this.execution + "$" + 
				this.location.get_node_id() + "$" + this.parameter;
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
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
	
	/* factory methods */
	/**
	 * @param execution
	 * @param expression
	 * @param value
	 * @return {condition; execution; statement; expression;}
	 * @throws Exception
	 */
	public static CirConstraint new_constraint(CirExecution execution, 
				Object expression, boolean value) throws Exception {
		return new CirConstraint(execution, SymbolFactory.sym_condition(expression, value));
	}
	/**
	 * @param execution
	 * @param times
	 * @return
	 * @throws Exception
	 */
	public static CirCoverCount new_coverage_count(CirExecution execution, int times) throws Exception {
		return new CirCoverCount(execution, SymbolFactory.sym_constant(Integer.valueOf(times)));
	}
	/**
	 * @param execution
	 * @return {trp_error; execution; statement; true;}
	 * @throws Exception
	 */
	public static CirTrapsError new_trap_error(CirExecution execution) throws Exception {
		return new CirTrapsError(execution, SymbolFactory.sym_constant(Boolean.TRUE));
	}
	/**
	 * @param execution
	 * @param execute
	 * @return {blk_error; execution; statement; true|false;}
	 * @throws Exception
	 */
	public static CirBlockError new_block_error(CirExecution execution, boolean execute) throws Exception {
		return new CirBlockError(execution, SymbolFactory.sym_constant(Boolean.valueOf(execute)));
	}
	/**
	 * @param orig_flow
	 * @param muta_flow
	 * @return {flw_error; if_exec; orig_target; muta_target;}
	 * @throws Exception
	 */
	public static CirFlowsError new_flow_error(CirExecutionFlow orig_flow, CirExecutionFlow muta_flow) throws Exception {
		return new CirFlowsError(orig_flow.get_source(), 
				orig_flow.get_target().get_statement(),
				SymbolFactory.sym_expression(muta_flow.get_target()));
	}
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return {val_error; execution; expression; value;}
	 * @throws Exception
	 */
	public static CirValueError new_value_error(CirExpression orig_expression, Object muta_expression) throws Exception {
		return new CirValueError(orig_expression.get_tree().get_localizer().
				get_execution(orig_expression.statement_of()),
				orig_expression, SymbolFactory.sym_expression(muta_expression));
	}
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return {ref_error; execution; expression; value;}
	 * @throws Exception
	 */
	public static CirReferError new_refer_error(CirExpression orig_expression, Object muta_expression) throws Exception {
		return new CirReferError(orig_expression.get_tree().get_localizer().
				get_execution(orig_expression.statement_of()),
				orig_expression, SymbolFactory.sym_expression(muta_expression));
	}
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return {val_error; execution; expression; value;}
	 * @throws Exception
	 */
	public static CirStateError new_state_error(CirExpression orig_expression, Object muta_expression) throws Exception {
		return new CirStateError(orig_expression.get_tree().get_localizer().
				get_execution(orig_expression.statement_of()),
				orig_expression, SymbolFactory.sym_expression(muta_expression));
	}
	
}
