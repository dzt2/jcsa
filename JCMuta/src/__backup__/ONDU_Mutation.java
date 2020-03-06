package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class ONDU_Mutation extends SingleMutation {

	protected ONDU_Mutation(MutationMode mode, AstUnaryExpression location) throws Exception {
		super(MutOperator.ONDU, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstUnaryExpression expr = (AstUnaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		switch(mode) {
		case ANG_DELETE:
			return operator == COperator.negative;
		case BNG_DELETE:
			return operator == COperator.bit_not;
		case LNG_DELETE:
			return operator == COperator.logic_not;
		default:
			return false;
		}
	}

}
