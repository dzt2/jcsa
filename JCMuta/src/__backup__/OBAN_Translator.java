package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;

/**
 * <code>{&, |, ^} ==> {+, -, *, /, %}</code>
 * @author yukimula
 */
public class OBAN_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OBAN_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		AstBitwiseBinaryExpression expr = (AstBitwiseBinaryExpression) mutation.get_location();
		AstExpression loperand = expr.get_loperand(), roperand = expr.get_roperand();
		
		/* mutate code */
		buffer.append("( (").append(loperand.get_location().read()).append(") ");
		switch(mutation.get_mode()) {
		case BAN_ADD: case BOR_ADD: case BXR_ADD: buffer.append('+'); break;
		case BAN_SUB: case BOR_SUB: case BXR_SUB: buffer.append('-'); break;
		case BAN_MUL: case BOR_MUL: case BXR_MUL: buffer.append('*'); break;
		case BAN_DIV: case BOR_DIV: case BXR_DIV: buffer.append('/'); break;
		case BAN_MOD: case BOR_MOD: case BXR_MOD: buffer.append('%'); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
