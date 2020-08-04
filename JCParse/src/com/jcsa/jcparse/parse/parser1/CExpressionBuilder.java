package com.jcsa.jcparse.parse.parser1;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArgumentList;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCastExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstField;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstSizeofExpression;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CEnumerator;
import com.jcsa.jcparse.lang.ctype.CField;
import com.jcsa.jcparse.lang.ctype.CFieldBody;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CParameterTypeList;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.ctype.CBasicType.CBasicTypeTag;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.CTypeQualifier;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstance;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.text.CLocation;

/**
 * To determine the type of each expression in C program.<br>
 * <br>
 * Since C's type system is statically decidable, this procedure completes
 * within the time of compilation, rather than dynamic execution.<br>
 * <br>
 * The following shows the rules to determine types of each expression, said
 * <code>Tk</code> be the type of expression <code>Ek</code>.<br>
 * =================== type inference rules ==================<br>
 * <code>
 * 01. { E --> id_expr } :: { T(E) = type_of(identifier, scope) }<br>
 * 02. { E --> constant } :: { T(E) = type_of(constant) }<br>
 * 03. { E --> literal } :: { T(E) = array(char, strlen(literal))}<br>
 * 
 * 04. { E --> E1 (+,-) E2 } :: { T(E) = maximum_precision(T(E1), T(E2)) } <= {is_number(T(E1)) && is_number(T(E2))} <br>
 * --. { E --> E1 (+,-) E2 } :: { T(E) = T(E1) } <= {is_address(T(E1)) && is_number(T(E2))} <br>
 * 05. { E --> E1 (+=, -=) E2 } :: { T(E) = T(E1) } <= { {is_number(E1) && is_number(E2)} || {is_pointer(E1) && is_integer(E2)} }<br>
 * 06. { E --> E1 (*,/) E2 } :: { T(E) = maximum_precision(T(E1), T(E2)) } <= {is_number(T(E1)) && is_number(T(E2))}<br>
 * 07. { E --> E1 (*=, /=) E2 } :: { T(E) = T(E1) } <= {is_number(T(E1)) && is_number(T(E2))}<br>
 * 08. { E --> E1 % E2 } :: { T(E) = maximum_precision(T(E1), T(E2))} <= {is_integer(T(E1)) && is_integer(T(E2))}<br>
 * 09. { E --> E1 %= E2 } :: { T(E) = T(E1) } <= {is_integer(T(E1)) && is_integer(T(E2))}<br>
 * 
 * 10. { E --> -E1 } :: { T(E) = maximum_precision(int, T(E1)) } <= {is_number(T(E1))}<br>
 * 11. { E --> E1 (&, |) E2 } :: { T(E) = maximum_precision(T(E1), T(E2)) } <= {is_integer(T(E1)) && is_integer(T(E2))}<br>
 * 12. { E --> ~ E1} :: { T(E) = maximum_precision(int, T(E1)) } <= {is_integer(T(E1))} <br>
 * 
 * 13. { E --> (--,++)E1 | E1(++,--)} :: { T(E) = T(E1) } <= {is_number(T(E1)) || is_pointer(T(E2))}<br>
 * 
 * 14. { E --> E1 (&&,||) E2 } :: { T(E) = Boolean } <== {is_number_or_pointer(T(E1)) && is_number_or_pointer(T(E2))}<br>
 * 15. { E --> !E } :: { T(E) = Boolean } <== {is_number_or_pointer(T(E1))}<br>
 * 
 * 16. { E --> E1 {->} Fd } :: { T(E), T(Fd) = type_of( field_of(T(*E1), Fd.name)) } <= { has_field(T(*E1), Fd.name) }<br>
 * 17. { E --> E1 (.) Fd } :: { T(E), T(Fd) = type_of( field_of(T(*E1), Fd.name)) } <= { has_field(T(E1), Fd.name) }<br> 
 * 
 * 18. { E --> E1 (>, >=, <=, <) E2 } :: { T(E) = Boolean } <= { {is_number(T(E1)) && is_number(T(E2))} || {is_pointer(T(E1) && is_pointer(T(E2)))} }<br>
 * 19. { E --> E1 (==, !=) E2 } :: { T(E) = Boolean } <= { is_number_or_pointer(T(E1)) && is_number_or_pointer(T(E2)) }<br>
 * 
 * 20. { E --> E1 (>>, <<) E2 } :: { T(E) = T(E1) } <= { is_integer(T(E1)) && is_integer(T(E2)) }<br>
 * 21. { E --> E1 (>>=, <<=) E2 } :: { T(E) = T(E1) } <= { is_integer(T(E1)) && is_integer(T(E2)) }<br>
 * 
 * 22. { E --> &E1 } :: { T(E) = pointer(T(E1)) }<br>
 * 23. { E --> *E1 } :: { T(E) = referOf(T(E1)) } <= { is_address(T(E1)) }<br>
 * 24. { E --> E1 [E2] } :: { T(E) = referOf(T(E1)) } <= { is_address(T(E1)) && is_integer(T(E2)) }<br>
 * 
 * 25. { E --> (Ty) E1 } :: { T(E) = type_of(Ty) } <= {is_transable(T(E1), type_of(Ty)) }<br>
 * 26. { E --> E1, E2, ..., En } :: { T(E) = T(En) } <br>
 * 27. { E --> E1 = E2 } :: { T(E) = T(E1) } <= {is_assignable(T(E2), T(E1)) }<br>
 * 28. { E --> E1 ? E2 : E3 } :: { T(E) = T(E2) } <= {is_number_or_pointer(T(E1)) && T(E2) == T(E3)}<br>
 * --. { E --> E1 ? E2 : E3 } :: { T(E) = maximum(T(E2), T(E3)) } <= {is_number_or_pointer(T(E1)) && is_number(T(E1)) && is_number(T(E2)) }<br>
 * 29. { E --> sizeof Ty | E1} :: { T(E) = integer } <= {is_defined(T(E1))}<br>
 * 30. { E --> E1 ( A1, A2, ..., An ) } :: { T(E) = return_of(T(E1)) } <= { {is_function(T(E1)) || is_function_pointer(T(E1))} && {is_transable(T(Ak), type_of(parameter_of(T(E1), k)))} }<br>
 * 
 * 31. { E --> ( E1 ) | E1 } :: { T(E) = T(E1) }
 * </code> <br>
 * =================== type inference rules ==================<br>
 * <br>
 * 
 * @author yukimula
 *
 */
public class CExpressionBuilder {

	// components
	/** factory to create type for expression **/
	protected CTypeFactory factory;

	/**
	 * constructor
	 */
	public CExpressionBuilder() {
		factory = new CTypeFactory();
	}

	// interface
	/**
	 * build up the type within expression node
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	public boolean build_up(AstExpression expr) throws Exception {
		CType type;

		// invalid input
		if (expr == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if (expr.get_value_type() != null)
			throw new IllegalArgumentException("Invalid expression: accessed");
		// basic expression
		else if (expr instanceof AstIdExpression)
			type = this.type_of((AstIdExpression) expr);
		else if (expr instanceof AstConstant)
			type = this.type_of((AstConstant) expr);
		else if (expr instanceof AstLiteral)
			type = this.type_of((AstLiteral) expr);
		// arithmetic expressions
		else if (expr instanceof AstArithAssignExpression)
			type = this.type_of((AstArithAssignExpression) expr);
		else if (expr instanceof AstArithBinaryExpression)
			type = this.type_of((AstArithBinaryExpression) expr);
		else if (expr instanceof AstArithUnaryExpression)
			type = this.type_of((AstArithUnaryExpression) expr);
		// bitwise expression
		else if (expr instanceof AstBitwiseAssignExpression)
			type = this.type_of((AstBitwiseAssignExpression) expr);
		else if (expr instanceof AstBitwiseBinaryExpression)
			type = this.type_of((AstBitwiseBinaryExpression) expr);
		else if (expr instanceof AstBitwiseUnaryExpression)
			type = this.type_of((AstBitwiseUnaryExpression) expr);
		// logical expression
		else if (expr instanceof AstLogicBinaryExpression)
			type = this.type_of((AstLogicBinaryExpression) expr);
		else if (expr instanceof AstLogicUnaryExpression)
			type = this.type_of((AstLogicUnaryExpression) expr);
		// incremental expression
		else if (expr instanceof AstIncreUnaryExpression)
			type = this.type_of((AstIncreUnaryExpression) expr);
		else if (expr instanceof AstIncrePostfixExpression)
			type = this.type_of((AstIncrePostfixExpression) expr);
		// access expression
		else if (expr instanceof AstPointUnaryExpression)
			type = this.type_of((AstPointUnaryExpression) expr);
		else if (expr instanceof AstFieldExpression)
			type = this.type_of((AstFieldExpression) expr);
		// relational expression
		else if (expr instanceof AstRelationExpression)
			type = this.type_of((AstRelationExpression) expr);
		// shift expression
		else if (expr instanceof AstShiftAssignExpression)
			type = this.type_of((AstShiftAssignExpression) expr);
		else if (expr instanceof AstShiftBinaryExpression)
			type = this.type_of((AstShiftBinaryExpression) expr);
		// assignment expression
		else if (expr instanceof AstAssignExpression)
			type = this.type_of((AstAssignExpression) expr);
		// function call expression
		else if (expr instanceof AstFunCallExpression)
			type = this.type_of((AstFunCallExpression) expr);
		// conditional expression
		else if (expr instanceof AstConditionalExpression)
			type = this.type_of((AstConditionalExpression) expr);
		// other expressions
		else if (expr instanceof AstArrayExpression)
			type = this.type_of((AstArrayExpression) expr);
		else if (expr instanceof AstCastExpression)
			type = this.type_of((AstCastExpression) expr);
		else if (expr instanceof AstCommaExpression)
			type = this.type_of((AstCommaExpression) expr);
		else if (expr instanceof AstParanthExpression)
			type = this.type_of((AstParanthExpression) expr);
		else if (expr instanceof AstConstExpression)
			type = this.type_of((AstConstExpression) expr);
		else if (expr instanceof AstSizeofExpression)
			type = this.type_of((AstSizeofExpression) expr);
		// initializer body is not determined based on itself but context
		else if (expr instanceof AstInitializerBody)
			return false;
		// invalid input
		else
			throw new IllegalArgumentException("Unable to understand expression: " + expr.getClass().getSimpleName()
					+ "\n\t" + this.code_of(expr));

		/* set type and return */ expr.set_value_type(type);
		return true;
	}

	// basic expression
	/**
	 * <code>
	 * 	( E --> identifier ) :: { type_of( instance_of(scope, identifier.name) )}
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstIdExpression expr) throws Exception {
		CName cname = expr.get_cname();
		CType type;
		if (cname instanceof CInstanceName) {
			CInstance instance = ((CInstanceName) cname).get_instance();
			/*
			 * if(instance == null) { AstIdentifier origin = cname.get_source();
			 * System.out.println("\t\t" + this.line_of(origin) + "\t" +
			 * this.code_of(origin)); System.out.println("\t\t" +
			 * cname.hashCode() + "\t" + origin.get_cname().hashCode());
			 * CInstanceName rname = (CInstanceName)
			 * cname.get_scope().get_name(expr.get_name()); int k = 1;
			 * while(rname != null) { System.out.println("\t\t[" + (k++) + "]\t"
			 * + this.line_of(rname.get_source()) + "\t" + rname.hashCode() +
			 * "\t" + rname.get_instance()); rname = rname.get_next_name(); } }
			 */
			type = instance.get_type();
		} else if (cname instanceof CEnumeratorName) {
			if (((CEnumeratorName) cname).get_enumerator() != null)
				type = CBasicTypeImpl.int_type;
			else
				throw new RuntimeException(
						"Undefined enumerator at line " + this.line_of(expr) + " : \"" + expr.get_name() + "\"");
		} else
			throw new RuntimeException(
					"Invalid identifier-expression at line " + this.line_of(expr) + " : \"" + expr.get_name() + "\"");

		return type;
	}

	/**
	 * <code>
	 * 	( E --> constant ) :: { T(E) = type_of(constant) }
	 * </code>
	 * 
	 * @param constant
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstConstant expr) throws Exception {
		CConstant constant = expr.get_constant();
		CType type = constant.get_type();
		return type;
	}

	/**
	 * <code>
	 * 	( E --> literal ) :: { T(E) = array(const char, literal.length) } 
	 * </code>
	 * 
	 * @param literal
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstLiteral literal) throws Exception {
		String lit = literal.get_literal();
		CType type = CBasicTypeImpl.char_type;
		// type = factory.get_qualifier_type(CTypeQualifier.c_const, type);
		type = factory.get_array_type(type, lit.length() + 1);
		return type;
	}

	// arithmetic binary expression
	/**
	 * <code>
	 * 	{ E --> E1 (+,-,*,/) E2 } :: { T(E) = maximum(VT(E1), VT(E2)) } <= { is_number(VT(E1)) && is_number(VT(E2)) }<br>
	 * 	{ E --> E1 % E2 } :: { T(E) = maximum(VT(E1), VT(E2)) } <= { is_integer(VT(E1)) && is_integer(VT(E2)) }<br>
	 * 	{ E --> E1 (+,-) E2 } :: { T(E) = VT(E1) } <= { is_address(VT(E1)) && is_integer(VT(E2)) } <br>
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstArithBinaryExpression expr) throws Exception {
		AstExpression E1 = expr.get_loperand();
		AstExpression E2 = expr.get_roperand();

		CType type;
		switch (expr.get_operator().get_operator()) {
		case arith_add:
			type = this.type_of_add(E1, E2);
			break;
		case arith_sub:
			type = this.type_of_sub(E1, E2);
			break;
		case arith_mul:
		case arith_div:
			type = this.type_of_mul(E1, E2);
			break;
		case arith_mod:
			type = this.type_of_mod(E1, E2);
			break;
		default:
			throw new IllegalArgumentException("Invalid operator: " + expr.get_operator().get_operator());
		}

		return type;
	}

	/**
	 * <code>
	 * 	{ E --> E1 + E2 } :: { T(E) = maximum(VT(E1), VT(E2)) } <= { is_number(VT(E1)) && is_number(VT(E2)) }<br>
	 * 	{ E --> E1 + E2 } :: { T(E) = VT(E1) } <= { is_address(VT(E1)) && is_integer(VT(E2)) }<br>
	 * 	{ E --> E1 + E2 } :: { T(E) = VT(E2) } <= { is_address(VT(E2)) && is_integer(VT(E1)) }<br>
	 * </code>
	 * 
	 * @param E1
	 * @param E2
	 * @return
	 * @throws Exception
	 */
	protected CType type_of_add(AstExpression E1, AstExpression E2) throws Exception {
		CType VT1 = this.type_of_value(E1.get_value_type());
		CType VT2 = this.type_of_value(E2.get_value_type());

		if (this.is_address_type(VT1)) {
			if (this.is_number_type(VT2))
				return this.type_of_address(VT1);
			else
				throw new RuntimeException("At line " + this.line_of(E2) + " : \"" + this.code_of(E2)
						+ "\"\n\tInvalid type: " + E2.get_value_type());
		} else if (this.is_address_type(VT2)) {
			if (this.is_number_type(VT1))
				return this.type_of_address(VT2);
			else
				throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
						+ "\"\n\tInvalid type: " + E1.get_value_type());
		} else if (this.is_number_type(VT1) && this.is_number_type(VT2))
			return this.maximum_number_type((CBasicType) VT1, (CBasicType) VT2);
		else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());
	}

	/**
	 * <code>
	 * 	{ E --> E1 - E2 } :: { T(E) = maximum(VT(E1), VT(E2)) } <= { is_number(VT(E1)) && is_number(VT(E2)) }<br>
	 * 	{ E --> E1 - E2 } :: { T(E) = VT(E1) } <= { is_address(VT(E1)) && is_integer(VT(E2)) }<br>
	 * 	{ E --> E1 - E2 } :: { T(E) = int } <= { is_address(VT(E1)) && is_address(VT(E2)) }<br>
	 * </code>
	 * 
	 * @param E1
	 * @param E2
	 * @return
	 * @throws Exception
	 */
	protected CType type_of_sub(AstExpression E1, AstExpression E2) throws Exception {
		CType VT1 = this.type_of_value(E1.get_value_type());
		CType VT2 = this.type_of_value(E2.get_value_type());

		if (this.is_address_type(VT1)) {
			if (this.is_address_type(VT2)) {
				return CBasicTypeImpl.int_type;
			} else if (this.is_integer_type(VT2)) {
				return this.type_of_address(VT1);
			} else
				throw new RuntimeException("At line " + this.line_of(E2) + " : \"" + this.code_of(E2)
						+ "\"\n\tInvalid type: " + E2.get_value_type());
		} else if (this.is_number_type(VT1)) {
			if (this.is_number_type(VT2)) {
				return this.maximum_number_type((CBasicType) VT1, (CBasicType) VT2);
			} else
				throw new RuntimeException("At line " + this.line_of(E2) + " : \"" + this.code_of(E2)
						+ "\"\n\tInvalid type: " + E2.get_value_type());
		} else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());
	}

	/**
	 * <code>
	 * 	{ E --> E1 (*,/) E2 } :: { T(E) = maximum(VT(E1), VT(E2)) } <= { is_number(VT(E1)) && is_number(VT(E2)) }
	 * </code>
	 * 
	 * @param E1
	 * @param E2
	 * @return
	 * @throws Exception
	 */
	protected CType type_of_mul(AstExpression E1, AstExpression E2) throws Exception {
		CType VT1 = this.type_of_value(E1.get_value_type());
		CType VT2 = this.type_of_value(E2.get_value_type());

		if (!this.is_number_type(VT1))
			throw new IllegalArgumentException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());
		else if (!this.is_number_type(VT2))
			throw new IllegalArgumentException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());
		else
			return this.maximum_number_type((CBasicType) VT1, (CBasicType) VT2);
	}

	/**
	 * <code>
	 * 	{ E --> E1 % E2 } :: { T(E) = maximum(VT(E1), VT(E2)) } <= { is_integer(VT(E1)) && is_integer(VT(E2)) }<br>
	 * </code>
	 * 
	 * @param E1
	 * @param E2
	 * @return
	 * @throws Exception
	 */
	protected CType type_of_mod(AstExpression E1, AstExpression E2) throws Exception {
		CType VT1 = this.type_of_value(E1.get_value_type());
		CType VT2 = this.type_of_value(E2.get_value_type());

		if (!this.is_integer_type(VT1))
			throw new IllegalArgumentException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());
		else if (!this.is_integer_type(VT2))
			throw new IllegalArgumentException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());
		else
			return this.maximum_number_type((CBasicType) VT1, (CBasicType) VT2);
	}

	// arithmetic assign expression
	/**
	 * <code>
	 * { E --> E1 (+=,-=) E2 } :: { T(E) = T(E1) } <= { { {is_number(VT(E1)) && is_number(VT(E2)) } || { is_address(VT(E1)) && is_integer(VT(E2)) }} && {is_assignable(T(E1))} }<br>
	 * { E --> E1 (*=,/=) E2 } :: { T(E) = T(E1) } <= { {is_number(VT(E1)) && is_number(VT(E2))} && {is_assignable(T(E1))} }<br>
	 * { E --> E1 %= E2 } :: { T(E) = T(E1) } <= { {is_integer(VT(E1)) && is_integer(VT(E2))} && {is_assignable(T(E1))} }<br>
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstArithAssignExpression expr) throws Exception {
		AstExpression E1 = expr.get_loperand();
		AstExpression E2 = expr.get_roperand();

		CType type;
		switch (expr.get_operator().get_operator()) {
		case arith_add_assign:
		case arith_sub_assign:
			type = this.type_of_add_assign(E1, E2);
			break;
		case arith_mul_assign:
		case arith_div_assign:
			type = this.type_of_mul_assign(E1, E2);
			break;
		case arith_mod_assign:
			type = this.type_of_mod_assign(E1, E2);
			break;
		default:
			throw new IllegalArgumentException("Invalid operator: " + expr.get_operator().get_operator());
		}

		return type;
	}

	/**
	 * <code>
	 * 	{ E --> E1 (+=,-=) E2 } :: { T(E) = T(E1) } <= { { {is_number(VT(E1)) && is_number(VT(E2)) } || { is_address(VT(E1)) && is_integer(VT(E2)) }} && {is_assignable(T(E1))} }
	 * </code>
	 * 
	 * @param E1
	 * @param E2
	 * @return
	 * @throws Exception
	 */
	protected CType type_of_add_assign(AstExpression E1, AstExpression E2) throws Exception {
		CType VT1 = this.type_of_value(E1.get_value_type());
		CType VT2 = this.type_of_value(E2.get_value_type());

		CType type;
		if (this.is_address_type(VT1)) {
			if (this.is_integer_type(VT2))
				type = E1.get_value_type();
			else
				throw new RuntimeException("At line " + this.line_of(E2) + " : \"" + this.code_of(E2)
						+ "\"\n\tInvalid type: " + E2.get_value_type());
		} else if (this.is_number_type(VT1)) {
			if (this.is_number_type(VT2))
				type = E1.get_value_type();
			else
				throw new RuntimeException("At line " + this.line_of(E2) + " : \"" + this.code_of(E2)
						+ "\"\n\tInvalid type: " + E2.get_value_type());
		} else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());

		if (this.is_assiged_type(type))
			return type;
		else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tCannot be assigned to: " + type);
	}

	/**
	 * <code>
	 * 	{ E --> E1 (*=,/=) E2 } :: { T(E) = T(E1) } <= { {is_number(VT(E1)) && is_number(VT(E2))} && {is_assignable(T(E1))} }
	 * </code>
	 * 
	 * @param E1
	 * @param E2
	 * @return
	 * @throws Exception
	 */
	protected CType type_of_mul_assign(AstExpression E1, AstExpression E2) throws Exception {
		CType VT1 = this.type_of_value(E1.get_value_type());
		CType VT2 = this.type_of_value(E2.get_value_type());

		CType type;
		if (this.is_number_type(VT1)) {
			if (this.is_number_type(VT2))
				type = E1.get_value_type();
			else
				throw new RuntimeException("At line " + this.line_of(E2) + " : \"" + this.code_of(E2)
						+ "\"\n\tInvalid type: " + E2.get_value_type());
		} else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());

		if (this.is_assiged_type(type))
			return type;
		else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tCannot be assigned to: " + type);
	}

	/**
	 * <code>
	 * 	{ E --> E1 %= E2 } :: { T(E) = T(E1) } <= { {is_integer(VT(E1)) && is_integer(VT(E2))} && {is_assignable(T(E1))} }
	 * </code>
	 * 
	 * @param E1
	 * @param E2
	 * @return
	 * @throws Exception
	 */
	protected CType type_of_mod_assign(AstExpression E1, AstExpression E2) throws Exception {
		CType VT1 = this.type_of_value(E1.get_value_type());
		CType VT2 = this.type_of_value(E2.get_value_type());

		CType type;
		if (this.is_integer_type(VT1)) {
			if (this.is_integer_type(VT2))
				type = E1.get_value_type();
			else
				throw new RuntimeException("At line " + this.line_of(E2) + " : \"" + this.code_of(E2)
						+ "\"\n\tInvalid type: " + E2.get_value_type());
		} else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());

		if (this.is_assiged_type(type))
			return type;
		else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tCannot be assigned to: " + type);
	}

	// arithmetic unary expression
	/**
	 * <code>
	 * 	{ E --> - E1 } :: { T(E) = maximum(int, VT(E1)) } <= {is_number(VT(E1))}
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstArithUnaryExpression expr) throws Exception {
		AstExpression E = expr.get_operand();
		CType type = this.type_of_value(E.get_value_type());
		if (this.is_number_type(type)) {
			type = this.maximum_number_type((CBasicType) type, CBasicTypeImpl.int_type);
			return type;
		} else
			throw new RuntimeException("At line " + this.line_of(expr) + " : \"" + this.code_of(expr)
					+ "\"\n\tInvalid type: " + expr.get_value_type());
	}

	// bitwise expression
	/**
	 * <code>
	 * 	{ E --> E1 (&,|,^) E2 } :: { T(E) = maximum(VT(E1), VT(E2)) } <= { is_integer(VT(E1)) && is_integer(VT(E2)) }
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstBitwiseBinaryExpression expr) throws Exception {
		AstExpression E1 = expr.get_loperand();
		AstExpression E2 = expr.get_roperand();
		CType VT1 = this.type_of_value(E1.get_value_type());
		CType VT2 = this.type_of_value(E2.get_value_type());

		CType type;
		if (this.is_integer_type(VT1)) {
			if (this.is_integer_type(VT2))
				type = this.maximum_number_type((CBasicType) VT1, (CBasicType) VT2);
			else
				throw new RuntimeException("At line " + this.line_of(E2) + " : \"" + this.code_of(E2)
						+ "\"\n\tInvalid type: " + E2.get_value_type());
		} else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());

		return type;
	}

	/**
	 * <code>
	 * 	{ E --> E1 (&=,|=,^=) E2 } :: { T(E) = T(E1) } <= { {is_integer(VT(E1)) && is_integer(VT(E2))} && {is_assignable(T(E1)} }
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstBitwiseAssignExpression expr) throws Exception {
		AstExpression E1 = expr.get_loperand();
		AstExpression E2 = expr.get_roperand();
		CType VT1 = this.type_of_value(E1.get_value_type());
		CType VT2 = this.type_of_value(E2.get_value_type());

		CType type;
		if (this.is_integer_type(VT1)) {
			if (this.is_integer_type(VT2))
				type = E1.get_value_type();
			else
				throw new RuntimeException("At line " + this.line_of(E2) + " : \"" + this.code_of(E2)
						+ "\"\n\tInvalid type: " + E2.get_value_type());
		} else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());

		return type;
	}

	/**
	 * <code>
	 * 	{ E --> ~ E1 } :: { T(E) = maximum(int, VT(E1)) } <= { is_integer(VT(E1)) }
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstBitwiseUnaryExpression expr) throws Exception {
		AstExpression E1 = expr.get_operand();
		CType VT1 = this.type_of_value(E1.get_value_type());

		CType type;
		if (this.is_integer_type(VT1))
			type = this.maximum_number_type(CBasicTypeImpl.int_type, (CBasicType) VT1);
		else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());

		return type;
	}

	// logical expression
	/**
	 * <code>
	 * 	{ E --> E1 (&&,||) E2 } :: { T(E) = _Bool } <= { is_number_or_address(VT(E1)) && is_number_or_address(VT(E2)) }
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstLogicBinaryExpression expr) throws Exception {
		AstExpression E1 = expr.get_loperand();
		AstExpression E2 = expr.get_roperand();
		CType VT1 = this.type_of_value(E1.get_value_type());
		CType VT2 = this.type_of_value(E2.get_value_type());

		CType type;
		if (this.is_number_type(VT1) || this.is_address_type(VT1)) {
			if (this.is_number_type(VT2) || this.is_address_type(VT2))
				type = CBasicTypeImpl.bool_type;
			else
				throw new RuntimeException("At line " + this.line_of(E2) + " : \"" + this.code_of(E2)
						+ "\"\n\tInvalid type: " + E2.get_value_type());
		} else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());

		return type;
	}

	/**
	 * <code>
	 * 	{ E --> ! E1 } :: { T(E) = _Bool } <= { is_number(VT(E1)) || is_address(VT(E1)) }
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstLogicUnaryExpression expr) throws Exception {
		AstExpression E1 = expr.get_operand();
		CType VT1 = this.type_of_value(E1.get_value_type());

		CType type;
		if (this.is_number_type(VT1) || this.is_address_type(VT1))
			type = CBasicTypeImpl.bool_type;
		else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());

		return type;
	}

	// incremental expression
	/**
	 * <code>
	 * 	{ E --> (++,--) E1 } :: { T(E) = T(E1) } <= { {is_number(VT(E1)) || is_address(VT(E1))} && {is_assignable(T(E1))} }
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstIncreUnaryExpression expr) throws Exception {
		AstExpression E1 = expr.get_operand();
		CType VT1 = this.type_of_value(E1.get_value_type());

		CType type;
		if ((this.is_number_type(VT1) || this.is_address_type(VT1)))
			type = E1.get_value_type();
		else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());

		if (this.is_assiged_type(E1.get_value_type())) {
			return type;
		} else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid assignment: " + E1.get_value_type());
	}

	/**
	 * <code>
	 * 	{ E --> E1 (++,--)} :: { T(E) = T(E1) } <= { {is_number(VT(E1)) || is_address(VT(E1))} && {is_assignable(T(E1))} }
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstIncrePostfixExpression expr) throws Exception {
		AstExpression E1 = expr.get_operand();
		CType VT1 = this.type_of_value(E1.get_value_type());

		CType type;
		if ((this.is_number_type(VT1) || this.is_address_type(VT1)))
			type = E1.get_value_type();
		else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());

		if (this.is_assiged_type(E1.get_value_type())) {
			return type;
		} else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid assignment: " + E1.get_value_type());
	}

	// pointer expression
	/**
	 * <code>
	 * 	{ E --> & E1 } :: { T(E) = pointer(VT(E1)) } <= {is_allocable(VT(E1))}<br>
	 * 	{ E --> * E1 } :: { T(E) = type_of_element(VT(E1)) } <= {is_array(VT(E1))}<br>
	 * 	{ E --> * E1 } :: { T(E) = type_of_reference(VT(E1)) } <= {is_point(VT(E1))}<br>
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstPointUnaryExpression expr) throws Exception {
		CType type;
		switch (expr.get_operator().get_operator()) {
		case address_of:
			type = this.type_of_addr(expr.get_operand());
			break;
		case dereference:
			type = this.type_of_refer(expr.get_operand());
			break;
		default:
			throw new RuntimeException("At line " + this.line_of(expr) + " : \"" + this.code_of(expr)
					+ "\"\n\tInvalid operator:" + expr.get_operator().get_operator());
		}
		return type;
	}

	/**
	 * <code>
	 * 	{ E --> & E1 } :: { T(E) = pointer(VT(E1)) } <= {is_allocable(VT(E1))}
	 * </code>
	 * 
	 * @param E1
	 * @return
	 * @throws Exception
	 */
	protected CType type_of_addr(AstExpression E1) throws Exception {
		CType VT1 = this.type_of_value(E1.get_value_type());

		CType type;
		if (!this.is_allocable_type(VT1))
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tUnable to allocate memory for " + E1.get_value_type() + "  (" + VT1.is_defined() + ")");
		else
			type = factory.get_pointer_type(VT1);

		return type;
	}

	/**
	 * <code>
	 * 	{E --> * E1 } :: { T(E) = type_of_element(VT(E1)) } <= {is_array(VT(E1))}<br>
	 * 	{E --> * E1 } :: { T(E) = type_of_reference(VT(E1)) } <= {is_point(VT(E1))}<br>
	 * </code>
	 * 
	 * @param E1
	 * @return
	 * @throws Exception
	 */
	protected CType type_of_refer(AstExpression E1) throws Exception {
		CType VT1 = this.type_of_value(E1.get_value_type());
		if (VT1 instanceof CArrayType)
			return ((CArrayType) VT1).get_element_type();
		else if (VT1 instanceof CPointerType)
			return ((CPointerType) VT1).get_pointed_type();
		else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());
	}

	// relation expression
	/**
	 * <code>
	 * 	{ E --> E1 (>, >=, <=, <) E2 } :: { T(E) = _Bool } <= { is_number(VT(E1)) && is_number(VT(E2)) }<br>
	 * 	{ E --> E1 (==, !=) E2 } :: { T(E) = _Bool } <= { is_number_or_address(VT(E1)) && is_number_or_address(VT(E2)) }<br>
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstRelationExpression expr) throws Exception {
		CType type;
		switch (expr.get_operator().get_operator()) {
		case greater_tn:
		case greater_eq:
		case smaller_eq:
		case smaller_tn:
			type = this.type_of_relation(expr.get_loperand(), expr.get_roperand());
			break;
		case equal_with:
		case not_equals:
			type = this.type_of_equality(expr.get_loperand(), expr.get_roperand());
			break;
		default:
			throw new RuntimeException("At line " + this.line_of(expr) + " : \"" + this.code_of(expr)
					+ "\"\n\tInvalid operator: " + expr.get_operator().get_operator());
		}
		return type;
	}

	/**
	 * <code>
	 * 	{ E --> E1 (>, >=, <=, <) E2 } :: { T(E) = _Bool } <= { is_number(VT(E1)) && is_number(VT(E2)) } || { is_addr(VT(E1)) && is_addr(VT(E2)) }
	 * </code>
	 * 
	 * @param E1
	 * @param E2
	 * @return
	 * @throws Exception
	 */
	protected CType type_of_relation(AstExpression E1, AstExpression E2) throws Exception {
		CType VT1 = this.type_of_value(E1.get_value_type());
		CType VT2 = this.type_of_value(E2.get_value_type());

		if (this.is_number_type(VT1)) {
			if (this.is_number_type(VT2))
				return CBasicTypeImpl.bool_type;
			else
				throw new RuntimeException("At line " + this.line_of(E2) + " : \"" + this.code_of(E2)
						+ "\"\n\tInvalid type: " + E2.get_value_type());
		} else if (this.is_address_type(VT1) && this.is_address_type(VT2))
			return CBasicTypeImpl.bool_type;
		else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());
	}

	/**
	 * <code>
	 * 	{ E --> E1 (==, !=) E2 } :: { T(E) = _Bool } <= { is_number_or_address(VT(E1)) && is_number_or_address(VT(E2)) }
	 * </code>
	 * 
	 * @param E1
	 * @param E2
	 * @return
	 * @throws Exception
	 */
	protected CType type_of_equality(AstExpression E1, AstExpression E2) throws Exception {
		CType VT1 = this.type_of_value(E1.get_value_type());
		CType VT2 = this.type_of_value(E2.get_value_type());

		if (this.is_number_type(VT1) || this.is_address_type(VT1)) {
			if (this.is_number_type(VT2) || this.is_address_type(VT2))
				return CBasicTypeImpl.bool_type;
			else
				throw new RuntimeException("At line " + this.line_of(E2) + " : \"" + this.code_of(E2)
						+ "\"\n\tInvalid type: " + E2.get_value_type());
		} else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());
	}

	// shift expression
	/**
	 * <code>
	 * 	{ E --> E1 (>>, <<) E2 } :: { T(E) = VT(E1) } <= { is_integer(E1) && is_integer(E2) }
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstShiftBinaryExpression expr) throws Exception {
		AstExpression E1 = expr.get_loperand();
		AstExpression E2 = expr.get_roperand();
		CType VT1 = this.type_of_value(E1.get_value_type());
		CType VT2 = this.type_of_value(E2.get_value_type());

		CType type;
		if (this.is_integer_type(VT1)) {
			if (this.is_integer_type(VT2))
				type = VT1;
			else
				throw new RuntimeException("At line " + this.line_of(E2) + " : \"" + this.code_of(E2)
						+ "\"\n\tInvalid type: " + E2.get_value_type());
		} else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());

		return type;
	}

	/**
	 * <code>
	 * 	{ E --> E1 (>>=, <<=) E2 } :: { T(E) = T(E1) } <= { is_integer(VT(E1)) && is_integer(VT(E2)) }
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstShiftAssignExpression expr) throws Exception {
		AstExpression E1 = expr.get_loperand();
		AstExpression E2 = expr.get_roperand();
		CType VT1 = this.type_of_value(E1.get_value_type());
		CType VT2 = this.type_of_value(E2.get_value_type());

		CType type;
		if (this.is_integer_type(VT1)) {
			if (this.is_integer_type(VT2))
				type = E1.get_value_type();
			else
				throw new RuntimeException("At line " + this.line_of(E2) + " : \"" + this.code_of(E2)
						+ "\"\n\tInvalid type: " + E2.get_value_type());
		} else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid type: " + E1.get_value_type());

		return type;
	}

	// assignment expression
	/**
	 * <code>
	 * 	{ E --> E1 = E2 } :: { T(E) = T(E1) } <= { is_assignable(T(E1)) && is_translable(VT(E2), VT(E1)) }
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstAssignExpression expr) throws Exception {
		AstExpression E1 = expr.get_loperand();
		AstExpression E2 = expr.get_roperand();

		CType type;
		if (this.is_assiged_type(E1.get_value_type())) {
			CType VT1 = this.type_of_value(E1.get_value_type());
			CType VT2 = this.type_of_value(E2.get_value_type());
			if (this.is_translated(VT2, VT1))
				type = E1.get_value_type();
			else
				throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
						+ "\"\n\tCannot be assigned by \"" + this.code_of(E2) + "\"" + "\n\tType-error: "
						+ VT2.toString() + " |--> " + VT1.toString());
		} else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tCannot be assigned by \"" + this.code_of(E2) + "\"");

		return type;
	}

	// field expression
	/**
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstFieldExpression expr) throws Exception {
		switch (expr.get_operator().get_punctuator()) {
		case dot:
			return this.type_of_dot(expr.get_body(), expr.get_field());
		case arrow:
			return this.type_of_arrow(expr.get_body(), expr.get_field());
		default:
			throw new RuntimeException("At line " + this.line_of(expr) + " : \"" + this.code_of(expr)
					+ "\"\n\tInvalid operator: " + expr.get_operator().get_punctuator());
		}
	}

	/**
	 * <code>
	 * 	{ E --> E1 -> Fd } :: { T(E) = T(field_of(VT(*E1), Fd.name)) } <= {has_field(VT(*E1), Fd.name)}
	 * </code>
	 * 
	 * @param base
	 * @param field
	 * @return
	 * @throws Exception
	 */
	protected CType type_of_arrow(AstExpression base, AstField field) throws Exception {
		CType VT = this.type_of_value(base.get_value_type());
		String name = field.get_name();

		CType EVT;
		if (VT instanceof CArrayType)
			EVT = this.type_of_value(((CArrayType) VT).get_element_type());
		else if (VT instanceof CPointerType)
			EVT = this.type_of_value(((CPointerType) VT).get_pointed_type());
		else
			throw new RuntimeException(
					"At line " + this.line_of(base) + " : \"" + this.code_of(base) + "\"\n\tInvalid type: " + VT);

		CFieldBody body;
		if (EVT instanceof CStructType)
			body = ((CStructType) EVT).get_fields();
		else if (EVT instanceof CUnionType)
			body = ((CUnionType) EVT).get_fields();
		else
			throw new RuntimeException(
					"At line " + this.line_of(base) + " : \"" + this.code_of(base) + "\"\n\tInvalid type: " + VT);

		if (body.has_field(name)) {
			CField cfield = body.get_field(name);
			/*
			 * Cannot reach the field-name CFieldName cname = (CFieldName)
			 * field.get_cname(); cname.set_field(cfield);
			 */
			return cfield.get_type();
		} else
			throw new RuntimeException("At line " + this.line_of(field) + " : " + name + "\n\tUndefined field in \""
					+ this.code_of(base) + "\"");
	}

	/**
	 * <code>
	 * 	{ E --> E1 -> Fd } :: { T(E) = T(field_of(VT(E1), Fd.name)) } <= {has_field(VT(E1), Fd.name)}
	 * </code>
	 * 
	 * @param base
	 * @param field
	 * @return
	 * @throws Exception
	 */
	protected CType type_of_dot(AstExpression base, AstField field) throws Exception {
		CType EVT = this.type_of_value(base.get_value_type());
		String name = field.get_name();

		CFieldBody body;
		if (EVT instanceof CStructType)
			body = ((CStructType) EVT).get_fields();
		else if (EVT instanceof CUnionType)
			body = ((CUnionType) EVT).get_fields();
		else
			throw new RuntimeException(
					"At line " + this.line_of(base) + " : \"" + this.code_of(base) + "\"\n\tInvalid type: " + EVT);

		if (body.has_field(name)) {
			CField cfield = body.get_field(name);
			/*
			 * CFieldName cname = (CFieldName) field.get_cname();
			 * cname.set_field(cfield);
			 */
			return cfield.get_type();
		} else
			throw new RuntimeException("At line " + this.line_of(field) + " : " + name + "\n\tUndefined field in \""
					+ this.code_of(base) + "\" for: " + EVT);
	}

	// function call
	/**
	 * <code>
	 * 	{ E --> E1 ( A1, A2,..., An ) } :: { T(E) = VT(ret(VT(E)))}
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstFunCallExpression expr) throws Exception {
		/* get children nodes */
		AstExpression func = expr.get_function();
		AstArgumentList list = expr.get_argument_list();

		/* get the function type */
		CFunctionType ftype;
		CType type = this.type_of_value(func.get_value_type());
		if (type instanceof CFunctionType)
			ftype = (CFunctionType) type;
		else if (type instanceof CPointerType)
			ftype = (CFunctionType) ((CPointerType) type).get_pointed_type();
		else
			throw new RuntimeException("At line " + this.line_of(expr) + " : " + this.code_of(func)
					+ "\"\n\tNot a function type: " + type);

		/* match arguments and return type */
		if (list != null)
			this.match_arguments(ftype.get_parameter_types(), list);
		return this.type_of_value(ftype.get_return_type());
	}

	/**
	 * 
	 * @param plist
	 * @param alist
	 * @return
	 * @throws Exception
	 */
	protected boolean match_arguments(CParameterTypeList plist, AstArgumentList alist) throws Exception {
		if (plist.size() == 0)
			return true;
		else {
			/* prefix match */
			int n = plist.size(), i, k = 0;
			int m = alist.number_of_arguments();
			for (i = 0; i < n && k < m; i++) {
				CType ptype = this.type_of_value(plist.get_parameter_type(i));
				if (this.is_void_type(ptype))
					continue;
				else {
					AstExpression ai = alist.get_argument(k++);
					CType atype = this.type_of_value(ai.get_value_type());
					if (!this.is_initialized(atype, ptype)) {
						throw new RuntimeException(
								"Invalid argument assignment at line " + this.line_of(ai) + " : \"" + this.code_of(ai)
										+ "\"\n\t\twhere: " + atype.toString() + "  ==>  " + ptype.toString());
					}
				}
			}

			if (i < n)
				throw new RuntimeException(
						"Argument match fails at line " + this.line_of(alist) + " : \"" + this.code_of(alist) + "\"");
			else if (plist.is_ellipsis())
				return true;
			else if (k < m)
				throw new RuntimeException(
						"Argument match fails at line " + this.line_of(alist) + " : \"" + this.code_of(alist) + "\"");
			else
				return true;
		}
	}

	/**
	 * The following shows the rules to be validated in assignment:<br>
	 * 1. <code>void</code> cannot be assigned to/by any type, including
	 * itself;<br>
	 * 2. <code>number</code> can be assigned to <code>number</code> or
	 * <code>pointer</code>;<br>
	 * 3. <code>complex</code> can only be assigned to <code>complex</code>;<br>
	 * 4. <code>imaginary</code> can only be assigned to
	 * <code>imaginary</code>;<br>
	 * 5. <code>struct</code> can only be assigned to <code>struct</code>;<br>
	 * 6. <code>union</code> can only be assigned to <code>union</code>;<br>
	 * 7. <code>array</code> can only be assigned to {compatible-element}
	 * <code>pointer</code>;<br>
	 * 8. <code>function</code> can only be assigned to {compatible-function}
	 * <code>pointer</code>;<br>
	 * 9. <code>pointer</code> can only be assigned to {compatible-element}
	 * <code>pointer</code>;<br>
	 * 
	 * @param src
	 * @param trg
	 * @return
	 * @throws Exception
	 */
	protected boolean is_initialized(CType src, CType trg) throws Exception {
		if (this.is_void_type(src))
			return false;
		else if (this.is_number_type(src)) {
			if (this.is_number_type(trg))
				return true;
			else if (trg instanceof CPointerType)
				return true;
			else
				return false;
		} else if (this.is_complex_type(src))
			return this.is_complex_type(trg);
		else if (this.is_imaginary_type(src))
			return this.is_imaginary_type(trg);
		else if (src instanceof CStructType)
			return src.equals(trg);
		else if (src instanceof CUnionType)
			return src.equals(trg);
		else if (src instanceof CArrayType) {
			if (trg instanceof CPointerType) {
				CType e1 = ((CArrayType) src).get_element_type();
				CType e2 = ((CPointerType) trg).get_pointed_type();
				e1 = this.type_of_value(e1);
				e2 = this.type_of_value(e2);
				return this.is_element_translated(e1, e2);
			} else
				return false;
		} else if (src instanceof CFunctionType) {
			if (trg instanceof CPointerType) {
				CType e2 = ((CPointerType) trg).get_pointed_type();
				return src.equals(e2);
			} else
				return false;
		} else if (src instanceof CPointerType) {
			if (trg instanceof CPointerType) {
				CType e1 = ((CPointerType) src).get_pointed_type();
				CType e2 = ((CPointerType) trg).get_pointed_type();
				e1 = this.type_of_value(e1);
				e2 = this.type_of_value(e2);
				return this.is_element_translated(e1, e2);
			} else
				return false;
		} else return src.equals(trg);
	}

	// conditional expression
	/**
	 * <code>
	 * 	{E --> E1 ? E2 : E3} :: { T(E) = VT(E2) } <= { is_number(VT(E1)) && {VT(E1) == VT(E2)}}<br>
	 * 	{E --> E1 ? E2 : E3} :: { T(E) = maximum(VT(E2), VT(E3)) } <= { is_number(VT(E1)) && {is_number(VT(E2)) && is_number(VT(E2))}}<br>
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstConditionalExpression expr) throws Exception {
		AstExpression E1 = expr.get_condition();
		AstExpression E2 = expr.get_true_branch();
		AstExpression E3 = expr.get_false_branch();

		CType btype = this.type_of_value(E1.get_value_type());
		CType ttype = this.type_of_value(E2.get_value_type());
		CType ftype = this.type_of_value(E3.get_value_type());

		if (this.is_number_type(btype) || this.is_address_type(btype)) {
			if (this.is_number_type(ttype) && this.is_number_type(ftype))
				return this.maximum_number_type((CBasicType) ttype, (CBasicType) ftype);
			else if (ttype.equals(ftype))
				return ttype;
			else if (this.is_address_type(ttype) && this.is_number_type(ftype))
				return this.type_of_value(ttype);
			else if (this.is_number_type(ttype) && this.is_address_type(ftype))
				return this.type_of_value(ftype);
			else if (this.is_address_type(ttype) && this.is_address_type(ftype))
				return this.maximum_address_type(ttype, ftype);
			else
				throw new RuntimeException("At line " + this.line_of(expr) + " : \"" + this.code_of(expr)
						+ "\"\n\tUnable to match expressions in two branches\n\t\t" + ttype.toString() + "\n\t\t"
						+ ftype.toString());
		} else
			throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
					+ "\"\n\tInvalid boolean-type: " + btype);
	}

	// other expressions
	/**
	 * <code>
	 * 	{ E --> E1 [ E2 ] } :: { T(E) = T(element_of(E1)) } <= { {is_address(VT(E1))} && {is_integer(VT(E2))} }
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstArrayExpression expr) throws Exception {
		AstExpression E1 = expr.get_array_expression();
		AstExpression E2 = expr.get_dimension_expression();
		CType VT1 = this.type_of_value(E1.get_value_type());
		CType VT2 = this.type_of_value(E2.get_value_type());

		if (this.is_integer_type(VT2)) {
			if (VT1 instanceof CArrayType)
				return ((CArrayType) VT1).get_element_type();
			else if (VT1 instanceof CPointerType)
				return ((CPointerType) VT1).get_pointed_type();
			else
				throw new RuntimeException("At line " + this.line_of(E1) + " : \"" + this.code_of(E1)
						+ "\"\n\tInvalid array-type: " + E1.get_value_type());
		} else
			throw new RuntimeException("At line " + this.line_of(E2) + " : \"" + this.code_of(E2)
					+ "\"\n\tNot an integer: " + E2.get_value_type());
	}

	/**
	 * <code>
	 * 	{ E --> (Ty) E1 } :: {T(E) = VT(Ty)} <= {is_castable(VT(E1), VT(E2))}
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstCastExpression expr) throws Exception {
		AstTypeName typename = expr.get_typename();
		AstExpression child = expr.get_expression();
		CType trg = this.type_of_value(typename.get_type());
		CType src = this.type_of_value(child.get_value_type());

		if (this.is_castable(src, trg))
			return trg;
		else
			throw new RuntimeException("At line " + this.line_of(expr) + " : \"" + this.code_of(child)
					+ "\"\n\tCannot be casted to \"" + this.code_of(typename) + "\"");
	}

	/**
	 * <code>
	 * 	{ E --> E1, E2, ..., En } :: { T(E) = VT(En) }
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstCommaExpression expr) throws Exception {
		int n = expr.number_of_arguments();
		AstExpression tail = expr.get_expression(n - 1);
		return this.type_of_value(tail.get_value_type());
	}

	/**
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstConstExpression expr) throws Exception {
		return expr.get_expression().get_value_type();
	}

	/**
	 * <code>
	 * 	{E --> ( E1 )} :: {T(E) = VT(E1)}
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstParanthExpression expr) throws Exception {
		return this.type_of_value(expr.get_sub_expression().get_value_type());
	}

	/**
	 * <code>
	 * 	{ E --> sizeof Ty } :: { T(E) = int } <= {is_defined(Ty)} <br>
	 * 	{ E --> sizeof E1 } :: { T(E) = int } <= {is_defined(VT(E1))} <br>
	 * </code>
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CType type_of(AstSizeofExpression expr) throws Exception {
		CType etype;
		if (expr.is_expression())
			etype = this.type_of_value(expr.get_expression().get_value_type());
		else
			etype = this.type_of_value(expr.get_typename().get_type());

		if (etype.is_defined())
			return CBasicTypeImpl.int_type;
		else
			throw new RuntimeException(
					"At line " + this.line_of(expr) + " : \"" + this.code_of(expr) + "\"\n\tUndefined type: " + etype);
	}

	// type getters
	/**
	 * get the type by eliminating qualifier from type expression
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private CType type_of_value(CType type) throws Exception {
		CType origin = type;
		while (type instanceof CQualifierType)
			type = ((CQualifierType) type).get_reference();

		if (type == null)
			throw new IllegalArgumentException("Invalid type: " + origin);
		else if (type instanceof CEnumType)
			return CBasicTypeImpl.int_type;
		else if (type instanceof CArrayType)
			return factory.get_pointer_type(((CArrayType) type).get_element_type());
		else
			return type;
	}

	/**
	 * get the type as address
	 * 
	 * @param type
	 *            : CArrayType | CPointerType | CFunctionType
	 * @return : CArrayType/CFunctionType |--> CPointerType
	 * @throws Exception
	 */
	private CType type_of_address(CType type) throws Exception {
		if (type instanceof CPointerType)
			return type;
		else if (type instanceof CArrayType)
			return factory.get_pointer_type(((CArrayType) type).get_element_type());
		else
			throw new IllegalArgumentException("Invalid address-transition : " + type);
	}

	// type classify
	/**
	 * whether the type is <code>void</code>
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private boolean is_void_type(CType type) throws Exception {
		if (type instanceof CBasicType)
			return ((CBasicType) type).get_tag() == CBasicTypeTag.c_void;
		else
			return false;
	}

	/**
	 * <code>
	 * 	_Bool, char, unsigned char, short, unsigned short, int,
	 * 	unsigned int, long, unsigned long, long long, unsigned 
	 * 	long long, float, double, long double; enum-type.
	 * </code>
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private boolean is_number_type(CType type) throws Exception {
		if (type instanceof CBasicType) {
			switch (((CBasicType) type).get_tag()) {
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
			case c_ldouble:
			case c_bool:
				return true;
			default:
				return false;
			}
		} else if (type instanceof CEnumType)
			return type.is_defined();
		else
			return false;
	}

	/**
	 * <code>
	 * 	_Bool, char, unsinged char, short, unsigned short, int,
	 * 	unsigned int, long, unsigned long, long long, unsigned 
	 * 	long long, enum
	 * </code>
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private boolean is_integer_type(CType type) throws Exception {
		if (type instanceof CBasicType) {
			switch (((CBasicType) type).get_tag()) {
			case c_char:
			case c_uchar:
			case c_short:
			case c_ushort:
			case c_int:
			case c_bool:
			case c_uint:
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
				return true;
			default:
				return false;
			}
		} else if (type instanceof CEnumType)
			return true;
		else
			return false;
	}

	/**
	 * <code>
	 * 	pointer-type | array-type
	 * </code>
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private boolean is_address_type(CType type) throws Exception {
		return (type instanceof CPointerType) || (type instanceof CArrayType);
	}

	/**
	 * <code>
	 * 	float | double | long double + _Complex
	 * </code>
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private boolean is_complex_type(CType type) throws Exception {
		if (type instanceof CBasicType) {
			switch (((CBasicType) type).get_tag()) {
			case c_float_complex:
			case c_double_complex:
			case c_ldouble_complex:
				return true;
			default:
				return false;
			}
		} else
			return false;
	}

	/**
	 * <code>
	 * 	float | double | long double + _Imaginary
	 * </code>
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private boolean is_imaginary_type(CType type) throws Exception {
		if (type instanceof CBasicType) {
			switch (((CBasicType) type).get_tag()) {
			case c_float_imaginary:
			case c_double_imaginary:
			case c_ldouble_imaginary:
				return true;
			default:
				return false;
			}
		} else
			return false;
	}

	/**
	 * a type is not assignable when it is const;<br>
	 * function or array is not assignable, too;<br>
	 * finally, void cannot be assigned at all.<br>
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private boolean is_assiged_type(CType type) throws Exception {
		if (type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else {
			while (type instanceof CQualifierType) {
				CTypeQualifier qualifier = ((CQualifierType) type).get_qualifier();
				if (qualifier == CTypeQualifier.c_const)
					return false;
				else
					type = ((CQualifierType) type).get_reference();
			}

			if (type instanceof CArrayType)
				return false;
			else if (type instanceof CFunctionType)
				return false;
			else if (type instanceof CBasicType) {
				if (((CBasicType) type).get_tag() == CBasicTypeTag.c_void)
					return false;
				else
					return true;
			} else
				return true;
		}
	}

	/**
	 * a type is not allocated if:<br>
	 * 1. type is void;<br>
	 * 2. type is not defined;<br>
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private boolean is_allocable_type(CType type) throws Exception {
		if (!type.is_defined())
			return false;
		else if (type instanceof CBasicType) {
			switch (((CBasicType) type).get_tag()) {
			case c_void:
				return false;
			default:
				return true;
			}
		} else
			return true;
	}

	/**
	 * whether the type is const-qualifier
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private boolean is_const_type(CType type) throws Exception {
		if (type instanceof CQualifierType) {
			while (type instanceof CQualifierType) {
				CTypeQualifier qualifier = ((CQualifierType) type).get_qualifier();
				if (qualifier == CTypeQualifier.c_const)
					return true;
				else
					type = ((CQualifierType) type).get_reference();
			}
			return false;
		} else
			return false;
	}

	/**
	 * To determine whether pointer(e2) := pointer(e1) is valid
	 * 
	 * @param e1
	 * @param e2
	 * @return
	 * @throws Exception
	 */
	private boolean is_element_translated(CType e1, CType e2) throws Exception {
		if (this.is_const_type(e1) && !this.is_const_type(e2))
			return false;
		else {
			CType VT1 = this.type_of_value(e1);
			CType VT2 = this.type_of_value(e2);
			if (this.is_void_type(e1))
				return true;
			else if (this.is_void_type(e2))
				return true;
			else
				return VT1.equals(VT2);
		}
	}

	/**
	 * whether the expression can be seen as zero of constant or zero enumerator
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private boolean is_zero_constant(AstExpression expr) throws Exception {
		if (expr instanceof AstConstant) {
			int value;

			CConstant constant = ((AstConstant) expr).get_constant();
			switch (constant.get_type().get_tag()) {
			case c_char:
			case c_uchar:
				char ch = constant.get_char();
				value = ch;
				break;
			case c_int:
			case c_uint:
				value = constant.get_integer();
				break;
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
				long lval = constant.get_long();
				value = (int) lval;
				break;
			default:
				return false;
			}

			return value == 0;
		} else if (expr instanceof AstIdExpression) {
			CName cname = ((AstIdExpression) expr).get_cname();
			if (cname instanceof CEnumeratorName) {
				CEnumerator enumerator = ((CEnumeratorName) cname).get_enumerator();
				return enumerator.get_value() == 0;
			} else
				return false;
		} else
			return false;
	}

	// type inferencer
	/**
	 * <code>
	 * 	[long double > double > float]
	 * 	>
	 * 	[unsigned long long > long long]
	 * 	>
	 * 	[unsigned long > long]
	 * 	> 
	 * 	[unsigned, int]
	 * 	>
	 * 	[unsigned short > short] | [unsigned char > char]
	 * 	>
	 * 	[_Bool]
	 * </code>
	 * 
	 * @param type1
	 * @param type2
	 * @return
	 * @throws Exception
	 */
	private CType maximum_number_type(CBasicType type1, CBasicType type2) throws Exception {
		/* improve the type to computation precision */
		CBasicTypeTag tag1 = type1.get_tag();
		switch (tag1) {
		case c_bool:
		case c_char:
		case c_uchar:
		case c_short:
		case c_ushort:
			tag1 = CBasicTypeTag.c_int;
			break;
		case c_float:
			tag1 = CBasicTypeTag.c_double;
			break;
		default:
			break;
		}

		/* improve the type to computation precision */
		CBasicTypeTag tag2 = type2.get_tag();
		switch (tag2) {
		case c_bool:
		case c_char:
		case c_uchar:
		case c_short:
		case c_ushort:
			tag2 = CBasicTypeTag.c_int;
			break;
		case c_float:
			tag2 = CBasicTypeTag.c_double;
			break;
		default:
			break;
		}

		/* find the maximum type for numbers */
		if (tag1 == CBasicTypeTag.c_ldouble || tag2 == CBasicTypeTag.c_ldouble) {
			return CBasicTypeImpl.ldouble_type;
		} else if (tag1 == CBasicTypeTag.c_double || tag2 == CBasicTypeTag.c_double) {
			return CBasicTypeImpl.double_type;
		} else {
			/* collecting type1 */
			boolean unsigned1;
			int int_level1;
			switch (tag1) {
			case c_ullong:
				int_level1 = 2;
				unsigned1 = true;
				break;
			case c_llong:
				int_level1 = 2;
				unsigned1 = false;
				break;
			case c_ulong:
				int_level1 = 1;
				unsigned1 = true;
				break;
			case c_long:
				int_level1 = 1;
				unsigned1 = false;
				break;
			case c_uint:
				int_level1 = 0;
				unsigned1 = true;
				break;
			case c_int:
				int_level1 = 0;
				unsigned1 = false;
				break;
			default:
				throw new IllegalArgumentException("Invalid type: " + type1);
			}

			/* collecting type2 */
			boolean unsigned2;
			int int_level2;
			switch (tag2) {
			case c_ullong:
				int_level2 = 2;
				unsigned2 = true;
				break;
			case c_llong:
				int_level2 = 2;
				unsigned2 = false;
				break;
			case c_ulong:
				int_level2 = 1;
				unsigned2 = true;
				break;
			case c_long:
				int_level2 = 1;
				unsigned2 = false;
				break;
			case c_uint:
				int_level2 = 0;
				unsigned2 = true;
				break;
			case c_int:
				int_level2 = 0;
				unsigned2 = false;
				break;
			default:
				throw new IllegalArgumentException("Invalid type: " + type2);
			}

			/* merge to maximum integer type */
			boolean unsigned = unsigned1 || unsigned2;
			int int_level = (int_level1 > int_level2) ? int_level1 : int_level2;

			/* return the maximum type */
			if (unsigned) {
				switch (int_level) {
				case 2:
					return CBasicTypeImpl.ullong_type;
				case 1:
					return CBasicTypeImpl.ulong_type;
				default:
					return CBasicTypeImpl.uint_type;
				}
			} else {
				switch (int_level) {
				case 2:
					return CBasicTypeImpl.llong_type;
				case 1:
					return CBasicTypeImpl.long_type;
				default:
					return CBasicTypeImpl.int_type;
				}
			}
		}
	}

	/**
	 * The following shows the rules to be validated in assignment:<br>
	 * 1. <code>void</code> cannot be assigned to/by any type, including
	 * itself;<br>
	 * 2. <code>number</code> can be assigned to <code>number</code> or
	 * <code>pointer</code>;<br>
	 * 3. <code>complex</code> can only be assigned to <code>complex</code>;<br>
	 * 4. <code>imaginary</code> can only be assigned to
	 * <code>imaginary</code>;<br>
	 * 5. <code>struct</code> can only be assigned to <code>struct</code>;<br>
	 * 6. <code>union</code> can only be assigned to <code>union</code>;<br>
	 * 7. <code>array</code> can only be assigned to {compatible-element}
	 * <code>pointer</code>;<br>
	 * 8. <code>function</code> can only be assigned to {compatible-function}
	 * <code>pointer</code>;<br>
	 * 9. <code>pointer</code> can only be assigned to {compatible-element}
	 * <code>pointer</code>;<br>
	 * 
	 * @param src
	 * @param trg
	 * @return
	 * @throws Exception
	 */
	private boolean is_translated(CType src, CType trg) throws Exception {
		if (this.is_void_type(src))
			return false;
		else if (this.is_number_type(src)) {
			if (this.is_number_type(trg))
				return true;
			else if (trg instanceof CPointerType)
				return true;
			else
				return false;
		} else if (this.is_complex_type(src))
			return this.is_complex_type(trg);
		else if (this.is_imaginary_type(src))
			return this.is_imaginary_type(trg);
		else if (src instanceof CStructType)
			return src.equals(trg);
		else if (src instanceof CUnionType)
			return src.equals(trg);
		else if (src instanceof CArrayType) {
			if (trg instanceof CPointerType) {
				CType e1 = ((CArrayType) src).get_element_type();
				CType e2 = ((CPointerType) trg).get_pointed_type();
				return this.is_element_translated(e1, e2);
			} else
				return false;
		} else if (src instanceof CFunctionType) {
			if (trg instanceof CPointerType) {
				CType e2 = ((CPointerType) trg).get_pointed_type();
				return src.equals(e2);
			} else
				return false;
		} else if (src instanceof CPointerType) {
			if (trg instanceof CPointerType) {
				CType e1 = ((CPointerType) src).get_pointed_type();
				CType e2 = ((CPointerType) trg).get_pointed_type();
				return this.is_element_translated(e1, e2);
			} else
				return false;
		} else
			throw new IllegalArgumentException("Unknown type: " + src);
	}

	/**
	 * The following shows the rules to determine whether src can be casted to
	 * trg:<br>
	 * 1. void cannot be casted to/by any type, including itself;<br>
	 * 2. complex can only be casted to itself;<br>
	 * 3. imaginary can only be casted to itself;<br>
	 * 4. struct | union can only be casted to itself;<br>
	 * 5. array | function | pointer can be casted to pointer without
	 * considering whether the types of their elements are compatible;<br>
	 * 6. array | pointer can be casted to the integer type;<br>
	 * 7. number can be casted to number | pointer.<br>
	 * 8. any types can be casted as void for once.<br>
	 * 
	 * @param src
	 * @param trg
	 * @return
	 * @throws Exception
	 */
	private boolean is_castable(CType src, CType trg) throws Exception {
		if (this.is_void_type(src))
			return false;
		else if (this.is_void_type(trg))
			return true;
		else if (this.is_number_type(src)) {
			if (this.is_number_type(trg))
				return true;
			else if (trg instanceof CPointerType)
				return true;
			else
				return false;
		} else if (this.is_complex_type(src))
			return this.is_complex_type(trg);
		else if (this.is_imaginary_type(src))
			return this.is_imaginary_type(trg);
		else if (src instanceof CStructType)
			return src.equals(trg);
		else if (src instanceof CUnionType)
			return src.equals(trg);
		else if (src instanceof CArrayType) {
			if (trg instanceof CPointerType)
				return true;
			else if (this.is_integer_type(trg))
				return true;
			else
				return false;
		} else if (src instanceof CFunctionType) {
			if (trg instanceof CPointerType)
				return true;
			else
				return false;
		} else if (src instanceof CPointerType) {
			if (trg instanceof CPointerType)
				return true;
			else if (this.is_integer_type(trg))
				return true;
			else
				return false;
		} else
			throw new IllegalArgumentException("Unknown type: " + src);
	}

	/**
	 * The maximum type for pointer | array type.<br>
	 * 1) * (void) is minimal;<br>
	 * 2) * (type) < * (const type).<br>
	 * 
	 * @param type1
	 * @param type2
	 * @return
	 * @throws Exception
	 */
	private CType maximum_address_type(CType type1, CType type2) throws Exception {
		CType etype1;
		if (type1 instanceof CArrayType)
			etype1 = ((CArrayType) type1).get_element_type();
		else if (type1 instanceof CPointerType)
			etype1 = ((CPointerType) type1).get_pointed_type();
		else
			throw new IllegalArgumentException("Not address-type: " + type1.toString());

		CType etype2;
		if (type2 instanceof CArrayType)
			etype2 = ((CArrayType) type2).get_element_type();
		else if (type2 instanceof CPointerType)
			etype2 = ((CPointerType) type2).get_pointed_type();
		else
			throw new IllegalArgumentException("Not address-type: " + type2.toString());

		boolean is_const = false;
		if (this.is_const_type(etype1))
			is_const = true;
		if (this.is_const_type(etype2))
			is_const = true;
		etype1 = this.type_of_value(etype1);
		etype2 = this.type_of_value(etype2);

		CType vtype;
		if (this.is_void_type(etype1))
			vtype = etype2;
		else if (this.is_void_type(etype2))
			vtype = etype1;
		else if (etype1.equals(etype2))
			vtype = etype1;
		else
			throw new IllegalArgumentException(
					"Incompatible address-type: " + "\n\t" + type1.toString() + "\n\t" + type2.toString());

		if (is_const)
			vtype = this.factory.get_qualifier_type(CTypeQualifier.c_const, vtype);
		vtype = this.factory.get_pointer_type(vtype);
		return vtype;
	}

	// code-getters
	/**
	 * get the line of the node in source text
	 * 
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private int line_of(AstNode node) throws Exception {
		CLocation loc = node.get_location();
		return loc.get_source().line_of(loc.get_bias());
	}

	/***
	 * get the code of the node in source text
	 * 
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private String code_of(AstNode node) throws Exception {
		CLocation loc = node.get_location();
		return loc.read();
	}

}
