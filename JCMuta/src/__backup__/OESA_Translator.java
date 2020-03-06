package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;

public class OESA_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OESA_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		AstAssignExpression expr = 
				(AstAssignExpression) mutation.get_location();
		AstExpression loperand = expr.get_loperand();
		AstExpression roperand = expr.get_roperand();
		
		buffer.append("( (").append(loperand.get_location().read()).append(") ");
		switch(mutation.get_mode()) {
		case ASG_LSH:	buffer.append("<<="); break;
		case ASG_RSH:	buffer.append(">>="); break;
		default: throw new IllegalArgumentException(
				"Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
