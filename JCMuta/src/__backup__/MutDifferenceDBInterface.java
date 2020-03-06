package __backup__;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Iterator;

import com.jcsa.jcparse.lang.base.BitSequence;

/**
 * Database interface for <code>MutDifference</code>
 * @author yukimula
 */
public class MutDifferenceDBInterface extends DBInterface {
	
	/** number of mutations in cache **/
	public static final int BUFF_SIZE = 1024 * 96; 
	
	/* SQL query */
	public static final String TABLE_NAME = "compares";
	public static final String WRITE_STMT = "insert into compares (source, target, difference) values (?, ?, ?);";
	public static final String READ_STMT_SOURCE = "select * from compares where source=?;";
	public static final String READ_STMT_TARGET = "select * from compares where target=?;";
	
	/* constructor */
	private StringBuilder string_buffer;
	/**
	 * construct a DB-API for access mutation scores 
	 */
	public MutDifferenceDBInterface() { 
		super(); 
		string_buffer = new StringBuilder();
	}
	
	/* DB-interfaces */
	/**
	 * translate the bit-string to string.
	 * @param seq
	 * @return
	 * @throws Exception
	 */
	private String string_of_bits(BitSequence seq) throws Exception {
		int k, n = seq.length();
		
		string_buffer.setLength(0);
		for(k = 0; k < n; k++) {
			if(seq.get(k))
				string_buffer.append('1');
			else string_buffer.append('0');
		}
		
		return string_buffer.toString();
	}
	/**
	 * write all the scores to the 
	 * @param scores
	 * @return
	 * @throws Exception
	 */
	public int write_differences(Iterator<MutDifference> differences) throws Exception {
		if(differences == null)
			throw new IllegalArgumentException("invalid differences: null");
		else if(differences.hasNext()) {
			/* set the connection not to commit for each SQL query */
			this.connection.setAutoCommit(false); int counter = 0, num = 0;
			PreparedStatement stmt = connection.prepareStatement(WRITE_STMT);
			
			/* write each mut-score to the data file */
			while(differences.hasNext()) {
				/* get next mutation difference */
				MutDifference difference = differences.next();
				if(difference == null) continue;
				
				/* add item to buffer */
				stmt.setInt(1, difference.get_source());
				stmt.setInt(2, difference.get_target());
				stmt.setString(3, string_of_bits(difference.get_difference()));
				
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
	 * read the differences correlated with program with mid
	 * @param mid : TestResult.PROGRAM_ID as original program
	 * @param differences
	 * @return
	 * @throws Exception
	 */
	public int read_differences(int mid, Collection<MutDifference> differences) throws Exception {
		if(differences == null)
			throw new IllegalArgumentException("no-outputs specified");
		else {
			int count = 0; ResultSet rs;
			MutDifference difference;
			
			PreparedStatement stmt = connection.
					prepareStatement(READ_STMT_SOURCE);
			stmt.setInt(1, mid); rs = stmt.executeQuery();
			while(rs.next()) {
				difference = this.parse(rs);
				differences.add(difference);
				count++;
			}
			
			stmt = connection.prepareStatement(READ_STMT_TARGET);
			stmt.setInt(1, mid); rs = stmt.executeQuery();
			while(rs.next()) {
				difference = this.parse(rs);
				differences.add(difference);
				count++;
			}
			
			return count;
		}
		
	}
	private MutDifference parse(ResultSet rs) throws Exception {
		int source = rs.getInt("source");
		int target = rs.getInt("target");
		String text = rs.getString("difference");
		BitSequence seq = new BitSequence(text.length());
		for(int k = 0; k < text.length(); k++) {
			char ch = text.charAt(k);
			if(ch == '0') seq.set(k, BitSequence.BIT0);
			else if(ch == '1') seq.set(k, BitSequence.BIT1);
			else throw new RuntimeException("invalid code: " + text);
		}
		return new MutDifference(source, target, seq);
	}
	
}
