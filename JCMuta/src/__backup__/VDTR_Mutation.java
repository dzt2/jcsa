package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;

public class VDTR_Mutation extends SingleMutation {

	protected VDTR_Mutation(MutationMode mode, AstExpression location) throws Exception {
		super(MutOperator.VDTR, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstExpression expr = (AstExpression) location;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		if(JC_Classifier.is_access_path(expr)
			&& JC_Classifier.is_integer_type(type)) {
			switch(mode) {
			case TRAP_ON_POS:
			case TRAP_ON_ZRO:
				return true;
			case TRAP_ON_NEG:
				return !JC_Classifier.is_unsigned_type(type);
			default: return false;
			}
		}
		else return false;
	}

}
