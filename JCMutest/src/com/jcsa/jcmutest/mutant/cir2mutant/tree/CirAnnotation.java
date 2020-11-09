package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * The annotation instance is defined by three elements as:<br>
 * 	1. <code>execution</code>: the execution point of the statement where the annotation is generated.<br>
 * 	2. <code>type: CirAnnotateType</code>: the type of the annotation.<br>
 * 	3. <code>location: CirNode</code>: the node in C-intermediate representation described by the note.<br>
 * 	4. <code>parameter: Object</code>: the parameter to refine the definition of the annotation or null.<br>
 * 
 * @author yukimula
 *
 */
public class CirAnnotation {
	
	/* definitions */
	/** the execution of statement where the annotation occurs **/
	private CirExecution execution;
	/** the type of the annotation **/
	private CirAnnotateType type;
	/** location described by this annotation **/
	private CirNode location;
	/** the parameter to refine the annotation descriptions **/
	private Object parameter;
	
	/* constructor */
	/**
	 * create an annotation as type:location:parameter
	 * @param type
	 * @param location
	 * @param parameter either statement or expression (others are invalid!)
	 * @throws Exception
	 */
	protected CirAnnotation(CirAnnotateType type, CirNode location, Object parameter) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("Invalid type as null");
		else if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else if(location instanceof CirStatement) {
			switch(type) {
			case covr_stmt:
			case eval_stmt:
			case add_stmt:
			case del_stmt:	break;
			default: throw new IllegalArgumentException("Invalid type: " + type);
			}
			CirStatement statement = (CirStatement) location;
			this.execution = statement.get_tree().get_localizer().get_execution(statement);
			this.type = type;
			this.location = location;
			this.parameter = parameter;
		}
		else if(location instanceof CirExpression) {
			switch(type) {
			case covr_stmt:
			case eval_stmt:
			case add_stmt:
			case del_stmt:	throw new IllegalArgumentException("Invalid type: " + type);
			default: 		break;
			}
			CirStatement statement = ((CirExpression) location).statement_of();
			this.execution = statement.get_tree().get_localizer().get_execution(statement);
			this.type = type;
			this.location = location;
			this.parameter = parameter;
		}
		else 
			throw new IllegalArgumentException(location.generate_code(true));
	}
	
	/* getters */
	/**
	 * @return the execution of statement where the annotation occurs
	 */
	public CirExecution get_execution() { return this.execution; }
	/**
	 * @return the statement where the annotation occurs
	 */
	public CirStatement get_statement() { return this.execution.get_statement(); }
	/**
	 * @return the type of the annotation
	 */
	public CirAnnotateType get_type() { return this.type; }
	/**
	 * @return the location described by this annotation
	 */
	public CirNode get_location() { return this.location; }
	/**
	 * @return the parameter to refine the annotation descriptions
	 */
	public Object get_parameter() { return this.parameter; }
	@Override
	public String toString() {
		if(this.parameter == null)
			return this.type + ":" + this.location.get_node_id();
		else
			return this.type + ":" + this.location.get_node_id() + "(" + this.parameter + ")";
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		else if(obj instanceof CirAnnotation)
			return obj.toString().equals(this.toString());
		else
			return false;
	}
	
}
