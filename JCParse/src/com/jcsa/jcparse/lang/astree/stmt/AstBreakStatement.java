package com.jcsa.jcparse.lang.astree.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>break ;</code>
 *
 * @author yukimula
 *
 */
public interface AstBreakStatement extends AstStatement {
	public AstKeyword get_break();

	public AstPunctuator get_semicolon();
}
