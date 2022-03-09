package com.jcsa.jcparse.parse.symbolic;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * 	<code>
 * 	|--	SymbolExpression					[data_type: CType]					<br>
 * 	|--	|--	SymbolBasicExpression			(basic expression as the leaf node)	<br>
 * 	|--	|--	|--	SymbolIdentifier			[name: String; scope: Object;]		<br>
 * 	|--	|--	|--	SymbolConstant				[constant: CConstant]				<br>
 * 	|--	|--	|--	SymbolLiteral				[literal: String]					<br>
 * 	|--	|--	SymbolCompositeExpression		(comp_expr --> operator expression)	<br>
 * 	|--	|--	|--	SymbolUnaryExpression		(unary)	[neg, rsv, not, adr, ref]	<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(arith)	[add, sub, mul, div, mod]	<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(bitws)	[and, ior, xor, lsh, rsh]	<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(logic)	[and, ior, eqv, neq, imp]	<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(relate)[grt, gre, smt, sme, neq...]<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(assign)[ass, pss]					<br>
 * 	|--	|--	SymbolSpecialExpression												<br>
 * 	|--	|--	|--	SymbolCastExpression		(cast_expr --> {type_name} expr)	<br>
 * 	|--	|--	|--	SymbolCallExpression		(call_expr --> expr arg_list)		<br>
 * 	|--	|--	|--	SymbolConditionExpression	(cond_expr --> expr ? expr : expr)	<br>
 * 	|--	|--	|--	SymbolInitializerList		(init_list --> {expr (, expr)*})	<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class SymbolExpression extends SymbolNode {
	
	/** data type of the expression value **/
	private	CType data_type;
	
	/**
	 * General constructor for symbolic expression with specified data type
	 * @param _class
	 * @param type
	 * @throws Exception
	 */
	protected SymbolExpression(SymbolClass _class, CType type) throws Exception {
		super(_class);
		this.data_type = SymbolFactory.get_type(type);
	}
	
	/**
	 * @return the data type of expression value
	 */
	public CType get_data_type() { return this.data_type; }
	
}
