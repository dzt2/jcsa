"""
This implements the translation from basic data element.
"""

import enum


class CTokenType(enum.Enum):
	Nullptr = 0
	Boolean = 1
	Character = 2
	Integer = 3
	Real = 4
	Complex = 5
	Keyword = 6
	Punctuate = 7
	Operator = 8
	AST = 9
	CIR = 10
	EXE = 11
	MUT = 12
	String = 13
	SYM = 14
	TYPE = 15
	Test = 16

	def __str__(self):
		return self.name


class CToken:
	def __init__(self, token_type: CTokenType, token_value):
		"""
		:param token_type:
		:param token_value:
			Nullptr		--> None
			Boolean		--> Bool
			Character	--> int
			Integer		--> int
			Real		--> float
			Complex		--> complex
			Keyword		--> string
			Punctuate	--> string
			Operator	--> string
			AST			--> int
			CIR			--> int
			EXE			--> string, int
			String		--> string
			SYM			--> string
			TYPE		--> string
			Test		--> string
		"""
		self.token_type = token_type
		self.token_value = token_value
		return

	def get_token_type(self):
		return self.token_type

	def get_token_value(self):
		return self.token_value

	def __str__(self):
		return str(self.token_value)

	@staticmethod
	def de_normalize(text: str):
		text = text.replace("\\s", " ")
		text = text.replace("\\a", "@")
		text = text.replace("\\p", "$")
		return text

	@staticmethod
	def parse(text: str):
		"""
		:param text:
		:return:
		"""
		items = text.strip().split('@')
		title = items[0].strip()
		if title == "n":
			return CToken(CTokenType(CTokenType.Nullptr), None)
		elif title == 'b':
			return CToken(CTokenType(CTokenType.Boolean), bool(items[1].strip() == "true"))
		elif title == "c":
			return CToken(CTokenType(CTokenType.Character), int(items[1].strip()))
		elif title == "i":
			return CToken(CTokenType(CTokenType.Integer), int(items[1].strip()))
		elif title == "f":
			return CToken(CTokenType(CTokenType.Real), float(items[1].strip()))
		elif title == "x":
			real = float(items[1].strip())
			image = float(items[2].strip())
			value = complex(real, image)
			return CToken(CTokenType(CTokenType.Complex), value)
		elif title == "key":
			return CToken(CTokenType(CTokenType.Keyword), items[1].strip())
		elif title == "pun":
			return CToken(CTokenType(CTokenType.Punctuate), items[1].strip())
		elif title == "opr":
			return CToken(CTokenType(CTokenType.Operator), items[1].strip())
		elif title == "ast":
			return CToken(CTokenType(CTokenType.AST), int(items[1].strip()))
		elif title == "cir":
			return CToken(CTokenType(CTokenType.CIR), int(items[1].strip()))
		elif title == "mut":
			return CToken(CTokenType(CTokenType.MUT), int(items[1].strip()))
		elif title == "exe":
			return CToken(CTokenType(CTokenType.EXE), [items[1].strip(), int(items[2].strip())])
		elif title == "s":
			return CToken(CTokenType(CTokenType.String), CToken.de_normalize(items[1].strip()))
		elif title == "typ":
			return CToken(CTokenType(CTokenType.TYPE), CToken.de_normalize(items[1].strip()))
		elif title == "sym":
			return CToken(CTokenType(CTokenType.SYM), CToken.de_normalize(items[1].strip()))
		elif title == "tst":
			return CToken(CTokenType(CTokenType.Test), CToken.de_normalize(items[1].strip()))
		else:
			return None		# unable to parse invalid text


if __name__ == "__main__":
	print(CToken.parse("n@null"))
	print(CToken.parse("b@true"))
	print(CToken.parse("b@false"))
	print(CToken.parse("c@96"))
	print(CToken.parse("i@-21585225"))
	print(CToken.parse("f@-678.048825"))
	print(CToken.parse("x@5@7.9"))
	print(CToken.parse("key@c89_int"))
	print(CToken.parse("pun@ari_mul"))
	print(CToken.parse("opr@greater_eq"))
	print(CToken.parse("typ@(int)*"))
	print(CToken.parse("exe@#init@5"))
	print(CToken.parse("ast@10"))
	print(CToken.parse("sym@(length#1263257405)\s!=\s(beg#1287200676)"))

