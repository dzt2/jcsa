package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

/**
 * <code>stmt |--> {}</code>
 * @author yukimula
 *
 */
public class SSDL_Mutation extends SingleMutation {

	protected SSDL_Mutation(AstStatement location) throws Exception {
		super(MutOperator.SSDL, MutationMode.DELETE_STATEMENT, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		switch(mode) {
		case DELETE_STATEMENT:
			return location instanceof AstStatement;
		default: return false;
		}
	}

}
