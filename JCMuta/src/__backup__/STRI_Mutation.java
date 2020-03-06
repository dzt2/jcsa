package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

/**
 * <code>
 * 	trap_on_true(predicate)<br>	
 * 	trap_on_false(predicate)<br>
 * </code>
 * @author yukimula
 *
 */
public class STRI_Mutation extends SingleMutation {

	protected STRI_Mutation(MutationMode mode, AstStatement location) throws Exception {
		super(MutOperator.STRI, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		switch(mode) {
		case TRAP_ON_TRUE:
		case TRAP_ON_FALSE:
			if(location instanceof AstIfStatement
				|| location instanceof AstForStatement
				|| location instanceof AstWhileStatement
				|| location instanceof AstDoWhileStatement)
				return true;
			else return false;
		default: return false;
		}
	}

}
