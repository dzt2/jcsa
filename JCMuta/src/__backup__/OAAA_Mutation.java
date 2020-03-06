package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OAAA_Mutation extends SingleMutation {

	protected OAAA_Mutation(MutationMode mode, AstArithAssignExpression location) throws Exception {
		super(MutOperator.OAAA, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstArithAssignExpression expr = (AstArithAssignExpression) location;
		COperator operator = expr.get_operator().get_operator();
		switch(mode) {
		case ADD_SUB_A: case ADD_MUL_A: case ADD_DIV_A: case ADD_MOD_A:
			return operator == COperator.arith_add_assign;
		case SUB_ADD_A: case SUB_MUL_A: case SUB_DIV_A: case SUB_MOD_A:
			return operator == COperator.arith_sub_assign;
		case MUL_ADD_A: case MUL_SUB_A: case MUL_DIV_A: case MUL_MOD_A:
			return operator == COperator.arith_mul_assign;
		case DIV_ADD_A: case DIV_SUB_A: case DIV_MUL_A: case DIV_MOD_A:
			return operator == COperator.arith_div_assign;
		case MOD_ADD_A: case MOD_SUB_A: case MOD_MUL_A: case MOD_DIV_A: 
			return operator == COperator.arith_mod_assign;
		default: return false;
		}
	}

}
