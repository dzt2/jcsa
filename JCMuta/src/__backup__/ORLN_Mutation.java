package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class ORLN_Mutation extends SingleMutation {

	protected ORLN_Mutation(MutationMode mode, AstRelationExpression location) throws Exception {
		super(MutOperator.ORLN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstRelationExpression expr = (AstRelationExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case GRT_LAN: case GRT_LOR:
			return operator == COperator.greater_tn;
		case GRE_LAN: case GRE_LOR:
			return operator == COperator.greater_eq;
		case EQV_LAN: case EQV_LOR:
			return operator == COperator.equal_with;
		case NEQ_LAN: case NEQ_LOR:
			return operator == COperator.not_equals;
		case SMT_LAN: case SMT_LOR:
			return operator == COperator.smaller_tn;
		case SME_LAN: case SME_LOR:
			return operator == COperator.smaller_eq;
		default: return false;
		}
	}

}
