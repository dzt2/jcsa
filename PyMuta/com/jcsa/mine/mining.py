"""This file implements the mining algorithm for generating killable prediction rules"""


import os
from typing.io import TextIO
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest
import com.jcsa.mine.encode as jcenco


### support class definitions


UR_CLASS = 0		# the mutant is not covered by used_tests
UI_CLASS = 1		# the mutant is not weakly killed but covered by used_tests
UP_CLASS = 2		# the mutant is not killed but infected by used_tests
KI_CLASS = 3		# the mutant is killed by used_tests


UNK_SUPPORT_CLASSES = [UR_CLASS, UI_CLASS, UP_CLASS]
WCC_SUPPORT_CLASSES = [UI_CLASS, UP_CLASS]
SCC_SUPPORT_CLASSES = [UP_CLASS]
KID_SUPPORT_CLASSES = [KI_CLASS]


## classification based on RIP process model


class MerClassification:
	"""
	It implements the classification methods based on RIP process model.
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
			classify_dict[MerClassification.__solve__(sample, used_tests)].add(sample)
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
			label = MerClassification.__solve__(sample, used_tests)
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
		:return: positive, negative, confidence
		"""
		positive, negative = 0, 0
		for sample in samples:
			label = MerClassification.__solve__(sample, used_tests)
			if label in support_classes:
				positive += 1
			else:
				negative += 1
		total = positive + negative
		if total > 0:
			confidence = positive / total
		else:
			confidence = 0.0
		return positive, negative, confidence


class MerPredictRule:
	"""
	It describes a predict rule using a set of symbolic conditions (encoded as int-list) as premises
	"""

	def __init__(self, space, features):
		"""
		:param space: where the rule is created as unique instance
		:param features: the set of integers encoding the symbolic conditions
		"""
		space: MerPredictRules
		self.space = space
		self.features = list()
		for feature in features:
			feature: int
			if not (feature in self.features):
				self.features.append(feature)
		self.features.sort()
		self.executions = set()		# the set of executions matching with the rule
		self.mutants = set()		# the set of mutants of which executions match with this rule
		return

	## data interfaces

	def get_space(self):
		return self.space

	def get_executions(self):
		return self.executions

	def get_mutants(self):
		return self.mutants

	def get_samples(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: true to select executions or mutants
		:return:
		"""
		if exe_or_mut:
			return self.executions
		else:
			return self.mutants

	def __match__(self, execution: jcenco.MerExecution):
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
		:param parent: the parent rule from which this rule is created or none
		:return: update the executions and mutants matching with this rule in space
		"""
		if parent is None:
			document = self.space.document
			document: jcenco.MerDocument
			executions = document.exec_space.get_executions()
		else:
			parent: MerPredictRule
			executions = parent.get_executions()
		self.executions.clear()
		self.mutants.clear()
		for execution in executions:
			execution: jcenco.MerExecution
			if self.__match__(execution):
				self.executions.add(execution)
				self.mutants.add(execution.get_mutant())
		return

	## feature model

	def get_features(self):
		return self.features

	def get_conditions(self):
		document = self.space.document
		document: jcenco.MerDocument
		return document.cond_space.decode(self.features)

	def __len__(self):
		return len(self.features)

	def __str__(self):
		return str(self.features)

	## estimation

	def classify(self, exe_or_mut: bool, used_tests):
		"""
		:param exe_or_mut: true to take executions or mutants as samples
		:param used_tests: the set of MerTestCase (int) or None
		:return: {UR_CLASS, UI_CLASS, UP_CLASS, KI_CLASS} --> set[sample]
		"""
		return MerClassification.classify(self.get_samples(exe_or_mut), used_tests)

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
		return MerClassification.counting(self.get_samples(exe_or_mut), used_tests)

	def estimate(self, exe_or_mut: bool, support_classes, used_tests):
		"""
		:param exe_or_mut: true to take executions or mutants as samples
		:param support_classes: the set of class names for supporting
		:param used_tests: the set of MerTestCase (int) or None
		:return: positive, negative, confidence
		"""
		return MerClassification.estimate(self.get_samples(exe_or_mut), support_classes, used_tests)


class MerPredictRules:
	"""
	The space to support construction and estimation of prediction rules.
	"""

	def __init__(self, document: jcenco.MerDocument):
		self.document = document
		self.rules = dict()		# String --> MerPredictRule
		return

	## construction

	def get_document(self):
		return self.document

	def __new_rule__(self, features):
		"""
		:param features:
		:return: generate the unique instance of rule w.r.t. input features
		"""
		rule = MerPredictRule(self, features)
		if not (str(rule) in self.rules):
			self.rules[str(rule)] = rule
			## find parent to update samples
			if len(rule.get_features()) > 0:
				child_features = rule.get_features()
				parent_features = list()
				for k in range(0, len(child_features) - 1):
					parent_features.append(child_features[k])
				parent = self.__new_rule__(parent_features)
			else:
				parent = None
			rule.set_samples(parent)
		rule = self.rules[str(rule)]
		rule: MerPredictRule
		return rule

	def get_root(self):
		return self.__new_rule__([])

	def get_child(self, parent: MerPredictRule, feature: int):
		features = set()
		for old_feature in parent.get_features():
			features.add(old_feature)
		features.add(feature)
		return self.__new_rule__(features)

	def get_rule(self, features):
		"""
		:param features:
		:return:
		"""
		return self.__new_rule__(features)

	## evaluation

	def evaluate(self, rule: MerPredictRule, exe_or_mut: bool, support_classes, used_tests):
		"""
		:param rule:
		:param exe_or_mut: true to take executions or mutants as samples
		:param support_classes: the set of class names for supporting
		:param used_tests: the set of MerTestCase (int) or None
		:return: length, support, confidence
		"""
		rule = self.__new_rule__(rule.get_features())
		positive, negative, confidence = rule.estimate(exe_or_mut, support_classes, used_tests)
		return len(rule.get_features()), positive, confidence

	## selection

	def find_prediction_rules(self, max_length: int, min_support: int, min_confidence: float,
							  exe_or_mut: bool, support_classes, used_tests, input_rules):
		"""
		:param max_length: 			maximal length of rules allowed
		:param min_support: 		minimal support required
		:param min_confidence: 		minimal confidence required
		:param exe_or_mut: 			true to take executions or mutants as samples
		:param support_classes: 	the set of classes to simulate support in estimation
		:param used_tests: 			the set of tests used to kill or None to represent all
		:param input_rules: 		the input rules from which the rules are extracted or None for all
		:return: 					mapping from selected rules to [length, support, confidence]
		"""
		rule_evaluation_dict = dict()
		if input_rules is None:
			input_rules = self.rules.values()
		for rule in input_rules:
			rule: MerPredictRule
			rule = self.__new_rule__(rule.get_features())
			length, support, confidence = self.evaluate(rule, exe_or_mut, support_classes, used_tests)
			if length <= max_length and support >= min_support and confidence >= min_confidence:
				rule_evaluation_dict[rule] = (length, support, confidence)
		return rule_evaluation_dict


def __sort_prediction_rules_by_keys__(rule_evaluation_dict: dict, input_rules, reverse: bool, key_index: int):
	"""
	:param rule_evaluation_dict:
	:param input_rules:
	:param reverse:
	:param key_index: 	0--length; 1-support; 2-confidence.
	:return:
	"""
	if input_rules is None:
		input_rules = rule_evaluation_dict.keys()
	key_rules, key_list = dict(), list()
	for rule in input_rules:
		rule: MerPredictRule
		evaluation = rule_evaluation_dict[rule]
		key = evaluation[key_index]
		if not (key in key_rules):
			key_rules[key] = set()
			key_list.append(key)
		key_rules[key].add(rule)
	key_list.sort(reverse=reverse)
	sorted_rules = list()
	for key in key_list:
		for rule in key_rules[key]:
			rule: MerPredictRule
			sorted_rules.append(rule)
	return sorted_rules


def sort_prediction_rules_in_length(rule_evaluation_dict: dict, input_rules):
	"""
	:param rule_evaluation_dict:
	:param input_rules:
	:return:
	"""
	return __sort_prediction_rules_by_keys__(rule_evaluation_dict, input_rules, False, 0)


def sort_prediction_rules_in_support(rule_evaluation_dict: dict, input_rules):
	"""
	:param rule_evaluation_dict:
	:param input_rules:
	:return:
	"""
	return __sort_prediction_rules_by_keys__(rule_evaluation_dict, input_rules, True, 1)


def sort_prediction_rules_in_confidence(rule_evaluation_dict: dict, input_rules):
	"""
	:param rule_evaluation_dict:
	:param input_rules:
	:return:
	"""
	return __sort_prediction_rules_by_keys__(rule_evaluation_dict, input_rules, True, 2)


class MerPredictionMineInputs:
	"""
	It maintains the parameters used for mining
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
		self.space = MerPredictRules(document)
		self.exe_or_mut = exe_or_mut
		self.support_classes = set()
		for support_class in support_classes:
			self.support_classes.add(support_class)
		self.max_length = max_length
		self.min_support = min_support
		self.min_confidence = min_confidence
		return

	# data space

	def get_document(self):
		return self.space.get_document()

	def find_prediction_rules(self, used_tests, input_rules=None):
		"""
		:param used_tests: 			the set of tests used to kill or None to represent all
		:param input_rules: 		the input rules from which the rules are extracted or None for all
		:return:					the set of good prediction rules w.r.t. parameters
		"""
		return self.space.find_prediction_rules(self.max_length, self.min_support, self.min_confidence,
												self.exe_or_mut, self.support_classes, used_tests, input_rules)

	# parameters

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

	# rules

	def get_root(self):
		return self.space.get_root()

	def get_child(self, parent: MerPredictRule, feature: int):
		return self.space.get_child(parent, feature)

	def evaluate(self, rule: MerPredictRule, used_tests):
		return self.space.evaluate(rule, self.exe_or_mut, self.support_classes, used_tests)


class MerPredictionRulesMiner:
	"""
	It exploits frequent pattern mining to identify prediction rules from inputs.
	"""

	def __init__(self, inputs: MerPredictionMineInputs):
		self.inputs = inputs
		self.solutions = set()
		return

	def __get_used_tests__(self, mutant: jcenco.MerMutant):
		"""
		:param mutant:
		:return: the set of tests that fail to kill the mutant
		"""
		used_tests = list()
		for test_case in self.inputs.get_document().test_space.get_test_cases():
			test_case: jcenco.MerTestCase
			if not mutant.get_result().is_killed_by(test_case):
				used_tests.append(test_case.get_tid())
		return used_tests

	def __get_features__(self, mutant: jcenco.MerMutant):
		"""
		:param mutant:
		:return: the integers encoding conditions used for killing mutant
		"""
		features = set()
		for execution in self.inputs.get_document().exec_space.get_executions_of(mutant):
			execution: jcenco.MerExecution
			for feature in execution.get_features():
				features.add(feature)
		feature_list = list()
		for feature in features:
			feature_list.append(feature)
		feature_list.sort()
		return feature_list

	def __mine__(self, rule: MerPredictRule, used_tests, features):
		"""
		:param rule:
		:param used_tests:
		:param features:
		:return:
		"""
		## 1. evaluation and record
		length, support, confidence = self.inputs.evaluate(rule, used_tests)
		self.solutions.add(rule)
		## 2. recursively mining on
		if length < self.inputs.get_max_length() and support >= self.inputs.get_min_support():
			for k in range(0, len(features)):
				child = self.inputs.get_child(rule, features[k])
				if child != rule:
					self.__mine__(child, used_tests, features[k + 1: ])
		return

	def mine(self, mutant: jcenco.MerMutant):
		"""
		:param mutant:
		:return: the good prediction rules for mutant
		"""
		used_tests = self.__get_used_tests__(mutant)
		features = self.__get_features__(mutant)
		print("\t\t\t-->Start: mutant#{} using {} tests and {} features.".format(mutant.get_mid(),
																				 len(used_tests),
																				 len(features)))
		self.solutions.clear()
		root_rule = self.inputs.get_root()
		self.__mine__(root_rule, used_tests, features)
		rule_evaluation_dict = self.inputs.find_prediction_rules(used_tests, self.solutions)
		print("\t\t\t--> Finish: generate {} rules and {} good rules.".format(len(self.solutions),
																			  len(rule_evaluation_dict)))
		return rule_evaluation_dict


class MerPredictionMineOutput:

	def __init__(self, c_document: jctest.CDocument, m_document: jcenco.MerDocument, miner: MerPredictionRulesMiner):
		self.writer = None
		self.c_document = c_document
		self.m_document = m_document
		self.miner = miner
		return

	## IO interfaces

	def __output__(self, text: str):
		self.writer: TextIO
		self.writer.write(text)
		self.writer.flush()
		return

	## mutant mining

	def __write_mutant_head__(self, mutant: jcenco.MerMutant):
		"""
		:param mutant:
		:return: ID result class operator line code parameter
		"""
		orig_mutant = self.c_document.project.muta_space.get_mutant(mutant.get_mid())
		result = mutant.get_result().is_killed_in(None)
		if result:
			result = "KILLED"
		else:
			result = "SURVIVE"
		line = orig_mutant.get_mutation().get_location().line_of(tail=False) + 1
		code = "\"{}\"".format(orig_mutant.get_mutation().get_location().get_code(True))
		head = "Mutant\t{}\t{}\t{}\t{}\t{}\t{}\t[{}]".format(mutant.get_mid(), result,
															 orig_mutant.get_mutation().get_mutation_class(),
															 orig_mutant.get_mutation().get_mutation_operator(),
															 line, code, orig_mutant.get_mutation().get_parameter())
		self.__output__(head)
		self.__output__("\n")
		return

	def __write_rules_in_mutant__(self, mutant: jcenco.MerMutant):
		"""
		:param mutant:
		:return:
		"""
		## mutant head
		self.__write_mutant_head__(mutant)
		## rule generation
		rule_evaluation_dict = self.miner.mine(mutant)
		for rule, evaluation in rule_evaluation_dict.items():
			rule_head = "Rule\tlength\t{}\tsupport\t{}\tconfidence\t{}%\n".format(evaluation[0], evaluation[1],
																				  int(evaluation[2] * 10000) / 100.0)
			self.__output__(rule_head)
			## condition outputs
			for condition in rule.get_conditions():
				sym_condition = self.c_document.get_conditions_lib().get_condition(condition.get_code())
				rule_line = "\t{}\t{}\t{}\t\"{}\"\t\"{}\"\t[{}]\n".format(sym_condition.get_category(),
																		  sym_condition.get_operator(),
																		  sym_condition.get_execution(),
																		  sym_condition.get_execution().get_statement().get_cir_code(),
																		  sym_condition.get_location().get_cir_code(),
																		  sym_condition.get_parameter())
				self.__output__(rule_line)
		self.__output__("\n")
		return

	def write_mutant_rules(self, file_path: str, mutants):
		"""
		:param file_path:
		:param mutants: the set of MerMutant to be printed
		:return:
		"""
		with open(file_path, 'w') as writer:
			self.writer = writer
			for mutant in mutants:
				if isinstance(mutant, jcmuta.Mutant):
					mutant = self.m_document.muta_space.get_mutant(mutant.get_muta_id())
				else:
					mutant: jcenco.MerMutant
				self.__write_rules_in_mutant__(mutant)
			return

	def write_alive_rules(self, file_path: str):
		"""
		:param file_path:
		:return:
		"""
		with open(file_path, 'w') as writer:
			self.writer = writer
			rule_evaluation_dict = self.miner.inputs.find_prediction_rules(None, None)
			for rule, evaluation in rule_evaluation_dict.items():
				head = "Rule\tlength\t{}\tsupport\t{}\tconfidence\t{}%\n".format(evaluation[0],
																				 evaluation[1],
																				 int(evaluation[2] * 10000) / 100.0)
				self.__output__(head)
				for condition in rule.get_conditions():
					sym_condition = self.c_document.get_conditions_lib().get_condition(condition.get_code())
					rule_line = "\t{}\t{}\t{}\t\"{}\"\t\"{}\"\t[{}]\n".format(sym_condition.get_category(),
																			  sym_condition.get_operator(),
																			  sym_condition.get_execution(),
																			  sym_condition.get_execution().get_statement().get_cir_code(),
																			  sym_condition.get_location().get_cir_code(),
																			  sym_condition.get_parameter())
					self.__output__(rule_line)
				self.__output__("\n")
		return


def main(prev_path: str, post_path: str, output_path: str, postfix: str, select_alive: bool):
	max_length, min_support, min_confidence, exe_or_mut = 1, 2, 0.70, True
	support_classes = UNK_SUPPORT_CLASSES
	for file_name in os.listdir(prev_path):
		## 1. load documents
		inputs_directory = os.path.join(prev_path, file_name)
		encode_directory = os.path.join(post_path, file_name)
		c_document = jctest.CDocument(inputs_directory, file_name, postfix)
		m_document = jcenco.MerDocument(encode_directory, file_name)
		print("Testing on", file_name)
		print("\tSummary:{} mutants\t{} executions\t{} conditions.".format(len(m_document.muta_space.get_mutants()),
																		   len(m_document.exec_space.get_executions()),
																		   len(m_document.cond_space.get_conditions())))
		## 2. construct mining machine
		inputs = MerPredictionMineInputs(m_document, exe_or_mut, support_classes, max_length, min_support, min_confidence)
		miner = MerPredictionRulesMiner(inputs)
		output = MerPredictionMineOutput(c_document, m_document, miner)
		mutants = set()
		for mutant in m_document.exec_space.get_mutants():
			if select_alive:
				if not mutant.get_result().is_killed_in(None):
					mutants.add(mutant)
			else:
				mutants.add(mutant)
		print("\t\t1. Output prediction rules for", len(mutants), "mutants in project.")

		## 3. output prediction rules
		output.write_mutant_rules(os.path.join(output_path, file_name + ".mr"), mutants)
		output.write_alive_rules(os.path.join(output_path, file_name + ".alr"))
		print("\t\t2. Output rules to final output directory...")
		print()
	print()
	return


if __name__ == "__main__":
	prev_directory = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	post_directory = "/home/dzt2/Development/Data/encodes"
	output_directory = "/home/dzt2/Development/Data/premises"
	main(prev_directory, post_directory, output_directory, ".sip", True)

