package __backup__;

/**
 * A test case in jcmuta represents a plan to execute some 
 * program (.exe file) based on a set of inputs. Usually,
 * the test case is modeled as following:<br>
 * 	<code>
 * 	[EXEC-FILE]	[INPUT-ITEM]+ [OUPUT-ITEM]+ > STDOUT<br>
 * 	</code>
 * <br>
 * Note, the EXEC and OUTPUT need to be further specified
 * based on the context provided by testing harness. In
 * other words, test case cannot be executed until the context
 * where testing performs is defined. Or a test context.
 * @author yukimula
 */
public class TestCase {
	
	/* constructor */
	protected TestSpace space;
	protected int id;
	protected String tag;
	protected String command;
	protected TestCase(TestSpace space, 
			int id, String command, String tag) throws Exception {
		if(space == null)
			throw new IllegalArgumentException("Invalid space: null");
		else if(command == null)
			throw new IllegalArgumentException("Invalid command: null");
		else {
			this.space = space; 
			this.id = id; 
			this.command = command; 
			if(tag != null) 
				this.tag = tag;
			else this.tag = "";
		}
	}
	
	/* constructor */
	/**
	 * get the space where test case is defined
	 * @return
	 */
	public TestSpace get_space() { return space; }
	/**
	 * get the test case id
	 * @return
	 */
	public int get_test_id() { return id; }
	/**
	 * get the tag of the test case (used for high-level)
	 * @return
	 */
	public String get_test_tag() { return tag; }
	/**
	 * get the command of the test case
	 * @return
	 */
	public String get_command() { return command; }
}
