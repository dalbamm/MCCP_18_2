#include <stdio.h>
#include <string.h>
#include <stdlib.h>
void mapnum(char k, char arr[], int idx){
	int i,op=2;
	for(i = 3 ; i >= 0; --i){
		arr[idx+i]=(k%op)/(op/2);
		op*=2;
	}
}
int main(void){
	char buffer[1000], *numch,*rawp, rst[64];
	int len,i, sign, exp, man, raw[4];
	double samp;
	printf("Enter a number: ");
	scanf("%s", buffer);
	len = strlen(buffer);
	numch = (char*)malloc(len*sizeof(char));
	strcpy(numch, buffer);
	samp = atof(numch);
	rawp = &samp;	
	for (i = 15; i >= 0; --i) { mapnum(rawp[i / 2] >> (i % 2 ? 4 : 0) & 15, rst, (15-i)*4);
			}
	for( i = 0 ; i < 64 ; ++i){
		printf("%d",rst[i]);
	}
	printf("\n%lf\n",samp);
	free(numch);
	return 0;
}
