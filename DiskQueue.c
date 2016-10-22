/*
 * DiskQueue.c
 *
 *  Created on: Oct 4, 2016
 *      Author: T440
 */

#include <stdio.h>
#include <string.h>
#include "DiskQueue.h"
#include "ReadyQueue.h"

int disk_Queue_size;

struct disk_Node *disk0_Head = NULL;
struct disk_Node *disk0_Tail = NULL;

struct disk_Node *disk1_Head = NULL;
struct disk_Node *disk1_Tail = NULL;
struct disk_Node *disk2_Head = NULL;
struct disk_Node *disk2_Tail = NULL;
struct disk_Node *disk3_Head = NULL;
struct disk_Node *disk3_Tail = NULL;
struct disk_Node *disk4_Head = NULL;
struct disk_Node *disk4_Tail = NULL;
struct disk_Node *disk5_Head = NULL;
struct disk_Node *disk5_Tail = NULL;
struct disk_Node *disk6_Head = NULL;
struct disk_Node *disk6_Tail = NULL;
struct disk_Node *disk7_Head = NULL;
struct disk_Node *disk7_Tail = NULL;

void Queue_D_Enqueue(struct disk_PCB *data) {
	int index = data->diskID;
	struct disk_Node *tmp = (struct disk_Node *) malloc(
			sizeof(struct disk_Node));
	struct disk_Node *currentHead = NULL;
	struct disk_Node *currentTail = NULL;

	switch (index) {
	case 0:
		currentHead = disk0_Head;
		currentTail = disk0_Tail;
		break;
	case 1:
		currentHead = disk1_Head;
		currentTail = disk1_Tail;
		break;
	case 2:
		currentHead = disk2_Head;
		currentTail = disk2_Tail;
		break;
	case 3:
		currentHead = disk3_Head;
		currentTail = disk3_Tail;
		break;
	case 4:
		currentHead = disk4_Head;
		currentTail = disk4_Tail;
		break;
	case 5:
		currentHead = disk5_Head;
		currentTail = disk5_Tail;
		break;
	case 6:
		currentHead = disk6_Head;
		currentTail = disk6_Tail;
		break;
	case 7:
		currentHead = disk7_Head;
		currentTail = disk7_Tail;
		break;
	default:

		currentHead = currentTail = NULL;
	}

	tmp->data = data;
	tmp->next = NULL;

	if (currentHead == NULL && currentTail == currentHead) {

		switch (index) {
		case 0:
			disk0_Head = disk0_Tail = tmp;
			break;
		case 1:
			disk1_Head = disk1_Tail = tmp;
			break;
		case 2:
			disk2_Head = disk2_Tail = tmp;
			break;
		case 3:
			disk3_Head = disk3_Tail = tmp;
			break;
		case 4:
			disk4_Head = disk4_Tail = tmp;
			break;
		case 5:
			disk5_Head = disk5_Tail = tmp;
			break;
		case 6:
			disk6_Head = disk6_Tail = tmp;
			break;
		case 7:
			disk7_Head = disk7_Tail = tmp;
			break;
		default:

			break;
		}

	} else {

		currentTail->next = tmp;
		currentTail = tmp;
	}
	disk_Queue_size++;
}

void Queue_D_Dequeue(struct disk_PCB *data) {
	int index = data->diskID;

	struct disk_Node *tmp = (struct disk_Node *) malloc(
			sizeof(struct disk_Node));
	struct disk_Node *currentHead = NULL;
	struct disk_Node *currentTail = NULL;

	switch (index) {
	case 0:
		currentHead = disk0_Head;
		currentTail = disk0_Tail;
		break;
	case 1:
		currentHead = disk1_Head;
		currentTail = disk1_Tail;
		break;
	case 2:
		currentHead = disk2_Head;
		currentTail = disk2_Tail;
		break;
	case 3:
		currentHead = disk3_Head;
		currentTail = disk3_Tail;
		break;
	case 4:
		currentHead = disk4_Head;
		currentTail = disk4_Tail;
		break;
	case 5:
		currentHead = disk5_Head;
		currentTail = disk5_Tail;
		break;
	case 6:
		currentHead = disk6_Head;
		currentTail = disk6_Tail;
		break;
	case 7:
		currentHead = disk7_Head;
		currentTail = disk7_Tail;
		break;
	default:

		currentHead = currentTail = NULL;
	}

	if (currentHead != NULL && currentHead != NULL) {
		if (currentHead == currentTail) {
			switch (index) {
			case 0:
				disk0_Head = disk0_Tail = NULL;
				break;
			case 1:
				disk1_Head = disk1_Tail = NULL;
				break;
			case 2:
				disk2_Head = disk2_Tail = NULL;
				break;
			case 3:
				disk3_Head = disk3_Tail = NULL;
				break;
			case 4:
				disk4_Head = disk4_Tail = NULL;
				break;
			case 5:
				disk5_Head = disk5_Tail = NULL;
				break;
			case 6:
				disk6_Head = disk6_Tail = NULL;
				break;
			case 7:
				disk7_Head = disk7_Tail = NULL;
				break;
			default:

				break;
			}
		} else {

			currentHead = currentHead->next;
		}

		free(tmp);
		tmp = NULL;
		disk_Queue_size--;
	}

}

boolean Queue_D_Remove(struct disk_PCB *data) {
	int index = data->diskID;
	struct disk_Node *tmp = NULL;

	boolean result = FALSE;
	if (tmp != NULL && data->sector == tmp->data->sector) {
		switch (index) {
		case 0:
			disk0_Head = disk0_Head->next;
			break;
		case 1:
			disk1_Head = disk1_Head->next;
			break;
		case 2:
			disk2_Head = disk2_Head->next;
			break;
		case 3:
			disk3_Head = disk3_Head->next;
			break;
		case 4:
			disk4_Head = disk4_Head->next;
			break;
		case 5:
			disk5_Head = disk5_Head->next;
			break;
		case 6:
			disk6_Head = disk6_Head->next;
			break;
		case 7:
			disk7_Head = disk7_Head->next;
			break;
		default:

			break;
		}
		result = TRUE;
		disk_Queue_size--;


	} else {
		while (tmp->next != NULL) {
			if (data->sector == tmp->next->data->sector) {
				struct disk_Node *remove = tmp->next;
				tmp->next = tmp->next->next;
				if (tmp->next == NULL) {
					switch (index) {
					case 0:
						disk0_Tail = tmp;
						break;
					case 1:
						disk1_Tail = tmp;
						break;
					case 2:
						disk2_Tail = tmp;
						break;
					case 3:
						disk3_Tail = tmp;
						break;
					case 4:
						disk4_Tail = tmp;
						break;
					case 5:
						disk5_Tail = tmp;
						break;
					case 6:
						disk6_Tail = tmp;
						break;
					case 7:
						disk7_Tail = tmp;
						break;
					default:

						break;
					}
				}
				result = TRUE;
				disk_Queue_size--;
				break;
			}

			tmp = tmp->next;

		}

	}
	return result;
}

struct disk_PCB *getHead(struct disk_PCB *data) {
	int index = data->diskID;
	struct disk_Node *currentHead = (struct disk_Node *) malloc(
			sizeof(struct disk_Node));
	struct disk_PCB * head;
	switch (index) {
		case 0:
			currentHead = disk0_Head;

			break;
		case 1:
			currentHead = disk1_Head;

			break;
		case 2:
			currentHead = disk2_Head;

			break;
		case 3:
			currentHead = disk3_Head;

			break;
		case 4:
			currentHead = disk4_Head;

			break;
		case 5:
			currentHead = disk5_Head;

			break;
		case 6:
			currentHead = disk6_Head;

			break;
		case 7:
			currentHead = disk7_Head;

			break;
		default:

			currentHead = NULL;
		}


	return currentHead;
}

