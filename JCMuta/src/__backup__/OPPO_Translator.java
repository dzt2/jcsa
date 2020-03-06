package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;

public class OPPO_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OPPO_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		AstNode loc = mutation.get_location();
		/* x++ */
		if(loc instanceof AstIncrePostfixExpression) {
			AstIncrePostfixExpression expr 
				= (AstIncrePostfixExpression) loc;
			AstExpression sub = expr.get_operand();
			switch(mutation.get_mode()) {
			/* x++ |==> ++x */
			case POST_PREV_INC:	buffer.append("( ++(").append(sub.get_location().read()).append(") )"); 	break;
			/* x++ |==> x-- */
			case POST_INC_DEC:	buffer.append("( (").append(sub.get_location().read()).append(")-- )"); break;
			default: throw new IllegalArgumentException("Invalid mode {" + mutation.get_mode() + "}");
			}
		}
		/* ++x */
		else {
			AstIncreUnaryExpression expr
				= (AstIncreUnaryExpression) loc;
			AstExpression sub = expr.get_operand();
			switch(mutation.get_mode()) {
			/* ++x |==> x++ */
			case PREV_POST_INC:	buffer.append("( (").append(sub.get_location().read()).append(")++ )"); break;
			/* ++x |==> --x */
			case PREV_INC_DEC:	buffer.append("( --(").append(sub.get_location().read()).append(") )"); break;
			default: throw new IllegalArgumentException("Invalid mode {" + mutation.get_mode() + "}");
			}
		}
		
	}

}
