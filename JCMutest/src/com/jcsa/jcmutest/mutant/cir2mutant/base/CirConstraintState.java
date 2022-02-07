package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * eva_expr([statement, stmt_key]; [must_need, condition])
 * where: must_need is True if the condition should be satisfied every time the
 * 		  statement is reached; or False if the condition needs be satisfied at
 *        least once during testing.
 *        
 * @author yukimula
 *
 */
public class CirConstraintState extends CirConditionState {

	protected CirConstraintState(CirExecution execution, boolean must_need,
			Object condition) throws Exception {
		super(CirAbstractClass.eva_expr, execution, 
				SymbolFactory.sym_constant(Boolean.valueOf(must_need)), 
				SymbolFactory.sym_condition(condition, true));
	}
	
	/* special getters */
	/** 
	 * @return whether the constraint must be hold for every time the statement
	 * 			is executed during testing
	 */
	public boolean is_must_constrain() {
		SymbolConstant loperand = (SymbolConstant) this.get_loperand();
		return loperand.get_bool();
	}
	/**
	 * @return whether the constraint must be hold for at least once if the
	 * 			statement is executed.
	 */
	public boolean is_need_constrain() {
		SymbolConstant loperand = (SymbolConstant) this.get_loperand();
		return !loperand.get_bool();
	}
	/**
	 * @return the symbolic condition to be evaluated at the statement
	 */
	public SymbolExpression get_condition() { return this.get_roperand(); }
	
}
