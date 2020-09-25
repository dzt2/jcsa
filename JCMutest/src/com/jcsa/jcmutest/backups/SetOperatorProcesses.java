package com.jcsa.jcmutest.backups;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SetOperatorProcesses {
	
	private static final Map<COperator, SetOperatorProcess> 
		map = new HashMap<COperator, SetOperatorProcess>();
	static {
		map.put(COperator.assign, 		new SetAssignProcess());
		
		map.put(COperator.arith_add, 	new SetArithAddProcess());
		map.put(COperator.arith_sub, 	new SetArithSubProcess());
		map.put(COperator.arith_mul, 	new SetArithMulProcess());
		map.put(COperator.arith_div, 	new SetArithDivProcess());
		map.put(COperator.arith_mod, 	new SetArithModProcess());
		
		map.put(COperator.bit_and, 		new SetBitwsAndProcess());
		map.put(COperator.bit_or, 		new SetBitwsIorProcess());
		map.put(COperator.bit_xor, 		new SetBitwsXorProcess());
		map.put(COperator.left_shift, 	new SetBitwsLshProcess());
		map.put(COperator.righ_shift, 	new SetBitwsRshProcess());
		
		map.put(COperator.logic_and, 	new SetLogicAndProcess());
		map.put(COperator.logic_or, 	new SetLogicIorProcess());
		
		map.put(COperator.greater_tn, 	new SetGreaterTnProcess());
		map.put(COperator.greater_eq, 	new SetGreaterEqProcess());
		map.put(COperator.smaller_tn, 	new SetSmallerTnProcess());
		map.put(COperator.smaller_eq, 	new SetSmallerEqProcess());
		map.put(COperator.equal_with, 	new SetEqualWithProcess());
		map.put(COperator.not_equals, 	new SetNotEqualsProcess());
		
		map.put(COperator.arith_add_assign, 	new SetArithAddProcess());
		map.put(COperator.arith_sub_assign, 	new SetArithSubProcess());
		map.put(COperator.arith_mul_assign, 	new SetArithMulProcess());
		map.put(COperator.arith_div_assign, 	new SetArithDivProcess());
		map.put(COperator.arith_mod_assign, 	new SetArithModProcess());
		
		map.put(COperator.bit_and_assign, 		new SetBitwsAndProcess());
		map.put(COperator.bit_or_assign, 		new SetBitwsIorProcess());
		map.put(COperator.bit_xor_assign, 		new SetBitwsXorProcess());
		map.put(COperator.left_shift_assign, 	new SetBitwsLshProcess());
		map.put(COperator.righ_shift_assign, 	new SetBitwsRshProcess());
	}
	
	public static boolean generate_infections(AstMutation mutation, 
			CirStatement statement, CirExpression expression, 
			CirExpression loperand, CirExpression roperand,
			SecInfection infection) throws Exception {
		AstBinaryExpression location = (AstBinaryExpression) mutation.get_location();
		return map.get(location.get_operator().get_operator()).generate_infections(
					mutation, statement, expression, loperand, roperand, infection);
	}
	
}
