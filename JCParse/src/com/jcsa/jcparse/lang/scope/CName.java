package com.jcsa.jcparse.lang.scope;

import com.jcsa.jcparse.lang.astree.base.AstIdentifier;

/**
 * A name in C program can be: <br>
 * <br>
 * 1. type name (user-defined) <br>
 * 2. object name (declared) <br>
 * 3. label name (labeled-stmt) <br>
 * 4. macro name (in #define) <br>
 * 5. enumerator (in enum definition) <br>
 * 6. field (in struct|union definition) <br>
 * <br>
 * A name is designated within a specific <i>scope</i> <br>
 *
 * @author yukimula
 */
public interface CName {

	/**
	 * linkage of each name
	 *
	 * @author yukimula
	 *
	 */
	public static enum CNameLinkage {
		extern_linkage, intern_linkage, no_linkage,
	}

	/**
	 * get the scope where the name is defined
	 *
	 * @return
	 */
	public CScope get_scope();

	/**
	 * the literal of this name
	 *
	 * @return
	 */
	public String get_name();

	/**
	 * node that defines this name (not its usage)
	 *
	 * @return
	 */
	public AstIdentifier get_source();

	/**
	 * set the source to this name refers
	 *
	 * @param node
	 */
	public void set_source(AstIdentifier node);

	/**
	 * get the linkage of this name
	 *
	 * @return
	 */
	public CNameLinkage get_linkage();

	/**
	 * set the linkage for this name
	 *
	 * @param linkage
	 * @return
	 * @throws Exception
	 */
	public void set_linkage(CNameLinkage linkage) throws Exception;

}
