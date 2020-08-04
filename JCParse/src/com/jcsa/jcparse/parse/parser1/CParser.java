package com.jcsa.jcparse.parse.parser1;

import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
import com.jcsa.jcparse.parse.tokenizer.CTokenStream;

/**
 * Parser is used to parse token stream into abstract syntax tree (AST)
 * 
 * @author yukimula
 *
 */
public interface CParser {
	/**
	 * parse the token stream to AST-root, which refers to the file scope and
	 * returned
	 * 
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	public AstTranslationUnit parse(CTokenStream stream) throws Exception;
}
