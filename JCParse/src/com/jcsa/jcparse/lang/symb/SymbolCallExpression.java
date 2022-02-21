package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymbolCallExpression extends SymbolExpression {

	private SymbolCallExpression(CType type) throws Exception {
		super(SymbolClass.call_expression, type);
	}
	
	/**
	 * @return the function being used in calling
	 */
	public SymbolExpression get_function() { return (SymbolExpression) this.get_child(0); }
	
	/**
	 * @return the list of arguments used to call
	 */
	public SymbolArgumentList get_argument_list() { return (SymbolArgumentList) this.get_child(1); }
	
	/**
	 * @param type
	 * @param function
	 * @param arguments
	 * @return
	 * @throws Exception
	 */
	protected static SymbolCallExpression create(CType type, SymbolExpression 
			function, SymbolArgumentList arguments) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(function == null) {
			throw new IllegalArgumentException("Invalid function: null");
		}
		else if(arguments == null) {
			throw new IllegalArgumentException("Invalid arguments: null");
		}
		else {
			SymbolCallExpression expression = new SymbolCallExpression(type);
			expression.add_child(function);
			expression.add_child(arguments);
			return expression;
		}
	}
	
	@Override
	protected SymbolNode construct_copy() throws Exception {
		return new SymbolCallExpression(this.get_data_type());
	}
	

	@Override
	protected String get_code(boolean simplified) throws Exception {
		return this.get_function().get_code(simplified) + this.get_argument_list().get_code(simplified);
	}
	

	@Override
	protected boolean is_refer_type() { return false; }
	

	@Override
	protected boolean is_side_affected() { return false; }
	
}
