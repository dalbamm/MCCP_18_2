#include <stdio.h>
#include <string.h>

int main(void){
	char buffer[1000];
	int len , i,flag=0;//flag 0--> bits to real
	
	printf("Enter a number: ");
	scanf("%s", buffer);
	len = strlen(buffer);
	for (i = 0; i < len; ++i) {
		if (buffer[i] == '.') {
			flag = 1;//flag 1--> real to bits
			break;
		}
	}
	printf("len = %d, flag = %d\n", len, flag);
	printf("%s\n", buffer);

	return 0;
}
