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

/* recursive quick sorting implementation */
static void _qsort(int *list, int beg, int end) {
    int left, right, pivot;
    if(beg < end) {
        /* initialization */ 
        left = beg; 
        right = end - 1; 
        pivot = list[left];

        /* divide the elements in list by the pivot  */ 
        while(right > left) {
            /* skip the greater ones in right side until... */ 
            while((right > left) && (list[right] >= pivot)) 
            {
            	right--;
            }
            list[left] = list[right]; 
            
            /* skip the smaller ones in left side until... */ 
            while((right > left) && (list[left] <= pivot)) 
            {
            	left++;
            }
            list[right] = list[left];
        }
        list[left] = pivot;

        /* recursive solutions */ 
        _qsort(list, beg, left);
        _qsort(list, left + 1, end);
    }
}

/* sort algorithm to sort the list */
void sort(int * list, int length) 
{
	_qsort(list, 0, length);
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

