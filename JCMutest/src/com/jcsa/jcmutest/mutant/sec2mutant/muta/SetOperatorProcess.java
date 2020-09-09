package com.jcsa.jcmutest.mutant.sec2mutant.muta;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaOperator;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;

public abstract class SetOperatorProcess {
	
	protected CirStatement statement;
	protected CirExpression expression;
	protected CirExpression loperand;
	protected CirExpression roperand;
	protected COperator operator;
	protected boolean compare;
	protected SecInfection infection;
	
	public void process(CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand, 
			AstMutation mutation, SecInfection infection) throws Exception {
		this.statement = statement;
		this.expression = expression;
		this.loperand = loperand;
		this.roperand = roperand;
		this.operator = (COperator) mutation.get_parameter();
		this.compare = (mutation.get_operator() == MutaOperator.cmp_operator);
		this.infection = infection;
		
		switch(this.operator) {
		case assign:				this.to_assign(); 	break;
		case arith_add:				this.arith_add();	break;
		case arith_sub:				this.arith_sub();	break;
		case arith_mul:				this.arith_mul();	break;
		case arith_div:				this.arith_div();	break;
		case arith_mod:				this.arith_mod();	break;
		case bit_and:				this.bitws_and(); 	break;
		case bit_or:				this.bitws_ior(); 	break;
		case bit_xor:				this.bitws_xor(); 	break;
		case left_shift:			this.bitws_lsh();	break;
		case righ_shift:			this.bitws_rsh();	break;
		case logic_and:				this.logic_and();	break;
		case logic_or:				this.logic_ior();	break;
		case greater_tn:			this.greater_tn();	break;
		case greater_eq:			this.greater_eq();	break;
		case smaller_tn:			this.smaller_tn();	break;
		case smaller_eq:			this.smaller_eq();	break;
		case not_equals:			this.not_equals();	break;
		case equal_with:			this.equal_with();	break;
		case arith_add_assign:		this.arith_add();	break;
		case arith_sub_assign:		this.arith_sub();	break;
		case arith_mul_assign:		this.arith_mul();	break;
		case arith_div_assign:		this.arith_div();	break;
		case arith_mod_assign:		this.arith_mod();	break;
		case bit_and_assign:		this.bitws_and();	break;
		case bit_or_assign:			this.bitws_ior();	break;
		case bit_xor_assign:		this.bitws_xor();	break;
		case left_shift_assign:		this.bitws_lsh();	break;
		case righ_shift_assign:		this.bitws_rsh();	break;
		default: throw new IllegalArgumentException("Invalid: " + this.operator);
		}
	}
	
	protected abstract void to_assign() throws Exception;
	protected abstract void arith_add() throws Exception;
	protected abstract void arith_sub() throws Exception;
	protected abstract void arith_mul() throws Exception;
	protected abstract void arith_div() throws Exception;
	protected abstract void arith_mod() throws Exception;
	protected abstract void bitws_and() throws Exception;
	protected abstract void bitws_ior() throws Exception;
	protected abstract void bitws_xor() throws Exception;
	protected abstract void bitws_lsh() throws Exception;
	protected abstract void bitws_rsh() throws Exception;
	protected abstract void logic_and() throws Exception;
	protected abstract void logic_ior() throws Exception;
	protected abstract void greater_tn() throws Exception;
	protected abstract void greater_eq() throws Exception;
	protected abstract void smaller_tn() throws Exception;
	protected abstract void smaller_eq() throws Exception;
	protected abstract void equal_with() throws Exception;
	protected abstract void not_equals() throws Exception;
	
	/* basic methods */
	/**
	 * add the infection-pair in the list of this.infection module
	 * @param constraint
	 * @param init_error
	 * @throws Exception
	 */
	protected void add_infection(SecDescription constraint, 
			SecDescription init_error) throws Exception {
		this.infection.add_infection_pair(constraint, init_error);;
	}
	/**
	 * throw an unsupported operation exception for incorrect operator
	 * @throws Exception
	 */
	protected void unsupport_operation() throws Exception {
		throw new UnsupportedOperationException(this.operator.toString());
	}
	/**
	 * @param condition
	 * @return condition as true
	 * @throws Exception
	 */
	protected SecConstraint get_constraint(Object condition) throws Exception {
		return SecFactory.assert_constraint(statement, condition, true);
	}
	protected SecDescription trap_statement() throws Exception {
		return SecFactory.trap_statement(this.statement);
	}
	protected SecDescription set_expression(SymExpression muta_expression) throws Exception {
		return SecFactory.set_expression(statement, expression, muta_expression);
	}
	protected SecDescription inc_expression(SymExpression operand) throws Exception {
		return SecFactory.add_expression(statement, expression, COperator.arith_add, operand);
	}
	protected SecDescription dec_expression(SymExpression operand) throws Exception {
		return SecFactory.add_expression(statement, expression, COperator.arith_sub, operand);
	}
	
}
