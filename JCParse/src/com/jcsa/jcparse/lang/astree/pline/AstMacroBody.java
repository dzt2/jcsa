package com.jcsa.jcparse.lang.astree.pline;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.ctoken.CToken;

/**
 * <code>macro_body ---> (ctoken)*</code>
 * 
 * @author yukimula
 *
 */
public interface AstMacroBody extends AstNode {
	public int number_of_tokens();

	public CToken get_token(int k);

	public void append_token(CToken tok) throws Exception;
}
