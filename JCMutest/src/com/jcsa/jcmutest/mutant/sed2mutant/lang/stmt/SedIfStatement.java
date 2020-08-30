package com.jcsa.jcmutest.mutant.sed2mutant.lang.stmt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * label: if(condition) then label else label;
 * @author yukimula
 *
 */
public class SedIfStatement extends SedStatement {

	public SedIfStatement(CirNode cir_source) {
		super(cir_source);
	}
	
	/**
	 * @return the condition to decide the branch
	 */
	public SedExpression get_condition() {
		return (SedExpression) this.get_child(1);
	}
	/**
	 * @return the label of the true branch
	 */
	public SedLabel get_true_label() {
		return (SedLabel) this.get_child(2);
	}
	/**
	 * @return the label of the false branch
	 */
	public SedLabel get_false_label() {
		return (SedLabel) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return "if(" + this.get_condition().generate_code() + ")"
				+ " then " + this.get_true_label().generate_code()
				+ " else " + this.get_false_label().generate_code() + ";";
	}

	@Override
	protected SedNode clone_self() {
		return new SedIfStatement(this.get_cir_source());
	}
	
}
