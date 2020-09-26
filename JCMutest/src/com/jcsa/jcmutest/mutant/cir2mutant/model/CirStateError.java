package com.jcsa.jcmutest.mutant.cir2mutant.model;

import com.jcsa.jcmutest.mutant.cir2mutant.CirErrorType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * The state error in C-intermediate representation code defines the difference made
 * by mutation during the program being executed, denoted as<br>
 * <br>
 * 	
 * 	1. <code>trap_on(statement)</code>: an exception is thrown at the statement, and
 * 	   the program is forcedly terminated because the exception causes an unexpected 
 * 	   behavior of program under test.<br>
 * 	
 * 	2. <code>set_flow(statement, orig_flow, muta_flow)</code>: the original execution
 * 	   flow starting from the statement is mutated to another point from the original
 * 	   to another point through the mutation flow.<br>
 * 	
 * 	3. <code>set_expr(expression, orig_val, muta_val)</code>: the value hold by that
 * 	   specified expression is replaced as the muta_val, which shall be orig_val.<br>
 * 	
 * 	4. <code>set_refer(reference, orig_val, muta_val)</code>: the value hold by that
 * 	   reference is replaced with the muta_val, of which value shall be orig_val.<br>
 * 	
 * @author yukimula
 *
 */
public abstract class CirStateError {
	
	/* definitions */
	/** the type of the state error **/
	private CirErrorType type;
	/** statement where the error is expected to occur **/
	private CirExecution execution;
	/**
	 * create a state error w.r.t. the type and occurs in specified statement
	 * @param type
	 * @param statement
	 * @throws Exception
	 */
	protected CirStateError(CirErrorType type, CirStatement statement) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else if(statement == null)
			throw new IllegalArgumentException("Invalid statement");
		else {
			this.type = type;
			this.execution = statement.get_tree().
					get_localizer().get_execution(statement);
		}
	}
	
	/* getters */
	/**
	 * @return the type of the state error
	 */
	public CirErrorType get_type() { return this.type; }
	/**
	 * @return the statement where the error is expected to occur
	 */
	public CirExecution get_execution() { return this.execution; }
	/**
	 * @return the statement where the error is expected to occur
	 */
	public CirStatement get_statement() { return this.execution.get_statement(); }
	/**
	 * @return code that describes the content of the state error
	 * @throws Exception
	 */
	protected abstract String generate_code() throws Exception;
	@Override
	public String toString() {
		try {
			return this.type + "::" + this.execution + 
					"(" + this.generate_code() + ")";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		else if(obj instanceof CirStateError)
			return this.toString().equals(obj.toString());
		else
			return false;
	}
	
}
