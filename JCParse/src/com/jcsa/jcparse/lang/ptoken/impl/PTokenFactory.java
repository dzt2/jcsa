package com.jcsa.jcparse.lang.ptoken.impl;

import com.jcsa.jcparse.lang.lexical.CDirective;
import com.jcsa.jcparse.lang.lexical.CNumberEncode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
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

/**
 * to create PToken
 * 
 * @author yukimula
 */
public class PTokenFactory {
	public PTokenFactory() {
	}

	public PIdentifierToken new_identifier(String name) {
		return new PIdentifierTokenImpl(name);
	}

	public PCharacterToken new_character(String c_sequence) {
		return new PCharacterTokenImpl(false, c_sequence);
	}

	public PCharacterToken new_widen_character(String c_sequence) {
		return new PCharacterTokenImpl(true, c_sequence);
	}

	public PIntegerToken new_integer(CNumberEncode encode, String literal) {
		return new PIntegerTokenImpl(encode, literal, "");
	}

	public PIntegerToken new_integer(CNumberEncode encode, String literal, String suffix) {
		return new PIntegerTokenImpl(encode, literal, suffix);
	}

	public PFloatingToken new_dec_float(String intpart, String fraction, boolean sign, String exponent) {
		return new PFloatingTokenImpl(CNumberEncode.decimal, intpart, fraction, sign, exponent, 'f');
	}

	public PFloatingToken new_dec_double(String intpart, String fraction, boolean sign, String exponent) {
		return new PFloatingTokenImpl(CNumberEncode.decimal, intpart, fraction, sign, exponent, 'd');
	}

	public PFloatingToken new_dec_ldouble(String intpart, String fraction, boolean sign, String exponent) {
		return new PFloatingTokenImpl(CNumberEncode.decimal, intpart, fraction, sign, exponent, 'l');
	}

	public PFloatingToken new_hex_float(String intpart, String fraction, boolean sign, String exponent) {
		return new PFloatingTokenImpl(CNumberEncode.hexical, intpart, fraction, sign, exponent, 'f');
	}

	public PFloatingToken new_hex_double(String intpart, String fraction, boolean sign, String exponent) {
		return new PFloatingTokenImpl(CNumberEncode.hexical, intpart, fraction, sign, exponent, 'd');
	}

	public PFloatingToken new_hex_ldouble(String intpart, String fraction, boolean sign, String exponent) {
		return new PFloatingTokenImpl(CNumberEncode.hexical, intpart, fraction, sign, exponent, 'l');
	}

	public PLiteralToken new_literal_token(String literal) {
		return new PLiteralTokenImpl(false, literal);
	}

	public PLiteralToken new_widen_literal_token(String literal) {
		return new PLiteralTokenImpl(true, literal);
	}

	public PPunctuatorToken new_punctuator_token(CPunctuator punc) {
		return new PPunctuatorTokenImpl(punc);
	}

	public PDirectiveToken new_directive_token(CDirective dir) {
		return new PDirectiveTokenImpl(dir);
	}

	public PHeaderToken new_system_header_token(String path) {
		return new PHeaderTokenImpl(true, path);
	}

	public PHeaderToken new_user_header_token(String path) {
		return new PHeaderTokenImpl(false, path);
	}

	public PNewlineToken new_newline_token() {
		return new PNewlineTokenImpl();
	}

	public PCommentToken new_block_comment(String cmt) {
		return new PCommentTokenImpl(true, cmt);
	}

	public PCommentToken new_line_comment(String cmt) {
		return new PCommentTokenImpl(false, cmt);
	}

}
