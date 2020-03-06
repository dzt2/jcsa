package com.jcsa.jcparse.lopt.analysis.flow;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

/**
 * Data dependence is defined as:<br>
 * <code>(source, target, variable)</code><br>
 * in which source directly data depends on target, such that source uses
 * some variable that is defined within the target via variable.
 * @author yukimula
 *
 */
public class CDependenceDataEdge extends CDependenceEdge {
	
	/** whether the data dependence represents that a definition relies on a usage **/
	private boolean is_define_use;
	/** the variable that data depends on another in the target statement instance **/
	private CirExpression source_reference;
	/** the variable that data depended by another in the source statement instance **/
	private CirExpression target_reference;
	/**
	 * create a data dependence from source to the target via reference specified and
	 * specify whether it is from definition point to the usage or the reverse.
	 * @param source
	 * @param target
	 * @param is_define_use
	 * @param reference
	 * @throws Exception
	 */
	protected CDependenceDataEdge(
			CDependenceNode source, CDependenceNode target, boolean is_define_use, 
			CirExpression source_reference, CirExpression target_reference) throws Exception {
		super(source, target);
		if(source_reference == null)
			throw new IllegalArgumentException("invalid source_reference: null");
		else if(target_reference == null)
			throw new IllegalArgumentException("invalid target_reference: null");
		else { 
			this.is_define_use = is_define_use;
			this.source_reference = source_reference;
			this.target_reference = target_reference;
		}
	}
	
	/**
	 * Definition data depends on usage when the latter is used to determine
	 * the value of the former.
	 * @return
	 */
	public boolean is_define_use() { return this.is_define_use; }
	/**
	 * Usage data depends on definition when the latter defines the last value
	 * hold by the former at that point.
	 * @return
	 */
	public boolean is_use_define() { return !this.is_define_use; }
	/**
	 * get the reference in source statement that data depends on another
	 * @return
	 */
	public CirExpression get_source_reference() { return this.source_reference; }
	/**
	 * get the reference in target statement that is data depended by another
	 * @return
	 */
	public CirExpression get_target_reference() { return this.target_reference; }
	
}
