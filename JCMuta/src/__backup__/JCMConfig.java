package __backup__;

import java.io.File;

/**
 * configuration files for JCMuta
 * @author yukimula
 *
 */
public class JCMConfig {
	
	/** config/ **/
	public static final File JCM_CONFIG_ROOT;
	/** config/jcmuta.db **/
	public static final File JCM_DB_TEMPLATE;
	/** config/csizeof.txt **/
	public static final File JCM_CSIZE_TFILE;
	/** config/jcmuta.db **/
	public static final File JCM_HEADER_FILE;
	/** config/empty.txt **/
	public static final File JCM_EMPTY_FILE;
	/* getting configurations */
	static {
		JCM_CONFIG_ROOT = new File("config/");
		JCM_HEADER_FILE = new File("config/__jcmuta__.h");
		JCM_CSIZE_TFILE = new File("config/csizeof.txt");
		JCM_DB_TEMPLATE = new File("config/jcmuta.db");
		JCM_EMPTY_FILE	= new File("config/empty.txt");
		
		try{
			if(!JCM_CONFIG_ROOT.exists())
				throw new RuntimeException();
		}
		catch(Exception ex) {
			System.err.println("config/ is not found, NOW EXIT!");
			System.exit(1);
		}
		
		try{
			if(!JCM_HEADER_FILE.exists())
				throw new RuntimeException();
		}
		catch(Exception ex) {
			System.err.println("jcm-header not found, NOW EXIT!");
			System.exit(1);
		}
		
		try{
			if(!JCM_CSIZE_TFILE.exists())
				throw new RuntimeException();
		}
		catch(Exception ex) {
			System.err.println("csizeof-file not found, NOW EXIT!");
			System.exit(1);
		}
		
		try{
			if(!JCM_DB_TEMPLATE.exists())
				throw new RuntimeException();
		}
		catch(Exception ex) {
			System.err.println("jcm-database not found, NOW EXIT!");
			System.exit(1);
		}
		
		try{
			if(!JCM_EMPTY_FILE.exists())
				throw new RuntimeException();
		}
		catch(Exception ex) {
			System.err.println("empty.txt is not found, NOW EXIT!");
			System.exit(1);
		}
	}
	
	
}
