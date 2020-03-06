package com.jcsa.jcparse.lang.irlang.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirArithExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirBitwsExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeclarator;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirIdentifier;
import com.jcsa.jcparse.lang.irlang.expr.CirImplicator;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirLogicExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirRelationExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReturnPoint;
import com.jcsa.jcparse.lang.irlang.expr.CirStringLiteral;
import com.jcsa.jcparse.lang.irlang.expr.CirType;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirBegStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBinAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirDefaultStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirInitAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionBody;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.unit.CirTransitionUnit;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.scope.CName;

public class CirTreeImpl implements CirTree {
	
	private List<CirNode> nodes;
	private Map<AstNode, List<CirNode>> index;
	private Map<AstNode, AstCirPair> ranges;
	private CirCodeGenerator generator;
	private CirCodeGenerator unique_generator;
	private CirFunctionCallGraph call_graph;
	
	public CirTreeImpl(AstTranslationUnit astroot) throws IllegalArgumentException {
		if(astroot == null)
			throw new IllegalArgumentException("invalid astroot: null");
		else {
			this.nodes = new ArrayList<CirNode>();
			this.index = new HashMap<AstNode, List<CirNode>>();
			this.ranges = new HashMap<AstNode, AstCirPair>();
			CirNode root = new CirTransitionUnitImpl(this, 0);
			this.nodes.add(root); root.set_ast_source(astroot);
			this.call_graph = null;
			
			this.generator = new CirCodeGenerator(false);
			this.unique_generator = new CirCodeGenerator(true);
		}
	}
	
	/* node getters */
	@Override
	public CirTransitionUnit get_root() { 
		return (CirTransitionUnit) nodes.get(0); 
	}
	@Override
	public int size() { return nodes.size(); }
	@Override
	public CirNode get_node(int id) throws IndexOutOfBoundsException {
		return this.nodes.get(id);
	}
	@Override
	public Iterable<CirNode> get_nodes() { return this.nodes; }
	
	/* index access */
	@Override
	public List<CirNode> get_cir_nodes(AstNode ast_source) {
		if(this.index.containsKey(ast_source)) {
			return new LinkedList<CirNode>(this.index.get(ast_source));
		}
		else {
			return new LinkedList<CirNode>();
		}
	}
	@Override
	public List<CirNode> get_cir_nodes(AstNode ast_source, Class<?> cir_type) {
		List<CirNode> nodes = new LinkedList<CirNode>();
		if(this.index.containsKey(ast_source)) {
			List<CirNode> all_nodes = this.index.get(ast_source);
			for(CirNode cir_node : all_nodes) {
				if(cir_type.isInstance(cir_node)) {
					nodes.add(cir_node);
				}
			}
		}
		return nodes;
	}
	/**
	 * record the CIR node with the AST source node it refers to
	 * @param tree_node
	 * @throws IllegalArgumentException
	 */
	protected void link_ast_with_cir(CirNode tree_node) throws IllegalArgumentException {
		if(tree_node == null || tree_node.get_tree() != this) {
			throw new IllegalArgumentException("Undefined: " + tree_node);
		}
		else if(tree_node.get_ast_source() == null) {
			throw new IllegalArgumentException("No ast-source specified");
		}
		else {
			AstNode ast_source = tree_node.get_ast_source();
			
			if(!this.index.containsKey(ast_source)) {
				this.index.put(ast_source, new LinkedList<CirNode>());
			}
			
			List<CirNode> cir_nodes = this.index.get(ast_source);
			cir_nodes.add(tree_node);
		}
	}
	
	/* factory methods */
	public CirIdentifier new_identifier(AstNode ast_source, 
			CName cname, CType data_type) throws IllegalArgumentException {
		CirIdentifier identifier = new 
				CirIdentifierImpl(this, this.nodes.size());
		this.nodes.add(identifier);
		
		identifier.set_ast_source(ast_source); 
		identifier.set_data_type(data_type);
		identifier.set_cname(cname);
		
		return identifier;
	}
	public CirDeclarator new_declarator(AstNode ast_source, 
			CName cname, CType data_type) throws IllegalArgumentException {
		CirDeclarator declarator = new CirDeclaratorImpl(this, nodes.size());
		this.nodes.add(declarator);
		
		declarator.set_ast_source(ast_source);
		declarator.set_cname(cname);
		declarator.set_data_type(data_type);
		
		return declarator;
	}
	public CirImplicator new_implicator(AstNode ast_source, CType data_type) throws IllegalArgumentException {
		CirImplicator implicator = new CirImplicatorImpl(this, nodes.size());
		this.nodes.add(implicator);
		
		implicator.set_ast_source(ast_source);
		implicator.set_data_type(data_type);
		
		return implicator;
	}
	public CirImplicator new_implicator(AstNode ast_source, CType data_type, String name) throws IllegalArgumentException {
		CirImplicator implicator = new CirImplicatorImpl(this, nodes.size(), name);
		this.nodes.add(implicator);
		
		implicator.set_ast_source(ast_source);
		implicator.set_data_type(data_type);
		
		return implicator;
	}
	public CirReturnPoint new_return_point(AstNode ast_source, 
			CType data_type) throws IllegalArgumentException {
		CirReturnPoint return_point = new CirReturnPointImpl(this, nodes.size());
		this.nodes.add(return_point);
		
		return_point.set_ast_source(ast_source);
		return_point.set_data_type(data_type);
		return return_point;
	}
	public CirDeferExpression new_defer_expression(AstNode ast_source, 
			CType data_type) throws IllegalArgumentException {
		CirDeferExpression expression = new CirDeferExpressionImpl(this, nodes.size());
		this.nodes.add(expression);
		
		expression.set_ast_source(ast_source);
		expression.set_data_type(data_type);
		
		return expression;
	}
	public CirFieldExpression new_field_expression(AstNode ast_source, 
			CType data_type) throws IllegalArgumentException {
		CirFieldExpression expression = new CirFieldExpressionImpl(this, nodes.size());
		this.nodes.add(expression);
		
		expression.set_ast_source(ast_source);
		expression.set_data_type(data_type);
		
		return expression;
	}
	public CirField new_field(AstNode ast_source, String name) throws IllegalArgumentException {
		CirField field = new CirFieldImpl(this, nodes.size());
		this.nodes.add(field);
		
		field.set_ast_source(ast_source); field.set_name(name);
		
		return field;
	}
	public CirConstExpression new_const_expression(AstNode ast_source, 
			CConstant constant, CType data_type) throws IllegalArgumentException {
		CirConstExpression expression = new CirConstExpressionImpl(this, nodes.size());
		this.nodes.add(expression);
		
		expression.set_ast_source(ast_source);
		expression.set_data_type(data_type);
		expression.set_constant(constant);
		
		return expression;
	}
	public CirStringLiteral new_string_literal(AstNode ast_source, 
			String literal, CType data_type) throws IllegalArgumentException {
		CirStringLiteral expression = new CirStringLiteralImpl(this, nodes.size());
		this.nodes.add(expression);
		
		expression.set_ast_source(ast_source);
		expression.set_data_type(data_type);
		expression.set_literal(literal);
		
		return expression;
	}
	public CirArithExpression new_arith_expression(AstNode ast_source, 
			COperator operator, CType data_type) throws IllegalArgumentException {
		CirArithExpression expression = new CirArithExpressionImpl(this, nodes.size(), operator);
		this.nodes.add(expression);
		
		expression.set_ast_source(ast_source);
		expression.set_data_type(data_type);
		
		return expression;
	}
	public CirBitwsExpression new_bitws_expression(AstNode ast_source, 
			COperator operator, CType data_type) throws IllegalArgumentException {
		CirBitwsExpression expression = new CirBitwsExpressionImpl(this, nodes.size(), operator);
		this.nodes.add(expression);
		
		expression.set_ast_source(ast_source);
		expression.set_data_type(data_type);
		
		return expression;
	}
	public CirLogicExpression new_logic_expression(AstNode ast_source, 
			COperator operator, CType data_type) throws IllegalArgumentException {
		CirLogicExpression expression = new CirLogicExpressionImpl(this, nodes.size(), operator);
		this.nodes.add(expression);
		
		expression.set_ast_source(ast_source);
		expression.set_data_type(data_type);
		
		return expression;
	}
	public CirRelationExpression new_relation_expression(AstNode ast_source, 
			COperator operator, CType data_type) throws IllegalArgumentException {
		CirRelationExpression expression = new CirRelationExpressionImpl(this, nodes.size(), operator);
		this.nodes.add(expression);
		
		expression.set_ast_source(ast_source);
		expression.set_data_type(data_type);
		
		return expression;
	}
	public CirAddressExpression new_address_expression(AstNode ast_source, 
			CType data_type) throws IllegalArgumentException {
		CirAddressExpression expression = new CirAddressExpressionImpl(this, nodes.size());
		this.nodes.add(expression);
		
		expression.set_ast_source(ast_source);
		expression.set_data_type(data_type);
		
		return expression;
	}
	public CirCastExpression new_cast_expression(AstNode ast_source, 
			CType data_type) throws IllegalArgumentException {
		CirCastExpression expression = new CirCastExpressionImpl(this, nodes.size());
		this.nodes.add(expression);
		
		expression.set_ast_source(ast_source);
		expression.set_data_type(data_type);
		
		return expression;
	}
	public CirType new_type(AstNode ast_source, CType data_type) throws IllegalArgumentException {
		CirType typename = new CirTypeImpl(this, nodes.size());
		this.nodes.add(typename);
		
		typename.set_ast_source(ast_source);
		typename.set_typename(data_type);
		
		return typename;
	}
	public CirWaitExpression new_wait_expression(AstNode ast_source, 
			CType data_type) throws IllegalArgumentException {
		CirWaitExpression expression = new CirWaitExpressionImpl(this, nodes.size());
		this.nodes.add(expression);
		
		expression.set_ast_source(ast_source);
		expression.set_data_type(data_type);
		
		return expression;
	}
	public CirInitializerBody new_initializer_body(AstNode ast_source, 
			CType data_type) throws IllegalArgumentException {
		CirInitializerBody expression = new 
				CirInitializerBodyImpl(this, nodes.size());
		this.nodes.add(expression);
		
		expression.set_ast_source(ast_source);
		expression.set_data_type(data_type);
		
		return expression;
	}
	public CirInitializerBody new_initializer_body(AstNode ast_source) throws IllegalArgumentException {
		CirInitializerBody expression = new 
				CirInitializerBodyImpl(this, nodes.size());
		this.nodes.add(expression);
		
		expression.set_ast_source(ast_source);
		
		return expression;
	}
	public CirDefaultValue new_default_value(CType data_type) throws IllegalArgumentException {
		CirDefaultValue expression = new 
				CirDefaultValueImpl(this, nodes.size());
		this.nodes.add(expression);
		
		expression.set_data_type(data_type);
		
		return expression;
	}
	public CirLabel new_label(AstNode ast_source) throws IllegalArgumentException {
		CirLabel label = new CirLabelImpl(this, nodes.size());
		this.nodes.add(label);
		label.set_ast_source(ast_source); return label;
	}
	public CirBinAssignStatement new_bin_assign_statement(AstNode ast_source) throws IllegalArgumentException {
		CirBinAssignStatement statement = new CirBinAssignStatementImpl(this, nodes.size());
		this.nodes.add(statement); statement.set_ast_source(ast_source); return statement;
	}
	public CirSaveAssignStatement new_save_assign_statement(AstNode ast_source) throws IllegalArgumentException {
		CirSaveAssignStatement statement = new CirSaveAssignStatementImpl(this, nodes.size());
		this.nodes.add(statement); statement.set_ast_source(ast_source); return statement;
	}
	public CirIncreAssignStatement new_inc_assign_statement(AstNode ast_source) throws IllegalArgumentException {
		CirIncreAssignStatement statement = new CirIncreAssignStatementImpl(this, nodes.size());
		this.nodes.add(statement); statement.set_ast_source(ast_source); return statement;
	}
	public CirInitAssignStatement new_init_assign_statement(AstNode ast_source) throws IllegalArgumentException {
		CirInitAssignStatement statement = new CirInitAssignStatementImpl(this, nodes.size());
		this.nodes.add(statement); statement.set_ast_source(ast_source); return statement;
	}
	public CirReturnAssignStatement new_return_assign_statement(AstNode ast_source) throws IllegalArgumentException {
		CirReturnAssignStatement statement = new CirReturnAssignStatementImpl(this, nodes.size());
		this.nodes.add(statement); statement.set_ast_source(ast_source); return statement;
	}
	public CirWaitAssignStatement new_wait_assign_statement(AstNode ast_source) throws IllegalArgumentException {
		CirWaitAssignStatement statement = new CirWaitAssignStatementImpl(this, nodes.size());
		this.nodes.add(statement); statement.set_ast_source(ast_source); return statement;
	}
	public CirCallStatement new_call_statement(AstNode ast_source) throws IllegalArgumentException {
		CirCallStatement statement = new CirCallStatementImpl(this, nodes.size());
		this.nodes.add(statement); statement.set_ast_source(ast_source); return statement;
	}
	public CirArgumentList new_argument_list(AstNode ast_source) throws IllegalArgumentException {
		CirArgumentList arguments = new CirArgumentListImpl(this, nodes.size());
		this.nodes.add(arguments); arguments.set_ast_source(ast_source); return arguments;
	}
	public CirCaseStatement new_case_statement(AstNode ast_source) throws IllegalArgumentException {
		CirCaseStatement statement = new CirCaseStatementImpl(this, nodes.size());
		this.nodes.add(statement); statement.set_ast_source(ast_source); return statement;
	}
	public CirGotoStatement new_goto_statement(AstNode ast_source) throws IllegalArgumentException {
		CirGotoStatement statement = new CirGotoStatementImpl(this, nodes.size());
		this.nodes.add(statement); statement.set_ast_source(ast_source); return statement;
	}
	public CirIfStatement new_if_statement(AstNode ast_source) throws IllegalArgumentException {
		CirIfStatement statement = new CirIfStatementImpl(this, nodes.size());
		this.nodes.add(statement); statement.set_ast_source(ast_source); return statement;
	}
	public CirBegStatement new_beg_statement(AstNode ast_source) throws IllegalArgumentException {
		CirBegStatement statement = new CirBegStatementImpl(this, nodes.size());
		this.nodes.add(statement); statement.set_ast_source(ast_source); return statement;
	}
	public CirEndStatement new_end_statement(AstNode ast_source) throws IllegalArgumentException {
		CirEndStatement statement = new CirEndStatementImpl(this, nodes.size());
		this.nodes.add(statement); statement.set_ast_source(ast_source); return statement;
	}
	public CirDefaultStatement new_default_statement(AstNode ast_source) throws IllegalArgumentException {
		CirDefaultStatement statement = new CirDefaultStatementImpl(this, nodes.size());
		this.nodes.add(statement); statement.set_ast_source(ast_source); return statement;
	}
	public CirIfEndStatement new_if_end_statement(AstNode ast_source) throws IllegalArgumentException {
		CirIfEndStatement statement = new CirIfEndStatementImpl(this, nodes.size());
		this.nodes.add(statement); statement.set_ast_source(ast_source); return statement;
	}
	public CirCaseEndStatement new_case_end_statement(AstNode ast_source) throws IllegalArgumentException {
		CirCaseEndStatement statement = new CirCaseEndStatementImpl(this, nodes.size());
		this.nodes.add(statement); statement.set_ast_source(ast_source); return statement;
	}
	public CirLabelStatement new_label_statement(AstNode ast_source) throws IllegalArgumentException {
		CirLabelStatement statement = new CirLabelStatementImpl(this, nodes.size());
		this.nodes.add(statement); statement.set_ast_source(ast_source); return statement;
	}
	public CirFunctionDefinition new_function_definition(AstNode ast_source) throws IllegalArgumentException {
		CirFunctionDefinition function = new CirFunctionDefinitionImpl(this, nodes.size());
		this.nodes.add(function); function.set_ast_source(ast_source); 
		return function;
	}
	public CirFunctionBody new_function_body(AstNode ast_source) throws IllegalArgumentException {
		CirFunctionBody body = new CirFunctionBodyImpl(this, nodes.size());
		this.nodes.add(body); body.set_ast_source(ast_source); return body;
	}
	@Override
	public CirNode copy(CirNode node) throws Exception {
		if(node == null)
			throw new IllegalArgumentException("invalid node: null");
		else {
			if(node instanceof CirDeclarator) {
				CirDeclarator declarator = this.new_declarator(
						node.get_ast_source(), 
						((CirDeclarator) node).get_cname(), 
						((CirDeclarator) node).get_data_type());
				return declarator;
			}
			else if(node instanceof CirIdentifier) {
				CirIdentifier identifier = this.new_identifier(
						node.get_ast_source(), 
						((CirIdentifier) node).get_cname(), 
						((CirIdentifier) node).get_data_type());
				return identifier;
			}
			else if(node instanceof CirImplicator) {
				CirImplicator implicator = this.new_implicator(
						node.get_ast_source(), ((CirImplicator) node).get_data_type());
				return implicator;
			}
			else if(node instanceof CirReturnPoint) {
				CirReturnPoint return_point = this.new_return_point(
						node.get_ast_source(), 
						((CirReturnPoint) node).get_data_type());
				return return_point;
			}
			else if(node instanceof CirDeferExpression) {
				CirDeferExpression expression = this.new_defer_expression(
						node.get_ast_source(), ((CirDeferExpression) node).get_data_type());
				expression.set_address((CirExpression) this.copy(((CirDeferExpression) node).get_address()));
				return expression;
			}
			else if(node instanceof CirFieldExpression) {
				CirFieldExpression expression = this.new_field_expression(
						node.get_ast_source(), ((CirFieldExpression) node).get_data_type());
				expression.set_body((CirExpression) this.copy(((CirFieldExpression) node).get_body()));
				expression.set_field((CirField) this.copy(((CirFieldExpression) node).get_field()));
				return expression;
			}
			else if(node instanceof CirField) {
				CirField field = this.new_field(node.get_ast_source(), ((CirField) node).get_name());
				return field;
			}
			else if(node instanceof CirAddressExpression) {
				CirAddressExpression expression = this.new_address_expression(
						node.get_ast_source(), ((CirAddressExpression) node).get_data_type());
				expression.set_operand((CirReferExpression) this.copy(((CirAddressExpression) node).get_operand()));
				return expression;
			}
			else if(node instanceof CirCastExpression) {
				CirCastExpression expression = this.new_cast_expression(
						node.get_ast_source(), ((CirCastExpression) node).get_data_type());
				expression.set_type((CirType) this.copy(((CirCastExpression) node).get_type()));
				expression.set_operand((CirExpression) this.copy(((CirCastExpression) node).get_operand()));
				return expression;
			}
			else if(node instanceof CirType) {
				CirType type = this.new_type(node.get_ast_source(), ((CirType) node).get_typename());
				return type;
			}
			else if(node instanceof CirConstExpression) {
				CirConstExpression expression = this.new_const_expression(
						node.get_ast_source(), 
						((CirConstExpression) node).get_constant(), 
						((CirConstExpression) node).get_data_type());
				return expression;
			}
			else if(node instanceof CirStringLiteral) {
				CirStringLiteral literal = this.new_string_literal(
						node.get_ast_source(), 
						((CirStringLiteral) node).get_literal(), 
						((CirStringLiteral) node).get_data_type());
				return literal;
			}
			else if(node instanceof CirWaitExpression) {
				CirWaitExpression expression = this.new_wait_expression(
						node.get_ast_source(), ((CirWaitExpression) node).get_data_type());
				expression.set_function((CirExpression) this.copy(((CirWaitExpression) node).get_function()));
				return expression;
			}
			else if(node instanceof CirInitializerBody) {
				CirInitializerBody body = this.new_initializer_body(
						node.get_ast_source(), 
						((CirInitializerBody) node).get_data_type());
				for(int k = 0; k < node.number_of_children(); k++) {
					body.add_element((CirExpression) this.copy(((CirInitializerBody) node).get_element(k)));
				}
				return body;
			}
			else if(node instanceof CirDefaultValue) {
				CirDefaultValue expression = this.new_default_value(((CirDefaultValue) node).get_data_type());
				return expression;
			}
			else if(node instanceof CirArithExpression) {
				CirArithExpression expression = this.new_arith_expression(
						node.get_ast_source(), 
						((CirArithExpression) node).get_operator(), 
						((CirArithExpression) node).get_data_type());
				for(int k = 0; k < node.number_of_children(); k++) {
					expression.add_operand((CirExpression) this.copy(
							((CirArithExpression) node).get_operand(k)));
				}
				return expression;
			}
			else if(node instanceof CirBitwsExpression) {
				CirBitwsExpression expression = this.new_bitws_expression(
						node.get_ast_source(), 
						((CirBitwsExpression) node).get_operator(), 
						((CirBitwsExpression) node).get_data_type());
				for(int k = 0; k < node.number_of_children(); k++) {
					expression.add_operand((CirExpression) this.
							copy(((CirBitwsExpression) node).get_operand(k)));
				}
				return expression;
			}
			else if(node instanceof CirLogicExpression) {
				CirLogicExpression expression = this.new_logic_expression(
						node.get_ast_source(), 
						((CirLogicExpression) node).get_operator(), 
						((CirLogicExpression) node).get_data_type());
				for(int k = 0; k < node.number_of_children(); k++) {
					expression.add_operand((CirExpression) this.
							copy(((CirLogicExpression) node).get_operand(k)));
				}
				return expression;
			}
			else if(node instanceof CirRelationExpression) {
				CirRelationExpression expression = this.new_relation_expression(
						node.get_ast_source(), 
						((CirRelationExpression) node).get_operator(), 
						((CirRelationExpression) node).get_data_type());
				for(int k = 0; k < node.number_of_children(); k++) {
					expression.add_operand((CirExpression) this.
							copy(((CirRelationExpression) node).get_operand(k)));
				}
				return expression;
			}
			else if(node instanceof CirBinAssignStatement) {
				CirBinAssignStatement statement = this.new_bin_assign_statement(node.get_ast_source());
				statement.set_lvalue((CirReferExpression) this.copy(((CirBinAssignStatement) node).get_lvalue()));
				statement.set_rvalue((CirExpression) this.copy(((CirBinAssignStatement) node).get_rvalue()));
				return statement;
			}
			else if(node instanceof CirSaveAssignStatement) {
				CirSaveAssignStatement statement = this.new_save_assign_statement(node.get_ast_source());
				statement.set_lvalue((CirReferExpression) this.copy(((CirSaveAssignStatement) node).get_lvalue()));
				statement.set_rvalue((CirExpression) this.copy(((CirSaveAssignStatement) node).get_rvalue()));
				return statement;
			}
			else if(node instanceof CirIncreAssignStatement) {
				CirIncreAssignStatement statement = this.new_inc_assign_statement(node.get_ast_source());
				statement.set_lvalue((CirReferExpression) this.copy(((CirIncreAssignStatement) node).get_lvalue()));
				statement.set_rvalue((CirExpression) this.copy(((CirIncreAssignStatement) node).get_rvalue()));
				return statement;
			}
			else if(node instanceof CirInitAssignStatement) {
				CirInitAssignStatement statement = this.new_init_assign_statement(node.get_ast_source());
				statement.set_lvalue((CirReferExpression) this.copy(((CirInitAssignStatement) node).get_lvalue()));
				statement.set_rvalue((CirExpression) this.copy(((CirInitAssignStatement) node).get_rvalue()));
				return statement;
			}
			else if(node instanceof CirReturnAssignStatement) {
				CirReturnAssignStatement statement = this.new_return_assign_statement(node.get_ast_source());
				statement.set_lvalue((CirReferExpression) this.copy(((CirReturnAssignStatement) node).get_lvalue()));
				statement.set_rvalue((CirExpression) this.copy(((CirReturnAssignStatement) node).get_rvalue()));
				return statement;
			}
			else if(node instanceof CirWaitAssignStatement) {
				CirWaitAssignStatement statement = this.new_wait_assign_statement(node.get_ast_source());
				statement.set_lvalue((CirReferExpression) this.copy(((CirWaitAssignStatement) node).get_lvalue()));
				statement.set_rvalue((CirExpression) this.copy(((CirWaitAssignStatement) node).get_rvalue()));
				return statement;
			}
			else if(node instanceof CirGotoStatement) {
				CirGotoStatement statement = this.new_goto_statement(node.get_ast_source());
				statement.set_label((CirLabel) this.copy(((CirGotoStatement) node).get_label()));
				return statement;
			}
			else if(node instanceof CirLabel) {
				CirLabel label = this.new_label(node.get_ast_source());
				label.set_target_node_id(((CirLabel) node).get_target_node_id());
				return label;
			}
			else if(node instanceof CirCaseStatement) {
				CirCaseStatement statement = this.new_case_statement(node.get_ast_source());
				statement.set_condition((CirExpression) this.copy(((CirCaseStatement) node).get_condition()));
				statement.set_false_branch((CirLabel) this.copy(((CirCaseStatement) node).get_false_label()));
				return statement;
			}
			else if(node instanceof CirIfStatement) {
				CirIfStatement statement = this.new_if_statement(node.get_ast_source());
				statement.set_condition((CirExpression) this.copy(((CirIfStatement) node).get_condition()));
				statement.set_true_branch((CirLabel) this.copy(((CirIfStatement) node).get_true_label()));
				statement.set_false_branch((CirLabel) this.copy(((CirIfStatement) node).get_false_label()));
				return statement;
			}
			else if(node instanceof CirCallStatement) {
				CirCallStatement statement = this.new_call_statement(node.get_ast_source());
				statement.set_function((CirExpression) this.copy(((CirCallStatement) node).get_function()));
				statement.set_arguments((CirArgumentList) this.copy(((CirCallStatement) node).get_arguments()));
				return statement;
			}
			else if(node instanceof CirArgumentList) {
				CirArgumentList arguments = this.new_argument_list(node.get_ast_source());
				for(int k = 0; k < node.number_of_children(); k++) {
					arguments.add_argument((CirExpression) this.copy(((CirArgumentList) node).get_argument(k)));
				}
				return arguments;
			}
			else if(node instanceof CirBegStatement) {
				CirBegStatement statement = this.new_beg_statement(node.get_ast_source());
				return statement;
			}
			else if(node instanceof CirEndStatement) {
				CirEndStatement statement = this.new_end_statement(node.get_ast_source());
				return statement;
			}
			else if(node instanceof CirDefaultStatement) {
				CirDefaultStatement statement = this.new_default_statement(node.get_ast_source());
				return statement;
			}
			else if(node instanceof CirIfEndStatement) {
				CirIfEndStatement statement = this.new_if_end_statement(node.get_ast_source());
				return statement;
			}
			else if(node instanceof CirCaseEndStatement) {
				CirCaseEndStatement statement = this.new_case_end_statement(node.get_ast_source());
				return statement;
			}
			else if(node instanceof CirLabelStatement) {
				CirLabelStatement statement = this.new_label_statement(node.get_ast_source());
				return statement;
			}
			else if(node instanceof CirFunctionDefinition) {
				CirFunctionDefinition function = this.new_function_definition(node.get_ast_source());
				function.set_declarator((CirNameExpression) this.copy(((CirFunctionDefinition) node).get_declarator()));
				function.set_body((CirFunctionBody) this.copy(node));
				return function;
			}
			else if(node instanceof CirFunctionBody) {
				CirFunctionBody body = this.new_function_body(node.get_ast_source());
				for(int k = 0; k < node.number_of_children(); k++) {
					body.add_statement((CirStatement) this.copy(((CirFunctionBody) node).get_statement(k)));
				}
				return body;
			}
			else throw new IllegalArgumentException("unsupport " + node.getClass().getSimpleName());
		}
	}
	
	/* code range access */
	@Override
	public boolean has_cir_range(AstNode ast_source) { return this.ranges.containsKey(ast_source); }
	@Override
	public AstCirPair get_cir_range(AstNode ast_source) throws IllegalArgumentException {
		if(this.ranges.containsKey(ast_source)) return this.ranges.get(ast_source);
		else throw new IllegalArgumentException("Undefined source: " + ast_source);
	}
	/**
	 * create a code range instance for the AST source if not exists in the tree yet.
	 * @param ast_source
	 * @return
	 * @throws IllegalArgumentException
	 */
	public AstCirPair new_cir_range(AstNode ast_source) throws IllegalArgumentException {
		if(ast_source == null)
			throw new IllegalArgumentException("invalid ast_source as null");
		else {
			if(!this.ranges.containsKey(ast_source))
				this.ranges.put(ast_source, new AstCirPairImpl(ast_source));
			return this.ranges.get(ast_source);
		}
	}
	/**
	 * get the generator for generating intermediate representation code
	 * @return
	 */
	protected CirCodeGenerator get_generator() { return this.generator; }
	/**
	 * get the generator for generating intermediate representation code with unique name of identifier.
	 * @return
	 */
	protected CirCodeGenerator get_unique_generator() { return this.unique_generator; }
	
	@Override
	public CirFunctionCallGraph get_function_call_graph() { return this.call_graph; }
	/**
	 * generate the newest function calling graph
	 * @throws Exception
	 */
	public void gen_function_call_graph() throws Exception {
		this.call_graph = CirFunctionCallGraph.graph(this);
	}
	
}
