package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OSAN_Mutation extends SingleMutation {

	protected OSAN_Mutation(MutationMode mode, AstShiftBinaryExpression location) throws Exception {
		super(MutOperator.OSAN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstShiftBinaryExpression expr = (AstShiftBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case LSH_ADD: case LSH_SUB: case LSH_MUL: case LSH_DIV: case LSH_MOD:
			return operator == COperator.left_shift;
		case RSH_ADD: case RSH_SUB: case RSH_MUL: case RSH_DIV: case RSH_MOD:
			return operator == COperator.righ_shift;
		default: return false;
		}
	}

}
