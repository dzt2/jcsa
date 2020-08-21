package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
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
	
	/* implementation */
	@Override
	protected SedNode copy_self() {
		return new SedConstant(this.get_source(), this.constant);
	}
	@Override
	public String generate_code() throws Exception {
		return this.constant.toString();
	}
	
}
