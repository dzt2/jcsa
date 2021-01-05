package com.jcsa.jcmutest.mutant.cir2mutant.cerr;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.parse.symbol.SymbolStateContexts;

/**
 * Symbolic instance in C-intermediate representation code.
 * 
 * @author yukimula
 *
 */
public abstract class SymInstance {
	
	/* definitions */
	/** type of the symbolic instance being evaluated **/
	private SymInstanceType type;
	/** statement where the instance is injected **/
	private CirExecution execution;
	
	/**
	 * @param type
	 * @param execution
	 * @throws IllegalArgumentException
	 */
	protected SymInstance(SymInstanceType type, CirExecution execution) throws IllegalArgumentException {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else if(type == null)
			throw new IllegalArgumentException("Invalid type as null");
		else {
			this.type = type;
			this.execution = execution;
		}
	}
	
	/* getters */
	/**
	 * @return type of the symbolic instance being evaluated
	 */
	public SymInstanceType get_type() { return this.type; }
	/**
	 * @return statement where the instance is injected
	 */ 
	public CirExecution get_execution() { return this.execution; }
	/**
	 * @return statement in CIR code where the instance is injected
	 */
	public CirStatement get_statement() { return this.execution.get_statement(); }
	@Override
	public String toString() {
		try {
			return this.generate_code();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		else if(obj instanceof SymInstance)
			return this.toString().equals(obj.toString());
		else
			return false;
	}
	
	/* implementation */
	/**
	 * @return generate the code that describes the instance in unique way
	 * @throws Exception
	 */
	protected abstract String generate_code() throws Exception;
	/**
	 * to validate whether the instance hold at the point w.r.t. the contexts as given
	 * when the execution point is reached.
	 * @param contexts
	 * @return	true if it actually holds
	 * 			false if it does not hold
	 * 			null if we don't know whether it holds
	 * @throws Exception
	 */
	public abstract Boolean validate(SymbolStateContexts contexts) throws Exception;
	
}
