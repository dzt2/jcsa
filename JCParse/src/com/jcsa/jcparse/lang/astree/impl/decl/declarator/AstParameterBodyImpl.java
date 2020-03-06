package com.jcsa.jcparse.lang.astree.impl.decl.declarator;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstIdentifierList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterBody;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterTypeList;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.scope.CScope;

public class AstParameterBodyImpl extends AstFixedNode implements AstParameterBody {

	public AstParameterBodyImpl(AstPunctuator lparanth, AstParameterTypeList plist, AstPunctuator rparanth)
			throws Exception {
		super(3);

		if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid lparanth: left-paranth");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid rparanth: right-paranth");
		else {
			this.set_child(0, lparanth);
			this.set_child(1, plist);
			this.set_child(2, rparanth);
		}
	}

	public AstParameterBodyImpl(AstPunctuator lparanth, AstIdentifierList plist, AstPunctuator rparanth)
			throws Exception {
		super(3);

		if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid lparanth: left-paranth");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid rparanth: right-paranth");
		else {
			this.set_child(0, lparanth);
			this.set_child(1, plist);
			this.set_child(2, rparanth);
		}
	}

	public AstParameterBodyImpl(AstPunctuator lparanth, AstPunctuator rparanth) throws Exception {
		super(2);

		if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid lparanth: left-paranth");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid rparanth: right-paranth");
		else {
			this.set_child(0, lparanth);
			this.set_child(1, rparanth);
		}
	}

	@Override
	public boolean has_parameter_type_list() {
		return children[1] instanceof AstParameterTypeList;
	}

	@Override
	public boolean has_identifier_list() {
		return children[1] instanceof AstIdentifierList;
	}

	@Override
	public AstPunctuator get_lparanth() {
		return (AstPunctuator) children[0];
	}

	@Override
	public AstParameterTypeList get_parameter_type_list() {
		if (!this.has_parameter_type_list())
			throw new IllegalArgumentException("Invalid access: no-parameter-type-list");
		else
			return (AstParameterTypeList) children[1];
	}

	@Override
	public AstIdentifierList get_identifier_list() {
		if (!this.has_identifier_list())
			throw new IllegalArgumentException("Invalid access: no identifier-list");
		else
			return (AstIdentifierList) children[1];
	}

	@Override
	public AstPunctuator get_rparanth() {
		return (AstPunctuator) children[2];
	}

	protected CScope scope;

	@Override
	public CScope get_scope() {
		return scope;
	}

	@Override
	public void set_scope(CScope scope) {
		this.scope = scope;
	}
}
