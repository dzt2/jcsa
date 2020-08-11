package com.jcsa.jcmutest.mutant.rip2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
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
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDefaultStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It provides interfaces to extend the mutations as coverage,
 * weak and strong mutation (standard version) such to compress
 * the number of mutants being used.
 * 
 * @author yukimula
 *
 */
public abstract class MutationExtender {
	
	/* extension methods */
	/**
	 * @param source
	 * @return the mutation that is killed once the statement where it 
	 * 			is seeded is executed
	 * @throws Exception
	 */
	protected abstract AstMutation coverage_mutation(AstMutation source) throws Exception;
	/**
	 * @param source
	 * @return the mutation that is killed once the state error is
	 * 			caused during the mutant is tested.
	 * @throws Exception
	 */
	protected abstract AstMutation weak_mutation(AstMutation source) throws Exception;
	/**
	 * @param source
	 * @return the mutation that is killed iff. the original mutant is killed
	 * @throws Exception
	 */
	protected abstract AstMutation strong_mutation(AstMutation source) throws Exception;
	
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
	/**
	 * id_expression
	 * array_expression
	 * dereference_expression
	 * field_expression
	 * 
	 * @param location
	 * @return
	 * @throws Exception
	 */
	protected boolean is_reference_expression(AstNode location) throws Exception {
		if(location instanceof AstIdExpression
			|| location instanceof AstArrayExpression
			|| location instanceof AstFieldExpression) {
			return true;
		}
		else if(location instanceof AstUnaryExpression) {
			return ((AstUnaryExpression) location).get_operator().get_operator() == COperator.dereference;
		}
		else {
			return false;
		}
	}
	/**
	 * assign_expr
	 * arith-assign-expr
	 * bitws-assign-expr
	 * incre-xxxxxx-expr
	 * 
	 * @param location
	 * @return
	 * @throws Exception
	 */
	protected boolean is_assign_expression(AstNode location) throws Exception {
		return location instanceof AstAssignExpression
				|| location instanceof AstArithAssignExpression
				|| location instanceof AstBitwiseAssignExpression
				|| location instanceof AstShiftAssignExpression
				|| location instanceof AstIncreUnaryExpression
				|| location instanceof AstIncrePostfixExpression;
	}
	/**
	 * @param expression
	 * @param operator
	 * @return whether the operator is compatible for replacing the expression
	 * @throws Exception
	 */
	protected boolean is_compatible(AstBinaryExpression expression, COperator operator) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_value_type());
		if(expression.get_operator().get_operator() != operator) {
			switch(operator) {
			case arith_mod:
			case arith_mod_assign:
			case bit_and:
			case bit_and_assign:
			case bit_or:
			case bit_or_assign:
			case bit_xor:
			case bit_xor_assign:
			case left_shift:
			case left_shift_assign:
			case righ_shift:
			case righ_shift_assign:
			{
				return CTypeAnalyzer.is_integer(type);
			}
			case arith_add:
			case arith_sub:
			case arith_mul:
			case arith_div:
			case greater_tn:
			case greater_eq:
			case smaller_tn:
			case smaller_eq:
			case equal_with:
			case not_equals:
			case logic_and:
			case logic_or:
			case assign:
			{
				return this.is_numeric_expression(expression);
			}
			default:	
			{
				return false;
			}
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param location
	 * @return
	 * @throws Exception
	 */
	protected AstMutation coverage_at(AstNode location) throws Exception {
		if(location instanceof AstStatement) {
			AstStatement statement = (AstStatement) location;
			if(statement instanceof AstGotoStatement
				|| statement instanceof AstBreakStatement
				|| statement instanceof AstContinueStatement
				|| statement instanceof AstLabeledStatement
				|| statement instanceof AstCaseStatement
				|| statement instanceof AstDefaultStatement
				|| statement instanceof AstDeclarationStatement) {
				return AstMutations.trap_on_statement(statement);
			}
			else if(statement instanceof AstReturnStatement) {
				if(((AstReturnStatement) statement).has_expression()) {
					return this.coverage_at(
							((AstReturnStatement) statement).get_expression());
				}
				else {
					return AstMutations.trap_on_statement(statement);
				}
			}
			else if(statement instanceof AstExpressionStatement) {
				if(((AstExpressionStatement) statement).has_expression()) {
					return this.coverage_at(
							((AstExpressionStatement) statement).get_expression());
				}
				else {
					return AstMutations.trap_on_statement(statement);
				}
			}
			else if(statement instanceof AstCompoundStatement) {
				if(((AstCompoundStatement) statement).has_statement_list()) {
					return this.coverage_at(((AstCompoundStatement) 
							statement).get_statement_list().get_child(0));
				}
				else {
					return AstMutations.trap_on_statement(statement);
				}
			}
			else if(statement instanceof AstIfStatement) {
				return this.coverage_at(((AstIfStatement) statement).get_condition());
			}
			else if(statement instanceof AstSwitchStatement) {
				return this.coverage_at(((AstSwitchStatement) statement).get_condition());
			}
			else if(statement instanceof AstWhileStatement) {
				return this.coverage_at(((AstWhileStatement) statement).get_condition());
			}
			else if(statement instanceof AstDoWhileStatement) {
				return this.coverage_at(((AstDoWhileStatement) statement).get_body());
			}
			else if(statement instanceof AstForStatement) {
				return this.coverage_at(((AstForStatement) statement).get_condition());
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + location);
			}
		}
		else if(location instanceof AstExpression) {
			AstNode[] statement_child = this.statement_context(location);
			AstStatement statement = (AstStatement) statement_child[0];
			AstExpression expression = (AstExpression) location;
			if(expression instanceof AstInitializerBody) {
				return this.coverage_at(statement);
			}
			else if(expression instanceof AstAssignExpression
					|| expression instanceof AstArithAssignExpression
					|| expression instanceof AstBitwiseAssignExpression
					|| expression instanceof AstShiftAssignExpression) {
				return this.coverage_at(((AstBinaryExpression) expression).get_roperand());
			}
			else if(this.is_left_reference(location)) {
				return this.coverage_at(statement);
			}
			else {
				return AstMutations.trap_on_expression(expression);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + location);
		}
	}
	
}
