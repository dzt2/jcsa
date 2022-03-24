package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * 	<code>
 * 	SymbolNode								[_class, source, parent, children]	<br>
 * 	|--	SymbolExpression					(typed evaluation unit) [data_type]	<br>
 * 	|--	|--	SymbolCompositeExpression		[comp_expr --> operator expression+]<br>
 * 	|--	|--	|--	SymbolArithExpression		(arith)	[add, sub, mul, div, mod]	<br>
 * 	|--	|--	|--	SymbolBitwsExpression		(bitws)	[and, ior, xor, lsh, rsh]	<br>
 * 	|--	|--	|--	SymbolLogicExpression		(logic)	[and, ior, eqv, neq, imp]	<br>
 * 	|--	|--	|--	SymbolRelationExpression	(relate)[grt, gre, smt, sme, neq]	<br>
 * 	|--	|--	|--	SymbolAssignExpression		(assign)[eas, ias]					<br>
 * 	</code>
 * 	
 *	@author yukimula
 *
 */
public abstract class SymbolBinaryExpression extends SymbolCompositeExpression {

	protected SymbolBinaryExpression(SymbolClass _class, CType data_type) throws IllegalArgumentException {
		super(_class, data_type);
	}
	
	/**
	 * @return the left-operand
	 */
	public SymbolExpression get_loperand() { return this.get_operand(0); }
	
	/**
	 * @return the right-operand 
	 */
	public SymbolExpression get_roperand() { return this.get_operand(1); }
	
	@Override
	protected String generate_code(boolean simplified) throws Exception {
		String operator = this.get_operator().generate_code(simplified);
		String loperand = this.get_loperand().generate_code(simplified);
		String roperand = this.get_roperand().generate_code(simplified);
		if(this.get_loperand().is_leaf()) loperand = "(" + loperand + ")";
		if(this.get_roperand().is_leaf()) roperand = "(" + roperand + ")";
		return loperand + " " + operator + " " + roperand;
	}
	
}
