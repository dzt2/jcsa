package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;

public class OLAN_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OLAN_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		AstLogicBinaryExpression expr = 
				(AstLogicBinaryExpression) mutation.get_location();
		AstExpression loperand = expr.get_loperand();
		AstExpression roperand = expr.get_roperand();
		
		buffer.append("( (").append(loperand.get_location().read()).append(") ");
		switch(mutation.get_mode()) {
		case LAN_ADD: case LOR_ADD: buffer.append('+'); break;
		case LAN_SUB: case LOR_SUB: buffer.append('-'); break;
		case LAN_MUL: case LOR_MUL: buffer.append('*'); break;
		case LAN_DIV: case LOR_DIV: buffer.append('/'); break;
		case LAN_MOD: case LOR_MOD: buffer.append('%'); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
