package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

/**
 * <code>while |--> do...while</code>
 * @author yukimula
 *
 */
public class SWDD_Mutation extends SingleMutation {

	protected SWDD_Mutation(AstWhileStatement location) throws Exception {
		super(MutOperator.SWDD, MutationMode.REP_BY_DO_WHILE, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		switch(mode) {
		case REP_BY_DO_WHILE:
			return location instanceof AstWhileStatement;
		default: return false;
		}
	}

}
