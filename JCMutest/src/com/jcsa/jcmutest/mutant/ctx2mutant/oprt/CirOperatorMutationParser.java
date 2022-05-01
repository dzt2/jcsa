package com.jcsa.jcmutest.mutant.ctx2mutant.oprt;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutations;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstAbstErrorState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstBlockErrorState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstConditionState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstConstraintState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstContextStates;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	It implements the operator-based mutation class.
 * 	
 * 	@author yukimula
 *
 */
public abstract class CirOperatorMutationParser {
	
	/* attributes */
	private	ContextMutation			output;
	private	AstBinaryExpression		expression;
	private	boolean					weak_strong;
	protected	CirOperatorMutationParser() { }
	
	/* implementation methods */
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
	 * @return	(#=, =)
	 * @throws Exception
	 */
	protected abstract boolean to_assign() throws Exception;
	
	/* basic methodology */
	/**
	 * @param condition
	 * @return eva_cond(statement; condition, false)
	 * @throws Exception
	 */
	private	AstConstraintState 	get_constraint(Object condition) throws Exception {
		if(this.output == null) {
			throw new IllegalArgumentException("Invalid output: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return AstContextStates.eva_cond(this.output.get_statement(), condition, false);
		}
	}
	/**
	 * @return
	 * @throws Exception
	 */
	private	AstBlockErrorState 	trap_statement() throws Exception {
		if(this.output == null) {
			throw new IllegalArgumentException("Invalid output as: null");
		}
		else {
			return AstContextStates.trp_stmt(this.output.get_statement());
		}
	}
	/**
	 * @param muvalue
	 * @return set_expr|trp_stmt
	 * @throws Exception
	 */
	private	AstAbstErrorState  	set_expression(Object muvalue) throws Exception {
		if(this.output == null) {
			throw new IllegalArgumentException("Invalid output as: null");
		}
		else if(muvalue == null) {
			throw new IllegalArgumentException("Invalid muvalue: null");
		}
		else if(this.weak_strong) {
			return this.trap_statement();
		}
		else {
			SymbolExpression orig_value = SymbolFactory.sym_expression(expression);
			SymbolExpression muta_value = SymbolFactory.sym_expression(muvalue);
			if(ContextMutations.has_trap_value(muta_value)) {
				return this.trap_statement();
			}
			else if(SymbolFactory.is_bool(orig_value)) {
				return AstContextStates.set_expr(this.output.
						get_location(), orig_value, SymbolFactory.sym_condition(muta_value, true));
			}
			else {
				return AstContextStates.set_expr(this.output.get_location(), orig_value, muta_value);
			}
		}
	}
	/**
	 * It puts the constraint-error infection to the output
	 * @param constraint
	 * @param init_error
	 * @throws Exception
	 */
	private boolean	put_infection(AstConditionState constraint, AstAbstErrorState init_error) throws Exception {
		if(constraint == null) {
			throw new IllegalArgumentException("Invalid constraint: null");
		}
		else if(init_error == null) {
			throw new IllegalArgumentException("Invalid init_error: null");
		}
		else if(this.output == null) {
			throw new IllegalArgumentException("Invalid output as null");
		}
		else {
			this.output.put_infection_error(constraint, init_error); return true;
		}
	}
	/**
	 * @return the left-operand
	 * @throws Exception
	 */
	protected AstExpression		get_loperand() throws Exception { 
		if(this.expression == null)
			throw new IllegalArgumentException("Undefined expression");
		return this.expression.get_loperand(); 
	}
	/**
	 * @return the right-operand
	 * @throws Exception
	 */
	protected AstExpression		get_roperand() throws Exception { 
		if(this.expression == null)
			throw new IllegalArgumentException("Undefined expression");
		return this.expression.get_roperand(); 
	}
	/**
	 * @return the original expression
	 */
	protected AstExpression		get_expression() { return this.expression; }
	
	/* symbolic getters */
	/**
	 * @param operator	{+, -, *, /, %, &, |, ^, <<, >>, ..., ==, !=, :=, <-}
	 * @param loperand	the left operand
	 * @param roperand	the right operand
	 * @return			binary expression constructed using the input parameter
	 * @throws Exception
	 */
	protected SymbolExpression	sym_expression(COperator operator, Object loperand, Object roperand) throws Exception {
		if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else if(this.expression == null) {
			throw new IllegalArgumentException("No data type is established");
		}
		else {
			CType type = this.expression.get_value_type();
			switch(operator) {
			case arith_add:			return SymbolFactory.arith_add(type, loperand, roperand);
			case arith_sub:			return SymbolFactory.arith_sub(type, loperand, roperand);
			case arith_mul:			return SymbolFactory.arith_mul(type, loperand, roperand);
			case arith_div:			return SymbolFactory.arith_div(type, loperand, roperand);
			case arith_mod:			return SymbolFactory.arith_mod(type, loperand, roperand);
			case bit_and:			return SymbolFactory.bitws_and(type, loperand, roperand);
			case bit_or:			return SymbolFactory.bitws_ior(type, loperand, roperand);
			case bit_xor:			return SymbolFactory.bitws_xor(type, loperand, roperand);
			case left_shift:		return SymbolFactory.bitws_lsh(type, loperand, roperand);
			case righ_shift:		return SymbolFactory.bitws_rsh(type, loperand, roperand);
			case logic_and:			return SymbolFactory.logic_and(loperand, roperand);
			case logic_or:			return SymbolFactory.logic_ior(loperand, roperand);
			case greater_tn:		return SymbolFactory.greater_tn(loperand, roperand);
			case greater_eq:		return SymbolFactory.greater_eq(loperand, roperand);
			case smaller_tn:		return SymbolFactory.smaller_tn(loperand, roperand);
			case smaller_eq:		return SymbolFactory.smaller_eq(loperand, roperand);
			case equal_with:		return SymbolFactory.equal_with(loperand, roperand);
			case not_equals:		return SymbolFactory.not_equals(loperand, roperand);
			case assign:			return SymbolFactory.exp_assign(loperand, roperand);
			case arith_add_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.arith_add(type, loperand, roperand));
			case arith_sub_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.arith_sub(type, loperand, roperand));
			case arith_mul_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.arith_mul(type, loperand, roperand));
			case arith_div_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.arith_div(type, loperand, roperand));
			case arith_mod_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.arith_mod(type, loperand, roperand));
			case bit_and_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_and(type, loperand, roperand));
			case bit_or_assign:		return SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_ior(type, loperand, roperand));
			case bit_xor_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_xor(type, loperand, roperand));
			case left_shift_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_lsh(type, loperand, roperand));
			case righ_shift_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_rsh(type, loperand, roperand));
			default:				throw new IllegalArgumentException("Invalid mutation operator to be parsed for: " + operator);
			}
		}
	}
	/**
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression	sym_expression(Object expression) throws Exception { return SymbolFactory.sym_expression(expression); }
	/**
	 * @param condition
	 * @param value
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression	sym_condition(Object condition, boolean value) throws Exception { 
		return SymbolFactory.sym_condition(condition, value); 
	}
	/**
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression	sym_condition(Object condition) throws Exception { return this.sym_condition(condition, true); }
	/**
	 * @param conditions
	 * @return the conjunction of the entire input conditions
	 * @throws Exception
	 */
	protected SymbolExpression	sym_conjunctions(Collection<Object> conditions) throws Exception {
		if(conditions == null || conditions.isEmpty()) {
			return SymbolFactory.sym_constant(Boolean.TRUE);
		}
		else {
			SymbolExpression expression = null, sub_condition;
			for(Object condition : conditions) {
				sub_condition = this.sym_condition(condition);
				if(expression == null) {
					expression = sub_condition;
				}
				else {
					expression = this.sym_expression(COperator.logic_and, expression, sub_condition);
				}
			}
			return expression;
		}
	}
	/**
	 * @param conditions
	 * @return the disjunction of the entire input conditions
	 * @throws Exception
	 */
	protected SymbolExpression	sym_disjunctions(Collection<Object> conditions) throws Exception {
		if(conditions == null || conditions.isEmpty()) {
			return SymbolFactory.sym_constant(Boolean.TRUE);
		}
		else {
			SymbolExpression expression = null, sub_condition;
			for(Object condition : conditions) {
				sub_condition = this.sym_condition(condition);
				if(expression == null) {
					expression = sub_condition;
				}
				else {
					expression = this.sym_expression(COperator.logic_or, expression, sub_condition);
				}
			}
			return expression;
		}
	}
	/**
	 * @param muvalue
	 * @return loperand != roperand
	 * @throws Exception 
	 */
	protected SymbolExpression 	neq_expression(Object loperand, Object roperand) throws Exception {
		return this.sym_expression(COperator.not_equals, loperand, roperand);
	}
	/**
	 * @param muvalue
	 * @return this.expression != muvalue
	 * @throws Exception
	 */
	protected SymbolExpression	dif_condition(Object muvalue) throws Exception { 
		if(SymbolFactory.is_bool(this.expression.get_value_type())) {
			muvalue = this.sym_condition(muvalue);
		}
		return this.neq_expression(this.expression, muvalue); 
	}
	
	/* support methods */
	/**
	 * It puts {(condition) --> (set_expr(expression, muvalue))}
	 * @param condition
	 * @param muvalue
	 * @return
	 * @throws Exception
	 */
	protected boolean 	parse_by_condition_and_muvalue(Object condition, Object muvalue) throws Exception {
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
			return this.put_infection(this.get_constraint(condition), this.set_expression(muvalue));
		}
	}
	/**
	 * @param condition
	 * @param operator
	 * @return
	 * @throws Exception
	 */
	protected boolean	parse_by_condition_and_operator(Object condition, COperator operator) throws Exception {
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
	 * It puts {(expression != muvalue) --> set_expr(expression, muvalue)}
	 * @param muvalue
	 * @return
	 * @throws Exception
	 */
	protected boolean	parse_by_muvalue(Object muvalue) throws Exception {
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
	 * @param operator
	 * @return
	 * @throws Exception
	 */
	protected boolean	parse_by_operator(COperator operator) throws Exception {
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
	 * @return
	 * @throws Exception
	 */
	protected boolean 	report_unsupport_exception() throws Exception {
		throw new IllegalArgumentException("Unsupport operator");
	}
	/**
	 * @return it reports the mutation operator is equivalence
	 * @throws Exception
	 */
	protected boolean	report_equivalent_mutation() throws Exception {
		return this.put_infection(this.get_constraint(Boolean.FALSE), this.set_expression(expression));
	}
	
	/* parsing methods */
	/**
	 * It parses the operator-based mutation to context-based form
	 * @param output
	 * @throws Exception
	 */
	protected boolean	parse(ContextMutation output) throws Exception {
		if(output == null) {
			throw new IllegalArgumentException("Invalid output: null");
		}
		else if(output.get_location().get_ast_source() instanceof AstBinaryExpression) {
			/* initialize */
			this.output = output;
			this.expression = (AstBinaryExpression) output.get_location().get_ast_source();
			AstMutation mutation = output.get_mutant().get_mutation();
			if(mutation.get_operator() == MutaOperator.cmp_operator) {
				this.weak_strong = true;
			}
			else {
				this.weak_strong = false;
			}
			
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
		else {
			throw new IllegalArgumentException("Invalid: " + output.get_location());
		}
	}
	
}
