package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;

public class OEAA_Mutation extends SingleMutation {

	protected OEAA_Mutation(MutationMode mode, AstAssignExpression location) throws Exception {
		super(MutOperator.OEAA, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		switch(mode) {
		case ASG_ADD:
		case ASG_SUB:
		case ASG_MUL:
		case ASG_DIV:
		case ASG_MOD: return true;
		default: return false;
		}
	}

}
