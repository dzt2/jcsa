package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;

public class ONDU_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof ONDU_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		AstUnaryExpression expr = (AstUnaryExpression) mutation.get_location();
		AstExpression expression = expr.get_operand();
		
		if(expression instanceof AstParanthExpression)
			buffer.append(expression.get_location().read());
		else {
			buffer.append("( ");
			buffer.append(expression.get_location().read());
			buffer.append(" )");
		}
	}

}
