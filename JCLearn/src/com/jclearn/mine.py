"""
It implements the data mining algorithms to find the potential causes for mutation being (not) killed in testing.
"""

import os
import src.com.jclearn.code as ccode
import src.com.jclearn.muta as cmuta


class MutationFeaturesCorpus:
	"""
	It generates the word to describe MutationFeature.
	"""
	def __init__(self):
		self.word_feature_dict = dict()
		return

	def __unique_feature__(self, key: str, feature: cmuta.MutationFeature):
		"""
		:param key:
		:param feature:
		:return: unique instance of feature using the string key
		"""
		if key not in self.word_feature_dict:
			self.word_feature_dict[key] = feature
		feature = self.word_feature_dict[key]
		feature: cmuta.MutationFeature
		return feature

	@staticmethod
	def __dynamic_feature_key__(feature: cmuta.MutationFeature):
		"""
		:param feature:
		:return: type:location:parameter:validity
		"""
		key = feature.feature_type + ":" + str(feature.location.cir_node_id)
		key += ":" + feature.parameter.strip()
		key += ":" + str(feature.validity)
		return key

	@staticmethod
	def __static_feature_key__(feature: cmuta.MutationFeature):
		"""
		:param feature:
		:return: type:location:parameter
		"""
		key = feature.feature_type + ":" + str(feature.location.cir_node_id)
		key += ":" + feature.parameter.strip()
		return key

	@staticmethod
	def __abstract_feature_key__(feature: cmuta.MutationFeature):
		"""
		:param feature:
		:return: type:location:parameter|word*
		"""
		key = feature.feature_type + ":" + str(feature.location.cir_node_id)
		if len(feature.words) == 0:
			key += ":" + feature.parameter.strip()
		else:
			for word in feature.words:
				key += ":" + word
		return key

	@staticmethod
	def __available_feature_key__(feature: cmuta.MutationFeature):
		"""
		:param feature:
		:return:
				#cons:location:parameter
				#trap:location
				#flow:location:parameter
				#expr:location{:word}*
				#refr:location{:word}*
				#stat:location{:word}*
		"""
		key = feature.feature_type + ":" + str(feature.location)
		if feature.feature_type == "#cons" or feature.feature_type == "#flow":
			key += ":" + feature.parameter
		elif feature.feature_type != "#trap":
			for word in feature.words:
				key += ":" + word
		return key

	def get_feature_and_word(self, feature: cmuta.MutationFeature, key_generator):
		"""
		:param feature:
		:param key_generator:
		:return: unique_feature, unique_key
		"""
		key = key_generator(feature)
		key: str
		return self.__unique_feature__(key, feature), key

	def clear(self):
		self.word_feature_dict.clear()


def classify_mutations(mutations):
	"""
	:param mutations:
	:return: killed, alive, predict_label
	"""
	killed, alive, unknown, label = 0, 0, 0, -1
	for mutation in mutations:
		mutation: cmuta.Mutation
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
	return killed, alive, label


class MutationFeaturesEvaluator:
	"""
	It evaluates the feature space using accuracy
	"""
	def __init__(self):
		self.feature_mutations_dict = dict()
		return

	@staticmethod
	def __mutation_key__(features: cmuta.MutationFeatures, key_generator):
		words = list()
		for feature in features.get_feature_list():
			feature: cmuta.MutationFeature
			word = key_generator(feature)
			words.append(word)
		words.sort()
		return str(words)

	def update(self, mutation_space: cmuta.MutantSpace, key_generator):
		"""
		:param mutation_space:
		:param key_generator:
		:return: update mapping from features to mutations they refers to
		"""
		self.feature_mutations_dict.clear()
		for mutation in mutation_space.get_mutations():
			mutation: cmuta.Mutation
			key = MutationFeaturesEvaluator.__mutation_key__(mutation.get_features(), key_generator)
			if key not in self.feature_mutations_dict:
				self.feature_mutations_dict[key] = list()
			self.feature_mutations_dict[key].append(mutation)
		return

	def __predict__(self, key: str):
		"""
		:param key:
		:return: the label hold by the features of specified key
		"""
		if key not in self.feature_mutations_dict:
			return -1
		mutations = self.feature_mutations_dict[key]
		killed, alive, unknown = 0, 0, 0
		for mutation in mutations:
			mutation: cmuta.Mutation
			if mutation.label == 1:
				killed += 1
			elif mutation.label == 0:
				alive += 1
			else:
				unknown += 1
		if killed > alive:
			return 1
		elif killed < alive:
			return 0
		else:
			return -1

	def evaluate_accuracy(self, error_mutation_file: str):
		"""
		:param error_mutation_file:
		:return: total_number, error_number, accuracy
		"""
		total_number, error_number = 0.0, 0.0
		with open(error_mutation_file, 'w') as writer:
			for key, mutations in self.feature_mutations_dict.items():
				predict_label = self.__predict__(key)
				for mutation in mutations:
					mutation: cmuta.Mutation
					total_number += 1.0
					if mutation.label != predict_label:
						error_number += 1.0
						writer.write("\t" + str(mutation.mutation_id))
						writer.write("\t" + mutation.get_muta_class() + "::" + mutation.muta_operator)
						writer.write("\t" + mutation.location.get_code(True))
						writer.write("\t" + str(mutation.parameter))
						writer.write("\t" + str(mutation.label) + " --> " + str(predict_label))
						writer.write("\t" + key)
						writer.write("\n")
		accuracy = (total_number - error_number) / total_number
		return int(total_number), int(error_number), accuracy


def evaluate_mutation_features(root_directory: str, post_directory: str, key_generator):
	evaluator = MutationFeaturesEvaluator()
	for file_name in os.listdir(root_directory):
		directory = os.path.join(root_directory, file_name)
		c_project = cmuta.CProject(directory, file_name)
		output_directory = os.path.join(post_directory, file_name)
		if not os.path.exists(output_directory):
			os.mkdir(output_directory)
		feature_label_file = os.path.join(directory, file_name + ".sfl")
		c_project.muta_space.load_features_and_labels(feature_label_file)
		error_file_path = os.path.join(output_directory, file_name + ".err")
		evaluator.update(c_project.muta_space, key_generator)
		total_number, error_number, accuracy = evaluator.evaluate_accuracy(error_file_path)
		print(file_name, ": total =", total_number, "; error =", error_number, "; accuracy =", accuracy * 100, "%.")
	return


class MutationFeaturesCounter:
	"""
	It counts the mutations with the features that they hold
	"""
	def __init__(self):
		self.feature_mutations_dict = dict()
		self.feature_corpus = MutationFeaturesCorpus()
		return

	def update(self, mutation_space: cmuta.MutantSpace, key_generator):
		"""
		:param mutation_space:
		:param key_generator:
		:return:
		"""
		self.feature_mutations_dict.clear()
		self.feature_corpus.clear()
		for mutation in mutation_space.get_mutations():
			mutation: cmuta.Mutation
			for feature in mutation.get_features().get_feature_list():
				feature: cmuta.MutationFeature
				feature, key = self.feature_corpus.get_feature_and_word(feature, key_generator)
				if feature not in self.feature_mutations_dict:
					self.feature_mutations_dict[feature] = list()
				self.feature_mutations_dict[feature].append(mutation)
		return

	def __predict__(self, feature: cmuta.MutationFeature):
		"""
		:param feature:
		:return: label predicted from the feature with its key-generator
		"""
		if feature not in self.feature_mutations_dict:
			return -1
		mutations = self.feature_mutations_dict[feature]
		killed, alive, unknown = 0, 0, 0
		for mutation in mutations:
			mutation: cmuta.Mutation
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
		return killed, alive, label

	def evaluate(self, feature_file_path: str):
		with open(feature_file_path, 'w') as writer:
			for feature, mutations in self.feature_mutations_dict.items():
				feature: cmuta.MutationFeature
				killed, alive, label = self.__predict__(feature)
				writer.write("#Feature")
				if label == 1:
					writer.write("\tKilled")
				elif label == 0:
					writer.write("\tAlive")
				else:
					writer.write("\tUnTested")
				writer.write("\t" + str(killed) + "/" + str(killed + alive))
				if feature.validity == 1:
					writer.write("\tSatisfied")
				elif feature.validity == 0:
					writer.write("\tInvalid")
				else:
					writer.write("\tUnknown")
				writer.write("\t" + feature.feature_type)
				writer.write("\t" + str(feature.location))
				if len(feature.words) == 0:
					writer.write("\t" + feature.parameter)
				else:
					writer.write("\t" + str(feature.words))
				writer.write("\n")
		return


def count_mutation_features(root_directory: str, post_directory: str, key_generator):
	counter = MutationFeaturesCounter()
	for file_name in os.listdir(root_directory):
		directory = os.path.join(root_directory, file_name)
		c_project = cmuta.CProject(directory, file_name)
		output_directory = os.path.join(post_directory, file_name)
		if not os.path.exists(output_directory):
			os.mkdir(output_directory)
		feature_label_file = os.path.join(directory, file_name + ".sfl")
		c_project.muta_space.load_features_and_labels(feature_label_file)
		feature_file_path = os.path.join(output_directory, file_name + ".fet")
		counter.update(c_project.muta_space, key_generator)
		counter.evaluate(feature_file_path)
		print(file_name, ": count on", len(c_project.muta_space.mutations), "mutations.")


class MutationFeaturePattern:
	"""
	pattern: [type, location, words]
	"""
	def __init__(self, feature_type: str, location: ccode.CirNode):
		"""
		create a pattern as [type, location, []]
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

	def match_feature(self, feature: cmuta.MutationFeature):
		"""
		:param feature:
		:return: whether the feature matches with this pattern
		"""
		if feature.feature_type == self.feature_type and feature.location == self.location:
			for word in self.feature_words:
				if (word != feature.parameter) and (word not in feature.words):
					return False
			return True
		return False

	def match_mutation(self, mutation: cmuta.Mutation):
		"""
		:param mutation:
		:return: whether the mutation instance matches with the pattern
		"""
		for feature in mutation.get_features().get_feature_list():
			feature: cmuta.MutationFeature
			if self.match_feature(feature):
				return True
		return False

	def new_child_pattern(self, feature_word: str):
		"""
		:param feature_word:
		:return: child pattern by adding the new word
		"""
		if feature_word in self.feature_words:
			return self
		child_pattern = MutationFeaturePattern(self.feature_type, self.location)
		for word in self.feature_words:
			child_pattern.feature_words.append(word)
		child_pattern.feature_words.append(feature_word)
		return child_pattern

	def filter_mutations(self, mutations: list):
		"""
		:param mutations:
		:return: mutations that match with this pattern
		"""
		new_mutations = list()
		for mutation in mutations:
			mutation: cmuta.Mutation
			if self.match_mutation(mutation):
				new_mutations.append(mutation)
		return new_mutations

	def __str__(self):
		key = self.feature_type + ":" + str(self.location)
		self.feature_words.sort()
		for word in self.feature_words:
			key += ":" + word
		return key


class MutationFeaturePatternMiner:
	"""
	It mines the pattern within mutation features.
	"""
	def __init__(self):
		self.patterns = dict()
		self.solutions = dict()
		return

	def __get_unique_pattern__(self, pattern: MutationFeaturePattern):
		key = str(pattern)
		if key not in self.patterns:
			self.patterns[key] = pattern
		pattern = self.patterns[key]
		pattern: MutationFeaturePattern
		return pattern

	def get_init_pattern(self, feature: cmuta.MutationFeature):
		pattern = MutationFeaturePattern(feature.feature_type, feature.location)
		return self.__get_unique_pattern__(pattern)

	def get_child_pattern(self, pattern: MutationFeaturePattern, feature_word: str):
		child_pattern = pattern.new_child_pattern(feature_word)
		return self.__get_unique_pattern__(child_pattern)

	def __solve__(self, parent_pattern: MutationFeaturePattern, mutations, feature_words):
		if parent_pattern not in self.solutions:
			parent_pattern = self.__get_unique_pattern__(parent_pattern)
			parent_mutations = parent_pattern.filter_mutations(mutations)
			print("\t\t==>", str(parent_pattern))
			killed, alive, label = classify_mutations(parent_mutations)
			self.solutions[parent_pattern] = parent_mutations
			if killed == 0 or alive == 0:
				return      # deterministic for further matching
			for feature_word in feature_words:
				if feature_word not in parent_pattern.feature_words:
					child_pattern = self.get_child_pattern(parent_pattern, feature_word)
					self.__solve__(child_pattern, parent_mutations, feature_words)
		return

	def solve(self, mutation_space: cmuta.MutantSpace):
		"""
		:param mutation_space:
		:return: mapping from MutationFeaturePattern to set of mutations matching with it
		"""
		self.patterns.clear()
		self.solutions = dict()
		for mutation in mutation_space.get_mutations():
			mutation: cmuta.Mutation
			for feature in mutation.get_features().get_feature_list():
				feature: cmuta.MutationFeature
				init_pattern = self.get_init_pattern(feature)
				feature_words = list()
				feature_words.append(feature.parameter)
				for word in feature.get_feature_words():
					feature_words.append(word)
				self.__solve__(init_pattern, mutation_space.mutations, feature_words)
		return self.solutions


def mutation_feature_pattern_mine(root_directory: str, post_directory: str):
	for file_name in os.listdir(root_directory):
		directory = os.path.join(root_directory, file_name)
		c_project = cmuta.CProject(directory, file_name)
		output_directory = os.path.join(post_directory, file_name)
		if not os.path.exists(output_directory):
			os.mkdir(output_directory)
		feature_label_file = os.path.join(directory, file_name + ".sfl")
		c_project.muta_space.load_features_and_labels(feature_label_file)
		pattern_file_path = os.path.join(output_directory, file_name + ".pat")
		print("Start to mine within", len(c_project.muta_space.mutations), "mutations of", file_name)
		miner = MutationFeaturePatternMiner()
		solutions = miner.solve(c_project.muta_space)
		print("Complete pattern mining with", len(solutions), "patterns being counted")
		with open(pattern_file_path, 'w') as writer:
			writer.write("\tLabel\tKilled\tAlive\tType\tLocation\tWords\n")
			for pattern, mutations in solutions.items():
				pattern: MutationFeaturePattern
				killed, alive, label = classify_mutations(mutations)
				writer.write("@Pattern")
				if label == 1:
					writer.write("\tKilled")
				elif label == 0:
					writer.write("\tAlive")
				else:
					writer.write("\tUnknown")
				writer.write("\t" + str(killed) + "\t" + str(alive))
				writer.write("\t" + pattern.feature_type)
				writer.write("\t" + str(pattern.location))
				writer.write("\t" + str(pattern.feature_words))
				writer.write("\n")
		print("Output", len(solutions), "patterns to", pattern_file_path)
		print()
	return


if __name__ == "__main__":
	root_dir = "/home/dzt2/Development/Data/features/"
	post_dir = "/home/dzt2/Development/Code/GitProject/jcsa/JCLearn/output"
	# evaluate_mutation_features(root_dir, post_dir, MutationFeaturesCorpus.__available_feature_key__)
	# count_mutation_features(root_dir, post_dir, MutationFeaturesCorpus.__available_feature_key__)
	mutation_feature_pattern_mine(root_dir, post_dir)

