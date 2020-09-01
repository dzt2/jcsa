package com.jcsa.jcmutest.mutant.sed2mutant.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.SedAddExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.SedAddStatementError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.SedDelStatementError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.SedExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.SedInsExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.SedMutStatementError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.SedSetExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.SedStateError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.SedStatementError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.SedAbstractValueError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.chgval.SedChgAddressError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.chgval.SedChgCharacterError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.chgval.SedChgDoubleError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.chgval.SedChgIntegerError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.chgval.SedChgStructError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.incval.SedAddAddressError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.incval.SedAddCharacterError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.incval.SedAddDoubleError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.incval.SedAddIntegerError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.incval.SedDecAddressError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.incval.SedDecCharacterError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.incval.SedDecDoubleError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.incval.SedDecIntegerError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.incval.SedIncAddressError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.incval.SedIncCharacterError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.incval.SedIncDoubleError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.incval.SedIncIntegerError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.setval.SedSetAddressError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.setval.SedSetBooleanError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.setval.SedSetCharacterError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.setval.SedSetDoubleError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.setval.SedSetIntegerError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.setval.SedSetStructError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.unary.SedNegNumericError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.unary.SedNotBooleanError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.unary.SedRsvIntegerError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.zroval.SedAndIntegerError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.zroval.SedGrowthIntegerError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.zroval.SedIorIntegerError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.zroval.SedMulDoubleError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.zroval.SedMulIntegerError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.zroval.SedShrinkIntegerError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.zroval.SedXorIntegerError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedBinaryExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedConstant;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedDefaultValue;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
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
	/** evaluator used to compute symbolic expression **/
	private SedEvaluator evaluator;
	/** mapping from key to unique state errors **/
	private Map<String, SedStateError> errors;
	/** the error to preserve the extension set of state error **/
	private Set<SedAbstractValueError> extension_set;
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
			this.evaluator = new SedEvaluator();
			this.extension_set = new HashSet<SedAbstractValueError>();
			this.errors = new HashMap<String, SedStateError>();
		}
	}
	
	/* getters */
	/**
	 * @return the cir-code in which errors are seeded
	 */
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * @return evaluator to compute symbolic expression
	 */
	public SedEvaluator get_evaluator() { return evaluator; }
	/**
	 * @param error
	 * @return the unique state error w.r.t. error as input
	 */
	private SedStateError unique_error(SedStateError error) {
		String key = error.toString();
		if(!this.errors.containsKey(key)) {
			this.errors.put(key, error);
		}
		return this.errors.get(key);
	}
	
	/* verifications */
	/**
	 * @param expression
	 * @return whether the expression is taken as a boolean condition
	 * @throws Exception
	 */
	private boolean is_boolean(CirExpression expression) throws Exception {
		CType data_type = CTypeAnalyzer.
				get_value_type(expression.get_data_type());
		if(CTypeAnalyzer.is_boolean(data_type)) {
			return true;
		}
		else {
			CirStatement statement = expression.statement_of();
			if(statement instanceof CirIfStatement
				|| statement instanceof CirCaseStatement) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is taken and used as a character
	 * @throws Exception
	 */
	private boolean is_character(CirExpression expression) throws Exception {
		CType data_type = CTypeAnalyzer.
				get_value_type(expression.get_data_type());
		return CTypeAnalyzer.is_character(data_type);
	}
	/**
	 * @param expression
	 * @return whether the expression is taken as integer or real
	 * @throws Exception
	 */
	private boolean is_integer(CirExpression expression) throws Exception {
		CType data_type = CTypeAnalyzer.
				get_value_type(expression.get_data_type());
		return CTypeAnalyzer.is_integer(data_type);
	}
	/**
	 * @param expression
	 * @return whether the expression is taken as integer or real
	 * @throws Exception
	 */
	private boolean is_double(CirExpression expression) throws Exception {
		CType data_type = CTypeAnalyzer.
				get_value_type(expression.get_data_type());
		return CTypeAnalyzer.is_real(data_type);
	}
	/**
	 * @param expression
	 * @return whether the expression is taken as an address (array or pointer)
	 * @throws Exception
	 */
	private boolean is_address(CirExpression expression) throws Exception {
		CType data_type = CTypeAnalyzer.
				get_value_type(expression.get_data_type());
		return CTypeAnalyzer.is_pointer(data_type);
	}
	
	/* expression building */
	/**
	 * @param constant
	 * @return the boolean value of the constant
	 * @throws Exception
	 */
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
		default: throw new IllegalArgumentException("Invalid: " + constant);
		}
	}
	/**
	 * @param constant
	 * @return
	 * @throws Exception
	 */
	private char get_char(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:		return constant.get_bool() ? '\1' : '\0';
		case c_char:
		case c_uchar:		return constant.get_char().charValue();
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
		default: throw new IllegalArgumentException("Invalid: " + constant);
		}
	}
	/**
	 * @param constant
	 * @return
	 * @throws Exception
	 */
	private long get_long(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:		return constant.get_bool() ? '\1' : '\0';
		case c_char:
		case c_uchar:		return constant.get_char().charValue();
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
		default: throw new IllegalArgumentException("Invalid: " + constant);
		}
	}
	/**
	 * @param constant
	 * @return
	 * @throws Exception
	 */
	private double get_double(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:		return constant.get_bool() ? '\1' : '\0';
		case c_char:
		case c_uchar:		return constant.get_char().charValue();
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
		default: throw new IllegalArgumentException("Invalid: " + constant);
		}
	}
	
	/* statement error creators */
	/**
	 * @param statement
	 * @return add_stmt(statement)
	 * @throws Exception
	 */
	public SedStatementError add_stmt(CirStatement statement) throws Exception {
		return (SedStatementError) this.unique_error(new SedAddStatementError(statement, statement));
	}
	/**
	 * @param statement
	 * @return del_stmt(statement)
	 * @throws Exception
	 */
	public SedStatementError del_stmt(CirStatement statement) throws Exception {
		return (SedStatementError) this.unique_error(new SedDelStatementError(statement, statement));
	}
	/**
	 * @param orig_statement
	 * @param muta_statement
	 * @return mut_stmt(statement, statement)
	 * @throws Exception
	 */
	public SedStatementError mut_stmt(CirStatement orig_statement,
			CirStatement muta_statement) throws Exception {
		return (SedStatementError) this.unique_error(new SedMutStatementError(
				orig_statement, orig_statement, muta_statement));
	}
	
	/* concrete error creator */
	public SedExpressionError ins_expr(CirStatement statement, 
			CirExpression expression, COperator operator) throws Exception {
		return (SedExpressionError) this.unique_error(new SedInsExpressionError(statement, 
				(SedExpression) SedParser.parse(expression), operator));
	}
	public SedExpressionError set_expr(CirStatement statement,
			CirExpression orig_expression, 
			SedExpression muta_expression) throws Exception {
		return (SedExpressionError) this.unique_error(new SedSetExpressionError(statement, 
				(SedExpression) SedParser.parse(orig_expression), muta_expression));
	}
	public SedExpressionError add_expr(CirStatement statement,
			CirExpression expression, COperator operator,
			SedExpression operand) throws Exception {
		return (SedExpressionError) this.unique_error(new SedAddExpressionError(statement, 
				(SedExpression) SedParser.parse(expression), operator, operand));
	}
	
	/* abstract error initializer */
	private SedAbstractValueError abs_error(SedInsExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression expression = source.get_orig_expression();
		COperator operator = source.get_ins_operator().get_operator();
		
		SedAbstractValueError result;
		switch(operator) {
		case negative:	result = new SedNegNumericError(statement, expression); break;
		case bit_not:	result = new SedRsvIntegerError(statement, expression);	break;
		case logic_not:	result = new SedNotBooleanError(statement, expression);	break;
		default: throw new IllegalArgumentException("Unsupport: " + operator.toString());
		}
		return (SedAbstractValueError) this.unique_error(result);
	}
	private SedAbstractValueError abs_error(SedSetExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_muta_expression();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		muta_expression = (SedExpression) this.evaluator.evaluate(muta_expression);
		
		SedAbstractValueError result;
		if(this.is_boolean(expression)) {
			if(muta_expression instanceof SedConstant) {
				Boolean value = Boolean.valueOf(this.get_bool(((SedConstant) muta_expression).get_constant()));
				result = new SedSetBooleanError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else if(muta_expression instanceof SedDefaultValue) {
				result = new SedNotBooleanError(statement, orig_expression);
			}
			else {
				result = new SedSetBooleanError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_character(expression)) {
			if(muta_expression instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) muta_expression).get_constant()));
				result = new SedSetCharacterError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else if(muta_expression instanceof SedDefaultValue) {
				result = new SedChgCharacterError(statement, orig_expression);
			}
			else {
				result = new SedSetCharacterError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_integer(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				result = new SedSetIntegerError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else if(muta_expression instanceof SedDefaultValue) {
				result = new SedChgIntegerError(statement, orig_expression);
			}
			else {
				result = new SedSetIntegerError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_double(expression)) {
			if(muta_expression instanceof SedConstant) {
				Double value = Double.valueOf(this.get_double(((SedConstant) muta_expression).get_constant()));
				result = new SedSetDoubleError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else if(muta_expression instanceof SedDefaultValue) {
				result = new SedChgDoubleError(statement, orig_expression);
			}
			else {
				result = new SedSetDoubleError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_address(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				result = new SedSetAddressError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else if(muta_expression instanceof SedDefaultValue) {
				result = new SedChgAddressError(statement, orig_expression);
			}
			else {
				result = new SedSetAddressError(statement, orig_expression, muta_expression);
			}
		}
		else {
			if(muta_expression instanceof SedDefaultValue) {
				result = new SedChgStructError(statement, orig_expression);
			}
			else {
				result = new SedSetStructError(statement, orig_expression, muta_expression);
			}
		}
		return (SedAbstractValueError) this.unique_error(result);
	}
	private SedAbstractValueError abs_error_of_add(SedAddExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_add_operand();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		muta_expression = (SedExpression) this.evaluator.evaluate(muta_expression);
		
		SedAbstractValueError result;
		if(this.is_boolean(expression)) {
			result = new SedSetBooleanError(statement, orig_expression, 
					(SedExpression) SedFactory.sed_node(Boolean.TRUE));
		}
		else if(this.is_character(expression)) {
			if(muta_expression instanceof SedConstant) {
				Character value = Character.valueOf(this.get_char(((SedConstant) muta_expression).get_constant()));
				result = new SedAddCharacterError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else if(muta_expression instanceof SedDefaultValue) {
				String name = ((SedDefaultValue) muta_expression).get_name();
				if(name.equals(SedDefaultValue.AnyPosNum)) {
					result = new SedIncCharacterError(statement, orig_expression);
				}
				else if(name.equals(SedDefaultValue.AnyNegNum)) {
					result = new SedDecCharacterError(statement, orig_expression);
				}
				else {
					result = new SedChgCharacterError(statement, orig_expression);
				}
			}
			else {
				result = new SedAddCharacterError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_integer(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				result = new SedAddIntegerError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else if(muta_expression instanceof SedDefaultValue) {
				String name = ((SedDefaultValue) muta_expression).get_name();
				if(name.equals(SedDefaultValue.AnyPosNum)) {
					result = new SedIncIntegerError(statement, orig_expression);
				}
				else if(name.equals(SedDefaultValue.AnyNegNum)) {
					result = new SedDecIntegerError(statement, orig_expression);
				}
				else {
					result = new SedChgIntegerError(statement, orig_expression);
				}
			}
			else {
				result = new SedAddIntegerError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_double(expression)) {
			if(muta_expression instanceof SedConstant) {
				Double value = Double.valueOf(this.get_double(((SedConstant) muta_expression).get_constant()));
				result = new SedAddDoubleError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else if(muta_expression instanceof SedDefaultValue) {
				String name = ((SedDefaultValue) muta_expression).get_name();
				if(name.equals(SedDefaultValue.AnyPosNum)) {
					result = new SedIncDoubleError(statement, orig_expression);
				}
				else if(name.equals(SedDefaultValue.AnyNegNum)) {
					result = new SedDecDoubleError(statement, orig_expression);
				}
				else {
					result = new SedChgDoubleError(statement, orig_expression);
				}
			}
			else {
				result = new SedAddDoubleError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_address(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				result = new SedAddAddressError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else if(muta_expression instanceof SedDefaultValue) {
				String name = ((SedDefaultValue) muta_expression).get_name();
				if(name.equals(SedDefaultValue.AnyPosNum)) {
					result = new SedIncAddressError(statement, orig_expression);
				}
				else if(name.equals(SedDefaultValue.AnyNegNum)) {
					result = new SedDecAddressError(statement, orig_expression);
				}
				else {
					result = new SedChgAddressError(statement, orig_expression);
				}
			}
			else {
				result = new SedAddAddressError(statement, orig_expression, muta_expression);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid type: " + expression.generate_code(true));
		}
		return (SedAbstractValueError) this.unique_error(result);
	}
	private SedAbstractValueError abs_error_of_sub(SedAddExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_add_operand();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		muta_expression = (SedExpression) this.evaluator.evaluate(muta_expression);
		
		SedAbstractValueError result;
		if(this.is_boolean(expression)) {
			result = new SedSetBooleanError(statement, orig_expression, 
					(SedExpression) SedFactory.sed_node(Boolean.TRUE));
		}
		else if(this.is_character(expression)) {
			if(muta_expression instanceof SedConstant) {
				Character value = Character.valueOf((char) -this.get_char(((SedConstant) muta_expression).get_constant()));
				result = new SedAddCharacterError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else if(muta_expression instanceof SedDefaultValue) {
				String name = ((SedDefaultValue) muta_expression).get_name();
				if(name.equals(SedDefaultValue.AnyPosNum)) {
					result = new SedDecCharacterError(statement, orig_expression);
				}
				else if(name.equals(SedDefaultValue.AnyNegNum)) {
					result = new SedIncCharacterError(statement, orig_expression);
				}
				else {
					result = new SedChgCharacterError(statement, orig_expression);
				}
			}
			else {
				muta_expression = SedFactory.arith_neg(expression.get_data_type(), muta_expression);
				result = new SedAddCharacterError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_integer(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(-this.get_long(((SedConstant) muta_expression).get_constant()));
				result = new SedAddIntegerError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else if(muta_expression instanceof SedDefaultValue) {
				String name = ((SedDefaultValue) muta_expression).get_name();
				if(name.equals(SedDefaultValue.AnyPosNum)) {
					result = new SedDecIntegerError(statement, orig_expression);
				}
				else if(name.equals(SedDefaultValue.AnyNegNum)) {
					result = new SedIncIntegerError(statement, orig_expression);
				}
				else {
					result = new SedChgIntegerError(statement, orig_expression);
				}
			}
			else {
				muta_expression = SedFactory.arith_neg(expression.get_data_type(), muta_expression);
				result = new SedAddIntegerError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_double(expression)) {
			if(muta_expression instanceof SedConstant) {
				Double value = Double.valueOf(-this.get_double(((SedConstant) muta_expression).get_constant()));
				result = new SedAddDoubleError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else if(muta_expression instanceof SedDefaultValue) {
				String name = ((SedDefaultValue) muta_expression).get_name();
				if(name.equals(SedDefaultValue.AnyPosNum)) {
					result = new SedDecDoubleError(statement, orig_expression);
				}
				else if(name.equals(SedDefaultValue.AnyNegNum)) {
					result = new SedIncDoubleError(statement, orig_expression);
				}
				else {
					result = new SedChgDoubleError(statement, orig_expression);
				}
			}
			else {
				muta_expression = SedFactory.arith_neg(expression.get_data_type(), muta_expression);
				result = new SedAddDoubleError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_address(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(-this.get_long(((SedConstant) muta_expression).get_constant()));
				result = new SedAddAddressError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
			}
			else if(muta_expression instanceof SedDefaultValue) {
				String name = ((SedDefaultValue) muta_expression).get_name();
				if(name.equals(SedDefaultValue.AnyPosNum)) {
					result = new SedDecAddressError(statement, orig_expression);
				}
				else if(name.equals(SedDefaultValue.AnyNegNum)) {
					result = new SedIncAddressError(statement, orig_expression);
				}
				else {
					result = new SedChgAddressError(statement, orig_expression);
				}
			}
			else {
				muta_expression = SedFactory.arith_neg(expression.get_data_type(), muta_expression);
				result = new SedAddAddressError(statement, orig_expression, muta_expression);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid type: " + expression.generate_code(true));
		}
		return (SedAbstractValueError) this.unique_error(result);
	}
	private SedAbstractValueError abs_error_of_mul(SedAddExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_add_operand();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		muta_expression = (SedExpression) this.evaluator.evaluate(muta_expression);
		
		SedAbstractValueError result;
		if(this.is_boolean(expression)) {
			if(muta_expression instanceof SedConstant) {
				if(this.get_bool(((SedConstant) muta_expression).get_constant())) {
					return null;	/* equivalent mutation */
				}
				else {
					result = new SedSetBooleanError(statement, orig_expression,
							(SedExpression) SedFactory.sed_node(Boolean.FALSE));
				}
			}
			else {
				result = new SedSetBooleanError(statement, orig_expression,
						(SedExpression) SedFactory.sed_node(Boolean.FALSE));
			}
		}
		else if(this.is_integer(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				if(value.longValue() == 0L) {
					result = new SedSetIntegerError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
				else {
					result = new SedMulIntegerError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
			}
			else if(muta_expression instanceof SedDefaultValue) {
				result = new SedChgIntegerError(statement, orig_expression);
			}
			else {
				result = new SedMulIntegerError(statement, orig_expression, muta_expression);
			}
		}
		else if(this.is_double(expression)) {
			if(muta_expression instanceof SedConstant) {
				Double value = Double.valueOf(this.get_double(((SedConstant) muta_expression).get_constant()));
				if(value.longValue() == 0.0) {
					result = new SedSetDoubleError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
				else {
					result = new SedMulDoubleError(statement, orig_expression, (SedExpression) SedFactory.sed_node(value));
				}
			}
			else if(muta_expression instanceof SedDefaultValue) {
				result = new SedChgDoubleError(statement, orig_expression);
			}
			else {
				result = new SedMulDoubleError(statement, orig_expression, muta_expression);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid type: " + expression.generate_code(true));
		}
		return (SedAbstractValueError) this.unique_error(result);
	}
	private SedAbstractValueError abs_error_of_and(SedAddExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_add_operand();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		muta_expression = (SedExpression) this.evaluator.evaluate(muta_expression);
		
		SedAbstractValueError result;
		if(this.is_boolean(expression)) {
			if(muta_expression instanceof SedConstant) {
				if(this.get_bool(((SedConstant) muta_expression).get_constant())) {
					return null;	// equivalent mutation
				}
				else {
					result = new SedSetBooleanError(statement, orig_expression, 
							(SedExpression) SedFactory.sed_node(Boolean.FALSE));
				}
			}
			else {
				result = new SedSetBooleanError(statement, orig_expression, 
						(SedExpression) SedFactory.sed_node(Boolean.FALSE));
			}
		}
		else if(this.is_integer(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				if(value.longValue() == 0L) {
					result = new SedSetIntegerError(statement, orig_expression, 
							(SedExpression) SedFactory.sed_node(value));
				}
				else if(value.longValue() == ~0L) {
					/* equivalent mutation */	return null;
				}
				else {
					result = new SedAndIntegerError(statement, orig_expression, muta_expression);
				}
			}
			else if(muta_expression instanceof SedDefaultValue) {
				result = new SedShrinkIntegerError(statement, orig_expression);
			}
			else {
				result = new SedAndIntegerError(statement, orig_expression, muta_expression);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.generate_code(true));
		}
		return (SedAbstractValueError) this.unique_error(result);
	}
	private SedAbstractValueError abs_error_of_ior(SedAddExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_add_operand();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		muta_expression = (SedExpression) this.evaluator.evaluate(muta_expression);
		
		SedAbstractValueError result;
		if(this.is_boolean(expression)) {
			if(muta_expression instanceof SedConstant) {
				if(this.get_bool(((SedConstant) muta_expression).get_constant())) {
					return null;	// equivalent mutation
				}
				else {
					result = new SedSetBooleanError(statement, orig_expression, 
							(SedExpression) SedFactory.sed_node(Boolean.TRUE));
				}
			}
			else {
				result = new SedSetBooleanError(statement, orig_expression, 
						(SedExpression) SedFactory.sed_node(Boolean.TRUE));
			}
		}
		else if(this.is_integer(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				if(value.longValue() == 0L) {
					/* equivalent mutation */	return null;
				}
				else if(value.longValue() == ~0L) {
					result = new SedSetIntegerError(statement, orig_expression, 
							(SedExpression) SedFactory.sed_node(value));
				}
				else {
					result = new SedIorIntegerError(statement, orig_expression, muta_expression);
				}
			}
			else if(muta_expression instanceof SedDefaultValue) {
				result = new SedGrowthIntegerError(statement, orig_expression);
			}
			else {
				result = new SedIorIntegerError(statement, orig_expression, muta_expression);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.generate_code(true));
		}
		return (SedAbstractValueError) this.unique_error(result);
	}
	private SedAbstractValueError abs_error_of_xor(SedAddExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		SedExpression muta_expression = source.get_add_operand();
		CirExpression expression = (CirExpression) orig_expression.get_cir_source();
		muta_expression = (SedExpression) this.evaluator.evaluate(muta_expression);
		
		SedAbstractValueError result;
		if(this.is_boolean(expression)) {
			if(muta_expression instanceof SedConstant) {
				if(!this.get_bool(((SedConstant) muta_expression).get_constant())) {
					return null;	// equivalent mutation
				}
				else {
					result = new SedNotBooleanError(statement, orig_expression);
				}
			}
			else {
				result = new SedNotBooleanError(statement, orig_expression);
			}
		}
		else if(this.is_integer(expression)) {
			if(muta_expression instanceof SedConstant) {
				Long value = Long.valueOf(this.get_long(((SedConstant) muta_expression).get_constant()));
				if(value.longValue() == 0L) {
					/* equivalent mutation */	return null;
				}
				else if(value.longValue() == ~0L) {
					result = new SedRsvIntegerError(statement, orig_expression);
				}
				else {
					result = new SedXorIntegerError(statement, orig_expression, muta_expression);
				}
			}
			else if(muta_expression instanceof SedDefaultValue) {
				result = new SedChgIntegerError(statement, orig_expression);
			}
			else {
				result = new SedXorIntegerError(statement, orig_expression, muta_expression);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.generate_code(true));
		}
		return (SedAbstractValueError) this.unique_error(result);
	}
	private SedAbstractValueError abs_error(SedAddExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_cir_statement();
		SedExpression orig_expression = source.get_orig_expression();
		COperator operator = source.get_add_operator().get_operator();
		
		switch(operator) {
		case arith_add:	return this.abs_error_of_add(source);
		case arith_sub:	return this.abs_error_of_sub(source);
		case arith_mul:	return this.abs_error_of_mul(source);
		case bit_and:	return this.abs_error_of_and(source);
		case bit_or:	return this.abs_error_of_ior(source);
		case bit_xor:	return this.abs_error_of_xor(source);
		default:
		{
			SedExpression muta_expression = new SedBinaryExpression(
					null, orig_expression.get_data_type(), operator);
			muta_expression.add_child(orig_expression);
			muta_expression.add_child(source.get_add_operand());
			SedSetExpressionError new_source = new 
					SedSetExpressionError(statement, orig_expression, muta_expression);
			return this.abs_error(new_source);
		}
		}
	}
	/**
	 * @param source
	 * @return the initial abstract error generated from the concrete source error
	 * @throws Exception
	 */
	protected SedAbstractValueError init_abs_error(SedExpressionError source) throws Exception {
		if(source instanceof SedSetExpressionError) {
			return this.abs_error((SedSetExpressionError) source);
		}
		else if(source instanceof SedInsExpressionError) {
			return this.abs_error((SedInsExpressionError) source);
		}
		else if(source instanceof SedAddExpressionError) {
			return this.abs_error((SedAddExpressionError) source);
		}
		else {
			throw new IllegalArgumentException("Invalid source: " + source);
		}
	}
	
	/* abstract error extensions */
	private void extend_at(SedAbstractValueError error) throws Exception {
		error = (SedAbstractValueError) this.unique_error(error);
		if(!this.extension_set.contains(error)) {
			this.extension_set.add(error);
			// TODO implement the extension algorithms
		}
	}
	/**
	 * set_bool --> not_bool
	 * @param error
	 * @throws Exception
	 */
	private void extend_set_bool(SedSetBooleanError error) throws Exception {
		CirStatement statement = error.get_location().get_cir_statement();
		SedExpression orig_expression = error.get_orig_expression();
		this.extend_at(new SedNotBooleanError(statement, orig_expression));
	}
	/**
	 * not_bool --> set_bool when 
	 * @param error
	 * @throws Exception
	 */
	private void extend_not_bool(SedNotBooleanError error) throws Exception {
		CirStatement statement = error.get_location().get_cir_statement();
		SedExpression orig_expression = error.get_orig_expression();
		SedExpression orig_value = 
				(SedExpression) this.evaluator.evaluate(orig_expression);
		
		if(orig_value instanceof SedConstant) {
			Boolean value = Boolean.valueOf(this.
					get_bool(((SedConstant) orig_value).get_constant()));
			extend_at(new SedSetBooleanError(statement, orig_expression, 
							(SedExpression) SedFactory.sed_node(value)));
		}
	}
	/**
	 * set_char |-->	chg_char
	 * set_char |-->	(neg_numb|rsv_numb) --> chg_char
	 * set_char |-->	(add_char)			--> chg_char
	 * @param error
	 * @throws Exception
	 */
	private void extend_set_char(SedSetCharacterError error) throws Exception {
		CirStatement statement = error.get_location().get_cir_statement();
		SedExpression orig_expression = error.get_orig_expression();
		SedExpression orig_value = 
				(SedExpression) this.evaluator.evaluate(orig_expression);
		SedExpression muta_expression = error.get_muta_expression();
		SedExpression muta_value = 
				(SedExpression) this.evaluator.evaluate(muta_expression);
		
		if(orig_value instanceof SedConstant) {
			if(muta_value instanceof SedConstant) {
				char orig_char = this.get_char(((SedConstant) orig_value).get_constant());
				char muta_char = this.get_char(((SedConstant) orig_value).get_constant());
				if(orig_char == muta_char)	return;
				this.extend_at(new SedAddCharacterError(statement, orig_expression, 
						(SedExpression) SedFactory.sed_node(muta_char - orig_char)));
				
				if(orig_char == -muta_char) {
					this.extend_at(new SedNegNumericError(statement, orig_expression));
				}
				else if(orig_char == ~muta_char) {
					this.extend_at(new SedRsvIntegerError(statement, orig_expression));
				}
			}
		}
		this.extend_at(new SedChgCharacterError(statement, orig_expression));
	}
	/**
	 * @param error
	 * @throws Exception
	 */
	private void extend_chg_char(SedChgCharacterError error) throws Exception {}
	/**
	 * set_long --> (dif_long|(neg_numb|rsv_long)) --> chg_long
	 * @param error
	 * @throws Exception
	 */
	private void extend_set_long(SedSetIntegerError error) throws Exception {
		CirStatement statement = error.get_location().get_cir_statement();
		SedExpression orig_expression = error.get_orig_expression();
		SedExpression orig_value = 
				(SedExpression) this.evaluator.evaluate(orig_expression);
		SedExpression muta_expression = error.get_muta_expression();
		SedExpression muta_value = 
				(SedExpression) this.evaluator.evaluate(muta_expression);
		
		if(orig_value instanceof SedConstant) {
			if(muta_value instanceof SedConstant) {
				long orig_long = this.get_long(((SedConstant) orig_value).get_constant());
				long muta_long = this.get_long(((SedConstant) muta_value).get_constant());
				if(orig_long == muta_long) return;	/* equivalent with original program */
				
				this.extend_at(new SedAddIntegerError(statement, orig_expression, 
						(SedExpression) SedFactory.sed_node(muta_long - muta_long)));
				if(orig_long == -muta_long) {
					this.extend_at(new SedNegNumericError(statement, orig_expression));
				}
				else if(orig_long == ~muta_long) {
					this.extend_at(new SedRsvIntegerError(statement, orig_expression));
				}
			}
		}
		this.extend_at(new SedChgIntegerError(statement, orig_expression));
	}
	private void extend_chg_long(SedChgIntegerError error) throws Exception {}
	/**
	 * set_real --> (add_real|(neg_numb)) --> chg_real
	 * @param error
	 * @throws Exception
	 */
	private void extend_set_double(SedSetDoubleError error) throws Exception {
		CirStatement statement = error.get_location().get_cir_statement();
		SedExpression orig_expression = error.get_orig_expression();
		SedExpression orig_value = 
				(SedExpression) this.evaluator.evaluate(orig_expression);
		SedExpression muta_expression = error.get_muta_expression();
		SedExpression muta_value = 
				(SedExpression) this.evaluator.evaluate(muta_expression);
		
		if(orig_value instanceof SedConstant) {
			if(muta_value instanceof SedConstant) {
				double x = this.get_double(((SedConstant) orig_value).get_constant());
				double y = this.get_double(((SedConstant) muta_value).get_constant());
				if(x == y)	return;	/* equivalent with the original program */
				
				this.extend_at(new SedAddDoubleError(statement, 
						orig_expression, (SedExpression) SedFactory.sed_node(y - x)));
				if(x == -y) {
					this.extend_at(new SedNegNumericError(statement, orig_expression));
				}
			}
		}
		this.extend_at(new SedChgDoubleError(statement, orig_expression));
	}
	private void extend_chg_double(SedChgDoubleError error) throws Exception {}
	/**
	 * set_addr --> (add_addr) --> chg_addr
	 * @param error
	 * @throws Exception
	 */
	private void extend_set_address(SedSetAddressError error) throws Exception {
		
	}
	
	
	
	
}
