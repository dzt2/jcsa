/***************************************************
 * The fizz_buzz program prints "fizz" for multipliers
 * of three while "buzz" for multipliers of fives, and
 * "fizzbuzz" for multipliers of both three and five.
 * 
 * It receives a sequence of integers as inputs, which
 * will be printed over the standard output, of which 
 * the multipliers of three are replaced by "F" while 
 * the multipliers of five are replaced by "B" while
 * the multipliers of seven are replaced by "K".
***************************************************/
#include <stdio.h>
#include <stdlib.h>

#define FUZZ "F"
#define BUZZ "B"
#define KAZZ "K"


/** parse input arguments to an integer list **/
int * parse_input(int argc, char *argv[])
{
	int * list, length, k;
	length = argc - 1;
	list = (int *) malloc(sizeof(int) * length);
	for(k = 1; k < argc; k++)
	{
		list[k - 1] = atoi(argv[k]);
	}
	return list;
}


/* print the fizz-buzz sequence by integers */
void fizz_buzz(int *list, int n) {
    int i, x;
    for(i = 0; i < n; i++) 
    {
        x = list[i];
        if(x % 3 == 0)
        {
            fprintf(stdout, "%s; ", FUZZ);
        }
        else if(x % 5 == 0)
        {
            fprintf(stdout, "%s; ", BUZZ);
        }
        else if(x % 7 == 0)
        {
            fprintf(stdout, "%s; ", KAZZ);
        }
        else 
        {
        	fprintf(stdout, "%d; ", x);
        }
    }
    fprintf(stdout, "\n");
}


/* main tester method */
int main(int argc, char **argv) {
    int * list;
    list = parse_input(argc, argv);
    fizz_buzz(list, argc-1);
    free(list); 
    return 0;
}


