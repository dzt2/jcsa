package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;

public class CCCR_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof CCCR_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		CCCR_Mutation mut = (CCCR_Mutation) mutation;
		buffer.append("( ");
		buffer.append(mut.get_replace().get_location().read());
		buffer.append(" )");
	}

}
