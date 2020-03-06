package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;

public class VSFR_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof VSFR_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		AstFieldExpression expr = 
				(AstFieldExpression) mutation.get_location();
		return expr.get_field();
	}

	@Override
	protected void mutate(Mutation mutation) throws Exception {
		VSFR_Mutation mut = (VSFR_Mutation) mutation;
		buffer.append(mut.get_field_name());
	}

}
