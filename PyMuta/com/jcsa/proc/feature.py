"""
This file implements the feature modeling before mining algorithm is performed and evaluated,
which refers to the implementation of pre-processing stage in data mining engineering.
"""


import os
import com.jcsa.libs.muta as jcmuta


class RIPExecution:
	"""
	It represents an execution between mutant and a test case (None if the execution describes abstract testing)
	annotated with a sequence of symbolic conditions defined in program context such that for killing the mutant
	all these conditions on the execution of paths need to be satisfied.
	"""

	def __init__(self, document, mutant: jcmuta.Mutant, test: jcmuta.TestCase, words):
		"""
		:param document: document where the execution is created
		:param mutant: the mutation used as test objective for being revealed
		:param test: the test case used to execute or None if execution is generated using static way
		:param words: the collection of words encoding the symbolic conditions required in the execution
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
		:return: document where the execution is created
		"""
		return self.document

	def get_mutant(self):
		"""
		:return: the mutation used as test objective for being revealed
		"""
		return self.mutant

	def has_test(self):
		return not(self.test is None)

	def get_test(self):
		"""
		:return: the test case used to execute or None if execution is generated using static way
		"""
		return self.test

	def get_words(self):
		"""
		:return: the collection of words encoding the symbolic conditions required in the execution
		"""
		return self.words

	def get_conditions(self):
		"""
		:return: the collection of symbolic conditions required in the execution
		"""
		conditions = list()
		for word in self.words:
			conditions.append(self.document.get_condition(word))
		return conditions


class RIPDocument:
	"""
	It preserves all the executions in mutation testing and their conditions required in transaction form.
	"""

	def __init__(self, project: jcmuta.CProject, postfix: str):
		"""
		:param project:
		:param postfix:
		"""
		document = project.load_documents(postfix)
		self.project = project
		self.executions = list()
		self.muta_execs = dict()
		self.test_execs = dict()
		self.conditions = dict()
		for line in document.get_executions():
			line: jcmuta.SymExecution
			mutant = line.get_mutant()
			test = line.get_test()
			words = set()
			for instance in line.get_instances():
				instance: jcmuta.SymInstance
				condition = instance.get_condition()
				words.add(str(condition))
				self.conditions[str(condition)] = condition
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
		return self.project

	def get_executions(self):
		return self.executions

	def get_executions_of(self, key):
		"""
		:param key: Mutant or TestCase
		:return: the executions w.r.t. the key when it is used
		"""
		if key in self.muta_execs:
			return self.muta_execs[key]
		elif key in self.test_execs:
			return self.test_execs[key]
		else:
			return set()

	def get_mutants(self):
		return self.muta_execs.keys()

	def get_tests(self):
		return self.test_execs.keys()

	def get_words(self):
		return self.conditions.keys()

	def get_conditions(self):
		return self.conditions.values()

	def get_condition(self, word: str):
		return self.conditions[word]


NR_CLASS = "NR"			# testing that fails to reach the mutation
NI_CLASS = "NI"			# testing that fails to infect but reaches
NP_CLASS = "NP"			# testing that fails to kill but infect it
KI_CLASS = "KI"			# testing that manages to kill the mutants

R_STAGE = "R"			# it annotates the stage of reaching the faulty statement where the mutation was seeded
I_STAGE = "I"			# it annotates the stage of infecting program state after reaching the faulty statement
P_STAGE = "P"			# it annotates the stage of killing a mutant after the program state have been infected


class RIPClassifier:
	"""
	The classifier used to classify, evaluate and estimate mutation or RIPExecution in form
	of three stages in RIP framework, named Reachability, Infection, Propagation and Kill.
	"""

	def __init__(self, stage: str, pass_threshold: float):
		"""
		:param stage: 	It evaluates at which stage of RIP testing the pass-rate is evaluated
						(1)	R: it evaluates the pass-rate of RIP testing at reachability stage.
						(2) I: it evaluates the pass-rate of RIP testing at infection stage.
						(3) P: it evaluates the pass-rate of RIP testing at propagation stage.
		:param pass_threshold: The probability as the minimal pass-rate at given stage in RIP testing.
		"""
		self.solutions = dict()		# String --> {int, int, int, int}
		self.stage = stage
		self.pass_threshold = pass_threshold
		return

	# singleton classifier

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
			test: jcmuta.TestCase
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

	def __evaluate__(self, sample):
		"""
		:param sample: Mutant or RIPExecution
		:return: It decides whether the sample successful pass through the stage in RIP testing with specified
				 minimal threshold of pass-rate, where pass_rate[stage] > pass_threshold and return following.
				 --	positive, negative, pass_rate, succeed
				 	(1) positive: the number of testing that pass through the stage
				 	(2) negative: the number of testing that fail to pass through the stage
				 	(3) pass_rate: positive / (positive + negative)
				 	(4) succeed: true if pass_rate >= pass_threshold
		"""
		nr, ni, np, ki = self.__counting__(sample)
		if self.stage == R_STAGE:
			negative = nr
			positive = ni + np + ki
		elif self.stage == I_STAGE:
			negative = nr + ni
			positive = np + ki
		else:
			negative = nr + ni + np
			positive = ki
		pass_rate = positive / (nr + ni + np + ki)
		return positive, negative, pass_rate, pass_rate > self.pass_threshold

	# collection classifier

	def counting(self, samples):
		"""
		:param samples: set of Mutant(s) or RIPExecution(s)
		:return: 	nr, ni, np, ki, uk, cc
					(1) nr: the number of testing that fail to reach the mutant
					(2) ni: the number of testing that reach but fail to infect
					(3) np: the number of testing that infect but fail to kill
					(4) ki: the number of testing to kill
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

	def estimate(self, samples, kill_or_not: bool):
		"""
		:param samples: the set of Mutant or RIPExecution(s)
		:param kill_or_not: True to take passing samples as support or non-passed ones
		:return: total, support, confidence
		"""
		support, total = 0, 0
		for sample in samples:
			positive, negative, pass_rate, succeed = self.__evaluate__(sample)
			if kill_or_not:
				if succeed:
					support += 1
				else:
					pass
			else:
				if succeed:
					pass
				else:
					support += 1
			total += 1
		if support > 0:
			confidence = support / total
		else:
			confidence = 0.0
		return total, support, confidence

	def select(self, samples, kill_or_not: bool):
		"""
		:param samples: the set of Mutant or RIPExecution(s)
		:param kill_or_not: True to select the samples passing or not if false
		:return:
		"""
		select = set()
		for sample in samples:
			positive, negative, pass_rate, succeed = self.__evaluate__(sample)
			if kill_or_not:
				if succeed:
					select.add(sample)
				else:
					pass
			else:
				if succeed:
					pass
				else:
					select.add(sample)
		return select


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

	def estimate(self, exe_or_mut: bool, kill_or_not: bool):
		"""
		:param exe_or_mut: True to take executions or mutants as samples for estimation
		:param kill_or_not: Take samples passing through stage or not if False as support
		:return: total, support, confidence
		"""
		return self.classifier.estimate(self.get_samples(exe_or_mut), kill_or_not)

	def select(self, exe_or_mut: bool, kill_or_not: bool):
		"""
		:param exe_or_mut: True to select on executions or mutants
		:param kill_or_not: Take samples passing through stage or not if False as output
		:return:
		"""
		return self.classifier.select(self.get_samples(exe_or_mut), kill_or_not)

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
	It provides interfaces to create and manage the unique instance of RIPPattern.
	"""

	def __init__(self, document: RIPDocument, stage: str, pass_threshold: float):
		self.document = document			# It provides entire dataset for document.
		# It is used as classification and estimate.
		self.classifier = RIPClassifier(stage, pass_threshold)
		self.patterns = dict()				# Mapping from String to Unique Pattern be created.
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

