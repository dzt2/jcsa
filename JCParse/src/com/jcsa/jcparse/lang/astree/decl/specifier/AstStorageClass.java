package com.jcsa.jcparse.lang.astree.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;

/**
 * <code>storage_class_spec |--> <b>typedef | auto | restrict | static | extern </b></code>
 * 
 * @author yukimula
 *
 */
public interface AstStorageClass extends AstSpecifier {
	/**
	 * get keyword of the storage class specifier
	 * 
	 * @return
	 */
	public AstKeyword get_keyword();
}
