"""This file implements first-order frequent context patterns."""


import os
from collections import deque
import com.jcsa.z3proof.libs.muta 	as jcmuta
import com.jcsa.z3proof.z3prove		as jcprov


## data model definition


class ContextStatePattern:
	"""
	Structural pattern to describe the set of ContextState(s).
	"""

	def __init__(self, project: jcmuta.CProject, states, mutations):
		"""
		:param project: mutation testing project where the pattern is defined
		:param states: 	the set of ContextState being enclosed in the pattern
		:param mutations: the set of mutations which the pattern matches with
		"""
		self.project = project
		self.__set_states__(states)
		self.__set_mutations__(mutations)
		return

	def __set_states__(self, states):
		"""
		:param states: the set of ContextState being enclosed in the pattern
		:return:
		"""
		self.features = list()
		self.states = set()
		if not (states is None):
			for state in states:
				if isinstance(state, jcmuta.ContextState):
					state: jcmuta.ContextState
					self.states.add(state)
		for state in self.states:
			self.features.append(state.get_index())
		self.features.sort()
		return

	def __match_with__(self, mutation: jcmuta.ContextMutation):
		"""
		:param mutation: ContextMutation
		:return: whether the mutation matches with the pattern
		"""
		for state in self.states:
			if not (state in mutation.get_states()):
				return False
		return True

	def __set_mutations__(self, mutations):
		"""
		:param mutations: the set of mutations which the pattern matches with
		:return:
		"""
		if mutations is None:
			mutations = self.project.context_space.get_mutations()
		self.mutations = set()
		for mutation in mutations:
			if isinstance(mutation, jcmuta.ContextMutation):
				if self.__match_with__(mutation):
					self.mutations.add(mutation)
		return

	def get_project(self):
		"""
		:return: mutation testing project where the pattern is defined
		"""
		return self.project

	def get_features(self):
		"""
		:return: unique feature vector to define this pattern
		"""
		return self.features

	def get_states(self):
		"""
		:return: the set of ContextState(s) to define this pattern
		"""
		return self.states

	def get_mutations(self):
		"""
		:return: the set of ContextMutation(s) being matched with this pattern
		"""
		return self.mutations

	def __len__(self):
		"""
		:return: the number of states
		"""
		return len(self.features)

	def __str__(self):
		return str(self.features)

	def classify(self, tests, threshold=0.50):
		"""
		:param tests:
		:param threshold: the degree to which the pattern is seen killing-or-alive pattern
		:return: result, kill_set, alive_set
					(1) result: whether the most mutations in this pattern are killed
					(2) kill_set: the set of ContextMutation(s) being killed by tests
					(3) alive_set: the set of ContextMutation(s) not killed by tests
		"""
		kill_set, alive_set = set(), set()
		for mutation in self.mutations:
			if mutation.get_mutant().get_result().is_killed_in(tests):
				kill_set.add(mutation)
			else:
				alive_set.add(mutation)
		score = 0.0
		if len(kill_set) > 0:
			score = len(kill_set) / (len(kill_set) + len(alive_set) + 0.0)
		if score >= threshold:
			result = True
		else:
			result = False
		return result, kill_set, alive_set

	def evaluate(self, tests):
		"""
		:param tests: the set of tests for killing mutations within
		:return: 	length, support, confidence
					(1) length: the length of this pattern
					(2) support: the number of undetected mutations
					(3) confidence: support / mutations
		"""
		_, kill_set, alive_set = self.classify(tests)
		length = len(self.features)
		support = len(alive_set)
		confidence = 0.0
		if support > 0:
			confidence = support / (len(kill_set) + len(alive_set) + 0.0)
		return length, support, confidence


class ContextStatePatternNode:
	"""
	Structural tree node of ContextStatePattern defined
	"""

	def __init__(self, tree, parent, states, mutations):
		"""
		:param tree: the tree where this node is defined
		:param parent: the parent node or None if it is a root
		:param states: the set of ContextState(s) enclosed in
		:param mutations: the set of ContextMutation(s) to be matched
		"""
		tree: ContextStatePatternTree
		self.tree = tree
		self.parent = parent
		self.pattern = ContextStatePattern(tree.get_project(), states, mutations)
		self.children = list()
		return

	def get_tree(self):
		"""
		:return: the tree where this node is defined
		"""
		return self.tree

	def is_root(self):
		"""
		:return: whether the node is root
		"""
		if self.parent is None:
			return True
		return False

	def get_parent(self):
		if self.parent is None:
			return None
		else:
			self.parent: ContextStatePatternNode
			return self.parent

	def get_pattern(self):
		"""
		:return: the pattern of the node
		"""
		return self.pattern

	def get_features(self):
		"""
		:return: the feature vector of this pattern
		"""
		return self.pattern.get_features()

	def get_states(self):
		"""
		:return: the set of ContextState(s) being enclosed
		"""
		return self.pattern.get_states()

	def get_mutations(self):
		"""
		:return: the set of ContextMutation(s) matched with the pattern of the node
		"""
		return self.pattern.get_mutations()

	def is_leaf(self):
		"""
		:return: whether the node is a leaf
		"""
		return len(self.children) == 0

	def get_children(self):
		"""
		:return: child patterns
		"""
		return self.children

	def number_of_children(self):
		"""
		:return: the number of children in this pattern
		"""
		return len(self.children)

	def get_child(self, k: int):
		"""
		:param k:
		:return: the kth child of this pattern node
		"""
		child = self.children[k]
		child: ContextStatePatternNode
		return child

	def __new_child__(self, state: jcmuta.ContextState):
		"""
		:param state:
		:return: 	It creates a child node or this node only by:
					1) return self is state is defined in this node or its parents;
					2) return the existing child of this node if state is enclosed;
					3) creates a new child node using the input state in this node.
		"""
		if state in self.pattern.get_states():
			return self
		for child in self.children:
			child: ContextStatePatternNode
			if state in child.get_states():
				return child
		new_states = set()
		new_states.add(state)
		for self_state in self.get_states():
			new_states.add(self_state)
		new_child = ContextStatePatternNode(self.tree, self, new_states, self.get_mutations())
		self.children.append(new_child)
		return new_child


class ContextStatePatternTree:
	"""
	Structural tree to define and create ContextStatePattern
	"""

	def __init__(self, project: jcmuta.CProject):
		"""
		:param project: mutation testing project
		"""
		self.project = project
		self.root = ContextStatePatternNode(self, None, None, None)
		return

	def get_project(self):
		"""
		:return: mutation testing project
		"""
		return self.project

	def get_root(self):
		return self.root

	def get_node(self, states):
		"""
		:param states:
		:return:
		"""
		features = list()
		for state in states:
			state: jcmuta.ContextState
			if state.get_index() in features:
				pass
			else:
				features.append(state.get_index())
		features.sort()
		node = self.root
		for feature in features:
			state = self.project.state_space.get_state_at(feature)
			node = node.__new_child__(state)
		return node

	def get_patterns(self):
		"""
		:return: the set of ContextStatePattern(s) defined in this tree.
		"""
		patterns = set()
		queue = deque()
		queue.append(self.root)
		while len(queue) > 0:
			node = queue.popleft()
			node: ContextStatePatternNode
			for child in node.get_children():
				queue.append(child)
			patterns.add(node.get_pattern())
		return patterns


## initialization


def get_file_names_in(directory: str):
	file_names = list()
	for file_name in os.listdir(directory):
		file_names.append(file_name)
	file_names.sort()
	return file_names


def load_TEQ_results(project: jcmuta.CProject, tce_directory: str):
	"""
	:param project:
	:param tce_directory:
	:return: the set of integer IDs of mutants detected by TCE
	"""
	file_name = project.program.name
	file_path = os.path.join(tce_directory, file_name + ".txt")
	tce_ids = set()
	with open(file_path, 'r') as reader:
		for line in reader:
			if len(line.strip()) > 0:
				items = line.strip().split('\t')
				mid = items[0].strip()
				if mid.isdigit():
					tce_ids.add(int(mid))
					mutant = project.muta_space.get_mutant(int(mid))
					mutant.get_result().result = ""
	return tce_ids


def is_state_available(state: jcmuta.ContextState):
	"""
	:param state:
	:return: whether to select this state for mining
	"""
	return (state.get_category() == "cov_stmt") or (state.get_category() == "eva_cond") or \
		   (state.get_category() == "set_expr") or (state.get_category() == "inc_expr") or \
		   (state.get_category() == "set_stmt")


def prove_exp_equivalence(project: jcmuta.CProject):
	"""
	:param project:
	:return: the set of expression-level equivalences
	"""
	prover = jcprov.SymbolToZ3Prover()
	exp_ids = set()
	results = prover.classify(project)
	for mutant, result in results.items():
		exp_ids.add(mutant.get_muta_id())
		mutant.get_result().result = ""
	return exp_ids


## mining algorithms


def mine_1st_patterns(project: jcmuta.CProject, tests, min_support: int, min_confidence: float):
	"""
	:param project:
	:param tests:
	:param min_support:
	:param min_confidence:
	:return: the set of ContextStatePattern(s) that match with the input criterion
	"""
	## 1. collect the states of alive mutations
	states = set()
	for mutation in project.context_space.get_mutations():
		if mutation.get_mutant().get_result().is_killed_in(tests):
			pass
		else:
			for state in mutation.get_states():
				if is_state_available(state):
					states.add(state)

	## 2. mine the patterns (first-order) by the states
	tree = ContextStatePatternTree(project)
	counter, total, steps = 0, len(states), 2000
	print("\tCollect {} states from alive mutations for mining.".format(total))
	for state in states:
		pattern = tree.get_node([state]).get_pattern()
		if counter % steps == 0:
			length, support, confidence = pattern.evaluate(tests)
			confidence = int(confidence * 10000) / 100.0
			print("\t\tPattern[{}/{}]\tsupport = {}\tconfidence = {}%".format(counter, total, support, confidence))
		counter += 1

	## 3. collect the good patterns by the input criteria
	outputs = set()
	for pattern in tree.get_patterns():
		length, support, confidence = pattern.evaluate(tests)
		if (support >= min_support) and (confidence >= min_confidence):
			outputs.add(pattern)
	print("\tCollect {} good patterns by input parameters.".format(len(outputs)))
	return outputs


def find_good_pattern_in(patterns, mutation: jcmuta.ContextMutation, tests):
	"""
	:param patterns:
	:param mutation:
	:param tests:
	:return:
	"""
	if not (patterns is None):
		best_pattern, max_support = None, 0
		for pattern in patterns:
			pattern: ContextStatePattern
			if len(pattern.get_states()) > 0:
				if mutation in pattern.get_mutations():
					if best_pattern is None:
						best_pattern = pattern
						length, support, confidence = pattern.evaluate(tests)
						max_support = support
					else:
						length, support, confidence = pattern.evaluate(tests)
						if support > max_support:
							best_pattern = pattern
							max_support = support
		return best_pattern
	else:
		return None


def classify_mutation_patterns(project: jcmuta.CProject, patterns, tests):
	"""
	:param project:
	:param patterns:
	:param tests:
	:return: alive_mutation --> ContextStatePattern
	"""
	results = dict()
	for mutation in project.context_space.get_mutations():
		good_pattern = find_good_pattern_in(patterns, mutation, tests)
		results[mutation.get_mutant()] = good_pattern
	return results


def write_mutation_pattern_maps(project: jcmuta.CProject, tests, patterns, tce_ids, exp_ids, file_path: str):
	"""
	:param project:
	:param tests:
	:param patterns:
	:param tce_ids:	the set of TCE detected equivalent mutations
	:param exp_ids:
	:param file_path:
	:return: MID KILL CLAS OPRT LINE CODE PARM TCE EXP PID SUPP CONF(%) CATE LOCT LORD RORD
	"""
	with open(file_path, 'w') as writer:
		writer.write("MID\tKILL\tCLAS\tOPRT\tLINE\tCODE\tPARM\tTCE\tEXP\tPID\tSUPP\tCONF(%)\tCATE\tLOCT\tLORD\tRORD\n")
		for mutation in project.context_space.get_mutations():
			mutant = mutation.get_mutant()
			mid = mutant.get_muta_id()
			res = mutant.get_result().is_killed_in(tests)
			mu_class = mutant.get_mutation().get_mutation_class()
			mu_operator = mutant.get_mutation().get_mutation_operator()
			location = mutant.get_mutation().get_location()
			line = location.line_of(False)
			code = location.generate_code(96)
			param = str(mutant.get_mutation().get_parameter())
			writer.write("{}\t{}\t{}\t{}\t{}\t\"{}\"\t{}\t{}\t{}".
						 format(mid, res, mu_class, mu_operator, line, code, param, mid in tce_ids, mid in exp_ids))
			pattern = find_good_pattern_in(patterns, mutation, tests)
			if not (pattern is None):
				length, support, confidence = pattern.evaluate(tests)
				confidence = int(confidence * 10000) / 100.0
				writer.write("\t{}\t{}\t{}".format(str(pattern), support, confidence))
				for state in pattern.get_states():
					category = state.get_category()
					location = state.get_location().get_node_type()
					loperand = state.get_loperand().get_code()
					roperand = state.get_roperand().get_code()
					writer.write("\t{}\t{}\t{}\t{}".format(category, location, loperand, roperand))
					break
			writer.write("\n")
		writer.write("\n")
	return


def write_pattern_evaluate_maps(project: jcmuta.CProject, tests, patterns, file_path: str):
	"""
	:param project:
	:param tests:
	:param patterns:
	:param file_path:
	:return:
	"""
	with open(file_path, 'w') as writer:
		mutant_pattern_dict = classify_mutation_patterns(project, patterns, tests)
		alive_mutants, cover_mutants, class_patterns = set(), set(), set()
		for mutant, pattern in mutant_pattern_dict.items():
			if not mutant.get_result().is_killed_in(tests):
				alive_mutants.add(mutant)
			if not (pattern is None):
				class_patterns.add(pattern)
				for cov_mutant in pattern.get_mutations():
					cover_mutants.add(cov_mutant.get_mutant())
		average = 0.0
		if len(class_patterns) > 0:
			average = len(cover_mutants) / (len(class_patterns) + 0.0)
		writer.write("ALV = {}\tCOV = {}\tPAT = {}\tAVG = {}\n".
					 format(len(alive_mutants), len(cover_mutants), len(class_patterns), average))
		common_mutants = alive_mutants & cover_mutants
		precision = len(common_mutants) / (len(cover_mutants) + 0.001)
		recall = len(common_mutants) / (len(alive_mutants) + 0.001)
		f1_score = 2 * precision * recall / (precision + recall + 0.001)
		precision = int(precision * 10000) / 100.0
		recall = int(recall * 10000) / 100.0
		writer.write("PREC = {}%\tRECL = {}%\tSCORE = {}\n".format(precision, recall, f1_score))
	return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zexp/features"
	post_path = "/home/dzt2/Development/Data/zexp/patterns"
	tces_path = "/home/dzt2/Development/Data/zexp/TCE"
	index, file_names = 0, get_file_names_in(root_path)
	for project_name in file_names:
		index += 1
		print("{}.\tTesting on project {}.".format(index, project_name))
		if project_name != "md4":
			project_directory = os.path.join(root_path, project_name)
			c_project = jcmuta.CProject(project_directory, project_name)
			tce_set = load_TEQ_results(c_project, tces_path)
			exp_set = prove_exp_equivalence(c_project)
			c_patterns = mine_1st_patterns(c_project, None, 1, 0.65)
			write_mutation_pattern_maps(c_project, None, c_patterns, tce_set, exp_set,
										os.path.join(post_path, project_name + ".mpt"))
			write_pattern_evaluate_maps(c_project, None, c_patterns, os.path.join(post_path, project_name + ".sum"))
		print()
	print("Testing end for all...")

