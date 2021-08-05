package com.jcsa.jcparse.lang.astree.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>default_stmt --> <b>default</b> : </code>
 *
 * @author yukimula
 *
 */
public interface AstDefaultStatement extends AstStatement {
	public AstKeyword get_default();

	public AstPunctuator get_colon();
}
