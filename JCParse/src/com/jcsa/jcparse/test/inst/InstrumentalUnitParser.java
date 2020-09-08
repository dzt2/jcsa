package com.jcsa.jcparse.test.inst;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstBasicExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCastExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstSizeofExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDefaultStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBinAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirDefaultStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;

/**
 * It is used to parse from InstrumentalLine to generate InstrumentalUnit
 * in form of the C-intermediate representation language (CIR).
 * 
 * @author yukimula
 *
 */
class InstrumentalUnitParser {
	
	/* definitions */
	private CRunTemplate template;
	private CirTree cir_tree;
	private List<InstrumentalUnit> units;
	private InstrumentalUnitParser() { }
	protected static final InstrumentalUnitParser parser = new InstrumentalUnitParser();
	
	/* basic methods */
	private CirExpression get_cir_value(AstExpression expression) throws Exception {
		return this.cir_tree.get_localizer().get_cir_value(expression);
	}
	private List<CirNode> get_cir_nodes(AstNode location, Class<?> cir_type) throws Exception {
		return this.cir_tree.get_localizer().get_cir_nodes(location, cir_type);
	}
	private void append_beg_stmt(CirStatement statement) throws Exception {
		this.units.add(InstrumentalUnit.beg_stmt(statement));
	}
	private void append_end_stmt(CirStatement statement) throws Exception {
		this.units.add(InstrumentalUnit.end_stmt(statement));
	}
	private void append_evaluate(CirExpression expression, byte[] value) throws Exception {
		/* avoid undefined or the invalid expression */
		if(expression != null && expression.statement_of() != null)	
			this.units.add(InstrumentalUnit.evaluate(template, expression, value));
	}
	
	/* local-parsing [expression + statement] */
	private void local_parse(AstNode location, InstrumentalLine line) throws Exception {
		if(location instanceof AstExpression) {
			if(location instanceof AstBasicExpression) 
				this.local_parse_basic_expression((AstBasicExpression) location, line);
			else if(location instanceof AstBinaryExpression)
				this.local_parse_binary_expression((AstBinaryExpression) location, line);
			else if(location instanceof AstUnaryExpression)
				this.local_parse_unary_expression((AstUnaryExpression) location, line);
			else if(location instanceof AstPostfixExpression)
				this.local_parse_postfix_expression((AstPostfixExpression) location, line);
			else if(location instanceof AstArrayExpression)
				this.local_parse_array_expression((AstArrayExpression) location, line);
			else if(location instanceof AstCastExpression)
				this.local_parse_cast_expression((AstCastExpression) location, line);
			else if(location instanceof AstCommaExpression)
				this.local_parse_comma_expression((AstCommaExpression) location, line);
			else if(location instanceof AstParanthExpression)
				this.local_parse_paranth_expression((AstParanthExpression) location, line);
			else if(location instanceof AstConstExpression)
				this.local_parse_const_expression((AstConstExpression) location, line);
			else if(location instanceof AstInitializerBody)
				this.local_parse_initializer_body((AstInitializerBody) location, line);
			else if(location instanceof AstFieldExpression)
				this.local_parse_field_expression((AstFieldExpression) location, line);
			else if(location instanceof AstSizeofExpression)
				this.local_parse_sizeof_expression((AstSizeofExpression) location, line);
			else if(location instanceof AstConditionalExpression)
				this.local_parse_conditional_expression((AstConditionalExpression) location, line);
			else if(location instanceof AstFunCallExpression)
				this.local_parse_fun_call_expression((AstFunCallExpression) location, line);
			else
				throw new IllegalArgumentException(location.generate_code());
		}
		else if(location instanceof AstStatement) {
			if(location instanceof AstExpressionStatement)
				this.local_parse_expression_statement((AstExpressionStatement) location, line);
			else if(location instanceof AstDeclarationStatement)
				this.local_parse_declaration_statement((AstDeclarationStatement) location, line);
			else if(location instanceof AstCompoundStatement)
				this.local_parse_compound_statement((AstCompoundStatement) location, line);
			else if(location instanceof AstBreakStatement)
				this.local_break_statement((AstBreakStatement) location, line);
			else if(location instanceof AstContinueStatement)
				this.local_continue_statement((AstContinueStatement) location, line);
			else if(location instanceof AstGotoStatement)
				this.local_goto_statement((AstGotoStatement) location, line);
			else if(location instanceof AstReturnStatement)
				this.local_return_statement((AstReturnStatement) location, line);
			else if(location instanceof AstLabeledStatement)
				this.local_labeled_statement((AstLabeledStatement) location, line);
			else if(location instanceof AstDefaultStatement)
				this.local_default_statement((AstDefaultStatement) location, line);
			else if(location instanceof AstCaseStatement)
				this.local_case_statement((AstCaseStatement) location, line);
			else if(location instanceof AstIfStatement)
				this.local_parse_if_statement((AstIfStatement) location, line);
			else if(location instanceof AstSwitchStatement)
				this.local_parse_switch_statement((AstSwitchStatement) location, line);
			else if(location instanceof AstForStatement)
				this.local_parse_for_statement((AstForStatement) location, line);
			else if(location instanceof AstWhileStatement)
				this.local_parse_while_statement((AstWhileStatement) location, line);
			else if(location instanceof AstDoWhileStatement)
				this.local_parse_do_while_statement((AstDoWhileStatement) location, line);
			else
				throw new IllegalArgumentException(location.generate_code());
		}
		else {
			throw new IllegalArgumentException(location.getClass().getName());
		}
	}
	private void local_parse_basic_expression(AstBasicExpression location,
			InstrumentalLine line) throws Exception {
		CirExpression expression = get_cir_value(location);
		this.append_evaluate(expression, line.get_value());
	}
	private void local_parse_binary_expression(AstBinaryExpression 
			location, InstrumentalLine line) throws Exception {
		if(location instanceof AstArithBinaryExpression
			|| location instanceof AstBitwiseBinaryExpression
			|| location instanceof AstShiftBinaryExpression
			|| location instanceof AstLogicBinaryExpression
			|| location instanceof AstRelationExpression
			|| location instanceof AstAssignExpression) {
			CirExpression expression = get_cir_value(location);
			this.append_evaluate(expression, line.get_value());
		}
		else if(location instanceof AstArithAssignExpression
				|| location instanceof AstBitwiseAssignExpression
				|| location instanceof AstShiftAssignExpression) {
			CirBinAssignStatement statement = (CirBinAssignStatement) this.
					get_cir_nodes(location, CirBinAssignStatement.class).get(0);
			this.append_evaluate(statement.get_rvalue(), line.get_value());
			
			CirExpression expression = get_cir_value(location);
			this.append_evaluate(expression, line.get_value());
		}
		else {
			throw new IllegalArgumentException(location.generate_code());
		}
	}
	private void local_parse_unary_expression(AstUnaryExpression
			location, InstrumentalLine line) throws Exception {
		if(location instanceof AstArithUnaryExpression
			|| location instanceof AstBitwiseUnaryExpression
			|| location instanceof AstLogicUnaryExpression
			|| location instanceof AstPointUnaryExpression) {
			CirExpression expression = get_cir_value(location);
			this.append_evaluate(expression, line.get_value());
		}
		else if(location instanceof AstIncreUnaryExpression) {
			CirIncreAssignStatement statement = (CirIncreAssignStatement) this.
					get_cir_nodes(location, CirIncreAssignStatement.class).get(0);
			this.append_evaluate(statement.get_rvalue(), line.get_value());
			
			CirExpression expression = get_cir_value(location);
			this.append_evaluate(expression, line.get_value());
		}
		else {
			throw new IllegalArgumentException(location.generate_code());
		}
	}
	private void local_parse_postfix_expression(AstPostfixExpression
			location, InstrumentalLine line) throws Exception {
		if(location instanceof AstIncrePostfixExpression) {
			CirAssignStatement statement;
			statement = (CirAssignStatement) this.get_cir_nodes(
					location, CirSaveAssignStatement.class).get(0);
			this.append_evaluate(statement.get_rvalue(), line.get_value());
			
			statement = (CirAssignStatement) this.get_cir_nodes(
					location, CirIncreAssignStatement.class).get(0);
			this.append_evaluate(statement.get_rvalue(), line.get_value());
			
			CirExpression expression = get_cir_value(location);
			this.append_evaluate(expression, line.get_value());
		}
		else {
			throw new IllegalArgumentException(location.generate_code());
		}
	}
	private void local_parse_array_expression(AstArrayExpression location, 
			InstrumentalLine line) throws Exception {
		CirExpression expression = get_cir_value(location);
		this.append_evaluate(expression, line.get_value());
	}
	private void local_parse_cast_expression(AstCastExpression location, 
			InstrumentalLine line) throws Exception {
		CirExpression expression = get_cir_value(location);
		this.append_evaluate(expression, line.get_value());
	}
	private void local_parse_comma_expression(AstCommaExpression location, 
			InstrumentalLine line) throws Exception {
		/* skip the duplicated and recursive structure */
	}
	private void local_parse_paranth_expression(AstParanthExpression location,
			InstrumentalLine line) throws Exception {
		/* skip the duplicated and recursive structure */
	}
	private void local_parse_const_expression(AstConstExpression location,
			InstrumentalLine line) throws Exception {
		/* skip the duplicated and recursive structure */
	}
	private void local_parse_field_expression(AstFieldExpression location,
			InstrumentalLine line) throws Exception {
		CirExpression expression = get_cir_value(location);
		this.append_evaluate(expression, line.get_value());
	}
	private void local_parse_initializer_body(AstInitializerBody location,
			InstrumentalLine line) throws Exception {
		CirExpression expression = get_cir_value(location);
		this.append_evaluate(expression, line.get_value());
	}
	private void local_parse_sizeof_expression(AstSizeofExpression location,
			InstrumentalLine line) throws Exception {
		CirExpression expression = get_cir_value(location);
		this.append_evaluate(expression, line.get_value());
	}
	private void local_parse_conditional_expression(AstConditionalExpression
			location, InstrumentalLine line) throws Exception {
		CirExpression expression = get_cir_value(location);
		this.append_evaluate(expression, line.get_value());
	}
	private void local_parse_fun_call_expression(AstFunCallExpression
			location, InstrumentalLine line) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) this.
				get_cir_nodes(location, CirWaitAssignStatement.class).get(0);
		this.append_evaluate(statement.get_rvalue(), line.get_value());
		
		CirExpression expression = get_cir_value(location);
		this.append_evaluate(expression, line.get_value());
	}
	private void local_break_statement(AstBreakStatement location,
			InstrumentalLine line) throws Exception {
		CirStatement statement = (CirStatement) get_cir_nodes(
					location, CirGotoStatement.class).get(0);
		this.append_beg_stmt(statement);
	}
	private void local_continue_statement(AstContinueStatement location,
			InstrumentalLine line) throws Exception {
		CirStatement statement = (CirStatement) get_cir_nodes(
					location, CirGotoStatement.class).get(0);
		this.append_beg_stmt(statement);
	}
	private void local_goto_statement(AstGotoStatement location,
			InstrumentalLine line) throws Exception {
		CirStatement statement = (CirStatement) get_cir_nodes(
					location, CirGotoStatement.class).get(0);
		this.append_beg_stmt(statement);
	}
	private void local_return_statement(AstReturnStatement location,
			InstrumentalLine line) throws Exception {
		CirStatement statement = this.cir_tree.
				get_cir_range(location).get_beg_statement();
		this.append_beg_stmt(statement);
	}
	private void local_labeled_statement(AstLabeledStatement location,
			InstrumentalLine line) throws Exception {
		CirStatement statement = (CirStatement) get_cir_nodes(
					location, CirLabelStatement.class).get(0);
		this.append_end_stmt(statement);
	}
	private void local_default_statement(AstDefaultStatement location,
			InstrumentalLine line) throws Exception {
		CirStatement statement = (CirStatement) get_cir_nodes(
					location, CirDefaultStatement.class).get(0);
		this.append_end_stmt(statement);
	}
	private void local_case_statement(AstCaseStatement location,
			InstrumentalLine line) throws Exception {
		CirStatement statement = (CirStatement) get_cir_nodes(
					location, CirCaseStatement.class).get(0);
		this.append_end_stmt(statement);
	}
	private void local_parse_expression_statement(AstExpressionStatement
			location, InstrumentalLine line) throws Exception {
	}
	private void local_parse_declaration_statement(AstDeclarationStatement
			location, InstrumentalLine line) throws Exception {
	}
	private void local_parse_compound_statement(AstCompoundStatement
			location, InstrumentalLine line) throws Exception {
		if(location.get_parent() instanceof AstFunctionDefinition) {
			CirFunctionDefinition def = (CirFunctionDefinition) get_cir_nodes(
					location.get_parent(), CirFunctionDefinition.class).get(0);
			CirFunction function = cir_tree.get_function_call_graph().get_function(def);
			CirStatement statement;
			switch(line.get_type()) {
			case beg_stmt:	
				statement = function.get_flow_graph().get_entry().get_statement(); 
				this.append_beg_stmt(statement);
				break;
			case end_stmt:	
				statement = function.get_flow_graph().get_exit().get_statement(); 
				this.append_end_stmt(statement);
				break;
			default: throw new IllegalArgumentException(line.get_type().toString());
			}
		}
	}
	private void local_parse_if_statement(AstIfStatement location,
			InstrumentalLine line) throws Exception { }
	private void local_parse_switch_statement(AstSwitchStatement location,
			InstrumentalLine line) throws Exception { }
	private void local_parse_for_statement(AstForStatement location,
			InstrumentalLine line) throws Exception { }
	private void local_parse_while_statement(AstWhileStatement location,
			InstrumentalLine line) throws Exception { }
	private void local_parse_do_while_statement(AstDoWhileStatement location,
			InstrumentalLine line) throws Exception { }
	
	protected static List<InstrumentalUnit> parse(CRunTemplate template, 
			CirTree cir_tree, List<InstrumentalLine> lines) throws Exception {
		parser.cir_tree = cir_tree;
		parser.template = template;
		parser.units = new ArrayList<InstrumentalUnit>();
		for(InstrumentalLine line : lines) {
			parser.local_parse(line.get_location(), line);
		}
		List<InstrumentalUnit> units = parser.units;
		parser.units = null;
		return units;
	}
	
}
