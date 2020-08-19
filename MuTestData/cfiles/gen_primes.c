/**
 *	The program generates the primes from 0 to a given integer.
 *	./a.exe N
 *	>> p1 p2 ... pK
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

/* main function for testing */
int main(int argc, char *argv[])
{
	int k, n;
	if(argc > 1)
	{
		n = atoi(argv[1]);
		for(k = 2; k <= n; k++)
		{
			if(is_prime(k))
			{
				fprintf(stdout, "%d; ", k);
			}
		}
		fprintf(stdout, "\n");
		return 0;
	}
	else
	{
		fprintf(stderr, "Invalid usage: a.exe N\n");
		return -1;
	}
}


