package com.jcsa.jcmutest.mutant.cir2mutant.cerr;

import com.jcsa.jcmutest.mutant.cir2mutant.CirErrorType;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymEvaluator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.test.state.CStateContexts;


public class CirStateValueError extends CirStateError {
	
	/** the reference of which referred will be mutated **/
	private CirReferExpression reference;
	/** the original value hold by the expression in testing **/
	private SymExpression orig_val;
	/** the mutation value that will replace the original one **/
	private SymExpression muta_val;
	/**
	 * @param reference the reference of which state will be replaced
	 * @param muta_val mutation value that will replace the original state
	 * @throws Exception
	 */
	protected CirStateValueError(CirReferExpression reference, 
			SymExpression orig_val, SymExpression muta_value) throws Exception {
		super(CirErrorType.stat_error, reference.statement_of());
		if(muta_value == null)
			throw new IllegalArgumentException("Invalid muta_value: null");
		else {
			this.reference = reference;
			this.orig_val = orig_val;
			this.muta_val = muta_value;
		}
	}
	
	/* getters */
	/**
	 * @return the reference of which state will be mutated
	 */
	public CirReferExpression get_reference() { return this.reference; }
	/**
	 * @return the original reference used by the point
	 */
	public SymExpression get_original_value() { return this.orig_val; }
	/**
	 * @return the mutation reference that will replace the original one
	 */
	public SymExpression get_mutation_value() { return this.muta_val; }

	@Override
	protected String generate_code() throws Exception {
		return this.reference.generate_code(false) + 
				", " + this.orig_val.generate_code() + 
				", " + this.muta_val.generate_code();
	}

	@Override
	public Boolean validate(CStateContexts contexts) throws Exception {
		SymExpression orig_value = SymEvaluator.evaluate_on(orig_val, contexts);
		SymExpression muta_value = SymEvaluator.evaluate_on(muta_val, contexts);
		if(orig_value.equals(muta_value))
			return Boolean.FALSE;
		else if(orig_value instanceof SymConstant
				|| muta_value instanceof SymConstant) {
			return Boolean.TRUE;
		}
		else {
			return Boolean.TRUE;
		}
	}
	
}
