package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;


/**
 * 	Structural symbolic condition is defined as following:	<br>
 * 	1. category: either "assertion" or "observation";		<br>
 * 	2. operator: operator to refine the descriptions;		<br>
 * 	3. execution: the execution point of the condition;		<br>
 * 	4. location: the CIR-node where the condition defined;	<br>
 * 	5. parameter: symbolic expression or null if useless;	<br>
 * 
 * 	@author yukimula
 *
 */
public class SymCondition {
	
	/* attributes */
	/** either "assertion" or "observation" **/
	private SymCategory category;
	/** operator to refine the descriptions **/
	private SymOperator operator;
	/** the execution point of the condition **/
	private CirExecution execution;
	/** the CIR-node where the condition defined **/
	private CirNode location;
	/** symbolic expression or null if useless **/
	private SymbolExpression parameter;
	/**
	 * private creator for factory mode
	 * @param category	either "assertion" or "observation"
	 * @param operator	operator to refine the descriptions
	 * @param execution	the execution point of the condition
	 * @param location	the CIR-node where the condition defined
	 * @param parameter	symbolic expression or null if useless
	 * @throws IllegalArgumentException
	 */
	protected SymCondition(SymCategory category, SymOperator operator, CirExecution execution, 
			CirNode location, SymbolExpression parameter) throws IllegalArgumentException {
		if(category == null)
			throw new IllegalArgumentException("Invalid category: null");
		else if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else {
			this.category = category;
			this.operator = operator;
			this.execution = execution;
			this.location = location;
			this.parameter = parameter;
		}
	}
	
	/* getters */
	/**
	 * @return either "assertion" or "observation"
	 */
	public SymCategory get_category() { return this.category; }
	/**
	 * @return operator to refine the descriptions
	 */
	public SymOperator get_operator() { return this.operator; }
	/**
	 * @return the execution point of the condition
	 */
	public CirExecution get_execution() { return this.execution; }
	/**
	 * @return the CIR-node where the condition defined
	 */
	public CirNode get_location() { return this.location; }
	/**
	 * @return symbolic expression or null if useless
	 */
	public SymbolExpression get_parameter() { return this.parameter; }
	/**
	 * @return whether parameter is used in this condition
	 */
	public boolean has_parameter() { return this.parameter != null; }
	/**
	 * @return whether the condition is constraint
	 */
	public boolean is_constraint() { return this.category == SymCategory.constraints; }
	/**
	 * @return whether the condition is state-error
	 */
	public boolean is_state_error() { return this.category == SymCategory.observation; }
	
	/* compare */
	@Override
	public String toString() {
		return this.category + "@" + this.operator + "@" + this.execution + 
					"@" + this.location.get_node_id() + "@" + this.parameter;
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		else if(obj instanceof SymCondition)
			return this.toString().equals(obj.toString());
		else
			return false;
	}
	
}
