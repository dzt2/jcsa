package com.jcsa.jcmutest.mutant.cir2mutant.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractClass;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirBixorErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirDataErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirIncreErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirValueErrorState;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It implements the inference of subsumption-relation over CirDataErrorState.
 * 
 * @author yukimula
 *
 */
final class CirDataStateInference {
	
	/* singleton mode */ /** constructor **/ private CirDataStateInference() {}
	static final CirDataStateInference inference = new CirDataStateInference();
	
	/* syntax-directed propagation */
	/**
	 * @param category		{set_expr | inc_expr | xor_expr}
	 * @param parent		the parent to which the data error propagates
	 * @param child			the child in which the source data error lies
	 * @param orig_value	the original value hold in the child expression
	 * @param muta_param	the parameter of the source data error state
	 * @param outputs		to preserve states directly subsumed by input
	 * @throws Exception
	 */
	private void vinf_by_if_condition(CirAbstractClass category,
			CirIfStatement parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(category == CirAbstractClass.set_expr) {
			/* 1. determines the branch to mutate from the source */
			Boolean mutated_branch;
			if(muta_param == CirMutations.true_value) {
				mutated_branch = Boolean.TRUE;
			}
			else if(muta_param == CirMutations.fals_value) {
				mutated_branch = Boolean.FALSE;
			}
			else {
				mutated_branch = null;
			}
			
			/* 2. it generates the subsumed flow error state from */
			if(mutated_branch != null) {
				CirExecution if_execution = parent.execution_of();
				CirExecutionFlow true_flow = null, fals_flow = null;
				for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
					if(flow.get_type() == CirExecutionFlowType.true_flow) {
						true_flow = flow;
					}
					else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
						fals_flow = flow;
					}
					else { continue; }
				}
				
				if(mutated_branch.booleanValue()) {	/* false --> true */
					outputs.add(CirAbstractState.mut_flow(fals_flow, true_flow));
				}
				else {								/* true --> false */
					outputs.add(CirAbstractState.mut_flow(true_flow, fals_flow));
				}
			}
		}
	}
	/**
	 * @param category		{set_expr | inc_expr | xor_expr}
	 * @param parent		the parent to which the data error propagates
	 * @param child			the child in which the source data error lies
	 * @param orig_value	the original value hold in the child expression
	 * @param muta_param	the parameter of the source data error state
	 * @param outputs		to preserve states directly subsumed by input
	 * @throws Exception
	 */
	private void vinf_by_case_condition(CirAbstractClass category,
			CirCaseStatement parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(category == CirAbstractClass.set_expr) {
			/* 1. determines the branch to mutate from the source */
			Boolean mutated_branch;
			if(muta_param == CirMutations.true_value) {
				mutated_branch = Boolean.TRUE;
			}
			else if(muta_param == CirMutations.fals_value) {
				mutated_branch = Boolean.FALSE;
			}
			else {
				mutated_branch = null;
			}
			
			/* 2. it generates the subsumed flow error state from */
			if(mutated_branch != null) {
				CirExecution if_execution = parent.execution_of();
				CirExecutionFlow true_flow = null, fals_flow = null;
				for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
					if(flow.get_type() == CirExecutionFlowType.true_flow) {
						true_flow = flow;
					}
					else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
						fals_flow = flow;
					}
					else { continue; }
				}
				
				if(mutated_branch.booleanValue()) {	/* false --> true */
					outputs.add(CirAbstractState.mut_flow(fals_flow, true_flow));
				}
				else {								/* true --> false */
					outputs.add(CirAbstractState.mut_flow(true_flow, fals_flow));
				}
			}
		}
	}
	/**
	 * @param category		{set_expr | inc_expr | xor_expr}
	 * @param parent		the parent to which the data error propagates
	 * @param child			the child in which the source data error lies
	 * @param orig_value	the original value hold in the child expression
	 * @param muta_param	the parameter of the source data error state
	 * @param outputs		to preserve states directly subsumed by input
	 * @throws Exception
	 */
	private void vinf_by_assign_lvalue(CirAbstractClass category,
			CirAssignStatement parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception { /* none */ }
	/**
	 * @param category		{set_expr | inc_expr | xor_expr}
	 * @param parent		the parent to which the data error propagates
	 * @param child			the child in which the source data error lies
	 * @param orig_value	the original value hold in the child expression
	 * @param muta_param	the parameter of the source data error state
	 * @param outputs		to preserve states directly subsumed by input
	 * @throws Exception
	 */
	private void vinf_by_assign_rvalue(CirAbstractClass category,
			CirAssignStatement parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception { 
		CirExpression reference = parent.get_lvalue();
		if(category == CirAbstractClass.set_expr) {
			outputs.add(CirAbstractState.set_expr(reference, muta_param));
		}
		else if(category == CirAbstractClass.inc_expr) {
			outputs.add(CirAbstractState.inc_expr(reference, muta_param));
		}
		else if(category == CirAbstractClass.xor_expr) {
			outputs.add(CirAbstractState.xor_expr(reference, muta_param));
		}
		else {
			throw new IllegalArgumentException("Unsupported " + category);
		}
	}
	/**
	 * @param category		{set_expr | inc_expr | xor_expr}
	 * @param parent		the parent to which the data error propagates
	 * @param child			the child in which the source data error lies
	 * @param orig_value	the original value hold in the child expression
	 * @param muta_param	the parameter of the source data error state
	 * @param outputs		to preserve states directly subsumed by input
	 * @throws Exception
	 */
	private void vinf_by_call_function(CirAbstractClass category,
			CirCallStatement parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception { 
		if(category == CirAbstractClass.set_expr) {
			/* 1. generate the mutated value of the parent expression */
			CirArgumentList alist = parent.get_arguments();
			List<Object> arguments = new ArrayList<Object>();
			for(int k = 0; k < alist.number_of_arguments(); k++) {
				arguments.add(alist.get_argument(k));
			}
			SymbolExpression muta_value = 
					SymbolFactory.call_expression(muta_param, arguments);
			
			/* 2. find the target expression in the return statement */
			CirExecution call = parent.execution_of();
			CirExecution wait = call.get_graph().get_execution(call.get_id() + 1);
			CirWaitAssignStatement wait_stmt = (CirWaitAssignStatement) wait.get_statement();
			CirWaitExpression expression = (CirWaitExpression) wait_stmt.get_rvalue();
			
			/* 3. generate set_expr error aat the target return stmt */
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else {
			/* ignore the other data error categories */
		}
	}
	/**
	 * @param category		{set_expr | inc_expr | xor_expr}
	 * @param parent		the parent to which the data error propagates
	 * @param child			the child in which the source data error lies
	 * @param orig_value	the original value hold in the child expression
	 * @param muta_param	the parameter of the source data error state
	 * @param outputs		to preserve states directly subsumed by input
	 * @throws Exception
	 */
	private void vinf_by_wait_expression(CirAbstractClass category,
			CirWaitExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception { 
		if(category == CirAbstractClass.set_expr) {
			/* 1. derive the argument list for generating muta_value */
			CirExecution wait = parent.execution_of();
			CirExecution call = wait.get_graph().get_execution(wait.get_id() - 1);
			CirCallStatement call_stmt = (CirCallStatement) call.get_statement();
			CirArgumentList alist = call_stmt.get_arguments();
			
			/* 2. generate the mutated value to the wait_expression */
			List<Object> arguments = new ArrayList<Object>();
			for(int k = 0; k < alist.number_of_arguments(); k++) {
				arguments.add(alist.get_argument(k));
			}
			SymbolExpression muta_value = SymbolFactory.call_expression(muta_param, arguments);
			
			/* 3. set_expr data error on the wait expression target */
			outputs.add(CirAbstractState.set_expr(parent, muta_value));
		}
		else {
			/* ignore the other data error categories */
		}
	}
	/**
	 * @param category		{set_expr | inc_expr | xor_expr}
	 * @param parent		the parent to which the data error propagates
	 * @param child			the child in which the source data error lies
	 * @param orig_value	the original value hold in the child expression
	 * @param muta_param	the parameter of the source data error state
	 * @param outputs		to preserve states directly subsumed by input
	 * @throws Exception
	 */
	private void vinf_by_defer_expression(CirAbstractClass category,
			CirDeferExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception { 
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.dereference(muta_param);
			outputs.add(CirAbstractState.set_expr(parent, muta_value));
		}
		else { /* no more propagation from this one */ }
	}
	/**
	 * @param category		{set_expr | inc_expr | xor_expr}
	 * @param parent		the parent to which the data error propagates
	 * @param child			the child in which the source data error lies
	 * @param orig_value	the original value hold in the child expression
	 * @param muta_param	the parameter of the source data error state
	 * @param outputs		to preserve states directly subsumed by input
	 * @throws Exception
	 */
	private void vinf_by_field_expression(CirAbstractClass category,
			CirFieldExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception { 
		if(category == CirAbstractClass.set_expr) {
			String field = parent.get_field().get_name();
			SymbolExpression muta_value = SymbolFactory.field_expression(child, field);
			outputs.add(CirAbstractState.set_expr(parent, muta_value));
		}
		else { /* no more propagation from this one */ }
	}
	/**
	 * @param category		{set_expr | inc_expr | xor_expr}
	 * @param parent		the parent to which the data error propagates
	 * @param child			the child in which the source data error lies
	 * @param orig_value	the original value hold in the child expression
	 * @param muta_param	the parameter of the source data error state
	 * @param outputs		to preserve states directly subsumed by input
	 * @throws Exception
	 */
	private void vinf_by_cast_expression(CirAbstractClass category,
			CirCastExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception { 
		if(category == CirAbstractClass.set_expr) {
			CType data_type = parent.get_type().get_typename();
			SymbolExpression muta_value = SymbolFactory.cast_expression(data_type, muta_param);
			outputs.add(CirAbstractState.set_expr(parent, muta_value));
		}
		else { /* no more propagation from this one */ }
	}
	/**
	 * @param category		{set_expr | inc_expr | xor_expr}
	 * @param parent		the parent to which the data error propagates
	 * @param child			the child in which the source data error lies
	 * @param orig_value	the original value hold in the child expression
	 * @param muta_param	the parameter of the source data error state
	 * @param outputs		to preserve states directly subsumed by input
	 * @throws Exception
	 */
	private void vinf_by_address_expression(CirAbstractClass category,
			CirAddressExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception { 
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.address_of(muta_param);
			outputs.add(CirAbstractState.set_expr(parent, muta_value));
		}
		else { /* no more propagation from this one */ }
	}
	/**
	 * @param category		{set_expr | inc_expr | xor_expr}
	 * @param parent		the parent to which the data error propagates
	 * @param child			the child in which the source data error lies
	 * @param orig_value	the original value hold in the child expression
	 * @param muta_param	the parameter of the source data error state
	 * @param outputs		to preserve states directly subsumed by input
	 * @throws Exception
	 */
	private void vinf_by_initializer_list(CirAbstractClass category,
			CirInitializerBody parent, CirExpression child, int index,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception { 
		if(category == CirAbstractClass.set_expr) {
			List<Object> elements = new ArrayList<Object>();
			for(int k = 0; k < parent.number_of_elements(); k++) {
				if(k == index) {
					elements.add(muta_param);
				}
				else {
					elements.add(parent.get_element(k));
				}
			}
			SymbolExpression muta_value = SymbolFactory.initializer_list(elements);
			outputs.add(CirAbstractState.set_expr(parent, muta_value));
		}
		else { /* no more propagation from this one */ }
	}
	/**
	 * @param category		{set_expr | inc_expr | xor_expr}
	 * @param parent		the parent to which the data error propagates
	 * @param child			the child in which the source data error lies
	 * @param orig_value	the original value hold in the child expression
	 * @param muta_param	the parameter of the source data error state
	 * @param outputs		to preserve states directly subsumed by input
	 * @throws Exception
	 */
	private void vinf_by_argument_list(CirAbstractClass category,
			CirArgumentList parent, CirExpression child, int index,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception { 
		if(category == CirAbstractClass.set_expr) { 
			/* 1. generate the argument list */
			List<Object> arguments = new ArrayList<Object>();
			for(int k = 0; k < parent.number_of_arguments(); k++) {
				if(k == index) {
					arguments.add(muta_param);
				}
				else {
					arguments.add(parent.get_argument(k));
				}
			}
			
			/* 2. generate the mutated value */
			CirCallStatement call_statement = (CirCallStatement) parent.get_parent();
			CirExpression function = call_statement.get_function();
			SymbolExpression muta_value = SymbolFactory.call_expression(function, arguments);
			
			/* 3. determine the target expression and next data error state */
			CirExecution call = call_statement.execution_of();
			CirExecution wait = call.get_graph().get_execution(call.get_id() + 1);
			CirWaitAssignStatement wait_statement = (CirWaitAssignStatement) wait.get_statement();
			CirExpression expression = wait_statement.get_rvalue();
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else { /* no more propagation from this one */ }
	}
	/**
	 * @param category		{set_expr | inc_expr | xor_expr}
	 * @param parent		the parent to which the data error propagates
	 * @param child			the child in which the source data error lies
	 * @param orig_value	the original value hold in the child expression
	 * @param muta_param	the parameter of the source data error state
	 * @param outputs		to preserve states directly subsumed by input
	 * @throws Exception
	 */
	private void vinf_by_compute_uoperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception { 
		COperator operator = parent.get_operator();
		switch(operator) {
		case positive:	this.vinf_by_arith_pos_uoperand(category, parent, child, orig_value, muta_param, outputs); break;
		case negative:	this.vinf_by_arith_neg_uoperand(category, parent, child, orig_value, muta_param, outputs); break;
		case bit_not:	this.vinf_by_bitws_rsv_uoperand(category, parent, child, orig_value, muta_param, outputs); break;
		case logic_not:	this.vinf_by_logic_not_uoperand(category, parent, child, orig_value, muta_param, outputs); break;	
		default:		throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	/**
	 * @param category		{set_expr | inc_expr | xor_expr}
	 * @param parent		the parent to which the data error propagates
	 * @param child			the child in which the source data error lies
	 * @param orig_value	the original value hold in the child expression
	 * @param muta_param	the parameter of the source data error state
	 * @param outputs		to preserve states directly subsumed by input
	 * @throws Exception
	 */
	private void vinf_by_compute_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception { 
		COperator operator = parent.get_operator();
		switch(operator) {
		case arith_add:		this.vinf_by_arith_add_loperand(category, parent, child, orig_value, muta_param, outputs); break;
		case arith_sub:		this.vinf_by_arith_sub_loperand(category, parent, child, orig_value, muta_param, outputs); break;
		case arith_mul:		this.vinf_by_arith_mul_loperand(category, parent, child, orig_value, muta_param, outputs); break;
		case arith_div:		this.vinf_by_arith_div_loperand(category, parent, child, orig_value, muta_param, outputs); break;
		case arith_mod:		this.vinf_by_arith_mod_loperand(category, parent, child, orig_value, muta_param, outputs); break;
		case bit_and:		this.vinf_by_bitws_and_loperand(category, parent, child, orig_value, muta_param, outputs); break;
		case bit_or:		this.vinf_by_bitws_ior_loperand(category, parent, child, orig_value, muta_param, outputs); break;
		case bit_xor:		this.vinf_by_bitws_xor_loperand(category, parent, child, orig_value, muta_param, outputs); break;
		case left_shift:	this.vinf_by_bitws_lsh_loperand(category, parent, child, orig_value, muta_param, outputs); break;
		case righ_shift:	this.vinf_by_bitws_rsh_loperand(category, parent, child, orig_value, muta_param, outputs); break;
		case logic_and:		this.vinf_by_logic_and_loperand(category, parent, child, orig_value, muta_param, outputs); break;
		case logic_or:		this.vinf_by_logic_ior_loperand(category, parent, child, orig_value, muta_param, outputs); break;
		case greater_tn:	this.vinf_by_greater_tn_loperand(category, parent, child, orig_value, muta_param, outputs);break;
		case greater_eq:	this.vinf_by_greater_eq_loperand(category, parent, child, orig_value, muta_param, outputs);break;
		case smaller_tn:	this.vinf_by_smaller_tn_loperand(category, parent, child, orig_value, muta_param, outputs);break;
		case smaller_eq:	this.vinf_by_smaller_eq_loperand(category, parent, child, orig_value, muta_param, outputs);break;
		case equal_with:	this.vinf_by_equal_with_loperand(category, parent, child, orig_value, muta_param, outputs);break;
		case not_equals:	this.vinf_by_not_equals_loperand(category, parent, child, orig_value, muta_param, outputs);break;
		default:			throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	/**
	 * @param category		{set_expr | inc_expr | xor_expr}
	 * @param parent		the parent to which the data error propagates
	 * @param child			the child in which the source data error lies
	 * @param orig_value	the original value hold in the child expression
	 * @param muta_param	the parameter of the source data error state
	 * @param outputs		to preserve states directly subsumed by input
	 * @throws Exception
	 */
	private void vinf_by_compute_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception { 
		COperator operator = parent.get_operator();
		switch(operator) {
		case arith_add:		this.vinf_by_arith_add_roperand(category, parent, child, orig_value, muta_param, outputs); break;
		case arith_sub:		this.vinf_by_arith_sub_roperand(category, parent, child, orig_value, muta_param, outputs); break;
		case arith_mul:		this.vinf_by_arith_mul_roperand(category, parent, child, orig_value, muta_param, outputs); break;
		case arith_div:		this.vinf_by_arith_div_roperand(category, parent, child, orig_value, muta_param, outputs); break;
		case arith_mod:		this.vinf_by_arith_mod_roperand(category, parent, child, orig_value, muta_param, outputs); break;
		case bit_and:		this.vinf_by_bitws_and_roperand(category, parent, child, orig_value, muta_param, outputs); break;
		case bit_or:		this.vinf_by_bitws_ior_roperand(category, parent, child, orig_value, muta_param, outputs); break;
		case bit_xor:		this.vinf_by_bitws_xor_roperand(category, parent, child, orig_value, muta_param, outputs); break;
		case left_shift:	this.vinf_by_bitws_lsh_roperand(category, parent, child, orig_value, muta_param, outputs); break;
		case righ_shift:	this.vinf_by_bitws_rsh_roperand(category, parent, child, orig_value, muta_param, outputs); break;
		case logic_and:		this.vinf_by_logic_and_roperand(category, parent, child, orig_value, muta_param, outputs); break;
		case logic_or:		this.vinf_by_logic_ior_roperand(category, parent, child, orig_value, muta_param, outputs); break;
		case greater_tn:	this.vinf_by_greater_tn_roperand(category, parent, child, orig_value, muta_param, outputs);break;
		case greater_eq:	this.vinf_by_greater_eq_roperand(category, parent, child, orig_value, muta_param, outputs);break;
		case smaller_tn:	this.vinf_by_smaller_tn_roperand(category, parent, child, orig_value, muta_param, outputs);break;
		case smaller_eq:	this.vinf_by_smaller_eq_roperand(category, parent, child, orig_value, muta_param, outputs);break;
		case equal_with:	this.vinf_by_equal_with_roperand(category, parent, child, orig_value, muta_param, outputs);break;
		case not_equals:	this.vinf_by_not_equals_roperand(category, parent, child, orig_value, muta_param, outputs);break;
		default:			throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	
	/* unary-operand error propagation */
	private void vinf_by_arith_pos_uoperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		if(category == CirAbstractClass.set_expr) {
			outputs.add(CirAbstractState.set_expr(expression, muta_param));
		}
		else if(category == CirAbstractClass.inc_expr) {
			outputs.add(CirAbstractState.inc_expr(expression, muta_param));
		}
		else if(category == CirAbstractClass.xor_expr) {
			outputs.add(CirAbstractState.xor_expr(expression, muta_param));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_arith_neg_uoperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_neg(muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) {
			SymbolExpression difference = SymbolFactory.arith_neg(muta_param);
			outputs.add(CirAbstractState.inc_expr(expression, difference));
		}
		else if(category == CirAbstractClass.xor_expr) { /* no propagation */ }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_bitws_rsv_uoperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_rsv(muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { 
			SymbolExpression difference = SymbolFactory.bitws_rsv(muta_param);
			outputs.add(CirAbstractState.xor_expr(expression, difference));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_logic_not_uoperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.sym_condition(muta_param, false);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	
	/* left-operand error propagation */
	private void vinf_by_arith_add_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_add(
						expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { 
			SymbolExpression difference = muta_param;
			outputs.add(CirAbstractState.inc_expr(expression, difference));
		}
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_arith_sub_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_sub(
						expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { 
			SymbolExpression difference = muta_param;
			outputs.add(CirAbstractState.inc_expr(expression, difference));
		}
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_arith_mul_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_mul(
						expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { 
			SymbolExpression difference = SymbolFactory.arith_mul(
					expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.inc_expr(expression, difference));
		}
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
		
		SymbolExpression condition = SymbolFactory.not_equals(roperand, Integer.valueOf(0));
		outputs.add(CirAbstractState.eva_need(expression.execution_of(), condition));
	}
	private void vinf_by_arith_div_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_div(
						expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_arith_mod_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_mod(
						expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_bitws_and_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_and(
						expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
		
		SymbolExpression condition = SymbolFactory.not_equals(roperand, Integer.valueOf(0));
		outputs.add(CirAbstractState.eva_need(expression.execution_of(), condition));
	}
	private void vinf_by_bitws_ior_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_ior(
						expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
		
		SymbolExpression condition = SymbolFactory.not_equals(roperand, Integer.valueOf(~0));
		outputs.add(CirAbstractState.eva_need(expression.execution_of(), condition));
	}
	private void vinf_by_bitws_xor_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_xor(
						expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { 
			SymbolExpression difference = muta_param;
			outputs.add(CirAbstractState.xor_expr(expression, difference));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_bitws_lsh_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_lsh(
					expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { 
			SymbolExpression difference = SymbolFactory.bitws_lsh(
					expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.inc_expr(expression, difference));
		}
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + category);
		}
	}
	private void vinf_by_bitws_rsh_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_rsh(
					expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + category);
		}
	}
	private void vinf_by_logic_and_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.logic_and(muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
		
		SymbolExpression condition = SymbolFactory.sym_condition(roperand, true);
		outputs.add(CirAbstractState.eva_need(expression.execution_of(), condition));
	}
	private void vinf_by_logic_ior_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.logic_ior(muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
		
		SymbolExpression condition = SymbolFactory.sym_condition(roperand, false);
		outputs.add(CirAbstractState.eva_need(expression.execution_of(), condition));
	}
	private void vinf_by_greater_tn_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.greater_tn(muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_greater_eq_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.greater_eq(muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_smaller_tn_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.smaller_tn(muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_smaller_eq_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.smaller_eq(muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_equal_with_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.equal_with(muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_not_equals_loperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression roperand = parent.get_operand(1);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.not_equals(muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	
	/* righ-operand error propagation */
	private void vinf_by_arith_add_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_add(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { 
			SymbolExpression difference = muta_param;
			outputs.add(CirAbstractState.inc_expr(expression, difference));
		}
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_arith_sub_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_sub(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { 
			SymbolExpression difference = SymbolFactory.arith_neg(muta_param);
			outputs.add(CirAbstractState.inc_expr(expression, difference));
		}
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_arith_mul_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_mul(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { 
			SymbolExpression difference = SymbolFactory.
					arith_mul(expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.inc_expr(expression, difference));
		}
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
		
		SymbolExpression condition = SymbolFactory.not_equals(loperand, Integer.valueOf(0));
		outputs.add(CirAbstractState.eva_need(expression.execution_of(), condition));
	}
	private void vinf_by_arith_div_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_div(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
		
		SymbolExpression condition = SymbolFactory.not_equals(loperand, Integer.valueOf(0));
		outputs.add(CirAbstractState.eva_need(expression.execution_of(), condition));
	}
	private void vinf_by_arith_mod_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_mod(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
		
		SymbolExpression condition = SymbolFactory.not_equals(loperand, Integer.valueOf(0));
		outputs.add(CirAbstractState.eva_need(expression.execution_of(), condition));
	}
	private void vinf_by_bitws_and_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_and(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
		
		SymbolExpression condition = SymbolFactory.not_equals(loperand, Integer.valueOf(0));
		outputs.add(CirAbstractState.eva_need(expression.execution_of(), condition));
	}
	private void vinf_by_bitws_ior_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_ior(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
		
		SymbolExpression condition = SymbolFactory.not_equals(loperand, Integer.valueOf(~0));
		outputs.add(CirAbstractState.eva_need(expression.execution_of(), condition));
	}
	private void vinf_by_bitws_xor_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_xor(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { 
			SymbolExpression difference = muta_param;
			outputs.add(CirAbstractState.xor_expr(expression, difference));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_bitws_lsh_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_lsh(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
		
		SymbolExpression condition = SymbolFactory.not_equals(loperand, Integer.valueOf(0));
		outputs.add(CirAbstractState.eva_need(expression.execution_of(), condition));
	}
	private void vinf_by_bitws_rsh_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_rsh(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
		
		SymbolExpression condition = SymbolFactory.not_equals(loperand, Integer.valueOf(0));
		outputs.add(CirAbstractState.eva_need(expression.execution_of(), condition));
	}
	private void vinf_by_logic_and_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.logic_and(loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
		
		SymbolExpression condition = SymbolFactory.sym_condition(loperand, true);
		outputs.add(CirAbstractState.eva_need(expression.execution_of(), condition));
	}
	private void vinf_by_logic_ior_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.logic_ior(loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
		
		SymbolExpression condition = SymbolFactory.sym_condition(loperand, false);
		outputs.add(CirAbstractState.eva_need(expression.execution_of(), condition));
	}
	private void vinf_by_greater_tn_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.greater_tn(loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_greater_eq_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.greater_eq(loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_smaller_tn_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.smaller_tn(loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_smaller_eq_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.smaller_eq(loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_equal_with_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.equal_with(loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	private void vinf_by_not_equals_roperand(CirAbstractClass category,
			CirComputeExpression parent, CirExpression child,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = parent;
		CirExpression loperand = parent.get_operand(0);
		if(category == CirAbstractClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.not_equals(loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(category == CirAbstractClass.inc_expr) { }
		else if(category == CirAbstractClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + category);
		}
	}
	
	/* syntax-directed transformation */
	private void vinf(CirDataErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		/* declarations and data getters */
		CirAbstractClass category = state.get_category();
		CirExpression child = state.get_expression();
		CirNode parent = child.get_parent();
		SymbolExpression orig_value = state.get_orig_value();
		SymbolExpression muta_param = state.get_roperand();
		
		/* syntax-directed inference */
		if(parent == null) { /* no more propagation of error */ }
		else if(parent instanceof CirIfStatement) {
			if(((CirIfStatement) parent).get_condition() == child) {
				this.vinf_by_if_condition(category, (CirIfStatement) parent, child, orig_value, muta_param, outputs);
			}
			else {
				throw new IllegalArgumentException("Invalid matched");
			}
		}
		else if(parent instanceof CirCaseStatement) {
			if(((CirCaseStatement) parent).get_condition() == child) {
				this.vinf_by_case_condition(category, (CirCaseStatement) parent, child, orig_value, muta_param, outputs);
			}
			else {
				throw new IllegalArgumentException("Invalid matched");
			}
		}
		else if(parent instanceof CirAssignStatement) {
			if(((CirAssignStatement) parent).get_lvalue() == child) {
				this.vinf_by_assign_lvalue(category, (CirAssignStatement) parent, child, orig_value, muta_param, outputs);
			}
			else {
				this.vinf_by_assign_rvalue(category, (CirAssignStatement) parent, child, orig_value, muta_param, outputs);
			}
		}
		else if(parent instanceof CirCallStatement) {
			if(((CirCallStatement) parent).get_function() == child) {
				this.vinf_by_call_function(category, (CirCallStatement) parent, child, orig_value, muta_param, outputs);
			}
			else {
				throw new IllegalArgumentException("Invalid matched");
			}
		}
		else if(parent instanceof CirWaitExpression) {
			if(((CirWaitExpression) parent).get_function() == child) {
				this.vinf_by_wait_expression(category, (CirWaitExpression) parent, child, orig_value, muta_param, outputs);
			}
			else {
				throw new IllegalArgumentException("Invalid matched");
			}
		}
		else if(parent instanceof CirDeferExpression) {
			if(((CirDeferExpression) parent).get_address() == child) {
				this.vinf_by_defer_expression(category, (CirDeferExpression) parent, child, orig_value, muta_param, outputs);
			}
			else {
				throw new IllegalArgumentException("Invalid matched");
			}
		}
		else if(parent instanceof CirFieldExpression) {
			if(((CirFieldExpression) parent).get_body() == child) {
				this.vinf_by_field_expression(category, (CirFieldExpression) parent, child, orig_value, muta_param, outputs);
			}
			else {
				throw new IllegalArgumentException("Invalid matched");
			}
		}
		else if(parent instanceof CirAddressExpression) {
			if(((CirAddressExpression) parent).get_operand() == child) {
				this.vinf_by_address_expression(category, (CirAddressExpression) parent, child, orig_value, muta_param, outputs);
			}
			else {
				throw new IllegalArgumentException("Invalid matched");
			}
		}
		else if(parent instanceof CirCastExpression) {
			if(((CirCastExpression) parent).get_operand() == child) {
				this.vinf_by_cast_expression(category, (CirCastExpression) parent, child, orig_value, muta_param, outputs);
			}
			else {
				throw new IllegalArgumentException("Invalid matched");
			}
		}
		else if(parent instanceof CirComputeExpression) {
			if(((CirComputeExpression) parent).number_of_operand() == 1) {
				this.vinf_by_compute_uoperand(category, (CirComputeExpression) parent, child, orig_value, muta_param, outputs);
			}
			else if(((CirComputeExpression) parent).get_operand(0) == child) {
				this.vinf_by_compute_loperand(category, (CirComputeExpression) parent, child, orig_value, muta_param, outputs);
			}
			else {
				this.vinf_by_compute_roperand(category, (CirComputeExpression) parent, child, orig_value, muta_param, outputs);
			}
		}
		else if(parent instanceof CirInitializerBody) {
			CirInitializerBody body = (CirInitializerBody) parent;
			for(int index = 0; index < body.number_of_elements(); index++) {
				if(body.get_element(index) == child) {
					this.vinf_by_initializer_list(category, body, child, index, orig_value, muta_param, outputs);
					break;
				}
			}
		}
		else if(parent instanceof CirArgumentList) {
			CirArgumentList list = (CirArgumentList) parent;
			for(int index = 0; index < list.number_of_arguments(); index++) {
				if(list.get_argument(index) == child) {
					this.vinf_by_argument_list(category, list, child, index, orig_value, muta_param, outputs);
					break;
				}
			}
		}
		else { /* no more errors propagation from this state */ }
	}
	/**
	 * @param expression
	 * @return whether the symbolic expression is a zero-constant
	 */
	private boolean is_zero_constant(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else if(expression instanceof SymbolConstant) {
			SymbolConstant constant = (SymbolConstant) expression;
			CType data_type = constant.get_data_type();
			if(CirMutations.is_doubles(data_type)) {
				return constant.get_double().doubleValue() == 0.0;
			}
			else {
				return constant.get_long().longValue() == 0L;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void inf_value_error(CirValueErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		/* 1. derive the expression and corresponding values */
		CirExecution execution = state.get_execution();
		CirExpression expression = state.get_expression();
		SymbolExpression orig_value = state.get_orig_value();
		SymbolExpression muta_param = state.get_muta_value();
		
		/* 2. general expression-across subsumption inference */
		if(CirMutations.is_trap_value(muta_param)) {					/** exception is found **/
			outputs.add(CirAbstractState.trp_stmt(execution));
		}
		else if(CirMutations.has_abst_value(muta_param)) { return; }	/** ignore abstract error **/
		else if(orig_value.equals(muta_param)) { return; }				/** equivalence ignored **/
		else {
			if(state.has_expression()) { this.vinf(state, outputs); }	/** use value propagation **/
			
			/* transformed to increment and bitwise-XOR operations */
			if(CirMutations.is_numeric(state.get_data_type())) {
				SymbolExpression difference = SymbolFactory.arith_sub(
						state.get_data_type(), muta_param, orig_value);
				difference = CirMutations.evaluate(difference);
				if(difference instanceof SymbolConstant) {
					if(state.has_expression()) {
						outputs.add(CirAbstractState.inc_expr(expression, difference));
					}
					else {
						expression = (CirExpression) state.get_identifier().get_source();
						outputs.add(CirAbstractState.inc_vdef(expression, difference));
					}
				}
			}
			if(CirMutations.is_integer(state.get_data_type())) {
				SymbolExpression difference = SymbolFactory.bitws_xor(
						state.get_data_type(), muta_param, orig_value);
				difference = CirMutations.evaluate(difference);
				if(difference instanceof SymbolConstant) {
					if(state.has_expression()) {
						outputs.add(CirAbstractState.xor_expr(expression, difference));
					}
					else {
						expression = (CirExpression) state.get_identifier().get_source();
						outputs.add(CirAbstractState.xor_vdef(expression, difference));
					}
				}
			}
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void inf_incre_error(CirIncreErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		/* 1. derive the expression and corresponding values */
		CirExecution execution = state.get_execution();
		CirExpression expression = state.get_expression();
		SymbolExpression orig_value = state.get_orig_value();
		SymbolExpression muta_param = state.get_difference();
		
		/* 2. general expression-across subsumption inference */
		if(CirMutations.is_trap_value(muta_param)) {					/** exception is found **/
			outputs.add(CirAbstractState.trp_stmt(execution));
		}
		else if(this.is_zero_constant(muta_param)) { return; }			/** ignore abstract error **/
		else {
			if(state.has_expression()) { this.vinf(state, outputs); }	/** use value propagation **/
			
			SymbolExpression muta_value = SymbolFactory.arith_add(
					state.get_data_type(), orig_value, muta_param);
			if(state.has_expression()) {
				outputs.add(CirAbstractState.set_expr(expression, muta_value));
			}
			else {
				expression = (CirExpression) state.get_identifier().get_source();
				outputs.add(CirAbstractState.set_vdef(expression, muta_value));
			}
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void inf_bixor_error(CirBixorErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		/* 1. derive the expression and corresponding values */
		CirExecution execution = state.get_execution();
		CirExpression expression = state.get_expression();
		SymbolExpression orig_value = state.get_orig_value();
		SymbolExpression muta_param = state.get_difference();
		
		/* 2. general expression-across subsumption inference */
		if(CirMutations.is_trap_value(muta_param)) {					/** exception is found **/
			outputs.add(CirAbstractState.trp_stmt(execution));
		}
		else if(this.is_zero_constant(muta_param)) { return; }			/** ignore abstract error **/
		else {
			if(state.has_expression()) { this.vinf(state, outputs); }	/** use value propagation **/
			
			SymbolExpression muta_value = SymbolFactory.arith_add(
					state.get_data_type(), orig_value, muta_param);
			if(state.has_expression()) {
				outputs.add(CirAbstractState.set_expr(expression, muta_value));
			}
			else {
				expression = (CirExpression) state.get_identifier().get_source();
				outputs.add(CirAbstractState.set_vdef(expression, muta_value));
			}
		}
	}
	/**
	 * It infers the subsumption between data error states
	 * @param state		the state from which the subsumption relations are inferred
	 * @param outputs	to preserve the set of states being subsumed by input state
	 * @param context	CDependGraph | CirExecutionPath | CStatePath | otherwise
	 * @throws Exception
	 */
	private void inf(CirDataErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else if(state instanceof CirValueErrorState) {
			this.inf_value_error((CirValueErrorState) state, outputs, context);
		}
		else if(state instanceof CirIncreErrorState) {
			this.inf_incre_error((CirIncreErrorState) state, outputs, context);
		}
		else if(state instanceof CirBixorErrorState) {
			this.inf_bixor_error((CirBixorErrorState) state, outputs, context);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + state);
		}
	}
	/**
	 * 	@param state	the source state from which the subsumption relation is computed
	 * 	@param outputs	to preserve the states being subsumed by the input state
	 * 	@param context	CDependGraph | CStatePath | CirExecutionPath | otherwise
	 * 	@throws Exception
	 */
	protected static void infer(CirDataErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else {
			Set<CirAbstractState> buffer = new HashSet<CirAbstractState>();
			inference.inf(state, buffer, context);
			for(CirAbstractState output : buffer) {
				outputs.add(output.normalize());
			}
		}
	}
	
}
