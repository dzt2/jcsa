/**
 *	The program accepts the integer parameters as inputs and prints only the prime integers in the list
 *		./a.exe num1 num2 num3 ... numN
 *		>>	numk1 numk2 ... numKt
 **/

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

/* whether x is prime */
int is_prime(int x) 
{
    int k, n;
    if(x < 2) 
    {
        return 0;
    }
    else 
    {
        n = (int) (sqrt(x));
        for(k = 2; k <= n; k++) 
        {
            if( (x % k) == 0 ) 
            {
                return 0;
            }
        }
        return 1;
    }
}

/* main testing method */
int main(int argc, char * argv[])
{
	int k, number;
	for(k = 1; k < argc; k++)
	{
		number = atoi(argv[k]);
		if(is_prime(number))
		{
			fprintf(stdout, "%d; ", number);
		}
	}
	fprintf(stdout, "\n");
	return 0;
}



