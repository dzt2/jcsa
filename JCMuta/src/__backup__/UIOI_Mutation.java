package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;

/**
 * <code>x |==> x++ | ++x | x-- | --x</code>
 * @author yukimula
 *
 */
public class UIOI_Mutation extends SingleMutation {

	protected UIOI_Mutation(MutationMode mode, AstExpression location) throws Exception {
		super(MutOperator.UIOI, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		switch(mode) {
		case PREV_INC_INS:
		case PREV_DEC_INS:
		case POST_INC_INS:
		case POST_DEC_INS:
			AstExpression expr = (AstExpression) location;
			CType type = expr.get_value_type();
			type = JC_Classifier.get_value_type(type);
			
			if(JC_Classifier.is_access_path(expr)) {
				if(JC_Classifier.is_integer_type(type)
					|| JC_Classifier.is_address_type(type))
					return true;
				else return false;
			}
			else return false;
		default: return false;
		}
	}
}
