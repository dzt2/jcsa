package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * 	The composite expression is driven by an operator
 * 	<code>
 * 	SymbolNode								[_class, source, parent, children]	<br>
 * 	|--	SymbolExpression					(typed evaluation unit) [data_type]	<br>
 * 	|--	|--	SymbolCompositeExpression		[comp_expr --> operator expression+]<br>
 * 	|--	|--	|--	SymbolUnaryExpression		(unary)	[neg, rsv, not, adr, ref]	<br>
 * 	|--	|--	|--	SymbolArithExpression		(arith)	[add, sub, mul, div, mod]	<br>
 * 	|--	|--	|--	SymbolBitwsExpression		(bitws)	[and, ior, xor, lsh, rsh]	<br>
 * 	|--	|--	|--	SymbolLogicExpression		(logic)	[and, ior, eqv, neq, imp]	<br>
 * 	|--	|--	|--	SymbolRelationExpression	(relate)[grt, gre, smt, sme, neq]	<br>
 * 	|--	|--	|--	SymbolAssignExpression		(assign)[eas, ias]					<br>
 * 	</code>
 * 	@author yukimula
 *
 */
public abstract class SymbolCompositeExpression extends SymbolExpression {

	protected SymbolCompositeExpression(SymbolClass _class, CType data_type) throws IllegalArgumentException {
		super(_class, data_type);
	}
	
	/**
	 * @return the operator node of the composite expression
	 */
	public SymbolOperator get_operator() { return (SymbolOperator) this.get_child(0); }
	
	/**
	 * @return {neg, rsv, not, adr, der, add, sub, mul, div, mod, and, ior, xor, lsh,
	 * 			rsh, and, ior, imp(pos), grt, gre, smt, sme, eqv, neq, ass, ias(inc)}
	 */
	public COperator get_coperator() { return this.get_operator().get_operator(); }
	
	/**
	 * @return the number of operands used in the composite expression
	 */
	public int number_of_operands() { return this.number_of_children() - 1; }
	
	/**
	 * @param k
	 * @return the kth operand used in this composite expression
	 * @throws IndexOutOfBoundsException
	 */
	public SymbolExpression get_operand(int k) throws IndexOutOfBoundsException {
		return (SymbolExpression) this.get_child(k + 1);
	}
	
}
