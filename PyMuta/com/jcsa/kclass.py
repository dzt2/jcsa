"""
This file implements the killing condition based mutant classification using pattern mining approach.
	---	RIPClassifier: to classify, counting and estimate symbolic executions via RIP-model results.
	---	KCPattern: killing condition pattern contains a set of conditions required for killing mutants in the
					executions of mutation testing.
	--- KCPatternSpace: it manages the set of killing condition patterns generated from the miner
	--- KCPatternMiner: it implements the pattern mining algorithm to select critical killing conditions
	--- KCPatternWriter: it writes information of generated killing conditions right there.
"""


import os
from typing import TextIO
import com.jcsa.libs.muta as jcmuta


UC_CLASS = "UC"			# execution that is not covered during testing
UI_CLASS = "UI"			# execution that is not infected but covered
UP_CLASS = "UP"			# execution that is not killed but infected
KI_CLASS = "KI"			# execution that is killed


class RIPClassifier:
	"""
	to classify, counting and estimate symbolic executions via RIP-model results.
	"""

	def __init__(self, tests):
		"""
		:param tests: test cases used as the context to determine whether a mutant is killed.
				(1) for dynamic execution (with test case), this parameter is ignored and it
					only counts the test result of mutant on specified test case.
				(2) for static execution, tests specify the set of test cases being used and
					it counts the number of tests in self.tests killing the target mutant.
				(3) for static execution, None tests mean all the test and it only count the
					number of mutants or static executions being killed.
		"""
		self.tests = tests
		self.solutions = dict()		# String --> [uc, ui, up, ki]
		return

	@staticmethod
	def __key_solution__(mutant: jcmuta.Mutant, test):
		"""
		:param mutant:
		:param test:
		:return:
		"""
		if test is None:
			return "{}".format(mutant.get_mut_id())
		else:
			test: jcmuta.TestCase
			return "{}:{}".format(mutant.get_mut_id(), test.get_test_id())

	def __get_solution__(self, mutant: jcmuta.Mutant, test):
		"""
		:param mutant:
		:param test:
		:return:
				uc, ui, up, ki
					(1) uc: number of test cases that cannot cover the target mutant
					(2) ui: number of test cases that cannot infect but cover the mutant
					(3) up: number of test cases that cannot kill but infect the mutant
					(4) ki: number of test cases that kill the target mutant
		"""
		solution = self.solutions[RIPClassifier.__key_solution__(mutant, test)]
		uc = solution[0]
		ui = solution[1]
		up = solution[2]
		ki = solution[3]
		uc: int
		ui: int
		up: int
		ki: int
		return uc, ui, up, ki

	def __set_solution__(self, mutant: jcmuta.Mutant, test):
		"""
		:param mutant:
		:param test:
		:return: update solution w.r.t. (m, t)
		"""
		uc, ui, up, ki = 0, 0, 0, 0
		s_result = mutant.get_result()
		w_result = mutant.get_weak_mutant().get_result()
		c_result = mutant.get_coverage_mutant().get_result()
		if test is None:
			if self.tests is None:
				if s_result.is_killable():
					ki += 1
				elif w_result.is_killable():
					up += 1
				elif c_result.is_killable():
					ui += 1
				else:
					uc += 1
			else:
				for test in self.tests:
					if s_result.is_killed_by(test):
						ki += 1
					elif w_result.is_killed_by(test):
						up += 1
					elif c_result.is_killed_by(test):
						ui += 1
					else:
						uc += 1
		else:
			if s_result.is_killed_by(test):
				ki += 1
			elif w_result.is_killed_by(test):
				up += 1
			elif c_result.is_killed_by(test):
				ui += 1
			else:
				uc += 1
		self.solutions[RIPClassifier.__key_solution__(mutant, test)] = (uc, ui, up, ki)
		return

	def __solving__(self, mutant: jcmuta.Mutant, test):
		"""
		:param mutant:
		:param test:
		:return: uc, ui, up, ki
					(1) uc: number of test cases that cannot cover the target mutant
					(2) ui: number of test cases that cannot infect but cover the mutant
					(3) up: number of test cases that cannot kill but infect the mutant
					(4) ki: number of test cases that kill the target mutant
		"""
		key = RIPClassifier.__key_solution__(mutant, test)
		if not(key in self.solutions):
			self.__set_solution__(mutant, test)
		return self.__get_solution__(mutant, test)

	def __counting__(self, sample):
		"""
		:param sample: Mutant or SymbolicExecution
		:return: uc, ui, up, ki
					(1) uc: number of test cases that cannot cover the target mutant
					(2) ui: number of test cases that cannot infect but cover the mutant
					(3) up: number of test cases that cannot kill but infect the mutant
					(4) ki: number of test cases that kill the target mutant
		"""
		if isinstance(sample, jcmuta.Mutant):
			sample: jcmuta.Mutant
			return self.__solving__(sample, None)
		else:
			sample: jcmuta.SymbolicExecution
			return self.__solving__(sample.get_mutant(), sample.get_test())

	def __classify__(self, sample):
		"""
		:param sample:
		:return: UC|UI|UP|KI
		"""
		uc, ui, up, ki = self.__counting__(sample)
		if ki > 0:
			return KI_CLASS
		elif up > 0:
			return UP_CLASS
		elif ui > 0:
			return UI_CLASS
		else:
			return UC_CLASS

	def counting(self, samples):
		"""
		:param samples:
		:return:	uc, ui, up, ki, uk, cc
					(1) uc: number of test cases that cannot cover the target mutant
					(2) ui: number of test cases that cannot infect but cover the mutant
					(3) up: number of test cases that cannot kill but infect the mutant
					(4) ki: number of test cases that kill the target mutant
					(5) uk: number of test cases that fail to kill the mutant
					(6) cc: number of test cases that fail to kill but cover the mutant
		"""
		uc, ui, up, ki = 0, 0, 0, 0
		for sample in samples:
			luc, lui, lup, lki = self.__counting__(sample)
			uc += luc
			ui += lui
			up += lup
			ki += lki
		return uc, ui, up, ki, uc + ui + up, ui + up

	def classify(self, samples):
		"""
		:param samples:
		:return: [UC|UI|UP|KI] ==> set of samples w.r.t. the class
		"""
		results = dict()
		results[UC_CLASS] = set()
		results[UI_CLASS] = set()
		results[UP_CLASS] = set()
		results[KI_CLASS] = set()
		for sample in samples:
			key = self.__classify__(sample)
			results[key].add(sample)
		return results

	def estimate(self, samples, uk_or_cc: bool):
		"""
		:param samples:
		:param uk_or_cc: true to take non-killed or coincidental correctness
		:return: total, support, confidence
		"""
		uc, ui, up, ki, uk, cc = self.counting(samples)
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
		:param uk_or_cc: true to take non-killed or coincidental correctness
		:return:
		"""
		results = self.classify(samples)
		selects = results[UI_CLASS] | results[UP_CLASS]
		if uk_or_cc:
			selects = selects | results[UC_CLASS]
		return selects

	def has_tests(self):
		return not(self.tests is None)

	def get_tests(self):
		return self.tests


class KCPattern:
	"""
	killing condition pattern
	"""

	def __init__(self, document: jcmuta.SymbolicDocument, classifier: RIPClassifier):
		"""
		:param document: it provides all the executions for being matched
		:param classifier: the classifier used to estimate the pattern's samples
		"""
		self.document = document
		self.classifier = classifier
		self.executions = set()
		self.mutants = set()
		self.words = list()
		return

	def get_document(self):
		"""
		:return: it provides all the executions for being matched
		"""
		return self.document

	def get_classifier(self):
		"""
		:return: the classifier used to estimate the pattern's samples
		"""
		return self.classifier

	def get_words(self):
		"""
		:return: set of words encoding the killing conditions being matched
		"""
		return self.words

	def get_conditions(self):
		"""
		:return: the set of killing conditions defining this pattern
		"""
		conditions = list()
		for word in self.words:
			conditions.append(self.document.get_condition(word))
		return conditions

	def __str__(self):
		return str(self.words)

	def __len__(self):
		return len(self.words)

	def get_executions(self):
		"""
		:return: executions matching with this pattern
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: mutants of which executions matching with it
		"""
		return self.mutants

	def get_samples(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: True to select executions or mutants
		:return:
		"""
		if exe_or_mut:
			return self.executions
		else:
			return self.mutants

	def __match_with__(self, execution: jcmuta.SymbolicExecution):
		"""
		:param execution:
		:return:
		"""
		for word in self.words:
			if not(word in execution.get_words()):
				return False
		return True

	def set_executions(self, parent):
		"""
		:param parent: pattern from which this one is extended or None to update samples on the document
		:return:
		"""
		if parent is None:
			executions = self.document.get_executions()
		else:
			executions = parent.executions
		self.executions.clear()
		self.mutants.clear()
		for execution in executions:
			execution: jcmuta.SymbolicExecution
			if self.__match_with__(execution):
				self.executions.add(execution)
				self.mutants.add(execution.get_mutant())
		return

	def counting(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: true to select executions or mutants
		:return:
		"""
		return self.classifier.counting(self.get_samples(exe_or_mut))

	def classify(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: true to select executions or mutants
		:return:
		"""
		return self.classifier.classify(self.get_samples(exe_or_mut))

	def estimate(self, exe_or_mut: bool, uk_or_cc: bool):
		"""
		:param exe_or_mut: true to select executions or mutants
		:param uk_or_cc: true to take non-killed or CC
		:return: total, support, confidence
		"""
		return self.classifier.estimate(self.get_samples(exe_or_mut), uk_or_cc)

	def extends(self, word: str):
		"""
		:param word:
		:return: child pattern extended from this one by adding one additional word
		"""
		word = word.strip()
		if len(word) > 0 and not(word in self.words):
			child = KCPattern(self.document, self.classifier)
			for old_word in self.words:
				child.words.append(old_word)
			child.words.append(word)
			child.words.sort()
			return child
		return self

	def subsume(self, pattern):
		"""
		:param pattern:
		:return:
		"""
		pattern: KCPattern
		for execution in pattern.get_executions():
			if not(execution in self.executions):
				return False
		return len(self.words) <= len(pattern.words)









