package com.jcsa.jcparse.lang.astree.pline;

/**
 * <code><b># error</b> (token)* \n</code>
 *
 * @author yukimula
 *
 */
public interface AstPreprocessErrorLine extends AstPreprocessLine {
	public AstMacroBody get_body();
}
