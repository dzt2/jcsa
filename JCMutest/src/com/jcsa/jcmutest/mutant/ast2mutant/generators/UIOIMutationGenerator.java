package com.jcsa.jcmutest.mutant.ast2mutant.generators;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class UIOIMutationGenerator extends AstMutationGenerator {
	
	/**
	 * @param expression
	 * @return whether the expression is a reference
	 * @throws Exception
	 */
	private boolean is_reference(AstExpression expression) throws Exception {
		if(expression instanceof AstIdExpression) {
			return true;
		}
		else if(expression instanceof AstFieldExpression) {
			return true;
		}
		else if(expression instanceof AstUnaryExpression) {
			return ((AstUnaryExpression) expression).
					get_operator().get_operator() == COperator.dereference;
		}
		else {
			return false;
		}
	}
	
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
					this.is_valid_context(expression) && 
					this.is_reference(expression);
		}
		else {
			return false;
		}
	}

	@Override
	protected Iterable<AstMutation> seed_mutations(AstNode location) throws Exception {
		AstExpression expression = (AstExpression) location;
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		mutations.add(AstMutations.insert_prev_inc(expression));
		mutations.add(AstMutations.insert_prev_dec(expression));
		mutations.add(AstMutations.insert_post_inc(expression));
		mutations.add(AstMutations.insert_post_dec(expression));
		return mutations;
	}

}
