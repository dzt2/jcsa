package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * eva_expr(execution, statement, {condition});
 * 
 * @author yukimula
 *
 */
public class CirConstraintState extends CirConditionState {

	protected CirConstraintState(CirExecution execution, SymbolExpression parameter) throws Exception {
		super(CirStateCategory.eva_expr, execution, parameter);
	}
	
	/**
	 * @return the symbolic constraint on which the state needs to match with
	 */
	public SymbolExpression get_constraint() { return this.get_parameter(); }

}
