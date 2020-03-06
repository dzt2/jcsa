package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;

/**
 * <code>break; |--> continue;</code>
 * @author yukimula
 */
public class SBRC_Mutation extends SingleMutation {

	protected SBRC_Mutation(AstBreakStatement location) throws Exception {
		super(MutOperator.SBRC, MutationMode.REP_BY_CONTINUE, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		switch(mode) {
		case REP_BY_CONTINUE:
			return location instanceof AstBreakStatement;
		default: return false;
		}
	}

}
