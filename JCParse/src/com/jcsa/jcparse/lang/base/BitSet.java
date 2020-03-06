package com.jcsa.jcparse.lang.base;

import java.util.Collection;

public class BitSet {
	
	/* properties */
	/** bit-set value base **/
	private BitSetBase base;
	/** the values in set **/
	private BitSequence set;
	
	/* constructor */
	/**
	 * construct an empty set described based on bit-string,
	 * of which values refer to those in the bit-set-base.
	 * @param base
	 * @throws Exception
	 */
	protected BitSet(BitSetBase base) throws Exception {
		if(base == null)
			throw new IllegalArgumentException("invalid base: null");
		else {
			this.base = base;
			this.set = new BitSequence(base.size());
		}
	}
	protected BitSet(BitSet value) throws Exception {
		if(value == null) 
			throw new IllegalArgumentException("invalid value: null");
		else { this.base = value.base; this.set = new BitSequence(value.set); }
	}
	
	/* getters */
	/**
	 * get the base to which the values in the set refer
	 * @return
	 */
	public BitSetBase get_base() { return base; }
	/**
	 * get the set described based on Bit-string
	 * @return
	 */
	public BitSequence get_set() { return set; }
	/**
	 * is the set empty
	 * @return
	 */
	public boolean isEmpty() { return set.degree() == 0; }
	/**
	 * number of values in the set
	 * @return
	 */
	public int size() { return this.set.degree(); }
	/**
	 * whether the value belongs to this set
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public boolean has(Object value) throws Exception {
		if(!base.has(value))
			throw new IllegalArgumentException("invalid access: " + value);
		else { return this.set.get(this.base.index_of(value)); }
	}
	/**
	 * add a value into the set
	 * @param value
	 * @throws Exception when the value is not in the bit-set-base
	 */
	public void add(Object value) throws Exception {
		if(!base.has(value))
			throw new IllegalArgumentException("invalid access: " + value);
		else { this.set.set(this.base.index_of(value), BitSequence.BIT1); }
	}
	/**
	 * remove a value from the set
	 * @param value
	 * @throws Exception when the value does not belong to the base
	 */
	public void del(Object value) throws Exception {
		if(!base.has(value))
			throw new IllegalArgumentException("invalid access: " + value);
		else { this.set.set(this.base.index_of(value), BitSequence.BIT0); }
	}
	/**
	 * clear all the values in the set
	 */
	public void clear() { this.set.clear(); }
	/**
	 * parse the bit-set into Java-Collection object
	 * @param values
	 * @return
	 * @throws Exception
	 */
	public int parse(Collection<Object> values) throws Exception {
		if(values == null)
			throw new IllegalArgumentException("invalid values: null");
		else {
			values.clear();
			for(int k = 0; k < this.set.length(); k++) {
				if(this.set.get(k)) values.add(base.get(k));
			}
			return values.size();
		}
	}
	/**
	 * set the set as the set
	 * @param set
	 * @return
	 * @throws Exception
	 */
	public boolean set(BitSet set) throws Exception {
		if(set == null)
			throw new IllegalArgumentException("invalid set: null");
		else if(this.base == set.base) {
			BitSequence x = this.set;
			BitSequence y = set.set;
			boolean change = !x.equals(y);
			x.set(y); return change;
		}
		else throw new IllegalArgumentException("not-the-same-base");
	}
	/**
	 * obtain the copy of this bit-set
	 * @return
	 * @throws Exception
	 */
	public BitSet copy() throws Exception { return new BitSet(this); }
	/**
	 * obtain the copy set of the union of two sets
	 * @param y
	 * @return
	 * @throws Exception
	 */
	public BitSet or(BitSet y) throws Exception {
		if(y == null || y.base != this.base)
			throw new IllegalArgumentException("invalid y: null");
		else { 
			BitSet result = new BitSet(base);
			result.set = this.set.or(y.set); 
			return result;
		}
	}
	/**
	 * obtain the copy set of the intersection of two sets
	 * @param y
	 * @return
	 * @throws Exception
	 */
	public BitSet and(BitSet y) throws Exception {
		if(y == null || y.base != this.base)
			throw new IllegalArgumentException("invalid y: null");
		else { 
			BitSet result = new BitSet(base);
			result.set = this.set.and(y.set); 
			return result;
		}
	}
	
	@Override
	public String toString() { return this.set.toString(); }
	@Override
	public boolean equals(Object value) {
		if(this == value) return true;
		else if(value instanceof BitSet)
			return ((BitSet) value).set.equals(this.set);
		else return false;
	}
	
}
