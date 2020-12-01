"""
It models the basic items in program and mutation description.
"""

import enum


class CTokenType(enum.Enum):
	Nullptr = 0
	Boolean = 1
	Character = 2
	Integer = 3
	Floating = 4
	Complex = 5
	String = 6
	Keyword = 7
	Punctuate = 8
	Operator = 9
	AstKey = 10
	CirKey = 11
	ExeKey = 12
	MutKey = 13
	DataType = 14

	def __str__(self):
		return self.name


class CToken:
	"""
	The tokens in C program can be either n@null, b@bool, c@char, i@int, f@real, x@complex, s@string, k@keyword,
	p@punctuate, o@operator, ast@key, cir@key, exe@key, mut@key, typ@code
	"""

	def __init__(self, token_type: CTokenType, value):
		"""
		:param token_type: type of this token
		:param value: value hold by this token
		"""
		self.token_type = token_type
		self.token_value = value
		return

	def get_token_type(self):
		return self.token_type

	def get_token_value(self):
		return self.token_value

	@staticmethod
	def parse(text: str):
		items = text.strip().split('@')
		head = items[0].strip()
		if head == "n":				# (Nullptr, None)
			return CToken(CTokenType(CTokenType.Nullptr), None)
		elif head == "b":			# (Boolean, bool)
			return CToken(CTokenType(CTokenType.Boolean), items[1].strip() == "true")
		elif head == "c":			# (Character, int)
			return CToken(CTokenType(CTokenType.Character), int(items[1].strip()))
		elif head == "i":			# (Integer, int)
			return CToken(CTokenType(CTokenType.Integer), int(items[1].strip()))
		elif head == "f":			# (Floating, float)
			return CToken(CTokenType(CTokenType.Floating), float(items[1].strip()))
		elif head == "x":			# (Complex, complex)
			return CToken(CTokenType(CTokenType.Complex), complex(float(items[1].strip()), float(items[2].strip())))
		elif head == "s":			# (String, str)
			text = items[1].strip()
			text = text.replace("\\s", " ")
			text = text.replace("\\a", "@")
			return CToken(CTokenType(CTokenType.String), text)
		elif head == "k":			# (Keyword, str)
			return CToken(CTokenType(CTokenType.Keyword), items[1].strip())
		elif head == "o":			# (Operator, str)
			return CToken(CTokenType(CTokenType.Operator), items[1].strip())
		elif head == "p":			# (Punctuate, str)
			return CToken(CTokenType(CTokenType.Punctuate), items[1].strip())
		elif head == "ast":			# (AstKey, int)
			return CToken(CTokenType(CTokenType.AstKey), int(items[1].strip()))
		elif head == "cir":			# (CirKey, int)
			return CToken(CTokenType(CTokenType.CirKey), int(items[1].strip()))
		elif head == "mut":			# (MutKey, int)
			return CToken(CTokenType(CTokenType.MutKey), int(items[1].strip()))
		elif head == "typ":			# (DataType, List[str])
			return CToken(CTokenType(CTokenType.DataType), items[1].strip().split(":"))
		elif head == "exe":			# (ExeKey, [str, int])
			text = items[1].strip()
			beg_index = text.find("[")
			end_index = text.find("]")
			func_name = text[0: beg_index].strip()
			exec_id = text[beg_index + 1: end_index].strip()
			exec_id = int(exec_id)
			return CToken(CTokenType(CTokenType.ExeKey), [func_name, exec_id])
		else:						# Unsupported return null
			return None

	def __str__(self):
		return str(self.token_value)


if __name__ == "__main__":
	print(CToken.parse("n@null"))
	print(CToken.parse("b@true"))
	print(CToken.parse("b@false"))
	print(CToken.parse("c@49"))
	print(CToken.parse("i@-752628"))
	print(CToken.parse("f@69.5583"))
	print(CToken.parse("x@10@21.3"))
	print(CToken.parse("s@Hello, world!"))
	print(CToken.parse("o@arith_add"))
	print(CToken.parse("exe@main[55]"))
	print(CToken.parse("ast@9282"))
	print(CToken.parse("typ@llong"))

