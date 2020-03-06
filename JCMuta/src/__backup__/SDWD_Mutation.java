package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;

/**
 * <code>do...while |--> while</code>
 * @author yukimula
 *
 */
public class SDWD_Mutation extends SingleMutation {

	protected SDWD_Mutation(AstDoWhileStatement location) throws Exception {
		super(MutOperator.SDWD, MutationMode.REP_BY_WHILE, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		switch(mode) {
		case REP_BY_WHILE:
			return location instanceof AstDoWhileStatement;
		default: return false;
		}
	}

}
