package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

/**
 * <code>stmt |--> {trap_on_statement(); stmt}</code>
 * @author yukimula
 */
public class STRP_Mutation extends SingleMutation {

	protected STRP_Mutation(AstStatement location) throws Exception {
		super(MutOperator.STRP, MutationMode.TRAP_ON_STMT, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		switch(mode) {
		case TRAP_ON_STMT:
			return location instanceof AstStatement;
		default: return false;
		}
	}

}
