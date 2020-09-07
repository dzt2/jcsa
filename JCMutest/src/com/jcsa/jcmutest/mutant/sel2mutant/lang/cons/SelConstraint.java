package com.jcsa.jcmutest.mutant.sel2mutant.lang.cons;

import com.jcsa.jcmutest.mutant.sel2mutant.lang.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.desc.SelDescription;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelConstraint															<br>
 * 	|--	SelExecutionConstraint		execute(stmt, int)						<br>
 * 	|--	SelConditionConstraint		asserts(stmt, expr)						<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SelConstraint extends SelDescription {

	public SelConstraint(CirStatement statement, SelKeywords keyword) throws Exception {
		super(statement, keyword);
	}

}
