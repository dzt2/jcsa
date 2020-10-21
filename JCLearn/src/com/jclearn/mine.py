"""
It implements the data mining algorithms to find the potential causes for mutation being (not) killed in testing.
"""

import os
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

	def get_feature_and_word(self, feature: cmuta.MutationFeature, key_generator):
		"""
		:param feature:
		:param key_generator:
		:return: unique_feature, unique_key
		"""
		key = key_generator(feature)
		key: str
		return self.__unique_feature__(key, feature), key


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
					total_number += 1.0
					if mutation.label != predict_label:
						error_number += 1.0
						writer.write("\t" + str(mutation.mutation_id))
						writer.write("\t" + mutation.get_muta_class() + "::" + mutation.muta_operator)
						writer.write("\t" + str(mutation.location))
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


if __name__ == "__main__":
	root_dir = "/home/dzt2/Development/Data/features/"
	post_dir = "/home/dzt2/Development/Code/GitProject/jcsa/JCLearn/output"
	evaluate_mutation_features(root_dir, post_dir, MutationFeaturesCorpus.__dynamic_feature_key__)

