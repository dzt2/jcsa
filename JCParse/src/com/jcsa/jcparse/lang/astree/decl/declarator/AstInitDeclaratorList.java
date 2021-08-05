package com.jcsa.jcparse.lang.astree.decl.declarator;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>init_decl_list --> init_declarator (, init_declarator)*</code>
 *
 * @author yukimula
 */
public interface AstInitDeclaratorList extends AstNode {
	public int number_of_init_declarators();

	public AstPunctuator get_comma(int k);

	public AstInitDeclarator get_init_declarator(int k);

	public void append_init_declarator(AstPunctuator comma, AstInitDeclarator decl) throws Exception;
}
