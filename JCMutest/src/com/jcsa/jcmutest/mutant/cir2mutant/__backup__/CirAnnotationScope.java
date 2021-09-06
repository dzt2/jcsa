package com.jcsa.jcmutest.mutant.cir2mutant.__backup__;

import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It defines the value scopes (domains) used in scop_error CirAnnotation.
 * 
 * @author yukimula
 *
 */
class CirAnnotationScope {
	
	/* boolean scope definitions */
	private static SymbolExpression TRUE_Scope;
	private static SymbolExpression FALS_Scope;
	/* numeric scope definitions */
	private static SymbolExpression POST_Scope;
	private static SymbolExpression ZERO_Scope;
	private static SymbolExpression NEGT_Scope;
	/* numeric scope definitions */
	private static SymbolExpression NPOS_Scope;
	private static SymbolExpression NZRO_Scope;
	private static SymbolExpression NNEG_Scope;
	/* pointer scope definitions */
	private static SymbolExpression NULL_Scope;
	private static SymbolExpression INVP_Scope;
	/* abstract scope definitions */
	private static SymbolExpression BOOL_Scope;
	private static SymbolExpression NUMB_Scope;
	private static SymbolExpression ADDR_Scope;
	
	/* definition construction */
	static {
		try {
			TRUE_Scope = SymbolFactory.identifier(CBasicTypeImpl.bool_type, 	"True");
			FALS_Scope = SymbolFactory.identifier(CBasicTypeImpl.bool_type, 	"Fals");
			
			POST_Scope = SymbolFactory.identifier(CBasicTypeImpl.double_type, 	"Post");
			ZERO_Scope = SymbolFactory.identifier(CBasicTypeImpl.double_type, 	"Zero");
			NEGT_Scope = SymbolFactory.identifier(CBasicTypeImpl.double_type, 	"Negt");
			
			NPOS_Scope = SymbolFactory.identifier(CBasicTypeImpl.double_type, 	"NPos");
			NZRO_Scope = SymbolFactory.identifier(CBasicTypeImpl.double_type, 	"NZro");
			NNEG_Scope = SymbolFactory.identifier(CBasicTypeImpl.double_type, 	"NNeg");
			
			NULL_Scope = SymbolFactory.identifier(CBasicTypeImpl.bool_type, 	"Null");
			INVP_Scope = SymbolFactory.identifier(CBasicTypeImpl.bool_type, 	"Invp");
			
			BOOL_Scope = SymbolFactory.identifier(CBasicTypeImpl.bool_type, 	"Bool");
			NUMB_Scope = SymbolFactory.identifier(CBasicTypeImpl.double_type, 	"Numb");
			ADDR_Scope = SymbolFactory.identifier(CBasicTypeImpl.long_type, 	"Addr");
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}
	
	/* getters */
	/**
	 * @return {true}
	 */
	protected static SymbolExpression get_true_scope() { return TRUE_Scope; }
	/**
	 * @return {false}
	 */
	protected static SymbolExpression get_fals_scope() { return FALS_Scope; }
	/**
	 * @return {true, false}
	 */
	protected static SymbolExpression get_bool_scope() { return BOOL_Scope; }
	/**
	 * @return {x | x > 0 }
	 */
	protected static SymbolExpression get_post_scope() { return POST_Scope; }
	/**
	 * @return {0}
	 */
	protected static SymbolExpression get_zero_scope() { return ZERO_Scope; }
	/**
	 * @return {x | x < 0}
	 */
	protected static SymbolExpression get_negt_scope() { return NEGT_Scope; }
	/**
	 * @return {x | x <= 0}
	 */
	protected static SymbolExpression get_npos_scope() { return NPOS_Scope; }
	/**
	 * @return {x | x != 0}
	 */
	protected static SymbolExpression get_nzro_scope() { return NZRO_Scope; }
	/**
	 * @return {x | x >= 0}
	 */
	protected static SymbolExpression get_nneg_scope() { return NNEG_Scope; }
	/**
	 * @return {null}
	 */
	protected static SymbolExpression get_null_scope() { return NULL_Scope; }
	/**
	 * @return { p | p != null }
	 */
	protected static SymbolExpression get_invp_scope() { return INVP_Scope; }
	/**
	 * @return {x | x \in R }
	 */
	protected static SymbolExpression get_numb_scope() { return NUMB_Scope; }
	/**
	 * @return { p | p \in P }
	 */
	protected static SymbolExpression get_addr_scope() { return ADDR_Scope; }
	
}
