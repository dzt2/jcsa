package __backup__;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Interface to access data file via SQLite
 * @author yukimula
 */
public abstract class DBInterface {
	
	/* arguments for SQLite */
	/** name for class to SQLite driver in JDBC lib **/
	public static final String SQLITE_DRIVER = "org.sqlite.JDBC";
	/** prefix to create connection in data file source **/
	public static final String SQLITE_PREFIX = "jdbc:sqlite:";
	static {
		/* driver connection for SQLite */
		try {
			Class.forName(SQLITE_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(2);
		}
	}
	
	/* attributes */
	/** SQLite connection **/
	protected Connection connection;
	/** data file for read | write items **/
	protected File data_source;
	
	/* constructor */
	/* constructor */
	/**
	 * constructor
	 */
	protected DBInterface() {
		data_source = null;
		connection = null;
	}
	
	/* IO-control access */
	/**
	 * close the connection to DB
	 * @return
	 * @throws Exception
	 */
	public boolean close() throws Exception {
		if(connection != null && 
				!connection.isClosed()) {
			connection.close();
			connection = null;
			return true;
		}
		else return false;
	}
	/**
	 * open an empty and new data file with specified template.
	 * This will over-write the original data file.
	 * @param source
	 * @param template
	 * @return
	 * @throws Exception
	 */
	public boolean open(File source, File template) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(template == null || !template.exists())
			throw new IllegalArgumentException("Invalid template: " + template);
		else if(connection != null)
			throw new RuntimeException("Invalid access: data access to \"" 
					+ this.data_source.getAbsolutePath() + "\" has not been closed.");
		else {
			this.file_copy(template, source);	this.data_source = source;
			connection = DriverManager.getConnection(
					SQLITE_PREFIX + data_source.getAbsolutePath());
			return true;
		}
	}
	/**
	 * open an existing data file 
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public boolean open(File source) throws Exception {
		if(source == null || !source.exists())
			throw new IllegalArgumentException("Invalid source: " + source);
		else {
			this.data_source = source;
			this.connection = DriverManager.getConnection(
					SQLITE_PREFIX + data_source.getAbsolutePath());
			return true;
		}
	}
	
	/* status validator */
	/**
	 * whether the connection is opened
	 * @return
	 * @throws Exception
	 */
	public boolean is_opened() throws Exception {
		return connection != null && !connection.isClosed();
	}
	
	/* basic method */
	/**
	 * file-copy
	 * @param src
	 * @param trg
	 * @throws Exception
	 */
	private void file_copy(File src, File trg) throws Exception {
		if(src.exists()) {
			FileInputStream fins = new FileInputStream(src);
			FileOutputStream fout = new FileOutputStream(trg);
			byte[] buffer = new byte[4096]; int length;
			while((length = fins.read(buffer)) != -1) {
				fout.write(buffer, 0, length);
			}
			fout.close(); fins.close(); 
		}
	}

}
