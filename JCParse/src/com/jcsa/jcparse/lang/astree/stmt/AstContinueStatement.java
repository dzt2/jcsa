package com.jcsa.jcparse.lang.astree.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>continue ;</code>
 * 
 * @author yukimula
 *
 */
public interface AstContinueStatement extends AstStatement {
	public AstKeyword get_continue();

	public AstPunctuator get_semicolon();
}
