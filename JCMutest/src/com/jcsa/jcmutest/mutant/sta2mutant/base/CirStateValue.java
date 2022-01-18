package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It models the value used to describe abstract execution state in mutation.
 * 
 * @author yukimula
 *
 */
class CirStateValue {
	
	/* definitions */
	/**	class	**/	private CirValueClass 		vtype;
	/**	values	**/	private SymbolExpression[]	vlist;
	private CirStateValue(CirValueClass vtype, SymbolExpression 
			loperand, SymbolExpression roperand) throws Exception {
		if(vtype == null) {
			throw new IllegalArgumentException("Invalid vtype: null");
		}
		else if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			this.vtype = vtype;
			this.vlist = new SymbolExpression[] {
				StateMutations.evaluate(loperand),
				StateMutations.evaluate(roperand)
			};
		}
	}
	
	/* getters */
	/**
	 * @return the category of the values hold in abstract execution state
	 */
	public CirValueClass	get_operator()	{ return this.vtype; }
	/**
	 * @return the left operand of the value
	 */
	public SymbolExpression	get_loperand()	{ return this.vlist[0]; } 
	/**
	 * @return the right operand of the value
	 */
	public SymbolExpression get_roperand()	{ return this.vlist[1]; }
	
	/* general */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.vtype.toString());
		buffer.append("(");
		buffer.append(this.get_loperand().toString());
		buffer.append(", ");
		buffer.append(this.get_roperand().toString());
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
			return new CirStateValue(this.vtype, 
					this.get_loperand(), this.get_roperand());
		}
		catch(Exception ex) {
			return null;
		}
	}
	
	/* factory */
	/**
	 * @param need_or_must	True {condition need be satisfied at least once} 
	 * 						False {condition should be satisfied every time}
	 * @param condition		symbolic expression as the condition being evaluated
	 * @return				cov_cond(must_or_never, condition)
	 * @throws Exception
	 */
	protected static CirStateValue cov_cond(boolean need_or_must, 
					SymbolExpression condition) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition; null");
		}
		else {
			return new CirStateValue(CirValueClass.cov_cond,
					SymbolFactory.sym_constant(need_or_must), condition);
		}
	}
	/**
	 * @param most_or_least	True {at most N times} or False {at least N times}
	 * @param int_times		the maximal or minimal number of executed times
	 * @return				cov_time(most_or_least, int_times)
	 * @throws Exception
	 */
	protected static CirStateValue cov_time(boolean most_or_least, int int_times) throws Exception {
		if(int_times < 0) {
			throw new IllegalArgumentException("Invalid times: " + int_times);
		}
		else {
			return new CirStateValue(CirValueClass.cov_time,
					SymbolFactory.sym_constant(most_or_least),
					SymbolFactory.sym_constant(int_times));
		}
	}
	/**
	 * @param orig_exec	True if the statement is executed in original version or not by False
	 * @param muta_exec	True if the statement is executed in mutation version or not by False
	 * @return			set_stmt(orig_exec, muta_exec)
	 * @throws Exception
	 */
	protected static CirStateValue set_stmt(boolean orig_exec, boolean muta_exec) throws Exception {
		return new CirStateValue(CirValueClass.set_stmt,
				SymbolFactory.sym_constant(orig_exec),
				SymbolFactory.sym_constant(muta_exec));
	}
	/**
	 * @param orig_target	the next statement being executed by follow in original version
	 * @param muta_target	the next statement being executed by follow in mutation version
	 * @return				set_flow(orig_target, muta_target)
	 * @throws Exception	
	 */
	protected static CirStateValue set_flow(CirExecution orig_target, CirExecution muta_target) throws Exception {
		if(orig_target == null) {
			throw new IllegalArgumentException("Invalid orig_target: null");
		}
		else if(muta_target == null) {
			throw new IllegalArgumentException("Invalid mtua_target: null");
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
	protected static CirStateValue set_trap(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirStateValue(CirValueClass.set_trap,
					SymbolFactory.sym_expression(execution),
					StateMutations.trap_value);
		}
	}
	/**
	 * @param orig_value	the original value to be replaced
	 * @param muta_value	the mutated value to replace with
	 * @return				set_expr(orig_value, muta_value)
	 * @throws Exception
	 */
	protected static CirStateValue set_expr(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			return new CirStateValue(CirValueClass.set_expr, orig_value, muta_value);
		}
	}
	/**
	 * @param base_value	the original basics to be increased
	 * @param difference	the difference to be increased into
	 * @return				inc_value(base_value, difference);
	 * @throws Exception	
	 */
	protected static CirStateValue inc_expr(SymbolExpression base_value, SymbolExpression difference) throws Exception {
		if(base_value == null) {
			throw new IllegalArgumentException("Invalid base_value: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else {
			return new CirStateValue(CirValueClass.inc_expr, base_value, difference);
		}
	}
	/**
	 * @param base_value	the original basics to be increased
	 * @param difference	the difference to be increased into
	 * @return				xor_value(base_value, difference);
	 * @throws Exception	
	 */
	protected static CirStateValue xor_expr(SymbolExpression base_value, SymbolExpression difference) throws Exception {
		if(base_value == null) {
			throw new IllegalArgumentException("Invalid base_value: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else {
			return new CirStateValue(CirValueClass.xor_expr, base_value, difference);
		}
	}
	
}
