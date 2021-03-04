"""
This file describes the data model of RIP-based mutation features and classifier to evaluate the performance
of RIP-based mutation patterns to being mined in the algorithm package.
"""


import os
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.code as jccode
import com.jcsa.libs.muta as jcmuta


class RIPCondition:
	"""
	It defines the test condition required for achieving objectives of reachability, infection or propagation in
	the purpose of revealing fault in mutation-based model, including attributes as:
		---	category: 	either "satisfaction" for constraint or "observation" for infected state.
		---	operator: 	the annotation name to refine type as [chg_numb, trap_stmt, ... mut_flow]
		---	validate:	True if the condition is actually satisfied during testing
						False if the condition is actually non-satisfied by tests
						None if whether the condition is satisfied by test remains unknown
		---	execution:	the statement node in control flow graph where the condition is evaluated
		---	location:	the C-intermediate representation location where the condition is defined
		---	parameter:	the symbolic expression to refine its description or None if not needed
	"""

	def __init__(self, category: str, operator: str, validate: bool,
				 execution: jccode.CirExecution, location: jccode.CirNode,
				 parameter: jcbase.SymNode):
		"""
		:param category:	either "satisfaction" for constraint or "observation" for infected state.
		:param operator:	the annotation name to refine type as [chg_numb, trap_stmt, ... mut_flow]
		:param validate:	True if the condition is actually satisfied during testing
							False if the condition is actually non-satisfied by tests
							None if whether the condition is satisfied by test remains unknown
		:param execution:	the statement node in control flow graph where the condition is evaluated
		:param location:	the C-intermediate representation location where the condition is defined
		:param parameter:	the symbolic expression to refine its description or None if not needed
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
		:return:	either "satisfaction" for constraint or "observation" for infected state.
		"""
		return self.category

	def get_operator(self):
		"""
		:return: 	the annotation name to refine type as [chg_numb, trap_stmt, ... mut_flow]
		"""
		return self.operator

	def get_validate(self):
		"""
		:return: 	True if the condition is actually satisfied during testing
					False if the condition is actually non-satisfied by tests
					None if whether the condition is satisfied by test remains unknown
		"""
		return self.validate

	def get_execution(self):
		"""
		:return:	the statement node in control flow graph where the condition is evaluated
		"""
		return self.execution

	def get_location(self):
		"""
		:return: the C-intermediate representation location where the condition is defined
		"""
		return self.location

	def get_parameter(self):
		"""
		:return: the symbolic expression to refine its description or None if not needed
		"""
		return self.parameter


class RIPExecution:
	"""
	It represents the execution between a mutant and a test case applied, annotated with a series of conditions being
	required for killing the target mutant used, including:
		--- mutant:	the mutation as target for being revealed;
		---	test:	the test case applied in the execution or None if the execution is extracted using static analysis
		---	words:	the set of words that encode the conditions required for killing the mutant in this execution in
					terms of Reachability-Infection-Propagation model.
	"""

	def __init__(self, document, mutant: jcmuta.Mutant, test: jcmuta.TestCase, words):
		"""
		:param mutant:	the mutation as target for being revealed;
		:param test: 	the test case applied in the execution or None if the execution is extracted using static analysis
		:param words: 	the set of words that encode the conditions required for killing the mutant in this execution in
						terms of Reachability-Infection-Propagation model.
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
		:return: the document where the execution is recorded.
		"""
		return self.document

	def get_mutant(self):
		"""
		:return: the mutation as target for being revealed;
		"""
		return self.mutant

	def get_test(self):
		"""
		:return: the test case applied in the execution or None if the execution is extracted using static analysis
		"""
		return self.test

	def get_words(self):
		"""
		:return: the set of words that encode the conditions required for killing the mutant in this execution in
				 terms of Reachability-Infection-Propagation model.
		"""
		return self.words

	def get_conditions(self):
		"""
		:return: the conditions required for killing the mutant in this execution in
				 terms of Reachability-Infection-Propagation model.
		"""
		conditions = list()
		for word in self.words:
			conditions.append(self.document.get_condition(word))
		return conditions


class RIPDocument:
	"""
	The document preserves all the executions between mutants and test cases applied during testing experiment.
	"""

	def __init__(self, project: jcmuta.CProject):
		self.project = project		# It provides all the original samples for interpreting the data in the document
		self.exec_list = list()		# The collection of RIP-executions performed during mutation testing
		self.muta_exec = dict()		# Mapping from Mutant to the executions where they are applied
		self.test_exec = dict()		# Mapping from TestCase to the executions where they are applied
		self.corpus = set()			# The set of words encoding the conditions required in the mutation executions
		return

	def get_project(self):
		"""
		:return: It provides all the original samples for interpreting the data in the document
		"""
		return self.project

	def get_executions(self):
		"""
		:return: The collection of RIP-executions performed during mutation testing
		"""
		return self.exec_list

	def get_executions_of(self, key):
		"""
		:param key: Mutant or TestCase
		:return: the set of executions where the key (Mutant or TestCase) is applied
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

	def get_corpus(self):
		"""
		:return: The set of words encoding the conditions required in the mutation executions
		"""
		return self.corpus

	def get_mutants(self):
		"""
		:return: The set of mutants applied in testing
		"""
		return self.muta_exec.keys()

	def get_tests(self):
		"""
		:return: The set of test cases used in testing
		"""
		return self.test_exec.keys()

	def get_condition(self, word: str):
		"""
		:param word: category$operator$validate$execution$location$parameter
		:return: RIPCondition
		"""
		items = word.strip().split('$')
		category = items[0].strip()
		operator = items[1].strip()
		validate = jcbase.CToken.parse(items[2].strip()).get_token_value()
		exec_tok = jcbase.CToken.parse(items[3].strip()).get_token_value()
		loct_tok = jcbase.CToken.parse(items[4].strip()).get_token_value()
		para_tok = jcbase.CToken.parse(items[5].strip()).get_token_value()
		execution = self.project.program.function_call_graph.get_execution(exec_tok[0], exec_tok[1])
		location = self.project.program.cir_tree.get_cir_node(loct_tok)
		if para_tok is None:
			parameter = None
		else:
			parameter = self.project.sym_tree.get_sym_node(items[5].strip())
		return RIPCondition(category, operator, validate, execution, location, parameter)

	def __wording__(self, word: str, t_value, f_value, n_value):
		"""
		:param word: category$operator$validate$execution$location$parameter
		:param t_value: the value set to validate part if it is True
		:param f_value: the value set to validate part if it is False
		:param n_value: the value set to validate part if it is None
		:return: category$operator$[validate]$execution$location$parameter & update self.corpus
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
		:param t_value: the value set to validate part if it is True
		:param f_value: the value set to validate part if it is False
		:param n_value: the value set to validate part if it is None
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
			words = list()
			for k in range(2, len(items)):
				word = self.__wording__(items[k].strip(), t_value, f_value, n_value)
				if len(word) > 0:
					words.append(word)
			return RIPExecution(self, mutant, test, words)
		else:
			return None

	def __consume__(self, execution):
		"""
		:param execution:
		:return: update the execution into the document list
		"""
		if not(execution is None):
			execution: RIPExecution
			self.exec_list.append(execution)
			mutant = execution.get_mutant()
			test = execution.get_test()
			if not(mutant in self.muta_exec):
				self.muta_exec[mutant] = set()
			self.muta_exec[mutant].add(execution)
			if not(test is None):
				if not(test in self.test_exec):
					self.test_exec[test] = set()
				self.test_exec[test].add(execution)
		return

	def __loading__(self, file_path: str, t_value, f_value, n_value):
		"""
		:param file_path: the file where the executions are provided
		:param t_value: the value set to validate part if it is True
		:param f_value: the value set to validate part if it is False
		:param n_value: the value set to validate part if it is None
		:return: update the library in the document
		"""
		with open(file_path, 'r') as reader:
			for line in reader:
				self.__consume__(self.__produce__(line, t_value, f_value, n_value))
		return

	@staticmethod
	def load_static_documents(project: jcmuta.CProject, t_value, f_value, n_value):
		"""
		:param project:
		:param t_value: the value set to validate part if it is True
		:param f_value: the value set to validate part if it is False
		:param n_value: the value set to validate part if it is None
		:return: document loaded from xxx.sft files
		"""
		document = RIPDocument(project)
		directory = project.program.directory
		for file_name in os.listdir(directory):
			if file_name.endswith(".sft"):
				document.__loading__(os.path.join(directory, file_name), t_value, f_value, n_value)
		return document

	@staticmethod
	def load_dynamic_documents(project: jcmuta.CProject, t_value, f_value, n_value):
		"""
		:param project:
		:param t_value: the value set to validate part if it is True
		:param f_value: the value set to validate part if it is False
		:param n_value: the value set to validate part if it is None
		:return: document loaded from xxx.sft files
		"""
		document = RIPDocument(project)
		directory = project.program.directory
		for file_name in os.listdir(directory):
			if file_name.endswith(".dft"):
				document.__loading__(os.path.join(directory, file_name), t_value, f_value, n_value)
		return document


NR_CLASS = "NR"			# testing that fails to reach the mutation
NI_CLASS = "NI"			# testing that fails to infect but reaches
NP_CLASS = "NP"			# testing that fails to kill but infect it
KI_CLASS = "KI"			# testing that manages to kill the mutants


class RIPClassifier:
	"""
	It is used to evaluate the performance of a group of executions matched by the pattern based on RIP model.
	"""

	def __init__(self):
		self.solutions = dict()		# Mapping from string to [nr, ni, np, ki]
		return

	# single solution

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
		if not(key in self.solutions):
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

	# collection operators

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


def testing_document_loading(root_path: str):
	for file_name in os.listdir(root_path):
		directory = os.path.join(root_path, file_name)
		project = jcmuta.CProject(directory, file_name)
		document = RIPDocument.load_static_documents(project, True, False, True)
		print("Testing on", file_name, "with", len(project.mutant_space.get_mutants()), "mutants and",
			  len(project.test_space.get_test_cases()), "tests.\n\tGet", len(document.get_executions()),
			  "RIP-executions and", len(document.get_corpus()), "words used to encode RIP-conditions.")
		for execution in document.get_executions():
			execution: RIPExecution
			print("\tExecution between mutation", execution.get_mutant().get_mut_id(), "and", execution.get_test())
			for condition in execution.get_conditions():
				print("\t==>{}\t{}\t{}\t{}\t\"{}\"\t({})".format(condition.get_category(),
																 condition.get_operator(),
																 condition.get_validate(),
																 condition.get_execution(),
																 condition.get_location().get_cir_code(),
																 condition.get_parameter()))
		print()


if __name__ == "__main__":
	testing_document_loading("/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features")

