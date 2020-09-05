package com.jcsa.jcmutest.selang.lang.expr;

import com.jcsa.jcmutest.selang.lang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

/**
 * <code>
 * 	SedExpression						{cir_expression: CirExpression}		<br>
 * 	|--	SedBasicExpression													<br>
 * 	|--	|--	SedIdExpression				{name: String}						<br>
 * 	|--	|--	SedConstant					{bool|char|int|long|float|double}	<br>
 * 	|--	|--	SedLiteral					{literal: String}					<br>
 * 	|--	|--	SedDefaultValue													<br>
 * 	|--	SedUnaryExpression				{-, ~, !, &, *, cast}				<br>
 * 	|--	SedBinaryExpression				[+, -, *, /, ..., ==, !=]			<br>
 * 	|--	SedFieldExpression													<br>
 * 	|--	SedInitializerList													<br>
 * 	|--	SedCallExpression													<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SedExpression extends SedNode {
	
	/* definitions */
	private CType data_type;
	private CirExpression cir_expression;
	public SedExpression(CirExpression cir_expression, CType data_type) throws Exception {
		if(data_type == null)
			throw new IllegalArgumentException("Invalid data_type");
		else {
			this.data_type = data_type;
			this.cir_expression = cir_expression;
		}
	}
	
	/**
	 * @return the data type of the value of the expression
	 */
	public CType get_data_type() { return this.data_type; }
	
	/**
	 * @return whether there is a source of the expression
	 */
	public boolean has_cir_expression() { return this.cir_expression != null; }
	
	/**
	 * @return the cir-source of this expression describes
	 */
	public CirExpression get_cir_expression() { return this.cir_expression; }
	/**
	 * reset the expression source and data type of the node
	 * @param cir_expression
	 * @param data_type
	 * @throws Exception
	 */
	public void set_cir_expression(CirExpression cir_expression, CType data_type) throws Exception {
		if(data_type == null)
			throw new IllegalArgumentException("Invalid data_type");
		else {
			this.data_type = data_type;
			this.cir_expression = cir_expression;
		}
	}
	
}
