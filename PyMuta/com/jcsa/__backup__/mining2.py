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
				 max_length: int, min_support: int, min_confidence: float, max_confidence: float):
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
		self.max_confidence = max_confidence
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

	def get_max_confidence(self):
		return self.max_confidence

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
		if length < self.inputs.get_max_length() and support >= self.inputs.get_min_support() and \
				confidence < self.inputs.get_max_confidence():
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
		print("\t\t\t--> Start: mutant#{} using {} tests and {} features.".format(mutant.get_mid(),
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

	## information text

	def __mut2str__(self, mutant):
		"""
		:param mutant: either MerMutant or original Mutant
		:return: id result class operator function line code parameter
		"""
		if isinstance(mutant, jcenco.MerMutant):
			mutant = self.c_document.project.muta_space.get_mutant(mutant.get_mid())
		else:
			mutant: jcmuta.Mutant
		mid = mutant.get_muta_id()
		result = mutant.get_result().is_killed_in(None)
		if result:
			result = "Killed"
		else:
			result = "Survive"
		m_class = mutant.get_mutation().get_mutation_class()
		operator = mutant.get_mutation().get_mutation_operator()
		location = mutant.get_mutation().get_location()
		line = location.line_of(tail=False) + 1
		definition = location.function_definition_of()
		code = "\"{}\"".format(location.get_code(True))
		def_code = definition.get_code(True)
		index = def_code.index('(')
		fun_code = def_code[0: index].strip()
		parameter = mutant.get_mutation().get_parameter()
		return "{}\t{}\t{}\t{}\t{}\t{}\t{}\t[{}]".format(mid, result, m_class, operator, fun_code, line, code, parameter)

	def __rul2str__(self, rule: MerPredictRule, support: int, confidence: float):
		"""
		:param rule:
		:return: length executions mutants support confidence [specialized]
		"""
		self.c_document = self.c_document
		return "{}\t{}\t{}\t{}\t{}%".format(len(rule), len(rule.get_executions()), len(rule.get_mutants()),
											support, int(confidence * 100000) / 1000.0)

	def __con2str__(self, condition):
		"""
		:param condition: either MerCondition or SymCondition
		:return: category operator execution statement location parameter
		"""
		if isinstance(condition, jcenco.MerCondition):
			condition = self.c_document.conditions.get_condition(condition.get_code())
		else:
			condition: jctest.SymCondition
		return "{}\t{}\t{}\t\"{}\"\t\"{}\"\t[{}]".format(condition.get_category(), condition.get_operator(),
														 condition.get_execution(),
														 condition.get_execution().get_statement().get_cir_code(),
														 condition.get_location().get_cir_code(),
														 condition.get_parameter())

	## output methods

	def __write_mutant_rules__(self, mutant, max_size: int):
		"""
		:param mutant:
		:param max_size: the maximal number of rules being printed
		:return:
		"""
		rule_evaluation_dict = self.miner.mine(mutant)
		good_rules = sort_prediction_rules_in_support(rule_evaluation_dict, None)
		if (len(good_rules) > max_size) and (max_size > 0):
			good_rules = good_rules[0: max_size]
		self.__output__("Mutant\t{}\n".format(self.__mut2str__(mutant)))
		for rule in good_rules:
			evaluation = rule_evaluation_dict[rule]
			self.__output__("\tRule\t{}\n".format(self.__rul2str__(rule, evaluation[1], evaluation[2])))
			index = 0
			for condition in rule.get_conditions():
				self.__output__("\tR.C[{}]\t{}\n".format(index, self.__con2str__(condition)))
				index += 1
		self.__output__("\n")
		return

	def write_mutant_rules(self, file_path: str, mutants, max_size: int):
		"""
		:param max_size:
		:param file_path:
		:param mutants:
		:return:
		"""
		counter = 1
		with open(file_path, 'w') as writer:
			self.writer = writer
			for mutant in mutants:
				if isinstance(mutant, jcmuta.Mutant):
					mutant = self.m_document.muta_space.get_mutant(mutant.get_muta_id())
				else:
					pass
				print("\t\t\tProcess[{}/{}]...".format(counter, len(mutants)))
				self.__write_mutant_rules__(mutant, max_size)
				counter += 1
		return

	def __write_predict_rule__(self, rule: MerPredictRule):
		"""
		:param rule:
		:return:
		"""
		length, support, confidence = self.miner.inputs.evaluate(rule, None)
		self.__output__("Rule\t{}\n".format(self.__rul2str__(rule, support, confidence)))
		self.__output__("\tMutants\n")
		for mutant in rule.get_mutants():
			self.__output__("\t{}\n".format(self.__mut2str__(mutant)))
		self.__output__("\tConditions\n")
		for condition in rule.get_conditions():
			self.__output__("\t{}\n".format(self.__con2str__(condition)))
		self.__output__("End Rule\n")
		return

	def write_prediction_rules(self, file_path: str):
		"""
		:param file_path:
		:return:
		"""
		with open(file_path, 'w') as writer:
			self.writer = writer
			rule_evaluation_dict = self.miner.inputs.find_prediction_rules(None, None)
			good_rules = sort_prediction_rules_in_support(rule_evaluation_dict, None)
			for rule in good_rules:
				self.__write_predict_rule__(rule)
				self.__output__("\n")
		return

	@staticmethod
	def __prf_score__(orig_samples, pred_samples):
		orig_set, pred_set = set(), set()
		for sample in orig_samples:
			orig_set.add(sample)
		for sample in pred_samples:
			pred_set.add(sample)
		como_set = orig_set & pred_set
		if len(como_set) > 0:
			precision = len(como_set) / len(pred_set)
			recall = len(como_set) / len(orig_set)
			return precision, recall, 2 * precision * recall / (precision + recall)
		return 0.0, 0.0, 0.0

	def write_prediction_summary(self, file_path: str, all_mutants):
		"""
		:param file_path:
		:param all_mutants: all the mutants used to mine rules
		:return: rules mutants support confidence
		"""
		with open(file_path, 'w') as writer:
			self.writer = writer
			rule_evaluation_dict = self.miner.inputs.find_prediction_rules(None, None)
			mutant_rule_dict, total_confidence = dict(), 0.0
			for rule, evaluation in rule_evaluation_dict.items():
				mutants = rule.get_mutants()
				for mutant in mutants:
					if not (mutant in mutant_rule_dict):
						mutant_rule_dict[mutant] = set()
					mutant_rule_dict[mutant].add(rule)
				total_confidence += evaluation[2]
			if total_confidence > 0:
				total_confidence = total_confidence / len(rule_evaluation_dict)
			precision, recall, score = MerPredictionMineOutput.__prf_score__(all_mutants, mutant_rule_dict.keys())
			self.__output__("Summary")
			self.__output__("\t{} rules, {} mutants, {}% confidence.\n".format(len(rule_evaluation_dict),
																			   len(mutant_rule_dict),
																			   int(total_confidence * 10000) / 100.0))
			self.__output__("\t{}% precision; {}% recall; {} score.\n".format(int(precision * 10000) / 100.0,
																			  int(recall * 10000) / 100.0,
																			  score))
			self.__output__("\n")
			self.__output__("Mutant\tRules\tLength\tSupports\tConfidence(%)\tExecutions\tMutants\tFeatureKey\n")
			for mutant, rules in mutant_rule_dict.items():
				mutant: jcenco.MerMutant
				good_rules = sort_prediction_rules_in_support(rule_evaluation_dict, rules)
				best_rule = good_rules[0]
				evaluation = rule_evaluation_dict[best_rule]
				self.__output__("{}\t{}\t{}\t{}\t{}%\t{}\t{}\t{}\n".format(mutant.get_mid(),
																		   len(rules),
																		   evaluation[0],
																		   evaluation[1],
																		   int(evaluation[2] * 10000) / 100.0,
																		   len(best_rule.get_executions()),
																		   len(best_rule.get_mutants()),
																		   str(best_rule.get_features())))
			self.__output__("\n")
		return


def main(prev_path: str, post_path: str, output_path: str, postfix: str, select_alive: bool):
	max_length, min_support, min_confidence, max_confidence = 1, 2, 0.70, 0.95
	exe_or_mut, support_classes, max_print_size = True, UNK_SUPPORT_CLASSES, 8
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
		inputs = MerPredictionMineInputs(m_document, exe_or_mut, support_classes, max_length,
										 min_support, min_confidence, max_confidence)
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
		output.write_mutant_rules(os.path.join(output_path, file_name + ".mur"), mutants, max_print_size)
		output.write_prediction_rules(os.path.join(output_path, file_name + ".prr"))
		output.write_prediction_summary(os.path.join(output_path, file_name + ".sum"), mutants)
		print("\t\t2. Output rules to final output directory...")
		print()
	print()
	return


if __name__ == "__main__":
	prev_directory = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	post_directory = "/home/dzt2/Development/Data/encodes"
	output_directory = "/home/dzt2/Development/Data/rules"
	main(prev_directory, post_directory, output_directory, ".sip", True)

