package com.jcsa.jcparse.lang.code;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirArithExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirBitwsExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeclarator;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirIdentifier;
import com.jcsa.jcparse.lang.irlang.expr.CirImplicator;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirLogicExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirRelationExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReturnPoint;
import com.jcsa.jcparse.lang.irlang.expr.CirStringLiteral;
import com.jcsa.jcparse.lang.irlang.expr.CirType;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBegStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirDefaultStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionBody;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.unit.CirTransitionUnit;

/**
 * Used to generate the C-like intermediate representation for source code.
 * 
 * @author yukimula
 *
 */
public class CirCodeGenerator {
	
	/** whether the generated code is unique with scoping information **/
	private boolean unique_code;
	/** the String buffer used as cache to preserve the generated code **/
	private StringBuilder buffer;
	
	/**
	 * create a code generator that generate the code for C-like intermediate
	 * representation with specified unique switch.
	 * @param unique_code true when the generated identifier is unique-name rather than simple name
	 */
	private CirCodeGenerator() {
		this.buffer = new StringBuilder();
	}
	/** the singleton used to generate code that describes in CIR language **/
	public static final CirCodeGenerator generator = new CirCodeGenerator();
	
	/* type generation */
	private void parse(CType type) throws Exception {
		this.buffer.append("type");
	}
	
	/* generation methods */
	private void parse(CirNode node) throws Exception {
		if(node == null)
			throw new IllegalArgumentException("invalid node: null");
		else if(node instanceof CirIdentifier)
			this.parse_identifier((CirIdentifier) node);
		else if(node instanceof CirDeclarator)
			this.parse_declarator((CirDeclarator) node);
		else if(node instanceof CirImplicator)
			this.parse_implicator((CirImplicator) node);
		else if(node instanceof CirReturnPoint)
			this.parse_return_point((CirReturnPoint) node);
		else if(node instanceof CirDeferExpression)
			this.parse_defer_expression((CirDeferExpression) node);
		else if(node instanceof CirFieldExpression)
			this.parse_field_expression((CirFieldExpression) node);
		else if(node instanceof CirField)
			this.parse_field((CirField) node);
		else if(node instanceof CirConstExpression)
			this.parse_const_expression((CirConstExpression) node);
		else if(node instanceof CirStringLiteral)
			this.parse_string_literal((CirStringLiteral) node);
		else if(node instanceof CirArithExpression)
			this.parse_arith_expression((CirArithExpression) node);
		else if(node instanceof CirBitwsExpression)
			this.parse_bitws_expression((CirBitwsExpression) node);
		else if(node instanceof CirLogicExpression)
			this.parse_logic_expression((CirLogicExpression) node);
		else if(node instanceof CirRelationExpression)
			this.parse_relation_expression((CirRelationExpression) node);
		else if(node instanceof CirAddressExpression)
			this.parse_address_expression((CirAddressExpression) node);
		else if(node instanceof CirCastExpression)
			this.parse_cast_expression((CirCastExpression) node);
		else if(node instanceof CirType)
			this.parse_type((CirType) node);
		else if(node instanceof CirDefaultValue)
			this.parse_default_value((CirDefaultValue) node);
		else if(node instanceof CirWaitExpression)
			this.parse_wait_expression((CirWaitExpression) node);
		else if(node instanceof CirInitializerBody)
			this.parse_initializer_body((CirInitializerBody) node);
		else if(node instanceof CirAssignStatement)
			this.parse_assign_statement((CirAssignStatement) node);
		else if(node instanceof CirCallStatement)
			this.parse_call_statement((CirCallStatement) node);
		else if(node instanceof CirArgumentList)
			this.parse_argument_list((CirArgumentList) node);
		else if(node instanceof CirGotoStatement)
			this.parse_goto_statement((CirGotoStatement) node);
		else if(node instanceof CirCaseStatement)
			this.parse_case_statement((CirCaseStatement) node);
		else if(node instanceof CirLabel)
			this.parse_label((CirLabel) node);
		else if(node instanceof CirLabelStatement)
			this.parse_label_statement((CirLabelStatement) node);
		else if(node instanceof CirIfEndStatement)
			this.parse_if_end_statement((CirIfEndStatement) node);
		else if(node instanceof CirIfStatement)
			this.parse_if_statement((CirIfStatement) node);
		else if(node instanceof CirDefaultStatement)
			this.parse_default_statement((CirDefaultStatement) node);
		else if(node instanceof CirCaseEndStatement)
			this.parse_case_end_statement((CirCaseEndStatement) node);
		else if(node instanceof CirBegStatement)
			this.parse_beg_statement((CirBegStatement) node);
		else if(node instanceof CirEndStatement)
			this.parse_end_statement((CirEndStatement) node);
		else if(node instanceof CirFunctionBody)
			this.parse_function_body((CirFunctionBody) node);
		else if(node instanceof CirFunctionDefinition)
			this.parse_function_definition((CirFunctionDefinition) node);
		else if(node instanceof CirTransitionUnit)
			this.parse_transition_unit((CirTransitionUnit) node);
		else throw new IllegalArgumentException("unsupport: " + node.getClass().getSimpleName());
	}
	private void parse_identifier(CirIdentifier node) throws Exception {
		if(this.unique_code)
			this.buffer.append(node.get_unique_name());
		else this.buffer.append(node.get_name());
	}
	private void parse_declarator(CirDeclarator node) throws Exception {
		if(this.unique_code)
			this.buffer.append(node.get_unique_name());
		else this.buffer.append(node.get_name());
	}
	private void parse_implicator(CirImplicator node) throws Exception {
		if(this.unique_code)
			this.buffer.append(node.get_unique_name());
		else this.buffer.append(node.get_name());
	}
	private void parse_return_point(CirReturnPoint node) throws Exception {
		if(this.unique_code)
			this.buffer.append(node.get_unique_name());
		else this.buffer.append(node.get_name());
	}
	private void parse_defer_expression(CirDeferExpression node) throws Exception {
		this.buffer.append("*(");
		this.parse(node.get_address());
		this.buffer.append(")");
	}
	private void parse_field_expression(CirFieldExpression node) throws Exception {
		this.buffer.append("(");
		this.parse(node.get_body());
		this.buffer.append(").");
		this.parse(node.get_field());
	}
	private void parse_field(CirField node) throws Exception {
		this.buffer.append(node.get_name());
	}
	private void parse_address_expression(CirAddressExpression node) throws Exception {
		this.buffer.append("&(");
		this.parse(node.get_operand());
		this.buffer.append(")");
	}
	private void parse_cast_expression(CirCastExpression node) throws Exception {
		this.buffer.append("(");
		this.parse(node.get_type());
		this.buffer.append(") ");
		this.parse(node.get_operand());
	}
	private void parse_type(CirType node) throws Exception {
		this.parse(node.get_typename());
	}
	private void parse_const_expression(CirConstExpression node) throws Exception {
		this.buffer.append(node.get_constant().toString());
	}
	private void parse_string_literal(CirStringLiteral node) throws Exception {
		this.buffer.append("\"");
		this.buffer.append(node.get_literal());
		this.buffer.append("\"");
	}
	private void parse_default_value(CirDefaultValue node) throws Exception {
		this.buffer.append("?");
	}
	private void parse_wait_expression(CirWaitExpression node) throws Exception {
		this.buffer.append("wait ");
		this.parse(node.get_function());
	}
	private void parse_initializer_body(CirInitializerBody node) throws Exception {
		this.buffer.append("{ ");
		for(int k = 0; k < node.number_of_elements(); k++) {
			this.parse(node.get_element(k));
			this.buffer.append(", ");
		}
		this.buffer.append("}");
	}
	private void parse_arith_expression(CirArithExpression node) throws Exception {
		if(node.number_of_operand() == 1) {
			this.buffer.append("-(");
			this.parse(node.get_operand(0));
			this.buffer.append(")");
		}
		else {
			this.buffer.append("(");
			this.parse(node.get_operand(0));
			this.buffer.append(")");
			
			switch(node.get_operator()) {
			case arith_add:	this.buffer.append(" + "); break;
			case arith_sub:	this.buffer.append(" - "); break;
			case arith_mul:	this.buffer.append(" * "); break;
			case arith_div:	this.buffer.append(" / "); break;
			case arith_mod:	this.buffer.append(" % "); break;
			default: throw new IllegalArgumentException(
					"Unsupport: " + node.get_operator());
			}
			
			this.buffer.append("(");
			this.parse(node.get_operand(1));
			this.buffer.append(")");
		}
	}
	private void parse_bitws_expression(CirBitwsExpression node) throws Exception {
		if(node.number_of_operand() == 1) {
			this.buffer.append("~(");
			this.parse(node.get_operand(0));
			this.buffer.append(")");
		}
		else {
			this.buffer.append("(");
			this.parse(node.get_operand(0));
			this.buffer.append(")");
			
			switch(node.get_operator()) {
			case bit_and:		this.buffer.append(" & "); break;
			case bit_or:		this.buffer.append(" | "); break;
			case bit_xor:		this.buffer.append(" ^ "); break;
			case left_shift:	this.buffer.append(" << "); break;
			case righ_shift:	this.buffer.append(" >> "); break;
			default: throw new IllegalArgumentException(
					"Unsupport: " + node.get_operator());
			}
			
			this.buffer.append("(");
			this.parse(node.get_operand(1));
			this.buffer.append(")");
		}
	}
	private void parse_logic_expression(CirLogicExpression node) throws Exception {
		this.buffer.append("!(");
		this.parse(node.get_operand(0));
		this.buffer.append(")");
	}
	private void parse_relation_expression(CirRelationExpression node) throws Exception {
		this.buffer.append("(");
		this.parse(node.get_operand(0));
		this.buffer.append(")");
		
		switch(node.get_operator()) {
		case greater_tn:	this.buffer.append(" > ");	break;
		case greater_eq:	this.buffer.append(" >= ");	break;
		case smaller_tn:	this.buffer.append(" < ");	break;
		case smaller_eq:	this.buffer.append(" <= ");	break;
		case equal_with:	this.buffer.append(" == ");	break;
		case not_equals:	this.buffer.append(" != ");	break;
		default: throw new IllegalArgumentException("Unsupport: " + node.get_operator());
		}
		
		this.buffer.append("(");
		this.parse(node.get_operand(1));
		this.buffer.append(")");
	}
	private void parse_assign_statement(CirAssignStatement node) throws Exception {
		this.parse(node.get_lvalue());
		this.buffer.append(" = ");
		this.parse(node.get_rvalue());
		this.buffer.append(";");
	}
	private void parse_call_statement(CirCallStatement node) throws Exception {
		this.buffer.append("call ");
		this.parse(node.get_function());
		this.buffer.append(" ");
		this.parse(node.get_arguments());
		this.buffer.append(";");
	}
	private void parse_argument_list(CirArgumentList node) throws Exception {
		this.buffer.append("(");
		for(int k = 0; k < node.number_of_arguments(); k++) {
			this.parse(node.get_argument(k));
			if(k < node.number_of_arguments() - 1)
				this.buffer.append(", ");
		}
		this.buffer.append(")");
	}
	private void parse_case_statement(CirCaseStatement node) throws Exception {
		this.buffer.append("case ");
		this.parse(node.get_condition());
		this.buffer.append(" or goto ");
		this.parse(node.get_false_label());
		this.buffer.append(":");
	}
	private void parse_label(CirLabel node) throws Exception {
		this.buffer.append("[");
		this.buffer.append(node.get_target_node_id());
		this.buffer.append("]");
	}
	private void parse_goto_statement(CirGotoStatement node) throws Exception {
		this.buffer.append("goto ");
		this.parse(node.get_label());
		this.buffer.append(":");
	}
	private void parse_if_statement(CirIfStatement node) throws Exception {
		this.buffer.append("if ");
		this.parse(node.get_condition());
		this.buffer.append(" then ");
		this.parse(node.get_true_label());
		this.buffer.append(" else ");
		this.parse(node.get_false_label());
		this.buffer.append(":");
	}
	private void parse_beg_statement(CirBegStatement node) throws Exception {
		this.buffer.append("begin");
	}
	private void parse_end_statement(CirEndStatement node) throws Exception {
		this.buffer.append("end");
	}
	private void parse_if_end_statement(CirIfEndStatement node) throws Exception {
		this.buffer.append("end if");
	}
	private void parse_case_end_statement(CirCaseEndStatement node) throws Exception {
		this.buffer.append("end case");
	}
	private void parse_default_statement(CirDefaultStatement node) throws Exception {
		this.buffer.append("default:");
	}
	private void parse_label_statement(CirLabelStatement node) throws Exception {
		this.buffer.append("#label: ");
	}
	private void parse_function_body(CirFunctionBody node) throws Exception {
		this.buffer.append("{\n");
		for(int k = 0; k < node.number_of_statements(); k++) {
			CirStatement statement = node.get_statement(k);
			this.buffer.append("\t[");
			this.buffer.append(statement.get_node_id());
			this.buffer.append("] ");
			this.parse(statement);
			this.buffer.append("\n");
		}
		this.buffer.append("}\n");
	}
	private void parse_function_definition(CirFunctionDefinition node) throws Exception {
		this.buffer.append("function ");
		this.parse(node.get_declarator());
		this.buffer.append("():\n");
		this.parse(node.get_body());
	}
	private void parse_transition_unit(CirTransitionUnit node) throws Exception {
		for(int k = 0; k < node.number_of_units(); k++) {
			this.buffer.append("\n");
			this.parse(node.get_unit(k));
		}
		this.buffer.append("\n");
	}
	
	/**
	 * @param simplified
	 * @param node
	 * @return code being generated to describe the structure of C-intermediate
	 * 			representation language specified by the node
	 * @throws Exception
	 */
	protected static String generate(boolean simplified, CirNode node) throws Exception {
		generator.unique_code = simplified;
		generator.buffer.setLength(0);
		generator.parse(node);
		return generator.buffer.toString();
	}
	
}
