package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;

/**
 * <code>bit_expr |==> ~(bit_expr)</code>
 * @author yukimula
 */
public class OBNG_Mutation extends SingleMutation {

	protected OBNG_Mutation(AstExpression location) throws Exception {
		super(MutOperator.OBNG, MutationMode.NEG_BITWISE, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		switch(mode) {
		case NEG_BITWISE:
			return location instanceof AstBitwiseBinaryExpression;
		default: return false;
		}
	}

}
