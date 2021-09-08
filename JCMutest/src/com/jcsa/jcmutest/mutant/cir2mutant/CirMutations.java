package com.jcsa.jcmutest.mutant.cir2mutant;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParsers;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * It provides the static interfaces for creating and analyzing cir-based mutation.
 * 
 * @author yukimula
 *
 */
public class CirMutations {
	
	/* creator */
	/**
	 * @param constraint
	 * @param init_error
	 * @return the instance of cir-based mutation from pre-condition to post-condition (state difference).
	 * @throws Exception
	 */
	public static CirMutation new_mutation(CirAttribute constraint, CirAttribute init_error) throws Exception {
		return new CirMutation(constraint, init_error);
	}
	/**
	 * @param mutant
	 * @return create the set of cir-based mutations from mutant or null if generation failed.
	 * @throws Exception
	 */
	public static Iterable<CirMutation> parse(Mutant mutant) {
		if(mutant == null) {
			return null;
		}
		else {
			try {
				return CirMutationParsers.parse(mutant.get_space().
						get_cir_tree(), mutant.get_mutation());
			}
			catch(Exception ex) {
				ex.printStackTrace(System.err);
				return null;
			}
		}
	}
	
	/* type classifier */
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
			return is_numeric(expression.get_data_type());
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
	
}
