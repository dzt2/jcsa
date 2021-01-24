"""
This file defines the mutation project data
"""


import os
import random
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.code as jcparse


class CProject:
	"""
	Mutation Testing Project
	"""

	def __init__(self, directory: str, file_name: str):
		self.program = jcparse.CProgram(directory, file_name)
		tst_file_path = os.path.join(directory, file_name + ".tst")
		mut_file_path = os.path.join(directory, file_name + ".mut")
		res_file_path = os.path.join(directory, file_name + ".res")
		sym_file_path = os.path.join(directory, file_name + ".sym")
		self.test_space = TestSpace(self, tst_file_path)
		self.sym_tree = jcbase.SymTree(sym_file_path)
		self.mutant_space = MutantSpace(self, mut_file_path, res_file_path)
		self.evaluation = MutationTestEvaluation(self)
		return

	def load_static_document(self, directory: str, set_none: bool, de_value):
		"""
		:param directory:
		:param set_none:	(T, T) --> Set validate of None as True
							(T, F) --> Set validate of None as False
							(T, N) --> Set validate of None as None (maintain)
							(F, T) --> Set validate of True|False as True
							(F, F) --> Set validate of True|False as False
							(F, N) --> Set validate of True|False as None
		:param de_value:
		:return: load execution from directory/xxx.sft
		"""
		document = RIPDocument(self)
		for file_name in os.listdir(directory):
			if file_name.endswith(".sft"):
				document.load(os.path.join(directory, file_name), set_none, de_value)
				break
		return document

	def load_dynamic_document(self, directory: str, set_none: bool, de_value):
		"""
		:param directory:
		:param set_none:	(T, T) --> Set validate of None as True
							(T, F) --> Set validate of None as False
							(T, N) --> Set validate of None as None (maintain)
							(F, T) --> Set validate of True|False as True
							(F, F) --> Set validate of True|False as False
							(F, N) --> Set validate of True|False as None
		:param de_value:
		:return: load execution from directory/xxx.dft
		"""
		document = RIPDocument(self)
		for file_name in os.listdir(directory):
			if file_name.endswith(".dft"):
				document.load(os.path.join(directory, file_name), set_none, de_value)
		return document


class TestCase:
	"""
	test case with parameter
	"""
	def __init__(self, space, test_id: int, parameter: str):
		space: TestSpace
		self.space = space
		self.test_id = test_id
		self.parameter = parameter.strip()
		return

	def get_space(self):
		return self.space

	def get_test_id(self):
		return self.test_id

	def get_parameter(self):
		return self.parameter

	def get_killing_mutants(self, mutants=None):
		"""
		:param mutants: the set of mutants or None for all the mutants in project
		:return: the set of mutants killed by this test
		"""
		if mutants is None:
			mutants = self.space.get_project().mutant_space.get_mutants()
		killed_mutants = set()
		for mutant in mutants:
			mutant: Mutant
			if mutant.get_result().is_killed_by(self):
				killed_mutants.add(mutant)
		return killed_mutants


class TestSpace:
	def __init__(self, project: CProject, tst_file_path: str):
		self.project = project
		self.test_cases = list()
		self.__parse__(tst_file_path)
		return

	def get_project(self):
		return self.project

	def get_test_cases(self):
		return self.test_cases

	def get_test_case(self, test_id: int):
		"""
		:param test_id:
		:return:
		"""
		test_case = self.test_cases[test_id]
		test_case: TestCase
		return test_case

	def __parse__(self, tst_file_path: str):
		test_case_dict = dict()
		with open(tst_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					test_id = int(items[0].strip())
					parameter = jcbase.CToken.parse(items[1].strip()).get_token_value()
					test_case = TestCase(self, test_id, parameter)
					test_case_dict[test_case.get_test_id()] = test_case
		self.test_cases.clear()
		for k in range(0, len(test_case_dict)):
			self.test_cases.append(test_case_dict[k])
		return


class Mutation:
	"""
	syntactic mutation
	"""
	def __init__(self, mutant, mutation_class: str, mutation_operator: str, location: jcparse.AstNode,
				 parameter: jcbase.CToken):
		"""
		:param mutant:
		:param mutation_class:
		:param mutation_operator:
		:param location:
		:param parameter:
		"""
		mutant: Mutant
		self.mutant = mutant
		self.mutation_class = mutation_class
		self.mutation_operator = mutation_operator
		self.location = location
		self.parameter = parameter
		return

	def get_mutant(self):
		"""
		:return: mutant that is defined by this mutation
		"""
		return self.mutant

	def get_mutation_class(self):
		return self.mutation_class

	def get_mutation_operator(self):
		return self.mutation_operator

	def get_location(self):
		return self.location

	def get_parameter(self):
		return self.parameter


class Mutant:
	"""
	space, ID, mutation, result, coverage, weak, strong
	"""
	def __init__(self, space, mut_id: int, mutation: Mutation):
		space: MutantSpace
		self.space = space
		self.mut_id = mut_id
		self.mutation = mutation
		self.c_mutant = None		# coverage mutation
		self.w_mutant = None		# weak mutation
		self.s_mutant = None		# strong mutation
		self.result = MutationResult(self)
		return

	def get_space(self):
		return self.space

	def get_mut_id(self):
		return self.mut_id

	def get_mutation(self):
		return self.mutation

	def has_result(self):
		return not(self.result is None)

	def get_result(self):
		self.result: MutationResult
		return self.result

	def get_coverage_mutant(self):
		self.c_mutant: Mutant
		return self.c_mutant

	def get_weak_mutant(self):
		self.w_mutant: Mutant
		return self.w_mutant

	def get_strong_mutant(self):
		self.s_mutant: Mutant
		return self.s_mutant

	def get_killing_tests(self, tests=None):
		"""
		:param tests: set of test cases or None for all the tests in space
		:return: test cases that kill this mutant
		"""
		test_cases = set()
		if tests is None:
			tests = self.space.get_project().test_space.get_test_cases()
		for test in tests:
			test: TestCase
			if self.result.is_killed_by(test):
				test_cases.add(test)
		return test_cases


class MutationResult:
	"""
	It preserves the test results for each mutant.
	"""
	def __init__(self, mutant: Mutant):
		"""
		create an empty test result for mutant
		:param mutant:
		"""
		self.mutant = mutant
		self.kill_string = ""
		return

	def get_mutant(self):
		return self.mutant

	def get_length(self):
		return len(self.kill_string)

	def is_killed_by(self, test):
		"""
		:param test: TestCase or its Integer ID
		:return:
		"""
		if isinstance(test, TestCase):
			test: TestCase
			test_id = test.get_test_id()
		else:
			test: int
			test_id = test
		if test_id < len(self.kill_string):
			return self.kill_string[test_id] == '1'
		return False

	def is_killed_in(self, tests):
		"""
		:param tests: collection of TestCase(s) or their Integer ID
		:return: whether be killed by any test in the inputs collection
		"""
		for test in tests:
			if self.is_killed_by(test):
				return True
		return False

	def is_killable(self):
		"""
		:return: whether killed by any tests in the space
		"""
		return '1' in self.kill_string

	def __str__(self):
		return self.kill_string

	def set_result(self, kill_string: str):
		"""
		:param kill_string:
		:return: set the results of the instance
		"""
		self.kill_string = kill_string
		return


class MutantSpace:
	def __init__(self, project: CProject, mut_file_path: str, res_file_path: str):
		"""
		:param project:
		:param mut_file_path:
		:param res_file_path:
		"""
		self.project = project
		self.mutants = list()
		self.results = dict()	# String --> String, Set[MutationResult]
		self.__parse__(mut_file_path)
		self.__loads__(res_file_path)
		return

	def get_project(self):
		return self.project

	def get_mutants(self):
		return self.mutants

	def get_mutant(self, mut_id: int):
		mutant = self.mutants[mut_id]
		mutant: Mutant
		return mutant

	def __parse__(self, mut_file_path: str):
		mutant_dict = dict()
		with open(mut_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mut_id = jcbase.CToken.parse(items[0].strip()).get_token_value()
					mutation_class = items[1].strip()
					mutation_operator = items[2].strip()
					ast_id = jcbase.CToken.parse(items[3].strip()).get_token_value()
					location = self.project.program.ast_tree.get_ast_node(ast_id)
					parameter = jcbase.CToken.parse(items[4].strip())
					mutation = Mutation(None, mutation_class, mutation_operator, location, parameter)
					mutant = Mutant(self, mut_id, mutation)
					mutation.mutant = mutant
					mutant_dict[mutant.get_mut_id()] = mutant
		with open(mut_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mut_id = jcbase.CToken.parse(items[0].strip()).get_token_value()
					mutant = mutant_dict[mut_id]
					cov_id = jcbase.CToken.parse(items[5].strip()).get_token_value()
					wek_id = jcbase.CToken.parse(items[6].strip()).get_token_value()
					sto_id = jcbase.CToken.parse(items[7].strip()).get_token_value()
					mutant.c_mutant = mutant_dict[cov_id]
					mutant.w_mutant = mutant_dict[wek_id]
					mutant.s_mutant = mutant_dict[sto_id]
		self.mutants.clear()
		for mut_id in range(0, len(mutant_dict)):
			self.mutants.append(mutant_dict[mut_id])
		return

	def __loads__(self, res_file_path: str):
		self.results.clear()
		with open(res_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mutant = self.get_mutant(int(items[0].strip()))
					kill_string = items[1].strip()
					if not(kill_string in self.results):
						self.results[kill_string] = (kill_string, set())
					solution = self.results[kill_string]
					kill_string = solution[0]
					kill_result = solution[1]
					mutant.get_result().set_result(kill_string)
					kill_result.add(mutant.get_result())
		return


class MutationTestEvaluation:
	"""
	It implements the selection of mutation and test case and their evaluation of mutation score.
	"""
	def __init__(self, project: CProject):
		self.project = project
		return

	def __mutants__(self, mutants):
		if mutants is None:
			mutants = self.project.mutant_space.get_mutants()
		return mutants

	def __test_cases__(self, tests):
		if tests is None:
			tests = self.project.test_space.get_test_cases()
		return tests

	@staticmethod
	def __rand_select__(samples):
		"""
		:param samples:
		:return: a sample randomly selected from samples or none
		"""
		counter = random.randint(0, len(samples))
		selected_sample = None
		for sample in samples:
			selected_sample = sample
			counter = counter - 1
			if counter < 0:
				break
		return selected_sample

	@staticmethod
	def __find_test_for__(tests, mutant: Mutant):
		"""
		:param tests: find a random test that kill the mutant
		:param mutant:
		:return: None if no such tests exist in the given inputs
		"""
		mutant.get_killing_tests(tests)

	''' mutation selection '''
	def select_mutants_by_classes(self, classes, mutants=None):
		"""
		:param mutants: collection of mutants from which are selected
		:param classes: set of mutation operator classes
		:return: set of mutants of which class is in the given inputs
		"""
		selected_mutants = set()
		mutants = self.__mutants__(mutants)
		for mutant in mutants:
			mutant: Mutant
			if mutant.get_mutation().get_mutation_class() in classes:
				selected_mutants.add(mutant)
		return selected_mutants

	def select_mutants_by_results(self, killed: bool, mutants=None, tests=None):
		"""
		:param killed: true to select mutants that are killed
		:param mutants: from which the outputs are selected
		:param tests: the set of tests for killing the mutants
		:return: the set of mutants being killed (True) or not (False) by the given tests
		"""
		selected_mutants = set()
		mutants = self.__mutants__(mutants)
		for mutant in mutants:
			mutant: Mutant
			if tests is None:
				result = mutant.get_result().is_killable()
			else:
				result = mutant.get_result().is_killed_in(tests)
			if result == killed:
				selected_mutants.add(mutant)
		return selected_mutants

	''' test case selection '''
	def select_tests_for_random(self, min_number: int, tests=None):
		"""
		:param min_number: minimal number of selected tests
		:param tests: test cases from which the tests are selected
		:return: the set of test cases randomly selected from project or inputs
		"""
		tests = self.__test_cases__(tests)
		remain_tests = set()
		for test in tests:
			test: TestCase
			remain_tests.add(test)
		selected_tests = set()
		while len(selected_tests) < min_number and len(remain_tests) > 0:
			selected_test = MutationTestEvaluation.__rand_select__(remain_tests)
			selected_test: TestCase
			remain_tests.remove(selected_test)
			selected_tests.add(selected_test)
		return selected_tests

	def select_tests_for_mutants(self, mutants, tests=None):
		"""
		:param mutants: the mutants being killed by selected tests
		:param tests: test cases from which the tests are selected
		:return: selected_tests, remain_mutants
		"""
		''' 1. initialization '''
		remain_mutants = self.select_mutants_by_results(True, mutants, None)		# killable mutants among inputs
		tests = self.__test_cases__(tests)
		remain_tests, selected_tests = set(), set()
		for test in tests:
			test: TestCase
			remain_tests.add(test)

		''' 2. test case selection based on mutation '''
		while len(remain_tests) > 0 and len(remain_mutants) > 0:
			''' 2.1. select a random mutant for being killed '''
			mutant = MutationTestEvaluation.__rand_select__(remain_mutants)
			mutant: Mutant
			remain_mutants.remove(mutant)

			''' 2.2. select a random test for killing the mutants '''
			killing_tests = mutant.get_killing_tests(remain_tests)
			selected_test = MutationTestEvaluation.__rand_select__(killing_tests)
			selected_test: TestCase
			remain_tests.remove(selected_test)
			selected_tests.add(selected_test)

			''' 2.3. update the remaining mutants '''
			killed_mutants = selected_test.get_killing_mutants(remain_mutants)
			for killed_mutant in killed_mutants:
				remain_mutants.remove(killed_mutant)

		''' 3. return the test cases selected for killing all the mutants '''
		return selected_tests, remain_mutants

	''' mutation score evaluation '''
	def evaluate_mutation_score(self, mutants, tests):
		"""
		:param mutants:
		:param tests:
		:return: killed_number, over_score (on all mutants), valid_score (on killable mutants)
		"""
		mutants = self.__mutants__(mutants)
		total, valid, killed = 0, 0, 0
		for mutant in mutants:
			mutant: Mutant
			if tests is None:
				result = mutant.get_result().is_killable()
			else:
				result = mutant.get_result().is_killed_in(tests)
			if result:
				killed += 1
			if mutant.get_result().is_killable():
				valid += 1
			total += 1
		over_score, valid_score = 0.0, 0.0
		if killed > 0:
			over_score = killed / (total + 0.0)
			valid_score = killed / (valid + 0.0)
		return killed, over_score, valid_score


class RIPFeature:
	"""
	It describes the feature of reachability-infection-propagation to evaluate testability of mutation in testing.
		(1) category: condition for satisfaction or infection for observation
		(2) operator: eval_stmt, chg_numb, set_bool, ..., trap_stmt, set_auto
		(3) validate: success(True), failure(False), unknown(None)
		(4) execution: the statement where the feature is validated (condition) or injected (infection)
		(5) location: the C-intermediate location where the feature is evaluated
		(6) parameter: symbolic expression to refine the feature description or None if not necessary
	"""

	def __init__(self, category: str, operator: str, validate: bool,
				 execution: jcparse.CirExecution, location: jcparse.CirNode,
				 parameter: jcbase.SymNode):
		"""
		:param category: condition for satisfaction or infection for observation
		:param operator: eval_stmt, chg_numb, set_bool, ..., trap_stmt, set_auto
		:param validate: success(True), failure(False), unknown(None)
		:param execution: the statement where the feature is validated (condition) or injected (infection)
		:param location: the C-intermediate location where the feature is evaluated
		:param parameter: symbolic expression to refine the feature description or None if not necessary
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
		:return: condition for satisfaction or infection for observation
		"""
		return self.category

	def get_operator(self):
		"""
		:return: eval_stmt, chg_numb, set_bool, ..., trap_stmt, set_auto
		"""
		return self.operator

	def get_validate(self):
		"""
		:return:  success(True), failure(False), unknown(None)
		"""
		return self.validate

	def get_execution(self):
		"""
		:return: the statement where the feature is validated (condition) or injected (infection)
		"""
		return self.execution

	def get_location(self):
		"""
		:return: the C-intermediate location where the feature is evaluated
		"""
		return self.location

	def get_parameter(self):
		"""
		:return: symbolic expression to refine the feature description or None if not necessary
		"""
		return self.parameter


class RIPExecution:
	"""
	It describes the mutation execution using RIP-testability-features defined as following.
		(0) document: the document where the execution information is preserved
		(1) mutant: the mutation as target to be killed during execution
		(2) test: the test case to be executed to perform the testing on mutation
		(3) words: the set of words encoding the RIP-features occur in executions
	"""

	def __init__(self, document, mutant: Mutant, test: TestCase, words):
		"""
		:param document: the document where the execution information is preserved
		:param mutant: the mutation as target to be killed during execution
		:param test: the test case to be executed to perform the testing on mutation
		:param words: the set of words encoding the RIP-features occur in executions
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
		:return: the document where the execution information is preserved
		"""
		return self.document

	def get_mutant(self):
		"""
		:return: the mutation as target to be killed during execution
		"""
		return self.mutant

	def has_test(self):
		"""
		:return: True if the execution is generated using dynamic analysis or static analysis alternatively
		"""
		return not(self.test is None)

	def get_test(self):
		"""
		:return: the test case to be executed to perform the testing on mutation
		"""
		return self.test

	def get_words(self):
		"""
		:return: the set of words encoding the RIP-features occur in executions
		"""
		return self.words

	def get_s_result(self):
		"""
		:return: whether the mutant is strongly killed by the test specified in execution
		"""
		if self.test is None:
			return self.mutant.get_result().is_killable()
		else:
			return self.mutant.get_result().is_killed_by(self.test)

	def get_w_result(self):
		"""
		:return: whether the mutant is weakly killed by the test specified in execution
		"""
		if self.test is None:
			return self.mutant.get_weak_mutant().get_result().is_killable()
		else:
			return self.mutant.get_weak_mutant().get_result().is_killed_by(self.test)

	def get_c_result(self):
		"""
		:return: whether the mutant is reached by the test specified in testing
		"""
		if self.test is None:
			return self.mutant.get_coverage_mutant().get_result().is_killable()
		else:
			return self.mutant.get_coverage_mutant().get_result().is_killed_by(self.test)

	def get_features(self):
		"""
		:return: the set of RIP-testability features required in execution
		"""
		features = list()
		for word in self.words:
			features.append(self.document.get_feature(word))
		return features


class RIPDocument:
	"""
	The document is used to preserve the executions, mutants and tests being executed and the words
	to encode RIP-testability features extracted from the symbolic analysis.
	"""

	def __init__(self, project: CProject):
		self.project = project			# It provides contextual information to parse RIP-feature from word
		self.exec_list = list()			# The set of executions against mutant and test being analyzed
		self.muta_exec = dict()			# The mapping from Mutant to the set of executions it is performed
		self.test_exec = dict()			# The mapping from TestCase to the set of executions it's performed
		self.corpus = set()				# The set of words encoding the RIP-testability features in executions
		return

	def get_project(self):
		"""
		:return: It provides contextual information to parse RIP-feature from word
		"""
		return self.project

	def get_executions(self):
		"""
		:return: The set of executions against mutant and test being analyzed
		"""
		return self.exec_list

	def get_mutants(self):
		"""
		:return: The set of mutants being executed against tests in executions
		"""
		return self.muta_exec.keys()

	def get_tests(self):
		"""
		:return: The set of test cases being executed against mutants in the executions
		"""
		return self.test_exec.keys()

	def get_executions_of(self, key):
		"""
		:param key: Mutant or TestCase
		:return: the set of executions performed on the Mutant or TestCase
		"""
		if key in self.muta_exec:
			executions = self.muta_exec[key]
		elif key in self.test_exec:
			executions = self.test_exec[key]
		else:
			executions = set()
		executions: set
		return executions

	def get_feature(self, word: str):
		"""
		:param word: category$operator$validate$execution$location$parameter
		:return: RIPFeature being encoded by the word
		"""
		items = word.strip().split('$')
		category = items[0].strip()
		operator = items[1].strip()
		validate = jcbase.CToken.parse(items[2].strip()).get_token_value()
		ex_token = jcbase.CToken.parse(items[3].strip()).get_token_value()
		lo_token = jcbase.CToken.parse(items[4].strip()).get_token_value()
		execution = self.project.program.function_call_graph.get_execution(ex_token[0], ex_token[1])
		location = self.project.program.cir_tree.get_cir_node(lo_token)
		param_tok = jcbase.CToken.parse(items[5].strip()).get_token_value()
		if param_tok is None:
			parameter = None
		else:
			parameter = self.project.sym_tree.get_sym_node(items[5].strip())
		return RIPFeature(category, operator, validate, execution, location, parameter)

	def get_corpus(self):
		return self.corpus

	def __add_word__(self, word: str, set_none: bool, de_value: bool):
		"""
		:param word: category$operator$validate$execution$location$parameter
		:param set_none: True to set None as de_value or True|False as de_value
		:param de_value: the boolean value set to None or Non-None validate
		:return: category$operator$[validate]$execution$location$parameter
		"""
		word = word.strip()
		if len(word) > 0:
			items = word.split('$')
			category = items[0].strip()
			operator = items[1].strip()
			bool_val = jcbase.CToken.parse(items[2].strip()).get_token_value()
			execution = items[3].strip()
			location = items[4].strip()
			parameter = items[5].strip()
			if set_none:
				if bool_val is None:
					bool_val = de_value
				else:
					pass
			else:
				if bool_val is None:
					pass
				else:
					bool_val = de_value
			if bool_val is None:
				validate = "n@null"
			elif bool_val:
				validate = "b@true"
			else:
				validate = "b@false"
			word = "{}${}${}${}${}${}".format(category, operator, validate, execution, location, parameter)
			self.corpus.add(word)
		return word

	def __produce__(self, line: str):
		"""
		:param line: mid tid word+
		:return: RIPExecution
		"""
		if len(line.strip()) > 0:
			items = line.strip().split('\t')
			mid = int(items[0].strip())
			mutant = self.project.mutant_space.get_mutant(mid)
			tid = int(items[1].strip())
			if tid < 0:
				test = None
			else:
				test = self.project.test_space.get_test_case(tid)
			words = set()
			for k in range(2, len(items)):
				word = items[k].strip()
				if len(word) > 0:
					words.add(word)
			return RIPExecution(self, mutant, test, words)
		else:
			return None

	def __consume__(self, execution: RIPExecution, set_none: bool, de_value: bool):
		"""
		:param execution:
		:return: update the table in the document
		"""
		if not(execution is None):
			self.exec_list.append(execution)
			if not (execution.get_mutant() in self.muta_exec):
				self.muta_exec[execution.get_mutant()] = set()
			self.muta_exec[execution.get_mutant()].add(execution)
			if not (execution.get_test() in self.test_exec):
				self.test_exec[execution.get_test()] = set()
			self.test_exec[execution.get_test()].add(execution)
			for word in execution.get_words():
				self.__add_word__(word, set_none, de_value)
		return

	def load(self, file_path: str, set_none: bool, de_value: bool):
		"""
		:param file_path:
		:param set_none: True to update None-validate or otherwise Not-None validate
		:param de_value: default value used to update the specified validate values
		:return:
		"""
		with open(file_path, 'r') as reader:
			for line in reader:
				execution = self.__produce__(line.strip())
				self.__consume__(execution, set_none, de_value)
		return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	for filename in os.listdir(root_path):
		directory_path = os.path.join(root_path, filename)
		c_project = CProject(directory_path, filename)
		print(filename, "contains", len(c_project.mutant_space.get_mutants()), "mutants and",
			  len(c_project.test_space.get_test_cases()), "test cases.")
		c_document = c_project.load_static_document(directory_path, True, None)
		print("\tLoad", len(c_document.get_executions()), "lines with", len(c_document.get_corpus()), "words...")
		for sym_exec in c_document.get_executions():
			sym_exec: RIPExecution
			for rip_feature in sym_exec.get_features():
				print("\t\t==>", rip_feature.get_category(),
					  "\t", rip_feature.get_operator(),
					  "\t", rip_feature.get_validate(),
					  "\t", rip_feature.get_execution(),
					  "\t\"", rip_feature.get_location().get_cir_code(),
					  "\"\t{", rip_feature.get_parameter(), "}")
		print()

