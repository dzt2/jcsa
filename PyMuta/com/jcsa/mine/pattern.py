"""
This file implements the data model of RIP execution patterns.
"""


import os
from typing import TextIO
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.muta as jcmuta


NR_CLASS = "NR"			# testing that fails to reach the mutation
NI_CLASS = "NI"			# testing that fails to infect but reaches
NP_CLASS = "NP"			# testing that fails to kill but infect it
KI_CLASS = "KI"			# testing that manages to kill the mutants


class RIPClassifier:
	"""
	It is used to determine the execution class by one of the following categories:
		(1) NR: testing failed to reach the mutated statement (not killed)
		(2) NI: testing reaches mutated statement but failed to infect the program state (not killed)
		(3) NP: testing failed to kill the target mutant even though the program state is infected
		(4) KI: testing manages to kill the target mutant.
	"""

	def __init__(self):
		self.solutions = dict()		# String ==> [nr, ni, np, ki]
		return

	@staticmethod
	def __key_solution__(mutant: jcmuta.Mutant, test):
		"""
		:param mutant:
		:param test:
		:return: String key w.r.t. execution of (m, t)
				(1) (m, None): mid#
				(2) (m, test): mid#tid
		"""
		if test is None:
			return "{}#".format(mutant.get_mut_id())
		else:
			test: jcmuta.TestCase
			return "{}#{}".format(mutant.get_mut_id(), test.get_test_id())

	def __get_solution__(self, key: str):
		"""
		:param key:
		:return: solution w.r.t. the string key
		"""
		solution = self.solutions[key]
		nr = solution[0]
		ni = solution[1]
		np = solution[2]
		ki = solution[3]
		nr: int
		ni: int
		np: int
		ki: int
		return nr, ni, np, ki

	def __set_solution__(self, mutant: jcmuta.Mutant, test, key: str):
		"""
		:param mutant:
		:param test:
		:param key:
		:return:
		"""
		nr, ni, np, ki = 0, 0, 0, 0
		s_result = mutant.get_result()
		w_result = mutant.get_weak_mutant().get_result()
		c_result = mutant.get_coverage_mutant().get_result()
		if test is None:
			if s_result.is_killable():
				ki += 1
			elif w_result.is_killable():
				np += 1
			elif c_result.is_killable():
				ni += 1
			else:
				nr += 1
		else:
			if s_result.is_killed_by(test):
				ki += 1
			elif w_result.is_killed_by(test):
				np += 1
			elif c_result.is_killed_by(test):
				ni += 1
			else:
				nr += 1
		self.solutions[key] = (nr, ni, np, ki)
		return

	def __counting__(self, sample):
		"""
		:param sample: either Mutant or RIPExecution
		:return: 	nr, ni, np, ki
					(1) nr: number of testing that fails to reach the target mutation;
					(2) ni: number of testing that fails to infect program state but reached the mutant;
					(3) np: number of testing that fails to kill even though program state is infected;
					(4) ki: number of testing that manages to kill the target mutation;
		"""
		if isinstance(sample, jcmuta.Mutant):
			sample: jcmuta.Mutant
			mutant = sample
			test = None
		else:
			sample: jcmuta.RIPExecution
			mutant = sample.get_mutant()
			test = sample.get_test()
		key = RIPClassifier.__key_solution__(mutant, test)
		if not(key in self.solutions):
			self.__set_solution__(mutant, test, key)
		return self.__get_solution__(key)

	def __classify__(self, sample):
		"""
		:param sample: Mutant or RIPExecution
		:return: 	NR: the testing failed to reach the target mutant
					NI: the testing failed to infect the program state but reach mutation
					NP: the testing failed to kill the mutant even when infect state
					KI: the testing manages to kill the target mutation
		"""
		nr, ni, np, ki = self.__counting__(sample)
		if ki > 0:
			return KI_CLASS
		elif np > 0:
			return NP_CLASS
		elif ni > 0:
			return NI_CLASS
		else:
			return NR_CLASS

	def counting(self, samples):
		"""
		:param samples: set of Mutant(s) or RIPExecution(s)
		:return: 	nr, ni, np, ki, uk, cc
					(1) nr: number of testing that fails to reach the target mutation;
					(2) ni: number of testing that fails to infect program state but reached the mutant;
					(3) np: number of testing that fails to kill even though program state is infected;
					(4) ki: number of testing that manages to kill the target mutation;
					(5) uk: number of testing that fails to kill the mutant {nr + ni + np}
					(6) cc: number of testing that fails to kill but reach the mutant {ni + np}
		"""
		nr, ni, np, ki = 0, 0, 0, 0
		for sample in samples:
			lnr, lni, lnp, lki = self.__counting__(sample)
			nr += lnr
			ni += lni
			np += lnp
			ki += lki
		return nr, ni, np, ki, nr + ni + np, ni + np

	def classify(self, samples):
		"""
		:param samples:
		:return: 	NR --> set of samples that are not reached
					NI --> set of samples that are reached but fail to infect
					NP --> set of samples that are infected but fail to kill
					KI --> set of samples that managed to kill the mutants
		"""
		results = dict()
		results[NR_CLASS] = set()
		results[NI_CLASS] = set()
		results[NP_CLASS] = set()
		results[KI_CLASS] = set()
		for sample in samples:
			results[self.__classify__(sample)].add(sample)
		return results

	def estimate(self, samples, uk_or_cc: bool):
		"""
		:param samples:
		:param uk_or_cc: 	True to take non-killed testing as support
							False to take coincidental correctness as support
		:return: total, support, confidence
		"""
		nr, ni, np, ki, uk, cc = self.counting(samples)
		if uk_or_cc:
			support = uk
		else:
			support = cc
		total = support + ki
		if support == 0:
			confidence = 0.0
		else:
			confidence = support / (total + 0.0)
		return total, support, confidence

	def select(self, samples, uk_or_cc: bool):
		"""
		:param samples:
		:param uk_or_cc: 	True to select non-killed samples
							False to select coincidental correct samples
		:return:
		"""
		results = self.classify(samples)
		selects = results[NI_CLASS] | results[NP_CLASS]
		if uk_or_cc:
			selects = selects | results[NR_CLASS]
		return selects


class RIPPattern:
	"""
	The pattern of RIP execution is defined to match a subset of testing along with mutants in the document
	"""

	def __init__(self, document: jcmuta.RIPDocument, classifier: RIPClassifier):
		"""
		:param document: it provides original data samples to be matched
		:param classifier: used to count, classify and estimate the samples in pattern
		"""
		self.document = document
		self.classifier = classifier
		self.executions = set()
		self.mutants = set()
		self.t_words = list()	# words to select samples
		self.f_words = list()	# words to remove samples
		return

	# data samples

	def get_document(self):
		"""
		:return: it provides original data samples to be matched
		"""
		return self.document

	def get_mutants(self):
		"""
		:return: set of mutants of which executions are matched with this pattern
		"""
		return self.mutants

	def get_executions(self):
		"""
		:return: set of executions being matched with the pattern
		"""
		return self.executions

	def get_samples(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: True to select execution or mutants
		:return:
		"""
		if exe_or_mut:
			return self.executions
		else:
			return self.mutants

	# evaluation by classifier

	def get_classifier(self):
		"""
		:return: used to count, classify and estimate the samples in pattern
		"""
		return self.classifier

	def counting(self, exe_or_mut: bool):
		return self.classifier.counting(self.get_samples(exe_or_mut))

	def classify(self, exe_or_mut: bool):
		return self.classifier.classify(self.get_samples(exe_or_mut))

	def estimate(self, exe_or_mut: bool, uk_or_cc: bool):
		return self.classifier.estimate(self.get_samples(exe_or_mut), uk_or_cc)

	def select(self, exe_or_mut: bool, uk_or_cc: bool):
		return self.classifier.select(self.get_samples(exe_or_mut), uk_or_cc)

	# feature model

	def get_words(self, select_or_remove: bool):
		"""
		:param select_or_remove:
								True to get selecting words
								False to get removing words
		:return:
		"""
		if select_or_remove:
			return self.t_words
		else:
			return self.f_words

	def get_conditions(self, select_or_remove: bool):
		"""
		:param select_or_remove:
								True to get selecting conditions
								False to get removing conditions
		:return:
		"""
		words = self.get_words(select_or_remove)
		conditions = list()
		for word in words:
			conditions.append(self.document.decode(word))
		return conditions

	def __str__(self):
		return str(self.t_words) + str(self.f_words)

	def __len__(self):
		return len(self.t_words) + len(self.f_words)

	def __matching__(self, execution: jcmuta.RIPExecution):
		"""
		:param execution:
		:return:
		"""
		for word in self.t_words:
			if not(word in execution.get_words()):
				return False
		for word in self.f_words:
			if word in execution.get_words():
				return False
		return True

	def set_samples(self, parent):
		"""
		:param parent: parent pattern that generates this one
		:return:
		"""
		if parent is None:
			executions = self.document.get_executions()
		else:
			parent: RIPPattern
			executions = parent.get_executions()
		self.executions.clear()
		self.mutants.clear()
		for execution in executions:
			execution: jcmuta.RIPExecution
			if self.__matching__(execution):
				self.executions.add(execution)
				self.mutants.add(execution.get_mutant())
		return

	# relations

	def extends(self, word: str, select_or_remove: bool):
		"""
		:param word:
		:param select_or_remove:
		:return: child pattern extended from this pattern but parent if word is invalid
		"""
		word = word.strip()
		if len(word) > 0:
			if not(word in self.t_words) and not(word in self.f_words):
				child = RIPPattern(self.document, self.classifier)
				for t_word in self.t_words:
					child.t_words.append(t_word)
				for f_word in self.f_words:
					child.f_words.append(f_word)
				if select_or_remove:
					child.t_words.append(word)
				else:
					child.f_words.append(word)
				child.t_words.sort()
				child.f_words.sort()
				return child
		return self

	def subsume(self, pattern):
		"""
		:param pattern:
		:return: True if samples matched with the pattern are also matched by this one
		"""
		pattern: RIPPattern
		for execution in pattern.get_executions():
			if execution in self.executions:
				continue
			else:
				return False
		if len(pattern.get_executions()) == len(self.get_executions()):
			return len(self) <= len(pattern)	# Depth Subsume
		else:
			return True		# Sound Subsume

	@staticmethod
	def __print_condition__(condition: jcmuta.RIPCondition):
		"""
		:param condition:
		:return:
		"""
		category = condition.get_category()
		operator = condition.get_operator()
		execution = str(condition.get_execution())
		location = "\"" + condition.get_location().get_cir_code() + "\""
		if condition.get_parameter() is None:
			parameter = "null"
		else:
			parameter = "[" + condition.get_parameter().get_code() + "]"
		return "({}:{}:{}:{}:{})".format(category, operator, execution, location, parameter)

	def print(self, split: str):
		text = ""
		for condition in self.get_conditions(True):
			text += RIPPattern.__print_condition__(condition)
			text += split
		for condition in self.get_conditions(False):
			text += "NOT" + RIPPattern.__print_condition__(condition)
			text += split
		text += "#EOF"
		return text


class RIPPatternSpace:
	"""
	It maintains the patterns of RIP-testability features generated from mining algorithms
	"""

	def __init__(self, document: jcmuta.RIPDocument, classifier: RIPClassifier, good_patterns: set):
		"""
		:param document: it provides original data samples for being classified and mined
		:param classifier: used to estimate the performance of generated RIP-patterns
		:param good_patterns: set of RIP-testability pattern being generated from program
		"""
		self.document = document
		self.doc_executions = set()
		self.doc_mutants = set()
		for execution in document.get_executions():
			execution: jcmuta.RIPExecution
			self.doc_executions.add(execution)
			self.doc_mutants.add(execution.get_mutant())
		self.classifier = classifier
		self.all_patterns = set()
		self.pat_executions = set()
		self.pat_mutants = set()
		for pattern in good_patterns:
			pattern: RIPPattern
			self.all_patterns.add(pattern)
			for execution in pattern.get_executions():
				execution: jcmuta.RIPExecution
				self.pat_executions.add(execution)
				self.pat_mutants.add(execution.get_mutant())
		self.sub_patterns = RIPPatternSpace.select_subsuming_patterns(self.all_patterns)
		return

	# data getters

	def get_document(self):
		return self.document

	def get_doc_executions(self):
		return self.doc_executions

	def get_doc_mutants(self):
		return self.doc_mutants

	def get_classifier(self):
		return self.classifier

	def get_patterns(self):
		return self.all_patterns

	def get_pat_executions(self):
		return self.pat_executions

	def get_pat_mutants(self):
		return self.pat_mutants

	def get_subsuming_patterns(self):
		return self.sub_patterns

	# selection algorithm

	@staticmethod
	def select_subsuming_patterns(patterns):
		"""
		:param patterns: set of RIP-testability patterns
		:return: minimal set of patterns that subsume the others
		"""
		remain_patterns, remove_patterns, minimal_patterns = set(), set(), set()
		patterns = jcbase.rand_resort(patterns)
		for pattern in patterns:
			pattern: RIPPattern
			remain_patterns.add(pattern)
		while len(remain_patterns) > 0:
			subsume_pattern = None
			remove_patterns.clear()
			for pattern in remain_patterns:
				if subsume_pattern is None:
					subsume_pattern = pattern
					remove_patterns.add(pattern)
				elif subsume_pattern.subsume(pattern):
					remove_patterns.add(pattern)
				elif pattern.subsume(subsume_pattern):
					subsume_pattern = pattern
					remove_patterns.add(pattern)
			for pattern in remove_patterns:
				remain_patterns.remove(pattern)
			if not(subsume_pattern is None):
				minimal_pattern = subsume_pattern
				minimal_pattern: RIPPattern
				minimal_patterns.add(minimal_pattern)
		return minimal_patterns

	@staticmethod
	def remap_keys_patterns(patterns, exe_or_mut: bool):
		"""
		:param patterns:
		:param exe_or_mut: True to use RIPExecution or Mutant as key
		:return: Sample ==> set of RIPPattern
		"""
		results = dict()
		for pattern in patterns:
			pattern: RIPPattern
			samples = pattern.get_samples(exe_or_mut)
			for sample in samples:
				if not(sample in results):
					results[sample] = set()
				results[sample].add(pattern)
		return results

	@staticmethod
	def select_best_pattern(patterns, exe_or_mut: bool, uk_or_cc: bool):
		"""
		:param patterns:
		:param exe_or_mut:
		:param uk_or_cc:
		:return:
		"""
		remain_patterns, solutions = set(), dict()
		for pattern in patterns:
			pattern: RIPPattern
			remain_patterns.add(pattern)
			total, support, confidence = pattern.estimate(exe_or_mut, uk_or_cc)
			length = len(pattern)
			solutions[pattern] = (length, support, confidence)

		remain_length = max(1, int(len(remain_patterns) / 2))
		while len(remain_patterns) > remain_length:
			worst_confidence, worst_pattern = 1.0, None
			for pattern in remain_patterns:
				solution = solutions[pattern]
				confidence = solution[2]
				if worst_pattern is None or confidence <= worst_confidence:
					worst_confidence = confidence
					worst_pattern = pattern
			remain_patterns.remove(worst_pattern)

		remain_length = max(1, int(len(remain_patterns) / 2))
		while len(remain_patterns) > remain_length:
			worst_support, worst_pattern = 99999, None
			for pattern in remain_patterns:
				solution = solutions[pattern]
				support = solution[1]
				if worst_pattern is None or support <= worst_support:
					worst_support = support
					worst_pattern = pattern
			remain_patterns.remove(worst_pattern)

		remain_length = max(1, int(len(remain_patterns) / 2))
		while len(remain_patterns) > remain_length:
			worst_length, worst_pattern = 0, None
			for pattern in remain_patterns:
				solution = solutions[pattern]
				length = solution[0]
				if worst_pattern is None or length >= worst_length:
					worst_length = length
					worst_pattern = pattern
			remain_patterns.remove(worst_pattern)

		for pattern in remain_patterns:
			return pattern
		return None

	@staticmethod
	def select_minimal_patterns(patterns, exe_or_mut: bool):
		"""
		:param patterns:
		:param exe_or_mut: True to cover RIPExecution or Mutant
		:return: minimal set of patterns covering all the executions in the set
		"""
		keys_patterns = RIPPatternSpace.remap_keys_patterns(patterns, exe_or_mut)
		minimal_patterns, removed_keys = set(), set()
		while len(keys_patterns) > 0:
			removed_keys.clear()
			for sample, patterns in keys_patterns.items():
				selected_pattern = jcbase.rand_select(patterns)
				if not (selected_pattern is None):
					pattern = selected_pattern
					pattern: RIPPattern
					for pat_sample in pattern.get_samples(exe_or_mut):
						removed_keys.add(pat_sample)
					minimal_patterns.add(pattern)
					break
			for sample in removed_keys:
				if sample in keys_patterns:
					keys_patterns.pop(sample)
		return minimal_patterns

	def select_best_patterns(self, exe_or_mut: bool, uk_or_cc: bool):
		"""
		:param exe_or_mut: used to estimate
		:param uk_or_cc: used to estimate
		:return: mapping from mutant to the pattern that best matches with the mutant
		"""
		mutants_patterns = RIPPatternSpace.remap_keys_patterns(self.all_patterns, False)
		best_patterns = dict()
		for mutant, patterns in mutants_patterns.items():
			mutant: jcmuta.Mutant
			best_pattern = RIPPatternSpace.select_best_pattern(patterns, exe_or_mut, uk_or_cc)
			if not (best_pattern is None):
				best_pattern: RIPPattern
				best_patterns[mutant] = best_pattern
		return best_patterns


class RIPPatternFactory:
	"""
	It manages the patterns and its estimate scores.
	"""

	def __init__(self, document: jcmuta.RIPDocument, classifier: RIPClassifier,
				 exe_or_mut: bool, uk_or_cc: bool):
		self.document = document
		self.classifier = classifier
		self.exe_or_mut = exe_or_mut
		self.uk_or_cc = uk_or_cc
		self.patterns = dict()	# String ==> RIPPattern
		self.estimate = dict()	# RIPPattern ==> [total, support, confidence]
		return

	def get_document(self):
		"""
		:return:
		"""
		return self.document

	def get_classifier(self):
		"""
		:return:
		"""
		return self.classifier

	def get_pattern(self, parent, word: str, select_or_remove: bool):
		"""
		:param parent:
		:param word:
		:param select_or_remove:
		:return: unique pattern w.r.t. the parent
					pattern, total, support, confidence
		"""
		if parent is None:
			pattern = RIPPattern(self.document, self.classifier)
			pattern = pattern.extends(word, select_or_remove)
		else:
			parent: RIPPattern
			pattern = parent.extends(word, select_or_remove)
		if not(str(pattern) in self.patterns):
			self.patterns[str(pattern)] = pattern
			pattern.set_samples(parent)
		pattern = self.patterns[str(pattern)]
		pattern: RIPPattern
		if not(pattern in self.estimate):
			total, support, confidence = pattern.estimate(self.exe_or_mut, self.uk_or_cc)
			self.estimate[pattern] = (total, support, confidence)
		solution = self.estimate[pattern]
		total = solution[0]
		support = solution[1]
		confidence = solution[2]
		total: int
		support: int
		confidence: float
		return pattern, total, support, confidence


class RIPPatternWriter:
	"""
	It writes the information of RIP-patterns to the output file for reviewing.
	"""

	def __init__(self):
		self.writer = None
		return

	def output(self, text: str):
		self.writer: TextIO
		self.writer.write(text)
		return

	@staticmethod
	def __percentage__(ratio: float):
		return int(ratio * 1000000) / 10000.0

	@staticmethod
	def __proportion__(x: int, y: int):
		if x == 0:
			ratio = 0.0
		else:
			ratio = x / (y + 0.0)
		return RIPPatternWriter.__percentage__(ratio)

	@staticmethod
	def __f1_measure__(doc_samples: set, pat_samples: set):
		"""
		:param doc_samples: samples from document
		:param pat_samples: samples matched with pattern
		:return: precision, recall, f1_score
		"""
		int_samples = doc_samples & pat_samples
		common = len(int_samples)
		if common > 0:
			precision = common / len(pat_samples)
			recall = common / len(doc_samples)
			f1_score = 2 * precision * recall / (precision + recall)
		else:
			precision = 0.0
			recall = 0.0
			f1_score = 0.0
		return precision, recall, f1_score

	# pattern writing

	def __write_pattern_summary__(self, pattern: RIPPattern):
		"""
		:param pattern:
		:return:
				Summary 	Length Executions Mutations
				Counting	title UR UI UP KI UK CC
				Estimate	title total support confidence
		"""
		# Summary Length Executions Mutants
		length = len(pattern)
		executions = len(pattern.get_executions())
		mutants = len(pattern.get_mutants())
		self.output("\t{}\t{}: {}\t{}: {}\t{}: {}\n".format("Summary",
															"Length", length,
															"Executions", executions,
															"Mutants", mutants))
		self.output("\n")

		# Counting title UR UI UP KI UK CC
		template = "\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n"
		self.output(template.format("Counting", "Title", "UR", "UI", "UP", "KI", "UK", "CC"))
		ur, ui, up, ki, uk, cc = pattern.counting(True)
		self.output(template.format("", "Executions", ur, ui, up, ki, uk, cc))
		ur, ui, up, ki, uk, cc = pattern.counting(False)
		self.output(template.format("", "Mutants", ur, ui, up, ki, uk, cc))
		self.output("\n")

		# Estimate title total support confidence(%)
		template = "\t{}\t{}\t{}\t{}\t{}\n"
		self.output(template.format("Estimate", "Title", "Total", "Support", "Confidence (%)"))
		total, support, confidence = pattern.estimate(True, True)
		self.output(template.format("", "UK_Executions", total, support, confidence))
		total, support, confidence = pattern.estimate(True, False)
		self.output(template.format("", "CC_Executions", total, support, confidence))
		total, support, confidence = pattern.estimate(False, True)
		self.output(template.format("", "UK_Mutants", total, support, confidence))
		total, support, confidence = pattern.estimate(False, False)
		self.output(template.format("", "CC_Mutants", total, support, confidence))
		self.output("\n")

		return

	def __write_pattern_feature__(self, pattern: RIPPattern):
		"""
		:param pattern:
		:return: select condition category operator validate execution statement location parameter
		"""
		template = "\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n"
		self.output(template.format("Select", "Condition", "Category", "Operator", "Validate",
									"Execution", "Statement", "Location", "Parameter"))
		index = 0
		for feature in pattern.get_conditions(True):
			index += 1
			category = feature.get_category()
			operator = feature.get_operator()
			validate = feature.get_validate()
			execution = feature.get_execution()
			statement = feature.get_execution().get_statement().get_cir_code()
			location = feature.get_location().get_cir_code()
			if feature.get_parameter() is None:
				parameter = ""
			else:
				parameter = feature.get_parameter().get_code()
			self.output(template.format(True, index, category, operator, validate,
										execution, statement, location, parameter))
		for feature in pattern.get_conditions(False):
			index += 1
			category = feature.get_category()
			operator = feature.get_operator()
			validate = feature.get_validate()
			execution = feature.get_execution()
			statement = feature.get_execution().get_statement().get_cir_code()
			location = feature.get_location().get_cir_code()
			if feature.get_parameter() is None:
				parameter = ""
			else:
				parameter = feature.get_parameter().get_code()
			self.output(template.format(False, index, category, operator, validate,
										execution, statement, location, parameter))
		self.output("\n")

	def __write_pattern_mutants__(self, pattern: RIPPattern):
		"""
		:param pattern:
		:return: Mutant Result Class Operator Line Location Parameter
		"""
		template = "\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n"
		self.output(template.format("ID", "Result", "Class", "Operator", "Line", "Location", "Parameter"))
		for mutant in pattern.get_mutants():
			mutant: jcmuta.Mutant
			mutant_id = mutant.get_mut_id()
			result = pattern.get_classifier().__classify__(mutant)
			mutation_class = mutant.get_mutation().get_mutation_class()
			operator = mutant.mutation.get_mutation_operator()
			location = mutant.mutation.get_location()
			parameter = mutant.mutation.get_parameter()
			line = location.line_of(False)
			code = location.get_code(True)
			self.output(template.format(mutant_id, result, mutation_class, operator, line, code, parameter))
		self.output("\n")

	def __write_pattern__(self, pattern: RIPPattern):
		self.output("#BEG\n")
		self.__write_pattern_summary__(pattern)
		self.__write_pattern_feature__(pattern)
		self.__write_pattern_mutants__(pattern)
		self.output("#END\n")

	def write_patterns(self, patterns, file_path: str):
		with open(file_path, 'w') as writer:
			self.writer = writer
			for pattern in patterns:
				self.__write_pattern__(pattern)
		return

	def write_matching(self, space: RIPPatternSpace, file_path: str, exe_or_mut: bool, uk_or_cc: bool):
		"""
		:param space:
		:param uk_or_cc:
		:param exe_or_mut:
		:param file_path:
		:return: 	Mutant 	ID RESULT CLASS OPERATOR LINE LOCATION PARMETER
					Pattern
					Category Operator Validate Execution Statement Location Parameter*
		"""
		mutants_patterns = space.select_best_patterns(exe_or_mut, uk_or_cc)
		with open(file_path, 'w') as writer:
			self.writer = writer
			for mutant, pattern in mutants_patterns.items():
				mutant_id = mutant.get_mut_id()
				result = pattern.get_classifier().__classify__(mutant)
				mutation_class = mutant.get_mutation().get_mutation_class()
				operator = mutant.get_mutation().get_mutation_operator()
				location = mutant.get_mutation().get_location()
				parameter = mutant.get_mutation().get_parameter()
				line = location.line_of(False)
				code = location.get_code(True)
				self.output("{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("Mutant", mutant_id, result,
																	  mutation_class, operator, line, code, parameter))
				self.__write_pattern_feature__(pattern)
				self.output("\n")
		return

	@staticmethod
	def __evaluate__(document: jcmuta.RIPDocument, patterns, exe_or_mut: bool, uk_or_cc: bool, classifier: RIPClassifier):
		"""
		:param document:
		:param patterns:
		:return: length doc_samples pat_samples reduce precision recall f1_score
		"""
		length = len(patterns)
		if exe_or_mut:
			doc_samples = classifier.select(document.get_executions(), uk_or_cc)
		else:
			doc_samples = classifier.select(document.get_mutants(), uk_or_cc)
		pat_samples = set()
		for pattern in patterns:
			pattern: RIPPattern
			samples = pattern.get_samples(exe_or_mut)
			for sample in samples:
				pat_samples.add(sample)
		reduce = length / (len(doc_samples) + 0.0)
		precision, recall, f1_score = RIPPatternWriter.__f1_measure__(doc_samples, pat_samples)
		return length, len(doc_samples), len(pat_samples), reduce, precision, recall, f1_score

	def __write_evaluate_all__(self, space: RIPPatternSpace):
		document = space.get_document()
		patterns = space.get_subsuming_patterns()
		classifier = space.get_classifier()

		self.output("# Cost-Effective Analysis #\n")
		template = "\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n"
		self.output(template.format("title", "LEN", "DOC", "PAT", "REDUCE(%)", "PRECISION(%)", "RECALL(%)", "F1_SCORE"))

		length, doc_number, pat_number, reduce_rate, precision, recall, f1_score = \
			RIPPatternWriter.__evaluate__(document, patterns, True, True, classifier)
		self.output(template.format("UK_EXE", length, doc_number, pat_number,
									RIPPatternWriter.__percentage__(reduce_rate),
									RIPPatternWriter.__percentage__(precision),
									RIPPatternWriter.__percentage__(recall),
									f1_score))

		length, doc_number, pat_number, reduce_rate, precision, recall, f1_score = \
			RIPPatternWriter.__evaluate__(document, patterns, True, False, classifier)
		self.output(template.format("CC_EXE", length, doc_number, pat_number,
									RIPPatternWriter.__percentage__(reduce_rate),
									RIPPatternWriter.__percentage__(precision),
									RIPPatternWriter.__percentage__(recall),
									f1_score))

		length, doc_number, pat_number, reduce_rate, precision, recall, f1_score = \
			RIPPatternWriter.__evaluate__(document, patterns, False, True, classifier)
		self.output(template.format("UK_MUT", length, doc_number, pat_number,
									RIPPatternWriter.__percentage__(reduce_rate),
									RIPPatternWriter.__percentage__(precision),
									RIPPatternWriter.__percentage__(recall),
									f1_score))

		length, doc_number, pat_number, reduce_rate, precision, recall, f1_score = \
			RIPPatternWriter.__evaluate__(document, patterns, False, False, classifier)
		self.output(template.format("CC_MUT", length, doc_number, pat_number,
									RIPPatternWriter.__percentage__(reduce_rate),
									RIPPatternWriter.__percentage__(precision),
									RIPPatternWriter.__percentage__(recall),
									f1_score))

		self.output("\n")
		return

	def __write_evaluate_one__(self, index: int, pattern: RIPPattern):
		"""
		:param pattern:
		:return: index length executions mutants uk_exe_supp uk_exe_conf cc_exe_supp cc_exe_conf uk_mut_supp
				uk_mut_conf cc_mut_supp cc_mut_conf
		"""
		executions = len(pattern.get_executions())
		mutants = len(pattern.get_mutants())
		_, uk_exe_supp, uk_exe_conf = pattern.estimate(True, True)
		_, cc_exe_supp, cc_exe_conf = pattern.estimate(True, False)
		_, uk_mut_supp, uk_mut_conf = pattern.estimate(False, True)
		_, cc_mut_supp, cc_mut_conf = pattern.estimate(False, False)
		self.output("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format(index, executions, mutants,
																			uk_exe_supp,
																			RIPPatternWriter.__percentage__(uk_exe_conf),
																			cc_exe_supp,
																			RIPPatternWriter.__percentage__(cc_exe_conf),
																			uk_mut_supp,
																			RIPPatternWriter.__percentage__(uk_mut_conf),
																			cc_mut_supp,
																			RIPPatternWriter.__percentage__(cc_mut_conf)
																			)
					)
		return

	def write_evaluate(self, space: RIPPatternSpace, file_path: str):
		"""
		:param space:
		:param file_path:
		:return:
			# Cost-Effective Analysis
			title	LEN DOC PAT REDUCE(%) PRECISION(%) RECALL(%) F1_SCORE
			UK_EXE
			CC_EXE
			UK_MUT
			CC_MUT
		"""
		with open(file_path, 'w') as writer:
			self.writer = writer
			self.__write_evaluate_all__(space)
			self.output("# Pattern Evaluate #\n")
			self.output("\tindex\tlength\texecutions\tmutants\tuk_exe_supp\tuk_exe_conf(%)\tcc_exe_supp\tcc_exe_conf(%)"
						"\tuk_mut_supp\tuk_mut_conf(%)\tcc_mut_supp\tcc_mut_conf(%)\n")
			index = 0
			for pattern in space.get_subsuming_patterns():
				index += 1
				self.__write_evaluate_one__(index, pattern)
		return

