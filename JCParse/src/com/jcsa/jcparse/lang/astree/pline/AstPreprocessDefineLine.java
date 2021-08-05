package com.jcsa.jcparse.lang.astree.pline;

/**
 * <code><b>#define</b> macro (id_list)? macro_body \n</code>
 *
 * @author yukimula
 */
public interface AstPreprocessDefineLine extends AstPreprocessLine {
	public AstMacro get_macro();

	public boolean has_id_list();

	public AstMacroList get_id_list();

	public AstMacroBody get_body();
}
