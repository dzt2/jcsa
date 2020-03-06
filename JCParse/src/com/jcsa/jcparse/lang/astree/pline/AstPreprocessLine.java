package com.jcsa.jcparse.lang.astree.pline;

import com.jcsa.jcparse.lang.astree.unit.AstExternalUnit;

/**
 * <code>pr_line --> pr_if_line | pr_ifdef_line | pr_ifndef_line |
 * 					 pr_elif_line | pr_else_line | pr_endif_line |
 * 					 pr_include_line | pr_define_line | pr_undef_line |
 * 					 pr_line_line | pr_error_line | pr_pragma_line | 
 * 					 pr_none_line</code>
 * 
 * @author yukimula
 *
 */
public interface AstPreprocessLine extends AstExternalUnit {
	/**
	 * get directive node
	 * 
	 * @return
	 */
	public AstDirective get_directive();
}
