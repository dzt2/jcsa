package com.jcsa.jcmutest.mutant.cir2mutant.error;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * It describes the error directly caused by the mutation during testing
 * at the point of executing faulty statement, or called infection.
 * 
 * @author yukimula
 *
 */
public abstract class CirStateError {
	
	/* definitions */
	/** the type of the state error **/
	private CirErrorType type;
	/** where the error is expected to occur **/
	private CirStatement statement;
	/**
	 * create a state error at the point of statement with specified type
	 * @param type
	 * @param statement
	 * @throws Exception
	 */
	protected CirStateError(CirErrorType type, CirStatement statement) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else if(statement == null)
			throw new IllegalArgumentException("Invalid statement.");
		else {
			this.type = type;
			this.statement = statement;
		}
	}
	
	/* getters */
	/**
	 * @return the type of the state error
	 */
	public CirErrorType get_type() { return this.type; }
	/**
	 * @return where the error is expected to occur
	 */
	public CirStatement get_statement() { return this.statement; }
	/**
	 * @return unique code that the state error represents
	 * @throws Exception
	 */
	protected abstract String generate_code() throws Exception;
	@Override
	public String toString() {
		try {
			CirExecution execution = this.statement.get_tree().
					get_localizer().get_execution(statement);
			return this.type + "::" + execution + "(" + this.generate_code() + ")";
		} catch (Exception e) {
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
			return obj.toString().equals(this.toString());
		else
			return false;
	}
	
}
