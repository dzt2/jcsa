package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;

/**
 * <code>{&, |, ^} ==> {<, <=, ==, !=, >=, >}</code>
 * @author yukimula
 *
 */
public class OBRN_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OBRN_Mutation;
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
		case BAN_GRT: case BOR_GRT: case BXR_GRT: buffer.append(">");  break;
		case BAN_GRE: case BOR_GRE: case BXR_GRE: buffer.append(">="); break;
		case BAN_EQV: case BOR_EQV: case BXR_EQV: buffer.append("=="); break;
		case BAN_NEQ: case BOR_NEQ: case BXR_NEQ: buffer.append("!="); break;
		case BAN_SMT: case BOR_SMT: case BXR_SMT: buffer.append("<");  break;
		case BAN_SME: case BOR_SME: case BXR_SME: buffer.append("<="); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
