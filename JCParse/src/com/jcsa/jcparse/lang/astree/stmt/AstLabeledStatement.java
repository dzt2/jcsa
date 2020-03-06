package com.jcsa.jcparse.lang.astree.stmt;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>label_stmt ---> label : </code>
 * 
 * @author yukimula
 *
 */
public interface AstLabeledStatement extends AstStatement {
	public AstLabel get_label();

	public AstPunctuator get_colon();
}
