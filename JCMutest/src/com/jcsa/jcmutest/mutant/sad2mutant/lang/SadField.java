package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * field |-- {name: String}
 * 
 * @author yukimula
 *
 */
public class SadField extends SadToken {
	
	/** the name of the field **/
	private String name;
	protected SadField(CirNode source, String name) {
		super(source);
		this.name = name;
	}
	
	/**
	 * @return the name of the field
	 */
	public String get_name() { return this.name; }
	
	@Override
	public String generate_code() throws Exception {
		return this.name;
	}
	
	@Override
	protected SadNode clone_self() {
		return new SadField(this.get_cir_source(), this.name);
	}
	
}
