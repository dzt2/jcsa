package com.jcsa.jcmutest.mutant.sed2mutant.util;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.SedAddExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.SedAddStatementError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.SedDelStatementError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.SedInsExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.SedMutStatementError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.SedSetExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.SedStateError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedDefaultValue;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It provides interfaces to generate state errors in program.
 * 
 * @author yukimula
 *
 */
public class SedStateErrors {
	
	/* definitions */
	/** the cir-code in which errors are seeded **/
	private CirTree cir_tree;
	/** mapping from key to unique state errors **/
	private Map<String, SedStateError> errors;
	/**
	 * create a state error library such that all the errors in the library
	 * are of unique
	 * @param cir_tree
	 * @throws Exception
	 */
	public SedStateErrors(CirTree cir_tree) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else {
			this.cir_tree = cir_tree;
			this.errors = new HashMap<String, SedStateError>();
		}
	}
	
	/* getters */
	/**
	 * @return the cir-code in which errors are seeded
	 */
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * @param error
	 * @return the unique state error w.r.t. the error in the space
	 * @throws Exception
	 */
	private SedStateError get_error(SedStateError error) throws Exception {
		String key = error.generate_code();
		if(!this.errors.containsKey(key)) {
			this.errors.put(key, error);
		}
		return this.errors.get(key);
	}
	/**
	 * @param orig_statement
	 * @return seed#stmt::add_stmt(stmt)
	 * @throws Exception
	 */
	public SedStateError add_stmt(CirStatement orig_statement) throws Exception {
		return this.get_error(new SedAddStatementError(orig_statement, orig_statement));
	}
	/**
	 * @param orig_statement
	 * @return seed#stmt::del_stmt(stmt)
	 * @throws Exception
	 */
	public SedStateError del_stmt(CirStatement orig_statement) throws Exception {
		return this.get_error(new SedDelStatementError(orig_statement, orig_statement));
	}
	/**
	 * @param orig_statement
	 * @param muta_statement
	 * @return seed#stmt::mut_stmt(stmt, stmt)
	 * @throws Exception
	 */
	public SedStateError mut_stmt(CirStatement 
			orig_statement, CirStatement muta_statement) throws Exception {
		return this.get_error(new SedMutStatementError(
				orig_statement, orig_statement, muta_statement));
	}
	/* ins_expr(expr, oprt) */
	/**
	 * @param statement
	 * @param orig_expression
	 * @return seed#stmt::ins_expr(oprt, logic_not)
	 * @throws Exception
	 */
	public SedStateError not_expr(CirStatement statement, 
			CirExpression orig_expression) throws Exception {
		return this.get_error(new SedInsExpressionError(statement,
				(SedExpression) SedParser.parse(orig_expression), 
				COperator.logic_not));
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @return seed#stmt::ins_expr(oprt, negative)
	 * @throws Exception
	 */
	public SedStateError neg_expr(CirStatement statement, 
			CirExpression orig_expression) throws Exception {
		return this.get_error(new SedInsExpressionError(statement,
				(SedExpression) SedParser.parse(orig_expression), 
				COperator.negative));
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @return seed#stmt::ins_expr(oprt, bit_not)
	 * @throws Exception
	 */
	public SedStateError rsv_expr(CirStatement statement, 
			CirExpression orig_expression) throws Exception {
		return this.get_error(new SedInsExpressionError(statement,
				(SedExpression) SedParser.parse(orig_expression), 
				COperator.bit_not));
	}
	/* set_expr(expr::bool, bool|any) */
	/**
	 * @param statement
	 * @param orig_expression
	 * @param value
	 * @return set_bool(expr, true|false)
	 * @throws Exception
	 */
	public SedStateError set_bool(CirStatement statement,
			CirExpression orig_expression, boolean value) throws Exception {
		return this.get_error(new SedSetExpressionError(statement,
				(SedExpression) SedParser.parse(orig_expression),
				(SedExpression) SedFactory.sed_node(Boolean.valueOf(value))));
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @return set_bool(expr, any)
	 * @throws Exception
	 */
	public SedStateError chg_bool(CirStatement statement,
			CirExpression orig_expression) throws Exception {
		return this.get_error(new SedSetExpressionError(statement,
				(SedExpression) SedParser.parse(orig_expression),
				new SedDefaultValue(null, CBasicTypeImpl.bool_type, SedDefaultValue.AnyValue)));
	}
	/* set_expr(expr::{int|real|pointer}, long|double|any) */
	/**
	 * @param statement
	 * @param orig_expression
	 * @param value
	 * @return set_numb(expr, long|double)
	 * @throws Exception
	 */
	public SedStateError set_numb(CirStatement statement, 
			CirExpression orig_expression, long value) throws Exception {
		return this.get_error(new SedSetExpressionError(statement,
				(SedExpression) SedParser.parse(orig_expression),
				(SedExpression) SedFactory.sed_node(Long.valueOf(value))));
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @param value
	 * @return set_numb(expr, long|double)
	 * @throws Exception
	 */
	public SedStateError set_numb(CirStatement statement, 
			CirExpression orig_expression, double value) throws Exception {
		return this.get_error(new SedSetExpressionError(statement,
				(SedExpression) SedParser.parse(orig_expression),
				(SedExpression) SedFactory.sed_node(Double.valueOf(value))));
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @param value
	 * @return chg_numb(expr, any)
	 * @throws Exception
	 */
	public SedStateError chg_numb(CirStatement statement, CirExpression orig_expression) throws Exception {
		return this.get_error(new SedSetExpressionError(statement,
				(SedExpression) SedParser.parse(orig_expression), 
				new SedDefaultValue(null, orig_expression.get_data_type(), SedDefaultValue.AnyValue)));
	}
	/* dif_numb(expr::{int|real|pointer}, long|double|any) */
	/**
	 * @param statement
	 * @param orig_expression
	 * @param value
	 * @return dif_numb(expr, +, long)
	 * @throws Exception
	 */
	public SedStateError dif_numb(CirStatement statement, 
			CirExpression orig_expression, long value) throws Exception {
		return this.get_error(new SedAddExpressionError(statement,
				(SedExpression) SedParser.parse(orig_expression),
				COperator.arith_add,
				(SedExpression) SedFactory.sed_node(Long.valueOf(value))));
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @param value
	 * @return dif_numb(expr, +, double)
	 * @throws Exception
	 */
	public SedStateError dif_numb(CirStatement statement, 
			CirExpression orig_expression, double value) throws Exception {
		return this.get_error(new SedAddExpressionError(statement,
				(SedExpression) SedParser.parse(orig_expression),
				COperator.arith_add,
				(SedExpression) SedFactory.sed_node(Double.valueOf(value))));
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @return dif_numb(expr, +, any_pos)
	 * @throws Exception
	 */
	public SedStateError inc_numb(CirStatement statement, CirExpression orig_expression) throws Exception {
		return this.get_error(new SedAddExpressionError(statement,
				(SedExpression) SedParser.parse(orig_expression),
				COperator.arith_add,
				new SedDefaultValue(null, orig_expression.get_data_type(), SedDefaultValue.AnyPosValue)));
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @return dif_numb(expr, +, any_neg)
	 * @throws Exception
	 */
	public SedStateError dec_numb(CirStatement statement, CirExpression orig_expression) throws Exception {
		return this.get_error(new SedAddExpressionError(statement,
				(SedExpression) SedParser.parse(orig_expression),
				COperator.arith_add,
				new SedDefaultValue(null, orig_expression.get_data_type(), SedDefaultValue.AnyNegValue)));
	}
	/* abstract expression error */
	/**
	 * @param statement
	 * @param orig_expression
	 * @param muta_expression
	 * @return set_expr(expr, expr)
	 * @throws Exception
	 */
	public SedStateError set_expr(CirStatement statement,
			CirExpression orig_expression, SedExpression muta_expression) throws Exception {
		return this.get_error(new SedSetExpressionError(statement,
				(SedExpression) SedParser.parse(orig_expression), muta_expression));
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @param add_operator
	 * @param add_operand
	 * @return add_expr(expr, oprt, expr)
	 * @throws Exception
	 */
	public SedStateError add_expr(CirStatement statement,
			CirExpression orig_expression, COperator add_operator,
			SedExpression add_operand) throws Exception {
		return this.get_error(new SedAddExpressionError(statement,
				(SedExpression) SedParser.parse(orig_expression),
				add_operator, add_operand));
	}
	
}
