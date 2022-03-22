package com.jcsa.jcparse.lang.symbol;

import java.util.Map;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * <code>SymbolCastExpression (cast_expr --> {type_name} expression)</code>
 * 
 * @author yukimula
 *
 */
public class SymbolCastExpression extends SymbolSpecialExpression {
	
	/**
	 * It creates a type-casting expression
	 * @param type
	 * @throws Exception
	 */
	private SymbolCastExpression(CType type) throws Exception {
		super(SymbolClass.cast_expression, type);
	}
	
	/**
	 * @return the casted type
	 */
	public SymbolType get_cast_type() { return (SymbolType) this.get_child(0); }
	
	/**
	 * @return the expression being casted
	 */
	public SymbolExpression get_operand() { return (SymbolExpression) this.get_child(1); }
	
	/**
	 * It creates a casting: (type) operand
	 * @param type		the casted type
	 * @param operand	the expression to be casted
	 * @return			(type) operand
	 * @throws Exception
	 */
	protected static SymbolCastExpression create(SymbolType type, SymbolExpression operand) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			SymbolCastExpression expression = new SymbolCastExpression(type.get_type());
			expression.add_child(type); expression.add_child(operand); return expression;
		}
	}

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolCastExpression(this.get_data_type());
	}

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		String operand = this.get_operand().generate_code(simplified);
		if(!this.get_operand().is_leaf()) { operand = "(" + operand + ")"; }
		return "(" + this.get_cast_type().generate_code(simplified) + ") " + operand;
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	@Override
	protected SymbolExpression symb_replace(Map<SymbolExpression, SymbolExpression> name_value_map) throws Exception {
		SymbolType cast_type = this.get_cast_type();
		SymbolExpression operand = this.get_operand();
		operand = operand.symb_replace(name_value_map);
		return create(cast_type, operand);
	}
	
}
