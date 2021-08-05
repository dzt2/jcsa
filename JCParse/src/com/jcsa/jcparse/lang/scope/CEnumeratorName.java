package com.jcsa.jcparse.lang.scope;

import com.jcsa.jcparse.lang.ctype.CEnumerator;

/**
 * name for enumerator in enum-type
 *
 * @author yukimula
 */
public interface CEnumeratorName extends CName {
	public CEnumerator get_enumerator();

	public void set_enumerator(CEnumerator e);
}
