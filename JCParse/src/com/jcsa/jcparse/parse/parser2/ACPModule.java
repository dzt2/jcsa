package com.jcsa.jcparse.parse.parser2;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

class ACPModule {
	
	/* definitions & constructor */
	private List<CirStatement> statements;
	private Map<AstNode, ACPSolution> solutions;
	private Stack<ACPScope> scopes;
	protected ACPModule(AstNode root) throws IllegalArgumentException {
		this.statements = new ArrayList<CirStatement>();
		this.solutions = new HashMap<AstNode, ACPSolution>();
		this.scopes = new Stack<ACPScope>();
		this.scopes.push(new ACPScope(root));
	}
	
	/* getters and setters */
	/**
	 * get all the solutions created within the solution space of the module
	 * @return
	 */
	public Iterable<ACPSolution> get_solutions() { return solutions.values(); }
	/**
	 * get the solution representing the parsing result of the specified AST source node
	 * @param ast_source
	 * @return
	 * @throws IllegalArgumentException
	 */
	public ACPSolution get_solution(AstNode ast_source) throws IllegalArgumentException {
		if(ast_source == null)
			throw new IllegalArgumentException("invalid ast_source as null");
		else {
			if(!this.solutions.containsKey(ast_source))
				this.solutions.put(ast_source, new ACPSolution(this, ast_source));
			return this.solutions.get(ast_source);
		}
	}
	/**
	 * append the statement into the module used to construct the function body.
	 * @param statement
	 * @throws IllegalArgumentException
	 */
	protected void append(CirStatement statement) throws IllegalArgumentException {
		if(statement == null)
			throw new IllegalArgumentException("invalid statement: null");
		else this.statements.add(statement);
	}
	/**
	 * get the scope at the top of the module stack
	 * @return
	 * @throws EmptyStackException
	 */
	public ACPScope get_top_scope() throws EmptyStackException { return this.scopes.peek(); }
	/**
	 * get the scope at the root of the module stack
	 * @return
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public ACPScope get_root_scope() throws ArrayIndexOutOfBoundsException { return this.scopes.get(0); }
	/**
	 * create a new child scope under the scope with respect to the AST key node at the top of the stack.
	 * @param ast_key
	 * @return
	 * @throws IllegalArgumentException
	 * @throws EmptyStackException
	 */
	public ACPScope push_scope(AstNode ast_key) throws IllegalArgumentException, EmptyStackException {
		ACPScope parent = this.scopes.peek();
		ACPScope child = parent.new_child_scope(ast_key);
		this.scopes.push(child); return child;
	}
	/**
	 * get the scope at the top of the stack and remove it from stack at the same time
	 * @return
	 * @throws EmptyStackException
	 */
	public ACPScope pop_scope() throws EmptyStackException {
		return this.scopes.pop();
	}
	/**
	 * get all the statements in the module
	 * @return
	 */
	public Iterable<CirStatement> get_statements() { return this.statements; }
	
}
