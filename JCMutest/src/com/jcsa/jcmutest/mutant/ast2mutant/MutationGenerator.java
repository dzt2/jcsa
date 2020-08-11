package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

/**
 * It provides interface to seed syntactic mutations in source code based on
 * the mutation operators as provided.
 * 
 * @author yukimula
 *
 */
public abstract class MutationGenerator {
	
	/* generation methods */
	/**
	 * initialize the generator state when a new function is put in
	 * @param function
	 * @throws Exception
	 */
	protected abstract void initialize(AstFunctionDefinition function) throws Exception;
	/**
	 * @param location
	 * @return whether the location is available for seeding mutation of specified class in
	 * @throws Exception
	 */
	protected abstract boolean available(AstNode location) throws Exception;
	/**
	 * generate the mutations in available location and put them in the tail of the mutations list
	 * @param location
	 * @param mutations
	 * @throws Exception
	 */
	protected abstract void generate(AstNode location, List<AstMutation> mutations) throws Exception;
	/**
	 * @param function
	 * @param locations the candidates in which mutations are going to be seeded
	 * @return the mutations of specific class seeded within the function's body
	 * @throws Exception
	 */
	protected List<AstMutation> generate(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception {
		this.initialize(function);
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		for(AstNode location : locations) {
			if(this.available(location)) {
				this.generate(location, mutations);
			}
		}
		return mutations;
	}
	
	/* utility methods */
	/**
	 * @param location
	 * @return whether the type of the expression is numeric {bool, char, short, int, long, float,
	 * 		   double, enum} such that it can be used as the 
	 * @throws Exception
	 */
	protected boolean is_numeric_expression(AstNode location) throws Exception {
		if(location instanceof AstInitializerBody) {
			return false;
		}
		else if(location instanceof AstExpression) {
			CType data_type = ((AstExpression) location).get_value_type();
			data_type = CTypeAnalyzer.get_value_type(data_type);
			
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
				case c_ldouble:	return true;
				default: return false;
				}
			}
			else if(data_type instanceof CEnumType) {
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
	 * logic_unary_expression or its operand
	 * logic_binary_expression or its operand
	 * relational_expression
	 * conditional_expression's condition
	 * if_statement.condition
	 * while_statement.condition
	 * do_while_statement.condition
	 * for_statement.condition.expression
	 * 
	 * @param location
	 * @return whether the location is taken as a conditional expression in C.
	 * @throws Exception
	 */
	protected boolean is_condition_expression(AstNode location) throws Exception {
		if(location instanceof AstExpression) {
			AstExpression expression = (AstExpression) location;
			expression = CTypeAnalyzer.get_expression_of(expression);
			AstNode parent = CTypeAnalyzer.get_parent_of_expression(expression);
			if(expression instanceof AstLogicUnaryExpression
				|| expression instanceof AstLogicBinaryExpression
				|| expression instanceof AstRelationExpression) {
				return true;
			}
			else if(parent instanceof AstLogicUnaryExpression
					|| parent instanceof AstLogicBinaryExpression) {
				return true;
			}
			else if(parent instanceof AstConditionalExpression) {
				return expression == CTypeAnalyzer.get_expression_of(
						((AstConditionalExpression) parent).get_condition());
			}
			else if(parent instanceof AstIfStatement) {
				return expression == CTypeAnalyzer.get_expression_of(
						((AstIfStatement) parent).get_condition());
			}
			else if(parent instanceof AstWhileStatement) {
				return expression == CTypeAnalyzer.get_expression_of(
						((AstWhileStatement) parent).get_condition());
			}
			else if(parent instanceof AstDoWhileStatement) {
				return expression == CTypeAnalyzer.get_expression_of(
						((AstDoWhileStatement) parent).get_condition());
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
		else {
			return false;
		}
	}
	/**
	 * assign_expression.loperand
	 * arith_assign_expression.loperand
	 * bitws_assign_expression.loperand
	 * incre_unary_expression.operand
	 * incre_postfix_expression.operand
	 * address_of_expression.operand
	 * field_expression.body
	 * 
	 * @param location
	 * @return whether the expression is a left-reference
	 * @throws Exception
	 */
	protected boolean is_left_reference(AstNode location) throws Exception {
		if(location instanceof AstExpression) {
			AstExpression expression = 
					CTypeAnalyzer.get_expression_of((AstExpression) location);
			AstNode parent = CTypeAnalyzer.get_parent_of_expression(expression);
			
			if(parent instanceof AstAssignExpression
				|| parent instanceof AstArithAssignExpression
				|| parent instanceof AstBitwiseAssignExpression
				|| parent instanceof AstShiftAssignExpression) {
				return expression == CTypeAnalyzer.get_expression_of(
						((AstBinaryExpression) parent).get_loperand());
			}
			else if(parent instanceof AstIncrePostfixExpression) {
				return true;
			}
			else if(parent instanceof AstUnaryExpression) {
				switch(((AstUnaryExpression) parent).
						get_operator().get_operator()) {
				case address_of:
				case increment:
				case decrement:	return true;
				default: 		return false;
				}
			}
			else if(parent instanceof AstFieldExpression) {
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
	 * @param location
	 * @return statement, child
	 * @throws Exception
	 */
	protected AstNode[] statement_context(AstNode location) throws Exception {
		AstNode parent = location.get_parent();
		AstNode child = location;
		while(parent != null) {
			if(parent instanceof AstStatement) {
				break;
			}
			else {
				child = parent;
				parent = parent.get_parent();
			}
		}
		return new AstNode[] { parent, child };
	}
	
	
	
}
