package com.jcsa.jcmutest.mutant.sta2mutant.__back__;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirDataErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirStoreClass;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirValueClass;
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
 * It implements the local propagation analysis.
 * 
 * @author yukimula
 *
 */
public final class CirLDataStatePropagate {
	
	/* singleton mode */  /** constructors **/  private CirLDataStatePropagate() { } 
	private static final CirLDataStatePropagate propagate = new CirLDataStatePropagate();
	
	/* public interfaces */
	/**
	 * @param state		the data error state as the input to propagate
	 * @param outputs	the set to preserve the new error state in propagation
	 * @param context	null | CDependGraph | CStatePath | CirExecutionPath
	 * @throws Exception
	 */
	protected static void propagate(Object context, CirDataErrorState state, 
			Collection<CirAbstractState> outputs) throws Exception {
		propagate.propagate_iter(state, outputs, context);
	}
	/**
	 * @param state		the data error state as the input to propagate
	 * @param outputs	the set to preserve the new error state in propagation
	 * @param context	null | CDependGraph | CStatePath | CirExecutionPath
	 * @throws Exception
	 */
	private void propagate_iter(CirDataErrorState state, 
			Collection<CirAbstractState> outputs, Object context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else {
			/* declarations and data initialization */
			outputs.clear(); 
			state = (CirDataErrorState) state.normalize();
			CirExecution execution = state.get_execution();
			CirExpression expression = state.get_expression();
			CirStoreClass store_type = state.get_store_type();
			SymbolExpression store_key = state.get_store_key();
			SymbolExpression orig_value = state.get_loperand();
			SymbolExpression muta_param = state.get_roperand();
			CirValueClass value_class = state.get_operator();
			CirNode pcontext = expression.get_parent();
			this.propagate_over(execution, expression, pcontext, 
					store_type, store_key, value_class, pcontext, 
					orig_value, muta_param, outputs);
		}
	}
	
	/* syntax-directed translation */
	/**
	 * @param execution		the execution point where the data error arises
	 * @param expression	the expression location to arise the data error
	 * @param pcontext		the parent node of the expression to syntax direct
	 * @param store_type	the type of the store unit among the error state
	 * @param store_key		the symbolic identifier of the store unit to define
	 * @param value_class	the class of data error state value to classify
	 * @param context		CDependGraph | CStatePath | CirExecutionPath | null
	 * @param orig_value	the original value 
	 * @param muta_param	the mutated parameter
	 * @param outputs		the set to preserve the error state for propagation
	 * @throws Exception
	 */
	private void propagate_over(CirExecution execution, 
			CirExpression expression, CirNode pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(pcontext == null) { 
			/* no more propagation from expression to NULL-parent */
		}
		else if(pcontext instanceof CirIfStatement) {
			this.propagate_on_if_statement(execution, expression, 
					(CirIfStatement) pcontext, store_type, store_key, 
					value_class, context, orig_value, muta_param, outputs);
		}
		else if(pcontext instanceof CirCaseStatement) {
			this.propagate_on_case_statement(execution, expression, 
					(CirCaseStatement) pcontext, store_type, store_key, 
					value_class, context, orig_value, muta_param, outputs);
		}
		else if(pcontext instanceof CirAssignStatement) {
			if(((CirAssignStatement) pcontext).get_lvalue() == expression) {
				this.propagate_on_assign_lvalue(execution, expression, 
						(CirAssignStatement) pcontext, store_type, store_key, 
						value_class, context, orig_value, muta_param, outputs);
			}
			else {
				this.propagate_on_assign_rvalue(execution, expression, 
						(CirAssignStatement) pcontext, store_type, store_key, 
						value_class, context, orig_value, muta_param, outputs);
			}
		}
		else if(pcontext instanceof CirCallStatement) {
			this.propagate_on_call_statement(execution, expression, 
					(CirCallStatement) pcontext, store_type, store_key, 
					value_class, context, orig_value, muta_param, outputs);
		}
		else if(pcontext instanceof CirArgumentList) {
			CirArgumentList alist = (CirArgumentList) pcontext;
			for(int k = 0; k < alist.number_of_arguments(); k++) {
				if(alist.get_argument(k) == expression) {
					this.propagate_on_argument_list(execution, expression, alist, k, 
							store_type, store_key, 
							value_class, context, orig_value, muta_param, outputs);
				}
			}
		}
		else if(pcontext instanceof CirDeferExpression) {
			this.propagate_on_defer_expression(execution, expression, 
					(CirDeferExpression) pcontext, store_type, store_key, 
					value_class, context, orig_value, muta_param, outputs);
		}
		else if(pcontext instanceof CirFieldExpression) {
			this.propagate_on_field_expression(execution, expression, 
					(CirFieldExpression) pcontext, store_type, store_key, 
					value_class, context, orig_value, muta_param, outputs);
		}
		else if(pcontext instanceof CirAddressExpression) {
			this.propagate_on_address_expression(execution, expression, 
					(CirAddressExpression) pcontext, store_type, store_key, 
					value_class, context, orig_value, muta_param, outputs);
		}
		else if(pcontext instanceof CirCastExpression) {
			this.propagate_on_cast_expression(execution, expression, 
					(CirCastExpression) pcontext, store_type, store_key, 
					value_class, context, orig_value, muta_param, outputs);
		}
		else if(pcontext instanceof CirWaitExpression) {
			this.propagate_on_wait_expression(execution, expression, 
					(CirWaitExpression) pcontext, store_type, store_key, 
					value_class, context, orig_value, muta_param, outputs);
		}
		else if(pcontext instanceof CirInitializerBody) {
			CirInitializerBody ibody = (CirInitializerBody) pcontext;
			for(int k = 0; k < ibody.number_of_elements(); k++) {
				if(ibody.get_element(k) == expression) {
					this.propagate_on_initializer_body(execution, 
							expression, ibody, k, store_type, store_key, 
							value_class, context, orig_value, muta_param, outputs);
				}
			}
		}
		else if(pcontext instanceof CirComputeExpression) {
			COperator operator = ((CirComputeExpression) pcontext).get_operator();
			if(((CirComputeExpression) pcontext).number_of_operand() == 1) {
				this.propagate_on_compute_uvalue(execution, expression, 
						(CirComputeExpression) pcontext, store_type, store_key, 
						value_class, context, operator, orig_value, muta_param, outputs);
			}
			else if(((CirComputeExpression) pcontext).get_operand(0) == expression) {
				this.propagate_on_compute_lvalue(execution, expression, 
						(CirComputeExpression) pcontext, store_type, store_key, 
						value_class, context, operator, orig_value, muta_param, outputs);
			}
			else {
				this.propagate_on_compute_rvalue(execution, expression, 
						(CirComputeExpression) pcontext, store_type, store_key, 
						value_class, context, operator, orig_value, muta_param, outputs);
			}
		}
		else {
			/* no more propagation from expression to other parent */
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param pcontext
	 * @param store_type
	 * @param store_key
	 * @param value_class
	 * @param context
	 * @param orig_value
	 * @param muta_param
	 * @param outputs
	 * @throws Exception
	 */
	private void propagate_on_if_statement(CirExecution execution, 
			CirExpression expression, CirIfStatement pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(expression != pcontext.get_condition()) {
			throw new IllegalArgumentException("Invalid match at: " + expression);
		}
		else if(value_class == CirValueClass.set_expr) {
			/* 1. decide the mutation value to be set over the predicate */
			Boolean muta_value;
			if(muta_param instanceof SymbolConstant) {
				muta_value = ((SymbolConstant) muta_param).get_bool();
			}
			else if(muta_param == StateMutations.true_value
					|| muta_param == StateMutations.post_value
					|| muta_param == StateMutations.negt_value
					|| muta_param == StateMutations.nzro_value
					|| muta_param == StateMutations.nnul_value) {
				muta_value = Boolean.TRUE;
			}
			else if(muta_param == StateMutations.fals_value
					|| muta_param == StateMutations.zero_value
					|| muta_param == StateMutations.null_value) {
				muta_value = Boolean.FALSE;
			}
			else {
				muta_value = null;
			}
			
			/* 2. generate the flow error propagated from the predicate */
			if(muta_value != null) {
				CirExecutionFlow true_flow = null, fals_flow = null;
				execution = pcontext.execution_of();
				for(CirExecutionFlow flow : execution.get_ou_flows()) {
					if(flow.get_type() == CirExecutionFlowType.true_flow) {
						true_flow = flow;
					}
					else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
						fals_flow = flow;
					}
					else {
						continue;
					}
				}
				if(muta_value.booleanValue()) {
					outputs.add(CirAbstractState.set_flow(fals_flow, true_flow));
				}
				else {
					outputs.add(CirAbstractState.set_flow(true_flow, fals_flow));
				}
			}
		}
		else if(value_class == CirValueClass.inc_expr) { /* no error to flow */ }
		else if(value_class == CirValueClass.xor_expr) { /* no error to flow */ }
		else {
			throw new IllegalArgumentException("Unsupport-class " + value_class);
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param pcontext
	 * @param store_type
	 * @param store_key
	 * @param value_class
	 * @param context
	 * @param orig_value
	 * @param muta_param
	 * @param outputs
	 * @throws Exception
	 */
	private void propagate_on_case_statement(CirExecution execution, 
			CirExpression expression, CirCaseStatement pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(expression != pcontext.get_condition()) {
			throw new IllegalArgumentException("Invalid match at: " + expression);
		}
		else if(value_class == CirValueClass.set_expr) {
			/* 1. decide the mutation value to be set over the predicate */
			Boolean muta_value;
			if(muta_param instanceof SymbolConstant) {
				muta_value = ((SymbolConstant) muta_param).get_bool();
			}
			else if(muta_param == StateMutations.true_value
					|| muta_param == StateMutations.post_value
					|| muta_param == StateMutations.negt_value
					|| muta_param == StateMutations.nzro_value
					|| muta_param == StateMutations.nnul_value) {
				muta_value = Boolean.TRUE;
			}
			else if(muta_param == StateMutations.fals_value
					|| muta_param == StateMutations.zero_value
					|| muta_param == StateMutations.null_value) {
				muta_value = Boolean.FALSE;
			}
			else {
				muta_value = null;
			}
			
			/* 2. generate the flow error propagated from the predicate */
			if(muta_value != null) {
				CirExecutionFlow true_flow = null, fals_flow = null;
				execution = pcontext.execution_of();
				for(CirExecutionFlow flow : execution.get_ou_flows()) {
					if(flow.get_type() == CirExecutionFlowType.true_flow) {
						true_flow = flow;
					}
					else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
						fals_flow = flow;
					}
					else {
						continue;
					}
				}
				if(muta_value.booleanValue()) {
					outputs.add(CirAbstractState.set_flow(fals_flow, true_flow));
				}
				else {
					outputs.add(CirAbstractState.set_flow(true_flow, fals_flow));
				}
			}
		}
		else if(value_class == CirValueClass.inc_expr) { /* no error to flow */ }
		else if(value_class == CirValueClass.xor_expr) { /* no error to flow */ }
		else {
			throw new IllegalArgumentException("Unsupport-class " + value_class);
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param pcontext
	 * @param store_type
	 * @param store_key
	 * @param value_class
	 * @param context
	 * @param orig_value
	 * @param muta_param
	 * @param outputs
	 * @throws Exception
	 */
	private void propagate_on_assign_lvalue(CirExecution execution, 
			CirExpression expression, CirAssignStatement pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement more detailed information here...
	}
	/**
	 * @param execution
	 * @param expression
	 * @param pcontext
	 * @param store_type
	 * @param store_key
	 * @param value_class
	 * @param context
	 * @param orig_value
	 * @param muta_param
	 * @param outputs
	 * @throws Exception
	 */
	private void propagate_on_assign_rvalue(CirExecution execution, 
			CirExpression expression, CirAssignStatement pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(expression != pcontext.get_rvalue()) {
			throw new IllegalArgumentException("Invalid match: " + expression);
		}
		else if(value_class == CirValueClass.set_expr) {
			if(store_type != CirStoreClass.vdef) {
				outputs.add(CirAbstractState.set_expr(pcontext.get_lvalue(), muta_param));
			}
		}
		else if(value_class == CirValueClass.inc_expr) {
			if(store_type != CirStoreClass.vdef) {
				outputs.add(CirAbstractState.inc_expr(pcontext.get_lvalue(), muta_param));
			}
		}
		else if(value_class == CirValueClass.xor_expr) {
			if(store_type != CirStoreClass.vdef) {
				outputs.add(CirAbstractState.xor_expr(pcontext.get_lvalue(), muta_param));
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport-class " + value_class);
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param pcontext
	 * @param store_type
	 * @param store_key
	 * @param value_class
	 * @param context
	 * @param orig_value
	 * @param muta_param
	 * @param outputs
	 * @throws Exception
	 */
	private void propagate_on_call_statement(CirExecution execution, 
			CirExpression expression, CirCallStatement pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(expression != pcontext.get_function()) {
			throw new IllegalArgumentException("Invalid match: " + expression);
		}
		else if(value_class != CirValueClass.set_expr) {
			CirArgumentList alist = pcontext.get_arguments();
			List<Object> arguments = new ArrayList<Object>();
			for(int k = 0; k < alist.number_of_arguments(); k++) {
				arguments.add(alist.get_argument(k));
			}
			SymbolExpression muta_value = SymbolFactory.call_expression(muta_param, arguments);
			CirExecution source = pcontext.execution_of();
			CirExecution target = source.get_graph().get_execution(source.get_id() + 1);
			CirWaitAssignStatement wait_statement = (CirWaitAssignStatement) target.get_statement();
			outputs.add(CirAbstractState.set_expr(wait_statement.get_rvalue(), muta_value));
		}
		else {
			throw new IllegalArgumentException("Unsupport-class " + value_class);
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param pcontext
	 * @param k
	 * @param store_type
	 * @param store_key
	 * @param value_class
	 * @param context
	 * @param orig_value
	 * @param muta_param
	 * @param outputs
	 * @throws Exception
	 */
	private void propagate_on_argument_list(CirExecution execution, 
			CirExpression expression, CirArgumentList pcontext, int k,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(value_class == CirValueClass.set_expr) {
			List<Object> arguments = new ArrayList<Object>();
			for(int i = 0; i < pcontext.number_of_arguments(); i++) {
				if(i != k) {
					arguments.add(pcontext.get_argument(i));
				}
				else {
					arguments.add(muta_param);
				}
			}
			CirCallStatement statement = (CirCallStatement) pcontext.get_parent();
			CirExpression function = statement.get_function();
			SymbolExpression muta_value = SymbolFactory.call_expression(function, arguments);
			
			CirExecution call = statement.execution_of();
			CirExecution wait = call.get_graph().get_execution(call.get_id() + 1);
			CirWaitAssignStatement wstmt = (CirWaitAssignStatement) wait.get_statement();
			outputs.add(CirAbstractState.set_expr(wstmt.get_rvalue(), muta_value));
		}
		else if(value_class == CirValueClass.inc_expr) { }
		else if(value_class == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupport-class " + value_class);
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param pcontext
	 * @param k
	 * @param store_type
	 * @param store_key
	 * @param value_class
	 * @param context
	 * @param orig_value
	 * @param muta_param
	 * @param outputs
	 * @throws Exception
	 */
	private void propagate_on_initializer_body(CirExecution execution, 
			CirExpression expression, CirInitializerBody pcontext, int k,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(value_class == CirValueClass.set_expr) {
			List<Object> elements = new ArrayList<Object>();
			for(int i = 0; i < pcontext.number_of_elements(); i++) {
				if(i == k) {
					elements.add(muta_param);
				}
				else {
					elements.add(pcontext.get_element(i));
				}
			}
			SymbolExpression muta_value = SymbolFactory.initializer_list(elements);
			outputs.add(CirAbstractState.set_expr(pcontext, muta_value));
		}
		else if(value_class == CirValueClass.inc_expr) { }
		else if(value_class == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupport-class " + value_class);
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param pcontext
	 * @param store_type
	 * @param store_key
	 * @param value_class
	 * @param context
	 * @param orig_value
	 * @param muta_param
	 * @param outputs
	 * @throws Exception
	 */
	private void propagate_on_defer_expression(CirExecution execution, 
			CirExpression expression, CirDeferExpression pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(expression != pcontext.get_address()) {
			throw new IllegalArgumentException("Not matched: " + expression);
		}
		else if(value_class == CirValueClass.set_expr) {
			if(store_type != CirStoreClass.vdef) {
				SymbolExpression muta_value = SymbolFactory.dereference(muta_param);
				CirExpression orig_expression = pcontext;
				outputs.add(CirAbstractState.set_expr(orig_expression, muta_value));
			}
		}
		else if(value_class == CirValueClass.inc_expr) { /* ignore special */ }
		else {
			throw new IllegalArgumentException("Unsupport: " + value_class);
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param pcontext
	 * @param store_type
	 * @param store_key
	 * @param value_class
	 * @param context
	 * @param orig_value
	 * @param muta_param
	 * @param outputs
	 * @throws Exception
	 */
	private void propagate_on_field_expression(CirExecution execution, 
			CirExpression expression, CirFieldExpression pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(expression != pcontext.get_body()) {
			throw new IllegalArgumentException("Not matched: " + expression);
		}
		else if(value_class == CirValueClass.set_expr) {
			if(store_type != CirStoreClass.vdef) {
				String field = pcontext.get_field().get_name();
				SymbolExpression muta_value = 
						SymbolFactory.field_expression(muta_param, field);
				CirExpression orig_expression = pcontext;
				outputs.add(CirAbstractState.set_expr(orig_expression, muta_value));
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + value_class);
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param pcontext
	 * @param store_type
	 * @param store_key
	 * @param value_class
	 * @param context
	 * @param orig_value
	 * @param muta_param
	 * @param outputs
	 * @throws Exception
	 */
	private void propagate_on_address_expression(CirExecution execution, 
			CirExpression expression, CirAddressExpression pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(expression != pcontext.get_operand()) {
			throw new IllegalArgumentException("Not matched: " + expression);
		}
		else if(value_class == CirValueClass.set_expr) {
			if(store_type != CirStoreClass.vdef) {
				SymbolExpression muta_value = SymbolFactory.address_of(muta_param);
				CirExpression orig_expression = pcontext;
				outputs.add(CirAbstractState.set_expr(orig_expression, muta_value));
			}
		}
		else if(value_class == CirValueClass.inc_expr) { }
		else if(value_class == CirValueClass.xor_expr) { } 
		else {
			throw new IllegalArgumentException("Unsupport: " + value_class);
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param pcontext
	 * @param store_type
	 * @param store_key
	 * @param value_class
	 * @param context
	 * @param orig_value
	 * @param muta_param
	 * @param outputs
	 * @throws Exception
	 */
	private void propagate_on_cast_expression(CirExecution execution, 
			CirExpression expression, CirCastExpression pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(pcontext.get_operand() != expression) {
			throw new IllegalArgumentException("Not matched: " + expression);
		}
		else if(value_class == CirValueClass.set_expr) {
			if(store_type != CirStoreClass.vdef) {
				CirExpression orig_expression = pcontext;
				SymbolExpression muta_value = SymbolFactory.cast_expression(
									orig_expression.get_data_type(), muta_param);
				outputs.add(CirAbstractState.set_expr(orig_expression, muta_value));
			}
		}
		else if(value_class == CirValueClass.inc_expr) { }
		else if(value_class == CirValueClass.xor_expr) { } 
		else {
			throw new IllegalArgumentException("Unsupport: " + value_class);
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param pcontext
	 * @param store_type
	 * @param store_key
	 * @param value_class
	 * @param context
	 * @param orig_value
	 * @param muta_param
	 * @param outputs
	 * @throws Exception
	 */
	private void propagate_on_wait_expression(CirExecution execution, 
			CirExpression expression, CirWaitExpression pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(pcontext.get_function() != expression) {
			throw new IllegalArgumentException("Not matched: " + expression);
		}
		else if(value_class == CirValueClass.set_expr) {
			if(store_type != CirStoreClass.vdef) {
				CirExpression orig_expression = pcontext;
				CirExecution wait = orig_expression.execution_of();
				CirExecution call = wait.get_graph().get_execution(wait.get_id() - 1);
				CirCallStatement call_stmt = (CirCallStatement) call.get_statement();
				CirArgumentList alist = call_stmt.get_arguments();
				List<Object> arguments = new ArrayList<Object>();
				for(int k = 0; k < alist.number_of_arguments(); k++) {
					arguments.add(alist.get_argument(k));
				}
				SymbolExpression muta_value = SymbolFactory.call_expression(muta_param, arguments);
				outputs.add(CirAbstractState.set_expr(orig_expression, muta_value));
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + value_class);
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param pcontext
	 * @param store_type
	 * @param store_key
	 * @param value_class
	 * @param context
	 * @param operator
	 * @param orig_value
	 * @param muta_param
	 * @param outputs
	 * @throws Exception
	 */
	private void propagate_on_compute_uvalue(CirExecution execution, 
			CirExpression expression, CirComputeExpression pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context, COperator operator,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(expression != pcontext.get_operand(0)) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else {
			switch(operator) {
			case positive:	
			{
				this.propagate_on_arith_pos(execution, expression, 
						pcontext, store_type, store_key, value_class, 
						context, orig_value, muta_param, outputs);
				break;
			}
			case negative:	
			{
				this.propagate_on_arith_neg(execution, expression, 
						pcontext, store_type, store_key, value_class, 
						context, orig_value, muta_param, outputs);
				break;
			}
			case bit_not:	
			{
				this.propagate_on_bitws_rsv(execution, expression, 
						pcontext, store_type, store_key, value_class, 
						context, orig_value, muta_param, outputs);
				break;
			}
			case logic_not:	
			{
				this.propagate_on_logic_not(execution, expression, 
						pcontext, store_type, store_key, value_class, 
						context, orig_value, muta_param, outputs);
				break;
			}
			default:	throw new IllegalArgumentException("unsupport: " + operator);
			}
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param pcontext
	 * @param store_type
	 * @param store_key
	 * @param value_class
	 * @param context
	 * @param operator
	 * @param orig_value
	 * @param muta_param
	 * @param outputs
	 * @throws Exception
	 */
	private void propagate_on_compute_lvalue(CirExecution execution, 
			CirExpression expression, CirComputeExpression pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context, COperator operator,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(expression != pcontext.get_operand(0)) {
			throw new IllegalArgumentException("Unmatched: " + pcontext);
		}
		else {
			CirComputeExpression pexpression = pcontext;
			CirExpression loperand = pexpression.get_operand(0);
			CirExpression roperand = pexpression.get_operand(1);
			switch(operator) {
			case arith_add:		
			{
				this.propagate_on_arith_ladd(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case arith_sub:
			{
				this.propagate_on_arith_lsub(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case arith_mul:
			{
				this.propagate_on_arith_lmul(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case arith_div:
			{
				this.propagate_on_arith_ldiv(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case arith_mod:
			{
				this.propagate_on_arith_lmod(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case bit_and:
			{
				this.propagate_on_bitws_land(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case bit_or:
			{
				this.propagate_on_bitws_lior(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case bit_xor:
			{
				this.propagate_on_bitws_lxor(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case left_shift:
			{
				this.propagate_on_bitws_llsh(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case righ_shift:
			{
				this.propagate_on_bitws_lrsh(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case logic_and:
			{
				this.propagate_on_logic_land(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case logic_or:
			{
				this.propagate_on_logic_lior(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case greater_tn:
			{
				this.propagate_on_lgreater_tn(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case greater_eq:
			{
				this.propagate_on_lgreater_eq(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case smaller_tn:
			{
				this.propagate_on_lsmaller_tn(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case smaller_eq:
			{
				this.propagate_on_lsmaller_eq(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case equal_with:
			{
				this.propagate_on_lequal_with(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case not_equals:
			{
				this.propagate_on_lnot_equals(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			default:	throw new IllegalArgumentException("Unsupport: " + operator);
			}
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param pcontext
	 * @param store_type
	 * @param store_key
	 * @param value_class
	 * @param context
	 * @param operator
	 * @param orig_value
	 * @param muta_param
	 * @param outputs
	 * @throws Exception
	 */
	private void propagate_on_compute_rvalue(CirExecution execution, 
			CirExpression expression, CirComputeExpression pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context, COperator operator,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(expression != pcontext.get_operand(1)) {
			throw new IllegalArgumentException("Unmatched: " + pcontext);
		}
		else {
			CirComputeExpression pexpression = pcontext;
			CirExpression loperand = pexpression.get_operand(0);
			CirExpression roperand = pexpression.get_operand(1);
			switch(operator) {
			case arith_add:		
			{
				this.propagate_on_arith_radd(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case arith_sub:
			{
				this.propagate_on_arith_rsub(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case arith_mul:
			{
				this.propagate_on_arith_rmul(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case arith_div:
			{
				this.propagate_on_arith_rdiv(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case arith_mod:
			{
				this.propagate_on_arith_rmod(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case bit_and:
			{
				this.propagate_on_bitws_rand(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case bit_or:
			{
				this.propagate_on_bitws_rior(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case bit_xor:
			{
				this.propagate_on_bitws_rxor(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case left_shift:
			{
				this.propagate_on_bitws_rlsh(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case righ_shift:
			{
				this.propagate_on_bitws_rrsh(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case logic_and:
			{
				this.propagate_on_logic_rand(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case logic_or:
			{
				this.propagate_on_logic_rior(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case greater_tn:
			{
				this.propagate_on_rgreater_tn(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case greater_eq:
			{
				this.propagate_on_rgreater_eq(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case smaller_tn:
			{
				this.propagate_on_rsmaller_tn(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case smaller_eq:
			{
				this.propagate_on_rsmaller_eq(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case equal_with:
			{
				this.propagate_on_requal_with(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			case not_equals:
			{
				this.propagate_on_rnot_equals(execution, pexpression, loperand, roperand, 
						value_class, store_type, store_key, orig_value, muta_param, outputs); 
				break;
			}
			default:	throw new IllegalArgumentException("Unsupport: " + operator);
			}
		}
	}
	
	/* unary expression */
	private void propagate_on_arith_pos(CirExecution execution, 
			CirExpression expression, CirComputeExpression pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(value_class == null) {
			throw new IllegalArgumentException("Invalid class as null");
		}
		else if(value_class == CirValueClass.set_expr) {
			if(store_type != CirStoreClass.vdef) {
				outputs.add(CirAbstractState.set_expr(pcontext, muta_param));
			}
		}
		else if(value_class == CirValueClass.inc_expr) {
			if(store_type != CirStoreClass.vdef) {
				outputs.add(CirAbstractState.inc_expr(pcontext, muta_param));
			}
		}
		else if(value_class == CirValueClass.xor_expr) {
			if(store_type != CirStoreClass.vdef) {
				outputs.add(CirAbstractState.xor_expr(pcontext, muta_param));
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + value_class);
		}
	}
	private void propagate_on_arith_neg(CirExecution execution, 
			CirExpression expression, CirComputeExpression pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(value_class == null) {
			throw new IllegalArgumentException("Invalid class as null");
		}
		else if(value_class == CirValueClass.set_expr) {
			if(store_type != CirStoreClass.vdef) {
				SymbolExpression muta_value = SymbolFactory.arith_neg(muta_param);
				outputs.add(CirAbstractState.set_expr(pcontext, muta_value));
			}
		}
		else if(value_class == CirValueClass.inc_expr) {
			if(store_type != CirStoreClass.vdef) {
				SymbolExpression difference = SymbolFactory.arith_neg(muta_param);
				outputs.add(CirAbstractState.inc_expr(pcontext, difference));
			}
		}
		else if(value_class == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupport: " + value_class);
		}
	}
	private void propagate_on_bitws_rsv(CirExecution execution, 
			CirExpression expression, CirComputeExpression pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(value_class == null) {
			throw new IllegalArgumentException("Invalid class as null");
		}
		else if(value_class == CirValueClass.set_expr) {
			if(store_type != CirStoreClass.vdef) {
				SymbolExpression muta_value = SymbolFactory.bitws_rsv(muta_param);
				outputs.add(CirAbstractState.set_expr(pcontext, muta_value));
			}
		}
		else if(value_class == CirValueClass.inc_expr) { }
		else if(value_class == CirValueClass.xor_expr) { 
			if(store_type != CirStoreClass.vdef) {
				SymbolExpression difference = SymbolFactory.bitws_rsv(muta_param);
				outputs.add(CirAbstractState.xor_expr(pcontext, difference));
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + value_class);
		}
	}
	private void propagate_on_logic_not(CirExecution execution, 
			CirExpression expression, CirComputeExpression pcontext,
			CirStoreClass store_type, SymbolExpression store_key, 
			CirValueClass value_class, Object context,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		if(value_class == null) {
			throw new IllegalArgumentException("Invalid class as null");
		}
		else if(value_class == CirValueClass.set_expr) {
			if(store_type != CirStoreClass.vdef) {
				SymbolExpression muta_value = SymbolFactory.sym_condition(muta_param, false);
				outputs.add(CirAbstractState.set_expr(pcontext, muta_value));
			}
		}
		else if(value_class == CirValueClass.inc_expr) { }
		else if(value_class == CirValueClass.xor_expr) { }
		else {
			throw new IllegalArgumentException("Unsupport: " + value_class);
		}
	}
	
	/* binary loperand */
	private void propagate_on_arith_ladd(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_arith_lsub(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_arith_lmul(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_arith_ldiv(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_arith_lmod(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_bitws_land(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_bitws_lior(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_bitws_lxor(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_bitws_llsh(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_bitws_lrsh(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_logic_land(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_logic_lior(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_lgreater_tn(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_lgreater_eq(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_lsmaller_tn(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_lsmaller_eq(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_lequal_with(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_lnot_equals(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	
	/* binary loperand */
	private void propagate_on_arith_radd(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_arith_rsub(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_arith_rmul(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_arith_rdiv(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_arith_rmod(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_bitws_rand(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_bitws_rior(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_bitws_rxor(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_bitws_rlsh(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_bitws_rrsh(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_logic_rand(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_logic_rior(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_rgreater_tn(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_rgreater_eq(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_rsmaller_tn(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_rsmaller_eq(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_requal_with(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	private void propagate_on_rnot_equals(CirExecution execution,
			CirExpression pexpression, CirExpression loperand,
			CirExpression roperand, CirValueClass value_class,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_param,
			Collection<CirAbstractState> outputs) throws Exception {
		// TODO implement this method...
	}
	
}
