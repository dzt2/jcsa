package com.jcsa.jcparse.lang.sym;

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
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumerator;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirStringLiteral;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstance;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;

/**
 * It is used to generate symbolic nodes by parsing AstNode, CirNode, CirExecution, CirStatement, Boolean,
 * Character, Short, Integer, Long, Float, Double, Complex and String.
 * 
 * @author yukimula
 *
 */
class SymParser {
	
	/* singleton definitions */
	/** private constructor **/
	private SymParser() { }
	/** used to support sizeof operation **/
	private CRunTemplate ast_run_template;
	/** the CIR is optimized for default-value **/
	private boolean cir_optimize_switch;
	/** singleton instance of the parser **/
	protected static final SymParser parser = new SymParser();
	
	/* parsing from SymNode */
	/**
	 * [bool]	--> expression 		{true}
	 * 			-->	!expression		{false}
	 * [number]	--> expression != 0	{true}
	 * 			--> expression == 0	{false}
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	protected SymExpression sym_condition(SymExpression expression, boolean value) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(CTypeAnalyzer.is_boolean(data_type)) {
			if(value) {
				return expression;
			}
			else {
				return SymUnaryExpression.create(CBasicTypeImpl.bool_type, SymOperator.create(COperator.logic_not), expression);
			}
		}
		else if(CTypeAnalyzer.is_number(data_type) || CTypeAnalyzer.is_pointer(data_type)) {
			CConstant constant = new CConstant();
			constant.set_int(0);
			SymConstant zero = SymConstant.create(constant);
			
			SymOperator operator;
			if(value) {
				operator = SymOperator.create(COperator.not_equals);
			}
			else {
				operator = SymOperator.create(COperator.equal_with);
			}
			
			return SymBinaryExpression.create(CBasicTypeImpl.bool_type, operator, expression, zero);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + data_type.generate_code());
		}
	}
	
	/* parsing from AstNode */
	/**
	 * @param source
	 * @param run_template
	 * @return parse from ast-node w.r.t. the sizeof template
	 * @throws Exception
	 */
	protected SymNode parse_ast(AstNode source, CRunTemplate run_template) throws Exception {
		this.ast_run_template = run_template;
		return this.parse_ast(source);
	}
	/**
	 * @param source
	 * @return symbolic node parsed from abstract syntactic node
	 * @throws Exception
	 */
	private SymNode parse_ast(AstNode source) throws Exception {
		SymNode solution;
		
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(source instanceof AstIdExpression)
			solution = this.parse_id_expression((AstIdExpression) source);
		else if(source instanceof AstConstant)
			solution = this.parse_constant((AstConstant) source);
		else if(source instanceof AstLiteral)
			solution = this.parse_literal((AstLiteral) source);
		else if(source instanceof AstOperator)
			solution = this.parse_operator((AstOperator) source);
		else if(source instanceof AstUnaryExpression)
			solution = this.parse_unary_expression((AstUnaryExpression) source);
		else if(source instanceof AstBinaryExpression)
			solution = this.parse_binary_expression((AstBinaryExpression) source);
		else if(source instanceof AstPostfixExpression)
			solution = this.parse_postfix_expression((AstPostfixExpression) source);
		else if(source instanceof AstArrayExpression)
			solution = this.parse_array_expression((AstArrayExpression) source);
		else if(source instanceof AstCastExpression)
			solution = this.parse_cast_expression((AstCastExpression) source);
		else if(source instanceof AstCommaExpression)
			solution = this.parse_comma_expression((AstCommaExpression) source);
		else if(source instanceof AstConstExpression)
			solution = this.parse_const_expression((AstConstExpression) source);
		else if(source instanceof AstParanthExpression)
			solution = this.parse_paranth_expression((AstParanthExpression) source);
		else if(source instanceof AstConditionalExpression)
			solution = this.parse_conditional_expression((AstConditionalExpression) source);
		else if(source instanceof AstField)
			solution = this.parse_field((AstField) source);
		else if(source instanceof AstFieldExpression)
			solution = this.parse_field_expression((AstFieldExpression) source);
		else if(source instanceof AstArgumentList)
			solution = this.parse_argument_list((AstArgumentList) source);
		else if(source instanceof AstFunCallExpression)
			solution = this.parse_fun_call_expression((AstFunCallExpression) source);
		else if(source instanceof AstInitializer)
			solution = this.parse_initializer((AstInitializer) source);
		else if(source instanceof AstInitializerBody)
			solution = this.parse_initializer_body((AstInitializerBody) source);
		else if(source instanceof AstFieldInitializer)
			solution = this.parse_field_initializer((AstFieldInitializer) source);
		else if(source instanceof AstSizeofExpression)
			solution = this.parse_sizeof_expression((AstSizeofExpression) source);
		else
			throw new IllegalArgumentException("Unsupported: " + source);
		
		if(!solution.has_source()) solution.set_source(source);
		
		return solution;
	}
	/**
	 * identifier 	--> sym_identifier 	{non-enumerator}
	 * 				--> sym_constant	{enumerator}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymNode parse_id_expression(AstIdExpression source) throws Exception {
		CName cname = source.get_cname();
		/* 1. identifier as declarator --> sym_identifier */
		if(cname instanceof CInstanceName) {
			CInstance instance = ((CInstanceName) cname).get_instance();
			return SymIdentifier.create(instance.get_type(), cname.get_name());
		}
		/* 2. identifier as parameter --> sym_identifier */
		else if(cname instanceof CParameterName) {
			CInstance instance = ((CParameterName) cname).get_parameter();
			return SymIdentifier.create(instance.get_type(), cname.get_name());
		}
		/* 3. identifier as enumerator --> sym_constant(int) */
		else if(cname instanceof CEnumeratorName) {
			CEnumerator enumerator = ((CEnumeratorName) cname).get_enumerator();
			CConstant int_constant = new CConstant();
			int_constant.set_int(enumerator.get_value());
			return SymConstant.create(int_constant);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + cname);
		}
	}
	/**
	 * constant --> sym_constant(numeric)
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymNode parse_constant(AstConstant source) throws Exception {
		CConstant constant = source.get_constant();
		return SymConstant.create(constant);
	}
	/**
	 * literal --> sym_literal
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymNode parse_literal(AstLiteral source) throws Exception {
		return SymLiteral.create(source.get_value_type(), source.get_literal());
	}
	/**
	 * @param source
	 * @return sym_operator referred to operator of ast_node
	 * @throws Exception
	 */
	private SymNode parse_operator(AstOperator source) throws Exception {
		return SymOperator.create(source.get_operator());
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
	private SymNode parse_unary_expression(AstUnaryExpression source) throws Exception {
		/* recursively parse the operand of the unary expression */
		COperator operator = source.get_operator().get_operator();
		SymExpression operand = (SymExpression) this.parse_ast(source.get_operand());
		CType data_type = source.get_value_type();
		
		/* syntax-directed translation */
		switch(operator) {
		case positive:				/* +x --> {x} */
		{
			return operand;
		}
		case negative:				/* -x --> -{x} */
		case bit_not:				/* ~x --> ~{x} */
		case address_of:			/* &x --> &{x} */
		case dereference:			/* *x --> *{x} */
		{
			return SymUnaryExpression.create(data_type, SymOperator.create(operator), operand);
		}
		case logic_not:				/* !x --> !(condition(x)) */
		{
			return this.sym_condition(operand, false);
		}
		case increment:				/* ++x --> x + 1 */
		{
			CConstant constant = new CConstant();
			constant.set_int(1);
			SymExpression roperand = SymConstant.create(constant);
			return SymBinaryExpression.create(data_type, SymOperator.create(COperator.arith_add), operand, roperand);
		}
		case decrement:				/* --x --> x - 1 */
		{
			CConstant constant = new CConstant();
			constant.set_int(1);
			SymExpression roperand = SymConstant.create(constant);
			return SymBinaryExpression.create(data_type, SymOperator.create(COperator.arith_sub), operand, roperand);
		}
		default: throw new IllegalArgumentException(source.generate_code());
		}
	}
	/**
	 * @param source
	 * @return x++ --> {x} & x-- --> {x}
	 * @throws Exception
	 */
	private SymNode parse_postfix_expression(AstPostfixExpression source) throws Exception {
		return this.parse_ast(source.get_operand());
	}
	/**
	 * @param source
	 * @return operator(loperand, roperand)
	 * @throws Exception
	 */
	private SymNode parse_binary_expression(AstBinaryExpression source) throws Exception {
		/* recursively solve the operator and operands */
		COperator operator = source.get_operator().get_operator();
		SymExpression loperand = (SymExpression) this.parse_ast(source.get_loperand());
		SymExpression roperand = (SymExpression) this.parse_ast(source.get_roperand());
		CType data_type = source.get_value_type();
		
		/* operator-oriented parsing algorithms */
		switch(operator) {
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
		case logic_and:
		case logic_or:
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:
		{
			return SymBinaryExpression.create(data_type, SymOperator.create(operator), loperand, roperand);
		}
		case arith_add_assign:
		case arith_sub_assign:
		case arith_mul_assign:
		case arith_div_assign:
		case arith_mod_assign:
		case bit_and_assign:
		case bit_or_assign:
		case bit_xor_assign:
		case left_shift_assign:
		case righ_shift_assign:			/* x += y --> x + y */
		{
			String operator_name = operator.toString();
			operator_name = operator_name.substring(0, operator_name.length() - 7);
			operator = COperator.valueOf(operator_name.strip());
			return SymBinaryExpression.create(data_type, SymOperator.create(operator), loperand, roperand);
		}
		case assign:					/* x = y --> cast(type, y) */
		{
			return SymUnaryExpression.create(data_type, SymOperator.create(COperator.assign), roperand);
		}
		default: throw new IllegalArgumentException(source.generate_code());
		}
	}
	/**
	 * @param source
	 * @return x[y] --> *(x + y)
	 * @throws Exception
	 */
	private SymNode parse_array_expression(AstArrayExpression source) throws Exception {
		SymExpression base_address = (SymExpression) this.parse_ast(source.get_array_expression());
		SymExpression bias_address = (SymExpression) this.parse_ast(source.get_dimension_expression());
		SymExpression address = SymBinaryExpression.create(base_address.get_data_type(), 
				SymOperator.create(COperator.arith_add), base_address, bias_address);
		return SymUnaryExpression.create(source.get_value_type(), SymOperator.create(COperator.dereference), address);
	}
	/**
	 * @param source
	 * @return (type)(= operand)
	 * @throws Exception
	 */
	private SymNode parse_cast_expression(AstCastExpression source) throws Exception {
		SymExpression operand = (SymExpression) this.parse_ast(source.get_expression());
		CType data_type = source.get_typename().get_type();
		return SymUnaryExpression.create(data_type, SymOperator.create(COperator.assign), operand);
	}
	/**
	 * @param source
	 * @return x1, x2, ..., xn --> {xn}
	 * @throws Exception
	 */
	private SymNode parse_comma_expression(AstCommaExpression source) throws Exception {
		int number = source.number_of_arguments();
		return this.parse_ast(source.get_expression(number - 1));
	}
	/**
	 * x ? y : z --> x * y + !x * z
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymNode parse_conditional_expression(AstConditionalExpression source) throws Exception {
		SymExpression x = (SymExpression) this.parse_ast(source.get_condition());
		SymExpression y = (SymExpression) this.parse_ast(source.get_true_branch());
		SymExpression z = (SymExpression) this.parse_ast(source.get_false_branch());
		
		SymExpression tcondition = this.sym_condition(x, true);
		SymExpression fcondition = this.sym_condition(x, false);
		
		SymExpression loperand = SymBinaryExpression.create(y.get_data_type(), 
				SymOperator.create(COperator.arith_mul), tcondition, y);
		SymExpression roperand = SymBinaryExpression.create(z.get_data_type(), 
				SymOperator.create(COperator.arith_mul), fcondition, z);
		
		return SymBinaryExpression.create(source.get_value_type(), 
				SymOperator.create(COperator.arith_add), loperand, roperand);
	}
	/**
	 * @param source
	 * @return recurively solving
	 * @throws Exception
	 */
	private SymNode parse_const_expression(AstConstExpression source) throws Exception {
		return this.parse_ast(source.get_expression());
	}
	/**
	 * @param source
	 * @return recurively solving
	 * @throws Exception
	 */
	private SymNode parse_paranth_expression(AstParanthExpression source) throws Exception {
		return this.parse_ast(source.get_sub_expression());
	}
	/**
	 * @param source
	 * @return sym_field as field.name
	 * @throws Exception
	 */
	private SymNode parse_field(AstField source) throws Exception {
		return SymField.create(source.get_name());
	}
	/**
	 * @param source
	 * @return field_expression as {body}.{field}
	 * @throws Exception
	 */
	private SymNode parse_field_expression(AstFieldExpression source) throws Exception {
		SymExpression body = (SymExpression) this.parse_ast(source.get_body());
		SymField field = (SymField) this.parse_ast(source.get_field());
		return SymFieldExpression.create(source.get_value_type(), body, field);
	}
	/**
	 * @param source
	 * @return argument_list |-- ({expression}*)
	 * @throws Exception
	 */
	private SymNode parse_argument_list(AstArgumentList source) throws Exception {
		List<SymExpression> arguments = new ArrayList<SymExpression>();
		for(int k = 0; k < source.number_of_arguments(); k++) {
			arguments.add((SymExpression) this.parse_ast(source.get_argument(k)));
		}
		return SymArgumentList.create(arguments);
	}
	/**
	 * @param source
	 * @return function argument_list
	 * @throws Exception
	 */
	private SymNode parse_fun_call_expression(AstFunCallExpression source) throws Exception {
		SymExpression function = (SymExpression) this.parse_ast(source.get_function());
		SymArgumentList arguments = (SymArgumentList) this.parse_ast(source.get_argument_list());
		return SymCallExpression.create(source.get_value_type(), function, arguments);
	}
	/**
	 * @param source
	 * @return initializer body | expression
	 * @throws Exception
	 */
	private SymNode parse_initializer(AstInitializer source) throws Exception {
		if(source.is_body())
			return this.parse_ast(source.get_body());
		else
			return this.parse_ast(source.get_expression());
	}
	/**
	 * @param source
	 * @return parse from initializer body
	 * @throws Exception
	 */
	private SymNode parse_initializer_body(AstInitializerBody source) throws Exception {
		List<SymExpression> elements = new ArrayList<SymExpression>();
		
		AstInitializerList list = source.get_initializer_list();
		for(int k = 0; k < list.number_of_initializer(); k++) {
			AstFieldInitializer field_initializer = list.get_initializer(k);
			elements.add((SymExpression) this.parse_ast(field_initializer));
		}
		
		return SymInitializerList.create(source.get_value_type(), elements);
	}
	/**
	 * @param source
	 * @return parse from field initializer
	 * @throws Exception
	 */
	private SymNode parse_field_initializer(AstFieldInitializer source) throws Exception {
		return this.parse_ast(source.get_initializer());
	}
	/**
	 * @param source
	 * @return this needs CRunTemplate to support sizeof computation
	 * @throws Exception
	 */
	private SymNode parse_sizeof_expression(AstSizeofExpression source) throws Exception {
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
		
		return SymConstant.create(constant);
	}
	
	/* parsing from CirNode */
	/**
	 * @param source
	 * @param cir_optimize whether to optimize the initialized value of default-value
	 * @return
	 * @throws Exception
	 */
	protected SymNode parse_cir(CirNode source, boolean cir_optimize) throws Exception {
		this.cir_optimize_switch = cir_optimize;
		return this.parse_cir(source);
	}
	/**
	 * @param source
	 * @return parse from C-intermediate representation
	 * @throws Exception
	 */
	private SymNode parse_cir(CirNode source) throws Exception {
		SymNode solution;
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(source instanceof CirNameExpression)
			solution = this.parse_name_expression((CirNameExpression) source);
		else if(source instanceof CirConstExpression)
			solution = this.parse_cir_const_expression((CirConstExpression) source);
		else if(source instanceof CirStringLiteral)
			solution = this.parse_string_literal((CirStringLiteral) source);
		else if(source instanceof CirAddressExpression)
			solution = this.parse_address_expression((CirAddressExpression) source);
		else if(source instanceof CirDeferExpression)
			solution = this.parse_defer_expression((CirDeferExpression) source);
		else if(source instanceof CirField)
			solution = this.parse_field((CirField) source);
		else if(source instanceof CirFieldExpression)
			solution = this.parse_field_expression((CirFieldExpression) source);
		else if(source instanceof CirCastExpression)
			solution = this.parse_cast_expression((CirCastExpression) source);
		else if(source instanceof CirDefaultValue)
			solution = this.parse_default_value((CirDefaultValue) source);
		else if(source instanceof CirInitializerBody)
			solution = this.parse_initializer_body((CirInitializerBody) source);
		else if(source instanceof CirArgumentList)
			solution = this.parse_argument_list((CirArgumentList) source);
		else if(source instanceof CirWaitExpression)
			solution = this.parse_wait_expression((CirWaitExpression) source);
		else if(source instanceof CirComputeExpression)
			solution = this.parse_compute_expression((CirComputeExpression) source);
		else
			throw new IllegalArgumentException(source.generate_code(true));
		if(!solution.has_source()) solution.set_source(source);
		return solution;
		
	}
	/**
	 * @param source
	 * @return (type, name_expression.unique_name)
	 * @throws Exception
	 */
	private SymNode parse_name_expression(CirNameExpression source) throws Exception {
		return SymIdentifier.create(source.get_data_type(), source.get_unique_name());
	}
	/**
	 * @param source
	 * @return constant
	 * @throws Exception
	 */
	private SymNode parse_cir_const_expression(CirConstExpression source) throws Exception {
		return SymConstant.create(source.get_constant());
	}
	/**
	 * @param source
	 * @return string literal
	 * @throws Exception
	 */
	private SymNode parse_string_literal(CirStringLiteral source) throws Exception {
		return SymLiteral.create(source.get_data_type(), source.get_literal());
	}
	/**
	 * @param source
	 * @return &x --> &{x}
	 * @throws Exception
	 */
	private SymNode parse_address_expression(CirAddressExpression source) throws Exception {
		SymExpression operand = (SymExpression) this.parse_cir(source.get_operand());
		SymOperator operator = SymOperator.create(COperator.address_of);
		return SymUnaryExpression.create(source.get_data_type(), operator, operand);
	}
	/**
	 * @param source
	 * @return *x --> *{x}
	 * @throws Exception
	 */
	private SymNode parse_defer_expression(CirDeferExpression source) throws Exception {
		SymExpression operand = (SymExpression) this.parse_cir(source.get_address());
		SymOperator operator = SymOperator.create(COperator.dereference);
		return SymUnaryExpression.create(source.get_data_type(), operator, operand);
	}
	/**
	 * @param source
	 * @return field.name --> {name}
	 * @throws Exception
	 */
	private SymNode parse_field(CirField source) throws Exception {
		return SymField.create(source.get_name());
	}
	/**
	 * @param source
	 * @return x.f --> {x}.{f}
	 * @throws Exception
	 */
	private SymNode parse_field_expression(CirFieldExpression source) throws Exception {
		SymExpression body = (SymExpression) this.parse_cir(source.get_body());
		SymField field = (SymField) this.parse_cir(source.get_field());
		return SymFieldExpression.create(source.get_data_type(), body, field);
	}
	/**
	 * @param source
	 * @return (type){x}
	 * @throws Exception
	 */
	private SymNode parse_cast_expression(CirCastExpression source) throws Exception {
		SymOperator operator = SymOperator.create(COperator.assign);
		SymExpression operand = (SymExpression) this.parse_cir(source.get_operand());
		return SymUnaryExpression.create(source.get_data_type(), operator, operand);
	}
	/**
	 * default value of the expression source
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymNode get_default_value(CType data_type, CirExpression source) throws Exception {
		String name = "default#" + source.get_node_id();
		return SymIdentifier.create(data_type, name);
	}
	/**
	 * @param source
	 * @return default_value --> (type, default#{CirNode.node_id})
	 * @throws Exception
	 */
	private SymNode parse_default_value(CirDefaultValue source) throws Exception {
		CType data_type = source.get_data_type();
		if(data_type != null)
			data_type = CTypeAnalyzer.get_value_type(data_type);
		else
			data_type = CBasicTypeImpl.void_type;
		
		if(this.cir_optimize_switch) {
			if(data_type instanceof CBasicType) {
				switch(((CBasicType) data_type).get_tag()) {
				case c_bool:		return this.parse_constant(Boolean.FALSE);
				case c_char: 
				case c_uchar:		return this.parse_constant(Character.valueOf('\0'));
				case c_short:
				case c_ushort:		return this.parse_constant(Short.valueOf((short) 0));
				case c_int:
				case c_uint:		return this.parse_constant(Integer.valueOf(0));
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:		return this.parse_constant(Long.valueOf(0L));
				case c_float:		return this.parse_constant(Float.valueOf(0.0f));
				case c_double:
				case c_ldouble:		return this.parse_constant(Double.valueOf(0.0));
				default:			return this.get_default_value(data_type, source);
				}
			}
			else {
				return this.get_default_value(data_type, source);
			}
		}
		else {
			return this.get_default_value(data_type, source);
		}
	}
	/**
	 * @param source
	 * @return {(expression)*}
	 * @throws Exception
	 */
	private SymNode parse_initializer_body(CirInitializerBody source) throws Exception {
		List<SymExpression> elements = new ArrayList<SymExpression>();
		for(int k = 0; k < source.number_of_elements(); k++) {
			elements.add((SymExpression) this.parse_cir(source.get_element(k)));
		}
		return SymInitializerList.create(source.get_data_type(), elements);
	}
	/**
	 * @param source
	 * @return ({expression}*)
	 * @throws Exception
	 */
	private SymNode parse_argument_list(CirArgumentList source) throws Exception {
		List<SymExpression> arguments = new ArrayList<SymExpression>();
		for(int k = 0; k < source.number_of_arguments(); k++) {
			arguments.add((SymExpression) this.parse_cir(source.get_argument(k)));
		}
		return SymArgumentList.create(arguments);
	}
	/**
	 * @param source
	 * @return wait expression --> function argument_list
	 * @throws Exception
	 */
	private SymNode parse_wait_expression(CirWaitExpression source) throws Exception {
		CirStatement wait_statement = source.statement_of();
		CirExecution wait_execution = wait_statement.get_tree().get_localizer().get_execution(wait_statement);
		CirExecution call_execution = wait_execution.get_graph().get_execution(wait_execution.get_id() - 1);
		CirCallStatement call_statement = (CirCallStatement) call_execution.get_statement();
		
		SymExpression function = (SymExpression) this.parse_cir(call_statement.get_function());
		SymArgumentList arguments = (SymArgumentList) this.parse_cir(call_statement.get_arguments());
		
		return SymCallExpression.create(source.get_data_type(), function, arguments);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymNode parse_compute_expression(CirComputeExpression source) throws Exception {
		/* 1. parse from the operands */
		List<SymExpression> operands = new ArrayList<SymExpression>();
		COperator operator = source.get_operator();
		for(int k = 0; k < source.number_of_operand(); k++) {
			operands.add((SymExpression) this.parse_cir(source.get_operand(k)));
		}
		
		/* 2. construct the computational expression */
		switch(operator) {
		case positive:
		case negative:
		case bit_not:
		case logic_not:
		{
			return SymUnaryExpression.create(source.get_data_type(), SymOperator.create(operator), operands.get(0));
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
			return SymBinaryExpression.create(source.get_data_type(), SymOperator.create(operator), operands.get(0), operands.get(1));
		}
		default: throw new IllegalArgumentException(source.generate_code(true));
		}
		
	}
	
	/* parsing from instance */
	/**
	 * @param source
	 * @return boolean|character|short|integer|long|float|double|CConstant
	 * @throws Exception
	 */
	protected SymConstant parse_constant(Object source) throws Exception {
		SymConstant solution; CConstant constant = new CConstant();
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(source instanceof Boolean) 
			constant.set_bool(((Boolean) source).booleanValue());
		else if(source instanceof Character)
			constant.set_char(((Character) source).charValue());
		else if(source instanceof Short)
			constant.set_int(((Short) source).intValue());
		else if(source instanceof Integer)
			constant.set_int(((Integer) source).intValue());
		else if(source instanceof Long)
			constant.set_long(((Long) source).longValue());
		else if(source instanceof Float)
			constant.set_float(((Float) source).floatValue());
		else if(source instanceof Double)
			constant.set_double(((Double) source).doubleValue());
		else if(source instanceof CConstant)
			constant = (CConstant) source;
		else
			throw new IllegalArgumentException(source.getClass().getName());
		solution = SymConstant.create(constant);
		solution.set_source(source);
		return solution;
	}
	
	/* parsing from statement */
	/**
	 * @param source
	 * @return (int, do#{source.toString())
	 * @throws Exception
	 */
	protected SymIdentifier parse_statement(CirExecution source) throws Exception {
		String name = "do#" + source.toString();
		SymIdentifier expression = SymIdentifier.create(CBasicTypeImpl.int_type, name);
		expression.set_source(source);
		return expression;
	}
	
}
