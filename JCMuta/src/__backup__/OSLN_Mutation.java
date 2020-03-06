package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OSLN_Mutation extends SingleMutation {

	protected OSLN_Mutation(MutationMode mode, AstShiftBinaryExpression location) throws Exception {
		super(MutOperator.OSLN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstShiftBinaryExpression expr = (AstShiftBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case LSH_LAN: case LSH_LOR:
			return operator == COperator.left_shift;
		case RSH_LAN: case RSH_LOR:
			return operator == COperator.righ_shift;
		default: return false;
		}
	}

}
