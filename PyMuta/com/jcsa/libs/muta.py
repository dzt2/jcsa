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

	def load_static_document(self, directory: str, print_value: bool):
		"""
		:param print_value: true to remain value in condition-word or not
		:param directory:
		:return: load execution from directory/xxx.sft
		"""
		document = SymbolicDocument(self)
		for file_name in os.listdir(directory):
			if file_name.endswith(".sft"):
				document.load(os.path.join(directory, file_name), print_value)
				break
		return document

	def load_dynamic_document(self, directory: str, print_value: bool):
		"""
		:param print_value: true to remain value in condition-word or not
		:param directory:
		:return: load execution from directory/xxx.dft
		"""
		document = SymbolicDocument(self)
		for file_name in os.listdir(directory):
			if file_name.endswith(".dft"):
				document.load(os.path.join(directory, file_name), print_value)
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


class SymbolicCondition:
	"""
	It describes the condition for killing a mutant symbolically, denoted as:
		(1) feature: const, chg_numb, set_bool, eval_stmt, ..., trap_stmt
		(2) execution: where the condition is evaluated
		(3) location: the subject of code being described by the condition
		(4) parameter: symbolic expression to refine the condition or None
		(5) value: whether the condition is met (True) or not (False) or unknown (None)
	"""

	def __init__(self, feature: str, execution: jcparse.CirExecution, location: jcparse.CirNode, parameter: jcbase.SymNode, value: bool):
		"""
		:param feature:  const, chg_numb, set_bool, eval_stmt, ..., trap_stmt
		:param execution: where the condition is evaluated
		:param location: the subject of code being described by the condition
		:param parameter: symbolic expression to refine the condition or None
		:param value: whether the condition is met (True) or not (False) or unknown (None)
		"""
		self.feature = feature
		self.execution = execution
		self.location = location
		self.parameter = parameter
		self.value = value
		return

	def get_feature(self):
		"""
		:return: const, chg_numb, set_bool, eval_stmt, ..., trap_stmt
		"""
		return self.feature

	def get_execution(self):
		"""
		:return: where the condition is evaluated
		"""
		return self.execution

	def get_location(self):
		"""
		:return: the subject of code being described by the condition
		"""
		return self.location

	def has_parameter(self):
		"""
		:return: whether the parameter is symbolic expression
		"""
		return not(self.parameter is None)

	def get_parameter(self):
		"""
		:return: symbolic expression to refine the condition or None
		"""
		return self.parameter

	def get_value(self):
		"""
		:return: whether the condition is met (True) or not (False) or unknown (None)
		"""
		return self.value

	def is_constraint(self):
		"""
		:return: whether the condition defined as a constraint of execution.
		"""
		return self.feature in ["const", "eval_stmt", "covr_stmt"]

	def is_state_error(self):
		"""
		:return: whether the condition defined as a state error be infected
		"""
		return not self.is_constraint()

	def __str__(self):
		feature = self.feature
		execution = "exe@" + self.execution.get_function().get_name() + "@" + str(self.execution.get_exe_id())
		location = "cir@" + str(self.location.get_cir_id())
		if self.parameter is None:
			parameter = "n@null"
		else:
			parameter = "sym@" + self.parameter.get_class_name() + "@" + str(self.parameter.get_class_id())
		if self.value is None:
			value = "n@null"
		elif self.value:
			value = "b@true"
		else:
			value = "b@false"
		return "{}${}${}${}${}".format(feature, execution, location, parameter, value)

	@staticmethod
	def parse(project: CProject, word: str):
		"""
		:param project:
		:param word: feature$execution$location$parameter$value
		:return:
		"""
		items = word.strip().split('$')
		feature = items[0].strip()
		execution_token = jcbase.CToken.parse(items[1].strip()).get_token_value()
		location_token = jcbase.CToken.parse(items[2].strip()).get_token_value()
		execution = project.program.function_call_graph.get_execution(execution_token[0], execution_token[1])
		location = project.program.cir_tree.get_cir_node(location_token)
		parameter_token = jcbase.CToken.parse(items[3].strip()).get_token_value()
		if parameter_token is None:
			parameter = None
		else:
			parameter = project.sym_tree.get_sym_node(items[3].strip())
		value = jcbase.CToken.parse(items[4].strip()).get_token_value()
		return SymbolicCondition(feature, execution, location, parameter, value)


class SymbolicExecution:
	"""
	It defines the symbolic execution for killing a mutant with specified conditions.
		(1) mutant: the mutation being killed as target.
		(2) test_case: test being executed (dynamic) or None (static)
		(3) words: to encode symbolic conditions in the execution.
		(4) get_conditions(): symbolic conditions in the execution.
		(5) document: provides contextual information
	"""

	def __init__(self, document, mutant: Mutant, test_case: TestCase, words):
		"""
		:param document: provides contextual information
		:param mutant: the mutation being killed as target.
		:param test_case: test being executed (dynamic) or None (static)
		:param words: to encode symbolic conditions in the execution.
		"""
		document: SymbolicDocument
		self.document = document
		self.mutant = mutant
		self.test_case = test_case
		self.words = list()
		for word in words:
			word: str
			self.words.append(word)
		return

	def get_document(self):
		"""
		:return: where the execution is preserved
		"""
		return self.document

	def get_mutant(self):
		"""
		:return: the mutation being killed as target.
		"""
		return self.mutant

	def has_test_case(self):
		"""
		:return: True if test_case is not None (dynamic) or False if test_case is None (static)
		"""
		return not(self.test_case is None)

	def get_test_case(self):
		"""
		:return: test being executed (dynamic) or None (static)
		"""
		return self.test_case

	def get_words(self):
		"""
		:return: to encode symbolic conditions in the execution.
		"""
		return self.words

	def get_conditions(self):
		"""
		:return: symbolic conditions required for killing mutant in the execution.
		"""
		conditions = list()
		for word in self.words:
			conditions.append(self.document.get_condition(word))
		return conditions


class SymbolicDocument:
	"""
	It maintains all the executions of mutants and conditions for killing them.
		(1) project: it provides contextual information
		(2) executions: mutant executions be preserved
		(3) mutants: mutations being executed by any execution in the document
		(4) test_cases: test cases executed by any execution (dynamic) in it
		(5) corpus: set of words to encode symbolic conditions in document
	"""

	def __init__(self, project: CProject):
		self.project = project		# it provides contextual information
		self.executions = list()	# mutant executions be preserved
		self.mutants = set()		# mutations being executed by any execution in the document
		self.test_cases = set()		# test cases executed by any execution (dynamic) in it
		self.corpus = set()			# set of words to encode symbolic conditions in document
		return

	def get_project(self):
		"""
		:return: it provides contextual information
		"""
		return self.project

	def get_executions(self):
		"""
		:return: mutant executions be preserved
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: mutations being executed by any execution in the document
		"""
		return self.mutants

	def get_test_case(self):
		"""
		:return: test cases executed by any execution (dynamic) in it
		"""
		return self.test_cases

	def get_corpus(self):
		"""
		:return: set of words to encode symbolic conditions in document
		"""
		return self.corpus

	def get_condition(self, word: str):
		"""
		:param word: feature$execution$location$parameter$value
		:return: SymbolicCondition
		"""
		return SymbolicCondition.parse(self.project, word)

	def __std_word__(self, word: str, print_value: bool):
		"""
		:param word: feature$execution$location$parameter$value
		:param print_value: true to remain value part or ignore it
		:return: unique standard word
		"""
		items = word.strip().split('$')
		if not print_value:
			items[4] = "n@null"
		word = "{}${}${}${}${}".format(items[0].strip(), items[1].strip(), items[2].strip(),
									   items[3].strip(), items[4].strip())
		self.corpus.add(word)
		return word

	def __add__(self, line: str, buffer: set, print_value: bool):
		"""
		:param line: mid tid word+
		:param buffer: to preserve unique words
		:param print_value: true to remain value part or ignore it
		:return: True if execution is created
		"""
		line = line.strip()
		if len(line) > 0:
			items = line.split('\t')
			mutant = self.project.mutant_space.get_mutant(int(items[0].strip()))
			self.mutants.add(mutant)
			tid = int(items[1].strip())
			if tid >= 0:
				test_case = self.project.test_space.get_test_case(tid)
				self.test_cases.add(test_case)
			else:
				test_case = None		# static symbolic analysis
			buffer.clear()
			for k in range(2, len(items)):
				word = items[k].strip()
				if len(word) > 0:
					word = self.__std_word__(word, print_value)
					buffer.add(word)
			execution = SymbolicExecution(self, mutant, test_case, buffer)
			self.executions.append(execution)
			return True
		else:
			return False

	def load(self, file_path: str, print_value: bool):
		"""
		:param file_path: xxx.sft or xxx.int.dft
		:param print_value: true to remain value part or ignore it
		:return: number of executions being loaded
		"""
		counter, buffer = 0, set()
		with open(file_path, 'r') as reader:
			for line in reader:
				if self.__add__(line, buffer, print_value):
					counter += 1
		return counter


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	for filename in os.listdir(root_path):
		directory_path = os.path.join(root_path, filename)
		c_project = CProject(directory_path, filename)
		print(filename, "contains", len(c_project.mutant_space.get_mutants()), "mutants and",
			  len(c_project.test_space.get_test_cases()), "test cases.")
		c_document = c_project.load_static_document(directory_path, True)
		print("\tLoad", len(c_document.get_executions()), "lines with", len(c_document.get_corpus()), "words...")
		for sym_exec in c_document.get_executions():
			sym_exec: SymbolicExecution
			for sym_condition in sym_exec.get_conditions():
				print("\t\t==>", sym_condition.get_feature(),
					  "\t", sym_condition.get_value(),
					  "\t", sym_condition.get_execution(),
					  "\t\"", sym_condition.get_location().get_cir_code(),
					  "\"\t{", sym_condition.get_parameter(), "}")
		print()

