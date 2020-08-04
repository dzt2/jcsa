package com.jcsa.jcparse.parse.tokenizer;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.ctoken.CDirectiveToken;
import com.jcsa.jcparse.lang.ctoken.CNewlineToken;
import com.jcsa.jcparse.lang.ctoken.CToken;

/**
 * <code>CStaticTokenStream</code> will first push all tokens from tokenizer in
 * its cache, and provides these tokens for user by the array list.
 * 
 * @author yukimula
 */
public class CStaticTokenStream implements CTokenStream {
	protected List<CToken> tokens;
	protected int cursor, length;

	public CStaticTokenStream(CTokenizer tokenizer) throws Exception {
		if (!tokenizer.is_openned())
			throw new RuntimeException("tokenizer is not openned");

		tokens = new ArrayList<CToken>();
		CToken ctoken;
		boolean close = true;
		while ((ctoken = tokenizer.tokenize()) != null) {
			if (close) {
				if (ctoken instanceof CNewlineToken)
					continue;
				else if (ctoken instanceof CDirectiveToken)
					close = false;
				tokens.add(ctoken);
			} else {
				tokens.add(ctoken);
				if (ctoken instanceof CNewlineToken)
					close = true;
			}
		}

		cursor = 0;
		length = tokens.size();

		/*
		 * CText text = tokenizer.get_source(); tokenizer.open(text);
		 */
	}

	@Override
	public boolean has_token() {
		return cursor < length;
	}

	@Override
	public int get_cursor() {
		return cursor;
	}

	@Override
	public CToken get_token() {
		if (cursor >= length)
			return null;
		else
			return tokens.get(cursor);
	}

	@Override
	public boolean consume() throws Exception {
		if (cursor >= length)
			return false;
		else {
			cursor++;
			return true;
		}
	}

	@Override
	public void recover(int cursor) throws Exception {
		if (cursor < 0 || cursor > length)
			throw new IllegalArgumentException("Invalid cursor: " + cursor);
		else
			this.cursor = cursor;
	}

}
