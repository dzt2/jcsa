package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;

/**
 * <code>switch(expr) |--> switch( trap_on_case(expr, val) )</code>
 * @author yukimula
 *
 */
public class SSWM_Mutation extends CustomizeMutation {

	protected SSWM_Mutation(AstSwitchStatement location, AstConstExpression value) throws Exception {
		super(MutOperator.SSWM, MutationMode.VTRAP_ON_CASE, location, value);
	}
	
	public AstConstExpression get_case_expression() {return (AstConstExpression) argument;}

	@Override
	protected boolean validate(MutationMode mode, AstNode location, Object argument) {
		switch(mode) {
		case VTRAP_ON_CASE:
			return location instanceof AstSwitchStatement;
		default: return false;
		}
	}

}
