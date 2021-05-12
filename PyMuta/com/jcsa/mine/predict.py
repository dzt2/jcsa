"""This file implements the prediction rule modeling and mining algorithms."""


import os
import com.jcsa.libs.test as jctest
import com.jcsa.mine.encode as jcenco


UR_CLASS = 0		# the mutant is not covered by used_tests
UI_CLASS = 1		# the mutant is not weakly killed but covered by used_tests
UP_CLASS = 2		# the mutant is not killed but infected by used_tests
KI_CLASS = 3		# the mutant is killed by used_tests


UNK_SUPPORT_CLASSES = [UR_CLASS, UI_CLASS, UP_CLASS]
WCC_SUPPORT_CLASSES = [UI_CLASS, UP_CLASS]
SCC_SUPPORT_CLASSES = [UP_CLASS]
KID_SUPPORT_CLASSES = [KI_CLASS]


class MerClassifier:
	"""
	It is used to classify and estimate symbolic condition premise based on reach-infect-propagate process model
	"""

	@staticmethod
	def __solve__(sample, used_tests):
		"""
		:param sample: the mutant or execution to be solved
		:param used_tests: the set of test cases for killing mutant or None
		:return: UR_CLASS|UI_CLASS|UP_CLASS|KI_CLASS
		"""
		if isinstance(sample, jcenco.MerExecution):
			sample: jcenco.MerExecution
			mutant = sample.get_mutant()
		else:
			sample: jcenco.MerMutant
			mutant = sample
		if mutant.get_result().is_killed_in(used_tests):
			return KI_CLASS
		elif mutant.w_mutant.get_result().is_killed_in(used_tests):
			return UP_CLASS
		elif mutant.c_mutant.get_result().is_killed_in(used_tests):
			return UI_CLASS
		else:
			return UR_CLASS

	@staticmethod
	def classify(samples, used_tests):
		"""
		:param samples: the collection of MerExecution or MerMutant
		:param used_tests: the set of MerTestCase (int) or None
		:return: {UR_CLASS, UI_CLASS, UP_CLASS, KI_CLASS} --> set[sample]
		"""
		classify_dict = dict()
		classify_dict[UR_CLASS] = set()
		classify_dict[UI_CLASS] = set()
		classify_dict[UP_CLASS] = set()
		classify_dict[KI_CLASS] = set()
		for sample in samples:
			classify_dict[MerClassifier.__solve__(sample, used_tests)].add(sample)
		return classify_dict

	@staticmethod
	def counting(samples, used_tests):
		"""
		:param samples: the collection of MerExecution or MerMutant
		:param used_tests: the set of MerTestCase (int) or None
		:return: ur, ui, up, ki, uk, cc
					ur: the number of samples not reached
					ui: the number of samples not infected but reached
					up: the number of samples not killed but infected
					ki: the number of samples being killed
		"""
		ur, ui, up, ki = 0, 0, 0, 0
		for sample in samples:
			label = MerClassifier.__solve__(sample, used_tests)
			if label == KI_CLASS:
				ki += 1
			elif label == UP_CLASS:
				up += 1
			elif label == UI_CLASS:
				ui += 1
			else:
				ur += 1
		return ur, ui, up, ki, ur + ui + up, ui + up

	@staticmethod
	def estimate(samples, support_classes, used_tests):
		"""
		:param samples: the collection of MerExecution or MerMutant
		:param support_classes: the set of class names for supporting
		:param used_tests: the set of MerTestCase (int) or None
		:return: total, positive, negative, confidence
		"""
		positive, negative = 0, 0
		for sample in samples:
			label = MerClassifier.__solve__(sample, used_tests)
			if label in support_classes:
				positive += 1
			else:
				negative += 1
		total = positive + negative
		if total > 0:
			confidence = positive / total
		else:
			confidence = 0.0
		return total, positive, negative, confidence


class MerPremise:
	"""
	It represents the premises used in prediction rule
	"""

	def __init__(self, space, features):
		"""
		:param space: the space where the premise is constructed
		:param features: the collection of integers encoding the symbolic conditions
		"""
		space: MerPremiseSpace
		self.space = space
		self.features = list()
		for feature in features:
			feature: int
			self.features.append(feature)
		self.features.sort()
		self.executions = set()
		self.mutants = set()
		return

	## data interfaces

	def get_space(self):
		return self.space

	def get_executions(self):
		return self.executions

	def get_mutants(self):
		return self.mutants

	def get_samples(self, exe_or_mut: bool):
		if exe_or_mut:
			return self.executions
		else:
			return self.mutants

	def __matching__(self, execution: jcenco.MerExecution):
		"""
		:param execution:
		:return:
		"""
		for feature in self.features:
			if not (feature in execution.get_features()):
				return False
		return True

	def set_samples(self, parent):
		"""
		:param parent: parent premise of this one
		:return:
		"""
		if parent is None:
			document = self.space.document
			document: jcenco.MerDocument
			executions = document.exec_space.get_executions()
		else:
			parent: MerPremise
			executions = parent.executions
		self.executions.clear()
		self.mutants.clear()
		for execution in executions:
			execution: jcenco.MerExecution
			if self.__matching__(execution):
				self.executions.add(execution)
				self.mutants.add(execution.get_mutant())
		return

	## feature model

	def __len__(self):
		return len(self.features)

	def __str__(self):
		return str(self.features)

	def get_features(self):
		return self.features

	def get_conditions(self):
		document = self.space.document
		document: jcenco.MerDocument
		return document.cond_space.decode(self.features)

	## estimation

	def classify(self, exe_or_mut: bool, used_tests):
		"""
		:param exe_or_mut: true to take executions or mutants as samples
		:param used_tests: the set of MerTestCase (int) or None
		:return: {UR_CLASS, UI_CLASS, UP_CLASS, KI_CLASS} --> set[sample]
		"""
		return MerClassifier.classify(self.get_samples(exe_or_mut), used_tests)

	def counting(self, exe_or_mut: bool, used_tests):
		"""
		:param exe_or_mut: true to take executions or mutants as samples
		:param used_tests: the set of MerTestCase (int) or None
		:return: ur, ui, up, ki, uk, cc
					ur: the number of samples not reached
					ui: the number of samples not infected but reached
					up: the number of samples not killed but infected
					ki: the number of samples being killed
		"""
		return MerClassifier.counting(self.get_samples(exe_or_mut), used_tests)

	def estimate(self, exe_or_mut: bool, support_classes, used_tests):
		"""
		:param exe_or_mut: true to take executions or mutants as samples
		:param support_classes: the set of class names for supporting
		:param used_tests: the set of MerTestCase (int) or None
		:return: total, positive, negative, confidence
		"""
		return MerClassifier.estimate(self.get_samples(exe_or_mut), support_classes, used_tests)


class MerPremiseSpace:
	"""
	It manages the construction and estimation of MerPremise
	"""

	def __init__(self, document: jcenco.MerDocument):
		self.document = document
		self.premises = dict()		# String --> MerPremise
		return

	def get_document(self):
		return self.document

	def get_all_premises(self):
		return self.premises.values()

	def __new_premise__(self, features):
		"""
		:param features:
		:return: generate the unique premise w.r.t. the feature vector
		"""
		child = MerPremise(self, features)
		if not (str(child) in self.premises):
			self.premises[str(child)] = child
			child_features = child.get_features()
			if len(child_features) > 0:
				parent_features = set()
				for k in range(0, len(child_features) - 1):
					parent_features.add(child_features[k])
				parent = self.__new_premise__(parent_features)
			else:
				parent = None
			child.set_samples(parent)
		premise = self.premises[str(child)]
		premise: MerPremise
		return premise

	def get_root(self):
		return self.__new_premise__([])

	def get_child(self, parent: MerPremise, feature: int):
		parent = self.__new_premise__(parent.get_features())
		features = set()
		features.add(feature)
		for old_feature in parent.get_features():
			features.add(old_feature)
		return self.__new_premise__(features)

	def get_premise(self, features):
		return self.__new_premise__(features)

	def evaluate(self, premise: MerPremise, exe_or_mut: bool, support_classes, used_tests):
		"""
		:param premise: the premise being estimated
		:param exe_or_mut: true to take executions or mutants
		:param support_classes: the set of class names for supporting
		:param used_tests: the set of MerTestCase (int) or None
		:return: length, support, confidence
		"""
		premise = self.__new_premise__(premise.get_features())
		total, positive, negative, confidence = premise.estimate(exe_or_mut, support_classes, used_tests)
		return len(premise.get_features()), positive, confidence

	def select_good_premises(self, max_length: int, min_support: int, min_confidence: float,
							 exe_or_mut: bool, support_classes, used_tests, premises):
		"""
		:param max_length: maximal length allowed
		:param min_support: minimal support required
		:param min_confidence: minimal confidence required
		:param exe_or_mut: true to take executions or mutants
		:param support_classes: the set of class names for supporting
		:param used_tests: the set of MerTestCase (int) or None
		:param premises: the collection of premises from which good ones are selected
		:return: map[MerPremise, (length, support, confidence)]
		"""
		if premises is None:
			premises = self.premises.values()
		good_premises = dict()		# premise --> (length, support, confidence)
		for premise in premises:
			premise: MerPremise
			length, support, confidence = self.evaluate(premise, exe_or_mut, support_classes, used_tests)
			if length <= max_length and support >= min_support and confidence >= min_confidence:
				good_premises[premise] = (length, support, confidence)
		return good_premises


class MerPremiseMineInputs:
	"""
	The inputs of mining algorithm
	"""

	def __init__(self, document: jcenco.MerDocument, exe_or_mut: bool, support_classes,
				 max_length: int, min_support: int, min_confidence: float):
		"""
		:param document: data source of context inputs
		:param max_length: maximal length allowed
		:param min_support: minimal support required
		:param min_confidence: minimal confidence required
		:param exe_or_mut: true to take executions or mutants
		:param support_classes: the set of class names for supporting
		"""
		self.space = MerPremiseSpace(document)
		self.exe_or_mut = exe_or_mut
		self.support_classes = set()
		for support_class in support_classes:
			self.support_classes.add(support_class)
		self.max_length = max_length
		self.min_support = min_support
		self.min_confidence = min_confidence
		return

	def get_document(self):
		return self.space.get_document()

	def get_space(self):
		return self.space

	def is_exe_or_mut(self):
		return self.exe_or_mut

	def get_support_classes(self):
		return self.support_classes

	def get_max_length(self):
		return self.max_length

	def get_min_support(self):
		return self.min_support

	def get_min_confidence(self):
		return self.min_confidence

	def get_root(self):
		return self.space.get_root()

	def get_child(self, parent: MerPremise, feature: int):
		return self.space.get_child(parent, feature)

	def get_premise(self, features):
		return self.space.get_premise(features)

	def evaluate(self, premise: MerPremise, used_tests):
		return self.space.evaluate(premise, self.exe_or_mut, self.support_classes, used_tests)

	def select_good_premises(self, premises, used_tests):
		"""
		:param premises: the premises from which the good ones are selected
		:param used_tests:
		:return:
		"""
		return self.space.select_good_premises(self.max_length, self.min_support, self.min_confidence,
											   self.exe_or_mut, self.support_classes, used_tests, premises)


def divide_killed_alive_tests(mutant: jcenco.MerMutant):
	"""
	:param mutant:
	:return: killed_tests, alive_tests
	"""
	killed_tests, alive_tests = list(), list()
	for test_case in mutant.space.document.test_space.get_test_cases():
		test_case: jcenco.MerTestCase
		if mutant.get_result().is_killed_by(test_case):
			killed_tests.append(test_case.get_tid())
		else:
			alive_tests.append(test_case.get_tid())
	return killed_tests, alive_tests


def extract_used_tests_and_features(mutant: jcenco.MerMutant):
	"""
	:param mutant:
	:return: used_tests, features
	"""
	killed_tests, alive_tests = divide_killed_alive_tests(mutant)
	used_tests = alive_tests
	features = list()
	document = mutant.space.get_document()
	for execution in document.exec_space.get_executions_of(mutant):
		execution: jcenco.MerExecution
		for feature in execution.get_features():
			if not (feature in features):
				features.append(feature)
	features.sort()
	return used_tests, features


def sort_premises_in_lengths(premises: set):
	"""
	:param premises:
	:return:
	"""
	length_premises, length_list = dict(), list()
	for premise in premises:
		premise: MerPremise
		length = len(premise)
		if not (length in length_premises):
			length_premises[length] = set()
			length_list.append(length)
		length_premises[length].add(premise)
	length_list.sort()
	sorted_premises = list()
	for length in length_list:
		premises = length_premises[length]
		for premise in premises:
			premise: MerPremise
			sorted_premises.append(premise)
	return sorted_premises


def sort_premises_in_supports(premise_solution_dict: dict):
	"""
	:param premise_solution_dict: [MerPremise, (length, support, confidence)]
	:return: sorted sequence of premise
	"""
	support_premises, support_list = dict(), list()
	for premise, solution in premise_solution_dict.items():
		premise: MerPremise
		support = solution[1]
		support: int
		if not (support in support_premises):
			support_premises[support] = set()
			support_list.append(support)
		support_premises[support].add(premise)
	support_list.sort(reverse=False)
	sorted_premises = list()
	for support in support_list:
		premises = support_premises[support]
		premises = sort_premises_in_lengths(premises)
		for premise in premises:
			sorted_premises.append(premise)
	return sorted_premises


class MerPremiseARMiner:
	"""
	It implements association rule mining.
	"""

	def __init__(self, inputs: MerPremiseMineInputs):
		self.inputs = inputs
		self.used_tests = None
		self.solutions = set()
		return

	def __mine__(self, parent: MerPremise, features: list):
		"""
		:param parent:
		:param features:
		:return:
		"""
		length, support, confidence = self.inputs.evaluate(parent, self.used_tests)
		self.solutions.add(parent)
		if length < self.inputs.max_length and support >= self.inputs.min_support:
			for k in range(0, len(features)):
				child = self.inputs.get_child(parent, features[k])
				if child != parent:
					self.__mine__(child, features[k + 1: ])
		return

	def mine(self, mutant: jcenco.MerMutant):
		"""
		:param mutant:
		:return:
		"""
		## 1. get original source
		used_tests, features = extract_used_tests_and_features(mutant)
		self.used_tests = used_tests
		## 2. recursive mining
		self.solutions.clear()
		print("\t\t\t--> Generate for mutant#{} using {} features.".format(mutant.get_mid(), len(features)))
		self.__mine__(self.inputs.get_root(), features)
		## return output
		premise_evaluation_dict = self.inputs.select_good_premises(self.solutions, self.used_tests)
		sorted_premises = sort_premises_in_supports(premise_evaluation_dict)
		print("\t\t\t--> It produces", len(sorted_premises), "premises from input.")
		return premise_evaluation_dict, sorted_premises


def mine_premises_and_output(document: jcenco.MerDocument, max_length: int, min_support: int, min_confidence: float,
							 exe_or_mut: bool, support_classes, file_path: str, c_document: jctest.CDocument):
	inputs = MerPremiseMineInputs(document, exe_or_mut, support_classes, max_length, min_support, min_confidence)
	miner = MerPremiseARMiner(inputs)
	with open(file_path, 'w') as writer:
		for mutant in document.exec_space.get_mutants():
			premise_evaluation_dict, premises = miner.mine(mutant)
			orig_mutant = c_document.project.muta_space.get_mutant(mutant.get_mid())
			line = orig_mutant.get_mutation().get_location().line_of(False) + 1
			code = "\"" + orig_mutant.get_mutation().get_location().get_code(True) + "\""
			if mutant.get_result().is_killed_in(None):
				result = "killed"
			else:
				result = "survive"
			writer.write("Mutant\t{}\t{}\t{}\t{}\t{}\t{}\t[{}]\n".format(orig_mutant.get_muta_id(),
																		 result,
																		 orig_mutant.get_mutation().get_mutation_class(),
																		 orig_mutant.get_mutation().get_mutation_operator(),
																		 line, code,
																		 orig_mutant.get_mutation().get_parameter()))
			for premise in premises:
				evaluation = premise_evaluation_dict[premise]
				writer.write("Premise\tlength\t{}\tsupport\t{}\tconfidence\t{}\n".format(evaluation[0],
																						 evaluation[1],
																						 int(evaluation[2] * 10000) / 100.0))
				for condition in premise.get_conditions():
					sym_condition = c_document.get_conditions_lib().get_condition(condition.get_code())
					writer.write("\t{}\t{}\t{}\t{}\t{}\n".format(sym_condition.get_category(),
																 sym_condition.get_operator(),
																 sym_condition.get_execution().get_statement().get_cir_code(),
																 sym_condition.get_location().get_cir_code(),
																 sym_condition.get_parameter()))
			writer.write("\n")
			writer.flush()
	return inputs


def main(prev_path: str, post_path: str, output_path: str):
	max_length, min_support, min_confidence, exe_or_mut = 1, 2, 0.70, True
	support_classes = UNK_SUPPORT_CLASSES
	for file_name in os.listdir(prev_path):
		c_document = jctest.CDocument(os.path.join(prev_path, file_name), file_name, ".sip")
		m_document = jcenco.MerDocument(os.path.join(post_path, file_name), file_name)
		print("Testing on", file_name)
		print("\tSummary:{} mutants\t{} executions\t{} conditions.".format(len(m_document.muta_space.get_mutants()),
																		   len(m_document.exec_space.get_executions()),
																		   len(m_document.cond_space.get_conditions())))
		output_file = os.path.join(output_path, file_name + ".pr")
		inputs = mine_premises_and_output(m_document, max_length, min_support, min_confidence, exe_or_mut,
										  support_classes, output_file, c_document)
	print()
	return


if __name__ == "__main__":
	prev_directory = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	post_directory = "/home/dzt2/Development/Data/encodes"
	output_directory = "/home/dzt2/Development/Data/premises"
	main(prev_directory, post_directory, output_directory)

