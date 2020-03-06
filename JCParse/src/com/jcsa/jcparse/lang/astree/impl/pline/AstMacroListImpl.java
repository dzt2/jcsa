package com.jcsa.jcparse.lang.astree.impl.pline;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstIdentifierList;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.pline.AstMacroList;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstMacroListImpl extends AstFixedNode implements AstMacroList {

	public AstMacroListImpl(AstPunctuator lparanth, AstPunctuator rparanth) throws Exception {
		super(2);

		if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid left-paranth: null");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid right-paranth: null");
		else {
			this.set_child(0, lparanth);
			this.set_child(1, rparanth);
		}
	}

	public AstMacroListImpl(AstPunctuator lparanth, AstIdentifierList idlist, AstPunctuator rparanth) throws Exception {
		super(3);

		if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid left-paranth: null");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid right-paranth: null");
		else {
			this.set_child(0, lparanth);
			this.set_child(1, idlist);
			this.set_child(2, rparanth);
		}
	}

	public AstMacroListImpl(AstPunctuator lparanth, AstPunctuator ellipsis, AstPunctuator rparanth) throws Exception {
		super(3);

		if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid left-paranth: null");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid right-paranth: null");
		else if (ellipsis == null || ellipsis.get_punctuator() != CPunctuator.ellipsis)
			throw new IllegalArgumentException("Invalid ellipsis: null");
		else {
			this.set_child(0, lparanth);
			this.set_child(1, ellipsis);
			this.set_child(2, rparanth);
		}
	}

	@Override
	public boolean has_identifiers() {
		return children[1] instanceof AstIdentifierList;
	}

	@Override
	public boolean has_ellipsis() {
		if (children[1] instanceof AstPunctuator)
			return ((AstPunctuator) children[1]).get_punctuator() != CPunctuator.ellipsis;
		else
			return false;
	}

	@Override
	public AstPunctuator get_lparanth() {
		return (AstPunctuator) children[0];
	}

	@Override
	public AstIdentifierList get_identifiers() {
		if (!this.has_identifiers())
			throw new IllegalArgumentException("Invalid access: no-identifier-list");
		else
			return (AstIdentifierList) children[1];
	}

	@Override
	public AstPunctuator get_ellipsis() {
		if (!this.has_ellipsis())
			throw new IllegalArgumentException("Invalid access: no ellipsis");
		else
			return (AstPunctuator) children[1];
	}

	@Override
	public AstPunctuator get_rparanth() {
		return (AstPunctuator) children[children.length - 1];
	}

}
