///*
// * Queue
// *
// *  Created on: Oct 4, 2016
// *      Author: T440
// */
//
#ifndef HEADER_QUEUE_H_
#define HEADER_QUEUE_H_

#define                  DOLOCK                     1
#define                  DOUNLOCK                   0
#define                  SUSPENDUNTILLOCKED        TRUE
#define                  DONOTSUSPEND              FALSE


typedef struct Node{
	void* data;
	struct Node* next;
}Node,*QueuePtr;


typedef struct Queue{
	QueuePtr front;
	QueuePtr rear;
	int size;
}Queue;



extern QueuePtr DeQueue(Queue* q);
extern QueuePtr GetHead(Queue* q);
extern void EnQueue(Queue* q, void* data);
extern void EnTimerQueue(Queue* q, void* data);


#endif /* HEADER_QUEUE_H_ */
