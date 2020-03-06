package com.jcsa.jcparse.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CField;
import com.jcsa.jcparse.lang.ctype.CFieldBody;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;

/**
 * To provide a base to determine the size of each instance based on their type
 * 
 * @author yukimula
 */
public class CSizeofBase {
	
	/** singleton for accessing local system platform information **/
	public static CSizeofBase sizeof_base;
	
	static {
		try {
			sizeof_base = CSizeofBase.parse(new File("config/csizeof.txt"));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);	/* fatal errors */
		}
	}
	protected int void_size, bool_size, char_size, short_size, int_size, long_size, long_long_size, float_size,
			double_size, long_double_size, float_complex_size, float_imaginary_size, double_complex_size,
			double_imaginary_size, long_double_complex_size, long_double_imaginary_size, address_size;

	private CSizeofBase() {
	}

	public int sizeof_void() {
		return void_size;
	}

	public int sizeof_bool() {
		return bool_size;
	}

	public int sizeof_char() {
		return char_size;
	}

	public int sizeof_short() {
		return short_size;
	}

	public int sizeof_int() {
		return int_size;
	}

	public int sizeof_long() {
		return long_size;
	}

	public int sizeof_llong() {
		return long_long_size;
	}

	public int sizeof_float() {
		return float_size;
	}

	public int sizeof_float_complex() {
		return float_complex_size;
	}

	public int sizeof_float_imaginary() {
		return float_imaginary_size;
	}

	public int sizeof_double() {
		return double_size;
	}

	public int sizeof_double_complex() {
		return double_complex_size;
	}

	public int sizeof_double_imaginary() {
		return double_imaginary_size;
	}

	public int sizeof_ldouble() {
		return long_double_size;
	}

	public int sizeof_ldouble_complex() {
		return long_double_complex_size;
	}

	public int sizeof_ldouble_imaginary() {
		return long_double_imaginary_size;
	}

	public int sizeof_address() {
		return address_size;
	}

	/**
	 * get the length of size based on system configuration
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public int sizeof(CType type) throws Exception {
		type = this.get_value_type_of(type);

		if (type == null || !type.is_defined())
			throw new IllegalArgumentException("Undefined type: " + type);
		else if (type instanceof CBasicType) {
			switch (((CBasicType) type).get_tag()) {
			case c_bool:
				return this.bool_size;
			case c_char:
			case c_uchar:
				return this.char_size;
			case c_short:
			case c_ushort:
				return this.short_size;
			case c_int:
			case c_uint:
				return this.int_size;
			case c_long:
			case c_ulong:
				return this.long_size;
			case c_llong:
			case c_ullong:
				return this.long_long_size;
			case c_float:
				return this.float_size;
			case c_float_complex:
				return this.float_complex_size;
			case c_float_imaginary:
				return this.float_imaginary_size;
			case c_double:
				return this.double_size;
			case c_double_complex:
				return this.double_complex_size;
			case c_double_imaginary:
				return this.double_imaginary_size;
			case c_ldouble:
				return this.long_double_size;
			case c_ldouble_complex:
				return this.long_double_complex_size;
			case c_ldouble_imaginary:
				return this.long_double_imaginary_size;
			default:
				return this.void_size;
			}
		} else if (type instanceof CArrayType) {
			int esize = sizeof(((CArrayType) type).get_element_type());
			return esize * ((CArrayType) type).length();
		} else if (type instanceof CPointerType)
			return this.address_size;
		else if (type instanceof CStructType) {
			CFieldBody body = ((CStructType) type).get_fields();
			int n = body.size(), fsize = 0;
			for (int i = 0; i < n; i++) {
				CField field = body.get_field(i);
				fsize += sizeof(field.get_type());
			}
			return fsize;
		} 
		else if (type instanceof CUnionType) {
			CFieldBody body = ((CUnionType) type).get_fields();
			int n = body.size(), fsize = 0, esize;
			for (int i = 0; i < n; i++) {
				CField field = body.get_field(i);
				esize = sizeof(field.get_type());
				if (fsize < esize)
					fsize = esize;
			}
			return fsize;
		} 
		else if (type instanceof CEnumType) {
			return this.int_size;
		}
		else if(type instanceof CQualifierType) {
			return sizeof(((CQualifierType) type).get_reference());
		}
		else {
			throw new IllegalArgumentException("Cannot interpret the size of " + type);
		}
	}

	/**
	 * parse the text to derive system template
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static CSizeofBase parse(File file) throws Exception {
		String line;
		CSizeofBase base = new CSizeofBase();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		while ((line = reader.readLine()) != null) {
			int index = line.indexOf(':');
			if (index < 0)
				continue;

			String int_text = line.substring(index + 1);
			int value = Integer.parseInt(int_text.trim());

			String prefix = line.substring(0, index).trim();
			if (prefix.equals("bool"))
				base.bool_size = value;
			else if (prefix.equals("char"))
				base.char_size = value;
			else if (prefix.equals("short"))
				base.short_size = value;
			else if (prefix.equals("int"))
				base.int_size = value;
			else if (prefix.equals("long"))
				base.long_size = value;
			else if (prefix.equals("llong"))
				base.long_long_size = value;
			else if (prefix.equals("float"))
				base.float_size = value;
			else if (prefix.equals("double"))
				base.double_size = value;
			else if (prefix.equals("ldouble"))
				base.long_double_size = value;
			else if (prefix.equals("cp_float"))
				base.float_complex_size = value;
			else if (prefix.equals("im_float"))
				base.float_imaginary_size = value;
			else if (prefix.equals("cp_double"))
				base.double_complex_size = value;
			else if (prefix.equals("im_double"))
				base.double_imaginary_size = value;
			else if (prefix.equals("cp_ldouble"))
				base.long_double_complex_size = value;
			else if (prefix.equals("im_ldouble"))
				base.long_double_imaginary_size = value;
			else if (prefix.equals("address"))
				base.address_size = value;
		}
		reader.close();
		base.void_size = 0;

		return base;
	}

	/**
	 * get the type for value of target
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private CType get_value_type_of(CType type) throws Exception {
		while (type instanceof CQualifierType)
			type = ((CQualifierType) type).get_reference();
		return type;
	}
	
	/**
	 * get the length of size based on system configuration
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static int Sizeof(CType type) throws Exception {
		return sizeof_base.sizeof(type);
	}
	
}
