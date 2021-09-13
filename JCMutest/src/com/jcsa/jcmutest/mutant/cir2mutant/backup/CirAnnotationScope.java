package com.jcsa.jcmutest.mutant.cir2mutant.backup;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It defines the scope for abstract annotations of summarization
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
	static 
	{
		try 
		{
			TRUE_Scope = SymbolFactory.identifier(CBasicTypeImpl.bool_type, 	"TRUE_Scope");
			FALS_Scope = SymbolFactory.identifier(CBasicTypeImpl.bool_type, 	"FALS_Scope");
			
			POST_Scope = SymbolFactory.identifier(CBasicTypeImpl.double_type, 	"POST_Scope");
			ZERO_Scope = SymbolFactory.identifier(CBasicTypeImpl.double_type, 	"ZERO_Scope");
			NEGT_Scope = SymbolFactory.identifier(CBasicTypeImpl.double_type, 	"NEGT_Scope");
			
			NPOS_Scope = SymbolFactory.identifier(CBasicTypeImpl.double_type, 	"NPOS_Scope");
			NZRO_Scope = SymbolFactory.identifier(CBasicTypeImpl.double_type, 	"NZRO_Scope");
			NNEG_Scope = SymbolFactory.identifier(CBasicTypeImpl.double_type, 	"NNEG_Scope");
			
			NULL_Scope = SymbolFactory.identifier(CBasicTypeImpl.bool_type, 	"NULL_Scope");
			INVP_Scope = SymbolFactory.identifier(CBasicTypeImpl.bool_type, 	"INVP_Scope");
			
			BOOL_Scope = SymbolFactory.identifier(CBasicTypeImpl.bool_type, 	"BOOL_Scope");
			NUMB_Scope = SymbolFactory.identifier(CBasicTypeImpl.double_type, 	"NUMB_Scope");
			ADDR_Scope = SymbolFactory.identifier(CBasicTypeImpl.long_type, 	"ADDR_Scope");
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
	
	/* difference */
	private static final CTypeFactory type_factory = new CTypeFactory();
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return muta_value - orig_value
	 * @throws Exception
	 */
	protected static SymbolExpression sub_difference(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		return SymbolFactory.arith_sub(orig_value.get_data_type(), muta_value, orig_value);
	}
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return fabs(muta_value) - fabs(orig_value)
	 * @throws Exception
	 */
	protected static SymbolExpression ext_difference(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		CType type = type_factory.get_variable_function_type(CBasicTypeImpl.double_type);
		SymbolExpression function = SymbolFactory.identifier(type, "fabs");
		List<Object> arguments = new ArrayList<Object>();
		
		arguments.clear(); 
		arguments.add(muta_value);
		muta_value = SymbolFactory.call_expression(function, arguments);
		
		arguments.clear();
		arguments.add(orig_value);
		orig_value = SymbolFactory.call_expression(function, arguments);
		
		return SymbolFactory.arith_sub(CBasicTypeImpl.double_type, muta_value, orig_value);
	}
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return muta_value ^ orig_value
	 * @throws Exception
	 */
	protected static SymbolExpression xor_difference(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		return SymbolFactory.bitws_xor(orig_value.get_data_type(), muta_value, orig_value);
	}
	
}
