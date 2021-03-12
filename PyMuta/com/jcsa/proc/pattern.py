"""
This file defines the structural description of RIP-based patterns and mining algorithms to identify them from RIP-based
executions and testing process.
"""


import os
from typing import TextIO
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.muta as jcmuta
import com.jcsa.proc.feature as jcfeat
import pydotplus
from sklearn import tree
from sklearn import metrics
from scipy import sparse


class RIPPattern:
	"""
	It describes the structure of patterns to match RIP-based executions in mutation testing.
	"""

	def __init__(self, document: jcfeat.RIPDocument, classifier: jcfeat.RIPClassifier):
		"""
		:param document: It provides the entire dataset for being matched by the pattern
		:param classifier: It is used to estimate the performance of the pattern matching
		"""
		self.document = document		# It provides the entire dataset for being matched by the pattern
		self.classifier = classifier	# It is used to evaluate the performance of the pattern in mining
		self.words = list()				# It defines the conditions being required in executions matched
		self.executions = set()			# The set of executions in RIP terms being matched by this ones
		self.mutants = set()			# The set of mutants of which executions are matched by this one
		return

	# data getters

	def get_document(self):
		"""
		:return: It provides the entire dataset for being matched by the pattern
		"""
		return self.document

	def get_executions(self):
		"""
		:return: The set of executions in RIP terms being matched by this ones
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: The set of mutants of which executions are matched by this one
		"""
		return self.mutants

	def get_samples(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: True to return executions or mutants
		:return:
		"""
		if exe_or_mut:
			return self.executions
		else:
			return self.mutants

	# estimations

	def get_classifier(self):
		"""
		:return: It is used to evaluate the performance of the pattern in mining
		"""
		return self.classifier

	def counting(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: True to count on executions or mutants
		:return: nr, ni, np, ki, uk, cc
		"""
		return self.classifier.counting(self.get_samples(exe_or_mut))

	def classify(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: True to classify executions or mutants
		:return: Mapping from class name to the samples it match with
		"""
		return self.classifier.classify(self.get_samples(exe_or_mut))

	def estimate(self, exe_or_mut: bool, uk_or_cc: bool):
		"""
		:param exe_or_mut: True to estimate on executions or mutants
		:param uk_or_cc: True to take non-killed or coincidental correct as supports
		:return: total, support, confidence
		"""
		return self.classifier.estimate(self.get_samples(exe_or_mut), uk_or_cc)

	def select(self, exe_or_mut: bool, uk_or_cc: bool):
		"""
		:param exe_or_mut: True to select on executions or mutants
		:param uk_or_cc: True to select non-killed or coincidental correct ones
		:return:
		"""
		return self.classifier.select(self.get_samples(exe_or_mut), uk_or_cc)

	# features

	def get_words(self):
		"""
		:return: It defines the conditions being required in executions matched
		"""
		return self.words

	def get_conditions(self):
		"""
		:return: It defines the conditions being required in executions matched
		"""
		conditions = list()
		for word in self.words:
			conditions.append(self.document.get_condition(word))
		return conditions

	def __str__(self):
		return str(self.words)

	def __len__(self):
		return len(self.words)

	# parent setters

	def extends(self, word: str):
		"""
		:param word: new word being added to the pattern
		:return: an empty child pattern extended from this one by adding one word (samples are not updated)
		"""
		word = word.strip()
		if len(word) > 0 and not(word in self.words):
			child = RIPPattern(self.document, self.classifier)
			for old_word in self.words:
				child.words.append(old_word)
			child.words.append(word)
			child.words.sort()
			return child
		return self

	def __matching__(self, execution: jcfeat.RIPExecution):
		"""
		:param execution:
		:return: True if all the conditions in the pattern are included in the execution
		"""
		for word in self.words:
			if not(word in execution.get_words()):
				return False
		return True

	def set_samples(self, parent):
		"""
		:param parent: pattern to update samples or None to update on all the document
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
			execution: jcfeat.RIPExecution
			if self.__matching__(execution):
				self.executions.add(execution)
				self.mutants.add(execution.get_mutant())
		return

	def subsume(self, pattern, uk_or_cc: bool):
		"""
		:param pattern:
		:param uk_or_cc: 	True to match on non-killed
							False to match on coincidental correct ones
							None to match on the entire executions
		:return: True if all the executions matched by pattern are matched by this one
		"""
		pattern: RIPPattern
		if uk_or_cc is None:
			ori_executions = self.executions
			sub_executions = pattern.get_executions()
		else:
			ori_executions = self.select(True, uk_or_cc)
			sub_executions = pattern.select(True, uk_or_cc)
		for execution in sub_executions:
			if not (execution in ori_executions):
				return False
		if len(ori_executions) == len(sub_executions):
			return len(self) <= len(pattern)  # equivalence
		return True  # strictly subsumes


class RIPMineContext:
	"""
	It provides the contextual information used for pattern mining and interfaces to produce unique instance of
	RIP-patterns in project under test.
	"""

	def __init__(self, document: jcfeat.RIPDocument, exe_or_mut: bool, uk_or_cc: bool,
				 min_support: int, min_confidence: float, max_confidence: float,
				 max_length: int):
		"""
		:param document: it provides the basic information for evaluating pattern mining
		:param exe_or_mut: True to take executions as samples or mutants for estimations
		:param uk_or_cc: True to take non-killed samples as support or coincidental correct
		:param min_support: minimal number of supports needed to generate patterns
		:param min_confidence: minimal confidence to select the better patterns generated
		:param max_confidence: maximal confidence to terminate the pattern generations
		:param max_length: maximal length of features in generated patterns
		"""
		self.document = document
		self.classifier = jcfeat.RIPClassifier()  # to estimate the patterns
		self.patterns = dict()  # String ==> RIPPattern (unique instance)
		self.solution = dict()  # RIPPattern ==> [total, support, confidence]
		# parameters
		self.exe_or_mut = exe_or_mut
		self.uk_or_cc = uk_or_cc
		self.min_support = min_support
		self.min_confidence = min_confidence
		self.max_confidence = max_confidence
		self.max_length = max_length
		return

	# getters

	def get_document(self):
		return self.document

	def get_classifier(self):
		return self.classifier

	def is_exe_or_mut(self):
		return self.exe_or_mut

	def is_uk_or_cc(self):
		return self.uk_or_cc

	def get_min_support(self):
		return self.min_support

	def get_min_confidence(self):
		return self.min_confidence

	def get_max_confidence(self):
		return self.max_confidence

	def get_max_length(self):
		return self.max_length

	# factory

	def __new_pattern__(self, parent, word: str):
		"""
		:param parent:
		:param word:
		:return: the unique instance of pattern extended from parent by adding one word
		"""
		if parent is None:
			pattern = RIPPattern(self.document, self.classifier)
			pattern = pattern.extends(word)
		else:
			parent: RIPPattern
			pattern = parent.extends(word)
		if not(str(pattern) in self.patterns):
			self.patterns[str(pattern)] = pattern
			pattern.set_samples(parent)
		pattern = self.patterns[str(pattern)]
		pattern: RIPPattern
		return pattern

	def new_root(self, word: str):
		return self.__new_pattern__(None, word)

	def new_child(self, parent: RIPPattern, word: str):
		return self.__new_pattern__(parent, word)

	def estimate(self, pattern: RIPPattern):
		"""
		:param pattern:
		:return: total, support, confidence
		"""
		if not(pattern in self.solution):
			total, support, confidence = pattern.estimate(self.exe_or_mut, self.uk_or_cc)
			self.solution[pattern] = (total, support, confidence)
		solution = self.solution[pattern]
		total = solution[0]
		support = solution[1]
		confidence = solution[2]
		total: int
		support: int
		confidence: float
		return total, support, confidence

	def extract_good_patterns(self):
		"""
		:return: the set of patterns available with the parameters in the context
		"""
		good_patterns = set()
		for pattern, solution in self.solution.items():
			pattern: RIPPattern
			support = solution[1]
			confidence = solution[2]
			length = len(pattern)
			if length <= self.max_length and support >= self.min_support and confidence >= self.min_confidence:
				good_patterns.add(pattern)
		return good_patterns


class RIPPatternSpace:
	"""
	The space preserves the patterns generated and selected from mining algorithms.
	"""

	def __init__(self, document: jcfeat.RIPDocument, classifier: jcfeat.RIPClassifier, good_patterns):
		"""
		:param document: it provides original data samples for being classified and mined
		:param classifier: used to estimate the performance of generated RIP-patterns
		:param good_patterns: set of RIP-testability pattern being generated from program
		"""
		self.document = document
		self.doc_executions = set()
		self.doc_mutants = set()
		for execution in document.get_executions():
			execution: jcfeat.RIPExecution
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
				execution: jcfeat.RIPExecution
				self.pat_executions.add(execution)
				self.pat_mutants.add(execution.get_mutant())
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

	# selecting methods

	@staticmethod
	def select_subsuming_patterns(patterns, uk_or_cc):
		"""
		:param patterns: set of RIP-testability patterns
		:param uk_or_cc: None to consider all executions or select corresponding class
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
				elif subsume_pattern.subsume(pattern, uk_or_cc):
					remove_patterns.add(pattern)
				elif pattern.subsume(subsume_pattern, uk_or_cc):
					subsume_pattern = pattern
					remove_patterns.add(pattern)
			for pattern in remove_patterns:
				remain_patterns.remove(pattern)
			if not (subsume_pattern is None):
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
				if not (sample in results):
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

	# data inference

	def get_subsuming_patterns(self, uk_or_cc):
		"""
		:param uk_or_cc: None to consider all executions or select corresponding class
		:return:
		"""
		return RIPPatternSpace.select_subsuming_patterns(self.all_patterns, uk_or_cc)

	def get_minimal_patterns(self, exe_or_mut: bool):
		"""
		:param exe_or_mut:
		:return: the minimal set of RIP patterns covering all the samples in the space
		"""
		return RIPPatternSpace.select_minimal_patterns(self.all_patterns, exe_or_mut)

	def get_best_patterns(self, exe_or_mut: bool, uk_or_cc: bool):
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


class RIPPatternWriter:
	"""
	It implements the writing of RIP conditions patterns.
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
		:return: condition category operator validate execution statement location parameter
		"""
		template = "\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n"
		self.output(template.format("Condition", "Category", "Operator", "Validate",
									"Execution", "Statement", "Location", "Parameter"))
		index = 0
		for feature in pattern.get_conditions():
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
			self.output(template.format(index, category, operator, validate,
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
				self.writer.write("\n")
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
		mutants_patterns = space.get_best_patterns(exe_or_mut, uk_or_cc)
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
	def __evaluate__(document: jcfeat.RIPDocument, patterns, exe_or_mut: bool, uk_or_cc: bool,
					 classifier: jcfeat.RIPClassifier):
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

	def __write_evaluate_all__(self, space: RIPPatternSpace, uk_or_cc):
		document = space.get_document()
		patterns = space.get_subsuming_patterns(uk_or_cc)
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
			self.__write_evaluate_all__(space, None)
			self.output("# Pattern Evaluate #\n")
			self.output("\tindex\tlength\texecutions\tmutants\tuk_exe_supp\tuk_exe_conf(%)\tcc_exe_supp\tcc_exe_conf(%)"
						"\tuk_mut_supp\tuk_mut_conf(%)\tcc_mut_supp\tcc_mut_conf(%)\n")
			index = 0
			for pattern in space.get_subsuming_patterns(None):
				index += 1
				self.__write_evaluate_one__(index, pattern)
		return


class RIPFPTMiner:
	"""
	It implements frequent pattern mining on RIP execution conditions (selected as True) features.
	"""

	def __init__(self):
		self.context = None
		return

	def __mine__(self, parent: RIPPattern, words):
		"""
		:param parent:
		:param words:
		:return:
		"""
		self.context: RIPMineContext
		total, support, confidence = self.context.estimate(parent)
		length = len(parent.get_words())
		if support >= self.context.get_min_support() and confidence <= self.context.get_max_confidence() and length < self.context.get_max_length():
			for word in words:
				child = self.context.new_child(parent, word)
				if child != parent:
					self.__mine__(child, words)
		return

	def mine(self, context: RIPMineContext):
		"""
		:param context:
		:return:
		"""
		self.context = context
		root_executions = context.get_classifier().select(context.get_document().get_executions(), context.is_uk_or_cc())
		for root_execution in root_executions:
			root_execution: jcfeat.RIPExecution
			words = root_execution.get_words()
			for word in words:
				root = context.new_root(word)
				self.__mine__(root, words)
		good_patterns = self.context.extract_good_patterns()
		return RIPPatternSpace(self.context.get_document(), self.context.get_classifier(), good_patterns)


class RIPDTTMiner:
	"""
	It implements the pattern mining using decision tree model
	"""

	def __init__(self):
		self.classifier = None
		self.context = None
		self.X = None
		self.Y = list()
		self.W = list()
		return

	def __input_context__(self, context: RIPMineContext):
		"""
		:param context:
		:return:	(1) update context information
					(2) update X, Y, W to train the decision tree
		"""
		self.context = context
		D = dict()
		self.Y.clear()
		self.W.clear()
		for execution in self.context.get_document().get_executions():
			execution: jcfeat.RIPExecution
			if self.context.is_exe_or_mut():
				sample = execution
			else:
				sample = execution.get_mutant()
			total, support, confidence = self.context.get_classifier().estimate([sample], self.context.is_uk_or_cc())
			if support > 0:
				self.Y.append(1)
			else:
				self.Y.append(0)
			for word in execution.get_words():
				if not(word in D):
					D[word] = len(self.W)
					self.W.append(word)
		rows, columns, dataset = list(), list(), list()
		line = 0
		for execution in self.context.get_document().get_executions():
			for word in execution.get_words():
				column = D[word]
				rows.append(line)
				columns.append(column)
				dataset.append(1)
			line += 1
		self.X = sparse.coo_matrix((dataset, (rows, columns)),
								   shape=(len(self.context.get_document().get_executions()), len(self.W)))
		return

	@staticmethod
	def __normalize__(text: str):
		new_text = ""
		for k in range(0, len(text)):
			char = text[k]
			if char in ['{', '}', '\"']:
				char = ' '
			new_text += char
		return new_text

	def __gen_normal_WN__(self):
		"""
		:return: sequence of normalized words to describe the RIP conditions.
		"""
		self.context: RIPMineContext
		WN = list()
		document = self.context.get_document()
		for word in self.W:
			condition = document.get_condition(word)
			category = condition.get_category()
			operator = condition.get_operator()
			execution = condition.get_execution()
			location = condition.get_location().get_cir_code()
			if condition.get_parameter() is None:
				parameter = "null"
			else:
				parameter = condition.get_parameter().get_code()
			norm_word = "[{}, {}, {}, \"{}\", {}]".format(category, operator, execution, location, parameter)
			WN.append(RIPDTTMiner.__normalize__(norm_word))
		return WN

	def __fit_decisions__(self, tree_file: str):
		"""
		:param tree_file:
		:return: create a classifier and training it using the context data and return the predicted results
		"""
		self.context: RIPMineContext
		self.classifier = tree.DecisionTreeClassifier(min_samples_leaf=self.context.get_min_support())
		self.classifier.fit(self.X, self.Y)
		YP = self.classifier.predict(self.X)
		print(metrics.classification_report(self.Y, YP, target_names=["Killable", "Equivalent"]))
		if not(tree_file is None):
			W = self.__gen_normal_WN__()
			dot_data = tree.export_graphviz(self.classifier, out_file=None, feature_names=W,
											class_names=["Killable", "Equivalent"])
			graph = pydotplus.graph_from_dot_data(dot_data)
			graph.write_pdf(tree_file)
		return YP

	def __get_leaf_path__(self, YP):
		"""
		:return: selecting leaf that decides type as equivalent and their corresponding path in the program
		"""
		self.context: RIPMineContext
		self.classifier: tree.DecisionTreeClassifier
		leaf_path = dict()	# exec_id --> leaf_id, node_path
		X_array = self.X.toarray()
		node_indicators = self.classifier.decision_path(X_array)
		leave_ids = self.classifier.apply(X_array)
		for exec_id in range(0, len(self.context.get_document().get_executions())):
			if YP[exec_id] == 1:
				leaf_node_id = leave_ids[exec_id]
				node_index = node_indicators.indices[
							 node_indicators.indptr[exec_id]:
							 node_indicators.indptr[exec_id + 1]]
				leaf_path[exec_id] = (leaf_node_id, node_index)
		return leaf_path

	def __path_patterns__(self, leaf_path: dict):
		"""
		:param leaf_path:
		:return: leaf_node_id, node_id_list
		"""
		self.context: RIPMineContext
		X = self.X.toarray()
		patterns = set()
		features = self.classifier.tree_.feature
		thresholds = self.classifier.tree_.threshold
		for exec_id, value in leaf_path.items():
			words = list()
			leaf_id = value[0]
			node_path = value[1]
			for node_id in node_path:
				if node_id != leaf_id:
					word = self.W[features[node_id]]
					word: str
					if X[exec_id, features[node_id]] > thresholds[node_id]:
						words.append(word)		# select True-branch words
			pattern = self.context.new_root("")
			for word in words:
				pattern = self.context.new_child(pattern, word)
			patterns.add(pattern)
		return patterns

	def mine(self, context: RIPMineContext, tree_path: str):
		self.__input_context__(context)
		YP = self.__fit_decisions__(tree_path)
		leaf_path = self.__get_leaf_path__(YP)
		patterns = self.__path_patterns__(leaf_path)
		return RIPPatternSpace(self.context.get_document(), self.context.get_classifier(), patterns)


def evaluate_results(space: RIPPatternSpace, output_directory: str, name: str, exe_or_mut: bool, uk_or_cc: bool):
	writer = RIPPatternWriter()
	writer.write_evaluate(space, os.path.join(output_directory, name + ".sum"))
	writer.write_matching(space, os.path.join(output_directory, name + ".bpt"), exe_or_mut, uk_or_cc)
	writer.write_patterns(space.get_subsuming_patterns(uk_or_cc), os.path.join(output_directory, name + ".mpt"))
	return


def get_rip_document(directory: str, file_name: str, t_value, f_value, n_value, output_directory: str):
	c_project = jcmuta.CProject(directory, file_name)
	document = jcfeat.RIPDocument.load_static_documents(c_project, t_value, f_value, n_value)
	if not(os.path.exists(output_directory)):
		os.mkdir(output_directory)
	return document


def do_frequent_mine(document: jcfeat.RIPDocument, exe_or_mut: bool, uk_or_cc: bool, min_support: int,
					 min_confidence: float, max_confidence: float, max_length: int, output_directory: str):
	miner = RIPFPTMiner()
	output_directory.strip()
	context = RIPMineContext(document, exe_or_mut, uk_or_cc, min_support,
							 min_confidence, max_confidence, max_length)
	return miner.mine(context)


def do_decision_mine(document: jcfeat.RIPDocument, exe_or_mut: bool, uk_or_cc: bool, min_support: int,
					 min_confidence: float, max_confidence: float, max_length: int, output_directory: str):
	miner = RIPDTTMiner()
	context = RIPMineContext(document, exe_or_mut, uk_or_cc, min_support,
							 min_confidence, max_confidence, max_length)
	name = document.get_project().program.name
	return miner.mine(context, os.path.join(output_directory, name + ".pdf"))


def testing(inputs_directory: str, output_directory: str, model_name: str, t_value, f_value, n_value,
			exe_or_mut: bool, uk_or_cc: bool, min_support: int, min_confidence: float,
			max_confidence: float, max_length: int, do_mining):
	"""
	:param inputs_directory:
	:param output_directory:
	:param model_name:
	:param t_value:
	:param f_value:
	:param n_value:
	:param exe_or_mut:
	:param uk_or_cc:
	:param min_support:
	:param min_confidence:
	:param max_confidence:
	:param max_length:
	:param do_mining:
	:return:
	"""
	output_directory = os.path.join(output_directory, model_name)
	if not(os.path.exists(output_directory)):
		os.mkdir(output_directory)
	for file_name in os.listdir(inputs_directory):
		print("Testing on", file_name)
		# Step-I. Load features from data files
		document = get_rip_document(os.path.join(inputs_directory, file_name),
									file_name, t_value, f_value, n_value, output_directory)
		print("\t(1) Load", len(document.get_executions()), "lines of", len(document.get_mutants()),
			  "mutants with", len(document.get_corpus()), "words.")
		# Step-II. Perform pattern mining algorithms
		space = do_mining(document=document, exe_or_mut=exe_or_mut, uk_or_cc=uk_or_cc, min_support=min_support,
						  min_confidence=min_confidence,
						  max_confidence=max_confidence, max_length=max_length, output_directory=output_directory)
		space: RIPPatternSpace
		print("\t(2) Generate", len(space.get_patterns()), "patterns with",
			  len(space.get_subsuming_patterns(None)), "subsuming ones.")
		# Step-III. Evaluate the performance of mining results
		evaluate_results(space, output_directory, file_name, exe_or_mut, uk_or_cc)
		print("\t(3) Output the pattern, test results to output file finally...")
		print()
	return


if __name__ == "__main__":
	prev_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	post_path = "/home/dzt2/Development/Data/"
	testing(prev_path, post_path, "frequent_mine", True, False, True, True, True, 2, 0.70, 0.90, 1, do_frequent_mine)
	testing(prev_path, post_path, "decision_tree", True, False, True, True, True, 2, 0.70, 0.90, 8, do_decision_mine)

