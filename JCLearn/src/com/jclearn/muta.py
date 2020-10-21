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
	def __init__(self, space, test_id: int, parameter: str):
		space: TestSpace
		self.space = space
		self.test_id = test_id
		self.parameter = parameter
		return

	def get_space(self):
		return self.space

	def get_test_id(self):
		return self.test_id

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
					test_case = TestCase(self, test_id, parameter)
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


class Mutation:
	"""
	Mutation seeded in abstract syntactic tree is defined as:
	[space, muta_class, muta_operator, location, parameter] | {features, label}
	"""
	def __init__(self, space, mutation_id: int, muta_class: str, muta_operator: str, location: ccode.AstNode, parameter: base.CToken):
		"""
		:param space: mutation space where the mutant is created
		:param muta_class: the class of mutation operator
		:param muta_operator: mutation operator
		:param location: where the mutation is injected
		:param parameter: the parameter to refine the mutation
		"""
		space: MutantSpace
		self.space = space
		self.mutation_id = mutation_id
		self.muta_class = muta_class
		self.muta_operator = muta_operator
		self.location = location
		self.parameter = parameter
		self.features = None
		self.label = None
		return

	def get_space(self):
		"""
		:return: mutation space where the mutant is created
		"""
		return self.space

	def get_mutation_id(self):
		return self.mutation_id

	def get_muta_class(self):
		"""
		:return: the class of mutation operator
		"""
		return self.muta_class

	def get_muta_operator(self):
		"""
		:return: mutation operator
		"""
		return self.muta_operator

	def get_location(self):
		"""
		:return: where the mutation is injected
		"""
		return self.location

	def get_parameter(self):
		"""
		:return: the parameter to refine the mutation
		"""
		return self.parameter

	def get_features(self):
		"""
		:return: features to describe the mutation
		"""
		self.features: MutationFeatures
		return self.features

	def get_label(self):
		"""
		:return: the label to define the mutant
		"""
		self.label: int
		return self.label

	def set_features_and_label(self, features, label: int):
		"""
		:param features:
		:param label:
		:return: set the features-label of the mutation
		"""
		features: MutationFeatures
		self.features = features
		self.label = label
		return


class MutantSpace:
	"""
	It defines the mutation space
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

	def get_mutation(self, mutant_id: int):
		mutation = self.mutations[mutant_id]
		mutation: Mutation
		return mutation

	def __parse__(self, mut_file_path: str):
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
					ast_node_id: int
					location = self.project.program.ast_tree.ast_nodes[ast_node_id]
					parameter = base.CToken.parse(items[4].strip())
					mutation = Mutation(self, mutant_id, muta_class, muta_operator, location, parameter)
					mutants_dict[mutant_id] = mutation
		for mutant_id in range(0, len(mutants_dict)):
			self.mutations.append(mutants_dict[mutant_id])
		return

	def load_features_and_labels(self, file_path: str):
		"""
		:param file_path:
		:return: update the features and labels of each mutation in space
		"""
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if line.startswith("#summary"):
					items = line.split('\t')
					mutation = self.mutations[int(items[1].strip())]
					mutation: Mutation
					mutation.label = int(items[2].strip())
					features = MutationFeatures(mutation)
					mutation.features = features
				elif line.startswith("#word"):
					features.append(self.project.program, line)
		return


class MutationFeature:
	"""
	Each feature refers to a constraint or error at a specific location as:
	[feature_type, execution, location, parameter, word*, validity]
	"""
	def __init__(self, features, program: ccode.CProgram, line: str):
		"""
		:param program:
		:param line: #word validity feature_type execution location parameter word*
		"""
		features: MutationFeatures
		self.features = features
		items = line.strip().split('\t')
		self.validity = int(items[1].strip())
		self.feature_type = items[2].strip()
		self.execution = program.function_call_graph.get_execution_by_id(items[3].strip())
		cir_location_id = base.CToken.parse(items[4].strip()).token_value
		cir_location_id: int
		location = program.cir_tree.cir_nodes[cir_location_id]
		location: ccode.CirNode
		self.location = location
		self.parameter = items[5].strip()
		self.words = list()
		for k in range(6, len(items)):
			word = items[k].strip()
			if len(word) > 0 and word not in self.words:
				self.words.append(items[k].strip())
		self.words.sort()
		return

	def get_features(self):
		return self.features

	def get_feature_type(self):
		return self.feature_type

	def get_execution(self):
		return self.execution

	def get_location(self):
		return self.location

	def get_parameter(self):
		return self.parameter

	def get_feature_words(self):
		return self.words


class MutationFeatures:
	def __init__(self, mutation: Mutation):
		self.mutation = mutation
		self.features = list()
		return

	def get_mutation(self):
		return self.mutation

	def get_feature_list(self):
		return self.features

	def append(self, program: ccode.CProgram, line: str):
		if line.strip().startswith("#word"):
			feature = MutationFeature(self, program, line.strip())
			self.features.append(feature)
		return


def output_mutation_features(mspace: MutantSpace, output_file_path):
	with open(output_file_path, 'w') as writer:
		for mutation in mspace.mutations:
			mutation: Mutation
			writer.write("#Mutation\n")
			writer.write("\tOperator: " + mutation.muta_class + "<" + mutation.muta_operator + ">\n")
			writer.write("\tLocation: " + mutation.location.class_name + " at line " + str(mutation.location.get_line_of()))
			writer.write("\n\tCode: ")
			code = mutation.location.get_code()
			trim_code = ""
			for ch in code:
				if ch.isspace():
					ch = ' '
				trim_code += ch
			writer.write(trim_code + "\n")
			writer.write("\tParameter: " + str(mutation.parameter) + "\n")
			for feature in mutation.get_features().features:
				feature: MutationFeature
				writer.write("\t\t" + feature.feature_type)
				if feature.validity == 1:
					writer.write("\tSatisfied")
				elif feature.validity == 0:
					writer.write("\tInvalid")
				else:
					writer.write("\tUnknown")
				writer.write("\t" + str(feature.execution))
				writer.write("\t" + feature.parameter)
				writer.write("\n")
			writer.write("\n")
	return


if __name__ == "__main__":
	root_directory = "/home/dzt2/Development/Data/features/"
	post_directory = "/home/dzt2/Development/Code/GitProject/jcsa/JCLearn/output"
	for file_name in os.listdir(root_directory):
		directory = os.path.join(root_directory, file_name)
		c_project = CProject(directory, file_name)
		output_directory = os.path.join(post_directory, file_name)
		if not os.path.exists(output_directory):
			os.mkdir(output_directory)
		feature_label_file = os.path.join(directory, file_name + ".sfl")
		c_project.muta_space.load_features_and_labels(feature_label_file)
		output_mutation_features(c_project.muta_space, os.path.join(output_directory, file_name + ".mut"))
		print("Generate", len(c_project.muta_space.get_mutations()), "mutations for", file_name)
