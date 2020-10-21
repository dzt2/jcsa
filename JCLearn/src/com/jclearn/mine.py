"""
It implements the data mining algorithms to find the potential causes for mutation being (not) killed in testing.
"""

import os
import src.com.jclearn.code as ccode
import src.com.jclearn.muta as cmuta


def classify_mutations(mutations):
	"""
	:param mutations:
	:return: killed, alive, unknown, label
	"""
	killed, alive, unknown, label = 0, 0, 0, -1
	for mutation in mutations:
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
	return killed, alive, unknown, label


class MutationFeaturePattern:
	"""
	The pattern of MutationFeature is a tuple as [feature_type, location, feature_words]
	in which feature_type::location are the strong head while the feature-words are extension part.
	"""
	def __init__(self, feature_type: str, location: ccode.CirNode):
		"""
		create a pattern without feature-words
		:param feature_type:
		:param location:
		"""
		self.feature_type = feature_type
		self.location = location
		self.feature_words = list()
		return

	def get_feature_type(self):
		return self.feature_type

	def get_location(self):
		return self.location

	def get_feature_words(self):
		return self.feature_words

	def __str__(self):
		key = self.feature_type + ":" + str(self.location)
		self.feature_words.sort()
		return key + ":" + str(self.feature_words)

	def new_child_pattern(self, feature_word: str):
		child_pattern = MutationFeaturePattern(self.feature_type, self.location)
		for word in self.feature_words:
			child_pattern.feature_words.append(word)
		if not (feature_word in child_pattern.feature_words):
			child_pattern.feature_words.append(feature_word)
		return child_pattern

	def match(self, feature: cmuta.MutationFeature):
		"""
		:param feature:
		:return: whether the feature matches with the pattern
		"""
		if (feature.feature_type == self.feature_type) and (feature.location == self.location):
			feature_words = MutationFeaturePatternMiner.__feature_words_of__(feature)
			for word in self.feature_words:
				if word not in feature_words:
					return False
			return True
		return False

	def match_mutation(self, mutation: cmuta.Mutation):
		"""
		:param mutation:
		:return: whether the features in mutation match with this pattern
		"""
		for feature in mutation.get_features().get_feature_list():
			if self.match(feature):
				return True
		return False

	def filter(self, mutations):
		"""
		:param mutations:
		:return: set of mutations that matches with this pattern
		"""
		good_mutations = set()
		for mutation in mutations:
			mutation: cmuta.Mutation
			if self.match_mutation(mutation):
				good_mutations.add(mutation)
		return good_mutations


class MutationFeaturePatternMiner:
	"""
	Frequent pattern mining
	"""
	def __init__(self):
		self.patterns = dict()
		self.solutions = dict()
		self.records = set()
		return

	def __unique_pattern__(self, pattern: MutationFeaturePattern):
		key = str(pattern)
		if key not in self.patterns:
			self.patterns[key] = pattern
		pattern = self.patterns[key]
		pattern: MutationFeaturePattern
		return pattern

	def __init_pattern__(self, feature: cmuta.MutationFeature):
		"""
		create initial patter as feature.type::feature.location
		:param feature:
		:return:
		"""
		return self.__unique_pattern__(MutationFeaturePattern(feature.feature_type, feature.location))

	def __new_child__(self, parent_pattern: MutationFeaturePattern, feature_word: str):
		"""
		:param parent_pattern:
		:param feature_word:
		:return: unique instance of the child pattern extended from parent using word
		"""
		return self.__unique_pattern__(parent_pattern.new_child_pattern(feature_word))

	@staticmethod
	def __feature_words_of__(feature: cmuta.MutationFeature):
		feature_words = set()
		if feature.feature_type == "#trap":
			pass
		elif feature.feature_type == "#cons" or feature.feature_type == "#flow":
			feature_words.add(feature.parameter)
		else:
			feature_words.add(feature.parameter)
			for word in feature.words:
				feature_words.add(str(word))
		return feature_words

	def __solve__(self, parent_pattern: MutationFeaturePattern, mutations, feature_words):
		parent_pattern = self.__unique_pattern__(parent_pattern)
		if parent_pattern not in self.solutions:
			parent_mutations = parent_pattern.filter(mutations)
			self.solutions[parent_pattern] = parent_mutations
		parent_mutations = self.solutions[parent_pattern]
		killed, alive, unknown, label = classify_mutations(parent_mutations)
		if killed > 0 and alive > 0:
			for feature_word in feature_words:
				if feature_word not in parent_pattern.feature_words:
					child_pattern = self.__new_child__(parent_pattern, feature_word)
					self.__solve__(child_pattern, parent_mutations, feature_words)
		return

	def mine(self, mutation_space: cmuta.MutantSpace):
		"""
		:param mutation_space:
		:return: patterns with their solutions as [killed, alive, unknown, label] for mutations in the space
		"""
		self.patterns = dict()
		self.solutions = dict()
		for mutation in mutation_space.get_mutations():
			mutation: cmuta.Mutation
			for feature in mutation.get_features().get_feature_list():
				feature: cmuta.MutationFeature
				feature_words = MutationFeaturePatternMiner.__feature_words_of__(feature)
				root_pattern = self.__init_pattern__(feature)
				self.__solve__(root_pattern, mutation_space.get_mutations(), feature_words)
		return self.solutions


def frequent_mine_patterns(root_directory: str, post_directory: str):
	for file_name in os.listdir(root_directory):
		directory = os.path.join(root_directory, file_name)
		c_project = cmuta.CProject(directory, file_name)
		output_directory = os.path.join(post_directory, file_name)
		if not os.path.exists(output_directory):
			os.mkdir(output_directory)
		feature_label_file = os.path.join(directory, file_name + ".sfl")
		c_project.muta_space.load_features_and_labels(feature_label_file)
		pattern_file_path = os.path.join(output_directory, file_name + ".pat")
		miner = MutationFeaturePatternMiner()
		print("Start to mine patterns in", len(c_project.muta_space.mutations), "mutations")
		solutions = miner.mine(c_project.muta_space)
		print("Generate", len(solutions), "patterns for mutations in", file_name)
		with open(pattern_file_path, 'w') as writer:
			writer.write("Label\tKilled\tAlive\tType\tLocation\tWords\tLine\tCode\n")
			for pattern, solution in solutions.items():
				pattern: MutationFeaturePattern
				killed, alive, unknown, label = classify_mutations(solution)
				if label == 1:
					writer.write("Killed")
				elif label == 0:
					writer.write("Alive")
				else:
					writer.write("Unknown")
				writer.write("\t" + str(killed) + "\t" + str(alive))
				writer.write("\t" + pattern.get_feature_type())
				writer.write("\t" + str(pattern.get_location()))
				writer.write("\t" + str(pattern.get_feature_words()))
				ast_node = pattern.get_location().get_ast_source()
				if ast_node is not None:
					writer.write("\t" + str(ast_node.get_line_of() + 1))
					writer.write("\t\"" + ast_node.get_code(True) + "\"")
				writer.write("\n")
			writer.write("\n")
	return


if __name__ == "__main__":
	root_dir = "/home/dzt2/Development/Data/features/"
	post_dir = "/home/dzt2/Development/Code/GitProject/jcsa/JCLearn/output"
	frequent_mine_patterns(root_dir, post_dir)

