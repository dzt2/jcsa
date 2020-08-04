package com.jcsa.jcparse.base;

/**
 * Complex describes the complex number with one real and one imaginary as a + b * i
 * 
 * @author yukimula
 */
public class Complex {
	
	/* properties */
	/** real-value **/
	private double real;
	/** imaginary **/
	private double imag;
	
	/* constructor */
	/**
	 * create a complex-value with specified real and imaginary
	 * @param real
	 * @param imag
	 */
	public Complex(double real, double imag) {
		this.real = real; this.imag = imag;
	}
	/**
	 * create a complex with only one real to represent real
	 * @param real
	 */
	public Complex(double real) {
		this.real = real; this.imag = 0.0;
	}
	/**
	 * create a complex to represent zero.
	 */
	public Complex() {
		this.real = 0.0; this.imag = 0.0;
	}
	/**
	 * copy-constructor
	 * @param x
	 */
	public Complex(Complex x) {
		if(x == null) {
			this.real = 0; this.imag = 0;
		}
		else {
			this.real = x.real; this.imag = x.imag;
		}
	}
	
	/* getter */
	/**
	 * get the real-value
	 * @return
	 */
	public double get_x() { return this.real; }
	/**
	 * get the imaginary
	 * @return
	 */
	public double get_y() { return this.imag; }
	
	/* common */
	@Override
	public boolean equals(Object val) {
		if(val instanceof Integer) {
			Double x = Double.valueOf((double)val);
			return (this.imag == 0.0) && (x.equals(this.real));
		}
		else if(val instanceof Double) {
			return (this.imag == 0.0) && (val.equals(this.real));
		}
		else if(val instanceof Complex) {
			return (this.real == ((Complex) val).real) 
					&& (this.imag == ((Complex) val).imag);
		}
		else return false;
	}
	@Override
	public String toString() {
		return this.real + " + " + this.imag + " * i";
	}
	
	/* computation */
	/**
	 * x.real + y.real, x.imag + y.imag
	 * @param x
	 * @param y
	 * @return
	 * @throws Exception
	 */
	public static Complex add(Complex x, Complex y) throws Exception {
		if(x == null)
			throw new IllegalArgumentException("invalid x: null");
		else if(y == null)
			throw new IllegalArgumentException("invalid y: null");
		else return new Complex(x.real + y.real, x.imag + y.imag);
	}
	/**
	 * x.real - y.real, x.imag - y.imag
	 * @param x
	 * @param y
	 * @return
	 * @throws Exception
	 */
	public static Complex sub(Complex x, Complex y) throws Exception {
		if(x == null)
			throw new IllegalArgumentException("invalid x: null");
		else if(y == null)
			throw new IllegalArgumentException("invalid y: null");
		else return new Complex(x.real - y.real, x.imag - y.imag);
	}
	/**
	 * x.real * y.real - x.imag * y.imag, x.real * y.imag + x.imag * y.real
	 * @param x
	 * @param y
	 * @return
	 * @throws Exception
	 */
	public static Complex mul(Complex x, Complex y) throws Exception {
		if(x == null)
			throw new IllegalArgumentException("invalid x: null");
		else if(y == null)
			throw new IllegalArgumentException("invalid y: null");
		else {
			double r = x.real * y.real - x.imag * y.imag;
			double i = x.real * y.imag + x.imag * y.real;
			return new Complex(r, i);
		}
	}
	@SuppressWarnings("unlikely-arg-type")
	public static Complex div(Complex x, Complex y) throws Exception {
		if(x == null)
			throw new IllegalArgumentException("invalid x: null");
		else if(y == null || y.equals(0.0))
			throw new IllegalArgumentException("invalid y: null");
		else {
			double x1 = x.real * y.real - x.imag * y.imag;
			double x2 = y.real * x.imag - y.imag * x.real;
			double x3 = y.real * y.real + y.imag * y.imag;
			return new Complex(x1 / x3, x2 / x3);
		}
	}
	/**
	 * x * x + y * y
	 * @param x
	 * @return
	 */
	public static double norm(Complex x) {
		return x.real * x.real + x.imag * x.imag;
	}
	/**
	 * sqrt(x * x + y * y)
	 * @param x
	 * @return
	 */
	public static double abs(Complex x) {
		return Math.sqrt(x.real * x.real + x.imag * x.imag);
	}
	/**
	 * get a complex as the real-part of the x
	 * @param x
	 * @return
	 * @throws Exception
	 */
	public static Complex real(Complex x) throws Exception {
		if(x == null)
			throw new IllegalArgumentException("invalid x: null");
		else return new Complex(x.real, 0);
	}
	/**
	 * get a complex as the imaginary of the x
	 * @param x
	 * @return
	 * @throws Exception
	 */
	public static Complex imag(Complex x) throws Exception {
		if(x == null)
			throw new IllegalArgumentException("invalid x: null");
		else return new Complex(0, x.imag);
	}
	/**
	 * -X
	 * @param x
	 * @return
	 * @throws Exception
	 */
	public static Complex neg(Complex x) throws Exception {
		if(x == null)
			throw new IllegalArgumentException("invalid x: null");
		else return new Complex(-x.real, -x.imag);
	}
	
	/* computation-2 */
	/**
	 * x.real + y.real, x.imag + y.imag
	 * @param y
	 * @return
	 * @throws Exception
	 */
	public Complex add(Complex y) throws Exception { return Complex.add(this, y); }
	/**
	 * x.real - y.real, x.imag - y.imag
	 * @param y
	 * @return
	 * @throws Exception
	 */
	public Complex sub(Complex y) throws Exception { return Complex.sub(this, y); }
	/**
	 * x.real * y.real - x.imag * y.imag, x.real * y.imag + x.imag * y.real
	 * @param y
	 * @return
	 * @throws Exception
	 */
	public Complex mul(Complex y) throws Exception { return Complex.mul(this, y); }
	/**
	 * 
	 * @param y
	 * @return
	 * @throws Exception
	 */
	public Complex div(Complex y) throws Exception { return Complex.div(this, y); }
	/**
	 * x * x + y * y
	 * @return
	 */
	public double norm() { return Complex.norm(this); }
	/**
	 * sqrt(x * x + y * y)
	 * @return
	 */
	public double abs() { return Complex.abs(this); }
	/**
	 * get a complex as the real-part of this value
	 * @return
	 */
	public Complex real() { return new Complex(this.real, 0); }
	/**
	 * get a complex as the imag-part of this value
	 * @return
	 */
	public Complex imag() { return new Complex(0, this.imag); }
	/**
	 * get a complex as the negative of this number
	 * @return
	 */
	public Complex neg() { return new Complex(-this.real, -this.imag); }
	
}
