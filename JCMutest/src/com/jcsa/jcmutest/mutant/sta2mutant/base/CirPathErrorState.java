package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * <code>
 * 	|--	CirPathErrorState		[category, execution, statement,  {oexe, mexe}]	<br>
 * 	|--	|--	CirBlockErrorState	[mut_stmt, execution, statement,  {bool, bool}]	<br>
 * 	|--	|--	CirFlowsErrorState	[mut_flow, execution, statement,  {ostm, mstm}]	<br>
 * 	|--	|--	CirBrachErrorState	[mut_brac, execution, statement,  {ocon, mcon}]	<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class CirPathErrorState extends CirAbstractState {

	protected CirPathErrorState(CirStateCategory category, CirExecution execution, 
			SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		super(category, execution, execution.get_statement(), 2);
		this.set_parameter(0, CirStateValuation.evaluate(orig_value));
		this.set_parameter(1, CirStateValuation.evaluate(muta_value));
	}
	
	/**
	 * @return the original value of the path error on the target statement
	 */
	protected SymbolExpression get_orig_value() { return this.get_parameter(0); }
	/**
	 * @return the mutation value of the path error on the target statement
	 */
	protected SymbolExpression get_muta_value() { return this.get_parameter(1); }
	
}
