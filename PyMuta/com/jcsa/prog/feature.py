"""
This file implements the pre-processing stage of data mining task, including definition the model of
inputs data and patterns presented, along with the interfaces for creating them.
"""


import os
import com.jcsa.libs.muta as jcmuta


class RIPExecution:
	"""
	It describes an execution between mutant and test case (or None if the execution is created statically)
	annotated with a collection of symbolic conditions required for killing that mutation.
	"""

	def __init__(self, document, mutant: jcmuta.Mutant, test: jcmuta.TestCase, words):
		"""
		:param document: the document of mutation executions in form of RIP testing process
		:param mutant: the mutation introduced as objective for being revealed during tests
		:param test: the test case applied in the execution or None if it is created statically
		:param words: the collection of words encoding the symbolic conditions required in.
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
		:return: the document of mutation executions in form of RIP testing process
		"""
		return self.document

	def get_mutant(self):
		"""
		:return: the mutation introduced as objective for being revealed during tests
		"""
		return self.mutant

	def get_test(self):
		"""
		:return: the test case applied in the execution or None if it is created statically
		"""
		return self.test

	def get_words(self):
		"""
		:return: the collection of words encoding the symbolic conditions required in.
		"""
		return self.words

	def get_conditions(self):
		"""
		:return: the collection of the symbolic conditions required in execution.
		"""
		conditions = list()
		for word in self.words:
			conditions.append(self.document.get_condition(word))
		return conditions


class RIPDocument:
	"""
	The document preserves the executions between mutants and test cases or statically generated
	annotated with a set of symbolic conditions required for killing them in form of RIP process
	where reachability, infection and propagation define the objectives for being met.
	"""

	def __init__(self, project: jcmuta.CProject, postfix: str):
		self.project = project
		self.executions = list()
		self.muta_execs = dict()
		self.test_execs = dict()
		self.conditions = dict()

		c_doc = self.project.load_documents(postfix)
		for line in c_doc.get_executions():
			line: jcmuta.SymExecution
			mutant = line.get_mutant()
			test = line.get_test()
			words = set()
			for sym_instance in line.get_instances():
				sym_instance: jcmuta.SymInstance
				sym_condition = sym_instance.get_condition()
				words.add(str(sym_condition))
				self.conditions[str(sym_condition)] = sym_condition
			execution = RIPExecution(self, mutant, test, words)
			self.executions.append(execution)
			if not(mutant in self.muta_execs):
				self.muta_execs[mutant] = set()
			self.muta_execs[mutant].add(execution)
			if not(test is None):
				if not(test in self.test_execs):
					self.test_execs[test] = set()
				self.test_execs[test].add(execution)
		return

	def get_project(self):
		"""
		:return: It provides the original data for mining
		"""
		return self.project

	def get_executions(self):
		"""
		:return: the set of executions between mutants and tests annotated with symbolic conditions as transactions
		"""
		return self.executions

	def get_executions_of(self, key):
		"""
		:param key: Mutant or TestCase
		:return: executions where the key is performed
		"""
		if key in self.muta_execs:
			return self.muta_execs[key]
		elif key in self.test_execs:
			return self.test_execs[key]
		else:
			return set()

	def get_mutants(self):
		"""
		:return: the set of mutants being executed
		"""
		return self.muta_execs.keys()

	def get_tests(self):
		"""
		:return: the set of test cases being executed
		"""
		return self.test_execs.keys()

	def get_conditions(self):
		"""
		:return: the set of conditions involved
		"""
		return self.conditions.values()

	def get_words(self):
		"""
		:return: the set of words encoding conditions applied
		"""
		return self.conditions.keys()

	def get_condition(self, word: str):
		"""
		:param word:
		:return: symbolic condition w.r.t. the word encoding it
		"""
		return self.conditions[word]


NR_CLASS = "NR"			# testing that fails to reach the mutation
NI_CLASS = "NI"			# testing that fails to infect but reaches
NP_CLASS = "NP"			# testing that fails to kill but infect it
KI_CLASS = "KI"			# testing that manages to kill the mutants


class RIPClassifier:
	"""
	It classifies and estimates the executions or mutants in form of reachability, infection and propagation framework.
	"""

	def __init__(self, tests):
		"""
		:param tests: the set of test cases applied or None if the executions are counted over entire set as killable
		or not.
		"""
		self.tests = tests
		self.solutions = dict()
		return

	# singleton methods

	@staticmethod
	def __key_solution__(mutant: jcmuta.Mutant, test):
		if test is None:
			return "{}".format(mutant.get_mut_id())
		else:
			test: jcmuta.TestCase
			return "{}#{}".format(mutant.get_mut_id(), test.get_test_id())

	def __get_solution__(self, key: str):
		"""
		:param key:
		:return: the solution w.r.t. the given key
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
		:return: update the solution w.r.t. key under the execution between mutant-test
		"""
		s_result = mutant.get_result()
		w_result = mutant.get_weak_mutant().get_result()
		c_result = mutant.get_coverage_mutant().get_result()
		nr, ni, np, ki = 0, 0, 0, 0
		if not(test is None):							# dynamic test case determines
			if s_result.is_killed_by(test):
				ki += 1
			elif w_result.is_killed_by(test):
				np += 1
			elif c_result.is_killed_by(test):
				ni += 1
			else:
				nr += 1
		else:											# static evaluation in context of tests
			if s_result.is_killed_in(self.tests):
				ki += 1
			elif w_result.is_killed_in(self.tests):
				np += 1
			elif c_result.is_killed_in(self.tests):
				ni += 1
			else:
				nr += 1
		self.solutions[key] = (nr, ni, np, ki)
		return

	def __counting__(self, sample):
		"""
		:param sample: either Mutant or RIPExecution
		:return: 	nr, ni, np, ki
					(1) nr: the number of testing that fail to reach the mutant
					(2) ni: the number of testing that reach but fail to infect
					(3) np: the number of testing that infect but fail to kill
					(4) ki: the number of testing to kill
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
		if not (key in self.solutions):
			self.__set_solution__(mutant, test, key)
		return self.__get_solution__(key)

	def __classify__(self, sample):
		"""
		:param sample: Mutant or TestCase
		:return:	NR -- the testing fail to reach the mutant
					NI -- the testing reach but fail to infect
					NP -- the testing infect but fail to kill
					KI -- the testing managed to kill the mutant
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

	# collection methods

	def counting(self, samples):
		"""
		:param samples: the collection of mutants or executions
		:return: nr, ni, np, ki, uk, cc
					(1) nr: the number of testing that fail to reach the mutant
					(2) ni: the number of testing that reach but fail to infect
					(3) np: the number of testing that infect but fail to kill
					(4) ki: the number of testing to kill
					(5) uk: the number of testing that fail to kill
					(6) cc: the number of testing that reach but fail to kill
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
		:param samples: the collection of mutants or executions
		:return: Mapping from class name to the samples it match with
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
		:param samples: the collection of mutants or executions
		:param uk_or_cc: True to take non-killed or coincidental correct as support
		:return: total, support, confidence (0.0 -- 1.0)
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
		:param samples: the collection of mutants or executions
		:param uk_or_cc: True to select non-killed or coincidental correct ones
		:return:
		"""
		results = self.classify(samples)
		selects = results[NI_CLASS] | results[NP_CLASS]
		if uk_or_cc:
			selects = selects | results[NR_CLASS]
		return selects


class RIPPattern:
	"""
	The reachability-infection-propagation pattern is defined in this way:
		It contains a subset of symbolic conditions required in executions such that: for any executions
		or their mutants which require these specified conditions, the mutants remain alive with a high
		likelihood in database of transaction forms.
	"""

	def __init__(self, document: RIPDocument, classifier: RIPClassifier):
		"""
		:param document: It provides original dataset for estimating the pattern
		:param classifier: It is used to implement the pattern evaluation.
		"""
		self.document = document
		self.classifier = classifier
		self.words = list()			# The ordered set of words encoding the conditions included.
		self.executions = set()		# The set of executions that match with this pattern.
		self.mutants = set()		# The set of mutants of which executions match with this one.
		return

	# data samples

	def get_document(self):
		"""
		:return: It provides original dataset for estimating the pattern
		"""
		return self.document

	def get_executions(self):
		return self.executions

	def get_mutants(self):
		return self.mutants

	def get_samples(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: True to select executions or mutants as samples
		:return:
		"""
		if exe_or_mut:
			return self.executions
		else:
			return self.mutants

	def __matching__(self, execution: RIPExecution):
		"""
		:param execution:
		:return: True if conditions included in the pattern are required in execution specified
		"""
		for word in self.words:
			if not(word in execution.get_words()):
				return False
		return True

	def set_samples(self, parent):
		"""
		:param parent: parent pattern from which this is extended directly or none to update on entire document
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

	# estimations

	def get_classifier(self):
		"""
		:return: It is used to implement the pattern evaluation.
		"""
		return self.classifier

	def counting(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: True to take executions or mutants as samples for counting
		:return: 	nr, ni, np, ki, uk, cc
		"""
		return self.classifier.counting(self.get_samples(exe_or_mut))

	def classify(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: True to take executions or mutants as samples for classification
		:return:
		"""
		return self.classifier.classify(self.get_samples(exe_or_mut))

	def estimate(self, exe_or_mut: bool, uk_or_cc: bool):
		"""
		:param exe_or_mut: True to take executions or mutants as samples for estimation
		:param uk_or_cc: True to take unkilled or coincidental correct ones as supports
		:return: total, support, confidence
		"""
		return self.classifier.estimate(self.get_samples(exe_or_mut), uk_or_cc)

	def select(self, exe_or_mut: bool, uk_or_cc: bool):
		"""
		:param exe_or_mut: True to select on executions or mutants
		:param uk_or_cc: True to select non-killed or coincidental correct ones
		:return:
		"""
		return self.classifier.select(self.get_samples(exe_or_mut), uk_or_cc)

	# feature model

	def get_words(self):
		return self.words

	def __len__(self):
		return len(self.words)

	def __str__(self):
		return str(self.words)

	def get_conditions(self):
		conditions = list()
		for word in self.words:
			conditions.append(self.document.get_condition(word))
		return conditions

	# other operations

	def extends(self, word: str):
		"""
		:param word: new word being added to the pattern
		:return: an empty child pattern extended from this one by adding one word (samples are not updated)
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

	def subsume(self, pattern):
		"""
		:param pattern:
		:return: True if the executions matched by input pattern are matched by the given pattern.
		"""
		pattern: RIPPattern
		for execution in pattern.executions:
			if not(execution in self.executions):
				return False
		return True


class RIPPatterns:
	"""
	It manages the structure of RIPPattern(s) generated from mining algorithms
	"""

	def __init__(self, document: RIPDocument, tests):
		self.document = document				# It provides entire dataset for document.
		self.classifier = RIPClassifier(tests)	# It is used as classification and estimate.
		self.patterns = dict()					# Mapping from String to Unique Pattern be created.
		return

	def get_document(self):
		return self.document

	def get_classifier(self):
		return self.classifier

	def get_patterns(self):
		return self.patterns.values()

	def __new_pattern__(self, parent, word: str):
		"""
		:param parent: pattern from which the pattern is extended or None for root.
		:param word:
		:return: the pattern extended from parent with one word appended in the set
		"""
		if parent is None:
			pattern = RIPPattern(self.document, self.classifier)
			pattern = pattern.extends(word)
		else:
			parent: RIPPattern
			pattern = parent.extends(word)
		if not(str(pattern) in self.patterns):
			self.patterns[str(pattern)] = pattern
			pattern.set_samples(parent)
			created = True
		else:
			created = False
		pattern = self.patterns[str(pattern)]
		pattern: RIPPattern
		return pattern

	def get_root(self, word: str):
		return self.__new_pattern__(None, word)

	def get_child(self, parent: RIPPattern, word: str):
		return self.__new_pattern__(parent, word)


def test_load_documents(root_directory: str):
	for filename in os.listdir(root_directory):
		directory_path = os.path.join(root_directory, filename)
		c_project = jcmuta.CProject(directory_path, filename)
		document = RIPDocument(c_project, ".sft")
		print(filename,": Load", len(document.get_mutants()), "mutants against", len(document.get_tests()),
			  "test cases with", len(document.get_conditions()), "symbolic conditions involved for",
			  len(document.get_executions()), "mutation executions in form of RIP transactions.")
		for execution in document.get_executions():
			for condition in execution.get_conditions():
				print("\t{}\t{}\t{}\t{}\t{}".format(condition.get_category(),
													condition.get_operator(),
													condition.get_execution(),
													condition.get_location().get_cir_code(),
													condition.get_parameter()))
	return


if __name__ == "__main__":
	test_load_documents("/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features")

