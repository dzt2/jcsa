package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;

/**
 * <code>continue; |--> break;</code>
 * @author yukimula
 *
 */
public class SCRB_Mutation extends SingleMutation {

	protected SCRB_Mutation(AstContinueStatement location) throws Exception {
		super(MutOperator.SCRB, MutationMode.REP_BY_BREAK, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		switch(mode){
		case REP_BY_BREAK:
			return location instanceof AstContinueStatement;
		default: return false;
		}
	}

}
