package com.jcsa.jcparse.lang.astree.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;

/**
 * <code>TypeKw --> <b>void | _Bool (c99) | char | short | int | long | signed | unsigned
 * | float | double | _Complex (c99|gnu) | _Imaginary (c99|gnu) | __builtin_va_list (gnu) </b></code>
 *
 * @author yukimula
 */
public interface AstTypeKeyword extends AstSpecifier {
	/**
	 * get the keyword of the type specifier
	 *
	 * @return
	 */
	public AstKeyword get_keyword();
}
