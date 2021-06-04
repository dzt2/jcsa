package com.jcsa.jcparse.parse.symbol2.process;

import java.util.Collection;
import java.util.HashSet;

import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.symbol.SymbolNodeFactory;

/**
 * SymbolProcess is the basic top instance to represent a program process being executed symbolically by the engine,
 * which maintains necessary data elements for analysis, including the local memory stack etc.
 * 
 * @author yukimula
 *
 */
public class SymbolProcess {
	
	/* definitions */
	/** abstract syntactic tree of the program being executed **/
	private AstTree ast_tree;
	/** C-intermediate representative code of the program for execution **/
	private CirTree cir_tree;
	/** used to construct symbolic expression or value used in analysis **/
	private SymbolNodeFactory symbol_factory;
	/** the set of symbolic invocators used to evaluate call-expression **/
	private Collection<SymbolInvoker> invokers;
	/** it simulates the data stack used in local memory system **/
	private SymbolDataStack data_stack;
	
	/* constructor */
	/**
	 * create a new process to symbolically execute the given program
	 * @param ast_tree
	 * @param cir_tree
	 * @throws Exception
	 */
	public SymbolProcess(AstTree ast_tree, CirTree cir_tree) throws Exception {
		if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else {
			this.ast_tree = ast_tree;
			this.cir_tree = cir_tree;
			this.symbol_factory = new SymbolNodeFactory();
			this.invokers = new HashSet<SymbolInvoker>();
			this.data_stack = new SymbolDataStack(this);
		}
	}
	
	/* basic getters */
	/**
	 * @return abstract syntactic tree of the program being executed
	 */
	public AstTree get_ast_tree() { return this.ast_tree; }
	/**
	 * @return C-intermediate representative code of the program for execution
	 */
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * @return used to construct symbolic expression or value used in analysis
	 */
	public SymbolNodeFactory get_symbol_factory() { return this.symbol_factory; }
	/**
	 * @return the set of symbolic invocators used to evaluate call-expression
	 */
	public Iterable<SymbolInvoker> get_invokers() { return this.invokers; }
	/**
	 * @return it simulates the data stack used in local memory system 
	 */
	public SymbolDataStack get_data_stack() { return this.data_stack; }
	/**
	 * add the invoker to the process library
	 * @param invoker
	 * @throws Exception
	 */
	public void add_invoker(SymbolInvoker invoker) throws Exception {
		if(invoker == null)
			throw new IllegalArgumentException("Invalid invoker: null");
		else 
			this.invokers.add(invoker);
	}
	
}
