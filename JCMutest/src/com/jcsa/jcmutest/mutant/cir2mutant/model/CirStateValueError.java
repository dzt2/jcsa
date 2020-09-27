package com.jcsa.jcmutest.mutant.cir2mutant.model;

import com.jcsa.jcmutest.mutant.cir2mutant.CirErrorType;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.sym.SymEvaluator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;
import com.jcsa.jcparse.test.state.CStateContexts;

public class CirStateValueError extends CirStateError {
	
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
			SymExpression muta_value) throws Exception {
		super(CirErrorType.stat_error, reference.statement_of());
		if(muta_value == null)
			throw new IllegalArgumentException("Invalid muta_value: null");
		else {
			this.orig_val = SymFactory.parse(reference);
			this.muta_val = muta_value;
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
	 * @return the original reference used by the point
	 */
	public SymExpression get_original_value() { return this.orig_val; }
	/**
	 * @return the mutation reference that will replace the original one
	 */
	public SymExpression get_mutation_value() { return this.muta_val; }

	@Override
	protected String generate_code() throws Exception {
		return this.orig_val.generate_code() + ", " + this.muta_val.generate_code();
	}

	
	@Override
	public CirStateError optimize(CStateContexts contexts) throws Exception {
		SymExpression original_value = 
				SymEvaluator.evaluate_on(this.orig_val, contexts);
		SymExpression mutation_value = 
				SymEvaluator.evaluate_on(this.muta_val, contexts);
		
		if(original_value.equals(mutation_value)) {
			return null;
		}
		else {
			return new CirStateValueError(this.get_reference(), mutation_value);
		}
	}
	
}
