package com.jcsa.jcparse.lang.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstAbsDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstArrayQualifierList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDimension;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstIdentifierList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterBody;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterDeclaration;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterTypeList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstPointer;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstDesignatorList;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstDeclarationSpecifiers;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumSpecifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumerator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumeratorBody;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumeratorList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstFunctionQualifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstSpecifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstSpecifierQualifierList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStorageClass;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclaration;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclarationList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclarator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructSpecifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructUnionBody;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstTypeKeyword;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstTypeQualifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstTypedefName;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstUnionSpecifier;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
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
import com.jcsa.jcparse.lang.astree.stmt.AstLabel;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstDeclarationList;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;

/**
 * Used to generate the normalized form of the entire code file.
 * 
 * @author yukimula
 *
 */
public class AstNodeNormalizer {
	
	/* properties */
	/** number of tabs before a statement generated **/
	private int tab_counter;
	/** to preserve the code generated **/
	private StringBuilder buffer;
	
	/* singleton and constructor */
	/**
	 * private constructor for singleton mode
	 */
	private AstNodeNormalizer() {
		this.buffer = new StringBuilder();
	}
	/** singleton of the code file normalizer **/
	private static AstNodeNormalizer normalizer = new AstNodeNormalizer();
	
	/**
	 * initialize the code generator before translating the entire source program
	 */
	private void init() {
		this.tab_counter = 0;
		this.buffer.setLength(0);
	}
	
	/* code text generation methods */
	/**
	 * Write the tabs before a statement is generated.
	 */
	private void write_tabs() {
		for(int k = 0; k < this.tab_counter; k++) {
			this.buffer.append('\t');
		}
	}
	private void write_text(String text) {
		this.buffer.append(text);
	}
	private void write_line() {
		this.buffer.append('\n');
	}
	
	/* type declaration generation */
	/**
	 * generate the name for user defined type specifier with explit or implicit name.
	 * @param specifier
	 * @return
	 * @throws Exception
	 */
	private String name_of_user_specifier(AstSpecifier specifier) throws Exception {
		if(specifier instanceof AstStructSpecifier) {
			AstStructSpecifier s_spec = (AstStructSpecifier) specifier;
			
			if(s_spec.has_name()) {
				return s_spec.get_name().get_name();
			}
			else {
				return "struct " + "_u_type_" + s_spec.hashCode();
			}
		}
		else if(specifier instanceof AstUnionSpecifier) {
			AstUnionSpecifier u_spec = (AstUnionSpecifier) specifier;
			
			if(u_spec.has_name()) {
				return u_spec.get_name().get_name();
			}
			else {
				return "union " + "_u_type_" + u_spec.hashCode();
			}
		}
		else {
			AstEnumSpecifier e_spec = (AstEnumSpecifier) specifier;
			
			if(e_spec.has_name()) {
				return e_spec.get_name().get_name();
			}
			else {
				return "enum " + "_u_type_" + e_spec.hashCode();
			}
		}
	}
	/**
	 * collect all the type definition nodes within the program, including:
	 * 	AstStructSpecifiers
	 * 	AstUnionSpecifiers
	 * 	AstEnumSpecifiers
	 * @param root
	 * @return
	 * @throws Exception
	 */
	private List<AstNode> get_user_specifiers(AstTranslationUnit root) throws Exception {
		Queue<AstNode> queue = new LinkedList<AstNode>();
		List<AstNode> specifiers = new ArrayList<AstNode>();
		
		queue.add(root);
		while(!queue.isEmpty()) {
			/* get the next node and collect all its children */
			AstNode node = queue.poll();
			for(int k = 0; k < node.number_of_children(); k++) {
				AstNode child = node.get_child(k);
				if(child != null) queue.add(child);
			}
			
			/* collect the user type specifiers */
			if(node instanceof AstStructSpecifier
				|| node instanceof AstUnionSpecifier
				|| node instanceof AstEnumSpecifier) {
				specifiers.add(node);
			}
		}
		
		return specifiers;
	}
	/**
	 * generate the code for declaring the user type (struct|union|enum) with explicit name
	 * @param specifiers
	 * @return
	 * @throws Exception
	 */
	private void generate_utype_declarations(AstTranslationUnit root) throws Exception {
		/** 1. collect the declaration for all the user-defined specifier (name) **/
		List<AstNode> specifiers = this.get_user_specifiers(root);
		Set<String> declarations = new HashSet<String>();
		for(AstNode specifier : specifiers) {
			String declaration = name_of_user_specifier((AstSpecifier) specifier);
			declarations.add(declaration);
		}
		
		/** 2. write the declarations for all the user defined specifiers **/
		for(String declaration : declarations) {
			this.write_tabs();
			this.write_text(declaration + ";");
			this.write_line();
		}
		this.write_line();
	}
	
	/* main generator */
	private void generate(AstNode node) throws Exception {
		if(node == null)
			throw new IllegalArgumentException("Invalid node as null");
		else if(node instanceof AstTypeKeyword)
			this.generate_type_keyword((AstTypeKeyword) node);
		else if(node instanceof AstTypeQualifier)
			this.generate_type_qualifier((AstTypeQualifier) node);
		else if(node instanceof AstStorageClass)
			this.generate_storage_class((AstStorageClass) node);
		else if(node instanceof AstTypedefName)
			this.generate_typedef_name((AstTypedefName) node);
		else if(node instanceof AstFunctionQualifier)
			this.generate_function_qualifier((AstFunctionQualifier) node);
		else if(node instanceof AstStructSpecifier)
			this.generate_struct_specifier((AstStructSpecifier) node);
		else if(node instanceof AstUnionSpecifier)
			this.generate_union_specifier((AstUnionSpecifier) node);
		else if(node instanceof AstEnumSpecifier)
			this.generate_enum_specifier((AstEnumSpecifier) node);
		else if(node instanceof AstDeclarationSpecifiers)
			this.generate_declaration_specifiers((AstDeclarationSpecifiers) node);
		else if(node instanceof AstSpecifierQualifierList)
			this.generate_specifier_qualifier_list((AstSpecifierQualifierList) node);
		else if(node instanceof AstStructDeclarator)
			this.generate_struct_declarator((AstStructDeclarator) node);
		else if(node instanceof AstEnumerator)
			this.generate_enumerator((AstEnumerator) node);
		else if(node instanceof AstName)
			this.generate_name((AstName) node);
		else if(node instanceof AstPointer)
			this.generate_pointer((AstPointer) node);
		else if(node instanceof AstArrayQualifierList)
			this.generate_array_qualifier_list((AstArrayQualifierList) node);
		else if(node instanceof AstDimension)
			this.generate_dimension((AstDimension) node);
		else if(node instanceof AstParameterBody)
			this.generate_parameter_body((AstParameterBody) node);
		else if(node instanceof AstParameterTypeList)
			this.generate_parameter_type_list((AstParameterTypeList) node);
		else if(node instanceof AstParameterDeclaration)
			this.generate_parameter_declaration((AstParameterDeclaration) node);
		else if(node instanceof AstIdentifierList)
			this.generate_identifier_list((AstIdentifierList) node);
		else if(node instanceof AstDeclarator)
			this.generate_declarator((AstDeclarator) node);
		else if(node instanceof AstAbsDeclarator)
			this.generate_abs_declarator((AstAbsDeclarator) node);
		else if(node instanceof AstInitializer)
			this.generate_initializer((AstInitializer) node);
		else if(node instanceof AstInitializerBody)
			this.generate_initializer_body((AstInitializerBody) node);
		else if(node instanceof AstFieldInitializer)
			this.generate_field_initializer((AstFieldInitializer) node);
		else if(node instanceof AstDesignatorList)
			this.generate_designator_list((AstDesignatorList) node);
		else if(node instanceof AstDeclaration)
			this.generate_declaration((AstDeclaration) node);
		else if(node instanceof AstTypeName)
			this.generate_typename((AstTypeName) node);
		else if(node instanceof AstIdExpression)
			this.generate_id_expression((AstIdExpression) node);
		else if(node instanceof AstConstant)
			this.generate_constant((AstConstant) node);
		else if(node instanceof AstLiteral)
			this.generate_literal((AstLiteral) node);
		else if(node instanceof AstArithUnaryExpression)
			this.generate_arith_unary_expression((AstArithUnaryExpression) node);
		else if(node instanceof AstBitwiseUnaryExpression)
			this.generate_bitws_unary_expression((AstBitwiseUnaryExpression) node);
		else if(node instanceof AstLogicUnaryExpression)
			this.generate_logic_unary_expression((AstLogicUnaryExpression) node);
		else if(node instanceof AstPointUnaryExpression)
			this.generate_point_unary_expression((AstPointUnaryExpression) node);
		else if(node instanceof AstIncreUnaryExpression)
			this.generate_incre_unary_expression((AstIncreUnaryExpression) node);
		else if(node instanceof AstIncrePostfixExpression)
			this.generate_incre_postf_expression((AstIncrePostfixExpression) node);
		else if(node instanceof AstArithBinaryExpression)
			this.generate_arith_binary_expression((AstArithBinaryExpression) node);
		else if(node instanceof AstBitwiseBinaryExpression)
			this.generate_bitws_binary_expression((AstBinaryExpression) node);
		else if(node instanceof AstLogicBinaryExpression)
			this.generate_logic_binary_expression((AstLogicBinaryExpression) node);
		else if(node instanceof AstRelationExpression)
			this.generate_relation_expression((AstRelationExpression) node);
		else if(node instanceof AstShiftBinaryExpression)
			this.generate_bitws_binary_expression((AstBinaryExpression) node);
		else if(node instanceof AstArithAssignExpression)
			this.generate_arith_assign_expression((AstArithAssignExpression) node);
		else if(node instanceof AstBitwiseAssignExpression)
			this.generate_bitws_assign_expression((AstBinaryExpression) node);
		else if(node instanceof AstShiftAssignExpression)
			this.generate_bitws_assign_expression((AstBinaryExpression) node);
		else if(node instanceof AstAssignExpression)
			this.generate_assign_expression((AstAssignExpression) node);
		else if(node instanceof AstArrayExpression)
			this.generate_array_expression((AstArrayExpression) node);
		else if(node instanceof AstCastExpression)
			this.generate_cast_expression((AstCastExpression) node);
		else if(node instanceof AstCommaExpression)
			this.generate_comma_expression((AstCommaExpression) node);
		else if(node instanceof AstConditionalExpression)
			this.generate_conditional_expression((AstConditionalExpression) node);
		else if(node instanceof AstField)
			this.generate_field((AstField) node);
		else if(node instanceof AstFieldExpression)
			this.generate_field_expression((AstFieldExpression) node);
		else if(node instanceof AstFunCallExpression)
			this.generate_fun_call_expression((AstFunCallExpression) node);
		else if(node instanceof AstSizeofExpression)
			this.generate_sizeof_expression((AstSizeofExpression) node);
		else if(node instanceof AstConstExpression)
			this.generate_const_expression((AstConstExpression) node);
		else if(node instanceof AstParanthExpression)
			this.generate_paranth_expression((AstParanthExpression) node);
		else if(node instanceof AstExpressionStatement)
			this.generate_expression_statement((AstExpressionStatement) node);
		else if(node instanceof AstDeclarationStatement)
			this.generate_declaration_statement((AstDeclarationStatement) node);
		else if(node instanceof AstCompoundStatement)
			this.generate_compound_statement((AstCompoundStatement) node);
		else if(node instanceof AstBreakStatement)
			this.generate_break_statement((AstBreakStatement) node);
		else if(node instanceof AstContinueStatement)
			this.generate_continue_statement((AstContinueStatement) node);
		else if(node instanceof AstGotoStatement)
			this.generate_goto_statement((AstGotoStatement) node);
		else if(node instanceof AstReturnStatement)
			this.generate_return_statement((AstReturnStatement) node);
		else if(node instanceof AstLabeledStatement)
			this.generate_labeled_statement((AstLabeledStatement) node);
		else if(node instanceof AstLabel)
			this.generate_label((AstLabel) node);
		else if(node instanceof AstCaseStatement)
			this.generate_case_statement((AstCaseStatement) node);
		else if(node instanceof AstDefaultStatement)
			this.generate_default_statement((AstDefaultStatement) node);
		else if(node instanceof AstIfStatement)
			this.generate_if_statement((AstIfStatement) node);
		else if(node instanceof AstSwitchStatement)
			this.generate_switch_statement((AstSwitchStatement) node);
		else if(node instanceof AstWhileStatement)
			this.generate_while_statement((AstWhileStatement) node);
		else if(node instanceof AstDoWhileStatement)
			this.generate_do_while_statement((AstDoWhileStatement) node);
		else if(node instanceof AstForStatement)
			this.generate_for_statement((AstForStatement) node);
		else if(node instanceof AstFunctionDefinition)
			this.generate_function_definition((AstFunctionDefinition) node);
		else throw new IllegalArgumentException(node.getClass().getSimpleName());
	}
	
	/* type definition generators */
	private void generate_struct_definition(AstStructSpecifier specifier) throws Exception {
		/** struct name **/
		this.write_tabs();
		this.write_text(this.name_of_user_specifier(specifier));
		this.write_line();
		
		/** { **/
		this.write_tabs();
		this.write_text("{");
		this.write_line();
		
		/** struct declarations here **/
		AstStructUnionBody body = specifier.get_body();
		this.tab_counter++;
		if(body.has_declaration_list()) {
			AstStructDeclarationList list = body.get_declaration_list();
			for(int i = 0; i < list.number_of_declarations(); i++) {
				AstStructDeclaration decl = list.get_declaration(i);
				AstSpecifierQualifierList specifiers = decl.get_specifiers();
				AstStructDeclaratorList decl_list = decl.get_declarators();
				
				for(int j = 0; j < decl_list.number_of_declarators(); j++) {
					AstStructDeclarator declarator = decl_list.get_declarator(j);
					
					this.write_tabs();
					this.generate(specifiers);
					this.write_text(" ");
					if(declarator.has_declarator()) 
						this.generate(declarator.get_declarator());
					if(declarator.has_expression()) {
						this.write_text(" : ");
						this.generate(declarator.get_expression());
					}
					this.write_text(";");
					this.write_line();
				}
			}
		}
		this.tab_counter--;
		
		/** }; **/
		this.write_tabs();
		this.write_text("};");
		this.write_line();
	}
	private void generate_union_definition(AstUnionSpecifier specifier) throws Exception {
		/** enum name **/
		this.write_tabs();
		this.write_text(this.name_of_user_specifier(specifier));
		this.write_line();
		
		/** { **/
		this.write_tabs();
		this.write_text("{");
		this.write_line();
		
		/** struct declarations here **/
		AstStructUnionBody body = specifier.get_body();
		this.tab_counter++;
		if(body.has_declaration_list()) {
			AstStructDeclarationList list = body.get_declaration_list();
			for(int i = 0; i < list.number_of_declarations(); i++) {
				AstStructDeclaration decl = list.get_declaration(i);
				AstSpecifierQualifierList specifiers = decl.get_specifiers();
				AstStructDeclaratorList decl_list = decl.get_declarators();
				
				for(int j = 0; j < decl_list.number_of_declarators(); j++) {
					AstStructDeclarator declarator = decl_list.get_declarator(j);
					
					this.write_tabs();
					this.generate(specifiers);
					this.write_text(" ");
					if(declarator.has_declarator()) 
						this.generate(declarator.get_declarator());
					if(declarator.has_expression()) {
						this.write_text(" : ");
						this.generate(declarator.get_expression());
					}
					this.write_text(";");
					this.write_line();
				}
			}
		}
		this.tab_counter--;
		
		/** }; **/
		this.write_tabs();
		this.write_text("};");
		this.write_line();
	}
	private void generate_enum_definition(AstEnumSpecifier specifier) throws Exception {
		/** enum name **/
		this.write_tabs();
		this.write_text(this.name_of_user_specifier(specifier));
		this.write_line();
		
		/** { **/
		this.write_tabs();
		this.write_text("{");
		this.write_line();
		
		/** enumerator+ **/
		this.tab_counter++;
		AstEnumeratorBody body = specifier.get_body();
		AstEnumeratorList list = body.get_enumerator_list();
		for(int k = 0; k < list.number_of_enumerators(); k++) {
			AstEnumerator enumerator = list.get_enumerator(k);
			this.write_tabs();
			this.write_text(enumerator.get_name().get_name());
			if(enumerator.has_expression()) {
				this.write_text(" = ");
				this.generate(enumerator.get_expression());
			}
			if(k < list.number_of_enumerators() - 1) {
				this.write_text(", ");
			}
			this.write_line();
		}
		this.tab_counter--;
		
		/** }; **/
		this.write_tabs();
		this.write_text("};");
		this.write_line();
	}
	private void generate_utype_definitions(AstNode root) throws Exception {
		Queue<AstNode> queue = new LinkedList<AstNode>(); queue.add(root);
		Stack<AstSpecifier> specifiers = new Stack<AstSpecifier>();
		
		while(!queue.isEmpty()) {
			AstNode node = queue.poll();
			for(int k = 0; k < node.number_of_children(); k++) {
				AstNode child = node.get_child(k);
				if(child != null) queue.add(child);
			}
			
			if(node instanceof AstStructSpecifier) {
				if(((AstStructSpecifier) node).has_body()) {
					specifiers.push((AstSpecifier) node);
					//this.generate_struct_definition((AstStructSpecifier) node);
				}
			}
			else if(node instanceof AstUnionSpecifier) {
				if(((AstUnionSpecifier) node).has_body()) {
					specifiers.push((AstSpecifier) node);
					//this.generate_union_definition((AstUnionSpecifier) node);
				}
			}
			else if(node instanceof AstEnumSpecifier) {
				if(((AstEnumSpecifier) node).has_body()) {
					specifiers.push((AstSpecifier) node);
					//this.generate_enum_definition((AstEnumSpecifier) node);
				}
			}
		}
		
		while(!specifiers.isEmpty()) {
			AstSpecifier specifier = specifiers.pop();
			if(specifier instanceof AstStructSpecifier) {
				this.generate_struct_definition((AstStructSpecifier) specifier);
			}
			else if(specifier instanceof AstUnionSpecifier) {
				this.generate_union_definition((AstUnionSpecifier) specifier);
			}
			else {
				this.generate_enum_definition((AstEnumSpecifier) specifier);
			}
		}
	}
	
	/* specifiers generators */
	private void generate_type_keyword(AstTypeKeyword node) throws Exception {
		switch(node.get_keyword().get_keyword()) {
		case c89_void:				this.write_text("void"); 		break;
		case c99_bool:				this.write_text("_Bool"); 		break;
		case c89_char:				this.write_text("char"); 		break;
		case c89_short:				this.write_text("short"); 		break;
		case c89_int:				this.write_text("int"); 		break;
		case c89_long:				this.write_text("long"); 		break;
		case c89_signed:			this.write_text("signed"); 		break;
		case c89_unsigned:			this.write_text("unsigned"); 	break;
		case c89_float:				this.write_text("float"); 		break;
		case c89_double:			this.write_text("double"); 		break;
		case c99_complex:			this.write_text("_Complex"); 	break;
		case c99_imaginary:			this.write_text("_Imaginary"); 	break;
		case gnu_builtin_va_list:	this.write_text("__builtin_va_list"); break;
		default: throw new IllegalArgumentException("Unknown: " + node.get_location().read());
		}
	}
	private void generate_type_qualifier(AstTypeQualifier node) throws Exception {
		switch(node.get_keyword().get_keyword()) {
		case c89_const:		this.write_text("const"); 		break;
		case c89_volatile:	this.write_text("volatile");	break;
		case c99_restrict:	this.write_text("restrict");	break;
		default: throw new IllegalArgumentException("Unknown: " + node.get_location().read());
		}
	}
	private void generate_storage_class(AstStorageClass node) throws Exception {
		switch(node.get_keyword().get_keyword()) {
		case c89_auto:		this.write_text("auto");	break;
		case c89_static:	this.write_text("static");	break;
		case c89_extern:	this.write_text("extern");	break;
		case c89_register:	this.write_text("register");break;
		case c89_typedef:	this.write_text("typedef");	break;
		default: throw new IllegalArgumentException("Unknown: " + node.get_location().read());
		}
	}
	private void generate_typedef_name(AstTypedefName node) throws Exception {
		this.write_text(node.get_name());
	}
	private void generate_function_qualifier(AstFunctionQualifier node) throws Exception {
		switch(node.get_keyword().get_keyword()) {
		case c99_inline:	this.write_text("inline"); break;
		default: throw new IllegalArgumentException("Unknown: " + node.get_location().read());
		}
	}
	private void generate_struct_specifier(AstStructSpecifier node) throws Exception {
		this.write_text(this.name_of_user_specifier(node));
	}
	private void generate_union_specifier(AstUnionSpecifier node) throws Exception {
		this.write_text(this.name_of_user_specifier(node));
	}
	private void generate_enum_specifier(AstEnumSpecifier node) throws Exception {
		this.write_text(this.name_of_user_specifier(node));
	}
	private void generate_declaration_specifiers(AstDeclarationSpecifiers node) throws Exception {
		if(node.number_of_specifiers() > 0) {
			for(int k = 0; k < node.number_of_specifiers(); k++) {
				this.generate(node.get_specifier(k));
				if(k < node.number_of_specifiers() - 1) {
					this.write_text(" ");
				}
			}
		}
		else {
			this.write_text("int");
		}
	}
	private void generate_specifier_qualifier_list(AstSpecifierQualifierList node) throws Exception {
		if(node.number_of_specifiers() > 0) {
			for(int k = 0; k < node.number_of_specifiers(); k++) {
				this.generate(node.get_specifier(k));
				if(k < node.number_of_specifiers() - 1) {
					this.write_text(" ");
				}
			}
		}
		else {
			this.write_text("int");
		}
	}
	private void generate_struct_declarator(AstStructDeclarator node) throws Exception {
		if(node.has_declarator()) {
			this.generate(node.get_declarator());
		}
		if(node.has_expression()) {
			this.write_text(": ");
			this.generate(node.get_expression());
		}
	}
	private void generate_enumerator(AstEnumerator node) throws Exception {
		this.generate(node.get_name());
		if(node.has_expression()) {
			this.write_text(" = ");
			this.generate(node.get_expression());
		}
	}
	
	/* declarator generators */
	private AstName find_name_in(AstDeclarator declarator) throws Exception {
		while(declarator.get_production() 
				!= DeclaratorProduction.identifier) {
			declarator = declarator.get_declarator();
		}
		return declarator.get_identifier();
	}
	private void generate_name(AstName node) throws Exception {
		this.write_text(node.get_name());
	}
	private void generate_pointer(AstPointer node) throws Exception {
		for(int k = 0; k < node.number_of_keywords(); k++) {
			AstNode keyword = node.get_specifier(k);
			if(keyword instanceof AstKeyword) {
				switch(((AstKeyword) keyword).get_keyword()) {
				case c89_const:		this.write_text("const");	break;
				case c89_volatile:	this.write_text("volatile");break;
				case c99_restrict:	this.write_text("restrict");break;
				default: throw new IllegalArgumentException("Unknown " + keyword.get_location().read());
				}
			}
			else {
				switch(((AstPunctuator) keyword).get_punctuator()) {
				case ari_mul:	this.write_text("*"); break;
				default: throw new IllegalArgumentException("Unknown " + keyword.get_location().read());
				}
			}
			
			if(k < node.number_of_keywords() - 1) {
				this.write_text(" ");
			}
		}
	}
	private void generate_array_qualifier_list(AstArrayQualifierList node) throws Exception {
		for(int k = 0; k < node.number_of_keywords(); k++) {
			AstKeyword keyword = node.get_keyword(k);
			switch(keyword.get_keyword()) {
			case c89_static:	this.write_text("static"); 		break;
			case c89_const:		this.write_text("const"); 		break;
			case c89_volatile:	this.write_text("volatile"); 	break;
			case c99_restrict:	this.write_text("restrict"); 	break;
			default: throw new IllegalArgumentException(keyword.get_location().read());
			}
		}
	}
	private void generate_dimension(AstDimension node) throws Exception {
		this.write_text("[");
		if(node.has_array_qualifier_list()) {
			this.generate(node.get_array_qualifier_list());
			if(node.has_expression()) {
				this.write_text(" ");
				this.generate(node.get_expression());
			}
		}
		else if(node.has_expression()) {
			this.generate(node.get_expression());
		}
		this.write_text("]");
	}
	private void generate_parameter_body(AstParameterBody node) throws Exception {
		this.write_text("(");
		if(node.has_parameter_type_list()) {
			this.generate(node.get_parameter_type_list());
		}
		else if(node.has_identifier_list()) {
			this.generate(node.get_identifier_list());
		}
		this.write_text(")");
	}
	private void generate_parameter_type_list(AstParameterTypeList node) throws Exception {
		AstParameterList list = node.get_parameter_list();
		for(int k = 0; k < list.number_of_parameters(); k++) {
			this.generate(list.get_parameter(k));
			if(k < list.number_of_parameters() - 1) {
				this.write_text(", ");
			}
		}
		
		if(node.has_ellipsis()) {
			this.write_text(", ...");
		}
	}
	private void generate_parameter_declaration(AstParameterDeclaration node) throws Exception {
		this.generate(node.get_specifiers());
		if(node.has_declarator()) {
			this.write_text(" ");
			this.generate(node.get_declarator());
		}
		else if(node.has_abs_declarator()) {
			this.write_text(" ");
			this.generate(node.get_abs_declarator());
		}
	}
	private void generate_identifier_list(AstIdentifierList node) throws Exception {
		/** 1. find the function definition node **/
		AstNode parent = node.get_parent();
		while(parent != null) {
			if(parent instanceof AstFunctionDefinition) {
				break;
			}
			else parent = parent.get_parent();
		}
		AstFunctionDefinition definition;
		if(parent == null)
			throw new IllegalArgumentException(
					"Invalid syntax: " + node.get_location().read());
		else definition = (AstFunctionDefinition) parent;
		
		/** 2. collect the declaration in declaration list in definition **/
		AstDeclarationList declaration_list = definition.get_declaration_list();
		Map<String, Object[]> name_spec_decl = new HashMap<String, Object[]>();
		for(int i = 0; i < declaration_list.number_of_declarations(); i++) {
			AstDeclaration declaration = 
					declaration_list.get_declaration(i).get_declaration();
			
			AstDeclarationSpecifiers specifiers = declaration.get_specifiers();
			
			AstInitDeclaratorList list = declaration.get_declarator_list();
			for(int j = 0; j < list.number_of_init_declarators(); j++) {
				AstInitDeclarator init_decl = list.get_init_declarator(j);
				AstDeclarator declarator = init_decl.get_declarator();
				String name = this.find_name_in(declarator).get_name();
				
				name_spec_decl.put(name, new Object[] { specifiers, declarator });
			}
		}
		
		/** 3. generate the code of each identifier in declaration **/
		for(int k = 0; k < node.number_of_identifiers(); k++) {
			String name = node.get_identifier(k).get_name();
			Object[] spec_decl = name_spec_decl.get(name);
			AstDeclarationSpecifiers specifiers = 
					(AstDeclarationSpecifiers) spec_decl[0];
			AstDeclarator declarator = (AstDeclarator) spec_decl[1];
			
			this.generate(specifiers);
			this.write_text(" ");
			this.generate(declarator);
			
			if(k < node.number_of_identifiers() - 1) {
				this.write_text(", ");
			}
		}
	}
	private void generate_declarator(AstDeclarator node) throws Exception {
		switch(node.get_production()) {
		case pointer_declarator:
			this.generate(node.get_pointer());
			this.write_text(" ");
			this.generate(node.get_declarator());
			break;
		case declarator_dimension:
			this.generate(node.get_declarator());
			this.write_text(" ");
			this.generate(node.get_dimension());
			break;
		case declarator_parambody:
			this.generate(node.get_declarator());
			this.write_text(" ");
			this.generate(node.get_parameter_body());
			break;
		case lp_declarator_rp:
			this.write_text("(");
			this.generate(node.get_declarator());
			this.write_text(")");
			break;
		case identifier:
			this.generate(node.get_identifier());
			break;
		default: throw new IllegalArgumentException(node.get_location().read());
		}
	}
	private void generate_abs_declarator(AstAbsDeclarator node) throws Exception {
		switch(node.get_production()) {
		case pointer_declarator:
			this.generate(node.get_pointer());
			if(node.get_declarator() != null) {
				this.write_text(" ");
				this.generate(node.get_declarator());
			}
			break;
		case declarator_dimension:
			if(node.get_declarator() != null) {
				this.generate(node.get_declarator());
				this.write_text(" ");
			}
			this.generate(node.get_dimension());
			break;
		case declarator_parambody:
			if(node.get_declarator() != null) {
				this.generate(node.get_declarator());
				this.write_text(" ");
			}
			this.generate(node.get_parameter_body());
			break;
		case lp_declarator_rp:
			this.write_text("(");
			this.generate(node.get_declarator());
			this.write_text(")");
			break;
		default: throw new IllegalArgumentException(node.get_location().read());
		}
	}
	
	/* declaration & initializer generator */
	private void generate_initializer(AstInitializer node) throws Exception {
		if(node.is_body()) generate(node.get_body());
		else this.generate(node.get_expression());
	}
	private void generate_initializer_body(AstInitializerBody node) throws Exception {
		this.write_text("{ ");
		
		AstInitializerList list = node.get_initializer_list();
		for(int k = 0; k < list.number_of_initializer(); k++) {
			this.generate(list.get_initializer(k));
			if(k < list.number_of_initializer() - 1) {
				this.write_text(", ");
			}
		}
		
		if(node.has_tail_comma()) {
			this.write_text(",");
		}
		
		this.write_text(" }");
	}
	private void generate_field_initializer(AstFieldInitializer node) throws Exception {
		if(node.has_designator_list()) {
			this.generate(node.get_designator_list());
			this.write_text(" = ");
		}
		this.generate(node.get_initializer());
	}
	private void generate_designator_list(AstDesignatorList node) throws Exception {
		this.write_text(node.get_location().trim_code());
	}
	private void generate_declaration(AstDeclaration node) throws Exception {
		/** previous type definition **/ this.generate_utype_definitions(node);
		
		AstDeclarationSpecifiers specifiers = node.get_specifiers();
		if(node.has_declarator_list()) {
			AstInitDeclaratorList declarators = node.get_declarator_list();
			
			for(int k = 0; k < declarators.number_of_init_declarators(); k++) {
				AstInitDeclarator init_decl = declarators.get_init_declarator(k);
				AstDeclarator declarator = init_decl.get_declarator();
				
				/** specifiers declarator (= initializer); **/
				this.write_tabs();
				this.generate(specifiers);
				this.write_text(" ");
				this.generate(declarator);
				if(init_decl.has_initializer()) {
					this.write_text(" = ");
					this.generate(init_decl.get_initializer());
				}
				this.write_text(";");
				this.write_line();
			}
		}
		else {
			this.write_tabs();
			this.generate(node.get_specifiers());
			this.write_text(";");
			this.write_line();
		}
	}
	private void generate_typename(AstTypeName node) throws Exception {
		/** previous type definition **/ 
		this.generate_utype_definitions(node);
		
		this.generate(node.get_specifiers());
		if(node.has_declarator()) {
			this.write_text(" ");
			this.generate(node.get_declarator());
		}
	}
	
	/* expression generator */
	private void generate_id_expression(AstIdExpression node) throws Exception {
		CName cname = node.get_cname();
		if(cname instanceof CInstanceName
			|| cname instanceof CParameterName) {
			this.write_text(node.get_name());
		}
		else {
			int value = ((CEnumeratorName) cname).get_enumerator().get_value();
			this.write_text(value + "");
		}
	}
	private void generate_constant(AstConstant node) throws Exception {
		this.write_text(node.get_location().read());
	}
	private void generate_literal(AstLiteral node) throws Exception {
		this.write_text(node.get_location().read());
	}
	private void generate_arith_unary_expression(AstArithUnaryExpression node) throws Exception {
		COperator operator = node.get_operator().get_operator();
		switch(operator) {
		case positive:
			this.generate(node.get_operand()); 
			break;
		case negative:
			this.write_text("-");
			this.generate(node.get_operand()); 
			break;
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	private void generate_bitws_unary_expression(AstBitwiseUnaryExpression node) throws Exception {
		this.write_text("~");
		this.generate(node.get_operand());
	}
	private void generate_logic_unary_expression(AstLogicUnaryExpression node) throws Exception {
		this.write_text("!");
		this.generate(node.get_operand());
	}
	private void generate_point_unary_expression(AstPointUnaryExpression node) throws Exception {
		COperator operator = node.get_operator().get_operator();
		switch(operator) {
		case address_of:	this.write_text("&"); break;
		case dereference:	this.write_text("*"); break;
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
		this.generate(node.get_operand());
	}
	private void generate_incre_unary_expression(AstIncreUnaryExpression node) throws Exception {
		COperator operator = node.get_operator().get_operator();
		switch(operator) {
		case increment:	this.write_text("++"); break;
		case decrement:	this.write_text("--"); break;
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
		this.generate(node.get_operand());
	}
	private void generate_incre_postf_expression(AstIncrePostfixExpression node) throws Exception {
		COperator operator = node.get_operator().get_operator();
		this.generate(node.get_operand());
		switch(operator) {
		case increment:	this.write_text("++"); break;
		case decrement:	this.write_text("--"); break;
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	private void generate_arith_binary_expression(AstArithBinaryExpression node) throws Exception {
		COperator operator = node.get_operator().get_operator();
		
		this.generate(node.get_loperand());
		switch(operator) {
		case arith_add: this.write_text(" + "); break;
		case arith_sub: this.write_text(" - "); break;
		case arith_mul: this.write_text(" * "); break;
		case arith_div: this.write_text(" / "); break;
		case arith_mod: this.write_text(" % "); break;
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
		this.generate(node.get_roperand());
	}
	private void generate_bitws_binary_expression(AstBinaryExpression node) throws Exception {
		COperator operator = node.get_operator().get_operator();
		
		this.generate(node.get_loperand());
		switch(operator) {
		case bit_and:		this.write_text(" & "); break;
		case bit_or:		this.write_text(" | "); break;
		case bit_xor:		this.write_text(" ^ "); break;	
		case left_shift:	this.write_text(" << ");break;
		case righ_shift:	this.write_text(" >> ");break;
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
		this.generate(node.get_roperand());
	}
	private void generate_logic_binary_expression(AstLogicBinaryExpression node) throws Exception {
		COperator operator = node.get_operator().get_operator();
		
		this.generate(node.get_loperand());
		switch(operator) {
		case logic_and:	this.write_text(" && "); break;
		case logic_or:	this.write_text(" || "); break;
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
		this.generate(node.get_roperand());
	}
	private void generate_relation_expression(AstRelationExpression node) throws Exception {
		COperator operator = node.get_operator().get_operator();
		
		this.generate(node.get_loperand());
		switch(operator) {
		case greater_tn:	this.write_text(" > ");	break;
		case greater_eq:	this.write_text(" >= ");break;
		case smaller_tn:	this.write_text(" < "); break;
		case smaller_eq:	this.write_text(" <= ");break;
		case equal_with:	this.write_text(" == ");break;
		case not_equals:	this.write_text(" != ");break;
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
		this.generate(node.get_roperand());
	}
	private void generate_arith_assign_expression(AstArithAssignExpression node) throws Exception {
		COperator operator = node.get_operator().get_operator();
		
		this.generate(node.get_loperand());
		switch(operator) {
		case arith_add_assign: this.write_text(" += "); break;
		case arith_sub_assign: this.write_text(" -= "); break;
		case arith_mul_assign: this.write_text(" *= "); break;
		case arith_div_assign: this.write_text(" /= "); break;
		case arith_mod_assign: this.write_text(" %= "); break;
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
		this.generate(node.get_roperand());
	}
	private void generate_bitws_assign_expression(AstBinaryExpression node) throws Exception {
		COperator operator = node.get_operator().get_operator();
		
		this.generate(node.get_loperand());
		switch(operator) {
		case bit_and_assign: 	this.write_text(" &= "); break;
		case bit_or_assign: 	this.write_text(" |= "); break;
		case bit_xor_assign: 	this.write_text(" ^= "); break;
		case left_shift_assign: this.write_text(" <<= ");break;
		case righ_shift_assign: this.write_text(" >>= ");break;
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
		this.generate(node.get_roperand());
	}
	private void generate_assign_expression(AstAssignExpression node) throws Exception {
		this.generate(node.get_loperand());
		this.write_text(" = ");
		this.generate(node.get_roperand());
	}
	private void generate_array_expression(AstArrayExpression node) throws Exception {
		this.generate(node.get_array_expression());
		this.write_text("[");
		this.generate(node.get_dimension_expression());
		this.write_text("]");
	}
	private void generate_cast_expression(AstCastExpression node) throws Exception {
		this.write_text("(");
		this.generate(node.get_typename());
		this.write_text(") ");
		this.generate(node.get_expression());
	}
	private void generate_comma_expression(AstCommaExpression node) throws Exception {
		for(int k = 0; k < node.number_of_arguments(); k++) {
			this.generate(node.get_expression(k));
			if(k < node.number_of_arguments() - 1) {
				this.write_text(", ");
			}
		}
	}
	private void generate_conditional_expression(AstConditionalExpression node) throws Exception {
		this.generate(node.get_condition());
		this.write_text(" ? ");
		this.generate(node.get_true_branch());
		this.write_text(" : ");
		this.generate(node.get_false_branch());
	}
	private void generate_field(AstField node) throws Exception {
		this.write_text(node.get_name());
	}
	private void generate_field_expression(AstFieldExpression node) throws Exception {
		this.generate(node.get_body());
		switch(node.get_operator().get_punctuator()) {
		case dot:	this.write_text("."); break;
		case arrow:	this.write_text("->");break;
		default: throw new IllegalArgumentException(node.get_operator().get_punctuator().toString());
		}
		this.generate(node.get_field());
	}
	private void generate_fun_call_expression(AstFunCallExpression node) throws Exception {
		this.generate(node.get_function());
		this.write_text("(");
		if(node.has_argument_list()) {
			AstArgumentList list = node.get_argument_list();
			for(int k = 0; k < list.number_of_arguments(); k++) {
				this.generate(list.get_argument(k));
				if(k < list.number_of_arguments() - 1) {
					this.write_text(", ");
				}
			}
		}
		this.write_text(")");
	}
	private void generate_sizeof_expression(AstSizeofExpression node) throws Exception {
		this.write_text("sizeof(");
		if(node.is_expression())
			this.generate(node.get_expression());
		else this.generate(node.get_typename());
		this.write_text(")");
	}
	private void generate_const_expression(AstConstExpression node) throws Exception {
		this.generate(node.get_expression());
	}
	private void generate_paranth_expression(AstParanthExpression node) throws Exception {
		this.write_text("(");
		this.generate(node.get_sub_expression());
		this.write_text(")");
	}
	
	/* statement generators */
	private void generate_expression_statement(AstExpressionStatement node) throws Exception {
		this.write_tabs();
		if(node.has_expression())
			this.generate(node.get_expression());
		this.write_text(";");
		this.write_line();
	}
	private void generate_declaration_statement(AstDeclarationStatement node) throws Exception {
		this.generate(node.get_declaration());
	}
	private void generate_compound_statement(AstCompoundStatement node) throws Exception {
		this.write_tabs();
		this.write_text("{");
		this.write_line();
		
		this.tab_counter++;
		if(node.has_statement_list()) {
			AstStatementList list = node.get_statement_list();
			for(int k = 0; k < list.number_of_statements(); k++) {
				this.generate(list.get_statement(k));
			}
		}
		this.tab_counter--;
		
		this.write_tabs();
		this.write_text("}");
		this.write_line();
	}
	private void generate_break_statement(AstBreakStatement node) throws Exception {
		this.write_tabs();
		this.write_text("break;");
		this.write_line();
	}
	private void generate_continue_statement(AstContinueStatement node) throws Exception {
		this.write_tabs();
		this.write_text("continue;");
		this.write_line();
	}
	private void generate_goto_statement(AstGotoStatement node) throws Exception {
		this.write_tabs();
		this.write_text("goto ");
		this.generate(node.get_label());
		this.write_text(";");
		this.write_line();
	}
	private void generate_return_statement(AstReturnStatement node) throws Exception {
		this.write_tabs();
		this.write_text("return");
		if(node.has_expression()) {
			this.write_text(" ");
			this.generate(node.get_expression());
		}
		this.write_text(";");
		this.write_line();
	}
	private void generate_label(AstLabel node) throws Exception {
		this.write_text(node.get_name());
	}
	private void generate_labeled_statement(AstLabeledStatement node) throws Exception {
		this.tab_counter--;
		this.write_tabs();
		this.tab_counter++;
		this.generate(node.get_label());
		this.write_text(" : ");
		this.write_line();
	}
	private void generate_case_statement(AstCaseStatement node) throws Exception {
		this.tab_counter--;
		this.write_tabs();
		this.tab_counter++;
		this.write_text("case ");
		this.generate(node.get_expression());
		this.write_text(" : ");
		this.write_line();
	}
	private void generate_default_statement(AstDefaultStatement node) throws Exception {
		this.tab_counter--;
		this.write_tabs();
		this.tab_counter++;
		this.write_text("default : ");
		this.write_line();
	}
	private void generate_if_statement(AstIfStatement node) throws Exception {
		this.write_tabs();
		this.write_text("if (");
		this.generate(node.get_condition());
		this.write_text(")");
		this.write_line();
		
		AstStatement tbranch = node.get_true_branch();
		if(!(tbranch instanceof AstCompoundStatement)) {
			this.tab_counter++;
		}
		this.generate(tbranch);
		if(!(tbranch instanceof AstCompoundStatement)) {
			this.tab_counter--;
		}
		
		if(node.has_else()) {
			this.write_tabs();
			this.write_text("else");
			this.write_line();
			
			AstStatement fbranch = node.get_false_branch();
			if(!(fbranch instanceof AstCompoundStatement)) {
				this.tab_counter++;
			}
			this.generate(fbranch);
			if(!(fbranch instanceof AstCompoundStatement)) {
				this.tab_counter--;
			}
		}
		/*
		else {
			this.write_tabs();
			this.write_text("else ;");
			this.write_line();
		}
		*/
	}
	private void generate_switch_statement(AstSwitchStatement node) throws Exception {
		this.write_tabs();
		this.write_text("switch (");
		this.generate(node.get_condition());
		this.write_text(")");
		this.write_line();
		
		AstStatement body = node.get_body();
		if(!(body instanceof AstCompoundStatement)) {
			this.tab_counter++;
		}
		this.generate(body);
		if(!(body instanceof AstCompoundStatement)) {
			this.tab_counter--;
		}
	}
	private void generate_while_statement(AstWhileStatement node) throws Exception {
		this.write_tabs();
		this.write_text("while (");
		this.generate(node.get_condition());
		this.write_text(")");
		this.write_line();
		
		AstStatement body = node.get_body();
		if(!(body instanceof AstCompoundStatement)) {
			this.tab_counter++;
		}
		this.generate(body);
		if(!(body instanceof AstCompoundStatement)) {
			this.tab_counter--;
		}
	}
	private void generate_do_while_statement(AstDoWhileStatement node) throws Exception {
		this.write_tabs();
		this.write_text("do");
		this.write_line();
		
		AstStatement body = node.get_body();
		if(!(body instanceof AstCompoundStatement)) {
			this.tab_counter++;
		}
		this.generate(body);
		if(!(body instanceof AstCompoundStatement)) {
			this.tab_counter--;
		}
		
		this.write_tabs();
		this.write_text("while (");
		this.generate(node.get_condition());
		this.write_text(") ;");
		this.write_line();
	}
	private void generate_for_statement(AstForStatement node) throws Exception {
		this.write_tabs();
		this.write_text("for (");
		this.write_text(node.get_initializer().get_location().read());
		this.write_text(" ");
		this.write_text(node.get_condition().get_location().read());
		this.write_text(" ");
		if(node.has_increment()) {
			this.generate(node.get_increment());
		}
		this.write_text(")");
		this.write_line();
		
		AstStatement body = node.get_body();
		if(!(body instanceof AstCompoundStatement)) {
			this.tab_counter++;
		}
		this.generate(body);
		if(!(body instanceof AstCompoundStatement)) {
			this.tab_counter--;
		}
	}
	
	/* unit generator */
	private void generate_function_definition(AstFunctionDefinition node) throws Exception {
		this.generate(node.get_specifiers());
		this.write_text(" ");
		this.generate(node.get_declarator());
		this.write_line();
		this.generate(node.get_body());
		this.write_line();
	}
	
	/* main generator methods */
	private void generate_external_units(AstTranslationUnit root) throws Exception {
		for(int k = 0; k < root.number_of_units(); k++) {
			this.generate(root.get_unit(k));
		}
	}
	
	/**
	 * @param node
	 * @return the normalized code generated from the given node.
	 * @throws Exception
	 */
	public static String normalize(AstNode node) throws Exception {
		normalizer.init();
		normalizer.generate(node);
		return normalizer.buffer.toString();
	}
	/**
	 * @param tree
	 * @return the normalized and correct code for entire program file.
	 * @throws Exception
	 */
	public static String normalize(AstTree tree) throws Exception {
		normalizer.init();
		/* implement the parsing process */
		normalizer.generate_utype_declarations(tree.get_ast_root());
		normalizer.generate_external_units(tree.get_ast_root());
		return normalizer.buffer.toString();
	}
	
}
