package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * 	BasicExpression
 * 	|--	Address			{identifier}
 * 	|--	Constant		{constant}
 * 	|--	StringLiteral	{literal}
 * 	|--	DefaultValue	{?}
 * @author yukimula
 *
 */
public abstract class SymBasicExpression extends SymExpression {
	
	/**
	 * value that the expression represents
	 */
	protected Object value;
	
	/**
	 * create a basic expression w.r.t. some value
	 * @param data_type
	 * @param value
	 * @throws Exception
	 */
	protected SymBasicExpression(CType data_type, Object value) throws IllegalArgumentException {
		super(data_type);
		this.value = value;
	}
	
	/**
	 * get the value that the expression represents
	 * @return
	 */
	public Object get_value() { return this.value; }

}
