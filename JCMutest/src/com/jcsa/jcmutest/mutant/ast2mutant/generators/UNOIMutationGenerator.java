package com.jcsa.jcmutest.mutant.ast2mutant.generators;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class UNOIMutationGenerator extends AstMutationGenerator {
	
	/**
	 * @param expression
	 * @return whether the expression is of numeric data type
	 * @throws Exception
	 */
	private boolean is_numeric_type(AstExpression expression) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_value_type());
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_bool:
			case c_char:
			case c_uchar:
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
			case c_float:
			case c_double:
			case c_ldouble:
					 return true;
			default: return false;
			}
		}
		else if(data_type instanceof CPointerType) {
			return true;
		}
		else if(data_type instanceof CEnumType) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * @param expression
	 * @return whether the reference is in assignment left-value
	 * @throws Exception
	 */
	private boolean is_valid_context(AstExpression expression) throws Exception {
		AstNode parent = CTypeAnalyzer.get_parent_of_expression(expression);
		if(parent instanceof AstIncreUnaryExpression) {
			return false;
		}
		else if(parent instanceof AstIncrePostfixExpression) {
			return false;
		}
		else if(parent instanceof AstFieldExpression) {
			return ((AstFieldExpression) parent).get_operator().get_punctuator() != CPunctuator.dot;
		}
		else if(parent instanceof AstAssignExpression
				|| parent instanceof AstArithAssignExpression
				|| parent instanceof AstBitwiseAssignExpression
				|| parent instanceof AstShiftAssignExpression) {
			return ((AstBinaryExpression) parent).get_loperand() != expression;
		}
		else if(parent instanceof AstCaseStatement) {
			return false;
		}
		else {
			return true;
		} 
	}
	
	@Override
	protected boolean is_seeded_location(AstNode location) throws Exception {
		if(location instanceof AstExpression) {
			AstExpression expression = (AstExpression) location;
			return this.is_numeric_type(expression) && 
					this.is_valid_context(expression);
		}
		else {
			return false;
		}
	}
	
	/**
	 * @param expression
	 * @return whether the expression is a boolean or condition of some statement
	 * @throws Exception
	 */
	private boolean is_boolean_expression(AstExpression expression) throws Exception {
		AstNode parent = expression.get_parent();
		if(expression instanceof AstLogicUnaryExpression
			|| expression instanceof AstLogicBinaryExpression
			|| expression instanceof AstRelationExpression) {
			return true;
		}
		else if(parent instanceof AstLogicUnaryExpression
				|| parent instanceof AstLogicBinaryExpression
				|| parent instanceof AstRelationExpression) {
			return true;
		}
		else if(parent instanceof AstIfStatement) {
			return ((AstIfStatement) parent).get_condition() == expression;
		}
		else if(parent instanceof AstWhileStatement) {
			return ((AstWhileStatement) parent).get_condition() == expression;
		}
		else if(parent instanceof AstDoWhileStatement) {
			return ((AstDoWhileStatement) parent).get_condition() == expression;
		}
		else if(parent instanceof AstExpressionStatement) {
			AstNode parent_parent = parent.get_parent();
			if(parent_parent instanceof AstForStatement) {
				return ((AstForStatement) parent_parent).get_condition() == parent;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private boolean is_integer_expression(AstExpression expression) throws Exception {
		return CTypeAnalyzer.is_integer(CTypeAnalyzer.
				get_value_type(expression.get_value_type()));
	}
	
	@Override
	protected Iterable<AstMutation> seed_mutations(AstNode location) throws Exception {
		AstExpression expression = (AstExpression) location;
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		if(this.is_boolean_expression(expression)) {
			mutations.add(AstMutations.UNOI(expression, COperator.logic_not));
		}
		else {
			if(this.is_integer_expression(expression)) {
				mutations.add(AstMutations.UNOI(expression, COperator.bit_not));
			}
			mutations.add(AstMutations.UNOI(expression, COperator.positive));
			mutations.add(AstMutations.UNOI(expression, COperator.negative));
			mutations.add(AstMutations.UNOI(expression, COperator.assign));
		}
		return mutations;
	}
	
}
