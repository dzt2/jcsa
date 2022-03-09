package com.jcsa.jcparse.parse.symbolic;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * 	<code>
 * 	|--	SymbolExpression					[data_type: CType]									<br>
 * 	|--	|--	SymbolBasicExpression			(basic expression as the leaf node)					<br>
 * 	|--	|--	|--	SymbolIdentifier			[name: String; scope: Object; identifier: String]	<br>
 * 	|--	|--	|--	SymbolConstant				[constant: CConstant]								<br>
 * 	|--	|--	|--	SymbolLiteral				[literal: String]									<br>
 * 	|--	|--	SymbolCompositeExpression		{comp_expr --> operator {expression}+)				<br>
 * 	|--	|--	|--	SymbolArithExpression		(neg, add, sub, mul, div, mod)						<br>
 * 	|--	|--	|--	SymbolBitwsExpression		(rsv, and, ior, xor, lsh, rsh)						<br>
 * 	|--	|--	|--	SymbolLogicExpression		(not, and, ior, xor, eqv, imp)						<br>
 * 	|--	|--	|--	SymbolRelationExpression	(gtn, geq, stn, ste, eqv, neq)						<br>
 * 	|--	|--	|--	SymbolAssignExpression		(ass, inc, dec)										<br>
 * 	|--	|--	SymbolAddressExpression			(addr_expr --> addr_of refer_expression)			<br>
 * 	|--	|--	SymbolDeferedExpression			(defr_expr --> derefer addr_expression)				<br>
 * 	|--	|--	SymbolCastExpression			(cast_expr --> {type_name} expression)				<br>
 * 	|--	|--	SymbolCallExpression			(call_expr --> expression argument_list)			<br>
 * 	|--	|--	SymbolConditionExpression		(cond_expr --> expr ? expr : expr)					<br>
 * 	|--	|--	SymbolInitializerList			(init_list --> {expr (, expr)*})					<br>
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
