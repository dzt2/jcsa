package com.jcsa.jcmutest.mutant.ext2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
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

/**
 * It provides the interfaces to extend the source mutation to its coverage,
 * weak and strong testing version in standard way.
 *
 * @author yukimula
 *
 */
public abstract class MutationExtension {

	/* extension methods */
	/**
	 * @param source source mutation being extended
	 * @return the coverage version of source mutation
	 * @throws Exception
	 */
	protected abstract AstMutation cover(AstMutation source) throws Exception;
	/**
	 * @param source source mutation being extended
	 * @return the weak version of source mutation
	 * @throws Exception
	 */
	protected abstract AstMutation weak(AstMutation source) throws Exception;
	/**
	 * @param source source mutation being extended
	 * @return the standard strong version of the source mutation
	 * @throws Exception
	 */
	protected abstract AstMutation strong(AstMutation source) throws Exception;
	/**
	 * @param source
	 * @return the list of coverage, weak and strong version of the source mutation
	 * @throws Exception
	 */
	protected AstMutation[] extend(AstMutation source) throws Exception {
		return new AstMutation[] {
			this.cover(source),
			this.weak(source),
			this.strong(source)
		};
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
	 * @param location
	 * @return the coverage mutation for reaching the target location
	 * @throws Exception
	 */
	protected AstMutation coverage_mutation(AstNode location) throws Exception {
		/* to avoid invalid instrumental node */
		while(this.is_left_reference(location)) {
			location = location.get_parent();
		}

		if(location instanceof AstExpression) {
			AstExpression expression = (AstExpression) location;
			expression = CTypeAnalyzer.get_expression_of(expression);
			AstNode[] statement_child = this.statement_context(expression);
			AstStatement statement = (AstStatement) statement_child[0];

			if(statement instanceof AstCaseStatement) {
				return AstMutations.trap_on_statement(statement);
			}
			else if(expression instanceof AstAssignExpression
					|| expression instanceof AstArithAssignExpression
					|| expression instanceof AstBitwiseAssignExpression
					|| expression instanceof AstShiftAssignExpression) {
				return this.coverage_mutation(((AstBinaryExpression) expression).get_roperand());
			}
			else {
				return AstMutations.trap_on_expression(expression);
			}
		}
		else if(location instanceof AstStatement) {
			AstStatement statement = (AstStatement) location;
			AstNode parent = statement.get_parent();
			/* automatic statement */
			if(statement instanceof AstGotoStatement
				|| statement instanceof AstBreakStatement
				|| statement instanceof AstContinueStatement
				|| statement instanceof AstCaseStatement
				|| statement instanceof AstDefaultStatement
				|| statement instanceof AstLabeledStatement) {
				return AstMutations.trap_on_statement(statement);
			}
			else if(statement instanceof AstDeclarationStatement) {
				if(parent instanceof AstForStatement) {
					if(((AstForStatement) parent).get_initializer() == statement) {
						/* covering the condition of the for-statement */
						return this.coverage_mutation(parent);
					}
					else {
						return AstMutations.trap_on_statement(statement);
					}
				}
				else {
					return AstMutations.trap_on_statement(statement);
				}
			}
			else if(statement instanceof AstExpressionStatement) {
				if(((AstExpressionStatement) statement).has_expression()) {
					return this.coverage_mutation(
							((AstExpressionStatement) statement).get_expression());
				}
				else if(parent instanceof AstForStatement) {
					if(((AstForStatement) parent).get_initializer() == statement
						|| ((AstForStatement) parent).get_condition() == statement) {
						return this.coverage_mutation(parent);
					}
					else {
						return AstMutations.trap_on_statement(statement);
					}
				}
				else {
					return AstMutations.trap_on_statement(statement);
				}
			}
			else if(statement instanceof AstCompoundStatement) {
				if(((AstCompoundStatement) statement).has_statement_list()) {
					return this.coverage_mutation(((AstCompoundStatement)
							statement).get_statement_list().get_statement(0));
				}
				else {
					return AstMutations.trap_on_statement(statement);
				}
			}
			else if(statement instanceof AstIfStatement) {
				return this.coverage_mutation(((AstIfStatement) statement).get_condition());
			}
			else if(statement instanceof AstSwitchStatement) {
				return this.coverage_mutation(((AstSwitchStatement) statement).get_condition());
			}
			else if(statement instanceof AstWhileStatement) {
				return this.coverage_mutation(((AstWhileStatement) statement).get_condition());
			}
			else if(statement instanceof AstDoWhileStatement) {
				return this.coverage_mutation(((AstDoWhileStatement) statement).get_body());
			}
			else if(statement instanceof AstForStatement) {
				AstForStatement for_statement = (AstForStatement) statement;
				if(for_statement.get_condition().has_expression()) {
					return AstMutations.trap_on_expression(
							for_statement.get_condition().get_expression());
				}
				else {
					return this.coverage_mutation(for_statement.get_body());
				}
			}
			else if(statement instanceof AstReturnStatement) {
				if(((AstReturnStatement) statement).has_expression()) {
					return this.coverage_mutation(
							((AstReturnStatement) statement).get_expression());
				}
				else {
					return AstMutations.trap_on_statement(statement);
				}
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + location);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + location);
		}
	}



}
