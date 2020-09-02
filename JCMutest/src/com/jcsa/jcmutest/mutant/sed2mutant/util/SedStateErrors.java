package com.jcsa.jcmutest.mutant.sed2mutant.util;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.SedExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.SedStateError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.abs.SedAbstExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.abs.SedAddExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.abs.SedInsExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.abs.SedSetExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.adr.SedAddAddrExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.adr.SedSetAddrExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.bol.SedNotBoolExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.bol.SedSetBoolExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.byt.SedSetByteExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedAddCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedAndCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedIorCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedMulCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedNegCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedRsvCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedSetCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedXorCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedAddLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedAndLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedIorLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedMulLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedNegLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedRsvLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedSetLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedXorLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.rea.SedAddRealExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.rea.SedMulRealExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.rea.SedNegRealExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.rea.SedSetRealExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.stm.SedAddStatementError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.stm.SedDelStatementError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.stm.SedSetStatementError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.stm.SedStatementError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedBinaryExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedConstant;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedUnaryExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It provides interfaces to create SedStateError and generate extension set.
 * 
 * @author yukimula
 *
 */
public class SedStateErrors {
	
	/* definition */
	private SedEvaluator evaluator;
	private Map<String, SedStateError> errors;
	public SedStateErrors() {
		this.evaluator = new SedEvaluator();
		this.errors = new HashMap<String, SedStateError>();
	}
	
	/* type verifications */
	private boolean is_void(CirExpression expression) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		return CTypeAnalyzer.is_void(data_type);
	}
	private boolean is_bool(CirExpression expression) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(CTypeAnalyzer.is_boolean(data_type)) {
			return true;
		}
		else {
			CirStatement statement = expression.statement_of();
			return statement instanceof CirIfStatement || 
					statement instanceof CirCaseStatement;
		}
	}
	private boolean is_char(CirExpression expression) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		return CTypeAnalyzer.is_character(data_type);
	}
	private boolean is_long(CirExpression expression) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		return CTypeAnalyzer.is_integer(data_type);
	}
	private boolean is_real(CirExpression expression) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		return CTypeAnalyzer.is_real(data_type);
	}
	private boolean is_addr(CirExpression expression) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		return CTypeAnalyzer.is_pointer(data_type);
	}
	
	/* constant getters */
	private boolean get_bool(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:		return constant.get_bool().booleanValue();
		case c_char:
		case c_uchar:		return constant.get_char().charValue() != '\0';
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
		default: throw new IllegalArgumentException("Unsupport: " + constant);
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
		default: throw new IllegalArgumentException("Unsupport: " + constant);
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
		default: throw new IllegalArgumentException("Unsupport: " + constant);
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
		default: throw new IllegalArgumentException("Unsupport: " + constant);
		}
	}
	
	/* data getters */
	public void set_eval_scope(SedEvalScope scope) {
		this.evaluator.set_context(scope);
	}
	/**
	 * @param error
	 * @return the unique state error in the space w.r.t. the source error
	 * @throws Exception
	 */
	private SedStateError get_unique_error(SedStateError error) throws Exception {
		if(error == null) {
			return null;
		}
		else {
			String key = error.generate_code();
			if(!this.errors.containsKey(key)) 
				this.errors.put(key, error);
			return this.errors.get(key);
		}
	}
	
	/* statement error creation */
	public SedStatementError add_statement(CirStatement statement) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else {
			return (SedStatementError) this.get_unique_error(new SedAddStatementError(statement, statement));
		}
	}
	public SedStatementError del_statement(CirStatement statement) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else {
			return (SedStatementError) this.get_unique_error(new SedDelStatementError(statement, statement));
		}
	}
	public SedStatementError set_statement(CirStatement orig_statement,
			CirStatement muta_statement) throws Exception {
		if(orig_statement == null)
			throw new IllegalArgumentException("Invalid orig_statement: null");
		else if(muta_statement == null)
			throw new IllegalArgumentException("Invalid muta_statement: null");
		else {
			return (SedStatementError) this.get_unique_error(new SedSetStatementError(
					orig_statement, orig_statement, muta_statement));
		}
	}
	
	/* abstract expression error */
	public SedExpressionError ins_expression(CirStatement statement, 
			CirExpression expression, COperator operator) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else if(expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else {
			switch(operator) {
			case negative:
			{
				if(this.is_long(expression) || this.is_real(expression)) {
					break;
				}
				else {
					throw new IllegalArgumentException("Invalid: " + expression.generate_code(true));
				}
			}
			case bit_not:
			{
				if(this.is_long(expression)) {
					break;
				}
				else {
					throw new IllegalArgumentException("Invalid: " + expression.generate_code(true));
				}
			}
			case logic_not:
			{
				if(this.is_bool(expression)) {
					break;
				}
				else {
					throw new IllegalArgumentException("Invalid: " + expression.generate_code(true));
				}
			}
			default: throw new IllegalArgumentException("Invalid operator: " + operator);
			}
			return (SedExpressionError) this.get_unique_error(new SedInsExpressionError(
					statement, (SedExpression) SedParser.parse(expression), operator));
		}
	}
	public SedExpressionError add_expression(CirStatement statement,
			CirExpression expression, COperator operator, SedExpression operand) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else if(expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else if(operand == null)
			throw new IllegalArgumentException("Invalid operand: null");
		else {
			switch(operator) {
			case arith_add:
			case arith_sub:
			{
				if(this.is_char(expression) || this.is_long(expression) || this.is_real(expression) || this.is_addr(expression)) {
					break;
				}
				else {
					throw new IllegalArgumentException("Invalid expression: " + expression.generate_code(true));
				}
			}
			case arith_mul:
			case arith_div:
			{
				if(this.is_char(expression) || this.is_long(expression) || this.is_real(expression)) {
					break;
				}
				else {
					throw new IllegalArgumentException("Invalid expression: " + expression.generate_code(true));
				}
			}
			case arith_mod:
			case bit_and:
			case bit_or:
			case bit_xor:
			case left_shift:
			case righ_shift:
			{
				if(this.is_char(expression) || this.is_long(expression)) {
					break;
				}
				else {
					throw new IllegalArgumentException("Invalid expression: " + expression.generate_code(true));
				}
			}
			case logic_and:
			case logic_or:
			{
				if(this.is_bool(expression)) {
					break;
				}
				else {
					throw new IllegalArgumentException("Invalid expression: " + expression.generate_code(true));
				}
			}
			default: throw new IllegalArgumentException("Invalid operator: " + operator);
			}
			return (SedExpressionError) this.get_unique_error(new SedAddExpressionError(
					statement, (SedExpression) SedParser.parse(expression), operator, operand));
		}
	}
	public SedExpressionError set_expression(CirStatement statement,
			CirExpression orig_expression, SedExpression muta_expression) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else if(orig_expression == null)
			throw new IllegalArgumentException("Invalid orig_expression: null");
		else if(muta_expression == null)
			throw new IllegalArgumentException("Invalid muta_expression: null");
		else if(this.is_void(orig_expression))
			throw new IllegalArgumentException("Invalid: " + orig_expression.generate_code(true));
		else {
			return (SedExpressionError) this.get_unique_error(new SedSetExpressionError(
					statement, (SedExpression) SedParser.parse(orig_expression), muta_expression));
		}
	}
	
	/* concrete expression initializer */
	/**
	 * @param source
	 * @return the initial concrete error from the abstract error or null if the source is equivalent.
	 * @throws Exception
	 */
	protected SedExpressionError init_abs_expr(SedAbstExpressionError source) throws Exception {
		if(source instanceof SedInsExpressionError) {
			return this.abs_ins_expr((SedInsExpressionError) source);
		}
		else if(source instanceof SedSetExpressionError) {
			return this.abs_set_expr((SedSetExpressionError) source);
		}
		else if(source instanceof SedAddExpressionError) {
			return this.abs_add_expr((SedAddExpressionError) source);
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	private SedExpressionError abs_ins_expr(SedInsExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		CirExpression expression = (CirExpression) source.get_orig_expression().get_cir_source();
		
		switch(source.get_ins_operator().get_operator()) {
		case negative:	
		{
			SedExpressionError error;
			if(this.is_char(expression)) {
				error = new SedNegCharExpressionError(statement, source.get_orig_expression());
			}
			else if(this.is_long(expression)) {
				error = new SedNegLongExpressionError(statement, source.get_orig_expression());
			}
			else if(this.is_real(expression)) {
				error = new SedNegRealExpressionError(statement, source.get_orig_expression());
			}
			else {
				throw new IllegalArgumentException("Invalid: " + expression.generate_code(true));
			}
			return (SedExpressionError) this.get_unique_error(error);
		}
		case bit_not:
		{
			SedExpressionError error;
			if(this.is_char(expression)) {
				error = new SedRsvCharExpressionError(statement, source.get_orig_expression());
			}
			else if(this.is_long(expression)) {
				error = new SedRsvLongExpressionError(statement, source.get_orig_expression());
			}
			else {
				throw new IllegalArgumentException("Invalid: " + expression.generate_code(true));
			}
			return (SedExpressionError) this.get_unique_error(error);
		}
		case logic_not:
		{
			SedExpressionError error;
			if(this.is_bool(expression)) {
				error = new SedNotBoolExpressionError(statement, source.get_orig_expression());
			}
			else {
				throw new IllegalArgumentException("Invalid: " + expression.generate_code(true));
			}
			return (SedExpressionError) this.get_unique_error(error);
		}
		default: throw new IllegalArgumentException(source.get_ins_operator().generate_code());
		}
	}
	private SedExpressionError abs_set_expr(SedSetExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		CirExpression expression = (CirExpression) source.get_orig_expression().get_cir_source();
		SedExpression orig_expression = source.get_orig_expression(), muta_expression;
		muta_expression = (SedExpression) this.evaluator.evaluate(source.get_muta_expression());
		
		SedExpressionError error;
		if(this.is_bool(expression)) {
			if(muta_expression instanceof SedConstant) {
				Boolean value = Boolean.valueOf(this.get_bool(((SedConstant) muta_expression).get_constant()));
				error = new SedSetBoolExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else {
				error = new SedSetBoolExpressionError(statement, orig_expression, source.get_muta_expression());
			}
		}
		else if(this.is_char(expression)) {
			if(muta_expression instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) muta_expression).get_constant()));
				error = new SedSetCharExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else {
				error = new SedSetCharExpressionError(statement, orig_expression, source.get_muta_expression());
			}
		}
		else if(this.is_long(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				error = new SedSetLongExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else {
				error = new SedSetLongExpressionError(statement, orig_expression, source.get_muta_expression());
			}
		}
		else if(this.is_real(expression)) {
			if(muta_expression instanceof SedConstant) {
				Double value = Double.valueOf(this.get_real(((SedConstant) muta_expression).get_constant()));
				error = new SedSetRealExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else {
				error = new SedSetRealExpressionError(statement, orig_expression, source.get_muta_expression());
			}
		}
		else if(this.is_addr(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				error = new SedSetAddrExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else {
				error = new SedSetAddrExpressionError(statement, orig_expression, source.get_muta_expression());
			}
		}
		else {
			error = new SedSetByteExpressionError(statement, orig_expression, source.get_muta_expression());
		}
		return (SedExpressionError) this.get_unique_error(error);
	}
	private SedExpressionError abs_add_expr(SedAddExpressionError source) throws Exception {
		SedExpressionError error;
		switch(source.get_add_operator().get_operator()) {
		case arith_add:	error = this.abs_arith_add_expr(source); break;
		case arith_sub:	error = this.abs_arith_sub_expr(source); break;
		case arith_mul:	error = this.abs_arith_mul_expr(source); break;
		case bit_and:	error = this.abs_bitws_and_expr(source); break;
		case bit_or:	error = this.abs_bitws_ior_expr(source); break;
		case bit_xor:	error = this.abs_bitws_xor_expr(source); break;
		case logic_and:	error = this.abs_logic_and_expr(source); break;
		case logic_or:	error = this.abs_logic_ior_expr(source); break;
		default:		error = this.abs_add_default_expr(source); break;
		}
		return (SedExpressionError) this.get_unique_error(error);
	}
	private SedExpressionError abs_add_default_expr(SedAddExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression(), muta_expression;
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		muta_expression = new SedBinaryExpression(null, expression.
				get_data_type(), source.get_add_operator().get_operator());
		muta_expression.add_child(orig_expression); 
		muta_expression.add_child(source.get_add_operand());
		SedExpressionError error = new SedSetExpressionError(
					statement, orig_expression, muta_expression);
		return this.abs_set_expr((SedSetExpressionError) error);
	}
	private SedExpressionError abs_arith_add_expr(SedAddExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression(), muta_expression;
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		muta_expression = (SedExpression) this.evaluator.evaluate(source.get_add_operand());
		if(this.is_char(expression)) {
			if(muta_expression instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) muta_expression).get_constant()));
				if(value == 0) return null;	/* being equivalent with the original program under the same test case */
				return new SedAddCharExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else {
				return new SedAddCharExpressionError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_long(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				if(value == 0) return null;	/* being equivalent with the original program under the same test case */
				return new SedAddLongExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else {
				return new SedAddLongExpressionError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_real(expression)) {
			if(muta_expression instanceof SedConstant) {
				Double value = Double.valueOf(this.get_real(((SedConstant) muta_expression).get_constant()));
				if(value == 0) return null;	/* being equivalent with the original program under the same test case */
				return new SedAddRealExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else {
				return new SedAddRealExpressionError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_addr(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				if(value == 0) return null;	/* being equivalent with the original program under the same test case */
				return new SedAddAddrExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else {
				return new SedAddAddrExpressionError(statement, orig_expression, muta_expression);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.generate_code(true));
		}
	}
	private SedExpressionError abs_arith_sub_expr(SedAddExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression(), muta_expression;
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		muta_expression = new SedUnaryExpression(null, expression.get_data_type(), COperator.negative);
		muta_expression.add_child(source.get_add_operand());
		muta_expression = (SedExpression) this.evaluator.evaluate(muta_expression);
		
		if(this.is_char(expression)) {
			if(muta_expression instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) muta_expression).get_constant()));
				if(value == 0) return null;	/* being equivalent with the original program under the same test case */
				return new SedAddCharExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else {
				return new SedAddCharExpressionError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_long(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				if(value == 0) return null;	/* being equivalent with the original program under the same test case */
				return new SedAddLongExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else {
				return new SedAddLongExpressionError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_real(expression)) {
			if(muta_expression instanceof SedConstant) {
				Double value = Double.valueOf(this.get_real(((SedConstant) muta_expression).get_constant()));
				if(value == 0) return null;	/* being equivalent with the original program under the same test case */
				return new SedAddRealExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else {
				return new SedAddRealExpressionError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_addr(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				if(value == 0) return null;	/* being equivalent with the original program under the same test case */
				return new SedAddAddrExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else {
				return new SedAddAddrExpressionError(statement, orig_expression, muta_expression);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.generate_code(true));
		}
	}
	private SedExpressionError abs_arith_mul_expr(SedAddExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression(), muta_expression;
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		muta_expression = (SedExpression) this.evaluator.evaluate(source.get_add_operand());
		if(this.is_char(expression)) {
			if(muta_expression instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) muta_expression).get_constant()));
				if(value == 0) {	/* set_char(expr, 0) */
					return new SedSetCharExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
				else if(value == 1) {	/* equivalent mutation since x == x * 1 */	return null; }
				else if(value == -1) {	/* neg_char(expr) */
					return new SedNegCharExpressionError(statement, orig_expression);
				}
				else {					/* mul_char(expr, value) */
					return new SedMulCharExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
			}
			else {
				return new SedMulCharExpressionError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_long(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				if(value == 0) {	/* set_long(expr, 0) */
					return new SedSetLongExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
				else if(value == 1) {	/* equivalent mutation since x == x * 1 */	return null; }
				else if(value == -1) {	/* neg_long(expr) */
					return new SedNegLongExpressionError(statement, orig_expression);
				}
				else {					/* mul_long(expr, value) */
					return new SedMulLongExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
			}
			else {
				return new SedMulLongExpressionError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_real(expression)) {
			if(muta_expression instanceof SedConstant) {
				Double value = Double.valueOf(this.get_real(((SedConstant) muta_expression).get_constant()));
				if(value == 0) {	/* set_real(expr, 0) */
					return new SedSetRealExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
				else if(value == 1) {	/* equivalent mutation since x == x * 1 */	return null; }
				else if(value == -1) {	/* neg_real(expr) */
					return new SedNegRealExpressionError(statement, orig_expression);
				}
				else {					/* mul_real(expr, value) */
					return new SedMulRealExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
			}
			else {
				return new SedMulRealExpressionError(statement, orig_expression, muta_expression);
			}
		}
		else {
			throw new IllegalArgumentException(expression.generate_code(true));
		}
	}
	private SedExpressionError abs_bitws_and_expr(SedAddExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression(), muta_expression;
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		muta_expression = (SedExpression) this.evaluator.evaluate(source.get_add_operand());
		if(this.is_char(expression)) {
			if(muta_expression instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) muta_expression).get_constant()));
				if(value == 0) {
					return new SedSetCharExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
				else if(value == ~0) { /* equivalent mutation since x == x & ~0 */ return null; }
				else {
					return new SedAndCharExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
			}
			else {
				return new SedAndCharExpressionError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_long(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				if(value == 0) {
					return new SedSetLongExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
				else if(value == ~0) { /* equivalent mutation since x == x & ~0 */ return null; }
				else {
					return new SedAndLongExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
			}
			else {
				return new SedAndLongExpressionError(statement, orig_expression, muta_expression);
			}
		}
		else {
			throw new IllegalArgumentException(expression.generate_code(true));
		}
	}
	private SedExpressionError abs_bitws_ior_expr(SedAddExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression(), muta_expression;
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		muta_expression = (SedExpression) this.evaluator.evaluate(source.get_add_operand());
		if(this.is_char(expression)) {
			if(muta_expression instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) muta_expression).get_constant()));
				if(value == ~0) {
					return new SedSetCharExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
				else if(value == 0) { /* equivalent mutation since x == x & ~0 */ return null; }
				else {
					return new SedIorCharExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
			}
			else {
				return new SedIorCharExpressionError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_long(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				if(value == ~0) {
					return new SedSetLongExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
				else if(value == 0) { /* equivalent mutation since x == x & ~0 */ return null; }
				else {
					return new SedIorLongExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
			}
			else {
				return new SedIorLongExpressionError(statement, orig_expression, muta_expression);
			}
		}
		else {
			throw new IllegalArgumentException(expression.generate_code(true));
		}
	}
	private SedExpressionError abs_bitws_xor_expr(SedAddExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression(), muta_expression;
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		muta_expression = (SedExpression) this.evaluator.evaluate(source.get_add_operand());
		if(this.is_char(expression)) {
			if(muta_expression instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) muta_expression).get_constant()));
				if(value == 0) { /* equivalent mutation since x == x ^ 0 */ return null; }
				else if(value == ~0) {
					return new SedRsvCharExpressionError(statement, orig_expression);
				}
				else {
					return new SedXorCharExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
			}
			else {
				return new SedXorCharExpressionError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_long(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				if(value == 0) { /* equivalent mutation since x == x ^ 0 */ return null; }
				else if(value == ~0) {
					return new SedRsvLongExpressionError(statement, orig_expression);
				}
				else {
					return new SedXorLongExpressionError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
			}
			else {
				return new SedXorLongExpressionError(statement, orig_expression, muta_expression);
			}
		}
		else {
			throw new IllegalArgumentException(expression.generate_code(true));
		}
	}
	private SedExpressionError abs_logic_and_expr(SedAddExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression(), muta_expression;
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		muta_expression = (SedExpression) this.evaluator.evaluate(source.get_add_operand());
		if(this.is_bool(expression)) {
			if(muta_expression instanceof SedConstant) {
				if(this.get_bool(((SedConstant) muta_expression).get_constant())) {
					/* equivalent mutation since x == x && true */	return null;
				}
				else {
					return new SedSetBoolExpressionError(statement, orig_expression, 
								(SedExpression) SedFactory.sed_node(Boolean.FALSE));
				}
			}
			else {
				return this.abs_add_default_expr(source);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.generate_code(true));
		}
	}
	private SedExpressionError abs_logic_ior_expr(SedAddExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression(), muta_expression;
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		muta_expression = (SedExpression) this.evaluator.evaluate(source.get_add_operand());
		if(this.is_bool(expression)) {
			if(muta_expression instanceof SedConstant) {
				if(!this.get_bool(((SedConstant) muta_expression).get_constant())) {
					/* equivalent mutation since x == x || false */	return null;
				}
				else {
					return new SedSetBoolExpressionError(statement, orig_expression, 
								(SedExpression) SedFactory.sed_node(Boolean.TRUE));
				}
			}
			else {
				return this.abs_add_default_expr(source);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.generate_code(true));
		}
	}
	
	
}
