package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OSAA_Mutation extends SingleMutation {

	protected OSAA_Mutation(MutationMode mode, 
			AstShiftAssignExpression location) throws Exception {
		super(MutOperator.OSAA, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstShiftAssignExpression expr = (AstShiftAssignExpression) location;
		COperator operator = expr.get_operator().get_operator();
		switch(mode) {
		case LSH_ADD_A:
		case LSH_SUB_A:
		case LSH_MUL_A:
		case LSH_DIV_A:
		case LSH_MOD_A:
			return operator == COperator.left_shift_assign;
		case RSH_ADD_A:
		case RSH_SUB_A:
		case RSH_MUL_A:
		case RSH_DIV_A:
		case RSH_MOD_A:
			return operator == COperator.righ_shift_assign;
		default:
			return false;
		}
	}

}
