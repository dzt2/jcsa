package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>expr |==> ~expr</code>
 * @author yukimula
 */
public class OBNG_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OBNG_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		AstExpression expr = (AstExpression) mutation.get_location();
		buffer.append("( ~(").append(expr.get_location().read()).append(") )");
	}

}
