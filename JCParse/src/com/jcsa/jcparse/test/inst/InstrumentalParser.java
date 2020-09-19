package com.jcsa.jcparse.test.inst;

import java.io.InputStream;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;

/**
 * It parses the instrumental tree from instrumental file.
 * 
 * @author yukimula
 *
 */
public class InstrumentalParser {
	
	/* definitions */
	private InputStream stream;
	private InstrumentalTree tree;
	private InstrumentalLine cursor;
	private InstrumentalParser(InstrumentalTree tree, InputStream stream) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree as null");
		else if(stream == null)
			throw new IllegalArgumentException("Invalid stream: null");
		else {
			this.stream = stream;
			this.tree = tree;
			this.cursor = InstrumentalLine.read_from(
					tree.get_template(), tree.get_ast_tree(), stream);
		}
	}
	
	/* basic methods */
	/**
	 * @return whether there are instrumental lines for matching
	 */
	private boolean has_cursor() { return this.cursor != null; }
	/**
	 * @param expression
	 * @return whether the expression matches with the current line
	 * @throws Exception
	 */
	private boolean match_expression(AstExpression expression) throws Exception {
		if(this.cursor != null) {
			return this.cursor.get_location() == expression;
		}
		else {
			return false;	/* no more instrumental line exists for matching */
		}
	}
	/**
	 * @param statement
	 * @return whether the statement matches with the line as start-point
	 * @throws Exception
	 */
	private boolean match_beg_statement(AstStatement statement) throws Exception {
		if(this.cursor != null) {
			return this.cursor.get_location() == statement &&
					!((Boolean) this.cursor.get_value()).booleanValue();
		}
		else {
			return false;	/* no more instrumental line exists for matching */
		}
	}
	/**
	 * @param statement
	 * @return whether the statement matches with the line as final-point
	 * @throws Exception
	 */
	private boolean match_end_statement(AstStatement statement) throws Exception {
		if(this.cursor != null) {
			return this.cursor.get_location() == statement &&
					((Boolean) this.cursor.get_value()).booleanValue();
		}
		else {
			return false;	/* no more instrumental line exists for matching */
		}
	}
	/**
	 * @param node
	 * @param begin whether to update the beginning or end of the location
	 * @return match with the current token and update the node's state and cursor
	 * @throws Exception
	 */
	private boolean match_and_update(InstrumentalNode node, boolean begin) throws Exception {
		/* 1. determine whether the node matches with the token */
		boolean match;
		if(node.get_location() instanceof AstExpression) {
			match = this.match_expression((AstExpression) node.get_location());
		}
		else if(node.get_location() instanceof AstStatement) {
			if(begin) {
				match = this.match_beg_statement((AstStatement) node.get_location());
			}
			else {
				match = this.match_end_statement((AstStatement) node.get_location());
			}
		}
		else {
			match = false;	/* invalid location case */
		}
		
		/* 2. update the node status as well as the stream cursor */
		if(match) {
			if(begin) {
				node.set_beg_line(this.cursor);
			}
			else {
				node.set_end_line(this.cursor);
			}
			this.cursor = InstrumentalLine.read_from(this.tree.
					get_template(), this.tree.get_ast_tree(), stream);
		}
		
		/* 3. return whether it matches and update */	
		return match;
	}
	/**
	 * @param data_type
	 * @return whether the expression with respect to the data type can be monitored
	 * 			and inserted with jcm_add for evaluating its value.
	 * @throws Exception
	 */
	private boolean is_valid_type(CType data_type) throws Exception {
		if(data_type == null) {
			return false;
		}
		else if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_void:
			case gnu_va_list:	return false;
			default:			return true;
			}
		}
		else if(data_type instanceof CArrayType) {
			return false;
		}
		else if(data_type instanceof CPointerType) {
			return true;
		}
		else if(data_type instanceof CFunctionType) {
			return false;
		}
		else if(data_type instanceof CStructType || 
				data_type instanceof CUnionType || 
				data_type instanceof CEnumType) {
			return true;
		}
		else if(data_type instanceof CQualifierType) {
			return this.is_valid_type(((CQualifierType) data_type).get_reference());
		}
		else {
			throw new IllegalArgumentException("Invalid data_type: " + data_type);
		}
	}
	/**
	 * The following context is invalid for evaluating the value of the expression when:
	 * 	1.	expr {=, +=, -=, *=, /=, %=, &=, |=, ^=, <<=, >>=} other_expression;<br>
	 * 	2.	&expr, expr.field, expr++, expr--, ++expr, --expr;<br>
	 * 	3. 	case expr:<br>
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private boolean is_valid_context(AstExpression node) throws Exception {
		/** 1. reach the direct-context where the node belongs to **/
		AstNode context = node.get_parent(), child = node;
		while(context instanceof AstParanthExpression
				|| context instanceof AstConstExpression
				|| context instanceof AstInitializer) {
			child = context;
			context = context.get_parent();
		}
		AstNode parent = context.get_parent();
		while(parent != null) {
			if(parent instanceof AstInitializerBody) {
				return false;
			}
			else {
				parent = parent.get_parent();
			}
		}
		
		/** 2. invalid case determination **/
		if(context instanceof AstAssignExpression
			|| context instanceof AstArithAssignExpression
			|| context instanceof AstBitwiseAssignExpression
			|| context instanceof AstShiftAssignExpression) {
			return ((AstBinaryExpression) context).get_loperand() != child;
		}
		else if(context instanceof AstUnaryExpression) {
			switch(((AstUnaryExpression) context).get_operator().get_operator()) {
			case address_of:
			case increment:
			case decrement:
			{
				return ((AstUnaryExpression) context).get_operand() != child;
			}
			default: return true;
			}
		}
		else if(context instanceof AstPostfixExpression) {
			switch(((AstPostfixExpression) context).get_operator().get_operator()) {
			case increment:
			case decrement:
			{
				return ((AstPostfixExpression) context).get_operand() != child;
			}
			default: return true;
			}
		}
		else if(context instanceof AstFieldExpression) {
			switch(((AstFieldExpression) context).get_operator().get_punctuator()) {
			case dot:	
			{
				return ((AstFieldExpression) context).get_body() != child;
			}
			default:	return true;
			}
		}
		else if(context instanceof AstCaseStatement) {
			return ((AstCaseStatement) context).get_expression() != child;
		}
		else {
			return true;
		}
	}
	/**
	 * the expression is invalid for analysis when it is assignment.
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private boolean is_valid_expression(AstExpression node) throws Exception {
		if(node instanceof AstBinaryExpression) {
			switch(((AstBinaryExpression) node).get_operator().get_operator()) {
			case assign:
			case arith_add_assign:
			case arith_sub_assign:
			case arith_mul_assign:
			case arith_div_assign:
			case arith_mod_assign:
			case bit_and_assign:
			case bit_or_assign:
			case bit_xor_assign:
			case left_shift_assign:
			case righ_shift_assign:	return false;
			default: return true;
			}
		}
		else {
			return true;
		}
	}
	/**
	 * @param location
	 * @return whether the location is seeded with instrumentation
	 * @throws Exception
	 */
	private boolean is_instrumental(AstNode location) throws Exception {
		if(location instanceof AstStatement) {
			return true;
		}
		else if(location instanceof AstExpression) {
			AstExpression expression = (AstExpression) location;
			return this.is_valid_context(expression) 
					&& this.is_valid_expression(expression) 
					&& this.is_valid_type(expression.get_value_type());
		}
		else {
			return false;
		}
	}
	/**
	 * @param location
	 * @return match with the cursor forcely
	 * @throws Exception
	 */
	private boolean force_to_match(InstrumentalNode node, AstExpression location) throws Exception {
		if(this.is_instrumental(location)) {
			if(this.has_cursor()) {
				if(this.match_and_update(node, false)) {
					return true;
				}
				else {
					throw new UnsupportedOperationException("Unable to match "
							+ location.generate_code() + " at line "
							+ location.get_location().line_of() + " with "
							+ this.cursor.get_location().generate_code()
							+ " at line " + this.cursor.
								get_location().get_location().line_of());
				}
			}
			else {
				return false;	/* no more token used */
			}
		}
		else {
			return false;	/* no need to match... */
		}
	}
	
	/* parsing methods */
	private void parse(InstrumentalNode node) throws Exception {
		AstNode location = node.get_location();
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else
			throw new IllegalArgumentException("Unsupport: " + location);
	}
	
	/* expression part */
	private void parse_id_expression(InstrumentalNode parent,
			AstIdExpression location) throws Exception {
		this.force_to_match(parent, location);
	}
	private void parse_constant(InstrumentalNode parent,
			AstConstant location) throws Exception {
		this.force_to_match(parent, location);
	}
	
	
	
	
}
