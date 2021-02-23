"""
This file implements the feature reading + pattern estimation.
"""


import os
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.code as jccode
import com.jcsa.libs.muta as jcmuta


class RIPFeature:
	"""
	This model defines the feature required in mutant execution in terms of reachability, infection & propagation.
		(1)	category: "satisfaction" for constraint feature, or "observation" for state-error feature.
		(2) operator: name of the annotation type including eval_stmt, trap_stmt, mut_expr, mut_refer.
		(3) validate: True -- if the feature has been satisfied or validated during testing.
					  False -- if the feature has not been satisfied or validated in testing.
					  None -- if the satisfaction of the feature in testing remains unknown.
		(4) execution: the statement point where the feature is defined and evaluated in execution.
		(5) location: the C-intermediate representation subject that defines the feature in testing.
		(6) parameter: None or symbolic expression used to refine the definition of the feature.
	"""

	def __init__(self, category: str, operator: str, validate: bool, execution: jccode.CirExecution,
				 location: jccode.CirNode, parameter: jcbase.SymNode):
		"""
		:param category: "satisfaction" for constraint feature, or "observation" for state-error feature.
		:param operator: name of the annotation type including eval_stmt, trap_stmt, mut_expr, mut_refer.
		:param validate: True -- if the feature has been satisfied or validated during testing.
					  	 False -- if the feature has not been satisfied or validated in testing.
					  	 None -- if the satisfaction of the feature in testing remains unknown.
		:param execution: the statement point where the feature is defined and evaluated in execution.
		:param location: the C-intermediate representation subject that defines the feature in testing.
		:param parameter: None or symbolic expression used to refine the definition of the feature.
		"""
		self.category = category
		self.operator = operator
		self.validate = validate
		self.execution = execution
		self.location = location
		self.parameter = parameter
		return

	def get_category(self):
		"""
		:return: "satisfaction" for constraint feature, or "observation" for state-error feature.
		"""
		return self.category

	def get_operator(self):
		"""
		:return: name of the annotation type including eval_stmt, trap_stmt, mut_expr, mut_refer.
		"""
		return self.operator

	def get_validate(self):
		"""
		:return:	True -- if the feature has been satisfied or validated during testing.
					False -- if the feature has not been satisfied or validated in testing.
					None -- if the satisfaction of the feature in testing remains unknown.
		"""
		return self.validate

	def get_execution(self):
		"""
		:return: the statement point where the feature is defined and evaluated in execution.
		"""
		return self.execution

	def get_location(self):
		"""
		:return: the C-intermediate representation subject that defines the feature in testing.
		"""
		return self.location

	def get_parameter(self):
		"""
		:return: None or symbolic expression used to refine the definition of the feature.
		"""
		return self.parameter


class RIPExecution:
	"""
	It describes the execution between a mutant and a test case with annotated with a set of features required
	for killing the target mutant during testing.
		(1) document: the RIPDocument where the execution is created and defined
		(2) mutant: the mutation as target for being killed
		(3) test: the test case executed against the mutant in the execution or None if the execution is derived
			from static symbolic analysis
		(4) words: the set of words encoding the RIP-features required in testing
		(5) get_features(): the set of RIP-features required during this execution
	"""

	def __init__(self, document, mutant: jcmuta.Mutant, test: jcmuta.TestCase, words):
		"""
		:param document: RIPDocument where the execution is created and defined
		:param mutant: the mutation as target for being killed
		:param test: the test case executed against the mutant in the execution or None if the execution is derived
					 from static symbolic analysis
		:param words: the set of words encoding the RIP-features required in testing
		"""
		document: RIPDocument
		self.document = document
		self.mutant = mutant
		self.test = test
		self.words = list()
		for word in words:
			word: str
			self.words.append(word)
		return

	def get_document(self):
		"""
		:return: the RIPDocument where the execution is created and defined
		"""
		return self.document

	def get_mutant(self):
		"""
		:return: the mutation as target for being killed
		"""
		return self.mutant

	def get_test(self):
		"""
		:return: the test case executed against the mutant in the execution or None if the execution is derived
				 from static symbolic analysis
		"""
		return self.test

	def get_words(self):
		"""
		:return: the set of words encoding the RIP-features required in testing
		"""
		return self.words

	def get_features(self):
		"""
		:return: the set of RIP-features required during this execution
		"""
		features = list()
		for word in self.words:
			features.append(self.document.get_feature(word))
		return features


class RIPDocument:
	"""
	It preserves the executions among mutants and test cases with annotated with words that encode the
	RIP-features required in testing for killing the target mutant.
	"""

	def __init__(self, project: jcmuta.CProject):
		self.project = project		# it provides original samples for analysis and used
		self.exec_list = list()		# the set of RIP-executions between mutants and tests
		self.muta_exec = dict()		# mapping from mutant to the executions where it is used as target
		self.test_exec = dict()		# mapping from test to the executions on which it is executed
		self.corpus = set()			# the set of words encoding the RIP-features required in testing.
		return

	def get_project(self):
		"""
		:return: it provides original samples for analysis and used
		"""
		return self.project

	def get_executions(self):
		"""
		:return: the set of RIP-executions between mutants and tests
		"""
		return self.exec_list

	def get_executions_of(self, key):
		"""
		:param key: Mutant or TestCase
		:return: executions performed on the key
		"""
		if key in self.muta_exec:
			executions = self.muta_exec[key]
			executions: set
		elif key in self.test_exec:
			executions = self.test_exec[key]
			executions: set
		else:
			executions = set()
		return executions

	def get_mutants(self):
		return self.muta_exec.keys()

	def get_tests(self):
		return self.test_exec.keys()

	def get_corpus(self):
		"""
		:return: the set of words encoding the RIP-features required in testing.
		"""
		return self.corpus

	def get_feature(self, word: str):
		"""
		:param word: category$operator$validate$execution$location$parameter
		:return: RIPFeature
		"""
		items = word.strip().split('$')
		category = items[0].strip()
		operator = items[1].strip()
		validate = jcbase.CToken.parse(items[2].strip()).get_token_value()
		exec_tok = jcbase.CToken.parse(items[3].strip()).get_token_value()
		loca_tok = jcbase.CToken.parse(items[4].strip()).get_token_value()
		para_tok = jcbase.CToken.parse(items[5].strip()).get_token_value()
		execution = self.project.program.function_call_graph.get_execution(exec_tok[0], exec_tok[1])
		location = self.project.program.cir_tree.get_cir_node(loca_tok)
		if para_tok is None:
			parameter = None
		else:
			parameter = self.project.sym_tree.get_sym_node(items[5].strip())
		return RIPFeature(category, operator, validate, execution, location, parameter)

	def __wording__(self, word: str, t_value, f_value, n_value):
		"""
		:param word: category$operator$validate$execution$location$parameter
		:param t_value: value set to validate if validate is True
		:param f_value: value set to validate if validate is False
		:param n_value: value set to validate if validate is None
		:return: category$operator$[validate]$execution$location$parameter
		"""
		if len(word.strip()) > 0:
			items = word.strip().split('$')
			category = items[0].strip()
			operator = items[1].strip()
			bool_val = jcbase.CToken.parse(items[2].strip()).get_token_value()
			execution = items[3].strip()
			location = items[4].strip()
			parameter = items[5].strip()
			if bool_val is None:
				bool_val = n_value
			elif bool_val:
				bool_val = t_value
			else:
				bool_val = f_value
			if bool_val is None:
				validate = "n@null"
			elif bool_val:
				validate = "b@true"
			else:
				validate = "b@false"
			word = "{}${}${}${}${}${}".format(category, operator, validate, execution, location, parameter)
			self.corpus.add(word)
		return word.strip()

	def __produce__(self, line: str, t_value, f_value, n_value):
		"""
		:param line: mid tid word+
		:param t_value: value set to validate if validate is True
		:param f_value: value set to validate if validate is False
		:param n_value: value set to validate if validate is None
		:return: RIPExecution or None
		"""
		if len(line.strip()) > 0:
			items = line.strip().split('\t')
			mid = int(items[0].strip())
			tid = int(items[1].strip())
			mutant = self.project.mutant_space.get_mutant(mid)
			if tid < 0:
				test = None
			else:
				test = self.project.test_space.get_test_case(tid)
			words = set()
			for k in range(2, len(items)):
				word = self.__wording__(items[k], t_value, f_value, n_value)
				if len(word) > 0:
					words.add(word)
			return RIPExecution(self, mutant, test, words)
		return None

	def __consume__(self, execution: RIPExecution):
		"""
		:param execution:
		:return: add the execution into the document
		"""
		if not(execution is None):
			self.exec_list.append(execution)
			mutant = execution.get_mutant()
			test = execution.get_test()
			if not(mutant in self.muta_exec):
				self.muta_exec[mutant] = set()
			if not(test in self.test_exec):
				self.test_exec[test] = set()
			self.muta_exec[mutant].add(execution)
			self.test_exec[test].add(execution)
		return

	def __load__(self, file_path: str, t_value, f_value, n_value):
		"""
		:param file_path:
		:param t_value: value set to validate if validate is True
		:param f_value: value set to validate if validate is False
		:param n_value: value set to validate if validate is None
		:return:
		"""
		with open(file_path, 'r') as reader:
			for line in reader:
				self.__consume__(self.__produce__(line, t_value, f_value, n_value))
		return

	@staticmethod
	def load_static_document(project: jcmuta.CProject, t_value, f_value, n_value):
		"""
		:param project: the mutation testing project from which the document is loaded
		:param t_value: value set to validate if validate is True
		:param f_value: value set to validate if validate is False
		:param n_value: value set to validate if validate is None
		:return: RIPDocument
		"""
		document = RIPDocument(project)
		directory = project.program.directory
		for file_name in os.listdir(directory):
			if file_name.endswith(".sft"):
				document.__load__(os.path.join(directory, file_name), t_value, f_value, n_value)
		return document

	@staticmethod
	def load_dynamic_document(project: jcmuta.CProject, t_value, f_value, n_value):
		"""
		:param project: the mutation testing project from which the document is loaded
		:param t_value: value set to validate if validate is True
		:param f_value: value set to validate if validate is False
		:param n_value: value set to validate if validate is None
		:return: RIPDocument
		"""
		document = RIPDocument(project)
		directory = project.program.directory
		for file_name in os.listdir(directory):
			if file_name.endswith(".dft"):
				document.__load__(os.path.join(directory, file_name), t_value, f_value, n_value)
		return document


def testing_document_loading(root_path: str):
	for file_name in os.listdir(root_path):
		directory = os.path.join(root_path, file_name)
		project = jcmuta.CProject(directory, file_name)
		document = RIPDocument.load_static_document(project, True, False, True)
		print("Testing on", file_name, "with", len(project.mutant_space.get_mutants()), "mutants and",
			  len(project.test_space.get_test_cases()), "tests.\n\tGet", len(document.get_executions()),
			  "RIP-executions and", len(document.get_corpus()), "words used to encode RIP-conditions.")
		for execution in document.get_executions():
			execution: RIPExecution
			print("\tExecution between mutation", execution.get_mutant().get_mut_id(), "and", execution.get_test())
			for feature in execution.get_features():
				print("\t==>{}\t{}\t{}\t{}\t\"{}\"\t({})".format(feature.get_category(),
																 feature.get_operator(),
																 feature.get_validate(),
																 feature.get_execution(),
																 feature.get_location().get_cir_code(),
																 feature.get_parameter()))
		print()


NR_CLASS = "NR"			# testing that fails to reach the mutation
NI_CLASS = "NI"			# testing that fails to infect but reaches
NP_CLASS = "NP"			# testing that fails to kill but infect it
KI_CLASS = "KI"			# testing that manages to kill the mutants


class RIPClassifier:
	"""
	It defines interfaces to classify, count and estimate the performance of pattern mining.
	"""

	def __init__(self):
		self.solutions = dict()		# String ==> (nr, ni, np, ki)
		return

	@staticmethod
	def __key_solution__(mutant: jcmuta.Mutant, test):
		"""
		:param mutant:
		:param test:
		:return: String key w.r.t. execution of (m, t)
				(1) (m, None): mid#
				(2) (m, test): mid#tid
		"""
		if test is None:
			return "{}#{}".format(mutant.get_mut_id(), 'nullptr')
		else:
			test: jcmuta.TestCase
			return "{}#{}".format(mutant.get_mut_id(), test.get_test_id())

	# single sample

	def __get_solution__(self, key: str):
		"""
		:param key:
		:return: solution w.r.t. the string key
		"""
		solution = self.solutions[key]
		nr = solution[0]
		ni = solution[1]
		np = solution[2]
		ki = solution[3]
		nr: int
		ni: int
		np: int
		ki: int
		return nr, ni, np, ki

	def __set_solution__(self, mutant: jcmuta.Mutant, test, key: str):
		"""
		:param mutant:
		:param test:
		:param key:
		:return: update the solution w.r.t. execution on (mutant, test) and preserve it in the key
		"""
		nr, ni, np, ki = 0, 0, 0, 0
		s_result = mutant.get_result()
		w_result = mutant.get_weak_mutant().get_result()
		c_result = mutant.get_coverage_mutant().get_result()
		if test is None:
			if s_result.is_killable():
				ki += 1
			elif w_result.is_killable():
				np += 1
			elif c_result.is_killable():
				ni += 1
			else:
				nr += 1
		else:
			if s_result.is_killed_by(test):
				ki += 1
			elif w_result.is_killed_by(test):
				np += 1
			elif c_result.is_killed_by(test):
				ni += 1
			else:
				nr += 1
		self.solutions[key] = (nr, ni, np, ki)
		return

	def __counting__(self, sample):
		"""
		:param sample: either Mutant or RIPExecution
		:return: 	nr, ni, np, ki
					(1) nr: number of testing that fails to reach the target mutation;
					(2) ni: number of testing that fails to infect program state but reached the mutant;
					(3) np: number of testing that fails to kill even though program state is infected;
					(4) ki: number of testing that manages to kill the target mutation;
		"""
		if isinstance(sample, jcmuta.Mutant):
			sample: jcmuta.Mutant
			mutant = sample
			test = None
		else:
			sample: RIPExecution
			mutant = sample.get_mutant()
			test = sample.get_test()
		key = RIPClassifier.__key_solution__(mutant, test)
		if not(key in self.solutions):
			self.__set_solution__(mutant, test, key)
		return self.__get_solution__(key)

	def __classify__(self, sample):
		"""
		:param sample: Mutant or RIPExecution
		:return: 	NR: the testing failed to reach the target mutant
					NI: the testing failed to infect the program state but reach mutation
					NP: the testing failed to kill the mutant even when infect state
					KI: the testing manages to kill the target mutation
		"""
		nr, ni, np, ki = self.__counting__(sample)
		if ki > 0:
			return KI_CLASS
		elif np > 0:
			return NP_CLASS
		elif ni > 0:
			return NI_CLASS
		else:
			return NR_CLASS

	# collection evaluations

	def counting(self, samples):
		"""
		:param samples: set of Mutant(s) or RIPExecution(s)
		:return: 	nr, ni, np, ki, uk, cc
					(1) nr: number of testing that fails to reach the target mutation;
					(2) ni: number of testing that fails to infect program state but reached the mutant;
					(3) np: number of testing that fails to kill even though program state is infected;
					(4) ki: number of testing that manages to kill the target mutation;
					(5) uk: number of testing that fails to kill the mutant {nr + ni + np}
					(6) cc: number of testing that fails to kill but reach the mutant {ni + np}
		"""
		nr, ni, np, ki = 0, 0, 0, 0
		for sample in samples:
			lnr, lni, lnp, lki = self.__counting__(sample)
			nr += lnr
			ni += lni
			np += lnp
			ki += lki
		return nr, ni, np, ki, nr + ni + np, ni + np

	def classify(self, samples):
		"""
		:param samples:
		:return: 	NR --> set of samples that are not reached
					NI --> set of samples that are reached but fail to infect
					NP --> set of samples that are infected but fail to kill
					KI --> set of samples that managed to kill the mutants
		"""
		results = dict()
		results[NR_CLASS] = set()
		results[NI_CLASS] = set()
		results[NP_CLASS] = set()
		results[KI_CLASS] = set()
		for sample in samples:
			results[self.__classify__(sample)].add(sample)
		return results

	def estimate(self, samples, uk_or_cc: bool):
		"""
		:param samples:
		:param uk_or_cc: 	True to take non-killed testing as support
							False to take coincidental correctness as support
		:return: total, support, confidence
		"""
		nr, ni, np, ki, uk, cc = self.counting(samples)
		if uk_or_cc:
			support = uk
		else:
			support = cc
		total = support + ki
		if support == 0:
			confidence = 0.0
		else:
			confidence = support / (total + 0.0)
		return total, support, confidence

	def select(self, samples, uk_or_cc: bool):
		"""
		:param samples:
		:param uk_or_cc: 	True to select non-killed samples
							False to select coincidental correct samples
		:return:
		"""
		results = self.classify(samples)
		selects = results[NI_CLASS] | results[NP_CLASS]
		if uk_or_cc:
			selects = selects | results[NR_CLASS]
		return selects


class RIPPattern:
	"""
	It defines the pattern of mutation execution using RIP-features used.
	"""

	def __init__(self, document: RIPDocument, classifier: RIPClassifier):
		"""
		:param document:
		:param classifier:
		"""
		self.document = document		# It provides original samples for being matched.
		self.classifier = classifier	# It is used to evaluate the samples of the pattern.
		self.words = list()				# It encodes the set of features defining this pattern
		self.executions = set()			# The set of RIP-executions matched with this pattern.
		self.mutants = set()			# The set of mutants of which executions matching with.
		return

	# data samples

	def get_document(self):
		"""
		:return: It provides original samples for being matched.
		"""
		return self.document

	def get_executions(self):
		"""
		:return: The set of RIP-executions matched with this pattern.
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: The set of mutants of which executions matching with.
		"""
		return self.mutants

	def get_samples(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: True to select executions or mutants
		:return:
		"""
		if exe_or_mut:
			return self.executions
		else:
			return self.mutants

	# estimations

	def get_classifier(self):
		"""
		:return: the classifier used to estimate the performance of this pattern.
		"""
		return self.classifier

	def counting(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: True to count on executions or mutants
		:return: nr, ni, np, ki, uk, cc
		"""
		return self.classifier.counting(self.get_samples(exe_or_mut))

	def classify(self, exe_or_mut: bool):
		"""
		:param exe_or_mut:
		:return: mapping from String to Samples
		"""
		return self.classifier.classify(self.get_samples(exe_or_mut))

	def estimate(self, exe_or_mut: bool, uk_or_cc: bool):
		"""
		:param exe_or_mut: True to estimate on executions or mutants
		:param uk_or_cc: True to take non-killed as support or coincidental correctness
		:return: total, support, confidence
		"""
		return self.classifier.estimate(self.get_samples(exe_or_mut), uk_or_cc)

	def select(self, exe_or_mut: bool, uk_or_cc: bool):
		return self.classifier.select(self.get_samples(exe_or_mut), uk_or_cc)

	# features

	def get_words(self):
		"""
		:return: It encodes the set of features defining this pattern
		"""
		return self.words

	def get_features(self):
		"""
		:return: the set of features defining this pattern
		"""
		features = list()
		for word in self.words:
			features.append(self.document.get_feature(word))
		return features

	def __str__(self):
		return str(self.words)

	def __len__(self):
		return len(self.words)

	# data-feature matching

	def __matching__(self, execution: RIPExecution):
		"""
		:param execution:
		:return: True if the execution matches with the pattern
		"""
		for word in self.words:
			if not(word in execution.get_words()):
				return False
		return True

	def set_samples(self, parent):
		"""
		:param parent: 	(1) None will update the samples using all the executions in document
						(2) Pattern will update the samples using executions in parent pattern
		:return:
		"""
		if parent is None:
			executions = self.document.get_executions()
		else:
			parent: RIPPattern
			executions = parent.get_executions()
		self.executions.clear()
		self.mutants.clear()
		for execution in executions:
			execution: RIPExecution
			if self.__matching__(execution):
				self.executions.add(execution)
				self.mutants.add(execution.get_mutant())
		return

	def subsume(self, pattern, uk_or_cc):
		"""
		:param pattern:
		:param uk_or_cc: True to only match the non-killed executions matched
						 False to only match the coincidental correct executions
						 None to match the entire set of executions within
		:return: True if the executions in this pattern include all those matched in the pattern
		"""
		pattern: RIPPattern
		if uk_or_cc is None:
			ori_executions = self.executions
			sub_executions = pattern.get_executions()
		else:
			ori_executions = self.select(True, uk_or_cc)
			sub_executions = pattern.select(True, uk_or_cc)
		for execution in sub_executions:
			if not(execution in ori_executions):
				return False
		if len(ori_executions) == len(sub_executions):
			return len(self) <= len(pattern)			# equivalence
		return True										# strictly subsumes

	def extends(self, word: str):
		"""
		:param word:
		:return: child pattern extended from this one by adding one single word
		"""
		word = word.strip()
		if len(word) > 0 and not(word in self.words):
			child = RIPPattern(self.document, self.classifier)
			for old_word in self.words:
				child.words.append(old_word)
			child.words.append(word)
			child.words.sort()
			return child
		return self


if __name__ == "__main__":
	testing_document_loading("/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features")

