package com.jcsa.jcmutest.mutant.uni2mutant.base.impl;

import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractClass;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractState;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractStore;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	It represents a state error correlated with execution path | statement.
 * 	<br>
 * 	<code>
 * 	UniPathErrorState			class(statement; lvalue, rvalue)			<br>
 * 	|--	UniBlockErrorState		mut_stmt(statement; orig_exec, muta_exec)	<br>
 * 	|--	UniFlowsErrorState		mut_flow(statement; orig_next, muta_next)	<br>
 * 	|--	UniTrapsErrorState		trp_stmt(statement; exception, exception)	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class UniPathErrorState extends UniAbstractState {

	protected UniPathErrorState(UniAbstractClass state_class, 
			UniAbstractStore state_store, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(state_class, state_store, loperand, roperand);
		if(state_store.is_statement() || state_store.is_goto_label()) { /* valid */ }
		else {
			throw new IllegalArgumentException("Invalid state_store: " + state_store);
		}
	}
	
	/**
	 * @return the statement where the path error arises from
	 */
	public CirStatement get_statement() { 
		return (CirStatement) this.get_store().get_cir_location(); 
	}
	
}
