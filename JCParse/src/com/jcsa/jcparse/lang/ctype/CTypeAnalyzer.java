package com.jcsa.jcparse.lang.ctype;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.ctype.CBasicType.CBasicTypeTag;
import com.jcsa.jcparse.lang.lexical.CKeyword;

/**
 * To analyze the CType
 * @author yukimula
 */
public class CTypeAnalyzer {

	public static CType get_value_type(CType type) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("invalid type: null");
		else {
			while(type instanceof CQualifierType) {
				type = ((CQualifierType) type).get_reference();
			}
			return type;
		}
	}

	public static boolean is_void(CType type) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("invalid type: null");
		else if(type instanceof CBasicType)
			return ((CBasicType) type).get_tag() == CBasicTypeTag.c_void;
		else return false;
	}
	public static boolean is_boolean(CType type) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("invalid type: null");
		else if(type instanceof CBasicType)
			return ((CBasicType) type).get_tag() == CBasicTypeTag.c_bool;
		else return false;
	}
	public static boolean is_character(CType type) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("invalid type: null");
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_char:
			case c_uchar: 	return true;
			default:		return false;
			}
		}
		else return false;
	}
	public static boolean is_integer(CType type) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("invalid type: null");
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_char:
			case c_uchar:
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:	return true;
			default:		return false;
			}
		}
		else if(type instanceof CEnumType) return true;
		else return false;
	}
	public static boolean is_real(CType type) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("invalid type: null");
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_float:
			case c_double:
			case c_ldouble:	return true;
			default:		return false;
			}
		}
		else return false;
	}
	public static boolean is_number(CType type) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("invalid type: null");
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_char:
			case c_uchar:
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
			case c_float:
			case c_double:
			case c_ldouble: return true;
			default:		return false;
			}
		}
		else if(type instanceof CEnumType) return true;
		else return false;
	}
	public static boolean is_signed(CType type) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("invalid type: null");
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_char:
			case c_short:
			case c_int:
			case c_long:
			case c_llong:
			case c_float:
			case c_double:
			case c_ldouble: return true;
			default:		return false;
			}
		}
		else if(type instanceof CEnumType) return true;
		else return false;
	}
	public static boolean is_unsigned(CType type) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("invalid type: null");
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:
			case c_uchar:
			case c_ushort:
			case c_uint:
			case c_ulong:
			case c_ullong:	return true;
			default:		return false;
			}
		}
		else return false;
	}
	public static boolean is_pointer(CType type) throws Exception {
		return type instanceof CArrayType || type instanceof CPointerType;
	}
	public static boolean is_keyword(AstNode node, CKeyword keyword) throws Exception {
		if(node instanceof AstKeyword) {
			return ((AstKeyword) node).get_keyword() == keyword;
		}
		else return false;
	}
	public static boolean is_complex(CType type) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("invalid type: null");
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_float_complex:
			case c_double_complex:
			case c_ldouble_complex:	return true;
			default: 				return false;
			}
		}
		else {
			return false;
		}
	}

	public static AstExpression get_expression_of(AstExpression expr) throws Exception {
		if(expr == null)
			throw new IllegalArgumentException("invalid expr: null");
		else {
			while(true) {
				if(expr instanceof AstParanthExpression)
					expr = ((AstParanthExpression) expr).get_sub_expression();
				else if(expr instanceof AstConstExpression)
					expr = ((AstConstExpression) expr).get_expression();
				else break;
			}
			return expr;
		}
	}
	public static AstExpression get_expression_of(AstInitializer initializer) throws Exception {
		if(initializer.is_body()) return initializer.get_body();
		else return get_expression_of(initializer.get_expression());
	}
	public static AstNode get_parent_of_expression(AstExpression expr) throws Exception {
		if(expr == null)
			throw new IllegalArgumentException("invalid expr: null");
		else {
			AstNode parent = expr.get_parent();
			while(true) {
				if(parent instanceof AstParanthExpression
						|| parent instanceof AstConstExpression)
					parent = parent.get_parent();
				else break;
			}
			return parent;
		}
	}

}
