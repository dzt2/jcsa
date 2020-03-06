package __backup__;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;

/**
 * 
 * @author yukimula
 *
 */
public class TestDBInterface extends DBInterface {
	
	public static final String TABLE_NAME = "testcase";
	
	public static final String WRITE_STMT = "insert into testcase (id, key, tag) values (?, ?, ?);";
	public static final String READ_STMT_ALL = "select * from testcase;";
	public static final String READ_STMT_COUNT = "select count(*) from testcase;";
	
	public static final int SIZE_CACHE = 1024 * 8;
	
	/* DB-access */
	/**
	 * get the number of test cases in database
	 * @return
	 * @throws Exception
	 */
	public int size() throws Exception {
		if(connection == null)
			throw new RuntimeException("Invalid access: not connected");
		else {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(READ_STMT_COUNT);
			rs.next(); int size = rs.getInt(1);
			stmt.close(); return size;
		}
	}
	/**
	 * read all the tests in data base to space
	 * @param space
	 * @return
	 * @throws Exception
	 */
	public int read(TestSpace space) throws Exception {
		if(connection == null)
			throw new RuntimeException("Invalid access: not connected");
		else if(space == null)
			throw new IllegalArgumentException("Output is not specified");
		else {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(READ_STMT_ALL);
			
			while(rs.next()) {
				int tid = rs.getInt("id"); 
				String command = rs.getString("key");
				String tag = null;
				try {
					tag = rs.getString(tag);
				}
				catch(Exception ex) {
					tag = "Nullptr";
				}
				space.set_test_case(tid, command, tag);
			}
			stmt.close(); 
			
			return space.size();
		}
	}
	/**
	 * write test cases in space to the data file
	 * @param space
	 * @return
	 * @throws Exception
	 */
	public int write(Iterator<TestCase> testcases) throws Exception {
		if(connection == null)
			throw new RuntimeException("Invalid access: not connected");
		else if(testcases == null)
			throw new IllegalArgumentException("Input is not specified");
		else if(!testcases.hasNext()) return 0;
		else {
			/* SQL-setting */
			this.connection.setAutoCommit(false); int counter = 0, num = 0;
			PreparedStatement stmt = connection.prepareStatement(WRITE_STMT);
			
			while(testcases.hasNext()) {
				/* get next test case */
				TestCase test = testcases.next();
				if(test == null) continue;
				
				/* reset the SQL statement */
				stmt.setInt(1, test.get_test_id());
				stmt.setString(2, test.get_command());
				stmt.setString(3, test.get_test_tag());
				
				/* add the query to the cache */
				stmt.addBatch(); counter++; num++;
				
				/* commit the queries when cache is full */
				if(counter >= SIZE_CACHE) {
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
	}
	
}
