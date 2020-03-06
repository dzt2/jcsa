package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;

public class OLBN_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OLBN_Mutation;
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
		case LAN_BAN: case LOR_BAN: buffer.append('&'); break;
		case LAN_BOR: case LOR_BOR: buffer.append('|'); break;
		case LAN_BXR: case LOR_BXR: buffer.append('^'); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
