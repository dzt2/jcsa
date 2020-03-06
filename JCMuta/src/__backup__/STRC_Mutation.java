package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>
 * 	trap_on_true(condition)<br>	
 * 	trap_on_false(condition)<br>
 * </code>
 * @author yukimula
 */
public class STRC_Mutation extends SingleMutation {

	protected STRC_Mutation(MutationMode mode, AstExpression location) throws Exception {
		super(MutOperator.STRC, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		switch(mode) {
		case TRAP_ON_TRUE:
		case TRAP_ON_FALSE:
			return true;
		default: return false;
		}
	}

}
