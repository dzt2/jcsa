package com.jcsa.jcparse.parse.symbolic;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * <code>
 * 	SymbolUnaryExpression		(unary)	[neg, rsv, not, adr, ref]	<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public class SymbolUnaryExpression extends SymbolCompositeExpression {

	private SymbolUnaryExpression(CType type) throws Exception {
		super(SymbolClass.unary_expression, type);
	}
	
	/**
	 * @return the unique operand
	 */
	public SymbolExpression get_operand() { return this.get_operand(0); }

	@Override
	protected SymbolNode new_one() throws Exception { 
		return new SymbolUnaryExpression(this.get_data_type()); 
	}

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		String operand = this.get_operand().generate_code(simplified);
		if(!this.get_operand().is_leaf()) {
			operand = "(" + operand + ")";
		}
		return this.get_operator().generate_code(simplified) + operand;
	}

	@Override
	protected boolean is_refer_type() {
		switch(this.get_coperator()) {
		case dereference:	return true;
		default:		return false;
		}
	}

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param type
	 * @param operator	[neg, rsv, not, adr, ref]
	 * @param operand	
	 * @return (type) (operator operand)
	 * @throws Exception
	 */
	protected static SymbolUnaryExpression create(CType type, COperator operator, SymbolExpression operand) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			SymbolUnaryExpression expression;
			expression = new SymbolUnaryExpression(type);
			switch(operator) {
			case address_of:
			{
				if(!operand.is_reference()) {
					throw new IllegalArgumentException("Not a reference: " + operand);
				}
				break;
			}
			case negative:
			case bit_not:
			case logic_not:
			case dereference:
			{
				break;
			}
			default:	throw new IllegalArgumentException(operator.toString());
			}
			expression.add_child(SymbolOperator.create(operator));
			expression.add_child(operand); return expression;
		}
	}
	
}
