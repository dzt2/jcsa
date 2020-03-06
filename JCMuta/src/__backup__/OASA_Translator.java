package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;

public class OASA_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OASA_Mutation;
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
		case ADD_LSH_A:
		case SUB_LSH_A:
		case MUL_LSH_A:
		case DIV_LSH_A:
		case MOD_LSH_A:
			buffer.append(" <<= "); break;
		case ADD_RSH_A:
		case SUB_RSH_A:
		case MUL_RSH_A:
		case DIV_RSH_A:
		case MOD_RSH_A:
			buffer.append(" >>= "); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
