#include <stdio.h>
#include <stdlib.h>


static FILE * jcm_writer;


static void jcm_write(int id, void * address, unsigned int length) {
	fprintf(jcm_writer, "%d", id);
	unsigned char * char_address = (unsigned char *) address;
	for(int k = 0; k < length; k++) {
		fprintf(jcm_writer, " %d", char_address[k]);
	}
	fprintf(jcm_writer, "\n");
	fflush(jcm_writer);
}


#define jcm_eval(x, y)	({typeof(y) __result__ = (y); jcm_write((x), &__result__, sizeof(__result__)); __result__;})

