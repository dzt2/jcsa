package __backup__;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeclarator;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirIdentifier;
import com.jcsa.jcparse.lang.irlang.expr.CirImplicator;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirReturnPoint;
import com.jcsa.jcparse.lang.irlang.expr.CirStringLiteral;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirBinAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirInitAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * The word used to describe <code>CirNode | CType | CirInfluenceEdge | CirSemanticWord</code>:<br>
 * (1) CirNode 			==> [attribute+]<br>
 * (2) CType			==> (attribute+)<br>
 * (3) CirInfluenceEdge	==>	<attribute+><br>
 * (4) CirSemanticNode	==> {attribute+}<br>
 * @author yukimula
 *
 */
public class CWord {
	
	private static final StringBuilder buffer = new StringBuilder();
	private static final StringBuilder toString = new StringBuilder();
	
	/* attribtues */
	/** the type of the object that the word describes **/
	private CWordType type;
	/** the object that this word describes **/
	private Object source;
	/** the set of attributes that define this word **/
	private List<String> attributes;
	
	/* constructor */
	/**
	 * create a word that describe the specified node
	 * @param source
	 * @throws Exception
	 */
	private CWord(Object source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else { 
			this.source = source; this.type = this.get_type(source);
			this.attributes = new ArrayList<String>();
		}
	}
	/**
	 * determine the type of the object that the word describes.
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private CWordType get_type(Object source) throws Exception {
		if(source instanceof CirNode)
			return CWordType.node;
		else if(source instanceof CType)
			return CWordType.type;
		else if(source instanceof CirInfluenceEdge)
			return CWordType.edge;
		else if(source instanceof CirSemanticNode)
			return CWordType.prop;
		else throw new IllegalArgumentException("Invalid source: null");
	}
	
	/* getters */
	/**
	 * get the type of the object that the word describes
	 * @return
	 */
	public CWordType get_type() { return this.type; }
	/**
	 * get the object that the word describes
	 * @return
	 */
	public Object get_source() { return this.source; }
	/**
	 * get the sequence of attributes that define this word
	 * @return
	 */
	public Iterable<String> get_attributes() { return this.attributes; }
	/**
	 * get the number of attributes in the word
	 * @return
	 */
	public int number_of_attributes() { return this.attributes.size(); }
	/**
	 * get the kth attribute of the word
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public String get_attribute(int k) throws IndexOutOfBoundsException { return attributes.get(k); }
	@Override
	public String toString() {
		toString.setLength(0);
		
		char prefix, postfix;
		switch(type) {
		case node:	prefix = '[';	postfix = ']';	break;
		case edge:	prefix = '<';	postfix = '>';	break;
		case prop:	prefix = '{';	postfix = '}';	break;
		case type:	prefix = '(';	postfix = ')';	break;
		default: throw new IllegalArgumentException("Invalid type");
		}
		
		toString.append(prefix).append(' ');
		for(String attribute : this.attributes) {
			toString.append(attribute);
			toString.append(' ');
		}
		toString.append(postfix);
		
		return toString.toString();
	}
	
	/* setters */
	/**
	 * translate the original name of attribute as the normalized way
	 * @param attribute
	 * @return
	 */
	private String normalize(String attribute) {
		buffer.setLength(0);
		
		for(int k = 0; k < attribute.length(); k++) {
			char ch = attribute.charAt(k);
			if(Character.isAlphabetic(ch) 
				|| Character.isDigit(ch) 
				|| ch == '_' || ch == '.') {
				buffer.append(ch);
			}
			else {
				switch(ch) {
				case '\b': 	buffer.append('\\').append('b');	break;
				case '\f': 	buffer.append('\\').append('f');	break;
				case '\n': 	buffer.append('\\').append('n');	break;
				case '\r': 	buffer.append('\\').append('r');	break;
				case '\t': 	buffer.append('\\').append('t');	break;
				case '\\': 	buffer.append("\\\\");				break;
				case '\'': 	buffer.append('\\').append('\'');	break;
				case '\"': 	buffer.append('\\').append('\"');	break;
				case '<': 	buffer.append('\\').append('<');	break;
				case '>': 	buffer.append('\\').append('>');	break;
				case '(': 	buffer.append('\\').append('(');	break;
				case ')': 	buffer.append('\\').append(')');	break;
				case '[': 	buffer.append('\\').append('[');	break;
				case ']': 	buffer.append('\\').append(']');	break;
				case '{': 	buffer.append('\\').append('{');	break;
				case '}': 	buffer.append('\\').append('}');	break;
				case ' ':	buffer.append('\\').append('s');	break;
				default: 	buffer.append(ch);					break;
				}
			}
		}
		
		return buffer.toString();
	}
	private void add(CWordAttribute attribute) {
		String name = attribute.toString();
		if(name.endsWith("_kw")) 
			name = name.substring(0, name.length() - 3);
		name = "@" + name.strip();
		this.attributes.add(name);
	}
	private void add(Boolean value) {
		if(value)
			this.attributes.add("@true");
		else this.attributes.add("@false");
	}
	private void add(Character value) {
		this.attributes.add(this.normalize(value.toString()));
	}
	private void add(Short value) {
		this.attributes.add(this.normalize(value.toString()));
	}
	private void add(Integer value) {
		this.attributes.add(this.normalize(value.toString()));
	}
	private void add(Long value) {
		this.attributes.add(this.normalize(value.toString()));
	}
	private void add(Float value) {
		this.attributes.add(this.normalize(value.toString()));
	}
	private void add(Double value) {
		this.attributes.add(this.normalize(value.toString()));
	}
	private void add(String value) {
		this.attributes.add(this.normalize(value.toString()));
	}
	/**
	 * add the attribute to the tail of the word
	 * @param attribute
	 * @throws Exception
	 */
	protected void append(Object attribute) throws Exception {
		if(attribute instanceof CWordAttribute)
			this.add((CWordAttribute) attribute);
		else if(attribute instanceof Boolean)
			this.add((Boolean) attribute);
		else if(attribute instanceof Character)
			this.add((Character) attribute);
		else if(attribute instanceof Short)
			this.add((Short) attribute);
		else if(attribute instanceof Integer)
			this.add((Integer) attribute);
		else if(attribute instanceof Long)
			this.add((Long) attribute);
		else if(attribute instanceof Float)
			this.add((Float) attribute);
		else if(attribute instanceof Double)
			this.add((Double) attribute);
		else if(attribute instanceof String)
			this.add((String) attribute);
		else throw new IllegalArgumentException("Invalid attribute: " + attribute);
	}
	
	/* generators */
	/**
	 * create a word to describe the syntactic node in C-like 
	 * intermediate representation code.
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static CWord word(CirNode source) throws Exception {
		CWord word = new CWord(source);
		if(source instanceof CirIdentifier) {
			word.add(CWordAttribute.expression);
			word.add(CWordAttribute.reference);
			word.add(CWordAttribute.identifier);
			word.add(((CirIdentifier) source).get_name());
		}
		else if(source instanceof CirDeclarator) {
			word.add(CWordAttribute.expression);
			word.add(CWordAttribute.reference);
			word.add(CWordAttribute.identifier);
			word.add(((CirDeclarator) source).get_name());
		}
		else if(source instanceof CirImplicator) {
			word.add(CWordAttribute.expression);
			word.add(CWordAttribute.reference);
			word.add(CWordAttribute.register);
		}
		else if(source instanceof CirReturnPoint) {
			word.add(CWordAttribute.expression);
			word.add(CWordAttribute.reference);
			word.add(CWordAttribute.returning);
		}
		else if(source instanceof CirDeferExpression) {
			word.add(CWordAttribute.expression);
			word.add(CWordAttribute.reference);
			word.add(CWordAttribute.deference);
		}
		else if(source instanceof CirFieldExpression) {
			word.add(CWordAttribute.expression);
			word.add(CWordAttribute.reference);
			word.add(CWordAttribute.field);
		}
		else if(source instanceof CirField) {
			word.add(CWordAttribute.identifier);
			word.add(CWordAttribute.field);
		}
		else if(source instanceof CirLabel) {
			word.add(CWordAttribute.identifier);
			word.add(CWordAttribute.skiping);
		}
		else if(source instanceof CirConstExpression) {
			word.add(CWordAttribute.expression);
			word.add(CWordAttribute.value);
			word.add(CWordAttribute.constant);
			CConstant constant = ((CirConstExpression) source).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:		word.add(constant.get_bool());		break;
			case c_char:
			case c_uchar:		word.add(constant.get_char());		break;
			case c_short:
			case c_ushort:		
			case c_int:
			case c_uint:		word.add(constant.get_integer());	break;
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:		word.add(constant.get_long());		break;
			case c_float:		word.add(constant.get_float());		break;
			case c_double:
			case c_ldouble:		word.add(constant.get_double());	break;
			default: throw new IllegalArgumentException("Invalid: " + constant.get_type());
			}
		}
		else if(source instanceof CirStringLiteral) {
			word.add(CWordAttribute.expression);
			word.add(CWordAttribute.value);
			word.add(CWordAttribute.literal);
			word.add("\"" + ((CirStringLiteral) source).get_literal() + "\"");
		}
		else if(source instanceof CirAddressExpression) {
			word.add(CWordAttribute.expression);
			word.add(CWordAttribute.value);
			word.add(CWordAttribute.address);
		}
		else if(source instanceof CirCastExpression) {
			word.add(CWordAttribute.expression);
			word.add(CWordAttribute.value);
			word.add(CWordAttribute.casting);
			word.add(CWordAttribute.assignment);
		}
		else if(source instanceof CirWaitExpression) {
			word.add(CWordAttribute.expression);
			word.add(CWordAttribute.value);
			word.add(CWordAttribute.returning);
		}
		else if(source instanceof CirDefaultValue) {
			word.add(CWordAttribute.expression);
			word.add(CWordAttribute.value);
			word.add(CWordAttribute.initial);
		}
		else if(source instanceof CirInitializerBody) {
			word.add(CWordAttribute.expression);
			word.add(CWordAttribute.value);
			word.add(CWordAttribute.initial);
			word.add(CWordAttribute.sequence);
		}
		else if(source instanceof CirComputeExpression) {
			word.add(CWordAttribute.expression);
			word.add(CWordAttribute.value);
			COperator operator = ((CirComputeExpression) source).get_operator();
			switch(operator) {
			case negative:		word.add(CWordAttribute.arith_neg);		break;
			case bit_not:		word.add(CWordAttribute.bitws_rsv);		break;
			case logic_not:		word.add(CWordAttribute.logic_not);		break;
			case arith_add:		word.add(CWordAttribute.arith_add);		break;
			case arith_sub:		word.add(CWordAttribute.arith_sub);		break;
			case arith_mul:		word.add(CWordAttribute.arith_mul);		break;
			case arith_div:		word.add(CWordAttribute.arith_div);		break;
			case arith_mod:		word.add(CWordAttribute.arith_mod);		break;
			case bit_and:		word.add(CWordAttribute.bitws_and);		break;
			case bit_or:		word.add(CWordAttribute.bitws_ior);		break;
			case bit_xor:		word.add(CWordAttribute.bitws_xor);		break;
			case left_shift:	word.add(CWordAttribute.bitws_lsh);		break;
			case righ_shift:	word.add(CWordAttribute.bitws_rsh);		break;
			case smaller_tn:	word.add(CWordAttribute.smaller_tn);	break;
			case smaller_eq:	word.add(CWordAttribute.smaller_tn);	
								word.add(CWordAttribute.equal_with);	break;
			case greater_tn:	word.add(CWordAttribute.logic_not);		
								word.add(CWordAttribute.smaller_tn);	break;
			case greater_eq:	word.add(CWordAttribute.logic_not);		
								word.add(CWordAttribute.smaller_tn);	
								word.add(CWordAttribute.equal_with);	break;
			case equal_with:	word.add(CWordAttribute.equal_with);	break;
			case not_equals:	word.add(CWordAttribute.logic_not);	
								word.add(CWordAttribute.equal_with);	break;
			default: throw new IllegalArgumentException("Invalid: " + operator);
			}
		}
		else if(source instanceof CirBinAssignStatement) {
			word.add(CWordAttribute.statement);
			word.add(CWordAttribute.assignment);
			word.add(CWordAttribute.value);
		}
		else if(source instanceof CirIncreAssignStatement) {
			word.add(CWordAttribute.statement);
			word.add(CWordAttribute.assignment);
			word.add(CWordAttribute.increment);
		}
		else if(source instanceof CirInitAssignStatement) {
			word.add(CWordAttribute.statement);
			word.add(CWordAttribute.assignment);
			word.add(CWordAttribute.initial);
		}
		else if(source instanceof CirSaveAssignStatement) {
			word.add(CWordAttribute.statement);
			word.add(CWordAttribute.assignment);
			word.add(CWordAttribute.register);
		}
		else if(source instanceof CirReturnAssignStatement) {
			word.add(CWordAttribute.statement);
			word.add(CWordAttribute.assignment);
			word.add(CWordAttribute.returning);
		}
		else if(source instanceof CirWaitAssignStatement) {
			word.add(CWordAttribute.statement);
			word.add(CWordAttribute.assignment);
			word.add(CWordAttribute.calling);
		}
		else if(source instanceof CirGotoStatement) {
			word.add(CWordAttribute.statement);
			word.add(CWordAttribute.transition);
			word.add(CWordAttribute.skiping);
		}
		else if(source instanceof CirIfStatement) {
			word.add(CWordAttribute.statement);
			word.add(CWordAttribute.transition);
			word.add(CWordAttribute.condition);
			word.add(CWordAttribute.if_kw);
		}
		else if(source instanceof CirCaseStatement) {
			word.add(CWordAttribute.statement);
			word.add(CWordAttribute.transition);
			word.add(CWordAttribute.condition);
			word.add(CWordAttribute.case_kw);
		}
		else if(source instanceof CirCallStatement) {
			word.add(CWordAttribute.statement);
			word.add(CWordAttribute.transition);
			word.add(CWordAttribute.calling);
		}
		else if(source instanceof CirTagStatement) {
			word.add(CWordAttribute.statement);
			word.add(CWordAttribute.dead);
		}
		else throw new IllegalArgumentException(source.getClass().getSimpleName());
		return word;
	}
	/**
	 * get the word to describe the data type
	 * @param source
	 * @throws Exception
	 */
	public static CWord word(CType source) throws Exception {
		CWord word = new CWord(source);
		
		if(source instanceof CBasicType) {
			switch(((CBasicType) source).get_tag()) {
			case c_void:	word.add(CWordAttribute.void_kw);		break;
			case c_bool:	word.add(CWordAttribute.number);
							word.add(CWordAttribute.boolean_kw);	break;
			case c_char:	word.add(CWordAttribute.number);
							word.add(CWordAttribute.integer);
							word.add(CWordAttribute.character);		break;
			case c_uchar:	word.add(CWordAttribute.number);
							word.add(CWordAttribute.integer);
							word.add(CWordAttribute.character);		
							word.add(CWordAttribute.unsigned); 		break;
			case c_short:	word.add(CWordAttribute.number);
							word.add(CWordAttribute.integer);
							word.add(CWordAttribute.short_kw);		break;
			case c_ushort:	word.add(CWordAttribute.number);
							word.add(CWordAttribute.integer);
							word.add(CWordAttribute.short_kw);		
							word.add(CWordAttribute.unsigned); 		break;
			case c_int:		word.add(CWordAttribute.number);
							word.add(CWordAttribute.integer);		break;
			case c_uint:	word.add(CWordAttribute.number);
							word.add(CWordAttribute.integer);
							word.add(CWordAttribute.unsigned);		break;
			case c_long:	word.add(CWordAttribute.number);
							word.add(CWordAttribute.integer);
							word.add(CWordAttribute.long_kw);		break;
			case c_ulong:	word.add(CWordAttribute.number);
							word.add(CWordAttribute.integer);
							word.add(CWordAttribute.long_kw);		
							word.add(CWordAttribute.unsigned); 		break;
			case c_llong:	word.add(CWordAttribute.number);
							word.add(CWordAttribute.integer);
							word.add(CWordAttribute.long_kw);		
							word.add(CWordAttribute.long_kw); 		break;
			case c_ullong:	word.add(CWordAttribute.number);
							word.add(CWordAttribute.integer);
							word.add(CWordAttribute.long_kw);		
							word.add(CWordAttribute.long_kw); 	
							word.add(CWordAttribute.unsigned); 		break;
			case c_float:	word.add(CWordAttribute.number);
							word.add(CWordAttribute.real);
							word.add(CWordAttribute.short_kw); 		break;
			case c_double:	word.add(CWordAttribute.number);
							word.add(CWordAttribute.real);			break;
			case c_ldouble:	word.add(CWordAttribute.number);
							word.add(CWordAttribute.real);
							word.add(CWordAttribute.long_kw); 		break;
			case c_float_complex:
							word.add(CWordAttribute.number);
							word.add(CWordAttribute.real);
							word.add(CWordAttribute.short_kw);
							word.add(CWordAttribute.complex);		break;
			case c_double_complex:
							word.add(CWordAttribute.number);
							word.add(CWordAttribute.real);
							word.add(CWordAttribute.complex);		break;
			case c_ldouble_complex:
							word.add(CWordAttribute.number);
							word.add(CWordAttribute.real);
							word.add(CWordAttribute.long_kw);
							word.add(CWordAttribute.complex);		break;
			case c_float_imaginary:
							word.add(CWordAttribute.number);
							word.add(CWordAttribute.real);
							word.add(CWordAttribute.short_kw);
							word.add(CWordAttribute.imaginary);		break;
			case c_double_imaginary:
							word.add(CWordAttribute.number);
							word.add(CWordAttribute.real);
							word.add(CWordAttribute.imaginary);		break;
			case c_ldouble_imaginary:
							word.add(CWordAttribute.number);
							word.add(CWordAttribute.real);
							word.add(CWordAttribute.long_kw);
							word.add(CWordAttribute.imaginary);		break;
			case gnu_va_list:
							word.add(CWordAttribute.calling);
							word.add(CWordAttribute.sequence);		break;
			default: throw new IllegalArgumentException(source.toString());
			}
		}
		else if(source instanceof CArrayType) {
			word.add(CWordAttribute.address);
			word.add(CWordAttribute.sequence);
			word.add(((CArrayType) source).length());
			
			CWord eword = word(((CArrayType) source).get_element_type());
			for(String attribute : eword.attributes) { word.attributes.add(attribute); }
		}
		else if(source instanceof CPointerType) {
			word.add(CWordAttribute.address);
			CWord eword = word(((CPointerType) source).get_pointed_type());
			for(String attribute : eword.attributes) { word.attributes.add(attribute); }
		}
		else if(source instanceof CFunctionType) {
			word.add(CWordAttribute.address);
			word.add(CWordAttribute.calling);
			CWord eword = word(((CFunctionType) source).get_return_type());
			for(String attribute : eword.attributes) { word.attributes.add(attribute); }
		}
		else if(source instanceof CStructType) {
			word.add(CWordAttribute.struct);
			String name = ((CStructType) source).get_name();
			if(name != null && !name.isEmpty())
				word.add(name);
		}
		else if(source instanceof CUnionType) {
			word.add(CWordAttribute.union);
			String name = ((CUnionType) source).get_name();
			if(name != null && !name.isEmpty())
				word.add(name);
		}
		else if(source instanceof CEnumType) {
			word.add(CWordAttribute.number);
			word.add(CWordAttribute.integer);
		}
		else if(source instanceof CQualifierType) {
			return word(((CQualifierType) source).get_reference());
		}
		else throw new IllegalArgumentException("Invalid: " + source);
		
		return word;
	}
	/**
	 * get the word to describe the influence relationship
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static CWord word(CirInfluenceEdge source) throws Exception {
		CWord word = new CWord(source);
		
		switch(source.get_type()) {
		case exec_a: 	word.add(CWordAttribute.calling); 		break;
		case exec_c: 	word.add(CWordAttribute.condition); 	break;
		case exec_e: 	word.add(CWordAttribute.expression); 	break;
		case exec_t: 	word.add(CWordAttribute.true_kw); 		break;
		case exec_f: 	word.add(CWordAttribute.false_kw); 		break;
		case exec_p: 	
		{
			word.add(CWordAttribute.argument); 
			word.add(source.get_target().get_cir_source().get_child_index());
		}
		break;
		case pas_du:
		case pas_ud:
		case pas_ap:
		case pas_rw:	word.add(CWordAttribute.assignment);	break;
		case gen_af:	
		{
			word.add(CWordAttribute.operand);
			word.add(source.get_source().get_cir_source().get_child_index());
		}
		break;
		case gen_fw:	word.add(CWordAttribute.returning);		break;
		case gen_cp:	
		{
			CirNode child = source.get_source().get_cir_source();
			CirNode parent = source.get_target().get_cir_source();
			if(parent != child.get_parent())
				throw new IllegalArgumentException("Not child-parent!");
			else if(parent instanceof CirDeferExpression) {
				word.add(CWordAttribute.address);
			}
			else if(parent instanceof CirFieldExpression) {
				if(((CirFieldExpression) parent).get_body() == child)
					word.add(CWordAttribute.sequence);
				else { word.add(CWordAttribute.field); }
			}
			else if(parent instanceof CirAddressExpression) {
				word.add(CWordAttribute.reference);
			}
			else if(parent instanceof CirCastExpression) {
				word.add(CWordAttribute.value);
			}
			else if(parent instanceof CirInitializerBody) {
				word.add(CWordAttribute.element);
				word.add(child.get_child_index());
			}
			else if(parent instanceof CirComputeExpression) {
				word.add(CWordAttribute.operand);
				word.add(child.get_child_index());
			}
			else {
				throw new IllegalArgumentException("Invalid parent: " + parent.getClass().getSimpleName());
			}
		}
		break;
		default: throw new IllegalArgumentException("Invalid edge: " + source.get_type());
		}
		
		word.add(CWordAttribute.on); 
		return word;
	}
	/**
	 * get the word to describe the dependence relationship in reversed way
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static CWord dword(CirInfluenceEdge source) throws Exception {
		CWord word = new CWord(source);
		
		switch(source.get_type()) {
		case exec_a: 	word.add(CWordAttribute.calling); 		break;
		case exec_c: 	word.add(CWordAttribute.condition); 	break;
		case exec_e: 	word.add(CWordAttribute.expression); 	break;
		case exec_t: 	word.add(CWordAttribute.true_kw); 		break;
		case exec_f: 	word.add(CWordAttribute.false_kw); 		break;
		case exec_p: 	
		{
			word.add(CWordAttribute.argument); 
			word.add(source.get_target().get_cir_source().get_child_index());
		}
		break;
		case pas_du:
		case pas_ud:
		case pas_ap:
		case pas_rw:	word.add(CWordAttribute.assignment);	break;
		case gen_af:	
		{
			word.add(CWordAttribute.operand);
			word.add(source.get_source().get_cir_source().get_child_index());
		}
		break;
		case gen_fw:	word.add(CWordAttribute.returning);		break;
		case gen_cp:	
		{
			CirNode child = source.get_source().get_cir_source();
			CirNode parent = source.get_target().get_cir_source();
			if(parent != child.get_parent())
				throw new IllegalArgumentException("Not child-parent!");
			else if(parent instanceof CirDeferExpression) {
				word.add(CWordAttribute.address);
			}
			else if(parent instanceof CirFieldExpression) {
				if(((CirFieldExpression) parent).get_body() == child)
					word.add(CWordAttribute.sequence);
				else { word.add(CWordAttribute.field); }
			}
			else if(parent instanceof CirAddressExpression) {
				word.add(CWordAttribute.reference);
			}
			else if(parent instanceof CirCastExpression) {
				word.add(CWordAttribute.value);
			}
			else if(parent instanceof CirInitializerBody) {
				word.add(CWordAttribute.element);
				word.add(child.get_child_index());
			}
			else if(parent instanceof CirComputeExpression) {
				word.add(CWordAttribute.operand);
				word.add(child.get_child_index());
			}
			else {
				throw new IllegalArgumentException("Invalid parent: " + parent.getClass().getSimpleName());
			}
		}
		break;
		default: throw new IllegalArgumentException("Invalid edge: " + source.get_type());
		}
		
		word.add(CWordAttribute.by); return word;
	}
	/**
	 * get the word to describe the semantic word
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static CWord word(CirSemanticNode source) throws Exception {
		CWord word = new CWord(source);
		
		switch(source.get_word()) {
		case cover:
		{
			word.add(CWordAttribute.cover);
			word.add(1);
		}
		break;
		case repeat:
		{
			word.add(CWordAttribute.cover);
			word.append(source.get_parameter());
		}
		break;
		case execute:
		{
			word.add(CWordAttribute.execute);
		}
		break;
		case non_execute:
		{
			word.add(CWordAttribute.logic_not);
			word.add(CWordAttribute.execute);
		}
		break;
		case traping:
		{
			word.add(CWordAttribute.traping);
		}
		break;
		case equal_with:
		{
			word.add(CWordAttribute.equal_with);
			
			Object parameter = source.get_parameter();
			if(parameter != null) {
				if(!(parameter instanceof CirNode))
					word.append(parameter);
			}
		}
		break;
		case not_equals:
		{
			word.add(CWordAttribute.logic_not);
			word.add(CWordAttribute.equal_with);
			
			Object parameter = source.get_parameter();
			if(parameter != null) {
				if(!(parameter instanceof CirNode))
					word.append(parameter);
			}
		}
		break;
		case smaller_tn:
		{
			word.add(CWordAttribute.smaller_tn);
			
			Object parameter = source.get_parameter();
			if(parameter != null) {
				if(!(parameter instanceof CirNode))
					word.append(parameter);
			}
		}
		break;
		case smaller_eq:
		{
			word.add(CWordAttribute.smaller_tn);
			word.add(CWordAttribute.equal_with);
			
			Object parameter = source.get_parameter();
			if(parameter != null) {
				if(!(parameter instanceof CirNode))
					word.append(parameter);
			}
		}
		break;
		case greater_tn:
		{
			word.add(CWordAttribute.logic_not);
			word.add(CWordAttribute.smaller_tn);
			
			Object parameter = source.get_parameter();
			if(parameter != null) {
				if(!(parameter instanceof CirNode))
					word.append(parameter);
			}
		}
		break;
		case greater_eq:
		{
			word.add(CWordAttribute.logic_not);
			word.add(CWordAttribute.smaller_tn);
			word.add(CWordAttribute.equal_with);
			
			Object parameter = source.get_parameter();
			if(parameter != null) {
				if(!(parameter instanceof CirNode))
					word.append(parameter);
			}
		}
		break;
		case in_range:
		{
			word.add(CWordAttribute.in);
			word.append(source.get_parameter());
		}
		break;
		case not_in_range:
		{
			word.add(CWordAttribute.logic_not);
			word.add(CWordAttribute.in);
			word.append(source.get_parameter());
		}
		break;
		case bit_intersc:
		{
			word.add(CWordAttribute.bitws_and);
			word.add(CWordAttribute.logic_not);
			word.add(CWordAttribute.equal_with);
			word.add(0);
		}
		break;
		case bit_include:
		{
			word.add(CWordAttribute.bitws_and);
			word.add(CWordAttribute.equal_with);
		}
		break;
		case bit_exclude:
		{
			word.add(CWordAttribute.bitws_and);
			word.add(CWordAttribute.equal_with);
			word.add(0);
		}
		break;
		case bno_include:
		{
			word.add(CWordAttribute.bitws_and);
			word.add(CWordAttribute.logic_not);
			word.add(CWordAttribute.equal_with);
		}
		break;
		case is_multiply:
		{
			word.add(CWordAttribute.arith_mul);
			word.add(CWordAttribute.integer);
			word.add(CWordAttribute.equal_with);
		}
		break;
		case is_negative:
		{
			word.add(CWordAttribute.arith_neg);
			word.add(CWordAttribute.equal_with);
		}
		break;
		case not_negative:
		{
			word.add(CWordAttribute.arith_neg);
			word.add(CWordAttribute.logic_not);
			word.add(CWordAttribute.equal_with);
		}
		break;
		case inc_val:
		{
			word.add(CWordAttribute.increment);
			word.append(source.get_parameter());
		}
		break;
		case set_val:
		{
			word.add(CWordAttribute.assignment);
			word.append(source.get_parameter());
		}
		break;
		case neg_val:
		{
			COperator operator = (COperator) source.get_parameter();
			switch(operator) {
			case negative:	word.add(CWordAttribute.arith_neg); break;
			case bit_not:	word.add(CWordAttribute.bitws_rsv); break;
			case logic_not:	word.add(CWordAttribute.logic_not);	break;
			default: throw new IllegalArgumentException("Invalid operator");
			}
		}
		break;
		case chg_val:
		{
			word.add(CWordAttribute.assignment);
		}
		break;
		default: throw new IllegalArgumentException(source.get_word().toString());
		}
		
		return word;
	}
	
}
