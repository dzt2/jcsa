package com.jcsa.jcparse.lang;

import com.jcsa.jcparse.lang.text.CLocalable;
import com.jcsa.jcparse.lang.text.CLocation;

/**
 * abstract element for C syntax
 * 
 * @author yukimula
 *
 */
public abstract class CSyntaxElmImpl implements CLocalable {

	/** location of this element to the source text **/
	protected CLocation location;

	/**
	 * abstract-constructor ...
	 */
	protected CSyntaxElmImpl() {
		this.location = null;
	}

	@Override
	public CLocation get_location() {
		return location;
	}

	@Override
	public void set_location(CLocation loc) {
		this.location = loc;
	}

}
