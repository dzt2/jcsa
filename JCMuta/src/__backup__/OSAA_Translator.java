package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;

public class OSAA_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OSAA_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_location();
		AstExpression loperand = expr.get_loperand(), roperand = expr.get_roperand();
		
		/* mutate code */
		buffer.append("( (").append(loperand.get_location().read()).append(") ");
		switch(mutation.get_mode()) {
		case LSH_ADD_A:
		case RSH_ADD_A:
			buffer.append(" += "); break;
		case LSH_SUB_A:
		case RSH_SUB_A:
			buffer.append(" -= "); break;
		case LSH_MUL_A:
		case RSH_MUL_A:
			buffer.append(" *= "); break;
		case LSH_DIV_A:
		case RSH_DIV_A:
			buffer.append(" /= "); break;
		case LSH_MOD_A:
		case RSH_MOD_A:
			buffer.append(" %= "); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
