package com.jcsa.jcparse.lang.astree.expr;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.ctype.CType;

/**
 * Expression in C program could be one of the followings:<br>
 * 1. <i>basic expression</i>:
 * <code>AstIdExpression | AstConstant | AstLiteral</code> <br>
 * 2. <i>operational expression</i>:
 * <code>AstUnaryExpression | AstPostfixExpression | AstBinaryExpression | AstConditionalExpression </code>
 * <br>
 * 3. <i>specialized expression</i>:
 * <code>AstFunCallExpression | AstArrayExpression | AstFieldExpression | AstSizeofExpression | AstCastExpression </code>
 * <br>
 * 4. <i>compositional expression</i>:
 * <code>AstParanthExpression | AstConstExpression </code> <br>
 * 
 * @author yukimula
 */
public interface AstExpression extends AstNode {
	/**
	 * get the type of the expression
	 * 
	 * @return
	 */
	public CType get_value_type();

	/**
	 * set the type for this expression
	 * 
	 * @param type
	 */
	public void set_value_type(CType type);
}
