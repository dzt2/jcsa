package com.jcsa.jcparse.lang.astree.impl.decl.initializer;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstInitializerBodyImpl extends AstFixedNode implements AstInitializerBody {

	public AstInitializerBodyImpl(AstPunctuator lbrace, AstInitializerList list, AstPunctuator rbrace)
			throws Exception {
		super(3);

		if (lbrace == null || lbrace.get_punctuator() != CPunctuator.left_brace)
			throw new IllegalArgumentException("Invalid lbrace: null");
		else if (rbrace == null || rbrace.get_punctuator() != CPunctuator.right_brace)
			throw new IllegalArgumentException("Invalid rbrace: null");
		else {
			this.set_child(0, lbrace);
			this.set_child(1, list);
			this.set_child(2, rbrace);
		}
	}

	public AstInitializerBodyImpl(AstPunctuator lbrace, AstInitializerList list, AstPunctuator comma,
			AstPunctuator rbrace) throws Exception {
		super(4);

		if (lbrace == null || lbrace.get_punctuator() != CPunctuator.left_brace)
			throw new IllegalArgumentException("Invalid lbrace: null");
		else if (rbrace == null || rbrace.get_punctuator() != CPunctuator.right_brace)
			throw new IllegalArgumentException("Invalid rbrace: null");
		else if (comma == null || comma.get_punctuator() != CPunctuator.comma)
			throw new IllegalArgumentException("Invalid comma: null");
		else {
			this.set_child(0, lbrace);
			this.set_child(1, list);
			this.set_child(2, comma);
			this.set_child(3, rbrace);
		}
	}

	@Override
	public AstPunctuator get_lbrace() {
		return (AstPunctuator) children[0];
	}

	@Override
	public AstInitializerList get_initializer_list() {
		return (AstInitializerList) children[1];
	}

	@Override
	public boolean has_tail_comma() {
		return children.length == 4;
	}

	@Override
	public AstPunctuator get_tail_comma() {
		if (children.length != 4)
			throw new IllegalArgumentException("Invalid access: no tail-comma");
		else
			return (AstPunctuator) children[2];
	}

	@Override
	public AstPunctuator get_rbrace() {
		return (AstPunctuator) children[children.length - 1];
	}

	protected CType type;

	@Override
	public CType get_value_type() {
		return type;
	}

	@Override
	public void set_value_type(CType type) {
		this.type = type;
	}

}
