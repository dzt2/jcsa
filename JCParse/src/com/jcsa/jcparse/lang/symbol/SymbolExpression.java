package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.parse.parser3.SymbolContext;
import com.jcsa.jcparse.parse.parser3.SymbolEvaluator;

/**
 * 	The typed evaluation unit as expression used in symbolic analysis.
 * 	<br>
 * 	<code>
 * 	SymbolNode								[_class, source, parent, children]	<br>
 * 	|--	SymbolExpression					(typed evaluation unit) [data_type]	<br>
 * 	|--	|--	SymbolBasicExpression			(basic expression without child)	<br>
 * 	|--	|--	|--	SymbolIdentifier			[name: String, scope: Object]		<br>
 * 	|--	|--	|--	SymbolConstant				[constant: CConstant]				<br>
 * 	|--	|--	|--	SymbolLiteral				[literal: String]					<br>
 * 	|--	|--	SymbolCompositeExpression		[comp_expr --> operator expression+]<br>
 * 	|--	|--	|--	SymbolUnaryExpression		(unary)	[neg, rsv, not, adr, ref]	<br>
 * 	|--	|--	|--	SymbolArithExpression		(arith)	[add, sub, mul, div, mod]	<br>
 * 	|--	|--	|--	SymbolBitwsExpression		(bitws)	[and, ior, xor, lsh, rsh]	<br>
 * 	|--	|--	|--	SymbolLogicExpression		(logic)	[and, ior, eqv, neq, imp]	<br>
 * 	|--	|--	|--	SymbolRelationExpression	(relate)[grt, gre, smt, sme, neq]	<br>
 * 	|--	|--	|--	SymbolAssignExpression		(assign)[eas, ias]					<br>
 * 	|--	|--	SymbolSpecialExpression												<br>
 * 	|--	|--	|--	SymbolCastExpression		(cast_expr --> {type_name} expr)	<br>
 * 	|--	|--	|--	SymbolInitializerList		(seq_list --> (expression+))		<br>
 * 	|--	|--	|--	SymbolCallExpression		(call_expr --> expr seq_list)		<br>
 * 	|--	|--	|--	SymbolIfElseExpression		(cond_expr --> expr ? expr : expr)	<br>
 * 	|--	|--	|--	SymbolFieldExpression		(field_expr --> expr.field)			<br>
 * 	|--	|--	|--	SymbolExpressionList		(expr_list --> (expr (, expr)+))	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class SymbolExpression extends SymbolNode {
	
	/** the data type of the value hold by this expression **/
	private	CType data_type;
	
	/**
	 * It creates the isolated expression with specified type
	 * @param _class		
	 * @param data_type
	 * @throws IllegalArgumentException
	 */
	protected SymbolExpression(SymbolClass _class, CType data_type) throws IllegalArgumentException {
		super(_class);
		this.data_type = SymbolFactory.get_type(data_type);
	}
	
	/**
	 * @return the data type of the value hold by this expression
	 */
	public CType get_data_type() { return this.data_type; }
	
	/**
	 * @param in_state		the state-context to provide the inputs
	 * @param ou_state		the state-context to preserve an output
	 * @return				the resulting expression from the input
	 * @throws Exception
	 */
	public SymbolExpression	evaluate(SymbolContext in_state, SymbolContext ou_state) throws Exception {
		return SymbolEvaluator.evaluate(this, in_state, ou_state);
	}
	
	
}
