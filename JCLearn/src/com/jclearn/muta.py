"""
It defines the features, labels and data models to describe Mutant.
"""

import os
import src.com.jclearn.base as cbase
import src.com.jclearn.code as ccode


class CProject:
	"""
	It maintains the data used for mutation analysis.
	"""
	def __init__(self, directory_path: str, name: str):
		self.program = ccode.CProgram(directory_path, name)
		tst_file_path = os.path.join(directory_path, name + ".tst")
		mut_file_path = os.path.join(directory_path, name + ".mut")
		self.test_space = TestSpace(self, tst_file_path)
		self.muta_space = MutationSpace(self, mut_file_path)
		self.conditions = MutationConditions(self)
		return


class TestCase:
	"""
	Test case defines the parameters used for executing program under test.
	"""
	def __init__(self, space, test_id: int, parameter: str):
		"""
		:param space: the space where the test case is created
		:param test_id: the integer ID of the test case
		:param parameter: the command parameter to run the code
		"""
		space: TestSpace
		self.space = space
		self.test_id = test_id
		self.parameter = parameter
		return

	def get_space(self):
		"""
		:return: the space where the test case is created
		"""
		return self.space

	def get_test_id(self):
		"""
		:return: the integer ID of the test case
		"""
		return self.test_id

	def get_parameter(self):
		"""
		:return: the command parameter to run the code
		"""
		return self.parameter


class TestSpace:
	"""
	The space of test cases in the project
	"""
	def __init__(self, project: CProject, tst_file_path: str):
		self.test_cases = list()
		self.project = project
		self.__parse__(tst_file_path)
		return

	def get_project(self):
		return self.project

	def get_test_cases(self):
		return self.test_cases

	def get_test_case(self, test_id: int):
		"""
		:param test_id:
		:return: the test case in the space w.r.t. the ID
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
					index = line.find(':')
					test_id = int(line[0: index].strip())
					parameter = line[index+1:].strip()
					test_case = TestCase(self, test_id, parameter)
					test_case_dict[test_id] = test_case
		for test_id in range(0, len(test_case_dict)):
			self.test_cases.append(test_case_dict[test_id])
		return


class Mutation:
	"""
	The data model of mutation is:
		[space, mutation_id, class, operator, location, parameter]
	"""
	def __init__(self, space, mutation_id: int, muta_class: str, muta_operator: str, location: ccode.AstNode, parameter: cbase.CToken):
		"""
		:param space: the space where the mutation is created
		:param mutant_id: the integer ID of the mutation in its space
		:param muta_class: the class of mutation operator
		:param muta_operator: mutation operator
		:param location: where the mutation is injected
		:param parameter: to refine the mutation being executed
		"""
		space: MutationSpace
		self.space = space
		self.mutation_id =mutation_id
		self.muta_class = muta_class
		self.muta_operator = muta_operator
		self.location = location
		self.parameter = parameter
		self.conditions = list()
		self.label = -1
		return

	def get_space(self):
		return self.space

	def get_mutation_id(self):
		return self.mutation_id

	def get_mutation_class(self):
		return self.muta_class

	def get_mutation_operator(self):
		return self.muta_operator

	def get_location(self):
		return self.location

	def get_parameter(self):
		return self.parameter

	def has_mutation_conditions(self):
		"""
		:return: whether the mutation contains feature data model
		"""
		return len(self.conditions) > 0

	def get_mutation_conditions(self):
		"""
		:return: features to describe the mutation
		"""
		return self.conditions

	def get_label(self):
		"""
		:return: the label to define the mutant
		"""
		self.label: int
		return self.label

	def set_conditions_and_label(self, conditions, label: int):
		"""
		:param conditions:
		:param label:
		:return: set the features-label of the mutation
		"""
		self.conditions.clear()
		self.label = label
		for condition in conditions:
			condition: MutationCondition
			self.conditions.append(condition)
		return

	def __str__(self):
		return self.muta_class + "::" + self.muta_operator + "[" + str(self.mutation_id) + "]"


class MutationSpace:
	"""
	The space of mutations created under the program
	"""
	def __init__(self, project: CProject, mut_file_path: str):
		self.project = project
		self.mutations = list()
		self.__parse__(mut_file_path)
		return

	def get_project(self):
		return self.project

	def get_mutations(self):
		return self.mutations

	def get_mutation(self, mutation_id: int):
		mutation = self.mutations[mutation_id]
		mutation: Mutation
		return mutation

	def __parse__(self, mut_file_path: str):
		mutation_dict = dict()
		with open(mut_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mutation_id = int(items[0].strip())
					muta_class = items[1].strip()
					muta_operator = items[2].strip()
					ast_node_id = cbase.CToken.parse(items[3].strip()).token_value
					ast_node_id: int
					location = self.project.program.ast_tree.ast_nodes[ast_node_id]
					location: ccode.AstNode
					parameter = cbase.CToken.parse(items[4].strip())
					mutation = Mutation(self, mutation_id, muta_class, muta_operator, location, parameter)
					mutation_dict[mutation_id] = mutation
		for mutation_id in range(0, len(mutation_dict)):
			self.mutations.append(mutation_dict[mutation_id])
		return


class MutationCondition:
	"""
	It describes the conditions required for killing a mutation as:
	[label, type, execution, location, parameters]
	"""
	def __init__(self, label: int, condition_type: str, execution: ccode.CirExecution, location: ccode.CirNode):
		"""
		:param label: whether the condition is satisfied in testing (YES-1, NO-0, Unknown-{-1})
		:param condition_type: #cons|#trap|#flow|#expr|#stat|#refr
		:param execution:
		:param location:
		"""
		self.label = label
		self.condition_type = condition_type
		self.execution = execution
		self.location = location
		self.parameters = list()
		return

	def get_label(self):
		return self.label

	def get_type(self):
		return self.condition_type

	def get_execution(self):
		return self.execution

	def get_location(self):
		return self.location

	def get_parameters(self):
		return self.parameters

	def __str__(self):
		key = str(self.label) + "\t" + str(self.condition_type) + "\t" + str(self.execution) + "\t" + str(self.location.cir_node_id)
		for parameter in self.parameters:
			key += "\t" + str(parameter)
		return key


class MutationConditions:
	"""
	Used to create mutation conditions in the program
	"""
	def __init__(self, project: CProject):
		self.project = project
		self.conditions = dict()
		return

	def get_condition(self, line: str):
		"""
		:param line: #word label type execution location parameter*
		:return: None if the line is not matched with the formatted
		"""
		line = line.strip()
		if line.startswith("#word"):
			items = line.split('\t')
			label = int(items[1].strip())
			ctype = items[2].strip()
			execution = self.project.program.function_call_graph.get_execution_by_id(items[3].strip())
			location_id = cbase.CToken.parse(items[4].strip()).token_value
			location_id: int
			location = self.project.program.cir_tree.cir_nodes[location_id]
			location: ccode.CirNode
			condition = MutationCondition(label, ctype, execution, location)
			for k in range(5, len(items)):
				parameter = items[k].strip()
				if len(parameter) > 0:
					condition.parameters.append(parameter)
			condition.parameters.sort()
			key = str(condition)
			if key not in self.conditions:
				self.conditions[key] = condition
			unique_condition = self.conditions[key]
			unique_condition: MutationCondition
			return unique_condition
		return None

	def load_features_and_labels(self, fet_file_path: str):
		with open(fet_file_path, 'r') as reader:
			conditions = set()
			for line in reader:
				line = line.strip()
				if line.startswith("#summary"):
					items = line.split('\t')
					mutation = self.project.muta_space.get_mutation(int(items[1].strip()))
					label = int(items[2].strip())
				elif line.startswith("#word"):
					condition = self.get_condition(line)
					condition: MutationCondition
					conditions.add(condition)
				elif line.startswith("#EndMutation"):
					mutation.set_conditions_and_label(conditions, label)
				elif line.startswith("#BegMutation"):
					conditions.clear()
				else:
					pass
		return


if __name__ == "__main__":
	root_dir = "/home/dzt2/Development/Data/features/"
	post_dir = "/home/dzt2/Development/Data/results/"
	for file_name in os.listdir(root_dir):
		directory = os.path.join(root_dir, file_name)
		c_project = CProject(directory, file_name)
		fet_file_path = os.path.join(directory, file_name + ".sfl")
		c_project.conditions.load_features_and_labels(fet_file_path)
		print("Project", file_name, ":", len(c_project.test_space.test_cases), "tests and", len(c_project.muta_space.mutations), "mutations.")

