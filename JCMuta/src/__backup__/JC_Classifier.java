package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CBasicType.CBasicTypeTag;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.lexical.CTypeQualifier;

/**
 * Provide basic APIs for other methods to classify or identify 
 * necessary information from the AST structure (and type)
 * @author yukimula
 */
public class JC_Classifier {
	
	/* type getters */
	/**
	 * get the value type
	 * @param type
	 * @return
	 */
	public static CType get_value_type(CType type) {
		while(type instanceof CQualifierType) 
			type = ((CQualifierType) type).get_reference();
		return type;
	}
	
	/* type classifier */
	/**
	 * whether the type is _Bool
	 * @param type
	 * @return
	 */
	public static boolean is_boolean_type(CType type) {
		if(type instanceof CBasicType) {
			return ((CBasicType) type).get_tag() == CBasicTypeTag.c_bool;
		}
		else return false;
	}
	/**
	 * <code>char, short, int, long, long long, enum</code>
	 * @param type
	 * @return
	 */
	public static boolean is_integer_type(CType type) {
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_char: 	case c_uchar:
			case c_short:	case c_ushort:
			case c_int:		case c_uint:
			case c_long:	case c_ulong:
			case c_llong: 	case c_ullong:
				return true;
			default: return false;
			}
		}
		else if(type instanceof CEnumType)
			return true;
		else return false;
	}
	/**
	 * <code>char | uchar</code>
	 * @param type
	 * @return
	 */
	public static boolean is_character_type(CType type) {
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_char: 	case c_uchar:
				return true;
			default: return false;
			}
		}
		else if(type instanceof CEnumType)
			return true;
		else return false;
	}
	/**
	 * <code>long | llong</code>
	 * @param type
	 * @return
	 */
	public static boolean is_long_integer(CType type) {
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_long:	case c_ulong:
			case c_llong: 	case c_ullong:
				return true;
			default: return false;
			}
		}
		else return false;
	}
	/**
	 * <code>float, double, long double</code>
	 * @param type
	 * @return
	 */
	public static boolean is_real_type(CType type) {
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_float:
			case c_double:
			case c_ldouble:
				return true;
			default: return false;
			}
		}
		else return false;
	}
	/**
	 * array | function | pointer
	 * @param type
	 * @return
	 */
	public static boolean is_address_type(CType type) {
		return (type instanceof CArrayType) 
				|| (type instanceof CFunctionType)
				|| (type instanceof CPointerType);
	}
	/**
	 * <code>uchar, ushort, uint, ulong, ullong</code>
	 * @param type
	 * @return
	 */
	public static boolean is_unsigned_type(CType type) {
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_uchar:
			case c_ushort:
			case c_uint:
			case c_ulong:
			case c_ullong:
				return true;
			default: return false;
			}
		}
		else return false;
	}
	/**
	 * whether the type has const-qualifier
	 * @param type
	 * @return
	 */
	public static boolean is_const_type(CType type) {
		while(type instanceof CQualifierType) {
			CTypeQualifier qualifier = ((CQualifierType) type).get_qualifier();
			if(qualifier == CTypeQualifier.c_const) return true;
			else type = ((CQualifierType) type).get_reference();
		}
		return false;
	}
	/**
	 * whether two types are equivalent
	 * @param type1
	 * @param type2
	 * @return
	 */
	public static boolean is_equal_types(CType type1, CType type2) {
		if(type1 instanceof CBasicType && type2 instanceof CBasicType) {
			return ((CBasicType)type1).get_tag() == ((CBasicType)type2).get_tag();
		}
		else if(type1 instanceof CPointerType && type2 instanceof CPointerType) {
			CType ptype1 = ((CPointerType)type1).get_pointed_type();
			CType ptype2 = ((CPointerType)type2).get_pointed_type();
			//ptype1 = JC_Classifier.get_value_type(ptype1);
			//ptype2 = JC_Classifier.get_value_type(ptype2);
			return is_equal_types(ptype1, ptype2);
		}
		else if(type1 instanceof CArrayType && type2 instanceof CArrayType) {
			CType etype1 = ((CArrayType)type1).get_element_type();
			CType etype2 = ((CArrayType)type2).get_element_type();
			return is_equal_types(etype1, etype2);
		}
		else if(type1 instanceof CQualifierType && type2 instanceof CQualifierType) {
			if(is_const_type(type1) == is_const_type(type2)) {
				type1 = JC_Classifier.get_value_type(type1);
				type2 = JC_Classifier.get_value_type(type2);
				return is_equal_types(type1, type2);
			}
			else return false;
		}
		else return type1 == type2;
	}
	
	/* expression classifier */
	/**
	 * identifier | a[k] | *ptr | st.field | st->field
	 * @param expr
	 * @return
	 */
	public static boolean is_access_path(AstExpression expr) {
		if((expr instanceof AstIdExpression)
			|| (expr instanceof AstArrayExpression)
			|| (expr instanceof AstFieldExpression)
			|| (expr instanceof AstPointUnaryExpression))
			return true;
		else if(expr instanceof AstPointUnaryExpression) {
			return ((AstPointUnaryExpression) expr).
					get_operator().get_operator() == COperator.dereference;
		}
		else return false;
	}
	/**
	 * Whether an expression is left-value, when one of the following 
	 * conditions hold:<br>
	 * 	1. E++, ++E, --E, E--;<br>
	 * 	2. E = X, E += X, E &= X;<br>
	 * 	3. E.field;<br>
	 * 	4. &E;<br>
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	public static boolean is_left_operand(AstExpression expr) throws Exception {
		AstNode parent = expr.get_parent();
		while(parent instanceof AstParanthExpression) {
			parent = parent.get_parent();
		}
		
		if(parent instanceof AstIncrePostfixExpression
				|| parent instanceof AstIncreUnaryExpression) {
			return true;
		}
		else if(parent instanceof AstAssignExpression
				|| parent instanceof AstArithAssignExpression
				|| parent instanceof AstBitwiseAssignExpression
				|| parent instanceof AstShiftAssignExpression) {
			return ((AstBinaryExpression) parent).get_loperand() == expr;
		}
		else if(parent instanceof AstFieldExpression) {
			return ((AstFieldExpression) parent).
					get_operator().get_punctuator() == CPunctuator.dot;
		}
		else if(parent instanceof AstPointUnaryExpression) {
			return ((AstPointUnaryExpression) parent).
					get_operator().get_operator() == COperator.address_of;
		}
		else return false;
	}
}
