package com.jcsa.jcparse.parse.tokenizer;

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
import com.jcsa.jcparse.lang.ptoken.PToken;
import com.jcsa.jcparse.lang.ptoken.impl.PTokenFactory;
import com.jcsa.jcparse.lang.text.CStream;
import com.jcsa.jcparse.lang.text.CText;

/**
 * To scan the characters in source code and produce corresponding PToken(s)
 * 
 * @author yukimula
 */
class CScanner {

	/** to produce PToken as outputs of each match-method **/
	private PTokenFactory factory;
	/** for matching string-cache **/
	private StringBuilder string_buff, string_buff2;

	/**
	 * construct scanner used for building ptoken(s)
	 */
	protected CScanner() {
		factory = new PTokenFactory();
		string_buff = new StringBuilder();
		string_buff2 = new StringBuilder();
	}

	/**
	 * using LL(1) to match the characters and produce tokens.
	 * 
	 * @param stream
	 *            : to provide characters linearly
	 * @param prefix
	 *            : the node produced by scanner at its last call
	 * @return : the next p-token or null when all the chars are scanned.
	 * @throws Exception
	 *             : unable to identify the next token
	 */
	protected PToken ll1_match(CStream stream, PToken prefix) throws Exception {
		this.match_spaces(stream); /* skip all spaces in the cursor */

		if (stream.has_char()) {
			char ch = stream.get_char(); /* LL(1) detection */

			int beg = stream.get_cursor(); /* record beg-ptr */

			PToken next = null; /* find next token */
			if (this.is_include(prefix)) /* #include header */
				next = this.match_header(stream);
			else if (ch == 'L') { /* character | literal --> identifier */
				if ((next = this.match_character(stream)) == null) {
					if ((next = this.match_literal(stream)) == null) {
						next = this.match_identifier(stream);
					}
				}
			} else if (ch == '#') { /* directive -> punctuator */
				if ((next = this.match_directive(stream)) == null)
					next = this.match_punctuator(stream);
			} else if (ch == '/') { /* comment -> punctuator */
				if ((next = this.match_comment(stream)) == null)
					next = this.match_punctuator(stream);
			} else if (ch == '.') { /* floating -> punctuator */
				if ((next = this.match_floating(stream)) == null)
					next = this.match_punctuator(stream);
			} else if (ch == '\'') /* character */
				next = this.match_character(stream);
			else if (ch == '\"') /* literal */
				next = this.match_literal(stream);
			else if (Character.isDigit(ch)) { /* floating -> integer */
				if ((next = this.match_floating(stream)) == null)
					next = this.match_integer(stream);
			} else if (ch == '_' || Character.isLetter(ch))
				next = this.match_identifier(stream);
			else if (ch == CText.LINE_SEPARATOR)
				next = this.match_newline(stream);
			else
				next = this.match_punctuator(stream);

			int end = stream.get_cursor(); /* record tail-ptr */

			if (next == null)
				throw new RuntimeException(
						"Lexical error occurs at: cursor = " + stream.get_cursor() + "; char = \'" + ch + "\'");
			else {
				next.set_location(stream.get_source().get_location(beg, end - beg));
				return next;
			}
		} else
			return null;
	}

	/**
	 * identifier |--> {_, a-z, A-Z} {_, 0-9, a-z, A-Z}*
	 * 
	 * @param stream
	 * @return
	 */
	protected PIdentifierToken match_identifier(CStream stream) throws Exception {
		string_buff.setLength(0); // initialize name cache
		if (stream.has_char()) {
			char ch = stream.get_char();
			if (ch == '_' || Character.isLetter(ch)) { // validate head of
														// identifier
				while (stream.has_char()) { // construct identifier name in
											// string_buff
					ch = stream.get_char();

					if (ch == '_' || Character.isLetter(ch) || Character.isDigit(ch)) {
						stream.consume();
						string_buff.append(ch);
					} else
						break; // find end
				}
			}
		}

		/* return the identifier token */
		if (string_buff.length() > 0)
			return factory.new_identifier(string_buff.toString());
		else
			return null;
	}

	/**
	 * character |--> (L)? ' c-char '
	 * 
	 * @param stream
	 * @return
	 */
	protected PCharacterToken match_character(CStream stream) throws Exception {
		int head = stream.get_cursor();
		boolean widen = false;
		String c_char = null;

		/* match the widen tag */
		widen = this.match_char(stream, 'L');

		if (this.match_char(stream, '\'')) { // match left-border
			c_char = this.match_c_char(stream); // match c-char-seqs
			if (!this.match_char(stream, '\'')) // match right-border
				c_char = null;
		}

		/* match success and produce token */
		if (c_char != null) {
			if (widen)
				return factory.new_widen_character(c_char);
			else
				return factory.new_character(c_char);
		}
		/* match failed, and recover the stream */
		else {
			stream.reset(head);
			return null;
		}
	}

	/**
	 * literal |--> (L)? " (c_str_char)* "
	 * 
	 * @param stream
	 * @return
	 */
	protected PLiteralToken match_literal(CStream stream) throws Exception {
		int head = stream.get_cursor();
		boolean widen = false;

		/* match the widen tag */
		widen = this.match_char(stream, 'L');

		string_buff.setLength(0);
		if (this.match_char(stream, '\"')) { // left border
			String s_char;
			while ((s_char = match_s_char(stream)) != null) // content literal
				string_buff.append(s_char);

			if (this.match_char(stream, '\"')) { // right border
				if (widen)
					return factory.new_literal_token(string_buff.toString());
				else
					return factory.new_widen_literal_token(string_buff.toString());
			}
		}

		stream.reset(head);
		return null; // not matched, recover stream
	}

	/**
	 * integer |--> (oct_digits | dec_digits | hex_digits) (int_suffix)?
	 * 
	 * @param stream
	 * @return
	 */
	protected PIntegerToken match_integer(CStream stream) throws Exception {
		/* declarations */
		char ch;
		CNumberEncode encode;
		String digits = null, suffix;

		/* match the digit sequence and get encode */
		ch = stream.get_char();
		if (ch == '0') {
			if ((digits = this.match_hex_digits(stream)) == null) {
				digits = this.match_oct_digits(stream);
				encode = CNumberEncode.octal;
			} else
				encode = CNumberEncode.hexical;
		} else if (ch >= '1' && ch <= '9') {
			digits = this.match_dec_digits(stream);
			encode = CNumberEncode.decimal;
		} else
			encode = CNumberEncode.decimal;

		if (digits == null)
			return null; // none matched
		else {
			/* match the int suffix */
			suffix = this.match_int_suffix(stream);

			/* return the integer token */
			if (suffix == null)
				return factory.new_integer(encode, digits);
			else
				return factory.new_integer(encode, digits, suffix);
		}
	}

	/**
	 * floating |--> (dec_real | hex_real) (real_suffix)?
	 * 
	 * @param stream
	 * @return
	 */
	protected PFloatingToken match_floating(CStream stream) throws Exception {
		PFloatingToken token = null;
		if ((token = this.match_hex_real(stream)) == null)
			token = this.match_dec_real(stream);
		return token;
	}

	/**
	 * @see CPunctuator definition
	 * @param stream
	 * @return
	 */
	protected PPunctuatorToken match_punctuator(CStream stream) throws Exception {
		CPunctuator punc;

		if (this.match_char(stream, '['))
			punc = CPunctuator.left_bracket;
		else if (this.match_char(stream, ']'))
			punc = CPunctuator.right_bracket;
		else if (this.match_char(stream, '('))
			punc = CPunctuator.left_paranth;
		else if (this.match_char(stream, ')'))
			punc = CPunctuator.right_paranth;
		else if (this.match_char(stream, '{'))
			punc = CPunctuator.left_brace;
		else if (this.match_char(stream, '}'))
			punc = CPunctuator.right_brace;
		else if (this.match_char(stream, '.')) { // . or ...
			if (this.match_string(stream, ".."))
				punc = CPunctuator.ellipsis;
			else
				punc = CPunctuator.dot;
		} else if (this.match_char(stream, '-')) { // ->, --, -, -=
			if (this.match_char(stream, '>')) // ->
				punc = CPunctuator.arrow;
			else if (this.match_char(stream, '-')) // --
				punc = CPunctuator.decrement;
			else if (this.match_char(stream, '=')) // -=
				punc = CPunctuator.ari_sub_assign;
			else
				punc = CPunctuator.ari_sub; // -
		} else if (this.match_char(stream, '+')) { // ++, +, +=
			if (this.match_char(stream, '+')) // ++
				punc = CPunctuator.increment;
			else if (this.match_char(stream, '=')) // +=
				punc = CPunctuator.ari_add_assign;
			else
				punc = CPunctuator.ari_add; // +
		} else if (this.match_char(stream, '&')) { // &, &&, &=
			if (this.match_char(stream, '&')) // &&
				punc = CPunctuator.log_and;
			else if (this.match_char(stream, '=')) // &=
				punc = CPunctuator.bit_and_assign;
			else
				punc = CPunctuator.bit_and; // &
		} else if (this.match_char(stream, '*')) { // *, *=
			if (this.match_char(stream, '=')) // *=
				punc = CPunctuator.ari_mul_assign;
			else
				punc = CPunctuator.ari_mul; // *
		} else if (this.match_char(stream, '~')) { // ~
			punc = CPunctuator.bit_not;
		} else if (this.match_char(stream, '!')) { // !, !=
			if (this.match_char(stream, '=')) // !=
				punc = CPunctuator.not_equals;
			else
				punc = CPunctuator.log_not; // !
		} else if (this.match_char(stream, '/')) { // /, /=
			if (this.match_char(stream, '=')) // /=
				punc = CPunctuator.ari_div_assign;
			else
				punc = CPunctuator.ari_div; // /
		} else if (this.match_char(stream, '%')) { // %, %=, %>, %:, %:%:
			if (this.match_string(stream, ":%:")) // %:%:
				punc = CPunctuator.hash_hash;
			else if (this.match_char(stream, ':')) // %:
				punc = CPunctuator.hash;
			else if (this.match_char(stream, '>')) // %>
				punc = CPunctuator.right_brace;
			else if (this.match_char(stream, '=')) // %=
				punc = CPunctuator.ari_mod_assign;
			else
				punc = CPunctuator.ari_mod; // %
		} else if (this.match_char(stream, '<')) { // <<, <<=, <=, <%, <:, <
			if (this.match_char(stream, '<')) { // <<, <<=
				if (this.match_char(stream, '=')) // <<=
					punc = CPunctuator.left_shift_assign;
				else
					punc = CPunctuator.left_shift; // <<
			} else if (this.match_char(stream, '=')) // <=
				punc = CPunctuator.smaller_eq;
			else if (this.match_char(stream, '%')) // <%
				punc = CPunctuator.left_brace;
			else if (this.match_char(stream, ':')) // <:
				punc = CPunctuator.left_bracket;
			else
				punc = CPunctuator.smaller_tn; // <
		} else if (this.match_char(stream, '>')) { // >>, >>=, >, >=
			if (this.match_char(stream, '>')) { // >>, >>=
				if (this.match_char(stream, '=')) // >>=
					punc = CPunctuator.right_shift_assign;
				else
					punc = CPunctuator.right_shift; // >>
			} else if (this.match_char(stream, '=')) // >=
				punc = CPunctuator.greater_eq;
			else
				punc = CPunctuator.greater_tn; // >
		} else if (this.match_char(stream, '=')) { // ==, =
			if (this.match_char(stream, '=')) // ==
				punc = CPunctuator.equal_with;
			else
				punc = CPunctuator.assign; // =
		} else if (this.match_char(stream, '^')) { // ^, ^=
			if (this.match_char(stream, '=')) // ^=
				punc = CPunctuator.bit_xor_assign;
			else
				punc = CPunctuator.bit_xor; // ^
		} else if (this.match_char(stream, '|')) { // |, ||, |=
			if (this.match_char(stream, '|')) // ||
				punc = CPunctuator.log_or;
			else if (this.match_char(stream, '=')) // |=
				punc = CPunctuator.bit_or_assign;
			else
				punc = CPunctuator.bit_or; // |
		} else if (this.match_char(stream, '?')) // ?
			punc = CPunctuator.question;
		else if (this.match_char(stream, ':')) // :
			punc = CPunctuator.colon;
		else if (this.match_char(stream, ';')) // ;
			punc = CPunctuator.semicolon;
		else if (this.match_char(stream, ',')) // ,
			punc = CPunctuator.comma;
		else if (this.match_char(stream, '#')) { // #, ##
			if (this.match_char(stream, '#')) // ##
				punc = CPunctuator.hash_hash;
			else
				punc = CPunctuator.hash; // #
		} else
			punc = CPunctuator.lex_error; // #error: not punctuator

		if (punc == CPunctuator.lex_error)
			return null;
		else
			return factory.new_punctuator_token(punc);
	}

	/**
	 * +
	 * 
	 * @see CDirective definition
	 * @param stream
	 * @return
	 */
	protected PDirectiveToken match_directive(CStream stream) throws Exception {
		int origin = stream.get_cursor();
		CDirective dir = CDirective.invalid_cdir;

		if (this.match_char(stream, '#')) {
			this.match_spaces(stream); /* skip the spaces */

			/* get the letters following # */
			string_buff.setLength(0);
			while (stream.has_char()) {
				char ch = stream.get_char();
				if (Character.isLetter(ch)) {
					string_buff.append(ch);
					stream.consume();
				} else
					break;
			}
			String name = string_buff.toString();

			if (name.equals("if"))
				dir = CDirective.cdir_if;
			else if (name.equals("ifdef"))
				dir = CDirective.cdir_ifdef;
			else if (name.equals("ifndef"))
				dir = CDirective.cdir_ifndef;
			else if (name.equals("elif"))
				dir = CDirective.cdir_elif;
			else if (name.equals("else"))
				dir = CDirective.cdir_else;
			else if (name.equals("endif"))
				dir = CDirective.cdir_endif;
			else if (name.equals("define"))
				dir = CDirective.cdir_define;
			else if (name.equals("undef"))
				dir = CDirective.cdir_undef;
			else if (name.equals("include"))
				dir = CDirective.cdir_include;
			else if (name.equals("line"))
				dir = CDirective.cdir_line;
			else if (name.equals("error"))
				dir = CDirective.cdir_error;
			else if (name.equals("pragma"))
				dir = CDirective.cdir_pragma;
			else
				dir = CDirective.invalid_cdir;
		}

		if (dir == CDirective.invalid_cdir) {
			stream.reset(origin);
			return null;
		} else
			return factory.new_directive_token(dir);
	}

	/**
	 * header |--> "<" {chars/{\", >, < }}* ">"
	 * 
	 * @param stream
	 * @return
	 */
	protected PHeaderToken match_header(CStream stream) throws Exception {
		PHeaderToken token;
		if ((token = this.match_system_header(stream)) == null)
			token = this.match_user_header(stream);
		return token;
	}

	/**
	 * comment |--> // comment | / * comment * /
	 * 
	 * @param stream
	 * @return
	 */
	protected PCommentToken match_comment(CStream stream) throws Exception {
		PCommentToken token;
		if ((token = this.match_block_comment(stream)) == null)
			token = this.match_line_comment(stream);
		return token;
	}

	/**
	 * newline |--> '\n'
	 * 
	 * @param stream
	 * @return
	 */
	protected PNewlineToken match_newline(CStream stream) throws Exception {
		if (this.match_char(stream, CText.LINE_SEPARATOR))
			return factory.new_newline_token();
		else
			return null;
	}

	/**
	 * c_char |--> single_char - {', \, newline} <br>
	 * |--> \', \", \?, \\, \a, \b, \f, \n, \r, \t, \v <br>
	 * |--> \ (0-7)+ <br>
	 * |--> \x (0-9|a-z|A-Z)+ <br>
	 * 
	 * @param stream
	 * @return
	 */
	private String match_c_char(CStream stream) throws Exception {
		int head = stream.get_cursor();
		string_buff2.setLength(0);

		/* determine the segment of c-char-seq */
		if (stream.has_char()) {
			char ch = stream.get_char();
			stream.consume();

			if (ch == '\'')
				; // not '
			else if (ch == CText.LINE_SEPARATOR)
				; // not \n
			else if (ch == '\\') { // escape \x
				string_buff2.append(ch);

				ch = stream.get_char(); // get head following \
				stream.consume();
				string_buff2.append(ch);

				if (this.is_oct_digit(ch)) { // octal char
					while (stream.has_char()) {
						ch = stream.get_char();
						if (this.is_oct_digit(ch)) {
							stream.consume();
							string_buff2.append(ch);
						} else
							break;
					}
				} else if (ch == '\'' || ch == '"' || ch == '?' || ch == '\\' || ch == 'a' || ch == 'b' || ch == 'f'
						|| ch == 'n' || ch == 'r' || ch == 't' || ch == 'v') { // escape
																				// char
				} else if (ch == 'x') { // hexical char
					while (stream.has_char()) {
						ch = stream.get_char();
						if (this.is_hex_digit(ch)) {
							stream.consume();
							string_buff2.append(ch);
						} else
							break;
					}
				} else
					string_buff2.setLength(0); // invalid
			} else
				string_buff2.append(ch); // single-char
		}

		/* return the derived character */
		if (string_buff2.length() == 0) {
			stream.reset(head);
			return null;
		} else
			return string_buff2.toString();
	}

	/**
	 * str_char |--> single_char - {", \, newline} <br>
	 * |--> \', \", \?, \\, \a, \b, \f, \n, \r, \t, \v <br>
	 * |--> \ (0-7)+ <br>
	 * |--> \x (0-9|a-z|A-Z)+ <br>
	 * 
	 * @param stream
	 * @return
	 */
	private String match_s_char(CStream stream) throws Exception {
		int head = stream.get_cursor();
		string_buff2.setLength(0);

		/* determine the segment of c-char-seq */
		if (stream.has_char()) {
			char ch = stream.get_char();
			stream.consume();

			if (ch == '\"')
				; // not '
			else if (ch == CText.LINE_SEPARATOR)
				; // not \n
			else if (ch == '\\') { // escape \x
				string_buff2.append(ch);

				ch = stream.get_char(); // get head following \
				stream.consume();
				string_buff2.append(ch);

				if (this.is_oct_digit(ch)) { // octal char
					while (stream.has_char()) {
						ch = stream.get_char();
						if (this.is_oct_digit(ch)) {
							stream.consume();
							string_buff2.append(ch);
						} else
							break;
					}
				} else if (ch == '\'' || ch == '"' || ch == '?' || ch == '\\' || ch == 'a' || ch == 'b' || ch == 'f'
						|| ch == 'n' || ch == 'r' || ch == 't' || ch == 'v') { // escape
																				// char
				} else if (ch == 'x') { // hexical char
					while (stream.has_char()) {
						ch = stream.get_char();
						if (this.is_hex_digit(ch)) {
							stream.consume();
							string_buff2.append(ch);
						} else
							break;
					}
				} else
					string_buff2.setLength(0); // invalid
			} else
				string_buff2.append(ch); // single-char
		}

		/* return the derived character */
		if (string_buff2.length() == 0) {
			stream.reset(head);
			return null;
		} else
			return string_buff2.toString();
	}

	/**
	 * dec_digits |--> (1-9) (0-9)*
	 * 
	 * @param stream
	 * @return : all digits matched
	 */
	private String match_dec_digits(CStream stream) throws Exception {
		string_buff2.setLength(0);

		if (stream.has_char()) {
			char ch = stream.get_char();
			if (ch >= '1' && ch <= '9') { // validate head
				while (stream.has_char()) { // construct int body
					ch = stream.get_char();
					if (!this.is_dec_digit(ch))
						break;
					else {
						string_buff2.append(ch);
						stream.consume();
					}
				}
			}
		}

		if (string_buff2.length() == 0)
			return null;
		else
			return string_buff2.toString();
	}

	/**
	 * oct_digits |--> 0 (0-7)*
	 * 
	 * @param stream
	 * @return : all digits matched
	 */
	private String match_oct_digits(CStream stream) throws Exception {
		string_buff2.setLength(0);

		if (this.match_char(stream, '0')) {
			string_buff2.append('0');

			while (stream.has_char()) {
				char ch = stream.get_char();
				if (this.is_oct_digit(ch)) {
					string_buff2.append(ch);
					stream.consume();
				} else
					break;
			}
		}

		if (string_buff2.length() == 0)
			return null;
		else
			return string_buff2.toString();
	}

	/**
	 * hex_digits |--> 0x|0X (0-9|a-z|A-Z)+
	 * 
	 * @param stream
	 * @return : all digits (without 0x) matched
	 */
	private String match_hex_digits(CStream stream) throws Exception {
		int origin = stream.get_cursor();
		string_buff2.setLength(0);

		if (this.match_string(stream, "0x") || this.match_string(stream, "0X")) {
			while (stream.has_char()) {
				char ch = stream.get_char();
				if (this.is_hex_digit(ch)) {
					string_buff2.append(ch);
					stream.consume();
				} else
					break;
			}
		}

		if (string_buff2.length() == 0) {
			stream.reset(origin);
			return null;
		} else
			return string_buff2.toString();
	}

	/**
	 * int_suffix |--> (u|U) (l|ll|L|LL)? <br>
	 * |--> (l|ll|L|LL) (u|U)? <br>
	 * 
	 * @param stream
	 * @return
	 */
	private String match_int_suffix(CStream stream) throws Exception {
		string_buff2.setLength(0);

		if (this.match_char(stream, 'u') || this.match_char(stream, 'U')) {
			if (this.match_char(stream, 'l')) {
				if (this.match_char(stream, 'l'))
					string_buff2.append("ll");
				else
					string_buff2.append("l");
			} else if (this.match_char(stream, 'L')) {
				if (this.match_char(stream, 'L'))
					string_buff2.append("ll");
				else
					string_buff2.append("l");
			}
			string_buff2.append('u');
		} else if (this.match_char(stream, 'l')) {
			if (this.match_char(stream, 'l'))
				string_buff2.append("ll");
			else
				string_buff2.append("l");

			if (this.match_char(stream, 'u') || this.match_char(stream, 'U'))
				string_buff2.append('u');
		} else if (this.match_char(stream, 'L')) {
			if (this.match_char(stream, 'L'))
				string_buff2.append("ll");
			else
				string_buff2.append("l");

			if (this.match_char(stream, 'u') || this.match_char(stream, 'U'))
				string_buff2.append('u');
		}

		if (string_buff2.length() == 0)
			return null;
		else
			return string_buff2.toString();
	}

	/**
	 * dec_real |--> dec_fraction (dec_exponent)? (real_suffix)? |-->
	 * dec_digit_seq dec_exponent (real_suffix)?
	 * 
	 * @param stream
	 * @return
	 */
	private PFloatingToken match_dec_real(CStream stream) throws Exception {
		/* declarations */
		int origin = stream.get_cursor();
		String[] fractions;
		Object[] exponents;
		char suffix;
		String intpart;

		if ((fractions = this.match_dec_fraction(stream)) != null) {
			/* reconstruct fractions */
			if (fractions[0] == null)
				fractions[0] = "0";
			else if (fractions[1] == null)
				fractions[1] = "0";

			/* reconstruct exponents */
			exponents = this.match_dec_exponent(stream);
			if (exponents == null) {
				exponents = new Object[2];
				exponents[0] = Boolean.valueOf(true);
				exponents[1] = "0";
			}

			/* return results */
			suffix = this.match_real_suffix(stream);
			switch (suffix) {
			case 'd':
				return factory.new_dec_double(fractions[0], fractions[1], (Boolean) exponents[0],
						(String) exponents[1]);
			case 'f':
				return factory.new_dec_float(fractions[0], fractions[1], (Boolean) exponents[0], (String) exponents[1]);
			case 'l':
				return factory.new_dec_ldouble(fractions[0], fractions[1], (Boolean) exponents[0],
						(String) exponents[1]);
			default:
				throw new RuntimeException("Invalid suffix: " + suffix);
			}
		} else if ((intpart = this.match_dec_digit_sequence(stream)) != null) {
			if ((exponents = this.match_dec_exponent(stream)) != null) {
				/* return results */
				suffix = this.match_real_suffix(stream);
				switch (suffix) {
				case 'd':
					return factory.new_dec_double(intpart, "0", (Boolean) exponents[0], (String) exponents[1]);
				case 'f':
					return factory.new_dec_float(intpart, "0", (Boolean) exponents[0], (String) exponents[1]);
				case 'l':
					return factory.new_dec_ldouble(intpart, "0", (Boolean) exponents[0], (String) exponents[1]);
				default:
					throw new RuntimeException("Invalid suffix: " + suffix);
				}
			}
		}

		stream.reset(origin);
		return null;
	}

	/**
	 * dec_fraction |--> digit_sequence . (digit_sequence)? <br>
	 * |--> . digit_sequence
	 * 
	 * @param stream
	 * @return : [int_part, frac_part]
	 */
	private String[] match_dec_fraction(CStream stream) throws Exception {
		int origin = stream.get_cursor();
		String intpart = null, frapart = null;

		if ((intpart = this.match_dec_digit_sequence(stream)) != null) {
			if (this.match_char(stream, '.')) {
				frapart = this.match_dec_digit_sequence(stream);
			} else
				intpart = null;
		} else if (this.match_char(stream, '.')) {
			frapart = this.match_dec_digit_sequence(stream);
		} else
			frapart = null;

		if (intpart == null && frapart == null) {
			stream.reset(origin);
			return null;
		} else {
			String[] ans = new String[2];
			ans[0] = intpart;
			ans[1] = frapart;
			return ans;
		}
	}

	/**
	 * dec_exponent |--> (e|E) (sign)? (digit_sequence)
	 * 
	 * @param stream
	 * @return : [sign; exponent]
	 */
	private Object[] match_dec_exponent(CStream stream) throws Exception {
		/* declarations */
		int origin = stream.get_cursor();
		boolean sign = true;
		String expn = null;

		if (this.match_char(stream, 'e') // match the head
				|| this.match_char(stream, 'E')) {

			if (this.match_char(stream, '-')) /* match the sign */
				sign = false;
			else if (this.match_char(stream, '+'))
				sign = true;

			/* match digits */
			expn = this.match_dec_digit_sequence(stream);
		}

		if (expn == null) {
			stream.reset(origin);
			return null;
		} else {
			Object[] ans = new Object[2];
			ans[0] = Boolean.valueOf(sign);
			ans[1] = expn;
			return ans;
		}
	}

	/**
	 * hex_real |--> 0x|0X hex_fraction (hex_exponent)? (real_suffix)?
	 * 
	 * @param stream
	 * @return
	 */
	private PFloatingToken match_hex_real(CStream stream) throws Exception {
		/* declarations */
		int origin = stream.get_cursor();
		String[] fractions;
		Object[] exponents;
		char suffix;
		String intpart;

		if (this.match_string(stream, "0x") || this.match_string(stream, "0X")) { // head
																					// match

			/* hex_fractions hex_exponent (real_suffix)? */
			if ((fractions = this.match_hex_fraction(stream)) != null) {
				/* reconstruct fractions */
				if (fractions[0] == null)
					fractions[0] = "0";
				else if (fractions[1] == null)
					fractions[1] = "0";

				if ((exponents = this.match_hex_exponent(stream)) != null) {
					suffix = this.match_real_suffix(stream);
					switch (suffix) {
					case 'd':
						return factory.new_hex_double(fractions[0], fractions[1], (Boolean) exponents[0],
								(String) exponents[1]);
					case 'f':
						return factory.new_hex_float(fractions[0], fractions[1], (Boolean) exponents[0],
								(String) exponents[1]);
					case 'l':
						return factory.new_hex_ldouble(fractions[0], fractions[1], (Boolean) exponents[0],
								(String) exponents[1]);
					default:
						throw new RuntimeException("Invalid real suffix: " + suffix);
					}
				}
			}
			/* hex_digit_seq hex_exponent (real_suffix)? */
			else if ((intpart = this.match_hex_digit_sequence(stream)) != null) {
				if ((exponents = this.match_hex_exponent(stream)) != null) {
					suffix = this.match_real_suffix(stream);
					switch (suffix) {
					case 'd':
						return factory.new_hex_double(intpart, "0", (Boolean) exponents[0], (String) exponents[1]);
					case 'f':
						return factory.new_hex_float(intpart, "0", (Boolean) exponents[0], (String) exponents[1]);
					case 'l':
						return factory.new_hex_ldouble(intpart, "0", (Boolean) exponents[0], (String) exponents[1]);
					default:
						throw new RuntimeException("Invalid real suffix: " + suffix);
					}
				}
			}
		}

		stream.reset(origin);
		return null;
	}

	/**
	 * hex_fraction |--> hex_digit_sequence . (hex_digit_sequence)? <br>
	 * |--> . hex_digit_sequence
	 * 
	 * @param stream
	 * @return
	 */
	private String[] match_hex_fraction(CStream stream) throws Exception {
		/* declarations */
		int origin = stream.get_cursor();
		String intpart = null, frapart = null;

		/* hex_digit_sequence . (hex_digit_sequence)? */
		if ((intpart = this.match_hex_digit_sequence(stream)) != null) {
			if (this.match_char(stream, '.')) {
				frapart = this.match_hex_digit_sequence(stream);
			} else
				intpart = null; /* hex_digits (only an integer) */
		}
		/* . hex_digit_sequence */
		else if (this.match_char(stream, '.')) {
			frapart = this.match_hex_digit_sequence(stream);
		}

		if (intpart == null && frapart == null) {
			stream.reset(origin);
			return null;
		} else {
			String[] ans = new String[2];
			ans[0] = intpart;
			ans[1] = frapart;
			return ans;
		}
	}

	/**
	 * hex_exponent |--> (p|P) (sign)? digit_sequence
	 * 
	 * @param stream
	 * @return
	 */
	private Object[] match_hex_exponent(CStream stream) throws Exception {
		int origin = stream.get_cursor();
		String expn = null;
		boolean sign = true;

		/* match the exponent-prefix */
		if (this.match_char(stream, 'p') || this.match_char(stream, 'P')) {
			/* match the exponent sign */
			if (this.match_char(stream, '-'))
				sign = false;
			else if (this.match_char(stream, '+'))
				sign = true;

			/* match the digit-sequence in exponent */
			expn = this.match_dec_digit_sequence(stream);
		}

		/* not valid exponent */
		if (expn == null) {
			stream.reset(origin);
			return null;
		} else {
			Object[] ans = new Object[2];
			ans[0] = sign;
			ans[1] = expn;
			return ans;
		}
	}

	/**
	 * real_suffix |--> f | F | l | L
	 * 
	 * @param stream
	 * @return
	 */
	private Character match_real_suffix(CStream stream) throws Exception {
		if (this.match_char(stream, 'f') || this.match_char(stream, 'F'))
			return 'f';
		else if (this.match_char(stream, 'l') || this.match_char(stream, 'L'))
			return 'l';
		else
			return 'd';
	}

	/**
	 * dec_digit_sequence |--> (0-9)+
	 * 
	 * @param stream
	 * @return
	 */
	private String match_dec_digit_sequence(CStream stream) throws Exception {
		string_buff2.setLength(0);
		while (stream.has_char()) {
			char ch = stream.get_char();
			if (this.is_dec_digit(ch)) {
				string_buff2.append(ch);
				stream.consume();
			} else
				break;
		}

		if (string_buff2.length() == 0)
			return null;
		else
			return string_buff2.toString();
	}

	/**
	 * hex_digit_sequence |--> (0-9|a-z|A-Z)+
	 * 
	 * @param stream
	 * @return
	 */
	private String match_hex_digit_sequence(CStream stream) throws Exception {
		string_buff2.setLength(0);
		while (stream.has_char()) {
			char ch = stream.get_char();
			if (this.is_hex_digit(ch)) {
				string_buff2.append(ch);
				stream.consume();
			} else
				break;
		}

		if (string_buff2.length() == 0)
			return null;
		else
			return string_buff2.toString();
	}

	/**
	 * sys_header |--> < chars >
	 * 
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	private PHeaderToken match_system_header(CStream stream) throws Exception {
		int origin = stream.get_cursor();

		string_buff2.setLength(0);
		if (this.match_char(stream, '<')) {
			while (stream.has_char()) {
				char ch = stream.get_char();
				if (ch == '>')
					break;
				else {
					string_buff2.append(ch);
					stream.consume();
				}
			}

			if (!this.match_char(stream, '>'))
				string_buff2.setLength(0);
		}

		if (string_buff2.length() == 0) {
			stream.reset(origin);
			return null;
		} else
			return factory.new_system_header_token(string_buff2.toString());
	}

	/**
	 * usr_header |--> " chars "
	 * 
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	private PHeaderToken match_user_header(CStream stream) throws Exception {
		int origin = stream.get_cursor();

		string_buff2.setLength(0);
		if (this.match_char(stream, '\"')) {
			while (stream.has_char()) {
				char ch = stream.get_char();
				if (ch == '\"')
					break;
				else {
					string_buff2.append(ch);
					stream.consume();
				}
			}

			if (!this.match_char(stream, '\"'))
				string_buff2.setLength(0);
		}

		if (string_buff2.length() == 0) {
			stream.reset(origin);
			return null;
		} else
			return factory.new_user_header_token(string_buff2.toString());
	}

	/**
	 * 
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	private PCommentToken match_block_comment(CStream stream) throws Exception {
		if (this.match_string(stream, "/*")) {
			string_buff.setLength(0);
			while (stream.has_char()) {
				char ch = stream.get_char();
				if (ch == '*') {
					if (this.match_string(stream, "*/"))
						return factory.new_block_comment(string_buff.toString());
				}
				string_buff.append(ch);
				stream.consume();
			}

			throw new RuntimeException("Invalid #EOF: expected with */ at block comment.");
		} else
			return null;
	}

	/**
	 * 
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	private PCommentToken match_line_comment(CStream stream) throws Exception {
		if (this.match_string(stream, "//")) {
			string_buff.setLength(0);
			while (stream.has_char()) {
				char ch = stream.get_char();
				if (ch == CText.LINE_SEPARATOR)
					break;
				else {
					string_buff.append(ch);
					stream.consume();
				}
			}

			return factory.new_line_comment(string_buff.toString());
		} else
			return null;
	}

	/**
	 * whether the current segment starts with specified character
	 * 
	 * @param stream
	 * @param ch
	 * @return
	 */
	private boolean match_char(CStream stream, char ch) throws Exception {
		if (stream.has_char()) {
			char ch2 = stream.get_char();
			if (ch == ch2)
				stream.consume();
			return ch == ch2;
		} else
			return false;
	}

	/**
	 * whether the current segment starts with specified characters
	 * 
	 * @param stream
	 * @param str
	 * @return
	 */
	private boolean match_string(CStream stream, String str) throws Exception {
		int origin = stream.get_cursor(), i, n = str.length();

		for (i = 0; i < n && stream.has_char(); i++) {
			char ch1 = str.charAt(i);
			char ch2 = stream.get_char();
			if (ch1 != ch2)
				break;
			else
				stream.consume();
		}

		if (i >= n)
			return true;
		else {
			stream.reset(origin);
			return false;
		}
	}

	/**
	 * skip all spaces except newline
	 * 
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	private int match_spaces(CStream stream) throws Exception {
		int spaces = 0;
		while (stream.has_char()) {
			char ch = stream.get_char();

			if (!Character.isWhitespace(ch))
				break;
			else if (ch == CText.LINE_SEPARATOR)
				break;

			stream.consume();
			spaces++;
		}
		return spaces;
	}

	/**
	 * oct_digit |--> (0-7)
	 * 
	 * @param ch
	 * @return
	 */
	private boolean is_oct_digit(char ch) {
		return (ch >= '0') && (ch <= '7');
	}

	/**
	 * dec_digit |--> (0-9)
	 * 
	 * @param ch
	 * @return
	 */
	private boolean is_dec_digit(char ch) {
		return ch >= '0' && ch <= '9';
	}

	/**
	 * hex_digit |--> (0-9 | a-z | A-Z)
	 * 
	 * @param ch
	 * @return
	 */
	private boolean is_hex_digit(char ch) {
		return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
	}

	/**
	 * whether the token refers to #include
	 * 
	 * @param token
	 * @return
	 */
	private boolean is_include(PToken token) {
		if (token != null) {
			if (token instanceof PDirectiveToken)
				return ((PDirectiveToken) token).get_directive() == CDirective.cdir_include;
		}
		return false;
	}

}
