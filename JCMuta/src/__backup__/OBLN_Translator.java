package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;

/**
 * <code>{&, |, ^} ==> {&&, ||}</code>
 * @author yukimula
 */
public class OBLN_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OBLN_Mutation;
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
		case BAN_LAN: case BOR_LAN: case BXR_LAN: buffer.append("&&"); break;
		case BAN_LOR: case BOR_LOR: case BXR_LOR: buffer.append("||"); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
