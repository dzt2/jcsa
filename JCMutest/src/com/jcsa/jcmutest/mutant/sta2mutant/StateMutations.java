package com.jcsa.jcmutest.mutant.sta2mutant;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * It implements the construction, evaluation, optimization and 
 * basic utilities used for state mutation computation.
 * 
 * @author yukimula
 *
 */
public final class StateMutations {
	
	/* type classifier */
	/**
	 * @param data_type
	 * @return
	 */
	public static boolean is_void(CType data_type) {
		try {
			return CTypeAnalyzer.is_void(data_type);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is a boolean
	 */
	public static boolean is_boolean(CType data_type) {
		try {
			return CTypeAnalyzer.is_boolean(data_type);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is a unsigned
	 */
	public static boolean is_usigned(CType data_type) {
		try {
			if(CTypeAnalyzer.is_integer(data_type)) {
				return CTypeAnalyzer.is_unsigned(data_type);
			}
			else {
				return false;
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is a integer
	 */
	public static boolean is_integer(CType data_type) {
		try {
			return CTypeAnalyzer.is_integer(data_type);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is real
	 */
	public static boolean is_doubles(CType data_type) {
		try {
			return CTypeAnalyzer.is_real(data_type);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is integer or real
	 */
	public static boolean is_numeric(CType data_type) {
		try {
			return CTypeAnalyzer.is_integer(data_type) || CTypeAnalyzer.is_real(data_type);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is a address pointer
	 */
	public static boolean is_address(CType data_type) {
		try {
			return CTypeAnalyzer.is_pointer(data_type);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	/* location classifier */
	/**
	 * @param expression
	 * @return
	 */
	public static boolean is_void(CirExpression expression) {
		return CirMutations.is_void(expression.get_data_type());
	}
	/**
	 * @param location
	 * @return whether the expression is a boolean
	 */
	public static boolean is_boolean(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else if(is_boolean(expression.get_data_type())) {
			return true;
		}
		else if(expression.get_parent() instanceof CirStatement) {
			CirStatement statement = expression.statement_of();
			return statement instanceof CirIfStatement || statement instanceof CirCaseStatement;
		}
		else {
			return false;
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a unsigned integer
	 */
	public static boolean is_usigned(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_usigned(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is an integer
	 */
	public static boolean is_integer(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_integer(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a real
	 */
	public static boolean is_doubles(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_doubles(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a real or integer
	 */
	public static boolean is_numeric(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_integer(expression) || is_doubles(expression);
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a pointer
	 */
	public static boolean is_address(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_address(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a reference defined in left-side of assignment
	 */
	public static boolean is_assigned(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else if(expression.get_parent() instanceof CirAssignStatement) {
			CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
			return statement.get_lvalue() == expression;
		}
		else {
			return false;
		}
	}
	
	
	
	
	
}
