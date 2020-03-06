package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;

public class OABA_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OABA_Mutation;
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
		case ADD_BAN_A: 
		case SUB_BAN_A: 
		case MUL_BAN_A: 
		case DIV_BAN_A:
		case MOD_BAN_A:
			buffer.append(" &= "); break;
		case ADD_BOR_A: 
		case SUB_BOR_A: 
		case MUL_BOR_A: 
		case DIV_BOR_A:
		case MOD_BOR_A:
			buffer.append(" |= "); break;
		case ADD_BXR_A: 
		case SUB_BXR_A: 
		case MUL_BXR_A: 
		case DIV_BXR_A:
		case MOD_BXR_A:
			buffer.append(" ^= "); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
