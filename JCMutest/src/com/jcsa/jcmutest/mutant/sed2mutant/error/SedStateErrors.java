package com.jcsa.jcmutest.mutant.sed2mutant.error;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedConstant;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.util.SedEvalScope;
import com.jcsa.jcmutest.mutant.sed2mutant.util.SedEvaluator;
import com.jcsa.jcmutest.mutant.sed2mutant.util.SedFactory;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It provides interfaces to create SedStateError.
 * 
 * @author yukimula
 *
 */
public class SedStateErrors {
	
	/* definitions */
	/** the evaluator to evaluate the SedExpression **/
	private SedEvaluator evaluator;
	/** the unique set of state errors in the space **/
	private Map<String, SedStateError> errors;
	/** used to extend from the state error source **/
	private Set<SedStateError> extension_set;
	/**
	 * create the space for creating and extending state errors.
	 */
	public SedStateErrors() {
		this.evaluator = new SedEvaluator();
		this.errors = new HashMap<String, SedStateError>();
		this.extension_set = new HashSet<SedStateError>();
	}
	
	/* getters */
	/**
	 * set the scope of symbolic evaluation to support computation of expression
	 * @param scope
	 */
	public void set_eval_scope(SedEvalScope scope) {
		this.evaluator.set_context(scope);
	}
	/**
	 * @param error
	 * @return the unique instance of the state error.
	 */
	private SedStateError get_unique_error(SedStateError error) {
		if(error == null)
			return null;
		else {
			String key = error.toString();
			if(!this.errors.containsKey(key)) {
				this.errors.put(key, error);
			}
			return this.errors.get(key);
		}
	}
	private SedExpression evaluate(SedExpression expr) throws Exception {
		return (SedExpression) this.evaluator.evaluate(expr);
	}
	private SedExpression arith_add(SedExpression orig_expression, SedExpression muta_expression) throws Exception {
		return this.evaluate(SedFactory.arith_add(orig_expression.
				get_data_type(), orig_expression, muta_expression));
	}
	private SedExpression arith_sub(SedExpression orig_expression, SedExpression muta_expression) throws Exception {
		return this.evaluate(SedFactory.arith_sub(orig_expression.
				get_data_type(), orig_expression, muta_expression));
	}
	private SedExpression arith_mul(SedExpression orig_expression, SedExpression muta_expression) throws Exception {
		return this.evaluate(SedFactory.arith_mul(orig_expression.
				get_data_type(), orig_expression, muta_expression));
	}
	private SedExpression bitws_and(SedExpression orig_expression, SedExpression muta_expression) throws Exception {
		return this.evaluate(SedFactory.bitws_and(orig_expression.
				get_data_type(), orig_expression, muta_expression));
	}
	private SedExpression bitws_ior(SedExpression orig_expression, SedExpression muta_expression) throws Exception {
		return this.evaluate(SedFactory.bitws_ior(orig_expression.
				get_data_type(), orig_expression, muta_expression));
	}
	private SedExpression bitws_xor(SedExpression orig_expression, SedExpression muta_expression) throws Exception {
		return this.evaluate(SedFactory.bitws_xor(orig_expression.
				get_data_type(), orig_expression, muta_expression));
	}
	
	/* verifications */
	private boolean is_void(CirExpression expression) throws Exception {
		CType data_type = expression.get_data_type();
		data_type = CTypeAnalyzer.get_value_type(data_type);
		return CTypeAnalyzer.is_void(data_type);
	}
	private boolean is_bool(CirExpression expression) throws Exception {
		CType data_type = expression.get_data_type();
		data_type = CTypeAnalyzer.get_value_type(data_type);
		if(CTypeAnalyzer.is_boolean(data_type)) return true;
		else {
			CirStatement statement = expression.statement_of();
			return statement instanceof CirIfStatement ||
					statement instanceof CirCaseStatement;
		}
	}
	private boolean is_char(CirExpression expression) throws Exception {
		CType data_type = expression.get_data_type();
		data_type = CTypeAnalyzer.get_value_type(data_type);
		return CTypeAnalyzer.is_character(data_type);
	}
	private boolean is_long(CirExpression expression) throws Exception {
		CType data_type = expression.get_data_type();
		data_type = CTypeAnalyzer.get_value_type(data_type);
		return CTypeAnalyzer.is_integer(data_type);
	}
	private boolean is_real(CirExpression expression) throws Exception {
		CType data_type = expression.get_data_type();
		data_type = CTypeAnalyzer.get_value_type(data_type);
		return CTypeAnalyzer.is_real(data_type);
	}
	private boolean is_addr(CirExpression expression) throws Exception {
		CType data_type = expression.get_data_type();
		data_type = CTypeAnalyzer.get_value_type(data_type);
		return CTypeAnalyzer.is_pointer(data_type);
	}
	
	/* constant getter */
	private boolean get_bool(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:		return constant.get_bool().booleanValue();
		case c_char:
		case c_uchar:		return constant.get_char().charValue() != 0;
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return constant.get_integer().intValue() != 0;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return constant.get_long().longValue() != 0L;
		case c_float:		return constant.get_float().floatValue() != 0.0f;
		case c_double:
		case c_ldouble:		return constant.get_double().doubleValue() != 0.0;
		default: throw new IllegalArgumentException("Invalid constant.");
		}
	}
	private char get_char(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:		return constant.get_bool().booleanValue() ? '\1' : '\0';
		case c_char:
		case c_uchar:		return (char) constant.get_char().charValue();
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return (char) constant.get_integer().intValue();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return (char) constant.get_long().longValue();
		case c_float:		return (char) constant.get_float().floatValue();
		case c_double:
		case c_ldouble:		return (char) constant.get_double().doubleValue();
		default: throw new IllegalArgumentException("Invalid constant.");
		}
	}
	private long get_long(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:		return constant.get_bool().booleanValue() ? '\1' : '\0';
		case c_char:
		case c_uchar:		return (long) constant.get_char().charValue();
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return (long) constant.get_integer().intValue();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return (long) constant.get_long().longValue();
		case c_float:		return (long) constant.get_float().floatValue();
		case c_double:
		case c_ldouble:		return (long) constant.get_double().doubleValue();
		default: throw new IllegalArgumentException("Invalid constant.");
		}
	}
	private double get_real(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:		return constant.get_bool().booleanValue() ? '\1' : '\0';
		case c_char:
		case c_uchar:		return (double) constant.get_char().charValue();
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return (double) constant.get_integer().intValue();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return (double) constant.get_long().longValue();
		case c_float:		return (double) constant.get_float().floatValue();
		case c_double:
		case c_ldouble:		return (double) constant.get_double().doubleValue();
		default: throw new IllegalArgumentException("Invalid constant.");
		}
	}
	
	/* state error creators */
	public SedStateError add_statement(CirStatement statement) throws Exception {
		return this.get_unique_error(new SedAddStatementError(statement, statement));
	}
	public SedStateError del_statement(CirStatement statement) throws Exception {
		return this.get_unique_error(new SedDelStatementError(statement, statement));
	}
	public SedStateError set_statement(
			CirStatement orig_statement,
			CirStatement muta_statement) throws Exception {
		return this.get_unique_error(new SedSetStatementError(
				orig_statement, orig_statement, muta_statement));
	}
	public SedStateError ins_expression(CirStatement statement,
			CirExpression expression, COperator operator) throws Exception {
		switch(operator) {
		case negative:
		case bit_not:
		case logic_not:	return this.get_unique_error(new SedInsExpressionError(statement, expression, operator));
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	public SedStateError set_expression(CirStatement statement,
			CirExpression orig_expression, 
			SedExpression muta_expression) throws Exception {
		return this.get_unique_error(new SedMutExpressionError(
				statement, orig_expression, muta_expression));
	}
	public SedStateError app_expression(CirStatement statement,
			CirExpression orig_expression, COperator operator,
			SedExpression app_expression) throws Exception {
		switch(operator) {
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:
		case logic_and:
		case logic_or:	
		{
			return this.get_unique_error(new SedAppExpressionError(
					statement, orig_expression, operator, app_expression));
		}
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
	}
	
	/* concrete error extension methods */
	/**
	 * @param source
	 * @return the set of state errors generated from source or empty if 
	 * 		   the source is equivalent and invalid
	 * @throws Exception
	 */
	public Collection<SedStateError> extend(SedStateError source) throws Exception {
		List<SedStateError> results = new ArrayList<SedStateError>();
		this.extension_set.clear();
		if(this.extend_at(source)) {
			results.addAll(this.extension_set);
		}
		return results;
	}
	private boolean extend_at(SedStateError source) throws Exception {
		source = this.get_unique_error(source);
		if(source == null) {
			return false;
		}
		else if(this.extension_set.contains(source)) {
			return true;
		}
		else {
			this.extension_set.add(source);
			if(source instanceof SedStatementError) {
				return this.extend_statement_error((SedStatementError) source);
			}
			else if(source instanceof SedAbsExpressionError) {
				return this.extend_abs_expression_error((SedAbsExpressionError) source);
			}
			else if(source instanceof SedConBinExpressionError) {
				return this.extend_bin_expression((SedConBinExpressionError) source);
			}
			// TODO implement the method of extending state error types
			else {
				throw new IllegalArgumentException("Invalid source: " + source);
			}
		}
	}
	private boolean extend_statement_error(SedStatementError source) throws Exception {
		return true;	// do not extend at statement error since it is too big
	}
	
	/* abstract expression error */
	private boolean extend_abs_expression_error(SedAbsExpressionError source) throws Exception {
		if(source instanceof SedInsExpressionError)
			return this.extend_ins_expression((SedInsExpressionError) source);
		else if(source instanceof SedMutExpressionError)
			return this.extend_mut_expression((SedMutExpressionError) source);
		else if(source instanceof SedAppExpressionError)
			return this.extend_app_expression((SedAppExpressionError) source);
		else
			throw new IllegalArgumentException(source.generate_code());
	}
	private boolean extend_ins_expression(SedInsExpressionError source) throws Exception {
		CirStatement statement = source.get_statement().get_cir_statement();
		CirExpression expression = (CirExpression) source.get_orig_expression().get_cir_source();
		COperator operator = source.get_ins_operator().get_operator();
		switch(operator) {
		case negative:
		{
			if(this.is_bool(expression)) {
				return false;	/* no influence on the boolean context */
			}
			else if(this.is_char(expression)) {	/* neg_char(statement, expression) */
				return this.extend_at(new SedNegExpressionError(statement, expression));
			}
			else if(this.is_long(expression)) {	/* neg_{sign|usign}(statement, expression) */
				return this.extend_at(new SedNegExpressionError(statement, expression));
			}
			else if(this.is_real(expression)) {	/* neg_real(statement, expression) */
				return this.extend_at(new SedNegExpressionError(statement, expression));
			}
			else {
				throw new IllegalArgumentException("Invalid: " + expression.generate_code(true));
			}
		}
		case bit_not:
		{
			if(this.is_bool(expression)) {	/* set_bool(expression, true) */
				SedExpression parameter = (SedExpression) SedFactory.sed_node(Boolean.TRUE);
				return this.extend_at(new SedSetExpressionError(statement, expression, parameter));
			}
			else if(this.is_char(expression)) {	/* rsv_char(expression) */
				return this.extend_at(new SedRsvExpressionError(statement, expression));
			}
			else if(this.is_long(expression)) {	/* rsv_{sign|usign}(expression) */
				return this.extend_at(new SedRsvExpressionError(statement, expression));
			}
			else {
				throw new IllegalArgumentException("Invalid: " + expression.generate_code(true));
			}
		}
		case logic_not:
		{
			if(this.is_bool(expression)) {	/* chg_bool(expression) as not_bool */
				return this.extend_at(new SedChgExpressionError(statement, expression));
			}
			else if(this.is_char(expression)) {	/* set_char(expression, '\0') in most case */
				SedExpression parameter = (SedExpression) SedFactory.sed_node(Character.valueOf('\0'));
				return this.extend_at(new SedSetExpressionError(statement, expression, parameter));
			}
			else if(this.is_long(expression)) {	/* set_long(expression, 0L) in most case */
				SedExpression parameter = (SedExpression) SedFactory.sed_node(Long.valueOf(0L));
				return this.extend_at(new SedSetExpressionError(statement, expression, parameter));
			}
			else if(this.is_real(expression)) {	/* set_real(expression, 0.0) in most case */
				SedExpression parameter = (SedExpression) SedFactory.sed_node(Double.valueOf(0.0));
				return this.extend_at(new SedSetExpressionError(statement, expression, parameter));
			}
			else if(this.is_addr(expression)) {	/* set_addr(expression, NULL) in most case */
				SedExpression parameter = (SedExpression) SedFactory.sed_node(Long.valueOf(0L));
				return this.extend_at(new SedSetExpressionError(statement, expression, parameter));
			}
			else {
				throw new IllegalArgumentException("Invalid: " + expression.generate_code(true));
			}
		}
		default:	throw new IllegalArgumentException("Invalid operator: " + operator);
		}
	}
	private boolean extend_mut_expression(SedMutExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_statement().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		muta_expression = this.evaluate(muta_expression);
		
		if(this.is_void(expression)) {
			throw new IllegalArgumentException("Void type is avoided in translation");
		}
		else if(this.is_bool(expression)) {
			if(muta_expression instanceof SedConstant) {
				Boolean value = Boolean.valueOf(this.get_bool(((SedConstant) muta_expression).get_constant()));
				SedExpression parameter = (SedExpression) SedFactory.sed_node(value);
				return this.extend_at(new SedSetExpressionError(statement, expression, parameter));
			}
			else {
				return this.extend_at(new SedSetExpressionError(statement, expression, source.get_muta_expression()));
			}
		}
		else if(this.is_char(expression)) {
			if(muta_expression instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) muta_expression).get_constant()));
				SedExpression parameter = (SedExpression) SedFactory.sed_node(value);
				return this.extend_at(new SedSetExpressionError(statement, expression, parameter));
			}
			else {
				return this.extend_at(new SedSetExpressionError(statement, expression, source.get_muta_expression()));
			}
		}
		else if(this.is_long(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				SedExpression parameter = (SedExpression) SedFactory.sed_node(value);
				return this.extend_at(new SedSetExpressionError(statement, expression, parameter));
			}
			else {
				return this.extend_at(new SedSetExpressionError(statement, expression, source.get_muta_expression()));
			}
		}
		else if(this.is_real(expression)) {
			if(muta_expression instanceof SedConstant) {
				Double value = Double.valueOf(this.get_real(((SedConstant) muta_expression).get_constant()));
				SedExpression parameter = (SedExpression) SedFactory.sed_node(value);
				return this.extend_at(new SedSetExpressionError(statement, expression, parameter));
			}
			else {
				return this.extend_at(new SedSetExpressionError(statement, expression, source.get_muta_expression()));
			}
		}
		else if(this.is_addr(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				SedExpression parameter = (SedExpression) SedFactory.sed_node(value);
				return this.extend_at(new SedSetExpressionError(statement, expression, parameter));
			}
			else {
				return this.extend_at(new SedSetExpressionError(statement, expression, source.get_muta_expression()));
			}
		}
		else {
			return this.extend_at(new SedSetExpressionError(statement, expression, source.get_muta_expression()));
		}
	}
	private boolean extend_app_expression(SedAppExpressionError source) throws Exception {
		COperator operator = source.get_app_operator().get_operator();
		switch(operator) {
		case arith_add:		return this.extend_app_add_expression(source);
		case arith_sub:		return this.extend_app_sub_expression(source);
		case arith_mul:		return this.extend_app_mul_expression(source);
		case arith_div:		return this.extend_app_div_expression(source);
		case arith_mod:		return this.extend_app_mod_expression(source);
		case bit_and:		return this.extend_app_and_expression(source);
		case bit_or:		return this.extend_app_ior_expression(source);
		case bit_xor:		return this.extend_app_xor_expression(source);
		case left_shift:	return this.extend_app_lsh_expression(source);
		case righ_shift:	return this.extend_app_rsh_expression(source);
		default: throw new IllegalArgumentException("Unsupport: " + operator);
		}
	}
	private boolean extend_app_add_expression(SedAppExpressionError source) throws Exception {
		CirStatement statement = source.get_statement().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		SedExpression muta_expression = this.evaluate(source.get_app_operand());
		if(this.is_bool(expression)) {	/* chg_bool(statement, expression) */
			return this.extend_at(new SedChgExpressionError(statement, expression));
		}
		else if(this.is_char(expression)) {
			if(muta_expression instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) muta_expression).get_constant()));
				return this.extend_at(new SedAddExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)));
			}
			else {
				return this.extend_at(new SedAddExpressionError(statement, expression, muta_expression));
			}
		}
		else if(this.is_long(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				return this.extend_at(new SedAddExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)));
			}
			else {
				return this.extend_at(new SedAddExpressionError(statement, expression, muta_expression));
			}
		}
		else if(this.is_real(expression)) {
			if(muta_expression instanceof SedConstant) {
				Double value = Double.valueOf(this.get_real(((SedConstant) muta_expression).get_constant()));
				return this.extend_at(new SedAddExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)));
			}
			else {
				return this.extend_at(new SedAddExpressionError(statement, expression, muta_expression));
			}
		}
		else if(this.is_addr(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				return this.extend_at(new SedAddExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)));
			}
			else {
				return this.extend_at(new SedAddExpressionError(statement, expression, muta_expression));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + source.generate_code());
		}
	}
	private boolean extend_app_sub_expression(SedAppExpressionError source) throws Exception {
		CirStatement statement = source.get_statement().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		SedExpression muta_expression = this.evaluate(SedFactory.
					arith_neg(expression.get_data_type(), source.get_app_operand()));
		if(this.is_bool(expression)) {	/* chg_bool(statement, expression) */
			return this.extend_at(new SedChgExpressionError(statement, expression));
		}
		else if(this.is_char(expression)) {
			if(muta_expression instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) muta_expression).get_constant()));
				return this.extend_at(new SedAddExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)));
			}
			else {
				return this.extend_at(new SedAddExpressionError(statement, expression, muta_expression));
			}
		}
		else if(this.is_long(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				return this.extend_at(new SedAddExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)));
			}
			else {
				return this.extend_at(new SedAddExpressionError(statement, expression, muta_expression));
			}
		}
		else if(this.is_real(expression)) {
			if(muta_expression instanceof SedConstant) {
				Double value = Double.valueOf(this.get_real(((SedConstant) muta_expression).get_constant()));
				return this.extend_at(new SedAddExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)));
			}
			else {
				return this.extend_at(new SedAddExpressionError(statement, expression, muta_expression));
			}
		}
		else if(this.is_addr(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				return this.extend_at(new SedAddExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)));
			}
			else {
				return this.extend_at(new SedAddExpressionError(statement, expression, muta_expression));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + source.generate_code());
		}
	}
	private boolean extend_app_mul_expression(SedAppExpressionError source) throws Exception {
		CirStatement statement = source.get_statement().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		if(this.is_char(expression) || this.is_long(expression) || this.is_real(expression)) {
			return this.extend_at(new SedMulExpressionError(statement, expression, source.get_app_operand()));
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	private boolean extend_app_div_expression(SedAppExpressionError source) throws Exception {
		CirStatement statement = source.get_statement().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		SedExpression muta_expression = this.evaluate(source.get_app_operand());
		if(this.is_char(expression) || this.is_long(expression) || this.is_real(expression)) {
			muta_expression = 
					SedFactory.arith_div(expression.get_data_type(), orig_expression, muta_expression);
			return this.extend_at(new SedSetExpressionError(statement, expression, muta_expression));
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	private boolean extend_app_mod_expression(SedAppExpressionError source) throws Exception {
		CirStatement statement = source.get_statement().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		SedExpression muta_expression = this.evaluate(source.get_app_operand());
		if(this.is_char(expression) || this.is_long(expression) || this.is_real(expression)) {
			muta_expression = 
					SedFactory.arith_mod(expression.get_data_type(), orig_expression, muta_expression);
			return this.extend_at(new SedSetExpressionError(statement, expression, muta_expression));
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	private boolean extend_app_and_expression(SedAppExpressionError source) throws Exception {
		CirStatement statement = source.get_statement().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		SedExpression muta_expression = this.evaluate(source.get_app_operand());
		if(this.is_bool(expression)) {
			if(muta_expression instanceof SedConstant) {
				if(this.get_bool(((SedConstant) muta_expression).get_constant())) {
					return true;	/* equivalent mutation since x == x & true */
				}
				else {
					muta_expression = (SedExpression) SedFactory.sed_node(Boolean.FALSE);
					return this.extend_at(new SedSetExpressionError(statement, expression, muta_expression));
				}
			}
			else {
				muta_expression = (SedExpression) SedFactory.sed_node(Boolean.FALSE);
				return this.extend_at(new SedSetExpressionError(statement, expression, muta_expression));
			}
		}
		else if(this.is_char(expression) || this.is_long(expression)) {
			return this.extend_at(new SedAndExpressionError(statement, expression, muta_expression));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + source.generate_code());
		}
	}
	private boolean extend_app_ior_expression(SedAppExpressionError source) throws Exception {
		CirStatement statement = source.get_statement().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		SedExpression muta_expression = this.evaluate(source.get_app_operand());
		if(this.is_bool(expression)) {
			if(muta_expression instanceof SedConstant) {
				if(!this.get_bool(((SedConstant) muta_expression).get_constant())) {
					return true;	/* equivalent mutation since x == x | false */
				}
				else {
					muta_expression = (SedExpression) SedFactory.sed_node(Boolean.TRUE);
					return this.extend_at(new SedSetExpressionError(statement, expression, muta_expression));
				}
			}
			else {
				muta_expression = (SedExpression) SedFactory.sed_node(Boolean.TRUE);
				return this.extend_at(new SedSetExpressionError(statement, expression, muta_expression));
			}
		}
		else if(this.is_char(expression) || this.is_long(expression)) {
			return this.extend_at(new SedIorExpressionError(statement, expression, muta_expression));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + source.generate_code());
		}
	}
	private boolean extend_app_xor_expression(SedAppExpressionError source) throws Exception {
		CirStatement statement = source.get_statement().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		SedExpression muta_expression = this.evaluate(source.get_app_operand());
		if(this.is_bool(expression)) {
			if(muta_expression instanceof SedConstant) {
				if(!this.get_bool(((SedConstant) muta_expression).get_constant())) {
					return true;	/* equivalent mutation since x == x ^ false */
				}
				else {
					return this.extend_at(new SedChgExpressionError(statement, expression));
				}
			}
			else {
				return this.extend_at(new SedChgExpressionError(statement, expression));
			}
		}
		else if(this.is_char(expression) || this.is_long(expression)) {
			return this.extend_at(new SedXorExpressionError(statement, expression, muta_expression));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + source.generate_code());
		}
	}
	private boolean extend_app_lsh_expression(SedAppExpressionError source) throws Exception {
		CirStatement statement = source.get_statement().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		SedExpression muta_expression = this.evaluate(source.get_app_operand());
		if(this.is_char(expression) || this.is_long(expression)) {
			muta_expression = 
					SedFactory.bitws_lsh(expression.get_data_type(), orig_expression, muta_expression);
			return this.extend_at(new SedSetExpressionError(statement, expression, muta_expression));
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	private boolean extend_app_rsh_expression(SedAppExpressionError source) throws Exception {
		CirStatement statement = source.get_statement().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		SedExpression muta_expression = this.evaluate(source.get_app_operand());
		if(this.is_char(expression) || this.is_long(expression)) {
			muta_expression = 
					SedFactory.bitws_rsh(expression.get_data_type(), orig_expression, muta_expression);
			return this.extend_at(new SedSetExpressionError(statement, expression, muta_expression));
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	
	/* concrete expression error {binary} */
	private boolean extend_set_expression(SedSetExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_statement().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		SedExpression loperand = this.evaluate(orig_expression);
		SedExpression roperand = this.evaluate(muta_expression);
		SedExpression difference = this.arith_sub(loperand, roperand);
		
		/**
		 * set_bool ==> chg_bool
		 */
		if(this.is_bool(expression)) {
			return this.extend_at(new SedChgExpressionError(statement, expression));
		}
		/**
		 * 	set_char ==> add_char			{difference: const}
		 * 	set_char ==> neg_char|rsv_char	{const, const}
		 * 	set_char ==> chg_char
		 */
		else if(this.is_char(expression)) {
			/* set_char ==> add_char			{difference: const} */
			if(difference instanceof SedConstant) {
				if(!this.extend_at(new SedAddExpressionError(statement, expression, difference))) {
					return false;
				}
			}
			/* set_char ==> neg_char|rsv_char	{const, const} */
			if(loperand instanceof SedConstant) {
				if(roperand instanceof SedConstant) {
					char x = this.get_char(((SedConstant) loperand).get_constant());
					char y = this.get_char(((SedConstant) roperand).get_constant());
					if(x == y) {
						return false;
					}
					else if(x == -y) {
						if(!this.extend_at(new SedNegExpressionError(statement, expression))) {
							return false;
						}
					}
					else if(x == ~y) {
						if(!this.extend_at(new SedRsvExpressionError(statement, expression))) {
							return false;
						}
					}
				}
			}
			/* set_char ==> chg_char */
			return this.extend_at(new SedChgExpressionError(statement, expression));
		}
		/**
		 * 	set_long ==> add_long			{difference: const}
		 * 	set_long ==> neg_long|rsv_long	{const, const}
		 * 	set_long ==> chg_long
		 */
		else if(this.is_long(expression)) {
			/* set_long ==> add_long			{difference: const} */
			if(difference instanceof SedConstant) {
				if(!this.extend_at(new SedAddExpressionError(statement, expression, difference))) {
					return false;
				}
			}
			/* set_long ==> neg_long|rsv_long	{const, const} */
			if(loperand instanceof SedConstant) {
				if(roperand instanceof SedConstant) {
					long x = this.get_long(((SedConstant) loperand).get_constant());
					long y = this.get_long(((SedConstant) roperand).get_constant());
					if(x == y) {
						return false;
					}
					else if(x == -y) {
						if(!this.extend_at(new SedNegExpressionError(statement, expression))) {
							return false;
						}
					}
					else if(x == ~y) {
						if(!this.extend_at(new SedRsvExpressionError(statement, expression))) {
							return false;
						}
					}
				}
			}
			/* set_long ==> chg_long */
			return this.extend_at(new SedChgExpressionError(statement, expression));
		}
		/**
		 * 	set_real ==> add_real			{difference: const}
		 * 	set_real ==> neg_real|rsv_real	{const, const}
		 * 	set_real ==> chg_real
		 */
		else if(this.is_real(expression)) {
			/* set_real ==> add_real			{difference: const} */
			if(difference instanceof SedConstant) {
				if(!this.extend_at(new SedAddExpressionError(statement, expression, difference))) {
					return false;
				}
			}
			/* set_real ==> neg_real	{const, const} */
			if(loperand instanceof SedConstant) {
				if(roperand instanceof SedConstant) {
					double x = this.get_real(((SedConstant) loperand).get_constant());
					double y = this.get_real(((SedConstant) roperand).get_constant());
					if(x == y) {
						return false;
					}
					else if(x == -y) {
						if(!this.extend_at(new SedNegExpressionError(statement, expression))) {
							return false;
						}
					}
				}
			}
			/* set_real ==> chg_real */
			return this.extend_at(new SedChgExpressionError(statement, expression));
		}
		/**
		 * 	set_addr ==> add_addr			{difference: const}
		 * 	set_addr ==> chg_addr
		 */
		else if(this.is_addr(expression)) {
			/* set_long ==> add_long			{difference: const} */
			if(difference instanceof SedConstant) {
				if(!this.extend_at(new SedAddExpressionError(statement, expression, difference))) {
					return false;
				}
			}
			/* set_long ==> chg_long */
			return this.extend_at(new SedChgExpressionError(statement, expression));
		}
		else {
			return this.extend_at(new SedChgExpressionError(statement, expression));
		}
	}
	private boolean extend_add_expression(SedAddExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_statement().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		SedExpression roperand = this.evaluate(muta_expression);
		SedExpression parameter = this.arith_add(orig_expression, muta_expression);
		
		if(this.is_char(expression)) {
			/* add_expr ==> inc_expr|dec_expr {xxxxx, const} */
			if(roperand instanceof SedConstant) {
				char difference = this.get_char(((SedConstant) roperand).get_constant());
				if(difference > 0) {
					if(!this.extend_at(new SedIncExpressionError(statement, expression))) {
						return false;
					}
				}
				else if(difference < 0) {
					if(!this.extend_at(new SedDecExpressionError(statement, expression))) {
						return false;
					}
				}
				else {
					return false;
				}
			}
			
			/* add_expr ==> set_expr(expr, expr) {x + y: const} */
			if(parameter instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) parameter).get_constant()));
				parameter = (SedExpression) SedFactory.sed_node(value);
				if(!this.extend_at(new SedSetExpressionError(statement, expression, parameter))) {
					return false;
				}
			}
			
			/* add_expr ==> chg_expr */
			return this.extend_at(new SedChgExpressionError(statement, expression));
		}
		else if(this.is_long(expression)) {
			/* add_expr ==> inc_expr|dec_expr {xxxxx, const} */
			if(roperand instanceof SedConstant) {
				long difference = this.get_long(((SedConstant) roperand).get_constant());
				if(difference > 0) {
					if(!this.extend_at(new SedIncExpressionError(statement, expression))) {
						return false;
					}
				}
				else if(difference < 0) {
					if(!this.extend_at(new SedDecExpressionError(statement, expression))) {
						return false;
					}
				}
				else {
					return false;
				}
			}
			
			/* add_expr ==> set_expr(expr, expr) {x + y: const} */
			if(parameter instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) parameter).get_constant()));
				parameter = (SedExpression) SedFactory.sed_node(value);
				if(!this.extend_at(new SedSetExpressionError(statement, expression, parameter))) {
					return false;
				}
			}
			
			/* add_expr ==> chg_expr */
			return this.extend_at(new SedChgExpressionError(statement, expression));
		}
		else if(this.is_real(expression)) {
			/* add_expr ==> inc_expr|dec_expr {xxxxx, const} */
			if(roperand instanceof SedConstant) {
				double difference = this.get_real(((SedConstant) roperand).get_constant());
				if(difference > 0) {
					if(!this.extend_at(new SedIncExpressionError(statement, expression))) {
						return false;
					}
				}
				else if(difference < 0) {
					if(!this.extend_at(new SedDecExpressionError(statement, expression))) {
						return false;
					}
				}
				else {
					return false;
				}
			}
			
			/* add_expr ==> set_expr(expr, expr) {x + y: const} */
			if(parameter instanceof SedConstant) {
				Double value = Double.valueOf(this.get_real(((SedConstant) parameter).get_constant()));
				parameter = (SedExpression) SedFactory.sed_node(value);
				if(!this.extend_at(new SedSetExpressionError(statement, expression, parameter))) {
					return false;
				}
			}
			
			/* add_expr ==> chg_expr */
			return this.extend_at(new SedChgExpressionError(statement, expression));
		}
		else if(this.is_addr(expression)) {
			/* add_expr ==> inc_expr|dec_expr {xxxxx, const} */
			if(roperand instanceof SedConstant) {
				long difference = this.get_long(((SedConstant) roperand).get_constant());
				if(difference > 0) {
					if(!this.extend_at(new SedIncExpressionError(statement, expression))) {
						return false;
					}
				}
				else if(difference < 0) {
					if(!this.extend_at(new SedDecExpressionError(statement, expression))) {
						return false;
					}
				}
				else {
					return false;
				}
			}
			
			/* add_expr ==> chg_expr */
			return this.extend_at(new SedChgExpressionError(statement, expression));
		}
		else {
			throw new IllegalArgumentException("Invalid expression: " + expression.generate_code(true));
		}
	}
	private boolean extend_mul_expression(SedMulExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_statement().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		SedExpression loperand = this.evaluate(orig_expression);
		SedExpression roperand = this.evaluate(muta_expression);
		SedExpression parameter = this.arith_mul(orig_expression, muta_expression);
		if(this.is_char(expression)) {
			/* mul_expr ==> equivalent when x == 0 */
			if(loperand instanceof SedConstant) {
				char value = this.get_char(((SedConstant) loperand).get_constant());
				if(value == 0) {
					return false;
				}
			}
			/* mul_expr ==> set_expr | neg_expr | ext_expr | shk_expr {xxxxx, const} */
			if(roperand instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) roperand).get_constant()));
				if(value == 0) {
					if(!this.extend_at(new SedSetExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)))) {
						return false;
					}
				}
				else if(value == 1) {
					return false;
				}
				else if(value == -1) {
					if(!this.extend_at(new SedNegExpressionError(statement, expression))) {
						return false;
					}
				}
				else if(value > 1 || value < -1) {
					if(!this.extend_at(new SedExtExpressionError(statement, expression))) {
						return false;
					}
				}
				else {
					if(!this.extend_at(new SedShkExpressionError(statement, expression))) {
						return false;
					}
				}
			}
			/* mul_expr ==> set_expr {x * y : const} */
			if(parameter instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) parameter).get_constant()));
				parameter = (SedExpression) SedFactory.sed_node(value);
				if(!this.extend_at(new SedSetExpressionError(statement, expression, parameter))) {
					return false;
				}
			}
			/* mul_expr ==> chg_expr */
			return this.extend_at(new SedChgExpressionError(statement, expression));
		}
		else if(this.is_long(expression)) {
			/* mul_expr ==> equivalent when x == 0 */
			if(loperand instanceof SedConstant) {
				long value = this.get_long(((SedConstant) loperand).get_constant());
				if(value == 0) {
					return false;
				}
			}
			/* mul_expr ==> set_expr | neg_expr | ext_expr | shk_expr {xxxxx, const} */
			if(roperand instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) roperand).get_constant()));
				if(value == 0) {
					if(!this.extend_at(new SedSetExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)))) {
						return false;
					}
				}
				else if(value == 1) {
					return false;
				}
				else if(value == -1) {
					if(!this.extend_at(new SedNegExpressionError(statement, expression))) {
						return false;
					}
				}
				else if(value > 1 || value < -1) {
					if(!this.extend_at(new SedExtExpressionError(statement, expression))) {
						return false;
					}
				}
				else {
					if(!this.extend_at(new SedShkExpressionError(statement, expression))) {
						return false;
					}
				}
			}
			/* mul_expr ==> set_expr {x * y : const} */
			if(parameter instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) parameter).get_constant()));
				parameter = (SedExpression) SedFactory.sed_node(value);
				if(!this.extend_at(new SedSetExpressionError(statement, expression, parameter))) {
					return false;
				}
			}
			/* mul_expr ==> chg_expr */
			return this.extend_at(new SedChgExpressionError(statement, expression));
		}
		else if(this.is_real(expression)) {
			/* mul_expr ==> equivalent when x == 0 */
			if(loperand instanceof SedConstant) {
				double value = this.get_real(((SedConstant) loperand).get_constant());
				if(value == 0) {
					return false;
				}
			}
			/* mul_expr ==> set_expr | neg_expr | ext_expr | shk_expr {xxxxx, const} */
			if(roperand instanceof SedConstant) {
				Double value = Double.valueOf(this.get_real(((SedConstant) roperand).get_constant()));
				if(value == 0) {
					if(!this.extend_at(new SedSetExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)))) {
						return false;
					}
				}
				else if(value == 1) {
					return false;
				}
				else if(value == -1) {
					if(!this.extend_at(new SedNegExpressionError(statement, expression))) {
						return false;
					}
				}
				else if(value > 1 || value < -1) {
					if(!this.extend_at(new SedExtExpressionError(statement, expression))) {
						return false;
					}
				}
				else {
					if(!this.extend_at(new SedShkExpressionError(statement, expression))) {
						return false;
					}
				}
			}
			/* mul_expr ==> set_expr {x * y : const} */
			if(parameter instanceof SedConstant) {
				Double value = Double.valueOf(this.get_real(((SedConstant) parameter).get_constant()));
				parameter = (SedExpression) SedFactory.sed_node(value);
				if(!this.extend_at(new SedSetExpressionError(statement, expression, parameter))) {
					return false;
				}
			}
			/* mul_expr ==> chg_expr */
			return this.extend_at(new SedChgExpressionError(statement, expression));
		}
		else {
			throw new IllegalArgumentException("Invalid expression: " + expression.generate_code(true));
		}
	}
	private boolean extend_and_expression(SedAndExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_statement().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		SedExpression loperand = this.evaluate(orig_expression);
		SedExpression roperand = this.evaluate(muta_expression);
		SedExpression parameter = this.bitws_and(orig_expression, muta_expression);
		if(this.is_char(expression)) {
			/* and_expr ==> equivalent when x == 0 */
			if(loperand instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) loperand).get_constant()));
				if(value == 0) {
					return false;
				}
			}
			/* and_expr ==> set_expr when param is const */
			if(parameter instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) parameter).get_constant()));
				if(!this.extend_at(new SedSetExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)))) {
					return false;
				}
			}
			/* and_expr ==> set_expr */
			if(roperand instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) roperand).get_constant()));
				if(value == 0) {
					if(!this.extend_at(new SedSetExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)))) {
						return false;
					}
				}
				else if(value == ~0) {
					return false;
				}
			}
			/* and_expr ==> shk_expr */
			return this.extend_at(new SedShkExpressionError(statement, expression));
		}
		else if(this.is_long(expression)) {
			/* and_expr ==> equivalent when x == 0 */
			if(loperand instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) loperand).get_constant()));
				if(value == 0) {
					return false;
				}
			}
			/* and_expr ==> set_expr when param is const */
			if(parameter instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) parameter).get_constant()));
				if(!this.extend_at(new SedSetExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)))) {
					return false;
				}
			}
			/* and_expr ==> set_expr */
			if(roperand instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) roperand).get_constant()));
				if(value == 0) {
					if(!this.extend_at(new SedSetExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)))) {
						return false;
					}
				}
				else if(value == ~0) {
					return false;
				}
			}
			/* and_expr ==> shk_expr */
			return this.extend_at(new SedShkExpressionError(statement, expression));
		}
		else {
			throw new IllegalArgumentException("Invalid expression: " + expression.generate_code(true));
		}
	}
	private boolean extend_ior_expression(SedIorExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_statement().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		SedExpression loperand = this.evaluate(orig_expression);
		SedExpression roperand = this.evaluate(muta_expression);
		SedExpression parameter = this.bitws_ior(orig_expression, muta_expression);
		if(this.is_char(expression)) {
			/* ior_expr ==> equivalent when x == ~0 */
			if(loperand instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) loperand).get_constant()));
				if(value == ~0) {
					return false;
				}
			}
			/* ior_expr ==> set_expr when y is const */
			if(roperand instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) roperand).get_constant()));
				if(value == 0) {
					return false;
				}
				else if(value == ~0) {
					if(!this.extend_at(new SedSetExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)))) {
						return false;
					}
				}
			}
			/* ior_expr ==> set_expr when x | y is const */
			if(parameter instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) parameter).get_constant()));
				if(!this.extend_at(new SedSetExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)))) {
					return false;
				}
			}
			/* ior_expr ==> ext_expr */
			return this.extend_at(new SedExtExpressionError(statement, expression));
		}
		else if(this.is_long(expression)) {
			/* ior_expr ==> equivalent when x == ~0 */
			if(loperand instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) loperand).get_constant()));
				if(value == ~0) {
					return false;
				}
			}
			/* ior_expr ==> set_expr when y is const */
			if(roperand instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) roperand).get_constant()));
				if(value == 0) {
					return false;
				}
				else if(value == ~0) {
					if(!this.extend_at(new SedSetExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)))) {
						return false;
					}
				}
			}
			/* ior_expr ==> set_expr when x | y is const */
			if(parameter instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) parameter).get_constant()));
				if(!this.extend_at(new SedSetExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)))) {
					return false;
				}
			}
			/* ior_expr ==> ext_expr */
			return this.extend_at(new SedExtExpressionError(statement, expression));
		}
		else {
			throw new IllegalArgumentException("Invalid expression: " + expression.generate_code(true));
		}
	}
	private boolean extend_xor_expression(SedIorExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_statement().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		SedExpression roperand = this.evaluate(muta_expression);
		SedExpression parameter = this.bitws_xor(orig_expression, muta_expression);
		if(this.is_char(expression)) {
			/* xor_expr ==> rsv_expr {roperand is const} */
			if(roperand instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) roperand).get_constant()));
				if(value == 0) {
					return false;
				}
				else if(value == ~0) {
					if(!this.extend_at(new SedRsvExpressionError(statement, expression))) {
						return false;
					}
				}
			}
			/* xor_expr ==> set_expr when parameter is const */
			if(parameter instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) parameter).get_constant()));
				if(!this.extend_at(new SedSetExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)))) {
					return false;
				}
			}
			/* xor_expr ==> chg_expr */
			return this.extend_at(new SedChgExpressionError(statement, expression));
		}
		else if(this.is_long(expression)) {
			/* xor_expr ==> rsv_expr {roperand is const} */
			if(roperand instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) roperand).get_constant()));
				if(value == 0) {
					return false;
				}
				else if(value == ~0) {
					if(!this.extend_at(new SedRsvExpressionError(statement, expression))) {
						return false;
					}
				}
			}
			/* xor_expr ==> set_expr when parameter is const */
			if(parameter instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) parameter).get_constant()));
				if(!this.extend_at(new SedSetExpressionError(statement, expression, (SedExpression) SedFactory.sed_node(value)))) {
					return false;
				}
			}
			/* xor_expr ==> chg_expr */
			return this.extend_at(new SedChgExpressionError(statement, expression));
		}
		else {
			throw new IllegalArgumentException("Invalid expression: " + expression.generate_code(true));
		}
	}
	private boolean extend_bin_expression(SedConBinExpressionError source) throws Exception {
		if(source instanceof SedSetExpressionError)
			return this.extend_set_expression((SedSetExpressionError) source);
		else if(source instanceof SedAddExpressionError)
			return this.extend_add_expression((SedAddExpressionError) source);
		else if(source instanceof SedMulExpressionError)
			return this.extend_mul_expression((SedMulExpressionError) source);
		else if(source instanceof SedAndExpressionError)
			return this.extend_and_expression((SedAndExpressionError) source);
		else if(source instanceof SedIorExpressionError)
			return this.extend_ior_expression((SedIorExpressionError) source);
		else if(source instanceof SedXorExpressionError)
			return this.extend_xor_expression((SedIorExpressionError) source);
		else
			throw new IllegalArgumentException("Invalid source: " + source.generate_code());
	}
	
	/* TODO implement unary concrete expression error */
	
}
