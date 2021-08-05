package com.jcsa.jcparse.parse.tokenizer;

import com.jcsa.jcparse.lang.ctoken.CConstantToken;
import com.jcsa.jcparse.lang.ctoken.CDirectiveToken;
import com.jcsa.jcparse.lang.ctoken.CHeaderToken;
import com.jcsa.jcparse.lang.ctoken.CLiteralToken;
import com.jcsa.jcparse.lang.ctoken.CNewlineToken;
import com.jcsa.jcparse.lang.ctoken.CPunctuatorToken;
import com.jcsa.jcparse.lang.ctoken.CToken;
import com.jcsa.jcparse.lang.ctoken.impl.CTokenFactory;
import com.jcsa.jcparse.lang.lexical.CLangKeywordLib;
import com.jcsa.jcparse.lang.ptoken.PCharacterToken;
import com.jcsa.jcparse.lang.ptoken.PDirectiveToken;
import com.jcsa.jcparse.lang.ptoken.PFloatingToken;
import com.jcsa.jcparse.lang.ptoken.PHeaderToken;
import com.jcsa.jcparse.lang.ptoken.PIdentifierToken;
import com.jcsa.jcparse.lang.ptoken.PIntegerToken;
import com.jcsa.jcparse.lang.ptoken.PLiteralToken;
import com.jcsa.jcparse.lang.ptoken.PNewlineToken;
import com.jcsa.jcparse.lang.ptoken.PPunctuatorToken;

/**
 * To improve the PToken and derive corresponding information. <br>
 * 1. PIdentifierToken |--> CIdentifierToken | CKeywordToken <br>
 * 2. PCharacterToken |--> CConstantToken <br>
 * 3. PIntegerToken |--> CConstantToken <br>
 * 4. PFloatingToken |--> CConstantToken <br>
 * 5. PLiteralToken |--> CLiteralToken <br>
 * 6. PDirectiveToken |--> CDirectiveToken <br>
 * 7. PHeaderToken |--> CHeaderToken <br>
 * 8. PPunctuatorToken |--> CPunctuatorToken <br>
 * 9. PNewlineToken |--> CNewlineToken <br>
 * a. PCommentToken |--> <i>none</i>
 *
 * @author yukimula
 */
public class PTokenImprover {

	protected CTokenFactory factory;
	private StringBuilder str_buff;

	protected PTokenImprover() {
		this.factory = new CTokenFactory();
		this.str_buff = new StringBuilder();
	}

	/**
	 * Improve PIdentifierToken to either CKeywordToken or CIdentifierToken,
	 * depending on the names of keywords defined in keyword-library.
	 *
	 * @param ptoken
	 * @param lib
	 * @return
	 * @throws Exception
	 */
	protected CToken improve_identifier(PIdentifierToken ptoken, CLangKeywordLib lib) throws Exception {
		CToken ctoken; /* declarations */

		String name = ptoken.get_name();
		if (lib.has_keyword(name)) /* when it's a keyword defined in lang */
			ctoken = factory.new_keyword(lib.get_keyword(name));
		/* a normal name for identifier */
		else
			ctoken = factory.new_identifier(ptoken.get_name());

		/* update location and return */
		ctoken.set_location(ptoken.get_location());
		return ctoken;
	}

	/**
	 * Improve PCharacterToken to CConstantToken
	 *
	 * @param ptoken
	 * @return
	 * @throws Exception
	 */
	protected CConstantToken improve_character(PCharacterToken ptoken) throws Exception {
		CConstantToken ctoken = factory
				.new_constant(); /* new a constant token */

		String str = ptoken
				.get_char_sequence(); /* parse character from string */
		Object[] ans = parse_char(str, 0);
		Integer index = (Integer) ans[0];
		Character ch = (Character) ans[1];

		if (index < str.length()) /* too more character */
			throw new IllegalArgumentException("Unable to parse: \'" + str + "\'");
		else if (ch == null) /* invalid character */
			throw new RuntimeException("Failed to parse: \'" + str + "\'");
		else if (ptoken.is_widen()) /* widen character */
			ctoken.get_constant().set_wchar(ch);
		else
			ctoken.get_constant().set_char(ch);

		ctoken.set_location(ptoken.get_location());
		return ctoken;
	}

	/**
	 * Improve PLiteralToken to CLiteralToken, this will parse string to its
	 * environmental format.
	 *
	 * @param ptoken
	 * @return
	 * @throws Exception
	 */
	protected CLiteralToken improve_literal(PLiteralToken ptoken) throws Exception {
		String str = ptoken.get_literal();
		int k, n = str.length();
		Character ch;
		Object[] ans;

		str_buff.setLength(0);
		for (k = 0; k < n;) {
			ans = this.parse_char(str, k); /* get the next exec-char */
			k = (Integer) ans[0];
			ch = (Character) ans[1];

			if (ch == null)
				throw new IllegalArgumentException("Fail to interpret: \"" + str + "\"");
			else
				str_buff.append(ch);
		}

		/* new the literal token and update its location */
		CLiteralToken ctoken = factory.new_literal(str_buff.toString());
		ctoken.set_location(ptoken.get_location());
		return ctoken;
	}

	/**
	 * Improve PIntegerToken to integer constant as CConstantToken
	 *
	 * @param ptoken
	 * @return
	 * @throws Exception
	 */
	protected CConstantToken improve_integer(PIntegerToken ptoken) throws Exception {
		/* new a constant token */
		CConstantToken ctoken = factory.new_constant();

		/* determine the step for multiplication */
		int step;
		switch (ptoken.get_encode()) {
		case octal:
			step = 8;
			break;
		case decimal:
			step = 10;
			break;
		case hexical:
			step = 16;
			break;
		default:
			throw new IllegalArgumentException("Unknown encode: " + ptoken.get_encode());
		}

		/* calculate number */
		String str = ptoken.get_int_literal();
		int i, n = str.length();
		long count = 0;
		for (i = 0; i < n; i++) {
			int num = parse_char_number(str.charAt(i));
			count = count * step + num;
		}

		/* set the value for constant */
		String suffix = ptoken.get_int_suffix();
		if (suffix.equals("u"))
			ctoken.get_constant().set_uint((int) count);
		else if (suffix.equals("lu"))
			ctoken.get_constant().set_ulong(count);
		else if (suffix.equals("llu"))
			ctoken.get_constant().set_ullong(count);
		else if (suffix.equals("ll"))
			ctoken.get_constant().set_llong(count);
		else if (suffix.equals("l"))
			ctoken.get_constant().set_long(count);
		else
			ctoken.get_constant().set_int((int) count);

		/* update location and return */
		ctoken.set_location(ptoken.get_location());
		return ctoken;
	}

	/**
	 * Improve PFloatingToken to CConstantToken, this will parse the floating
	 * string to a double value represented in Java, which might be <i>not that
	 * precise</i>.
	 *
	 * @param ptoken
	 * @return
	 * @throws Exception
	 */
	protected CConstantToken improve_floating(PFloatingToken ptoken) throws Exception {
		CConstantToken ctoken = factory.new_constant();

		switch (ptoken.get_encode()) {
		case decimal:
			this.improve_dec_floating(ptoken, ctoken);
			break;
		case hexical:
			this.improve_hex_floating(ptoken, ctoken);
			break;
		default:
			throw new IllegalArgumentException("Unknown encode: " + ptoken.get_encode());
		}

		ctoken.set_location(ptoken.get_location());
		return ctoken;
	}

	/**
	 * Improve directive token
	 *
	 * @param ptoken
	 * @return
	 * @throws Exception
	 */
	protected CDirectiveToken improve_directive(PDirectiveToken ptoken) throws Exception {
		CDirectiveToken ctoken = factory.new_directive(ptoken.get_directive());
		ctoken.set_location(ptoken.get_location());
		return ctoken;
	}

	/**
	 * Improve header token
	 *
	 * @param ptoken
	 * @return
	 * @throws Exception
	 */
	protected CHeaderToken improve_header(PHeaderToken ptoken) throws Exception {
		CHeaderToken ctoken;
		if (ptoken.is_system())
			ctoken = factory.new_system_header(ptoken.get_path());
		else
			ctoken = factory.new_user_header(ptoken.get_path());
		ctoken.set_location(ptoken.get_location());
		return ctoken;
	}

	/**
	 * Improve punctuator token
	 *
	 * @param ptoken
	 * @return
	 * @throws Exception
	 */
	protected CPunctuatorToken improve_punctuator(PPunctuatorToken ptoken) throws Exception {
		CPunctuatorToken ctoken = factory.new_punctuator(ptoken.get_punctuator());
		ctoken.set_location(ptoken.get_location());
		return ctoken;
	}

	/**
	 * Improve newline as token
	 *
	 * @param ptoken
	 * @return
	 * @throws Exception
	 */
	protected CNewlineToken improve_newline(PNewlineToken ptoken) throws Exception {
		CNewlineToken ctoken = factory.new_newline();
		ctoken.set_location(ptoken.get_location());
		return ctoken;
	}

	/**
	 * parse the next character to its number in execution environment.
	 *
	 * @param str
	 *            : string where characters are specified
	 * @param k
	 *            : the next character(s) to be parsed
	 * @return : [index to be updated, exec-character (can-be-null)]
	 * @throws Exception
	 */
	private Object[] parse_char(String str, int k) throws Exception {
		Character result;
		int n = str.length();

		if (k >= str.length())
			result = null;
		else {
			char head = str.charAt(k++);

			if (head == '\\') {
				head = str.charAt(k++);

				if (head == '\'')
					result = Character.valueOf('\'');
				else if (head == '\"')
					result = Character.valueOf('\"');
				else if (head == '?')
					result = Character.valueOf((char) 63);
				else if (head == '\\')
					result = Character.valueOf('\\');
				else if (head == 'a')
					result = Character.valueOf((char) 7);
				else if (head == 'b')
					result = Character.valueOf('\b');
				else if (head == 'f')
					result = Character.valueOf('\f');
				else if (head == 'n')
					result = Character.valueOf('\n');
				else if (head == 'r')
					result = Character.valueOf('\r');
				else if (head == 't')
					result = Character.valueOf('\t');
				else if (head == 'v')
					result = Character.valueOf((char) 11);
				else if (head == 'x') {
					int code = 0;
					while (k < n) {
						char ch = str.charAt(k++);
						int num = parse_char_number(ch);
						code = code * 16 + num;
					}
					result = Character.valueOf((char) code);
				} else if (head >= '0' && head <= '7') {
					k--;
					int code = 0;
					while (k < n) {
						char ch = str.charAt(k++);
						if (ch >= '0' && ch <= '7') {
							int num = parse_char_number(ch);
							code = code * 8 + num;
						} else {
							k--;
							break;
						}
					}
					result = Character.valueOf((char) code);
				} else
					throw new RuntimeException("Invalid character at \"" + str.substring(k - 2) + "\"");
			} else
				result = Character.valueOf(head);
		}

		/* return index and result */
		Object[] ans = new Object[2];
		ans[0] = Integer.valueOf(k);
		ans[1] = result;
		return ans;
	}

	/**
	 * 0-9 |--> decimal number <br>
	 * a-f |--> hexical number <br>
	 * A-F |--> hexical number <br>
	 *
	 * @param ch
	 * @return
	 * @throws Exception
	 */
	private int parse_char_number(char ch) throws Exception {
		if (ch >= '0' && ch <= '9')
			return (ch - '0');
		else if (ch >= 'a' && ch <= 'f')
			return (ch - 'a' + 10);
		else if (ch >= 'A' && ch <= 'F')
			return (ch - 'A' + 10);
		else
			throw new IllegalArgumentException("Invalid character: \'" + ch + "\' at line ");
	}

	/**
	 * parse string as decimal-string
	 *
	 * @param str
	 * @return
	 * @throws Exception
	 */
	private long parse_decimals(String str) throws Exception {
		int n = str.length();
		long count;

		count = 0;
		for (int i = 0; i < n; i++) {
			int num = parse_char_number(str.charAt(i));
			count = count * 10 + num;
		}
		return count;
	}

	/**
	 * parse string as hexical numbers
	 *
	 * @param str
	 * @return
	 * @throws Exception
	 */
	private long parse_hexicals(String str) throws Exception {
		int n = str.length();
		long count;

		count = 0;
		for (int i = 0; i < n; i++) {
			int num = parse_char_number(str.charAt(i));
			count = count * 16 + num;
		}
		return count;
	}

	/**
	 * improve the value of decimal floating constant
	 *
	 * @param ptoken
	 * @param ctoken
	 * @throws Exception
	 */
	private void improve_dec_floating(PFloatingToken ptoken, CConstantToken ctoken) throws Exception {
		String int_part = ptoken.get_integer_part();
		String fracpart = ptoken.get_fraction_part();
		String exponent = ptoken.get_exponent_part();

		long ip = parse_decimals(int_part);
		long fp = parse_decimals(fracpart);
		long ep = parse_decimals(exponent);
		int n = fracpart.length();

		if (!ptoken.get_exponent_sign())
			ep = -ep;

		double value = ip;
		double fracv = (fp) / Math.pow(10, n);
		value = value + fracv;
		value = value * Math.pow(10, ep);

		switch (ptoken.get_floating_suffix()) {
		case 'd':
			ctoken.get_constant().set_double(value);
			break;
		case 'f':
			ctoken.get_constant().set_float((float) value);
			break;
		case 'l':
			ctoken.get_constant().set_ldouble(value);
			break;
		default:
			throw new IllegalArgumentException("Invalid suffix: " + ptoken.get_floating_suffix());
		}
	}

	/**
	 * improve the value of hexical floating constant
	 *
	 * @param ptoken
	 * @param ctoken
	 * @throws Exception
	 */
	private void improve_hex_floating(PFloatingToken ptoken, CConstantToken ctoken) throws Exception {
		String int_part = ptoken.get_integer_part();
		String fracpart = ptoken.get_fraction_part();
		String exponent = ptoken.get_exponent_part();

		long ip = parse_hexicals(int_part);
		long fp = parse_hexicals(fracpart);
		long ep = parse_decimals(exponent);
		int n = fracpart.length();

		if (!ptoken.get_exponent_sign())
			ep = -ep;

		double value = ip;
		double fracv = (fp) / Math.pow(16, n);
		value = value + fracv;
		value = value * Math.pow(2, ep);

		switch (ptoken.get_floating_suffix()) {
		case 'd':
			ctoken.get_constant().set_double(value);
			break;
		case 'f':
			ctoken.get_constant().set_float((float) value);
			break;
		case 'l':
			ctoken.get_constant().set_ldouble(value);
			break;
		default:
			throw new IllegalArgumentException("Invalid suffix: " + ptoken.get_floating_suffix());
		}
	}

}
