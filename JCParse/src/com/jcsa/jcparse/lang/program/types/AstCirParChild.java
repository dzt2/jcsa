package com.jcsa.jcparse.lang.program.types;

/**
 * 	The type of the link from AstCirNode (parent) to its AstCirNode (child).
 * 	
 * 	@author yukimula
 *
 */
public enum AstCirParChild {
	
	/** AstUnary|Cast|CommaExpression --> unary-operand **/		uoperand,
	/** LogicXXX|CondExpr|If|While|For|Do --> condition **/		condition,
	/** Arith|Bitws|Relate|CondExpression --> left_operand **/	loperand,
	/** Arith|Bitws|Relate|CondExpression --> righ_operand **/	roperand,
	
	/** AstUnary|PostfixIncreExpression --> operand **/			ivalue,
	/** Arith|BitwsAssignExpression --> left-operand **/		lvalue,
	/** Arith|BitwsAssignExpression --> right-operand **/		rvalue,
	/** AstPointUnary(*)|AstArrayExpression --> operand **/		address,
	/** AstArrayExpression --> dimension **/					index,
	
	/** AstInitializerBody --> AstExpression(*) **/				element,
	/** AstFunCallExpression --> function **/					callee,
	/** AstFunCallExpression.arg_list --> argument[k] **/		argument,
	/** AstFieldExpression --> body **/							fbody, 
	/** AstFieldExpression --> field **/						field,
	
	/** AstDeclStmt --> InitDecl; AstExprStmt --> expr **/		evaluate,
	/** AstComp|Switch|ForStmt --> sub_statement|init **/		execute,
	/** AstIf|DoWhile|While|ForStatement --> body **/			tbranch,
	/** AstIfStatement --> else.body **/						fbranch,
	/** AstSitch|CaseStatement --> condition **/				n_condition,
	
	/** AstFunctionDefinition --> declarator **/				name,
	/** AstFunctionDefinition --> body **/						body,
	/** AstTranslationUnit	--> AstExternalUnit **/				define,
	
}
