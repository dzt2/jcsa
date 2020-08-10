package com.jcsa.jcmutest.mutant.ast2mutant.generators;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

/**
 * Only the numeric expression is allowed of being injected.
 * 
 * @author yukimula
 *
 */
public class ETRPMutationGenerator extends AstMutationGenerator {
	
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
	 * invalid-case:
	 * 	{x++, --y, x.field, x += expr, case.expr}
	 * @param expression
	 * @return whether the context is valid of injecting ETRP-mutations
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
	
	/**
	 * @param expression
	 * @return whether the expression is available for seeding ETRP-mutation
	 * @throws Exception
	 */
	private boolean is_valid_expression(AstExpression expression) throws Exception {
		if(expression instanceof AstInitializerBody) {
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
					this.is_valid_context(expression) && 
					this.is_valid_expression(expression);
		}
		else {
			return false;
		}
	}

	@Override
	protected Iterable<AstMutation> seed_mutations(AstNode location) throws Exception {
		AstExpression expression = (AstExpression) location;
		expression = CTypeAnalyzer.get_expression_of(expression);
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		mutations.add(AstMutations.trap_on_expression(expression));
		return mutations;
	}

}
