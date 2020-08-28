package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.unary;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadParser;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.SadInfection;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UNOISadInfection extends SadInfection {
	
	private void insert_arith_neg(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		AstExpression location = (AstExpression) mutation.get_location();
		CirExpression expression = this.find_result(tree, location);
		CirStatement statement = expression.statement_of();
		
		if(statement != null) {
			SadExpression condition = this.condition_of(expression, false);
			SadAssertion constraint = SadFactory.assert_condition(statement, condition);
			this.connect(reach_node, constraint, 
				SadFactory.insert_operator(statement, expression, COperator.arith_add));
		}
	}
	private void insert_bitws_rsv(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		AstExpression location = (AstExpression) mutation.get_location();
		CirExpression expression = this.find_result(tree, location);
		CirStatement statement = expression.statement_of();
		
		if(statement != null) {
			this.connect(reach_node, SadFactory.insert_operator(
					statement, expression, COperator.bit_not));
		}
	}
	private void insert_logic_not(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		AstExpression location = (AstExpression) mutation.get_location();
		CirExpression expression = this.find_result(tree, location);
		CirStatement statement = expression.statement_of();
		
		if(statement != null) {
			this.connect(reach_node, SadFactory.insert_operator(
					statement, expression, COperator.logic_not));
		}
	}
	private void insert_abs_value(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		AstExpression location = (AstExpression) mutation.get_location();
		CirExpression expression = this.find_result(tree, location);
		CirStatement statement = expression.statement_of();
		
		if(statement != null) {
			SadExpression condition = SadFactory.smaller_tn(CBasicTypeImpl.bool_type, 
					(SadExpression) SadParser.cir_parse(expression), SadFactory.constant(0));
			SadAssertion constraint = SadFactory.assert_condition(statement, condition);
			SadAssertion state_error = SadFactory.insert_operator(statement, expression, COperator.negative);
			this.connect(reach_node, state_error, constraint);
		}
	}
	private void insert_nabs_value(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		AstExpression location = (AstExpression) mutation.get_location();
		CirExpression expression = this.find_result(tree, location);
		CirStatement statement = expression.statement_of();
		
		if(statement != null) {
			SadExpression condition = SadFactory.greater_tn(CBasicTypeImpl.bool_type, 
					(SadExpression) SadParser.cir_parse(expression), SadFactory.constant(0));
			SadAssertion constraint = SadFactory.assert_condition(statement, condition);
			SadAssertion state_error = SadFactory.insert_operator(statement, expression, COperator.negative);
			this.connect(reach_node, state_error, constraint);
		}
	}
	
	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		switch(mutation.get_operator()) {
		case insert_arith_neg:	this.insert_arith_neg(tree, mutation, reach_node); break;
		case insert_bitws_rsv:	this.insert_bitws_rsv(tree, mutation, reach_node); break;
		case insert_logic_not:	this.insert_logic_not(tree, mutation, reach_node); break;
		case insert_abs_value:	this.insert_abs_value(tree, mutation, reach_node); break;
		case insert_nabs_value:	this.insert_nabs_value(tree, mutation, reach_node);break;
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.toString());
		}
	}
	
}
