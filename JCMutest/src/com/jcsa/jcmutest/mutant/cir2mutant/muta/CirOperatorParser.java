package com.jcsa.jcmutest.mutant.cir2mutant.muta;

import java.util.Collection;
import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;
import com.jcsa.jcmutest.mutant.cir2mutant.base.SymConditions;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaOperator;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public abstract class CirOperatorParser {

	/** any boolean value **/
	public static final String AnyBoolean = "default#boolean";
	
	/* definitions */
	/** source mutation to cause state infections **/
	private AstMutation mutation;
	/** the statement where the mutation is reached **/
	private CirStatement statement;
	/** the expression being mutated with state error **/
	private CirExpression expression;
	/** the left-operand in the binary expression set **/
	protected CirExpression loperand;
	/** the right-operand in the binary expression set **/
	protected CirExpression roperand;
	/** true for cmp_operator; false for set_operator **/
	protected boolean compare_or_mutate;
	/** mapping from state errors to the constraints for killing mutation **/
	private Map<SymCondition, SymCondition> infections;
	
	/* constructor */
	public CirOperatorParser() { }
	
	/* parsing methods */
	/**
	 * generate the infection module w.r.t. the mutation.
	 * @param mutation
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	public boolean generate_infections(AstMutation mutation, 
			CirStatement statement, CirExpression expression, 
			CirExpression loperand, CirExpression roperand,
			Map<SymCondition, SymCondition> infection) throws Exception {
		/* declarations */
		this.mutation = mutation;
		this.statement = statement;
		this.expression = expression;
		this.loperand = loperand;
		this.roperand = roperand;
		if(mutation.get_operator() == MutaOperator.cmp_operator)
			this.compare_or_mutate = true;
		else
			this.compare_or_mutate = false;
		this.infections = infection;
		
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
	
	/* basic data operations */
	/**
	 * @param constraint
	 * @param init_error
	 * @return add the infection-pair of [constraint, init_error] into the module.
	 * @throws Exception
	 */
	protected boolean add_infection(SymCondition constraint, SymCondition init_error) throws Exception {
		this.infections.put(init_error, constraint); return true;
	}
	/**
	 * @param condition
	 * @return assert_on(this.statement, condition, true).
	 * @throws Exception
	 */
	protected SymCondition get_constraint(Object condition) throws Exception {
		return SymConditions.eva_expr(SymConditions.execution_of(this.statement), condition, true);
	}
	/**
	 * @return trp_stmt(this.statement)
	 * @throws Exception
	 */
	protected SymCondition trap_statement() throws Exception {
		return SymConditions.trp_exec(SymConditions.execution_of(this.statement));
	}
	
	/* exception handles */
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
	
	/* expression errors */
	/**
	 * @param muta_expression
	 * @return set_expr(this.expression, muta_expression)
	 * @throws Exception
	 */
	protected SymCondition set_expression(Object muta_expression) throws Exception {
		return SymConditions.mut_expr(expression, muta_expression);
	}
	/**
	 * @param operand
	 * @return add_expr(this.expression, +, operand)
	 * @throws Exception
	 */
	protected SymCondition add_expression(Object operand) throws Exception {
		return SymConditions.mut_expr(expression, SymbolFactory.arith_add(expression.get_data_type(), expression, operand));
	}
	/**
	 * @param operand
	 * @return add_expr(this.expression, -, operand)
	 * @throws Exception
	 */
	protected SymCondition sub_expression(Object operand) throws Exception {
		return SymConditions.mut_expr(expression, SymbolFactory.arith_sub(expression.get_data_type(), expression, operand));
	}
	/**
	 * @return uny_expr(this.expression, -)
	 * @throws Exception
	 */
	protected SymCondition neg_expression() throws Exception {
		return SymConditions.mut_expr(expression, SymbolFactory.arith_neg(expression));
	}
	/**
	 * @return uny_expr(this.expression, ~)
	 * @throws Exception
	 */
	protected SymCondition rsv_expression() throws Exception {
		return SymConditions.mut_expr(expression, SymbolFactory.bitws_rsv(expression));
	}
	/**
	 * @return uny_expr(this.expression, !)
	 * @throws Exception
	 */
	protected SymCondition not_expression() throws Exception {
		return SymConditions.mut_expr(expression, SymbolFactory.logic_not(expression));
	}
	/**
	 * @param operand
	 * @param operator
	 * @return ins_expr(orig_expr, operator, operand)
	 * @throws Exception
	 */
	protected SymCondition ins_expression(Object operand, COperator operator) throws Exception {
		return SymConditions.mut_expr(expression, this.sym_expression(operator, operand, expression));
	}
	
	/* symbolic operations */
	/**
	 * @param expression
	 * @param value
	 * @return symbolic condition as expression == value
	 * @throws Exception
	 */
	protected SymbolExpression sym_condition(Object expression, boolean value) throws Exception {
		SymbolExpression condition = SymbolFactory.sym_expression(expression);
		CType type = CTypeAnalyzer.get_value_type(condition.get_data_type());
		if(CTypeAnalyzer.is_boolean(type)) {
			if(value) { }
			else {
				condition = SymbolFactory.logic_not(condition);
			}
		}
		else if(CTypeAnalyzer.is_integer(type) 
				|| CTypeAnalyzer.is_real(type)
				|| CTypeAnalyzer.is_pointer(type)) {
			if(value) {
				condition = SymbolFactory.not_equals(condition, Integer.valueOf(0));
			}
			else {
				condition = SymbolFactory.equal_with(condition, Integer.valueOf(0));
			}
		}
		else {
			throw new IllegalArgumentException(type.generate_code());
		}
		return condition;
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
	
	/* composite descriptions */
	/**
	 * @param descriptions
	 * @return conjunction of the descriptions
	 * @throws Exception
	 */
	protected SymCondition conjunct(Collection<SymCondition> constraints) throws Exception {
		if(constraints.isEmpty())
			return SymConditions.eva_expr(SymConditions.execution_of(this.statement), Boolean.TRUE, true);
		else if(constraints.size() == 1)
			return constraints.iterator().next();
		else {
			SymbolExpression condition = null;
			for(SymCondition constraint : constraints) {
				if(condition == null)
					condition = constraint.get_parameter();
				else
					condition = SymbolFactory.logic_and(condition, constraint.get_parameter());
			}
			return SymConditions.eva_expr(SymConditions.execution_of(statement), condition, true);
		}
	}
	/**
	 * @param descriptions
	 * @return disjunction of the descriptions
	 * @throws Exception
	 */
	protected SymCondition disjunct(Collection<SymCondition> constraints) throws Exception {
		if(constraints.isEmpty())
			return SymConditions.eva_expr(SymConditions.execution_of(this.statement), Boolean.FALSE, true);
		else if(constraints.size() == 1)
			return constraints.iterator().next();
		else {
			SymbolExpression condition = null;
			for(SymCondition constraint : constraints) {
				if(condition == null)
					condition = constraint.get_parameter();
				else
					condition = SymbolFactory.logic_ior(condition, constraint.get_parameter());
			}
			return SymConditions.eva_expr(SymConditions.execution_of(statement), condition, true);
		}
	}
	
}
