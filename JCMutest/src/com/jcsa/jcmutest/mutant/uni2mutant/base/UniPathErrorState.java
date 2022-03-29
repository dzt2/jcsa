package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	<code>
 * 	UniAbstractState				[st_class, c_loct; lsh_value, rsh_value]	<br>
 * 	|--	UniPathErrorState			[s_class,  c_stmt; lsh_value, rsh_value]	<br>
 * 	|--	|--	UniBlockErrorState		[mut_stmt, c_stmt; orig_exec, muta_exec]	<br>
 * 	|--	|--	UniFlowsErrorState		[mut_flow, c_stmt; orig_next, muta_next]	<br>
 * 	|--	|--	UniTrapsErrorState		[trp_stmt, c_stmt; exception, exception]	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class UniPathErrorState extends UniAbstErrorState {
	
	/**
	 * @param _class
	 * @param _store
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	protected UniPathErrorState(UniAbstractClass _class, UniAbstractStore _store, SymbolExpression lvalue,
			SymbolExpression rvalue) throws Exception {
		super(_class, _store, lvalue, rvalue);
		if(!_store.is_statement()) {
			throw new IllegalArgumentException("Statement-Store required.");
		}
	}
	
	/**
	 * @return the statement where the condition is evaluated as a state
	 */
	public CirStatement get_statement() { return (CirStatement) this.get_state_store().get_cir_location(); }
	
}
