package com.jcsa.jcparse.parse.parser2;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

class ACPSolution {
	
	/* definitions */
	/** the module in which statements are created and maintained **/
	private ACPModule module;
	/** the AST source node to be solved **/
	private AstNode ast_source;
	/** the first statement being executed as the entry to the range of the AST node **/
	private CirStatement beg_statement;
	/** the final statement as the exit to the range of the AST source node **/
	private CirStatement end_statement;
	/** the representative expression as the result of the AST source node **/
	private CirExpression result;
	/**
	 * create a solution corresponding to the AST source node in the context of a given module
	 * @param module
	 * @param ast_source
	 * @throws IllegalArgumentException
	 */
	protected ACPSolution(ACPModule module, AstNode ast_source) throws IllegalArgumentException {
		if(module == null)
			throw new IllegalArgumentException("invalid module: null");
		else if(ast_source == null)
			throw new IllegalArgumentException("invalid ast_source as null");
		else {
			this.module = module; 
			this.ast_source = ast_source;
			this.beg_statement = null;
			this.end_statement = null;
			this.result = null;
		}
	}
	
	/* getters */
	/**
	 * get the AST source node to be solved
	 * @return
	 */
	public AstNode get_ast_source() { return this.ast_source; }
	/**
	 * get the first statement as entry to the code range
	 * @return
	 */
	public CirStatement get_beg_statement() { return this.beg_statement; }
	/**
	 * get the final statement as the exits of code range
	 * @return
	 */
	public CirStatement get_end_statement() { return this.end_statement; }
	/**
	 * get the representative expression for the AST source node's result
	 * @return
	 */
	public CirExpression get_result() { return this.result; }
	/**
	 * whether the AST source node refers to any statement in CIR code range
	 * @return
	 */
	public boolean executional() { return (this.beg_statement != null) && (this.end_statement != null); }
	/**
	 * whether there are some expressions representing the result of the AST source node
	 * @return
	 */
	public boolean computational() { return this.result != null; }
	
	/* setters */
	/**
	 * add the statement at the tail of the code range referred from the AST node
	 * @param statement
	 * @throws IllegalArgumentException
	 */
	public void append(CirStatement statement) throws IllegalArgumentException {
		this.module.append(statement);
		if(this.beg_statement == null)
			this.beg_statement = statement;
		this.end_statement = statement;
	}
	/**
	 * add the statements in the next solution into the range of this AST source
	 * @param next_solution
	 * @throws IllegalArgumentException
	 */
	public void append(ACPSolution next_solution) throws IllegalArgumentException {
		if(next_solution == null)
			throw new IllegalArgumentException("invalid solution: null");
		else {
			if(next_solution.executional()) {
				if(this.beg_statement == null)
					this.beg_statement = next_solution.beg_statement;
				this.end_statement = next_solution.end_statement;
			}
		}
	}
	/**
	 * set the expression as the representative result of the AST source node
	 * @param expression
	 * @throws IllegalArgumentException
	 */
	public void set(CirExpression expression) throws IllegalArgumentException {
		if(expression == null)
			throw new IllegalArgumentException("invalid expression as null");
		else this.result = expression;
	}
	
}
