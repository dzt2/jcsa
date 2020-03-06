package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;

public class OAAA_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OAAA_Mutation;
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
		case SUB_ADD_A: case MUL_ADD_A: case DIV_ADD_A: case MOD_ADD_A: buffer.append(" += "); break;
		case ADD_SUB_A: case MUL_SUB_A: case DIV_SUB_A: case MOD_SUB_A: buffer.append(" -= "); break;
		case ADD_MUL_A: case SUB_MUL_A: case DIV_MUL_A: case MOD_MUL_A: buffer.append(" *= "); break;
		case ADD_DIV_A: case SUB_DIV_A: case MUL_DIV_A: case MOD_DIV_A: buffer.append(" /= "); break;
		case ADD_MOD_A: case SUB_MOD_A: case MUL_MOD_A: case DIV_MOD_A: buffer.append(" %= "); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
