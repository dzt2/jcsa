package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

public class VABS_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof VABS_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		AstExpression expr = 
				(AstExpression) mutation.get_location();
		buffer.append(MutaCode.Abs_Value).append("( ");
		buffer.append(expr.get_location().read()).append(" )");
	}

}
