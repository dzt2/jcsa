package com.jcsa.jcparse.parse.symbolic;

import java.util.Map;

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
	
	/**
	 * with both input and output state data
	 * @param in_state	the state to provide values used before evaluation
	 * @param ou_state	the state to preserve values defined after evaluation
	 * @return			the symbolic expression as main result of the expression
	 * @throws Exception
	 */
	public SymbolExpression io_evaluate(Map<SymbolExpression, SymbolExpression> in_state,
			Map<SymbolExpression, SymbolExpression> ou_state) throws Exception {
		return SymbolEvaluator.evaluate(this, in_state, ou_state);
	}
	
	/**
	 * with only input state data
	 * @param in_state	the state to provide values used before evaluation
	 * @return			the symbolic expression as main result of the expression
	 * @throws Exception
	 */
	public SymbolExpression i_evaluate(Map<SymbolExpression, SymbolExpression> in_state) throws Exception {
		return SymbolEvaluator.evaluate(this, in_state, null);
	}
	
	/**
	 * with only output state data
	 * @param ou_state	the state to preserve values defined after evaluation
	 * @return			the symbolic expression as main result of the expression
	 * @throws Exception
	 */
	public SymbolExpression o_evaluate(Map<SymbolExpression, SymbolExpression> ou_state) throws Exception {
		return SymbolEvaluator.evaluate(this, null, ou_state);
	}
	
	/**
	 * without input and output state data 
	 * @return	the symbolic expression as main result of the expression
	 * @throws Exception
	 */
	public SymbolExpression evaluate() throws Exception {
		return SymbolEvaluator.evaluate(this, null, null);
	}
	
}
