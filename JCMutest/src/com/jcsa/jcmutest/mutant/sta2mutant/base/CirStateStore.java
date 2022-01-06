package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateValuations;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * The store unit to preserve values hold in abstract execution state used in
 * mutation-oriented analysis.
 * 
 * @author yukimula
 *
 */
public class CirStateStore {
	
	/* attributes */
	private CirStoreClass		store_type;
	private	CirNode				store_unit;
	private	SymbolExpression	symbol_key;
	private CirStateStore(CirStoreClass store_type,
			CirNode store_unit, 
			SymbolExpression symbol_key) throws Exception {
		if(store_type == null) {
			throw new IllegalArgumentException("Invalid store_type: null");
		}
		else if(store_unit == null) {
			throw new IllegalArgumentException("Invalid store_unit: null");
		}
		else if(symbol_key == null) {
			throw new IllegalArgumentException("Invalid symbol_key: null");
		}
		else {
			this.store_type = store_type;
			this.store_unit = store_unit;
			this.symbol_key = StateValuations.evaluate(symbol_key);
		}
	}
	
	/* getters */
	/**
	 * @return	the category of the store unit 
	 */
	public 	CirStoreClass 		get_store_type()	{ return this.store_type; }
	/**
	 * @return the store unit location 
	 */
	public	CirNode				get_store_unit()	{ return this.store_unit; }
	/**
	 * @return	the execution point where this store unit is allocated
	 */
	public	CirExecution		get_execution()		{ return this.store_unit.execution_of(); }
	/**
	 * @return the symbolic key to refine the definition (uniquely)
	 */
	public	SymbolExpression	get_symbol_key()	{ return this.symbol_key; }
	/**
	 * @return whether this store unit is virtually defined (vcon, vuse, vdef)
	 */
	public	boolean				is_virtual()		{
		switch(this.store_type) {
		case vcon:
		case vuse:
		case vdef:	return true;
		default:	return false;
		}
	}
	@Override
	public 	String 				toString() 			{
		String type = this.store_type.toString();
		String unit = "#" + this.store_unit.get_node_id();
		String key = "#" + this.symbol_key.toString();
		return type + unit + key;
	}
	@Override
	public 	int					hashCode()			{ return this.toString().hashCode(); }
	@Override
	public 	boolean 			equals(Object obj) 	{
		if(obj instanceof CirStateStore) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	
	/* creation */
	/**
	 * It creates a non-virtual store unit from the given location
	 * @param unit		CirStatement|CirExpression
	 * @return			CirStatement				-->	stmt:unit:{sym(unit)}	<br>
	 * 					CirExpression 	{boolean}	-->	cond:unit:{sym(unit)}	<br>
	 * 					CirExpression	{no-left}	-->	usep:unit:{sym(unit)}	<br>
	 * 					CirExpression	{on-left}	-->	defp:unit:{sym(unit)}	<br>
	 * @throws Exception
	 */
	public	static	CirStateStore	new_unit(CirNode unit)	throws Exception {
		if(unit == null) {
			throw new IllegalArgumentException("Invalid unit as null");
		}
		else if(unit instanceof CirStatement) {
			CirStatement statement = (CirStatement) unit;
			return new CirStateStore(CirStoreClass.stmt, statement, 
							SymbolFactory.sym_expression(statement));
		}
		else if(unit instanceof CirExpression) {
			CirExpression expression = (CirExpression) unit;
			if(StateValuations.is_boolean(expression)) {
				return new CirStateStore(CirStoreClass.cond, expression,
							SymbolFactory.sym_expression(expression));
			}
			else if(StateValuations.is_assigned(expression)) {
				return new CirStateStore(CirStoreClass.defp, expression,
							SymbolFactory.sym_expression(expression));
			}
			else {
				return new CirStateStore(CirStoreClass.usep, expression,
						SymbolFactory.sym_expression(expression));
			}
		}
		else {
			throw new IllegalArgumentException(unit.getClass().getSimpleName());
		}
	}
	/**
	 * It creates a virtual predicate in a given statement point.
	 * @param statement
	 * @param condition
	 * @return vcon:statement:{sym(condition)}
	 * @throws Exception
	 */
	public 	static	CirStateStore	new_vcon(CirStatement statement, SymbolExpression condition) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new CirStateStore(CirStoreClass.vcon, statement, condition);
		}
	}
	/**
	 * It creates a virtual use_point to replace an existing use_point 
	 * @param ori_usep	the existing use_point being replaced with new use_point
	 * @param new_usep	the new use_point to replace the original use_point in the store unit
	 * @return			vuse:ori_usep:new_usep
	 * @throws Exception
	 */
	public	static 	CirStateStore	new_vuse(CirExpression ori_usep, SymbolExpression new_usep) throws Exception {
		if(ori_usep == null) {
			throw new IllegalArgumentException("Invalid ori_usep: null");
		}
		else if(new_usep == null) {
			throw new IllegalArgumentException("Invalid new_usep: null");
		}
		else if(new_usep.is_reference()) {
			if(StateValuations.is_assigned(ori_usep)) {
				throw new IllegalArgumentException("left-value is not allowed");
			}
			else {
				return new CirStateStore(CirStoreClass.vuse, ori_usep, new_usep);
			}
		}
		else {
			throw new IllegalArgumentException(new_usep.generate_code(false));
		}
	}
	/**
	 * It creates a virtual def_point to replace the original location unit
	 * @param statement	the statement point in which the new definition point is introduced
	 * @param new_defp	the symbolic identifier or reference to define the new def_point in
	 * @return			vdef:statement:new_defp
	 * @throws Exception
	 */
	public	static	CirStateStore	new_vdef(CirStatement statement, SymbolExpression new_defp) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(new_defp == null) {
			throw new IllegalArgumentException("Invalid new_defp: null");
		}
		else if(new_defp.is_reference()) {
			return new CirStateStore(CirStoreClass.vdef, statement, new_defp);
		}
		else {
			throw new IllegalArgumentException(new_defp.generate_code(false));
		}
	}
	
}
