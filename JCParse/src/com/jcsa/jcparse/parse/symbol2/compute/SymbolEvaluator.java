package com.jcsa.jcparse.parse.symbol2.compute;

import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol2.process.SymbolProcess;

/**
 * It implements the evaluation module for simplifying or computing symbolic expression based on
 * contextual information provided from memory of symbolic process or none to simplify expression
 * @author yukimula
 *
 */
public class SymbolEvaluator {
	
	/* definitions */
	/** the context in which the evaluation was performed **/
	private SymbolProcess symbol_process;
	/** the computational unit for simplifying expression  **/
	protected SymbolComputer computer_unit;
	/**
	 * construct an empty non-initialized evaluator
	 */
	public SymbolEvaluator() { 
		this.set_symbol_process(null); 
		this.computer_unit = new SymbolComputer(this);
	}
	
	/* parameters */
	/**
	 * @return the context in which the evaluation was performed
	 */
	public SymbolProcess get_symbol_process() { return this.symbol_process; }
	/**
	 * @return the factory used to construct symbolic expression
	 */
	public SymbolFactory get_symbol_factory() { 
		if(this.symbol_process == null) {
			return SymbolFactory.factory;
		}
		else {
			return this.symbol_process.get_symbol_factory();
		}
	}
	/**
	 * @param process context to be established for evaluation
	 */
	public void set_symbol_process(SymbolProcess process) { this.symbol_process = process; }
	
	
	
	
	
}
