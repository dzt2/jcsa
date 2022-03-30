package com.jcsa.jcmutest.mutant.fil2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OXXAMutationTextParser extends MutationTextParser {

	@Override
	protected AstNode get_location(AstMutation source) throws Exception {
		return source.get_location();
	}

	@Override
	protected String get_muta_code(AstMutation source, AstNode location) throws Exception {
		AstBinaryExpression expression = (AstBinaryExpression) location;
		String loperand = "(" + CTypeAnalyzer.get_expression_of(
				expression.get_loperand()).generate_code() + ")";
		String roperand = "(" + CTypeAnalyzer.get_expression_of(
				expression.get_roperand()).generate_code() + ")";
		COperator operator = (COperator) source.get_parameter();

		String op;
		switch(operator) {
		case assign:			op = " = ";			break;
		case arith_add_assign:	op = " += ";		break;
		case arith_sub_assign:	op = " -= ";		break;
		case arith_mul_assign:	op = " *= ";		break;
		case arith_div_assign:	op = " /= ";		break;
		case arith_mod_assign:	op = " %= ";		break;
		case bit_and_assign:	op = " &= ";		break;
		case bit_or_assign:		op = " |= ";		break;
		case bit_xor_assign:	op = " ^= ";		break;
		case left_shift_assign:	op = " <<= ";		break;
		case righ_shift_assign:	op = " >>= ";		break;
		default: throw new IllegalArgumentException(source.toString());
		}

		return loperand + op + roperand;
	}

}
