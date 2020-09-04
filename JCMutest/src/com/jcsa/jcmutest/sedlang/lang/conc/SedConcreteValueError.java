package com.jcsa.jcmutest.sedlang.lang.conc;

import com.jcsa.jcmutest.sedlang.SedExpressionType;
import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcmutest.sedlang.lang.dess.SedDescription;
import com.jcsa.jcmutest.sedlang.lang.expr.SedExpression;
import com.jcsa.jcmutest.sedlang.util.SedParser;
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
	
	public static SedExpressionType expression_type(CType data_type) throws Exception {
		if(data_type == null)
			return SedExpressionType.cvoid;
		else if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_void:	return SedExpressionType.cchar;
			case c_bool:	return SedExpressionType.cbool;
			case c_char:
			case c_uchar:	return SedExpressionType.cchar;
			case c_short:
			case c_int:
			case c_long:
			case c_llong:	return SedExpressionType.csign;
			case c_ushort:
			case c_uint:
			case c_ulong:
			case c_ullong:	return SedExpressionType.usign;
			case c_float:
			case c_double:
			case c_ldouble:	return SedExpressionType.creal;
			default: 		return SedExpressionType.clist;
			}
		}
		else if(data_type instanceof CArrayType
				|| data_type instanceof CPointerType
				|| data_type instanceof CFunctionType) {
			return SedExpressionType.caddr;
		}
		else if(data_type instanceof CStructType
				|| data_type instanceof CUnionType) {
			return SedExpressionType.clist;
		}
		else if(data_type instanceof CEnumType) {
			return SedExpressionType.csign;
		}
		else if(data_type instanceof CQualifierType) {
			return expression_type(((CQualifierType) data_type).get_reference());
		}
		else {
			throw new IllegalArgumentException(data_type.generate_code());
		}
	}

	private SedExpressionType expression_type;
	public SedConcreteValueError(CirStatement statement, SedKeywords 
			keyword, CirExpression orig_expression) throws Exception {
		super(statement, keyword);
		this.add_child(SedParser.parse(orig_expression));
		this.expression_type = expression_type(this.get_orig_expression().get_data_type());
		if(!this.verify_expression_type(this.expression_type))
			throw new IllegalArgumentException("Not available type: " + expression_type);
	}
	
	/**
	 * @param type
	 * @return determine whether the type is available for this type of error
	 */
	protected abstract boolean verify_expression_type(SedExpressionType type);
	
	/**
	 * @return the original expression where error is seeded
	 */
	public SedExpression get_orig_expression() {
		return (SedExpression) this.get_child(2);
	}
	
	/**
	 * @return type of the expression being seeded with error
	 */
	public SedExpressionType get_orig_expression_type() {
		return this.expression_type;
	}

	@Override
	protected String generate_content() throws Exception {
		return this.expression_type + ", " + this.generate_follow_content();
	}
	
	protected abstract String generate_follow_content() throws Exception;

}
