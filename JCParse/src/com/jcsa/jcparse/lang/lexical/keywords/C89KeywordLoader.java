package com.jcsa.jcparse.lang.lexical.keywords;

import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.CLangKeywordLib;
import com.jcsa.jcparse.lang.lexical.CLangKeywordLoader;

/**
 * This will bind the keyword names defined in <b>C89</b> standard with
 * specified c89_xxx keyword in CKeyword
 *
 * @author yukimula
 *
 */
public class C89KeywordLoader implements CLangKeywordLoader {

	protected C89KeywordLoader() {
	}

	@Override
	public void load(CLangKeywordLib lib) throws Exception {
		lib.insert_keyword("auto", CKeyword.c89_auto);
		lib.insert_keyword("break", CKeyword.c89_break);
		lib.insert_keyword("case", CKeyword.c89_case);
		lib.insert_keyword("char", CKeyword.c89_char);
		lib.insert_keyword("const", CKeyword.c89_const);
		lib.insert_keyword("continue", CKeyword.c89_continue);
		lib.insert_keyword("default", CKeyword.c89_default);
		lib.insert_keyword("do", CKeyword.c89_do);

		lib.insert_keyword("double", CKeyword.c89_double);
		lib.insert_keyword("else", CKeyword.c89_else);
		lib.insert_keyword("enum", CKeyword.c89_enum);
		lib.insert_keyword("extern", CKeyword.c89_extern);
		lib.insert_keyword("float", CKeyword.c89_float);
		lib.insert_keyword("for", CKeyword.c89_for);
		lib.insert_keyword("goto", CKeyword.c89_goto);
		lib.insert_keyword("if", CKeyword.c89_if);

		lib.insert_keyword("int", CKeyword.c89_int);
		lib.insert_keyword("long", CKeyword.c89_long);
		lib.insert_keyword("register", CKeyword.c89_register);
		lib.insert_keyword("return", CKeyword.c89_return);
		lib.insert_keyword("short", CKeyword.c89_short);
		lib.insert_keyword("signed", CKeyword.c89_signed);
		lib.insert_keyword("sizeof", CKeyword.c89_sizeof);
		lib.insert_keyword("static", CKeyword.c89_static);

		lib.insert_keyword("struct", CKeyword.c89_struct);
		lib.insert_keyword("switch", CKeyword.c89_switch);
		lib.insert_keyword("typedef", CKeyword.c89_typedef);
		lib.insert_keyword("union", CKeyword.c89_union);
		lib.insert_keyword("unsigned", CKeyword.c89_unsigned);
		lib.insert_keyword("void", CKeyword.c89_void);
		lib.insert_keyword("volatile", CKeyword.c89_volatile);
		lib.insert_keyword("while", CKeyword.c89_while);
	}

}
