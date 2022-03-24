package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymbolCallExpression extends SymbolSpecialExpression {
	
	/**
	 * It creates an isolated call-expression with specified type
	 * @param data_type
	 * @throws IllegalArgumentException
	 */
	private SymbolCallExpression(CType data_type) throws IllegalArgumentException {
		super(SymbolClass.call_expression, data_type);
	}
	
	/**
	 * @param type
	 * @param function
	 * @param arguments
	 * @return	it creates a calling-expression with specified type and function and arguments
	 * @throws IllegalArgumentException
	 */
	protected static SymbolCallExpression create(CType type, SymbolExpression function, 
			SymbolArgumentList arguments) throws IllegalArgumentException {
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
			expression.add_child(function); expression.add_child(arguments);
			return expression;
		}
	}
	
	/**
	 * @return the function being called
	 */
	public SymbolExpression get_function() { return (SymbolExpression) this.get_child(0); }
	
	/**
	 * @return the list of arguments in the call-expression
	 */
	public SymbolArgumentList get_argument_list() { return (SymbolArgumentList) this.get_child(1); }

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolCallExpression(this.get_data_type());
	}

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		return this.get_function().generate_code(simplified) + this.get_argument_list().generate_code(simplified);
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return true; }
	
}
