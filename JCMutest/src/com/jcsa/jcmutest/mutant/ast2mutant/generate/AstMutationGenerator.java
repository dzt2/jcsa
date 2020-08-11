package com.jcsa.jcmutest.mutant.ast2mutant.generate;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * 	It provides interface to generate mutation in the source code written in
 * 	C programming language.
 * 	
 * 	@author yukimula
 *
 */
public abstract class AstMutationGenerator {
	
	/* generation methods */
	/**
	 * @param location
	 * @return whether the location of abstract syntax node can seed the mutations
	 * @throws Exception
	 */
	protected abstract boolean is_available(AstNode location) throws Exception;
	/**
	 * generate the mutations seeded in the location and append them in the buffer
	 * @param location
	 * @param mutations
	 * @throws Exception
	 */
	protected abstract void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception;
	/**
	 * @param locations
	 * @return the mutations seeded at some locations in specified
	 * @throws Exception
	 */
	public Iterable<AstMutation> generate(Iterable<AstNode> locations) throws Exception {
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		for(AstNode location : locations) {
			if(this.is_available(location)) {
				this.generate_mutations(location, mutations);
			}
		}
		return mutations;
	}
	
	/* utility methods */
	/**
	 * logic_unary_expression
	 * logic_binary_expression
	 * relation_expression
	 * conditional_expression.condition
	 * if_statement.condition
	 * while_statement.condition
	 * do_while_statement.condition
	 * for_statement.condition.expression
	 * logic_unary_expression.operand
	 * logic_binary_expression.loperand|roperand
	 * 
	 * @param location
	 * @return whether the location can be taken as a condition in program.
	 * @throws Exception
	 */
	protected boolean is_condition_expression(AstNode location) throws Exception {
		if(location instanceof AstExpression) {
			AstExpression expression = (AstExpression) location;
			AstNode parent = CTypeAnalyzer.get_parent_of_expression(expression);
			if(expression instanceof AstLogicUnaryExpression
				|| expression instanceof AstLogicBinaryExpression
				|| expression instanceof AstRelationExpression) {
				return true;
			}
			else if(parent instanceof AstConditionalExpression) {
				return CTypeAnalyzer.get_expression_of(((AstConditionalExpression) parent).get_condition()) 
						== CTypeAnalyzer.get_expression_of(expression);
			}
			else if(parent instanceof AstIfStatement) {
				return CTypeAnalyzer.get_expression_of(expression) == CTypeAnalyzer.
						get_expression_of(((AstIfStatement) parent).get_condition());
			}
			else if(parent instanceof AstWhileStatement) {
				return CTypeAnalyzer.get_expression_of(expression) == CTypeAnalyzer.
						get_expression_of(((AstWhileStatement) parent).get_condition());
			}
			else if(parent instanceof AstDoWhileStatement) {
				return CTypeAnalyzer.get_expression_of(expression) == CTypeAnalyzer.
						get_expression_of(((AstDoWhileStatement) parent).get_condition());
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
			else if(parent instanceof AstLogicUnaryExpression
					|| parent instanceof AstLogicBinaryExpression) {
				return true;
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
	 * id_expression
	 * array_expression
	 * pointer_unary_expression
	 * field_expression
	 * 
	 * @param location
	 * @return whether the expression is a reference
	 * @throws Exception
	 */
	protected boolean is_reference_expression(AstNode location) throws Exception {
		if(location instanceof AstExpression) {
			if(location instanceof AstIdExpression
				|| location instanceof AstArrayExpression
				|| location instanceof AstFieldExpression) {
				return true;
			}
			else if(location instanceof AstPointUnaryExpression) {
				return ((AstPointUnaryExpression) location).
						get_operator().get_operator() == COperator.dereference;
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
	 * assign_expression.left_operand
	 * address_expression.operand
	 * incre_unary_expression.operand
	 * incre_postfix_expression.operand
	 * 
	 * @param location
	 * @return whether the location is taken as the left-reference in assignment
	 * @throws Exception
	 */
	protected boolean is_left_reference(AstNode location) throws Exception {
		if(location instanceof AstExpression) {
			AstExpression expression = (AstExpression) location;
			AstNode parent = CTypeAnalyzer.get_parent_of_expression(expression);
			if(parent instanceof AstAssignExpression
				|| parent instanceof AstArithAssignExpression
				|| parent instanceof AstBitwiseAssignExpression
				|| parent instanceof AstShiftAssignExpression) {
				return CTypeAnalyzer.get_expression_of(((AstBinaryExpression) parent).
						get_loperand()) == CTypeAnalyzer.get_expression_of(expression);
			}
			else if(parent instanceof AstUnaryExpression) {
				switch(((AstUnaryExpression) parent).get_operator().get_operator()) {
				case increment:
				case decrement:
				case address_of:	
							return true;
				default: 
							return false;
				}
			}
			else if(parent instanceof AstIncrePostfixExpression) {
				return true;
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
	 * boolean, char, short, int, long, float, double, enumeration
	 * @param location
	 * @return
	 * @throws Exception
	 */
	protected boolean is_numeric_expression(AstNode location) throws Exception {
		if(location instanceof AstExpression) {
			CType data_type = ((AstExpression) location).get_value_type();
			if(data_type != null) {
				data_type = CTypeAnalyzer.get_value_type(data_type);
				if(CTypeAnalyzer.is_boolean(data_type)) {
					return true;
				}
				else if(CTypeAnalyzer.is_integer(data_type)) {
					return true;
				}
				else if(CTypeAnalyzer.is_real(data_type)) {
					return true;
				}
				else {
					return false;
				}
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
	 * @param operator
	 * @return whether the operator is available to replace the operator in expression
	 * @throws Exception
	 */
	protected boolean is_compatible(AstBinaryExpression expression, COperator operator) throws Exception {
		if(expression.get_operator().get_operator() == operator) {
			return false;
		}
		else {
			CType data_type = CTypeAnalyzer.get_value_type(expression.get_value_type());
			switch(operator) {
			case arith_mod:
			case bit_and:
			case bit_or:
			case bit_xor:
			case left_shift:
			case righ_shift:
			case arith_mod_assign:
			case bit_and_assign:
			case bit_or_assign:
			case bit_xor_assign:
			case left_shift_assign:
			case righ_shift_assign:
				return CTypeAnalyzer.is_boolean(data_type) || CTypeAnalyzer.is_integer(data_type);
			case arith_add:
			case arith_sub:
			case arith_mul:
			case arith_div:
			case arith_add_assign:
			case arith_sub_assign:
			case arith_mul_assign:
			case arith_div_assign:
			case greater_tn:
			case greater_eq:
			case smaller_tn:
			case smaller_eq:
			case equal_with:
			case not_equals:
				return CTypeAnalyzer.is_boolean(data_type) || CTypeAnalyzer.is_integer(data_type) || CTypeAnalyzer.is_real(data_type);
			case logic_and:
			case logic_or:
			case assign:
				return CTypeAnalyzer.is_integer(data_type) || CTypeAnalyzer.is_real(data_type) || CTypeAnalyzer.is_boolean(data_type);
			default:
				return false;
			}
		}
	}
	
}
