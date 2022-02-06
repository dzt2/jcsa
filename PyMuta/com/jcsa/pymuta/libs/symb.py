"""This file defines the data model of symbolic execution states at mutation testing."""


import os
from collections import deque
import com.jcsa.pymuta.libs.base as jcbase
import com.jcsa.pymuta.libs.code as jccode
import com.jcsa.pymuta.libs.muta as jcmuta
import graphviz


class CDocument:
	"""
	It integrates the subsumption hierarchies and corresponding annotations from each state.
	"""

	def __init__(self, directory: str, file_name: str, middle_name: str):
		"""
		:param directory: 	the directory where the feature files are preserved
		:param file_name: 	the name of the program under test with 'c' postfix
		:param middle_name: the middle-name to select static (.pdg) or dynamics (.tid)
		"""
		self.project = jcmuta.CProject(directory, file_name)
		return

	def get_name(self):
		"""
		:return: the name of the C program under test
		"""
		return self.project.program.name

	def get_program(self):
		"""
		:return: the C program model on which the document is defined
		"""
		return self.project.program

	def get_project(self):
		"""
		:return: the mutation testing project in which the document is defined
		"""
		return self.project


class CirAbstractState:
	"""
	It defines an abstract execution state at some program point.
	---	execution:	the CFG-node where the execution state is specified.
	---	store_type:	
	"""






















