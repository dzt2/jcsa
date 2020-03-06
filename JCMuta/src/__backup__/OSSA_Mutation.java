package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OSSA_Mutation extends SingleMutation {

	protected OSSA_Mutation(MutationMode mode, AstShiftAssignExpression location) throws Exception {
		super(MutOperator.OSSA, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstShiftAssignExpression expr = (AstShiftAssignExpression) location;
		COperator operator = expr.get_operator().get_operator();
		switch(mode) {
		case LSH_RSH_A:
			return operator == COperator.left_shift_assign;
		case RSH_LSH_A:
			return operator == COperator.righ_shift_assign;
		default:
			return false;
		}
	}

}
