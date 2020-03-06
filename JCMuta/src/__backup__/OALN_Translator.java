package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;

/**
 * <code> {+, -, *, /, %} ==> {&&, ||} </code>
 * @author yukimula
 */
public class OALN_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OALN_Mutation;
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
		case ADD_LAN: case SUB_LAN: case MUL_LAN: case DIV_LAN: case MOD_LAN: buffer.append("&&"); break;
		case ADD_LOR: case SUB_LOR: case MUL_LOR: case DIV_LOR: case MOD_LOR: buffer.append("||"); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}
}
