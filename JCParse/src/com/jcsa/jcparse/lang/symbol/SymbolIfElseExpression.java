package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymbolIfElseExpression extends SymbolSpecialExpression {

	/**
	 * It creates the conditional expression
	 * @param data_type
	 * @throws IllegalArgumentException
	 */
	private SymbolIfElseExpression(CType data_type) throws IllegalArgumentException {
		super(SymbolClass.ifte_expression, data_type);
	}
	
	/**
	 * It creates the conditional expression
	 * @param type
	 * @param condition
	 * @param t_operand
	 * @param f_operand
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected static SymbolIfElseExpression create(CType type, SymbolExpression condition,
			SymbolExpression t_operand, SymbolExpression f_operand) throws IllegalArgumentException {
		if(type == null) {
			throw new IllegalArgumentException("Invalid data_type: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else if(t_operand == null) {
			throw new IllegalArgumentException("Invalid t_operand: null");
		}
		else if(f_operand == null) {
			throw new IllegalArgumentException("Invalid f_operand: null");
		}
		else {
			SymbolIfElseExpression expression = new SymbolIfElseExpression(type);
			expression.add_child(condition);
			expression.add_child(t_operand);
			expression.add_child(f_operand);
			return expression;
		}
	}
	
	/**
	 * @return the condition to decide which operand is selected to return
	 */ 
	public SymbolExpression get_condition() { return (SymbolExpression) this.get_child(0); } 
	
	/**
	 * @return the operand to be returned if the condition is evaluated true
	 */
	public SymbolExpression get_t_operand() { return (SymbolExpression) this.get_child(1); }
	
	/**
	 * @return the operand to be returned if the condition is evaluated false
	 */
	public SymbolExpression get_f_operand() { return (SymbolExpression) this.get_child(2); }

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolIfElseExpression(this.get_data_type());
	}

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		String condition = this.get_condition().generate_code(simplified);
		String t_operand = this.get_t_operand().generate_code(simplified);
		String f_operand = this.get_f_operand().generate_code(simplified);
		return condition + " ? " + t_operand + " : " + f_operand; 
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
}
