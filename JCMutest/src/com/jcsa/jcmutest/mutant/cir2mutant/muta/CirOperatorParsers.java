package com.jcsa.jcmutest.mutant.cir2mutant.muta;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetArithAddParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetArithDivParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetArithModParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetArithMulParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetArithSubParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetAssignParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetBitwsAndParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetBitwsIorParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetBitwsLshParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetBitwsRshParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetBitwsXorParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetEqualWithParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetGreaterEqParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetGreaterTnParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetLogicAndParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetLogicIorParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetNotEqualsParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetSmallerEqParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetSmallerTnParser;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class CirOperatorParsers {

	private static final Map<COperator, CirOperatorParser> map = new HashMap<>();

	static {
		map.put(COperator.assign, 		new CirSetAssignParser());

		map.put(COperator.arith_add, 	new CirSetArithAddParser());
		map.put(COperator.arith_sub, 	new CirSetArithSubParser());
		map.put(COperator.arith_mul, 	new CirSetArithMulParser());
		map.put(COperator.arith_div, 	new CirSetArithDivParser());
		map.put(COperator.arith_mod, 	new CirSetArithModParser());

		map.put(COperator.bit_and, 		new CirSetBitwsAndParser());
		map.put(COperator.bit_or, 		new CirSetBitwsIorParser());
		map.put(COperator.bit_xor, 		new CirSetBitwsXorParser());
		map.put(COperator.left_shift, 	new CirSetBitwsLshParser());
		map.put(COperator.righ_shift, 	new CirSetBitwsRshParser());

		map.put(COperator.logic_and, 	new CirSetLogicAndParser());
		map.put(COperator.logic_or, 	new CirSetLogicIorParser());

		map.put(COperator.greater_tn, 	new CirSetGreaterTnParser());
		map.put(COperator.greater_eq, 	new CirSetGreaterEqParser());
		map.put(COperator.smaller_tn, 	new CirSetSmallerTnParser());
		map.put(COperator.smaller_eq, 	new CirSetSmallerEqParser());
		map.put(COperator.equal_with, 	new CirSetEqualWithParser());
		map.put(COperator.not_equals, 	new CirSetNotEqualsParser());

		map.put(COperator.arith_add_assign, 	new CirSetArithAddParser());
		map.put(COperator.arith_sub_assign, 	new CirSetArithSubParser());
		map.put(COperator.arith_mul_assign, 	new CirSetArithMulParser());
		map.put(COperator.arith_div_assign, 	new CirSetArithDivParser());
		map.put(COperator.arith_mod_assign, 	new CirSetArithModParser());

		map.put(COperator.bit_and_assign, 		new CirSetBitwsAndParser());
		map.put(COperator.bit_or_assign, 		new CirSetBitwsIorParser());
		map.put(COperator.bit_xor_assign, 		new CirSetBitwsXorParser());
		map.put(COperator.left_shift_assign, 	new CirSetBitwsLshParser());
		map.put(COperator.righ_shift_assign, 	new CirSetBitwsRshParser());
	}

	public static boolean generate_infections(AstMutation mutation,
			CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand,
			Map<CirAttribute, CirAttribute> infections) throws Exception {
		AstBinaryExpression location = (AstBinaryExpression) mutation.get_location();
		return map.get(location.get_operator().get_operator()).generate_infections(
					mutation, statement, expression, loperand, roperand, infections);
	}

}
