package com.jcsa.jcparse.lang.scope;

import java.util.Iterator;

import com.jcsa.jcparse.lang.astree.AstScopeNode;

/**
 * scope defines a space where names are defined or declared
 * 
 * @author yukimula
 */
public interface CScope {

	/**
	 * get the AST-node where the scope is based on
	 * 
	 * @return
	 */
	public AstScopeNode get_origin();

	/**
	 * set the origin for this scope
	 * 
	 * @param origin
	 * @throws Exception
	 */
	public void set_origin(AstScopeNode origin) throws Exception;

	/**
	 * get the parent scope
	 * 
	 * @return : null for lang_scope
	 */
	public CScope get_parent();

	/**
	 * get the parent scope where this scope is based on
	 * 
	 * @return
	 */
	public Iterator<CScope> get_children();

	/**
	 * create a new child-scope within this one
	 * 
	 * @return
	 * @throws Exception
	 */
	public CScope new_child() throws Exception;

	/**
	 * whether the child belongs to this scope or its parent
	 * 
	 * @param child
	 * @return
	 */
	public boolean has_child(CScope child);

	/**
	 * delete an existing child from current scope or its parent
	 * 
	 * @param child
	 * @return
	 * @throws Exception
	 */
	public boolean del_child(CScope child) throws Exception;

	/**
	 * get the table of names for this scope
	 * 
	 * @return
	 */
	public CNameTable get_name_table();

	/**
	 * whether there defined or declared a name in current scope or its parent
	 * 
	 * @param name
	 * @return
	 */
	public boolean has_name(String name);

	/**
	 * get the name defined or declared in local scope or its parent scope
	 * 
	 * @param name
	 * @return
	 */
	public CName get_name(String name) throws Exception;
}
