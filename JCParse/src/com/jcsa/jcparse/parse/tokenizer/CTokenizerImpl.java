package com.jcsa.jcparse.parse.tokenizer;

import com.jcsa.jcparse.lang.ctoken.CToken;
import com.jcsa.jcparse.lang.lexical.CLangKeywordLib;
import com.jcsa.jcparse.lang.ptoken.PCharacterToken;
import com.jcsa.jcparse.lang.ptoken.PCommentToken;
import com.jcsa.jcparse.lang.ptoken.PDirectiveToken;
import com.jcsa.jcparse.lang.ptoken.PFloatingToken;
import com.jcsa.jcparse.lang.ptoken.PHeaderToken;
import com.jcsa.jcparse.lang.ptoken.PIdentifierToken;
import com.jcsa.jcparse.lang.ptoken.PIntegerToken;
import com.jcsa.jcparse.lang.ptoken.PLiteralToken;
import com.jcsa.jcparse.lang.ptoken.PNewlineToken;
import com.jcsa.jcparse.lang.ptoken.PPunctuatorToken;
import com.jcsa.jcparse.lang.ptoken.PToken;
import com.jcsa.jcparse.lang.text.CStream;
import com.jcsa.jcparse.lang.text.CText;

public class CTokenizerImpl implements CTokenizer {

	private CStream stream;
	private CLangKeywordLib keywordLib;
	private PTokenizer ptokenizer;
	private PTokenImprover improver;

	public CTokenizerImpl(CLangKeywordLib lib) {
		if (lib == null)
			throw new IllegalArgumentException("Keywords are not defined");
		stream = null;
		keywordLib = lib;
		ptokenizer = new PTokenizerImpl();
		improver = new PTokenImprover();
	}

	@Override
	public void open(CText text) {
		if (text != null) {
			this.close();
			stream = text.get_stream();
			ptokenizer.open(stream);
		} else
			throw new IllegalArgumentException("Invalid text: null");
	}

	@Override
	public void close() {
		stream = null;
		ptokenizer.close();
	}

	@Override
	public boolean is_openned() {
		return stream != null;
	}

	@Override
	public CText get_source() {
		if (stream == null)
			return null;
		else
			return stream.get_source();
	}

	@Override
	public CToken tokenize() throws Exception {
		if (stream == null)
			throw new IllegalArgumentException("Invalid access: not openned");
		else {
			PToken ptoken;
			CToken ctoken = null;
			do {
				ptoken = ptokenizer.tokenize();

				if (ptoken == null)
					break;
				else if (ptoken instanceof PIdentifierToken)
					ctoken = improver.improve_identifier((PIdentifierToken) ptoken, keywordLib);
				else if (ptoken instanceof PCharacterToken)
					ctoken = improver.improve_character((PCharacterToken) ptoken);
				else if (ptoken instanceof PIntegerToken)
					ctoken = improver.improve_integer((PIntegerToken) ptoken);
				else if (ptoken instanceof PFloatingToken)
					ctoken = improver.improve_floating((PFloatingToken) ptoken);
				else if (ptoken instanceof PLiteralToken)
					ctoken = improver.improve_literal((PLiteralToken) ptoken);
				else if (ptoken instanceof PDirectiveToken)
					ctoken = improver.improve_directive((PDirectiveToken) ptoken);
				else if (ptoken instanceof PHeaderToken)
					ctoken = improver.improve_header((PHeaderToken) ptoken);
				else if (ptoken instanceof PNewlineToken)
					ctoken = improver.improve_newline((PNewlineToken) ptoken);
				else if (ptoken instanceof PPunctuatorToken)
					ctoken = improver.improve_punctuator((PPunctuatorToken) ptoken);
				else if (ptoken instanceof PCommentToken)
					ctoken = null;
				else
					throw new RuntimeException("Unsupported preprocessing token: " + ptoken);
			} while (ctoken == null);

			return ctoken;
		}
	}

	@Override
	public CTokenStream get_stream() throws Exception {
		return new CStaticTokenStream(this);
	}

	protected String derive_error_line(int cursor) throws Exception {
		CText text = stream.get_source();
		int line = text.line_of(cursor);
		int beg = text.index_of(line);
		int end = text.index_of(line + 1);
		return "[" + line + "]:  " + text.substring(beg, end);
	}

}
