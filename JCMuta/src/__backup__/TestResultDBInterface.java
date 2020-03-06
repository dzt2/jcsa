package __backup__;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Database interface for reading and writing the <code>TestResult</code> objects.
 * @author yukimula
 */
public class TestResultDBInterface extends DBInterface {
	
	/** maximal size of the buffer in writing results **/
	protected static final int BUFF_SIZE = 1024 * 128;
	
	/* SQL query */
	public static final String TABLE_NAME = "outputs";
	public static final String WRITE_STMT = "insert into outputs (mutant, testcase, status, outputs) values (?, ?, ?, ?);";
	public static final String READ_STMT_ALL = "select * from outputs;";
	public static final String READ_STMT_COUNT = "select count(*) from outputs;";
	public static final String READ_STMT_MUTANT = "select * from outputs where mutant=?;";
	public static final String READ_STMT_TEST = "select * from outputs where testcase=?;";
	public static final String READ_STMT_MUTANT_TEST = "select * from outputs where mutant=? and testcase=?;";
	
	private StringBuilder string_buffer;
	private List<Integer> integer_buffer;
	/**
	 * create a DB-interface to access test results
	 */
	public TestResultDBInterface() { 
		super(); 
		string_buffer = new StringBuilder();
		integer_buffer = new ArrayList<Integer>();
	}
	
	/**
	 * get the number of results in the data file
	 * @return
	 * @throws Exception
	 */
	public int size() throws Exception {
		if(connection == null)
			throw new RuntimeException("Invalid access: not openned");
		else {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(READ_STMT_COUNT);
			int size = rs.getInt(1); stmt.close(); return size;
		}
	}
	/**
	 * write all the results into the 
	 * @param results
	 * @return
	 * @throws Exception
	 */
	public int write(Iterator<TestResult> results) throws Exception {
		if(results == null)
			throw new IllegalArgumentException("Invalid results: null");
		else if(results.hasNext()) {
			/* set the connection not to commit for each SQL query */
			this.connection.setAutoCommit(false); int counter = 0, num = 0;
			PreparedStatement stmt = connection.prepareStatement(WRITE_STMT);
			
			/* write each result to the data file */
			while(results.hasNext()) {
				/* get the next result */
				TestResult result = results.next();
				if(result == null) continue;
				
				/* write the information to item */
				stmt.setInt(1, result.get_mutant());
				stmt.setInt(2, result.get_test());
				stmt.setString(3, result.get_status().toString());
				stmt.setString(4, string_of_vector(result.get_outputs()));
				
				/* add the query to the cache */
				stmt.addBatch(); counter++; num++;
				
				/* commit the queries when cache is full */
				if(counter >= BUFF_SIZE) {
					stmt.executeBatch();
					connection.commit(); 
					counter = 0;
				}
			}
			
			/* commit the remainders of query */
			if(counter > 0) {
				stmt.executeBatch();
				connection.commit();
			}
			
			/* recover the connection settings */
			connection.setAutoCommit(true); 
			stmt.close(); return num;
		}
		else return 0;
	}
	/**
	 * Read all the results to the collection.<br>
	 * It does not clear the objects in the set
	 * but only append new result to it.
	 * @param results
	 * @return
	 * @throws Exception
	 */
	public int read_all(Collection<TestResult> results) throws Exception {
		if(results == null)
			throw new IllegalArgumentException("No outputs specified");
		else {
			int count = 0; 
			Statement stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(READ_STMT_ALL);
			while(rs.next()) {
				try {
					TestResult result = parse(rs);
					if(result != null) {
						results.add(result); count++;
					}
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			return count;
		}
	}
	/**
	 * Read all the results of mutant into collection
	 * @param mutant
	 * @param results
	 * @return
	 * @throws Exception
	 */
	public int read_mutant(Mutant mutant, Collection<TestResult> results) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("invalid mutant: null");
		else if(results == null)
			throw new IllegalArgumentException("No outputs specified");
		else {
			PreparedStatement stmt =
					connection.prepareStatement(READ_STMT_MUTANT);
			stmt.setInt(1, mutant.get_mutant_id());
			
			ResultSet rs = stmt.executeQuery(); int count = 0;
			while(rs.next()) {
				try {
					TestResult result = parse(rs);
					if(result != null) {
						results.add(result);
						count++;
					}
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			return count;
		}
	}
	/**
	 * Read all the results of mutant into collection
	 * @param mutant
	 * @param results
	 * @return
	 * @throws Exception
	 */
	public int read_mutant(int mutant, Collection<TestResult> results) throws Exception {
		if(results == null)
			throw new IllegalArgumentException("No outputs specified");
		else {
			PreparedStatement stmt =
					connection.prepareStatement(READ_STMT_MUTANT);
			stmt.setInt(1, mutant);
			
			ResultSet rs = stmt.executeQuery(); int count = 0;
			while(rs.next()) {
				try {
					TestResult result = parse(rs);
					if(result != null) {
						results.add(result);
						count++;
					}
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			return count;
		}
	}
	/**
	 * read the results of the test case for all mutants
	 * @param test
	 * @param results
	 * @return
	 * @throws Exception
	 */
	public int read_test(TestCase test, Collection<TestResult> results) throws Exception {
		if(test == null)
			throw new IllegalArgumentException("invalid test: null");
		else if(results == null)
			throw new IllegalArgumentException("No outputs specified");
		else {
			PreparedStatement stmt =
					connection.prepareStatement(READ_STMT_TEST);
			stmt.setInt(1, test.get_test_id());
			
			ResultSet rs = stmt.executeQuery(); int count = 0;
			while(rs.next()) {
				try {
					TestResult result = parse(rs);
					if(result != null) {
						results.add(result);
						count++;
					}
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			return count;
		}
	}
	/**
	 * read the results of the test case for all mutants
	 * @param test
	 * @param results
	 * @return
	 * @throws Exception
	 */
	public int read_test(int test, Collection<TestResult> results) throws Exception {
		if(results == null)
			throw new IllegalArgumentException("No outputs specified");
		else {
			PreparedStatement stmt =
					connection.prepareStatement(READ_STMT_TEST);
			stmt.setInt(1, test);
			
			ResultSet rs = stmt.executeQuery(); int count = 0;
			while(rs.next()) {
				try {
					TestResult result = parse(rs);
					if(result != null) {
						results.add(result); count++;
					}
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			return count;
		}
	}
	/**
	 * Read the result of specified mutant and test case
	 * @param mutant
	 * @param test
	 * @return
	 * @throws Exception
	 */
	public TestResult read_mutant_test(Mutant mutant, TestCase test) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("invalid mutant: null");
		else if(test == null)
			throw new IllegalArgumentException("invalid test: null");
		else {
			PreparedStatement stmt =
					connection.prepareStatement(READ_STMT_MUTANT_TEST);
			stmt.setInt(1, mutant.get_mutant_id());
			stmt.setInt(2, test.get_test_id());
			ResultSet rs = stmt.executeQuery();
			try {
				rs.next(); return this.parse(rs);
			}
			catch(Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
	}
	/**
	 * Read the result of specified mutant and test case
	 * @param mutant
	 * @param test
	 * @return
	 * @throws Exception
	 */
	public TestResult read_mutant_test(int mutant, int test) throws Exception {
		PreparedStatement stmt =
				connection.prepareStatement(READ_STMT_MUTANT_TEST);
		stmt.setInt(1, mutant);
		stmt.setInt(2, test);
		ResultSet rs = stmt.executeQuery();
		try {
			rs.next(); return this.parse(rs);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	/* basic methods */
	private String string_of_vector(int[] list) {
		string_buffer.setLength(0);
		for(int k = 0; k < list.length; k++) {
			string_buffer.append(list[k] + ",");
		}
		return string_buffer.toString();
	}
	private TestResult parse(ResultSet rs) throws Exception {
		/* extract the items */
		int mutant = rs.getInt("mutant");
		int test = rs.getInt("test");
		String st = rs.getString("status");
		String vc = rs.getString("outputs");
		TestStatus status = this.get_status(st);
		this.get_outputs(vc);
		if(integer_buffer.size() != 5)
			throw new IllegalArgumentException("invalid counter: " + integer_buffer.size());
		else return new TestResult(mutant, test, status, integer_buffer.iterator());
	}
	private TestStatus get_status(String text) throws Exception {
		if(text == null)
			throw new IllegalArgumentException("invalid text: null");
		else if(text.equals(TestStatus.COMMAND_FAILS.toString()))
			return TestStatus.COMMAND_FAILS;
		else if(text.equals(TestStatus.OUT_OF_MEMORY.toString()))
			return TestStatus.OUT_OF_MEMORY;
		else if(text.equals(TestStatus.OUT_OF_EXTIME.toString()))
			return TestStatus.OUT_OF_EXTIME;
		else if(text.equals(TestStatus.OBTAIN_OUTPUT.toString()))
			return TestStatus.OBTAIN_OUTPUT;
		else throw new IllegalArgumentException("unknown: " + text);
	}
	private void get_outputs(String text) throws Exception {
		this.integer_buffer.clear();
		if(text != null) {
			String[] array = text.split(",");
			for(int k = 0; k < array.length - 1; k++) {
				int value = Integer.parseInt(array[k]);
				this.integer_buffer.add(value);
			}
		}
	}
	
}
