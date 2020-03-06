#include <iostream>
#include <fstream>
#include <cstdlib>

int main(int argc, char *argv[]) {
	std::ofstream out;
	out.open("csizeof.txt");

	out << "bool : "	<< sizeof(bool)			<< "\n";
	out << "char : "	<< sizeof(char)			<< "\n";
	out << "short : "	<< sizeof(short)		<< "\n";
	out << "int : "		<< sizeof(int)			<< "\n";
	out << "long : "	<< sizeof(long)			<< "\n";
	out << "llong : "	<< sizeof(long long)	<< "\n";
	out << "float : "	<< sizeof(float)		<< "\n";
	out << "double : "	<< sizeof(double)		<< "\n";
	out << "ldouble : "	<< sizeof(long double) << "\n";
	out << "cp_float : " << sizeof(float _Complex) << "\n";
	out << "cp_double : " << sizeof(double _Complex) << "\n";
	out << "cp_ldouble : " << sizeof(long double _Complex) << "\n";
	out << "im_float : " << sizeof(float) << "\n";
	out << "im_double : " << sizeof(double) << "\n";
	out << "im_ldouble : " << sizeof(long double) << "\n";
	out << "address : " << sizeof(void *) << "\n";
	out << std::endl;

	out.close(); return 0;
}