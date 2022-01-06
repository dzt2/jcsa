package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateValuations;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * The store unit instance to be connected with values (interpretation) of the 
 * abstract execution states established at some point in the mutation testing.
 * 
 * @author yukimula
 *
 */
public class CirStateStore {
	
	/* definitions */
	private CirStoreClass 		type;
	private CirNode				unit;
	private SymbolExpression	skey;
	private CirStateStore(CirStoreClass type, CirNode unit,
				SymbolExpression skey) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(unit == null) {
			throw new IllegalArgumentException("Invalid unit: null");
		}
		else if(skey == null) {
			throw new IllegalArgumentException("Invalid skey: null");
		}
		else { 
			this.type = type; this.unit = unit; 
			this.skey = StateValuations.evaluate(skey);
		}
	}
	
	/* getters */
	/**
	 * @return the category of the store unit to preserve state values
	 */
	public CirStoreClass 	get_type() { return this.type; }
	/**
	 * @return the C-intermediate representations to define store unit
	 */
	public CirNode			get_unit() { return this.unit; }
	/**
	 * @return the execution point where the store unit is established
	 */
	public CirExecution		get_exec() { return this.unit.execution_of(); }
	/**
	 * @return the symbolic expression to uniquely define store unit
	 */
	public SymbolExpression get_skey() { return this.skey; }
	
	/* general */
	@Override
	public String toString() { 
		return this.type + "#" + this.unit.get_node_id() + "#" + this.skey; 
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CirStateStore) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	@Override
	public CirStateStore clone() {
		try {
			return new CirStateStore(this.type, this.unit, this.skey);
		}
		catch(Exception ex) {
			return this;
		}
	}
	
	/* factory */
	/**
	 * It creates a non-virtual store unit to preserve value for the input unit
	 * @param unit	
	 * @return	CirStatement 	{execute}	-->	stmt:unit:sym_statement		<br>
	 * 			CirExpression	{no-left}	-->	usep:unit:sym_expression	<br>
	 * 			CirExpression	{on-left}	-->	defp:unit:sym_reference		<br>
	 * @throws Exception
	 */
	public static CirStateStore new_unit(CirNode unit) throws Exception {
		if(unit == null) {
			throw new IllegalArgumentException("Invalid unit: null");
		}
		else if(unit instanceof CirStatement) {
			return new CirStateStore(CirStoreClass.stmt, unit,
							SymbolFactory.sym_expression(unit));
		}
		else if(unit instanceof CirExpression) {
			CirExpression expression = (CirExpression) unit;
			if(StateValuations.is_assigned(expression)) {
				return new CirStateStore(CirStoreClass.defp, expression,
						SymbolFactory.sym_expression(expression));
			}
			else if(StateValuations.is_boolean(expression)) {
				return new CirStateStore(CirStoreClass.usep, expression,
						SymbolFactory.sym_condition(expression, true));
			}
			else {
				return new CirStateStore(CirStoreClass.usep, expression,
						SymbolFactory.sym_expression(expression));
			}
		}
		else {
			throw new IllegalArgumentException(unit.getClass().getName());
		}
	}
	/**
	 * It creates a virtual store unit to represent the definition point introduced
	 * @param unit
	 * @param reference
	 * @return	vdef:unit:reference
	 * @throws Exception
	 */
	public static CirStateStore new_vdef(CirExpression unit, SymbolExpression reference) throws Exception {
		if(unit == null) {
			throw new IllegalArgumentException("Invalid unit: null");
		}
		else if(reference == null || !reference.is_reference()) {
			throw new IllegalArgumentException("Invalid reference: " + reference);
		}
		else {
			return new CirStateStore(CirStoreClass.vdef, unit, reference);
		}
	}
	
}
