package com.jcsa.jcmutest.backups;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecExecutionConstraint extends SecConstraint {
	
	public SecExecutionConstraint(CirStatement statement, SymExpression times) throws Exception {
		super(statement, SecKeywords.execute);
		this.add_child(new SecExpression(times));
	}
	
	/**
	 * @return the times at least the target statement should be executed
	 */
	public SecExpression get_times() { return (SecExpression) this.get_child(2); }
	
	@Override
	public SymExpression get_sym_condition() throws Exception {
		return SymFactory.greater_eq(
				SecFactory.sym_statement(this.get_statement().get_statement()), 
				this.get_times().get_expression());
	}
	
	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_times().generate_code() + ")";
	}
	
}
