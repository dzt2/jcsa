package com.jcsa.jcmutest.mutant.cir2mutant.enco;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * It denotes an abstract annotation to describe the abstract features of killing process.
 * 
 * @author yukimula
 *
 */
public class CirAnnotation {
	
	/* attributes */
	/** abstract category of the annotation **/
	private CirAnnotationClass		category;
	/** refined class of the annotation **/
	private CirAnnotationType		operator;
	/** the CFG-node where the annotation is evaluated **/
	private CirExecution			execution;
	/** the C-intermediate location where annotation is defined **/
	private CirNode					location;
	/** symbolic expression as the parameter to refine the node **/
	private SymbolExpression		parameter;
	
	/* constructor */
	/**
	 * @param category
	 * @param operator
	 * @param execution
	 * @param location
	 * @param parameter
	 * @throws IllegalArgumentException
	 */
	protected CirAnnotation(CirAnnotationClass category, CirAnnotationType operator, CirExecution 
			execution, CirNode location, SymbolExpression parameter) throws IllegalArgumentException {
		if(category == null) {
			throw new IllegalArgumentException("Invalid category: null");
		}
		else if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
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
	 * @return abstract category of the annotation
	 */
	public CirAnnotationClass	get_category() 	{ return this.category; }
	/**
	 * @return the refined class of the annotation
	 */
	public CirAnnotationType	get_operator() 	{ return this.operator; }
	/**
	 * @return the CFG-node where the annotation is evaluated
	 */
	public CirExecution			get_execution() { return this.execution; }
	/**
	 * @return the C-intermediate location where annotation is defined
	 */
	public CirNode				get_location() 	{ return this.location; }
	/**
	 * @return whether the parameter is not null
	 */
	public boolean				has_parameter() { return this.parameter != null; }
	/**
	 * @return symbolic expression as the parameter to refine the node
	 */
	public SymbolExpression		get_parameter() { return this.parameter; }
	
	/* universals */
	@Override
	public String toString() {
		return this.category + "$" + this.operator + "$" + 
				this.execution + "$" + this.location + "$" + this.parameter;
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		else if(obj instanceof CirAnnotation) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}
	
}
