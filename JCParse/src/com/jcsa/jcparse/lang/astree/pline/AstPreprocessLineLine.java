package com.jcsa.jcparse.lang.astree.pline;

import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;

/**
 * <code><b># line</b> constant (literal)? \n</code>
 * 
 * @author yukimula
 */
public interface AstPreprocessLineLine extends AstPreprocessLine {
	public AstConstant get_line_constant();

	public boolean has_path_literal();

	public AstLiteral get_path_literal();
}
