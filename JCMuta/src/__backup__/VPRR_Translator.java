package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;

public class VPRR_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof VPRR_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		VPRR_Mutation mut = (VPRR_Mutation) mutation;
		buffer.append(mut.get_replace().get_name());
	}

}
