package com.jcsa.jcparse.lang.astree.pline;

/**
 * <code><b># pragma</b> (token)* \n</code>
 *
 * @author yukimula
 */
public interface AstPreprocessPragmaLine extends AstPreprocessLine {
	public AstMacroBody get_body();
}
