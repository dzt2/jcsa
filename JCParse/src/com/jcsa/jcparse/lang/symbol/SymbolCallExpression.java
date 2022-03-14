package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * <code>SymbolCallExpression	(call_expr --> expression argument_list)</code>
 * 
 * @author yukimula
 *
 */
public class SymbolCallExpression extends SymbolSpecialExpression {
	
	/**
	 * It creates a call-expression with specified return type
	 * @param type
	 * @throws Exception
	 */
	private SymbolCallExpression(CType type) throws Exception {
		super(SymbolClass.call_expression, type);
	}
	
	/**
	 * @return the function to be called 
	 */
	public SymbolExpression get_function() { return (SymbolExpression) this.get_child(0); }
	
	/**
	 * @return the argument list of call
	 */
	public SymbolArgumentList get_argument_list() { return (SymbolArgumentList) this.get_child(1); }
	
	/**
	 * @param type			the return-type of calling expression
	 * @param function		the function to be called in
	 * @param argument_list	the argument_list of calling function
	 * @return				(type) function argument_list
	 * @throws Exception
	 */
	protected static SymbolCallExpression create(CType type, SymbolExpression 
			function, SymbolArgumentList argument_list) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(function == null) {
			throw new IllegalArgumentException("Invalid function.");
		}
		else if(argument_list == null) {
			throw new IllegalArgumentException("Invalid argument_list");
		}
		else {
			SymbolCallExpression expression = new SymbolCallExpression(type);
			expression.add_child(function); expression.add_child(argument_list);
			return expression;
		}
	}

	@Override
	protected SymbolNode new_one() throws Exception { 
		return new SymbolCallExpression(this.get_data_type()); 
	}

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		return this.get_function().generate_code(simplified) + 
				this.get_argument_list().generate_code(simplified);
	}

	@Override
	protected boolean is_refer_type() { return true; }

	@Override
	protected boolean is_side_affected() { return true; }
	
}
