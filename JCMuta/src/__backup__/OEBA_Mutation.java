package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;

public class OEBA_Mutation extends SingleMutation {

	protected OEBA_Mutation(MutationMode mode, AstAssignExpression location) throws Exception {
		super(MutOperator.OEBA, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		switch(mode) {
		case ASG_BAN: case ASG_BOR: case ASG_BXR: return true;
		default: return false;
		}
	}

}
