#include <stdio.h>
#include <time.h>
int main(void){
	float x=1;
	long long cnt;
	long start, end;double t;int k;
	printf("Enter the number of instructions(unit=2M): ");
	scanf("%lld",&cnt);
	k=cnt;
	cnt*=1000000;
	start=clock();
	while(cnt--){
		x=0.5*0.1;
		x=x+0.23;
	}
	end=clock();
	t=(end-start)/1000000.;
	printf("time elapsed: %0.4lf(sec)\nflops: %lf(Gflops)\n", t, 2*k/t/1000);

	return 0;
}
