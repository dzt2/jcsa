package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * <code>
 * 	SymNode
 * 	|--	SymExpression		{data_type}
 * 	|--	|--	SymBasicExpression		{identifier|constant|literal}
 * 	|--	|--	SymUnaryExpression		{+, -, ~, !, assign, *, &}
 * 	|--	|--	SymBinaryExpression		{+, -, *, /, %, ...}
 * 	|--	|--	SymFieldExpression		{.}
 * 	|--	|--	SymInitializerList		
 * 	|--	|--	SymFunCallExpression	
 * 	|--	|--	SymReference			{AstNode|CirNode}
 * 	</code>
 * @author yukimula
 *
 */
public abstract class SymExpression extends SymNode {
	
	/** data type of the value of the expression **/
	private CType data_type;
	
	/**
	 * an abstract isolated expression node
	 * @param data_type
	 * @param token
	 */
	protected SymExpression(CType data_type, Object token) {
		super(token);
		this.data_type = data_type;
	}
	
	/**
	 * @return data type of the value hold by the expression
	 */
	public CType get_data_type() { return this.data_type; }
	
}
