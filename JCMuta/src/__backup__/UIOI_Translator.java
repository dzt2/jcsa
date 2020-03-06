package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>x |==> x++ | ++x | x-- | --x</code>
 * @author yukimula
 *
 */
public class UIOI_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof UIOI_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		AstExpression expr = (AstExpression) mutation.get_location();
		switch(mutation.get_mode()) {
		case POST_INC_INS:	buffer.append("( (").append(expr.get_location().read()).append(")++ )"); break;
		case PREV_INC_INS:	buffer.append("( ++(").append(expr.get_location().read()).append(") )"); break;
		case POST_DEC_INS:	buffer.append("( (").append(expr.get_location().read()).append(")-- )"); break;
		case PREV_DEC_INS:	buffer.append("( --(").append(expr.get_location().read()).append(") )"); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
	}

}
