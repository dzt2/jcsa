package com.jcsa.jcmutest.mutant.cir2mutant.error;

import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * refer_error(statement, reference, expression) in which the value hold by
 * the reference during executing the statement be replaced by expression.
 * 
 * @author yukimula
 *
 */
public class CirReferenceError extends CirStateError {
	
	protected CirReferenceError(CirStatement statement,
			CirReferExpression reference,
			SymExpression muta_expression) throws Exception {
		super(CirErrorType.refr_error, statement);
		if(reference == null)
			throw new IllegalArgumentException("Invalid reference");
		else if(muta_expression == null)
			throw new IllegalArgumentException("Invalid muta_expression");
		else {
			this.reference = reference;
			this.muta_expression = muta_expression;
			this.orig_expression = SymFactory.parse(reference);
		}
		
	}
	
	/* definitions */
	/** the reference being mutated during executing the statement **/
	private CirReferExpression reference;
	/** it describes the value hold by the reference at the point **/
	private SymExpression orig_expression;
	/** the expression that replaces the value of the reference **/
	private SymExpression muta_expression;
	
	/* getters */
	/**
	 * @return the reference being mutated during executing the statement
	 */
	public CirReferExpression get_reference() { return this.reference; }
	/**
	 * @return the value hold by the reference at the point of the statement
	 */
	public SymExpression get_orig_expression() { return this.orig_expression; }
	/**
	 * @return the expression that replaces the value of the reference
	 */
	public SymExpression get_muta_expression() { return this.muta_expression; }
	
	@Override
	protected String generate_code() throws Exception {
		return this.reference.generate_code(true)
				+ ", " + this.muta_expression.generate_code();
	}
	
}
