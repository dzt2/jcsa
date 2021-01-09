"""
This file implements pattern mining on killing conditions of equivalent mutants.
	(1) MutationClassifier: to classify & estimate the mutation and its symbolic executions.
	(2) KCPattern: it defines the pattern of killing conditions to be mined.
	(3) KCPatternSpace: it maintains the generated patterns and its minimal set.
	(4) KCPatternMiner: it mines the frequent patterns from non-killed mutations.
	(5) KCPEvaluation: it evaluates the performance of KCPattern(s) being mined.
"""


import os
from typing import TextIO
import com.jcsa.libs.muta as jcmuta


UC_CLASS = "UC"			# execution that is not covered during testing
UI_CLASS = "UI"			# execution that is not infected but covered
UP_CLASS = "UP"			# execution that is not killed but infected
KI_CLASS = "KI"			# execution that is killed


class MutationClassifier:
	"""
	to classify & estimate the mutation and its symbolic executions with parameters.
	"""

	def __init__(self, tests):
		"""
		:param tests: context test cases to determine the result of execution.
			(1) exe.test is not None: counting dynamic execution and determine its result by exe.test
			(2) self.tests is not None: counting virtual execution and determine result for each test in self.tests
			(3) self.tests is None: counting static execution and determine result on all the tests in project
		"""
		self.tests = tests
		self.solutions = dict()		# SymbolicExecution --> {uc, ui, up, ki}
		return

	def __compute__(self, mutant: jcmuta.Mutant, test):
		"""
		:param mutant: mutation as target for being killed
		:param test: non-None for dynamic execution
		:return: uc, ui, up, ki
				(1) number of execution(s) not covering mutation
				(2) number of execution(s) not infecting state (but covering mutation)
				(3) number of execution(s) not killed but infecting state
				(4) number of execution(s) being killed
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
		return uc, ui, up, ki

	def __set_solution__(self, sample):
		"""
		:param sample: either Mutant or SymbolicExecution
		:return: update the solution w.r.t. sample in space
		"""
		if isinstance(sample, jcmuta.SymbolicExecution):
			sample: jcmuta.SymbolicExecution
			solution = self.__compute__(sample.get_mutant(), sample.get_test_case())
		else:
			sample: jcmuta.Mutant
			solution = self.__compute__(sample, None)
		self.solutions[sample] = solution
		return

	def __get_solution__(self, sample):
		"""
		:param sample: either Mutant or SymbolicExecution
		:return: uc, ui, up, ki
				(1) number of execution(s) not covering mutation
				(2) number of execution(s) not infecting state (but covering mutation)
				(3) number of execution(s) not killed but infecting state
				(4) number of execution(s) being killed
		"""
		solution = self.solutions[sample]
		uc = solution[0]
		ui = solution[1]
		up = solution[2]
		ki = solution[3]
		uc: int
		ui: int
		up: int
		ki: int
		return uc, ui, up, ki

	def __counting__(self, sample):
		"""
		:param sample: either Mutant or SymbolicExecution
		:return: uc, ui, up, ki
				(1) number of execution(s) not covering mutation
				(2) number of execution(s) not infecting state (but covering mutation)
				(3) number of execution(s) not killed but infecting state
				(4) number of execution(s) being killed
		"""
		if not(sample in self.solutions):
			self.__set_solution__(sample)
		return self.__get_solution__(sample)

	def __classify__(self, sample):
		"""
		:param sample: either Mutant or SymbolicExecution
		:return: UC, UI, UP, KI
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
		:param samples: set of either Mutant or SymbolicExecution
		:return: uc, ui, up, ki, uk, cc
				(1) number of execution(s) not covering mutation
				(2) number of execution(s) not infecting state (but covering mutation)
				(3) number of execution(s) not killed but infecting state
				(4) number of execution(s) being killed
				(5) number of execution(s) being alive
				(6) number of execution(s) being alive & covered
		"""
		uc, ui, up, ki = 0, 0, 0, 0
		for sample in samples:
			luc, lui, lup, lki = self.__counting__(sample)
			uc += luc
			ui += lui
			up += lup
			ki +=  lki
		return uc, ui, up, ki, uc + ui + up, ui + up





















