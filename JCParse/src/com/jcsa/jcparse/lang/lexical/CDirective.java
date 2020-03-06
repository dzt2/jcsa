package com.jcsa.jcparse.lang.lexical;

/**
 * directive is used as instruction in preprocessing: <br>
 * <b> #if, #ifdef, #ifndef, #elif, #else, #endif, #define, #undef, #include,
 * #line, #error, #pragma </b>
 * 
 * @author yukimula
 *
 */
public enum CDirective {
	/** _error **/
	invalid_cdir,
	/** #if **/
	cdir_if,
	/** #ifdef **/
	cdir_ifdef,
	/** #ifndef **/
	cdir_ifndef,
	/** #elif **/
	cdir_elif,
	/** #else **/
	cdir_else,
	/** #endif **/
	cdir_endif,
	/** #define **/
	cdir_define,
	/** #undef **/
	cdir_undef,
	/** #include **/
	cdir_include,
	/** #line **/
	cdir_line,
	/** #error **/
	cdir_error,
	/** #pragma **/
	cdir_pragma,
}
