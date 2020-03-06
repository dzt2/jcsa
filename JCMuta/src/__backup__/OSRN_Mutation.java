package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OSRN_Mutation extends SingleMutation {

	protected OSRN_Mutation(MutationMode mode, AstShiftBinaryExpression location) throws Exception {
		super(MutOperator.OSRN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstShiftBinaryExpression expr = (AstShiftBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case LSH_GRT: case LSH_GRE: case LSH_EQV: case LSH_NEQ: case LSH_SMT: case LSH_SME:
			return operator == COperator.left_shift;
		case RSH_GRT: case RSH_GRE: case RSH_EQV: case RSH_NEQ: case RSH_SMT: case RSH_SME:
			return operator == COperator.righ_shift;
		default: return false;
		}
	}

}
