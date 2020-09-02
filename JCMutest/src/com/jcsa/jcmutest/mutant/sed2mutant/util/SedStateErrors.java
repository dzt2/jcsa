package com.jcsa.jcmutest.mutant.sed2mutant.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.SedExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.SedStateError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.abs.SedAbstExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.abs.SedAddExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.abs.SedInsExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.abs.SedSetExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.adr.SedAddAddrExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.adr.SedAddrExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.adr.SedChgAddrExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.adr.SedDecAddrExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.adr.SedIncAddrExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.adr.SedSetAddrExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.bol.SedBoolExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.bol.SedNotBoolExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.bol.SedSetBoolExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.byt.SedByteExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.byt.SedChgByteExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.byt.SedSetByteExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedAddCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedAndCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedChgCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedDecCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedExtCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedIncCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedIorCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedMulCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedNegCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedRsvCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedSetCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedShkCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha.SedXorCharExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedAddLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedAndLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedChgLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedDecLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedExtLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedIncLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedIorLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedMulLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedNegLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedRsvLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedSetLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedShkLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon.SedXorLongExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.rea.SedAddRealExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.rea.SedChgRealExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.rea.SedDecRealExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.rea.SedExtRealExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.rea.SedIncRealExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.rea.SedMulRealExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.rea.SedNegRealExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.rea.SedRealExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.rea.SedSetRealExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.rea.SedShkRealExpressionError;
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
	private Set<SedStateError> extension_set;
	public SedStateErrors() {
		this.evaluator = new SedEvaluator();
		this.errors = new HashMap<String, SedStateError>();
		this.extension_set = new HashSet<SedStateError>();
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
	/**
	 * @param orig_expr
	 * @param muta_expr
	 * @return orig_expr == muta_expr
	 * @throws Exception
	 */
	private SedExpression eqv(SedExpression orig_expr, SedExpression muta_expr) throws Exception {
		SedExpression difference = new SedBinaryExpression(null, 
				orig_expr.get_data_type(), COperator.equal_with);
		difference.add_child(orig_expr); difference.add_child(muta_expr);
		return (SedExpression) this.evaluator.evaluate(difference);
	}
	/**
	 * @param orig_expr
	 * @param muta_expr
	 * @return muta_expr - orig_expr
	 * @throws Exception
	 */
	private SedExpression dif(SedExpression orig_expr, SedExpression muta_expr) throws Exception {
		SedExpression difference = new SedBinaryExpression(null, 
				orig_expr.get_data_type(), COperator.arith_sub);
		difference.add_child(muta_expr); difference.add_child(orig_expr);
		return (SedExpression) this.evaluator.evaluate(difference);
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
	private SedExpressionError init_abs_expr(SedAbstExpressionError source) throws Exception {
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
	
	/* concrete expression error extensions */
	/**
	 * @param source
	 * @return the extension set of the state errors generated from the source
	 * @throws Exception
	 */
	protected Set<SedStateError> extend(SedStateError source) throws Exception {
		this.extension_set.clear();
		if(source instanceof SedStatementError) {
			Set<SedStateError> results = new HashSet<SedStateError>();
			results.add(source);
			return results;
		}
		else if(this.extend_at((SedExpressionError) source)) {
			Set<SedStateError> results = new HashSet<SedStateError>();
			results.addAll(this.extension_set);
			return results;
		}
		else {
			return null;
		}
	}
	/**
	 * @param source
	 * @return generate the extension set of the source error or return false
	 *         to call the users that the error is non-observable.
	 * @throws Exception
	 */
	private boolean extend_at(SedExpressionError source) throws Exception {
		source = (SedExpressionError) this.get_unique_error(source);
		if(source == null) {
			return false;	/* equivalent mutation proved */
		}
		else if(this.extension_set.contains(source)) {
			return true;	/* to avoid duplicated traversal */
		}
		else {
			this.extension_set.add(this.get_unique_error(source));
			if(source instanceof SedAbstExpressionError) {
				return this.extend_at(this.init_abs_expr((SedAbstExpressionError) source));
			}
			else if(source instanceof SedBoolExpressionError) {
				return this.extend_bool_error((SedBoolExpressionError) source);
			}
			else if(source instanceof SedCharExpressionError) {
				return this.extend_char_error((SedCharExpressionError) source);
			}
			else if(source instanceof SedLongExpressionError) {
				return this.extend_long_error((SedLongExpressionError) source);
			}
			else if(source instanceof SedRealExpressionError) {
				return this.extend_real_error((SedRealExpressionError) source);
			}
			else if(source instanceof SedAddrExpressionError) {
				return this.extend_addr_error((SedAddrExpressionError) source);
			}
			else if(source instanceof SedByteExpressionError) {
				return this.extend_byte_error((SedByteExpressionError) source);
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + source);
			}
		}
	}
	/* boolean expression error extensions */
	/**
	 * @param source
	 * @return set_bool(expr, true|false|expr) --> not_bool
	 * @throws Exception
	 */
	private boolean extend_set_bool(SedSetBoolExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		SedExpression difference = this.eqv(orig_expression, muta_expression);
		if(difference instanceof SedConstant) {
			if(this.get_bool(((SedConstant) difference).get_constant())) {
				/* equivalent mutation: orig_expr == muta_expr */ return false;
			}
			else {
				return this.extend_at(new SedNotBoolExpressionError(statement, orig_expression));
			}
		}
		else {
			return this.extend_at(new SedNotBoolExpressionError(statement, orig_expression));
		}
	}
	/**
	 * @param source
	 * @return not_bool ~~> set_bool(expr, const)
	 * @throws Exception
	 */
	private boolean extend_not_bool(SedNotBoolExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		orig_expression = (SedExpression) this.evaluator.evaluate(orig_expression);
		if(orig_expression instanceof SedConstant) {
			Boolean value = Boolean.valueOf(this.get_bool(((SedConstant) orig_expression).get_constant()));
			return this.extend_at(new SedSetBoolExpressionError(
					statement, source.get_orig_expression(), (SedExpression) SedFactory.sed_node(value)));
		}
		else {
			return true;
		}
	}
	private boolean extend_bool_error(SedBoolExpressionError source) throws Exception {
		if(source instanceof SedSetBoolExpressionError) {
			return this.extend_set_bool((SedSetBoolExpressionError) source);
		}
		else if(source instanceof SedNotBoolExpressionError) {
			return this.extend_not_bool((SedNotBoolExpressionError) source);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + source.generate_code());
		}
	}
	/* character expression error extensions */
	/**
	 * @param source
	 * @return 	set_char ~~> neg_char|rsv_char	(const, const)
	 * 			set_char ~~> add_char			{difference: const}
	 * 			set_char --> chg_char
	 * @throws Exception
	 */
	private boolean extend_set_char(SedSetCharExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		
		/* loperand, roperand, difference */
		SedExpression loperand = (SedExpression) this.evaluator.evaluate(orig_expression);
		SedExpression roperand = (SedExpression) this.evaluator.evaluate(muta_expression);
		SedExpression difference = this.dif(orig_expression, muta_expression);
		
		/* (loperand: const, roperand: const) ==> neg_char | rsv_char */
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			char x = this.get_char(((SedConstant) loperand).get_constant());
			char y = this.get_char(((SedConstant) roperand).get_constant());
			if(x == y) { /* equivalent mutation if x == y */ return false; }
			else if(x == -y) {
				if(!this.extend_at(new SedNegCharExpressionError(statement, orig_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
			else if(x == ~y) {
				if(!this.extend_at(new SedRsvCharExpressionError(statement, orig_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
		}
		
		/* (difference: const) */
		if(difference instanceof SedConstant) {
			if(!this.extend_at(new SedAddCharExpressionError(statement, orig_expression, difference))) {
				return false; 	/* failed to extend recursively */
			}
		}
		
		return this.extend_at(new SedChgCharExpressionError(statement, orig_expression));
	}
	/**
	 * @param source
	 * @return	add_char ==> set_char ==> chg_char			{orig + muta: const}
	 * 			add_char ==> inc_char|dec_char ==> chg_char {difference: const}
	 * 			add_char ==> chg_char						{otherwise}
	 * @throws Exception
	 */
	private boolean extend_add_char(SedAddCharExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		
		/* add_char ==> set_char {orig + muta: const} */
		SedExpression parameter = SedFactory.arith_add(
				orig_expression.get_data_type(), 
				orig_expression, muta_expression);
		if(parameter instanceof SedConstant) {
			Character value = Character.valueOf(get_char(((SedConstant) parameter).get_constant()));
			if(!this.extend_at(new SedSetCharExpressionError(
					statement, orig_expression, (SedExpression) SedFactory.sed_node(value)))) {
				/* failed to solve the extension recursively */	return false;
			}
		}
		
		/* add_char ==> inc_char|dec_char ==> chg_char {difference: const} */
		SedExpression difference = (SedExpression) this.evaluator.evaluate(muta_expression);
		if(difference instanceof SedConstant) {
			char value = this.get_char(((SedConstant) difference).get_constant());
			if(value > 0) {
				if(!this.extend_at(new SedIncCharExpressionError(statement, orig_expression))) {
					/* failed to solve the extension recursively */	return false;
				}
			}
			else if(value < 0) {
				if(!this.extend_at(new SedDecCharExpressionError(statement, orig_expression))) {
					/* failed to solve the extension recursively */	return false;
				}
			}
			else {
				/* equivalent mutation if orig_expr == muta_expr */	return false;
			}
		}
		
		/* otherwise, chg_char(expr) */
		return this.extend_at(new SedChgCharExpressionError(statement, orig_expression));
	}
	/**
	 * @param source
	 * @return	mul_char ==> set_char {x * y: const}
	 * 			mul_char ==> set_char|neg_char|grw_char|shk_char (y: const)
	 * 			mul_char ==> chg_char
	 * @throws Exception
	 */
	private boolean extend_mul_char(SedMulCharExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		
		/* mul_char ==> set_char {x * y: const} */
		SedExpression parameter = SedFactory.arith_mul(orig_expression.
					get_data_type(), orig_expression, muta_expression);
		parameter = (SedExpression) this.evaluator.evaluate(parameter);
		if(parameter instanceof SedConstant) {
			Character value = Character.valueOf(get_char(((SedConstant) parameter).get_constant()));
			if(!this.extend_at(new SedSetCharExpressionError(
						statement, orig_expression, (SedExpression) SedFactory.sed_node(value)))) {
				/* failed to solve the error recursively */	return false;
			}
		}
		
		/* mul_char ==> set_char|grw_char|shk_char (y: const) */
		parameter = (SedExpression) this.evaluator.evaluate(muta_expression);
		if(parameter instanceof SedConstant) {
			Character value = Character.valueOf(this.get_char(((SedConstant) parameter).get_constant()));
			if(value == 0) {
				if(!this.extend_at(new SedSetCharExpressionError(
						statement, orig_expression, (SedExpression) SedFactory.sed_node(value)))) {
					/* failed to solve the error recursively */	return false;
				}
			}
			else if(value == 1) {
				/* equivalent mutation since x == x * 1 */	return false;
			}
			else if(value == -1) {
				if(!this.extend_at(new SedNegCharExpressionError(statement, orig_expression))) {
					/* failed to solve the error recursively */	return false;
				}
			}
			else {
				if(!this.extend_at(new SedExtCharExpressionError(statement, orig_expression))) {
					/* failed to solve the error recursively */	return false;
				}
			}
		}
		
		return this.extend_at(new SedChgCharExpressionError(statement, orig_expression));
	}
	/**
	 * @param source
	 * @return	and_char ==> set_char {x & y : const}
	 * 			and_char ==> set_char|shk_char	{y: const}
	 * 			and_char ==> chg_char
	 * @throws Exception
	 */
	private boolean extend_and_char(SedAndCharExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		
		/* and_char ==> set_char {x & y : const} */
		SedExpression parameter = SedFactory.bitws_and(orig_expression.
					get_data_type(), orig_expression, muta_expression);
		parameter = (SedExpression) this.evaluator.evaluate(parameter);
		if(parameter instanceof SedConstant) {
			Character value = Character.valueOf(get_char(((SedConstant) parameter).get_constant()));
			if(!this.extend_at(new SedSetCharExpressionError(
					statement, orig_expression, (SedExpression) SedFactory.sed_node(value)))) {
				/* failed to solve the error recursively */	return false;
			}
		}
		
		/* and_char ==> set_char|shk_char	{y: const} */
		muta_expression = (SedExpression) this.evaluator.evaluate(muta_expression);
		if(muta_expression instanceof SedConstant) {
			Character value = Character.valueOf(get_char(((SedConstant) muta_expression).get_constant()));
			if(value == 0) {
				if(!this.extend_at(new SedSetCharExpressionError(
						statement, orig_expression, (SedExpression) SedFactory.sed_node(value)))) {
					/* failed to solve the error recursively */	return false;
				}
			}
			else if(value == ~0) {
				/* equivalent mutation since x == x & 111111...1111 */	return false;
			}
			else {
				if(!this.extend_at(new SedShkCharExpressionError(statement, orig_expression))) {
					/* failed to solve the error recursively */	return false;
				}
			}
		}
		
		return this.extend_at(new SedChgCharExpressionError(statement, orig_expression));
	}
	/**
	 * @param source
	 * @return	ior_char ==> set_char {x | y : const}
	 * 			ior_char ==> set_char|ext_char {y:char}
	 * 			and_char ==> chg_char
	 * @throws Exception
	 */
	private boolean extend_ior_char(SedIorCharExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		
		/* ior_char ==> set_char {x | y : const} */
		SedExpression parameter = (SedExpression) this.evaluator.evaluate(SedFactory.
				bitws_ior(orig_expression.get_data_type(), orig_expression, muta_expression));
		if(parameter instanceof SedConstant) {
			Character value = Character.valueOf(this.get_char(((SedConstant) parameter).get_constant()));
			if(!this.extend_at(new SedSetCharExpressionError(
					statement, orig_expression, (SedExpression) SedFactory.sed_node(value)))) {
				/* failed to solve the error recursively */	return false;
			}
		}
		
		/* ior_char ==> set_char|ext_char {y:char} */
		muta_expression = (SedExpression) this.evaluator.evaluate(muta_expression);
		if(muta_expression instanceof SedConstant) {
			Character value = Character.valueOf(get_char(((SedConstant) muta_expression).get_constant()));
			if(value == 0) {
				/* equivalent mutation since x == x || 0 */	return false;
			}
			else if(value == ~0) {
				if(!this.extend_at(new SedSetCharExpressionError(
						statement, orig_expression, (SedExpression) SedFactory.sed_node(value)))) {
					/* failed to solve the error recursively */	return false;
				}
			}
			else {
				if(!this.extend_at(new SedExtCharExpressionError(statement, orig_expression))) {
					/* failed to solve the error recursively */	return false;
				}
			}
		}
		
		return this.extend_at(new SedChgCharExpressionError(statement, orig_expression));
	}
	/**
	 * @param source
	 * @return 	xor_char --> set_char	[x^y:const]
	 * 			xor_char --> rsv_char	[y: const]
	 * 			xor_char --> chg_char
	 * @throws Exception
	 */
	private boolean extend_xor_char(SedXorCharExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		
		/* xor_char --> set_char	[x^y:const] */
		SedExpression parameter = (SedExpression) this.evaluator.evaluate(SedFactory.
				bitws_xor(orig_expression.get_data_type(), orig_expression, muta_expression));
		if(parameter instanceof SedConstant) {
			Character value = Character.valueOf(get_char(((SedConstant) parameter).get_constant()));
			if(!this.extend_at(new SedSetCharExpressionError(
					statement, orig_expression, (SedExpression) SedFactory.sed_node(value)))) {
				/* failed to solve the error recursively */	return false;
			}
		}
		
		/* xor_char --> rsv_char	[y: const] */
		muta_expression = (SedExpression) this.evaluator.evaluate(muta_expression);
		if(muta_expression instanceof SedConstant) {
			Character value = Character.valueOf(get_char(((SedConstant) muta_expression).get_constant()));
			if(value == 0) {
				/* equivalent mutation since x == x ^ 0 */	return false;
			}
			else if(value == ~0) {
				if(!this.extend_at(new SedRsvCharExpressionError(statement, orig_expression))) {
					/* failed to solve the error recursively */	return false;
				}
			}
		}
		
		return this.extend_at(new SedChgCharExpressionError(statement, orig_expression));
	}
	/**
	 * @param source
	 * @return 	neg_char --> set_char [x: const]
	 * 			neg_char --> chg_char
	 * @throws Exception
	 */
	private boolean extend_neg_char(SedNegCharExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		orig_expression = (SedExpression) this.evaluator.evaluate(orig_expression);
		
		if(orig_expression instanceof SedConstant) {
			Character value = Character.valueOf((char) -get_char(((SedConstant) orig_expression).get_constant()));
			if(!this.extend_at(new SedSetCharExpressionError(statement, 
					source.get_orig_expression(), (SedExpression) SedFactory.sed_node(value)))) {
				return false;
			}
		}
		
		return this.extend_at(new SedChgCharExpressionError(statement, source.get_orig_expression()));
	}
	/**
	 * @param source
	 * @return 	rsv_char --> set_char [x: const]
	 * 			rsv_char --> chg_char
	 * @throws Exception
	 */
	private boolean extend_rsv_char(SedRsvCharExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		orig_expression = (SedExpression) this.evaluator.evaluate(orig_expression);
		
		if(orig_expression instanceof SedConstant) {
			Character value = Character.valueOf((char) ~get_char(((SedConstant) orig_expression).get_constant()));
			if(!this.extend_at(new SedSetCharExpressionError(statement, 
					source.get_orig_expression(), (SedExpression) SedFactory.sed_node(value)))) {
				return false;
			}
		}
		
		return this.extend_at(new SedChgCharExpressionError(statement, source.get_orig_expression()));
	}
	private boolean extend_otr_char(SedCharExpressionError source) throws Exception {
		return this.extend_at(new SedChgCharExpressionError(
				source.get_location().get_cir_statement(), 
				source.get_orig_expression()));
	}
	private boolean extend_char_error(SedCharExpressionError source) throws Exception {
		if(source instanceof SedSetCharExpressionError)
			return this.extend_set_char((SedSetCharExpressionError) source);
		else if(source instanceof SedAddCharExpressionError)
			return this.extend_add_char((SedAddCharExpressionError) source);
		else if(source instanceof SedMulCharExpressionError)
			return this.extend_mul_char((SedMulCharExpressionError) source);
		else if(source instanceof SedAndCharExpressionError)
			return this.extend_and_char((SedAndCharExpressionError) source);
		else if(source instanceof SedIorCharExpressionError) 
			return this.extend_ior_char((SedIorCharExpressionError) source);
		else if(source instanceof SedXorCharExpressionError)
			return this.extend_xor_char((SedXorCharExpressionError) source);
		else if(source instanceof SedNegCharExpressionError)
			return this.extend_neg_char((SedNegCharExpressionError) source);
		else if(source instanceof SedRsvCharExpressionError)
			return this.extend_rsv_char((SedRsvCharExpressionError) source);
		else
			return this.extend_otr_char(source);
	}
	/* integer expression error extensions */
	/**
	 * @param source
	 * @return 	set_long --> neg_long | rsv_long (const, const)
	 * 			set_long --> add_long (difference: const)
	 * 			set_long --> chg_long
	 * @throws Exception
	 */
	private boolean extend_set_long(SedSetLongExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		
		/* loperand, roperand, difference */
		SedExpression loperand = (SedExpression) this.evaluator.evaluate(orig_expression);
		SedExpression roperand = (SedExpression) this.evaluator.evaluate(muta_expression);
		SedExpression difference = this.dif(orig_expression, muta_expression);
		
		/* (loperand: const, roperand: const) ==> neg_char | rsv_char */
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			long x = this.get_long(((SedConstant) loperand).get_constant());
			long y = this.get_long(((SedConstant) roperand).get_constant());
			if(x == y) { /* equivalent mutation if x == y */ return false; }
			else if(x == -y) {
				if(!this.extend_at(new SedNegLongExpressionError(statement, orig_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
			else if(x == ~y) {
				if(!this.extend_at(new SedRsvLongExpressionError(statement, orig_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
		}
		
		/* (difference: const) */
		if(difference instanceof SedConstant) {
			if(!this.extend_at(new SedAddLongExpressionError(statement, orig_expression, difference))) {
				return false; 	/* failed to extend recursively */
			}
		}
		
		return this.extend_at(new SedChgLongExpressionError(statement, orig_expression));
	}
	/**
	 * @param source
	 * @return	add_long --> inc_long|dec_long 	(xxx, const)
	 * 			add_long --> set_long			(const, const)
	 * 			add_long --> chg_long
	 * @throws Exception
	 */
	private boolean extend_add_long(SedAddLongExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		
		/* loperand, roperand, difference */
		SedExpression loperand = (SedExpression) this.evaluator.evaluate(orig_expression);
		SedExpression roperand = (SedExpression) this.evaluator.evaluate(muta_expression);
		
		/* add_long --> inc_long|dec_long 	(xxx, const) */
		if(roperand instanceof SedConstant) {
			Long value = Long.valueOf(this.get_long(((SedConstant) roperand).get_constant()));
			if(value == 0) { /* equivalent mutation when x == y */	return false; }
			else if(value > 0) {
				if(!this.extend_at(new SedIncLongExpressionError(statement, orig_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
			else {
				if(!this.extend_at(new SedDecLongExpressionError(statement, orig_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
		}
		
		/* add_long --> set_long (const, const) */
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			Long x = Long.valueOf(this.get_long(((SedConstant) loperand).get_constant()));
			Long y = Long.valueOf(this.get_long(((SedConstant) roperand).get_constant()));
			muta_expression = (SedExpression) SedFactory.sed_node(Long.valueOf(x + y));
			if(!this.extend_at(new SedSetLongExpressionError(statement, orig_expression, muta_expression))) {
				return false;	/* failed to extend recursively */
			}
		}
		
		return this.extend_at(new SedChgLongExpressionError(statement, orig_expression));
	}
	/**
	 * @param source
	 * @return 	[const, const] --> set_long
	 * 			[xxxxx, const] --> set_long | neg_long | ext_long
	 * 			--> chg_long
	 * @throws Exception
	 */
	private boolean extend_mul_long(SedMulLongExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		
		/* loperand, roperand, difference */
		SedExpression loperand = (SedExpression) this.evaluator.evaluate(orig_expression);
		SedExpression roperand = (SedExpression) this.evaluator.evaluate(muta_expression);
		
		/* [const, const] --> set_long */
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			Long x = Long.valueOf(this.get_long(((SedConstant) loperand).get_constant()));
			Long y = Long.valueOf(this.get_long(((SedConstant) roperand).get_constant()));
			muta_expression = (SedExpression) SedFactory.sed_node(Long.valueOf(x * y));
			if(!this.extend_at(new SedSetLongExpressionError(statement, orig_expression, muta_expression))) {
				return false;	/* failed to extend recursively */
			}
		}
		
		/* [xxxxx, const] --> set_long | neg_long | ext_long */
		if(roperand instanceof SedConstant) {
			Long value = Long.valueOf(this.get_long(((SedConstant) roperand).get_constant()));
			if(value == 0) {
				muta_expression = (SedExpression) SedFactory.sed_node(Long.valueOf(0));
				if(!this.extend_at(new SedSetLongExpressionError(statement, orig_expression, muta_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
			else if(value == 1) {
				/* equivalent mutation when x == x * 1 */	return false;
			}
			else if(value == -1) {
				if(!this.extend_at(new SedNegLongExpressionError(statement, orig_expression))) {
					return false;
				}
			}
			else {
				if(!this.extend_at(new SedExtLongExpressionError(statement, orig_expression))) {
					return false;
				}
			}
		}
		
		return this.extend_at(new SedChgLongExpressionError(statement, orig_expression));
	}
	/**
	 * @param source
	 * @return	[const, const] --> set_long
	 * 			[xxxxx, const] --> set_long | neg_long | ext_long
	 * 			--> chg_long
	 * @throws Exception
	 */
	private boolean extend_and_long(SedAndLongExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		
		/* loperand, roperand, difference */
		SedExpression loperand = (SedExpression) this.evaluator.evaluate(orig_expression);
		SedExpression roperand = (SedExpression) this.evaluator.evaluate(muta_expression);
		
		/* [const, const] --> set_long */
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			Long x = Long.valueOf(this.get_long(((SedConstant) loperand).get_constant()));
			Long y = Long.valueOf(this.get_long(((SedConstant) roperand).get_constant()));
			muta_expression = (SedExpression) SedFactory.sed_node(Long.valueOf(x & y));
			if(!this.extend_at(new SedSetLongExpressionError(statement, orig_expression, muta_expression))) {
				return false;	/* failed to extend recursively */
			}
		}
		
		/* [xxxxx, const] --> set_long | neg_long | ext_long */
		if(roperand instanceof SedConstant) {
			Long value = Long.valueOf(this.get_long(((SedConstant) roperand).get_constant()));
			if(value == 0) {
				muta_expression = (SedExpression) SedFactory.sed_node(Long.valueOf(0));
				if(!this.extend_at(new SedSetLongExpressionError(statement, orig_expression, muta_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
			else if(value == ~0) {
				/* equivalent mutation since x == x & 1111...1111 */	return false;
			}
			else {
				if(!this.extend_at(new SedShkLongExpressionError(statement, orig_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
		}
		
		return this.extend_at(new SedChgLongExpressionError(statement, orig_expression));
	}
	/**
	 * @param source
	 * @return	[const, const] --> set_long
	 * 			[xxxxx, const] --> set_long | ext_long
	 * 			--> chg_long
	 * @throws Exception
	 */
	private boolean extend_ior_long(SedIorLongExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		
		/* loperand, roperand, difference */
		SedExpression loperand = (SedExpression) this.evaluator.evaluate(orig_expression);
		SedExpression roperand = (SedExpression) this.evaluator.evaluate(muta_expression);
		
		/* [const, const] --> set_long */
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			Long x = Long.valueOf(this.get_long(((SedConstant) loperand).get_constant()));
			Long y = Long.valueOf(this.get_long(((SedConstant) roperand).get_constant()));
			muta_expression = (SedExpression) SedFactory.sed_node(Long.valueOf(x | y));
			if(!this.extend_at(new SedSetLongExpressionError(statement, orig_expression, muta_expression))) {
				return false;	/* failed to extend recursively */
			}
		}
		
		/* [xxxxx, const] --> set_long | neg_long | ext_long */
		if(roperand instanceof SedConstant) {
			Long value = Long.valueOf(this.get_long(((SedConstant) roperand).get_constant()));
			if(value == 0) {
				/* equivalent mutation since x == x | 0 */	return false;
			}
			else if(value == ~0) {
				muta_expression = (SedExpression) SedFactory.sed_node(Long.valueOf(value));
				if(!this.extend_at(new SedSetLongExpressionError(statement, orig_expression, muta_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
			else {
				if(!this.extend_at(new SedExtLongExpressionError(statement, orig_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
		}
		
		return this.extend_at(new SedChgLongExpressionError(statement, orig_expression));
	}
	/**
	 * @param source
	 * @return	
	 * @throws Exception
	 */
	private boolean extend_xor_long(SedXorLongExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		
		/* loperand, roperand, difference */
		SedExpression loperand = (SedExpression) this.evaluator.evaluate(orig_expression);
		SedExpression roperand = (SedExpression) this.evaluator.evaluate(muta_expression);
		
		/* [const, const] --> set_long */
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			Long x = Long.valueOf(this.get_long(((SedConstant) loperand).get_constant()));
			Long y = Long.valueOf(this.get_long(((SedConstant) roperand).get_constant()));
			muta_expression = (SedExpression) SedFactory.sed_node(Long.valueOf(x ^ y));
			if(!this.extend_at(new SedSetLongExpressionError(statement, orig_expression, muta_expression))) {
				return false;	/* failed to extend recursively */
			}
		}
		
		/* [xxxxx, const] --> set_long | neg_long | ext_long */
		if(roperand instanceof SedConstant) {
			Long value = Long.valueOf(this.get_long(((SedConstant) roperand).get_constant()));
			if(value == 0) {
				/* equivalent mutation since x == x ^ 0 */	return false;
			}
			else if(value == ~0) {
				if(!this.extend_at(new SedRsvLongExpressionError(statement, orig_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
		}
		
		return this.extend_at(new SedChgLongExpressionError(statement, orig_expression));
	}
	/**
	 * @param source
	 * @return neg_long ~~> set_long
	 * 					--> chg_long
	 * @throws Exception
	 */
	private boolean extend_neg_long(SedNegLongExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		
		SedExpression loperand = (SedExpression) this.evaluator.evaluate(orig_expression);
		if(loperand instanceof SedConstant) {
			Long value = Long.valueOf(-this.get_long(((SedConstant) loperand).get_constant()));
			if(!this.extend_at(new SedSetLongExpressionError(
					statement, orig_expression, (SedExpression) SedFactory.sed_node(value)))) {
				return false;
			}
		}
		return this.extend_at(new SedChgLongExpressionError(statement, orig_expression));
	}
	/**
	 * @param source
	 * @return rsv_long ~~> set_long
	 * 					--> chg_long
	 * @throws Exception
	 */
	private boolean extend_rsv_long(SedRsvLongExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		
		SedExpression loperand = (SedExpression) this.evaluator.evaluate(orig_expression);
		if(loperand instanceof SedConstant) {
			Long value = Long.valueOf(~this.get_long(((SedConstant) loperand).get_constant()));
			if(!this.extend_at(new SedSetLongExpressionError(
					statement, orig_expression, (SedExpression) SedFactory.sed_node(value)))) {
				return false;
			}
		}
		return this.extend_at(new SedChgLongExpressionError(statement, orig_expression));
	}
	private boolean extend_otr_long(SedLongExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		return this.extend_at(new SedChgLongExpressionError(statement, orig_expression));
	}
	private boolean extend_long_error(SedLongExpressionError source) throws Exception {
		if(source instanceof SedSetLongExpressionError)
			return this.extend_set_long((SedSetLongExpressionError) source);
		else if(source instanceof SedAddLongExpressionError)
			return this.extend_add_long((SedAddLongExpressionError) source);
		else if(source instanceof SedMulLongExpressionError)
			return this.extend_mul_long((SedMulLongExpressionError) source);
		else if(source instanceof SedAndLongExpressionError)
			return this.extend_and_long((SedAndLongExpressionError) source);
		else if(source instanceof SedIorLongExpressionError)
			return this.extend_ior_long((SedIorLongExpressionError) source);
		else if(source instanceof SedXorLongExpressionError)
			return this.extend_xor_long((SedXorLongExpressionError) source);
		else if(source instanceof SedNegLongExpressionError)
			return this.extend_neg_long((SedNegLongExpressionError) source);
		else if(source instanceof SedRsvLongExpressionError)
			return this.extend_rsv_long((SedRsvLongExpressionError) source);
		else
			return this.extend_otr_long(source);
	}
	/* double expression error extension */
	private boolean extend_set_real(SedSetRealExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		
		/* loperand, roperand, difference */
		SedExpression loperand = (SedExpression) this.evaluator.evaluate(orig_expression);
		SedExpression roperand = (SedExpression) this.evaluator.evaluate(muta_expression);
		SedExpression difference = this.dif(orig_expression, muta_expression);
		
		/* (loperand: const, roperand: const) ==> neg_char */
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			double x = this.get_real(((SedConstant) loperand).get_constant());
			double y = this.get_real(((SedConstant) roperand).get_constant());
			if(x == y) { /* equivalent mutation if x == y */ return false; }
			else if(x == -y) {
				if(!this.extend_at(new SedNegRealExpressionError(statement, orig_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
		}
		
		/* (difference: const) */
		if(difference instanceof SedConstant) {
			if(!this.extend_at(new SedAddRealExpressionError(statement, orig_expression, difference))) {
				return false; 	/* failed to extend recursively */
			}
		}
		
		return this.extend_at(new SedChgRealExpressionError(statement, orig_expression));
	}
	private boolean extend_add_real(SedAddRealExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		
		/* loperand, roperand, difference */
		SedExpression loperand = (SedExpression) this.evaluator.evaluate(orig_expression);
		SedExpression roperand = (SedExpression) this.evaluator.evaluate(muta_expression);
		
		/* add_long --> inc_long|dec_long 	(xxx, const) */
		if(roperand instanceof SedConstant) {
			Double value = Double.valueOf(this.get_real(((SedConstant) roperand).get_constant()));
			if(value == 0) { /* equivalent mutation when x == y */	return false; }
			else if(value > 0) {
				if(!this.extend_at(new SedIncRealExpressionError(statement, orig_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
			else {
				if(!this.extend_at(new SedDecRealExpressionError(statement, orig_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
		}
		
		/* add_long --> set_long (const, const) */
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			Double x = Double.valueOf(this.get_real(((SedConstant) loperand).get_constant()));
			Double y = Double.valueOf(this.get_real(((SedConstant) roperand).get_constant()));
			muta_expression = (SedExpression) SedFactory.sed_node(Double.valueOf(x + y));
			if(!this.extend_at(new SedSetRealExpressionError(statement, orig_expression, muta_expression))) {
				return false;	/* failed to extend recursively */
			}
		}
		
		return this.extend_at(new SedChgRealExpressionError(statement, orig_expression));
	}
	private boolean extend_mul_real(SedMulRealExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		
		/* loperand, roperand, difference */
		SedExpression loperand = (SedExpression) this.evaluator.evaluate(orig_expression);
		SedExpression roperand = (SedExpression) this.evaluator.evaluate(muta_expression);
		
		/* [const, const] --> set_long */
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			Double x = Double.valueOf(this.get_real(((SedConstant) loperand).get_constant()));
			Double y = Double.valueOf(this.get_real(((SedConstant) roperand).get_constant()));
			muta_expression = (SedExpression) SedFactory.sed_node(Double.valueOf(x * y));
			if(!this.extend_at(new SedSetRealExpressionError(statement, orig_expression, muta_expression))) {
				return false;	/* failed to extend recursively */
			}
		}
		
		/* [xxxxx, const] --> set_long | neg_long | ext_long */
		if(roperand instanceof SedConstant) {
			Double value = Double.valueOf(this.get_real(((SedConstant) roperand).get_constant()));
			if(value == 0) {
				muta_expression = (SedExpression) SedFactory.sed_node(Double.valueOf(value));
				if(!this.extend_at(new SedSetRealExpressionError(statement, orig_expression, muta_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
			else if(value == 1) {
				/* equivalent mutation when x == x * 1 */	return false;
			}
			else if(value == -1) {
				if(!this.extend_at(new SedNegRealExpressionError(statement, orig_expression))) {
					return false;
				}
			}
			else if(value > 1 || value < -1) {
				if(!this.extend_at(new SedExtRealExpressionError(statement, orig_expression))) {
					return false;
				}
			}
			else {
				if(!this.extend_at(new SedShkRealExpressionError(statement, orig_expression))) {
					return false;
				}
			}
		}
		
		return this.extend_at(new SedChgRealExpressionError(statement, orig_expression));
	}
	private boolean extend_neg_real(SedNegRealExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		
		SedExpression loperand = (SedExpression) this.evaluator.evaluate(orig_expression);
		if(loperand instanceof SedConstant) {
			Double value = Double.valueOf(-this.get_real(((SedConstant) loperand).get_constant()));
			if(!this.extend_at(new SedSetRealExpressionError(
					statement, orig_expression, (SedExpression) SedFactory.sed_node(value)))) {
				return false;
			}
		}
		return this.extend_at(new SedChgRealExpressionError(statement, orig_expression));
	}
	private boolean extend_otr_real(SedRealExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		return this.extend_at(new SedChgRealExpressionError(statement, orig_expression));
	}
	private boolean extend_real_error(SedRealExpressionError source) throws Exception {
		if(source instanceof SedSetRealExpressionError)
			return this.extend_set_real((SedSetRealExpressionError) source);
		else if(source instanceof SedAddRealExpressionError)
			return this.extend_add_real((SedAddRealExpressionError) source);
		else if(source instanceof SedMulRealExpressionError)
			return this.extend_mul_real((SedMulRealExpressionError) source);
		else if(source instanceof SedNegRealExpressionError)
			return this.extend_neg_real((SedNegRealExpressionError) source);
		else
			return this.extend_otr_real(source);
	}
	/* address expression error extension */
	private boolean extend_set_addr(SedSetAddrExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		
		/* loperand, roperand, difference */
		SedExpression loperand = (SedExpression) this.evaluator.evaluate(orig_expression);
		SedExpression roperand = (SedExpression) this.evaluator.evaluate(muta_expression);
		SedExpression difference = this.dif(orig_expression, muta_expression);
		
		/* (loperand: const, roperand: const) ==> neg_char */
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			long x = this.get_long(((SedConstant) loperand).get_constant());
			long y = this.get_long(((SedConstant) roperand).get_constant());
			if(x == y) { /* equivalent mutation if x == y */ return false; }
		}
		
		/* (difference: const) */
		if(difference instanceof SedConstant) {
			if(!this.extend_at(new SedAddAddrExpressionError(statement, orig_expression, difference))) {
				return false; 	/* failed to extend recursively */
			}
		}
		
		return this.extend_at(new SedChgAddrExpressionError(statement, orig_expression));
	}
	private boolean extend_add_addr(SedAddAddrExpressionError source) throws Exception {
		/* declarations */
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		
		/* loperand, roperand, difference */
		SedExpression loperand = (SedExpression) this.evaluator.evaluate(orig_expression);
		SedExpression roperand = (SedExpression) this.evaluator.evaluate(muta_expression);
		
		/* add_long --> inc_long|dec_long 	(xxx, const) */
		if(roperand instanceof SedConstant) {
			Long value = Long.valueOf(this.get_long(((SedConstant) roperand).get_constant()));
			if(value == 0) { /* equivalent mutation when x == y */	return false; }
			else if(value > 0) {
				if(!this.extend_at(new SedIncAddrExpressionError(statement, orig_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
			else {
				if(!this.extend_at(new SedDecAddrExpressionError(statement, orig_expression))) {
					return false;	/* failed to extend recursively */
				}
			}
		}
		
		/* add_long --> set_long (const, const) */
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			Long x = Long.valueOf(this.get_long(((SedConstant) loperand).get_constant()));
			Long y = Long.valueOf(this.get_long(((SedConstant) roperand).get_constant()));
			muta_expression = (SedExpression) SedFactory.sed_node(Long.valueOf(x + y));
			if(!this.extend_at(new SedSetAddrExpressionError(statement, orig_expression, muta_expression))) {
				return false;	/* failed to extend recursively */
			}
		}
		
		return this.extend_at(new SedChgAddrExpressionError(statement, orig_expression));
	}
	private boolean extend_otr_addr(SedAddrExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		return this.extend_at(new SedChgAddrExpressionError(statement, orig_expression));
	}
	private boolean extend_addr_error(SedAddrExpressionError source) throws Exception {
		if(source instanceof SedSetAddrExpressionError)
			return this.extend_set_addr((SedSetAddrExpressionError) source);
		else if(source instanceof SedAddAddrExpressionError)
			return this.extend_add_addr((SedAddAddrExpressionError) source);
		else
			return this.extend_otr_addr(source);
	}
	/* byte expression error extensions */
	private boolean extend_byte_error(SedByteExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		return this.extend_at(new SedChgByteExpressionError(statement, orig_expression));
	}
	
}
