"""
This file defines the data model of features directly used for mining algorithms.
"""


import os
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest


class WordCorpus:
	"""
	It is used to encode and decode string word into a file.
	"""

	def __init__(self):
		self.words = list()
		self.codes = dict()
		return

	def get_words(self):
		"""
		:return: the set of words being encoded in the feature engineering
		"""
		return self.words

	def __len__(self):
		"""
		:return: the number of words being encoded in the corpus
		"""
		return len(self.words)

	def decode(self, code: int):
		"""
		:param code:
		:return: decode the integer code to a string word being encoded before
		"""
		word = self.words[code]
		word: str
		return word

	def decodes(self, code_list):
		"""
		:param code_list: the collection of integer codes to decode the words
		:return: the collection of words decoded from the input integer codes
		"""
		words = list()
		for code in code_list:
			words.append(self.decode(code))
		return words

	def encode(self, word: str):
		"""
		:param word: string word to be encoded
		:return: integer code encoding the word or -1 if the word is empty
		"""
		word = word.strip()
		if len(word) > 0:
			if not (word in self.codes):
				self.codes[word] = len(self.words)
				self.words.append(word)
			code = self.codes[word]
			code: int
			return code
		return -1	# empty string is not encoded

	def encodes(self, word_list):
		"""
		:param word_list: the collection of string words to be encoded
		:return: the collection of integer code encoding the input words
		"""
		code_list = list()
		for word in word_list:
			code_list.append(self.encode(word))
		return code_list

	def save(self, file_path: str):
		"""
		:param file_path:
		:return: write the words in the corpus to file for reload
		"""
		with open(file_path, 'w') as writer:
			for word in self.words:
				word: str
				writer.write(word + "\n")
		return

	def load(self, file_path: str):
		"""
		:param file_path:
		:return: update the corpus by loading the file
		"""
		self.words.clear()
		self.codes.clear()
		with open(file_path, 'r') as reader:
			for line in reader:
				word = line.strip()
				if len(word) > 0:
					self.encode(word)
		return


class SymFeatureLine:
	"""
	Each line in feature encoding file is encoded as eid, result, condition_code_list
	"""

	def __init__(self, execution_id: int, result: bool, features):
		"""
		:param execution_id: 	integer ID of symbolic execution in a document
		:param result: 			boolean result to denote whether the mutant of the execution is killed
		:param features: 		the sequence of encoded features (condition-status pair) in execution
		"""
		self.eid = execution_id
		self.res = result
		self.features = list()
		for feature in features:
			feature: int
			self.features.append(feature)
		self.features.sort()
		return

	def get_execution_id(self):
		"""
		:return: integer ID of symbolic execution in a document
		"""
		return self.eid

	def get_result(self):
		"""
		:return: boolean result to denote whether the mutant of the execution is killed
		"""
		return self.res

	def get_feature_vector(self):
		"""
		:return: the sequence of encoded features (condition-status pair) in execution
		"""
		return self.features

	def __str__(self):
		text = "{}\t{}".format(self.eid, self.res)
		for feature in self.features:
			text += "\t{}".format(feature)
		return text

	@staticmethod
	def save(feature_lines, file_path: str):
		"""
		:param feature_lines: the sequence of SymFeatureLine
		:param file_path: the file to preserve the feature lines
		:return:
		"""
		with open(file_path, 'w') as writer:
			for feature_line in feature_lines:
				feature_line: SymFeatureLine
				writer.write(str(feature_line) + "\n")
		return

	@staticmethod
	def load(file_path: str):
		"""
		:param file_path:
		:return: load the lines in feature file to memory
		"""
		feature_lines = list()
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					eid = int(items[0].strip())
					if items[1].strip() == str(True):
						res = True
					else:
						res = False
					features = set()
					for k in range(2, len(items)):
						features.add(int(items[k].strip()))
					feature_lines.append(SymFeatureLine(eid, res, features))
		return feature_lines


class SymFeatureLines:
	"""
	It is used to write and read lines of symbolic feature lines to feature and word-corpus file.
	"""

	def __init__(self):
		self.__corpus__ = WordCorpus()
		return

	def get_word_corpus(self):
		return self.__corpus__

	def encode_word(self, condition: jctest.SymCondition, status=None):
		"""
		:param condition: condition to be encoded in testing
		:param status: status of the symbolic condition to be encoded
		:return: integer code to encode the word of status$condition
		"""
		if status is None:
			status_item = "n@null"
		elif status:
			status_item = "b@true"
		else:
			status_item = "b@false"
		return self.__corpus__.encode("{}${}".format(status_item, str(condition)))

	def encode_line(self, sym_execution: jctest.SymExecution, used_tests, true_status, false_status, none_status):
		"""
		:param sym_execution: 	the execution of which symbolic conditions will be encoded
		:param used_tests: 		the set of test cases used to kill the target mutation
		:param true_status: 	the status to replace if status is true
		:param false_status: 	the status to replace if status is false
		:param none_status: 	the status to replace if status is none
		:return: SymFeatureLine (isolated from the symbolic document for minimal memory)
		"""
		eid = sym_execution.get_id()
		res = sym_execution.get_mutant().get_result().is_killed_in(used_tests)
		features = set()
		for condition_node in sym_execution.get_condition_nodes():
			status = condition_node.get_status()
			if status.is_accepted():
				status_value = true_status
			elif status.is_rejected():
				status_value = false_status
			else:
				status_value = none_status
			sym_condition = condition_node.get_condition(0)
			feature_code = self.encode_word(sym_condition, status_value)
			features.add(feature_code)
		return SymFeatureLine(eid, res, features)

	def encode_lines(self, document: jctest.CDocument, used_tests, true_status, false_status, none_status):
		"""
		:param document:
		:param used_tests: 		the set of test cases used to kill the target mutation
		:param true_status: 	the status to replace if status is true
		:param false_status: 	the status to replace if status is false
		:param none_status: 	the status to replace if status is none
		:return: the collection of SymFeatureLine(s) generated from its executions in document
		"""
		feature_lines = list()
		for sym_execution in document.get_executions():
			feature_lines.append(self.encode_line(sym_execution, used_tests, true_status, false_status, none_status))
		return feature_lines

	def decode_word(self, word_code: int, document: jctest.CDocument):
		"""
		:param word_code:
		:param document:
		:return: int --> string --> status, SymCondition
		"""
		word = self.__corpus__.decode(word_code)
		index = word.index('$')
		status = jcbase.CToken.parse(word[0: index].strip()).get_token_value()
		condition = document.get_conditions_lib().get_condition(word[index + 1: ].strip())
		return status, condition

	def decode_words(self, word_codes, document: jctest.CDocument):
		"""
		:param word_codes:
		:param document:
		:return: the set of symbolic conditions in the set of encoded words
		"""
		conditions = list()
		for word_code in word_codes:
			status, condition = self.decode_word(word_code, document)
			conditions.append(condition)
		return conditions

	def decode_line(self, feature_line: SymFeatureLine, document: jctest.CDocument):
		"""
		:param feature_line:
		:param document: context to parse condition-status pair from word of integer code
		:return: sym_execution, result, [(status, sym_condition)+]
		"""
		sym_execution = document.get_execution(feature_line.get_execution_id())
		result = feature_line.get_result()
		pairs = list()
		for feature_code in feature_line.get_feature_vector():
			status, condition = self.decode_word(feature_code, document)
			pairs.append((status, condition))
		return sym_execution, result, pairs


def select_test_cases_from(document: jctest.CDocument, select_tests: bool, random_proportion: float):
	"""
	:param document:
	:param select_tests: whether the select test cases
	:param random_proportion: the proportion of how many test cases are selected randomly
	:return:
	"""
	if select_tests:
		evaluator = jcmuta.MutationTestEvaluation(document.get_project())
		selected_mutants = evaluator.select_mutants_by_classes(["STRP", "BTRP"])
		mutation_tests = evaluator.select_tests_for_mutants(selected_mutants)
		random_number = int(len(document.get_project().test_space.get_test_cases()) * random_proportion)
		random_tests = evaluator.select_tests_for_random(random_number)
		used_tests = random_tests | mutation_tests
		score = evaluator.measure_score(None, used_tests)
		print("\t==> Select", len(used_tests), "test cases with {}% score.".format(score))
	else:
		used_tests = None
		print("\t==> Select all the possible test inputs from mutation test project...")
	return used_tests


def encoding_feature_files(project_path: str, feature_path: str, feature_file_postfix: str, select_proportion: float):
	"""
	:param select_proportion:
	:param project_path: directory where the feature directories are created
	:param feature_path: the directory where the features are generated
	:param feature_file_postfix: either ".sip" or ".sit"
	:return:
	"""
	for file_name in os.listdir(project_path):
		print("Encoding on", file_name)
		c_directory = os.path.join(project_path, file_name)
		c_document = jctest.CDocument(c_directory, file_name, feature_file_postfix)
		select_tests = select_test_cases_from(c_document,
											  (select_proportion > 0) and (select_proportion <= 1.0),
											  select_proportion)
		sym_library = SymFeatureLines()
		feature_lines = sym_library.encode_lines(c_document, select_tests, None, None, None)
		SymFeatureLine.save(feature_lines, os.path.join(feature_path, file_name + ".x"))
		sym_library.get_word_corpus().save(os.path.join(feature_path, file_name + ".w"))
		print("\t==>", len(c_document.get_mutants()), "mutants",
			  "\t", len(c_document.get_executions()), "executions",
			  "\t", len(sym_library.get_word_corpus()), "words of conditions.")
		print()
	return


def decoding_feature_files(project_path: str, feature_path: str, feature_file_postfix: str):
	"""
	:param project_path:
	:param feature_path:
	:param feature_file_postfix:
	:return: It tests the encoding of feature lines
	"""
	for file_name in os.listdir(project_path):
		print("Decoding on", file_name)
		c_directory = os.path.join(project_path, file_name)
		c_document = jctest.CDocument(c_directory, file_name, feature_file_postfix)
		sym_library = SymFeatureLines()
		sym_library.get_word_corpus().load(os.path.join(feature_path, file_name + ".w"))
		feature_lines = SymFeatureLine.load(os.path.join(feature_path, file_name + ".x"))
		print("\t==>", len(c_document.get_mutants()), "mutants",
			  "\t", len(feature_lines), "feature lines",
			  "\t", len(sym_library.get_word_corpus()), "words of conditions.")
		for feature_line in feature_lines:
			sym_library.decode_line(feature_line, c_document)
	return


class SymPredictionRule:
	"""
	The prediction rule is used to predict the kill-ability of mutant(s) using a set of the
	encoded symbolic conditions as premise.
	"""

	def __init__(self, context):
		"""
		:param context: the collection of all the feature lines used for mining
		"""
		self.context = context
		self.premises = list()	# the collection of integer words encoding conditions used in this premises.
		self.lines = set()		# the collection of SymFeatureLine(s) matching with this rule
		return

	def get_premises(self):
		"""
		:return: 	the collection of (integer) words encoding the symbolic conditions used as premises to predict
					the kill-ability of any mutation(s) being used in a given project.
		"""
		return self.premises

	def get_premise(self, k: int):
		"""
		:param k:
		:return: the kth premise (encoded as integer) of symbolic condition used in the rule
		"""
		premise = self.premises[k]
		premise: int
		return premise

	def get_conditions(self, sym_feature_lib: SymFeatureLines, document: jctest.CDocument):
		"""
		:param sym_feature_lib: used to decode integer code to symbolic condition
		:param document: the document used to generate symbolic conditions used
		:return: the symbolic conditions that the premises represent
		"""
		conditions = list()
		for premise in self.premises:
			status, condition = sym_feature_lib.decode_word(premise, document)
			conditions.append(condition)
		return conditions

	def get_context(self):
		"""
		:return: all the feature lines used in context
		"""
		return self.context

	def get_lines(self):
		"""
		:return: the collection of local SymFeatureLine(s) matching with this rule
		"""
		return self.lines

	def __matching__(self, feature_line: SymFeatureLine):
		"""
		:param feature_line: whether the feature line matches with this rule's premises
		:return:
		"""
		for premise in self.premises:
			if not (premise in feature_line.get_feature_vector()):
				return False
		return True

	def set_lines(self, parent):
		"""
		:param parent: the parent rule from which this rule is created
		:return: update this.lines
		"""
		if parent is None:
			parent_lines = self.context
		else:
			parent: SymPredictionRule
			parent_lines = parent.get_lines()
		self.lines.clear()
		for line in parent_lines:
			line: SymFeatureLine
			if self.__matching__(line):
				self.lines.add(line)
		return

	def __len__(self):
		"""
		:return: the number of premises
		"""
		return len(self.premises)

	def estimate(self):
		"""
		counting the number of killed and alive samples in the local feature lines
		:return: predict_result, support, confidence
		"""
		total, killed, alive = 0, 0, 0
		for line in self.lines:
			line: SymFeatureLine
			if line.get_result():
				killed += 1
			else:
				alive += 1
		total = killed + alive
		if total == 0:
			confidence = 0.0
		else:
			confidence = max(killed, alive) / total
		if killed > alive:
			result = True
		elif killed < alive:
			result = False
		else:
			result = None
		return result, max(killed, alive), confidence

	def classify(self):
		"""
		:return: killed_lines, alive_lines
		"""
		killed_lines, alive_lines = list(), list()
		for line in self.lines:
			line: SymFeatureLine
			if line.get_result():
				killed_lines.append(line)
			else:
				alive_lines.append(line)
		return killed_lines, alive_lines

	def new_child(self, premise: int):
		"""
		create a direct child rule from this rule as parent
		:param premise:
		:return:
		"""
		if not (premise in self.premises):
			child = SymPredictionRule(self.context)
			for old_premise in self.premises:
				child.premises.append(old_premise)
			child.premises.append(premise)
			child.premises.sort()
			return child
		else:
			return self

	def __str__(self):
		return str(self.premises)

	def subsume(self, rule):
		"""
		:param rule:
		:return: subsume
		"""
		rule: SymPredictionRule
		for line in rule.get_lines():
			if not (line in self.lines):
				return False
		return True

	def strict_subsume(self, rule):
		"""
		:param rule:
		:return:
		"""
		rule: SymPredictionRule
		for line in rule.get_lines():
			if not (line in self.lines):
				return False
		return len(self.lines) > len(rule.get_lines())


class SymPredictionRules:
	"""
	It provides interfaces to construct the prediction rules based on symbolic conditions as premises.
	"""

	def __init__(self, all_feature_lines):
		"""
		:param all_feature_lines: the collection of all the feature lines used for mining prediction rules
		"""
		self.all_feature_lines = all_feature_lines
		self.prediction_rules = dict()	# string --> SymPredictionRule
		self.estimation_maps = dict()	# SymPredictionRule --> [result, support, confidence]
		return

	def get_context(self):
		"""
		:return: all the feature lines used in context
		"""
		return self.all_feature_lines

	def get_prediction_rules(self):
		"""
		:return: the set of all the rules produced in this space
		"""
		return self.prediction_rules.values()

	def __unique_rule__(self, parent, rule: SymPredictionRule):
		"""
		:param parent:
		:param rule:
		:return: get the unique instance of the rule using parent to update its data
		"""
		if not (str(rule) in self.prediction_rules):
			self.prediction_rules[str(rule)] = rule
			rule.set_lines(parent)
		rule = self.prediction_rules[str(rule)]
		rule: SymPredictionRule
		return rule

	def get_root(self):
		"""
		:return: [] --> True|False as root rule without any premises
		"""
		rule = SymPredictionRule(self.get_context())
		return self.__unique_rule__(None, rule)

	def get_child(self, parent: SymPredictionRule, premise: int):
		"""
		:param parent:
		:param premise:
		:return: create a child rule extended from parent by adding one premise
		"""
		parent = self.__unique_rule__(None, parent)
		child = parent.new_child(premise)
		return self.__unique_rule__(parent, child)

	def get_rule(self, premises):
		"""
		:param premises: the set of premises used to construct the rule
		:return: construct a rule using specified premises set
		"""
		rule = self.get_root()
		for premise in premises:
			rule = self.get_child(rule, premise)
		return rule

	def estimate(self, rule):
		"""
		:param rule:
		:return: predict_result, support, confidence
		"""
		rule = self.__unique_rule__(None, rule)
		if not (rule in self.estimation_maps):
			predict_result, support, confidence = rule.estimate()
			self.estimation_maps[rule] = (predict_result, support, confidence)
		solution = self.estimation_maps[rule]
		predict_result = solution[0]
		support = solution[1]
		confidence = solution[2]
		predict_result: bool
		support: int
		confidence: float
		return predict_result, support, confidence

	def select_good_rules(self, min_support: int, min_confidence: float, rules=None):
		"""
		:param min_support: minimal support required for good rule
		:param min_confidence: minimal confidence required for good rule
		:param rules: the set of rules being filtered or None for all of those in this library
		:return:
		"""
		good_rules = set()
		if rules is None:
			rules = self.prediction_rules.values()
		for rule in rules:
			rule: SymPredictionRule
			result, support, confidence = self.estimate(rule)
			if support >= min_support and confidence >= min_confidence:
				good_rules.add(rule)
		return good_rules

	def save_rules(self, file_path: str, rules=None):
		"""
		:param file_path: the path of file to preserve the rules and estimations
		:param rules: the collection of prediction rules to be printed
		:return: [rule.premises]	[feature_line.exec_id]
		"""
		if rules is None:
			rules = self.prediction_rules.values()
		with open(file_path, 'w') as writer:
			for rule in rules:
				rule = self.__unique_rule__(None, rule)
				writer.write("$")
				for premise in rule.get_premises():
					writer.write(str(premise) + "$")
				writer.write("\t")
				writer.write("$")
				for line in rule.get_lines():
					line: SymFeatureLine
					writer.write(str(line.get_execution_id()) + "$")	## exec_id is the index of feature line
				writer.write("\n")
		return

	def load_rules(self, file_path: str):
		"""
		:param file_path:
		:return: load rules printed by self.save_rules()
		"""
		rules = list()
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					premise_string = items[0].strip()
					feature_string = items[1].strip()
					premise_items = premise_string.split('$')
					feature_items = feature_string.split('$')

					rule = SymPredictionRule(self.prediction_rules)
					for premise_item in premise_items:
						premise_item = premise_item.strip()
						if len(premise_item) > 0:
							rule = rule.new_child(int(premise_item))

					for feature_item in feature_items:
						feature_item = feature_item.strip()
						if len(feature_item) > 0:
							feature_line = self.all_feature_lines[int(feature_item)]
							rule.lines.add(feature_line)

					rules.append(rule)
		return rules


def encoding_1_prediction_rules(project_path: str, feature_path: str, min_support: int, min_confidence: float):
	"""
	:param project_path:
	:param feature_path:
	:param min_support:
	:param min_confidence:
	:return: generate the good prediction rules and output them to xxx.r1 in feature_path directory
	"""
	for file_name in os.listdir(project_path):
		print("Encoding rules(1) for", file_name)
		feature_file = os.path.join(feature_path, file_name + ".x")
		feature_lines = SymFeatureLine.load(feature_file)
		sym_rules_lib = SymPredictionRules(feature_lines)
		alive_premises = set()
		for feature_line in feature_lines:
			feature_line: SymFeatureLine
			for feature in feature_line.get_feature_vector():
				alive_premises.add(feature)
		for premise in alive_premises:
			sym_rules_lib.get_rule([premise])
		good_rules = sym_rules_lib.select_good_rules(min_support, min_confidence)
		alive_rules = set()
		for rule in good_rules:
			result, support, confidence = rule.estimate()
			if not result:
				alive_rules.add(rule)
		print("\t==> {} lines\t{} premises\t{} rules\t{} alive.".format(len(feature_lines),
																		len(alive_premises),
																		len(good_rules),
																		len(alive_rules)))
		sym_rules_lib.save_rules(os.path.join(feature_path, file_name + ".r1"), alive_rules)
	print()
	return


def decoding_1_prediction_rules(project_path: str, feature_path: str, feature_file_postfix: str):
	"""
	:param feature_file_postfix:
	:param project_path:
	:param feature_path:
	:return:
	"""
	for file_name in os.listdir(project_path):
		print("Decoding rules on", file_name)
		c_directory = os.path.join(project_path, file_name)
		c_document = jctest.CDocument(c_directory, file_name, feature_file_postfix)
		sym_feature_lib = SymFeatureLines()
		sym_feature_lib.get_word_corpus().load(os.path.join(feature_path, file_name + ".w"))
		feature_lines = SymFeatureLine.load(os.path.join(feature_path, file_name + ".x"))
		sym_rule_lib = SymPredictionRules(feature_lines)
		rules = sym_rule_lib.load_rules(os.path.join(feature_path, file_name + ".r1"))
		print("\t--> Load", len(rules), "rules for", len(feature_lines), "lines of", len(c_document.get_mutants()), "mutants.")
		with open(os.path.join(feature_path, file_name + ".s1"), 'w') as writer:
			writer.write("Prediction\tSupport\tConfidence(%)\tPremise\n")
			for rule in rules:
				result, support, confidence = rule.estimate()
				writer.write("{}\t{}\t{}%\t".format(result, support, int(confidence * 10000) / 100.0))
				conditions = rule.get_conditions(sym_feature_lib, c_document)
				if len(conditions) > 0:
					condition = conditions[0]
					writer.write("[{}; {}; {}; \"{}\"; {}]".format(condition.get_category(),
																   condition.get_operator(),
																   condition.get_execution(),
																   condition.get_location().get_cir_code(),
																   condition.get_parameter()))
				writer.write("\n")
	print()
	return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	post_path = "/home/dzt2/Development/Code/git/jcsa/PyMuta/data"

	# 1. generate the feature and word file
	# encoding_feature_files(root_path, post_path, ".sip", 0.01)
	# decoding_feature_files(root_path, post_path, ".sip")

	# 2. generate 1-order prediction rules
	encoding_1_prediction_rules(root_path, post_path, 2, 0.70)
	decoding_1_prediction_rules(root_path, post_path, ".sip")

