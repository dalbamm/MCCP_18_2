#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
//====================================================================
// a[N][M] x b[M][N] = c[N][N]
//====================================================================
#define NUM_THREADS 32


  float* ga;
  float* gb;
  float* gc;
  int Ndiv ;
  int gN;
  int gM;

void pmat_mul(void* threadarg) {
  int offset = (int)threadarg;
  for (int i = offset*Ndiv; i < offset*Ndiv + Ndiv; i++) {
    for (int j = 0; j < gN; j++) {
      for (int k = 0; k < gM; k++) {
        gc[i * gN + j] += ga[i * gM + k] * gb[k * gN + j];
      }
    }
  }
}
void mat_mul(float *a, float *b, float *c, int N, int M) {
    void* status;
    pthread_attr_t attr;
    pthread_t threads[NUM_THREADS];
    long t;int rc;
    ga=a; gb=b ; gc=c ; gN=N ; gM=M ; Ndiv=N/NUM_THREADS;
    
    pthread_attr_init(&attr);
    pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_JOINABLE);

    for(t= 0 ; t < NUM_THREADS; t++){
      rc = pthread_create(&threads[t], &attr, pmat_mul,(void*)t);
      if(rc){
        printf("Error");
        exit(-1);
      }
    }

    pthread_attr_destroy(&attr);
    for(t= 0 ; t < NUM_THREADS; t++){
      rc = pthread_join(threads[t],&status);
      if(rc){
        printf("Error");
        exit(-1);
      }
    }

/*pmat_mul(0);
pmat_mul(1);*/}


void mat_mul2(float *a, float *b, float *c,int offset,int Ndiv, int N, int M) {
  for (int i = offset*Ndiv; i < offset*Ndiv + Ndiv; i++) {
    for (int j = 0; j < N; j++) {
      for (int k = 0; k < M; k++) {
        c[i * N + j] += a[i * M + k] * b[k * N + j];
      }
    }
  }
}
