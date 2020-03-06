package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;

public class OESA_Mutation extends SingleMutation {

	protected OESA_Mutation(MutationMode mode, AstAssignExpression location) throws Exception {
		super(MutOperator.OESA, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		switch(mode) {
		case ASG_LSH: case ASG_RSH: return true;
		default: return false;
		}
	}

}
