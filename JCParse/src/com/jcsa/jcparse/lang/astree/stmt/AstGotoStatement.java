package com.jcsa.jcparse.lang.astree.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>gogo_stmt --> <b>goto</b> label ;</code>
 * 
 * @author yukimula
 *
 */
public interface AstGotoStatement extends AstStatement {
	public AstKeyword get_goto();

	public AstLabel get_label();

	public AstPunctuator get_semicolon();
}
