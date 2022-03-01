package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * <code>
 * 	|--	SymbolExpression				[get_data_type();]												<br>
 * 	|--	|--	SymbolBasicExpression		(<i>leaf expression used in the computation</i>)				<br>
 * 	|--	|--	|--	SymbolIdentifier		[get_name(): String]											<br>
 * 	|--	|--	|--	SymbolConstant			[get_constant(): CConstant; ...]								<br>
 * 	|--	|--	|--	SymbolLiteral			[get_literal(): String]											<br>
 * 	|--	|--	SymbolUnaryExpression		{unary_expr |--> (+, -, ~, !, &, *, ++, --, p++, p--) expr}		<br>
 * 	|--	|--	SymbolBinaryExpression		{+,-,*,/,%, &,|,^,<<,>>, &&,||, <,<=,>,>=,==,!=, :=}			<br>
 * 	|--	|--	SymbolCastExpression		{cast_expr 	|--> (type) expression}								<br>
 * 	|--	|--	SymbolFieldExpression		{field_expr |--> expression.field}								<br>
 * 	|--	|--	SymbolCallExpression		{call_expr 	|--> expression argument_list}						<br>
 * 	|--	|--	SymbolInitializerLiist		{init_list	|--> {expr (, expr)*}}								<br>
 * 	|--	|--	SymbolConditionExpression	{cond_expr	|--> expr ? expr : expr}							<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class SymbolExpression extends SymbolNode {
	
	/** the data type of the value that this expression holds **/
	private CType type;
	
	/**
	 * @param _class
	 * @param type		the type of the value hold by this expression
	 * @throws Exception
	 */
	protected SymbolExpression(SymbolClass _class, CType type) throws Exception {
		super(_class);
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else {
			this.type = SymbolFactory.get_type(type);
		}
	}
	
	/**
	 * @return the type of the value hold by this expression
	 */
	public CType get_data_type() { return this.type; }

}
