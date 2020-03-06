package __backup__;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A cache list is a list of directories under the specified directory
 * space. User can dispatch and release the cache directories with a
 * specified prefix. If a file is not in the cache any more, it is said
 * that the file has been out of date. Out-of-date file is invalid and must
 * be abandoned by the user.<br>
 * <br>
 * The names of cache directories are defined as following:<br>
 * <code> $PREFIX $[0~N-1] </code>
 * @author yukimula
 */
public class FileCacheList {
	
	protected String prefix;
	protected File root;
	protected List<File> cache;
	public FileCacheList(File root, String prefix) throws Exception {
		if(root == null || !root.exists() || !root.isDirectory())
			throw new IllegalArgumentException("Invalid root: null");
		else if(prefix == null)
			throw new IllegalArgumentException("Invalid prefix: null");
		else {
			this.prefix = prefix; this.root = root;
			cache = new ArrayList<File>(); update();
		}
	}
	
	/**
	 * get the root directory
	 * @return
	 */
	public File get_root() { return root; }
	/**
	 * number of cache directories
	 * @return
	 */
	public int size() { return cache.size(); }
	/**
	 * prefix of cache directories
	 * @return
	 */
	public String get_prefix() { return prefix; }
	/**
	 * get the kth cache
	 * @param k
	 * @return
	 * @throws Exception
	 */
	public File get_cache(int k) throws Exception {
		if(k < 0 || k >= cache.size())
			throw new IllegalArgumentException("Invalid access by " + k);
		else return this.cache.get(k);
	}
	/**
	 * get the set of cache files
	 * @return
	 */
	public Iterator<File> get_cache_list() { return cache.iterator(); }
	/**
	 * whether the file is still in the cache.
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public boolean available(File file) throws Exception {
		if(file == null)
			throw new IllegalArgumentException("Invalid file: null");
		else if(!file.exists()) return false;
		else if(!file.isDirectory()) return false;
		else {
			for(File target : cache) {
				if(file.equals(target))
					return true;
			}
			return false;
		}
	}
	
	/* setters */
	/**
	 * update the cache list from root directory
	 * @throws Exception
	 */
	private void update() throws Exception {
		this.cache.clear();
		File[] files = root.listFiles();
		for(int i = 0; i < files.length; i++) {
			File file = files[i];
			if(file.isDirectory()) {
				String name = FileProcess.name_of(file);
				if(name.startsWith(prefix)) {
					cache.add(file);
				}
			}
		}
	}
	/**
	 * clear all the cache files in the root
	 * @throws Exception
	 */
	public void clear() throws Exception {
		for(File target : cache)
			FileProcess.remove(target);
		this.cache.clear();
	}
	/**
	 * rebuild the cache list.
	 * This will remove the original file
	 * and then re-create a set of cache
	 * list under the root directory.
	 * @param n
	 * @throws Exception
	 */
	public void reset(int n) throws Exception {
		this.clear();
		
		for(int i = 0; i < n; i++) {
			File trg = FileProcess.file_of(root, prefix + i);
			trg = FileProcess.get_directory(trg);
			cache.add(trg);
		}
	}
	
}
