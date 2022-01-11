package com.jcsa.jcmutest.mutant.sta2mutant.muta;

import java.util.Collection;
import java.util.Map;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirConditionState;
import com.jcsa.jcparse.lang.ctype.CType;
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
	/**
	 * @return	(#=, =)
	 * @throws Exception
	 */
	protected abstract boolean to_assign() throws Exception;
	/**
	 * @return (#, +)
	 * @throws Exception
	 */
	protected abstract boolean arith_add() throws Exception;
	/**
	 * @return (#, -)
	 * @throws Exception
	 */
	protected abstract boolean arith_sub() throws Exception;
	/**
	 * @return (#, *)
	 * @throws Exception
	 */
	protected abstract boolean arith_mul() throws Exception;
	/**
	 * @return (#, /)
	 * @throws Exception
	 */
	protected abstract boolean arith_div() throws Exception;
	/**
	 * @return (#, %)
	 * @throws Exception
	 */
	protected abstract boolean arith_mod() throws Exception;
	/**
	 * @return (#, &)
	 * @throws Exception
	 */
	protected abstract boolean bitws_and() throws Exception;
	/**
	 * @return (#, |)
	 * @throws Exception
	 */
	protected abstract boolean bitws_ior() throws Exception;
	/**
	 * @return (#, ^)
	 * @throws Exception
	 */
	protected abstract boolean bitws_xor() throws Exception;
	/**
	 * @return (#, <<)
	 * @throws Exception
	 */
	protected abstract boolean bitws_lsh() throws Exception;
	/**
	 * @return (#, >>)
	 * @throws Exception
	 */
	protected abstract boolean bitws_rsh() throws Exception;
	/**
	 * @return (#, &&)
	 * @throws Exception
	 */
	protected abstract boolean logic_and() throws Exception;
	/**
	 * @return (#, ||)
	 * @throws Exception
	 */
	protected abstract boolean logic_ior() throws Exception;
	/**
	 * @return (#, >)
	 * @throws Exception
	 */
	protected abstract boolean greater_tn()throws Exception;
	/**
	 * @return (#, >=)
	 * @throws Exception
	 */
	protected abstract boolean greater_eq()throws Exception;
	/**
	 * @return (#, <)
	 * @throws Exception
	 */
	protected abstract boolean smaller_tn()throws Exception;
	/**
	 * @return (#, <=)
	 * @throws Exception
	 */
	protected abstract boolean smaller_eq()throws Exception;
	/**
	 * @return (#, ==)
	 * @throws Exception
	 */
	protected abstract boolean equal_with()throws Exception;
	/**
	 * @return (#, !=)
	 * @throws Exception
	 */
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
	
	/* data state constructs */
	/**
	 * @param condition
	 * @return	[stmt:execution.statement] <== eva_cond(true, condition)
	 * @throws Exception
	 */
	private CirConditionState get_constraint(Object condition) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else if(this.execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return CirAbstractState.eva_cond(this.execution, condition, true);
		}
	}
	/**
	 * @param muta_value
	 * @return weak to trap or strong to set_expr
	 * @throws Exception
	 */
	private CirAbstErrorState mut_expression(Object muta_value) throws Exception {
		if(this.expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(this.weak_or_strong) {
			return this.trap_statement();	/* trapped for weak mutation */
		}
		else {
			return CirAbstractState.set_expr(this.expression, muta_value);
		}
	}
	/**
	 * @return
	 * @throws Exception
	 */
	private CirAbstErrorState trap_statement() throws Exception {
		if(this.execution == null) {
			throw new IllegalArgumentException("Not established at");
		}
		else {
			return CirAbstractState.set_trap(this.execution);
		}
	}
	
	/* basic data operations */
	/**
	 * @param constraint
	 * @param init_error
	 * @return it updates the infection maps from error to condition
	 * @throws Exception
	 */
	protected boolean put_state_infection(CirConditionState constraint,
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
		throw new UnsupportedOperationException("Unsupport: " + this.mutation);
	}
	/**
	 * @return report the mutation as equivalence due to the analysis process
	 * @throws Exception
	 */
	protected boolean report_equivalences() throws Exception {
		CirConditionState constraint = this.get_constraint(Boolean.FALSE);
		CirAbstErrorState init_error = this.mut_expression(this.expression);
		return this.put_state_infection(constraint, init_error);
	}
	
	/* symbolic construction */
	/**
	 * @param operand
	 * @return it transforms the operand to symbolic expression
	 * @throws Exception
	 */
	protected SymbolExpression sym_expression(Object operand) throws Exception {
		return SymbolFactory.sym_expression(operand);
	}
	/**
	 * @param operator	{+, -, ~, !}
	 * @param operand
	 * @return the symbolic unary expression w.r.t. the operand
	 * @throws Exception
	 */
	protected SymbolExpression sym_expression(COperator operator, Object operand) throws Exception {
		if(operator == null) {
			throw new IllegalArgumentException("Invalid operator");
		}
		else if(operand == null) {
			throw new IllegalArgumentException("Invalid operand");
		}
		else {
			switch(operator) {
			case positive:	return SymbolFactory.sym_expression(operand);
			case negative:	return SymbolFactory.arith_neg(operand);
			case bit_not:	return SymbolFactory.bitws_rsv(operand);
			case logic_not:	return SymbolFactory.logic_not(operand);
			default:		throw new IllegalArgumentException(operator.toString());
			}
		}
	}
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
	 * @param condition
	 * @param value
	 * @return the symbolic condition of input as value
	 * @throws Exception
	 */
	protected SymbolExpression sym_condition(Object condition, boolean value) throws Exception {
		return SymbolFactory.sym_condition(condition, value);
	}
	/**
	 * @param condition
	 * @return the symbolic condition of input as true
	 * @throws Exception
	 */
	protected SymbolExpression sym_condition(Object condition) throws Exception {
		return SymbolFactory.sym_condition(condition, true);
	}
	/**
	 * @param expressions
	 * @return the conjunctions of multiple input expressions
	 * @throws Exception
	 */
	protected SymbolExpression sym_conjunction(Collection<Object> expressions) throws Exception {
		if(expressions == null || expressions.isEmpty()) {
			return SymbolFactory.sym_constant(Boolean.TRUE);
		}
		else {
			SymbolExpression conjunctions = null, condition;
			for(Object expression : expressions) {
				condition = this.sym_condition(expression);
				if(conjunctions == null) {
					conjunctions = condition;
				}
				else {
					conjunctions = SymbolFactory.
							logic_and(conjunctions, condition);
				}
			}
			return conjunctions;
		}
	}
	/**
	 * @param expressions
	 * @return the disjunctions of multiple input expressions
	 * @throws Exception
	 */
	protected SymbolExpression sym_disjunction(Collection<Object> expressions) throws Exception {
		if(expressions == null || expressions.isEmpty()) {
			return SymbolFactory.sym_constant(Boolean.TRUE);
		}
		else {
			SymbolExpression disjunctions = null, condition;
			for(Object expression : expressions) {
				condition = this.sym_condition(expression);
				if(disjunctions == null) {
					disjunctions = condition;
				}
				else {
					disjunctions = SymbolFactory.
							logic_ior(disjunctions, condition);
				}
			}
			return disjunctions;
		}
	}
	
	/**
	 * @param operator
	 * @return {orig_value != loperand op roperand}
	 * @throws Exception
	 */
	protected SymbolExpression dif_condition(COperator operator) throws Exception {
		if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		else if(this.expression == null) {
			throw new IllegalArgumentException("Not established");
		}
		else {
			SymbolExpression muvalue = this.sym_expression(
					operator, this.loperand, this.roperand);
			return this.dif_condition(muvalue);
		}
	}
	/**
	 * @param operator
	 * @return {orig_value != muvalue}
	 * @throws Exception
	 */
	protected SymbolExpression dif_condition(Object muvalue) throws Exception {
		if(muvalue == null) {
			throw new IllegalArgumentException("Invalid muvalue: " + muvalue);
		}
		else if(this.expression == null) {
			throw new IllegalArgumentException("Not established");
		}
		else {
			return this.sym_expression(COperator.not_equals, this.expression, muvalue);
		}
	}
	
	/* parsing simplification */
	/**
	 * @param condition
	 * @param muvalue
	 * @return	[C]	{condition as true}
	 * 			[E]	{ovalue --> mvalue}
	 * @throws Exception
	 */
	protected boolean parse_by_condition_and_muvalue(Object condition, Object muvalue) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else if(muvalue == null) {
			throw new IllegalArgumentException("Invalid muvalue: null");
		}
		else if(this.expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			CirConditionState constraint = this.get_constraint(condition);
			CirAbstErrorState init_error = this.mut_expression(muvalue);
			return this.put_state_infection(constraint, init_error);
		}
	}
	/**
	 * @param muvalue
	 * @return	[C]	{ovalue != muvalue}
	 * 			[E]	{ovalue -> muvalue}
	 * @throws Exception
	 */
	protected boolean parse_by_muvalue(Object muvalue) throws Exception {
		if(muvalue == null) {
			throw new IllegalArgumentException("Invalid muvalue: null");
		}
		else if(this.expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			SymbolExpression condition = this.dif_condition(muvalue);
			return this.parse_by_condition_and_muvalue(condition, muvalue);
		}
	}
	/**
	 * @param condition
	 * @param operator
	 * @return	[C]	{x op y != x op' y}
	 * 			[E]	{x op y -> x op' y}
	 * @throws Exception
	 */
	protected boolean parse_by_condition_and_operator(Object condition, COperator operator) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else if(this.expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			SymbolExpression muvalue = this.sym_expression(
					operator, this.loperand, this.roperand);
			return this.parse_by_condition_and_muvalue(condition, muvalue);
		}
	}
	/**
	 * @param operator
	 * @return	[C]	{x op y != x op' y}
	 * 			[E]	{x op y -> x op' y}
	 * @throws Exception
	 */
	protected boolean parse_by_operator(COperator operator) throws Exception {
		if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else if(this.expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			SymbolExpression muvalue = this.sym_expression(
					operator, this.loperand, this.roperand);
			return this.parse_by_muvalue(muvalue);
		}
	}
	
}
