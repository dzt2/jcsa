package com.jcsa.jcmutest.selang.muta.oprt;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.selang.muta.SedInfection;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It implements the construction of infection pairs for any mutation with 
 * set_operator or cmp_operator as mutation operator in testing.
 * 
 * @author yukimula
 *
 */
public abstract class AbsSetOperatorProcess {
	
	/* process data */
	/** the C-intermediate representation code for process **/
	protected CirTree cir_tree;
	/** statement where the mutation is seeded and reached **/
	protected CirStatement statement;
	/** the expression as the result being directly infected **/
	protected CirExpression expression;
	/** the left-operand in binary expression **/
	protected CirExpression loperand;
	/** the right-operand in binary expression **/
	protected CirExpression roperand;
	/** it is cmp_operator (true) or set_operator (false) **/
	protected boolean compare_or_mutate;
	/** the core data to be generated of infection pairs **/
	protected SedInfection infection;
	
	/* public interface */
	/**
	 * generate the infection process for the mutation with specified data
	 * @param cir_tree the C-intermediate representation code for process
	 * @param statement statement where the mutation is seeded and reached
	 * @param expression the expression as the result being directly infected
	 * @param loperand the left-operand in binary expression
	 * @param roperand the right-operand in binary expression
	 * @param mutation the mutation being seeded in the target program
	 * @param infection the core data to be generated of infection pairs
	 * @throws Exception
	 */
	public void process(CirTree cir_tree, CirStatement statement, 
			CirExpression expression, CirExpression loperand, 
			CirExpression roperand, AstMutation mutation, 
			SedInfection infection) throws Exception {
		/* 1. initial core data set */
		this.cir_tree = cir_tree;
		this.statement = statement;
		this.expression = expression;
		this.loperand = loperand;
		this.roperand = roperand;
		switch(mutation.get_operator()) {
		case set_operator:	this.compare_or_mutate = false;	break;
		case cmp_operator:	this.compare_or_mutate = true;	break;
		default: throw new IllegalArgumentException("Invalid: " + mutation);
		}
		this.infection = infection;
		
		/* 2. obtain the operator to replace the original one */
		COperator operator = (COperator) mutation.get_parameter();
		switch(operator) {
		case arith_add_assign:	operator = COperator.arith_add; break;
		case arith_sub_assign:	operator = COperator.arith_sub; break;
		case arith_mul_assign:	operator = COperator.arith_mul; break;
		case arith_div_assign:	operator = COperator.arith_div; break;
		case arith_mod_assign:	operator = COperator.arith_mod; break;
		case bit_and_assign:	operator = COperator.bit_and; 	break;
		case bit_or_assign:		operator = COperator.bit_or; 	break;
		case bit_xor_assign:	operator = COperator.bit_xor; 	break;
		case left_shift_assign:	operator = COperator.left_shift;break;
		case righ_shift_assign:	operator = COperator.righ_shift;break;
		default: 												break;
		}
		
		/* 3. operator-directed translation */
		switch(operator) {
		case arith_add:		this.arith_add(); break;
		case arith_sub:		this.arith_sub(); break;
		case arith_mul:		this.arith_mul(); break;
		case arith_div:		this.arith_div(); break;
		case arith_mod:		this.arith_mod(); break;
		case bit_and:		this.bitws_and(); break;
		case bit_or:		this.bitws_ior(); break;
		case bit_xor:		this.bitws_xor(); break;
		case left_shift:	this.bitws_lsh(); break;
		case righ_shift:	this.bitws_rsh(); break;
		case logic_and:		this.logic_and(); break;
		case logic_or:		this.logic_ior(); break;
		case greater_tn:	this.greater_tn();break;
		case greater_eq:	this.greater_eq();break;
		case smaller_tn:	this.smaller_tn();break;
		case smaller_eq:	this.smaller_eq();break;
		case equal_with:	this.equal_with();break;
		case not_equals:	this.not_equals();break;
		case assign:		this.assignment();break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
	}
	
	/* implementation */
	protected abstract void assignment() throws Exception;
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
	
}
