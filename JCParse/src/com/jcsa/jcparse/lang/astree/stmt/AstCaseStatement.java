package com.jcsa.jcparse.lang.astree.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;

/**
 * <code><b>case</b> const_expr :</code>
 * 
 * @author yukimula
 *
 */
public interface AstCaseStatement extends AstStatement {
	public AstKeyword get_case();

	public AstConstExpression get_expression();

	public AstPunctuator get_colon();
}
