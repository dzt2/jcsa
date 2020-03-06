package com.jcsa.jcparse.lang.parse.tokenizer;

import com.jcsa.jcparse.lang.ptoken.PToken;
import com.jcsa.jcparse.lang.text.CStream;

public class PTokenizerImpl implements PTokenizer {

	private PToken prefix;
	private CStream stream;
	private CScanner scanner;

	public PTokenizerImpl() {
		stream = null;
		prefix = null;
		scanner = new CScanner();
	}

	@Override
	public void open(CStream stream) {
		this.close();
		this.stream = stream;
	}

	@Override
	public boolean is_openned() {
		return (stream != null);
	}

	@Override
	public void close() {
		stream = null;
		prefix = null;
	}

	@Override
	public PToken tokenize() throws Exception {
		if (stream == null)
			throw new RuntimeException("Invalid access: tokenizer is not openned");
		else {
			/* get the next token from scanner */
			PToken next = scanner.ll1_match(stream, prefix);

			/* update tokenizer and return */
			prefix = next;
			return next;
		}
	}

}
