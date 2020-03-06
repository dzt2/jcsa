package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * <code>
 * 	x++ |--> ++x || x-- <br>
 * 	++x |--> x++ || --x <br>
 * </code>
 * @author yukimula
 *
 */
public class OPPO_Mutation extends SingleMutation {

	protected OPPO_Mutation(MutationMode mode, AstExpression location) throws Exception {
		super(MutOperator.OPPO, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		switch(mode) {
		case POST_PREV_INC:
		case POST_INC_DEC:
			if(location instanceof AstIncrePostfixExpression) {
				return ((AstIncrePostfixExpression) location).get_operator().get_operator() == COperator.increment;
			}
			else return false;
		case PREV_POST_INC:
		case PREV_INC_DEC:
			if(location instanceof AstIncreUnaryExpression) {
				return ((AstIncreUnaryExpression) location).get_operator().get_operator() == COperator.increment;
			}
			else return false;
		default: return false;
		}
	}

}
