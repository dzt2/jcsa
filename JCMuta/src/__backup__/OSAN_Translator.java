package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;

public class OSAN_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OSAN_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		AstShiftBinaryExpression expr = 
				(AstShiftBinaryExpression) mutation.get_location();
		AstExpression loperand = expr.get_loperand(), roperand = expr.get_roperand();
		
		/* mutate code */
		buffer.append("( (").append(loperand.get_location().read()).append(") ");
		switch(mutation.get_mode()) {
		case LSH_ADD: case RSH_ADD: buffer.append('+'); break;
		case LSH_SUB: case RSH_SUB: buffer.append('-'); break;
		case LSH_MUL: case RSH_MUL: buffer.append('*'); break;
		case LSH_DIV: case RSH_DIV: buffer.append('/'); break;
		case LSH_MOD: case RSH_MOD: buffer.append('%'); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
