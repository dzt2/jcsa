package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	<code>
 * 	CirAbstractState			st_class(storage_l; l_operand, r_operand)		<br>
 * 	|--	CirConditionState		st_class(statement; l_operand, r_operand)		<br>
 * 	|--	|--	CirSeedMutantState	sed_muta(statement; mutant_ID, clas_oprt)		<br>
 * 	|--	|--	CirCoverTimesState	cov_time(statement; min_times, max_times)		<br>
 * 	|--	|--	CirConstraintState	eva_cond(statement; condition, must_need)		<br>
 * 	</code>
 * @author yukimula
 *
 */
public abstract class CirConditionState extends CirAbstractState {

	protected CirConditionState(CirAbstractClass category, CirAbstractStore location, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(category, location, loperand, roperand);
		if(!location.is_statement()) {
			throw new IllegalArgumentException("Invalid location: " + location);
		}
	}

}
