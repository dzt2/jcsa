package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;

/**
 * <code>logexpr |==> !logeexpr</code>
 * @author yukimula
 */
public class OLNG_Mutation extends SingleMutation {

	protected OLNG_Mutation(AstExpression location) throws Exception {
		super(MutOperator.OLNG, MutationMode.NEG_BOOLEAN, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		switch(mode) {
		case NEG_BOOLEAN:
			if(location instanceof AstRelationExpression
				|| location instanceof AstLogicBinaryExpression
				|| location instanceof AstLogicUnaryExpression)
				return true;
			else return false;
		default: return false;
		}
	}

}
