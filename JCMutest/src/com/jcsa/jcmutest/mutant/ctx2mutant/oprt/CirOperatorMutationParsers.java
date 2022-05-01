package com.jcsa.jcmutest.mutant.ctx2mutant.oprt;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutation;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public final class CirOperatorMutationParsers {
	
	private static final Map<COperator, CirOperatorMutationParser> parsers = 
						new HashMap<COperator, CirOperatorMutationParser>();
	
	static {
		parsers.put(COperator.assign, 			new CirAssignToMutationParser());
		
		parsers.put(COperator.arith_add, 		new CirArithAddMutationParser());
		parsers.put(COperator.arith_sub, 		new CirArithSubMutationParser());
		parsers.put(COperator.arith_mul, 		new CirArithMulMutationParser());
		parsers.put(COperator.arith_div, 		new CirArithDivMutationParser());
		parsers.put(COperator.arith_mod, 		new CirArithModMutationParser());
		
		parsers.put(COperator.bit_and, 			new CirBitwsAndMutationParser());
		parsers.put(COperator.bit_or, 			new CirBitwsIorMutationParser());
		parsers.put(COperator.bit_xor, 			new CirBitwsXorMutationParser());
		parsers.put(COperator.left_shift, 		new CirBitwsLshMutationParser());
		parsers.put(COperator.righ_shift, 		new CirBitwsRshMutationParser());
		
		parsers.put(COperator.logic_and, 		new CirLogicAndMutationParser());
		parsers.put(COperator.logic_or, 		new CirLogicIorMutationParser());
		
		parsers.put(COperator.greater_tn, 		new CirGreaterTnMutationParser());
		parsers.put(COperator.greater_eq, 		new CirGreaterEqMutationParser());
		parsers.put(COperator.smaller_tn, 		new CirSmallerTnMutationParser());
		parsers.put(COperator.smaller_eq, 		new CirSmallerEqMutationParser());
		parsers.put(COperator.equal_with, 		new CirEqualWithMutationParser());
		parsers.put(COperator.not_equals, 		new CirNotEqualsMutationParser());
		
		parsers.put(COperator.arith_add_assign, new CirAssignAddMutationParser());
		parsers.put(COperator.arith_sub_assign, new CirAssignSubMutationParser());
		parsers.put(COperator.arith_mul_assign, new CirAssignMulMutationParser());
		parsers.put(COperator.arith_div_assign, new CirAssignDivMutationParser());
		parsers.put(COperator.arith_mod_assign, new CirAssignModMutationParser());
		
		parsers.put(COperator.bit_and_assign, 	new CirAssignAndMutationParser());
		parsers.put(COperator.bit_or_assign, 	new CirAssignIorMutationParser());
		parsers.put(COperator.bit_xor_assign, 	new CirAssignXorMutationParser());
		parsers.put(COperator.left_shift_assign,new CirAssignLshMutationParser());
		parsers.put(COperator.righ_shift_assign,new CirAssignRshMutationParser());
		
	}
	
	/**
	 * @param output
	 * @return It complements the context-based mutation for operator-based mutation operator
	 * @throws Exception
	 */
	public static boolean parse_mutation(ContextMutation output) throws Exception {
		if(output == null) {
			throw new IllegalArgumentException("Invalid output: null");
		}
		else if(output.get_location().get_ast_source() instanceof AstBinaryExpression) {
			AstBinaryExpression expression = (AstBinaryExpression) output.get_location().get_ast_source();
			COperator source_operator = expression.get_operator().get_operator();
			if(parsers.containsKey(source_operator)) {
				return parsers.get(source_operator).parse(output);
			}
			else {
				throw new IllegalArgumentException("Undefined: " + source_operator);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + output.get_location());
		}
	}
	
}
