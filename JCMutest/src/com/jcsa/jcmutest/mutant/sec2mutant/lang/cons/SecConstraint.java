package com.jcsa.jcmutest.mutant.sec2mutant.lang.cons;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecOptimizer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.test.state.CStateContexts;

/**
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecConstraint															<br>
 * 	|--	SecConditionConstraint			assert(statement, expression)		<br>
 * 	|--	SecExecutionConstraint			execute(statement, expression)		<br>
 * 	|--	SecConjunctConstraints			conjunct{constraint+}				<br>
 * 	|--	SecDisjunctConstraints			disjunct{constraint+}				<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SecConstraint extends SecDescription {
	
	public SecConstraint(CirStatement statement, SecKeywords keyword) throws Exception {
		super(statement, keyword);
	}
	
	@Override
	public CirNode get_cir_location() {
		return this.get_statement().get_statement();
	}
	
	/**
	 * @return the symbolic expression that describes the constraint
	 * @throws Exception
	 */
	public abstract SymExpression get_sym_condition() throws Exception;
	
	/**
	 * @param contexts
	 * @return the constraint optimized from this one using contextual data
	 * @throws Exception
	 */
	public SecConstraint optimize(CStateContexts contexts) throws Exception {
		return SecOptimizer.optimize(this, contexts);
	}
	
}
