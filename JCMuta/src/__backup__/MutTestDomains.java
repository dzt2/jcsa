package __backup__;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.jcsa.jcparse.lang.base.BitSequence;

public class MutTestDomains {
	
	protected static final Random random_instance = new Random(System.currentTimeMillis());
	
	/* definitions and constructor */
	protected MutScoreClusters cluster_set;
	protected Collection<MutTestDomain> all_domains;
	protected Collection<MutTestDomain> min_domains;
	private MutTestDomains(MutScoreClusters cluster_set) throws Exception {
		if(cluster_set == null || cluster_set.isEmpty())
			throw new IllegalArgumentException("invalid cluster_set as null");
		else {
			this.cluster_set = cluster_set;
			this.all_domains = new HashSet<MutTestDomain>();
			this.min_domains = new HashSet<MutTestDomain>();
		}
	}
	/**
	 * whether the domain source subsumes target such that target is redundant.
	 * @param source
	 * @param target
	 * @return
	 * @throws Exception
	 */
	private boolean subsume(MutTestDomain source, MutTestDomain target) throws Exception {
		BitSequence x = source.get_test_domain().get_score_set();
		BitSequence y = target.get_test_domain().get_score_set();
		BitSequence z = x.and(y);
		return z.equals(x);
	}
	/**
	 * generate the minimal testing domains by reducing the requirements being met
	 * @param domains
	 * @throws Exception
	 */
	private void gen_min_domains() throws Exception {
		this.min_domains.clear(); min_domains.addAll(all_domains);
		Set<MutTestDomain> removes = new HashSet<MutTestDomain>();
		Set<MutTestDomain> vis_set = new HashSet<MutTestDomain>();
		
		boolean update = true;
		while(update) {
			update = false;
			
			// 1. find the first not-used domain yet
			MutTestDomain source = null;
			for(MutTestDomain domain : min_domains) {
				if(!vis_set.contains(domain)) {
					vis_set.add(domain); 
					source = domain; break;
				}
			}
			
			// 2. determine the domains need to be removed
			removes.clear();
			if(source != null) {
				for(MutTestDomain target : min_domains) {
					if(target != source) {
						if(this.subsume(source, target)) {
							removes.add(target);
						}
					}
				}
			}
			
			// 3. remove the redundant domains and update
			min_domains.removeAll(removes);
			update = !removes.isEmpty();
		}
	}
	
	/* factory methods */
	private static boolean is_statement_requirement(Mutant mutant) throws Exception {
		MutOperator operator = mutant.get_mutation().get_operator();
		return operator == MutOperator.STRP;
	}
	private static boolean is_branching_requirement(Mutant mutant) throws Exception {
		MutOperator operator = mutant.get_mutation().get_operator();
		return operator == MutOperator.STRI;
	}
	private static boolean is_condition_requirement(Mutant mutant) throws Exception {
		MutOperator operator = mutant.get_mutation().get_operator();
		return operator == MutOperator.STRI || operator == MutOperator.STRC;
	}
	/**
	 * get the testing domains for statement coverage testing
	 * @param cluster_set
	 * @return
	 * @throws Exception
	 */
	public static MutTestDomains get_statement_domains(MutScoreClusters cluster_set) throws Exception {
		MutTestDomains domains = new MutTestDomains(cluster_set);
		
		Iterable<MutScoreCluster> clusters = cluster_set.get_clusters();
		for(MutScoreCluster cluster : clusters) {
			if(cluster.get_score_key().get_score_degree() > 0) {
				Iterable<Mutant> mutants = cluster.get_mutants();
				for(Mutant mutant : mutants) {
					if(is_statement_requirement(mutant)) {
						domains.all_domains.add(new MutTestDomain(domains, cluster));
						break;
					}
				}
			}
		}
		
		domains.gen_min_domains(); return domains;
	}
	/**
	 * get the testing domains for branch coverage testing
	 * @param cluster_set
	 * @return
	 * @throws Exception
	 */
	public static MutTestDomains get_branching_domains(MutScoreClusters cluster_set) throws Exception {
		MutTestDomains domains = new MutTestDomains(cluster_set);
		
		Iterable<MutScoreCluster> clusters = cluster_set.get_clusters();
		for(MutScoreCluster cluster : clusters) {
			if(cluster.get_score_key().get_score_degree() > 0) {
				Iterable<Mutant> mutants = cluster.get_mutants();
				for(Mutant mutant : mutants) {
					if(is_branching_requirement(mutant)) {
						domains.all_domains.add(new MutTestDomain(domains, cluster));
						break;
					}
				}
			}
		}
		
		domains.gen_min_domains(); return domains;
	}
	/**
	 * get the testing domains for condition coverage testing
	 * @param cluster_set
	 * @return
	 * @throws Exception
	 */
	public static MutTestDomains get_condition_domains(MutScoreClusters cluster_set) throws Exception {
		MutTestDomains domains = new MutTestDomains(cluster_set);
		
		Iterable<MutScoreCluster> clusters = cluster_set.get_clusters();
		for(MutScoreCluster cluster : clusters) {
			if(cluster.get_score_key().get_score_degree() > 0) {
				Iterable<Mutant> mutants = cluster.get_mutants();
				for(Mutant mutant : mutants) {
					if(is_condition_requirement(mutant)) {
						domains.all_domains.add(new MutTestDomain(domains, cluster));
						break;
					}
				}
			}
		}
		
		domains.gen_min_domains(); return domains;
	}
	/**
	 * get the testing domains that are hard to be killed by those based on original domains (included)
	 * @param original_domains
	 * @return
	 * @throws Exception
	 */
	public static MutTestDomains get_stubborn_domains(MutTestDomains original_domains, double threshold) throws Exception {
		MutTestDomains domains = new MutTestDomains(original_domains.cluster_set);
		
		Set<MutScoreCluster> clusters = new HashSet<MutScoreCluster>();
		for(MutTestDomain domain : original_domains.all_domains) {
			clusters.add(domain.get_cluster());
		}
		
		for(MutScoreCluster cluster : domains.cluster_set.get_clusters()) {
			double probability = original_domains.detection_probability(
					cluster.get_score_key(), original_domains.min_domains);
			if(probability <= threshold) clusters.add(cluster);
		}
		
		for(MutScoreCluster cluster : clusters) {
			if(cluster.get_score_key().get_score_degree() > 0)
				domains.all_domains.add(new MutTestDomain(domains, cluster));
		}
		
		domains.gen_min_domains(); return domains;
	}
	
	/* getters */
	/**
	 * get the number of all domains in set
	 * @return
	 */
	public int size() { return all_domains.size(); }
	/**
	 * size of minimal domains
	 * @return
	 */
	public int min_size() { return min_domains.size(); }
	/**
	 * get the clustering set on which the domains are constructed
	 * @return
	 */
	public MutScoreClusters get_cluster_set() { return this.cluster_set; }
	/**
	 * get all the testing domains in the set
	 * @return
	 */
	public Collection<MutTestDomain> get_all_domains() { return all_domains; }
	/**
	 * get the minimal essential testing domains in the set
	 * @return
	 */
	public Collection<MutTestDomain> get_min_domains() { return min_domains; }
	
	/* evaluator */
	/**
	 * determine the detection probability for the mutant with the killing set being killed
	 * when tests are generated by selecting one random test from each selected domains.
	 * 
	 * @param kill_set
	 * @param domains
	 * @return
	 * @throws Exception
	 */
	public double detection_probability(MutScore kill_set, Collection<MutTestDomain> domains) throws Exception {
		if(domains == null)
			throw new IllegalArgumentException("invalid domains: null");
		else {
			double production = 1.0, probability;
			for(MutTestDomain domain : domains) {
				probability = domain.detection_probability(kill_set);
				production = production * (1.0 - probability);
			}
			return 1.0 - production;
		}
	}
	/**
	 * determine the detection probability for the mutant with the killing set being killed
	 * when tests are generated by selecting one random test from each selected domains.
	 * @param mutant
	 * @param domains
	 * @return
	 * @throws Exception
	 */
	public double detection_probability(Mutant mutant, Collection<MutTestDomain> domains) throws Exception {
		if(domains == null)
			throw new IllegalArgumentException("invalid domains: null");
		else {
			double production = 1.0, probability;
			for(MutTestDomain domain : domains) {
				probability = domain.detection_probability(mutant);
				production = production * (1.0 - probability);
			}
			return 1.0 - production;
		}
	}
	
	/* tests selection */
	/**
	 * get a random domain from the set of domains
	 * @param domains
	 * @return
	 * @throws Exception
	 */
	private static MutTestDomain get_domain(Collection<MutTestDomain> domains) throws Exception {
		if(domains.isEmpty())
			throw new IllegalArgumentException("unable to select domains.");
		else {
			int k = MutTestDomains.random_instance.nextInt(domains.size());
			for(MutTestDomain domain : domains) {
				if(k-- == 0) return domain;
			}
			throw new IllegalArgumentException("invalid case");
		}
	}
	/**
	 * select random tests from random domains in provided set until all of them are satisfied.
	 * @param selected_domains
	 * @return
	 * @throws Exception
	 */
	public static Map<MutTestDomain, Integer> select_tests(Iterable<MutTestDomain> selected_domains) throws Exception {
		if(selected_domains == null)
			throw new IllegalArgumentException("invalid selected_domains");
		else {
			Map<MutTestDomain, Integer> solution = new HashMap<MutTestDomain, Integer>();
			
			Set<MutTestDomain> domains = new HashSet<MutTestDomain>();
			Set<MutTestDomain> removes = new HashSet<MutTestDomain>();
			for(MutTestDomain domain : selected_domains) domains.add(domain);
			
			while(!domains.isEmpty()) {
				MutTestDomain selected_domain = get_domain(domains);
				int selected_test = selected_domain.get_test();
				
				if(!solution.containsKey(selected_domain)) {
					solution.put(selected_domain, selected_test);
					
					removes.clear();
					for(MutTestDomain domain : domains) {
						if(domain.has_test_case(selected_test)) {
							removes.add(domain);
						}
					}
					
					domains.removeAll(removes);
				}
			}
			
			return solution;
		}
	}
	
}
