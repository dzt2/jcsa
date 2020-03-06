package com.jcsa.jcparse.lang.lexical.keywords;

import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.CLangKeywordLib;
import com.jcsa.jcparse.lang.lexical.CLangKeywordLoader;

/**
 * To load GNU specific keyword for keyword library
 * 
 * @author yukimula
 *
 */
public class GNUKeywordLoader implements CLangKeywordLoader {

	protected GNUKeywordLoader() {
	}

	@Override
	public void load(CLangKeywordLib lib) throws Exception {
		// C89 keywords with GNU-names
		lib.insert_keyword("__const", CKeyword.c89_const);
		lib.insert_keyword("__signed", CKeyword.c89_signed);
		lib.insert_keyword("__signed__", CKeyword.c89_signed);
		lib.insert_keyword("__volatile", CKeyword.c89_volatile);
		lib.insert_keyword("__volatile__", CKeyword.c89_volatile);

		// C99 keywords with GNU-names
		lib.insert_keyword("__inline", CKeyword.c99_inline);
		lib.insert_keyword("__inline__", CKeyword.c99_inline);
		lib.insert_keyword("__restrict", CKeyword.c99_restrict);
		lib.insert_keyword("__restrict__", CKeyword.c99_restrict);
		lib.insert_keyword("__complex", CKeyword.c99_complex);
		lib.insert_keyword("__complex__", CKeyword.c99_complex);
		lib.insert_keyword("__imag", CKeyword.c99_imaginary);
		lib.insert_keyword("__imag__", CKeyword.c99_imaginary);

		// GNU-specific keywords
		lib.insert_keyword("__FUNCTION__", CKeyword.gnu_function);
		lib.insert_keyword("__PRETTY_FUNCTION__", CKeyword.gnu_pretty_function);
		lib.insert_keyword("__alignof", CKeyword.gnu_alignof);
		lib.insert_keyword("__alignof__", CKeyword.gnu_alignof);
		lib.insert_keyword("__asm", CKeyword.gnu_asm);
		lib.insert_keyword("__asm__", CKeyword.gnu_asm);
		lib.insert_keyword("__attribute", CKeyword.gnu_attribute);
		lib.insert_keyword("__attribute__", CKeyword.gnu_attribute);
		lib.insert_keyword("__builtin_offsetof", CKeyword.gnu_builtin_offsetof);
		lib.insert_keyword("__builtin_va_arg", CKeyword.gnu_builtin_va_arg);
		lib.insert_keyword("__builtin_va_list", CKeyword.gnu_builtin_va_list);
		lib.insert_keyword("__extension__", CKeyword.gnu_extension);
		lib.insert_keyword("__func__", CKeyword.gnu_func);
		lib.insert_keyword("__label__", CKeyword.gnu_label);
		lib.insert_keyword("__null", CKeyword.gnu_null);
		lib.insert_keyword("__real", CKeyword.gnu_real);
		lib.insert_keyword("__real__", CKeyword.gnu_real);
		lib.insert_keyword("__thread", CKeyword.gnu_thread);
		lib.insert_keyword("__typeof", CKeyword.gnu_typeof);

	}

}
