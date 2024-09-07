package MyInventory;

public class Lego {
	//private instance variables
	private int serial_number;
	private String set_name;
	private String theme;
	private int year;
	private int pieces;
	private boolean second_hand;
	private boolean box_included;
	
	//Accessor and Mutator Methods
	public int getSerialNumber() {
		return this.serial_number;
	}
	
	public void setSerialNumber(int serial_number) {
		this.serial_number = serial_number;
	}
	
	public String getSetName() {
		return this.set_name;
	}
	
	public void setSetName(String set_name) {
		this.set_name = set_name;
	}
	
	public String getTheme() {
		return this.theme;
	}
	
	public void setTheme(String theme) {
		this.theme = theme;
	}
	
	public int getYear() {
		return this.year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public int getPieces() {
		return this.pieces;
	}
	
	public void setPieces(int pieces) {
		this.pieces = pieces;
	}
	
	public boolean getSecondHand() {
		return this.second_hand;
	}
	
	public void setSecondHand(boolean second_hand) {
		this.second_hand = second_hand;
	}
	
	public boolean getBox() {
		return this.box_included;
	}
	
	public void setBox(boolean box_included) {
		this.box_included = box_included;
	}
}

class LinkedList {
	Node head; //head of the list
	private class Node {
		Lego lego;
		Node next; //next Node
		
		Node(Lego lego) {
			this.lego = lego;
		}
	}
	
	//returns whether or not list is empty
	public Boolean isEmpty() {
		return head==null;
	}
	
	//returns size of list
	public int getSize() {
		if (isEmpty()) {
			return 0;
		}
		Node temp = head;
		int i = 1;
		while (temp.next != null) {
			temp = temp.next;
			i++;
		}
		return i;
	}
	
	//addNext (New Node becomes head)
	public void addNext(Lego lego) {
		Node newNode = new Node(lego);
		newNode.next = head;
        head = newNode;
	}
	
	//add Node at given position
	public void addNode(Lego lego, int position) {
	    if (position < 0) {
	        throw new IllegalStateException("Invalid position. Position must be non-negative.");
	    }

	    Node newNode = new Node(lego);

	    if (position == 0) { //if position is 0 (head), addNext
	        addNext(lego);
	    } 
	    else { //for every other case
	        Node temp = head; //use for help
	        int current_pos = 0;
	       
	        //iterate to Node before target Node
	        while (temp != null && current_pos < position) {
	            temp = temp.next;
	            current_pos++;
	        }

	        if (temp == null) { //position out of bounds
	        	throw new IllegalStateException("Position Out of Bounds");
	        } 
	        else 
	        {
	            newNode.next = temp.next; //NewNode points to the Node after temp (or null if in the end)
	            temp.next = newNode; //Temp points to newNode
	        }
	    }
	}
	
	//delete node at given position
	public void deleteNode(int position) {
	    if (position < 0) { //position is not valid, throw Exception
	        throw new IllegalStateException("Invalid position. Position must be non-negative.");
	    }

	    if (head == null) { //if linked list is empty, throw exception
	        throw new IllegalStateException("List is empty. Cannot delete from an empty list.");
	    }

	    if (position == 0) { //delete head
	        head = head.next;
	    } 
	    else {
	        Node temp = head; //use for help
	        int current_pos = 0;
	        
	        //iterate to node before target node
	        while (temp != null && current_pos < position-1) {
	            temp = temp.next;
	            current_pos++;
	        }

	        //throw exception if the target node or its preceding node is null
	        if (temp == null || temp.next == null) {
	        	throw new IllegalStateException("Position is out of bounds. Cannot delete. Position: " + position);
	        } 
	        else {
	            temp.next = temp.next.next; //point to the Node after the target Node
	        }
	    }
	}
	//return the Lego of Node at a given position in the list
	public Lego getNode(int position) {
		if (position < 0) { //position is not valid, throw Exception
			throw new IllegalStateException("Invalid position. Position must be non-negative.");
		}
		Node temp = head;
		int i = 0;
		while (i < position) { //iterate to target Node
			if (temp != null) { //validation method to find out if position is out of bounds
				temp = temp.next;
				i++;
			}
			else { //position out of bounds
				throw new IllegalStateException("Position is out of bounds. Cannot get value. Position: " + position);
			}
		}
		return temp.lego;
	}
	//convert the linked list to an array
	public Lego[] convertToArray() {
		Lego[] lego = new Lego[this.getSize()]; //declare array with size of linked list
		Node temp = head;
		for (int i=0; i<lego.length; i++) {
			lego[i] = temp.lego;
			temp = temp.next;
		}
		return lego;
	}
	//convert an array to a linked list
	public static LinkedList convertToList(Lego[] lego) {
		LinkedList list = new LinkedList();
		for (int i=0; i<lego.length; i++) {
			list.addNext(lego[lego.length-i-1]);
		}
		return list;
	}
	
	//merging method for merge sorting (year)
	public static void merge_forYear(Lego[] legoArray, Lego[] left, Lego[] right) {
	    int nL = left.length; 
	    int nR = right.length;
	    int i = 0, j = 0, k = 0;

	    while (i < nL && j < nR) {
	    	//inserting to legoArray in ascending order
	        if (left[i].getYear() <= right[j].getYear()) {
	            legoArray[k] = left[i]; //insert from left when smaller than the element in right
	            i++;
	        } else {
	            legoArray[k] = right[j]; //insert from right when smaller than the element in left
	            j++;
	        }
	        k++;
	    }

	    //when all right elements are already inserted, insert the remaining left elements
	    while (i < nL) {
	        legoArray[k] = left[i];
	        i++;
	        k++;
	    }

	    //when all left elements are already inserted, insert the remaining right elements
	    while (j < nR) {
	        legoArray[k] = right[j];
	        j++;
	        k++;
	    }
	}
	
	//merging method for merge sorting (pieces)
	public static void merge_forPieces(Lego[] legoArray, Lego[] left, Lego[] right) {
	    int nL = left.length;
	    int nR = right.length;
	    int i = 0, j = 0, k = 0;

	    while (i < nL && j < nR) {
	    	//inserting to legoArray in ascending order
	        if (left[i].getPieces() <= right[j].getPieces()) {
	            legoArray[k] = left[i]; //insert from left when smaller than the element in right
	            i++;
	        } else {
	            legoArray[k] = right[j]; //insert from right when smaller than the element in left
	            j++;
	        }
	        k++;
	    }

	    //when all right elements are already inserted, insert the remaining left elements
	    while (i < nL) {
	        legoArray[k] = left[i];
	        i++;
	        k++;
	    }
	   
	    //when all left elements are already inserted, insert the remaining right elements
	    while (j < nR) {
	        legoArray[k] = right[j];
	        j++;
	        k++;
	    }
	}
	
	//recursive Merge Sorting Method (Year)
	public static Lego[] sort_byYear(Lego[] legoArray) {
	    if (legoArray == null) {
	        return legoArray;
	    }
	    //split to two subarrays
	    int n = legoArray.length;
	    if (n > 1) {
	        int mid = n / 2;
	        Lego[] left = new Lego[mid];
	        Lego[] right = new Lego[n - mid];

	        //fill left and right subarrays
	        for (int i = 0; i < mid; i++) {
	            left[i] = legoArray[i];
	        }
	        for (int i = mid; i < n; i++) {
	            right[i - mid] = legoArray[i];
	        }

	        //recursively sort the left and right subarrays
	        sort_byYear(left);
	        sort_byYear(right);

	        //merge the sorted subarrays
	        merge_forYear(legoArray, left, right);
	    }
	    return legoArray;
	}

	public static Lego[] sort_byPieces(Lego[] legoArray) {
	    if (legoArray == null) {
	        return legoArray;
	    }
	    //split to two subarrays
	    int n = legoArray.length;
	    if (n > 1) {
	        int mid = n / 2;
	        Lego[] left = new Lego[mid];
	        Lego[] right = new Lego[n - mid];

	        //fill left and right subarrays
	        for (int i = 0; i < mid; i++) {
	            left[i] = legoArray[i];
	        }
	        for (int i = mid; i < n; i++) {
	            right[i - mid] = legoArray[i];
	        }

	        //recursively sort the left and right subarrays
	        sort_byPieces(left);
	        sort_byPieces(right);

	        //merge the sorted subarrays
	        merge_forPieces(legoArray, left, right);
	    }
	    return legoArray;
	}

	//reverse an array with stack and queue
	public static Lego[] reverse_list(Lego[] legoArray) {
		LegoQueue queue = new LegoQueue(legoArray);
		Lego[] temp = new Lego[queue.getCapacity()];
		LegoStack stack = new LegoStack(temp);
		while(!queue.isEmpty()) { //dequeue all elements and push to stack
			stack.push(queue.dequeue());
		}
		while(!stack.isEmpty()) { //enqueue all elements popped from stack
			queue.enqueue(stack.pop());
		}
		return queue.getQueue();
	}

	//filter by theme: remove all elements from linked list that are != target theme
	public void filterByTheme(String theme) {
		Node temp = head;
		int i = 0;
		while(temp!=null) {
			if (!temp.lego.getTheme().equalsIgnoreCase(theme)) {
				temp = temp.next;
				this.deleteNode(i); //points to next Node while deleting current Node
			} else {
				temp = temp.next; 
				i++; //points to next Node, add 1 to i as no Node is deleted in this iteration
			}
		}
	}
	
	//filter by second hand: remove all elements from linked list that are/are not second hand
    public void filterBySecondHand(boolean sh) {
		Node temp = head;
		int i = 0;
		while(temp!=null) {
			if (temp.lego.getSecondHand()!= sh) {
				temp = temp.next;			
				this.deleteNode(i); //points to next Node while deleting current Node
			}
			else {
				temp = temp.next;		
				i++; //points to next Node, add 1 to i as no Node is deleted in this iteration 
			}
		}
    }

    //filter by box: remove all elements from linked list that do/do not have a box
	public void filterByBox(boolean box) {
		Node temp = head;
		int i = 0;
		while(temp!=null) {
			if (temp.lego.getBox()!= box) {
				temp = temp.next;
				this.deleteNode(i); //points to next Node while deleting current Node
			}
			else {
				temp = temp.next;
				i++; //points to next Node, add 1 to i as no Node is deleted in this iteration 
			}
		}
	}
}

//queue for reversing sorted list
class LegoQueue {
	private Lego[] queue;
	private int capacity;
	
	//constructor
	LegoQueue(Lego[] queue) {
		this.queue = queue;
		this.capacity=queue.length;
	}
	
	//get size of queue
	int size() {
		for(int i=0; i<this.capacity; i++) {
			if (this.queue[i] == null) {
				return i;
			}
		}
		return this.capacity;
	}
	
	//returns whether or not queue is full
	boolean isFull() {
		return size()==this.capacity;
	}
	
	//returns whether or not queue is empty
	boolean isEmpty() {
		return size()==0;
	}
	
	//enqueue item into queue
	void enqueue(Lego new_lego) {
		if (isFull()) {
			throw new IllegalStateException("Queue is Full");
		}
		this.queue[size()] = new_lego;
	}
	
	//peek front of queue
	Lego peek() {
		if (isEmpty()) {
			return null;
		}
		Lego result = this.queue[0];
		return result;
	}
	
	//dequeue item from queue
	Lego dequeue() {
		if (isEmpty()) {
			throw new IllegalStateException("Queue is Empty");
		}
		Lego result = peek();
		int size = size();
		this.queue[0] = null;
		if (size < capacity) {
			for (int i=0; i<size; i++) {
				this.queue[i] = this.queue[i+1];
			}
		}
		else {
			for (int i=0; i<size-1; i++) {
				this.queue[i] = this.queue[i+1];
			}
			queue[size-1] = null;
		}
		return result;
		
	}
	
	//returns queue in the form of Lego array
	Lego[] getQueue() {
		return this.queue;
	}
	
	//returns capacity of the queue (array length)
	int getCapacity() {
		return this.capacity;
	}
}

//stack for reversing sorted list
class LegoStack {
	private Lego[] stack;
	private int capacity;
	
	//constructor
	LegoStack(Lego[] stack) {
		this.stack = stack;
		this.capacity = stack.length;
	}
	
	//returns size of stack
	int size() {
		for(int i=0; i<this.capacity; i++) {
			if (this.stack[i] == null) {
				return i;
			}
		}
		return this.capacity;
	}
	
	//returns whether or not stack is full
	boolean isFull() {
		return size()==this.capacity;
	}
	
	//returns whether or not stack is empty
	boolean isEmpty() {
		return size()==0;
	}
	
	//pushes item onto stack
	public void push(Lego new_lego) {
		if (isFull()) {
			throw new IllegalStateException("Queue is Full");
		}
		this.stack[size()] = new_lego;
	}
	
	//peeks the top of the stack
	Lego peek() {
		if (isEmpty()) {
			return null;
		}
		Lego result = this.stack[size()-1];
		return result;
	}
	
	//pops item from stack
	public Lego pop() {
		if (isEmpty()) {
			throw new IllegalStateException("Error: Stack is Empty");
		}
		Lego result = peek();
		this.stack[size()-1] = null;
		return result;
	}
	
	//returns stack in the form of Lego Array
	Lego[] getStack() {
		return this.stack;
	}
	
	//returns capacity of Stack (Length of Array)
	int getCapacity() {
		return this.capacity;
	}
}

//binary Search Tree
class LegoTree {
	private class Node {
		int position; //position in linked list
		int serial_number; //each Node's serial Number
		Node left; //the Node's left child
		Node right; //the Node's right child
		
		//Node constructor (each Node containing serial number and position)
		public Node(int serial_number, int position) {
			this.serial_number = serial_number;
			this.position = position;
			left = null;
			right = null;
		}
	}
	
	Node root;	//root of the tree
	
	//bst constructor
	LegoTree(LinkedList list) { //linked list as parameter (converts it to bst)
		Lego[] array = list.convertToArray();
		for (int i=0; i<array.length; i++) {
			insert(array[i].getSerialNumber(), i); //inserts a Node for each Lego containing its serial no and position in linked list
		}
	}
	
	//method to check if bst is empty
	public boolean isEmpty() {
		return this.root == null;
	}
	
	//insert element in bst
	public void insert(int serial_number, int position) {
		this.root = insert_recursively(this.root, serial_number, position); //call recursive insert method
	}
	
	//recursive insert method
	public Node insert_recursively(Node node, int serial_number, int position) {
		if (node == null) { //empty spot in tree has been reached, inserted node will be a new leaf
			node = new Node(serial_number, position);
			return node;
		}
		
		if (serial_number < node.serial_number) { //recursively call method for the left child if less than current Node
			node.left = insert_recursively(node.left, serial_number, position);
		}
		else if (serial_number > node.serial_number) { //recursively call method for the left child if more than current Node
			node.right = insert_recursively(node.right, serial_number, position);
		}
		
		return node; //
	}
	
	//search for a Node with target serial number and returns its position in linked list
	public int search(int serial_number) {
		Node a = search_recursively(this.root, serial_number); //call recursive search method
		if (a == null) {
			return -1; //not found
		}
		else {
			return a.position; //return Node's position
		}
	}
	
	//recursive search method
	public Node search_recursively(Node node, int serial_number) {
		if (node == null || node.serial_number == serial_number) { //the Node is either found or not found after bst search
			return node;
		}
		
		if (serial_number < node.serial_number) { //recursively call method for the left child if less than current Node
			return search_recursively(node.left, serial_number); 
		}
		else { //recursively call method for the left child if more than current Node
			return search_recursively(node.right, serial_number);		
		}	
	}
}

