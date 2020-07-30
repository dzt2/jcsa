package com.jcsa.jcparse.flwa.dynamics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;

/**
 * The execution path in form of AstNode.
 * 
 * @author yukimula
 *
 */
public class AstPath {
	
	/* attribute and constructor */
	private List<AstPathElement> elements;
	private AstPath() { this.elements = new ArrayList<AstPathElement>(); }
	
	/* getters */
	/**
	 * @return the number of elements in the execution path
	 */
	public int length() { return this.elements.size(); }
	/**
	 * @return the elements in the execution path in form of AST
	 */
	public Iterable<AstPathElement> get_elements() { return this.elements; }
	/**
	 * @param k
	 * @return the kth element in the execution path.
	 * @throws IndexOutOfBoundsException
	 */
	public AstPathElement get_element(int k) throws IndexOutOfBoundsException {
		return this.elements.get(k);
	}
	
	/* parsing */
	/**
	 * @param tree abstract syntactic tree
	 * @param file instrumental result file
	 * @return the execution path in form of the AST with respect to the instrumental file as given or null if the file is not defined
	 * @throws Exception
	 */
	public static AstPath path(AstTree tree, File file) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else if(file == null || !file.exists()) return null;
		else {
			AstPath path = new AstPath(); String line;
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while((line = reader.readLine()) != null) {
				line = line.strip();
				if(!line.isEmpty()) {
					String[] items = line.split(" ");
					AstNode location = tree.get_node(Integer.parseInt(items[0].strip()));
					byte[] status = new byte[items.length - 1];
					for(int k = 1; k < items.length; k++) {
						status[k - 1] = (byte) Integer.parseInt(items[k].strip());
					}
					AstPathElement element = new AstPathElement(
							path, path.elements.size(), location, status);
					path.elements.add(element);
				}
			}
			reader.close();
			return path;
		}
	}
	
}
