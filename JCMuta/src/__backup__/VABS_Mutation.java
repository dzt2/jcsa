package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;

/**
 * <code>expr |=> abs(expr)</code>
 * @author yukimula
 *
 */
public class VABS_Mutation extends SingleMutation {

	protected VABS_Mutation(AstExpression location) throws Exception {
		super(MutOperator.VABS, MutationMode.ABS_VAL, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstExpression expr = (AstExpression) location;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		if(JC_Classifier.is_access_path(expr)) {
			if(JC_Classifier.is_integer_type(type)
				|| JC_Classifier.is_real_type(type)) {
				switch(mode) {
				case ABS_VAL: return !JC_Classifier.is_unsigned_type(type);
				default: return false;
				}
			}
			else return false;
		}
		else return false;
	}

}
