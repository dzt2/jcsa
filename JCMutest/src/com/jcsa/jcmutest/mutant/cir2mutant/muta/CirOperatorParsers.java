package com.jcsa.jcmutest.mutant.cir2mutant.muta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirArithAddParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirArithDivParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirArithModParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirArithMulParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirArithSubParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirAssignmentParser;
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
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.lexical.COperator;

public final class CirOperatorParsers {
	
	/** mapping from mutation operator to the corresponding parser on Cir mutation **/
	private static final Map<COperator, CirOperatorParser> parsers = new HashMap<COperator, CirOperatorParser>();
	static {
		parsers.put(COperator.arith_add,			new CirArithAddParser());
		parsers.put(COperator.arith_sub,			new CirArithSubParser());
		parsers.put(COperator.arith_mul,			new CirArithMulParser());
		parsers.put(COperator.arith_div,			new CirArithDivParser());
		parsers.put(COperator.arith_mod,			new CirArithModParser());
		
		parsers.put(COperator.bit_and, 				new CirBitwsAndParser());
		parsers.put(COperator.bit_or, 				new CirBitwsIorParser());
		parsers.put(COperator.bit_xor, 				new CirBitwsXorParser());
		parsers.put(COperator.left_shift,			new CirBitwsLshParser());
		parsers.put(COperator.righ_shift, 			new CirBitwsRshParser());
		
		parsers.put(COperator.logic_and, 			new CirLogicAndParser());
		parsers.put(COperator.logic_or, 			new CirLogicIorParser());
		
		parsers.put(COperator.greater_tn, 			new CirGreaterTnParser());
		parsers.put(COperator.greater_eq, 			new CirGreaterEqParser());
		parsers.put(COperator.smaller_tn, 			new CirSmallerTnParser());
		parsers.put(COperator.smaller_eq, 			new CirSmallerEqParser());
		parsers.put(COperator.equal_with, 			new CirEqualWithParser());
		parsers.put(COperator.not_equals, 			new CirNotEqualsParser());
		
		parsers.put(COperator.assign, 				new CirAssignmentParser());	
		
		parsers.put(COperator.arith_add_assign,		new CirArithAddParser());
		parsers.put(COperator.arith_sub_assign,		new CirArithSubParser());
		parsers.put(COperator.arith_mul_assign,		new CirArithMulParser());
		parsers.put(COperator.arith_div_assign,		new CirArithDivParser());
		parsers.put(COperator.arith_mod_assign,		new CirArithModParser());
		
		parsers.put(COperator.bit_and_assign, 		new CirBitwsAndParser());
		parsers.put(COperator.bit_or_assign, 		new CirBitwsIorParser());
		parsers.put(COperator.bit_xor_assign, 		new CirBitwsXorParser());
		parsers.put(COperator.left_shift_assign,	new CirBitwsLshParser());
		parsers.put(COperator.righ_shift_assign, 	new CirBitwsRshParser());
	}
	/**
	 * @param mutation		the syntactic mutation of operator-replacement
	 * @param execution		the execution point where the expression is used
	 * @param expression	the expression to be replaced with mutated value
	 * @param loperand		the left-operand in the given expression
	 * @param roperand		the right-operand in the given expression
	 * @param i_states		to preserve the infection states
	 * @param p_states		to preserve the propagation states
	 * @return				whether the parsing is successful
	 * @throws Exception
	 */
	public static boolean parse(AstMutation mutation, 
			CirExecution execution, CirExpression expression, 
			CirExpression loperand, CirExpression roperand,
			List<CirConditionState> i_states,
			List<CirAbstErrorState> p_states) throws Exception {
		AstBinaryExpression location = (AstBinaryExpression) mutation.get_location();
		CirOperatorParser parser = parsers.get(location.get_operator().get_operator());
		if(parser == null) {
			throw new IllegalArgumentException("Unsupport: " + location.generate_code());
		}
		else {
			return parser.parse(mutation, execution, expression, loperand, roperand, i_states, p_states);
		}
	}

}
