package com.jcsa.jcparse.lang.base;

/**
 * sequence of bits, used for encoding binary key
 * @author yukimula
 */
public class BitSequence {
	
	protected static final byte[] BITS = new byte[] {1, 2, 4, 8, 16, 32, 64, -128};
	protected static final StringBuilder buff = new StringBuilder();
	
	public static final boolean BIT1 = true;
	public static final boolean BIT0 = false;
	
	private byte[] bytes;
	private int size;
	public BitSequence(BitSequence x) throws Exception {
		if(x == null)
			throw new IllegalArgumentException("Invalid x: null");
		else {
			this.size = x.size;
			if(x.bytes != null) {
				this.bytes = new byte[x.bytes.length];
				for(int i = 0; i < x.bytes.length; i++)
					this.bytes[i] = x.bytes[i];
			}
		}
	}
	public BitSequence(int size) throws Exception {
		if(size < 0)
			throw new IllegalArgumentException("Invalid size: " + size);
		else {
			this.size = size;
			int cap = capability();
			if(cap == 0) bytes = null;
			else {
				bytes = new byte[cap];
				for(int i = 0; i < bytes.length; i++)
					bytes[i] = 0;
			}
		}
	}
	private int capability() {
		if(size <= 0) return 0;
		else {
			int cap = size / 8;
			if(size % 8 != 0) cap++;
			return cap;
		}
	}
	
	/**
	 * number of bit in the sequence
	 * @return
	 */
	public int length() { return size; }
	/**
	 * get the kth bit from the sequence
	 * @param k
	 * @return
	 * @throws Exception
	 */
	public boolean get(int k) {
		if(k < 0 || k >= size)
			throw new IllegalArgumentException("Invalid index: " + k);
		else {
			int x = k / 8, y = k % 8;
			byte b = bytes[x];
			b = (byte) (b & BITS[y]);
			return b != 0;
		}
	}
	/**
	 * number of BIT-1 in the sequence 
	 * @return
	 */
	public int degree() {
		int deg = 0;
		if(bytes != null) {
			for(int i = 0; i < bytes.length; i++) {
				byte bk = bytes[i];
				for(int j = 0; j < BITS.length; j++) {
					if((bk & BITS[j]) != 0) deg++;
				}
			}
		}
		return deg;
	}
	/**
	 * set the kth bit as specified value
	 * @param k
	 * @param bit
	 * @throws Exception
	 */
	public void set(int k, boolean bit) throws Exception {
		if(k < 0 || k >= size)
			throw new IllegalArgumentException("Invalid index: " + k);
		else {
			int x = k / 8, y = k % 8;
			byte b = bytes[x];
			
			if(bit) {
				b = (byte) (b | BITS[y]);
			}
			else {
				b = (byte) (b & ~(BITS[y]));
			}
			
			bytes[x] = b;
		}
	}
	/**
	 * set the bits as all-zeros
	 */
	public void clear() {
		if(bytes != null) {
			for(int i = 0; i < bytes.length; i++)
				bytes[i] = 0;
		}
	}
	
	/* computational methods */
	/**
	 * X := Y<br>
	 * 1) if y == null, then clear X;<br>
	 * 2) if y longer than x, x is assigned by its lower-address;<br>
	 * 3) if y shorter than x, then x's high address is cleared.<br>
	 * @param y
	 * @throws Exception
	 */
	public void set(BitSequence y) {
		int k = 0;
		/* set lower values */
		if(y != null) {
			if(y.bytes != null) {
				while((k < bytes.length) && (k < y.bytes.length)) {
					bytes[k] = y.bytes[k]; k = k + 1;
				}
			}
		}
		/* clear higher address */
		if(bytes == null) return;
		while(k < bytes.length) {
			bytes[k] = 0; k = k + 1;
		}
		
	}
	/**
	 * Z = X & Y<br>
	 * 	1) If y is null, then return null;<br>
	 * 	2) Z'length is the shortest of X and Y;<br>
	 * 	3) Only the bits in Z are conjuncted.<br>
	 * @param y
	 * @return
	 * @throws Exception
	 */
	public BitSequence and(BitSequence y) throws Exception {
		if(y == null) return null;
		else {
			int min = Math.min(size, y.size);
			BitSequence z = new BitSequence(min);
			
			for(int i = 0; i < z.bytes.length; i++) {
				z.bytes[i] = (byte) (bytes[i] & y.bytes[i]);
			}
			return z;
		}
	}
	/**
	 * Z = X | Y<br>
	 * 	1) If y is null, then return X;<br>
	 * 	2) Z'length is the maximum of X and Y;<br>
	 * 	3) Only the bits in shorter are disjuncted.<br>
	 * 	4) The bits in high-address are copy by longer input.<br>
	 * @param y
	 * @return
	 * @throws Exception
	 */
	public BitSequence or(BitSequence y) throws Exception {
		if(y == null) return new BitSequence(this);
		else if(this.bytes == null) return new BitSequence(this);
		else {
			/* create */
			int max = Math.max(size, y.size);
			BitSequence z = new BitSequence(max);
			/* disjunct common bits */
			int n = Math.min(bytes.length, y.bytes.length);
			for(int i = 0; i < n; i++) {
				z.bytes[i] = (byte) (bytes[i] | y.bytes[i]);
			}
			/* complement the x's bits */
			while(n < this.bytes.length) {
				z.bytes[n] = this.bytes[n];
				n = n + 1;
			}
			/* complement the y's bits */
			while(n < y.bytes.length) {
				z.bytes[n] = this.bytes[n];
				n = n + 1;
			}
			/* return */	return z;
		}
	}
	/**
	 * Z = ~X
	 * @return
	 * @throws Exception
	 */
	public BitSequence not() throws Exception {
		BitSequence y = new BitSequence(this.size);
		for(int k = 0; k < this.bytes.length; k++) {
			y.bytes[k] = (byte) ~(this.bytes[k]);
		}
		return y;
	}
	
	/* translation methods */
	/** number of bytes used to represent one integer **/
	public static final int INT_BYTES = 4;
	/** used to compute the integer of production in bytes **/
	protected static final int INT_STEP = (1<<8);
	/**
	 * get the array of integers as compression of the bit-sequence
	 * @return null when no bits in the sequence
	 */
	public int[] array_of() {
		if(this.bytes == null) return null;
		else {
			int length = (int) Math.ceil(bytes.length / INT_BYTES);
			int[] int_array = new int[length]; int value, product;
			for(int k = 0; k < length; k++) {
				value = 0; product = 1; 
				for(int i = 0; i < INT_BYTES; i++) {
					value = value + bytes[4 * k + i] * product;
					product = product * INT_STEP;
				}
				int_array[k] = value;
			}
			return int_array;
		}
	}
	/**
	 * get the clone of the byte representation of bit-sequence
	 * @return
	 */
	public byte[] bytes_of() { return this.bytes.clone(); }
	
	@Override
	public boolean equals(Object y) {
		if(y == null) return false;
		else if(y instanceof BitSequence) {
			BitSequence res = (BitSequence) y;
			/* match the common bits */
			int k = 0;
			while((k < this.size) && (k < res.size)) {
				if(this.get(k) != res.get(k)) 
					return false;
				else k = k + 1;
			}
			/* match the x's posfix */
			while(k < this.size) {
				if(this.get(k) != BIT0)
					return false;
				else k = k + 1;
			}
			/* match the y's posfix */
			while(k < res.size) {
				if(res.get(k) != BIT0)
					return false;
				else k = k + 1;
			}
			/* all bits matched! */
			return true;
		}
		else return false;
	}
	@Override
	public String toString() {
		buff.setLength(0);
		for(int i = 0; i < size; i++) {
			if(get(i)) buff.append('1');
			else buff.append('0');
		}
		return buff.toString();
	}
}
