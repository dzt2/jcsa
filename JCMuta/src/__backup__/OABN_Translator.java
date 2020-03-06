package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;

/**
 * <code>{+, -, *, /, %} ==> {&, |, ^}</code>
 * @author yukimula
 */
public class OABN_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OABN_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		/* getters */
		AstArithBinaryExpression expr = (AstArithBinaryExpression) mutation.get_location();
		AstExpression loperand = expr.get_loperand(), roperand = expr.get_roperand();
		
		/* mutate code */
		buffer.append("( (").append(loperand.get_location().read()).append(") ");
		switch(mutation.get_mode()) {
		case ADD_BAN: case SUB_BAN: case MUL_BAN: case DIV_BAN: case MOD_BAN: buffer.append('&'); break;
		case ADD_BOR: case SUB_BOR: case MUL_BOR: case DIV_BOR: case MOD_BOR: buffer.append('|'); break;
		case ADD_BXR: case SUB_BXR: case MUL_BXR: case DIV_BXR: case MOD_BXR: buffer.append('^'); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
