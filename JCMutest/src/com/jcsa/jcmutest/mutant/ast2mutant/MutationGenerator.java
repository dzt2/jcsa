package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
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
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CTypeQualifier;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;

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
	protected abstract void initialize(AstFunctionDefinition function,
			Iterable<AstNode> locations) throws Exception;
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
		this.initialize(function, locations);
		List<AstMutation> mutations = new ArrayList<>();
		for(AstNode location : locations) {
			if(this.available(location)) {
				this.generate(location, mutations);
			}
		}
		return mutations;
	}

	/* utility methods */
	/**
	 * @param expression
	 * @return not in case-statement or left-operand of some assignment.
	 * @throws Exception
	 */
	protected boolean is_valid_context(AstNode expression) throws Exception {
		if(expression instanceof AstExpression) {
			AstNode child = expression;
			AstNode parent = expression.get_parent();
			while(parent != null) {
				if(parent instanceof AstCaseStatement) {
					return false;	/* constant in the case-statement is removed */
				}
				else if(parent instanceof AstAssignExpression
						|| parent instanceof AstArithAssignExpression
						|| parent instanceof AstBitwiseAssignExpression
						|| parent instanceof AstShiftAssignExpression) {
					/* reference that is assigned in statement cannot be seeded */
					return child != ((AstBinaryExpression) parent).get_loperand();
				}
				else if(parent instanceof AstDeclarator) {
					return false;	/* constant in array-declarator is not allowed */
				}
				else {
					child = parent;
					parent = parent.get_parent();
				}
			}
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * @param location
	 * @return whether the type of location contains const
	 * @throws Exception
	 */
	protected boolean is_const_type(AstNode location) throws Exception {
		if(location instanceof AstExpression) {
			CType type = ((AstExpression) location).get_value_type();
			while(type instanceof CQualifierType) {
				if(((CQualifierType) type).get_qualifier() == CTypeQualifier.c_const) {
					return true;
				}
				else {
					type = ((CQualifierType) type).get_reference();
				}
			}

			if(location instanceof AstConstant) {
				return true;
			}
			else if(location instanceof AstIdExpression) {
				return ((AstIdExpression) location).get_cname() instanceof CEnumeratorName;
			}
			else if(location instanceof AstLiteral) {
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
				case c_ldouble:	// return true;
						 /* only numeric expressions in valid contexts allowed */
						 return this.is_valid_context(location);
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
		if(location instanceof AstIdExpression) {
			return !(((AstIdExpression) location).get_cname() instanceof CEnumeratorName);
		}
		else if(location instanceof AstArrayExpression
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
		// CType type = CTypeAnalyzer.get_value_type(expression.get_value_type());
		CType ltype = CTypeAnalyzer.get_value_type(expression.get_loperand().get_value_type());
		CType rtype = CTypeAnalyzer.get_value_type(expression.get_roperand().get_value_type());

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
				return CTypeAnalyzer.is_integer(ltype) && CTypeAnalyzer.is_integer(rtype);
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

}
