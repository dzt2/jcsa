package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OSBA_Mutation extends SingleMutation {

	protected OSBA_Mutation(MutationMode mode, AstShiftAssignExpression location) throws Exception {
		super(MutOperator.OSBA, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstShiftAssignExpression expr = (AstShiftAssignExpression) location;
		COperator operator = expr.get_operator().get_operator();
		switch(mode) {
		case LSH_BAN_A:
		case LSH_BOR_A:
		case LSH_BXR_A:
			return operator == COperator.left_shift_assign;
		case RSH_BAN_A:
		case RSH_BOR_A:
		case RSH_BXR_A:
			return operator == COperator.righ_shift_assign;
		default:
			return false;
		}
	}

}
