package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OSSN_Mutation extends SingleMutation {

	protected OSSN_Mutation(MutationMode mode, AstShiftBinaryExpression location) throws Exception {
		super(MutOperator.OSSN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstShiftBinaryExpression expr = (AstShiftBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case LSH_RSH:
			return operator == COperator.left_shift;
		case RSH_LSH:
			return operator == COperator.righ_shift;
		default: return false;
		}
	}

}
