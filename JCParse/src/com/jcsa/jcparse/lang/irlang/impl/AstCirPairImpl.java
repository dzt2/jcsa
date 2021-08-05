package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class AstCirPairImpl implements AstCirPair {

	private AstNode ast_source;
	private CirStatement beg_statement;
	private CirStatement end_statement;
	private CirExpression result;

	protected AstCirPairImpl(AstNode ast_source) throws IllegalArgumentException {
		if(ast_source == null)
			throw new IllegalArgumentException("invalid ast-source as null");
		else {
			this.ast_source = ast_source;
			this.beg_statement = null;
			this.end_statement = null;
			this.result = null;
		}
	}

	@Override
	public AstNode get_ast_source() { return this.ast_source; }
	@Override
	public CirStatement get_beg_statement() { return this.beg_statement; }
	@Override
	public CirStatement get_end_statement() { return this.end_statement; }
	@Override
	public CirExpression get_result() { return this.result; }
	@Override
	public boolean executional() { return (this.beg_statement != null) && (this.end_statement != null); }
	@Override
	public boolean computational() { return this.result != null; }
	/**
	 * set the code range by setting its statements range and the representative expression
	 * @param beg_statement
	 * @param end_statement
	 * @param result
	 */
	public void set(CirStatement beg_statement, CirStatement end_statement, CirExpression result) {
		this.beg_statement = beg_statement; this.end_statement = end_statement; this.result = result;
	}

}
