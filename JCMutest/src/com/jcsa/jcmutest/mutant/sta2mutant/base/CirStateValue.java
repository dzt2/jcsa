package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateValuations;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It defines the values used to define abstract execution state that is 
 * connected with some store unit in the program for mutation testing.
 * 
 * @author yukimula
 *
 */
public class CirStateValue {
	
	/* definitions */
	private CirValueClass		vtype;
	private SymbolExpression[]	vlist;
	private CirStateValue(CirValueClass vtype, SymbolExpression value) throws Exception {
		if(vtype == null) {
			throw new IllegalArgumentException("Invalid vtype: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value: null");
		}
		else {
			this.vtype = vtype; 
			this.vlist = new SymbolExpression[] {
					StateValuations.evaluate(value)
			};
		}
	}
	private CirStateValue(CirValueClass vtype, SymbolExpression lvalue, SymbolExpression rvalue) throws Exception {
		if(vtype == null) {
			throw new IllegalArgumentException("Invalid vtype: null");
		}
		else if(lvalue == null) {
			throw new IllegalArgumentException("Invalid lvalue: null");
		}
		else if(rvalue == null) {
			throw new IllegalArgumentException("Invalid rvalue: null");
		}
		else {
			this.vtype = vtype; 
			this.vlist = new SymbolExpression[] {
					StateValuations.evaluate(lvalue),
					StateValuations.evaluate(rvalue)
			};
		}
	}
	
	/* getters */
	/**
	 * @return	the category of the values hold in abstract execution state
	 */
	public CirValueClass	get_type() 	{ return this.vtype; }
	/**
	 * @return	whether the value is a unary operand
	 */
	public boolean			is_unary()	{ return this.vlist.length == 1; }
	/**
	 * @return the unary (first) operand preserved in the value
	 */
	public SymbolExpression	get_uvalue() { return this.vlist[0]; }
	/**
	 * @return whether the value contains binary operands
	 */
	public boolean			is_binary()	{ return this.vlist.length == 2; }
	/**
	 * @return the left (first) operand preserved in the value
	 */
	public SymbolExpression get_lvalue() { return this.vlist[0]; }
	/**
	 * @return the right (second) operand preserved in the value
	 */
	public SymbolExpression get_rvalue() { 
		if(this.vlist.length < 2) {
			return null;
		}
		else {
			return this.vlist[1];
		}
	}
	
	/* general */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.vtype.toString());
		buffer.append("(");
		if(this.is_unary()) {
			buffer.append(this.get_uvalue().toString());
		}
		else {
			buffer.append(this.get_lvalue().toString());
			buffer.append(", ");
			buffer.append(this.get_rvalue().toString());
		}
		buffer.append(")");
		return buffer.toString();
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CirStateValue) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	@Override
	public CirStateValue clone() {
		try {
			if(this.is_unary()) {
				return new CirStateValue(this.vtype, this.get_uvalue());
			}
			else {
				return new CirStateValue(this.vtype, this.get_lvalue(), this.get_rvalue());
			}
		}
		catch(Exception ex) {
			return this;
		}
	}
	
	/* factory */
	/**
	 * @param int_times {> 0}
	 * @return	cov_time(int_times)
	 * @throws Exception
	 */
	public static CirStateValue cov_time(int int_times) throws Exception {
		if(int_times <= 0) {
			throw new IllegalArgumentException("Invalid: " + int_times);
		}
		else {
			return new CirStateValue(CirValueClass.cov_stmt,
					SymbolFactory.sym_constant(Integer.valueOf(int_times)));
		}
	}
	/**
	 * @param condition
	 * @param value
	 * @return eva_cond(condition as value)
	 * @throws Exception
	 */
	public static CirStateValue eva_cond(Object condition, boolean value) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new CirStateValue(CirValueClass.eva_cond,
					SymbolFactory.sym_condition(condition, value));
		}
	}
	/**
	 * @param execute whether the statement should be executed or not
	 * @return	set_stmt(!execute, execute)
	 * @throws Exception
	 */
	public static CirStateValue set_stmt(boolean execute) throws Exception {
		return new CirStateValue(CirValueClass.set_stmt, 
				SymbolFactory.sym_constant(Boolean.valueOf(!execute)),
				SymbolFactory.sym_constant(Boolean.valueOf(execute)));
	}
	/**
	 * @param orig_target
	 * @param muta_target
	 * @return	set_flow(orig_exec, muta_exec)
	 * @throws Exception
	 */
	public static CirStateValue set_flow(CirExecution orig_target, CirExecution muta_target) throws Exception {
		if(orig_target == null) {
			throw new IllegalArgumentException("Invalid orig_target: null");
		}
		else if(muta_target == null) {
			throw new IllegalArgumentException("Invalid muta_target: null");
		}
		else {
			return new CirStateValue(CirValueClass.set_flow,
					SymbolFactory.sym_expression(orig_target),
					SymbolFactory.sym_expression(muta_target));
		}
	}
	/**
	 * @param execution
	 * @return	set_trap(execution, exception)
	 * @throws Exception
	 */
	public static CirStateValue set_trap(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirStateValue(CirValueClass.set_trap,
					SymbolFactory.sym_expression(execution),
					StateValuations.trap_value);
		}
	}
	/**
	 * @param ovalue
	 * @param mvalue
	 * @return set_expr(ovalue, mvalue)
	 * @throws Exception
	 */
	public static CirStateValue set_expr(SymbolExpression ovalue, SymbolExpression mvalue) throws Exception {
		return new CirStateValue(CirValueClass.set_expr, ovalue, mvalue);
	}
	/**
	 * @param base	the base value from which the value is differentiated
	 * @param diff	the difference to be incremented (+) to the base ones
	 * @return		inc_expr(base, diff)
	 * @throws Exception	
	 */
	public static CirStateValue inc_expr(SymbolExpression base, SymbolExpression diff) throws Exception {
		if(base == null) {
			throw new IllegalArgumentException("Invalid base: null");
		}
		else if(diff == null) {
			throw new IllegalArgumentException("Invalid diff: null");
		}
		else {
			return new CirStateValue(CirValueClass.inc_expr, base, diff);
		}
	}
	/**
	 * @param base	the base value from which the value is differentiated
	 * @param diff	the difference to be incremented (+) to the base ones
	 * @return		xor_expr(base, diff)
	 * @throws Exception	
	 */
	public static CirStateValue xor_expr(SymbolExpression base, SymbolExpression diff) throws Exception {
		if(base == null) {
			throw new IllegalArgumentException("Invalid base: null");
		}
		else if(diff == null) {
			throw new IllegalArgumentException("Invalid diff: null");
		}
		else {
			return new CirStateValue(CirValueClass.xor_expr, base, diff);
		}
	}
	
}
