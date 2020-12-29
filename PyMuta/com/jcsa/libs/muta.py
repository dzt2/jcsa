"""
This file defines the mutation project data
"""


import os
import random
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.code as jcparse


class CProject:
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

	def load_static_document(self, directory: str):
		document = SymbolicExecutionDocument(self)
		for file_name in os.listdir(directory):
			if file_name.endswith(".sft"):
				document.load(os.path.join(directory, file_name))
				break
		return document

	def load_dynamic_document(self, directory: str):
		document = SymbolicExecutionDocument(self)
		for file_name in os.listdir(directory):
			if file_name.endswith(".dft"):
				document.load(os.path.join(directory, file_name))
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
		return self.result is not None

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


class SymbolicExecutionFeature:
	"""
	The feature in symbolic execution is modeled as [type, execution, type, parameter]
		--- type: feature type of the symbolic execution
		---	execution: the statement where the feature is annotated
		--- location: the subject that the feature describes on
		--- parameter: symbolic expression to refine the feature in symbolic execution
	"""
	def __init__(self, feature_type: str, execution: jcparse.CirExecution, location: jcparse.CirNode,
				 parameter: jcbase.SymNode):
		"""
		:param feature_type: feature type of the symbolic execution
		:param execution: the statement where the feature is annotated
		:param location: the subject that the feature describes on
		:param parameter: symbolic expression to refine the feature in symbolic execution
		"""
		self.feature_type = feature_type
		self.execution = execution
		self.location = location
		self.parameter = parameter
		return

	def get_feature_type(self):
		"""
		:return:  feature type of the symbolic execution
		"""
		return self.feature_type

	def get_execution(self):
		"""
		:return: the statement where the feature is annotated
		"""
		return self.execution

	def get_location(self):
		"""
		:return: the subject that the feature describes on
		"""
		return self.location

	def has_parameter(self):
		"""
		:return: whether the symbolic expression is used to refine the feature
		"""
		return self.parameter is not None

	def get_parameter(self):
		"""
		:return: symbolic expression to refine the feature in symbolic execution
		"""
		return self.parameter

	def is_constraint(self):
		return self.feature_type == "const"

	def is_state_error(self):
		return self.feature_type != "const"

	def __str__(self):
		feature_type = self.feature_type
		execution = "exe@" + self.execution.get_function().get_name() + "@" + str(self.execution.get_exe_id())
		location = "cir@" + str(self.location.get_cir_id())
		parameter = "n@null"
		if self.has_parameter():
			parameter = "sym@" + self.parameter.get_class_name() + "@" + str(self.parameter.get_class_id())
		return feature_type + "$" + execution + "$" + location + "$" + parameter

	@staticmethod
	def parse(project: CProject, feature_word: str):
		"""
		:param project:
		:param feature_word: string word to encode the symbolic execution feature
		:return: SymbolicExecutionFeature
		"""
		items = feature_word.strip().split('$')
		feature_type = items[0].strip()
		execution_token = jcbase.CToken.parse(items[1].strip()).get_token_value()
		location_token = jcbase.CToken.parse(items[2].strip()).get_token_value()
		execution = project.program.function_call_graph.get_execution(execution_token[0], execution_token[1])
		location = project.program.cir_tree.get_cir_node(location_token)
		parameter_token = jcbase.CToken.parse(items[3].strip()).get_token_value()
		parameter = None
		if parameter_token is not None:
			parameter = project.sym_tree.get_sym_node(items[3].strip())
		return SymbolicExecutionFeature(feature_type, execution, location, parameter)


class SymbolicExecution:
	"""
	symbolic execution in mutation testing is modeled as [mutant, test_case, feature_words, features]
	"""
	def __init__(self, document, mutant: Mutant, test_case: TestCase, feature_words):
		"""
		:param document: where the symbolic execution is preserved
		:param mutant: mutation being executed against test case
		:param test_case: test case executed against the mutation in testing or None if the execution is
						generated from static symbolic analysis
		:param feature_words: sequence of words to encode features in symbolic execution
		"""
		document: SymbolicExecutionDocument
		self.document = document
		self.mutant = mutant
		self.test_case = test_case
		self.feature_words = list()
		for feature_word in feature_words:
			feature_word: str
			self.feature_words.append(feature_word)
		return

	def get_document(self):
		"""
		:return: where the symbolic execution is preserved
		"""
		return self.document

	def get_mutant(self):
		"""
		:return: mutation being executed against test case
		"""
		return self.mutant

	def has_test_case(self):
		"""
		:return:
		"""
		return self.test_case is not None

	def get_test_case(self):
		"""
		:return: test case executed against the mutation in testing or None if the execution is from static analysis
		"""
		return self.test_case

	def is_static(self):
		"""
		:return: symbolic execution is generated from static symbolic analysis where test_case is None
		"""
		return self.test_case is None

	def is_dynamic(self):
		"""
		:return: symbolic execution is generated from dynamic symbolic analysis where test_case is not None
		"""
		return self.test_case is not None

	def get_feature_words(self):
		"""
		:return: words to encode features in symbolic execution in mutation testing
		"""
		return self.feature_words

	def get_features(self):
		"""
		:return: structural features in symbolic execution in mutation testing
		"""
		features = list()
		project = self.document.project
		for feature_word in self.feature_words:
			features.append(SymbolicExecutionFeature.parse(project, feature_word))
		return features


class SymbolicExecutionDocument:
	"""
	It preserves the symbolic executions with features in mutation testing
	"""
	def __init__(self, project: CProject):
		self.project = project			# context to define features in symbolic executions
		self.executions = list()		# list of symbolic executions in the document
		self.mutants = set()			# set of mutants being executed in the execution of document
		self.test_cases = set()			# set of test cases used to execute against mutants in document
		self.corpus = set()				# set of words to encode features in symbolic execution in testing
		return

	def get_project(self):
		return self.project

	def get_executions(self):
		return self.executions

	def get_mutants(self):
		return self.mutants

	def get_test_cases(self):
		return self.test_cases

	def get_corpus(self):
		return self.corpus

	def __append__(self, line: str):
		"""
		:param line:
		:return: SymbolicExecution
		"""
		line = line.strip()
		if len(line) > 0:
			items = line.split('\t')
			mid = int(items[0].strip())
			tid = int(items[1].strip())
			mutant = self.project.mutant_space.get_mutant(mid)
			self.mutants.add(mutant)
			test_case = None
			if tid >= 0:
				test_case = self.project.test_space.get_test_case(tid)
				self.test_cases.add(test_case)
			feature_words = set()
			for k in range(2, len(items)):
				feature_word = items[k].strip()
				if len(feature_word) > 0:
					feature_words.add(feature_word)
					self.corpus.add(feature_word)
			execution = SymbolicExecution(self, mutant, test_case, feature_words)
			self.executions.append(execution)
		return

	def clear(self):
		self.corpus.clear()
		self.mutants.clear()
		self.test_cases.clear()
		self.executions.clear()
		return

	def load(self, file_path: str):
		"""
		:param file_path:
		:return: load the symbolic execution from feature file
		"""
		with open(file_path, 'r') as reader:
			for line in reader:
				self.__append__(line.strip())
		return



if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	for filename in os.listdir(root_path):
		directory_path = os.path.join(root_path, filename)
		c_project = CProject(directory_path, filename)
		print(filename, "contains", len(c_project.mutant_space.get_mutants()), "mutants and",
			  len(c_project.test_space.get_test_cases()), "test cases.")
		c_document = c_project.load_static_document(directory_path)
		print("\tLoad", len(c_document.get_executions()), "lines with", len(c_document.get_corpus()), "words...")
		# for m_execution in c_document.get_executions():
		# 	m_execution: SymbolicExecution
		# 	for s_feature in m_execution.get_features():
		# 		print("\t\t==>", s_feature.get_feature_type(), "\t", s_feature.get_execution(),
		# 			  "\t\"", s_feature.get_location().get_cir_code(), "\"\t{", s_feature.get_parameter(), "}")
		print()
