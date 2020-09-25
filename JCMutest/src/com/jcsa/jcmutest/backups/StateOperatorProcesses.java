package com.jcsa.jcmutest.backups;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class StateOperatorProcesses {
	
	/** mapping from the mutation operator to operator processes **/
	private static final Map<COperator, StateOperatorProcess> map =
					new HashMap<COperator, StateOperatorProcess>();
	
	/** construct singleton instances w.r.t. the operators used **/
	static {
		map.put(COperator.assign, 		new StateAssignProcess());
		
		map.put(COperator.arith_add, 	new StateArithAddProcess());
		map.put(COperator.arith_sub, 	new StateArithSubProcess());
		map.put(COperator.arith_mul, 	new StateArithMulProcess());
		map.put(COperator.arith_div, 	new StateArithDivProcess());
		map.put(COperator.arith_mod, 	new StateArithModProcess());
		
		map.put(COperator.bit_and, 		new StateBitwsAndProcess());
		map.put(COperator.bit_or, 		new StateBitwsIorProcess());
		map.put(COperator.bit_xor, 		new StateBitwsXorProcess());
		map.put(COperator.left_shift, 	new StateBitwsLshProcess());
		map.put(COperator.righ_shift, 	new StateBitwsRshProcess());
		
		map.put(COperator.logic_and, 	new StateLogicAndProcess());
		map.put(COperator.logic_or, 	new StateLogicIorProcess());
		
		map.put(COperator.greater_tn, 	new StateGreaterTnProcess());
		map.put(COperator.greater_eq, 	new StateGreaterEqProcess());
		map.put(COperator.smaller_tn, 	new StateSmallerTnProcess());
		map.put(COperator.smaller_eq, 	new StateSmallerEqProcess());
		map.put(COperator.equal_with, 	new StateEqualWithProcess());
		map.put(COperator.not_equals, 	new StateNotEqualsProcess());
		
		map.put(COperator.arith_add_assign, 	new StateArithAddProcess());
		map.put(COperator.arith_sub_assign, 	new StateArithSubProcess());
		map.put(COperator.arith_mul_assign, 	new StateArithMulProcess());
		map.put(COperator.arith_div_assign, 	new StateArithDivProcess());
		map.put(COperator.arith_mod_assign, 	new StateArithModProcess());
		
		map.put(COperator.bit_and_assign, 		new StateBitwsAndProcess());
		map.put(COperator.bit_or_assign, 		new StateBitwsIorProcess());
		map.put(COperator.bit_xor_assign, 		new StateBitwsXorProcess());
		map.put(COperator.left_shift_assign, 	new StateBitwsLshProcess());
		map.put(COperator.righ_shift_assign, 	new StateBitwsRshProcess());
	}
	
	public static boolean generate_infections(AstMutation mutation, 
			CirStatement statement, CirExpression expression, 
			CirExpression loperand, CirExpression roperand,
			StateMutation state_mutation) throws Exception {
		AstBinaryExpression location = (AstBinaryExpression) mutation.get_location();
		return map.get(location.get_operator().get_operator()).generate_infections(
					mutation, statement, expression, loperand, roperand, state_mutation);
	}
	
}
