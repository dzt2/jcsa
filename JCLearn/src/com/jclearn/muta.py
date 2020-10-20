"""
It defines the data model to describe mutation, feature and test input.
"""

import os
import src.com.jclearn.base as base
import src.com.jclearn.code as ccode


class CProject:
	def __init__(self, directory: str, name: str):
		tst_file_path = os.path.join(directory, name + ".tst")
		mut_file_path = os.path.join(directory, name + ".mut")
		self.program = ccode.CProgram(directory, name)
		self.test_space = TestSpace(self, tst_file_path)
		self.muta_space = MutantSpace(self, mut_file_path)
		return


class TestCase:
	"""
	test case defines the parameter used to execute program
	"""
	def __init__(self, space, parameter: str):
		space: TestSpace
		self.space = space
		self.parameter = parameter
		return

	def get_space(self):
		return self.space

	def get_parameter(self):
		return self.parameter


class TestSpace:
	def __init__(self, project: CProject, tst_file_path: str):
		self.test_cases = list()
		self.project = project
		test_cases_dict = dict()
		with open(tst_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					index = line.find(':')
					test_id = int(line[0: index].strip())
					parameter = line[index + 1:].strip()
					test_case = TestCase(self, parameter)
					test_cases_dict[test_id] = test_case
		for test_id in range(0, len(test_cases_dict)):
			self.test_cases.append(test_cases_dict[test_id])
		return

	def get_project(self):
		return self.project

	def get_test_cases(self):
		return self.test_cases

	def get_test_case(self, test_case_id: int):
		return self.test_cases[test_case_id]


class Mutant:
	def __init__(self, space, muta_class: str, muta_operator: str, location: ccode.AstNode, parameter):
		"""
		:param space: mutation space
		:param muta_class: the class of mutation operator
		:param muta_operator: mutation operator
		:param location: location where the mutation is seeded
		:param parameter: parameter to refine the mutation
		"""
		self.space = space
		self.muta_class = muta_class
		self.muta_operator = muta_operator
		self.location = location
		self.parameter = parameter
		return

	def get_space(self):
		return self.space

	def get_muta_class(self):
		return self.muta_class

	def get_muta_operator(self):
		return self.muta_operator

	def get_location(self):
		return self.location

	def get_parameter(self):
		return self.parameter


class MutantSpace:
	def __init__(self, project: CProject, mut_file_path: str):
		self.project = project
		self.mutants = list()
		mutants_dict = dict()
		with open(mut_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mutant_id = int(items[0].strip())
					muta_class = items[1].strip()
					muta_operator = items[2].strip()
					ast_node_id = base.CToken.parse(items[3].strip()).token_value
					location = self.project.program.ast_tree.ast_nodes[ast_node_id]
					parameter = base.CToken.parse(items[4].strip())
					mutant = Mutant(self, muta_class, muta_operator, location, parameter)
					mutants_dict[mutant_id] = mutant
		for mutant_id in range(0, len(mutants_dict)):
			self.mutants.append(mutants_dict[mutant_id])
		return

	def get_project(self):
		return self.project

	def get_mutants(self):
		return self.mutants

	def get_mutant(self, mutant_id: int):
		return self.mutants[mutant_id]


if __name__ == "__main__":
	root_directory = "/home/dzt2/Development/Data/features/"
	post_directory = "/home/dzt2/Development/Code/GitProject/jcsa/JCLearn/output"
	for file_name in os.listdir(root_directory):
		directory = os.path.join(root_directory, file_name)
		project = CProject(directory, file_name)
		output_directory = os.path.join(post_directory, file_name)
		if not os.path.exists(output_directory):
			os.mkdir(output_directory)
		print("Generate", len(project.muta_space.get_mutants()), "mutations for", file_name)
