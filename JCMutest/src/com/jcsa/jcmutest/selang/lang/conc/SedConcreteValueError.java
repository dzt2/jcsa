package com.jcsa.jcmutest.selang.lang.conc;

import com.jcsa.jcmutest.selang.SedKeywords;
import com.jcsa.jcmutest.selang.lang.SedDescription;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.lang.tokn.SedKeyword;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	|--	SedConcreteValueError			{orig_expression: SedExpression}	<br>
 * 	|--	|--	SedChgExpressionError		{bool|char|sign|usig|real|addr|list}<br>
 * 	|--	|--	SedSetExpressionError		{bool|char|sign|usig|real|addr|list}<br>
 * 	|--	|--	SedAddExpressionError		{char|sign|usig|real|addr}			<br>
 * 	|--	|--	SedIncExpressionError		{char|sign|usig|real|addr}			<br>
 * 	|--	|--	SedDecExpressionError		{char|sign|usig|real|addr}			<br>
 * 	|--	|--	SedMulExpressionError		{char|sign|usig|real}				<br>
 * 	|--	|--	SedExtExpressionError		{char|sign|usig|real}				<br>
 * 	|--	|--	SedShkExpressionError		{char|sign|usig|real}				<br>
 * 	|--	|--	SedAndExpressionError		{char|sign|usig}					<br>
 * 	|--	|--	SedIorExpressionError		{char|sign|usig}					<br>
 * 	|--	|--	SedXorExpressionError		{char|sign|usig}					<br>
 * 	|--	|--	SedNegExpressionError		{char|sign|usig|real}				<br>
 * 	|--	|--	SedRsvExpressionError		{char|sign|usig|real}				<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SedConcreteValueError extends SedDescription {
	
	public static SedKeywords expression_type(CType data_type) throws Exception {
		if(data_type == null)
			return SedKeywords.cvoid;
		else if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_void:	return SedKeywords.cchar;
			case c_bool:	return SedKeywords.cbool;
			case c_char:
			case c_uchar:	return SedKeywords.cchar;
			case c_short:
			case c_int:
			case c_long:
			case c_llong:	return SedKeywords.csign;
			case c_ushort:
			case c_uint:
			case c_ulong:
			case c_ullong:	return SedKeywords.usign;
			case c_float:
			case c_double:
			case c_ldouble:	return SedKeywords.creal;
			default: 		return SedKeywords.clist;
			}
		}
		else if(data_type instanceof CArrayType
				|| data_type instanceof CPointerType
				|| data_type instanceof CFunctionType) {
			return SedKeywords.caddr;
		}
		else if(data_type instanceof CStructType
				|| data_type instanceof CUnionType) {
			return SedKeywords.clist;
		}
		else if(data_type instanceof CEnumType) {
			return SedKeywords.csign;
		}
		else if(data_type instanceof CQualifierType) {
			return expression_type(((CQualifierType) data_type).get_reference());
		}
		else {
			throw new IllegalArgumentException(data_type.generate_code());
		}
	}
	
	public SedConcreteValueError(CirStatement statement, SedKeywords 
			keyword, CirExpression orig_expression) throws Exception {
		super(statement, keyword);
		SedKeyword expression_type = new SedKeyword(
				expression_type(this.get_orig_expression().get_data_type()));
		this.add_child(expression_type);
		this.add_child(SedFactory.parse(orig_expression));
		if(!this.verify_expression_type(expression_type.get_keyword()))
			throw new IllegalArgumentException("Not available type: " + expression_type);
	}
	
	/**
	 * @param type
	 * @return determine whether the type is available for this type of error
	 */
	protected abstract boolean verify_expression_type(SedKeywords type);
	
	/**
	 * @return type of the expression being seeded with error
	 */
	public SedKeyword get_expression_type() {
		return (SedKeyword) this.get_child(2);
	}
	
	/**
	 * @return the original expression where error is seeded
	 */
	public SedExpression get_orig_expression() {
		return (SedExpression) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return "::" + this.get_expression_type() + 
				"(" + this.generate_follow_content() + ")";
	}
	
	/**
	 * @return the parameter part
	 * @throws Exception
	 */
	protected abstract String generate_follow_content() throws Exception;

}
