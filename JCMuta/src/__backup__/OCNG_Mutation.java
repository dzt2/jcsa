package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

/**
 * if | while | do...while | for statement predicate
 * @author yukimula
 *
 */
public class OCNG_Mutation extends SingleMutation {

	protected OCNG_Mutation(AstStatement location) throws Exception {
		super(MutOperator.OCNG, MutationMode.NEG_BOOLEAN, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		switch(mode) {
		case NEG_BOOLEAN:
			if(location instanceof AstIfStatement
				|| location instanceof AstWhileStatement
				|| location instanceof AstDoWhileStatement
				|| location instanceof AstForStatement)
				return true;
			else return false;
		default: return false;
		}
	}

}
