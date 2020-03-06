package com.jcsa.jcparse.lang.lexical;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * provide the map from keyword-identifier to keyword entity
 * 
 * @author yukimula
 */
public class CLangKeywordLib {

	/** map from name to keyword **/
	private Map<String, CKeyword> keywords;

	public CLangKeywordLib() {
		keywords = new HashMap<String, CKeyword>();
	}

	/**
	 * whether there is a keyword referring to this name in specified language
	 * lib
	 * 
	 * @param name
	 * @return
	 */
	public boolean has_keyword(String name) {
		if (name == null)
			return false;
		else
			return keywords.containsKey(name);
	}

	/**
	 * get the keyword of specified name
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 *             : name is not defined as keyword in the language lib
	 */
	public CKeyword get_keyword(String name) throws Exception {
		if (name == null || !keywords.containsKey(name))
			throw new IllegalArgumentException("Invalid access: undefined " + name);
		else
			return keywords.get(name);
	}

	/**
	 * add a keyword with its binding name in the language lib
	 * 
	 * @param name
	 * @param keyword
	 * @throws Exception
	 *             : name is duplicated
	 */
	public void insert_keyword(String name, CKeyword keyword) throws Exception {
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("Invalid name: null");
		else if (keyword == null)
			throw new IllegalArgumentException("Invalid keyword: null");
		else if (keywords.containsKey(name))
			throw new RuntimeException("Invalid access: duplicated \"" + name + "\"");
		else
			keywords.put(name, keyword);
	}

	/**
	 * unbound the specified name with its keyword
	 * 
	 * @param name
	 * @throws Exception
	 *             : name is not defined
	 */
	public void delete_keyword(String name) throws Exception {
		if (name == null || !keywords.containsKey(name))
			throw new IllegalArgumentException("Invalid access: undefined " + name);
		else
			keywords.remove(name);
	}

	/**
	 * clear the names in keyword lib
	 */
	public void clear() {
		keywords.clear();
	}

	/**
	 * get the number of names binded with keywords
	 */
	public int size() {
		return keywords.size();
	}

	/**
	 * get the collection of names binded with keywords
	 * 
	 * @return
	 */
	public Collection<String> get_names() {
		return keywords.keySet();
	}
}
