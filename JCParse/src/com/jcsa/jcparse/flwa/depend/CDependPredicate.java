package com.jcsa.jcparse.flwa.depend;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * Element in control dependence represents a predicate as 
 * 			{if_statement, condition, predicate_value}
 * @author yukimula
 *
 */
public class CDependPredicate {
	
	private CirExpression condition;
	private boolean predicate_value;
	
	protected CDependPredicate(CirIfStatement statement, boolean predicate_value) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else {
			this.condition = statement.get_condition(); 
			this.predicate_value = predicate_value;
		}
	}
	protected CDependPredicate(CirCaseStatement statement, boolean predicate_value) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else {
			this.condition = statement.get_condition(); 
			this.predicate_value = predicate_value;
		}
	}
	
	public CirStatement get_statement() { return this.condition.statement_of(); }
	public CirExpression get_condition() { return this.condition; }
	public boolean get_predicate_value() { return this.predicate_value; }
	
	@Override
	public String toString() {
		try {
			return condition.generate_code(true) + " as " + predicate_value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
