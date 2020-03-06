package __backup__;

import com.jcsa.jcparse.lang.base.BitSequence;

/**
 * The testing domain specifies a set of tests that can achieve the same identical requirement.
 * In mutation testing, such requirement refers to killing a particular set of mutants.
 * @author yukimula
 *
 */
public class MutTestDomain {
	
	/* definitions and constructor */
	private MutTestDomains domains;
	private MutScoreCluster cluster;
	private int size;
	protected MutTestDomain(MutTestDomains domains, MutScoreCluster cluster) throws Exception {
		if(cluster == null)
			throw new IllegalArgumentException("invalid cluster as null");
		else if(domains == null)
			throw new IllegalArgumentException("invalid domains as null");
		else if(cluster.get_score_key().get_score_degree() == 0)
			throw new IllegalArgumentException("invalid domain as empty");
		else { 
			this.domains = domains; this.cluster = cluster; 
			this.size = cluster.get_score_key().get_score_degree();
		}
	}
	
	/* getters */
	/**
	 * get the set of domains where this domain is created
	 * @return
	 */
	public MutTestDomains get_domains() { return this.domains; }
	/**
	 * get the cluster in which a set of mutants being satisfied are preserved
	 * @return
	 */
	public MutScoreCluster get_cluster() { return this.cluster; }
	/**
	 * get the set of test inputs in this domain
	 * @return
	 */
	public MutScore get_test_domain() { return cluster.get_score_key(); }
	/**
	 * whether there is a test case in the domain
	 * @param tid
	 * @return
	 */
	public boolean has_test_case(int tid) { 
		return cluster.get_score_key().is_killed_by(tid); 
	}
	/**
	 * get the number of tests in the domain
	 * @return
	 */
	public int size() { return this.size; }
	
	/* test generator */
	/**
	 * get the kth test in the domain
	 * @param k
	 * @return
	 */
	public int get_test(int k) {
		while(k < 0) k += this.size;
		while(k >= size) k -= size;
		
		BitSequence bits = this.cluster.
				get_score_key().get_score_set();
		int n = bits.length(); int tid;
		for(tid = 0; tid < n; tid++) {
			if(bits.get(tid)) {
				if(k-- == 0) break;
			}
		}
		return tid;
	}
	/**
	 * get a random test in the domain
	 * @return
	 */
	public int get_test() {
		int k = MutTestDomains.random_instance.nextInt(this.size);
		return this.get_test(k);
	}
	
	/* evaluator */
	/**
	 * compute the detection probability of mutation according to its killing set
	 * @param kill_set set of tests that kill the target mutant
	 * @return
	 * @throws Exception
	 */
	public double detection_probability(MutScore kill_set) throws Exception {
		if(kill_set == null)
			throw new IllegalArgumentException("invalid kill_set as null");
		else {
			BitSequence x = this.cluster.get_score_key().get_score_set();
			BitSequence y = kill_set.get_score_set();
			BitSequence z = x.and(y);
			return ((double) z.degree()) / ((double) this.size);
		}
	}
	/**
	 * compute the detection probability of specified mutant according to its killing
	 * set defined in the MutScoreClusters.
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	public double detection_probability(Mutant mutant) throws Exception {
		MutScoreCluster cluster = this.domains.cluster_set.get_cluster(mutant);
		BitSequence x = this.cluster.get_score_key().get_score_set();
		BitSequence y = cluster.get_score_key().get_score_set();
		BitSequence z = x.and(y);
		return ((double) z.degree()) / ((double) this.size);
	}
	
}
