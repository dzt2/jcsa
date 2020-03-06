package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

public class VTWD_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof VTWD_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		AstExpression expr = 
				(AstExpression) mutation.get_location();
		switch(mutation.get_mode()) {
		case SUCC_VAL:	buffer.append(MutaCode.Succ_Value); break;
		case PRED_VAL:	buffer.append(MutaCode.Pred_Value); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append("( ").append(expr.get_location().read()).append(" )");
	}

}
