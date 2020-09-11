package com.jcsa.jcparse.parse.parser2;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterBody;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterDeclaration;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterList;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
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
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstDeclarationList;
import com.jcsa.jcparse.lang.astree.unit.AstExternalUnit;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirArithExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirBitwsExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeclarator;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirImplicator;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirLogicExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirRelationExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReturnPoint;
import com.jcsa.jcparse.lang.irlang.expr.CirType;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.impl.AstCirPairImpl;
import com.jcsa.jcparse.lang.irlang.impl.CirTreeImpl;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
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
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.unit.CirTransitionUnit;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.lexical.CStorageClass;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstance;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;
import com.jcsa.jcparse.lang.scope.CScope;

/**
 * Used to parse the Abstract syntactic tree to the C-like intermediate representation code based on the following rules.<br>
 * <code>
 * 	+-----------------------------------------------------------------------------------------------------------------------+
 * 		translation_unit			|==	function_definition(#init)	{function_definition*}
 * 		function_definition			|==	function_definition
 * 		declaration_statement		|== init_assign_statement (declarator = default_value|expression)
 * 	+-----------------------------------------------------------------------------------------------------------------------+
 * 	
 * 	+-----------------------------------------------------------------------------------------------------------------------+
 * 		id_expression (var)			|== identifier 			[cname]
 * 		id_expression (enum)		|== const_expression 	[integer]
 * 		constant					|== const_expression 	[character|short|integer|long|float|double]
 * 		literal						|== string_literal	 	[string]
 * 		E --> +E1					|== (E1)				{skip}
 * 		E --> -E1					|== (arith_neg (E1))	
 * 		E --> ~E1					|== (bitws_rsv (E1))
 * 		E --> !E1					|== (logic_not (E1))
 * 		E --> &E1					|== (address (E1))
 * 		E --> *E1					|== (defer (E1))
 * 		E --> ++E					|== [(assign (E) as (E) + 1); return (E)]
 * 		E --> --E					|== [(assign (E) as (E) - 1); return (E)]
 * 		E --> E++					|== [(assign #temporal as (E)); (assign (E) as (E) + 1); return (#temporal)]
 * 		E --> E--					|== [(assign #temporal as (E)); (assign (E) as (E) + 1); return (#temporal)]
 * 		E --> E1 + E2				|== (arith_add (E1) (E2))
 * 		E --> E1 -= E2				|== [(assign (E1) as (arith_sub (E1) (E2))); return (E1)]
 * 		E --> E1 = E2				|== [(assign (E1) as (E2)); return (E1)]
 * 		E --> E1 && E2				|== {E1}
 * 									|== (assign $temporal as (E1))
 * 									|== (if $temporal then L1 else L2)				@require	{true|false}
 * 									|== [L1: {E2}; (assign $temporal as (E2));]		@solve		{true}
 * 									|== [L2: if_end_statement]						@solve		{false}
 * 		E --> E1 || E2				|== {E1}
 * 									|== (assign $temporal as (E1))
 * 									|== (if (not $temporal) then L1 else L2)		@require	{true|false}
 * 									|== [L1: {E2}; (assign $temporal as (E2));]		@solve		{true}
 * 									|== [L2: if_end_statement]						@solve		{false}
 * 		E1 [E2]						|== (defer (arith_add (E1) (E2)))
 * 		E1.F2						|== (field (E1) (F2))
 * 		E1->F2						|== (field (defer (E1)) (F2))
 * 		E1, E2, ..., En				|== [{E1}; {E2}; ... {En}; return (En)]
 * 		(T) E						|== (cast (T) (E))
 * 		E1 ? E2 : E3				|== [(if (E1) then L1 else L2); 
 * 									|== [L1: (assign #temporal as (E1)); (goto L3);]
 * 									|== [L2: (assign #temporal as (E2)); (goto L3);]
 * 									|== [L3: if_end_statement]
 * 									|== return #temporal
 * 		F(A1, A2, ..., An)			|== [{F1}; {A1}; {A2}; ...; {An};]
 * 									|== (call (F) ((A1) (A2) (A3) ... (An)))
 * 									|== (wait_assign #temporal as (wait (F)))
 * 									|== return #temporal
 * 		sizeof (T|E)				|== const_expression [integer]
 * 		{ E1, E2, ..., En }			|== (initializer_body (E1) (E2) ... (En))
 * 		declarator					|== declarator [cname]
 * 		init_declarator				|== (init_assign declarator as default_value)
 * 		declarator = initializer	|== (init_assign declarator as (initializer))
 * 	+-----------------------------------------------------------------------------------------------------------------------+
 * 	
 * 	+-----------------------------------------------------------------------------------------------------------------------+
 * 		E ;							|== {E}
 * 		;							|== {}
 * 		D ;							|== {D}
 * 		{S1; S2; ... Sn;}			|== {{S1}; {S2}; {S3}; ...; {Sn};}
 * 		break;						|== (goto ::<break>)							@require 	{break}
 * 		continue;					|== (goto ::<continue>)							@require 	{continue}
 * 		goto L;						|== (goto $$<{label}>)							@require 	{label}
 * 		return;						|== (goto $$<return>)							@require 	{return}
 * 		return E;					|== {E}
 * 									|== (assign #returnPoint as (E))
 * 									|== (goto $$<return>)							@require 	{return}
 * 		L :							|== {label_statement}
 * 									|== @solve {L}.requires							@solve		{L}
 * 		case E :					|== {E}
 * 									|== (case (equal_with #temporal (E) or (L)) 	@solve		{case}
 * 																					@require	{case}	<== L
 * 		default :					|== (default_statement)							@solve		{case}
 * 																					@remove		{case}
 * 		if E S						|==	{E}
 * 									|== (if (E) then L1 else L2)					@require	{true}
 * 																					@require	{false}
 * 									|== [L1: {S1}; (goto L2);]						@solve		{true}
 * 																					@require	{false}
 * 									|== [L2: if_end_statement]						@solve		{false}
 * 		if E S1 else S2				|== {E}
 * 									|== (if (E) then L1 else L2)					@require	{true}
 * 																					@require	{false}
 * 		switch E S					|== {E}	
 * 									|== (assign #temporal as (E))
 * 									|== (goto L1)									@require	{case}
 * 									|== {S}											
 * 									|== [L2: case_end_statement]					@solve		{break}
 * 		while E S					|== {E}											@solve		{continue}
 * 									|== (if (E) then L1 else L2]					@require	{true}
 * 																					@require	{false}
 * 									|== [L1: {S} (goto {continue})]					@solve		{true}
 * 																					@require	
 * 									|== [L2: if_end_statement]						@solve		{false}
 * 																					@solve		{break}
 * 		do S while E;				|==	[L: {S};]									@solve		{true}
 * 									|== {E}											@solve		{continue}
 * 									|== (if (E) then L1 else L2)					@require	{true|false}
 * 									|== (L2: if_end_statement)						@solve		{false|break}
 * 		for(S1 S2 E3) S4			|== {S1}
 * 									|== {S2}
 * 									|== (if (S2) then L1 else L2)					@require	{true|false}		
 * 									|== {L1: {S4}}									@solve		{true}
 * 																					@require	{break|continue}
 * 									|== {{E3}; (goto {true});}						@solve		{continue}
 * 									|== (if_end_statement)							@solve		{break|false}
 * 	+-----------------------------------------------------------------------------------------------------------------------+
 * 
 * </code>
 * <br>
 * @author yukimula
 *
 */
public class CirParser {
	
	public static final String InitFunctionName = "#init";
	
	/* definitions and constructor */
	private ACParserData data;
	private CirTreeImpl cir_tree;
	private ACPModule cur_module;
	private CTypeFactory type_factory;
	private CRunTemplate template;
	private CirParser(AstTranslationUnit ast_root, CRunTemplate 
			template) throws IllegalArgumentException {
		this.template = template;
		this.data = new ACParserData(ast_root);
		this.cir_tree = this.data.get_cir_tree();
		this.cur_module = null;
		this.type_factory = new CTypeFactory();
	}
	
	/* basic methods */
	/**
	 * find the base name that defines or declares the instance in the source code.
	 * @param cname
	 * @return
	 * @throws Exception
	 */
	private CName find_base_name(CInstanceName cname) throws Exception {
		/* 1. initialization */
		CScope scope = cname.get_scope();
		CName final_name = null;
		
		/* 2. try to find the scope where the definition is provided */
		while(scope != null) {
			
			/* A. verify whether there are cnames in current scope */
			if(scope.get_name_table().has_name(cname.get_name())) {
				/* A.1 get the instance cname in current scope and record it as final name */
				CInstanceName iname = (CInstanceName) scope.
						get_name_table().get_name(cname.get_name());
				final_name = iname;	// record the final cname if not defined in current file
				
				/* A.2 traverse the links of the instance name until definition is found */
				while(iname != null) {
					CInstance instance = iname.get_instance();
					if(instance.get_storage_class() == CStorageClass.c_extern) {
						iname = iname.get_next_name();
					}
					else {
						return iname;	// when the definition name found
					}
				}
			}
			
			/* B. get to its parent scope to find any definition name */
			scope = scope.get_parent();
		}
		
		/* 3. when no valid cname found that represents the definition of the instance in code */
		if(final_name == null)
			throw new IllegalArgumentException("unable to find base-name for " + cname.get_name());
		/* 4. return the final recorded name as the instance not defined in current source file */
		else return final_name;	
	}
	/**
	 * generate the const-expression node in IR program representing the integer value
	 * @param value
	 * @return
	 * @throws Exception
	 */
	private CirConstExpression get_integer(int value) throws Exception {
		CConstant constant = new CConstant(); constant.set_int(value);
		return this.cir_tree.new_const_expression(null, constant, constant.get_type());
	}
	/**
	 * get the solution corresponding to the AST source node in current module
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution get_solution(AstNode source) throws Exception {
		if(this.cur_module == null)
			throw new IllegalArgumentException("invalic access: no module defined");
		else return this.cur_module.get_solution(source);
	}
	/**
	 * find the statement in which the node is defined
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private CirStatement find_statement_of(CirNode node) throws Exception {
		while(node != null) {
			if(node instanceof CirStatement)
				return (CirStatement) node;
			else node = node.get_parent();
		}
		return null;
	}
	/**
	 * build up the code range index in the CIR-tree with respect to all the
	 * solutions created in the module.
	 * @param module
	 * @throws Exception
	 */
	private void build_cir_range(ACPModule module) throws Exception {
		Iterable<ACPSolution> solutions = module.get_solutions();
		
		for(ACPSolution solution : solutions) {
			/* 1. declarations */
			AstNode ast_source = solution.get_ast_source();
			AstCirPairImpl ast_cir = (AstCirPairImpl) 
					this.cir_tree.new_cir_range(ast_source);
			CirStatement beg_statement, end_statement;
			CirExpression result = solution.get_result();
			
			/* 2. get statement range */
			if(solution.executional()) {
				beg_statement = solution.get_beg_statement();
				end_statement = solution.get_end_statement();
			}
			else if(result != null) {
				beg_statement = this.find_statement_of(result);
				end_statement = beg_statement;
			}
			else {
				beg_statement = null;
				end_statement = null;
			}
			
			/* update the index range */
			ast_cir.set(beg_statement, end_statement, result);
		}
	}
	
	/* main parsing methods */
	/**
	 * Parse from the abstract syntax tree according to its production type.
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse(AstNode source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("invalid source: null");
		else if(source instanceof AstIdExpression)
			return this.parse_id_expression((AstIdExpression) source);
		else if(source instanceof AstConstant)
			return this.parse_constant((AstConstant) source);
		else if(source instanceof AstLiteral)
			return this.parse_literal((AstLiteral) source);
		else if(source instanceof AstArithUnaryExpression)
			return this.parse_arith_unary_expression((AstArithUnaryExpression) source);
		else if(source instanceof AstBitwiseUnaryExpression)
			return this.parse_bitws_unary_expression((AstBitwiseUnaryExpression) source);
		else if(source instanceof AstLogicUnaryExpression)
			return this.parse_logic_unary_expression((AstLogicUnaryExpression) source);
		else if(source instanceof AstPointUnaryExpression)
			return this.parse_point_unary_expression((AstPointUnaryExpression) source);
		else if(source instanceof AstIncreUnaryExpression)
			return this.parse_incre_unary_expression((AstIncreUnaryExpression) source);
		else if(source instanceof AstIncrePostfixExpression)
			return this.parse_incre_postf_expression((AstIncrePostfixExpression) source);
		else if(source instanceof AstArithBinaryExpression)
			return this.parse_arith_binary_expression((AstArithBinaryExpression) source);
		else if(source instanceof AstBitwiseBinaryExpression
				|| source instanceof AstShiftBinaryExpression)
			return this.parse_bitws_binary_expression((AstBinaryExpression) source);
		else if(source instanceof AstLogicBinaryExpression)
			return this.parse_logic_binary_expression((AstLogicBinaryExpression) source);
		else if(source instanceof AstRelationExpression)
			return this.parse_relation_expression((AstRelationExpression) source);
		else if(source instanceof AstAssignExpression)
			return this.parse_assign_expression((AstAssignExpression) source);
		else if(source instanceof AstArithAssignExpression)
			return this.parse_arith_assign_expression((AstArithAssignExpression) source);
		else if(source instanceof AstBitwiseAssignExpression
				|| source instanceof AstShiftAssignExpression)
			return this.parse_bitws_assign_expression((AstBinaryExpression) source);
		else if(source instanceof AstArrayExpression)
			return this.parse_array_expression((AstArrayExpression) source);
		else if(source instanceof AstCastExpression)
			return this.parse_cast_expression((AstCastExpression) source);
		else if(source instanceof AstCommaExpression)
			return this.parse_comma_expression((AstCommaExpression) source);
		else if(source instanceof AstConditionalExpression)
			return this.parse_conditional_expression((AstConditionalExpression) source);
		else if(source instanceof AstFieldExpression)
			return this.parse_field_expression((AstFieldExpression) source);
		else if(source instanceof AstFunCallExpression)
			return this.parse_func_call_expression((AstFunCallExpression) source);
		else if(source instanceof AstConstExpression)
			return this.parse_const_expression((AstConstExpression) source);
		else if(source instanceof AstParanthExpression)
			return this.parse_paranth_expression((AstParanthExpression) source);
		else if(source instanceof AstSizeofExpression)
			return this.parse_sizeof_expression((AstSizeofExpression) source);
		else if(source instanceof AstDeclaration)
			return this.parse_declaration((AstDeclaration) source);
		else if(source instanceof AstInitDeclarator)
			return this.parse_init_declarator((AstInitDeclarator) source);
		else if(source instanceof AstDeclarator)
			return this.parse_declarator((AstDeclarator) source);
		else if(source instanceof AstInitializer)
			return this.parse_initializer((AstInitializer) source);
		else if(source instanceof AstInitializerBody)
			return this.parse_initializer_body((AstInitializerBody) source);
		else if(source instanceof AstExpressionStatement)
			return this.parse_expression_statement((AstExpressionStatement) source);
		else if(source instanceof AstDeclarationStatement)
			return this.parse_declaration_statement((AstDeclarationStatement) source);
		else if(source instanceof AstCompoundStatement)
			return this.parse_compound_statement((AstCompoundStatement) source);
		else if(source instanceof AstGotoStatement)
			return this.parse_goto_statement((AstGotoStatement) source);
		else if(source instanceof AstBreakStatement)
			return this.parse_break_statement((AstBreakStatement) source);
		else if(source instanceof AstContinueStatement)
			return this.parse_continue_statement((AstContinueStatement) source);
		else if(source instanceof AstReturnStatement)
			return this.parse_return_statement((AstReturnStatement) source);
		else if(source instanceof AstLabeledStatement)
			return this.parse_labeled_statement((AstLabeledStatement) source);
		else if(source instanceof AstCaseStatement)
			return this.parse_case_statement((AstCaseStatement) source);
		else if(source instanceof AstDefaultStatement)
			return this.parse_default_statement((AstDefaultStatement) source);
		else if(source instanceof AstIfStatement)
			return this.parse_if_statement((AstIfStatement) source);
		else if(source instanceof AstSwitchStatement)
			return this.parse_switch_statement((AstSwitchStatement) source);
		else if(source instanceof AstWhileStatement)
			return this.parse_while_statement((AstWhileStatement) source);
		else if(source instanceof AstDoWhileStatement)
			return this.parse_do_while_statement((AstDoWhileStatement) source);
		else if(source instanceof AstForStatement)
			return this.parse_for_statement((AstForStatement) source);
		else if(source instanceof AstFunctionDefinition)
			return this.parse_function_definition((AstFunctionDefinition) source);
		else if(source instanceof AstTranslationUnit)
			return this.parse_transition_unit((AstTranslationUnit) source);
		else throw new IllegalArgumentException("unsupport: " + source);
	}
	
	/* expression layer */
	/**
	 * identifier [as variable]		|-- identifier with c-instance name<br>
	 * identifier [as parameter]	|-- identifier with c-parameter name<br>
	 * identifier [as enumerator]	|-- constant expression with integer<br>
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_id_expression(AstIdExpression source) throws Exception {
		/* 1. declarations */
		CName cname = source.get_cname(); CirExpression expression; 
		
		/* 2. identifier as variable */
		if(cname instanceof CInstanceName) {
			cname = this.find_base_name((CInstanceName) cname);
			expression = this.cir_tree.new_identifier(source, cname, source.get_value_type());
		}
		/* 3. identifier as parameter */
		else if(cname instanceof CParameterName) {
			expression = this.cir_tree.new_identifier(source, cname, source.get_value_type());
		}
		/* 4. identifier as enumerator */
		else if(cname instanceof CEnumeratorName) {
			CConstant constant = new CConstant();
			constant.set_int(((CEnumeratorName) cname).get_enumerator().get_value());
			expression = this.cir_tree.new_const_expression(source, constant, constant.get_type());
		}
		/* 5. invalid case produced */
		else throw new IllegalArgumentException("unsupport: " + cname.getClass().getSimpleName());
		
		/* 6. update the solution of the source and return it */
		ACPSolution solution = this.get_solution(source);
		solution.set(expression); return solution;
	}
	/**
	 * constant	|-- constant with constant-value<br>
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_constant(AstConstant source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		solution.set(this.cir_tree.new_const_expression(source, 
				source.get_constant(), source.get_value_type()));
		return solution;
	}
	/**
	 * literal	|-- string_literal with string<br>
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_literal(AstLiteral source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		solution.set(this.cir_tree.new_string_literal(source, 
				source.get_literal(), source.get_value_type()));
		return solution;
	}
	/**
	 * +E	|-- expression of E
	 * -E	|-- arith_expression as (arith_neg (expression of E))
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_arith_unary_expression(AstArithUnaryExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator();
		
		/* +E	|== {E}; return (E) */
		if(operator == COperator.positive) {
			return this.parse(source.get_operand());
		}
		/* -E	|== {E}; return (arith_neg (E)) */
		else if(operator == COperator.negative) {
			ACPSolution osolution = this.parse(source.get_operand());
			ACPSolution solution = this.get_solution(source);
			solution.append(osolution);
			
			CirArithExpression expression = this.cir_tree.
					new_arith_expression(source, operator, source.get_value_type());
			expression.add_operand(osolution.get_result()); solution.set(expression);
			
			return solution;
		}
		else throw new IllegalArgumentException("invalid operator: " + operator);
	}
	/**
	 * ~E	|-- {E}; return (bitws_rsv (E))
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_bitws_unary_expression(AstBitwiseUnaryExpression source) throws Exception {
		ACPSolution osolution = this.parse(source.get_operand());
		ACPSolution solution = this.get_solution(source);
		solution.append(osolution);
		
		CirBitwsExpression expression = this.cir_tree.
				new_bitws_expression(source, COperator.bit_not, source.get_value_type());
		expression.add_operand(osolution.get_result()); solution.set(expression);
		
		return solution;
	}
	/**
	 * !E	|-- {E}; return (logic_not (E))
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_logic_unary_expression(AstLogicUnaryExpression source) throws Exception {
		ACPSolution osolution = this.parse(source.get_operand());
		ACPSolution solution = this.get_solution(source);
		solution.append(osolution);
		
		CirLogicExpression expression = this.cir_tree.
				new_logic_expression(source, COperator.logic_not, source.get_value_type());
		expression.add_operand(osolution.get_result()); solution.set(expression);
		
		return solution;
	}
	/**
	 * &E	|== {E}; return (address (E))
	 * *E	|== {E}; return (defer (E))
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_point_unary_expression(AstPointUnaryExpression source) throws Exception {
		ACPSolution osolution = this.parse(source.get_operand());
		ACPSolution solution = this.get_solution(source);
		solution.append(osolution);
		
		COperator operator = source.get_operator().get_operator();
		if(operator == COperator.address_of) {
			CirAddressExpression expression = this.cir_tree.new_address_expression(source, source.get_value_type());
			expression.set_operand((CirReferExpression) osolution.get_result()); solution.set(expression);
		}
		else if(operator == COperator.dereference) {
			CirDeferExpression expression = this.cir_tree.new_defer_expression(source, source.get_value_type());
			expression.set_address(osolution.get_result()); solution.set(expression);
		}
		else throw new IllegalArgumentException("invalid: " + operator);
		
		return solution;
	}
	/**
	 * ++E	|== {E}; (inc_assign (E) as (arith_add (E) 1)); return (E)
	 * --E 	|== {E}; (inc_assign (E) as (arith_sub (E) 1)); return (E)
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_incre_unary_expression(AstIncreUnaryExpression source) throws Exception {
		/* 1. {E} */
		ACPSolution osolution = this.parse(source.get_operand());
		ACPSolution solution = this.get_solution(source);
		solution.append(osolution);
		
		/* 2. ��arith_add|arith_sub (E) 1�� */
		CirReferExpression lvalue = (CirReferExpression) this.cir_tree.copy(osolution.get_result());
		CirArithExpression rvalue;
		switch(source.get_operator().get_operator()) {
		case increment:	rvalue = this.cir_tree.new_arith_expression(null, COperator.arith_add, source.get_value_type()); break;
		case decrement: rvalue = this.cir_tree.new_arith_expression(null, COperator.arith_sub, source.get_value_type()); break;
		default: throw new IllegalArgumentException("invalid: " + source.get_operator().get_operator());
		}
		rvalue.add_operand(osolution.get_result()); rvalue.add_operand(this.get_integer(1));
		
		/* 3. (inc_assign (E) as ((E) +\- 1) */
		CirIncreAssignStatement statement = this.cir_tree.new_inc_assign_statement(source);
		statement.set_lvalue(lvalue); statement.set_rvalue(rvalue); solution.append(statement);
		
		/* 4. return (E) */
		solution.set((CirExpression) this.cir_tree.copy(osolution.get_result())); return solution;
	}
	/**
	 * 	E++	|==	{E}; (sav_assign $temporal as (E)); (inc_assign (E) as (arith_add (E) 1)); return $temporal
	 *  E--	|==	{E}; (sav_assign $temporal as (E)); (inc_assign (E) as (arith_sub (E) 1)); return $temporal
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_incre_postf_expression(AstIncrePostfixExpression source) throws Exception {
		/* 1. {E} */
		ACPSolution osolution = this.parse(source.get_operand());
		ACPSolution solution = this.get_solution(source);
		solution.append(osolution);
		
		/* 2. (sav_assign $temporal as (E)); */
		CirImplicator temporal = this.cir_tree.new_implicator(source, source.get_value_type());
		CirExpression save_val = osolution.get_result();
		CirSaveAssignStatement statement1 = this.cir_tree.new_save_assign_statement(source);
		statement1.set_lvalue(temporal); statement1.set_rvalue(save_val); 
		solution.append(statement1);
		
		/* 3. (arith_add|arith_sub (E) 1) */
		CirExpression loperand = (CirExpression) this.cir_tree.copy(osolution.get_result());
		CirExpression roperand = this.get_integer(1); CirArithExpression rvalue;
		switch(source.get_operator().get_operator()) {
		case increment:	rvalue = this.cir_tree.new_arith_expression(null, COperator.arith_add, source.get_value_type()); break;
		case decrement:	rvalue = this.cir_tree.new_arith_expression(null, COperator.arith_sub, source.get_value_type()); break;
		default: throw new IllegalArgumentException("invalid operator: " + source.get_operator().get_operator());
		}
		rvalue.add_operand(loperand); rvalue.add_operand(roperand); 
		
		/* 4. (inc-assign (E) as (arith_add|arith_sub (E) 1)) */
		CirReferExpression lvalue = (CirReferExpression) this.cir_tree.copy(osolution.get_result());
		CirIncreAssignStatement statement2 = this.cir_tree.new_inc_assign_statement(source);
		statement2.set_lvalue(lvalue); statement2.set_rvalue(rvalue); 
		solution.append(statement2);
		
		/* 5. return $temporal */
		solution.set(this.cir_tree.new_implicator(source, source.get_value_type())); return solution;
	}
	/**
	 * E1 + E2	|== {E1}; {E2}; return (arith_add (E1) (E2))
	 * E1 - E2	|== {E1}; {E2}; return (arith_sub (E1) (E2))
	 * E1 * E2	|== {E1}; {E2}; return (arith_mul (E1) (E2))
	 * E1 / E2	|== {E1}; {E2}; return (arith_div (E1) (E2))
	 * E1 % E2	|== {E1}; {E2}; return (arith_mod (E1) (E2))
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_arith_binary_expression(AstArithBinaryExpression source) throws Exception {
		/* 1. {E1}; {E2}; */
		ACPSolution lsolution = this.parse(source.get_loperand());
		ACPSolution rsolution = this.parse(source.get_roperand());
		ACPSolution solution = this.get_solution(source);
		solution.append(lsolution); solution.append(rsolution);
		
		/* 2. (arith_xxx (E1) (E2)) */
		COperator operator = source.get_operator().get_operator();
		CirArithExpression expression = this.cir_tree.new_arith_expression(source, operator, source.get_value_type());
		expression.add_operand(lsolution.get_result()); expression.add_operand(rsolution.get_result());
		
		/* 3. return expression */	solution.set(expression); return solution;
	}
	/**
	 * E1 & E2	|== {E1}; {E2}; return (bitws_and (E1) (E2))
	 * E1 | E2	|== {E1}; {E2}; return (bitws_ior (E1) (E2))
	 * E1 ^ E2	|== {E1}; {E2}; return (bitws_xor (E1) (E2))
	 * E1 << E2	|== {E1}; {E2}; return (bitws_lsh (E1) (E2))
	 * E1 >> E2	|== {E1}; {E2}; return (bitws_rsh (E1) (E2))
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_bitws_binary_expression(AstBinaryExpression source) throws Exception {
		/* 1. {E1}; {E2}; */
		ACPSolution lsolution = this.parse(source.get_loperand());
		ACPSolution rsolution = this.parse(source.get_roperand());
		ACPSolution solution = this.get_solution(source);
		solution.append(lsolution); solution.append(rsolution);
		
		/* 2. (bitws_xxx (E1) (E2)) */
		COperator operator = source.get_operator().get_operator();
		CirBitwsExpression expression = this.cir_tree.new_bitws_expression(source, operator, source.get_value_type());
		expression.add_operand(lsolution.get_result()); expression.add_operand(rsolution.get_result());
		
		/* 3. return expression */	solution.set(expression); return solution;
	}
	/**
	 * E1 += E2	|==	(bin_assign (E1) (arith_add (E1) (E2))); return (E1)
	 * E1 -= E2	|==	(bin_assign (E1) (arith_sub (E1) (E2))); return (E1)
	 * E1 *= E2	|==	(bin_assign (E1) (arith_mul (E1) (E2))); return (E1)
	 * E1 /= E2	|==	(bin_assign (E1) (arith_div (E1) (E2))); return (E1)
	 * E1 %= E2	|==	(bin_assign (E1) (arith_mod (E1) (E2))); return (E1)
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_arith_assign_expression(AstArithAssignExpression source) throws Exception {
		/* 1. {E1}; {E2}; */
		ACPSolution lsolution = this.parse(source.get_loperand());
		ACPSolution rsolution = this.parse(source.get_roperand());
		ACPSolution solution = this.get_solution(source);
		solution.append(lsolution); solution.append(rsolution);
		
		/* 2. (arith_xxx (E1) (E2)) */
		COperator operator = source.get_operator().get_operator();
		switch(operator) {
		case arith_add_assign:	operator = COperator.arith_add; break;
		case arith_sub_assign:	operator = COperator.arith_sub; break;
		case arith_mul_assign:	operator = COperator.arith_mul; break;
		case arith_div_assign:	operator = COperator.arith_div; break;
		case arith_mod_assign:	operator = COperator.arith_mod; break;
		default: throw new IllegalArgumentException("invalid " + operator);
		}
		CirArithExpression expression = this.cir_tree.new_arith_expression(source, operator, source.get_value_type());
		expression.add_operand(lsolution.get_result()); expression.add_operand(rsolution.get_result());
		
		/* 3. (bin_assign (E1) expression)*/
		CirReferExpression lvalue = (CirReferExpression) this.cir_tree.copy(lsolution.get_result());
		CirBinAssignStatement statement = this.cir_tree.new_bin_assign_statement(source);
		statement.set_lvalue(lvalue); statement.set_rvalue(expression); solution.append(statement);
		
		/* 4. return (E1) */
		solution.set((CirExpression) this.cir_tree.copy(lsolution.get_result())); return solution;
	}
	/**
	 * E1 &= E2	|==	(bin_assign (E1) (bitws_and (E1) (E2))); return (E1)
	 * E1 |= E2	|==	(bin_assign (E1) (bitws_ior (E1) (E2))); return (E1)
	 * E1 ^= E2	|==	(bin_assign (E1) (bitws_xor (E1) (E2))); return (E1)
	 * E1 <<= E2|==	(bin_assign (E1) (bitws_lsh (E1) (E2))); return (E1)
	 * E1 >>= E2|==	(bin_assign (E1) (bitws_rsh (E1) (E2))); return (E1)
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_bitws_assign_expression(AstBinaryExpression source) throws Exception {
		/* 1. {E1}; {E2}; */
		ACPSolution lsolution = this.parse(source.get_loperand());
		ACPSolution rsolution = this.parse(source.get_roperand());
		ACPSolution solution = this.get_solution(source);
		solution.append(lsolution); solution.append(rsolution);
		
		/* 2. (bitws_xxx (E1) (E2)) */
		COperator operator = source.get_operator().get_operator();
		switch(operator) {
		case bit_and_assign:	operator = COperator.bit_and; 		break;
		case bit_or_assign:		operator = COperator.bit_or; 		break;
		case bit_xor_assign:	operator = COperator.bit_xor; 		break;
		case left_shift_assign:	operator = COperator.left_shift; 	break;
		case righ_shift_assign:	operator = COperator.righ_shift; 	break;
		default: throw new IllegalArgumentException("invalid " + operator);
		}
		CirBitwsExpression expression = this.cir_tree.new_bitws_expression(source, operator, source.get_value_type());
		expression.add_operand(lsolution.get_result()); expression.add_operand(rsolution.get_result());
		
		/* 3. (bin_assign (E1) expression)*/
		CirReferExpression lvalue = (CirReferExpression) this.cir_tree.copy(lsolution.get_result());
		CirBinAssignStatement statement = this.cir_tree.new_bin_assign_statement(source);
		statement.set_lvalue(lvalue); statement.set_rvalue(expression); solution.append(statement);
		
		/* 4. return (E1) */
		solution.set((CirExpression) this.cir_tree.copy(lsolution.get_result())); return solution;
	}
	/**
	 * E1 = E2	|== {E1}; {E2}; (bin_assign (E1) as (E2)); return (E1)
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_assign_expression(AstAssignExpression source) throws Exception {
		/* 1. {E1}; {E2}; */
		ACPSolution lsolution = this.parse(source.get_loperand());
		ACPSolution rsolution = this.parse(source.get_roperand());
		ACPSolution solution = this.get_solution(source);
		solution.append(lsolution); solution.append(rsolution);
		
		/* 2. (bin_assign (E1) as (E2)); */
		CirBinAssignStatement statement = this.cir_tree.new_bin_assign_statement(source);
		statement.set_lvalue((CirReferExpression) lsolution.get_result()); 
		statement.set_rvalue(rsolution.get_result()); solution.append(statement);
		
		/* 3. return (E1) */	
		solution.set((CirExpression) this.cir_tree.copy(lsolution.get_result())); 
		return solution;
	}
	/**
	 * E1 > E2	|==	{E1} {E2} return (greater_tn (E1) (E2))
	 * E1 >= E2	|==	{E1} {E2} return (greater_eq (E1) (E2))
	 * E1 < E2	|==	{E1} {E2} return (smaller_tn (E1) (E2))
	 * E1 <= E2	|==	{E1} {E2} return (smaller_eq (E1) (E2))
	 * E1 == E2	|==	{E1} {E2} return (equal_with (E1) (E2))
	 * E1 != E2	|==	{E1} {E2} return (not_equals (E1) (E2))
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_relation_expression(AstRelationExpression source) throws Exception {
		/* 1. {E1}; {E2}; */
		ACPSolution lsolution = this.parse(source.get_loperand());
		ACPSolution rsolution = this.parse(source.get_roperand());
		ACPSolution solution = this.get_solution(source);
		solution.append(lsolution); solution.append(rsolution);
		
		/* 2. (xxx (E1) (E2)) */
		COperator operator = source.get_operator().get_operator();
		CirRelationExpression expression = this.cir_tree.new_relation_expression(source, operator, source.get_value_type());
		expression.add_operand(lsolution.get_result()); expression.add_operand(rsolution.get_result());
		
		/* 3. return expression */	solution.set(expression); return solution;
	}
	/**
	 * E1 && E2	|== {E1}
	 * 			|== (save_assign $temporal as (E1))
	 * 			|== (if $temporal then L1 else L2)
	 * 			|== {E2}
	 * 			|== (save_assign $temporal as (E2))
	 * 			|== (if_end_statement)
	 * 
	 * E1 || E2	|== {E1}
	 * 			|== (save_assign $temporal as (E1))
	 * 			|== (if not $temporal then L1 else L2)
	 * 			|== {E2}
	 * 			|== (save_assign $temporal as (E2))
	 * 			|== (if_end_statement)
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_logic_binary_expression(AstLogicBinaryExpression source) throws Exception {
		/* 1. {E1} */
		ACPSolution lsolution = this.parse(source.get_loperand());
		ACPSolution solution = this.get_solution(source);
		solution.append(lsolution);
		
		/* 2. (save_assign $temporal as (E1)) */
		CirImplicator temporal = this.cir_tree.new_implicator(source, source.get_value_type());
		CirExpression sav_val1 = lsolution.get_result();
		CirSaveAssignStatement statement1 = this.cir_tree.new_save_assign_statement(source);
		statement1.set_lvalue(temporal); statement1.set_rvalue(sav_val1); solution.append(statement1);
		
		/* 3. predicate as (not) temporal */
		temporal = this.cir_tree.new_implicator(source, source.get_value_type());
		CirExpression predicate; COperator operator = source.get_operator().get_operator();
		if(operator == COperator.logic_and) {
			predicate = temporal;
		}
		else if(operator == COperator.logic_or) {
			predicate = this.cir_tree.new_logic_expression(null, COperator.logic_not, CBasicTypeImpl.bool_type);
			((CirLogicExpression) predicate).add_operand(temporal);
		}
		else throw new IllegalArgumentException("unsupport " + operator);
		
		/* 4. (if predicate then L1 else L2) */
		CirIfStatement statement2 = this.cir_tree.new_if_statement(source);
		statement2.set_condition(predicate); 
		statement2.set_true_branch(this.cir_tree.new_label(null));
		statement2.set_false_branch(this.cir_tree.new_label(null));
		solution.append(statement2);
		
		/* 5. {E2} */
		ACPSolution fsolution = this.parse(source.get_roperand());
		solution.append(fsolution);
		
		/* 6. (save_assign $temporal as (E2)) */
		temporal = this.cir_tree.new_implicator(source, source.get_value_type());
		CirExpression sav_val2 = fsolution.get_result();
		CirSaveAssignStatement statement3 = this.cir_tree.new_save_assign_statement(source);
		statement3.set_lvalue(temporal); statement3.set_rvalue(sav_val2); solution.append(statement3);
		
		/* 7. if_end_statement */
		CirIfEndStatement if_end = this.cir_tree.new_if_end_statement(source);
		solution.append(if_end);
		
		/* 8. link the flows between statements */
		if(fsolution.executional()) {
			statement2.get_true_label().set_target_node_id(fsolution.get_beg_statement().get_node_id());
		}
		else {
			statement2.get_true_label().set_target_node_id(statement3.get_node_id());
		}
		statement2.get_false_label().set_target_node_id(if_end.get_node_id());
		
		/* 9. return $temporal */
		solution.set(this.cir_tree.new_implicator(source, source.get_value_type())); return solution;
	}
	/**
	 * E1 [E2]	|== {E1} {E2} return (defer (arith_add (E1) (E2))
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_array_expression(AstArrayExpression source) throws Exception {
		/* 1. {E1} {E2} */
		ACPSolution lsolution = this.parse(source.get_array_expression());
		ACPSolution rsolution = this.parse(source.get_dimension_expression());
		ACPSolution solution = this.get_solution(source);
		solution.append(lsolution); solution.append(rsolution);
		
		/* 2. (arith_add (E1) (E2)) */
		CirArithExpression address = this.cir_tree.new_arith_expression(null, 
				COperator.arith_add, lsolution.get_result().get_data_type());
		address.add_operand(lsolution.get_result());
		address.add_operand(rsolution.get_result());
		
		/* 3. (defer address) */
		CirDeferExpression reference = this.cir_tree.new_defer_expression(source, source.get_value_type());
		reference.set_address(address); solution.set(reference); return solution;
	}
	/**
	 * T E	|== {E}; return (cast (T) (E))
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_cast_expression(AstCastExpression source) throws Exception {
		/* 1. {E} */
		ACPSolution esolution = this.parse(source.get_expression());
		ACPSolution solution = this.get_solution(source);
		solution.append(esolution);
		
		/* 2. (cast (T) (E)) */
		CirType type = this.cir_tree.new_type(source.get_typename(), source.get_typename().get_type());
		CirCastExpression expression = this.cir_tree.new_cast_expression(source, source.get_value_type());
		expression.set_type(type); expression.set_operand(esolution.get_result()); solution.set(expression);
		
		return solution;
	}
	/**
	 * E1, E2, ..., En	|== {E1} {E2} ... {En}; return (En)
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_comma_expression(AstCommaExpression source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		CirExpression final_expression = null;
		
		for(int k = 0; k < source.number_of_arguments(); k++) {
			ACPSolution esolution = this.parse(source.get_expression(k));
			solution.append(esolution);
			final_expression = esolution.get_result();
		}
		
		solution.set(final_expression); return solution;
	}
	/**
	 * E1 ? E2 : E3	|==	{E1}
	 * 				|== (if (E1) then L1 else L2)
	 * 				|== [L1: {E2}; (save_assign $temporal as (E2)); (goto L3);]
	 * 				|== [L2: {E3}; (save_assign $temporal as (E3)); (goto L3);]
	 * 				|== L3: (if_end_statement)
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_conditional_expression(AstConditionalExpression source) throws Exception {
		/* 1. {E1} */
		ACPSolution csolution = this.parse(source.get_condition());
		ACPSolution solution = this.get_solution(source);
		solution.append(csolution);
		
		/* 2. (if (E1) then L1 else L2) */
		CirIfStatement if_statement = this.cir_tree.new_if_statement(source);
		if_statement.set_condition(csolution.get_result());
		if_statement.set_true_branch(this.cir_tree.new_label(null));
		if_statement.set_false_branch(this.cir_tree.new_label(null));
		solution.append(if_statement);
		
		/* 3. [L1: {E2}; (save_assign $temporal as (E2)); (goto L3);] */
		ACPSolution tsolution = this.parse(source.get_true_branch());
		solution.append(tsolution);
		CirSaveAssignStatement t_assign = this.cir_tree.new_save_assign_statement(source);
		t_assign.set_lvalue(this.cir_tree.new_implicator(source, source.get_value_type()));
		t_assign.set_rvalue(tsolution.get_result()); solution.append(t_assign);
		CirGotoStatement t_goto_end = this.cir_tree.new_goto_statement(null);
		t_goto_end.set_label(this.cir_tree.new_label(null)); solution.append(t_goto_end);
		
		/* 4. [L2: {E3}; (save_assign $temporal as (E3)); (goto L3);] */
		ACPSolution fsolution = this.parse(source.get_false_branch());
		solution.append(fsolution);
		CirSaveAssignStatement f_assign = this.cir_tree.new_save_assign_statement(source);
		f_assign.set_lvalue(this.cir_tree.new_implicator(source, source.get_value_type()));
		f_assign.set_rvalue(fsolution.get_result()); solution.append(f_assign);
		CirGotoStatement f_goto_end = this.cir_tree.new_goto_statement(null);
		f_goto_end.set_label(this.cir_tree.new_label(null)); solution.append(f_goto_end);
		
		/* 5. [L3: if_end_statement] */
		CirIfEndStatement if_end_statement = this.cir_tree.new_if_end_statement(source);
		solution.append(if_end_statement);
		
		/* 6. link the statements together */
		if(tsolution.executional()) {
			if_statement.get_true_label().set_target_node_id(tsolution.get_beg_statement().get_node_id());
		}
		else {
			if_statement.get_true_label().set_target_node_id(t_assign.get_node_id());
		}
		if(fsolution.executional()) {
			if_statement.get_false_label().set_target_node_id(fsolution.get_beg_statement().get_node_id());
		}
		else {
			if_statement.get_false_label().set_target_node_id(f_assign.get_node_id());
		}
		t_goto_end.get_label().set_target_node_id(if_end_statement.get_node_id());
		f_goto_end.get_label().set_target_node_id(if_end_statement.get_node_id());
		
		/* 7. return $temporal */
		solution.set(this.cir_tree.new_implicator(source, source.get_value_type())); return solution;
	}
	/**
	 * E.F	|==	{E}; (field (E) (F))
	 * E->F	|== {E}; (field (defer (E)) (F))
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_field_expression(AstFieldExpression source) throws Exception {
		/* 1. {E}; */
		ACPSolution bsolution = this.parse(source.get_body());
		ACPSolution solution = this.get_solution(source);
		solution.append(bsolution);
		
		/* 2. construct body */
		CirReferExpression body;
		CPunctuator operator = source.get_operator().get_punctuator();
		if(operator == CPunctuator.dot) {
			body = (CirReferExpression) bsolution.get_result();
		}
		else if(operator == CPunctuator.arrow) {
			CType data_type = source.get_body().get_value_type();
			data_type = CTypeAnalyzer.get_value_type(data_type);
			if(data_type instanceof CArrayType) {
				data_type = ((CArrayType) data_type).get_element_type();
			}
			else if(data_type instanceof CPointerType) {
				data_type = ((CPointerType) data_type).get_pointed_type();
			}
			else throw new IllegalArgumentException("invalid type: " + data_type);
			CirDeferExpression bexpr = this.cir_tree.new_defer_expression(null, data_type);
			bexpr.set_address(bsolution.get_result()); body = bexpr;
		}
		else throw new IllegalArgumentException("invalid operator: " + operator);
		
		/* 3. (field body (F)) */
		CirField field = this.cir_tree.new_field(source.get_field(), source.get_field().get_name());
		CirFieldExpression expression = this.cir_tree.new_field_expression(source, source.get_value_type());
		expression.set_body(body); expression.set_field(field); solution.set(expression); return solution;
	}
	/**
	 * F(A1, A2, ..., An)	|== {F}; {A1}; {A2}; {An};
	 * 						|== (call (F) ((A1), (A2), ..., (An))
	 * 						|== (wait_assign $temporal as (wait (F)))
	 * @param source
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private ACPSolution parse_fun_call_expression(AstFunCallExpression source) throws Exception {
		/* 1. {F} */
		ACPSolution solution = this.get_solution(source);
		ACPSolution fsolution = this.parse(source.get_function());
		solution.append(fsolution);
		
		/* 2. {A1}; ... {An}; (A1, A2, ..., An) */
		CirArgumentList arguments;
		if(source.has_argument_list()) {
			AstArgumentList list = source.get_argument_list();
			arguments = this.cir_tree.new_argument_list(list);
			for(int k = 0; k < list.number_of_arguments(); k++) {
				ACPSolution asolution = this.parse(list.get_argument(k));
				solution.append(asolution); 
				arguments.add_argument(asolution.get_result());
			}
		}
		else {
			arguments = this.cir_tree.new_argument_list(null);
		}
		
		/* 3. (F) */
		CirExpression function = fsolution.get_result();
		CType ftype = source.get_function().get_value_type();
		ftype = CTypeAnalyzer.get_value_type(ftype);
		if(!(ftype instanceof CFunctionType)) {
			if(ftype instanceof CArrayType) {
				ftype = ((CArrayType) ftype).get_element_type();
			}
			else if(ftype instanceof CPointerType) {
				ftype = ((CPointerType) ftype).get_pointed_type();
			}
			else throw new IllegalArgumentException("invalid type: " + ftype);
			CFunctionType fun_type = (CFunctionType) ftype;
			
			CirDeferExpression fexpr = this.cir_tree.new_defer_expression(null, fun_type);
			fexpr.set_address(function); function = fexpr;
		}
		
		/* 4. (call (F) ((A1), (A2), ..., (An)) */
		CirCallStatement call_statement = this.cir_tree.new_call_statement(source);
		call_statement.set_function(function); call_statement.set_arguments(arguments);
		solution.append(call_statement); 
		
		/* 5. (wait_assign $temporal as (wait (F))) */
		function = (CirExpression) this.cir_tree.copy(function);
		CirWaitExpression rvalue = this.cir_tree.new_wait_expression(source, source.get_value_type());
		rvalue.set_function(function); 
		CirImplicator lvalue = this.cir_tree.new_implicator(source, source.get_value_type());
		CirWaitAssignStatement wait_statement = this.cir_tree.new_wait_assign_statement(source);
		wait_statement.set_lvalue(lvalue); wait_statement.set_rvalue(rvalue); solution.append(wait_statement);
		
		/* 6. return $temporal */
		solution.set(this.cir_tree.new_implicator(source, source.get_value_type())); return solution;
	}
	/**
	 * F(A1, A2, ..., An)	|== {F}; {A1}; {A2}; {An};
	 * 						|==	P1 = A1; P2 = A2; ... Pn = An;
	 * 						|== (call (F) ((A1), (A2), ..., (An))
	 * 						|== (wait_assign $temporal as (wait (F)))
	 * @param source
	 * @return this is a more secure way to parse Cir-Code
	 * @throws Exception
	 */
	private ACPSolution parse_func_call_expression(AstFunCallExpression source) throws Exception {
		/* 1. {F} */
		ACPSolution solution = this.get_solution(source);
		ACPSolution fsolution = this.parse(source.get_function());
		solution.append(fsolution);
		
		/* 2. {A1}; ... {An}; (A1, A2, ..., An) */
		CirArgumentList arguments;
		List<CirExpression> parameters = new ArrayList<CirExpression>();
		if(source.has_argument_list()) {
			AstArgumentList list = source.get_argument_list();
			arguments = this.cir_tree.new_argument_list(list);
			for(int k = 0; k < list.number_of_arguments(); k++) {
				ACPSolution asolution = this.parse(list.get_argument(k));
				solution.append(asolution);
				
				/* param_k := argument_k; */
				CirImplicator parameter = this.cir_tree.new_implicator(list.
						get_argument(k), list.get_argument(k).get_value_type());
				CirSaveAssignStatement param_assign = this.
						cir_tree.new_save_assign_statement(list.get_argument(k));
				param_assign.set_lvalue(parameter); 
				param_assign.set_rvalue(asolution.get_result());
				solution.append(param_assign);
				
				/* save the copy of the parameter as for usage in argument-list */
				parameters.add((CirExpression) this.cir_tree.copy(parameter));
			}
		}
		else {
			arguments = this.cir_tree.new_argument_list(null);
		}
		
		/* register the kth argument in the list. */
		for(CirExpression parameter : parameters) arguments.add_argument(parameter);
		
		/* 3. (F) */
		CirExpression function = fsolution.get_result();
		CType ftype = source.get_function().get_value_type();
		ftype = CTypeAnalyzer.get_value_type(ftype);
		if(!(ftype instanceof CFunctionType)) {
			if(ftype instanceof CArrayType) {
				ftype = ((CArrayType) ftype).get_element_type();
			}
			else if(ftype instanceof CPointerType) {
				ftype = ((CPointerType) ftype).get_pointed_type();
			}
			else throw new IllegalArgumentException("invalid type: " + ftype);
			CFunctionType fun_type = (CFunctionType) ftype;
			
			CirDeferExpression fexpr = this.cir_tree.new_defer_expression(null, fun_type);
			fexpr.set_address(function); function = fexpr;
		}
		
		/* 4. (call (F) ((A1), (A2), ..., (An)) */
		CirCallStatement call_statement = this.cir_tree.new_call_statement(source);
		call_statement.set_function(function); call_statement.set_arguments(arguments);
		solution.append(call_statement); 
		
		/* 5. (wait_assign $temporal as (wait (F))) */
		function = (CirExpression) this.cir_tree.copy(function);
		CirWaitExpression rvalue = this.cir_tree.new_wait_expression(source, source.get_value_type());
		rvalue.set_function(function); 
		CirImplicator lvalue = this.cir_tree.new_implicator(source, source.get_value_type());
		CirWaitAssignStatement wait_statement = this.cir_tree.new_wait_assign_statement(source);
		wait_statement.set_lvalue(lvalue); wait_statement.set_rvalue(rvalue); solution.append(wait_statement);
		
		/* 6. return $temporal */
		solution.set(this.cir_tree.new_implicator(source, source.get_value_type())); return solution;
	}
	/**
	 * 
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_sizeof_expression(AstSizeofExpression source) throws Exception {
		CType data_type;
		if(source.is_expression()) {
			data_type = source.get_expression().get_value_type();
		}
		else {
			data_type = source.get_typename().get_type();
		}
		data_type = CTypeAnalyzer.get_value_type(data_type);
		
		int size = this.template.sizeof(data_type);
		CConstant constant = new CConstant();
		constant.set_int(size);
		
		CirExpression expression = cir_tree.new_const_expression(source, constant, source.get_value_type());
		ACPSolution solution = this.get_solution(source); solution.set(expression); return solution;
	}
	private ACPSolution parse_paranth_expression(AstParanthExpression source) throws Exception {
		return this.parse(source.get_sub_expression());
	}
	private ACPSolution parse_const_expression(AstConstExpression source) throws Exception {
		return this.parse(source.get_expression());
	}
	/* declaration layer */
	/**
	 * D1, D2, ..., Dn	|== {D1}; {D2}; ... {Dn};
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_declaration(AstDeclaration source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		if(source.has_declarator_list()) {
			AstInitDeclaratorList list = source.get_declarator_list();
			for(int k = 0; k < list.number_of_init_declarators(); k++) {
				ACPSolution isolution = this.parse(list.get_init_declarator(k));
				solution.append(isolution);
			}
		}
		return solution;
	}
	/**
	 * D		|==	(init_assign (D) as (default_val))
	 * D = E	|== {E}; (init_assign (D) as (E))
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_init_declarator(AstInitDeclarator source) throws Exception {
		/* 1. (D) */
		ACPSolution solution = this.get_solution(source);
		ACPSolution dsolution = this.parse(source.get_declarator());
		CirReferExpression lvalue = (CirReferExpression) dsolution.get_result();
		
		// when the declarator is a variable declarator
		if(lvalue != null) {
			/* 2. (E) */
			CirExpression rvalue;
			if(source.has_initializer()) {
				ACPSolution esolution = this.parse(source.get_initializer());
				solution.append(esolution); rvalue = esolution.get_result();
			}
			else {
				rvalue = this.cir_tree.new_default_value(lvalue.get_data_type());
			}
			
			/* 3. (init_assign (D) as (E)) */
			CirInitAssignStatement statement = this.cir_tree.new_init_assign_statement(source);
			statement.set_lvalue(lvalue); statement.set_rvalue(rvalue); solution.append(statement);
		}
		
		return solution;
	}
	/**
	 * declarator
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_declarator(AstDeclarator source) throws Exception {
		/* 1. get the name in the declarator */
		while(source.get_production() != DeclaratorProduction.identifier) {
			source = source.get_declarator();
		}
		AstName identifier = source.get_identifier();
		
		/* 2. determine whether to return solution */
		ACPSolution solution = this.get_solution(source);
		CName cname = identifier.get_cname();
		if(cname instanceof CInstanceName) {
			cname = this.find_base_name((CInstanceName) cname);
			solution.set(this.cir_tree.new_declarator(source, cname, 
					((CInstanceName) cname).get_instance().get_type()));
		}
		else if(cname instanceof CParameterName) {
			solution.set(this.cir_tree.new_declarator(source, cname, 
					((CParameterName) cname).get_parameter().get_type()));
		}
		
		/* 3. return solution */	return solution;
	}
	private ACPSolution parse_initializer(AstInitializer source) throws Exception {
		if(source.is_body())
			return this.parse(source.get_body());
		else return this.parse(source.get_expression());
	}
	private ACPSolution parse_initializer_body(AstInitializerBody source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		CirInitializerBody expression = this.cir_tree.new_initializer_body(source);
		
		AstInitializerList list = source.get_initializer_list();
		for(int k = 0;  k < list.number_of_initializer(); k++) {
			AstFieldInitializer field_initializer = list.get_initializer(k);
			ACPSolution esolution = this.parse(field_initializer.get_initializer());
			solution.append(esolution); expression.add_element(esolution.get_result());
		}
		
		solution.set(expression); return solution;
	}
	/* statement layer */
	/**
	 * ;		|==	{}
	 * E;		|== {E}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public ACPSolution parse_expression_statement(AstExpressionStatement source) throws Exception {
		if(source.has_expression()) {
			ACPSolution solution = this.get_solution(source);
			ACPSolution esolution = parse(source.get_expression());
			solution.append(esolution);
			if(esolution.computational())
				solution.set(esolution.get_result());
			return solution;
		}
		else {
			return this.get_solution(source);
		}
	}
	/**
	 * D ;	|== {D}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_declaration_statement(AstDeclarationStatement source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		solution.append(parse(source.get_declaration()));
		return solution;
	}
	/**
	 * {S1; S2; ... Sn}	|==	{S1}; {S2}; ... {Sn};
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_compound_statement(AstCompoundStatement source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		if(source.has_statement_list()) {
			AstStatementList list = source.get_statement_list();
			for(int k = 0; k < list.number_of_statements(); k++) {
				ACPSolution ssolution = this.parse(list.get_statement(k));
				solution.append(ssolution);
			}
		}
		return solution;
	}
	/**
	 * break ;	|== (goto L)	@require {break}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_break_statement(AstBreakStatement source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		
		CirGotoStatement statement = this.cir_tree.new_goto_statement(source);
		statement.set_label(this.cir_tree.new_label(null)); solution.append(statement);
		
		ACPScope scope = this.cur_module.get_top_scope();
		ACPLabelsTarget label_target = scope.get_labels_target(ACPScope.BREAK_LABEL);
		label_target.add_label(statement.get_label());
		
		return solution;
	}
	/**
	 * continue ;	|== (goto L)	@require {break}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_continue_statement(AstContinueStatement source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		
		CirGotoStatement statement = this.cir_tree.new_goto_statement(source);
		statement.set_label(this.cir_tree.new_label(null)); solution.append(statement);
		
		ACPScope scope = this.cur_module.get_top_scope();
		ACPLabelsTarget label_target = scope.get_labels_target(ACPScope.CONTINUE_LABEL);
		label_target.add_label(statement.get_label());
		
		return solution;
	}
	/**
	 * goto L	|== (goto (L))	@require {L}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_goto_statement(AstGotoStatement source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		
		CirGotoStatement statement = this.cir_tree.new_goto_statement(source);
		statement.set_label(this.cir_tree.new_label(source.get_label()));
		solution.append(statement);
		
		String label_name = source.get_label().get_name();
		ACPScope scope = this.cur_module.get_root_scope();
		ACPLabelsTarget label_target = scope.new_labels_target(label_name);
		label_target.add_label(statement.get_label());
		
		return solution;
	}
	/**
	 * return		|==	(goto {return})		@require	{return}
	 * return E		|== {E}
	 * 				|== (return_assign (returnPoint) (E))
	 * 				|== (goto {return})		@require	{return}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_return_statement(AstReturnStatement source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		
		if(source.has_expression()) {
			ACPSolution esolution = this.parse(source.get_expression());
			solution.append(esolution);
			
			CirReturnPoint lvalue = this.cir_tree.new_return_point(
					source.get_return(), source.get_expression().get_value_type());
			CirExpression rvalue = esolution.get_result();
			CirReturnAssignStatement statement = this.cir_tree.new_return_assign_statement(source);
			statement.set_lvalue(lvalue); statement.set_rvalue(rvalue); solution.append(statement);
		}
		
		CirGotoStatement statement = this.cir_tree.new_goto_statement(source);
		statement.set_label(this.cir_tree.new_label(null)); solution.append(statement);
		
		ACPScope scope = this.cur_module.get_root_scope();
		ACPLabelsTarget label_target = scope.get_labels_target(ACPScope.RETURN_LABEL);
		label_target.add_label(statement.get_label());
		
		return solution;
	}
	/**
	 * L :	|==	(label_statement)		@solve {L}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_labeled_statement(AstLabeledStatement source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		
		CirLabelStatement statement = this.cir_tree.new_label_statement(source);
		solution.append(statement);
		
		String label_name = source.get_label().get_name();
		ACPScope scope = this.cur_module.get_root_scope();
		ACPLabelsTarget label_target = scope.new_labels_target(label_name);
		label_target.set_target(statement);
		
		return solution;
	}
	/**
	 * case E	|==	{E}											@solve		{case}
	 * 			|== (case $temporal as (E) then next or L)		@require	{case}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_case_statement(AstCaseStatement source) throws Exception {
		ACPScope scope = this.cur_module.get_top_scope();
		AstSwitchStatement ast_key = (AstSwitchStatement) scope.get_ast_key();
		
		/* 1. {E} */
		ACPSolution esolution = this.parse(source.get_expression());
		ACPSolution solution = this.get_solution(source);
		solution.append(esolution);
		
		/* 2. (equal_with $temporal (E)) */
		CirImplicator lvalue = this.cir_tree.new_implicator(ast_key, ast_key.get_condition().get_value_type());
		CirExpression rvalue = esolution.get_result();
		CirRelationExpression predicate = this.cir_tree.new_relation_expression(source, COperator.equal_with, CBasicTypeImpl.bool_type);
		predicate.add_operand(lvalue); predicate.add_operand(rvalue);
		
		/* 3. (case predicate then next else L) */
		CirCaseStatement statement = this.cir_tree.new_case_statement(source);
		statement.set_condition(predicate); 
		statement.set_false_branch(this.cir_tree.new_label(null));
		solution.append(statement);
		
		/* 4. @solve {case} */
		ACPLabelsTarget label_target = scope.get_labels_target(ACPScope.CASE_LABEL);
		label_target.set_target(solution.get_beg_statement());
		
		/* 5. @require {case} */
		label_target.init();
		label_target.add_label(statement.get_false_label());
		
		return solution;
	}
	/**
	 * default :	|==	(default_statement)		@solve	{case}
	 * 											@delete	{case}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_default_statement(AstDefaultStatement source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		
		/* (default_statement) */
		CirDefaultStatement statement = this.cir_tree.new_default_statement(source);
		solution.append(statement);
		
		/* @solve {case} */
		ACPScope scope = this.cur_module.get_top_scope();
		ACPLabelsTarget label_target = scope.get_labels_target(ACPScope.CASE_LABEL);
		label_target.set_target(statement); 
		
		/* delete {case} */
		scope.del_labels_target(ACPScope.CASE_LABEL);
		
		return solution;
	}
	/**
	 * if E S			|==	{E}
	 * 					|== (if (E) then L1 else L2)	@require 	{true|false}
	 * 					|== {S}							@solve		{true}
	 * 					|== (if_end_statement)			@solve		{false}
	 * 
	 * if E S1 else S2	|==	{E}
	 * 					|== (if (E) then L1 else L2)	@require	{true|false}
	 * 					|== [{S1}; (goto L3);]			@solve		{true}
	 * 													@require	{if}
	 * 					|== [{S2}; (goto L3);]			@solve		{false}
	 * 													@require	{if}
	 * 					|== (if_end_statement)			@solve		{if}						
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_if_statement(AstIfStatement source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		
		/* 1. {E} */
		ACPSolution csolution = this.parse(source.get_condition());
		solution.append(csolution);
		
		/* 2. (if (E) then L1 else L2) */
		CirIfStatement if_statement = this.cir_tree.new_if_statement(source);
		if_statement.set_condition(csolution.get_result());
		if_statement.set_true_branch(this.cir_tree.new_label(null));
		if_statement.set_false_branch(this.cir_tree.new_label(null));
		solution.append(if_statement);
		
		/* 3. [{S1}; (goto L3);] */
		ACPSolution tsolution = this.parse(source.get_true_branch());
		CirGotoStatement t_goto_end = this.cir_tree.new_goto_statement(source);
		t_goto_end.set_label(this.cir_tree.new_label(null));
		tsolution.append(t_goto_end); solution.append(tsolution);
		
		/* 4. [{S2}; (goto L3);] */
		ACPSolution fsolution = null;
		if(source.has_else()) {
			fsolution = this.parse(source.get_false_branch());
			CirGotoStatement f_goto_end = this.cir_tree.new_goto_statement(source);
			f_goto_end.set_label(this.cir_tree.new_label(null));
			fsolution.append(f_goto_end); solution.append(fsolution);
		}
		
		/* 5. (if_end_statement) */
		CirIfEndStatement if_end = this.cir_tree.new_if_end_statement(source);
		solution.append(if_end);
		
		/* 6. link the statements */
		if_statement.get_true_label().set_target_node_id(tsolution.get_beg_statement().get_node_id());
		t_goto_end.get_label().set_target_node_id(if_end.get_node_id());
		if(fsolution == null) {
			if_statement.get_false_label().set_target_node_id(if_end.get_node_id());
		}
		else {
			if_statement.get_false_label().set_target_node_id(fsolution.get_beg_statement().get_node_id());
			CirGotoStatement f_goto_end = (CirGotoStatement) fsolution.get_end_statement();
			f_goto_end.get_label().set_target_node_id(if_end.get_node_id());
		}
		
		return solution;
	}
	/**
	 * switch E S	|==	{E}
	 * 				|== (save_assign $temporal as (E))
	 * 				|== (goto {case})					@require	{case}
	 * 													@require	{break}
	 * 				|== {S}								@solve		{case}
	 * 													@require	{break}
	 * 				|== (case_end_statement)			@solve		{break}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_switch_statement(AstSwitchStatement source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		
		/* 1. {E} */
		ACPSolution csolution = this.parse(source.get_condition());
		solution.append(csolution);
		
		/* 2. (save_assign $temporal as (E)) */
		CirImplicator lvalue = this.cir_tree.new_implicator(source, source.get_condition().get_value_type());
		CirExpression rvalue = csolution.get_result();
		CirSaveAssignStatement statement = this.cir_tree.new_save_assign_statement(source);
		statement.set_lvalue(lvalue); statement.set_rvalue(rvalue); solution.append(statement);
		
		/* 3. (goto {case}) */
		CirGotoStatement goto_case = this.cir_tree.new_goto_statement(source);
		goto_case.set_label(this.cir_tree.new_label(null)); solution.append(goto_case);
		
		/* 4. @require {case|break} */
		ACPScope scope = this.cur_module.push_scope(source);
		ACPLabelsTarget break_target = scope.new_labels_target(ACPScope.BREAK_LABEL);
		ACPLabelsTarget case_label_target = scope.new_labels_target(ACPScope.CASE_LABEL);
		case_label_target.add_label(goto_case.get_label());
		
		/* 5. {S} @solve {break} */
		ACPSolution bsolution = this.parse(source.get_body());
		solution.append(bsolution);
		
		/* 6. (case_end_statement)		@solve	{case} */
		CirCaseEndStatement case_end = this.cir_tree.new_case_end_statement(source);
		solution.append(case_end); break_target.set_target(case_end);
		
		/* 7. binding the last case without default... */
		if(!case_label_target.has_target()) {
			case_label_target.set_target(case_end);
		}
		
		this.cur_module.pop_scope(); return solution;
	}
	/**
	 * while E S	|== {E}								@solve		{continue}
	 * 				|== (if (E) then L1 else L2)		@require 	{true|false|break}
	 * 				|== [L1: {S}; (goto L3)]			@require	{break|continue}
	 * 				|== (if_end_statement)				@solve		{break}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_while_statement(AstWhileStatement source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		ACPScope scope = this.cur_module.push_scope(source);
		
		/* 1. {E} */
		ACPSolution csolution = this.parse(source.get_condition());
		solution.append(csolution);
		
		/* 2. (if (E) then L1 else L2) */
		CirIfStatement if_statement = this.cir_tree.new_if_statement(source);
		if_statement.set_condition(csolution.get_result());
		if_statement.set_true_branch(this.cir_tree.new_label(null));
		if_statement.set_false_branch(this.cir_tree.new_label(null));
		solution.append(if_statement);
		
		/* 3. @solve {continue} @require {break} */
		scope.new_labels_target(ACPScope.CONTINUE_LABEL).set_target(solution.get_beg_statement());
		scope.new_labels_target(ACPScope.BREAK_LABEL).add_label(if_statement.get_false_label());
		
		/* 4. [L1: {S}; (goto L3)] 	@solve {true} */
		ACPSolution bsolution = this.parse(source.get_body());
		CirGotoStatement goto_end = this.cir_tree.new_goto_statement(source);
		goto_end.set_label(this.cir_tree.new_label(null));
		bsolution.append(goto_end); solution.append(bsolution);
		scope.get_labels_target(ACPScope.CONTINUE_LABEL).add_label(goto_end.get_label());
		if_statement.get_true_label().set_target_node_id(bsolution.get_beg_statement().get_node_id());
		
		/* 5. (if_end_statement)	@solve {break} */
		CirIfEndStatement if_end = this.cir_tree.new_if_end_statement(source);
		solution.append(if_end);
		scope.get_labels_target(ACPScope.BREAK_LABEL).set_target(if_end);
		
		this.cur_module.pop_scope(); return solution;
	}
	/**
	 * do S while E	|==	{S}							@require	{break|continue}
	 * 				|== {E}							@solve		{continue}
	 * 				|== (if (E) then L1 else L2)	@require	{true|false|break}
	 * 				|== (if_end_statement)			@solve		{false|break}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_do_while_statement(AstDoWhileStatement source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		ACPScope scope = this.cur_module.push_scope(source);
		
		/* 0. @define {break|continue} */
		scope.new_labels_target(ACPScope.BREAK_LABEL);
		scope.new_labels_target(ACPScope.CONTINUE_LABEL);
		
		/* 1. {S} */
		ACPSolution bsolution = this.parse(source.get_body());
		solution.append(bsolution);
		
		/* 2. {E} */
		ACPSolution csolution = this.parse(source.get_condition());
		solution.append(csolution);
		
		/* 3. (if (E) then L1 else L2) 	*/
		CirIfStatement if_statement = this.cir_tree.new_if_statement(source);
		if_statement.set_condition(csolution.get_result());
		if_statement.set_true_branch(this.cir_tree.new_label(null));
		if_statement.set_false_branch(this.cir_tree.new_label(null));
		solution.append(if_statement);
		
		/* 4. @solve {true|continue}	@require {break} */
		if(bsolution.executional()) {
			if_statement.get_true_label().set_target_node_id(bsolution.get_beg_statement().get_node_id());
		}
		else if(csolution.executional()) {
			if_statement.get_true_label().set_target_node_id(csolution.get_beg_statement().get_node_id());
		}
		else {
			if_statement.get_true_label().set_target_node_id(if_statement.get_node_id());
		}
		scope.get_labels_target(ACPScope.BREAK_LABEL).add_label(if_statement.get_false_label());
		if(csolution.executional()) {
			scope.get_labels_target(ACPScope.CONTINUE_LABEL).set_target(csolution.get_beg_statement());
		}
		else {
			scope.get_labels_target(ACPScope.CONTINUE_LABEL).set_target(if_statement);
		}
		
		/* 5. (if_end_statement)	@solve {break} */
		CirIfEndStatement if_end = this.cir_tree.new_if_end_statement(source);
		solution.append(if_end);
		scope.get_labels_target(ACPScope.BREAK_LABEL).set_target(if_end);
		
		this.cur_module.pop_scope(); return solution;
	}
	/**
	 * for(S1 S2 E3) B	|==	{S1}
	 * 					|==	[{S2}; (if (S2) then L1 else L2);]		@require 	{true|false}
	 * 																@define	 	{break|continue}
	 * 					|==	[{B}; {E3}; (goto L3);]					@solve		{continue}
	 * 																@require	{break}
	 * 					|== (if_end_statement)						@solve		{break}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_for_statement(AstForStatement source) throws Exception {
		ACPSolution solution = this.get_solution(source);
		
		/* 1. {S1} */
		ACPSolution isolution = this.parse(source.get_initializer());
		solution.append(isolution);
		ACPScope scope = this.cur_module.push_scope(source);				// push scope
		
		/* 2. [{S2}; (if (S2) then L1 else L2);] */
		ACPSolution csolution = this.parse(source.get_condition());
		solution.append(csolution);
		CirIfStatement if_statement = this.cir_tree.new_if_statement(source);
		CirExpression predicate = csolution.get_result();
		if(predicate == null) {
			CConstant constant = new CConstant();
			constant.set_bool(true);
			predicate = this.cir_tree.new_const_expression(source.get_condition(), constant, constant.get_type());
		}
		if_statement.set_condition(predicate);
		if_statement.set_true_branch(this.cir_tree.new_label(null));
		if_statement.set_false_branch(this.cir_tree.new_label(null));
		solution.append(if_statement);
		
		/* 3. @define	{break|continue} */
		ACPLabelsTarget break_target = scope.new_labels_target(ACPScope.BREAK_LABEL);
		ACPLabelsTarget continue_target = scope.new_labels_target(ACPScope.CONTINUE_LABEL);
		
		/* 4. [{B}; {E3}; (goto L3);] */
		ACPSolution bsolution = this.parse(source.get_body());
		solution.append(bsolution);
		ACPSolution tsolution = null;
		if(source.has_increment()) {
			tsolution = this.parse(source.get_increment());
			solution.append(tsolution);
		}
		CirGotoStatement goto_begin = this.cir_tree.new_goto_statement(source);
		goto_begin.set_label(this.cir_tree.new_label(null));
		solution.append(goto_begin);
		
		/* 5. (if_end_statement)	@solve {break|continue} */
		CirIfEndStatement if_end = this.cir_tree.new_if_end_statement(source);
		solution.append(if_end);
		break_target.set_target(if_end);
		if(tsolution != null && tsolution.executional()) {
			continue_target.set_target(tsolution.get_beg_statement());
		}
		else {
			continue_target.set_target(goto_begin);
		}
		
		/* 6. link the statement */
		if(bsolution.executional()) {
			if_statement.get_true_label().set_target_node_id(bsolution.get_beg_statement().get_node_id());
		}
		else if(tsolution != null && tsolution.executional()) {
			if_statement.get_true_label().set_target_node_id(tsolution.get_beg_statement().get_node_id());
		}
		else {
			if_statement.get_true_label().set_target_node_id(goto_begin.get_node_id());
		}
		break_target.add_label(if_statement.get_false_label());
		if(csolution.executional()) {
			goto_begin.get_label().set_target_node_id(csolution.get_beg_statement().get_node_id());
		}
		else {
			goto_begin.get_label().set_target_node_id(if_statement.get_node_id());
		}
		
		this.cur_module.pop_scope(); return solution;						// pops scope
	}
	/**
	 * build function_definition
	 * return function_definition.declarator
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_function_definition(AstFunctionDefinition source) throws Exception {
		this.cur_module = this.data.get_parsing_module(source);
		ACPScope scope = this.cur_module.get_root_scope();
		scope.new_labels_target(ACPScope.RETURN_LABEL);
		
		/* 0. function definition */
		ACPSolution solution = this.get_solution(source);
		CirFunctionDefinition fun_def = this.cir_tree.new_function_definition(source);
		
		/* 1. declarator */
		ACPSolution dsolution = this.parse(source.get_declarator());
		CirDeclarator declarator = (CirDeclarator) dsolution.get_result();
		fun_def.set_declarator(declarator);
		fun_def.set_body(this.cir_tree.new_function_body(source.get_body()));
		fun_def.get_body().add_statement(this.cir_tree.new_beg_statement(null));
		
		/* 2. parameters */
		if(source.has_declaration_list()) {
			AstDeclarationList dlist = source.get_declaration_list();
			for(int k = 0; k < dlist.number_of_declarations(); k++) {
				ACPSolution psolution = this.parse(dlist.get_declaration(k));
				solution.append(psolution);
			}
		}
		else {
			AstDeclarator decl = source.get_declarator();
			while(decl.get_production() != DeclaratorProduction.declarator_parambody) {
				decl = decl.get_declarator();
			}
			AstParameterBody pbody = decl.get_parameter_body();
			
			if(pbody.has_parameter_type_list()) {
				AstParameterList plist = pbody.get_parameter_type_list().get_parameter_list();
				for(int k = 0; k < plist.number_of_parameters(); k++) {
					AstParameterDeclaration pdecl = plist.get_parameter(k);
					if(pdecl.has_declarator()) {
						ACPSolution psolution = this.parse(pdecl.get_declarator());
						CirDeclarator lvalue = (CirDeclarator) psolution.get_result();
						CirExpression rvalue = this.cir_tree.new_default_value(lvalue.get_data_type());
						CirInitAssignStatement pstatement = this.cir_tree.new_init_assign_statement(pdecl);
						pstatement.set_lvalue(lvalue); pstatement.set_rvalue(rvalue); solution.append(pstatement);
					}
				}
			}
		}
		
		/* 3. body */ 
		ACPSolution bsolution = this.parse(source.get_body());
		solution.append(bsolution);
		
		/* 4. construct function body */
		Iterable<CirStatement> statements = this.cur_module.get_statements();
		for(CirStatement statement : statements) fun_def.get_body().add_statement(statement);
		CirEndStatement end = this.cir_tree.new_end_statement(null);
		fun_def.get_body().add_statement(end);
		scope.get_labels_target(ACPScope.RETURN_LABEL).set_target(end);
		
		/* 5. update the code index range according to solutions in module */
		this.build_cir_range(cur_module);
		
		this.cur_module.pop_scope(); this.cur_module = null; 
		solution.set(declarator); return solution;
	}
	/**
	 * #init function
	 * return function_definition.declarator
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private ACPSolution parse_transition_unit(AstTranslationUnit source) throws Exception {
		/* #init_function */
		this.cur_module = this.data.get_parsing_module(source);
		ACPSolution solution = this.get_solution(source);
		
		CirFunctionDefinition fun_def = this.cir_tree.new_function_definition(source);
		fun_def.set_declarator(this.cir_tree.new_implicator(
				source, this.void_function_type(), InitFunctionName));
		fun_def.set_body(this.cir_tree.new_function_body(null));
		
		fun_def.get_body().add_statement(this.cir_tree.new_beg_statement(null));
		for(int k = 0; k < source.number_of_units(); k++) {
			AstExternalUnit unit = source.get_unit(k);
			if(unit instanceof AstDeclarationStatement) {
				ACPSolution dsolution = this.parse(unit);
				solution.append(dsolution);
			}
		}
		Iterable<CirStatement> statements = this.cur_module.get_statements();
		for(CirStatement statement : statements) fun_def.get_body().add_statement(statement);
		fun_def.get_body().add_statement(this.cir_tree.new_end_statement(null));
		
		this.build_cir_range(cur_module);
		this.cur_module.pop_scope(); this.cur_module = null; 
		solution.set(fun_def.get_declarator());
		
		/* build up transition unit */
		CirTransitionUnit root = this.cir_tree.get_root();
		root.add_unit(fun_def);
		for(int k = 0; k < source.number_of_units(); k++) {
			AstExternalUnit unit = source.get_unit(k);
			if(unit instanceof AstFunctionDefinition) {
				ACPSolution fsolution = this.parse(unit);
				CirFunctionDefinition func = (CirFunctionDefinition) fsolution.get_result().get_parent();
				root.add_unit(func);
			}
		}
		
		return solution;
	}
	private CFunctionType void_function_type() throws Exception {
		return this.type_factory.get_fixed_function_type(CBasicTypeImpl.void_type);
	}
	
	/* public methods */
	/**
	 * parse from the abstract syntax tree to the C-like intermediate representation together
	 * with the program flow graph.
	 * @param ast_root
	 * @return
	 * @throws Exception
	 */
	public static CirTree parse_all(AstTranslationUnit ast_root,
			CRunTemplate template) throws Exception {
		CirParser parser = new CirParser(ast_root, template);		// create CIR-tree empty
		parser.parse(ast_root); 						// parsing AST to construct CIR tree
		parser.cir_tree.gen_function_call_graph();		// build up the flow graph for CIR
		return parser.cir_tree;
	}
	
}
