package com.jcsa.jcparse.lang.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The base of a <code>BitSet</code> describes of which object refers to which index
 * in the <code>BitSet</code> represented based on a <code>BitSequence</code>.
 * 
 * @author yukimula
 *
 */
public class BitSetBase {
	
	/* properties */
	/** mapping from index to the value **/
	private List<Object> index_value;
	/** mapping from value to the index **/
	private Map<Object, Integer> value_index;
	
	/* constructor */
	/**
	 * construct an empty bit-set base for creating bit-set
	 */
	private BitSetBase() {
		this.index_value = new ArrayList<Object>();
		this.value_index = new HashMap<Object, Integer>();
	}
	
	/* getters */
	/**
	 * whether the set-base is empty
	 * @return
	 */
	public boolean isEmpty() { return index_value.isEmpty(); }
	/**
	 * get the number of values in the bit-set base
	 * @return
	 */
	public int size() { return this.index_value.size(); }
	/**
	 * get the list of values in the bit-set base
	 * @return
	 */
	public Iterator<Object> get_values() { 
		return index_value.iterator(); 
	}
	/**
	 * get the kth value in the bit-set base
	 * @param index
	 * @return
	 * @throws Exception
	 */
	public Object get(int index) throws Exception {
		if(index < 0 || index >= index_value.size())
			throw new IllegalArgumentException("out-of-range: " + index);
		else return this.index_value.get(index);
	}
	/**
	 * whether the value is in the bit-set base
	 * @param value
	 * @return
	 */
	public boolean has(Object value) { 
		return this.value_index.containsKey(value);
	}
	/**
	 * get the index of the value in the bit-set base
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public int index_of(Object value) throws Exception {
		if(this.value_index.containsKey(value)) return value_index.get(value);
		else throw new IllegalArgumentException("undefined value: " + value);
	}
	/**
	 * create a new set referring to its values in this base.
	 * @return
	 * @throws Exception
	 */
	public BitSet new_set() throws Exception { return new BitSet(this); }
	
	/* generation method */
	/**
	 * add a value into the index of the base
	 * @param value
	 * @return
	 */
	private boolean add(Object value) {
		if(this.value_index.containsKey(value)) return false;
		else {
			value_index.put(value, index_value.size());
			this.index_value.add(value); return true;
		}
	}
	/**
	 * rebuild the bit-set base from a set of values
	 * @param values
	 */
	private void generate(Collection<Object> values) {
		this.index_value.clear(); 
		this.value_index.clear();
		
		if(values == null) return;
		else {
			for(Object value : values)
				this.add(value);
		}
	}
	
	/* factory method */
	/**
	 * generate a bit-set base constructed from the set of values
	 * @param values
	 * @return
	 * @throws Exception
	 */
	public static BitSetBase base(Collection<Object> values) {
		BitSetBase base = new BitSetBase();
		base.generate(values); return base;
	}
	
}
