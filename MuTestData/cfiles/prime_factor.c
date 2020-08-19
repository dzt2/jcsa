/**
 *	It prints the prime factor that constructs the given number.
 *	>> a.exe x1 x2 x3 ... xN
 *	>> x1 p1 p2 ... pn
 *	>> x2 q1 q2 ... qm
 *	...
 **/

#include <stdio.h>
#include <stdlib.h>

/* print the prime factor that constructs the number */
void prime_factor(int number)
{
	int k = 2;
	printf("%d: ", number);
	while(number > 1 || number < -1)
	{
		while((number > 1 || number < -1) && ((number % k) == 0))
		{
			printf("%d; ", k);
			number = number / k;
		}
		k++;
	}
	printf("\n");
}

/* main function being tested */
int main(int argc, char *argv[])
{
	int k, number;
	for(k = 1; k < argc; k++)
	{
		number = atoi(argv[k]);
		prime_factor(number);
	}
	return 0;
}


