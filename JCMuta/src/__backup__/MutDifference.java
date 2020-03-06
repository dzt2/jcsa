package __backup__;

import com.jcsa.jcparse.lang.base.BitSequence;

/**
 * Difference between the programs 
 * [source, target, difference]
 * @author yukimula
 */
public class MutDifference {
	
	private int source;
	private int target;
	private BitSequence difference;
	public MutDifference(int source, int target, 
			BitSequence difference) throws Exception {
		if(difference == null)
			throw new IllegalArgumentException("invalid difference: null");
		else {
			this.source = source; 
			this.target = target; 
			this.difference = difference;
		}
	}
	
	/**
	 * get the source program
	 * @return
	 */
	public int get_source() { return source; }
	/**
	 * get the target program
	 * @return
	 */
	public int get_target() { return target; }
	/**
	 * get the difference vector
	 * @return
	 */
	public BitSequence get_difference() { return difference; }
	/**
	 * number of tests that differ two programs.
	 * @return
	 */
	public int get_distance() { return difference.degree(); }
	
}
