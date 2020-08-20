/**
 *	The program implements the binary-search algorithm, which:
 *		(1) it accepts the integer parameters as inputs;
 *		(2) it first parses the inputs into integer list;
 *		(3)	it then sorts integer[1:n] into sorted list;
 *		(4) it then performs bi-search on list[1:n] by list[0];
 *		(5) it finally prints the index of list[0] in list[1:n];
 **/

#include <stdio.h>
#include <stdlib.h>

/* parse the arguments into integer list */
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

/* used to compare integers for qsort method */
int cmp_int(const void *xptr, const void *yptr) {
    int x, y;
    x = *((int *) xptr);
    y = *((int *) yptr);
    return x - y;
}

/* binary-search algorithm or return -1 if not found */
int bi_search(int *list, int length, int value) 
{
    int beg, mid, end;
    beg = 0;
    end = length - 1;
    while(beg <= end) 
    {
        mid = (beg + end) / 2;
        if(list[mid] < value)
        {
            beg = mid + 1;
        }
        else if(list[mid] > value)
        {
            end = mid - 1;
        }
        else
        {
            return mid;
        }
    }
    return -1;
}

/* main function */
int main(int argc, char *argv[]) 
{
	/* declarations */
    int *list, value, answer;
    
    /* normal branch */
    if(argc > 1)
    {
    	/* parse the inputs, sort it and search the index */
    	list = parse_input(argc, argv);
    	value = list[0];
    	qsort(list + 1, argc - 2, sizeof(int), cmp_int);
    	answer = bi_search(list, argc - 2, value);
    	free(list);
    	
    	/* print the output list */
    	if(answer >= 0) 
    	{
        	fprintf(stdout, "list[%d] = %d\n", answer, value);
    	}
    	else
    	{
        	fprintf(stdout, "%d is not in list.\n", value);
    	}
    	return 0;
    }
    /* exceptional branch */
    else
    {
    	fprintf(stderr, "Too few arguments are provided.\n");
    	return -1;
    }
}

