package com.jcsa.jcmutest.mutant.cir2mutant.model;

import com.jcsa.jcmutest.mutant.cir2mutant.CirErrorType;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.sym.SymEvaluator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.test.state.CStateContexts;


/**
 * <code>set_refer(reference, orig_val, muta_val)</code>: the reference used
 * in specified statement is replaced as another reference such as variables
 * being replaced during reference mutation.<br>
 * 
 * @author yukimula
 *
 */
public class CirReferenceError extends CirStateError {
	
	/* definitions */
	/** the reference of which referred will be mutated **/
	private CirReferExpression reference;
	/** the original value hold by the expression in testing **/
	private SymExpression orig_val;
	/** the mutation value that will replace the original one **/
	private SymExpression muta_val;
	/**
	 * @param reference the reference of which state will be replaced
	 * @param muta_val mutation variable that will replace the original one
	 * @throws Exception
	 */
	protected CirReferenceError(CirReferExpression reference, 
			SymExpression orig_val, SymExpression muta_val) throws Exception {
		super(CirErrorType.refr_error, reference.statement_of());
		if(muta_val == null)
			throw new IllegalArgumentException("Invalid muta_value: null");
		else {
			this.reference = reference;
			this.orig_val = orig_val;
			this.muta_val = muta_val;
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
		return this.reference.generate_code(false) + ", " + 
				this.orig_val.generate_code() + ", " + 
				this.muta_val.generate_code();
	}
	@Override
	public boolean is_valid(CStateContexts contexts) throws Exception {
		SymExpression orig_value = SymEvaluator.evaluate_on(this.orig_val, contexts);
		SymExpression muta_value = SymEvaluator.evaluate_on(this.muta_val, contexts);
		return !orig_value.equals(muta_value);
	}
	
}
