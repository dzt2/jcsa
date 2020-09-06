package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

/**
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SymExpression							{data_type: CType}				<br>
 * 	|--	SymBasicExpression													<br>
 * 	|--	|--	SymIdentifier					{name: String}					<br>
 * 	|--	|--	SymConstant						{constant: CConstant}			<br>
 * 	|--	|--	SymLiteral						{literal: String}				<br>
 * 	|--	SymInitializerList													<br>
 * 	|--	SymFieldExpression													<br>
 * 	|--	SymCallExpression													<br>
 * 	|--	SymUnaryExpression					{-, ~, !, &, *, cast}			<br>
 * 	|--	SymBinaryExpression					{-, /, %, <<, >>, <, ..., >}	<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SymExpression extends SymNode {
	
	/* definition */
	private CType data_type;
	protected SymExpression(CType data_type) throws IllegalArgumentException {
		if(data_type == null)
			throw new IllegalArgumentException("Invalid data_type");
		else
			this.data_type = data_type;
	}
	
	/**
	 * @return the data type of the value of expression
	 */
	public CType get_data_type() { return this.data_type; }
	
	/**
	 * @return the AST-source of the expression or null
	 */
	public AstExpression get_ast_source() {
		Object source = this.get_source();
		if(source instanceof AstExpression)
			return (AstExpression) source;
		else
			return null;
	}
	
	/**
	 * @return the CIR-source of the expression or null
	 */
	public CirExpression get_cir_source() {
		Object source = this.get_source();
		if(source instanceof CirExpression)
			return (CirExpression) source;
		else
			return null;
	}
	
}
