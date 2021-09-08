package com.jcsa.jcmutest.mutant.cir2mutant;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;

/**
 * It denotes a basic infection channel with two attributes, said constraint
 * (or precondition) and initial state error (post-condition).
 *
 * @author yukimula
 *
 */
public class CirMutation {

	/* definitions */
	private CirAttribute constraint;
	private CirAttribute init_error;
	protected CirMutation(CirAttribute constraint, CirAttribute
				init_error) throws IllegalArgumentException {
		if(constraint == null || !constraint.is_constraint()) {
			throw new IllegalArgumentException("Invalid constraint: " + constraint);
		}
		else if(init_error == null || !init_error.is_abst_error()) {
			throw new IllegalArgumentException("Invalid init_error: " + init_error);
		}
		else {
			this.constraint = constraint; this.init_error = init_error;
		}
	}

	/* getters */
	/**
	 * @return state infection condition to infect
	 */
	public CirAttribute get_constraint() { return this.constraint; }
	/**
	 * @return state error introduced in infection
	 */
	public CirAttribute get_init_error() { return this.init_error; }
	/**
	 * @return where the mutation is introduced and evaluated
	 */
	public CirExecution get_execution() { return this.init_error.get_execution(); }

	/* universal */
	@Override
	public String toString() { return this.constraint + " :: " + this.init_error; }
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		else if(obj instanceof CirMutation) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}

	/* classifiers */
	public static boolean is_boolean(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			CirNode parent = expression.get_parent();
			if(parent instanceof CirIfStatement || parent instanceof CirCaseStatement) {
				return true;
			}
			else {
				try {
					CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
					if(CTypeAnalyzer.is_boolean(type)) {
						return true;
					}
					else {
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}
	}
	public static boolean is_numeric(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			try {
				CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
				if(CTypeAnalyzer.is_number(type)) {
					return true;
				}
				else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	public static boolean is_integer(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			try {
				CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
				if(CTypeAnalyzer.is_integer(type)) {
					return true;
				}
				else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	public static boolean is_usigned(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			try {
				CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
				if(CTypeAnalyzer.is_integer(type)) {
					return CTypeAnalyzer.is_unsigned(type);
				}
				else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	public static boolean is_pointer(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			try {
				CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
				if(CTypeAnalyzer.is_pointer(type)) {
					return true;
				}
				else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	public static boolean is_automic(CirExpression expression) {
		return !is_boolean(expression) 
				&& !is_numeric(expression)
				&& !is_pointer(expression);
	}
	
}
