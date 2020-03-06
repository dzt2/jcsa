package com.jcsa.jcparse.lang.astree.impl.pline;

import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;
import com.jcsa.jcparse.lang.astree.pline.AstMacroBody;
import com.jcsa.jcparse.lang.ctoken.CToken;

public class AstMacroBodyImpl extends AstVariableNode implements AstMacroBody {

	public AstMacroBodyImpl() throws Exception {
		super();
	}

	protected static class AstTokenImpl extends AstFixedNode {
		protected CToken token;

		protected AstTokenImpl(CToken token) throws Exception {
			super(0);

			if (token == null)
				throw new IllegalArgumentException("Invalid token: null");
			else {
				this.token = token;
				this.set_location(token.get_location());
			}
		}
	}

	@Override
	public int number_of_tokens() {
		return children.size();
	}

	@Override
	public CToken get_token(int k) {
		if (k < 0 || k >= children.size())
			throw new IllegalArgumentException("Invalid index: " + k);
		else
			return ((AstTokenImpl) children.get(k)).token;
	}

	@Override
	public void append_token(CToken tok) throws Exception {
		this.append_child(new AstTokenImpl(tok));
	}

}
