package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class ORRN_Mutation extends SingleMutation {

	protected ORRN_Mutation(MutationMode mode, AstRelationExpression location) throws Exception {
		super(MutOperator.ORRN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstRelationExpression expr = (AstRelationExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case GRT_GRE: case GRT_EQV: case GRT_NEQ: case GRT_SMT: case GRT_SME:
			return operator == COperator.greater_tn;
		case GRE_GRT: case GRE_EQV: case GRE_NEQ: case GRE_SMT: case GRE_SME:
			return operator == COperator.greater_eq;
		case EQV_GRT: case EQV_GRE: case EQV_NEQ: case EQV_SMT: case EQV_SME:
			return operator == COperator.equal_with;
		case NEQ_GRT: case NEQ_GRE: case NEQ_EQV: case NEQ_SMT: case NEQ_SME:
			return operator == COperator.not_equals;
		case SMT_GRT: case SMT_GRE: case SMT_EQV: case SMT_NEQ: case SMT_SME:
			return operator == COperator.smaller_tn;
		case SME_GRT: case SME_GRE: case SME_EQV: case SME_NEQ: case SME_SMT: 
			return operator == COperator.smaller_eq;
		default: return false;
		}
	}

}
