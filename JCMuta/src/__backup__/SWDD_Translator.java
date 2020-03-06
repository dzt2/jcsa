package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

/**
 * <code>
 * 	while ( expr ) body <br>
 * 	|==> <br>
 * 	do body while ( expr ); <br>
 * </code>
 * @author yukimula
 */
public class SWDD_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof SWDD_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		/* getters */
		AstWhileStatement stmt = 
				(AstWhileStatement) mutation.get_location();
		AstExpression expr = stmt.get_condition();
		AstStatement body = stmt.get_body();
		
		/* mutate code */
		buffer.append("do ");
		buffer.append(body.get_location().read());
		buffer.append(" while( ");
		buffer.append(expr.get_location().read());
		buffer.append(" );");
	}

}
