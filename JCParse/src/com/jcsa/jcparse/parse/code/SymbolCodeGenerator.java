package com.jcsa.jcparse.parse.code;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.symbol.SymbolArgumentList;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolCallExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolField;
import com.jcsa.jcparse.lang.symbol.SymbolFieldExpression;
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;
import com.jcsa.jcparse.lang.symbol.SymbolInitializerList;
import com.jcsa.jcparse.lang.symbol.SymbolLiteral;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.lang.symbol.SymbolOperator;
import com.jcsa.jcparse.lang.symbol.SymbolUnaryExpression;

/**
 * It is used to generate code for SymbolNode.
 *
 * @author yukimula
 *
 */
public class SymbolCodeGenerator {

	/* attributes */
	/** whether to generate simplified code for SymNodes **/
	private boolean simplified;
	/** used to preserve the code generated from SymNode **/
	private StringBuilder buffer;

	/* constructor and singleton */
	/** private constructor for singleton mode **/
	private SymbolCodeGenerator() {
		this.simplified = false;
		this.buffer = new StringBuilder();
	}
	/** singleton of code generator for generating code for SymbolNode **/
	private static SymbolCodeGenerator generator = new SymbolCodeGenerator();

	/* utility methods */
	/**
	 * @param node
	 * @param simplified whether to generate simplified code of SymbolicNode
	 * @return code to describe SymbolNode
	 * @throws Exception
	 */
	public static String generate_code(SymbolNode node, boolean simplified) throws Exception {
		generator.buffer.setLength(0);
		generator.simplified = simplified;
		generator.gen_node(node);
		return generator.buffer.toString();
	}

	/* generator methods */
	/**
	 * recursively generate the code of symbolic node
	 * @param node
	 * @throws Exception
	 */
	private void gen_node(SymbolNode node) throws Exception {
		if(node == null)
			throw new IllegalArgumentException("Invalid node: null");
		else if(node instanceof SymbolArgumentList)
			this.gen_argument_list((SymbolArgumentList) node);
		else if(node instanceof SymbolField)
			this.gen_field((SymbolField) node);
		else if(node instanceof SymbolOperator)
			this.gen_operator((SymbolOperator) node);
		else if(node instanceof SymbolIdentifier)
			this.gen_identifier((SymbolIdentifier) node);
		else if(node instanceof SymbolConstant)
			this.gen_constant((SymbolConstant) node);
		else if(node instanceof SymbolLiteral)
			this.gen_literal((SymbolLiteral) node);
		else if(node instanceof SymbolBinaryExpression)
			this.gen_binary_expression((SymbolBinaryExpression) node);
		else if(node instanceof SymbolUnaryExpression)
			this.gen_unary_expression((SymbolUnaryExpression) node);
		else if(node instanceof SymbolCallExpression)
			this.gen_call_expression((SymbolCallExpression) node);
		else if(node instanceof SymbolFieldExpression)
			this.gen_field_expression((SymbolFieldExpression) node);
		else if(node instanceof SymbolInitializerList)
			this.gen_initializer_list((SymbolInitializerList) node);
		else
			throw new IllegalArgumentException("Unsupported: " + node.getClass().getSimpleName());
	}
	/**
	 * @param code
	 * @return
	 * @throws Exception
	 */
	private static String strip_code(String code) throws Exception {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < code.length(); k++) {
			char ch = code.charAt(k);
			if(Character.isWhitespace(ch)) {
				ch = ' ';
			}
			buffer.append(ch);
		}
		return buffer.toString();
	}

	/* implement syntax-directed code generation */
	private void gen_argument_list(SymbolArgumentList node) throws Exception {
		this.buffer.append("(");
		for(int k = 0; k < node.number_of_arguments(); k++) {
			this.gen_node(node.get_argument(k));
			if(k < node.number_of_arguments() - 1) {
				this.buffer.append(", ");
			}
		}
		this.buffer.append(")");
	}
	private void gen_field(SymbolField node) throws Exception {
		this.buffer.append(node.get_name());
	}
	private void gen_operator(SymbolOperator node) throws Exception {
		switch(node.get_operator()) {
		case negative:		this.buffer.append("-");	break;
		case bit_not:		this.buffer.append("~");	break;
		case logic_not:		this.buffer.append("!");	break;
		case address_of:	this.buffer.append("&");	break;
		case dereference:	this.buffer.append("*");	break;
		case assign:		this.buffer.append("");		break;
		case arith_add:		this.buffer.append("+");	break;
		case arith_sub:		this.buffer.append("-");	break;
		case arith_mul:		this.buffer.append("*");	break;
		case arith_div:		this.buffer.append("/");	break;
		case arith_mod:		this.buffer.append("%");	break;
		case bit_and:		this.buffer.append("&");	break;
		case bit_or:		this.buffer.append("|");	break;
		case bit_xor:		this.buffer.append("^");	break;
		case left_shift:	this.buffer.append("<<");	break;
		case righ_shift:	this.buffer.append(">>");	break;
		case logic_and:		this.buffer.append("&&");	break;
		case logic_or:		this.buffer.append("||");	break;
		case greater_tn:	this.buffer.append(">");	break;
		case greater_eq:	this.buffer.append(">=");	break;
		case smaller_tn:	this.buffer.append("<");	break;
		case smaller_eq:	this.buffer.append("<=");	break;
		case equal_with:	this.buffer.append("==");	break;
		case not_equals:	this.buffer.append("!=");	break;
		default: throw new IllegalArgumentException("Unsupport: " + node.get_operator());
		}
	}
	private void gen_identifier(SymbolIdentifier node) throws Exception {
		String name = node.get_name();
		int index = name.indexOf('#');
		String base = name.substring(0, index).trim();
		String bias = name.substring(index + 1).trim();
		Object source = node.get_source();

		if(this.simplified) {
			if(base.isEmpty()) {
				int ast_key = Integer.parseInt(bias);
				if(source instanceof AstNode) {
					AstNode ast_source = ((AstNode) source).get_tree().get_node(ast_key);
					if(ast_source instanceof AstSwitchStatement) {
						ast_source = ((AstSwitchStatement) ast_source).get_condition();
					}
					else if(ast_source instanceof AstCaseStatement) {
						ast_source = ((AstCaseStatement) ast_source).get_expression();
					}
					AstExpression expression = (AstExpression) ast_source;
					expression = CTypeAnalyzer.get_expression_of(expression);
					this.buffer.append(strip_code(expression.generate_code()));
				}
				else if(source instanceof CirNode) {
					if(((CirNode) source).get_ast_source() != null) {
						AstNode ast_source = ((CirNode) source).get_ast_source();
						ast_source = ast_source.get_tree().get_node(ast_key);
						if(ast_source instanceof AstSwitchStatement) {
							ast_source = ((AstSwitchStatement) ast_source).get_condition();
						}
						else if(ast_source instanceof AstCaseStatement) {
							ast_source = ((AstCaseStatement) ast_source).get_expression();
						}
						AstExpression expression = (AstExpression) ast_source;
						expression = CTypeAnalyzer.get_expression_of(expression);
						this.buffer.append(strip_code(expression.generate_code()));
					}
					else {
						this.buffer.append(strip_code(((CirNode) source).generate_code(true)));
					}
				}
				else {
					this.buffer.append(name);
				}
			}
			else if(base.equals("default")) {
				this.buffer.append("default#(").append(strip_code(node.get_data_type().generate_code())).append(")");
			}
			else if(base.equals("do")) {
				this.buffer.append(bias);
			}
			else if(base.equals("return")) {
				this.buffer.append(base);
			}
			else {
				this.buffer.append(base);
			}
		}
		else {
			this.buffer.append(name);
		}
	}
	private void gen_constant(SymbolConstant node) throws Exception {
		CConstant constant = node.get_constant();
		String code;
		switch(constant.get_type().get_tag()) {
		case c_bool:
		{
			code = constant.get_bool().toString();
			break;
		}
		case c_char:
		case c_uchar:
		{
			code = "" + ((int) constant.get_char().charValue());
			break;
		}
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
		{
			code = constant.get_integer().toString();
			break;
		}
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
		{
			code = constant.get_long().toString();
			break;
		}
		case c_float:
		{
			code = constant.get_float().toString();
			break;
		}
		case c_double:
		case c_ldouble:
		{
			code = constant.get_double().toString();
			break;
		}
		default:
		{
			throw new IllegalArgumentException("Invalid: " + constant.get_type().generate_code());
		}
		}
		this.buffer.append(code);
	}
	private void gen_literal(SymbolLiteral node) throws Exception {
		this.buffer.append("\"").append(strip_code(node.get_literal())).append("\"");
	}
	private void gen_binary_expression(SymbolBinaryExpression node) throws Exception {
		this.buffer.append("(");
		this.gen_node(node.get_loperand());
		this.buffer.append(") ");
		this.gen_node(node.get_operator());
		this.buffer.append(" (");
		this.gen_node(node.get_roperand());
		this.buffer.append(")");
	}
	private void gen_unary_expression(SymbolUnaryExpression node) throws Exception {
		this.gen_node(node.get_operator());
		this.buffer.append("(");
		this.gen_node(node.get_operand());
		this.buffer.append(")");
	}
	private void gen_field_expression(SymbolFieldExpression node) throws Exception {
		this.buffer.append("(");
		this.gen_node(node.get_body());
		this.buffer.append(").");
		this.gen_node(node.get_field());
	}
	private void gen_call_expression(SymbolCallExpression node) throws Exception {
		this.gen_node(node.get_function());
		this.gen_node(node.get_argument_list());
	}
	private void gen_initializer_list(SymbolInitializerList node) throws Exception {
		this.buffer.append("{");
		for(int k = 0; k < node.number_of_elements(); k++) {
			this.gen_node(node.get_element(k));
			if(k < node.number_of_elements() - 1)
				this.buffer.append(", ");
		}
		this.buffer.append("}");
	}


}
