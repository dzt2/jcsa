package com.jcsa.jcparse.lang.symbol;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
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
import com.jcsa.jcparse.lang.ctype.CEnumerator;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeclarator;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirIdentifier;
import com.jcsa.jcparse.lang.irlang.expr.CirImplicator;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReturnPoint;
import com.jcsa.jcparse.lang.irlang.expr.CirStringLiteral;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstance;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;


/**
 * It is used to parse from AstNode, CirNode, constant or CirExecution to SymbolNode.
 * 
 * @author yukimula
 *
 */
class SymbolParser {
	
	/* constructor & singleton */
	/**
	 * private constructor for singleton mode
	 */
	private SymbolParser() {
		this.ast_run_template = null;
		this.cir_optimize_switch = false;
	}
	/** singleton of the symbolic node parser **/
	protected static final SymbolParser parser = new SymbolParser();
	
	/* parameters for parsing */
	/** used to support sizeof operation **/
	private CRunTemplate ast_run_template;
	/** the CIR is optimized for default-value **/
	private boolean cir_optimize_switch;
	
	/* ast parsing */
	/**
	 * @param source 
	 * @return generate symbolic node from AstNode
	 * @throws Exception
	 */
	private SymbolNode parse_ast(AstNode source) throws Exception {
		SymbolNode target;
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(source instanceof AstIdExpression)
			target = this.parse_ast_id_expression((AstIdExpression) source);
		else if(source instanceof AstConstant)
			target = this.parse_ast_constant((AstConstant) source);
		else if(source instanceof AstLiteral)
			target = this.parse_ast_literal((AstLiteral) source);
		else if(source instanceof AstOperator)
			target = this.parse_ast_operator((AstOperator) source);
		else if(source instanceof AstUnaryExpression)
			target = this.parse_ast_unary_expression((AstUnaryExpression) source);
		else if(source instanceof AstPostfixExpression)
			target = this.parse_ast_postfix_expression((AstPostfixExpression) source);
		else if(source instanceof AstBinaryExpression)
			target = this.parse_ast_binary_expression((AstBinaryExpression) source);
		else if(source instanceof AstArrayExpression)
			target = this.parse_ast_array_expression((AstArrayExpression) source);
		else if(source instanceof AstCastExpression)
			target = this.parse_ast_cast_expression((AstCastExpression) source);
		else if(source instanceof AstCommaExpression)
			target = this.parse_ast_comma_expression((AstCommaExpression) source);
		else if(source instanceof AstParanthExpression)
			target = this.parse_ast_paranth_expression((AstParanthExpression) source);
		else if(source instanceof AstConstExpression)
			target = this.parse_ast_const_expression((AstConstExpression) source);
		else if(source instanceof AstField)
			target = this.parse_ast_field((AstField) source);
		else if(source instanceof AstFieldExpression)
			target = this.parse_ast_field_expression((AstFieldExpression) source);
		else if(source instanceof AstArgumentList)
			target = this.parse_ast_argument_list((AstArgumentList) source);
		else if(source instanceof AstFunCallExpression)
			target = this.parse_ast_call_expression((AstFunCallExpression) source);
		else if(source instanceof AstConditionalExpression)
			target = this.parse_ast_conditional_expression((AstConditionalExpression) source);
		else if(source instanceof AstInitializer)
			target = this.parse_initializer((AstInitializer) source);
		else if(source instanceof AstInitializerBody)
			target = this.parse_initializer_body((AstInitializerBody) source);
		else if(source instanceof AstSizeofExpression)
			target = this.parse_sizeof_expression((AstSizeofExpression) source);
		else 
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		if(!target.has_source())
			target.set_source(source);
		return target;
	}
	/**
	 * @param source
	 * @return id_expression ==> identifier | constant {enumerator}
	 * @throws Exception
	 */
	private SymbolNode parse_ast_id_expression(AstIdExpression source) throws Exception {
		CName cname = source.get_cname();
		if(cname instanceof CInstanceName) {
			CInstance instance = ((CInstanceName) cname).get_instance();
			return SymbolIdentifier.create(instance.get_type(), cname);
		}
		else if(cname instanceof CParameterName) {
			CInstance instance = ((CParameterName) cname).get_parameter();
			return SymbolIdentifier.create(instance.get_type(), cname);
		}
		else if(cname instanceof CEnumeratorName) {
			CEnumerator enumerator = ((CEnumeratorName) cname).get_enumerator();
			CConstant constant = new CConstant();
			constant.set_int(enumerator.get_value());
			return SymbolConstant.create(constant);
		}
		else {
			throw new IllegalArgumentException(cname.getClass().getSimpleName());
		}
	}
	/**
	 * @param source
	 * @return ast_constant --> constant
	 * @throws Exception
	 */
	private SymbolNode parse_ast_constant(AstConstant source) throws Exception {
		CConstant constant = source.get_constant();
		return SymbolConstant.create(constant);
	}
	/**
	 * @param source
	 * @return ast_literal --> literal
	 * @throws Exception
	 */
	private SymbolNode parse_ast_literal(AstLiteral source) throws Exception {
		return SymbolLiteral.create(source.get_literal());
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_operator(AstOperator source) throws Exception {
		COperator operator = source.get_operator();
		
		switch(operator) {
		case arith_add_assign:
		case arith_sub_assign:
		case arith_mul_assign:
		case arith_div_assign:
		case arith_mod_assign:
		case bit_and_assign:
		case bit_or_assign:
		case bit_xor_assign:
		case left_shift_assign:
		case righ_shift_assign:		
		{
			String name = operator.toString();
			name = name.substring(0, name.length() - 7).strip();
			operator = COperator.valueOf(name);
			break;
		}
		default:	break;
		}
		
		return SymbolOperator.create(operator);
	}
	/**
	 * 	+x	--> {x}
	 * 	-x	--> -{x}
	 * 	*x	--> defer{x}
	 * 	&x	--> addrs{x}
	 * 	~x	--> ~{x}
	 * 	++x	--> {x} + 1
	 * 	--x	--> {x} - 1
	 * 	!x	--> !{x}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_unary_expression(AstUnaryExpression source) throws Exception {
		/* recursively parse the operand of the unary expression */
		COperator operator = source.get_operator().get_operator();
		SymbolExpression operand = (SymbolExpression) this.parse_ast(source.get_operand());
		CType data_type = source.get_value_type();
		
		/* syntax-directed translation */
		switch(operator) {
		case positive:		return operand;
		case negative:
		case bit_not:
		case address_of:
		case dereference:
		{
			SymbolOperator sym_operator = (SymbolOperator) this.parse_ast(source.get_operator());
			return SymbolUnaryExpression.create(data_type, sym_operator, operand);
		}
		case logic_not: 
		{
			return this.parse_cod(operand, false);
		}
		case increment:
		{
			SymbolOperator sym_operator = SymbolOperator.create(COperator.arith_add);
			CConstant constant = new CConstant(); constant.set_int(1);
			SymbolConstant one = SymbolConstant.create(constant);
			return SymbolBinaryExpression.create(data_type, sym_operator, operand, one);
		}
		case decrement:
		{
			SymbolOperator sym_operator = SymbolOperator.create(COperator.arith_sub);
			CConstant constant = new CConstant(); constant.set_int(1);
			SymbolConstant one = SymbolConstant.create(constant);
			return SymbolBinaryExpression.create(data_type, sym_operator, operand, one);
		}
		default: throw new IllegalArgumentException("Invalid operator: null");
		}
	}
	/**
	 * x++ ==> {x}
	 * x-- ==> {x}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_postfix_expression(AstPostfixExpression source) throws Exception {
		return this.parse_ast(source.get_operand());
	}
	/**
	 * {oprt} {x} {y}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_binary_expression(AstBinaryExpression source) throws Exception {
		SymbolOperator operator = (SymbolOperator) this.parse_ast(source.get_operator());
		SymbolExpression loperand = (SymbolExpression) this.parse_ast(source.get_loperand());
		SymbolExpression roperand = (SymbolExpression) this.parse_ast(source.get_roperand());
		if(operator.get_operator() == COperator.logic_and || operator.get_operator() == COperator.logic_or) {
			loperand = (SymbolExpression) this.parse_cod(loperand, true);
			roperand = (SymbolExpression) this.parse_cod(roperand, true);
		}
		return SymbolBinaryExpression.create(source.get_value_type(), operator, loperand, roperand);
	}
	/**
	 * x[y] ==> *({x} + {y})
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_array_expression(AstArrayExpression source) throws Exception {
		SymbolExpression array = (SymbolExpression) this.parse_ast(source.get_array_expression());
		SymbolExpression index = (SymbolExpression) this.parse_ast(source.get_dimension_expression());
		
		SymbolExpression address = SymbolBinaryExpression.create(array.
				get_data_type(), SymbolOperator.create(COperator.arith_add), array, index);
		return SymbolUnaryExpression.create(source.get_value_type(), 
				SymbolOperator.create(COperator.dereference), address);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_cast_expression(AstCastExpression source) throws Exception {
		SymbolExpression operand = (SymbolExpression) this.parse_ast(source.get_expression());
		CType data_type = source.get_typename().get_type();
		return SymbolUnaryExpression.create(data_type, SymbolOperator.create(COperator.assign), operand);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_comma_expression(AstCommaExpression source) throws Exception {
		AstNode last_source = source.get_expression(source.number_of_arguments() - 1);
		return this.parse_ast(last_source);
	}
	private SymbolNode parse_ast_paranth_expression(AstParanthExpression source) throws Exception {
		return this.parse_ast(source.get_sub_expression());
	}
	private SymbolNode parse_ast_const_expression(AstConstExpression source) throws Exception {
		return this.parse_ast(source.get_expression());
	}
	private SymbolNode parse_ast_field(AstField source) throws Exception {
		return SymbolField.create(source.get_name());
	}
	private SymbolNode parse_ast_field_expression(AstFieldExpression source) throws Exception {
		SymbolExpression body = (SymbolExpression) this.parse_ast(source.get_body());
		SymbolField field = (SymbolField) this.parse_ast(source.get_field());
		
		if(source.get_operator().get_punctuator() == CPunctuator.arrow) {
			CType type = body.get_data_type();
			if(type instanceof CArrayType) {
				type = ((CArrayType) type).get_element_type();
			}
			else if(type instanceof CPointerType) {
				type = ((CPointerType) type).get_pointed_type();
			}
			else {
				throw new IllegalArgumentException(type.generate_code());
			}
			body = SymbolUnaryExpression.create(type, SymbolOperator.create(COperator.dereference), body);
		}
		
		return SymbolFieldExpression.create(source.get_value_type(), body, field);
	}
	private SymbolNode parse_ast_conditional_expression(AstConditionalExpression source) throws Exception {
		SymbolExpression condition = (SymbolExpression) this.parse_ast(source.get_condition());
		SymbolExpression toperand = (SymbolExpression) this.parse_ast(source.get_true_branch());
		SymbolExpression foperand = (SymbolExpression) this.parse_ast(source.get_false_branch());
		SymbolExpression ncondition = SymbolUnaryExpression.create(
				CBasicTypeImpl.bool_type, SymbolOperator.create(COperator.logic_not), condition);
		
		SymbolExpression loperand = SymbolBinaryExpression.create(source.get_value_type(), 
				SymbolOperator.create(COperator.arith_mul), condition, toperand);
		SymbolExpression roperand = SymbolBinaryExpression.create(source.get_value_type(), 
				SymbolOperator.create(COperator.arith_mul), ncondition, foperand);
		
		return SymbolBinaryExpression.create(source.get_value_type(), 
				SymbolOperator.create(COperator.arith_add), loperand, roperand);
	}
	private SymbolNode parse_ast_argument_list(AstArgumentList source) throws Exception {
		List<SymbolExpression> arguments = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_arguments(); k++) {
			SymbolExpression argument = (SymbolExpression) this.parse_ast(source.get_argument(k));
			arguments.add(argument);
		}
		return SymbolArgumentList.create(arguments);
	}
	private SymbolNode parse_ast_call_expression(AstFunCallExpression source) throws Exception {
		SymbolExpression function = (SymbolExpression) this.parse_ast(source.get_function());
		SymbolArgumentList arguments;
		if(source.has_argument_list()) {
			arguments = (SymbolArgumentList) this.parse_ast(source.get_argument_list());
		}
		else {
			arguments = SymbolArgumentList.create(new ArrayList<SymbolExpression>());
		}
		return SymbolCallExpression.create(source.get_value_type(), function, arguments);
	}
	private SymbolNode parse_initializer(AstInitializer source) throws Exception {
		if(source.is_body())
			return this.parse_ast(source.get_body());
		else
			return this.parse_ast(source.get_expression());
	}
	private SymbolNode parse_initializer_body(AstInitializerBody source) throws Exception {
		List<SymbolExpression> elements = new ArrayList<SymbolExpression>();
		AstInitializerList ilist = source.get_initializer_list();
		for(int k = 0; k < ilist.number_of_initializer(); k++) {
			AstFieldInitializer finit = ilist.get_initializer(k);
			SymbolExpression element = (SymbolExpression) this.parse_ast(finit.get_initializer());
			elements.add(element);
		}
		return SymbolInitializerList.create(source.get_value_type(), elements);
	}
	private SymbolNode parse_sizeof_expression(AstSizeofExpression source) throws Exception {
		CType data_type;
		if(source.is_expression())
			data_type = source.get_expression().get_value_type();
		else
			data_type = source.get_typename().get_type();
		data_type = CTypeAnalyzer.get_value_type(data_type);
		
		CConstant constant = new CConstant();
		if(this.ast_run_template == null)
			throw new IllegalArgumentException("Not support sizeof operator");
		else
			constant.set_int(this.ast_run_template.sizeof(data_type));
		
		return SymbolConstant.create(constant);
	}
	/**
	 * @param source
	 * @param template
	 * @return generate symbolic node from AstNode
	 * @throws Exception
	 */
	protected SymbolNode parse_ast(AstNode source, CRunTemplate template) throws Exception {
		this.ast_run_template = template;
		return this.parse_ast(source);
	}
	
	/* cir parser */
	/**
	 * @param source
	 * @return symbolic node parsed from CirNode
	 * @throws Exception
	 */
	private SymbolNode parse_cir(CirNode source) throws Exception {
		SymbolNode target;
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(source instanceof CirNameExpression)
			target = this.parse_cir_name_expression((CirNameExpression) source);
		else if(source instanceof CirConstExpression)
			target = this.parse_cir_const_expression((CirConstExpression) source);
		else if(source instanceof CirStringLiteral)
			target = this.parse_cir_literal((CirStringLiteral) source);
		else if(source instanceof CirAddressExpression)
			target = this.parse_cir_address_expression((CirAddressExpression) source);
		else if(source instanceof CirDeferExpression)
			target = this.parse_cir_defer_expression((CirDeferExpression) source);
		else if(source instanceof CirField)
			target = this.parse_cir_field((CirField) source);
		else if(source instanceof CirFieldExpression)
			target = this.parse_cir_field_expression((CirFieldExpression) source);
		else if(source instanceof CirCastExpression)
			target = this.parse_cir_cast_expression((CirCastExpression) source);
		else if(source instanceof CirDefaultValue)
			target = this.parse_cir_default_value((CirDefaultValue) source);
		else if(source instanceof CirComputeExpression)
			target = this.parse_cir_compute_expression((CirComputeExpression) source);
		else if(source instanceof CirInitializerBody)
			target = this.parse_cir_initializer_list((CirInitializerBody) source);
		else if(source instanceof CirArgumentList)
			target = this.parse_cir_argument_list((CirArgumentList) source);
		else if(source instanceof CirWaitExpression)
			target = this.parse_cir_wait_expression((CirWaitExpression) source);
		else
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		if(!target.has_source())
			target.set_source(source);
		return target;
	}
	/**
	 * @param source
	 * @return 
	 * @throws Exception
	 */
	private SymbolNode parse_cir_name_expression(CirNameExpression source) throws Exception {
		if(source instanceof CirDeclarator) {
			return SymbolIdentifier.create(source.get_data_type(), ((CirDeclarator) source).get_cname());
		}
		else if(source instanceof CirIdentifier) {
			return SymbolIdentifier.create(source.get_data_type(), ((CirIdentifier) source).get_cname());
		}
		else if(source instanceof CirImplicator) {
			return SymbolIdentifier.create(source.get_data_type(), source.get_ast_source());
		}
		else if(source instanceof CirReturnPoint) {
			return SymbolIdentifier.create(source.get_data_type(), source.function_of());
		}
		else {
			throw new IllegalArgumentException(source.generate_code(true));
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_const_expression(CirConstExpression source) throws Exception {
		return SymbolConstant.create(source.get_constant());
	}
	private SymbolNode parse_cir_literal(CirStringLiteral source) throws Exception {
		return SymbolLiteral.create(source.get_literal());
	}
	private SymbolNode parse_cir_address_expression(CirAddressExpression source) throws Exception {
		SymbolExpression operand = (SymbolExpression) this.parse_cir(source.get_operand());
		return SymbolUnaryExpression.create(source.get_data_type(), 
				SymbolOperator.create(COperator.address_of), operand);
	}
	private SymbolNode parse_cir_defer_expression(CirDeferExpression source) throws Exception {
		SymbolExpression operand = (SymbolExpression) this.parse_cir(source.get_address());
		return SymbolUnaryExpression.create(source.get_data_type(), 
				SymbolOperator.create(COperator.dereference), operand);
	}
	private SymbolNode parse_cir_field(CirField source) throws Exception {
		return SymbolField.create(source.get_name());
	}
	private SymbolNode parse_cir_field_expression(CirFieldExpression source) throws Exception {
		SymbolExpression body = (SymbolExpression) this.parse_cir(source.get_body());
		SymbolField field = (SymbolField) this.parse_cir(source.get_field());
		return SymbolFieldExpression.create(source.get_data_type(), body, field);
	}
	private SymbolNode parse_cir_cast_expression(CirCastExpression source) throws Exception {
		SymbolExpression operand = (SymbolExpression) this.parse_cir(source.get_operand());
		return SymbolUnaryExpression.create(source.get_data_type(), SymbolOperator.create(COperator.assign), operand);
	}
	private SymbolNode parse_cir_default_value(CirDefaultValue source) throws Exception {
		CType data_type = source.get_data_type();
		if(data_type == null)
			data_type = CBasicTypeImpl.void_type;
		else
			data_type = CTypeAnalyzer.get_value_type(data_type);
		
		CConstant constant;
		if(this.cir_optimize_switch) {
			if(data_type instanceof CBasicType) {
				constant = new CConstant();
				switch(((CBasicType) data_type).get_tag()) {
				case c_bool:		constant.set_bool(false); 	break;
				case c_char:
				case c_uchar:		constant.set_char('\0');	break;
				case c_short:
				case c_ushort:
				case c_int:
				case c_uint:		constant.set_int(0);		break;
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:		constant.set_long(0L);		break;
				case c_float:		constant.set_float(0.0f);	break;
				case c_double:
				case c_ldouble:		constant.set_double(0.0);	break;
				default: throw new IllegalArgumentException(data_type.generate_code());
				}
			}
			else if(data_type instanceof CPointerType) {
				constant = new CConstant();
				constant.set_long(0L);
			}
			else {
				constant = null;
			}
		}
		else {
			constant = null;
		}
		
		if(constant == null) {
			return SymbolIdentifier.create(source.get_data_type(), source);
		}
		else {
			return SymbolConstant.create(constant);
		}
	}
	private SymbolNode parse_cir_compute_expression(CirComputeExpression source) throws Exception {
		COperator operator = source.get_operator();
		switch(operator) {
		case positive:
		case negative:
		case bit_not:
		{
			return SymbolUnaryExpression.create(source.get_data_type(), 
					SymbolOperator.create(operator), 
					(SymbolExpression) this.parse_cir(source.get_operand(0)));
		}
		case logic_not:
		{
			SymbolExpression operand = (SymbolExpression) this.parse_cir(source.get_operand(0));
			return this.parse_cod(operand, false);
		}
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:
		{
			return SymbolBinaryExpression.create(source.get_data_type(), 
					SymbolOperator.create(operator), 
					(SymbolExpression) this.parse_cir(source.get_operand(0)),
					(SymbolExpression) this.parse_cir(source.get_operand(1)));
		}
		case logic_and:
		case logic_or:
		{
			SymbolExpression loperand = (SymbolExpression) this.parse_cir(source.get_operand(0));
			SymbolExpression roperand = (SymbolExpression) this.parse_cir(source.get_operand(1));
			loperand = (SymbolExpression) this.parse_cod(loperand, true);
			roperand = (SymbolExpression) this.parse_cod(roperand, true);
			return SymbolBinaryExpression.create(source.get_data_type(), 
					SymbolOperator.create(operator), loperand, roperand);
		}
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	private SymbolNode parse_cir_initializer_list(CirInitializerBody source) throws Exception {
		List<SymbolExpression> elements = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_elements(); k++) {
			elements.add((SymbolExpression) this.parse_cir(source.get_element(k)));
		}
		return SymbolInitializerList.create(source.get_data_type(), elements);
	}
	private SymbolNode parse_cir_argument_list(CirArgumentList source) throws Exception {
		List<SymbolExpression> arguments = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_arguments(); k++) {
			arguments.add((SymbolExpression) this.parse_cir(source.get_argument(k)));
		}
		return SymbolArgumentList.create(arguments);
	}
	private SymbolNode parse_cir_wait_expression(CirWaitExpression source) throws Exception {
		CirStatement wait_statement = source.statement_of();
		CirExecution wait_execution = wait_statement.get_tree().get_localizer().get_execution(wait_statement);
		CirExecution call_execution = wait_execution.get_graph().get_execution(wait_execution.get_id() - 1);
		CirCallStatement call_statement = (CirCallStatement) call_execution.get_statement();
		
		SymbolExpression function = (SymbolExpression) this.parse_cir(call_statement.get_function());
		SymbolArgumentList arguments = (SymbolArgumentList) this.parse_cir(call_statement.get_arguments());
		return SymbolCallExpression.create(source.get_data_type(), function, arguments);
	}
	/**
	 * @param source
	 * @param cir_optimize
	 * @return symbolic node parsed from CirNode
	 * @throws Exception
	 */
	protected SymbolNode parse_cir(CirNode source, boolean cir_optimize) throws Exception {
		this.cir_optimize_switch = cir_optimize;
		return this.parse_cir(source);
	}
	
	/* exe parser */
	/**
	 * @param execution
	 * @return do#execution.toString()
	 * @throws Exception
	 */
	protected SymbolNode parse_exe(CirExecution execution) throws Exception {
		SymbolNode node = SymbolIdentifier.create(execution);
		node.set_source(execution);
		return node;
	}
	
	/* constant parser */
	/**
	 * @param value
	 * @return 	Boolean|Character|Short|Integer|Long|Float|Double 	==> SymbolConstant
	 * 			String 												==> SymbolLiteral
	 * @throws Exception
	 */
	protected SymbolNode parse_con(Object value) throws Exception {
		CConstant constant = new CConstant();
		if(value instanceof Boolean) {
			constant.set_bool(((Boolean) value).booleanValue());
		}
		else if(value instanceof Character) {
			constant.set_char(((Character) value).charValue());
		}
		else if(value instanceof Short) {
			constant.set_int(((Short) value).shortValue());
		}
		else if(value instanceof Integer) {
			constant.set_int(((Integer) value).intValue());
		}
		else if(value instanceof Long) {
			constant.set_long(((Long) value).longValue());
		}
		else if(value instanceof Float) {
			constant.set_float(((Float) value).floatValue());
		}
		else if(value instanceof Double) {
			constant.set_double(((Double) value).doubleValue());
		}
		else if(value instanceof String) {
			return SymbolLiteral.create(value.toString());
		}
		else {
			throw new IllegalArgumentException(value.getClass().getSimpleName());
		}
		return SymbolConstant.create(constant);
	}
	
	/* condition parser */
	/**
	 * @param expression
	 * @param value
	 * @return	
	 * @throws Exception
	 */
	protected SymbolNode parse_cod(SymbolExpression expression, boolean value) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(CTypeAnalyzer.is_boolean(data_type)) {
			if(value) {
				return expression;
			}
			else {
				return SymbolUnaryExpression.create(CBasicTypeImpl.bool_type, 
						SymbolOperator.create(COperator.logic_not), expression);
			}
		}
		else if(CTypeAnalyzer.is_number(data_type) || CTypeAnalyzer.is_pointer(data_type)) {
			/* x != 0 */
			if(value) {
				return SymbolBinaryExpression.create(
						CBasicTypeImpl.bool_type, SymbolOperator.create(COperator.not_equals), 
						expression, (SymbolExpression) this.parse_con(Integer.valueOf(0)));
			}
			/* x == 0 */
			else {
				return SymbolBinaryExpression.create(
						CBasicTypeImpl.bool_type, SymbolOperator.create(COperator.equal_with), 
						expression, (SymbolExpression) this.parse_con(Integer.valueOf(0)));
			}
		}
		else {
			throw new IllegalArgumentException(data_type.toString());
		}
	}
	
}
