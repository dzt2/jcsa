package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * 	|--	SymExpression
 * 	|--	|--	SymBasicExpression
 * 	|--	|--	|--	SymIdentifier		{name: String}
 * 	|--	|--	|--	SymConstant			{constant: CConstant}
 * 	|--	|--	|--	SymLiteral			{literal: String}
 * 	|--	|--	SymUnaryExpression		{operator: +, -, ~, !, &, *, assign}
 * 	|--	|--	SymBinaryExpression		{operator: -, /, %, <<, >>, <, <=, >, >=, ==, !=}
 * 	|--	|--	SymMultiExpression		{operator: +, *, &, |, ^, &&, ||}
 * 	|--	|--	SymInitializerList
 * 	|--	|--	SymFieldExpression		{operator: dot}
 * 	|--	|--	SymFunCallExpression
 * 	
 * 	@author yukimula
 *
 */
public abstract class SymExpression extends SymNode {
	
	private CType data_type;
	protected SymExpression(CType data_type) {
		this.data_type = data_type;
	}
	
	/**
	 * @return the data type of the expression
	 */
	public CType get_data_type() {
		return this.data_type;
	}
	
}
