package __backup__;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
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
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArgumentList;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCastExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;

/**
 * To collect locations where mutant can be seeded
 * @author yukimula
 */
public class MutSource {
	
	/* set of sources for seeding mutants */
	protected Set<AstStatement> 				STRPs 	= new HashSet<AstStatement>();					/* compound statement */
	protected Set<AstStatement> 				STRIs 	= new HashSet<AstStatement>();					/* if | while | do..while | for */
	protected Set<AstExpression>				STRCs 	= new HashSet<AstExpression>();					/* conditions in predicate */
	protected Set<AstStatement> 				SSDLs 	= new HashSet<AstStatement>();					/* any statement except body */
	protected Set<AstBreakStatement> 			SBRCs 	= new HashSet<AstBreakStatement>();				/* break; */
	protected Set<AstContinueStatement>			SCRBs 	= new HashSet<AstContinueStatement>();			/* continue; */
	protected Set<AstWhileStatement>			SWDDs 	= new HashSet<AstWhileStatement>();				/* while-stmt */
	protected Set<AstDoWhileStatement>			SDWDs 	= new HashSet<AstDoWhileStatement>();			/* do-while-stmt */
	protected Set<AstSwitchStatement>			SSWMs 	= new HashSet<AstSwitchStatement>();			/* switch-stmts */
	protected Set<AstStatement>					SMTCs 	= new HashSet<AstStatement>();					/* for | while | do...while */
	protected Set<AstStatement>					OCNGs 	= new HashSet<AstStatement>();					/* if | while | do..while | for */
	
	protected Set<AstExpression>				OPPOs 	= new HashSet<AstExpression>();					/* x++ || ++x */
	protected Set<AstExpression>				OMMOs 	= new HashSet<AstExpression>();					/* x-- || --x */
	protected Set<AstExpression>				UIOIs 	= new HashSet<AstExpression>();					/* access-path */
	protected Set<AstBitwiseBinaryExpression>	OBNGs	= new HashSet<AstBitwiseBinaryExpression>();	/* & | ^ */
	protected Set<AstExpression>				OLNGs	= new HashSet<AstExpression>();					/* logic | relation */
	protected Set<AstUnaryExpression>			ONDUs	= new HashSet<AstUnaryExpression>();			/* unary deletion */
	
	protected Set<AstArithBinaryExpression>		OAXNs 	= new HashSet<AstArithBinaryExpression>();		/* arithmetic operator */
	protected Set<AstBitwiseBinaryExpression>	OBXNs 	= new HashSet<AstBitwiseBinaryExpression>();	/* bitwise operator */
	protected Set<AstLogicBinaryExpression>		OLXNs 	= new HashSet<AstLogicBinaryExpression>();		/* logical operator */
	protected Set<AstRelationExpression>		ORXNs 	= new HashSet<AstRelationExpression>();			/* relation operator */
	protected Set<AstShiftBinaryExpression>		OSXNs 	= new HashSet<AstShiftBinaryExpression>();		/* shifting operator */
	protected Set<AstAssignExpression>			OEXAs 	= new HashSet<AstAssignExpression>();			/* assignment operator */
	
	protected Set<AstArithAssignExpression>		OAXAs	= new HashSet<AstArithAssignExpression>();		/* arithmetic assignment */
	protected Set<AstBitwiseAssignExpression>	OBXAs	= new HashSet<AstBitwiseAssignExpression>();	/* bitwise assignment */
	protected Set<AstShiftAssignExpression>		OSXAs	= new HashSet<AstShiftAssignExpression>();		/* shifting assignment */
	
	protected Set<AstExpression>				VABSs 	= new HashSet<AstExpression>();					/* char | integer | real reference */
	protected Set<AstExpression>				VBCRs 	= new HashSet<AstExpression>();					/* expression(s) in predicate, logical | relation | direct-predicate-expression */
	protected Set<AstExpression>				VDTRs 	= new HashSet<AstExpression>();					/* scalar reference (int | real | char) */
	protected Set<AstExpression> 				VTWDs 	= new HashSet<AstExpression>();					/* scalar reference (int | real | char) */
	protected Set<AstConstant>					Ccsrs 	= new HashSet<AstConstant>();					/* for CRCR, CCSR, CCCR */
	protected Set<AstExpression>				VARRs 	= new HashSet<AstExpression>();					/* array reference */
	protected Set<AstExpression>				VPRRs 	= new HashSet<AstExpression>();					/* pointer reference */
	protected Set<AstExpression>				VSRRs 	= new HashSet<AstExpression>();					/* scalar (char, int, real) reference */
	protected Set<AstExpression>				VTRRs 	= new HashSet<AstExpression>();					/* struct | union reference */
	protected Set<AstFieldExpression>			VSFRs 	= new HashSet<AstFieldExpression>();			/* field-expression */
	
	/* constructor */
	protected MutSource() {}
	
	/* main interface */
	/**
	 * clear all previous records
	 */
	public void initialize() {
		STRPs.clear(); STRIs.clear(); SSDLs.clear(); SSWMs.clear();
		SBRCs.clear(); SCRBs.clear(); SWDDs.clear(); SDWDs.clear();
		SMTCs.clear(); OCNGs.clear(); OBNGs.clear(); OLNGs.clear();
		
		OPPOs.clear(); OMMOs.clear(); UIOIs.clear(); ONDUs.clear();
		
		OAXNs.clear(); OBXNs.clear(); OLXNs.clear(); 
		ORXNs.clear(); OSXNs.clear(); OEXAs.clear();
		OAXAs.clear(); OBXAs.clear(); OSXAs.clear();
		
		VABSs.clear(); VBCRs.clear(); VDTRs.clear(); VTWDs.clear(); VSFRs.clear();
		
		Ccsrs.clear(); VARRs.clear(); VPRRs.clear(); VSRRs.clear(); VTRRs.clear();
		
		/* additional operators here */
		STRCs.clear();
	}
	/**
	 * update the seed source for specific function
	 * @param def
	 * @throws Exception
	 */
	public void collect_all(AstFunctionDefinition def) throws Exception {
		this.collect_in_statement(def.get_body());
	}
	
	/* collect methods */
	/**
	 * collect all sources in the given statement
	 * @param statement
	 * @throws Exception
	 */
	protected void collect_in_statement(AstStatement statement) throws Exception {
		Queue<AstStatement> queue = new LinkedList<AstStatement>();
		
		for(queue.add(statement); !queue.isEmpty();) {
			statement = queue.poll();
			
			if(statement instanceof AstExpressionStatement) {
				this.process_expr_statement((AstExpressionStatement) statement);
			}
			else if(statement instanceof AstBreakStatement) {
				this.process_break((AstBreakStatement) statement);
			}
			else if(statement instanceof AstContinueStatement) {
				this.process_continue((AstContinueStatement) statement);
			}
			else if(statement instanceof AstReturnStatement) {
				this.process_return((AstReturnStatement) statement);
			}
			else if(statement instanceof AstCaseStatement) {
				this.process_case((AstCaseStatement) statement);
			}
			else if(statement instanceof AstSwitchStatement) {
				this.process_switch((AstSwitchStatement) statement);
				queue.add(((AstSwitchStatement) statement).get_body());
			}
			else if(statement instanceof AstIfStatement) {
				this.process_if((AstIfStatement) statement);
				queue.add(((AstIfStatement) statement).get_true_branch());
				if(((AstIfStatement) statement).has_else())
					queue.add(((AstIfStatement) statement).get_false_branch());
			}
			else if(statement instanceof AstWhileStatement) {
				this.process_while((AstWhileStatement) statement);
				queue.add(((AstWhileStatement) statement).get_body());
			}
			else if(statement instanceof AstDoWhileStatement) {
				this.process_do_while((AstDoWhileStatement) statement);
				queue.add(((AstDoWhileStatement) statement).get_body());
			}
			else if(statement instanceof AstForStatement) {
				this.process_for((AstForStatement) statement);
				queue.add(((AstForStatement) statement).get_initializer());
				queue.add(((AstForStatement) statement).get_condition());
				queue.add(((AstForStatement) statement).get_body());
			}
			else if(statement instanceof AstCompoundStatement) {
				this.process_compound((AstCompoundStatement) statement);
				if(((AstCompoundStatement) statement).has_statement_list()) {
					AstStatementList list = ((AstCompoundStatement) 
							statement).get_statement_list();
					int n = list.number_of_statements();
					for(int i = 0; i < n; i++) 
						queue.add(list.get_statement(i));
				}
			}
		}
	}
	/**
	 * collect all sources in the given expression
	 * @param expression
	 * @throws Exception
	 */
	protected void collect_in_expression(AstExpression expression) throws Exception {
		Queue<AstExpression> queue = new LinkedList<AstExpression>();
		
		AstExpression expr;
		for(queue.add(expression);!queue.isEmpty();) {
			expr = queue.poll();	// get next expression
			
			/* access expression */
			if(expr instanceof AstIdExpression) {
				this.process_id_expr((AstIdExpression) expr);
			}
			else if(expr instanceof AstArrayExpression) {
				this.process_array_expr((AstArrayExpression) expr);
				queue.add(((AstArrayExpression) expr).get_array_expression());
				queue.add(((AstArrayExpression) expr).get_dimension_expression());
			}
			else if(expr instanceof AstFieldExpression) {
				this.process_field_expr((AstFieldExpression) expr);
				queue.add(((AstFieldExpression) expr).get_body());
			}
			else if(expr instanceof AstPointUnaryExpression) {
				this.process_point_expr((AstPointUnaryExpression) expr);
				queue.add(((AstPointUnaryExpression) expr).get_operand());
			}
			/* constant expression */
			else if(expr instanceof AstConstant) {
				this.process_constant((AstConstant) expr);
			}
			else if(expr instanceof AstLiteral) {
				// nothing
			}
			/* increment expression */
			else if(expr instanceof AstIncrePostfixExpression) {
				this.process_incre_postfix_expr((AstIncrePostfixExpression) expr);
				queue.add(((AstIncrePostfixExpression) expr).get_operand());
			}
			else if(expr instanceof AstIncreUnaryExpression) {
				this.process_incre_unary_expr((AstIncreUnaryExpression) expr);
				queue.add(((AstIncreUnaryExpression) expr).get_operand());
			}
			/* arithmetic expression */
			else if(expr instanceof AstArithUnaryExpression) {
				this.process_arith_unary_expr((AstArithUnaryExpression) expr);
				queue.add(((AstArithUnaryExpression) expr).get_operand());
			}
			else if(expr instanceof AstArithBinaryExpression) {
				this.process_arith_binary_expr((AstArithBinaryExpression) expr);
				queue.add(((AstArithBinaryExpression) expr).get_loperand());
				queue.add(((AstArithBinaryExpression) expr).get_roperand());
			}
			/* bitwise expression */
			else if(expr instanceof AstBitwiseUnaryExpression) {
				this.process_bit_unary_expr((AstBitwiseUnaryExpression) expr);
				queue.add(((AstBitwiseUnaryExpression) expr).get_operand());
			}
			else if(expr instanceof AstBitwiseBinaryExpression) {
				this.process_bit_binary_expr((AstBitwiseBinaryExpression) expr);
				queue.add(((AstBitwiseBinaryExpression) expr).get_loperand());
				queue.add(((AstBitwiseBinaryExpression) expr).get_roperand());
			}
			/* logical expression*/
			else if(expr instanceof AstLogicUnaryExpression) {
				this.process_log_unary_expr((AstLogicUnaryExpression) expr);
				queue.add(((AstLogicUnaryExpression) expr).get_operand());
			}
			else if(expr instanceof AstLogicBinaryExpression) {
				this.process_log_binary_expr((AstLogicBinaryExpression) expr);
				queue.add(((AstLogicBinaryExpression) expr).get_loperand());
				queue.add(((AstLogicBinaryExpression) expr).get_roperand());
			}
			/* relation */
			else if(expr instanceof AstRelationExpression) {
				this.process_relation_expr((AstRelationExpression) expr);
				queue.add(((AstRelationExpression) expr).get_loperand());
				queue.add(((AstRelationExpression) expr).get_roperand());
			}
			/* shifting expression */
			else if(expr instanceof AstShiftBinaryExpression) {
				this.process_shift_expr((AstShiftBinaryExpression) expr);
				queue.add(((AstShiftBinaryExpression) expr).get_loperand());
				queue.add(((AstShiftBinaryExpression) expr).get_roperand());
			}
			/* assignment expression */
			else if(expr instanceof AstAssignExpression) {
				this.process_assign_expr((AstAssignExpression) expr);
				queue.add(((AstAssignExpression) expr).get_loperand());
				queue.add(((AstAssignExpression) expr).get_roperand());
			}
			/* arithmetic assignment expression */
			else if(expr instanceof AstArithAssignExpression) {
				this.process_arith_assign_expr((AstArithAssignExpression) expr);
				queue.add(((AstArithAssignExpression) expr).get_loperand());
				queue.add(((AstArithAssignExpression) expr).get_roperand());
			}
			/* bitwise assignment expression */
			else if(expr instanceof AstBitwiseAssignExpression) {
				this.process_bit_assign_expr((AstBitwiseAssignExpression) expr);
				queue.add(((AstBitwiseAssignExpression) expr).get_loperand());
				queue.add(((AstBitwiseAssignExpression) expr).get_roperand());
			}
			/* shifting assignment expression */
			else if(expr instanceof AstShiftAssignExpression) {
				this.process_shift_assign_expr((AstShiftAssignExpression) expr);
				queue.add(((AstShiftAssignExpression) expr).get_loperand());
				queue.add(((AstShiftAssignExpression) expr).get_roperand());
			}
			/* special expression*/
			else if(expr instanceof AstCastExpression) {
				queue.add(((AstCastExpression) expr).get_expression());
			}
			else if(expr instanceof AstCommaExpression) {
				int n = ((AstCommaExpression) expr).number_of_arguments();
				for(int k = 0; k < n; k++) {
					queue.add(((AstCommaExpression) expr).get_expression(k));
				}
			}
			else if(expr instanceof AstConditionalExpression) {
				this.process_conditional_expr((AstConditionalExpression) expr);
				queue.add(((AstConditionalExpression) expr).get_condition());
				queue.add(((AstConditionalExpression) expr).get_true_branch());
				queue.add(((AstConditionalExpression) expr).get_false_branch());
			}
			else if(expr instanceof AstFunCallExpression) {
				if(((AstFunCallExpression) expr).has_argument_list()) {
					AstArgumentList alist = ((AstFunCallExpression) expr).get_argument_list();
					for(int k = 0; k < alist.number_of_arguments(); k++) {
						queue.add(alist.get_argument(k));
					}
				}
			}
			/* composite expression */
			else if(expr instanceof AstConstExpression) {
				queue.add(((AstConstExpression) expr).get_expression());
			}
			else if(expr instanceof AstParanthExpression) {
				queue.add(((AstParanthExpression) expr).get_sub_expression());
			}
		}
	}
	
	/* processor for statement */
	/**
	 * collect source in expression-statement
	 * @param stmt
	 * @throws Exception
	 */
	protected void process_expr_statement(AstExpressionStatement stmt) throws Exception {
		if(stmt.has_expression()) {
			SSDLs.add(stmt); 
			this.collect_in_expression(stmt.get_expression());
			STRPs.add(stmt);
		}
	}
	/**
	 * collect source in break statement
	 * @param stmt
	 * @throws Exception
	 */
	protected void process_break(AstBreakStatement stmt) throws Exception {
		SBRCs.add(stmt); SSDLs.add(stmt);
		STRPs.add(stmt);
	}
	/**
	 * collect source in continue statement
	 * @param stmt
	 * @throws Exception
	 */
	protected void process_continue(AstContinueStatement stmt) throws Exception {
		SCRBs.add(stmt); SSDLs.add(stmt);
		STRPs.add(stmt);
	}
	/**
	 * collect source in return statement
	 * @param stmt
	 * @throws Exception
	 */
	protected void process_return(AstReturnStatement stmt) throws Exception {
		if(stmt.has_expression())
			this.collect_in_expression(stmt.get_expression());
		STRPs.add(stmt);
	}
	/**
	 * collect source in case-statement
	 * @param stmt
	 * @throws Exception
	 */
	protected void process_case(AstCaseStatement stmt) throws Exception {
		/**
		 * @deprecated The expression in case statement, if mutated, are highly likely 
		 * to create syntactic-incorrect program code and be avoided to be seeded.
		 */
		// this.collect_in_expression(stmt.get_expression());
	}
	/**
	 * collect source in switch-statement
	 * @param stmt
	 * @throws Exception
	 */
	protected void process_switch(AstSwitchStatement stmt) throws Exception {
		SSDLs.add(stmt); SSWMs.add(stmt); STRPs.add(stmt); 
		this.collect_in_expression(stmt.get_condition());
	}
	/**
	 * collect source in if-statement
	 * @param stmt
	 * @throws Exception
	 */
	protected void process_if(AstIfStatement stmt) throws Exception {
		STRIs.add(stmt); OCNGs.add(stmt);
		SSDLs.add(stmt); STRPs.add(stmt);
		this.collect_in_expression(stmt.get_condition());
		
		this.process_in_predicate(stmt.get_condition());
	}
	/**
	 * collect source in while-statement
	 * @param stmt
	 * @throws Exception
	 */
	protected void process_while(AstWhileStatement stmt) throws Exception {
		SSDLs.add(stmt); STRPs.add(stmt);
		STRIs.add(stmt); OCNGs.add(stmt);
		SWDDs.add(stmt); SMTCs.add(stmt);
		
		process_in_predicate(stmt.get_condition());
	}
	/**
	 * collect source in do-while-statement
	 * @param stmt
	 * @throws Exception
	 */
	protected void process_do_while(AstDoWhileStatement stmt) throws Exception {
		SSDLs.add(stmt); STRPs.add(stmt);
		STRIs.add(stmt); OCNGs.add(stmt);
		SDWDs.add(stmt); SMTCs.add(stmt);
		
		process_in_predicate(stmt.get_condition());
	}
	/**
	 * collect source in for-statement
	 * @param stmt
	 * @throws Exception
	 */
	protected void process_for(AstForStatement stmt) throws Exception {
		SSDLs.add(stmt); STRPs.add(stmt); SMTCs.add(stmt);
		
		if(stmt.get_condition().has_expression()) {
			OCNGs.add(stmt); STRIs.add(stmt); 
			process_in_predicate(stmt.get_condition().get_expression());
		}
		
		if(stmt.has_increment())
			this.collect_in_expression(stmt.get_increment());
	}
	/**
	 * collect source in compound statement
	 * @param stmt
	 * @throws Exception
	 */
	protected void process_compound(AstCompoundStatement stmt) throws Exception {
		if(!(stmt.get_parent() instanceof AstFunctionDefinition)) {
			SSDLs.add(stmt); STRPs.add(stmt);
		}
	}
	/**
	 * collect all the conditions in predicate
	 * @param expr
	 * @throws Exception
	 */
	private void process_in_predicate(AstExpression expr) throws Exception {
		Queue<AstExpression> queue = new LinkedList<AstExpression>();
		
		queue.add(expr);	
		while(!queue.isEmpty()) {
			/* get next pure condition */
			expr = queue.poll();
			while(true) {
				if(expr instanceof AstConstExpression) 
					expr = ((AstConstExpression) expr).get_expression();
				else if(expr instanceof AstParanthExpression)
					expr = ((AstParanthExpression) expr).get_sub_expression();
				else break;
			}
			
			/* append to corresponding mutants */
			VBCRs.add(expr); STRCs.add(expr);
			
			/* add child expressions to the */
			if(expr instanceof AstLogicBinaryExpression) {
				queue.add(((AstLogicBinaryExpression) expr).get_loperand());
				queue.add(((AstLogicBinaryExpression) expr).get_roperand());
			}
			else if(expr instanceof AstLogicUnaryExpression) {
				queue.add(((AstLogicUnaryExpression) expr).get_operand());
			}
		}
	}
	
	/* processor for expressions */
	protected void process_id_expr(AstIdExpression expr) throws Exception {
		CName cname = expr.get_cname();
		if(cname instanceof CInstanceName
				|| cname instanceof CParameterName)		// ignore enumerator
			this.process_access_path(expr);
	}
	protected void process_array_expr(AstArrayExpression expr) throws Exception {
		this.process_access_path(expr);
	}
	protected void process_point_expr(AstPointUnaryExpression expr) throws Exception {
		this.process_access_path(expr);
	}
	protected void process_field_expr(AstFieldExpression expr) throws Exception {
		this.process_access_path(expr);
	}
	private void process_access_path(AstExpression expr) throws Exception {
		CType type = expr.get_value_type();
		boolean cst = JC_Classifier.is_const_type(type);
		type = JC_Classifier.get_value_type(type);
		cst = cst || (type instanceof CArrayType);
		
		if(!JC_Classifier.is_left_operand(expr)) {
			if(JC_Classifier.is_boolean_type(type)) { }
			else if(JC_Classifier.is_character_type(type)) {
				VDTRs.add(expr); VTWDs.add(expr); VSRRs.add(expr);
				if(!JC_Classifier.is_unsigned_type(type)) VABSs.add(expr);
			}
			else if(JC_Classifier.is_integer_type(type)) {
				if(!cst) UIOIs.add(expr); 
				VDTRs.add(expr); VTWDs.add(expr); VSRRs.add(expr);
				if(!JC_Classifier.is_unsigned_type(type)) VABSs.add(expr);
			}
			else if(JC_Classifier.is_real_type(type)) {
				VTWDs.add(expr); VSRRs.add(expr);
				if(!JC_Classifier.is_unsigned_type(type)) VABSs.add(expr);
			}
			else if(type instanceof CArrayType) { VARRs.add(expr); }
			else if(type instanceof CPointerType) { UIOIs.add(expr); VPRRs.add(expr); }
			else if(type instanceof CStructType) { VTRRs.add(expr); }
			else if(type instanceof CUnionType) { }
			
			if(expr instanceof AstFieldExpression) 
				VSFRs.add((AstFieldExpression) expr);
		}
		else { /* left-operand is not seeded for mutants */ }
	}
	protected void process_constant(AstConstant expr) throws Exception {
		Ccsrs.add(expr);
	}
	protected void process_incre_unary_expr(AstIncreUnaryExpression expr) throws Exception {
		COperator op = expr.get_operator().get_operator();
		if(op == COperator.increment) OPPOs.add(expr);
		else if(op == COperator.decrement) OMMOs.add(expr);
	}
	protected void process_incre_postfix_expr(AstIncrePostfixExpression expr) throws Exception {
		COperator op = expr.get_operator().get_operator();
		if(op == COperator.increment) OPPOs.add(expr);
		else if(op == COperator.decrement) OMMOs.add(expr);
	}
	protected void process_arith_unary_expr(AstArithUnaryExpression expr) throws Exception {
		if(expr.get_operator().get_operator() == COperator.negative) ONDUs.add(expr);
	}
	protected void process_arith_binary_expr(AstArithBinaryExpression expr) throws Exception {
		OAXNs.add(expr);
	}
	protected void process_bit_unary_expr(AstBitwiseUnaryExpression expr) throws Exception {
		ONDUs.add(expr);
	}
	protected void process_bit_binary_expr(AstBitwiseBinaryExpression expr) throws Exception {
		OBNGs.add(expr); OBXNs.add(expr);
	}
	protected void process_log_unary_expr(AstLogicUnaryExpression expr) throws Exception {
		OLNGs.add(expr); VBCRs.add(expr); ONDUs.add(expr);
	}
	protected void process_log_binary_expr(AstLogicBinaryExpression expr) throws Exception {
		OLNGs.add(expr); OLXNs.add(expr); VBCRs.add(expr);
	}
	protected void process_relation_expr(AstRelationExpression expr) throws Exception {
		OLNGs.add(expr); ORXNs.add(expr); VBCRs.add(expr);
	}
	protected void process_shift_expr(AstShiftBinaryExpression expr) throws Exception {
		OSXNs.add(expr);
	}
	protected void process_assign_expr(AstAssignExpression expr) throws Exception {
		OEXAs.add(expr);
	}
	protected void process_arith_assign_expr(AstArithAssignExpression expr) throws Exception {
		OAXAs.add(expr);
	}
	protected void process_bit_assign_expr(AstBitwiseAssignExpression expr) throws Exception {
		OBXAs.add(expr);
	}
	protected void process_shift_assign_expr(AstShiftAssignExpression expr) throws Exception {
		OSXAs.add(expr);
	}
	protected void process_conditional_expr(AstConditionalExpression expr) throws Exception {
		this.process_in_predicate(expr.get_condition());
	}
}
