package com.jcsa.jcparse.lang.ptoken.impl;

import com.jcsa.jcparse.lang.ptoken.PCommentToken;

public class PCommentTokenImpl extends PTokenImpl implements PCommentToken {

	private boolean is_block;
	private String comment;

	protected PCommentTokenImpl(boolean block, String cmt) {
		super();
		if (cmt == null)
			throw new IllegalArgumentException("Invalid comment: null");
		else {
			this.is_block = block;
			this.comment = cmt;
		}
	}

	@Override
	public boolean is_block_comment() {
		return is_block;
	}

	@Override
	public boolean is_line_comment() {
		return !is_block;
	}

	@Override
	public String get_comment() {
		return comment;
	}

	@Override
	public String toString() {
		return "[comment]{ " + (is_block ? "block" : "line") + ": \"" + comment + "\" }";
	}
}
