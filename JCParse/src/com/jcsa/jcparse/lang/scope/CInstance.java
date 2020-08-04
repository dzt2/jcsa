package com.jcsa.jcparse.lang.scope;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.CStorageClass;

/**
 * instance of specified type
 * 
 * @author yukimula
 */
public interface CInstance {
	/**
	 * get the type of instance
	 * 
	 * @return
	 */
	public CType get_type();

	/**
	 * get the storage class of this instance
	 * 
	 * @return
	 */
	public CStorageClass get_storage_class();
}
