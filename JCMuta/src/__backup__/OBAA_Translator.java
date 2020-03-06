package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;

public class OBAA_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OBAA_Mutation;
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
		case BAN_ADD_A:
		case BOR_ADD_A:
		case BXR_ADD_A:
			buffer.append(" += "); break;
		case BAN_SUB_A:
		case BOR_SUB_A:
		case BXR_SUB_A:
			buffer.append(" -= "); break;
		case BAN_MUL_A:
		case BOR_MUL_A:
		case BXR_MUL_A:
			buffer.append(" *= "); break;
		case BAN_DIV_A:
		case BOR_DIV_A:
		case BXR_DIV_A:
			buffer.append(" /= "); break;
		case BAN_MOD_A:
		case BOR_MOD_A:
		case BXR_MOD_A:
			buffer.append(" %= "); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
