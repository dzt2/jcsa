package com.jcsa.jcparse.lang.astree.impl.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstTypeKeyword;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;

public class AstTypeKeywordImpl extends AstFixedNode implements AstTypeKeyword {

	public AstTypeKeywordImpl(AstKeyword keyword) throws Exception {
		super(1);

		if (keyword == null)
			throw new IllegalArgumentException("Invalid keyword: null");
		else {
			switch (keyword.get_keyword()) {
			case c89_void:
			case c99_bool:
			case c89_char:
			case c89_short:
			case c89_int:
			case c89_long:
			case c89_signed:
			case c89_unsigned:
			case c89_float:
			case c89_double:
			case c99_complex:
			case c99_imaginary:
			case gnu_builtin_va_list:
				this.set_child(0, keyword);
				break;
			default:
				throw new IllegalArgumentException("Invalid keyword: " + keyword);
			}
		}
	}

	@Override
	public AstKeyword get_keyword() {
		return (AstKeyword) children[0];
	}

}
