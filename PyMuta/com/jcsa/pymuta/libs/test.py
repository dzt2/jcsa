import os
from collections import deque

import com.jcsa.pymuta.libs.base as jcbase
import com.jcsa.pymuta.libs.code as jccode
import com.jcsa.pymuta.libs.muta as jcmuta
import graphviz


class CDocument:
	"""
	It maintains the feature database used for pattern mining.
	"""

	def __init__(self, directory: str, file_name: str):
		"""
		:param directory: where the feature files are preserved
		:param file_name: the name of the program under test.
		"""
		self.project = jcmuta.CProject(directory, file_name)
		return










