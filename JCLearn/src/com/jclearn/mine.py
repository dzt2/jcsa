"""
It implements the algorithms to mine the association rules between mutation cnditions
and the equivalence (label).
"""

import os
import src.com.jclearn.code as ccode
import src.com.jclearn.muta as cmute


class MutationConditionPattern:
	"""
	The pattern of mutation condition is defined as {type, location, parameters*}
	"""
	def __init__(self, condition_type: str, location: ccode.CirNode):
		"""
		create a root pattern as [type, location]
		:param condition_type:
		:param location:
		"""
		self.condition_type = condition_type
		self.location = location
		self.parameters = list()
		return

	def get_type(self):
		return self.condition_type

	def get_location(self):
		return self.location

	def get_parameters(self):
		return self.parameters

	def __str__(self):
		key = self.condition_type + ":" + str(self.location.cir_node_id)
		return key + str(self.parameters)

	def new_child_pattern(self, parameter: str):
		"""
		:param parameter:
		:return:
		"""
		if parameter not in self.parameters:
			child_pattern = MutationConditionPattern(self.condition_type, self.location)
			for old_parameter in self.parameters:
				child_pattern.parameters.append(old_parameter)
			child_pattern.parameters.append(parameter)
			child_pattern.parameters.sort()
			return child_pattern
		return self

	def match_condition(self, condition: cmute.MutationCondition):
		"""
		:param condition:
		:return: whether the condition matches with this pattern
		"""
		if condition.condition_type == self.condition_type and condition.location == self.location:
			for parameter in self.parameters:
				if not (parameter in condition.parameters):
					return False
			return True
		return False

	def match_mutation(self, mutation: cmute.Mutation):
		for condition in mutation.conditions:
			condition: cmute.MutationCondition
			if self.match_condition(condition):
				return True
		return False

	def filter_mutations(self, mutations):
		"""
		:param mutations:
		:return: set of mutations matching with this pattern
		"""
		matched_mutations = list()
		for mutation in mutations:
			mutation: cmute.Mutation
			if self.match_mutation(mutation):
				matched_mutations.append(mutation)
		return matched_mutations

	def is_parent(self, pattern):
		child_pattern: MutationConditionPattern
		if pattern.condition_type == self.condition_type and pattern.location == self.location:
			if len(self.parameters) < len(pattern.parameters):
				for word in self.parameters:
					if word in pattern.parameters:
						continue
					else:
						return False
				return True
			else:
				return False
		else:
			return False


def count_mutation_labels(mutations):
	"""
	:param mutations:
	:return: killed, alive, unknown, alive_rate, label
	"""
	killed, alive, unknown = 0, 0, 0
	for mutation in mutations:
		mutation: cmute.Mutation
		if mutation.label == 1:
			killed += 1
		elif mutation.label == 0:
			alive += 1
		else:
			unknown += 1
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
	return killed, alive, unknown, alive_rate, label


class MutationConditionPatterns:
	"""
	pattern space of mutation conditions
	"""
	def __init__(self):
		self.patterns = dict()
		self.solutions = dict()
		return

	def __unique_pattern__(self, pattern: MutationConditionPattern):
		"""
		:param pattern:
		:return: the unique instance of mutation patterns
		"""
		key = str(pattern)
		if key not in self.patterns:
			self.patterns[key] = pattern
		unique_pattern = self.patterns[key]
		unique_pattern: MutationConditionPattern
		return unique_pattern

	def __init_pattern__(self, condition: cmute.MutationCondition):
		"""
		:param condition:
		:return: initial pattern w.r.t. the condition
		"""
		return self.__unique_pattern__(MutationConditionPattern(condition.get_type(), condition.get_location()))

	def __child_pattern__(self, parent_pattern: MutationConditionPattern, parameter: str):
		child_pattern = parent_pattern.new_child_pattern(parameter)
		return self.__unique_pattern__(child_pattern)

	def __generate__(self, pattern: MutationConditionPattern, mutations, parameters):
		"""
		:param pattern:
		:param parameters:
		:return: generate the patterns under the pattern of parent.
		"""
		if not (pattern in self.solutions):
			self.solutions[pattern] = pattern.filter_mutations(mutations)
		mutations = self.solutions[pattern]
		killed, alive, unknown, alive_rate, label = count_mutation_labels(mutations)
		if (killed > 0) and (alive > 0):
			for parameter in parameters:
				if not (parameter in pattern.parameters):
					child_pattern = self.__child_pattern__(pattern, parameter)
					self.__generate__(child_pattern, mutations, parameters)
		return

	@staticmethod
	def __extract_parameters__(condition: cmute.MutationCondition):
		parameters = set()
		if condition.get_type() in ["#cons", "#expr", "#refr", "#stat"]:
			for parameter in condition.get_parameters():
				parameter: str
				parameters.add(parameter.strip())
		return parameters

	def generate_alive_patterns(self, project: cmute.CProject):
		"""
		:param project:
		:return: mapping from pattern to the mutations matching with it
		"""
		self.patterns = dict()
		self.solutions = dict()
		for mutation in project.muta_space.mutations:
			mutation: cmute.Mutation
			if mutation.label == 0:
				for condition in mutation.conditions:
					condition: cmute.MutationCondition
					root_pattern = self.__init_pattern__(condition)
					parameters = MutationConditionPatterns.__extract_parameters__(condition)
					self.__generate__(root_pattern, project.muta_space.mutations, parameters)
		return self.solutions


class MutationConditionPatternsSelection:
	"""
	Used to select useful mutation conditions in patterns
	"""

	@staticmethod
	def extract_patterns_of_mutation(patterns, mutation: cmute.Mutation):
		"""
		:param patterns: set of generated patterns to the mutations they match with
		:param mutation: the mutation being matched
		:return: the set of patterns that match with the mutation as specified
		"""
		matched_patterns = set()
		for pattern in patterns:
			pattern: MutationConditionPattern
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
			pattern: MutationConditionPattern
			killed, alive, unknown, alive_rate, label = count_mutation_labels(mutations)
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
			mutation: cmute.Mutation
			if mutation.label == 0:
				alive_mutations.add(mutation)
		for pattern in patterns:
			pattern: MutationConditionPattern
			for mutation in pattern_mutation_dict[pattern]:
				mutation: cmute.Mutation
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
				pattern: MutationConditionPattern
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
	def select_best_mutation_pattern(pattern_mutation_dict: dict, mutation: cmute.Mutation, min_alive_number: int, min_alive_rate: float):
		"""
		:param pattern_mutation_dict: mapping from patterns generated to the mutations that they match with
		:param mutation: the mutation being matched
		:param min_alive_number: the minimal rate of alive mutations among the patterns being selected
		:param min_alive_rate: the minimal rate of alive mutations among the patterns being selected
		:return: the best pattern matched with the mutation under the limited range (with maximal number)
		"""
		patterns = MutationConditionPatternsSelection.extract_patterns_of_mutation(pattern_mutation_dict.keys(), mutation)
		filtered_patterns = set()
		for pattern in patterns:
			pattern: MutationConditionPattern
			killed, alive, unknown, alive_rate, label = count_mutation_labels(pattern_mutation_dict[pattern])
			if alive >= min_alive_number and alive_rate >= min_alive_rate:
				filtered_patterns.add(pattern)
		minimal_filtered_patterns = MutationConditionPatternsSelection.minimize_mutation_patterns(filtered_patterns)
		best_alive_number, best_patterns = 0, set()
		for pattern in minimal_filtered_patterns:
			pattern: MutationConditionPattern
			killed, alive, unknown, alive_rate, label = count_mutation_labels(pattern_mutation_dict[pattern])
			if alive > best_alive_number:
				best_patterns.clear()
				best_alive_number = alive
				best_patterns.add(pattern)
			elif alive == best_alive_number:
				best_patterns.add(pattern)
		best_alive_rate, best_pattern = 0.0, None
		for pattern in best_patterns:
			pattern: MutationConditionPattern
			killed, alive, unknown, alive_rate, label = count_mutation_labels(pattern_mutation_dict[pattern])
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
		c_project = cmute.CProject(directory, file_name)
		output_directory = os.path.join(post_directory, file_name)
		if not os.path.exists(output_directory):
			os.mkdir(output_directory)
		feature_label_file = os.path.join(directory, file_name + ".sfl")
		c_project.conditions.load_features_and_labels(feature_label_file)
		print("1. Load", len(c_project.muta_space.get_mutations()), "mutations from", file_name)

		''' 2.B select the available mutation patterns for analysis and evaluation '''
		pattern_mutation_dict = MutationConditionPatterns().generate_alive_patterns(c_project)
		patterns = MutationConditionPatternsSelection.filter_patterns_of_alive_mutations(pattern_mutation_dict, min_alive_number, min_alive_rate)
		patterns = MutationConditionPatternsSelection.minimize_mutation_patterns(patterns)
		alive_mutations, matched_mutations, common_mutations, precision, recall, pattern_rate = MutationConditionPatternsSelection.evaluate_alive_mutation_patterns(patterns, c_project.muta_space.get_mutations(), pattern_mutation_dict)
		print("2. Select", len(patterns), "patterns with P =", int(100 * precision), "%, R =", int(100 * recall), "% and C =", int(100 * pattern_rate), "%.")

		''' 2.C write the selected mutation patterns to the file '''
		pattern_file_path = os.path.join(output_directory, file_name + ".ptn")
		with open(pattern_file_path, 'w') as writer:
			writer.write("Label\tKilled\tAlive\tType\tLocation\tWords\tLine\tCode\tMutations\n")
			for pattern in patterns:
				pattern: MutationConditionPattern
				mutations = pattern_mutation_dict[pattern]
				killed, alive, unknown, alive_rate, label = count_mutation_labels(mutations)
				''' label, killed, alive '''
				writer.write(str(label) + "\t" + str(killed) + "\t" + str(alive))
				writer.write("\t" + pattern.get_type())
				writer.write("\t" + str(pattern.location))
				writer.write("\t" + str(pattern.get_parameters()))
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
		c_project = cmute.CProject(directory, file_name)
		output_directory = os.path.join(post_directory, file_name)
		if not os.path.exists(output_directory):
			os.mkdir(output_directory)
		feature_label_file = os.path.join(directory, file_name + ".sfl")
		c_project.conditions.load_features_and_labels(feature_label_file)
		print("1. Load", len(c_project.muta_space.get_mutations()), "mutations from", file_name)

		''' 2.B generate the mutations patterns for program '''
		pattern_mutation_dict = MutationConditionPatterns().generate_alive_patterns(c_project)
		print("2. Generate", len(pattern_mutation_dict), "patterns for mutations in program.")

		''' 2.C. select the best patterns for each alive mutation and write them to file '''
		mutation_file_path = os.path.join(output_directory, file_name + ".mnt")
		with open(mutation_file_path, 'w') as writer:
			writer.write("ID\tClass\tOprt\tLine\tCode\tParam\tType\tLocation\tWords\tKilled\tAlive\tRate\n")
			for mutation in c_project.muta_space.get_mutations():
				mutation: cmute.Mutation
				if mutation.label == 0:
					writer.write(str(mutation.get_mutation_id()))
					writer.write("\t" + mutation.get_mutation_class())
					writer.write("\t" + mutation.get_mutation_operator())
					writer.write("\t" + str(mutation.get_location().get_line_of() + 1))
					code = mutation.get_location().get_code(True)
					if len(code) > 64:
						code = code[0: 64]
					writer.write("\t" + code)
					writer.write("\t" + str(mutation.get_parameter()))
					best_pattern = MutationConditionPatternsSelection.select_best_mutation_pattern(pattern_mutation_dict, mutation, min_alive_number, min_alive_rate)
					if best_pattern is None:
						writer.write("\t\t\t\t0\t0\t0.0%")
					else:
						writer.write("\t" + best_pattern.get_type())
						writer.write("\t" + str(best_pattern.get_location()))
						writer.write("\t" + str(best_pattern.get_parameters()))
						killed, alive, unknown, alive_rate, label = count_mutation_labels(pattern_mutation_dict[best_pattern])
						writer.write("\t" + str(killed) + "\t" + str(alive) + "\t" + str(int(alive_rate * 100)) + "%")
					writer.write("\n")
		print("3. Write best patterns for alive mutations to", mutation_file_path)
	return


if __name__ == "__main__":
	root_dir = "/home/dzt2/Development/Data/features/"
	post_dir = "/home/dzt2/Development/Data/results/"
	# generate_best_pattern_of_mutations(root_dir, post_dir)
	generate_and_write_alive_patterns(root_dir, post_dir)

