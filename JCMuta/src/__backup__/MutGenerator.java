package __backup__;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstExternalUnit;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.scope.CScope;

/**
 * To generate mutation among AST-node
 * @author yukimula
 */
public class MutGenerator {
	
	/* attributes */
	protected MutResource global;
	protected MutResource locals;
	protected MutSource source;
	protected MutationFactory producer;
	protected AstTranslationUnit astroot;
	
	/* constructor */
	public MutGenerator() {
		astroot = null;
		global = new MutResource(); 
		locals = new MutResource();
		source = new MutSource();
		producer = new MutationFactory();
	}
	
	/* main method */
	/**
	 * generate all the available mutations among file
	 * @param astroot
	 * @return
	 * @throws Exception
	 */
	public Set<Mutation> generate_all(AstTranslationUnit astroot) throws Exception {
		Set<Mutation> mutations = new HashSet<Mutation>();
		
		this.open(astroot);
		int n  = astroot.number_of_children();
		for(int k = 0; k < n; k++) {
			AstExternalUnit unit = astroot.get_unit(k);
			if(unit instanceof AstFunctionDefinition) {
				this.generate((AstFunctionDefinition) unit, mutations);
			}
		}
		this.close();
		
		return mutations;
	}
	
	/* IO methods */
	/**
	 *  initialize the generator with specified source file
	 * @param astroot
	 * @throws Exception
	 */
	protected void open(AstTranslationUnit astroot) throws Exception {
		if(astroot == null)
			throw new IllegalArgumentException("Invalid input: null");
		else {
			/* set input data */
			this.close(); this.astroot = astroot;
			
			/* collect global information */
			int n = astroot.number_of_units();
			for(int i = 0; i < n; i++) {
				AstExternalUnit unit = astroot.get_unit(i);
				if(unit instanceof AstDeclarationStatement)
					global.collect_constants(unit);
			}
			global.collect_reference(astroot.get_scope());
		}
	}
	/**
	 * close the generator 
	 */
	protected void close() {
		global.initialize();
		locals.initialize();
		source.initialize();
		astroot = null;
	}
	
	/* generate methods */
	/**
	 * initialize the local information for function definition
	 * @param def
	 * @throws Exception
	 */
	protected void init(AstFunctionDefinition def) throws Exception {
		/* initialization */
		locals.initialize();
		source.initialize();
		
		/* collect local constants */
		locals.collect_constants(def.get_body());
		
		/* collect local declarations in all scopes */
		Queue<CScope> queue = new LinkedList<CScope>();
		for(queue.add(def.get_scope()); !queue.isEmpty();) {
			CScope scope = queue.poll();
			locals.collect_reference(scope);
			Iterator<CScope> children = scope.get_children();
			while(children.hasNext()) queue.add((children.next()));
		}
		
		/* collect sources */
		source.collect_all(def);
	}
	/**
	 * generate mutations from function-def source
	 * @param mutations
	 * @throws Exception
	 */
	protected void generate(Set<Mutation> mutations) throws Exception {
		/* statement */
		this.generate_STRP(mutations);
		this.generate_STRI(mutations);
		this.generate_STRC(mutations);
		this.generate_SSDL(mutations);
		this.generate_SCRB(mutations);
		this.generate_SBRC(mutations);
		this.generate_SWDD(mutations);
		this.generate_SDWD(mutations);
		this.generate_SMTC(mutations);
		this.generate_OCNG(mutations);
		
		/* unary */
		this.generate_OPPO(mutations);
		this.generate_OMMO(mutations);
		this.generate_UIOI(mutations);
		this.generate_OBNG(mutations);
		this.generate_OLNG(mutations);
		
		/* binary */
		this.generate_OAXN(mutations);
		this.generate_OBXN(mutations);
		this.generate_OLXN(mutations);
		this.generate_ORXN(mutations);
		this.generate_OSXN(mutations);
		
		/* value */
		this.generate_VABS(mutations);
		this.generate_VBCR(mutations);
		this.generate_VDTR(mutations);
		this.generate_VTWD(mutations);
		
		/* reference */
		this.generate_VARR(mutations);
		this.generate_VPRR(mutations);
		this.generate_VSRR(mutations);
		this.generate_VSFR(mutations);
		this.generate_VTRR(mutations);
		
		this.generate_Ccsr(mutations);
	}
	/**
	 * generate mutations for each function
	 * @param def
	 * @param mutation
	 * @throws Exception
	 */
	protected void generate(AstFunctionDefinition def, Set<Mutation> mutations) throws Exception {
		this.init(def);
		this.generate(mutations);
	}
	
	protected void generate_STRP(Set<Mutation> mutations) throws Exception {
		for(AstStatement stmt : source.STRPs) {
			this.add(producer.gen_STRP(stmt), mutations);
		}
	}
	protected void generate_STRC(Set<Mutation> mutations) throws Exception {
		for(AstExpression expr : source.STRCs)
			this.add(producer.gen_STRC(expr), mutations);
	}
	protected void generate_STRI(Set<Mutation> mutations) throws Exception {
		for(AstStatement stmt : source.STRIs) {
			this.add(producer.gen_STRI(stmt), mutations);
		}
	}
	protected void generate_SSDL(Set<Mutation> mutations) throws Exception {
		for(AstStatement stmt : source.SSDLs) {
			this.add(producer.gen_SSDL(stmt), mutations);
		}
	}
	protected void generate_SBRC(Set<Mutation> mutations) throws Exception {
		for(AstBreakStatement stmt : source.SBRCs) {
			this.add(producer.gen_SBRC(stmt), mutations);
		}
	}
	protected void generate_SCRB(Set<Mutation> mutations) throws Exception {
		for(AstContinueStatement stmt : source.SCRBs) {
			this.add(producer.gen_SCRB(stmt), mutations);
		}
	}
	protected void generate_SWDD(Set<Mutation> mutations) throws Exception {
		for(AstWhileStatement stmt : source.SWDDs) {
			this.add(producer.gen_SWDD(stmt), mutations);
		}
	}
	protected void generate_SDWD(Set<Mutation> mutations) throws Exception {
		for(AstDoWhileStatement stmt : source.SDWDs) {
			this.add(producer.gen_SDWD(stmt), mutations);
		}
	}
	protected void generate_SSWM(Set<Mutation> mutations) throws Exception {
		for(AstSwitchStatement stmt : source.SSWMs) {
			this.add(producer.gen_SSWM(stmt), mutations);
		}
	}
	static final int[] times = new int[]{2, 3, 5, 7, 13, 23, 61, 127, 1053};
	protected void generate_SMTC(Set<Mutation> mutations) throws Exception {
		for(AstStatement stmt : source.SMTCs) {
			for(int i = 0;i < times.length;i++) {
				this.add(producer.gen_SMTC(stmt, times[i]), mutations);
			}
		}
	}
	protected void generate_OCNG(Set<Mutation> mutations) throws Exception {
		for(AstStatement stmt : source.OCNGs) {
			this.add(producer.gen_OCNG(stmt), mutations);
		}
	}
	protected void generate_ONDU(Set<Mutation> mutations) throws Exception {
		for(AstUnaryExpression expr : source.ONDUs) {
			this.add(producer.gen_ONDU(expr), mutations);
		}
	}
	
	protected void generate_OPPO(Set<Mutation> mutations) throws Exception {
		for(AstExpression expr : source.OPPOs) {
			if(expr instanceof AstIncrePostfixExpression)
				this.add(producer.gen_OPPO((AstIncrePostfixExpression)expr), mutations);
			else this.add(producer.gen_OPPO((AstIncreUnaryExpression) expr), mutations);
		}
	}
	protected void generate_OMMO(Set<Mutation> mutations) throws Exception {
		for(AstExpression expr : source.OMMOs) {
			if(expr instanceof AstIncrePostfixExpression)
				this.add(producer.gen_OMMO((AstIncrePostfixExpression)expr), mutations);
			else this.add(producer.gen_OMMO((AstIncreUnaryExpression) expr), mutations);
		}
	}
	protected void generate_UIOI(Set<Mutation> mutations) throws Exception {
		for(AstExpression expr : source.UIOIs) {
			this.add(producer.gen_UIOI(expr), mutations);
		}
	}
	protected void generate_OBNG(Set<Mutation> mutations) throws Exception {
		for(AstBitwiseBinaryExpression expr : source.OBNGs) {
			this.add(producer.gen_OBNG(expr), mutations);
		}
	}
	protected void generate_OLNG(Set<Mutation> mutations) throws Exception {
		for(AstExpression expr : source.OLNGs) {
			this.add(producer.gen_OLNG(expr), mutations);
		}
	}
	
	protected void generate_OAXN(Set<Mutation> mutations) throws Exception {
		for(AstArithBinaryExpression expr : source.OAXNs) {
			this.add(producer.gen_OAAN(expr), mutations);
			this.add(producer.gen_OABN(expr), mutations); 
			this.add(producer.gen_OALN(expr), mutations);
			this.add(producer.gen_OARN(expr), mutations);
			this.add(producer.gen_OASN(expr), mutations);
		}
	}
	protected void generate_OBXN(Set<Mutation> mutations) throws Exception {
		for(AstBitwiseBinaryExpression expr : source.OBXNs) {
			this.add(producer.gen_OBAN(expr), mutations);
			this.add(producer.gen_OBBN(expr), mutations);
			this.add(producer.gen_OBLN(expr), mutations);
			this.add(producer.gen_OBRN(expr), mutations);
			this.add(producer.gen_OBSN(expr), mutations);
		}
	}
	protected void generate_OLXN(Set<Mutation> mutations) throws Exception {
		for(AstLogicBinaryExpression expr : source.OLXNs) {
			this.add(producer.gen_OLAN(expr), mutations);
			this.add(producer.gen_OLBN(expr), mutations);
			this.add(producer.gen_OLLN(expr), mutations);
			this.add(producer.gen_OLRN(expr), mutations);
			this.add(producer.gen_OLSN(expr), mutations);
		}
	}
	protected void generate_ORXN(Set<Mutation> mutations) throws Exception {
		for(AstRelationExpression expr : source.ORXNs) {
			this.add(producer.gen_ORAN(expr), mutations);
			this.add(producer.gen_ORBN(expr), mutations);
			this.add(producer.gen_ORLN(expr), mutations);
			this.add(producer.gen_ORRN(expr), mutations);
			this.add(producer.gen_ORSN(expr), mutations);
		}
	}
	protected void generate_OSXN(Set<Mutation> mutations) throws Exception {
		for(AstShiftBinaryExpression expr : source.OSXNs) {
			this.add(producer.gen_OSAN(expr), mutations);
			this.add(producer.gen_OSBN(expr), mutations);
			this.add(producer.gen_OSLN(expr), mutations);
			this.add(producer.gen_OSRN(expr), mutations);
			this.add(producer.gen_OSSN(expr), mutations);
		}
	}
	protected void generate_OEXA(Set<Mutation> mutations) throws Exception {
		for(AstAssignExpression expr : source.OEXAs) {
			this.add(producer.gen_OEAA(expr), mutations);
			this.add(producer.gen_OEBA(expr), mutations);
			this.add(producer.gen_OESA(expr), mutations);
		}
	}
	protected void generate_OAXA(Set<Mutation> mutations) throws Exception {
		for(AstArithAssignExpression expr : source.OAXAs) {
			this.add(producer.gen_OAAA(expr), mutations);
			this.add(producer.gen_OABA(expr), mutations);
			this.add(producer.gen_OASA(expr), mutations);
		}
	}
	protected void generate_OBXA(Set<Mutation> mutations) throws Exception {
		for(AstBitwiseAssignExpression expr : source.OBXAs) {
			this.add(producer.gen_OBAA(expr), mutations);
			this.add(producer.gen_OBBA(expr), mutations);
			this.add(producer.gen_OBSA(expr), mutations);
		}
	}
	protected void generate_OSXA(Set<Mutation> mutations) throws Exception {
		for(AstShiftAssignExpression expr : source.OSXAs) {
			this.add(producer.gen_OSAA(expr), mutations);
			this.add(producer.gen_OSBA(expr), mutations);
			this.add(producer.gen_OSSA(expr), mutations);
		}
	}
	
	protected void generate_VABS(Set<Mutation> mutations) throws Exception {
		for(AstExpression expr : source.VABSs) {
			this.add(producer.gen_VABS(expr), mutations);
		}
	}
	protected void generate_VBCR(Set<Mutation> mutations) throws Exception {
		for(AstExpression expr : source.VBCRs) {
			try{
				this.add(producer.gen_VBCR(expr), mutations);
			}catch(Exception ex){
				System.err.println(expr.get_value_type());
				throw ex;
			}
			//this.add(producer.gen_VBCR(expr), mutations);
		}
	}
	protected void generate_VTWD(Set<Mutation> mutations) throws Exception {
		for(AstExpression expr : source.VTWDs) {
			this.add(producer.gen_VTWD(expr), mutations);
		}
	}
	protected void generate_VDTR(Set<Mutation> mutations) throws Exception {
		for(AstExpression expr : source.VDTRs) {
			this.add(producer.gen_VDTR(expr), mutations);
		}
	}
	
	protected void generate_VARR(Set<Mutation> mutations) throws Exception {
		for(AstExpression expr : source.VARRs) {
			for(AstName replace : locals.array_names) {
				try{
					this.add(producer.gen_VARR(expr, replace), mutations);
				}catch(Exception ex){continue;}
			}
			for(AstName replace : global.array_names) {
				try{
					this.add(producer.gen_VARR(expr, replace), mutations);
				}catch(Exception ex){continue;}
			}
		}
	}
	protected void generate_VPRR(Set<Mutation> mutations) throws Exception {
		for(AstExpression expr : source.VPRRs) {
			for(AstName replace : locals.point_names) {
				try{
					this.add(producer.gen_VPRR(expr, replace), mutations);
				}catch(Exception ex){continue;}
			}
			for(AstName replace : global.point_names) {
				try{
					this.add(producer.gen_VPRR(expr, replace), mutations);
				}catch(Exception ex){continue;}
			}
		}
	}
	protected void generate_VTRR(Set<Mutation> mutations) throws Exception {
		for(AstExpression expr : source.VTRRs) {
			for(AstName replace : locals.strut_names) {
				try{
					this.add(producer.gen_VTRR(expr, replace), mutations);
				}catch(Exception ex){continue;}
			}
			for(AstName replace : global.strut_names) {
				try{
					this.add(producer.gen_VTRR(expr, replace), mutations);
				}catch(Exception ex){continue;}
			}
		}
	}
	protected void generate_VSRR(Set<Mutation> mutations) throws Exception {
		for(AstExpression expr : source.VSRRs) {
			CType type = expr.get_value_type();
			type = JC_Classifier.get_value_type(type);
			
			if(JC_Classifier.is_character_type(type)) {
				for(AstName replace : locals.chars_names) {
					try{
						this.add(producer.gen_VSRR(expr, replace), mutations);
					}catch(Exception ex){continue;}
				}
				for(AstName replace : global.chars_names) {
					try{
						this.add(producer.gen_VSRR(expr, replace), mutations);
					}catch(Exception ex){continue;}
				}
			}
			else if(JC_Classifier.is_integer_type(type) && !(type instanceof CEnumType)) {
				for(AstName replace : locals.intes_names) {
					try{
						this.add(producer.gen_VSRR(expr, replace), mutations);
					}catch(Exception ex){continue;}
				}
				for(AstName replace : global.intes_names) {
					try{
						this.add(producer.gen_VSRR(expr, replace), mutations);
					}catch(Exception ex){continue;}
				}
			}
			else if(JC_Classifier.is_real_type(type)) {
				for(AstName replace : locals.reals_names) {
					try{
						this.add(producer.gen_VSRR(expr, replace), mutations);
					}catch(Exception ex){continue;}
				}
				for(AstName replace : global.reals_names) {
					try{
						this.add(producer.gen_VSRR(expr, replace), mutations);
					}catch(Exception ex){continue;}
				}
			}
		}
	}
	protected void generate_VSFR(Set<Mutation> mutations) throws Exception {
		for(AstFieldExpression expr : source.VSFRs) {
			this.add(producer.gen_VFRR(expr), mutations);
		}
	}
	
	protected void generate_Ccsr(Set<Mutation> mutations) throws Exception {
		for(AstConstant constant : source.Ccsrs) {
			for(AstConstant replace : locals.constants.values()) {
				try{
					this.add(producer.gen_CCCR(constant, replace), mutations);
				}catch(Exception ex){continue;}
			}
			for(AstConstant replace : global.constants.values()) {
				try{
					this.add(producer.gen_CCCR(constant, replace), mutations);
				}catch(Exception ex){continue;}
			}
			
			CType type = constant.get_value_type();
			type = JC_Classifier.get_value_type(type);
			if(JC_Classifier.is_character_type(type)) {
				for(AstName replace : locals.chars_names) {
					try{
						this.add(producer.gen_CCSR(constant, replace), mutations);
					}catch(Exception ex){continue;}
				}
				for(AstName replace : global.chars_names) {
					try{
						this.add(producer.gen_CCSR(constant, replace), mutations);
					}catch(Exception ex){continue;}
				}
			}
			else if(JC_Classifier.is_integer_type(type)) {
				this.add(producer.gen_CRCR(constant), mutations);
				for(AstName replace : locals.intes_names) {
					try{
						this.add(producer.gen_CCSR(constant, replace), mutations);
					}catch(Exception ex){continue;}
				}
				for(AstName replace : global.intes_names) {
					try{
						this.add(producer.gen_CCSR(constant, replace), mutations);
					}catch(Exception ex){continue;}
				}
			}
			else if(JC_Classifier.is_real_type(type)) {
				for(AstName replace : locals.reals_names) {
					try{
						this.add(producer.gen_CCSR(constant, replace), mutations);
					}catch(Exception ex){continue;}
				}
				for(AstName replace : global.reals_names) {
					try{
						this.add(producer.gen_CCSR(constant, replace), mutations);
					}catch(Exception ex){continue;}
				}
			}
		}
	}
	
	/* basic method */
	private void add(Mutation mutation, Set<Mutation> mutations) {
		if(mutation != null) mutations.add(mutation);
	}
	private void add(Mutation[] mutation, Set<Mutation> mutations) {
		if(mutation != null) {
			for(int i = 0;i < mutation.length;i++)
				if(mutation[i] != null)
					mutations.add(mutation[i]);
		}
	}
	
	
	
}
