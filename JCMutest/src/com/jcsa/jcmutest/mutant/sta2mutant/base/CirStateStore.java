package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It models the store unit in the abstract execution state of mutation testing.
 * 
 * @author yukimula
 *
 */
class CirStateStore {
	
	/* definitions */
	/**	category 	**/	private CirStoreClass 		type;
	/**	location	**/	private CirNode				unit;
	/**	identifier	**/	private SymbolExpression	skey;
	private CirStateStore(CirStoreClass type, CirNode 
			unit, SymbolExpression skey) throws Exception {
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
			this.skey = StateMutations.evaluate(skey);
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
	 * It creates a non-virtual store unit from C-intermediate code entity
	 * @param unit	{CirStatement|CirExpression}
	 * @return		CirStatement	(execute)	-->	[stmt]
	 * 				CirExpression	(boolean)	-->	[cond]
	 * 				CirExpression	(no-left)	-->	[expr]
	 * 				CirExpression	(on-left)	-->	[dvar]
	 * @throws Exception
	 */
	protected static CirStateStore new_unit(CirNode unit) throws Exception {
		if(unit == null) {
			throw new IllegalArgumentException("Invalid unit as null");
		}
		else if(unit instanceof CirStatement) {
			return new CirStateStore(CirStoreClass.stmt, unit, SymbolFactory.sym_expression(unit));
		}
		else if(unit instanceof CirExpression) {
			CirExpression expression = (CirExpression) unit;
			SymbolExpression skey = SymbolFactory.sym_expression(expression);
			if(StateMutations.is_boolean(expression)) {
				return new CirStateStore(CirStoreClass.cond, unit, skey);
			}
			else if(StateMutations.is_assigned(expression)) {
				return new CirStateStore(CirStoreClass.dvar, unit, skey);
			}
			else {
				return new CirStateStore(CirStoreClass.expr, unit, skey);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + unit.getClass().getSimpleName());
		}
	}
	/**
	 * It creates a virtual reference unit to preserve value that is never defined in original program entities.
	 * @param unit	the location where the virtual reference node is created
	 * @param refer	the symbolic identifier to specify the unique virtual definition point
	 * @return		vdef(unit, refer)
	 * @throws Exception
	 */
	protected static CirStateStore new_vdef(CirNode unit, SymbolExpression refer) throws Exception {
		if(unit == null) {
			throw new IllegalArgumentException("Invalid unit: null");
		}
		else if(refer == null) {
			throw new IllegalArgumentException("Invalid refer: null");
		}
		else if(refer.is_reference()) {
			return new CirStateStore(CirStoreClass.vdef, unit, refer);
		}
		else {
			throw new IllegalArgumentException(refer.generate_code(true));
		}
	}
	
}
