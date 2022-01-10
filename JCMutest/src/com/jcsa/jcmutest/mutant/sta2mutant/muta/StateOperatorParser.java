package com.jcsa.jcmutest.mutant.sta2mutant.muta;

import java.util.Collection;
import java.util.Map;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirConditionState;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It implements the transformation from operator-mutation to state-mutation.
 * 
 * @author yukimula
 *
 */
public abstract class StateOperatorParser {
	
	/* attributes */
	/** any boolean value **/
	protected static final String AnyBoolean = "default#boolean";
	/** source mutation to cause state infections **/
	private AstMutation mutation;
	/** the execution where the mutation is reached **/
	private CirExecution execution;
	/** mapping from initial error to state infection condition **/
	private Map<CirAbstErrorState, CirConditionState> infections;
	/**
	 * Abstract Constructor
	 */
	public StateOperatorParser() { }
	
	/* operation */
	/** the expression being mutated with state error **/
	protected CirExpression expression;
	/** the left-operand in the binary expression set **/
	protected CirExpression loperand;
	/** the right-operand in the binary expression set **/
	protected CirExpression roperand;
	/** true to weak mutation false to strong mutation **/
	protected boolean weak_or_strong;
	
	/* implementation methods */
	protected abstract boolean to_assign() throws Exception;
	protected abstract boolean arith_add() throws Exception;
	protected abstract boolean arith_sub() throws Exception;
	protected abstract boolean arith_mul() throws Exception;
	protected abstract boolean arith_div() throws Exception;
	protected abstract boolean arith_mod() throws Exception;
	protected abstract boolean bitws_and() throws Exception;
	protected abstract boolean bitws_ior() throws Exception;
	protected abstract boolean bitws_xor() throws Exception;
	protected abstract boolean bitws_lsh() throws Exception;
	protected abstract boolean bitws_rsh() throws Exception;
	protected abstract boolean logic_and() throws Exception;
	protected abstract boolean logic_ior() throws Exception;
	protected abstract boolean greater_tn()throws Exception;
	protected abstract boolean greater_eq()throws Exception;
	protected abstract boolean smaller_tn()throws Exception;
	protected abstract boolean smaller_eq()throws Exception;
	protected abstract boolean equal_with()throws Exception;
	protected abstract boolean not_equals()throws Exception;
	/**
	 * It parses the operator-mutation to the specified operator in weak or 
	 * strong mutation testing context.
	 * 
	 * @param mutation		the syntactic mutation
	 * @param execution		the execution where the expression is used
	 * @param expression	the original expression to be mutated from
	 * @param loperand		the left operand in original expression
	 * @param roperand		the right operand in original expression
	 * @param infections	mapping from error to infection condition
	 * @return 				whether the parsing succeeds finally.
	 * @throws Exception
	 */
	protected boolean parse(AstMutation mutation, 
			CirExecution execution, CirExpression expression, 
			CirExpression loperand, CirExpression roperand,
			Map<CirAbstErrorState, CirConditionState> infections) throws Exception {
		/* establishment */
		this.mutation = mutation; 
		this.execution = execution;
		this.expression = expression;
		this.loperand = loperand;
		this.roperand = roperand;
		
		/* initialization */
		if(mutation.get_operator() == MutaOperator.cmp_operator) {
			this.weak_or_strong = true;
		}
		else {
			this.weak_or_strong = false;
		}
		this.infections = infections;
		this.infections.clear();
		
		/* operator */
		COperator operator = (COperator) mutation.get_parameter();
		switch(operator) {
		case assign:				return this.to_assign();
		case arith_add:				return this.arith_add();
		case arith_sub:				return this.arith_sub();
		case arith_mul:				return this.arith_mul();
		case arith_div:				return this.arith_div();
		case arith_mod:				return this.arith_mod();
		case bit_and:				return this.bitws_and();
		case bit_or:				return this.bitws_ior();
		case bit_xor:				return this.bitws_xor();
		case left_shift:			return this.bitws_lsh();
		case righ_shift:			return this.bitws_rsh();
		case logic_and:				return this.logic_and();
		case logic_or:				return this.logic_ior();
		case greater_tn:			return this.greater_tn();
		case greater_eq:			return this.greater_eq();
		case smaller_tn:			return this.smaller_tn();
		case smaller_eq:			return this.smaller_eq();
		case equal_with:			return this.equal_with();
		case not_equals:			return this.not_equals();
		case arith_add_assign:		return this.arith_add();
		case arith_sub_assign:		return this.arith_sub();
		case arith_mul_assign:		return this.arith_mul();
		case arith_div_assign:		return this.arith_div();
		case arith_mod_assign:		return this.arith_mod();
		case bit_and_assign:		return this.bitws_and();
		case bit_or_assign:			return this.bitws_ior();
		case bit_xor_assign:		return this.bitws_xor();
		case left_shift_assign:		return this.bitws_lsh();
		case righ_shift_assign:		return this.bitws_rsh();
		default:	throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	
	/* basic data operations */
	/**
	 * @param constraint
	 * @param init_error
	 * @return it updates the infection maps from error to condition
	 * @throws Exception
	 */
	protected boolean add_infection(CirConditionState constraint,
				CirAbstErrorState init_error) throws Exception {
		if(constraint == null) {
			throw new IllegalArgumentException("Invalid constraint");
		}
		else if(init_error == null) {
			throw new IllegalArgumentException("Invalid init_error");
		}
		else {
			this.infections.put(init_error, constraint); return true;
		}
	}
	/**
	 * @return report that the mutation operator is not supported in current location
	 * @throws Exception
	 */
	protected boolean unsupport_exception() throws Exception {
		throw new UnsupportedOperationException("Unsupport: " + this.expression.generate_code(true));
	}
	/**
	 * @return report the mutation as equivalence due to the analysis process
	 * @throws Exception
	 */
	protected boolean report_equivalence_mutation() throws Exception {
		throw new UnsupportedOperationException("Equivalent mutation: " + this.mutation);
	}
	
	/* data state generation */
	/**
	 * @param condition
	 * @return [stmt:statement] <== cov_cond(true, condition)
	 * @throws Exception
	 */
	protected CirConditionState get_constraint(Object condition) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition");
		}
		else {
			return CirAbstractState.eva_cond(this.execution, condition, true);
		}
	}
	/**
	 * @param muta_value
	 * @return [expr:expression] <== set_expr(orig_value, muta_value)
	 * @throws Exception
	 */
	protected CirAbstErrorState mut_expression(Object muta_value) throws Exception {
		if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(this.weak_or_strong) {
			return CirAbstractState.dif_expr(this.expression, muta_value);
		}
		else {
			return CirAbstractState.set_expr(this.expression, muta_value);
		}
	}
	/**
	 * @return
	 * @throws Exception
	 */
	protected CirAbstErrorState trp_statement() throws Exception {
		return CirAbstractState.set_trap(this.execution);
	}
	/**
	 * @param conditions
	 * @return
	 * @throws Exception
	 */
	protected CirConditionState	conjuncts(Collection<SymbolExpression> conditions) throws Exception {
		if(conditions.isEmpty()) {
			return CirAbstractState.cov_time(this.execution, 1);
		}
		else {
			SymbolExpression condition = null;
			for(SymbolExpression expression : conditions) {
				if(condition == null)
					condition = expression;
				else
					condition = SymbolFactory.logic_and(condition, expression);
			}
			return CirAbstractState.eva_cond(this.execution, condition, true);
		}
	}
	/**
	 * @param conditions
	 * @return
	 * @throws Exception
	 */
	protected CirConditionState	disjuncts(Collection<SymbolExpression> conditions) throws Exception {
		if(conditions.isEmpty()) {
			return CirAbstractState.cov_time(this.execution, 1);
		}
		else {
			SymbolExpression condition = null;
			for(SymbolExpression expression : conditions) {
				if(condition == null)
					condition = expression;
				else
					condition = SymbolFactory.logic_ior(condition, expression);
			}
			return CirAbstractState.eva_cond(this.execution, condition, true);
		}
	}
	
	/* symbolic expressions */
	/**
	 * @param operator {+, -, *, /, %, &, |, ^, <<, >>, &&, ||, <, <=, >, >=, ==, !=}
	 * @param loperand
	 * @param roperand
	 * @return the symbolic binary expression w.r.t. the loperand as well as roperand
	 * @throws Exception
	 */
	protected SymbolExpression sym_expression(COperator operator, Object loperand, Object roperand) throws Exception {
		CType type = this.expression.get_data_type();
		switch(operator) {
		case arith_add:		return SymbolFactory.arith_add(type, loperand, roperand);
		case arith_sub:		return SymbolFactory.arith_sub(type, loperand, roperand);
		case arith_mul:		return SymbolFactory.arith_mul(type, loperand, roperand);
		case arith_div:		return SymbolFactory.arith_div(type, loperand, roperand);
		case arith_mod:		return SymbolFactory.arith_mod(type, loperand, roperand);
		case bit_and:		return SymbolFactory.bitws_and(type, loperand, roperand);
		case bit_or:		return SymbolFactory.bitws_ior(type, loperand, roperand);
		case bit_xor:		return SymbolFactory.bitws_xor(type, loperand, roperand);
		case left_shift:	return SymbolFactory.bitws_lsh(type, loperand, roperand);
		case righ_shift:	return SymbolFactory.bitws_rsh(type, loperand, roperand);
		case logic_and:		return SymbolFactory.logic_and(loperand, roperand);
		case logic_or:		return SymbolFactory.logic_ior(loperand, roperand);
		case greater_tn:	return SymbolFactory.greater_tn(loperand, roperand);
		case greater_eq:	return SymbolFactory.greater_eq(loperand, roperand);
		case smaller_tn:	return SymbolFactory.smaller_tn(loperand, roperand);
		case smaller_eq:	return SymbolFactory.smaller_eq(loperand, roperand);
		case equal_with:	return SymbolFactory.equal_with(loperand, roperand);
		case not_equals:	return SymbolFactory.not_equals(loperand, roperand);
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
	}
	/**
	 * @param operator	{+, -, ~, !}
	 * @param operand
	 * @return the symbolic unary expression w.r.t. the operand
	 * @throws Exception
	 */
	protected SymbolExpression sym_expression(COperator operator, Object operand) throws Exception {
		switch(operator) {
		case positive:	return SymbolFactory.sym_expression(operand);
		case negative:	return SymbolFactory.arith_neg(operand);
		case bit_not:	return SymbolFactory.bitws_rsv(operand);
		case logic_not:	return SymbolFactory.logic_not(operand);
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
	}
	/**
	 * @param operand
	 * @return operand > 1 or operand < 0
	 * @throws Exception
	 */
	protected SymbolExpression non_bool_condition(Object operand) throws Exception {
		SymbolExpression expression = SymbolFactory.sym_expression(operand);
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(CTypeAnalyzer.is_boolean(type)) {
			return SymbolFactory.sym_constant(Boolean.FALSE);
		}
		else if(CTypeAnalyzer.is_number(type)) {
			return SymbolFactory.logic_ior(
					SymbolFactory.smaller_tn(expression, 0), 
					SymbolFactory.greater_tn(expression, 1));
		}
		else if(CTypeAnalyzer.is_pointer(type)) {
			return SymbolFactory.not_equals(expression, 0);
		}
		else {
			throw new IllegalArgumentException("Invalid type: " + type);
		}
	}
	
}
