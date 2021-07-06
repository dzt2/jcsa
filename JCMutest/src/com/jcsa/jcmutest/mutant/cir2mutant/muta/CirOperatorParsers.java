package com.jcsa.jcmutest.mutant.cir2mutant.muta;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirArithAddParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirArithDivParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirArithModParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirArithMulParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirArithSubParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirAssignParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirBitwsAndParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirBitwsIorParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirBitwsLshParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirBitwsRshParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirBitwsXorParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirEqualWithParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirGreaterEqParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirGreaterTnParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirLogicAndParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirLogicIorParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirNotEqualsParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSmallerEqParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSmallerTnParser;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class CirOperatorParsers {
	
	private static final Map<COperator, CirOperatorParser> map = new HashMap<COperator, CirOperatorParser>();
	
	static {
		map.put(COperator.assign, 		new CirAssignParser());
		
		map.put(COperator.arith_add, 	new CirArithAddParser());
		map.put(COperator.arith_sub, 	new CirArithSubParser());
		map.put(COperator.arith_mul, 	new CirArithMulParser());
		map.put(COperator.arith_div, 	new CirArithDivParser());
		map.put(COperator.arith_mod, 	new CirArithModParser());
		
		map.put(COperator.bit_and, 		new CirBitwsAndParser());
		map.put(COperator.bit_or, 		new CirBitwsIorParser());
		map.put(COperator.bit_xor, 		new CirBitwsXorParser());
		map.put(COperator.left_shift, 	new CirBitwsLshParser());
		map.put(COperator.righ_shift, 	new CirBitwsRshParser());
		
		map.put(COperator.logic_and, 	new CirLogicAndParser());
		map.put(COperator.logic_or, 	new CirLogicIorParser());
		
		map.put(COperator.greater_tn, 	new CirGreaterTnParser());
		map.put(COperator.greater_eq, 	new CirGreaterEqParser());
		map.put(COperator.smaller_tn, 	new CirSmallerTnParser());
		map.put(COperator.smaller_eq, 	new CirSmallerEqParser());
		map.put(COperator.equal_with, 	new CirEqualWithParser());
		map.put(COperator.not_equals, 	new CirNotEqualsParser());
		
		map.put(COperator.arith_add_assign, 	new CirArithAddParser());
		map.put(COperator.arith_sub_assign, 	new CirArithSubParser());
		map.put(COperator.arith_mul_assign, 	new CirArithMulParser());
		map.put(COperator.arith_div_assign, 	new CirArithDivParser());
		map.put(COperator.arith_mod_assign, 	new CirArithModParser());
		
		map.put(COperator.bit_and_assign, 		new CirBitwsAndParser());
		map.put(COperator.bit_or_assign, 		new CirBitwsIorParser());
		map.put(COperator.bit_xor_assign, 		new CirBitwsXorParser());
		map.put(COperator.left_shift_assign, 	new CirBitwsLshParser());
		map.put(COperator.righ_shift_assign, 	new CirBitwsRshParser());
	}
	
	public static boolean generate_infections(AstMutation mutation, 
			CirStatement statement, CirExpression expression, 
			CirExpression loperand, CirExpression roperand,
			Map<SymCondition, SymCondition> infections) throws Exception {
		AstBinaryExpression location = (AstBinaryExpression) mutation.get_location();
		return map.get(location.get_operator().get_operator()).generate_infections(
					mutation, statement, expression, loperand, roperand, infections);
	}
	
}
