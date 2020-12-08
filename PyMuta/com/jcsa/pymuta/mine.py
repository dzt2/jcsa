"""
It implements the frequent pattern mining algorithm for coincidental correctness.
"""


import os
import com.jcsa.pymuta.muta as cmuta


UC_CLASS = "UC"
UI_CLASS = "UI"
UP_CLASS = "UP"
KI_CLASS = "KI"


class MutationClassifier:
	"""
	classification algorithm
	"""
	@staticmethod
	def classify_one(sample):
		"""
		:param sample: either MutantExecution or Mutant
		:return: UC|UI|UP|KI
		"""
		if isinstance(sample, cmuta.Mutant):
			sample: cmuta.Mutant
			if sample.get_result().is_killed():
				return KI_CLASS
			elif sample.get_weak_mutant().get_result().is_killed():
				return UP_CLASS
			elif sample.get_coverage_mutant().get_result().is_killed():
				return UI_CLASS
			else:
				return UC_CLASS
		else:
			sample: cmuta.MutantExecution
			if sample.is_killed():
				return KI_CLASS
			elif sample.is_infected():
				return UP_CLASS
			elif sample.is_covered():
				return UI_CLASS
			else:
				return UC_CLASS

	@staticmethod
	def classify_all(samples):
		"""
		:param samples: the collection of samples [Mutant|MutantExecution]
		:return: mapping from [UC|UI|UP|KI] --> set[Mutant|MutationExecution]
		"""
		results = dict()
		results[UC_CLASS] = set()
		results[UI_CLASS] = set()
		results[UP_CLASS] = set()
		results[KI_CLASS] = set()
		for sample in samples:
			key = MutationClassifier.classify_one(sample)
			results[key].add(sample)
		return results









