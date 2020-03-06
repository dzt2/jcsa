package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;

public class VSRR_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof VSRR_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		VSRR_Mutation mut = (VSRR_Mutation) mutation;
		buffer.append(mut.get_replace().get_name());
	}

}
