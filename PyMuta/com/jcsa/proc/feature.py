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


class RIPClassifier:
	"""
	The classifier used to classify, evaluate and estimate mutation or RIPExecution in form
	of three stages in RIP framework, named Reachability, Infection, Propagation and Kill.
	"""

	def __init__(self):
		self.solutions = dict()		# String --> {int, int, int, int}
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

	# collection classifier













