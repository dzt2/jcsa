package com.jcsa.jcparse.lang.astree.impl.pline;

import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.pline.AstDirective;
import com.jcsa.jcparse.lang.astree.pline.AstMacro;
import com.jcsa.jcparse.lang.astree.pline.AstMacroBody;
import com.jcsa.jcparse.lang.astree.pline.AstMacroList;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessDefineLine;
import com.jcsa.jcparse.lang.lexical.CDirective;

public class AstPreprocessDefineLineImpl extends AstFixedNode implements AstPreprocessDefineLine {

	public AstPreprocessDefineLineImpl(AstDirective define, AstMacro macro, AstMacroBody body) throws Exception {
		super(3);

		if (define == null || define.get_directive() != CDirective.cdir_define)
			throw new IllegalArgumentException("Invalid #define: null");
		else {
			this.set_child(0, define);
			this.set_child(1, macro);
			this.set_child(2, body);
		}
	}

	public AstPreprocessDefineLineImpl(AstDirective define, AstMacro macro, AstMacroList mlist, AstMacroBody body)
			throws Exception {
		super(4);

		if (define == null || define.get_directive() != CDirective.cdir_define)
			throw new IllegalArgumentException("Invalid #define: null");
		else {
			this.set_child(0, define);
			this.set_child(1, macro);
			this.set_child(2, mlist);
			this.set_child(3, body);
		}
	}

	@Override
	public AstDirective get_directive() {
		return (AstDirective) children[0];
	}

	@Override
	public AstMacro get_macro() {
		return (AstMacro) children[1];
	}

	@Override
	public boolean has_id_list() {
		return children.length == 4;
	}

	@Override
	public AstMacroList get_id_list() {
		if (children.length != 4)
			throw new IllegalArgumentException("Invalid access: no macro-list");
		else
			return (AstMacroList) children[2];
	}

	@Override
	public AstMacroBody get_body() {
		return (AstMacroBody) children[children.length - 1];
	}

}
