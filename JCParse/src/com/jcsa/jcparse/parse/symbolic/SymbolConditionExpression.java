package com.jcsa.jcparse.parse.symbolic;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * <code>cond_expr --> expression ? expression : expression</code>
 * 
 * @author yukimula
 *
 */
public class SymbolConditionExpression extends SymbolExpression {
	
	/**
	 * It creates a symbolic node for conditional expression
	 * @param type	the data type of expression output value
	 * @throws Exception
	 */
	private SymbolConditionExpression(CType type) throws Exception {
		super(SymbolClass.cond_expression, type);
	}
	
	/**
	 * @return the condition to determine of which operands being evaluated
	 */
	public SymbolExpression get_condition() { return (SymbolExpression) this.get_child(0); }
	
	/**
	 * @return the operand is evaluated iff. the condition is evaluated as true
	 */
	public SymbolExpression get_t_operand() { return (SymbolExpression) this.get_child(1); }
	
	/**
	 * @return the operand is evaluated iff. the condition is evaluated as false
	 */
	public SymbolExpression get_f_operand() { return (SymbolExpression) this.get_child(2); }

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolConditionExpression(this.get_data_type());
	}

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		return this.get_condition().generate_code(simplified) + 
				" ? " + this.get_t_operand().generate_code(simplified) + 
				" : " + this.get_f_operand().generate_code(simplified);
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param type		the data type of the expression output value
	 * @param condition	the condition to determine of which operands being evaluated
	 * @param t_operand	the operand is evaluated iff. the condition is evaluated as true
	 * @param f_operand	the operand is evaluated iff. the condition is evaluated as false
	 * @return			condition ? t_operand : f_operand
	 * @throws Exception
	 */
	protected static SymbolConditionExpression create(CType type, SymbolExpression condition,
			SymbolExpression t_operand, SymbolExpression f_operand) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
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
			SymbolConditionExpression expression;
			expression = new SymbolConditionExpression(type);
			expression.add_child(condition);
			expression.add_child(t_operand);
			expression.add_child(f_operand); return expression;
		}
	}
	
}
