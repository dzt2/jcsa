package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	+--------------------------------------------------------------------------+<br>
 * 	|--	<i>SedStatement</i>								{source: CirStatement}	<br>
 * 	|--	|--	SedAssignStatement													<br>
 * 	|--	|--	SedGotoStatement													<br>
 * 	|--	|--	SedIfStatement														<br>
 * 	|--	|--	SedCallStatement													<br>
 * 	|--	|--	SedLabelStatement													<br>
 * 	+--------------------------------------------------------------------------+<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class SedStatement extends SedNode {
	
	/* definitions */
	/** the executional node to which the statement points **/
	private CirExecution execution;
	protected SedStatement(CirNode source) {
		super(source);
		if(source instanceof CirStatement) {
			this.execution = source.get_tree().get_function_call_graph().get_function(
						source).get_flow_graph().get_execution((CirStatement) source);
		}
		else {
			this.execution = null;
		}
	}
	/**
	 * @return the executional node to which the statement points
	 */
	public CirExecution get_execution() { return this.execution; }
	/**
	 * @return the cir-statement to which this node describes
	 */
	public CirStatement get_cir_statement() { 
		if(this.execution == null)
			return null;
		else
			return this.execution.get_statement();
	}
	@Override
	public String generate_code() throws Exception {
		String code = "{" + this.generate_content() + "}";
		if(this.get_execution() != null) {
			code = this.get_execution().toString() + "::" + code;
		}
		return code;
	}
	/**
	 * @return the content of code that explains the semantics of the statement
	 * @throws Exception
	 */
	protected abstract String generate_content() throws Exception;
	
}
