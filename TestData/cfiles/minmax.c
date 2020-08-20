/**
 *	The program prints the minimal and maximal value in the input integer sequence.
 *	>> a.exe x1 x2 x3 x4 ... xN
 *	>> min = xxx; max = xxx;
 **/

#include <stdio.h>
#include <stdlib.h>

/* parse the input parameters to integer list */
int * parse_input(int argc, char *argv[]) 
{
	int * list, k;
	list = (int *) malloc(sizeof(int) * (argc - 1));
	for(k = 1; k < argc; k++) 
	{
		list[k - 1] = atoi(argv[k]);
	}
	return list;
}

/* generate the minimal and maximal values */
void min_max(int *list, int length)
{
	int k, min_value, max_value;
	if(length > 0)
	{
		min_value = list[0];
		max_value = list[0];
		for(k = 1; k < length; k++)
		{
			if(list[k] > max_value)
			{
				max_value = list[k];
			}
			if(list[k] < min_value)
			{
				min_value = list[k];
			}
		}
		printf("min = %d; max = %d;\n", min_value, max_value);
	}
}

/* main function for testing */
int main(int argc, char *argv[])
{
	int * list, length;
	length = argc - 1;
	list = parse_input(argc, argv);
	min_max(list, length);
	free(list);
	return 0;
}


