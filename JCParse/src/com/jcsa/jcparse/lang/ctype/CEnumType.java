package com.jcsa.jcparse.lang.ctype;

/**
 * <code>(enum (enumerator)+)</code>
 *
 * @author yukimula
 */
public interface CEnumType extends CType {

	public String get_name();
	public CEnumeratorList get_enumerator_list();

	@Override
	public boolean is_defined();
}
