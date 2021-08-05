package com.jcsa.jcparse.lang.astree.pline;

/**
 * <code><b># ifdef</b> identifier \n</code>
 *
 * @author yukimula
 */
public interface AstPreprocessIfdefLine extends AstPreprocessLine {
	public AstMacro get_macro();
}
