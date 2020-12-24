"""
This file defines the mutation project data
"""


import os
import com.jcsa.mark.base as cbase
import com.jcsa.mark.code as ccode


class CProject:
	def __init__(self, directory: str, file_name: str):
		self.program = ccode.CProgram(directory, file_name)
		tst_file_path = os.path.join(directory, file_name + ".tst")
		mut_file_path = os.path.join(directory, file_name + ".mut")
		res_file_path = os.path.join(directory, file_name + ".res")
		sym_file_path = os.path.join(directory, file_name + ".sym")
		self.test_space = TestSpace(self, tst_file_path)
		self.sym_tree = cbase.SymTree(sym_file_path)
		self.mutant_space = MutantSpace(self, mut_file_path, res_file_path)
		return

	def load_static_document(self, directory: str):
		document = MutationFeatureDocument(self)
		for file_name in os.listdir(directory):
			if file_name.endswith(".sft"):
				document.load(os.path.join(directory, file_name))
				break
		return document

	def load_dynamic_document(self, directory: str):
		document = MutationFeatureDocument(self)
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
					parameter = cbase.CToken.parse(items[1].strip()).get_token_value()
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
	def __init__(self, mutant, mutation_class: str, mutation_operator: str, location: ccode.AstNode, parameter: cbase.CToken):
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
		return self.kill_string[test_id] == '1'

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
					mut_id = cbase.CToken.parse(items[0].strip()).get_token_value()
					mutation_class = items[1].strip()
					mutation_operator = items[2].strip()
					ast_id = cbase.CToken.parse(items[3].strip()).get_token_value()
					location = self.project.program.ast_tree.get_ast_node(ast_id)
					parameter = cbase.CToken.parse(items[4].strip())
					mutation = Mutation(None, mutation_class, mutation_operator, location, parameter)
					mutant = Mutant(self, mut_id, mutation)
					mutation.mutant = mutant
					mutant_dict[mutant.get_mut_id()] = mutant
		with open(mut_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mut_id = cbase.CToken.parse(items[0].strip()).get_token_value()
					mutant = mutant_dict[mut_id]
					cov_id = cbase.CToken.parse(items[5].strip()).get_token_value()
					wek_id = cbase.CToken.parse(items[6].strip()).get_token_value()
					sto_id = cbase.CToken.parse(items[7].strip()).get_token_value()
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


class MutationFeature:
	"""
	type execution location parameter
	"""
	def __init__(self, feature_type: str, execution: ccode.CirExecution, location: ccode.CirNode, parameter: cbase.SymNode):
		"""
		:param feature_type: const | annotation_type
		:param execution: where the feature is expected to hold
		:param location: where the feature is defined upon
		:param parameter: symbolic expression or None
		"""
		self.feature_type = feature_type
		self.execution = execution
		self.location = location
		self.parameter = parameter
		return

	def get_feature_type(self):
		return self.feature_type

	def get_execution(self):
		return self.execution

	def get_location(self):
		return self.location

	def get_parameter(self):
		return self.parameter

	def has_parameter(self):
		return self.parameter is not None

	@staticmethod
	def parse(project: CProject, feature_word: str):
		"""
		:param project:
		:param feature_word: [ type execution location parameter ]
		:return:
		"""
		items = feature_word.strip().split(' ')
		feature_type = items[1].strip()
		execution_token = cbase.CToken.parse(items[2].strip()).get_token_value()
		location_token = cbase.CToken.parse(items[3].strip()).get_token_value()
		parameter_token = cbase.CToken.parse(items[4].strip()).get_token_value()
		execution = project.program.function_call_graph.get_execution(execution_token[0], execution_token[1])
		location = project.program.cir_tree.get_cir_node(location_token)
		parameter = None
		if parameter_token is not None:
			parameter = project.sym_tree.get_sym_node(items[4].strip())
		return MutationFeature(feature_type, execution, location, parameter)

	def __str__(self):
		return self.feature_type + ":" + str(self.execution) + ":\"" + self.location.get_cir_code() + "\":" + str(self.parameter)


class MutationFeatureLine:
	def __init__(self, document, mutant: Mutant, test_case: TestCase, feature_words: list):
		document: MutationFeatureDocument
		self.document = document
		self.mutant = mutant
		self.test_case = test_case
		self.feature_words = feature_words
		return

	def get_document(self):
		return self.document

	def get_mutant(self):
		return self.mutant

	def get_test_case(self):
		return self.test_case

	def has_test_case(self):
		return self.test_case is not None

	def get_feature_words(self):
		return self.feature_words

	def get_feature_word(self, k: int):
		word = self.feature_words[k]
		word: str
		return word

	def generate_features(self):
		features = list()
		for word in self.feature_words:
			feature = MutationFeature.parse(self.document.project, word.strip())
			features.append(feature)
		return features


class MutationFeatureDocument:
	def __init__(self, project: CProject):
		self.project = project
		self.lines = list()
		self.corpus = set()
		return

	def get_project(self):
		return self.project

	def get_feature_lines(self):
		return self.lines

	def __add__(self, line):
		"""
		:param line:
		:return: return True if successfully append a line to the document
		"""
		line: str
		items = line.strip().split('\t')
		if len(items) > 2:
			mid = int(items[0].strip())
			tid = int(items[1].strip())
			mutant = self.project.mutant_space.get_mutant(mid)
			if tid < 0:
				test_case = None
			else:
				test_case = self.project.test_space.get_test_case(tid)
			words = list()
			for k in range(2, len(items)):
				word = items[k].strip()
				if len(word) > 0:
					words.append(items[k].strip())
					self.corpus.add(word)
			feature_line = MutationFeatureLine(self, mutant, test_case, words)
			self.lines.append(feature_line)
			return True
		return False

	def load(self, feature_file: str):
		with open(feature_file, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					self.__add__(line)
		return

	def clear(self):
		self.corpus.clear()
		self.lines.clear()
		return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	for fname in os.listdir(root_path):
		dir = os.path.join(root_path, fname)
		c_project = CProject(dir, fname)
		print(fname, "contains", len(c_project.mutant_space.get_mutants()), "mutants and",
			  len(c_project.test_space.get_test_cases()), "test cases.")
		docs = c_project.load_dynamic_document(dir)
		print("\tLoad", len(docs.get_feature_lines()), "lines with", len(docs.corpus), "words...")
		for line in docs.get_feature_lines():
			line: MutationFeatureLine
			features = line.generate_features()
			for feature in features:
				feature: MutationFeature
				print("\t\t==>", str(feature))
		print()

