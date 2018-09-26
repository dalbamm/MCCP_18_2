#include <stdio.h>
#include <time.h>
#include <immintrin.h>

void calc_avx(const __m256d *x1,
	const __m256d *x2, const __m256d *x3, __m256d *y) {
	*y = _mm256_mul_pd(*x1, *x2);
	*y = _mm256_add_pd(*y, *x3);
}

int main(void){
	long x=1;
	long long cnt,cnt2;
	long start, end;double t;int k,q;
	double a[4],b[4],c[4],d[4],rst1,rst2;
	__m256d vec1, vec2, vec3, rst; int i;
	vec1 = _mm256_set_pd(1.,2.,3.,4.);
	vec2 = _mm256_set_pd(1., 2., 3., 4.);
	vec3 = _mm256_set_pd(-0., -2., -6., -12.);
	for(k = 0 ; k < 4 ; ++k){
		a[k]=k+1;
		b[k]=k+1;
		c[k]=-k*(k+1);
	}

	printf("Enter the number of instructions(unit=8M): ");
	scanf("%d",&q);
	puts("--------------------------------------------------------");
	
	cnt=q * 1000000;
	cnt2=cnt;
	
	start=clock();
	while(cnt2--)
	{
		for(k=0;k<4;++k){
			d[k]=a[k]*b[k];
			d[k]=c[k]+d[k];
		}
	}
	end=clock();
	t=(end-start)/1000000.;
	printf("[Normal Instructions]\ntime elapsed: %0.4lf(sec)\nflops: %0.4lf(GHz)\n", t, rst1=8*q/t/1000);
	puts("--------------------------------------------------------");
//	printf("[0]: %lf\n[1]: %lf\n[2]: %lf\n[3]: %lf\n", d[0], d[1], d[2], d[3]);
	
	start=clock();
	while(cnt--)
		calc_avx(&vec1, &vec2, &vec3, &rst);
	end=clock();
	t=(end-start)/1000000.;
	printf("[FMA Instructions]\ntime elapsed: %0.4lf(sec)\nflops: %0.4lf(GHz)\n", t, rst2=8*q/t/1000);

	puts("--------------------------------------------------------");
	printf("[Increase Rate]: %0.0lf%%\n",(rst2/rst1-1)*100);
	return 0;
}
/*int main(void) {
	__m256d vec1, vec2, vec3, rst; int i;
	double x = 1., y = 2., z = 3., d = 0, arr[4];
	arr[0] = x; arr[1] = y; arr[2] = z; arr[3] = 0;
	vec1 = _mm256_set_pd(1.,2.,3.,4.);
	vec2 = _mm256_set_pd(1., 2., 3., 4.);
	vec3 = _mm256_set_pd(-0., -2., -6., -12.);
//	rst = _mm256_set_pd(1., 2., 3., 4.);

	for (i = 0; i < 2; ++i) {
		printf("[0]: %lf\n[1]: %lf\n[2]: %lf\n[3]: %lf\n", rst[0], rst[1], rst[2], rst[3]);
		foo_avx(&vec1, &vec2, &vec3, &rst);
		printf("[0]: %lf\n[1]: %lf\n[2]: %lf\n[3]: %lf\n", rst[0], rst[1], rst[2], rst[3]);
	}
	printf("[0]: %lf\n[1]: %lf\n[2]: %lf\n[3]: %lf\n", rst[0],rst[1],rst[2],rst[3]);
	return 0;
}
*/
