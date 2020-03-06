package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;

/**
 * <code>
 * x-- |==> x++ | --x<br>
 * --x |==> ++x | x--<br>
 * </code>
 * @author yukimula
 */
public class OMMO_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OMMO_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		AstNode loc = mutation.get_location();
		
		/* x-- */
		if(loc instanceof AstIncrePostfixExpression) {
			AstIncrePostfixExpression expr = 
					(AstIncrePostfixExpression) loc;
			AstExpression sub = expr.get_operand();
			switch(mutation.get_mode()) {
			/* x-- |==> --x */
			case POST_PREV_DEC:	buffer.append("( --(").append(sub.get_location().read()).append(") )"); break;
			/* x-- |==> x++ */
			case POST_DEC_INC:	buffer.append("( (").append(sub.get_location().read()).append(")++ )"); break;
			default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
			}
		}
		/* --x */
		else {
			AstIncreUnaryExpression expr = 
					(AstIncreUnaryExpression) loc;
			AstExpression sub = expr.get_operand();
			switch(mutation.get_mode()) {
			/* --x |==> x-- */
			case PREV_POST_DEC: buffer.append("( (").append(sub.get_location().read()).append(")-- )"); break;
			/* --x |==> ++x */
			case PREV_DEC_INC:  buffer.append("( ++(").append(sub.get_location().read()).append(") )"); break;
			default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
			}
		}
	}

}
