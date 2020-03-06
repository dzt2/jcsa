package com.jcsa.jcparse.lopt.analysis.flow;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * The control dependence relation is defined as<br>
 * <code>(source, target, condition, predicate_value)</code><br>
 * in which source control depends on target, in which the target will be
 * executed when the condition as predicate value.
 * @author yukimula
 *
 */
public class CDependenceControlEdge extends CDependenceEdge {
	
	/* properties */
	/** the control condition **/
	private CirExpression condition;
	/** the value required for source being executed **/
	private boolean predicate_value;
	
	/* constructor */
	/**
	 * create a control dependence from source to target, in which condition refers to
	 * the expression that determines whether the source is executed if the expression
	 * equals with the predicate value as provided.
	 * @param source
	 * @param target
	 * @param condition
	 * @param predicate_value
	 * @throws Exception
	 */
	protected CDependenceControlEdge(CDependenceNode source, 
			CDependenceNode target, boolean predicate_value) throws Exception {
		super(source, target);
		CirStatement statement = target.get_statement();
		if(statement instanceof CirIfStatement) {
			this.condition = ((CirIfStatement) statement).get_condition();
		}
		else if(statement instanceof CirCaseStatement) {
			this.condition = ((CirCaseStatement) statement).get_condition();
		}
		else throw new IllegalArgumentException("Invalid statement: " + statement);
		this.predicate_value = predicate_value; 
	}
	
	/* getters */
	/**
	 * get the expression refers to the condition of the control dependence
	 * @return
	 */
	public CirExpression get_condition() { return this.condition; }
	/**
	 * get the predicate value that the condition is required to be
	 * @return
	 */
	public boolean get_predicate_value() { return predicate_value; }
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		else if(obj instanceof CDependenceControlEdge) {
			CDependenceControlEdge edge = (CDependenceControlEdge) obj;
			return (this.get_source() == edge.get_source())
					&& (this.get_target() == edge.get_target())
					&& (this.predicate_value == edge.predicate_value);
		}
		else return false;
	}
	
}
