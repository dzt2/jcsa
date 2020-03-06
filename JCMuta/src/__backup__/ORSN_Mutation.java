package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class ORSN_Mutation extends SingleMutation {

	protected ORSN_Mutation(MutationMode mode, AstRelationExpression location) throws Exception {
		super(MutOperator.ORSN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstRelationExpression expr = (AstRelationExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case GRT_LSH: case GRT_RSH:
			return operator == COperator.greater_tn;
		case GRE_LSH: case GRE_RSH:
			return operator == COperator.greater_eq;
		case EQV_LSH: case EQV_RSH:
			return operator == COperator.equal_with;
		case NEQ_LSH: case NEQ_RSH:
			return operator == COperator.not_equals;
		case SMT_LSH: case SMT_RSH:
			return operator == COperator.smaller_tn;
		case SME_LSH: case SME_RSH:
			return operator == COperator.smaller_eq;
		default: return false;
		}
	}

}
