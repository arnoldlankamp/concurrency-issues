#include <pthread.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

uint64_t global = 0;

static void check() {
    uint64_t doubleWord = global;
    if ((doubleWord >> 32) != (doubleWord & 0xffffffff)) {
        fprintf(stderr, "Concurrency issue detected\nRead: 0x%llx\n", doubleWord);
        exit(1);
    }
}

static void *runTest(void *voidPtr) {
    uint64_t doubleWord = *((uint64_t *) voidPtr);
    uint32_t i;
    for (i = 0; i < 10000000; i++) {
        global = doubleWord;
        check();
    }
    return NULL;
}

int main() {
    pthread_t threads[8];
    uint32_t i;
    uint64_t ids[8];
    for (i = 0; i < 8; i++) {
        ids[i] = ((uint64_t) i + 1) << 32 | i + 1;
        fprintf(stdout, "Created job with id: %llx\n", ids[i]);
        if (pthread_create(&threads[i], NULL, runTest, &ids[i])) {
            fprintf(stderr, "Error creating thread\n");
            return 1;
        }
    }

    for (i = 0; i < 8; i++) {
        if (pthread_join(threads[i], NULL)) {
            fprintf(stderr, "Failed to join on thread\n");
            return 1;
        }
    }

    printf("No problems detected\n");

    return 0;
}
