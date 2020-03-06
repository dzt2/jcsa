package com.jcsa.jcparse.lang.ptoken.impl;

import com.jcsa.jcparse.lang.lexical.CNumberEncode;
import com.jcsa.jcparse.lang.ptoken.PFloatingToken;

public class PFloatingTokenImpl extends PTokenImpl implements PFloatingToken {

	private CNumberEncode encode;
	private String int_part;
	private String frac_part;
	private boolean exp_sign;
	private String exponent;
	private char real_suffix;

	protected PFloatingTokenImpl(CNumberEncode encode, String intpart, String fraction, boolean sign, String expn,
			char suffix) {
		super();
		if (intpart == null || intpart.isEmpty())
			throw new IllegalArgumentException("Invalid intpart: null");
		else if (fraction == null || fraction.isEmpty())
			throw new IllegalArgumentException("Invalid fraction: null");
		else if (expn == null || expn.isEmpty())
			throw new IllegalArgumentException("Invalid exponent: null");
		else {
			this.encode = encode;
			this.int_part = intpart;
			this.frac_part = fraction;
			this.exp_sign = sign;
			this.exponent = expn;
			this.real_suffix = suffix;
		}
	}

	protected PFloatingTokenImpl(CNumberEncode encode, String intpart, String fraction, String exponent, char suffix) {
		this(encode, intpart, fraction, true, exponent, suffix);
	}

	protected PFloatingTokenImpl(CNumberEncode encode, String intpart, String fraction, String exponent) {
		this(encode, intpart, fraction, true, exponent, 'd');
	}

	protected PFloatingTokenImpl(CNumberEncode encode, String intpart, String fraction, char suffix) {
		this(encode, intpart, fraction, true, "0", suffix);
	}

	protected PFloatingTokenImpl(CNumberEncode encode, String intpart, String fraction) {
		this(encode, intpart, fraction, true, "0", 'd');
	}

	@Override
	public CNumberEncode get_encode() {
		return encode;
	}

	@Override
	public String get_integer_part() {
		return int_part;
	}

	@Override
	public String get_fraction_part() {
		return frac_part;
	}

	@Override
	public boolean get_exponent_sign() {
		return exp_sign;
	}

	@Override
	public String get_exponent_part() {
		return exponent;
	}

	@Override
	public char get_floating_suffix() {
		return real_suffix;
	}

	@Override
	public String toString() {
		return "[Real]{ encode: " + encode + "; " + "int: " + int_part + "; " + "frac: " + frac_part + "; " + "sign: "
				+ exp_sign + "; " + "expn: " + exponent + "; " + "suff: " + real_suffix + "; }";
	}
}
