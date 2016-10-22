/*
 * DiskQueue.h
 *
 *  Created on: Oct 4, 2016
 *      Author: T440
 */


#ifndef HEADER_DISKQUEUE_H_
#define HEADER_DISKQUEUE_H_




#include <stdio.h>
#include <stdlib.h>

typedef int boolean;
#define true 1
#define false 0

struct disk_Node {
    struct  disk_PCB *data;
    struct disk_Node *next;
}disk_Node;

struct disk_PCB {
    volatile int disk_state;
    long diskID;
    long sector;
    void *disk_context;
}disk_PCB;














#endif /* HEADER_DISKQUEUE_H_ */
