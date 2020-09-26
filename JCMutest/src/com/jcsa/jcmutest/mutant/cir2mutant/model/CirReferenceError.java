package com.jcsa.jcmutest.mutant.cir2mutant.model;

import com.jcsa.jcmutest.mutant.cir2mutant.CirErrorType;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * <code>set_refer(reference, orig_val, muta_val)</code>: the value hold by that
 * reference is replaced with the muta_val, of which value shall be orig_val.<br>
 * 
 * @author yukimula
 *
 */
public class CirReferenceError extends CirStateError {
	
	/* definitions */
	/** the original value hold by the expression in testing **/
	private SymExpression orig_val;
	/** the mutation value that will replace the original one **/
	private SymExpression muta_val;
	/**
	 * @param reference the reference of which state will be replaced
	 * @param muta_val mutation value that will replace the original state
	 * @throws Exception
	 */
	protected CirReferenceError(CirReferExpression reference, SymExpression muta_val) throws Exception {
		super(CirErrorType.refr_error, reference.statement_of());
		if(muta_val == null)
			throw new IllegalArgumentException("Invalid muta_value: null");
		else {
			this.orig_val = SymFactory.parse(reference);
			this.muta_val = muta_val;
		}
	}
	
	/* getters */
	/**
	 * @return the reference of which state will be mutated
	 */
	public CirReferExpression get_reference() {
		return (CirReferExpression) this.orig_val.get_cir_source();
	}
	/**
	 * @return the original value hold by the reference
	 */
	public SymExpression get_original_value() { return this.orig_val; }
	/**
	 * @return the mutation value that will replace the original one
	 */
	public SymExpression get_mutation_value() { return this.muta_val; }

	@Override
	protected String generate_code() throws Exception {
		return this.orig_val.generate_code() + ", " + this.muta_val.generate_code();
	}
	
}
