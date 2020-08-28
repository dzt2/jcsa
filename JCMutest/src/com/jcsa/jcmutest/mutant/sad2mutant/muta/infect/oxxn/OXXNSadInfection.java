package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.oxxn;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaOperator;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.SadInfection;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public abstract class OXXNSadInfection extends SadInfection {

	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		AstBinaryExpression ast_expression = (AstBinaryExpression) mutation.get_location();
		AstExpression ast_loperand = CTypeAnalyzer.get_expression_of(ast_expression.get_loperand());
		AstExpression ast_roperand = CTypeAnalyzer.get_expression_of(ast_expression.get_roperand());
		CirExpression expression = this.find_result(tree, ast_expression);
		CirExpression loperand = this.find_result(tree, ast_loperand);
		CirExpression roperand = this.find_result(tree, ast_roperand);
		CirStatement statement = loperand.statement_of();
		COperator operator = (COperator) mutation.get_parameter();
		
		if(statement != null) {
			if(mutation.get_operator() == MutaOperator.set_operator) {
				switch(operator) {
				case arith_add:		this.set_arith_add(reach_node, tree, statement, expression, loperand, roperand);  break;
				case arith_sub:		this.set_arith_sub(reach_node, tree, statement, expression, loperand, roperand);  break;
				case arith_mul:		this.set_arith_mul(reach_node, tree, statement, expression, loperand, roperand);  break;
				case arith_div:		this.set_arith_div(reach_node, tree, statement, expression, loperand, roperand);  break;
				case arith_mod:		this.set_arith_mod(reach_node, tree, statement, expression, loperand, roperand);  break;
				case bit_and:		this.set_bitws_and(reach_node, tree, statement, expression, loperand, roperand);  break;
				case bit_or:		this.set_bitws_ior(reach_node, tree, statement, expression, loperand, roperand);  break;
				case bit_xor:		this.set_bitws_xor(reach_node, tree, statement, expression, loperand, roperand);  break;
				case left_shift:	this.set_bitws_lsh(reach_node, tree, statement, expression, loperand, roperand);  break;
				case righ_shift:	this.set_bitws_rsh(reach_node, tree, statement, expression, loperand, roperand);  break;
				case logic_and:		this.set_logic_and(reach_node, tree, statement, expression, loperand, roperand);  break;
				case logic_or:		this.set_logic_ior(reach_node, tree, statement, expression, loperand, roperand);  break;
				case greater_tn:	this.set_greater_tn(reach_node, tree, statement, expression, loperand, roperand); break;
				case greater_eq:	this.set_greater_eq(reach_node, tree, statement, expression, loperand, roperand); break;
				case smaller_tn:	this.set_smaller_tn(reach_node, tree, statement, expression, loperand, roperand); break;
				case smaller_eq:	this.set_smaller_eq(reach_node, tree, statement, expression, loperand, roperand); break;
				case equal_with:	this.set_equal_with(reach_node, tree, statement, expression, loperand, roperand); break;
				case not_equals:	this.set_not_equals(reach_node, tree, statement, expression, loperand, roperand); break;
				default: throw new IllegalArgumentException(operator.toString());
				}
			}
			else if(mutation.get_operator() == MutaOperator.cmp_operator) {
				switch(operator) {
				case arith_add:		this.cmp_arith_add(reach_node, tree, statement, expression, loperand, roperand);  break;
				case arith_sub:		this.cmp_arith_sub(reach_node, tree, statement, expression, loperand, roperand);  break;
				case arith_mul:		this.cmp_arith_mul(reach_node, tree, statement, expression, loperand, roperand);  break;
				case arith_div:		this.cmp_arith_div(reach_node, tree, statement, expression, loperand, roperand);  break;
				case arith_mod:		this.cmp_arith_mod(reach_node, tree, statement, expression, loperand, roperand);  break;
				case bit_and:		this.cmp_bitws_and(reach_node, tree, statement, expression, loperand, roperand);  break;
				case bit_or:		this.cmp_bitws_ior(reach_node, tree, statement, expression, loperand, roperand);  break;
				case bit_xor:		this.cmp_bitws_xor(reach_node, tree, statement, expression, loperand, roperand);  break;
				case left_shift:	this.cmp_bitws_lsh(reach_node, tree, statement, expression, loperand, roperand);  break;
				case righ_shift:	this.cmp_bitws_rsh(reach_node, tree, statement, expression, loperand, roperand);  break;
				case logic_and:		this.cmp_logic_and(reach_node, tree, statement, expression, loperand, roperand);  break;
				case logic_or:		this.cmp_logic_ior(reach_node, tree, statement, expression, loperand, roperand);  break;
				case greater_tn:	this.cmp_greater_tn(reach_node, tree, statement, expression, loperand, roperand); break;
				case greater_eq:	this.cmp_greater_eq(reach_node, tree, statement, expression, loperand, roperand); break;
				case smaller_tn:	this.cmp_smaller_tn(reach_node, tree, statement, expression, loperand, roperand); break;
				case smaller_eq:	this.cmp_smaller_eq(reach_node, tree, statement, expression, loperand, roperand); break;
				case equal_with:	this.cmp_equal_with(reach_node, tree, statement, expression, loperand, roperand); break;
				case not_equals:	this.cmp_not_equals(reach_node, tree, statement, expression, loperand, roperand); break;
				default: throw new IllegalArgumentException(operator.toString());
				}
			}
			else {
				throw new IllegalArgumentException("Invalid: " + mutation);
			}
		}
	}
	
	/* set_operator */
	protected abstract void set_arith_add(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void set_arith_sub(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void set_arith_mul(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void set_arith_div(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void set_arith_mod(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void set_bitws_and(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void set_bitws_ior(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void set_bitws_xor(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void set_bitws_lsh(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void set_bitws_rsh(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void set_logic_and(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void set_logic_ior(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void set_greater_tn(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void set_greater_eq(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void set_smaller_tn(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void set_smaller_eq(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void set_equal_with(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void set_not_equals(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	
	/* cmp_operator */
	protected abstract void cmp_arith_add(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void cmp_arith_sub(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void cmp_arith_mul(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void cmp_arith_div(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void cmp_arith_mod(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void cmp_bitws_and(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void cmp_bitws_ior(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void cmp_bitws_xor(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void cmp_bitws_lsh(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void cmp_bitws_rsh(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void cmp_logic_and(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void cmp_logic_ior(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void cmp_greater_tn(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void cmp_greater_eq(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void cmp_smaller_tn(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void cmp_smaller_eq(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void cmp_equal_with(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	protected abstract void cmp_not_equals(SadVertex source, CirTree tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception;
	
}
