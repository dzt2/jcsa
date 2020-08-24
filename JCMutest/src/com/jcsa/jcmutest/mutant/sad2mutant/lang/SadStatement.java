package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * statement |-- label : xxx
 * @author yukimula
 *
 */
public abstract class SadStatement extends SadNode {
	
	protected SadStatement(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the label of the statement node
	 */
	public SadLabel get_source_label() { 
		return (SadLabel) this.get_child(0); 
	}
	
	@Override
	public String generate_code() throws Exception {
		return this.get_source_label().generate_code() + 
						": " + this.generate_content();
	}
	
	protected abstract String generate_content() throws Exception;
	
}
