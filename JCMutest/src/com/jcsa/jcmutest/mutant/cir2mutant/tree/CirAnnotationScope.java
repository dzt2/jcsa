package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It defines the scope domains to describe scop_error in CirAnnotation.
 * 
 * @author yukimula
 *
 */
final class CirAnnotationScope {
	
	/* scope domain for boolean expression */
	private static SymbolExpression TRUE_Scope;
	private static SymbolExpression FALS_Scope;
	private static SymbolExpression BOOL_Scope;
	
	/* scope domain for integer or double expression */
	private static SymbolExpression POST_Scope;
	private static SymbolExpression ZERO_Scope;
	private static SymbolExpression NEGT_Scope;
	private static SymbolExpression NPOS_Scope;
	private static SymbolExpression NZRO_Scope;
	private static SymbolExpression NNEG_Scope;
	private static SymbolExpression NUMB_Scope;
	
	/* scope domain for address or pointer expression */
	private static SymbolExpression NULL_Scope;
	private static SymbolExpression INVP_Scope;
	private static SymbolExpression ADDR_Scope;
	
	/* initialization or definition block */
	static {
		try {
			TRUE_Scope = SymbolFactory.identifier(CBasicTypeImpl.bool_type, "True");
			FALS_Scope = SymbolFactory.identifier(CBasicTypeImpl.bool_type, "Fals");
			BOOL_Scope = SymbolFactory.identifier(CBasicTypeImpl.bool_type, "Bool");
			
			POST_Scope = SymbolFactory.identifier(CBasicTypeImpl.long_type, "Post");
			ZERO_Scope = SymbolFactory.identifier(CBasicTypeImpl.long_type, "Zero");
			NEGT_Scope = SymbolFactory.identifier(CBasicTypeImpl.long_type, "Negt");
			
			NPOS_Scope = SymbolFactory.identifier(CBasicTypeImpl.long_type, "NPos");
			NZRO_Scope = SymbolFactory.identifier(CBasicTypeImpl.long_type, "NZro");
			NNEG_Scope = SymbolFactory.identifier(CBasicTypeImpl.long_type, "NNeg");
			
			NULL_Scope = SymbolFactory.identifier(CBasicTypeImpl.long_type, "Null");
			INVP_Scope = SymbolFactory.identifier(CBasicTypeImpl.long_type, "Invp");
			
			NUMB_Scope = SymbolFactory.identifier(CBasicTypeImpl.long_type, "Numb");
			ADDR_Scope = SymbolFactory.identifier(CBasicTypeImpl.long_type, "Addr");
		} catch (Exception e) {
			e.printStackTrace();
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
	/**
	 * @param lscope
	 * @param rscope
	 * @return domain(lscope, rscope)
	 * @throws Exception
	 */
	protected static SymbolExpression get_cons_scope(Object lscope, Object rscope) throws Exception {
		CType ftype = (new CTypeFactory()).get_variable_function_type(CBasicTypeImpl.double_type);
		SymbolExpression function = SymbolFactory.identifier(ftype, "JcmutaDomain");
		List<Object> arguments = new ArrayList<Object>(); 
		arguments.add(lscope); arguments.add(rscope);
		return SymbolFactory.call_expression(function, arguments);
	}
	
}
