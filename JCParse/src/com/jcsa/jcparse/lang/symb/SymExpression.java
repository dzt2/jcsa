package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * 	Expression
 * 	|--	BasicExpression
 * 	|--	|--	Address			{identifier}
 * 	|--	|--	Constant		{constant}
 * 	|--	|--	StringLiteral	{literal}
 * 	|--	|--	DefaultValue	{?}
 * 	|--	UnaryExpression
 * 	|--	|--	{+, -, !, ~, &, *, cast<null>}
 * 	|--	BinaryExpression
 * 	|--	|--	{-, /, <<, >>, <, <=, >, >=, !=, ==}
 * 	|--	MultiExpression
 * 	|--	|--	{+, *, &, |, ^, &&, ||}
 * 	|--	FieldExpression
 * 	|--	SequenceExpression
 * 	|--	InvocateExpression
 * @author yukimula
 *
 */
public abstract class SymExpression extends SymNode {
	
	/* attribute */
	/** data type the value of this expression holds **/
	private CType data_type;
	
	/* constructor */
	/**
	 * create an abstract expression
	 * @param data_type
	 * @throws Exception
	 */
	protected SymExpression(CType data_type) throws IllegalArgumentException {
		super();
		if(data_type == null)
			throw new IllegalArgumentException("Invalid data_type: null");
		else this.data_type = data_type;
	}
	
	/* getters */
	/**
	 * get the data type of this expression node
	 * @return
	 */
	public CType get_data_type() { return this.data_type; }
	
}
