package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * <code>
 * 	|--	CirDataErrorState		(execution, [expr|cond|dvar|vdef], (val1, val2))<br>
 * 	|--	|--	CirValueErrorState	(execution, [expr|cond|dvar|vdef], (val1, val2))<br>
 * 	|--	|--	CirIncreErrorState	(execution, [expr|dvar|vdef],	(base, differ))	<br>
 * 	|--	|--	CirBixorErrorState	(execution, [expr|dvar|vdef],	(base, differ))	<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class CirDataErrorState extends CirAbstErrorState {

	protected CirDataErrorState(CirExecution point, CirStateStore store, CirStateValue value) throws Exception {
		super(point, store, value);
	}
	
	/**
	 * @return the expression where the data state error is injected
	 */
	public CirExpression get_expression() { return (CirExpression) this.get_clocation(); }
	
	/**
	 * @return whether the expression/store unit is definition point
	 */
	public boolean is_defined_point() {
		if(StateMutations.is_assigned(this.get_expression())) {
			return true;
		}
		else {
			return this.get_store_type() == CirStoreClass.vdef;
		}
	}
	
}
