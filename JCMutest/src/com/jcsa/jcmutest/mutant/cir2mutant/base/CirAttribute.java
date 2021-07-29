package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;


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
	
	/* utility methods */
	/**
	 * @return whether the attribute is taken as a condition
	 */
	public boolean is_constraint() {
		switch(this.type) {
		case cov_count:
		case condition:	return true;
		default:		return false;
		}
	}
	/**
	 * @return {val_error; sta_error; ref_error;}
	 */
	public boolean is_expr_error() {
		switch(this.type) {
		case val_error:
		case sta_error:
		case ref_error:	return true;
		default:		return false;
		}
	}
	/**
	 * @return {trp_error; flw_error; blk_error;}
	 */
	public boolean is_stmt_error() {
		switch(this.type) {
		case trp_error:
		case flw_error:
		case blk_error:	return true;
		default:		return false;
		}
	}
	/**
	 * @return {val_error; sta_error; ref_error; trp_error; flw_error; blk_error;}
	 */
	public boolean is_abst_error() {
		return this.is_expr_error() || this.is_stmt_error();
	}
	
}
