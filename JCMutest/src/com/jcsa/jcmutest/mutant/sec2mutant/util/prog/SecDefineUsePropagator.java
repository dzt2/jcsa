package com.jcsa.jcmutest.mutant.sec2mutant.util.prog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecAddReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecInsReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecSetReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecUnyReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecAddStatementError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecDelStatementError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecSetStatementError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.uniq.SecNoneError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.uniq.SecTrapError;
import com.jcsa.jcmutest.mutant.sec2mutant.util.apis.SecErrorPropagator;
import com.jcsa.jcmutest.mutant.sec2mutant.util.apis.SecPathFinder;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecDefineUsePropagator extends SecErrorPropagator {

	@Override
	protected void propagate_add_statement(SecAddStatementError error) throws Exception {
		this.report_unsupported_operation();
	}

	@Override
	protected void propagate_del_statement(SecDelStatementError error) throws Exception {
		this.report_unsupported_operation();
	}

	@Override
	protected void propagate_set_statement(SecSetStatementError error) throws Exception {
		this.report_unsupported_operation();
	}

	@Override
	protected void propagate_trap_error(SecTrapError error) throws Exception { }

	@Override
	protected void propagate_none_error(SecNoneError error) throws Exception { }

	@Override
	protected void propagate_set_expression(SecSetExpressionError error) throws Exception { }

	@Override
	protected void propagate_add_expression(SecAddExpressionError error) throws Exception { }

	@Override
	protected void propagate_ins_expression(SecInsExpressionError error) throws Exception { }

	@Override
	protected void propagate_uny_expression(SecUnyExpressionError error) throws Exception { }
	
	private SecConstraint generate_constraint(SecReferenceError error) throws Exception {
		CirStatement source_statement = error.get_orig_reference().
				get_expression().get_cir_source().statement_of();
		CirStatement target_statement = this.target_statement();
		CirExecution source = source_statement.get_tree().get_localizer().get_execution(source_statement);
		CirExecution target = target_statement.get_tree().get_localizer().get_execution(target_statement);
		Set<Set<CirExecutionFlow>> paths = SecPathFinder.intra_simple_paths(source, target);
		
		Set<CirExecutionFlow> dominance_flows = new HashSet<CirExecutionFlow>();
		boolean first = true;
		for(Set<CirExecutionFlow> path : paths) {
			if(first) {
				first = false;
				dominance_flows.addAll(path);
			}
			else {
				for(CirExecutionFlow flow : dominance_flows) {
					if(!path.contains(flow)) {
						dominance_flows.remove(flow);
					}
				}
			}
		}
		
		List<SecConstraint> constraints = new ArrayList<SecConstraint>();
		for(CirExecutionFlow flow : dominance_flows) {
			switch(flow.get_type()) {
			case true_flow:
			{
				CirStatement statement = flow.get_source().get_statement();
				SymExpression condition;
				if(statement instanceof CirIfStatement) {
					condition = SecFactory.sym_condition(((CirIfStatement) statement).get_condition(), true);
				}
				else {
					condition = SecFactory.sym_condition(((CirCaseStatement) statement).get_condition(), true);
				}
				constraints.add(SecFactory.condition_constraint(statement, condition, true));
				break;
			}
			case fals_flow:
			{
				CirStatement statement = flow.get_source().get_statement();
				SymExpression condition;
				if(statement instanceof CirIfStatement) {
					condition = SecFactory.sym_condition(((CirIfStatement) statement).get_condition(), false);
				}
				else {
					condition = SecFactory.sym_condition(((CirCaseStatement) statement).get_condition(), false);
				}
				constraints.add(SecFactory.condition_constraint(statement, condition, true));
				break;
			}
			default: break;
			}
		}
		
		if(constraints.isEmpty()) {
			return this.condition_constraint();
		}
		else if(constraints.size() == 1) {
			return constraints.iterator().next();
		}
		else {
			return SecFactory.conjunct_constraints(target_statement, constraints);
		}
	}
	
	@Override
	protected void propagate_set_reference(SecSetReferenceError error) throws Exception {
		SymExpression muta_expression = error.get_muta_expression().get_expression();
		SecStateError target_error = this.set_expression(muta_expression);
		this.append_propagation_pair(this.generate_constraint(error), target_error);
	}

	@Override
	protected void propagate_add_reference(SecAddReferenceError error) throws Exception {
		SymExpression ori_operand = error.get_orig_reference().get_expression();
		SymExpression add_operand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		CType type = this.target_reference().get_data_type();
		
		SymExpression muta_expression; 
		switch(operator) {
		case arith_add:
		{
			muta_expression = SymFactory.arith_add(type, ori_operand, add_operand);
			break;
		}
		case arith_sub:
		{
			muta_expression = SymFactory.arith_sub(type, ori_operand, add_operand);
			break;
		}
		case arith_mul:
		{
			muta_expression = SymFactory.arith_mul(type, ori_operand, add_operand);
			break;
		}
		case arith_div:
		{
			muta_expression = SymFactory.arith_div(type, ori_operand, add_operand);
			break;
		}
		case arith_mod:
		{
			muta_expression = SymFactory.arith_mod(type, ori_operand, add_operand);
			break;
		}
		case bit_and:
		{
			muta_expression = SymFactory.bitws_and(type, ori_operand, add_operand);
			break;
		}
		case bit_or:
		{
			muta_expression = SymFactory.bitws_ior(type, ori_operand, add_operand);
			break;
		}
		case bit_xor:
		{
			muta_expression = SymFactory.bitws_xor(type, ori_operand, add_operand);
			break;
		}
		case left_shift:
		{
			muta_expression = SymFactory.bitws_lsh(type, ori_operand, add_operand);
			break;
		}
		case righ_shift:
		{
			muta_expression = SymFactory.bitws_rsh(type, ori_operand, add_operand);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		SecStateError target_error = this.set_expression(muta_expression);
		this.append_propagation_pair(this.generate_constraint(error), target_error);
	}

	@Override
	protected void propagate_ins_reference(SecInsReferenceError error) throws Exception {
		SymExpression ori_operand = error.get_orig_reference().get_expression();
		SymExpression add_operand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		CType type = this.target_reference().get_data_type();
		
		SymExpression muta_expression; 
		switch(operator) {
		case arith_sub:
		{
			muta_expression = SymFactory.arith_sub(type, add_operand, ori_operand);
			break;
		}
		case arith_div:
		{
			muta_expression = SymFactory.arith_div(type, add_operand, ori_operand);
			break;
		}
		case arith_mod:
		{
			muta_expression = SymFactory.arith_mod(type, add_operand, ori_operand);
			break;
		}
		case left_shift:
		{
			muta_expression = SymFactory.bitws_lsh(type, add_operand, ori_operand);
			break;
		}
		case righ_shift:
		{
			muta_expression = SymFactory.bitws_rsh(type, add_operand, ori_operand);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		SecStateError target_error = this.set_expression(muta_expression);
		this.append_propagation_pair(this.generate_constraint(error), target_error);
	}

	@Override
	protected void propagate_uny_reference(SecUnyReferenceError error) throws Exception {
		SymExpression operand = error.get_orig_reference().get_expression();
		COperator operator = error.get_operator().get_operator();
		CType type = this.target_reference().get_data_type();
		
		SymExpression muta_expression;
		switch(operator) {
		case negative:
		{
			muta_expression = SymFactory.arith_neg(type, operand);
			break;
		}
		case bit_not:
		{
			muta_expression = SymFactory.bitws_rsv(type, operand);
			break;
		}
		case logic_not:
		{
			muta_expression = SymFactory.logic_not(operand);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		SecStateError target_error = this.set_expression(muta_expression);
		this.append_propagation_pair(this.generate_constraint(error), target_error);
	}

}
