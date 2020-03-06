package __backup__;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
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
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CFieldBody;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

/**
 * Factory for producing mutation based on AST node
 * @author yukimula
 */
public class MutationFactory {
	
	/* constructor */
	/**
	 * constructor
	 */
	public MutationFactory() {}
	
	/* trap-class mutation */
	/**
	 * <code>statement |==> {trap_on_statement(); statement}</code>
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public STRP_Mutation gen_STRP(AstStatement statement) throws Exception {
		return new STRP_Mutation(statement);
	}
	/**
	 * <code>expression |==> trap_on_true|false(condition) </code>
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public STRC_Mutation[] gen_STRC(AstExpression condition) throws Exception {
		STRC_Mutation[] mutations = new STRC_Mutation[2];
		mutations[0] = new STRC_Mutation(MutationMode.TRAP_ON_TRUE, condition);
		mutations[1] = new STRC_Mutation(MutationMode.TRAP_ON_FALSE,condition);
		return mutations;
	}
	/**
	 * <code>trap_on_true(predicate)</code>
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public STRI_Mutation[] gen_STRI(AstStatement statement) throws Exception {
		STRI_Mutation[] mutations = new STRI_Mutation[2];
		mutations[0] = new STRI_Mutation(MutationMode.TRAP_ON_TRUE, statement);
		mutations[1] = new STRI_Mutation(MutationMode.TRAP_ON_FALSE,statement);
		return mutations;
	}
	/**
	 * <code>block |--> {}</code>
	 * @param block
	 * @return
	 * @throws Exception
	 */
	public SSDL_Mutation gen_SSDL(AstStatement block) throws Exception {
		return new SSDL_Mutation(block);
	}
	/**
	 * <code>break; |==> continue;</code>
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public SBRC_Mutation gen_SBRC(AstBreakStatement statement) throws Exception {
		return new SBRC_Mutation(statement);
	}
	/**
	 * <code>continue; |==> break;</code>
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public SCRB_Mutation gen_SCRB(AstContinueStatement statement) throws Exception {
		return new SCRB_Mutation(statement);
	}
	/**
	 * <code>while |--> do...while</code>
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public SWDD_Mutation gen_SWDD(AstWhileStatement statement) throws Exception {
		return new SWDD_Mutation(statement);
	}
	/**
	 * <code>do...while |==> while</code>
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public SDWD_Mutation gen_SDWD(AstDoWhileStatement statement) throws Exception {
		return new SDWD_Mutation(statement);
	}
	/**
	 * <code>trap_on_case(expr, value)</code>
	 * @param statement
	 * @param constant
	 * @return
	 * @throws Exception
	 */
	public SSWM_Mutation[] gen_SSWM(AstSwitchStatement statement) throws Exception {
		/* collect case-list */
		AstStatement body = statement.get_body();
		AstStatementList slist; AstStatement si;
		List<AstConstExpression> cases = new ArrayList<AstConstExpression>();
		if(body instanceof AstCompoundStatement) {
			slist = ((AstCompoundStatement) body).get_statement_list();
			for(int k = 0;k < slist.number_of_statements();k++) {
				si = slist.get_statement(k);
				if(si instanceof AstCaseStatement) {
					cases.add(((AstCaseStatement) si).get_expression());
				}
			}
		}
		
		/* produce mutations */
		SSWM_Mutation[] mutations;
		if(!cases.isEmpty()) {
			mutations = new SSWM_Mutation[cases.size()];
			for(int k = 0; k < mutations.length; k++)
				mutations[k] = new SSWM_Mutation(statement, cases.get(k));
		}
		else mutations = null;
		
		return mutations;
	}
	/**
	 * <code>trap_after_times(expr, n)</code>
	 * @param loop_statement
	 * @param times
	 * @return
	 * @throws Exception
	 */
	public SMTC_Mutation gen_SMTC(AstStatement loop_statement, int times) throws Exception {
		return new SMTC_Mutation(loop_statement, times);
	}
	
	/* unary operator mutation */
	/**
	 * <code>x++ |==> ++x || x--</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OPPO_Mutation[] gen_OPPO(AstIncrePostfixExpression expression) throws Exception {
		OPPO_Mutation[] mutations = new OPPO_Mutation[2];
		mutations[0] = new OPPO_Mutation(MutationMode.POST_PREV_INC, expression);
		mutations[1] = new OPPO_Mutation(MutationMode.POST_INC_DEC,  expression);
		return mutations;
	}
	/**
	 * <code>++x |==> x++ || --x</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OPPO_Mutation[] gen_OPPO(AstIncreUnaryExpression expression) throws Exception {
		OPPO_Mutation[] mutations = new OPPO_Mutation[2];
		mutations[0] = new OPPO_Mutation(MutationMode.PREV_POST_INC, expression);
		mutations[1] = new OPPO_Mutation(MutationMode.PREV_INC_DEC,  expression);
		return mutations;
	}
	/**
	 * <code>x-- |==> --x || x++</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OMMO_Mutation[] gen_OMMO(AstIncrePostfixExpression expression) throws Exception {
		OMMO_Mutation[] mutations = new OMMO_Mutation[2];
		mutations[0] = new OMMO_Mutation(MutationMode.POST_PREV_DEC, expression);
		mutations[1] = new OMMO_Mutation(MutationMode.POST_DEC_INC,  expression);
		return mutations;
	}
	/**
	 * <code>--x |==> x-- || ++x</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OMMO_Mutation[] gen_OMMO(AstIncreUnaryExpression expression) throws Exception {
		OMMO_Mutation[] mutations = new OMMO_Mutation[2];
		mutations[0] = new OMMO_Mutation(MutationMode.PREV_POST_DEC, expression);
		mutations[1] = new OMMO_Mutation(MutationMode.PREV_DEC_INC,  expression);
		return mutations;
	}
	/**
	 * <code>x |==> x++ | ++x | x-- | --x</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public UIOI_Mutation[] gen_UIOI(AstExpression expression) throws Exception {
		UIOI_Mutation[] mutations = new UIOI_Mutation[4];
		mutations[0] = new UIOI_Mutation(MutationMode.POST_INC_INS, expression);
		mutations[1] = new UIOI_Mutation(MutationMode.POST_DEC_INS, expression);
		mutations[2] = new UIOI_Mutation(MutationMode.PREV_INC_INS, expression);
		mutations[3] = new UIOI_Mutation(MutationMode.PREV_DEC_INS, expression);
		return mutations;
	}
	/**
	 * <code>expr |==> ~expr</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OBNG_Mutation gen_OBNG(AstBitwiseBinaryExpression expression) throws Exception {
		return new OBNG_Mutation(expression);
	}
	/**
	 * <code>predicate |==> !(predicate)</code>
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public OCNG_Mutation gen_OCNG(AstStatement statement) throws Exception {
		return new OCNG_Mutation(statement);
	}
	/**
	 * <code>{relation | logical} expression |--> !expr</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OLNG_Mutation gen_OLNG(AstExpression expression) throws Exception {
		return new OLNG_Mutation(expression);
	}
	
	/* arithmetic operator mutation */
	/**
	 * <code>{+, -, *, /, %}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OAAN_Mutation[] gen_OAAN(AstArithBinaryExpression expression) throws Exception {
		/* declarations */
		CType type = JC_Classifier.get_value_type(expression.get_value_type());
		boolean mod = JC_Classifier.is_boolean_type(type) || JC_Classifier.is_integer_type(type);
		boolean ptr = JC_Classifier.is_address_type(type); 
		List<OAAN_Mutation> mutations = new ArrayList<OAAN_Mutation>();
		
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case arith_add:
			if(!ptr) mutations.add(new OAAN_Mutation(MutationMode.ADD_SUB, expression));
			if(!ptr) mutations.add(new OAAN_Mutation(MutationMode.ADD_MUL, expression));
			if(!ptr) mutations.add(new OAAN_Mutation(MutationMode.ADD_DIV, expression));
			if(mod)	 mutations.add(new OAAN_Mutation(MutationMode.ADD_MOD, expression));
			break;
		case arith_sub:
			if(!ptr) mutations.add(new OAAN_Mutation(MutationMode.SUB_ADD, expression));
			if(!ptr) mutations.add(new OAAN_Mutation(MutationMode.SUB_MUL, expression));
			if(!ptr) mutations.add(new OAAN_Mutation(MutationMode.SUB_DIV, expression));
			if(mod)	 mutations.add(new OAAN_Mutation(MutationMode.SUB_MOD, expression));
			break;
		case arith_mul:
			if(!ptr) mutations.add(new OAAN_Mutation(MutationMode.MUL_ADD, expression));
			if(!ptr) mutations.add(new OAAN_Mutation(MutationMode.MUL_SUB, expression));
			if(!ptr) mutations.add(new OAAN_Mutation(MutationMode.MUL_DIV, expression));
			if(mod)	 mutations.add(new OAAN_Mutation(MutationMode.MUL_MOD, expression));
			break;
		case arith_div: 
			if(!ptr) mutations.add(new OAAN_Mutation(MutationMode.DIV_ADD, expression));
			if(!ptr) mutations.add(new OAAN_Mutation(MutationMode.DIV_SUB, expression));
			if(!ptr) mutations.add(new OAAN_Mutation(MutationMode.DIV_MUL, expression));
			if(mod)	 mutations.add(new OAAN_Mutation(MutationMode.DIV_MOD, expression));
			break;
		case arith_mod:
			if(!ptr) mutations.add(new OAAN_Mutation(MutationMode.MOD_ADD, expression));
			if(!ptr) mutations.add(new OAAN_Mutation(MutationMode.MOD_SUB, expression));
			if(!ptr) mutations.add(new OAAN_Mutation(MutationMode.MOD_MUL, expression));
			if(mod)	 mutations.add(new OAAN_Mutation(MutationMode.MOD_DIV, expression));
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		
		OAAN_Mutation[] array = new OAAN_Mutation[mutations.size()];
		for(int k = 0; k < array.length; k++) array[k] = mutations.get(k);
		return array;
	}
	/**
	 * <code>{+, -, *, /, %} ==> {&, |, ^}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OABN_Mutation[] gen_OABN(AstArithBinaryExpression expression) throws Exception {
		CType type = JC_Classifier.get_value_type(expression.get_value_type());
		boolean bit = JC_Classifier.is_boolean_type(type) || JC_Classifier.is_integer_type(type);
		
		if(bit) {
			OABN_Mutation[] mutations = new OABN_Mutation[3];
			COperator operator = expression.get_operator().get_operator();
			
			switch(operator) {
			case arith_add:
				mutations[0] = new OABN_Mutation(MutationMode.ADD_BAN, expression);
				mutations[1] = new OABN_Mutation(MutationMode.ADD_BOR, expression);
				mutations[2] = new OABN_Mutation(MutationMode.ADD_BXR, expression);
				break;
			case arith_sub:
				mutations[0] = new OABN_Mutation(MutationMode.SUB_BAN, expression);
				mutations[1] = new OABN_Mutation(MutationMode.SUB_BOR, expression);
				mutations[2] = new OABN_Mutation(MutationMode.SUB_BXR, expression);
				break;
			case arith_mul:
				mutations[0] = new OABN_Mutation(MutationMode.MUL_BAN, expression);
				mutations[1] = new OABN_Mutation(MutationMode.MUL_BOR, expression);
				mutations[2] = new OABN_Mutation(MutationMode.MUL_BXR, expression);
				break;
			case arith_div:
				mutations[0] = new OABN_Mutation(MutationMode.DIV_BAN, expression);
				mutations[1] = new OABN_Mutation(MutationMode.DIV_BOR, expression);
				mutations[2] = new OABN_Mutation(MutationMode.DIV_BXR, expression);
				break;
			case arith_mod:
				mutations[0] = new OABN_Mutation(MutationMode.MOD_BAN, expression);
				mutations[1] = new OABN_Mutation(MutationMode.MOD_BOR, expression);
				mutations[2] = new OABN_Mutation(MutationMode.MOD_BXR, expression);
				break;
			default: throw new IllegalArgumentException("Invalid operator: " + operator);
			}
			
			return mutations;
		}
		else return null;
		
	}
	/**
	 * <code>{+, -, *, /, %} ==> {&&, ||}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OALN_Mutation[] gen_OALN(AstArithBinaryExpression expression) throws Exception {
		/* declarations */
		CType type = JC_Classifier.get_value_type(expression.get_value_type());
		boolean ptr = JC_Classifier.is_address_type(type); 
		
		/* return the OALN mutations when valid */
		if(!ptr) {
			OALN_Mutation[] mutations = new OALN_Mutation[2];
			COperator operator = expression.get_operator().get_operator();
			
			switch(operator) {
			case arith_add:
				mutations[0] = new OALN_Mutation(MutationMode.ADD_LAN, expression);
				mutations[1] = new OALN_Mutation(MutationMode.ADD_LOR, expression);
				break;
			case arith_sub:
				mutations[0] = new OALN_Mutation(MutationMode.SUB_LAN, expression);
				mutations[1] = new OALN_Mutation(MutationMode.SUB_LOR, expression);
				break;
			case arith_mul:
				mutations[0] = new OALN_Mutation(MutationMode.MUL_LAN, expression);
				mutations[1] = new OALN_Mutation(MutationMode.MUL_LOR, expression);
				break;
			case arith_div:
				mutations[0] = new OALN_Mutation(MutationMode.DIV_LAN, expression);
				mutations[1] = new OALN_Mutation(MutationMode.DIV_LOR, expression);
				break;
			case arith_mod:
				mutations[0] = new OALN_Mutation(MutationMode.MOD_LAN, expression);
				mutations[1] = new OALN_Mutation(MutationMode.MOD_LOR, expression);
				break;
			default: throw new IllegalArgumentException("Invalid operator: " + operator);
			}
			
			return mutations;
		}
		else return null;
	}
	/**
	 * <code>{+, -, *, /, %} ==> {>, >=, ==, !=, <, <=}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OARN_Mutation[] gen_OARN(AstArithBinaryExpression expression) throws Exception {
		CType type = JC_Classifier.get_value_type(expression.get_value_type());
		if(JC_Classifier.is_address_type(type)) return null;
		else {
			OARN_Mutation[] mutations = new OARN_Mutation[6];
			COperator operator = expression.get_operator().get_operator();
			
			switch(operator) {
			case arith_add:
				mutations[0] = new OARN_Mutation(MutationMode.ADD_GRT, expression);
				mutations[1] = new OARN_Mutation(MutationMode.ADD_GRE, expression);
				mutations[2] = new OARN_Mutation(MutationMode.ADD_EQV, expression);
				mutations[3] = new OARN_Mutation(MutationMode.ADD_NEQ, expression);
				mutations[4] = new OARN_Mutation(MutationMode.ADD_SMT, expression);
				mutations[5] = new OARN_Mutation(MutationMode.ADD_SME, expression);
				break;
			case arith_sub:
				mutations[0] = new OARN_Mutation(MutationMode.SUB_GRT, expression);
				mutations[1] = new OARN_Mutation(MutationMode.SUB_GRE, expression);
				mutations[2] = new OARN_Mutation(MutationMode.SUB_EQV, expression);
				mutations[3] = new OARN_Mutation(MutationMode.SUB_NEQ, expression);
				mutations[4] = new OARN_Mutation(MutationMode.SUB_SMT, expression);
				mutations[5] = new OARN_Mutation(MutationMode.SUB_SME, expression);
				break;
			case arith_mul:
				mutations[0] = new OARN_Mutation(MutationMode.MUL_GRT, expression);
				mutations[1] = new OARN_Mutation(MutationMode.MUL_GRE, expression);
				mutations[2] = new OARN_Mutation(MutationMode.MUL_EQV, expression);
				mutations[3] = new OARN_Mutation(MutationMode.MUL_NEQ, expression);
				mutations[4] = new OARN_Mutation(MutationMode.MUL_SMT, expression);
				mutations[5] = new OARN_Mutation(MutationMode.MUL_SME, expression);
				break;
			case arith_div:
				mutations[0] = new OARN_Mutation(MutationMode.DIV_GRT, expression);
				mutations[1] = new OARN_Mutation(MutationMode.DIV_GRE, expression);
				mutations[2] = new OARN_Mutation(MutationMode.DIV_EQV, expression);
				mutations[3] = new OARN_Mutation(MutationMode.DIV_NEQ, expression);
				mutations[4] = new OARN_Mutation(MutationMode.DIV_SMT, expression);
				mutations[5] = new OARN_Mutation(MutationMode.DIV_SME, expression);
				break;
			case arith_mod:
				mutations[0] = new OARN_Mutation(MutationMode.MOD_GRT, expression);
				mutations[1] = new OARN_Mutation(MutationMode.MOD_GRE, expression);
				mutations[2] = new OARN_Mutation(MutationMode.MOD_EQV, expression);
				mutations[3] = new OARN_Mutation(MutationMode.MOD_NEQ, expression);
				mutations[4] = new OARN_Mutation(MutationMode.MOD_SMT, expression);
				mutations[5] = new OARN_Mutation(MutationMode.MOD_SME, expression);
				break;
			default: throw new IllegalArgumentException("Invalid operator: " + operator);
			}
			
			return mutations;
		}
	}
	/**
	 * <code>{+, -, *, /, %} ==> {>>, <<}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OASN_Mutation[] gen_OASN(AstArithBinaryExpression expression) throws Exception {
		CType type = JC_Classifier.get_value_type(expression.get_value_type());
		boolean bit = JC_Classifier.is_boolean_type(type) || JC_Classifier.is_integer_type(type);
		if(bit) {
			OASN_Mutation[] mutations = new OASN_Mutation[2];
			COperator operator = expression.get_operator().get_operator();
			
			switch(operator) {
			case arith_add:
				mutations[0] = new OASN_Mutation(MutationMode.ADD_LSH, expression);
				mutations[1] = new OASN_Mutation(MutationMode.ADD_RSH, expression);
				break;
			case arith_sub:
				mutations[0] = new OASN_Mutation(MutationMode.SUB_LSH, expression);
				mutations[1] = new OASN_Mutation(MutationMode.SUB_RSH, expression);
				break;
			case arith_mul:
				mutations[0] = new OASN_Mutation(MutationMode.MUL_LSH, expression);
				mutations[1] = new OASN_Mutation(MutationMode.MUL_RSH, expression);
				break;
			case arith_div:
				mutations[0] = new OASN_Mutation(MutationMode.DIV_LSH, expression);
				mutations[1] = new OASN_Mutation(MutationMode.DIV_RSH, expression);
				break;
			case arith_mod:
				mutations[0] = new OASN_Mutation(MutationMode.MOD_LSH, expression);
				mutations[1] = new OASN_Mutation(MutationMode.MOD_RSH, expression);
				break;
			default: throw new IllegalArgumentException("Invalid operator: " + operator);
			}
			
			return mutations;
		}
		else return null;
	}
	
	/* bitwise operator mutation */
	/**
	 * <code>{&, |, ^} |==> {+, -, *, /, %}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OBAN_Mutation[] gen_OBAN(AstBitwiseBinaryExpression expression) throws Exception {
		OBAN_Mutation[] mutations = new OBAN_Mutation[5];
		COperator operator = expression.get_operator().get_operator();
		
		switch(operator) {
		case bit_and:
			mutations[0] = new OBAN_Mutation(MutationMode.BAN_ADD, expression);
			mutations[1] = new OBAN_Mutation(MutationMode.BAN_SUB, expression);
			mutations[2] = new OBAN_Mutation(MutationMode.BAN_MUL, expression);
			mutations[3] = new OBAN_Mutation(MutationMode.BAN_DIV, expression);
			mutations[4] = new OBAN_Mutation(MutationMode.BAN_MOD, expression);
			break;
		case bit_or:
			mutations[0] = new OBAN_Mutation(MutationMode.BOR_ADD, expression);
			mutations[1] = new OBAN_Mutation(MutationMode.BOR_SUB, expression);
			mutations[2] = new OBAN_Mutation(MutationMode.BOR_MUL, expression);
			mutations[3] = new OBAN_Mutation(MutationMode.BOR_DIV, expression);
			mutations[4] = new OBAN_Mutation(MutationMode.BOR_MOD, expression);
			break;
		case bit_xor:
			mutations[0] = new OBAN_Mutation(MutationMode.BXR_ADD, expression);
			mutations[1] = new OBAN_Mutation(MutationMode.BXR_SUB, expression);
			mutations[2] = new OBAN_Mutation(MutationMode.BXR_MUL, expression);
			mutations[3] = new OBAN_Mutation(MutationMode.BXR_DIV, expression);
			mutations[4] = new OBAN_Mutation(MutationMode.BXR_MOD, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		
		return mutations;
	}
	/**
	 * <code>{&, |, ^}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OBBN_Mutation[] gen_OBBN(AstBitwiseBinaryExpression expression) throws Exception {
		OBBN_Mutation[] mutations = new OBBN_Mutation[2];
		COperator operator = expression.get_operator().get_operator();
		
		switch(operator) {
		case bit_and:
			mutations[0] = new OBBN_Mutation(MutationMode.BAN_BOR, expression);
			mutations[1] = new OBBN_Mutation(MutationMode.BAN_BXR, expression);
			break;
		case bit_or:
			mutations[0] = new OBBN_Mutation(MutationMode.BOR_BAN, expression);
			mutations[1] = new OBBN_Mutation(MutationMode.BOR_BXR, expression);
			break;
		case bit_xor:
			mutations[0] = new OBBN_Mutation(MutationMode.BXR_BOR, expression);
			mutations[1] = new OBBN_Mutation(MutationMode.BXR_BAN, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		
		return mutations;
	}
	/**
	 * <code>{&, |, ^} |==> {&&, ||}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OBLN_Mutation[] gen_OBLN(AstBitwiseBinaryExpression expression) throws Exception {
		OBLN_Mutation[] mutations = new OBLN_Mutation[2];
		COperator operator = expression.get_operator().get_operator();
		
		switch(operator) {
		case bit_and:
			mutations[0] = new OBLN_Mutation(MutationMode.BAN_LAN, expression);
			mutations[1] = new OBLN_Mutation(MutationMode.BAN_LOR, expression);
			break;
		case bit_or:
			mutations[0] = new OBLN_Mutation(MutationMode.BOR_LAN, expression);
			mutations[1] = new OBLN_Mutation(MutationMode.BOR_LOR, expression);
			break;
		case bit_xor:
			mutations[0] = new OBLN_Mutation(MutationMode.BXR_LAN, expression);
			mutations[1] = new OBLN_Mutation(MutationMode.BXR_LOR, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		
		return mutations;
	}
	/**
	 * <code>{&, |, ^} |==> {<, <=, ==, !=, >, >=}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OBRN_Mutation[] gen_OBRN(AstBitwiseBinaryExpression expression) throws Exception {
		OBRN_Mutation[] mutations = new OBRN_Mutation[6];
		COperator operator = expression.get_operator().get_operator();
		
		switch(operator) {
		case bit_and:
			mutations[0] = new OBRN_Mutation(MutationMode.BAN_GRT, expression);
			mutations[1] = new OBRN_Mutation(MutationMode.BAN_GRE, expression);
			mutations[2] = new OBRN_Mutation(MutationMode.BAN_EQV, expression);
			mutations[3] = new OBRN_Mutation(MutationMode.BAN_NEQ, expression);
			mutations[4] = new OBRN_Mutation(MutationMode.BAN_SMT, expression);
			mutations[5] = new OBRN_Mutation(MutationMode.BAN_SME, expression);
			break;
		case bit_or:
			mutations[0] = new OBRN_Mutation(MutationMode.BOR_GRT, expression);
			mutations[1] = new OBRN_Mutation(MutationMode.BOR_GRE, expression);
			mutations[2] = new OBRN_Mutation(MutationMode.BOR_EQV, expression);
			mutations[3] = new OBRN_Mutation(MutationMode.BOR_NEQ, expression);
			mutations[4] = new OBRN_Mutation(MutationMode.BOR_SMT, expression);
			mutations[5] = new OBRN_Mutation(MutationMode.BOR_SME, expression);
			break;
		case bit_xor:
			mutations[0] = new OBRN_Mutation(MutationMode.BXR_GRT, expression);
			mutations[1] = new OBRN_Mutation(MutationMode.BXR_GRE, expression);
			mutations[2] = new OBRN_Mutation(MutationMode.BXR_EQV, expression);
			mutations[3] = new OBRN_Mutation(MutationMode.BXR_NEQ, expression);
			mutations[4] = new OBRN_Mutation(MutationMode.BXR_SMT, expression);
			mutations[5] = new OBRN_Mutation(MutationMode.BXR_SME, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		
		return mutations;
	}
	/**
	 * <code>{&, |, ^} |==> {>>, <<}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OBSN_Mutation[] gen_OBSN(AstBitwiseBinaryExpression expression) throws Exception {
		OBSN_Mutation[] mutations = new OBSN_Mutation[2];
		COperator operator = expression.get_operator().get_operator();
		
		switch(operator) {
		case bit_and:
			mutations[0] = new OBSN_Mutation(MutationMode.BAN_LSH, expression);
			mutations[1] = new OBSN_Mutation(MutationMode.BAN_RSH, expression);
			break;
		case bit_or:
			mutations[0] = new OBSN_Mutation(MutationMode.BOR_LSH, expression);
			mutations[1] = new OBSN_Mutation(MutationMode.BOR_RSH, expression);
			break;
		case bit_xor:
			mutations[0] = new OBSN_Mutation(MutationMode.BXR_LSH, expression);
			mutations[1] = new OBSN_Mutation(MutationMode.BXR_RSH, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		
		return mutations;
	}
	
	/* logical operator mutation */
	/**
	 * <code>{&&, ||} |==> {+, -, *, /, %}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OLAN_Mutation[] gen_OLAN(AstLogicBinaryExpression expression) throws Exception {
		OLAN_Mutation[] mutations = new OLAN_Mutation[5];
		COperator operator = expression.get_operator().get_operator();
		
		switch(operator) {
		case logic_and:
			mutations[0] = new OLAN_Mutation(MutationMode.LAN_ADD, expression);
			mutations[1] = new OLAN_Mutation(MutationMode.LAN_SUB, expression);
			mutations[2] = new OLAN_Mutation(MutationMode.LAN_MUL, expression);
			mutations[3] = new OLAN_Mutation(MutationMode.LAN_DIV, expression);
			mutations[4] = new OLAN_Mutation(MutationMode.LAN_MOD, expression);
			break;
		case logic_or:
			mutations[0] = new OLAN_Mutation(MutationMode.LOR_ADD, expression);
			mutations[1] = new OLAN_Mutation(MutationMode.LOR_SUB, expression);
			mutations[2] = new OLAN_Mutation(MutationMode.LOR_MUL, expression);
			mutations[3] = new OLAN_Mutation(MutationMode.LOR_DIV, expression);
			mutations[4] = new OLAN_Mutation(MutationMode.LOR_MOD, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		
		return mutations;
	}
	/**
	 * <code>{&&, ||} |==> {&, |, ^}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OLBN_Mutation[] gen_OLBN(AstLogicBinaryExpression expression) throws Exception {
		OLBN_Mutation[] mutations = new OLBN_Mutation[3];
		COperator operator = expression.get_operator().get_operator();
		
		switch(operator) {
		case logic_and:
			mutations[0] = new OLBN_Mutation(MutationMode.LAN_BAN, expression);
			mutations[1] = new OLBN_Mutation(MutationMode.LAN_BOR, expression);
			mutations[2] = new OLBN_Mutation(MutationMode.LAN_BXR, expression);
			break;
		case logic_or:
			mutations[0] = new OLBN_Mutation(MutationMode.LOR_BAN, expression);
			mutations[1] = new OLBN_Mutation(MutationMode.LOR_BOR, expression);
			mutations[2] = new OLBN_Mutation(MutationMode.LOR_BXR, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		
		return mutations;
	}
	/**
	 * <code>{&&, ||}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OLLN_Mutation gen_OLLN(AstLogicBinaryExpression expression) throws Exception {
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case logic_and:
			return new OLLN_Mutation(MutationMode.LAN_LOR, expression);
		case logic_or:
			return new OLLN_Mutation(MutationMode.LOR_LAN, expression);
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
	}
	/**
	 * <code>{&&, ||} ==> {>, >=, ==, !=, <, <=}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OLRN_Mutation[] gen_OLRN(AstLogicBinaryExpression expression) throws Exception {
		OLRN_Mutation[] mutations = new OLRN_Mutation[6];
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case logic_and:
			mutations[0] = new OLRN_Mutation(MutationMode.LAN_GRT, expression);
			mutations[1] = new OLRN_Mutation(MutationMode.LAN_GRE, expression);
			mutations[2] = new OLRN_Mutation(MutationMode.LAN_EQV, expression);
			mutations[3] = new OLRN_Mutation(MutationMode.LAN_NEQ, expression);
			mutations[4] = new OLRN_Mutation(MutationMode.LAN_SMT, expression);
			mutations[5] = new OLRN_Mutation(MutationMode.LAN_SME, expression);
			break;
		case logic_or:
			mutations[0] = new OLRN_Mutation(MutationMode.LOR_GRT, expression);
			mutations[1] = new OLRN_Mutation(MutationMode.LOR_GRE, expression);
			mutations[2] = new OLRN_Mutation(MutationMode.LOR_EQV, expression);
			mutations[3] = new OLRN_Mutation(MutationMode.LOR_NEQ, expression);
			mutations[4] = new OLRN_Mutation(MutationMode.LOR_SMT, expression);
			mutations[5] = new OLRN_Mutation(MutationMode.LOR_SME, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return mutations;
	}
	/**
	 * <code>{&&, ||} ==> {>>, <<}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OLSN_Mutation[] gen_OLSN(AstLogicBinaryExpression expression) throws Exception {
		OLSN_Mutation[] mutations = new OLSN_Mutation[2];
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case logic_and:
			mutations[0] = new OLSN_Mutation(MutationMode.LAN_LSH, expression);
			mutations[1] = new OLSN_Mutation(MutationMode.LAN_RSH, expression);
			break;
		case logic_or:
			mutations[0] = new OLSN_Mutation(MutationMode.LOR_LSH, expression);
			mutations[1] = new OLSN_Mutation(MutationMode.LOR_RSH, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return mutations;
	}
	
	/* relational operator mutation */
	/**
	 * <code>{>, >=, ==, !=, <, <=} ==> {+, -, *, /, %}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public ORAN_Mutation[] gen_ORAN(AstRelationExpression expression) throws Exception {
		AstExpression E1 = expression.get_loperand(), E2 = expression.get_roperand();
		CType type1 = JC_Classifier.get_value_type(E1.get_value_type());
		CType type2 = JC_Classifier.get_value_type(E2.get_value_type());
		boolean ptr = !(JC_Classifier.is_address_type(type1) || JC_Classifier.is_address_type(type2));
		boolean mod = JC_Classifier.is_boolean_type(type1) || JC_Classifier.is_integer_type(type1);
		if(mod) mod = JC_Classifier.is_boolean_type(type2) || JC_Classifier.is_integer_type(type2);
		
		List<ORAN_Mutation> mutations = new ArrayList<ORAN_Mutation>();
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case greater_tn:
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.GRT_ADD, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.GRT_SUB, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.GRT_MUL, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.GRT_DIV, expression));
			if(mod)	mutations.add(new ORAN_Mutation(MutationMode.GRT_MOD, expression));
			break;
		case greater_eq:
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.GRE_ADD, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.GRE_SUB, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.GRE_MUL, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.GRE_DIV, expression));
			if(mod)	mutations.add(new ORAN_Mutation(MutationMode.GRE_MOD, expression));
			break;
		case equal_with:
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.EQV_ADD, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.EQV_SUB, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.EQV_MUL, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.EQV_DIV, expression));
			if(mod)	mutations.add(new ORAN_Mutation(MutationMode.EQV_MOD, expression));
			break;
		case not_equals:
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.NEQ_ADD, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.NEQ_SUB, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.NEQ_MUL, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.NEQ_DIV, expression));
			if(mod)	mutations.add(new ORAN_Mutation(MutationMode.NEQ_MOD, expression));
			break;
		case smaller_tn:
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.SMT_ADD, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.SMT_SUB, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.SMT_MUL, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.SMT_DIV, expression));
			if(mod)	mutations.add(new ORAN_Mutation(MutationMode.SMT_MOD, expression));
			break;
		case smaller_eq:
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.SME_ADD, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.SME_SUB, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.SME_MUL, expression));
			if(ptr) mutations.add(new ORAN_Mutation(MutationMode.SME_DIV, expression));
			if(mod)	mutations.add(new ORAN_Mutation(MutationMode.SME_MOD, expression));
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		
		if(!mutations.isEmpty()) {
			ORAN_Mutation[] array = new ORAN_Mutation[mutations.size()];
			for(int k = 0; k < array.length; k++) array[k] = mutations.get(k);
			return array;
		}
		else return null;
	}
	/**
	 * <code>{>, >=, ==, !=, <, <=} ==> {&, |, ^}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public ORBN_Mutation[] gen_ORBN(AstRelationExpression expression) throws Exception {
		AstExpression E1 = expression.get_loperand(), E2 = expression.get_roperand();
		CType type1 = JC_Classifier.get_value_type(E1.get_value_type()); boolean bit;
		CType type2 = JC_Classifier.get_value_type(E2.get_value_type());
		bit = JC_Classifier.is_boolean_type(type1) || JC_Classifier.is_integer_type(type1);
		if(bit) bit = JC_Classifier.is_boolean_type(type1) || JC_Classifier.is_integer_type(type2);
		
		if(bit) {
			ORBN_Mutation[] mutations = new ORBN_Mutation[3];
			COperator operator = expression.get_operator().get_operator();
			switch(operator) {
			case greater_tn:
				mutations[0] = new ORBN_Mutation(MutationMode.GRT_BAN, expression);
				mutations[1] = new ORBN_Mutation(MutationMode.GRT_BOR, expression);
				mutations[2] = new ORBN_Mutation(MutationMode.GRT_BXR, expression);
				break;
			case greater_eq:
				mutations[0] = new ORBN_Mutation(MutationMode.GRE_BAN, expression);
				mutations[1] = new ORBN_Mutation(MutationMode.GRE_BOR, expression);
				mutations[2] = new ORBN_Mutation(MutationMode.GRE_BXR, expression);
				break;
			case equal_with:
				mutations[0] = new ORBN_Mutation(MutationMode.EQV_BAN, expression);
				mutations[1] = new ORBN_Mutation(MutationMode.EQV_BOR, expression);
				mutations[2] = new ORBN_Mutation(MutationMode.EQV_BXR, expression);
				break;
			case not_equals:
				mutations[0] = new ORBN_Mutation(MutationMode.NEQ_BAN, expression);
				mutations[1] = new ORBN_Mutation(MutationMode.NEQ_BOR, expression);
				mutations[2] = new ORBN_Mutation(MutationMode.NEQ_BXR, expression);
				break;
			case smaller_tn:
				mutations[0] = new ORBN_Mutation(MutationMode.SMT_BAN, expression);
				mutations[1] = new ORBN_Mutation(MutationMode.SMT_BOR, expression);
				mutations[2] = new ORBN_Mutation(MutationMode.SMT_BXR, expression);
				break;
			case smaller_eq:
				mutations[0] = new ORBN_Mutation(MutationMode.SME_BAN, expression);
				mutations[1] = new ORBN_Mutation(MutationMode.SME_BOR, expression);
				mutations[2] = new ORBN_Mutation(MutationMode.SME_BXR, expression);
				break;
			default: throw new IllegalArgumentException("Invalid operator: " + operator);
			}
			return mutations;
		}
		else return null;
		
	}
	/**
	 * <code>{>, >=, ==, !=, <, <=} ==> {&&, ||}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public ORLN_Mutation[] gen_ORLN(AstRelationExpression expression) throws Exception {
		ORLN_Mutation[] mutations = new ORLN_Mutation[2];
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case greater_tn:
			mutations[0] = new ORLN_Mutation(MutationMode.GRT_LAN, expression);
			mutations[1] = new ORLN_Mutation(MutationMode.GRT_LOR, expression);
			break;
		case greater_eq:
			mutations[0] = new ORLN_Mutation(MutationMode.GRE_LAN, expression);
			mutations[1] = new ORLN_Mutation(MutationMode.GRE_LOR, expression);
			break;
		case equal_with:
			mutations[0] = new ORLN_Mutation(MutationMode.EQV_LAN, expression);
			mutations[1] = new ORLN_Mutation(MutationMode.EQV_LOR, expression);
			break;
		case not_equals:
			mutations[0] = new ORLN_Mutation(MutationMode.NEQ_LAN, expression);
			mutations[1] = new ORLN_Mutation(MutationMode.NEQ_LOR, expression);
			break;
		case smaller_tn:
			mutations[0] = new ORLN_Mutation(MutationMode.SMT_LAN, expression);
			mutations[1] = new ORLN_Mutation(MutationMode.SMT_LOR, expression);
			break;
		case smaller_eq:
			mutations[0] = new ORLN_Mutation(MutationMode.SME_LAN, expression);
			mutations[1] = new ORLN_Mutation(MutationMode.SME_LOR, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return mutations;
	}
	/**
	 * <code>{>, >=, ==, !=, <, <=}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public ORRN_Mutation[] gen_ORRN(AstRelationExpression expression) throws Exception {
		ORRN_Mutation[] mutations = new ORRN_Mutation[5];
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case greater_tn:
			mutations[0] = new ORRN_Mutation(MutationMode.GRT_GRE, expression);
			mutations[1] = new ORRN_Mutation(MutationMode.GRT_EQV, expression);
			mutations[2] = new ORRN_Mutation(MutationMode.GRT_NEQ, expression);
			mutations[3] = new ORRN_Mutation(MutationMode.GRT_SMT, expression);
			mutations[4] = new ORRN_Mutation(MutationMode.GRT_SME, expression);
			break;
		case greater_eq:
			mutations[0] = new ORRN_Mutation(MutationMode.GRE_GRT, expression);
			mutations[1] = new ORRN_Mutation(MutationMode.GRE_EQV, expression);
			mutations[2] = new ORRN_Mutation(MutationMode.GRE_NEQ, expression);
			mutations[3] = new ORRN_Mutation(MutationMode.GRE_SMT, expression);
			mutations[4] = new ORRN_Mutation(MutationMode.GRE_SME, expression);
			break;
		case equal_with:
			mutations[0] = new ORRN_Mutation(MutationMode.EQV_GRE, expression);
			mutations[1] = new ORRN_Mutation(MutationMode.EQV_GRT, expression);
			mutations[2] = new ORRN_Mutation(MutationMode.EQV_NEQ, expression);
			mutations[3] = new ORRN_Mutation(MutationMode.EQV_SMT, expression);
			mutations[4] = new ORRN_Mutation(MutationMode.EQV_SME, expression);
			break;
		case not_equals:
			mutations[0] = new ORRN_Mutation(MutationMode.NEQ_GRE, expression);
			mutations[1] = new ORRN_Mutation(MutationMode.NEQ_EQV, expression);
			mutations[2] = new ORRN_Mutation(MutationMode.NEQ_GRT, expression);
			mutations[3] = new ORRN_Mutation(MutationMode.NEQ_SMT, expression);
			mutations[4] = new ORRN_Mutation(MutationMode.NEQ_SME, expression);
			break;
		case smaller_tn:
			mutations[0] = new ORRN_Mutation(MutationMode.SMT_GRE, expression);
			mutations[1] = new ORRN_Mutation(MutationMode.SMT_EQV, expression);
			mutations[2] = new ORRN_Mutation(MutationMode.SMT_NEQ, expression);
			mutations[3] = new ORRN_Mutation(MutationMode.SMT_GRT, expression);
			mutations[4] = new ORRN_Mutation(MutationMode.SMT_SME, expression);
			break;
		case smaller_eq:
			mutations[0] = new ORRN_Mutation(MutationMode.SME_GRE, expression);
			mutations[1] = new ORRN_Mutation(MutationMode.SME_EQV, expression);
			mutations[2] = new ORRN_Mutation(MutationMode.SME_NEQ, expression);
			mutations[3] = new ORRN_Mutation(MutationMode.SME_SMT, expression);
			mutations[4] = new ORRN_Mutation(MutationMode.SME_GRT, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return mutations;
	}
	/**
	 * <code>{>, >=, ==, !=, <, <=} ==> {>>, <<}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public ORSN_Mutation[] gen_ORSN(AstRelationExpression expression) throws Exception {
		AstExpression E1 = expression.get_loperand(), E2 = expression.get_roperand();
		CType type1 = JC_Classifier.get_value_type(E1.get_value_type()); boolean bit;
		CType type2 = JC_Classifier.get_value_type(E2.get_value_type());
		bit = JC_Classifier.is_boolean_type(type1) || JC_Classifier.is_integer_type(type1);
		if(bit) bit = JC_Classifier.is_boolean_type(type1) || JC_Classifier.is_integer_type(type2);
		
		if(bit) {
			ORSN_Mutation[] mutations = new ORSN_Mutation[2];
			COperator operator = expression.get_operator().get_operator();
			switch(operator) {
			case greater_tn:
				mutations[0] = new ORSN_Mutation(MutationMode.GRT_LSH, expression);
				mutations[1] = new ORSN_Mutation(MutationMode.GRT_RSH, expression);
				break;
			case greater_eq:
				mutations[0] = new ORSN_Mutation(MutationMode.GRE_LSH, expression);
				mutations[1] = new ORSN_Mutation(MutationMode.GRE_RSH, expression);
				break;
			case equal_with:
				mutations[0] = new ORSN_Mutation(MutationMode.EQV_LSH, expression);
				mutations[1] = new ORSN_Mutation(MutationMode.EQV_RSH, expression);
				break;
			case not_equals:
				mutations[0] = new ORSN_Mutation(MutationMode.NEQ_LSH, expression);
				mutations[1] = new ORSN_Mutation(MutationMode.NEQ_RSH, expression);
				break;
			case smaller_tn:
				mutations[0] = new ORSN_Mutation(MutationMode.SMT_LSH, expression);
				mutations[1] = new ORSN_Mutation(MutationMode.SMT_RSH, expression);
				break;
			case smaller_eq:
				mutations[0] = new ORSN_Mutation(MutationMode.SME_LSH, expression);
				mutations[1] = new ORSN_Mutation(MutationMode.SME_RSH, expression);
				break;
			default: throw new IllegalArgumentException("Invalid operator: " + operator);
			}
			return mutations;
		}
		else return null;
	}
	
	/* shifting operator mutation */
	/**
	 * <code>{<<, >>} ==> {+, -, *, /, %}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OSAN_Mutation[] gen_OSAN(AstShiftBinaryExpression expression) throws Exception {
		OSAN_Mutation[] mutations = new OSAN_Mutation[5];
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case left_shift:
			mutations[0] = new OSAN_Mutation(MutationMode.LSH_ADD, expression);
			mutations[1] = new OSAN_Mutation(MutationMode.LSH_SUB, expression);
			mutations[2] = new OSAN_Mutation(MutationMode.LSH_MUL, expression);
			mutations[3] = new OSAN_Mutation(MutationMode.LSH_DIV, expression);
			mutations[4] = new OSAN_Mutation(MutationMode.LSH_MOD, expression);
			break;
		case righ_shift:
			mutations[0] = new OSAN_Mutation(MutationMode.RSH_ADD, expression);
			mutations[1] = new OSAN_Mutation(MutationMode.RSH_SUB, expression);
			mutations[2] = new OSAN_Mutation(MutationMode.RSH_MUL, expression);
			mutations[3] = new OSAN_Mutation(MutationMode.RSH_DIV, expression);
			mutations[4] = new OSAN_Mutation(MutationMode.RSH_MOD, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return mutations;
	}
	/**
	 * <code>{<<, >>} ==> {&, |, ^}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OSBN_Mutation[] gen_OSBN(AstShiftBinaryExpression expression) throws Exception {
		OSBN_Mutation[] mutations = new OSBN_Mutation[3];
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case left_shift:
			mutations[0] = new OSBN_Mutation(MutationMode.LSH_BAN, expression);
			mutations[1] = new OSBN_Mutation(MutationMode.LSH_BOR, expression);
			mutations[2] = new OSBN_Mutation(MutationMode.LSH_BOR, expression);
			break;
		case righ_shift:
			mutations[0] = new OSBN_Mutation(MutationMode.RSH_BAN, expression);
			mutations[1] = new OSBN_Mutation(MutationMode.RSH_BOR, expression);
			mutations[2] = new OSBN_Mutation(MutationMode.RSH_BOR, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return mutations;
	}
	/**
	 * <code>{<<, >>} ==> {&&, ||}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OSLN_Mutation[] gen_OSLN(AstShiftBinaryExpression expression) throws Exception {
		OSLN_Mutation[] mutations = new OSLN_Mutation[2];
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case left_shift:
			mutations[0] = new OSLN_Mutation(MutationMode.LSH_LAN, expression);
			mutations[1] = new OSLN_Mutation(MutationMode.LSH_LOR, expression);
			break;
		case righ_shift:
			mutations[0] = new OSLN_Mutation(MutationMode.RSH_LAN, expression);
			mutations[1] = new OSLN_Mutation(MutationMode.RSH_LOR, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return mutations;
	}
	/**
	 * <code>{<<, >>} ==> {>, >=, ==, !=, <, <=}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OSRN_Mutation[] gen_OSRN(AstShiftBinaryExpression expression) throws Exception {
		OSRN_Mutation[] mutations = new OSRN_Mutation[6];
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case left_shift:
			mutations[0] = new OSRN_Mutation(MutationMode.LSH_GRT, expression);
			mutations[1] = new OSRN_Mutation(MutationMode.LSH_GRE, expression);
			mutations[2] = new OSRN_Mutation(MutationMode.LSH_EQV, expression);
			mutations[3] = new OSRN_Mutation(MutationMode.LSH_NEQ, expression);
			mutations[4] = new OSRN_Mutation(MutationMode.LSH_SMT, expression);
			mutations[5] = new OSRN_Mutation(MutationMode.LSH_SME, expression);
			break;
		case righ_shift:
			mutations[0] = new OSRN_Mutation(MutationMode.RSH_GRT, expression);
			mutations[1] = new OSRN_Mutation(MutationMode.RSH_GRE, expression);
			mutations[2] = new OSRN_Mutation(MutationMode.RSH_EQV, expression);
			mutations[3] = new OSRN_Mutation(MutationMode.RSH_NEQ, expression);
			mutations[4] = new OSRN_Mutation(MutationMode.RSH_SMT, expression);
			mutations[5] = new OSRN_Mutation(MutationMode.RSH_SME, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return mutations;
	}
	/**
	 * <code>{<<, >>}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OSSN_Mutation gen_OSSN(AstShiftBinaryExpression expression) throws Exception {
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case left_shift:
			return new OSSN_Mutation(MutationMode.LSH_RSH, expression);
		case righ_shift:
			return new OSSN_Mutation(MutationMode.RSH_LSH, expression);
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
	}
	
	/* assignment operator mutation */  
	/**
	 * <code>{+=, -=, *=, /=, %=}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OEAA_Mutation[] gen_OEAA(AstAssignExpression expression) throws Exception {
		CType type = JC_Classifier.get_value_type(expression.get_value_type());
		boolean bool = JC_Classifier.is_boolean_type(type);
		boolean integer = JC_Classifier.is_integer_type(type);
		boolean real = JC_Classifier.is_real_type(type);
		
		if(bool || integer) {
			if(real) {
				OEAA_Mutation[] mutations = new OEAA_Mutation[4];
				mutations[0] = new OEAA_Mutation(MutationMode.ASG_ADD, expression);
				mutations[1] = new OEAA_Mutation(MutationMode.ASG_SUB, expression);
				mutations[2] = new OEAA_Mutation(MutationMode.ASG_MUL, expression);
				mutations[3] = new OEAA_Mutation(MutationMode.ASG_DIV, expression);
				return mutations;
			}
			else {
				OEAA_Mutation[] mutations = new OEAA_Mutation[5];
				mutations[0] = new OEAA_Mutation(MutationMode.ASG_ADD, expression);
				mutations[1] = new OEAA_Mutation(MutationMode.ASG_SUB, expression);
				mutations[2] = new OEAA_Mutation(MutationMode.ASG_MUL, expression);
				mutations[3] = new OEAA_Mutation(MutationMode.ASG_DIV, expression);
				mutations[4] = new OEAA_Mutation(MutationMode.ASG_MOD, expression);
				return mutations;
			}
		}
		else return null;
	}
	/**
	 * <code>{&=, |=, ^=}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OEBA_Mutation[] gen_OEBA(AstAssignExpression expression) throws Exception {
		CType type = JC_Classifier.get_value_type(expression.get_value_type());
		boolean bit = JC_Classifier.is_boolean_type(type) || JC_Classifier.is_integer_type(type);
		if(bit) {
			OEBA_Mutation[] mutations = new OEBA_Mutation[3];
			mutations[0] = new OEBA_Mutation(MutationMode.ASG_BAN, expression);
			mutations[1] = new OEBA_Mutation(MutationMode.ASG_BOR, expression);
			mutations[2] = new OEBA_Mutation(MutationMode.ASG_BXR, expression);
			return mutations;
		}
		else return null;
	}
	/**
	 * <code>{<<=, >>=}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OESA_Mutation[] gen_OESA(AstAssignExpression expression) throws Exception {
		CType type = JC_Classifier.get_value_type(expression.get_value_type());
		boolean bit = JC_Classifier.is_boolean_type(type) || JC_Classifier.is_integer_type(type);
		if(bit) {
			OESA_Mutation[] mutations = new OESA_Mutation[2];
			mutations[0] = new OESA_Mutation(MutationMode.ASG_LSH, expression);
			mutations[0] = new OESA_Mutation(MutationMode.ASG_RSH, expression);
			return mutations;
		}
		else return null;
	}
	
	/* arithmetic assignment mutation */
	/**
	 * <code>{+=, -=, *=, /=, %=}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OAAA_Mutation[] gen_OAAA(AstArithAssignExpression expression) throws Exception {
		CType type = JC_Classifier.get_value_type(expression.get_value_type());
		boolean mod = JC_Classifier.is_integer_type(type) || JC_Classifier.is_boolean_type(type);
		boolean ptr = JC_Classifier.is_address_type(type); 
		List<OAAA_Mutation> mutations = new ArrayList<OAAA_Mutation>();
		
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case arith_add_assign:
			if(!ptr)	mutations.add(new OAAA_Mutation(MutationMode.ADD_SUB_A, expression));
			if(!ptr)	mutations.add(new OAAA_Mutation(MutationMode.ADD_MUL_A, expression));
			if(!ptr)	mutations.add(new OAAA_Mutation(MutationMode.ADD_DIV_A, expression));
			if(mod)		mutations.add(new OAAA_Mutation(MutationMode.ADD_MOD_A, expression));
			break;
		case arith_sub_assign:
			if(!ptr)	mutations.add(new OAAA_Mutation(MutationMode.SUB_ADD_A, expression));
			if(!ptr)	mutations.add(new OAAA_Mutation(MutationMode.SUB_MUL_A, expression));
			if(!ptr)	mutations.add(new OAAA_Mutation(MutationMode.SUB_DIV_A, expression));
			if(mod)		mutations.add(new OAAA_Mutation(MutationMode.SUB_MOD_A, expression));
			break;
		case arith_mul_assign:
			if(!ptr)	mutations.add(new OAAA_Mutation(MutationMode.MUL_ADD_A, expression));
			if(!ptr)	mutations.add(new OAAA_Mutation(MutationMode.MUL_SUB_A, expression));
			if(!ptr)	mutations.add(new OAAA_Mutation(MutationMode.MUL_DIV_A, expression));
			if(mod)		mutations.add(new OAAA_Mutation(MutationMode.MUL_MOD_A, expression));
			break;
		case arith_div_assign:
			if(!ptr)	mutations.add(new OAAA_Mutation(MutationMode.DIV_ADD_A, expression));
			if(!ptr)	mutations.add(new OAAA_Mutation(MutationMode.DIV_SUB_A, expression));
			if(!ptr)	mutations.add(new OAAA_Mutation(MutationMode.DIV_MUL_A, expression));
			if(mod)		mutations.add(new OAAA_Mutation(MutationMode.DIV_MOD_A, expression));
			break;
		case arith_mod_assign:
			if(!ptr)	mutations.add(new OAAA_Mutation(MutationMode.MOD_ADD_A, expression));
			if(!ptr)	mutations.add(new OAAA_Mutation(MutationMode.MOD_SUB_A, expression));
			if(!ptr)	mutations.add(new OAAA_Mutation(MutationMode.MOD_MUL_A, expression));
			if(!ptr)	mutations.add(new OAAA_Mutation(MutationMode.MOD_DIV_A, expression));
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		
		if(!mutations.isEmpty()) {
			OAAA_Mutation[] array = new OAAA_Mutation[mutations.size()];
			for(int k = 0; k < array.length; k++) array[k] = mutations.get(k);
			return array;
		}
		else return null;
	}
	/**
	 * <code>{+=, -=, *=, /=, %=} |--> {&=, |=, ^=}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OABA_Mutation[] gen_OABA(AstArithAssignExpression expression) throws Exception {
		CType type = JC_Classifier.get_value_type(expression.get_value_type());
		boolean bit = JC_Classifier.is_integer_type(type) || JC_Classifier.is_boolean_type(type);
		if(bit) {
			OABA_Mutation[] mutations = new OABA_Mutation[3];
			COperator operator = expression.get_operator().get_operator();
			switch(operator) {
			case arith_add_assign:
				mutations[0] = new OABA_Mutation(MutationMode.ADD_BAN_A, expression);
				mutations[1] = new OABA_Mutation(MutationMode.ADD_BOR_A, expression);
				mutations[2] = new OABA_Mutation(MutationMode.ADD_BXR_A, expression);
				break;
			case arith_sub_assign:
				mutations[0] = new OABA_Mutation(MutationMode.SUB_BAN_A, expression);
				mutations[1] = new OABA_Mutation(MutationMode.SUB_BOR_A, expression);
				mutations[2] = new OABA_Mutation(MutationMode.SUB_BXR_A, expression);
				break;
			case arith_mul_assign:
				mutations[0] = new OABA_Mutation(MutationMode.MUL_BAN_A, expression);
				mutations[1] = new OABA_Mutation(MutationMode.MUL_BOR_A, expression);
				mutations[2] = new OABA_Mutation(MutationMode.MUL_BXR_A, expression);
				break;
			case arith_div_assign:
				mutations[0] = new OABA_Mutation(MutationMode.DIV_BAN_A, expression);
				mutations[1] = new OABA_Mutation(MutationMode.DIV_BOR_A, expression);
				mutations[2] = new OABA_Mutation(MutationMode.DIV_BXR_A, expression);
				break;
			case arith_mod_assign:
				mutations[0] = new OABA_Mutation(MutationMode.MOD_BAN_A, expression);
				mutations[1] = new OABA_Mutation(MutationMode.MOD_BOR_A, expression);
				mutations[2] = new OABA_Mutation(MutationMode.MOD_BXR_A, expression);
				break;
			default: throw new IllegalArgumentException("Invalid operator: " + operator);
			}
			return mutations;
		}
		else return null;
	}
	/**
	 * <code>{+=, -=, *=, /=, %=} |--> {<<=, >>=}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OASA_Mutation[] gen_OASA(AstArithAssignExpression expression) throws Exception {
		CType type = JC_Classifier.get_value_type(expression.get_value_type());
		boolean bit = JC_Classifier.is_integer_type(type) || JC_Classifier.is_boolean_type(type);
		if(bit) {
			OASA_Mutation[] mutations = new OASA_Mutation[2];
			COperator operator = expression.get_operator().get_operator();
			switch(operator) {
			case arith_add_assign:
				mutations[0] = new OASA_Mutation(MutationMode.ADD_LSH_A, expression);
				mutations[1] = new OASA_Mutation(MutationMode.ADD_RSH_A, expression);
				break;
			case arith_sub_assign:
				mutations[0] = new OASA_Mutation(MutationMode.SUB_LSH_A, expression);
				mutations[1] = new OASA_Mutation(MutationMode.SUB_RSH_A, expression);
				break;
			case arith_mul_assign:
				mutations[0] = new OASA_Mutation(MutationMode.MUL_LSH_A, expression);
				mutations[1] = new OASA_Mutation(MutationMode.MUL_RSH_A, expression);
				break;
			case arith_div_assign:
				mutations[0] = new OASA_Mutation(MutationMode.DIV_LSH_A, expression);
				mutations[1] = new OASA_Mutation(MutationMode.DIV_RSH_A, expression);
				break;
			case arith_mod_assign:
				mutations[0] = new OASA_Mutation(MutationMode.MOD_LSH_A, expression);
				mutations[1] = new OASA_Mutation(MutationMode.MOD_RSH_A, expression);
				break;
			default: throw new IllegalArgumentException("Invalid operator: " + operator);
			}
			return mutations;
		}
		else return null;
	}
	
	/* bitwise assignment mutation */
	/**
	 *  <code>{&=, |=, ^=} |==> {+=, -=, *=, /=, %=}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OBAA_Mutation[] gen_OBAA(AstBitwiseAssignExpression expression) throws Exception {
		OBAA_Mutation[] mutations = new OBAA_Mutation[5];
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case bit_and_assign:
			mutations[0] = new OBAA_Mutation(MutationMode.BAN_ADD_A, expression);
			mutations[1] = new OBAA_Mutation(MutationMode.BAN_SUB_A, expression);
			mutations[2] = new OBAA_Mutation(MutationMode.BAN_MUL_A, expression);
			mutations[3] = new OBAA_Mutation(MutationMode.BAN_DIV_A, expression);
			mutations[4] = new OBAA_Mutation(MutationMode.BAN_MOD_A, expression);
			break;
		case bit_or_assign:
			mutations[0] = new OBAA_Mutation(MutationMode.BOR_ADD_A, expression);
			mutations[1] = new OBAA_Mutation(MutationMode.BOR_SUB_A, expression);
			mutations[2] = new OBAA_Mutation(MutationMode.BOR_MUL_A, expression);
			mutations[3] = new OBAA_Mutation(MutationMode.BOR_DIV_A, expression);
			mutations[4] = new OBAA_Mutation(MutationMode.BOR_MOD_A, expression);
			break;
		case bit_xor_assign:
			mutations[0] = new OBAA_Mutation(MutationMode.BXR_ADD_A, expression);
			mutations[1] = new OBAA_Mutation(MutationMode.BXR_SUB_A, expression);
			mutations[2] = new OBAA_Mutation(MutationMode.BXR_MUL_A, expression);
			mutations[3] = new OBAA_Mutation(MutationMode.BXR_DIV_A, expression);
			mutations[4] = new OBAA_Mutation(MutationMode.BXR_MOD_A, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return mutations;
	}
	/**
	 * <code>{&=, |=, ^=} |==> {&=, |=, ^=}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OBBA_Mutation[] gen_OBBA(AstBitwiseAssignExpression expression) throws Exception {
		OBBA_Mutation[] mutations = new OBBA_Mutation[2];
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case bit_and_assign:
			mutations[0] = new OBBA_Mutation(MutationMode.BAN_BOR_A, expression);
			mutations[1] = new OBBA_Mutation(MutationMode.BAN_BXR_A, expression);
			break;
		case bit_or_assign:
			mutations[0] = new OBBA_Mutation(MutationMode.BOR_BAN_A, expression);
			mutations[1] = new OBBA_Mutation(MutationMode.BOR_BXR_A, expression);
			break;
		case bit_xor_assign:
			mutations[0] = new OBBA_Mutation(MutationMode.BXR_BOR_A, expression);
			mutations[1] = new OBBA_Mutation(MutationMode.BXR_BAN_A, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return mutations;
	}
	/**
	 *  <code>{&=, |=, ^=} |==> {<<=, >>=}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OBSA_Mutation[] gen_OBSA(AstBitwiseAssignExpression expression) throws Exception {
		OBSA_Mutation[] mutations = new OBSA_Mutation[2];
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case bit_and_assign:
			mutations[0] = new OBSA_Mutation(MutationMode.BAN_LSH_A, expression);
			mutations[1] = new OBSA_Mutation(MutationMode.BAN_RSH_A, expression);
			break;
		case bit_or_assign:
			mutations[0] = new OBSA_Mutation(MutationMode.BOR_LSH_A, expression);
			mutations[1] = new OBSA_Mutation(MutationMode.BOR_RSH_A, expression);
			break;
		case bit_xor_assign:
			mutations[0] = new OBSA_Mutation(MutationMode.BXR_LSH_A, expression);
			mutations[1] = new OBSA_Mutation(MutationMode.BXR_RSH_A, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return mutations;
	}
	
	/* shifting assignment mutation */
	/**
	 * <code>{<<=, >>=} |--> {+=, -=, *=, /=, %=}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OSAA_Mutation[] gen_OSAA(AstShiftAssignExpression expression) throws Exception {
		OSAA_Mutation[] mutations = new OSAA_Mutation[5];
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case left_shift_assign:
			mutations[0] = new OSAA_Mutation(MutationMode.LSH_ADD_A, expression);
			mutations[1] = new OSAA_Mutation(MutationMode.LSH_SUB_A, expression);
			mutations[2] = new OSAA_Mutation(MutationMode.LSH_MUL_A, expression);
			mutations[3] = new OSAA_Mutation(MutationMode.LSH_DIV_A, expression);
			mutations[4] = new OSAA_Mutation(MutationMode.LSH_MOD_A, expression);
			break;
		case righ_shift_assign:
			mutations[0] = new OSAA_Mutation(MutationMode.RSH_ADD_A, expression);
			mutations[1] = new OSAA_Mutation(MutationMode.RSH_SUB_A, expression);
			mutations[2] = new OSAA_Mutation(MutationMode.RSH_MUL_A, expression);
			mutations[3] = new OSAA_Mutation(MutationMode.RSH_DIV_A, expression);
			mutations[4] = new OSAA_Mutation(MutationMode.RSH_MOD_A, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return mutations;
	}
	/**
	 * <code>{<<=, >>=} |--> {&=, |=, ^=}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OSBA_Mutation[] gen_OSBA(AstShiftAssignExpression expression) throws Exception {
		OSBA_Mutation[] mutations = new OSBA_Mutation[3];
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case left_shift_assign:
			mutations[0] = new OSBA_Mutation(MutationMode.LSH_BAN_A, expression);
			mutations[1] = new OSBA_Mutation(MutationMode.LSH_BOR_A, expression);
			mutations[2] = new OSBA_Mutation(MutationMode.LSH_BXR_A, expression);
			break;
		case righ_shift_assign:
			mutations[0] = new OSBA_Mutation(MutationMode.RSH_BAN_A, expression);
			mutations[1] = new OSBA_Mutation(MutationMode.RSH_BOR_A, expression);
			mutations[2] = new OSBA_Mutation(MutationMode.RSH_BXR_A, expression);
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return mutations;
	}
	/**
	 *  <code>{<<=, >>=}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public OSSA_Mutation gen_OSSA(AstShiftAssignExpression expression) throws Exception {
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case left_shift_assign:
			return new OSSA_Mutation(MutationMode.LSH_RSH_A, expression);
		case righ_shift_assign:
			return new OSSA_Mutation(MutationMode.RSH_LSH_A, expression);
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
	}
	
	/* negative deletion mutation */
	/**
	 * <code> (-,~,!)e ==> e </code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public ONDU_Mutation gen_ONDU(AstUnaryExpression expression) throws Exception {
		if(expression instanceof AstArithUnaryExpression)
			return new ONDU_Mutation(MutationMode.ANG_DELETE, expression);
		else if(expression instanceof AstBitwiseUnaryExpression)
			return new ONDU_Mutation(MutationMode.BNG_DELETE, expression);
		else if(expression instanceof AstLogicUnaryExpression)
			return new ONDU_Mutation(MutationMode.LNG_DELETE, expression);
		else throw new IllegalArgumentException("Invalid expression: \"" + 
			expression.get_location().read() + "\"");
	}
	
	/* value insertion mutation */
	/**
	 * <code>x |==> abs(x)</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public VABS_Mutation gen_VABS(AstExpression expression) throws Exception {
		return new VABS_Mutation(expression);
	}
	/**
	 * <code>expr |==> {true; false}</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public VBCR_Mutation[] gen_VBCR(AstExpression expression) throws Exception {
		VBCR_Mutation[] mutations = new VBCR_Mutation[2];
		mutations[0] = new VBCR_Mutation(MutationMode.MUT_TRUE, expression);
		mutations[1] = new VBCR_Mutation(MutationMode.MUT_FALSE,expression);
		return mutations;
	}
	/**
	 * <code>x ==> trap_on_xxx(x)</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public VDTR_Mutation[] gen_VDTR(AstExpression expression) throws Exception {
		VDTR_Mutation[] mutations = new VDTR_Mutation[3];
		mutations[0] = new VDTR_Mutation(MutationMode.TRAP_ON_POS, expression);
		mutations[1] = new VDTR_Mutation(MutationMode.TRAP_ON_ZRO, expression);
		/* undecidable unsigned reference to be prevented */
		try{
			mutations[2] = new VDTR_Mutation(MutationMode.TRAP_ON_NEG, expression);
		}catch(Exception ex){
			mutations[2] = null;
		}
		
		return mutations;
	}
	/**
	 * <code>x ==> succ(x) | pred(x)</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public VTWD_Mutation[] gen_VTWD(AstExpression expression) throws Exception {
		VTWD_Mutation[] mutations = new VTWD_Mutation[2];
		mutations[0] = new VTWD_Mutation(MutationMode.SUCC_VAL, expression);
		mutations[1] = new VTWD_Mutation(MutationMode.PRED_VAL, expression);
		return mutations;
	}
	
	/* reference mutation */
	/**
	 * <code>constant |==> constant'</code>
	 * @param source
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public CCCR_Mutation gen_CCCR(AstConstant source, AstConstant target) throws Exception {
		if(!target.get_location().read().trim().equals(source.get_location().read().trim()))
			return new CCCR_Mutation(source, target);
		else return null;
	}
	/**
	 * <code>constant |==> {0, +1, -1, -c, c + 1, c - 1}</code>
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public CRCR_Mutation[] gen_CRCR(AstConstant source) throws Exception {
		CRCR_Mutation[] mutations = new CRCR_Mutation[6];
		
		/* constant |==> 0 */
		try {
			mutations[0] = new CRCR_Mutation(MutationMode.CST_TOT_ZRO, source);
		} catch(Exception e1) {
			mutations[0] = null;
		}
		/* constant |==> 0 */
		try {
			mutations[1] = new CRCR_Mutation(MutationMode.CST_POS_ONE, source);
		} catch(Exception e1) {
			mutations[1] = null;
		}
		/* constant |==> 0 */
		try {
			mutations[2] = new CRCR_Mutation(MutationMode.CST_NEG_ONE, source);
		} catch(Exception e1) {
			mutations[2] = null;
		}
		
		/* constant |==> -constant */
		try{
			mutations[3] = new CRCR_Mutation(MutationMode.CST_NEG_CST, source);
		} catch(Exception e1) {
			mutations[3] = null;
		}
		
		/* constant |==> +inf | -inf */
		mutations[4] = new CRCR_Mutation(MutationMode.CST_INC_ONE, source);
		mutations[5] = new CRCR_Mutation(MutationMode.CST_DEC_ONE, source);
		
		return mutations;	/* return */
	}
	/**
	 * <code>constant |==> reference (scalar)</code>
	 * @param source
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public CCSR_Mutation gen_CCSR(AstConstant source, AstName target) throws Exception {
		return new CCSR_Mutation(source, target);
	}
	/**
	 * <code>array_identifier |--> array_identifier'</code>
	 * @param source
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public VARR_Mutation gen_VARR(AstExpression source, AstName target) throws Exception {
		if(!target.get_name().trim().equals(source.get_location().read().trim()))
			return new VARR_Mutation(source, target);
		else return null;
	}
	/**
	 * <code>ptr |--> ptr'</code>
	 * @param source
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public VPRR_Mutation gen_VPRR(AstExpression source, AstName target) throws Exception {
		if(!target.get_name().trim().equals(source.get_location().read().trim()))
			return new VPRR_Mutation(source, target);
		else return null;
	}
	/**
	 * scalar reference: <code>var ==> var'</code>
	 * @param source
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public VSRR_Mutation gen_VSRR(AstExpression source, AstName target) throws Exception {
		if(!target.get_name().trim().equals(source.get_location().read().trim()))
			return new VSRR_Mutation(source, target);
		else return null;
	}
	/**
	 * <code>x.field1 ==> x.field2</code>
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public VSFR_Mutation[] gen_VFRR(AstFieldExpression expression) throws Exception {
		/*  */
		CType type = expression.get_body().get_value_type();
		type = JC_Classifier.get_value_type(type); CFieldBody fields;
		CPunctuator op = expression.get_operator().get_punctuator();
		
		if(op == CPunctuator.arrow) {
			if(type instanceof CPointerType) 
				type = ((CPointerType) type).get_pointed_type();
			else type = ((CArrayType) type).get_element_type();
			type = JC_Classifier.get_value_type(type);
		}
		
		VSFR_Mutation[] mutations = null;
		if(type instanceof CStructType) {
			fields = ((CStructType) type).get_fields();
			Set<String> field_names = new HashSet<String>();
			String field1 = expression.get_field().get_name();
			for(int i = 0; i < fields.size(); i++) {
				String field2 = fields.get_field(i).get_name();
				if(!field2.equals(field1)) field_names.add(field2);
			}
			
			if(!field_names.isEmpty()) {
				mutations = new VSFR_Mutation[field_names.size()];
				int k = 0;
				for(String name : field_names) {
					mutations[k++] = new VSFR_Mutation(expression, name);
				}
			}
		}
		
		return mutations;
	}
	/**
	 * <code>st ==> st'</code>
	 * @param source
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public VTRR_Mutation gen_VTRR(AstExpression source, AstName target) throws Exception {
		if(!target.get_name().trim().equals(source.get_location().read().trim()))
			return new VTRR_Mutation(source, target);
		else return null;
	}
	
}
