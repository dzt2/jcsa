package com.jcsa.jcparse.lang.program.types;

/**
 * 	The type of AstCirNode based on syntactic type.
 * 	
 * 	@author yukimula
 *
 */
public enum AstCirNodeType {
	
	/** translation unit as the root of AST **/		tra_unit,
	/** function definition **/						func_def,
	
	/** AstIdExpression|AstName|return **/			name_expr,
	/** AstConstant|AstSizeofExpression **/			cons_expr,
	/** AstStringLiteral **/						strg_expr,
	/** AstArray|Field|PointUnaryExpression **/		refr_expr,
	/** AstInitDeclarator **/						decl_expr,
	/** Ast(Arith|Bitws|Shift)AssignExpression **/	assg_expr,
	/** AstIncre(Unary|Postfix)Expression **/		incr_expr,
	/** Ast(Arith|Bitwise|Shift|Logic|Relation) **/	biny_expr,
	/** AstUnaryExpression **/						unry_expr,
	/** AstCastExpression **/						cast_expr,
	/** AstConditionalExpression **/				cond_expr,
	/** AstFunCallExpression **/					call_expr,
	/** AstCommaExpression **/						coma_expr,
	/** AstInitializerBody **/						init_body,
	
	/** AstExpression|DeclarationStatement **/		expr_stmt,
	/** AstCompoundStatement **/					comp_stmt,
	/** AstBreak|Continue|GotoStatement **/			skip_stmt,
	/** AstLabeled|DefaultStatement **/				labl_stmt,
	/** AstFor|While|DoWhileStatement **/			loop_stmt,
	/** AstIfStatement **/							ifte_stmt,
	/** AstSwitchStatement **/						swit_stmt,
	/** AstCaseStatement **/						case_stmt,
	/** AstReturnStatement **/						retr_stmt,
	
}
