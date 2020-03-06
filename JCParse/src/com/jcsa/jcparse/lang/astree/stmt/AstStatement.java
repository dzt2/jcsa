package com.jcsa.jcparse.lang.astree.stmt;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * <code>
 * 	Statement --> 	expr_stmt | decl_stmt | compound_stmt |
 * 					case_stmt | default_stmt | label_stmt |
 * 					break_stmt | continue_stmt | ret_stmt | goto_stmt |
 * 					if_stmt | switch_stmt |
 * 					while_stmt | do_stmt | for_stmt |
 * </code>
 * 
 * @author yukimula
 */
public interface AstStatement extends AstNode {
}
