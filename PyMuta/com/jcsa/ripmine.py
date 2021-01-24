"""
This file implements the pattern mining on RIP-testability execution features.
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
	It is used to classify and estimate the RIP-testability results.
	"""

	def __init__(self):
		self.solutions = dict()		# String --> [nr, ni, np, ki]
		return

	# single sample

	@staticmethod
	def __key_solution__(mutant: jcmuta.Mutant, test):
		"""
		:param mutant:
		:param test:
		:return:
		"""
		if test is None:
			return str(mutant.get_mut_id())
		else:
			test: jcmuta.TestCase
			return "{}:{}".format(mutant.get_mut_id(), test.get_test_id())

	def __get_solution__(self, key: str):
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

	def __set_solution__(self, key: str, mutant: jcmuta.Mutant, test):
		"""
		:param key:
		:param mutant:
		:param test:
		:return:
		"""
		s_result = mutant.get_result()
		w_result = mutant.get_weak_mutant().get_result()
		c_result = mutant.get_coverage_mutant().get_result()
		nr, ni, np, ki = 0, 0, 0, 0
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
		:param sample: Mutant or RIPExecution
		:return: nr, ni, np, ki
				(1) nr: testing that fails to reach the mutation
				(2) ni: testing that fails to infect but reaches
				(3) np: testing that fails to kill but infect it
				(4) ki: testing that manage to detect the mutant
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
			self.__set_solution__(key, mutant, test)
		return self.__get_solution__(key)

	def __classify__(self, sample):
		"""
		:param sample: Mutant or RIPExecution
		:return: NR, NI, NP, KI
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

	# set estimation

	def classify(self, samples):
		"""
		:param samples: set of Mutant or RIPExecution
		:return: [NR|NI|NP|KI] ==> set of samples w.r.t.
		"""
		results = dict()
		results[NR_CLASS] = set()
		results[NI_CLASS] = set()
		results[NP_CLASS] = set()
		results[KI_CLASS] = set()
		for sample in samples:
			key = self.__classify__(sample)
			results[key].add(sample)
		return results

	def counting(self, samples):
		"""
		:param samples:
		:return: nr, ni, np, ki, uk, cc
				(1) nr: testing that fails to reach the mutation
				(2) ni: testing that fails to infect but reaches
				(3) np: testing that fails to kill but infect it
				(4) ki: testing that manage to detect the mutant
				(5) uk: testing that fails to kill the mutations
				(6) cc: testing that fails to kill but exercised
		"""
		nr, ni, np, ki = 0, 0, 0, 0
		for sample in samples:
			lnr, lni, lnp, lki = self.__counting__(sample)
			nr += lnr
			ni += lni
			np += lnp
			ki += lki
		return nr, ni, np, ki, nr + ni + np, ni + np

	def estimate(self, samples, uk_or_cc: bool):
		"""
		:param samples:
		:param uk_or_cc: True to take un-killed testing or coincidental correctness
		:return: total, support, confidence
		"""
		nr, ni, np, ki, uk, cc = self.counting(samples)
		if uk_or_cc:
			support = uk
		else:
			support = cc
		total = support + ki
		if support > 0:
			confidence = support / (total + 0.0)
		else:
			confidence = 0.0
		return total, support, confidence

	def select(self, samples, uk_or_cc: bool):
		"""
		:param samples:
		:param uk_or_cc: True to take un-killed testing or coincidental correctness
		:return:
		"""
		results = self.classify(samples)
		selects = results[NI_CLASS] | results[NP_CLASS]
		if uk_or_cc:
			selects = selects | results[NR_CLASS]
		return selects


class RIPPattern:
	"""
	The pattern of RIP-testability records a set of testability-features on non-killed mutations.
	"""

	def __init__(self, document: jcmuta.RIPDocument, classifier: RIPClassifier):
		"""
		:param document: it provides entire dataset for estimation and matching
		:param classifier: used to classify and estimate samples matched within
		"""
		self.document = document		# it provides entire dataset for estimation and matching
		self.classifier = classifier	# used to classify and estimate samples matched within
		self.words = list()				# the set of words encoding features in the pattern
		self.executions = set()			# the set of RIPExecution matched with this pattern
		self.mutants = set()			# the set of mutants of which executions match with this
		return

	# data parameter

	def get_document(self):
		"""
		:return: it provides entire dataset for estimation and matching
		"""
		return self.document

	def get_classifier(self):
		"""
		:return: used to classify and estimate samples matched within
		"""
		return self.classifier

	# data samples

	def get_executions(self):
		"""
		:return: the set of RIPExecution matched with this pattern
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: the set of mutants of which executions match with this
		"""
		return self.mutants

	def get_samples(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: True to select executions matched or mutants
		:return:
		"""
		if exe_or_mut:
			return self.executions
		else:
			return self.mutants

	def __matching__(self, execution: jcmuta.RIPExecution):
		"""
		:param execution:
		:return:
		"""
		for word in self.words:
			if not(word in execution.get_words()):
				return False
		return True

	def set_samples(self, parent):
		"""
		:param parent: pattern or None as the parent that extends this one
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

	# features defined

	def get_words(self):
		return self.words

	def get_features(self):
		"""
		:return: the set of testability-features defined in this pattern
		"""
		features = list()
		for word in self.words:
			features.append(self.document.get_feature(word))
		return features

	def __str__(self):
		return str(self.words)

	def __len__(self):
		"""
		:return: the number of testability-features in the pattern
		"""
		return len(self.words)

	# generations

	def extends(self, word: str):
		"""
		:param word:
		:return: child pattern extended from this one by adding one word or itself
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

	def subsume(self, pattern):
		"""
		:param pattern:
		:return: True if samples matched by pattern are also matched in this one
		"""
		pattern: RIPPattern
		for execution in pattern.get_executions():
			if not(execution in self.executions):
				return False
		return len(self.words) <= len(pattern.get_words())

	# estimations

	def classify(self, exe_or_mut: bool):
		return self.classifier.classify(self.get_samples(exe_or_mut))

	def counting(self, exe_or_mut: bool):
		return self.classifier.counting(self.get_samples(exe_or_mut))

	def estimate(self, exe_or_mut: bool, uk_or_cc: bool):
		return self.classifier.estimate(self.get_samples(exe_or_mut), uk_or_cc)

	def select(self, exe_or_mut: bool, uk_or_cc: bool):
		return self.classifier.select(self.get_samples(exe_or_mut), uk_or_cc)


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
			length = len(pattern.get_words())
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


class RIPPatternMiner:
	"""
	It implements frequent pattern mining to mine patterns from RIP-testability features in symbolic execution.
	"""

	def __init__(self, exe_or_mut: bool, uk_or_cc: bool, min_support: int, max_confidence: float, max_length: int):
		"""
		:param exe_or_mut: True to take RIPExecution or Mutant as samples being estimated
		:param uk_or_cc: True to take non-killed testing or coincidental correctness as supports
		:param min_support: minimal number of supports required
		:param max_confidence: maximal confidence to stop the recursive mining algorithm
		:param max_length: the maximal length of patterns allowed to be generated from dataset
		"""
		self.exe_or_mut = exe_or_mut
		self.uk_or_cc = uk_or_cc
		self.min_support = min_support
		self.max_confidence = max_confidence
		self.max_length = max_length
		self.document = None
		self.classifier = None
		self.patterns = dict()		# String --> RIPPattern
		self.solutions = dict()		# RIPPattern --> [total, support, confidence]
		return

	def __root__(self, word: str):
		"""
		:param word:
		:return: the unique pattern with one word
		"""
		root = RIPPattern(self.document, self.classifier)
		root = root.extends(word)
		if not(str(root) in self.patterns):
			self.patterns[str(root)] = root
			root.set_samples(None)
		root = self.patterns[str(root)]
		root: RIPPattern
		return root

	def __child__(self, parent: RIPPattern, word: str):
		"""
		:param parent:
		:param word:
		:return: unique child extended from parent by adding one word
		"""
		child = parent.extends(word)
		if child != parent:
			if not(str(child) in self.patterns):
				self.patterns[str(child)] = child
				child.set_samples(parent)
			child = self.patterns[str(child)]
			child: RIPPattern
			return child
		return parent

	def __mine__(self, parent: RIPPattern, words):
		"""
		:param parent:
		:param words:
		:return:
		"""
		if not(parent in self.solutions):
			solution = parent.estimate(self.exe_or_mut, self.uk_or_cc)
			self.solutions[parent] = solution
		solution = self.solutions[parent]
		support = solution[1]
		confidence = solution[2]
		if len(parent.get_words()) < self.max_length and support >= self.min_support and confidence <= self.max_confidence:
			for word in words:
				child = self.__child__(parent, word)
				if child != parent:
					self.__mine__(child, words)
		return

	def __output__(self):
		"""
		:return:
		"""
		good_patterns = set()
		for pattern, solution in self.solutions.items():
			pattern: RIPPattern
			support = solution[1]
			confidence = solution[2]
			if support >= self.min_support and confidence >= self.max_confidence:
				good_patterns.add(pattern)
		return RIPPatternSpace(self.document, self.classifier, good_patterns)

	def mine(self, document: jcmuta.RIPDocument):
		"""
		:param document:
		:return:
		"""
		self.patterns.clear()
		self.solutions.clear()
		self.document = document
		self.classifier = RIPClassifier()

		lines = self.classifier.select(document.get_executions(), self.uk_or_cc)
		for line in lines:
			line: jcmuta.RIPExecution
			words = line.get_words()
			for word in words:
				root = self.__root__(word)
				self.__mine__(root, words)

		output = self.__output__()
		self.document = None
		self.classifier = None
		self.solutions.clear()
		self.patterns.clear()

		return output



class RIPPatternWriter:
	"""
	It writes the information of RIP-patterns to the output file for reviewing.
	"""

	def __init__(self, space: RIPPatternSpace):
		self.writer = None
		self.space = space
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
		length = len(pattern.get_words())
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
		for feature in pattern.get_features():
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
			self.output(template.format(index, category, operator, validate, execution, statement, location, parameter))
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

	def write_patterns(self, file_path: str):
		with open(file_path, 'w') as writer:
			self.writer = writer
			for pattern in self.space.get_subsuming_patterns():
				self.__write_pattern__(pattern)
		return

	def write_matching(self, file_path: str, exe_or_mut: bool, uk_or_cc: bool):
		"""
		:param uk_or_cc:
		:param exe_or_mut:
		:param file_path:
		:return: 	Mutant 	ID RESULT CLASS OPERATOR LINE LOCATION PARMETER
					Pattern
					Category Operator Validate Execution Statement Location Parameter*
		"""
		mutants_patterns = self.space.select_best_patterns(exe_or_mut, uk_or_cc)
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

	def __write_evaluate_all__(self):
		document = self.space.get_document()
		patterns = self.space.get_subsuming_patterns()
		classifier = self.space.get_classifier()

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
		length = len(pattern)
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

	def write_evaluate(self, file_path: str):
		"""
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
			self.__write_evaluate_all__()
			self.output("# Pattern Evaluate #\n")
			self.output("\tindex\tlength\texecutions\tmutants\tuk_exe_supp\tuk_exe_conf(%)\tcc_exe_supp\tcc_exe_conf(%)"
						"\tuk_mut_supp\tuk_mut_conf(%)\tcc_mut_supp\tcc_mut_conf(%)\n")
			index = 0
			for pattern in self.space.get_subsuming_patterns():
				index += 1
				self.__write_evaluate_one__(index, pattern)
		return


def mining_patterns(document: jcmuta.RIPDocument, exe_or_mut: bool, uk_or_cc: bool,
					min_support: int, max_confidence: float, max_length: int, output_directory: str):
	"""
	:param document: it provides lines and mutations in the program
	:param classifier_tests: used to generate classifier of mutation
	:param exe_or_mut: true to take line as sample or false to take mutant as sample
	:param uk_or_cc: true to estimate on non-killed samples or false on coincidental correctness samples
	:param min_support: minimal number of samples supporting the patterns
	:param max_confidence: maximal confidence once achieved to stop the pattern generation
	:param max_length: maximal length of the patterns allowed to generate
	:param output_directory: directory where the output files are preserved
	:return:
	"""
	if not(os.path.exists(output_directory)):
		os.mkdir(output_directory)
	if len(document.get_executions()) > 0:
		print("Testing on", document.get_project().program.name)
		print("\t(1) Load", len(document.get_executions()), "lines of", len(document.get_mutants()),
			  "mutants with", len(document.get_corpus()), "words.")

		generator = RIPPatternMiner(exe_or_mut, uk_or_cc, min_support, max_confidence, max_length)
		patterns = generator.mine(document)
		print("\t(2) Generate", len(patterns.get_patterns()), "patterns with", len(patterns.get_subsuming_patterns()),
			  "of subsuming patterns set from.")

		writer = RIPPatternWriter(patterns)
		writer.write_patterns(os.path.join(output_directory, document.get_project().program.name + ".mpt"))
		writer.write_matching(os.path.join(output_directory, document.get_project().program.name + ".bpt"), exe_or_mut, uk_or_cc)
		writer.write_evaluate(os.path.join(output_directory, document.get_project().program.name + ".sum"))
		print("\t(3) Output the pattern, test results to output file finally...")
		print()
	return


def testing_project(directory: str, file_name: str,
					set_none: bool, de_value,
					exe_or_mut: bool,
					uk_or_cc: bool,
					stat_directory: str,
					dyna_directory):
	c_project = jcmuta.CProject(directory, file_name)

	docs = c_project.load_static_document(directory, set_none, de_value)
	mining_patterns(docs, exe_or_mut, uk_or_cc, 2, 0.80, 1, os.path.join(stat_directory))

	if not(dyna_directory is None):
		docs = c_project.load_dynamic_document(directory, set_none, de_value)
		mining_patterns(docs, exe_or_mut, uk_or_cc, 20, 0.80, 1, os.path.join(dyna_directory))
	return


if __name__ == "__main__":
	prev_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	stat_path = "/home/dzt2/Development/Data/patterns/stat"
	dyna_path = None
	# dyna_path = "/home/dzt2/Development/Data/patterns/dyna"
	for filename in os.listdir(prev_path):
		direct = os.path.join(prev_path, filename)
		testing_project(directory=direct, file_name=filename, set_none=False, de_value=None,
						exe_or_mut=True, uk_or_cc=True, stat_directory=stat_path, dyna_directory=dyna_path)

