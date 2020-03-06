package com.jcsa.jcparse.lang.astree.pline;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * node to represent header (system or user-defined)
 * 
 * @author yukimula
 */
public interface AstHeader extends AstNode {
	public boolean is_system();

	public boolean is_user_define();

	public String get_path();
}
