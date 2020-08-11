package com.jcsa.jcmutest.mutant.ast2mutant.generate.unary;

import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UNOIMutationGenerator extends AstMutationGenerator {
	
	@Override
	protected boolean is_available(AstNode location) throws Exception {
		return !this.is_left_reference(location) && 
				this.is_numeric_expression(location);
	}
	
	/**
	 * bitws_unary_expr
	 * bitws_binary_expr
	 * or its operand
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private boolean is_bitws_context(AstExpression expression) throws Exception {
		AstNode parent = CTypeAnalyzer.get_parent_of_expression(expression);
		if(expression instanceof AstBitwiseUnaryExpression
			|| expression instanceof AstBitwiseBinaryExpression
			|| expression instanceof AstShiftBinaryExpression) {
			return true;
		}
		else if(parent instanceof AstBitwiseUnaryExpression
				|| parent instanceof AstBitwiseBinaryExpression
				|| parent instanceof AstShiftBinaryExpression
				|| parent instanceof AstBitwiseAssignExpression
				|| parent instanceof AstShiftAssignExpression) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		CType data_type = CTypeAnalyzer.
				get_value_type(expression.get_value_type());
		expression = CTypeAnalyzer.get_expression_of(expression);
		
		if(this.is_condition_expression(expression)) {
			mutations.add(AstMutations.UNOI(expression, COperator.logic_not));
		}
		else if(this.is_bitws_context(expression)) {
			mutations.add(AstMutations.UNOI(expression, COperator.bit_not));
		}
		else if(expression instanceof AstConstant) {
			mutations.add(AstMutations.UNOI(expression, COperator.negative));
		}
		else if(CTypeAnalyzer.is_unsigned(data_type)) {
			mutations.add(AstMutations.UNOI(expression, COperator.negative));
		}
		else {
			mutations.add(AstMutations.UNOI(expression, COperator.negative));
			mutations.add(AstMutations.UNOI(expression, COperator.positive));
			mutations.add(AstMutations.UNOI(expression, COperator.assign));
		}
	}
	
}
