package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * {val_error; execution; expression; value;}
 * 
 * @author yukimula
 *
 */
public class CirValueError extends CirAttribute {
	
	protected CirValueError(CirExecution execution, CirExpression expression, 
			SymbolExpression parameter) throws IllegalArgumentException {
		super(CirAttributeType.val_error, execution, expression, parameter);
	}
	
	/* specialized */
	/**
	 * @return original expression being replaced with mutation value
	 */
	public CirExpression get_orig_expression() { return (CirExpression) this.get_location(); }
	/**
	 * @return the value to replace the original expression being seeded
	 */
	public SymbolExpression get_muta_expression() { return this.get_parameter(); }
	
}
