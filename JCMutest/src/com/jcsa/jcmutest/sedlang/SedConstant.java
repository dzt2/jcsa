package com.jcsa.jcmutest.sedlang;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * constant |-- {bool|char|short|int|long|float|double}
 * @author yukimula
 *
 */
public class SedConstant extends SedBasicExpression {
	
	/* definition */
	/** the constant of the value of this node **/
	private CConstant constant;
	protected SedConstant(CirNode source, CConstant constant) {
		super(source, constant.get_type());
		this.constant = constant;
	}
	
	/* getters */
	/**
	 * @return the constant of the value of this node
	 */
	public CConstant get_constant() { return this.constant; }
	
	@Override
	protected SedNode copy_self() {
		return new SedConstant(this.get_source(), this.constant);
	}
	
	@Override
	protected String generate_code() throws Exception {
		return this.constant.toString();
	}
	
}
