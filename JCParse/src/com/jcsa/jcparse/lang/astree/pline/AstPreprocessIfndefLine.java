package com.jcsa.jcparse.lang.astree.pline;

/**
 * <code><b># ifndef</b> identifier \n</code>
 * 
 * @author yukimula
 */
public interface AstPreprocessIfndefLine extends AstPreprocessLine {
	public AstMacro get_macro();
}
