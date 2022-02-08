package com.jcsa.jcmutest.mutant.sta2mutant.muta;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateArithAddParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateArithDivParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateArithModParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateArithMulParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateArithSubParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateAssignmentParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateBitwsAndParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateBitwsIorParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateBitwsLshParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateBitwsRshParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateBitwsXorParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateEqualWithParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateGreaterEqParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateGreaterTnParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateLogicAndParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateLogicIorParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateNotEqualsParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateSmallerEqParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt.StateSmallerTnParser;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It integerates the operator-parser for StateMutation
 * 
 * @author yukimula
 *
 */
public final class StateOperatorParsers {
	
	/** mapping from mutation operator to the corresponding parser on state mutation **/
	private static final Map<COperator, StateOperatorParser> parsers = new HashMap<COperator, StateOperatorParser>();
	
	/** constructors **/
	static {
		parsers.put(COperator.arith_add,			new StateArithAddParser());
		parsers.put(COperator.arith_sub,			new StateArithSubParser());
		parsers.put(COperator.arith_mul,			new StateArithMulParser());
		parsers.put(COperator.arith_div,			new StateArithDivParser());
		parsers.put(COperator.arith_mod,			new StateArithModParser());
		
		parsers.put(COperator.bit_and, 				new StateBitwsAndParser());
		parsers.put(COperator.bit_or, 				new StateBitwsIorParser());
		parsers.put(COperator.bit_xor, 				new StateBitwsXorParser());
		parsers.put(COperator.left_shift,			new StateBitwsLshParser());
		parsers.put(COperator.righ_shift, 			new StateBitwsRshParser());
		
		parsers.put(COperator.logic_and, 			new StateLogicAndParser());
		parsers.put(COperator.logic_or, 			new StateLogicIorParser());
		
		parsers.put(COperator.greater_tn, 			new StateGreaterTnParser());
		parsers.put(COperator.greater_eq, 			new StateGreaterEqParser());
		parsers.put(COperator.smaller_tn, 			new StateSmallerTnParser());
		parsers.put(COperator.smaller_eq, 			new StateSmallerEqParser());
		parsers.put(COperator.equal_with, 			new StateEqualWithParser());
		parsers.put(COperator.not_equals, 			new StateNotEqualsParser());
		
		parsers.put(COperator.assign, 				new StateAssignmentParser());	
		
		parsers.put(COperator.arith_add_assign,		new StateArithAddParser());
		parsers.put(COperator.arith_sub_assign,		new StateArithSubParser());
		parsers.put(COperator.arith_mul_assign,		new StateArithMulParser());
		parsers.put(COperator.arith_div_assign,		new StateArithDivParser());
		parsers.put(COperator.arith_mod_assign,		new StateArithModParser());
		
		parsers.put(COperator.bit_and_assign, 		new StateBitwsAndParser());
		parsers.put(COperator.bit_or_assign, 		new StateBitwsIorParser());
		parsers.put(COperator.bit_xor_assign, 		new StateBitwsXorParser());
		parsers.put(COperator.left_shift_assign,	new StateBitwsLshParser());
		parsers.put(COperator.righ_shift_assign, 	new StateBitwsRshParser());
	}
	
	/**
	 * @param mutation		syntactic mutation to be parsed (operator-mutation)
	 * @param execution		the execution point where the state is mutated
	 * @param expression	the original expression to be injected with error
	 * @param loperand		the left-operand of the binary expression
	 * @param roperand		the right-operand of the binary expression
	 * @param infections	mapping from abstract state error to constraints
	 * @return
	 * @throws Exception
	 */
	public static boolean parse(AstMutation mutation, 
			CirExecution execution, CirExpression expression, 
			CirExpression loperand, CirExpression roperand,
			Map<CirAbstErrorState, CirConditionState> infections) throws Exception {
		AstBinaryExpression location = (AstBinaryExpression) mutation.get_location();
		StateOperatorParser parser = parsers.get(location.get_operator().get_operator());
		if(parser == null) {
			throw new IllegalArgumentException("Unsupport: " + location.generate_code());
		}
		else {
			return parser.parse(mutation, execution, expression, loperand, roperand, infections);
		}
	}
	
}
