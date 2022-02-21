package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymbolConditionExpression extends SymbolExpression {

	private SymbolConditionExpression(CType type) throws Exception {
		super(SymbolClass.condition_expression, type);
	}
	
	/**
	 * @return the condition of the expression
	 */
	public SymbolExpression get_condition() { return (SymbolExpression) this.get_child(0); }
	
	/**
	 * @return the operand taken when condition is True
	 */
	public SymbolExpression get_toperand() { return (SymbolExpression) this.get_child(1); }
	
	/**
	 * @return the operand taken when condition is False
	 */
	public SymbolExpression get_foperand() { return (SymbolExpression) this.get_child(2); }
	
	/**
	 * @param type
	 * @param condition
	 * @param toperand
	 * @param foperand
	 * @return
	 * @throws Exception
	 */
	protected static SymbolConditionExpression create(CType type, SymbolExpression condition,
			SymbolExpression toperand, SymbolExpression foperand) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else if(toperand == null) {
			throw new IllegalArgumentException("Invalid toperand: null");
		}
		else if(foperand == null) {
			throw new IllegalArgumentException("Invalid foperand: null");
		}
		else {
			SymbolConditionExpression expression = new SymbolConditionExpression(type);
			expression.add_child(condition);
			expression.add_child(toperand);
			expression.add_child(foperand);
			return expression;
		}
	}

	
	@Override
	protected SymbolNode construct_copy() throws Exception {
		return new SymbolConditionExpression(this.get_data_type());
	}
	

	@Override
	protected String get_code(boolean simplified) throws Exception {
		String condition = this.get_condition().get_code(simplified);
		String t_operand = this.get_toperand().get_code(simplified);
		String f_operand = this.get_foperand().get_code(simplified);
		return condition + " ? " + t_operand + " : " + f_operand;
	}
	

	@Override
	protected boolean is_refer_type() { return false; }
	

	@Override
	protected boolean is_side_affected() { return false; }
	
}
