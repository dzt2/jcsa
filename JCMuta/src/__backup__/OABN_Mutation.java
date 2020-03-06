package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * <code>{+, -, *, /, %} |==> {&, |, ^}</code>
 * @author yukimula
 */
public class OABN_Mutation extends SingleMutation {

	protected OABN_Mutation(MutationMode mode, AstArithBinaryExpression location) throws Exception {
		super(MutOperator.OABN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstArithBinaryExpression expr = (AstArithBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case ADD_BAN: case ADD_BOR: case ADD_BXR:
			return operator == COperator.arith_add;
		case SUB_BAN: case SUB_BOR: case SUB_BXR:
			return operator == COperator.arith_sub;
		case MUL_BAN: case MUL_BOR: case MUL_BXR:
			return operator == COperator.arith_mul;
		case DIV_BAN: case DIV_BOR: case DIV_BXR:
			return operator == COperator.arith_div;
		case MOD_BAN: case MOD_BOR: case MOD_BXR:
			return operator == COperator.arith_mod;
		default: return false;
		}
	}

}
