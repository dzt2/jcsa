package com.jcsa.jcmutest.mutant.sed2mutant.util;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.cons.SedConditionConstraint;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.cons.SedConjunctConstraints;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.cons.SedConstraint;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.cons.SedDisjunctConstraints;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.cons.SedExecutionConstraint;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedBinaryExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedConstant;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedDefaultValue;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedIdExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedUnaryExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It provides interfaces to construct the SedNode in a user-friendly way.
 * @author yukimula
 *
 */
public class SedFactory {
	
	/**
	 * @param source [bool|char|short|int|long|float|
	 * 					double|AstNode|CirNode|SedNode]
	 * @return
	 * @throws Exception
	 */
	public static SedNode sed_node(Object source) throws Exception {
		if(source instanceof Boolean) {
			CConstant constant = new CConstant();
			constant.set_bool(((Boolean) source).booleanValue());
			return new SedConstant(null, constant.get_type(), constant);
		}
		else if(source instanceof Character) {
			CConstant constant = new CConstant();
			constant.set_char(((Character) source).charValue());
			return new SedConstant(null, constant.get_type(), constant);
		}
		else if(source instanceof Short) {
			CConstant constant = new CConstant();
			constant.set_int(((Short) source).shortValue());
			return new SedConstant(null, constant.get_type(), constant);
		}
		else if(source instanceof Integer) {
			CConstant constant = new CConstant();
			constant.set_int(((Integer) source).intValue());
			return new SedConstant(null, constant.get_type(), constant);
		}
		else if(source instanceof Long) {
			CConstant constant = new CConstant();
			constant.set_long(((Long) source).longValue());
			return new SedConstant(null, constant.get_type(), constant);
		}
		else if(source instanceof Float) {
			CConstant constant = new CConstant();
			constant.set_float(((Float) source).floatValue());
			return new SedConstant(null, constant.get_type(), constant);
		}
		else if(source instanceof Double) {
			CConstant constant = new CConstant();
			constant.set_double(((Double) source).doubleValue());
			return new SedConstant(null, constant.get_type(), constant);
		}
		else if(source instanceof AstNode) {
			return SedParser.parse((AstNode) source);
		}
		else if(source instanceof CirNode) {
			return SedParser.parse((CirNode) source);
		}
		else if(source instanceof SedNode) {
			return (SedNode) source;
		}
		else {
			throw new IllegalArgumentException("Invalid: " + source);
		}
	}
	
	public static SedIdExpression any_pos_value(CType data_type) throws Exception {
		return new SedIdExpression(null, data_type, "#POS");
	}
	public static SedIdExpression any_neg_value(CType data_type) throws Exception {
		return new SedIdExpression(null, data_type, "#NEG");
	}
	
	public static SedUnaryExpression arith_neg(CType data_type, Object operand) throws Exception {
		SedUnaryExpression expr = new 
				SedUnaryExpression(null, data_type, COperator.negative);
		expr.add_child(sed_node(operand)); return expr;
	}
	public static SedUnaryExpression bitws_rsv(CType data_type, Object operand) throws Exception {
		SedUnaryExpression expr = new 
				SedUnaryExpression(null, data_type, COperator.bit_not);
		expr.add_child(sed_node(operand)); return expr;
	}
	public static SedUnaryExpression logic_not(Object operand) throws Exception {
		SedUnaryExpression expr = new SedUnaryExpression(null, 
				CBasicTypeImpl.bool_type, COperator.logic_not);
		expr.add_child(sed_node(operand)); return expr;
	}
	public static SedUnaryExpression address_of(CType data_type, Object operand) throws Exception {
		SedUnaryExpression expr = new 
				SedUnaryExpression(null, data_type, COperator.address_of);
		expr.add_child(sed_node(operand)); return expr;
	}
	public static SedUnaryExpression dereference(CType data_type, Object operand) throws Exception {
		SedUnaryExpression expr = new 
				SedUnaryExpression(null, data_type, COperator.dereference);
		expr.add_child(sed_node(operand)); return expr;
	}
	public static SedUnaryExpression type_cast(CType data_type, Object operand) throws Exception {
		SedUnaryExpression expr = new 
				SedUnaryExpression(null, data_type, COperator.assign);
		expr.add_child(sed_node(operand)); return expr;
	}
	
	public static SedBinaryExpression arith_add(CType data_type, Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, data_type, COperator.arith_add);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	public static SedBinaryExpression arith_sub(CType data_type, Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, data_type, COperator.arith_sub);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	public static SedBinaryExpression arith_mul(CType data_type, Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, data_type, COperator.arith_mul);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	public static SedBinaryExpression arith_div(CType data_type, Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, data_type, COperator.arith_div);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	public static SedBinaryExpression arith_mod(CType data_type, Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, data_type, COperator.arith_mod);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	
	public static SedBinaryExpression bitws_and(CType data_type, Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, data_type, COperator.bit_and);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	public static SedBinaryExpression bitws_ior(CType data_type, Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, data_type, COperator.bit_or);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	public static SedBinaryExpression bitws_xor(CType data_type, Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, data_type, COperator.bit_xor);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	public static SedBinaryExpression bitws_lsh(CType data_type, Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, data_type, COperator.left_shift);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	public static SedBinaryExpression bitws_rsh(CType data_type, Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, data_type, COperator.righ_shift);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	
	public static SedBinaryExpression logic_and(Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, CBasicTypeImpl.bool_type, COperator.logic_and);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	public static SedBinaryExpression logic_ior(Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, CBasicTypeImpl.bool_type, COperator.logic_or);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	
	public static SedBinaryExpression greater_tn(Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, CBasicTypeImpl.bool_type, COperator.greater_tn);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	public static SedBinaryExpression greater_eq(Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, CBasicTypeImpl.bool_type, COperator.greater_eq);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	public static SedBinaryExpression smaller_tn(Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, CBasicTypeImpl.bool_type, COperator.smaller_tn);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	public static SedBinaryExpression smaller_eq(Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, CBasicTypeImpl.bool_type, COperator.smaller_eq);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	public static SedBinaryExpression equal_with(Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, CBasicTypeImpl.bool_type, COperator.equal_with);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	public static SedBinaryExpression not_equals(Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expr = new SedBinaryExpression(
					null, CBasicTypeImpl.bool_type, COperator.not_equals);
		expr.add_child(sed_node(loperand)); 
		expr.add_child(sed_node(roperand)); 
		return expr;
	}
	
	public static SedDefaultValue any_value(CType data_type) throws Exception {
		data_type = CTypeAnalyzer.get_value_type(data_type);
		if(CTypeAnalyzer.is_boolean(data_type)) {
			return new SedDefaultValue(null, 
					data_type, SedDefaultValue.AnyBoolean);
		}
		else if(CTypeAnalyzer.is_character(data_type)) {
			return new SedDefaultValue(null, 
					data_type, SedDefaultValue.AnyCharacter);
		}
		else if(CTypeAnalyzer.is_number(data_type)) {
			return new SedDefaultValue(null, 
					data_type, SedDefaultValue.AnyNumber);
		}
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			return new SedDefaultValue(null, 
					data_type, SedDefaultValue.AnyAddress);
		}
		else {
			return new SedDefaultValue(null, 
					data_type, SedDefaultValue.AnySequence);
		}
	}
	
	public static SedExecutionConstraint execution_constraint(CirStatement statement, int times) throws Exception {
		SedExecutionConstraint constraint = new SedExecutionConstraint();
		constraint.add_child(new SedLabel(null, statement));
		constraint.add_child(SedFactory.sed_node(Long.valueOf(times)));
		return constraint;
	}
	public static SedConditionConstraint condition_constraint(CirStatement statement,
			SedExpression condition) throws Exception {
		SedConditionConstraint constraint = new SedConditionConstraint();
		constraint.add_child(new SedLabel(null, statement));
		constraint.add_child(condition);
		return constraint;
	}
	public static SedConjunctConstraints conjunct_constraints(Iterable<SedConstraint> constraints) throws Exception {
		SedConjunctConstraints result = new SedConjunctConstraints();
		for(SedConstraint constraint : constraints) {
			result.add_child(constraint);
		}
		return result;
	}
	public static SedDisjunctConstraints disjunct_constraints(Iterable<SedConstraint> constraints) throws Exception {
		SedDisjunctConstraints result = new SedDisjunctConstraints();
		for(SedConstraint constraint : constraints) {
			result.add_child(constraint);
		}
		return result;
	}
	
}
