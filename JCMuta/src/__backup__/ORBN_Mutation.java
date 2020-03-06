package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class ORBN_Mutation extends SingleMutation {

	protected ORBN_Mutation(MutationMode mode, AstRelationExpression location) throws Exception {
		super(MutOperator.ORBN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstRelationExpression expr = (AstRelationExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case GRT_BAN: case GRT_BOR: case GRT_BXR:
			return operator == COperator.greater_tn;
		case GRE_BAN: case GRE_BOR: case GRE_BXR:
			return operator == COperator.greater_eq;
		case EQV_BAN: case EQV_BOR: case EQV_BXR:
			return operator == COperator.equal_with;
		case NEQ_BAN: case NEQ_BOR: case NEQ_BXR:
			return operator == COperator.not_equals;
		case SMT_BAN: case SMT_BOR: case SMT_BXR:
			return operator == COperator.smaller_tn;
		case SME_BAN: case SME_BOR: case SME_BXR:
			return operator == COperator.smaller_eq;
		default: return false;
		}
	}

}
