#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <complex.h>


int is_little_endian() 
{
	unsigned long long x = 1;
	char * buffer = (char *) &x;
	if(buffer[0] != '\0')
		return 1;	/* little-endian */
	else
		return 0;	/* big-endian */
}


int main(int argc, char *argv[]) 
{
	if(argc > 1) 
	{
		FILE * writer = fopen(argv[1], "w");
		fprintf(writer, "little_endian\t%d\n", is_little_endian());
		fprintf(writer, "word_size\t%lu\n", sizeof(void *));
		fprintf(writer, "void_size\t%lu\n", sizeof(void));
		fprintf(writer, "bool_size\t%lu\n", sizeof(bool));
		fprintf(writer, "char_size\t%lu\n", sizeof(char));
		fprintf(writer, "short_size\t%lu\n", sizeof(short));
		fprintf(writer, "int_size\t%lu\n", sizeof(int));
		fprintf(writer, "long_size\t%lu\n", sizeof(long));
		fprintf(writer, "long_long_size\t%lu\n", sizeof(long long));
		fprintf(writer, "float_size\t%lu\n", sizeof(float));
		fprintf(writer, "double_size\t%lu\n", sizeof(double));
		fprintf(writer, "long_double_size\t%lu\n", sizeof(long double));
		fprintf(writer, "float_complex_size\t%lu\n", sizeof(float complex));
		fprintf(writer, "double_complex_size\t%lu\n", sizeof(double complex));
		fprintf(writer, "long_double_complex_size\t%lu\n", sizeof(long double complex));
		fprintf(writer, "float_imaginary_size\t%lu\n", sizeof(float));
		fprintf(writer, "double_imaginary_size\t%lu\n", sizeof(double));
		fprintf(writer, "long_double_imaginary_size\t%lu\n", sizeof(long double));
		fprintf(writer, "pointer_size\t%lu\n", sizeof(char *));
		fprintf(writer, "va_list_size\t%lu\n", sizeof(void *));
		fclose(writer);
		fprintf(stdout, "cruntime.txt generated.\n");
		exit(0);
	}
	else 
	{
		fprintf(stderr, "Usage: a.out cruntime.txt\n");
		exit(-1);
	}
}


