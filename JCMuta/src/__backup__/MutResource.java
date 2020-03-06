package __backup__;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CNameTable;
import com.jcsa.jcparse.lang.scope.CScope;

/**
 * To provide resource for mutation replacement
 * @author yukimula
 */
public class MutResource {
	
	/* attributes */
	protected Map<String, AstConstant> constants;
	protected Set<AstName> chars_names, intes_names, union_names,
		reals_names, array_names, point_names, strut_names;
	
	/* constructor */
	protected MutResource() {
		constants   = new HashMap<String, AstConstant>();
		chars_names = new HashSet<AstName>();
		intes_names = new HashSet<AstName>();
		reals_names = new HashSet<AstName>();
		array_names = new HashSet<AstName>();
		point_names = new HashSet<AstName>();
		strut_names = new HashSet<AstName>();
		union_names = new HashSet<AstName>();
	}
	
	/* collect methods */
	/**
	 * clear all information
	 */
	protected void initialize() {
		constants.clear();
		chars_names.clear();
		intes_names.clear();
		reals_names.clear();
		array_names.clear();
		point_names.clear();
		strut_names.clear();
		union_names.clear();
	}
	/**
	 * collect all the constants in specified node
	 * @param root
	 */
	protected void collect_constants(AstNode root) {
		Queue<AstNode> queue = new LinkedList<AstNode>();
		
		queue.add(root); AstNode node;
		while(!queue.isEmpty()) {
			node = queue.poll();
			if(node instanceof AstConstant)
				constants.put(name_of(node), (AstConstant) node);
			else {
				int n = node.number_of_children();
				for(int k = 0; k < n; k++) 
					queue.add(node.get_child(k));
			}
		}
	}
	/**
	 * collect all reference in the local of scope
	 * @param scope
	 * @throws Exception
	 */
	protected void collect_reference(CScope scope) throws Exception {
		/* declarations */
		CNameTable table = scope.get_name_table();
		Iterator<String> names = table.get_names();
		
		while(names.hasNext()) {
			/* get the next name from scope */
			String name = names.next();
			CName cname = table.get_name(name);
			
			if(cname instanceof CInstanceName
				&& cname.get_source() instanceof AstName) {
				/* get the type and ast-name */
				CInstanceName insname = (CInstanceName) cname;
				CType type = insname.get_instance().get_type();
				CType vtype = JC_Classifier.get_value_type(type);
				AstName astname = (AstName) cname.get_source();
				
				/* update references */
				if(JC_Classifier.is_character_type(vtype))
					chars_names.add(astname);
				else if(JC_Classifier.is_integer_type(vtype))
					intes_names.add(astname);
				else if(JC_Classifier.is_real_type(vtype))
					reals_names.add(astname);
				else if(vtype instanceof CArrayType)
					array_names.add(astname);
				else if(vtype instanceof CPointerType)
					point_names.add(astname);
				else if(vtype instanceof CStructType)
					strut_names.add(astname);
				else if(vtype instanceof CUnionType)
					union_names.add(astname);
			}
		}
	}
	
	/* basic method */
	private String name_of(AstNode node) {
		return node.get_location().read();
	}
}
