package com.jcsa.jcparse.parse.symbol2.evaluate;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolNodeFactory;
import com.jcsa.jcparse.parse.symbol2.process.SymbolProcess;

/**
 * It implements the recursive evaluation of symbolic expression using context data provided from SymbolProcess or null
 * 
 * @author yukimula
 *
 */
public class SymbolEvaluator {
	
	/* definitions of singleton */
	private SymbolProcess process;
	private SymbolNodeFactory symbol_factory;
	private SymbolComputer computer;
	private SymbolEvaluator() {
		this.process = null;
		this.symbol_factory = SymbolFactory.factory;
		this.computer = new SymbolComputer(this);
	}
	private static final SymbolEvaluator evaluator = new SymbolEvaluator();
	
	/* context operations */
	/**
	 * @return the factory used to construct symbolic value in evaluation
	 */
	protected SymbolNodeFactory get_symbol_factory() { return this.symbol_factory; }
	/**
	 * @return the process to provide memory to store value of references.
	 */
	protected SymbolProcess get_symbol_process() { return this.process; }
	/**
	 * update the symbolic process to provide contextual data
	 * @param process
	 */
	private void set_process(SymbolProcess process) {
		this.process = process;
		if(process == null) {
			this.symbol_factory = SymbolFactory.factory;
		}
		else {
			this.symbol_factory = process.get_symbol_factory();
		}
	}
	/**
	 * @param reference
	 * @return load the existing value stored for given reference in the process's memory space
	 * @throws Exception
	 */
	private SymbolExpression load_value(SymbolExpression reference) throws Exception {
		if(reference == null)
			throw new IllegalArgumentException("Invalid reference: null");
		else if(reference.is_reference() && this.process != null) 
			return this.process.get_data_stack().load(reference);
		else 
			return null;
	}
	
	
	
}
