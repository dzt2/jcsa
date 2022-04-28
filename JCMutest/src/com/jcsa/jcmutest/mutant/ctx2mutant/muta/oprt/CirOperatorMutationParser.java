package com.jcsa.jcmutest.mutant.ctx2mutant.muta.oprt;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstContextMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.ContextMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.EvaCondMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.TrpStmtMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public abstract class CirOperatorMutationParser {
	
	/* attributes */
	private	AstMutation			mutation;
	private	AstContextMutation	outputs;
	private	AstBinaryExpression	expression;
	private	boolean				weak_strong;
	public CirOperatorMutationParser() { }
	
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
	 * It parses the operator-mutation to the given contextual outputs.
	 * @param program
	 * @param mutation
	 * @param outputs
	 * @return
	 * @throws Exception
	 */
	public boolean parse(AstMutation mutation, AstContextMutation outputs) throws Exception {
		if(mutation == null) {
			throw new IllegalArgumentException("Invalid mutation: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else {
			/* establishment */
			this.mutation = mutation; this.outputs = outputs;
			this.expression = (AstBinaryExpression) mutation.get_location();
			if(mutation.get_operator() == MutaOperator.cmp_operator) {
				this.weak_strong = true;
			}
			else {
				this.weak_strong = false;
			}
			
			/* parse-algorithm */
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
	}
	
	/* mutation-getters */
	private	EvaCondMutation	get_constraint(Object condition) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else if(this.outputs == null) {
			throw new IllegalArgumentException("Invalid output as: null");
		}
		else {
			AstCirNode statement = this.outputs.get_ast_location().statement_of();
			return ContextMutation.eva_cond(statement, condition, false);
		}
	}
	private	ContextMutation mut_expression(Object orig_value, Object muta_value) throws Exception {
		if(this.outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(this.weak_strong) { return this.trap_statement(); }
		else {
			return ContextMutation.set_expr(this.outputs.get_ast_location(), 
					SymbolFactory.sym_expression(orig_value), 
					SymbolFactory.sym_expression(muta_value));
		}
	}
	private	TrpStmtMutation	trap_statement() throws Exception {
		if(this.outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else {
			return ContextMutation.trp_stmt(this.outputs.get_ast_location().statement_of());
		}
	}
	/**
	 * It puts the infection-error pair to the outputs list
	 * @param constraint
	 * @param init_error
	 * @throws Exception
	 */
	protected boolean put_infection(ContextMutation constraint, ContextMutation init_error) throws Exception {
		if(constraint == null || !constraint.is_conditional()) {
			throw new IllegalArgumentException("Invalid: " + constraint);
		}
		else if(init_error == null || !init_error.is_abst_error()) {
			throw new IllegalArgumentException("Invalid: " + init_error);
		}
		else if(this.outputs == null) {
			throw new IllegalArgumentException("Invalid: " + outputs);
		}
		else { this.outputs.put_infection(constraint, init_error); return true; }
	}
	/**
	 * @return report that the mutation operator is not supported in current location
	 * @throws Exception
	 */
	protected boolean unsupport_exception() throws Exception {
		throw new UnsupportedOperationException("Unsupport: " + this.mutation.get_operator());
	}
	/**
	 * @return it reports the equivalent mutation
	 * @throws Exception
	 */
	protected boolean report_equivalences() throws Exception {
		return this.put_infection(this.get_constraint(Boolean.FALSE), 
				this.mut_expression(this.expression, this.expression));
	}
	
	/* symbolic-expression */
	protected AstExpression get_loperand() throws Exception { 
		if(this.expression == null) {
			throw new IllegalArgumentException("Invalid expression");
		}
		else {
			return this.expression.get_loperand();
		}
	}
	protected AstExpression get_roperand() throws Exception { 
		if(this.expression == null) {
			throw new IllegalArgumentException("Invalid expression");
		}
		else {
			return this.expression.get_roperand();
		}
	}
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
		CType type = this.expression.get_value_type();
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
		default: 			throw new IllegalArgumentException("Invalid: " + operator);
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
	 * @param muta_value
	 * @return expression != muta_value
	 * @throws Exception
	 */
	protected SymbolExpression dif_condition(Object muta_value) throws Exception {
		if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: " + muta_value);
		}
		else if(this.expression == null) {
			throw new IllegalArgumentException("Not established");
		}
		else {
			if(SymbolFactory.is_bool(this.expression.get_value_type())) {
				muta_value = this.sym_condition(muta_value, true);
			}
			else {
				muta_value = SymbolFactory.sym_expression(muta_value);
			}
			return this.sym_expression(COperator.not_equals, this.expression, muta_value);
		}
	}
	/**
	 * @param operator
	 * @return
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
			return this.dif_condition(this.sym_expression(
					operator, this.get_loperand(), this.get_roperand()));
		}
	}
	
	/* generation methods */
	/**
	 * @param condition
	 * @param muta_value
	 * @return condition --> set_expr(expression, muta_value)
	 * @throws Exception
	 */
	protected boolean parse_by_condition_and_muvalue(Object condition, Object muta_value) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid conditions: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(this.expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			EvaCondMutation constraint = this.get_constraint(condition);
			ContextMutation init_error = this.mut_expression(this.expression, muta_value);
			return this.put_infection(constraint, init_error);
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
					operator, this.get_loperand(), this.get_roperand());
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
					operator, this.get_loperand(), this.get_roperand());
			return this.parse_by_muvalue(muvalue);
		}
	}
	/**
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	protected boolean parse_by_condition_and_trapping(Object condition) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else if(this.expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			return this.put_infection(this.get_constraint(condition), this.trap_statement());
		}
	}
	
}
