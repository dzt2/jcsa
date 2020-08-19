/**
 *	The program accepts the integer parameters as inputs and prints its sorted version.
 *		./a.exe num1 num2 num3 ... numN
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

/* print the integer list and free it */
void print_list(int * list, int length)
{
	int k;
	for(k = 0; k < length; k++)
	{
		fprintf(stdout, "%d; ", list[k]);
	}
	fprintf(stdout, "\n");
}

/* sort algorithm to sort the list */
void sort(int * list, int length) 
{
	int i, j, k, tmp;
	for(i = 1; i < length; i++)
	{
		/* find j where list[i] is inserted */
		for(j = 0; j < i; j++)
		{
			if(list[i] < list[j])
			{
				break;
			}
		}
		
		/* insert the list[i] at j location */
		tmp = list[i];
		for(k = i; k > j; k--)
		{
			list[k] = list[k - 1];
		}
		list[j] = tmp;
	}
}

/* main function for testing */
int main(int argc, char *argv[])
{
	int * list;
	list = parse_input(argc, argv);
	sort(list, argc - 1);
	print_list(list, argc - 1);
	free(list);
	return 0;
}

