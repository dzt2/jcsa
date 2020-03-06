package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;

public class VTRR_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof VTRR_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		VTRR_Mutation mut = (VTRR_Mutation) mutation;
		buffer.append(mut.get_replace().get_name());
	}

}
