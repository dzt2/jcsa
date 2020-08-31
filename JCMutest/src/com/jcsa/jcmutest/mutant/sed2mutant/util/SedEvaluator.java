package com.jcsa.jcmutest.mutant.sed2mutant.util;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt.SedAddExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt.SedAssertion;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt.SedAssertions;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt.SedConditionConstraint;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt.SedConjunction;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt.SedConstraint;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt.SedDelStatementError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt.SedDisjunction;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt.SedExecutionConstraint;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt.SedInsExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt.SedInsStatementError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt.SedSetExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt.SedSetStatementError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt.SedStateError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedBasicExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedBinaryExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedCallExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedConstant;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedDefaultValue;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedFieldExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedIdExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedInitializerList;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedLiteral;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedUnaryExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.stmt.SedAssignStatement;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.stmt.SedCallStatement;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.stmt.SedGotoStatement;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.stmt.SedIfStatement;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.stmt.SedStatement;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.stmt.SedTagStatement;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.stmt.SedWaitStatement;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedArgumentList;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedField;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedOperator;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedToken;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It is the computational unit used in symbolic execution for evaluating
 * the value of SedExpression as well as SedAssertion.
 * 
 * @author yukimula
 *
 */
public class SedEvaluator {
	
	/* definitions */
	private SedEvalScope scope;
	private SedEvaluator() { }
	private static final SedEvaluator evaluator = new SedEvaluator();
	/**
	 * @param source
	 * @param scope
	 * @return the symbolic result evaluated from the source w.r.t. the given scope
	 * @throws Exception
	 */
	public static SedNode evaluate(SedNode source, SedEvalScope scope) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else {
			evaluator.scope = scope;
			return evaluator.eval(source);
		}
	}
	/**
	 * @param source
	 * @return the symbolic result evaluated from the source without any context of scope
	 * @throws Exception
	 */
	public static SedNode evaluate(SedNode source) throws Exception {
		return evaluate(source, null);
	}
	
	/* computation methods */
	private CConstant arith_neg(CConstant constant) throws Exception {
		CConstant result = new CConstant();
		switch(constant.get_type().get_tag()) {
		case c_bool:	result.set_int(constant.get_bool() ? -1 : 0); break;
		case c_char:
		case c_uchar:	result.set_int(-constant.get_char().charValue()); break;
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	result.set_int(-constant.get_integer().intValue()); break;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	result.set_long(-constant.get_long().longValue()); break;
		case c_float:	result.set_float(-constant.get_float().floatValue()); break;
		case c_double:
		case c_ldouble:	result.set_double(-constant.get_double().doubleValue()); break;
		default: throw new IllegalArgumentException("Invalid constant");
		}
		return result;
	}
	private CConstant bitws_rsv(CConstant constant) throws Exception {
		CConstant result = new CConstant();
		switch(constant.get_type().get_tag()) {
		case c_bool:	result.set_int(constant.get_bool() ? ~1 : ~0); break;
		case c_char:
		case c_uchar:	result.set_int(~constant.get_char().charValue()); break;
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	result.set_int(~constant.get_integer().intValue()); break;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	result.set_long(~constant.get_long().longValue()); break;
		default: throw new IllegalArgumentException("Invalid constant");
		}
		return result;
	}
	private CConstant logic_not(CConstant constant) throws Exception {
		CConstant result = new CConstant();
		switch(constant.get_type().get_tag()) {
		case c_bool:	result.set_bool(!constant.get_bool().booleanValue()); break;
		case c_char:
		case c_uchar:	result.set_bool(constant.get_char().charValue() == '\0');; break;
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	result.set_bool(constant.get_integer().intValue() == 0);; break;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	result.set_bool(constant.get_long().longValue() == 0L);; break;
		case c_float:	result.set_bool(constant.get_float().floatValue() == 0.0f); break;
		case c_double:
		case c_ldouble:	result.set_bool(constant.get_double().doubleValue() == 0.0); break;
		default: throw new IllegalArgumentException("Invalid constant");
		}
		return result;
	}
	private CConstant cast_to_bool(CConstant constant) throws Exception {
		CConstant result = new CConstant();
		switch(constant.get_type().get_tag()) {
		case c_bool:	result.set_bool(constant.get_bool().booleanValue()); break;
		case c_char:
		case c_uchar:	result.set_bool(constant.get_char().charValue() != '\0'); break;
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	result.set_bool(constant.get_integer().intValue() != 0); break;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	result.set_bool(constant.get_long().longValue() != 0L); break;
		case c_float:	result.set_bool(constant.get_float().floatValue() != 0.0f); break;
		case c_double:
		case c_ldouble:	result.set_bool(constant.get_double().doubleValue() != 0.0); break;
		default: throw new IllegalArgumentException("Invalid constant.");
		}
		return result;
	}
	private CConstant cast_to_char(CConstant constant) throws Exception {
		CConstant result = new CConstant();
		switch(constant.get_type().get_tag()) {
		case c_bool:	result.set_char((char) (constant.get_bool() ? 1 : 0));; break;
		case c_char:
		case c_uchar:	result.set_char(constant.get_char().charValue()); break;
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	result.set_char((char) constant.get_integer().intValue()); break;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	result.set_char((char) constant.get_long().longValue()); break;
		case c_float:	result.set_char((char) constant.get_float().floatValue()); break;
		case c_double:
		case c_ldouble:	result.set_char((char) constant.get_double().doubleValue()); break;
		default: throw new IllegalArgumentException("Invalid constant.");
		}
		return result;
	}
	private CConstant cast_to_int(CConstant constant) throws Exception {
		CConstant result = new CConstant();
		switch(constant.get_type().get_tag()) {
		case c_bool:	result.set_int(constant.get_bool() ? 1 : 0); break;
		case c_char:
		case c_uchar:	result.set_int(constant.get_char().charValue()); break;
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	result.set_int(constant.get_integer().intValue()); break;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	result.set_int((int) constant.get_long().longValue()); break;
		case c_float:	result.set_int((int) constant.get_float().floatValue()); break;
		case c_double:
		case c_ldouble:	result.set_int((int) constant.get_double().doubleValue()); break;
		default: throw new IllegalArgumentException("Invalid constant.");
		}
		return result;
	}
	private CConstant cast_to_long(CConstant constant) throws Exception {
		CConstant result = new CConstant();
		switch(constant.get_type().get_tag()) {
		case c_bool:	result.set_long(constant.get_bool() ? 1 : 0); break;
		case c_char:
		case c_uchar:	result.set_long(constant.get_char().charValue()); break;
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	result.set_long(constant.get_integer().intValue()); break;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	result.set_long(constant.get_long().longValue()); break;
		case c_float:	result.set_long((long) constant.get_float().floatValue()); break;
		case c_double:
		case c_ldouble:	result.set_long((long) constant.get_double().doubleValue()); break;
		default: throw new IllegalArgumentException("Invalid constant.");
		}
		return result;
	}
	private CConstant cast_to_float(CConstant constant) throws Exception {
		CConstant result = new CConstant();
		switch(constant.get_type().get_tag()) {
		case c_bool:	result.set_float(constant.get_bool() ? 1 : 0); break;
		case c_char:
		case c_uchar:	result.set_float(constant.get_char().charValue()); break;
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	result.set_float(constant.get_integer().intValue()); break;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	result.set_float(constant.get_long().longValue()); break;
		case c_float:	result.set_float(constant.get_float().floatValue()); break;
		case c_double:
		case c_ldouble:	result.set_float((float) constant.get_double().doubleValue()); break;
		default: throw new IllegalArgumentException("Invalid constant.");
		}
		return result;
	}
	private CConstant cast_to_double(CConstant constant) throws Exception {
		CConstant result = new CConstant();
		switch(constant.get_type().get_tag()) {
		case c_bool:	result.set_double(constant.get_bool() ? 1 : 0); break;
		case c_char:
		case c_uchar:	result.set_double(constant.get_char().charValue()); break;
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	result.set_double(constant.get_integer().intValue()); break;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	result.set_double(constant.get_long().longValue()); break;
		case c_float:	result.set_double(constant.get_float().floatValue()); break;
		case c_double:
		case c_ldouble:	result.set_double(constant.get_double().doubleValue()); break;
		default: throw new IllegalArgumentException("Invalid constant.");
		}
		return result;
	}
	private Object get_number(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:	return Long.valueOf(constant.get_bool() ? 1 : 0);
		case c_char:
		case c_uchar:	return Long.valueOf(constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	return Long.valueOf(constant.get_integer().intValue());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	return constant.get_long();
		case c_float:	return Double.valueOf(constant.get_float().floatValue());
		case c_double:
		case c_ldouble:	return constant.get_double();
		default: throw new IllegalArgumentException("Invalid constant.");
		}
	}
	private CConstant arith_add(CConstant loperand, CConstant roperand) throws Exception {
		Object lvalue = this.get_number(loperand), rvalue = this.get_number(roperand);
		CConstant result = new CConstant();
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_long(x + y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_double(x + y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_double(x + y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_double(x + y);
			}
		}
		return result;
	}
	private CConstant arith_sub(CConstant loperand, CConstant roperand) throws Exception {
		Object lvalue = this.get_number(loperand), rvalue = this.get_number(roperand);
		CConstant result = new CConstant();
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_long(x - y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_double(x - y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_double(x - y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_double(x - y);
			}
		}
		return result;
	}
	private CConstant arith_mul(CConstant loperand, CConstant roperand) throws Exception {
		Object lvalue = this.get_number(loperand), rvalue = this.get_number(roperand);
		CConstant result = new CConstant();
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_long(x * y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_double(x * y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_double(x * y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_double(x * y);
			}
		}
		return result;
	}
	private CConstant arith_div(CConstant loperand, CConstant roperand) throws Exception {
		Object lvalue = this.get_number(loperand), rvalue = this.get_number(roperand);
		CConstant result = new CConstant();
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_long(x / y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_double(x / y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_double(x / y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_double(x / y);
			}
		}
		return result;
	}
	private CConstant arith_mod(CConstant loperand, CConstant roperand) throws Exception {
		Object lvalue = this.get_number(loperand), rvalue = this.get_number(roperand);
		CConstant result = new CConstant();
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_long(x % y);
			}
			else {
				long y = ((Double) rvalue).longValue();
				result.set_long(x % y);
			}
		}
		else {
			long x = ((Double) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_long(x % y);
			}
			else {
				long y = ((Double) rvalue).longValue();
				result.set_long(x % y);
			}
		}
		return result;
	}
	private CConstant bitws_and(CConstant loperand, CConstant roperand) throws Exception {
		Object lvalue = this.get_number(loperand), rvalue = this.get_number(roperand);
		CConstant result = new CConstant();
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_long(x & y);
			}
			else {
				long y = ((Double) rvalue).longValue();
				result.set_long(x & y);
			}
		}
		else {
			long x = ((Double) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_long(x & y);
			}
			else {
				long y = ((Double) rvalue).longValue();
				result.set_long(x & y);
			}
		}
		return result;
	}
	private CConstant bitws_ior(CConstant loperand, CConstant roperand) throws Exception {
		Object lvalue = this.get_number(loperand), rvalue = this.get_number(roperand);
		CConstant result = new CConstant();
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_long(x | y);
			}
			else {
				long y = ((Double) rvalue).longValue();
				result.set_long(x | y);
			}
		}
		else {
			long x = ((Double) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_long(x | y);
			}
			else {
				long y = ((Double) rvalue).longValue();
				result.set_long(x | y);
			}
		}
		return result;
	}
	private CConstant bitws_xor(CConstant loperand, CConstant roperand) throws Exception {
		Object lvalue = this.get_number(loperand), rvalue = this.get_number(roperand);
		CConstant result = new CConstant();
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_long(x ^ y);
			}
			else {
				long y = ((Double) rvalue).longValue();
				result.set_long(x ^ y);
			}
		}
		else {
			long x = ((Double) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_long(x & y);
			}
			else {
				long y = ((Double) rvalue).longValue();
				result.set_long(x ^ y);
			}
		}
		return result;
	}
	private CConstant bitws_lsh(CConstant loperand, CConstant roperand) throws Exception {
		Object lvalue = this.get_number(loperand), rvalue = this.get_number(roperand);
		CConstant result = new CConstant();
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_long(x << y);
			}
			else {
				long y = ((Double) rvalue).longValue();
				result.set_long(x << y);
			}
		}
		else {
			long x = ((Double) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_long(x << y);
			}
			else {
				long y = ((Double) rvalue).longValue();
				result.set_long(x << y);
			}
		}
		return result;
	}
	private CConstant bitws_rsh(CConstant loperand, CConstant roperand) throws Exception {
		Object lvalue = this.get_number(loperand), rvalue = this.get_number(roperand);
		CConstant result = new CConstant();
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_long(x >> y);
			}
			else {
				long y = ((Double) rvalue).longValue();
				result.set_long(x >> y);
			}
		}
		else {
			long x = ((Double) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_long(x >> y);
			}
			else {
				long y = ((Double) rvalue).longValue();
				result.set_long(x >> y);
			}
		}
		return result;
	}
	private boolean get_bool(CConstant constant) throws Exception {
		CConstant result = new CConstant();
		switch(constant.get_type().get_tag()) {
		case c_bool:	result.set_bool(constant.get_bool().booleanValue()); break;
		case c_char:
		case c_uchar:	result.set_bool(constant.get_char().charValue() != '\0'); break;
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	result.set_bool(constant.get_integer().intValue() != 0); break;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	result.set_bool(constant.get_long().longValue() != 0L); break;
		case c_float:	result.set_bool(constant.get_float().floatValue() != 0.0f); break;
		case c_double:
		case c_ldouble:	result.set_bool(constant.get_double().doubleValue() != 0.0); break;
		default: throw new IllegalArgumentException("Invalid constant.");
		}
		return result.get_bool().booleanValue();
	}
	private CConstant logic_and(CConstant loperand, CConstant roperand) throws Exception {
		CConstant result = new CConstant();
		boolean lvalue = this.get_bool(loperand);
		boolean rvalue = this.get_bool(roperand);
		result.set_bool(lvalue && rvalue);
		return result;
	}
	private CConstant logic_ior(CConstant loperand, CConstant roperand) throws Exception {
		CConstant result = new CConstant();
		boolean lvalue = this.get_bool(loperand);
		boolean rvalue = this.get_bool(roperand);
		result.set_bool(lvalue || rvalue);
		return result;
	}
	private CConstant greater_tn(CConstant loperand, CConstant roperand) throws Exception {
		Object lvalue = this.get_number(loperand), rvalue = this.get_number(roperand);
		CConstant result = new CConstant();
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_bool(x > y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_bool(x > y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_bool(x > y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_bool(x > y);
			}
		}
		return result;
	}
	private CConstant greater_eq(CConstant loperand, CConstant roperand) throws Exception {
		Object lvalue = this.get_number(loperand), rvalue = this.get_number(roperand);
		CConstant result = new CConstant();
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_bool(x >= y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_bool(x >= y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_bool(x >= y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_bool(x >= y);
			}
		}
		return result;
	}
	private CConstant smaller_tn(CConstant loperand, CConstant roperand) throws Exception {
		Object lvalue = this.get_number(loperand), rvalue = this.get_number(roperand);
		CConstant result = new CConstant();
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_bool(x < y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_bool(x < y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_bool(x < y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_bool(x < y);
			}
		}
		return result;
	}
	private CConstant smaller_eq(CConstant loperand, CConstant roperand) throws Exception {
		Object lvalue = this.get_number(loperand), rvalue = this.get_number(roperand);
		CConstant result = new CConstant();
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_bool(x <= y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_bool(x <= y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_bool(x <= y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_bool(x <= y);
			}
		}
		return result;
	}
	private CConstant equal_with(CConstant loperand, CConstant roperand) throws Exception {
		Object lvalue = this.get_number(loperand), rvalue = this.get_number(roperand);
		CConstant result = new CConstant();
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_bool(x == y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_bool(x == y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_bool(x == y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_bool(x == y);
			}
		}
		return result;
	}
	private CConstant not_equals(CConstant loperand, CConstant roperand) throws Exception {
		Object lvalue = this.get_number(loperand), rvalue = this.get_number(roperand);
		CConstant result = new CConstant();
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_bool(x != y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_bool(x != y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result.set_bool(x != y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result.set_bool(x != y);
			}
		}
		return result;
	}
	private boolean equal_with(CConstant constant, long value) throws Exception {
		Object number = this.get_number(constant);
		if(number instanceof Long) {
			return ((Long) number).longValue() == value;
		}
		else {
			return ((Double) number).doubleValue() == value;
		}
	}
	
	/* evaluation methods */
	private SedNode eval(SedNode source) throws Exception {
		String key = source.toString();
		if(this.scope != null && this.scope.has(key)) 
			return this.scope.get(key);
		else if(source instanceof SedToken)
			return this.eval_token((SedToken) source);
		else if(source instanceof SedExpression)
			return this.eval_expression((SedExpression) source);
		else if(source instanceof SedStatement)
			return this.eval_statement((SedStatement) source);
		else if(source instanceof SedConstraint)
			return this.eval_constraint((SedConstraint) source);
		else if(source instanceof SedStateError)
			return this.eval_state_error((SedStateError) source);
		else if(source instanceof SedAssertions)
			return this.eval_assertions((SedAssertions) source);
		else
			throw new IllegalArgumentException("Unsupport: " + source.getClass().getSimpleName());
	}
	
	/* token packages */
	private SedNode eval_field(SedField source) throws Exception {
		return source.clone();
	}
	private SedNode eval_label(SedLabel source) throws Exception {
		return source.clone();
	}
	private SedNode eval_operator(SedOperator source) throws Exception {
		return source.clone();
	}
	private SedNode eval_argument_list(SedArgumentList source) throws Exception {
		SedArgumentList arguments = 
				new SedArgumentList(source.get_cir_source());
		for(int k = 0; k < source.number_of_arguments(); k++) {
			arguments.add_child(this.eval(source.get_argument(k)));
		}
		return arguments;
	}
	private SedNode eval_token(SedToken source) throws Exception {
		if(source instanceof SedField)
			return this.eval_field((SedField) source);
		else if(source instanceof SedLabel)
			return this.eval_label((SedLabel) source);
		else if(source instanceof SedOperator)
			return this.eval_operator((SedOperator) source);
		else if(source instanceof SedArgumentList)
			return this.eval_argument_list((SedArgumentList) source);
		else
			throw new IllegalArgumentException("Invalid token: " + source.getClass().getSimpleName());
	}
	
	/* basic expression */
	private SedNode eval_id_expression(SedIdExpression source) throws Exception {
		return source.clone();
	}
	private SedNode eval_constant(SedConstant source) throws Exception {
		return source.clone();
	}
	private SedNode eval_literal(SedLiteral source) throws Exception {
		return source.clone();
	}
	private SedNode eval_default_value(SedDefaultValue source) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		CConstant constant = new CConstant();
		
		if(CTypeAnalyzer.is_boolean(data_type)) {
			constant.set_bool(false);
			return new SedConstant(source.get_cir_source(), data_type, constant);
		}
		else if(CTypeAnalyzer.is_integer(data_type)) {
			constant.set_long(0L);
			return new SedConstant(source.get_cir_source(), data_type, constant);
		}
		else if(CTypeAnalyzer.is_real(data_type)) {
			constant.set_double(0.0);
			return new SedConstant(source.get_cir_source(), data_type, constant);
		}
		else {
			return source.clone();
		}
	}
	private SedNode eval_basic_expression(SedBasicExpression source) throws Exception {
		if(source instanceof SedIdExpression)
			return this.eval_id_expression((SedIdExpression) source);
		else if(source instanceof SedConstant)
			return this.eval_constant((SedConstant) source);
		else if(source instanceof SedLiteral)
			return this.eval_literal((SedLiteral) source);
		else if(source instanceof SedDefaultValue)
			return this.eval_default_value((SedDefaultValue) source);
		else
			throw new IllegalArgumentException("Invalid basic-expression: " + source.getClass().getSimpleName());
	}
	
	/* unary expression */
	private SedNode eval_arith_neg(SedUnaryExpression source) throws Exception {
		SedNode operand = this.eval(source.get_operand());
		if(operand instanceof SedConstant) {
			CConstant constant = this.arith_neg(((SedConstant) operand).get_constant());
			return new SedConstant(source.get_cir_source(), source.get_data_type(), constant);
		}
		else if(operand instanceof SedUnaryExpression) {
			COperator operator = ((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.negative) {
				return ((SedUnaryExpression) operand).get_operand().clone();
			}
			else {
				SedNode expr = new SedUnaryExpression(
						source.get_cir_source(), 
						source.get_data_type(), 
						COperator.negative);
				expr.add_child(operand);
				return expr;
			}
		}
		else if(operand instanceof SedBinaryExpression) {
			COperator operator = ((SedBinaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.arith_sub) {
				SedBinaryExpression expr = new SedBinaryExpression(
						source.get_cir_source(), 
						source.get_data_type(), 
						COperator.arith_sub);
				expr.add_child(((SedBinaryExpression) operand).get_roperand());
				expr.add_child(((SedBinaryExpression) operand).get_loperand());
				return expr;
			}
			else {
				SedNode expr = new SedUnaryExpression(
						source.get_cir_source(), 
						source.get_data_type(), 
						COperator.negative);
				expr.add_child(operand);
				return expr;
			}
		}
		else {
			SedNode expr = new SedUnaryExpression(
					source.get_cir_source(), 
					source.get_data_type(), 
					COperator.negative);
			expr.add_child(operand);
			return expr;
		}
	}
	private SedNode eval_bitws_rsv(SedUnaryExpression source) throws Exception {
		SedNode operand = this.eval(source.get_operand());
		if(operand instanceof SedConstant) {
			CConstant constant = this.bitws_rsv(((SedConstant) operand).get_constant());
			return new SedConstant(source.get_cir_source(), source.get_data_type(), constant);
		}
		else if(operand instanceof SedUnaryExpression) {
			COperator operator = ((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.bit_not) {
				return ((SedUnaryExpression) operand).get_operand().clone();
			}
			else {
				SedUnaryExpression expr = new SedUnaryExpression(
						source.get_cir_source(), 
						source.get_data_type(), 
						COperator.bit_not);
				expr.add_child(operand);
				return expr;
			}
		}
		else {
			SedUnaryExpression expr = new SedUnaryExpression(
					source.get_cir_source(), 
					source.get_data_type(), 
					COperator.bit_not);
			expr.add_child(operand);
			return expr;
		}
	}
	private SedNode eval_logic_not(SedUnaryExpression source) throws Exception {
		SedNode operand = this.eval(source.get_operand());
		if(operand instanceof SedConstant) {
			CConstant constant = this.logic_not(((SedConstant) operand).get_constant());
			return new SedConstant(source.get_cir_source(), source.get_data_type(), constant);
		}
		else if(operand instanceof SedUnaryExpression) {
			COperator operator = ((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.logic_not) {
				return ((SedUnaryExpression) operand).get_operand().clone();
			}
			else {
				SedNode expr = new SedUnaryExpression(
						source.get_cir_source(), 
						source.get_data_type(), 
						COperator.logic_not);
				expr.add_child(operand); 
				return expr;
			}
		}
		else {
			SedNode expr = new SedUnaryExpression(
					source.get_cir_source(), 
					source.get_data_type(), 
					COperator.logic_not);
			expr.add_child(operand); 
			return expr;
		}
	}
	private SedNode eval_address_of(SedUnaryExpression source) throws Exception {
		SedNode operand = this.eval(source.get_operand());
		SedNode expr = new SedUnaryExpression(
				source.get_cir_source(), 
				source.get_data_type(), 
				COperator.address_of);
		expr.add_child(operand);
		return expr;
	}
	private SedNode eval_dereference(SedUnaryExpression source) throws Exception {
		SedNode operand = this.eval(source.get_operand());
		SedNode expr = new SedUnaryExpression(
				source.get_cir_source(), 
				source.get_data_type(), 
				COperator.dereference);
		expr.add_child(operand);
		return expr;
	}
	private SedNode eval_type_cast(SedUnaryExpression source) throws Exception {
		SedNode operand = this.eval(source.get_operand());
		if(operand instanceof SedConstant) {
			CConstant constant = ((SedConstant) operand).get_constant();
			CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
			if(data_type instanceof CBasicType) {
				switch(((CBasicType) data_type).get_tag()) {
				case c_bool:
				{
					return new SedConstant(
							source.get_cir_source(), 
							source.get_data_type(), 
							cast_to_bool(constant));
				}
				case c_char:
				case c_uchar:
				{
					return new SedConstant(
							source.get_cir_source(), 
							source.get_data_type(), 
							cast_to_char(constant));
				}
				case c_short:
				case c_ushort:
				case c_int:
				case c_uint:
				{
					return new SedConstant(
							source.get_cir_source(), 
							source.get_data_type(), 
							cast_to_int(constant));
				}
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:
				{
					return new SedConstant(
							source.get_cir_source(), 
							source.get_data_type(), 
							cast_to_long(constant));
				}
				case c_float:
				{
					return new SedConstant(
							source.get_cir_source(), 
							source.get_data_type(), 
							cast_to_float(constant));
				}
				case c_double:
				case c_ldouble:
				{
					return new SedConstant(
							source.get_cir_source(), 
							source.get_data_type(), 
							cast_to_double(constant));
				}
				default:
				{
					SedNode expr = new SedUnaryExpression(
							source.get_cir_source(), 
							source.get_data_type(), 
							COperator.assign);
					expr.add_child(operand);
					return expr;
				}
				}
			}
			else if(data_type instanceof CEnumType) {
				return new SedConstant(
						source.get_cir_source(), 
						source.get_data_type(), 
						cast_to_int(constant));
			}
			else {
				SedNode expr = new SedUnaryExpression(
						source.get_cir_source(), 
						source.get_data_type(), 
						COperator.assign);
				expr.add_child(operand);
				return expr;
			}
		}
		else {
			SedNode expr = new SedUnaryExpression(
					source.get_cir_source(), 
					source.get_data_type(), 
					COperator.assign);
			expr.add_child(operand);
			return expr;
		}
	}
	private SedNode eval_unary_expression(SedUnaryExpression source) throws Exception {
		switch(source.get_operator().get_operator()) {
		case negative:		return this.eval_arith_neg(source);
		case bit_not:		return this.eval_bitws_rsv(source);
		case logic_not:		return this.eval_logic_not(source);
		case address_of:	return this.eval_address_of(source);
		case dereference:	return this.eval_dereference(source);
		case assign:		return this.eval_type_cast(source);
		default: throw new IllegalArgumentException("Invalid source: null");
		}
	}
	
	/* binary expression */
	private void extend_arith_add_and_sub(SedExpression source, 
			List<SedExpression> pos_operands, 
			List<SedExpression> neg_operands) throws Exception {
		if(source instanceof SedUnaryExpression) {
			COperator operator = ((SedUnaryExpression) source).get_operator().get_operator();
			if(operator == COperator.negative) {
				SedExpression operand = ((SedUnaryExpression) source).get_operand();
				this.extend_arith_add_and_sub(operand, neg_operands, pos_operands);
			}
			else {
				pos_operands.add(source);
			}
		}
		else if(source instanceof SedBinaryExpression) {
			COperator operator = ((SedBinaryExpression) source).get_operator().get_operator();
			if(operator == COperator.arith_add) {
				this.extend_arith_add_and_sub(((SedBinaryExpression) source).get_loperand(), pos_operands, neg_operands);
				this.extend_arith_add_and_sub(((SedBinaryExpression) source).get_roperand(), pos_operands, neg_operands);
			}
			else if(operator == COperator.arith_sub) {
				this.extend_arith_add_and_sub(((SedBinaryExpression) source).get_loperand(), pos_operands, neg_operands);
				this.extend_arith_add_and_sub(((SedBinaryExpression) source).get_roperand(), neg_operands, pos_operands);
			}
			else {
				pos_operands.add(source);
			}
		}
		else {
			pos_operands.add(source);
		}
	}
	private SedExpression accumulate_arith_add(CType data_type, List<SedExpression> operands) throws Exception {
		/* divide the operands into variables part and constant part */
		List<SedExpression> variables = new ArrayList<SedExpression>();
		CConstant constant = new CConstant(); constant.set_int(0);
		for(SedExpression operand : operands) {
			SedExpression new_operand = (SedExpression) this.eval(operand);
			if(new_operand instanceof SedConstant) {
				constant = this.arith_add(constant, ((SedConstant) new_operand).get_constant());
			}
			else {
				variables.add(new_operand);
			}
		}
		
		/* complete the variables list */
		if(!this.equal_with(constant, 0L)) {
			variables.add(new SedConstant(null, data_type, constant));
		}
		
		/* construct the expression */
		if(variables.isEmpty()) {
			return new SedConstant(null, data_type, constant);
		}
		else {
			SedExpression expression = null, new_expression;
			for(SedExpression operand : variables) {
				if(expression == null) {
					expression = operand;
				}
				else {
					new_expression = new SedBinaryExpression(null, 
							data_type, COperator.arith_add);
					new_expression.add_child(expression);
					new_expression.add_child(operand);
					expression = new_expression;
				}
			}
			return expression;
		}
	}
	private SedNode eval_arith_add_and_sub(SedBinaryExpression source) throws Exception {
		List<SedExpression> pos_operands = new ArrayList<SedExpression>();
		List<SedExpression> neg_operands = new ArrayList<SedExpression>();
		this.extend_arith_add_and_sub(source, pos_operands, neg_operands);
		SedExpression loperand = this.accumulate_arith_add(source.get_data_type(), pos_operands);
		SedExpression roperand = this.accumulate_arith_add(source.get_data_type(), neg_operands);
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				CConstant constant = this.arith_sub(
						((SedConstant) loperand).get_constant(), 
						((SedConstant) roperand).get_constant());
				return new SedConstant(source.get_cir_source(), source.get_data_type(), constant);
			}
			else {
				if(this.equal_with(((SedConstant) loperand).get_constant(), 0)) {
					SedExpression expression = new SedUnaryExpression(source.
							get_cir_source(), source.get_data_type(), COperator.negative);
					expression.add_child(roperand);
					return expression;
				}
				else {
					SedExpression expression = new SedBinaryExpression(source.
							get_cir_source(), source.get_data_type(), COperator.arith_sub);
					expression.add_child(loperand);
					expression.add_child(roperand);
					return expression;
				}
			}
		}
		else {
			if(roperand instanceof SedConstant) {
				if(this.equal_with(((SedConstant) roperand).get_constant(), 0)) {
					return loperand;
				}
				else {
					SedExpression expression = new SedBinaryExpression(source.
							get_cir_source(), source.get_data_type(), COperator.arith_sub);
					expression.add_child(loperand);
					expression.add_child(roperand);
					return expression;
				}
			}
			else {
				SedExpression expression = new SedBinaryExpression(source.
						get_cir_source(), source.get_data_type(), COperator.arith_sub);
				expression.add_child(loperand);
				expression.add_child(roperand);
				return expression;
			}
		}
	}
	private void extend_arith_mul_and_div(SedExpression source, 
			List<SedExpression> pos_operands, 
			List<SedExpression> neg_operands) throws Exception {
		if(source instanceof SedBinaryExpression) {
			COperator operator = ((SedBinaryExpression) source).get_operator().get_operator();
			if(operator == COperator.arith_mul) {
				this.extend_arith_mul_and_div(((SedBinaryExpression) source).get_loperand(), pos_operands, neg_operands);
				this.extend_arith_mul_and_div(((SedBinaryExpression) source).get_roperand(), pos_operands, neg_operands);
			}
			else if(operator == COperator.arith_div) {
				this.extend_arith_mul_and_div(((SedBinaryExpression) source).get_loperand(), pos_operands, neg_operands);
				this.extend_arith_mul_and_div(((SedBinaryExpression) source).get_roperand(), neg_operands, pos_operands);
			}
			else {
				pos_operands.add(source);
			}
		}
		else {
			pos_operands.add(source);
		}
	}
	private SedExpression accumulate_arith_mul(CType data_type, List<SedExpression> operands) throws Exception {
		List<SedExpression> variables = new ArrayList<SedExpression>();
		CConstant constant = new CConstant(); constant.set_int(1);
		for(SedExpression operand : operands) {
			SedExpression result = (SedExpression) this.eval(operand);
			if(result instanceof SedConstant) {
				constant = this.arith_mul(constant, ((SedConstant) result).get_constant());
			}
			else {
				variables.add(result);
			}
		}
		if(this.equal_with(constant, 1L) || variables.isEmpty()) {
			variables.add(new SedConstant(null, data_type, constant));
		}
		
		SedExpression expression = null, new_expression;
		for(SedExpression operand : variables) {
			if(expression == null) {
				expression = operand;
			}
			else {
				new_expression = new SedBinaryExpression(null, data_type, COperator.arith_mul);
				new_expression.add_child(expression);
				new_expression.add_child(operand);
				expression = new_expression;
			}
		}
		
		return expression;
	}
	private SedNode eval_arith_mul_and_div(SedBinaryExpression source) throws Exception {
		List<SedExpression> pos_operands = new ArrayList<SedExpression>();
		List<SedExpression> neg_operands = new ArrayList<SedExpression>();
		this.extend_arith_mul_and_div(source, pos_operands, neg_operands);
		SedExpression loperand = this.accumulate_arith_mul(source.get_data_type(), pos_operands);
		SedExpression roperand = this.accumulate_arith_mul(source.get_data_type(), neg_operands);
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				CConstant constant = this.arith_div(
						((SedConstant) loperand).get_constant(), 
						((SedConstant) roperand).get_constant());
				return new SedConstant(source.get_cir_source(), 
								source.get_data_type(), constant);
			}
			else {
				if(this.equal_with(((SedConstant) loperand).get_constant(), 0L)) {
					CConstant constant = new CConstant();
					constant.set_int(0);
					return new SedConstant(source.get_cir_source(),
							source.get_data_type(), constant);
				}
				else {
					SedExpression expression = new SedBinaryExpression(source.
							get_cir_source(), source.get_data_type(), COperator.arith_div);
					expression.add_child(loperand);
					expression.add_child(roperand);
					return expression;
				}
			}
		}
		else {
			if(roperand instanceof SedConstant) {
				if(this.equal_with(((SedConstant) roperand).get_constant(), 1L)) {
					return loperand;
				}
				else if(this.equal_with(((SedConstant) roperand).get_constant(), -1L)) {
					SedExpression expression = new SedUnaryExpression(source.
							get_cir_source(), source.get_data_type(), COperator.negative);
					expression.add_child(loperand);
					return expression; 
				}
				else {
					SedExpression expression = new SedBinaryExpression(source.
							get_cir_source(), source.get_data_type(), COperator.arith_div);
					expression.add_child(loperand);
					expression.add_child(roperand);
					return expression;
				}
			}
			else {
				SedExpression expression = new SedBinaryExpression(source.
						get_cir_source(), source.get_data_type(), COperator.arith_div);
				expression.add_child(loperand);
				expression.add_child(roperand);
				return expression;
			}
		}
	}
	private SedNode eval_arith_mod(SedBinaryExpression source) throws Exception {
		SedExpression loperand = (SedExpression) this.eval(source.get_loperand());
		SedExpression roperand = (SedExpression) this.eval(source.get_roperand());
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				CConstant constant = this.arith_mod(
						((SedConstant) loperand).get_constant(), 
						((SedConstant) roperand).get_constant());
				return new SedConstant(source.
						get_cir_source(), source.get_data_type(), constant);
			}
			else {
				if(this.equal_with(((SedConstant) loperand).get_constant(), 0L)
					|| this.equal_with(((SedConstant) loperand).get_constant(), 1L)) {
					return loperand;
				}
				else {
					SedExpression expression = new SedBinaryExpression(source.
							get_cir_source(), source.get_data_type(), COperator.arith_mod);
					expression.add_child(loperand);
					expression.add_child(roperand);
					return expression;
				}
			}
		}
		else {
			if(roperand instanceof SedConstant) {
				if(this.equal_with(((SedConstant) roperand).get_constant(), 1L)
					|| this.equal_with(((SedConstant) roperand).get_constant(), -1L)) {
					CConstant constant = new CConstant();
					constant.set_int(0);
					return new SedConstant(source.get_cir_source(), source.get_data_type(), constant);
				}
				else {
					SedExpression expression = new SedBinaryExpression(source.
							get_cir_source(), source.get_data_type(), COperator.arith_mod);
					expression.add_child(loperand);
					expression.add_child(roperand);
					return expression;
				}
			}
			else {
				SedExpression expression = new SedBinaryExpression(source.
						get_cir_source(), source.get_data_type(), COperator.arith_mod);
				expression.add_child(loperand);
				expression.add_child(roperand);
				return expression;
			}
		}
	}
	private void extend_binary_expression(SedExpression source, List<SedExpression> operands, COperator operator) throws Exception {
		if(source instanceof SedBinaryExpression) {
			if(((SedBinaryExpression) source).get_operator().get_operator() == operator) {
				this.extend_binary_expression(((SedBinaryExpression) source).get_loperand(), operands, operator);
				this.extend_binary_expression(((SedBinaryExpression) source).get_roperand(), operands, operator);
			}
			else {
				operands.add(source);
			}
		}
		else {
			operands.add(source);
		}
	}
	private SedNode eval_bitws_and(SedBinaryExpression source) throws Exception {
		List<SedExpression> operands = new ArrayList<SedExpression>();
		this.extend_binary_expression(source, operands, COperator.bit_and);
		
		List<SedExpression> variables = new ArrayList<SedExpression>();
		CConstant constant = new CConstant(); constant.set_int(~0);
		for(SedExpression operand : operands) {
			SedExpression result = (SedExpression) this.eval(operand);
			if(result instanceof SedConstant) {
				constant = this.bitws_and(constant, ((SedConstant) result).get_constant());
			}
			else {
				variables.add(result);
			}
		}
		
		if(this.equal_with(constant, 0L)) {
			return new SedConstant(source.get_cir_source(), source.get_data_type(), constant);
		}
		else {
			if(!this.equal_with(constant, ~0L)) {
				variables.add(new SedConstant(null, source.get_data_type(), constant));
			}
			
			SedExpression expression = null, new_expression;
			for(SedExpression operand : variables) {
				if(expression == null) {
					expression = operand;
				}
				else {
					new_expression = new SedBinaryExpression(null, source.get_data_type(), COperator.bit_and);
					new_expression.add_child(expression);
					new_expression.add_child(operand);
					expression = new_expression;
				}
			}
			return expression;
		}
	}
	private SedNode eval_bitws_ior(SedBinaryExpression source) throws Exception {
		List<SedExpression> operands = new ArrayList<SedExpression>();
		this.extend_binary_expression(source, operands, COperator.bit_or);
		
		List<SedExpression> variables = new ArrayList<SedExpression>();
		CConstant constant = new CConstant(); constant.set_int(0);
		for(SedExpression operand : operands) {
			SedExpression result = (SedExpression) this.eval(operand);
			if(result instanceof SedConstant) {
				constant = this.bitws_ior(constant, ((SedConstant) result).get_constant());
			}
			else {
				variables.add(result);
			}
		}
		
		if(this.equal_with(constant, ~0L)) {
			return new SedConstant(source.get_cir_source(), source.get_data_type(), constant);
		}
		else {
			if(!this.equal_with(constant, 0L)) {
				variables.add(new SedConstant(null, source.get_data_type(), constant));
			}
			
			SedExpression expression = null, new_expression;
			for(SedExpression operand : variables) {
				if(expression == null) {
					expression = operand;
				}
				else {
					new_expression = new SedBinaryExpression(null, source.get_data_type(), COperator.bit_or);
					new_expression.add_child(expression);
					new_expression.add_child(operand);
					expression = new_expression;
				}
			}
			return expression;
		}
	}
	private SedNode eval_bitws_xor(SedBinaryExpression source) throws Exception {
		List<SedExpression> operands = new ArrayList<SedExpression>();
		this.extend_binary_expression(source, operands, COperator.bit_xor);
		
		List<SedExpression> variables = new ArrayList<SedExpression>();
		CConstant constant = new CConstant(); constant.set_int(0);
		for(SedExpression operand : operands) {
			SedExpression result = (SedExpression) this.eval(operand);
			if(result instanceof SedConstant) {
				constant = this.bitws_xor(constant, ((SedConstant) result).get_constant());
			}
			else {
				variables.add(result);
			}
		}
		
		if(!this.equal_with(constant, ~0L)) {
			variables.add(new SedConstant(null, source.get_data_type(), constant));
		}
		
		SedExpression expression = null, new_expression;
		for(SedExpression operand : variables) {
			if(expression == null) {
				expression = operand;
			}
			else {
				new_expression = new SedBinaryExpression(null, source.get_data_type(), COperator.bit_xor);
				new_expression.add_child(expression);
				new_expression.add_child(operand);
				expression = new_expression;
			}
		}
		return expression;
	}
	private SedNode eval_bitws_lsh(SedBinaryExpression source) throws Exception {
		SedExpression loperand = (SedExpression) this.eval(source.get_loperand());
		SedExpression roperand = (SedExpression) this.eval(source.get_roperand());
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				CConstant constant = this.bitws_lsh(
						((SedConstant) loperand).get_constant(), 
						((SedConstant) roperand).get_constant());
				return new SedConstant(source.get_cir_source(), source.get_data_type(), constant);
			}
			else {
				if(this.equal_with(((SedConstant) loperand).get_constant(), 0L)) {
					CConstant constant = new CConstant();
					constant.set_int(0);
					return new SedConstant(source.get_cir_source(), source.get_data_type(), constant);
				}
				else {
					SedExpression expression = new SedBinaryExpression(source.
							get_cir_source(), source.get_data_type(), COperator.left_shift);
					expression.add_child(loperand);
					expression.add_child(roperand);
					return expression;
				}
			}
		}
		else {
			if(roperand instanceof SedConstant) {
				if(this.equal_with(((SedConstant) roperand).get_constant(), 0)) {
					return loperand;
				}
				else {
					SedExpression expression = new SedBinaryExpression(source.
							get_cir_source(), source.get_data_type(), COperator.left_shift);
					expression.add_child(loperand);
					expression.add_child(roperand);
					return expression;
				}
			}
			else {
				SedExpression expression = new SedBinaryExpression(source.
						get_cir_source(), source.get_data_type(), COperator.left_shift);
				expression.add_child(loperand);
				expression.add_child(roperand);
				return expression;
			}
		}
	}
	private SedNode eval_bitws_rsh(SedBinaryExpression source) throws Exception {
		SedExpression loperand = (SedExpression) this.eval(source.get_loperand());
		SedExpression roperand = (SedExpression) this.eval(source.get_roperand());
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				CConstant constant = this.bitws_rsh(
						((SedConstant) loperand).get_constant(), 
						((SedConstant) roperand).get_constant());
				return new SedConstant(source.get_cir_source(), source.get_data_type(), constant);
			}
			else {
				if(this.equal_with(((SedConstant) loperand).get_constant(), 0L)) {
					CConstant constant = new CConstant();
					constant.set_int(0);
					return new SedConstant(source.get_cir_source(), source.get_data_type(), constant);
				}
				else {
					SedExpression expression = new SedBinaryExpression(source.
							get_cir_source(), source.get_data_type(), COperator.righ_shift);
					expression.add_child(loperand);
					expression.add_child(roperand);
					return expression;
				}
			}
		}
		else {
			if(roperand instanceof SedConstant) {
				if(this.equal_with(((SedConstant) roperand).get_constant(), 0)) {
					return loperand;
				}
				else {
					SedExpression expression = new SedBinaryExpression(source.
							get_cir_source(), source.get_data_type(), COperator.righ_shift);
					expression.add_child(loperand);
					expression.add_child(roperand);
					return expression;
				}
			}
			else {
				SedExpression expression = new SedBinaryExpression(source.
						get_cir_source(), source.get_data_type(), COperator.righ_shift);
				expression.add_child(loperand);
				expression.add_child(roperand);
				return expression;
			}
		}
	}
	private SedNode eval_logic_and(SedBinaryExpression source) throws Exception {
		List<SedExpression> operands = new ArrayList<SedExpression>();
		this.extend_binary_expression(source, operands, COperator.logic_and);
		
		List<SedExpression> variables = new ArrayList<SedExpression>();
		CConstant constant = new CConstant(); constant.set_bool(true);
		for(SedExpression operand : operands) {
			SedExpression result = (SedExpression) this.eval(operand);
			if(result instanceof SedConstant) {
				constant = this.logic_and(constant, ((SedConstant) result).get_constant());
			}
			else {
				variables.add(result);
			}
		}
		
		if(constant.get_bool().booleanValue()) {
			if(variables.isEmpty()) {
				return new SedConstant(source.get_cir_source(), source.get_data_type(), constant);
			}
			else {
				SedExpression expression = null, new_expression;
				for(SedExpression operand : variables) {
					if(expression == null) {
						expression = operand;
					}
					else {
						new_expression = new SedBinaryExpression(null, 
								source.get_data_type(), COperator.logic_and);
						new_expression.add_child(expression);
						new_expression.add_child(operand);
						expression = new_expression;
					}
				}
				return expression;
			}
		}
		else {
			return new SedConstant(source.get_cir_source(), source.get_data_type(), constant);
		}
	}
	private SedNode eval_logic_ior(SedBinaryExpression source) throws Exception {
		List<SedExpression> operands = new ArrayList<SedExpression>();
		this.extend_binary_expression(source, operands, COperator.logic_or);
		
		List<SedExpression> variables = new ArrayList<SedExpression>();
		CConstant constant = new CConstant(); constant.set_bool(false);
		for(SedExpression operand : operands) {
			SedExpression result = (SedExpression) this.eval(operand);
			if(result instanceof SedConstant) {
				constant = this.logic_ior(constant, ((SedConstant) result).get_constant());
			}
			else {
				variables.add(result);
			}
		}
		
		if(!constant.get_bool().booleanValue()) {
			if(variables.isEmpty()) {
				return new SedConstant(source.get_cir_source(), source.get_data_type(), constant);
			}
			else {
				SedExpression expression = null, new_expression;
				for(SedExpression operand : variables) {
					if(expression == null) {
						expression = operand;
					}
					else {
						new_expression = new SedBinaryExpression(null, 
								source.get_data_type(), COperator.logic_or);
						new_expression.add_child(expression);
						new_expression.add_child(operand);
						expression = new_expression;
					}
				}
				return expression;
			}
		}
		else {
			return new SedConstant(source.get_cir_source(), source.get_data_type(), constant);
		}
	}
	private SedNode eval_greater_tn(SedBinaryExpression source) throws Exception {
		SedExpression loperand = (SedExpression) this.eval(source.get_loperand());
		SedExpression roperand = (SedExpression) this.eval(source.get_roperand());
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				CConstant constant = this.greater_tn(
						((SedConstant) loperand).get_constant(), 
						((SedConstant) roperand).get_constant());
				return new SedConstant(source.get_cir_source(), 
								source.get_data_type(), constant);
			}
		}
		SedExpression expression = new SedBinaryExpression(source.
				get_cir_source(), source.get_data_type(), COperator.greater_tn);
		expression.add_child(loperand); expression.add_child(roperand);
		return expression;
	}
	private SedNode eval_greater_eq(SedBinaryExpression source) throws Exception {
		SedExpression loperand = (SedExpression) this.eval(source.get_loperand());
		SedExpression roperand = (SedExpression) this.eval(source.get_roperand());
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				CConstant constant = this.greater_eq(
						((SedConstant) loperand).get_constant(), 
						((SedConstant) roperand).get_constant());
				return new SedConstant(source.get_cir_source(), 
								source.get_data_type(), constant);
			}
		}
		SedExpression expression = new SedBinaryExpression(source.
				get_cir_source(), source.get_data_type(), COperator.greater_eq);
		expression.add_child(loperand); expression.add_child(roperand);
		return expression;
	}
	private SedNode eval_smaller_tn(SedBinaryExpression source) throws Exception {
		SedExpression loperand = (SedExpression) this.eval(source.get_loperand());
		SedExpression roperand = (SedExpression) this.eval(source.get_roperand());
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				CConstant constant = this.smaller_tn(
						((SedConstant) loperand).get_constant(), 
						((SedConstant) roperand).get_constant());
				return new SedConstant(source.get_cir_source(), 
								source.get_data_type(), constant);
			}
		}
		SedExpression expression = new SedBinaryExpression(source.
				get_cir_source(), source.get_data_type(), COperator.smaller_tn);
		expression.add_child(loperand); expression.add_child(roperand);
		return expression;
	}
	private SedNode eval_smaller_eq(SedBinaryExpression source) throws Exception {
		SedExpression loperand = (SedExpression) this.eval(source.get_loperand());
		SedExpression roperand = (SedExpression) this.eval(source.get_roperand());
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				CConstant constant = this.smaller_eq(
						((SedConstant) loperand).get_constant(), 
						((SedConstant) roperand).get_constant());
				return new SedConstant(source.get_cir_source(), 
								source.get_data_type(), constant);
			}
		}
		SedExpression expression = new SedBinaryExpression(source.
				get_cir_source(), source.get_data_type(), COperator.smaller_eq);
		expression.add_child(loperand); expression.add_child(roperand);
		return expression;
	}
	private SedNode eval_equal_with(SedBinaryExpression source) throws Exception {
		SedExpression loperand = (SedExpression) this.eval(source.get_loperand());
		SedExpression roperand = (SedExpression) this.eval(source.get_roperand());
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				CConstant constant = this.equal_with(
						((SedConstant) loperand).get_constant(), 
						((SedConstant) roperand).get_constant());
				return new SedConstant(source.get_cir_source(), 
								source.get_data_type(), constant);
			}
		}
		SedExpression expression = new SedBinaryExpression(source.
				get_cir_source(), source.get_data_type(), COperator.equal_with);
		expression.add_child(loperand); expression.add_child(roperand);
		return expression;
	}
	private SedNode eval_not_equals(SedBinaryExpression source) throws Exception {
		SedExpression loperand = (SedExpression) this.eval(source.get_loperand());
		SedExpression roperand = (SedExpression) this.eval(source.get_roperand());
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				CConstant constant = this.not_equals(
						((SedConstant) loperand).get_constant(), 
						((SedConstant) roperand).get_constant());
				return new SedConstant(source.get_cir_source(), 
								source.get_data_type(), constant);
			}
		}
		SedExpression expression = new SedBinaryExpression(source.
				get_cir_source(), source.get_data_type(), COperator.not_equals);
		expression.add_child(loperand); expression.add_child(roperand);
		return expression;
	}
	private SedNode eval_binary_expression(SedBinaryExpression source) throws Exception {
		switch(source.get_operator().get_operator()) {
		case arith_add:
		case arith_sub:		return this.eval_arith_add_and_sub(source);
		case arith_mul:
		case arith_div:		return this.eval_arith_mul_and_div(source);
		case arith_mod:		return this.eval_arith_mod(source);
		case bit_and:		return this.eval_bitws_and(source);
		case bit_or:		return this.eval_bitws_ior(source);
		case bit_xor:		return this.eval_bitws_xor(source);
		case left_shift:	return this.eval_bitws_lsh(source);
		case righ_shift:	return this.eval_bitws_rsh(source);
		case logic_and:		return this.eval_logic_and(source);
		case logic_or:		return this.eval_logic_ior(source);
		case greater_tn:	return this.eval_greater_tn(source);
		case greater_eq:	return this.eval_greater_eq(source);
		case smaller_tn:	return this.eval_smaller_tn(source);
		case smaller_eq:	return this.eval_smaller_eq(source);
		case equal_with:	return this.eval_equal_with(source);
		case not_equals:	return this.eval_not_equals(source);
		default: throw new IllegalArgumentException("Invalid source: " + source.generate_code());
		}
	}
	
	/* special expression */
	private SedNode eval_field_expression(SedFieldExpression source) throws Exception {
		SedNode body = this.eval(source.get_body());
		SedNode field = this.eval(source.get_field());
		SedNode expr = new SedFieldExpression(source.
				get_cir_source(), source.get_data_type());
		expr.add_child(body); expr.add_child(field);
		return expr;
	}
	private SedNode eval_initializer_list(SedInitializerList source) throws Exception {
		SedInitializerList list = new SedInitializerList(
				source.get_cir_source(), source.get_data_type());
		for(int k = 0; k < source.number_of_elements(); k++) {
			list.add_child(this.eval(source.get_element(k)));
		}
		return list;
	}
	private SedNode eval_call_expression(SedCallExpression source) throws Exception {
		SedNode function = this.eval(source.get_function());
		SedNode arguments = this.eval(source.get_argument_list());
		SedCallExpression expr = new SedCallExpression(
				source.get_cir_source(), source.get_data_type());
		expr.add_child(function); expr.add_child(arguments);
		
		if(this.scope != null) {
			return this.scope.invocate(expr);
		}
		else {
			return expr;
		}
	}
	private SedNode eval_expression(SedExpression source) throws Exception {
		if(source instanceof SedBasicExpression)
			return this.eval_basic_expression((SedBasicExpression) source);
		else if(source instanceof SedUnaryExpression)
			return this.eval_unary_expression((SedUnaryExpression) source);
		else if(source instanceof SedBinaryExpression)
			return this.eval_binary_expression((SedBinaryExpression) source);
		else if(source instanceof SedFieldExpression)
			return this.eval_field_expression((SedFieldExpression) source);
		else if(source instanceof SedInitializerList)
			return this.eval_initializer_list((SedInitializerList) source);
		else if(source instanceof SedCallExpression)
			return this.eval_call_expression((SedCallExpression) source);
		else 
			throw new IllegalArgumentException("Invalid: " + source.generate_code());
	}
	
	/* statement part */
	private SedNode eval_assign_statement(SedAssignStatement source) throws Exception {
		SedNode label = this.eval(source.get_source_label());
		SedNode lvalue = this.eval(source.get_lvalue());
		SedNode rvalue = this.eval(source.get_rvalue());
		SedNode statement = new SedAssignStatement(source.get_cir_source());
		statement.add_child(label);
		statement.add_child(lvalue);
		statement.add_child(rvalue);
		return statement;
	}
	private SedNode eval_goto_statement(SedGotoStatement source) throws Exception {
		SedNode label = this.eval(source.get_source_label());
		SedNode next_label = this.eval(source.get_target_label());
		SedNode statement = new SedGotoStatement(source.get_cir_source());
		statement.add_child(label);
		statement.add_child(next_label);
		return statement;
	}
	private SedNode eval_if_statement(SedIfStatement source) throws Exception {
		SedNode label = this.eval(source.get_source_label());
		SedNode condition = this.eval(source.get_condition());
		SedNode tlabel = this.eval(source.get_true_label());
		SedNode flabel = this.eval(source.get_false_label());
		SedNode statement = new SedIfStatement(source.get_cir_source());
		statement.add_child(label);
		statement.add_child(condition);
		statement.add_child(tlabel);
		statement.add_child(flabel);
		return statement;
	}
	private SedNode eval_tag_statement(SedTagStatement source) throws Exception {
		return source.clone();
	}
	private SedNode eval_call_statement(SedCallStatement source) throws Exception {
		SedNode label = this.eval(source.get_source_label());
		SedNode function = this.eval(source.get_function());
		SedNode arguments = this.eval(source.get_argument_list());
		SedNode statement = new SedCallStatement(source.get_cir_source());
		statement.add_child(label);
		statement.add_child(function);
		statement.add_child(arguments);
		return statement;
	}
	private SedNode eval_wait_statement(SedWaitStatement source) throws Exception {
		SedNode label = this.eval(source.get_source_label());
		SedNode lvalue = this.eval(source.get_lvalue());
		SedNode rvalue = this.eval(source.get_rvalue());
		SedNode statement = new SedWaitStatement(source.get_cir_source());
		statement.add_child(label);
		statement.add_child(lvalue);
		statement.add_child(rvalue);
		return statement;
	}
	private SedNode eval_statement(SedStatement source) throws Exception {
		if(source instanceof SedAssignStatement)
			return this.eval_assign_statement((SedAssignStatement) source);
		else if(source instanceof SedGotoStatement)
			return this.eval_goto_statement((SedGotoStatement) source);
		else if(source instanceof SedIfStatement)
			return this.eval_if_statement((SedIfStatement) source);
		else if(source instanceof SedTagStatement)
			return this.eval_tag_statement((SedTagStatement) source);
		else if(source instanceof SedCallStatement)
			return this.eval_call_statement((SedCallStatement) source);
		else if(source instanceof SedWaitStatement)
			return this.eval_wait_statement((SedWaitStatement) source);
		else
			throw new IllegalArgumentException(source.generate_code());
	}
	
	/* constraint */
	private SedNode eval_execution_constraint(SedExecutionConstraint source) throws Exception {
		SedNode location = this.eval(source.get_location());
		SedNode statement = this.eval(source.get_statement());
		SedNode times = this.eval(source.get_times());
		SedNode constraint = new SedExecutionConstraint();
		constraint.add_child(location);
		constraint.add_child(statement);
		constraint.add_child(times);
		return constraint;
	}
	private SedNode eval_condition_constraint(SedConditionConstraint source) throws Exception {
		SedNode location = this.eval(source.get_location());
		SedNode condition = this.eval(source.get_condition());
		SedNode constraint = new SedConditionConstraint();
		constraint.add_child(location);
		constraint.add_child(condition);
		return constraint;
	}
	private SedNode eval_constraint(SedConstraint source) throws Exception {
		if(source instanceof SedExecutionConstraint)
			return this.eval_execution_constraint((SedExecutionConstraint) source);
		else if(source instanceof SedConditionConstraint)
			return this.eval_condition_constraint((SedConditionConstraint) source);
		else
			throw new IllegalArgumentException(source.generate_code());
	}
	
	/* state-error */
	private SedNode eval_ins_statement(SedInsStatementError source) throws Exception {
		SedNode location = this.eval(source.get_location());
		SedNode orig_statement = this.eval(source.get_orig_statement());
		SedNode error = new SedInsStatementError();
		error.add_child(location);
		error.add_child(orig_statement);
		return error;
	}
	private SedNode eval_del_statement(SedDelStatementError source) throws Exception {
		SedNode location = this.eval(source.get_location());
		SedNode orig_statement = this.eval(source.get_orig_statement());
		SedNode error = new SedDelStatementError();
		error.add_child(location);
		error.add_child(orig_statement);
		return error;
	}
	private SedNode eval_set_statement(SedSetStatementError source) throws Exception {
		SedNode location = this.eval(source.get_location());
		SedNode orig_statement = this.eval(source.get_orig_statement());
		SedNode muta_statement = this.eval(source.get_muta_statement());
		SedNode error = new SedSetStatementError();
		error.add_child(location);
		error.add_child(orig_statement);
		error.add_child(muta_statement);
		return error;
	}
	private SedNode eval_ins_expression(SedInsExpressionError source) throws Exception {
		SedNode location = this.eval(source.get_location());
		SedNode orig_expr = this.eval(source.get_orig_expression());
		SedNode muta_oprt = this.eval(source.get_muta_operator());
		SedNode error = new SedInsExpressionError();
		error.add_child(location);
		error.add_child(orig_expr);
		error.add_child(muta_oprt);
		return error;
	}
	private SedNode eval_add_expression(SedAddExpressionError source) throws Exception {
		SedNode location = this.eval(source.get_location());
		SedNode orig_expr = this.eval(source.get_orig_expression());
		SedNode muta_oprt = this.eval(source.get_muta_operator());
		SedNode muta_expr = this.eval(source.get_muta_operand());
		SedNode error = new SedInsExpressionError();
		error.add_child(location);
		error.add_child(orig_expr);
		error.add_child(muta_oprt);
		error.add_child(muta_expr);
		return error;
	}
	private SedNode eval_set_expression(SedSetExpressionError source) throws Exception {
		SedNode location = this.eval(source.get_location());
		SedNode orig_expr = this.eval(source.get_orig_expression());
		SedNode muta_expr = this.eval(source.get_muta_expression());
		SedNode error = new SedSetExpressionError();
		error.add_child(location);
		error.add_child(orig_expr);
		error.add_child(muta_expr);
		return error;
	}
	private SedNode eval_state_error(SedStateError source) throws Exception {
		if(source instanceof SedInsStatementError)
			return this.eval_ins_statement((SedInsStatementError) source);
		else if(source instanceof SedDelStatementError)
			return this.eval_del_statement((SedDelStatementError) source);
		else if(source instanceof SedSetStatementError)
			return this.eval_set_statement((SedSetStatementError) source);
		else if(source instanceof SedInsExpressionError)
			return this.eval_ins_expression((SedInsExpressionError) source);
		else if(source instanceof SedAddExpressionError)
			return this.eval_add_expression((SedAddExpressionError) source);
		else if(source instanceof SedSetExpressionError)
			return this.eval_set_expression((SedSetExpressionError) source);
		else
			throw new IllegalArgumentException(source.generate_code());
	}
	
	/* assertions */
	private Boolean get_boolean(SedAssertion assertion) throws Exception {
		if(assertion instanceof SedConditionConstraint) {
			SedExpression condition = ((SedConditionConstraint) assertion).get_condition();
			if(condition instanceof SedConstant) {
				return this.get_bool(((SedConstant) condition).get_constant());
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	private SedNode eval_conjunction(SedConjunction source) throws Exception {
		List<SedAssertion> assertions = new ArrayList<SedAssertion>();
		for(int k = 0; k < source.number_of_assertions(); k++) {
			SedAssertion assertion = 
					(SedAssertion) this.eval(source.get_assertion(k));
			Boolean value = this.get_boolean(assertion);
			if(value != null) {
				if(!value.booleanValue()) {
					SedAssertion result = new SedConditionConstraint();
					result.add_child(source.get_location());
					result.add_child(SedFactory.sed_node(Boolean.FALSE));
					return result;
				}
			}
			else {
				assertions.add(assertion);
			}
		}
		
		if(!assertions.isEmpty()) {
			SedAssertion result = new SedConditionConstraint();
			result.add_child(source.get_location());
			result.add_child(SedFactory.sed_node(Boolean.TRUE));
			return result;
		}
		else if(assertions.size() == 1) {
			return assertions.get(0);
		}
		else {
			SedAssertion result = new SedConjunction();
			result.add_child(source.get_location());
			for(SedAssertion assertion : assertions) {
				result.add_child(assertion);
			}
			return result;
		}
	}
	private SedNode eval_disjunction(SedDisjunction source) throws Exception {
		List<SedAssertion> assertions = new ArrayList<SedAssertion>();
		for(int k = 0; k < source.number_of_assertions(); k++) {
			SedAssertion assertion = 
					(SedAssertion) this.eval(source.get_assertion(k));
			Boolean value = this.get_boolean(assertion);
			if(value != null) {
				if(value.booleanValue()) {
					SedAssertion result = new SedConditionConstraint();
					result.add_child(source.get_location());
					result.add_child(SedFactory.sed_node(Boolean.TRUE));
					return result;
				}
			}
			else {
				assertions.add(assertion);
			}
		}
		
		if(!assertions.isEmpty()) {
			SedAssertion result = new SedConditionConstraint();
			result.add_child(source.get_location());
			result.add_child(SedFactory.sed_node(Boolean.TRUE));
			return result;
		}
		else if(assertions.size() == 1) {
			return assertions.get(0);
		}
		else {
			SedAssertion result = new SedDisjunction();
			result.add_child(source.get_location());
			for(SedAssertion assertion : assertions) {
				result.add_child(assertion);
			}
			return result;
		}
	}
	private SedNode eval_assertions(SedAssertions source) throws Exception {
		if(source instanceof SedConjunction)
			return this.eval_conjunction((SedConjunction) source);
		else if(source instanceof SedDisjunction)
			return this.eval_disjunction((SedDisjunction) source);
		else
			throw new IllegalArgumentException(source.generate_code());
	}
	
}
