package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;

public class VBCR_Mutation extends SingleMutation {

	protected VBCR_Mutation(MutationMode mode, AstExpression location) throws Exception {
		super(MutOperator.VBCR, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstExpression expr = (AstExpression) location;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		if(JC_Classifier.is_boolean_type(type)
			|| JC_Classifier.is_integer_type(type)
			|| JC_Classifier.is_real_type(type)
			|| type instanceof CPointerType) {
			switch(mode) {
			case MUT_TRUE:
			case MUT_FALSE:
				return true;
			default: return false;
			}
		}
		else return false;
	}

	
}
