package com.jcsa.jcmutest.sedlang.util;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.sedlang.lang.abst.SedAppExpressionError;
import com.jcsa.jcmutest.sedlang.lang.abst.SedInsExpressionError;
import com.jcsa.jcmutest.sedlang.lang.abst.SedMutExpressionError;
import com.jcsa.jcmutest.sedlang.lang.abst.SedNevExpressionError;
import com.jcsa.jcmutest.sedlang.lang.conc.SedAddExpressionError;
import com.jcsa.jcmutest.sedlang.lang.conc.SedAndExpressionError;
import com.jcsa.jcmutest.sedlang.lang.conc.SedChgExpressionError;
import com.jcsa.jcmutest.sedlang.lang.conc.SedDecExpressionError;
import com.jcsa.jcmutest.sedlang.lang.conc.SedExtExpressionError;
import com.jcsa.jcmutest.sedlang.lang.conc.SedIncExpressionError;
import com.jcsa.jcmutest.sedlang.lang.conc.SedIorExpressionError;
import com.jcsa.jcmutest.sedlang.lang.conc.SedMulExpressionError;
import com.jcsa.jcmutest.sedlang.lang.conc.SedNegExpressionError;
import com.jcsa.jcmutest.sedlang.lang.conc.SedRsvExpressionError;
import com.jcsa.jcmutest.sedlang.lang.conc.SedSetExpressionError;
import com.jcsa.jcmutest.sedlang.lang.conc.SedShkExpressionError;
import com.jcsa.jcmutest.sedlang.lang.conc.SedXorExpressionError;
import com.jcsa.jcmutest.sedlang.lang.cons.SedConditionConstraint;
import com.jcsa.jcmutest.sedlang.lang.cons.SedExecutionConstraint;
import com.jcsa.jcmutest.sedlang.lang.dess.SedDescription;
import com.jcsa.jcmutest.sedlang.lang.serr.SedAddStatementError;
import com.jcsa.jcmutest.sedlang.lang.serr.SedDelStatementError;
import com.jcsa.jcmutest.sedlang.lang.serr.SedMutStatementError;
import com.jcsa.jcmutest.sedlang.lang.serr.SedSetStatementError;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SedStateSpace {
	
	/* definitions */
	private CirTree cir_tree;
	private SedEvaluator evaluator;
	private Map<String, SedDescription> descriptions;
	private SedStateSpace(CirTree cir_tree) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree");
		else {
			this.evaluator = new SedEvaluator();
			this.descriptions = new HashMap<String, SedDescription>();
		}
	}
	
	/* basic methdos */
	public CirTree get_cir_tree() { return this.cir_tree; }
	public SedEvaluator get_evaluator() { return this.evaluator; }
	/**
	 * @param description
	 * @return the unique instance of the SedDescription
	 * @throws Exception
	 */
	private SedDescription get_unique_description(
			SedDescription description) throws Exception {
		String key = description.generate_code();
		if(!this.descriptions.containsKey(key)) {
			this.descriptions.put(key, description);
		}
		return this.descriptions.get(key);
	}
	
	/* constraint */
	/**
	 * @param statement
	 * @param loop_times
	 * @return execute(statement, constant)
	 * @throws Exception
	 */
	public SedExecutionConstraint execution_constraint(
			CirStatement statement, int loop_times) throws Exception {
		SedExecutionConstraint constraint = new SedExecutionConstraint(statement);
		constraint.add_child(SedParser.fetch(Integer.valueOf(loop_times)));
		return (SedExecutionConstraint) this.get_unique_description(constraint);
	}
	/**
	 * @param statement
	 * @param condition
	 * @return assert(statement, expression)
	 * @throws Exception
	 */
	public SedConditionConstraint condition_constraint(
			CirStatement statement, Object condition) throws Exception {
		SedConditionConstraint constraint = new SedConditionConstraint(statement);
		constraint.add_child(SedParser.fetch(condition)); 
		return (SedConditionConstraint) this.get_unique_description(constraint);
	}
	
	/* statement error */
	/**
	 * @param statement
	 * @return add_stmt(statement)
	 * @throws Exception
	 */
	public SedAddStatementError add_statement(CirStatement statement) throws Exception {
		SedAddStatementError error = new SedAddStatementError(statement, statement);
		return (SedAddStatementError) this.get_unique_description(error);
	}
	/**
	 * @param statement
	 * @return del_stmt(statement)
	 * @throws Exception
	 */
	public SedDelStatementError del_statement(CirStatement statement) throws Exception {
		SedDelStatementError error = new SedDelStatementError(statement, statement);
		return (SedDelStatementError) this.get_unique_description(error);
	}
	/**
	 * @param statement
	 * @return set_stmt(orig_stmt, muta_stmt)
	 * @throws Exception
	 */
	public SedSetStatementError set_statement(CirStatement 
			orig_statement, CirStatement muta_statement) throws Exception {
		SedSetStatementError error = new SedSetStatementError(
				orig_statement, orig_statement, muta_statement);
		return (SedSetStatementError) this.get_unique_description(error);
	}
	/**
	 * @param statement
	 * @return mut_stmt(orig_stmt, muta_stmt)
	 * @throws Exception
	 */
	public SedMutStatementError mut_statement(CirStatement 
			orig_statement, CirStatement muta_statement) throws Exception {
		SedMutStatementError error = new SedMutStatementError(
				orig_statement, orig_statement, muta_statement);
		return (SedMutStatementError) this.get_unique_description(error);
	}
	
	/* abstract value error */
	/**
	 * @param statement
	 * @param expression
	 * @param operator {-, ~, !}
	 * @return nev_expr(expr, oprt)
	 * @throws Exception
	 */
	public SedNevExpressionError nev_expression(CirStatement statement,
			CirExpression expression, COperator operator) throws Exception {
		SedNevExpressionError error = new 
				SedNevExpressionError(statement, expression, operator);
		return (SedNevExpressionError) this.get_unique_description(error);
	}
	/**
	 * @param statement
	 * @param expression
	 * @param operator {+, -, *, /, %, &, |, ^, <<, >>}
	 * @param operand
	 * @return app_expr(expr, oprt, expr)
	 * @throws Exception
	 */
	public SedAppExpressionError app_expression(CirStatement statement,
			CirExpression expression, COperator operator, Object operand) throws Exception {
		SedAppExpressionError error = new SedAppExpressionError(
				statement, expression, operator, SedParser.fetch(operand));
		return (SedAppExpressionError) this.get_unique_description(error);
	}
	/**
	 * @param statement
	 * @param expression
	 * @param operator {+, -, *, /, %, &, |, ^, <<, >>}
	 * @param operand
	 * @return ins_expr(expr, oprt, expr)
	 * @throws Exception
	 */
	public SedInsExpressionError ins_expression(CirStatement statement,
			CirExpression expression, COperator operator, Object operand) throws Exception {
		SedInsExpressionError error = new SedInsExpressionError(
				statement, expression, operator, SedParser.fetch(operand));
		return (SedInsExpressionError) this.get_unique_description(error);
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @param muta_expression
	 * @return mut_expr(expr, expr)
	 * @throws Exception
	 */
	public SedMutExpressionError mut_expression(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		SedMutExpressionError error = new SedMutExpressionError(
				statement, orig_expression, SedParser.fetch(muta_expression));
		return (SedMutExpressionError) this.get_unique_description(error);
	}
	
	/* concrete value error */
	public SedChgExpressionError chg_expression(CirStatement statement,
			CirExpression orig_expression) throws Exception {
		SedChgExpressionError error = 
				new SedChgExpressionError(statement, orig_expression);
		return (SedChgExpressionError) this.get_unique_description(error);
	}
	public SedNegExpressionError neg_expression(CirStatement statement,
			CirExpression orig_expression) throws Exception {
		SedNegExpressionError error = 
				new SedNegExpressionError(statement, orig_expression);
		return (SedNegExpressionError) this.get_unique_description(error);
	}
	public SedRsvExpressionError rsv_expression(CirStatement statement,
			CirExpression orig_expression) throws Exception {
		SedRsvExpressionError error = 
				new SedRsvExpressionError(statement, orig_expression);
		return (SedRsvExpressionError) this.get_unique_description(error);
	}
	public SedIncExpressionError inc_expression(CirStatement statement,
			CirExpression orig_expression) throws Exception {
		SedIncExpressionError error = 
				new SedIncExpressionError(statement, orig_expression);
		return (SedIncExpressionError) this.get_unique_description(error);
	}
	public SedDecExpressionError dec_expression(CirStatement statement,
			CirExpression orig_expression) throws Exception {
		SedDecExpressionError error = 
				new SedDecExpressionError(statement, orig_expression);
		return (SedDecExpressionError) this.get_unique_description(error);
	}
	public SedExtExpressionError ext_expression(CirStatement statement,
			CirExpression orig_expression) throws Exception {
		SedExtExpressionError error = 
				new SedExtExpressionError(statement, orig_expression);
		return (SedExtExpressionError) this.get_unique_description(error);
	}
	public SedShkExpressionError shk_expression(CirStatement statement,
			CirExpression orig_expression) throws Exception {
		SedShkExpressionError error = 
				new SedShkExpressionError(statement, orig_expression);
		return (SedShkExpressionError) this.get_unique_description(error);
	}
	public SedSetExpressionError set_expression(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		SedSetExpressionError error = new SedSetExpressionError(statement,
				orig_expression, SedParser.fetch(muta_expression));
		return (SedSetExpressionError) this.get_unique_description(error);
	}
	public SedAddExpressionError add_expression(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		SedAddExpressionError error = new SedAddExpressionError(statement,
				orig_expression, SedParser.fetch(muta_expression));
		return (SedAddExpressionError) this.get_unique_description(error);
	}
	public SedMulExpressionError mul_expression(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		SedMulExpressionError error = new SedMulExpressionError(statement,
				orig_expression, SedParser.fetch(muta_expression));
		return (SedMulExpressionError) this.get_unique_description(error);
	}
	public SedAndExpressionError and_expression(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		SedAndExpressionError error = new SedAndExpressionError(statement,
				orig_expression, SedParser.fetch(muta_expression));
		return (SedAndExpressionError) this.get_unique_description(error);
	}
	public SedIorExpressionError ior_expression(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		SedIorExpressionError error = new SedIorExpressionError(statement,
				orig_expression, SedParser.fetch(muta_expression));
		return (SedIorExpressionError) this.get_unique_description(error);
	}
	public SedXorExpressionError xor_expression(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		SedXorExpressionError error = new SedXorExpressionError(statement,
				orig_expression, SedParser.fetch(muta_expression));
		return (SedXorExpressionError) this.get_unique_description(error);
	}
	
}
