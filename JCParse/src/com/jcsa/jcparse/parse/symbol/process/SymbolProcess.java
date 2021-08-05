package com.jcsa.jcparse.parse.symbol.process;

import java.util.Collection;
import java.util.HashSet;

import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBegStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStateUnit;


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
	private SymbolFactory symbol_factory;
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
			this.symbol_factory = new SymbolFactory();
			this.invokers = new HashSet<>();
			this.data_stack = new SymbolDataStack(this);

			/* NOTE add default invokers here */
			this.add_invoker(new SymbolMathPackageInvoker());
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
	public SymbolFactory get_symbol_factory() { return this.symbol_factory; }
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
	/**
	 * accumulate the state into node
	 * @param node
	 * @throws Exception
	 */
	public void accumulate(CStateNode node) throws Exception {
		if(node == null)
			throw new IllegalArgumentException("Invalid node");
		else {
			/* 1. update the left-reference in prev-assignment */
			if(node.get_prev_node() != null) {
				CirStatement prev_statement = node.get_prev_node().get_statement();
				if(prev_statement instanceof CirAssignStatement) {
					CirExpression lvalue = ((CirAssignStatement) prev_statement).get_lvalue();
					CirExpression rvalue = ((CirAssignStatement) prev_statement).get_rvalue();
					if(node.get_prev_node().has_unit(rvalue)) {
						this.data_stack.save(lvalue, node.get_prev_node().get_unit(rvalue).get_value());
					}
				}
			}

			/* 2. update the scope at the border of function */
			CirStatement statement = node.get_statement();
			CirFunction def = statement.get_tree().get_localizer().
					get_execution(statement).get_graph().get_function();
			if(statement instanceof CirBegStatement) {
				this.data_stack.push_block(def);
			}
			else if(statement instanceof CirEndStatement) {
				this.data_stack.pop_block(def);
			}

			/* 3. update the local state in current scope */
			for(CStateUnit unit : node.get_units()) {
				if(unit.has_value()) {
					Object orig_value = unit.get_value();
					SymbolExpression source = SymbolFactory.sym_expression(orig_value);
					SymbolExpression target = SymbolEvaluator.evaluate_on(source, this);
					this.data_stack.save(unit.get_expression(), target);
				}
			}

			/* 4. accumulate the statement as being executed */
			SymbolExpression value = this.data_stack.load(statement);
			int counter = 0;
			if(value != null) {
				counter = ((SymbolConstant) value).get_int();
			}
			counter++;
			this.data_stack.save(statement, Integer.valueOf(counter));
		}
	}

}
