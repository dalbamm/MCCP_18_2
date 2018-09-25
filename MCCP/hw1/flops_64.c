#include <stdio.h>
#include <time.h>
int main(void){
	long x=1;
	long long cnt;
	long start, end;double t;int k;
	printf("Enter the number of instructions(unit=2M): ");
	scanf("%lld",&cnt);
	k=cnt;
	cnt*=1000000;
	start=clock();
	while(cnt--){
		x+=1;
		x*=0.5;
	}
	end=clock();
	t=(end-start)/1000000.;
	printf("time elapsed: %0.4lf(sec)\nflops: %0.4lf(GHz)\n", t, 2*k/t/1000);
	return 0;
}
