package com.jcsa.jcparse.flwa.analysis;

import com.jcsa.jcparse.base.BitSequence;
import com.jcsa.jcparse.base.BitSet;

/**
 * The value that describes a set of objects used for data flow analysis
 * @author yukimula
 *
 */
public class BitSetValue implements AbsValue {
	
	/* constructor */
	/** the set of objects that represent this value **/
	private BitSet bitset;
	/**
	 * create a bitset value with respect to the given set
	 * @param bitset
	 * @throws Exception
	 */
	public BitSetValue(BitSet bitset) throws Exception {
		if(bitset == null)
			throw new IllegalArgumentException("invalid bitset: null");
		else this.bitset = bitset;
	}
	
	/* getter and setter */
	/**
	 * get the bit representation of set this value describes
	 * @return
	 */
	public BitSet get() { return this.bitset; }
	@Override
	public boolean set(AbsValue value) throws Exception {
		if(value == null)
			throw new IllegalArgumentException("invalid value: null");
		else if(value instanceof BitSetValue) {
			BitSequence x = ((BitSetValue) value).get().get_set();
			BitSequence y = this.bitset.get_set();
			boolean change = !x.equals(y);
			this.bitset = ((BitSetValue) value).bitset.copy();
			return change;
		}
		else throw new IllegalArgumentException("invalid: " + value);
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;
		else if(obj instanceof BitSetValue) {
			BitSet x = this.bitset;
			BitSet y = ((BitSetValue) obj).bitset;
			return x.get_set().equals(y.get_set());
		}
		else return false;
	}
	
}
