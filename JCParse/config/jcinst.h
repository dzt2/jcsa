#include <stdio.h>
#include <stdlib.h>


static FILE * jcm_writer;


static void jcm_open(char * filepath) {
	jcm_writer = fopen(filepath, "w");
}


static void jcm_append(int id, void * address, unsigned int length) {
	fprintf(jcm_writer, "%d", id);
	unsigned char * char_address = (unsigned char *) address;
	for(int k = 0; k < length; k++) {
		fprintf(jcm_writer, " %d", char_address[k]);
	}
	fprintf(jcm_writer, "\n");
	fflush(jcm_writer);
}


#define jcm_add(x, y)	({typeof(y) __result__ = (y); jcm_append((x), &__result__, sizeof(__result__)); __result__;})


static void jcm_close() {
	fclose(jcm_writer);
}


