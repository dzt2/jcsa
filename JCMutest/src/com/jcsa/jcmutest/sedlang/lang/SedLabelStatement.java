package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * label_statement |-- label :
 * @author yukimula
 *
 */
public class SedLabelStatement extends SedStatement {
	
	/* definition */
	protected SedLabelStatement(CirNode source) {
		super(source);
		this.add_child(new SedLabel(source, this.get_execution()));
	}
	/**
	 * @return the label of the statement tags
	 */
	public SedLabel get_label() { 
		return (SedLabel) this.get_child(0);
	}
	
	/* implementation */
	@Override
	protected String generate_content() throws Exception {
		return this.get_label().generate_code();
	}
	@Override
	protected SedNode copy_self() {
		return new SedLabelStatement(this.get_source());
	}

}
