package com.jcsa.jcparse.parse.symbolic;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * <code>
 * 	|--	|--	SymbolUnaryExpression			[neg, rsv, not, inc, dec, addr, defr]				<br>
 * 	|--	|--	SymbolBinaryExpression			[add, sub, mul, div, mod, and, ior, xor, lsh, rsh]	<br>
 * 											[and, ior, imp, eqv, neq, grt, gre, smt, sme, ass]	<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class SymbolCompositeExpression extends SymbolExpression {

	protected SymbolCompositeExpression(SymbolClass _class, CType type) throws Exception {
		super(_class, type);
	}
	
	/**
	 * @return
	 */
	public SymbolOperator get_operator() { return (SymbolOperator) this.get_child(0); }
	
	/**
	 * @return	[neg, rsv, not, inc, dec, adr, ref]
	 * 			[add, sub, mul, div, mod, and, ior, xor, lsh, rsh]
	 * 			[and, ior, imp, eqv, neq, grt, gre, smt, sme, ass]
	 */
	public COperator get_coperator() { return this.get_operator().get_operator(); }
	
	/**
	 * @return the number of operands under composite expression
	 */
	public int number_of_operands() { return this.number_of_children() - 1; }
	
	/**
	 * @param k
	 * @return the kth operand in the expression
	 * @throws IndexOutOfBoundsException
	 */
	public SymbolExpression get_operand(int k) throws IndexOutOfBoundsException {
		return (SymbolExpression) this.get_child(k + 1);
	}
	
}
