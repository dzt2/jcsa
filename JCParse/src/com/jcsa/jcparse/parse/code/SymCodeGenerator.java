package com.jcsa.jcparse.parse.code;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.sym.SymArgumentList;
import com.jcsa.jcparse.lang.sym.SymBinaryExpression;
import com.jcsa.jcparse.lang.sym.SymCallExpression;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymField;
import com.jcsa.jcparse.lang.sym.SymFieldExpression;
import com.jcsa.jcparse.lang.sym.SymIdentifier;
import com.jcsa.jcparse.lang.sym.SymInitializerList;
import com.jcsa.jcparse.lang.sym.SymLiteral;
import com.jcsa.jcparse.lang.sym.SymNode;
import com.jcsa.jcparse.lang.sym.SymOperator;
import com.jcsa.jcparse.lang.sym.SymUnaryExpression;

/**
 * Code generator for describing SymNode.
 * 
 * @author yukimula
 *
 */
public class SymCodeGenerator {
	
	/* definitions */
	/** whether to generate simplified code for SymNodes **/
	private boolean simplified;
	/** used to preserve the code generated from SymNode **/
	private StringBuilder buffer;
	/**
	 * private constructor for generating code of SymNode
	 */
	private SymCodeGenerator() { this.buffer = new StringBuilder(); this.simplified = false; }
	
	/* singleton mode */
	/** singleton instance of the code generator for generating SymNode **/
	private static final SymCodeGenerator generator = new SymCodeGenerator();
	
	/* syntax-directed translation rules */
	/**
	 * generate the code of symbolic representation recursively
	 * @param node
	 * @throws Exception
	 */
	private void gen_sym_node(SymNode node) throws Exception {
		if(node == null)
			throw new IllegalArgumentException("Invalid node: null");
		else if(node instanceof SymIdentifier) 
			this.gen_identifier((SymIdentifier) node);
		else if(node instanceof SymConstant)
			this.gen_constant((SymConstant) node);
		else if(node instanceof SymLiteral)
			this.gen_literal((SymLiteral) node);
		else if(node instanceof SymUnaryExpression)
			this.gen_unary_expression((SymUnaryExpression) node);
		else if(node instanceof SymBinaryExpression)
			this.gen_binary_expression((SymBinaryExpression) node);
		else if(node instanceof SymArgumentList)
			this.gen_argument_list((SymArgumentList) node);
		else if(node instanceof SymCallExpression)
			this.gen_call_expression((SymCallExpression) node);
		else if(node instanceof SymField)
			this.gen_field((SymField) node);
		else if(node instanceof SymFieldExpression)
			this.gen_field_expression((SymFieldExpression) node);
		else if(node instanceof SymOperator)
			this.gen_operator((SymOperator) node);
		else if(node instanceof SymInitializerList)
			this.gen_initializer_list((SymInitializerList) node);
		else
			throw new IllegalArgumentException(node.getClass().getSimpleName());
	}
	
	/* unit package */
	private void gen_argument_list(SymArgumentList node) throws Exception {
		this.buffer.append("(");
		for(int k = 0; k < node.number_of_arguments(); k++) {
			this.gen_sym_node(node.get_argument(k));
			if(k < node.number_of_arguments() - 1) {
				this.buffer.append(", ");
			}
		}
		this.buffer.append(")");
	}
	private void gen_field(SymField node) throws Exception {
		this.buffer.append(node.get_name());
	}
	private void gen_operator(SymOperator node) throws Exception {
		String code;
		switch(node.get_operator()) {
		case arith_add:		code = "+";		break;
		case arith_sub:		code = "-";		break;
		case arith_mul:		code = "*";		break;
		case arith_div:		code = "/";		break;
		case arith_mod:		code = "%";		break;
		case negative:		code = "-";		break;
		case bit_and:		code = "&";		break;
		case bit_or:		code = "|";		break;
		case bit_xor:		code = "^";		break;
		case left_shift:	code = "<<";	break;
		case righ_shift:	code = ">>";	break;
		case bit_not:		code = "~";		break;
		case logic_and:		code = "&&";	break;
		case logic_or:		code = "||";	break;
		case logic_not:		code = "!";		break;
		case greater_tn:	code = ">";		break;
		case greater_eq:	code = ">=";	break;
		case smaller_tn:	code = "<";		break;
		case smaller_eq:	code = "<=";	break;
		case equal_with:	code = "==";	break;
		case not_equals:	code = "!=";	break;
		case address_of:	code = "&";		break;
		case dereference:	code = "*";		break;
		case assign:		
		{
			SymExpression parent = (SymExpression) node.get_parent();
			if(parent == null) {
				code = "(void)";
			}
			else {
				CType data_type = parent.get_data_type();
				if(data_type == null) {
					code = "(void)";
				}
				else {
					code = "(" + data_type.generate_code() + ")";
				}
			}
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + node.get_operator());
		}
		this.buffer.append(code);
	}
	
	/* basic expression */
	private void gen_identifier(SymIdentifier node) throws Exception {
		int index = node.get_name().indexOf('#');
		String name = node.get_name().strip(), code;
		
		if(this.simplified) {
			if(index > 0) {
				name = name.substring(0, index).strip();
				if(name.equals("default")) {
					/* CirDefaultValue */
					code = "?";
				}
				else if(name.equals("return")) {
					/* CirReturnPoint <-- return#function_id */
					code = name;	
				}
				else {
					/* CirDeclarator | CirIdentifier by AstIdExpression */
					code = name;
				}
			}
			else {
				/* CirImplicator */
				AstNode ast_source;
				ast_source = node.get_ast_source();
				if(ast_source == null) {
					if(node.get_cir_source() != null)
						ast_source = node.get_cir_source().get_ast_source();
				}
				if(ast_source == null) {
					if(node.get_cir_source() != null) {
						CirTree cir_tree = node.get_cir_source().get_tree();
						AstTree ast_tree = cir_tree.get_root().get_ast_source().get_tree();
						int ast_key = Integer.parseInt(node.get_name().substring(index + 1).strip());
						ast_source = ast_tree.get_node(ast_key);
					}
				}
				
				/* non-ast-source based implicator */
				if(ast_source == null) {
					code = name;
				}
				/* extract expression code of AST */
				else {
					AstExpression expression;
					if(ast_source instanceof AstCaseStatement) {
						expression = ((AstCaseStatement) ast_source).get_expression();
					}
					else if(ast_source instanceof AstSwitchStatement) {
						expression = ((AstSwitchStatement) ast_source).get_condition();
					}
					else {
						expression = (AstExpression) ast_source;
					}
					expression = CTypeAnalyzer.get_expression_of(expression);
					code = expression.generate_code();
				}
			}
		}
		else {
			code = name;
		}
		
		/* remove spaces in the code generated */
		for(int k = 0; k < code.length(); k++) {
			char ch = code.charAt(k);
			if(Character.isWhitespace(ch)) {
				ch = ' ';
			}
			this.buffer.append(ch);
		}
	}
	private void gen_constant(SymConstant node) throws Exception {
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
	private void gen_literal(SymLiteral node) throws Exception {
		String literal = node.get_literal();
		this.buffer.append("\"");
		for(int k = 0; k < literal.length(); k++) {
			char ch = literal.charAt(k);
			if(Character.isWhitespace(ch)) 
				ch = ' ';
			this.buffer.append(ch);
		}
		this.buffer.append("\"");
	}
	
	/* special expression */
	private void gen_field_expression(SymFieldExpression node) throws Exception {
		this.buffer.append("(");
		this.gen_sym_node(node.get_body());
		this.buffer.append(").");
		this.gen_sym_node(node.get_field());
	}
	private void gen_initializer_list(SymInitializerList node) throws Exception {
		this.buffer.append("{");
		for(int k = 0; k < node.number_of_elements(); k++) {
			this.gen_sym_node(node.get_element(k));
			if(k < node.number_of_elements() - 1) {
				this.buffer.append(", ");
			}
		}
		this.buffer.append("}");
	}
	private void gen_call_expression(SymCallExpression node) throws Exception {
		this.gen_sym_node(node.get_function());
		this.gen_sym_node(node.get_argument_list());
	}
	
	/* general expression */
	private void gen_unary_expression(SymUnaryExpression node) throws Exception {
		this.gen_sym_node(node.get_operator());
		this.buffer.append("(");
		this.gen_sym_node(node.get_operand());
		this.buffer.append(")");
	}
	private void gen_binary_expression(SymBinaryExpression node) throws Exception {
		this.buffer.append("(");
		this.gen_sym_node(node.get_loperand());
		this.buffer.append(") ");
		this.gen_sym_node(node.get_operator());
		this.buffer.append(" (");
		this.gen_sym_node(node.get_roperand());
		this.buffer.append(")");
	}
	
	/**
	 * @param node
	 * @param simplified whether to use simplified code w.r.t. the node
	 * @return
	 * @throws Exception
	 */
	public static String generate_code(SymNode node, boolean simplified) throws Exception {
		generator.simplified = simplified;
		generator.buffer.setLength(0);
		generator.gen_sym_node(node);
		return generator.buffer.toString();
	}
	
}
