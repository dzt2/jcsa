package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;

public class VTWD_Mutation extends SingleMutation {

	protected VTWD_Mutation(MutationMode mode, 
			AstExpression location) throws Exception {
		super(MutOperator.VTWD, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstExpression expr = (AstExpression) location;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		if(JC_Classifier.is_access_path(expr)) {
			if(JC_Classifier.is_integer_type(type)
				|| JC_Classifier.is_real_type(type)) {
				switch(mode) {
				case SUCC_VAL: case PRED_VAL: return true;
				default: return false;
				}
			}
			else return false;
		}
		else return false;
	}

}
