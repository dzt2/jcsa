package com.jcsa.jcmutest.mutant.cir2mutant.path;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It defines the abstract value used to summarize symbolic evaluation results.
 * 
 * @author yukimula
 *
 */
final class CirValueScope {
	
	/* supporting method */
	/**
	 * @param data_type
	 * @param name
	 * @return the symbolic identifier to represent the variable of specified name
	 */
	private static SymbolExpression new_expr(CType data_type, String name) {
		return SymbolFactory.variable(data_type, name);
	}
	
	/* scope-based abstract values declared */
	/** {true, false} **/
	protected static final SymbolExpression bool_value = new_expr(CBasicTypeImpl.bool_type, "@BoolScope");
	/** true **/
	protected static final SymbolExpression true_value = new_expr(CBasicTypeImpl.bool_type, "@TrueScope");
	/** false **/
	protected static final SymbolExpression fals_value = new_expr(CBasicTypeImpl.bool_type, "@FalsScope");
	/** any integer or real **/
	protected static final SymbolExpression numb_value = new_expr(CBasicTypeImpl.long_type, "@NumbScope");
	/** {x | x > 0} **/
	protected static final SymbolExpression post_value = new_expr(CBasicTypeImpl.long_type, "@PostScope");
	/** {x | x < 0} **/
	protected static final SymbolExpression negt_value = new_expr(CBasicTypeImpl.long_type, "@NegtScope");
	/** 0 **/
	protected static final SymbolExpression zero_value = new_expr(CBasicTypeImpl.long_type, "@ZeroScope");
	/** {x | x <= 0} **/
	protected static final SymbolExpression npos_value = new_expr(CBasicTypeImpl.long_type, "@NposScope");
	/** {x | x >= 0} **/
	protected static final SymbolExpression nneg_value = new_expr(CBasicTypeImpl.long_type, "@NnegScope");
	/** {x | x != 0} **/
	protected static final SymbolExpression nzro_value = new_expr(CBasicTypeImpl.long_type, "@NzroScope");
	/** any pointer **/
	protected static final SymbolExpression addr_value = new_expr(CBasicTypeImpl.long_type, "@AddrScope");
	/** null **/
	protected static final SymbolExpression null_value = new_expr(CBasicTypeImpl.long_type, "@NullScope");
	/** {x | x != null} **/
	protected static final SymbolExpression nnul_value = new_expr(CBasicTypeImpl.long_type, "@NnulScope");
	/** It denotes the exception value hold by store unit abstractly. **/
	protected static final SymbolExpression expt_value = new_expr(CBasicTypeImpl.bool_type, "@Exception");
	
}
