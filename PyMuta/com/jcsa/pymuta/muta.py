"""
It describes the model for mutation and test cases along with their features and results.
"""

import os
import com.jcsa.pymuta.base as base
import com.jcsa.pymuta.code as ccode


class CProject:
	def __init__(self, directory: str, name: str):
		self.program = ccode.CProgram(directory, name)
		tst_file_path = os.path.join(directory, name + ".tst")
		mut_file_path = os.path.join(directory, name + ".mut")
		res_file_path = os.path.join(directory, name + ".res")
		self.test_space = TestCaseSpace(self, tst_file_path)
		self.muta_space = MutationSpace(self, mut_file_path)
		self.muta_space.load_mutation_results(res_file_path)
		return


class TestCase:
	def __init__(self, space, test_id: int, parameter: str):
		"""
		:param space: space where the test case is created
		:param test_id: the integer ID of the test case in its space
		:param parameter: the parameter used to execute command
		"""
		space: TestCaseSpace
		self.space = space
		self.test_id = test_id
		self.parameter = parameter
		return

	def get_test_space(self):
		return self.space

	def get_test_id(self):
		return self.test_id

	def get_parameter(self):
		return self.parameter


class TestCaseSpace:
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
		test_case = self.test_cases[test_id]
		test_case: TestCase
		return test_case

	def __parse__(self, tst_file_path: str):
		test_case_dict = dict()
		with open(tst_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				items = line.split('\t')
				if len(line) > 0:
					test_id = int(items[0].strip())
					parameter = base.CToken.parse(items[1].strip())
					test_case = TestCase(self, test_id, parameter.token_value)
					test_case_dict[test_case.test_id] = test_case
		self.test_cases.clear()
		for k in range(0, len(test_case_dict)):
			self.test_cases.append(test_case_dict[k])
		return


class MutationResult:
	def __init__(self, mutation):
		mutation: Mutation
		self.mutation = mutation
		self.results = ""
		return

	def get_mutation(self):
		return self.mutation

	def get_results(self):
		return self.results

	def set_results(self, results: str):
		self.results = results
		return

	def clear(self):
		self.results = ""
		return

	def get_degrees(self):
		degree = 0
		for k in range(0, len(self.results)):
			if self.results[k] == "1":
				degree += 1
		return degree

	def is_killed_by(self, test_id: int):
		"""
		:param test_id:
		:return: true if the mutation is killed by this test
		"""
		return self.results[test_id] == "1"

	def is_killed(self):
		"""
		:return: true if the mutation is killed by at least one test case
		"""
		for k in range(0, len(self.results)):
			result = self.results[k]
			if result == "1":
				return True
		return False

	def get_kill_test_cases(self):
		"""
		:return: the set of test cases killing this mutation
		"""
		test_space = self.mutation.space.project.test_space
		test_cases = list()
		for test_id in range(0, len(self.results)):
			result = self.results[test_id]
			if result == "1":
				test_case = test_space.get_test_case(test_id)
				test_cases.append(test_case)
		return test_cases

	def is_covered_by(self, test_id: int):
		return self.mutation.get_coverage_mutation().get_result().is_killed_by(test_id)

	def is_covered(self):
		return self.mutation.get_coverage_mutation().get_result().is_killed()

	def is_infected_by(self, test_id: int):
		return self.mutation.get_weakness_mutation().get_result().is_killed_by(test_id)

	def is_infected(self):
		return self.mutation.get_weakness_mutation().get_result().is_killed()


class Mutation:
	def __init__(self, space, muta_id: int, muta_class: str, muta_operator: str, location: ccode.AstNode, parameter: base.CToken):
		space: MutationSpace
		self.space = space
		self.muta_id = muta_id
		self.muta_class = muta_class
		self.muta_operator = muta_operator
		self.location = location
		self.parameter = parameter
		self.cov_mutation = None
		self.wek_mutation = None
		self.str_mutation = None
		self.result = MutationResult(self)
		return

	def get_mutation_space(self):
		return self.space

	def get_muta_id(self):
		return self.muta_id

	def get_muta_class(self):
		return self.muta_class

	def get_muta_operator(self):
		return self.muta_operator

	def get_location(self):
		return self.location

	def get_parameter(self):
		return self.parameter

	def get_coverage_mutation(self):
		self.cov_mutation: Mutation
		return self.cov_mutation

	def get_weakness_mutation(self):
		self.wek_mutation: Mutation
		return self.wek_mutation

	def get_stronger_mutation(self):
		self.str_mutation: Mutation
		return self.str_mutation

	def get_result(self):
		return self.result


class MutationSpace:
	def __init__(self, project: CProject, mut_file_path: str):
		self.project = project
		self.mutations = list()
		self.__parse__(mut_file_path)
		return

	def get_project(self):
		return self.project

	def get_mutations(self):
		return self.mutations

	def get_mutation(self, muta_id: int):
		mutation = self.mutations[muta_id]
		mutation: Mutation
		return mutation

	def __parse__(self, mut_file_path: str):
		mutation_dict = dict()
		ast_tree = self.project.program.ast_tree
		with open(mut_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					muta_id = base.CToken.parse(items[0].strip()).token_value
					muta_class = items[1].strip()
					muta_operator = items[2].strip()
					ast_key = base.CToken.parse(items[3].strip())
					location = ast_tree.get_ast_node(ast_key.get_token_value())
					parameter = base.CToken.parse(items[4].strip())
					mutation = Mutation(self, muta_id, muta_class, muta_operator, location, parameter)
					mutation_dict[mutation.muta_id] = mutation
					subsume_keys = items[5].strip().split(' ')
					mutation.cov_mutation = base.CToken.parse(subsume_keys[1].strip()).token_value
					mutation.wek_mutation = base.CToken.parse(subsume_keys[2].strip()).token_value
					mutation.str_mutation = base.CToken.parse(subsume_keys[3].strip()).token_value
		self.mutations.clear()
		for k in range(0, len(mutation_dict)):
			mutation = mutation_dict[k]
			self.mutations.append(mutation)
			mutation.cov_mutation = mutation_dict[mutation.cov_mutation]
			mutation.wek_mutation = mutation_dict[mutation.wek_mutation]
			mutation.str_mutation = mutation_dict[mutation.str_mutation]
		return

	def load_mutation_results(self, res_file_path: str):
		with open(res_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mutation = self.get_mutation(int(items[0].strip()))
					mutation.get_result().set_results(items[1].strip())
		return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	for file_name in os.listdir(root_path):
		directory = os.path.join(root_path, file_name)
		c_project = CProject(directory, file_name)
		print("Load", len(c_project.muta_space.mutations), "mutations and", len(c_project.test_space.test_cases), "tests for", file_name)
		for c_mutation in c_project.muta_space.get_mutations():
			c_mutation: Mutation
			print("\t", file_name, c_mutation.muta_id, c_mutation.muta_class, c_mutation.muta_operator,
				  c_mutation.get_result().is_covered(), c_mutation.get_result().is_infected(),
				  c_mutation.get_result().is_killed(), "--> ", c_mutation.get_coverage_mutation().muta_id,
				  c_mutation.get_weakness_mutation().muta_id, c_mutation.get_stronger_mutation().muta_id)

