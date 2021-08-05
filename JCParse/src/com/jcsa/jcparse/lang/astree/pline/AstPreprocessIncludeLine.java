package com.jcsa.jcparse.lang.astree.pline;

/**
 * <code><b># include</b> header \n</code>
 *
 * @author yukimula
 */
public interface AstPreprocessIncludeLine extends AstPreprocessLine {
	public AstHeader get_header();
}
