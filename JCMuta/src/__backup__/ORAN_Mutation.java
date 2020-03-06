package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class ORAN_Mutation extends SingleMutation {

	protected ORAN_Mutation(MutationMode mode, AstRelationExpression location) throws Exception {
		super(MutOperator.ORAN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstRelationExpression expr = (AstRelationExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case GRT_ADD: case GRT_SUB: case GRT_MUL: case GRT_DIV: case GRT_MOD:
			return operator == COperator.greater_tn;
		case GRE_ADD: case GRE_SUB: case GRE_MUL: case GRE_DIV: case GRE_MOD:
			return operator == COperator.greater_eq;
		case EQV_ADD: case EQV_SUB: case EQV_MUL: case EQV_DIV: case EQV_MOD:
			return operator == COperator.equal_with;
		case NEQ_ADD: case NEQ_SUB: case NEQ_MUL: case NEQ_DIV: case NEQ_MOD:
			return operator == COperator.not_equals;
		case SMT_ADD: case SMT_SUB: case SMT_MUL: case SMT_DIV: case SMT_MOD:
			return operator == COperator.smaller_tn;
		case SME_ADD: case SME_SUB: case SME_MUL: case SME_DIV: case SME_MOD:
			return operator == COperator.smaller_eq;
		default: return false;
		}
	}

}
