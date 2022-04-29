package com.jcsa.jcmutest.mutant.ctx2mutant.muta.oprt;

import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstAbstErrorState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstBlockErrorState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstConstraintState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstContextState;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
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
	public	CirOperatorMutationParser() { }
	
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
	
	/* basic methods */
	/**
	 * @param condition
	 * @return eva_cond(statement; condition, false)
	 * @throws Exception
	 */
	private	AstConstraintState get_constraint(Object condition) throws Exception {
		if(this.output == null) {
			throw new IllegalArgumentException("Invalid output: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return AstContextState.eva_cond(this.output.get_statement(), condition, false);
		}
	}
	/**
	 * @return
	 * @throws Exception
	 */
	private	AstBlockErrorState trap_statement() throws Exception {
		if(this.output == null) {
			throw new IllegalArgumentException("Invalid output as: null");
		}
		else {
			return AstContextState.trp_stmt(this.output.get_statement());
		}
	}
	/**
	 * @param muvalue
	 * @return set_expr|trp_stmt
	 * @throws Exception
	 */
	private	AstAbstErrorState  set_expression(Object muvalue) throws Exception {
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
			return AstContextState.set_expr(this.output.get_location(), 
					SymbolFactory.sym_expression(expression), 
					SymbolFactory.sym_expression(muvalue));
		}
	}
	
	/* symbolic analysis */
	
	
	
	
	
	
	
}
