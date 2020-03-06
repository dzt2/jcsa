package com.jcsa.jcparse.lang.irlang.expr;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * expression |-- refer_expression | value_expression
 * @author yukimula
 *
 */
public interface CirExpression extends CirNode {
	/**
	 * get the data type hold by the expression
	 * @return
	 */
	public CType get_data_type();
	/**
	 * set the data type hold by the expression
	 * @param type
	 */
	public void set_data_type(CType type);
	/**
	 * get the statement where the expression defined
	 * @return
	 */
	public CirStatement statement_of();
}
