package __backup__;

/**
 * <code>[mutant, tag, cursor]</code>
 * @author yukimula
 */
public class CompileRecord {
	
	protected int mutant;
	protected boolean tag;
	protected int cursor;
	protected CompileRecord(int mutant, boolean tag, int cursor) throws Exception {
		this.mutant = mutant; this.tag = tag; this.cursor = cursor;
	}
	
	/**
	 * get the mutant to be compiled
	 * @return
	 */
	public int get_mutant() { return mutant; }
	/**
	 * whether the mutant is correct
	 * @return
	 */
	public boolean get_tag() { return tag; }
	/**
	 * get the index to its result data-file
	 * @return
	 */
	public int get_cursor() { return cursor; }
	
	@Override
	public String toString() {
		return mutant + "\t" + tag + "\t" + cursor;
	}
	/**
	 * get a record from compile log based on its text line
	 * @param line
	 * @return
	 * @throws Exception
	 */
	public static CompileRecord parse(String line) throws Exception {
		if(line == null || line.trim().isEmpty())
			throw new IllegalArgumentException("invalid line: " + line);
		else {
			String[] array = line.split("\t");
			int mutant = Integer.parseInt(array[0]);
			boolean tag = Boolean.parseBoolean(array[1]);
			int cursor = Integer.parseInt(array[2]);
			return new CompileRecord(mutant, tag, cursor);
		}
	}
	
}
