package com.jcsa.jcmutest.mutant.sta2mutant.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirBixorErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirDataErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirIncreErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirStoreClass;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirValueClass;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirValueErrorState;
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
 * It implements the inference of subsumption analysis over CirDataErrorState.
 * 
 * @author yukimula
 *
 */
final class StateDataInference {
	
	/* singleton mode */ /** constructor **/ private StateDataInference() { }
	private static final StateDataInference inf = new StateDataInference();
	
	/* implementation functions */
	/**
	 * @param execution		the execution point where the source error arises
	 * @param store_type	the type of store unit in the source data error
	 * @param store_key		the symbolic key to identify the source store unit
	 * @param context		the parent node in which the source error arises
	 * @param value_type	the value type of the source data type
	 * @param orig_value	the original value of the source data error
	 * @param muta_param	the mutated parameter of the source data error
	 * @param outputs		to preserve the subsumed error states subsumed
	 * @throws Exception
	 */
	private void vinf_by_if_condition(CirExecution execution,
			CirIfStatement context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		Boolean mutated_branch;
		if(muta_param == StateMutations.true_value) {
			mutated_branch = Boolean.TRUE;
		}
		else if(muta_param == StateMutations.fals_value) {
			mutated_branch = Boolean.FALSE;
		}
		else { mutated_branch = null; }
		
		if(mutated_branch != null) {
			CirExecution if_execution = context.execution_of();
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
				outputs.add(CirAbstractState.set_flow(fals_flow, true_flow));
			}
			else {								/* true --> false */
				outputs.add(CirAbstractState.set_flow(true_flow, fals_flow));
			}
		}
	}
	/**
	 * @param execution		the execution point where the source error arises
	 * @param store_type	the type of store unit in the source data error
	 * @param store_key		the symbolic key to identify the source store unit
	 * @param context		the parent node in which the source error arises
	 * @param value_type	the value type of the source data type
	 * @param orig_value	the original value of the source data error
	 * @param muta_param	the mutated parameter of the source data error
	 * @param outputs		to preserve the subsumed error states subsumed
	 * @throws Exception
	 */
	private void vinf_by_case_condition(CirExecution execution,
			CirCaseStatement context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		Boolean mutated_branch;
		if(muta_param == StateMutations.true_value) {
			mutated_branch = Boolean.TRUE;
		}
		else if(muta_param == StateMutations.fals_value) {
			mutated_branch = Boolean.FALSE;
		}
		else { mutated_branch = null; }
		
		if(mutated_branch != null) {
			CirExecution if_execution = context.execution_of();
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
				outputs.add(CirAbstractState.set_flow(fals_flow, true_flow));
			}
			else {								/* true --> false */
				outputs.add(CirAbstractState.set_flow(true_flow, fals_flow));
			}
		}
	}
	/**
	 * @param execution		the execution point where the source error arises
	 * @param store_type	the type of store unit in the source data error
	 * @param store_key		the symbolic key to identify the source store unit
	 * @param context		the parent node in which the source error arises
	 * @param value_type	the value type of the source data type
	 * @param orig_value	the original value of the source data error
	 * @param muta_param	the mutated parameter of the source data error
	 * @param outputs		to preserve the subsumed error states subsumed
	 * @throws Exception
	 */
	private void vinf_by_assign_lvalue(CirExecution execution,
			CirAssignStatement context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception { /* no more */ }
	/**
	 * @param execution		the execution point where the source error arises
	 * @param store_type	the type of store unit in the source data error
	 * @param store_key		the symbolic key to identify the source store unit
	 * @param context		the parent node in which the source error arises
	 * @param value_type	the value type of the source data type
	 * @param orig_value	the original value of the source data error
	 * @param muta_param	the mutated parameter of the source data error
	 * @param outputs		to preserve the subsumed error states subsumed
	 * @throws Exception
	 */
	private void vinf_by_assign_rvalue(CirExecution execution,
			CirAssignStatement context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression reference = context.get_lvalue();
		if(value_type == CirValueClass.set_expr) {
			outputs.add(CirAbstractState.set_expr(reference, muta_param));
		}
		else if(value_type == CirValueClass.inc_expr) {
			outputs.add(CirAbstractState.inc_expr(reference, muta_param));
		}
		else if(value_type == CirValueClass.xor_expr) {
			outputs.add(CirAbstractState.xor_expr(reference, muta_param));
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + value_type);
		}
	}
	/**
	 * @param execution		the execution point where the source error arises
	 * @param store_type	the type of store unit in the source data error
	 * @param store_key		the symbolic key to identify the source store unit
	 * @param context		the parent node in which the source error arises
	 * @param value_type	the value type of the source data type
	 * @param orig_value	the original value of the source data error
	 * @param muta_param	the mutated parameter of the source data error
	 * @param outputs		to preserve the subsumed error states subsumed
	 * @throws Exception
	 */
	private void vinf_by_call_fucntion(CirExecution execution,
			CirCallStatement context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		if(value_type == CirValueClass.set_expr) {
			CirArgumentList alist = context.get_arguments();
			List<Object> arguments = new ArrayList<Object>();
			for(int k = 0; k < alist.number_of_arguments(); k++) {
				arguments.add(alist.get_argument(k));
			}
			SymbolExpression muta_value = SymbolFactory.call_expression(muta_param, arguments);
			
			CirExecution call = context.execution_of();
			CirExecution wait = call.get_graph().get_execution(call.get_id() + 1);
			CirWaitAssignStatement wait_stmt = (CirWaitAssignStatement) wait.get_statement();
			CirWaitExpression expression = (CirWaitExpression) wait_stmt.get_rvalue();
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else { /* ignore the other possibility */ }
	}
	/**
	 * @param execution		the execution point where the source error arises
	 * @param store_type	the type of store unit in the source data error
	 * @param store_key		the symbolic key to identify the source store unit
	 * @param context		the parent node in which the source error arises
	 * @param value_type	the value type of the source data type
	 * @param orig_value	the original value of the source data error
	 * @param muta_param	the mutated parameter of the source data error
	 * @param outputs		to preserve the subsumed error states subsumed
	 * @throws Exception
	 */
	private void vinf_by_wait_expression(CirExecution execution,
			CirWaitExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		if(value_type == CirValueClass.set_expr) {
			CirExecution wait = context.execution_of();
			CirExecution call = wait.get_graph().get_execution(wait.get_id() - 1);
			CirCallStatement call_stmt = (CirCallStatement) call.get_statement();
			CirArgumentList alist = call_stmt.get_arguments();
			
			List<Object> arguments = new ArrayList<Object>();
			for(int k = 0; k < alist.number_of_arguments(); k++) {
				arguments.add(alist.get_argument(k));
			}
			SymbolExpression muta_value = SymbolFactory.call_expression(muta_param, arguments);
			outputs.add(CirAbstractState.set_expr(context, muta_value));
		}
		else { /* ignore the other possibility */ }
	}
	/**
	 * @param execution		the execution point where the source error arises
	 * @param store_type	the type of store unit in the source data error
	 * @param store_key		the symbolic key to identify the source store unit
	 * @param context		the parent node in which the source error arises
	 * @param value_type	the value type of the source data type
	 * @param orig_value	the original value of the source data error
	 * @param muta_param	the mutated parameter of the source data error
	 * @param outputs		to preserve the subsumed error states subsumed
	 * @throws Exception
	 */
	private void vinf_by_defer_expression(CirExecution execution,
			CirDeferExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		if(value_type == CirValueClass.set_expr) {
			CirExpression expression = context;
			SymbolExpression muta_value = SymbolFactory.dereference(muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else { /* ignore the other possibility */ }
	}
	/**
	 * @param execution		the execution point where the source error arises
	 * @param store_type	the type of store unit in the source data error
	 * @param store_key		the symbolic key to identify the source store unit
	 * @param context		the parent node in which the source error arises
	 * @param value_type	the value type of the source data type
	 * @param orig_value	the original value of the source data error
	 * @param muta_param	the mutated parameter of the source data error
	 * @param outputs		to preserve the subsumed error states subsumed
	 * @throws Exception
	 */
	private void vinf_by_field_expression(CirExecution execution,
			CirFieldExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		if(value_type == CirValueClass.set_expr) {
			CirExpression expression = context;
			String field = context.get_field().get_name();
			SymbolExpression muta_value = SymbolFactory.field_expression(muta_param, field);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else { /* ignore the other possibility */ }
	}
	/**
	 * @param execution		the execution point where the source error arises
	 * @param store_type	the type of store unit in the source data error
	 * @param store_key		the symbolic key to identify the source store unit
	 * @param context		the parent node in which the source error arises
	 * @param value_type	the value type of the source data type
	 * @param orig_value	the original value of the source data error
	 * @param muta_param	the mutated parameter of the source data error
	 * @param outputs		to preserve the subsumed error states subsumed
	 * @throws Exception
	 */
	private void vinf_by_cast_expression(CirExecution execution,
			CirCastExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		if(value_type == CirValueClass.set_expr) {
			CirExpression expression = context;
			SymbolExpression muta_value = SymbolFactory.cast_expression(context.get_data_type(), muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else { /* ignore the other possibility */ }
	}
	/**
	 * @param execution		the execution point where the source error arises
	 * @param store_type	the type of store unit in the source data error
	 * @param store_key		the symbolic key to identify the source store unit
	 * @param context		the parent node in which the source error arises
	 * @param value_type	the value type of the source data type
	 * @param orig_value	the original value of the source data error
	 * @param muta_param	the mutated parameter of the source data error
	 * @param outputs		to preserve the subsumed error states subsumed
	 * @throws Exception
	 */
	private void vinf_by_address_expression(CirExecution execution,
			CirAddressExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		if(value_type == CirValueClass.set_expr) {
			CirExpression expression = context;
			SymbolExpression muta_value = SymbolFactory.address_of(muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else { /* ignore the other possibility */ }
	}
	/**
	 * @param execution		the execution point where the source error arises
	 * @param store_type	the type of store unit in the source data error
	 * @param store_key		the symbolic key to identify the source store unit
	 * @param context		the parent node in which the source error arises
	 * @param value_type	the value type of the source data type
	 * @param orig_value	the original value of the source data error
	 * @param muta_param	the mutated parameter of the source data error
	 * @param outputs		to preserve the subsumed error states subsumed
	 * @throws Exception
	 */
	private void vinf_by_initializer_body(CirExecution execution,
			CirInitializerBody context, int k, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		if(value_type == CirValueClass.set_expr) {
			CirExpression expression = context;
			List<Object> elements = new ArrayList<Object>();
			for(int i = 0; i < context.number_of_elements(); i++) {
				if(i == k) {
					elements.add(muta_param);
				}
				else {
					elements.add(context.get_element(i));
				}
			}
			SymbolExpression muta_value = SymbolFactory.initializer_list(elements);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else { /* ignore the other possibility */ }
	}
	/**
	 * @param execution		the execution point where the source error arises
	 * @param store_type	the type of store unit in the source data error
	 * @param store_key		the symbolic key to identify the source store unit
	 * @param context		the parent node in which the source error arises
	 * @param value_type	the value type of the source data type
	 * @param orig_value	the original value of the source data error
	 * @param muta_param	the mutated parameter of the source data error
	 * @param outputs		to preserve the subsumed error states subsumed
	 * @throws Exception
	 */
	private void vinf_by_argument_list(CirExecution execution,
			CirArgumentList context, int k, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		if(value_type == CirValueClass.set_expr) {
			List<Object> arguments = new ArrayList<Object>();
			for(int i = 0; i < context.number_of_arguments(); i++) {
				if(i == k) {
					arguments.add(muta_param);
				}
				else {
					arguments.add(context.get_argument(i));
				}
			}
			
			CirExecution call = context.execution_of();
			CirExecution wait = call.get_graph().get_execution(call.get_id() + 1);
			CirWaitAssignStatement wait_stmt = (CirWaitAssignStatement) wait.get_statement();
			CirWaitExpression expression = (CirWaitExpression) wait_stmt.get_rvalue();
			SymbolExpression muta_value = SymbolFactory.call_expression(expression.get_function(), arguments);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else { /* ignore the other possibility */ }
	}
	/**
	 * @param execution		the execution point where the source error arises
	 * @param store_type	the type of store unit in the source data error
	 * @param store_key		the symbolic key to identify the source store unit
	 * @param context		the parent node in which the source error arises
	 * @param value_type	the value type of the source data type
	 * @param orig_value	the original value of the source data error
	 * @param muta_param	the mutated parameter of the source data error
	 * @param outputs		to preserve the subsumed error states subsumed
	 * @throws Exception
	 */
	private void vinf_by_compute_uoperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		COperator operator = context.get_operator();
		switch(operator) {
		case positive:	this.vinf_by_arith_pos_uoperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case negative:	this.vinf_by_arith_neg_uoperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case bit_not:	this.vinf_by_bitws_rsv_uoperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case logic_not:	this.vinf_by_logic_not_uoperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	/**
	 * @param execution		the execution point where the source error arises
	 * @param store_type	the type of store unit in the source data error
	 * @param store_key		the symbolic key to identify the source store unit
	 * @param context		the parent node in which the source error arises
	 * @param value_type	the value type of the source data type
	 * @param orig_value	the original value of the source data error
	 * @param muta_param	the mutated parameter of the source data error
	 * @param outputs		to preserve the subsumed error states subsumed
	 * @throws Exception
	 */
	private void vinf_by_compute_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		COperator operator = context.get_operator();
		switch(operator) {
		case arith_add:		this.vinf_by_arith_add_loperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case arith_sub:		this.vinf_by_arith_sub_loperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case arith_mul:		this.vinf_by_arith_mul_loperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case arith_div:		this.vinf_by_arith_div_loperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case arith_mod:		this.vinf_by_arith_mod_loperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case bit_and:		this.vinf_by_bitws_and_loperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case bit_or:		this.vinf_by_bitws_ior_loperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case bit_xor:		this.vinf_by_bitws_xor_loperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case left_shift:	this.vinf_by_bitws_lsh_loperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case righ_shift:	this.vinf_by_bitws_rsh_loperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case logic_and:		this.vinf_by_logic_and_loperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case logic_or:		this.vinf_by_logic_ior_loperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case greater_tn:	this.vinf_by_greater_tn_loperand(execution, context, value_type, orig_value, muta_param, outputs);break;
		case greater_eq:	this.vinf_by_greater_eq_loperand(execution, context, value_type, orig_value, muta_param, outputs);break;
		case smaller_tn:	this.vinf_by_smaller_tn_loperand(execution, context, value_type, orig_value, muta_param, outputs);break;
		case smaller_eq:	this.vinf_by_smaller_eq_loperand(execution, context, value_type, orig_value, muta_param, outputs);break;
		case equal_with:	this.vinf_by_equal_with_loperand(execution, context, value_type, orig_value, muta_param, outputs);break;
		case not_equals:	this.vinf_by_not_equals_loperand(execution, context, value_type, orig_value, muta_param, outputs);break;
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	/**
	 * @param execution		the execution point where the source error arises
	 * @param store_type	the type of store unit in the source data error
	 * @param store_key		the symbolic key to identify the source store unit
	 * @param context		the parent node in which the source error arises
	 * @param value_type	the value type of the source data type
	 * @param orig_value	the original value of the source data error
	 * @param muta_param	the mutated parameter of the source data error
	 * @param outputs		to preserve the subsumed error states subsumed
	 * @throws Exception
	 */
	private void vinf_by_compute_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		COperator operator = context.get_operator();
		switch(operator) {
		case arith_add:		this.vinf_by_arith_add_roperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case arith_sub:		this.vinf_by_arith_sub_roperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case arith_mul:		this.vinf_by_arith_mul_roperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case arith_div:		this.vinf_by_arith_div_roperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case arith_mod:		this.vinf_by_arith_mod_roperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case bit_and:		this.vinf_by_bitws_and_roperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case bit_or:		this.vinf_by_bitws_ior_roperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case bit_xor:		this.vinf_by_bitws_xor_roperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case left_shift:	this.vinf_by_bitws_lsh_roperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case righ_shift:	this.vinf_by_bitws_rsh_roperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case logic_and:		this.vinf_by_logic_and_roperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case logic_or:		this.vinf_by_logic_ior_roperand(execution, context, value_type, orig_value, muta_param, outputs); break;
		case greater_tn:	this.vinf_by_greater_tn_roperand(execution, context, value_type, orig_value, muta_param, outputs);break;
		case greater_eq:	this.vinf_by_greater_eq_roperand(execution, context, value_type, orig_value, muta_param, outputs);break;
		case smaller_tn:	this.vinf_by_smaller_tn_roperand(execution, context, value_type, orig_value, muta_param, outputs);break;
		case smaller_eq:	this.vinf_by_smaller_eq_roperand(execution, context, value_type, orig_value, muta_param, outputs);break;
		case equal_with:	this.vinf_by_equal_with_roperand(execution, context, value_type, orig_value, muta_param, outputs);break;
		case not_equals:	this.vinf_by_not_equals_roperand(execution, context, value_type, orig_value, muta_param, outputs);break;
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	
	/* computational operation guided translation (uoperand) */
	private void vinf_by_arith_pos_uoperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		if(value_type == CirValueClass.set_expr) {
			outputs.add(CirAbstractState.set_expr(expression, muta_param));
		}
		else if(value_type == CirValueClass.inc_expr) {
			outputs.add(CirAbstractState.inc_expr(expression, muta_param));
		}
		else if(value_type == CirValueClass.xor_expr) {
			outputs.add(CirAbstractState.xor_expr(expression, muta_param));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + value_type);
		}
	}
	private void vinf_by_arith_neg_uoperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_neg(muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) {
			SymbolExpression difference = SymbolFactory.arith_neg(muta_param);
			outputs.add(CirAbstractState.inc_expr(expression, difference));
		}
		else if(value_type == CirValueClass.xor_expr) { /* set_expr parse */ }
		else {
			throw new IllegalArgumentException("Invalid: " + value_type);
		}
	}
	private void vinf_by_bitws_rsv_uoperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_rsv(muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { 
			SymbolExpression difference = SymbolFactory.bitws_rsv(muta_param);
			outputs.add(CirAbstractState.xor_expr(expression, difference));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + value_type);
		}
	}
	private void vinf_by_logic_not_uoperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.sym_condition(muta_param, false);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Invalid: " + value_type);
		}
	}
	
	/* computational operation guided translation (loperand) */
	private void vinf_by_arith_add_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_add(expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) {
			SymbolExpression difference = muta_param;
			outputs.add(CirAbstractState.inc_expr(expression, difference));
		}
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_arith_sub_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_sub(expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) {
			SymbolExpression difference = muta_param;
			outputs.add(CirAbstractState.inc_expr(expression, difference));
		}
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_arith_mul_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_mul(
					expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) {
			SymbolExpression difference = SymbolFactory.arith_mul(
					expression.get_data_type(), roperand, muta_param);
			outputs.add(CirAbstractState.inc_expr(expression, difference));
		}
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
		
		SymbolExpression condition = SymbolFactory.sym_condition(roperand, true);
		outputs.add(CirAbstractState.eva_cond(execution, condition, true));
	}
	private void vinf_by_arith_div_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_div(
					expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_arith_mod_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_mod(
					expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { 
			SymbolExpression condition = SymbolFactory.sym_condition(SymbolFactory.
					arith_mod(expression.get_data_type(), muta_param, roperand), true);
			outputs.add(CirAbstractState.eva_cond(execution, condition, true));
		}
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_bitws_and_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_and(
					expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
		
		SymbolExpression condition = SymbolFactory.sym_condition(roperand, true);
		outputs.add(CirAbstractState.eva_cond(execution, condition, true));
	}
	private void vinf_by_bitws_ior_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_ior(
					expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
		
		SymbolExpression condition = SymbolFactory.not_equals(roperand, Integer.valueOf(~0));
		outputs.add(CirAbstractState.eva_cond(execution, condition, true));
	}
	private void vinf_by_bitws_xor_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_ior(
					expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { 
			SymbolExpression difference = muta_param;
			outputs.add(CirAbstractState.xor_expr(expression, difference));
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_bitws_lsh_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_lsh(
					expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { 
			SymbolExpression difference = SymbolFactory.bitws_lsh(
					expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.inc_expr(expression, difference));
		}
		else if(value_type == CirValueClass.xor_expr) { 
			SymbolExpression difference = SymbolFactory.bitws_lsh(
					expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.xor_expr(expression, difference));
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_bitws_rsh_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_rsh(
					expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { 
			SymbolExpression difference = SymbolFactory.bitws_rsh(
					expression.get_data_type(), muta_param, roperand);
			outputs.add(CirAbstractState.xor_expr(expression, difference));
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_logic_and_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.logic_and(muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
		
		SymbolExpression condition = SymbolFactory.sym_condition(roperand, true);
		outputs.add(CirAbstractState.eva_cond(execution, condition, true));
	}
	private void vinf_by_logic_ior_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.logic_ior(muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
		
		SymbolExpression condition = SymbolFactory.sym_condition(roperand, false);
		outputs.add(CirAbstractState.eva_cond(execution, condition, true));
	}
	private void vinf_by_greater_tn_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.greater_tn(muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_greater_eq_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.greater_eq(muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_smaller_tn_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.smaller_tn(muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_smaller_eq_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.smaller_eq(muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_equal_with_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.equal_with(muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_not_equals_loperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression roperand = context.get_operand(1);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.not_equals(muta_param, roperand);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	
	/* computational operation guided translation (roperand) */
	private void vinf_by_arith_add_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_add(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) {
			SymbolExpression difference = muta_param;
			outputs.add(CirAbstractState.inc_expr(expression, difference));
		}
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_arith_sub_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_sub(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) {
			SymbolExpression difference = SymbolFactory.arith_neg(muta_param);
			outputs.add(CirAbstractState.inc_expr(expression, difference));
		}
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_arith_mul_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_mul(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) {
			SymbolExpression difference = SymbolFactory.arith_mul(
					expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.inc_expr(expression, difference));
		}
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
		
		SymbolExpression condition = SymbolFactory.sym_condition(loperand, true);
		outputs.add(CirAbstractState.eva_cond(execution, condition, true));
	}
	private void vinf_by_arith_div_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_div(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
		
		SymbolExpression condition = SymbolFactory.sym_condition(loperand, true);
		outputs.add(CirAbstractState.eva_cond(execution, condition, true));
	}
	private void vinf_by_arith_mod_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.arith_mod(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
		
		SymbolExpression condition = SymbolFactory.sym_condition(loperand, true);
		outputs.add(CirAbstractState.eva_cond(execution, condition, true));
	}
	private void vinf_by_bitws_and_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_and(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
		
		SymbolExpression condition = SymbolFactory.sym_condition(loperand, true);
		outputs.add(CirAbstractState.eva_cond(execution, condition, true));
	}
	private void vinf_by_bitws_ior_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_ior(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
		
		SymbolExpression condition = SymbolFactory.not_equals(loperand, Integer.valueOf(~0));
		outputs.add(CirAbstractState.eva_cond(execution, condition, true));
	}
	private void vinf_by_bitws_xor_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_xor(
						expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { 
			SymbolExpression difference = muta_param;
			outputs.add(CirAbstractState.xor_expr(expression, difference));
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_bitws_lsh_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_lsh(
					expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
		
		SymbolExpression condition = SymbolFactory.sym_condition(loperand, true);
		outputs.add(CirAbstractState.eva_cond(execution, condition, true));
	}
	private void vinf_by_bitws_rsh_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.bitws_rsh(
					expression.get_data_type(), loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
		
		SymbolExpression condition = SymbolFactory.sym_condition(loperand, true);
		outputs.add(CirAbstractState.eva_cond(execution, condition, true));
	}
	private void vinf_by_logic_and_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.logic_and(loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
		
		SymbolExpression condition = SymbolFactory.sym_condition(loperand, true);
		outputs.add(CirAbstractState.eva_cond(execution, condition, true));
	}
	private void vinf_by_logic_ior_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.logic_ior(loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
		
		SymbolExpression condition = SymbolFactory.sym_condition(loperand, false);
		outputs.add(CirAbstractState.eva_cond(execution, condition, true));
	}
	private void vinf_by_greater_tn_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.greater_tn(loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_greater_eq_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.greater_eq(loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_smaller_tn_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.smaller_tn(loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_smaller_eq_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.smaller_eq(loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_equal_with_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.equal_with(loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	private void vinf_by_not_equals_roperand(CirExecution execution,
			CirComputeExpression context, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirExpression expression = context;
		CirExpression loperand = context.get_operand(0);
		if(value_type == CirValueClass.set_expr) {
			SymbolExpression muta_value = SymbolFactory.not_equals(loperand, muta_param);
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
		else if(value_type == CirValueClass.inc_expr) { }
		else if(value_type == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupported: " + value_type);
		}
	}
	
	/* interface for analyzing subsumption between data error */
	/**
	 * It infers the value error across expression-level
	 * @param execution			the execution point where the error arises
	 * @param store_type		the class of store unit of the child error
	 * @param store_key			the symbolic identify to decide store unit
	 * @param parent			the parent node of the expression node
	 * @param expression		the expression where the child error arise
	 * @param value_type		set_expr | inc_expr | xor_expr
	 * @param orig_value		the original value of the child data error
	 * @param muta_param		the parameter to define the mutation error
	 * @param outputs			to preserve the abstract state subsumed by
	 * @throws Exception
	 */
	private void vinf(CirExecution execution,
			CirExpression expression, CirValueClass value_type, 
			SymbolExpression orig_value, SymbolExpression muta_param, 
			Collection<CirAbstractState> outputs) throws Exception {
		CirNode parent = expression.get_parent();
		if(parent == null) { /* no more inference from error */ }
		else if(parent instanceof CirIfStatement) {
			CirIfStatement context = (CirIfStatement) parent;
			if(context.get_condition() == expression) {
				this.vinf_by_if_condition(execution, 
						context, value_type, orig_value, muta_param, outputs);
			}
			else {
				throw new IllegalArgumentException("Unmatched: " + context);
			}
		}
		else if(parent instanceof CirCaseStatement) {
			CirCaseStatement context = (CirCaseStatement) parent;
			if(context.get_condition() == expression) {
				this.vinf_by_case_condition(execution, 
						context, value_type, orig_value, muta_param, outputs);
			}
			else {
				throw new IllegalArgumentException("Unmatched: " + context);
			}
		}
		else if(parent instanceof CirAssignStatement) {
			CirAssignStatement context = (CirAssignStatement) parent;
			if(context.get_lvalue() == expression) {
				this.vinf_by_assign_lvalue(execution, 
						context, value_type, orig_value, muta_param, outputs);
			}
			else {
				this.vinf_by_assign_rvalue(execution, 
						context, value_type, orig_value, muta_param, outputs);
			}
		}
		else if(parent instanceof CirCallStatement) {
			CirCallStatement context = (CirCallStatement) parent;
			if(context.get_function() == expression) {
				this.vinf_by_call_fucntion(execution, 
						context, value_type, orig_value, muta_param, outputs);
			}
			else {
				throw new IllegalArgumentException("Unmatched: " + context);
			}
		}
		else if(parent instanceof CirDeferExpression) {
			CirDeferExpression context = (CirDeferExpression) parent;
			if(context.get_address() == expression) {
				this.vinf_by_defer_expression(execution, 
						context, value_type, orig_value, muta_param, outputs);
			}
			else {
				throw new IllegalArgumentException("Unmatched: " + context);
			}
		}
		else if(parent instanceof CirFieldExpression) {
			CirFieldExpression context = (CirFieldExpression) parent;
			if(context.get_body() == expression) {
				this.vinf_by_field_expression(execution, 
						context, value_type, orig_value, muta_param, outputs);
			}
			else {
				throw new IllegalArgumentException("Unmatched: " + context);
			}
		}
		else if(parent instanceof CirAddressExpression) {
			CirAddressExpression context = (CirAddressExpression) parent;
			if(context.get_operand() == expression) {
				this.vinf_by_address_expression(execution,
						context, value_type, orig_value, muta_param, outputs);
			}
			else {
				throw new IllegalArgumentException("Unmatched: " + context);
			}
		}
		else if(parent instanceof CirCastExpression) {
			CirCastExpression context = (CirCastExpression) parent;
			if(context.get_operand() == expression) {
				this.vinf_by_cast_expression(execution, 
						context, value_type, orig_value, muta_param, outputs);
			}
			else {
				throw new IllegalArgumentException("Unmatched: " + context);
			}
		}
		else if(parent instanceof CirWaitExpression) {
			CirWaitExpression context = (CirWaitExpression) parent;
			if(context.get_function() == expression) {
				this.vinf_by_wait_expression(execution, 
						context, value_type, orig_value, muta_param, outputs);
			}
			else {
				throw new IllegalArgumentException("Unmatched: " + context);
			}
		}
		else if(parent instanceof CirInitializerBody) {
			CirInitializerBody context = (CirInitializerBody) parent;
			for(int k = 0; k < context.number_of_elements(); k++) {
				if(context.get_element(k) == expression) {
					this.vinf_by_initializer_body(execution, 
							context, k, value_type, orig_value, muta_param, outputs);
					break;
				}
			}
		}
		else if(parent instanceof CirArgumentList) {
			CirArgumentList context = (CirArgumentList) parent;
			for(int k = 0; k < context.number_of_arguments(); k++) {
				if(context.get_argument(k) == expression) {
					this.vinf_by_argument_list(execution, 
							context, k, value_type, orig_value, muta_param, outputs);
					break;
				}
			}
		}
		else if(parent instanceof CirComputeExpression) {
			CirComputeExpression context = (CirComputeExpression) parent;
			if(context.number_of_operand() == 1) {
				this.vinf_by_compute_uoperand(execution, 
						context, value_type, orig_value, muta_param, outputs);
			}
			else if(context.get_operand(0) == expression) {
				this.vinf_by_compute_loperand(execution,
						context, value_type, orig_value, muta_param, outputs);
			}
			else {
				this.vinf_by_compute_roperand(execution, 
						context, value_type, orig_value, muta_param, outputs);
			}
		}
		else { /* no more inference from error */ }
	}
	
	/* type-directed algorithms to derive subsumption relations */
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
			if(StateMutations.is_doubles(data_type)) {
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
		CirStoreClass store_type = state.get_store_type();
		SymbolExpression store_key = state.get_store_key();
		CirValueClass value_type = state.get_operator();
		
		/* 2. general expression-across subsumption inference */
		if(StateMutations.is_trap_value(muta_param)) {					/** exception arises **/
			outputs.add(CirAbstractState.set_trap(execution));
		}
		else if(StateMutations.has_abst_value(muta_param)) { return; }	/** ignore abstract error **/
		else if(orig_value.equals(muta_param)) { return; }				/** equivalence ignored **/
		else if(state.is_defined_point()) {								/** general value infer **/
			this.vinf(execution, expression, value_type, orig_value, muta_param, outputs);
			if(StateMutations.is_numeric(expression)) {
				SymbolExpression difference = SymbolFactory.arith_sub(
						expression.get_data_type(), muta_param, orig_value);
				difference = StateMutations.evaluate(difference);
				if(difference instanceof SymbolConstant) {
					if(store_type == CirStoreClass.vdef) {
						outputs.add(CirAbstractState.inc_vdef(expression, store_key, difference));
					}
					else {
						outputs.add(CirAbstractState.inc_expr(expression, difference));
					}
				}
			}
			if(StateMutations.is_integer(expression)) {
				SymbolExpression difference = SymbolFactory.bitws_xor(
						expression.get_data_type(), muta_param, orig_value);
				difference = StateMutations.evaluate(difference);
				if(difference instanceof SymbolConstant) {
					if(store_type == CirStoreClass.vdef) {
						outputs.add(CirAbstractState.xor_vdef(expression, store_key, difference));
					}
					else {
						outputs.add(CirAbstractState.xor_expr(expression, difference));
					}
				}
			}
		}
		else { /* TODO definition point propagation on data-flow */ }
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
		SymbolExpression orig_value = state.get_base_value();
		SymbolExpression muta_param = state.get_difference();
		CirStoreClass store_type = state.get_store_type();
		SymbolExpression store_key = state.get_store_key();
		CirValueClass value_type = state.get_operator();
		
		/* 2. general expression-across subsumption inference */
		if(StateMutations.is_trap_value(muta_param)) {					/** exception arises **/
			outputs.add(CirAbstractState.set_trap(execution));
		}
		else if(StateMutations.has_abst_value(muta_param)) { return; }	/** ignore abstract error **/
		else if(this.is_zero_constant(muta_param)) { return; }			/** equivalence ignored **/
		else if(state.is_defined_point()) {								/** general value infer **/
			this.vinf(execution, expression, value_type, orig_value, muta_param, outputs);
			SymbolExpression muta_value = SymbolFactory.arith_add(
					expression.get_data_type(), orig_value, muta_param);
			if(store_type == CirStoreClass.vdef) {
				outputs.add(CirAbstractState.set_vdef(expression, store_key, muta_value));
			}
			else {
				outputs.add(CirAbstractState.set_expr(expression, muta_value));
			}
		}
		else { /* TODO definition point propagation on data-flow */ }
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
		SymbolExpression orig_value = state.get_base_value();
		SymbolExpression muta_param = state.get_difference();
		CirStoreClass store_type = state.get_store_type();
		SymbolExpression store_key = state.get_store_key();
		CirValueClass value_type = state.get_operator();
		
		/* 2. general expression-across subsumption inference */
		if(StateMutations.is_trap_value(muta_param)) {					/** exception arises **/
			outputs.add(CirAbstractState.set_trap(execution));
		}
		else if(StateMutations.has_abst_value(muta_param)) { return; }	/** ignore abstract error **/
		else if(this.is_zero_constant(muta_param)) { return; }			/** equivalence ignored **/
		else if(state.is_defined_point()) {								/** general value infer **/
			this.vinf(execution, expression, value_type, orig_value, muta_param, outputs);
			SymbolExpression muta_value = SymbolFactory.bitws_xor(
					expression.get_data_type(), orig_value, muta_param);
			if(store_type == CirStoreClass.vdef) {
				outputs.add(CirAbstractState.set_vdef(expression, store_key, muta_value));
			}
			else {
				outputs.add(CirAbstractState.set_expr(expression, muta_value));
			}
		}
		else { /* TODO definition point propagation on data-flow */ }
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
			inf.inf(state, buffer, context);
			for(CirAbstractState output : buffer) {
				outputs.add(output.normalize());
			}
		}
	}
	
}
