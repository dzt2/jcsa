package com.jcsa.jcmutest.mutant.cir2mutant.stat;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It defines the abstract value (scope) used in differential analysis.
 * 
 * @author yukimula
 *
 */
final class CirValueScope {
	
	/* no-exception expression constructor */
	private static SymbolExpression new_expr(CType data_type, String name) {
		try {
			return SymbolFactory.identifier(data_type, name);
		}
		catch(Exception ex) {
			ex.printStackTrace(System.err);
			System.exit(-1);
			return null;
		}
	}
	
	/* scope-based abstract values declared */
	/** {true, false} **/
	protected static final SymbolExpression bool_value = new_expr(CBasicTypeImpl.bool_type, "#BoolScope");
	/** true **/
	protected static final SymbolExpression true_value = new_expr(CBasicTypeImpl.bool_type, "#TrueScope");
	/** false **/
	protected static final SymbolExpression fals_value = new_expr(CBasicTypeImpl.bool_type, "#FalsScope");
	/** any integer or real **/
	protected static final SymbolExpression numb_value = new_expr(CBasicTypeImpl.long_type, "#NumbScope");
	/** {x | x > 0} **/
	protected static final SymbolExpression post_value = new_expr(CBasicTypeImpl.long_type, "#PostScope");
	/** {x | x < 0} **/
	protected static final SymbolExpression negt_value = new_expr(CBasicTypeImpl.long_type, "#NegtScope");
	/** 0 **/
	protected static final SymbolExpression zero_value = new_expr(CBasicTypeImpl.long_type, "#ZeroScope");
	/** {x | x <= 0} **/
	protected static final SymbolExpression npos_value = new_expr(CBasicTypeImpl.long_type, "#NposScope");
	/** {x | x >= 0} **/
	protected static final SymbolExpression nneg_value = new_expr(CBasicTypeImpl.long_type, "#NnegScope");
	/** {x | x != 0} **/
	protected static final SymbolExpression nzro_value = new_expr(CBasicTypeImpl.long_type, "#NzroScope");
	/** any pointer **/
	protected static final SymbolExpression addr_value = new_expr(CBasicTypeImpl.long_type, "#AddrScope");
	/** null **/
	protected static final SymbolExpression null_value = new_expr(CBasicTypeImpl.long_type, "#NullScope");
	/** {x | x != null} **/
	protected static final SymbolExpression nnul_value = new_expr(CBasicTypeImpl.long_type, "#NnulScope");
	/** It denotes the exception value hold by store unit abstractly. **/
	protected static final SymbolExpression except_value = new_expr(CBasicTypeImpl.bool_type, "#Exception");
	
}
