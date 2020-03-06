package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OSBN_Mutation extends SingleMutation {

	protected OSBN_Mutation(MutationMode mode, 
			AstShiftBinaryExpression location) throws Exception {
		super(MutOperator.OSBN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstShiftBinaryExpression expr = (AstShiftBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case LSH_BAN: case LSH_BOR: case LSH_BXR:
			return operator == COperator.left_shift;
		case RSH_BAN: case RSH_BOR: case RSH_BXR:
			return operator == COperator.righ_shift;
		default: return false;
		}
	}

}
