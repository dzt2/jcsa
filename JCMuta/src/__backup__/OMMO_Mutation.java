package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * <code>
 * 	x-- |==> x++ || --x <br>
 * 	--x |==> ++x || x-- <br>
 * </code>
 * @author yukimula
 *
 */
public class OMMO_Mutation extends SingleMutation {

	protected OMMO_Mutation(MutationMode mode, AstNode location) throws Exception {
		super(MutOperator.OMMO, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		switch(mode) {
		case POST_PREV_DEC:
		case POST_DEC_INC:
			if(location instanceof AstIncrePostfixExpression) {
				return ((AstIncrePostfixExpression) location).get_operator().get_operator() == COperator.decrement;
			}
			else return false;
		case PREV_POST_DEC:
		case PREV_DEC_INC:
			if(location instanceof AstIncreUnaryExpression) {
				return ((AstIncreUnaryExpression) location).get_operator().get_operator() == COperator.decrement;
			}
			else return false;
		default: return false;
		}
	}

}
