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
		self.muta_space = MutationSpace(self, mut_file_path)
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
		space: MutationSpace
		self.space = space
		self.mutation_id = mutation_id
		self.muta_class = muta_class
		self.muta_operator = muta_operator
		self.location = location
		self.parameter = parameter
		self.features = None
		self.label = -1
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

	def has_features(self):
		"""
		:return: whether the mutation contains feature data model
		"""
		return self.features is not None

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

	def __str__(self):
		return self.muta_class + "::" + self.muta_operator + "[" + str(self.mutation_id) + "]"


class MutationSpace:
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


def output_mutation_features(mutation_space: MutationSpace, output_file_path):
	with open(output_file_path, 'w') as writer:
		for mutation in mutation_space.mutations:
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


def load_and_output_mutation_features(root_directory: str, post_directory: str):
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
	return


class MutationFeaturePattern:
	"""
	The pattern of each feature in mutation (MutationFeature) contains following element:
		[feature_type, location]::[word, word, ..., word]
	"""
	def __init__(self, feature_type: str, location: ccode.CirNode):
		"""
		create an empty pattern as [feature_type, location]::[]
		:param feature_type:
		:param location:
		"""
		self.feature_type = feature_type
		self.location = location
		self.feature_words = list()
		return

	def get_feature_type(self):
		"""
		:return: #cons|#flow|#trap|#expr|#refr|#stat
		"""
		return self.feature_type

	def get_feature_words(self):
		"""
		:return: the words that describe the mutation feature
		"""
		return self.feature_words

	def get_location(self):
		"""
		:return: the location where the feature is defined
		"""
		return self.location

	def child_pattern(self, feature_word: str):
		"""
		:param feature_word:
		:return: the child pattern extended from this pattern with using feature-word specified
		"""
		if feature_word in self.feature_words:
			return self
		child_pattern = MutationFeaturePattern(self.feature_type, self.location)
		for word in self.feature_words:
			child_pattern.feature_words.append(word)
		child_pattern.feature_words.append(feature_word)
		child_pattern.feature_words.sort()
		return child_pattern

	def match_feature(self, feature: MutationFeature):
		"""
		:param feature: feature being matched with the pattern
		:return: whether the pattern matches with the feature
		"""
		if self.feature_type == feature.get_feature_type() and self.location == feature.get_location():
			for word in self.feature_words:
				if word == feature.parameter or (word in feature.words):
					continue
				else:
					return False
			return True
		return False

	def match_features(self, features: MutationFeatures):
		"""
		:param features:
		:return: whether the mutation features match with this pattern
		"""
		for feature in features.get_feature_list():
			feature: MutationFeature
			if self.match_feature(feature):
				return True
		return False

	def match_mutation(self, mutation: Mutation):
		"""
		:param mutation:
		:return: whether the features of the mutation match with the pattern
		"""
		return self.match_features(mutation.get_features())

	def filter_mutations(self, mutations):
		"""
		:param mutations:
		:return: the set of mutations matching with this pattern
		"""
		good_mutations = list()
		for mutation in mutations:
			mutation: Mutation
			if self.match_mutation(mutation):
				good_mutations.append(mutation)
		return good_mutations

	def __str__(self):
		key = self.feature_type + ":" + str(self.location)
		return key + str(self.feature_words)

	def is_parent(self, pattern):
		"""
		:param pattern:
		:return: whether this pattern is the parent of the specified pattern
		"""
		pattern: MutationFeaturePattern
		if pattern.feature_type == self.feature_type and pattern.location == self.location:
			if len(self.feature_words) < len(pattern.feature_words):
				for word in self.feature_words:
					if word in pattern.feature_words:
						continue
					else:
						return False
				return True
			else:
				return False
		else:
			return False


def classify_and_count_mutations(mutations):
	"""
	:param mutations:
	:return: killed, alive, unknown, label, alive_rate
	"""
	killed, alive, unknown = 0, 0, 0
	for mutation in mutations:
		if mutation.label == 1:
			killed += 1
		elif mutation.label == 0:
			alive += 1
		else:
			unknown = 0
	if killed > alive:
		label = 1
	elif killed < alive:
		label = 0
	else:
		label = -1
	if alive == 0:
		alive_rate = 0.0
	else:
		alive_rate = alive / (killed + alive + 0.0)
	return killed, alive, unknown, label, alive_rate


class MutationFeaturePatterns:
	"""
	It generates the patterns of the mutations as well as their patterns in the program
	"""
	def __init__(self):
		self.patterns = dict()
		self.solutions = dict()
		self.feature_words = set()
		return

	def __unique_pattern__(self, pattern: MutationFeaturePattern):
		"""
		:param pattern:
		:return: the unique instance of the pattern in the space
		"""
		key = str(pattern)
		if key not in self.patterns:
			self.patterns[key] = pattern
		unique_pattern = self.patterns[key]
		unique_pattern: MutationFeaturePattern
		return unique_pattern

	def __root_pattern__(self, feature: MutationFeature):
		"""
		:param feature:
		:return: the unique instance of mutation patterns generated from feature as root without words
		"""
		return self.__unique_pattern__(MutationFeaturePattern(feature.feature_type, feature.location))

	def __child_pattern__(self, parent_pattern: MutationFeaturePattern, feature_word: str):
		"""
		:param parent_pattern:
		:param feature_word:
		:return: the unique instance of child-pattern extended from the parent using feature word
		"""
		return self.__unique_pattern__(parent_pattern.child_pattern(feature_word))

	def __extract_feature_words__(self, feature: MutationFeature):
		"""
		:param feature:
		:return: update the words in self.feature_words using the feature
		"""
		self.feature_words.clear()
		if feature.feature_type == "#cons":
			self.feature_words.add(feature.parameter)
		elif feature.feature_type == "#expr" or feature.feature_type == "#refr" or feature.feature_type == "#stat":
			self.feature_words.add(feature.parameter)
			for word in feature.words:
				self.feature_words.add(word)
		return

	def __generate__(self, parent_pattern: MutationFeaturePattern, mutations):
		"""
		:param parent_pattern:
		:param mutations:
		:return:
		"""
		if parent_pattern not in self.solutions:
			self.solutions[parent_pattern] = parent_pattern.filter_mutations(mutations)
		mutations = self.solutions[parent_pattern]      # get existing solution to parent
		killed, alive, unknown, label, alive_rate = classify_and_count_mutations(mutations)
		if killed > 0 and alive > 0:
			for feature_word in self.feature_words:
				if not (feature_word in parent_pattern.feature_words):
					child_pattern = self.__child_pattern__(parent_pattern, feature_word)
					self.__generate__(child_pattern, mutations)
		return

	def generate(self, mutations):
		"""
		:param mutations:
		:return: It generates the patterns and their mutations within the specified ones
		"""
		self.patterns = dict()
		self.solutions = dict()
		for mutation in mutations:
			mutation: Mutation
			for feature in mutation.get_features().get_feature_list():
				feature: MutationFeature
				self.__extract_feature_words__(feature)
				root_pattern = self.__root_pattern__(feature)
				self.__generate__(root_pattern, mutations)
		return self.solutions


class MutationPatternsSelection:
	"""
	It implements the algorithms to select mutation patterns for each alive ones.
	"""
	@staticmethod
	def extract_patterns_of_mutation(patterns, mutation: Mutation):
		"""
		:param patterns: set of generated patterns to the mutations they match with
		:param mutation: the mutation being matched
		:return: the set of patterns that match with the mutation as specified
		"""
		matched_patterns = set()
		for pattern in patterns:
			pattern: MutationFeaturePattern
			if pattern.match_mutation(mutation):
				matched_patterns.add(pattern)
		return matched_patterns

	@staticmethod
	def filter_patterns_of_alive_mutations(pattern_mutation_dict: dict, min_alive_number: int, min_alive_rate: float):
		"""
		:param pattern_mutation_dict: mapping from patterns generated to the mutations that they match with
		:param min_alive_number: the minimal number of alive mutations that matched by the pattern
		:param min_alive_rate: the minimal rate of alive mutations among the patterns being selected
		:return: the set of mutation patterns being selected using the specified parameters.
		"""
		selected_patterns = set()
		for pattern, mutations in pattern_mutation_dict.items():
			pattern: MutationFeaturePattern
			killed, alive, unknown, label, alive_rate = classify_and_count_mutations(mutations)
			if (alive >= min_alive_number) and (alive_rate >= min_alive_rate):
				selected_patterns.add(pattern)
		return selected_patterns

	@staticmethod
	def evaluate_alive_mutation_patterns(patterns, mutations, pattern_mutation_dict: dict):
		"""
		:param patterns: the set of selected patterns being evaluated
		:param mutations: the set of mutations being matched with the patterns
		:param pattern_mutation_dict: mapping from patterns to the mutations that they match with
		:return: alive_mutations, matched_mutations, common_mutations, precision, recall, pattern_rate
		"""
		alive_mutations, matched_mutations, common_mutations = set(), set(), set()
		for mutation in mutations:
			mutation: Mutation
			if mutation.label == 0:
				alive_mutations.add(mutation)
		for pattern in patterns:
			pattern: MutationFeaturePattern
			for mutation in pattern_mutation_dict[pattern]:
				mutation: Mutation
				matched_mutations.add(mutation)
				if mutation in alive_mutations:
					common_mutations.add(mutation)
		precision = (len(common_mutations) + 0.0) / len(matched_mutations)
		recall = (len(common_mutations) + 0.0) / len(alive_mutations)
		pattern_rate = (len(patterns) + 0.0) / len(alive_mutations)
		return alive_mutations, matched_mutations, common_mutations, precision, recall, pattern_rate

	@staticmethod
	def minimize_mutation_patterns(patterns: set):
		"""
		:param patterns:
		:return: the minimal set of mutation patterns without children
		"""
		min_patterns, remove_patterns = set(), set()
		while len(patterns) > 0:
			''' select one root pattern in the set '''
			root_pattern = None
			for pattern in patterns:
				pattern: MutationFeaturePattern
				if root_pattern is None:
					root_pattern = pattern
				elif pattern.is_parent(root_pattern):
					root_pattern = pattern
			if root_pattern is None:
				break
			''' remove those as the children of root '''
			min_patterns.add(root_pattern)
			remove_patterns.clear()
			for pattern in patterns:
				if pattern == root_pattern or root_pattern.is_parent(pattern):
					remove_patterns.add(pattern)
			for pattern in remove_patterns:
				patterns.remove(pattern)
		return min_patterns

	@staticmethod
	def select_best_mutation_pattern(pattern_mutation_dict: dict, mutation: Mutation, min_alive_number: int, min_alive_rate: float):
		"""
		:param pattern_mutation_dict: mapping from patterns generated to the mutations that they match with
		:param mutation: the mutation being matched
		:param min_alive_number: the minimal rate of alive mutations among the patterns being selected
		:param min_alive_rate: the minimal rate of alive mutations among the patterns being selected
		:return: the best pattern matched with the mutation under the limited range (with maximal number)
		"""
		patterns = MutationPatternsSelection.extract_patterns_of_mutation(pattern_mutation_dict.keys(), mutation)
		filtered_patterns = set()
		for pattern in patterns:
			pattern: MutationFeaturePattern
			killed, alive, unknown, label, alive_rate = classify_and_count_mutations(pattern_mutation_dict[pattern])
			if alive >= min_alive_number and alive_rate >= min_alive_rate:
				filtered_patterns.add(pattern)
		minimal_filtered_patterns = MutationPatternsSelection.minimize_mutation_patterns(filtered_patterns)
		best_alive_number, best_patterns = 0, set()
		for pattern in minimal_filtered_patterns:
			pattern: MutationFeaturePattern
			killed, alive, unknown, label, alive_rate = classify_and_count_mutations(pattern_mutation_dict[pattern])
			if alive > best_alive_number:
				best_patterns.clear()
				best_alive_number = alive
				best_patterns.add(pattern)
			elif alive == best_alive_number:
				best_patterns.add(pattern)
		best_alive_rate, best_pattern = 0.0, None
		for pattern in best_patterns:
			pattern: MutationFeaturePattern
			killed, alive, unknown, label, alive_rate = classify_and_count_mutations(pattern_mutation_dict[pattern])
			if alive_rate > best_alive_rate:
				best_alive_rate = alive_rate
				best_pattern = pattern
		return best_pattern


def generate_and_write_alive_patterns(root_directory: str, post_directory: str):
	"""
	:param root_directory:
	:param post_directory:
	:return: the mutation patterns selected from entire program with limited parameters as filtered
	"""
	''' 1. declaration of parameters for selecting patterns '''
	min_alive_number, min_alive_rate = 2, 0.5
	for file_name in os.listdir(root_directory):
		print("+-------------------------------------------------------------------------+")
		''' 2.A load mutation, features and labels to memory '''
		directory = os.path.join(root_directory, file_name)
		c_project = CProject(directory, file_name)
		output_directory = os.path.join(post_directory, file_name)
		if not os.path.exists(output_directory):
			os.mkdir(output_directory)
		feature_label_file = os.path.join(directory, file_name + ".sfl")
		c_project.muta_space.load_features_and_labels(feature_label_file)
		print("1. Load", len(c_project.muta_space.get_mutations()), "mutations from", file_name)

		''' 2.B select the available mutation patterns for analysis and evaluation '''
		pattern_mutation_dict = MutationFeaturePatterns().generate(c_project.muta_space.get_mutations())
		patterns = MutationPatternsSelection.filter_patterns_of_alive_mutations(pattern_mutation_dict, min_alive_number, min_alive_rate)
		patterns = MutationPatternsSelection.minimize_mutation_patterns(patterns)
		alive_mutations, matched_mutations, common_mutations, precision, recall, pattern_rate = MutationPatternsSelection.evaluate_alive_mutation_patterns(patterns, c_project.muta_space.get_mutations(), pattern_mutation_dict)
		print("2. Select", len(patterns), "patterns with P =", int(100 * precision), "%, R =", int(100 * recall), "% and C =", int(100 * pattern_rate), "%.")

		''' 2.C write the selected mutation patterns to the file '''
		pattern_file_path = os.path.join(output_directory, file_name + ".ptn")
		with open(pattern_file_path, 'w') as writer:
			writer.write("Label\tKilled\tAlive\tType\tLocation\tWords\tLine\tCode\tMutations\n")
			for pattern in patterns:
				pattern: MutationFeaturePattern
				mutations = pattern_mutation_dict[pattern]
				killed, alive, unknown, label, alive_rate = classify_and_count_mutations(mutations)
				''' label, killed, alive '''
				writer.write(str(label) + "\t" + str(killed) + "\t" + str(alive))
				writer.write("\t" + pattern.feature_type)
				writer.write("\t" + str(pattern.location))
				writer.write("\t" + str(pattern.feature_words))
				ast_node = pattern.get_location().get_ast_source()
				if ast_node is not None:
					writer.write("\t" + str(ast_node.get_line_of() + 1))
					code = ast_node.get_code(True)
					if len(code) > 96:
						code = code[0: 96]
					writer.write("\t\"" + code + "\"")
				else:
					writer.write("\t\t")
				writer.write("\t[ ")
				for mutation in mutations:
					writer.write(str(mutation) + "; ")
				writer.write("]")
				writer.write("\n")
			writer.write("\n")
		print("3. Write mutation patterns to", pattern_file_path)
		print("+-------------------------------------------------------------------------+\n")
	return


def generate_best_pattern_of_mutations(root_directory: str, post_directory: str):
	"""
	:param root_directory:
	:param post_directory:
	:return: the alive mutation and the best pattern that it matches with
	"""
	''' 1. declaration of parameters for selecting patterns '''
	min_alive_number, min_alive_rate = 1, 0.50
	for file_name in os.listdir(root_directory):
		print("+-------------------------------------------------------------------------+")
		''' 2.A load mutation, features and labels to memory '''
		directory = os.path.join(root_directory, file_name)
		c_project = CProject(directory, file_name)
		output_directory = os.path.join(post_directory, file_name)
		if not os.path.exists(output_directory):
			os.mkdir(output_directory)
		feature_label_file = os.path.join(directory, file_name + ".sfl")
		c_project.muta_space.load_features_and_labels(feature_label_file)
		print("1. Load", len(c_project.muta_space.get_mutations()), "mutations from", file_name)

		''' 2.B generate the mutations patterns for program '''
		pattern_mutation_dict = MutationFeaturePatterns().generate(c_project.muta_space.get_mutations())
		print("2. Generate", len(pattern_mutation_dict), "patterns for mutations in program.")

		''' 2.C. select the best patterns for each alive mutation and write them to file '''
		mutation_file_path = os.path.join(output_directory, file_name + ".mnt")
		with open(mutation_file_path, 'w') as writer:
			writer.write("ID\tClass\tOprt\tLine\tCode\tParam\tType\tLocation\tWords\tKilled\tAlive\tRate\n")
			for mutation in c_project.muta_space.get_mutations():
				mutation: Mutation
				if mutation.label == 0:
					writer.write(str(mutation.get_mutation_id()))
					writer.write("\t" + mutation.get_muta_class())
					writer.write("\t" + mutation.get_muta_operator())
					writer.write("\t" + str(mutation.get_location().get_line_of() + 1))
					code = mutation.get_location().get_code(True)
					if len(code) > 64:
						code = code[0: 64]
					writer.write("\t" + code)
					writer.write("\t" + str(mutation.get_parameter()))
					best_pattern = MutationPatternsSelection.select_best_mutation_pattern(pattern_mutation_dict, mutation, min_alive_number, min_alive_rate)
					if best_pattern is None:
						writer.write("\t\t\t\t0\t0\t0.0%")
					else:
						writer.write("\t" + best_pattern.get_feature_type())
						writer.write("\t" + str(best_pattern.get_location()))
						writer.write("\t" + str(best_pattern.get_feature_words()))
						killed, alive, unknown, label, alive_rate = classify_and_count_mutations(pattern_mutation_dict[best_pattern])
						writer.write("\t" + str(killed) + "\t" + str(alive) + "\t" + str(int(alive_rate * 100)) + "%")
					writer.write("\n")
		print("3. Write best patterns for alive mutations to", mutation_file_path)
	return


if __name__ == "__main__":
	root_dir = "/home/dzt2/Development/Data/features/"
	post_dir = "/home/dzt2/Development/Code/GitProject/jcsa/JCLearn/output"
	generate_best_pattern_of_mutations(root_dir, post_dir)
	generate_and_write_alive_patterns(root_dir, post_dir)

