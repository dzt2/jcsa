package com.jcsa.jcmutest.mutant.txt2mutant;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * The mutation describes how the code of mutant is constructed, based on
 * following syntactic rules:<br>
 * 
 * prev_code
 * muta_code
 * {comment}
 * post_code
 * 
 * @author yukimula
 *
 */
class TextMutation {
	
	/* definition */
	/** the location in which the code is mutated (while the others are not) **/
	private AstNode location;
	/** the code to replace the original code specified in range of location **/
	private String muta_code;
	protected TextMutation(AstNode location, String muta_code) throws Exception {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else if(muta_code == null)
			throw new IllegalArgumentException("Invalid muta_code: null");
		else {
			this.location = location;
			this.muta_code = muta_code;
		}
	}
	
	/* getters */
	/**
	 * @return the location in which the code is mutated (while the others are not)
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return the code to replace the original code specified in range of location
	 */
	public String get_muta_code() { return this.muta_code; }
	
}
