"""
It implements the data model for mutation and test data.
"""

import os
import com.jcsa.pymuta.base as cbase
import com.jcsa.pymuta.code as ccode


class CProject:
	def __init__(self, directory: str, file_name: str):
		self.program = ccode.CProgram(directory, file_name)
		tst_file = os.path.join(directory, file_name + ".tst")
		mut_file = os.path.join(directory, file_name + ".mut")
		res_file = os.path.join(directory, file_name + ".res")
		self.test_space = TestSpace(self, tst_file)
		self.mutant_space = MutantSpace(self, mut_file, res_file)
		return

	def load_execution_document(self, fet_file_path: str):
		return MutantExecutionDocument(self, fet_file_path)


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


class MutantResult:
	"""
	test result of a mutant
	"""
	def __init__(self, mutant, kill_sequence: str):
		"""
		:param mutant:
		:param kill_sequence:
		"""
		mutant: Mutant
		self.mutant = mutant
		self.kill_sequence = kill_sequence.strip()
		return

	def get_mutant(self):
		return self.mutant

	def get_result(self):
		"""
		:return: 01-sequence for killing or not
		"""
		return self.kill_sequence

	def __str__(self):
		return self.kill_sequence

	def get_length(self):
		"""
		:return: length of the killing result
		"""
		return len(self.kill_sequence)

	def is_killed_by(self, test_case):
		"""
		:param test_case: TestCase or integer ID
		:return: true --> killed by test of specified ID or None if test_id out of range
		"""
		if isinstance(test_case, TestCase):
			test_case: TestCase
			test_id = test_case.get_test_id()
		else:
			test_case: int
			test_id = test_case
		return self.kill_sequence[test_id] == '1'

	def is_killed(self):
		"""
		:return: whether killed by any tests
		"""
		return '1' in self.kill_sequence

	def is_killed_in(self, test_cases):
		"""
		:param test_cases: collection of test-cases
		:return: whether the mutant is killed by the set of input test cases
		"""
		for test_case in test_cases:
			if self.is_killed_by(test_case):
				return True
		return False

	def get_degree(self):
		"""
		:return: number of tests that kill this mutant
		"""
		degree = 0
		for test_id in range(0, len(self.kill_sequence)):
			if self.is_killed_by(test_id):
				degree += 1
		return degree

	def get_killing_tests(self):
		"""
		:return: collection of test cases for killing this mutant
		"""
		project = self.mutant.get_space().get_project()
		project: CProject
		test_space = project.test_space
		test_cases = list()
		# print("\t\t==>", len(test_space.get_test_cases()), "tests against", len(self.kill_sequence), "results.")
		for test_id in range(0, len(self.kill_sequence)):
			if self.is_killed_by(test_id):
				test_case = test_space.get_test_case(test_id)
				test_cases.append(test_case)
		return test_cases

	def get_survive_tests(self):
		"""
		:return: collection of test cases that cannot kill this mutant
		"""
		project = self.mutant.get_space().get_project()
		project: CProject
		test_space = project.test_space
		test_cases = list()
		for test_id in range(0, len(self.kill_sequence)):
			if not(self.is_killed_by(test_id)):
				test_case = test_space.get_test_case(test_id)
				test_cases.append(test_case)
		return test_cases


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
		self.result = None			# test result
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
		self.result: MutantResult
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


class MutantSpace:
	def __init__(self, project: CProject, mut_file_path: str, res_file_path: str):
		"""
		:param project:
		:param mut_file_path:
		:param res_file_path:
		"""
		self.project = project
		self.mutants = list()
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
		with open(res_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mutant = self.get_mutant(int(items[0].strip()))
					result = MutantResult(mutant, items[1].strip())
					mutant.result = result
		return


class CirAnnotation:
	"""
	type, execution, location, parameter
	"""
	def __init__(self, annotation_type: str, execution: ccode.CirExecution, location: ccode.CirNode, parameter: cbase.CToken):
		self.annotation_type = annotation_type
		self.execution = execution
		self.location = location
		self.parameter = parameter
		return

	def get_type(self):
		return self.annotation_type

	def get_execution(self):
		return self.execution

	def get_location(self):
		return self.location

	def get_parameter(self):
		return self.parameter

	@staticmethod
	def parse(text: str, program: ccode.CProgram):
		"""
		:param text:
		:param program:
		:return:
		"""
		items = text.strip().split('$')
		annotation_type = items[0].strip()
		exe_id = cbase.CToken.parse(items[1].strip()).get_token_value()
		cir_id = cbase.CToken.parse(items[2].strip()).get_token_value()
		parameter = cbase.CToken.parse(items[3].strip())
		execution = program.function_call_graph.get_execution(exe_id[0], exe_id[1])
		location = program.cir_tree.get_cir_node(cir_id)
		return CirAnnotation(annotation_type, execution, location, parameter)


class MutantExecutionLine:
	"""
	mutant, test_case, words
	"""
	def __init__(self, mutant: Mutant, test_case: TestCase):
		self.mutant = mutant
		self.test_case = test_case
		self.words = list()
		return

	def get_mutant(self):
		return self.mutant

	def get_test_case(self):
		return self.test_case

	def has_test_case(self):
		return self.test_case is not None

	def get_words(self):
		return self.words

	def get_word(self, k: int):
		word = self.words[k]
		word: str
		return word

	def get_annotations(self):
		program = self.mutant.get_space().get_project().program
		annotations = list()
		for word in self.words:
			word: str
			word = word.strip()
			if len(word) > 0:
				annotations.append(CirAnnotation.parse(word, program))
		return annotations

	def __killed__(self, mutant: Mutant):
		if mutant.has_result():
			result = mutant.get_result()
			if self.test_case is None:
				return result.is_killed()
			return result.is_killed_by(self.test_case.get_test_id())
		return None

	def is_killed(self):
		return self.__killed__(self.mutant)

	def is_infected(self):
		return self.__killed__(self.mutant.get_weak_mutant())

	def is_covered(self):
		return self.__killed__(self.mutant.get_coverage_mutant())


class MutantExecutionDocument:
	def __init__(self, project: CProject, fet_file_path: str):
		self.project = project
		self.lines = list()
		self.corpus = dict()
		self.__parse__(fet_file_path)
		return

	def get_project(self):
		return self.project

	def get_lines(self):
		return self.lines

	def get_line(self, k: int):
		line = self.lines[k]
		line: MutantExecutionLine
		return line

	def get_length(self):
		"""
		:return: number of execution lines in the file
		"""
		return len(self.lines)

	def get_corpus(self):
		return self.corpus.keys()

	def __word__(self, word: str):
		if not(word in self.corpus):
			self.corpus[word] = word
		word = self.corpus[word]
		word: str
		return word

	def __parse__(self, fet_file_path: str):
		self.lines.clear()
		with open(fet_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mutant = self.project.mutant_space.get_mutant(int(items[0].strip()))
					test_id = int(items[1].strip())
					if test_id < 0:
						test_case = None
					else:
						test_case = self.project.test_space.get_test_case(test_id)
					exec_line = MutantExecutionLine(mutant, test_case)
					for k in range(2, len(items)):
						word = items[k].strip()
						if len(word) > 0:
							exec_line.words.append(self.__word__(word))
					self.lines.append(exec_line)
		return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	for file_name in os.listdir(root_path):
		directory = os.path.join(root_path, file_name)
		c_project = CProject(directory, file_name)
		docs = c_project.load_execution_document(os.path.join(directory, file_name + ".sft"))
		# print("Load", len(c_project.mutant_space.get_mutants()), "mutants from", file_name)
		# for c_mutant in c_project.mutant_space.get_mutants():
		# 	c_mutant: Mutant
		# 	print("\t{}\t{}\t{}\t{}\t\"{}\"\t{}".format(c_mutant.get_mut_id(), c_mutant.get_mutation().mutation_class,
		# 												c_mutant.get_mutation().get_mutation_operator(),
		# 												c_mutant.get_mutation().get_location().line_of(),
		# 												c_mutant.get_mutation().get_location().get_code(True),
		# 												c_mutant.get_mutation().get_parameter()))
		print("Load", docs.get_length(), "execution lines with", len(docs.corpus), "words from", file_name)
		print()

