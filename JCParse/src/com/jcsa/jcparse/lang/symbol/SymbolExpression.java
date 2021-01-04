package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;
import com.jcsa.jcparse.parse.symbol.SymbolStateContexts;

/**
 * <code>
 * 	SymbolExpression				{data_type: CType;}																	<br>
 * 	|--	SymbolBasicExpression		[<i>as leaf in SymbolExpression tree</i>]											<br>
 * 	|--	|--	SymbolIdentifier		{name: String}																		<br>
 * 	|--	|--	SymbolConstant			{constant: CConstant}																<br>
 * 	|--	|--	SymbolLiteral			{literal: String}																	<br>
 *	|--	SymbolBinaryExpression		{operator: +, -, *, /, %, &, |, ^, <<, >>, &&, ||}									<br>
 *	|--	SymbolUnaryExpression		{operator: -, ~, !, *, &, =} 														<br>
 *	|--	SymbolFieldExpression																							<br>
 *	|--	SymbolCallExpression																							<br>
 *	|--	SymbolInitializerList																							<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SymbolExpression extends SymbolNode {
	
	/** data type of the value hold by the expression **/
	private CType data_type;
	
	/**
	 * create an isolated expression node w.r.t. data type
	 * @param data_type
	 * @throws IllegalArgumentException
	 */
	protected SymbolExpression(CType data_type) throws IllegalArgumentException {
		if(data_type == null)
			this.data_type = CBasicTypeImpl.void_type;
		else {
			this.data_type = data_type;
		}
	}
	
	/**
	 * @return data type of the value hold by the expression
	 */
	public CType get_data_type() { return this.data_type; }
	
	/**
	 * @param contexts
	 * @return evaluate the value of this expression w.r.t. the given context
	 * @throws Exception
	 */
	public SymbolExpression evaluate(SymbolStateContexts contexts) throws Exception {
		return SymbolEvaluator.evaluate_on(this, contexts);
	}
	
}
