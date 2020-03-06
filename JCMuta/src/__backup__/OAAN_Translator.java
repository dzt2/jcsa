package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;

/**
 * <code> {+, -, *, /, %} </code>
 * @author yukimula
 */
public class OAAN_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OAAN_Mutation;
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
		case SUB_ADD: case MUL_ADD: case DIV_ADD: case MOD_ADD: buffer.append('+'); break;
		case ADD_SUB: case MUL_SUB: case DIV_SUB: case MOD_SUB: buffer.append('-'); break;
		case ADD_MUL: case SUB_MUL: case DIV_MUL: case MOD_MUL: buffer.append('*'); break;
		case ADD_DIV: case SUB_DIV: case MUL_DIV: case MOD_DIV: buffer.append('/'); break;
		case ADD_MOD: case SUB_MOD: case MUL_MOD: case DIV_MOD: buffer.append('%'); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
