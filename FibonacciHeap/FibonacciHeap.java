import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
* Eyalgrinberg_207129792
* Matanshvide_201414315
*/

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
	private HeapNode min;
	private HeapNode exLeft; 
	private List<HeapNode> root_lst;
	private int marked;
	private int trees;
	private static int totalLinks;
	private static int totalCuts;
	private int size;
	
	public FibonacciHeap(HeapNode rootMin) { //constructor for new one tree heap
		this.min = rootMin;
		this.exLeft = rootMin;
		this.min.setNext(rootMin);
		this.min.setPrev(rootMin);
		this.marked = 0;
		this.trees = 1;
		this.size = (int) Math.pow(2,rootMin.getRank());
		totalLinks = 0;
		totalCuts = 0;
		this.root_lst = new ArrayList<HeapNode>();
		for (int i=0; i<3*rootMin.getRank(); i++) {
			this.root_lst.add(new HeapNode(Integer.MIN_VALUE));
		}
	}
	
	public FibonacciHeap() {
		this.min = null;
		this.root_lst = new ArrayList<HeapNode>();
	}

   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    *   
    */
    public boolean isEmpty() // O(1) - no root ---> heap is empty
    {
    	return this.min==null;  
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    */
    public HeapNode insert(int key) //O(1)
    {    
    	HeapNode toAdd = new HeapNode(key); 
    	if (this.isEmpty()) { //empty tree
    		this.min = toAdd;
    		this.exLeft = toAdd; //pointer to connect end of root list
    		this.min.setPrev(toAdd);
    		this.min.setNext(toAdd);
    		this.min.setChild(null);
    	}else {
    		toAdd.setNext(this.exLeft); // connect to previous node inserted
    		toAdd.setPrev(this.exLeft.getPrev()); // connect to other end
    		toAdd.getNext().setPrev(toAdd); // connect brothers to new node
    		toAdd.getPrev().setNext(toAdd);
    		this.exLeft = toAdd;
    		if (toAdd.getKey()<this.min.getKey()) { //update min if necessary
    			this.min = toAdd;
    		}
    	}
    	HeapNode dumby = new HeapNode(Integer.MIN_VALUE);
    	if (root_lst.size() < 70) {//condition added for testing******************************************
    		this.root_lst.add(dumby); //create a template list for future consolidation
    	}
    	this.size+=1;
    	this.trees += 1;
    	return toAdd;
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
    public void deleteMin() // O(logn) amortized. O(n) worst case
    {
    	if (this.isEmpty()) { //empty heap - return
    		return;
    	}
    	
    	if ((this.min.getPrev()==this.min)&&(this.min.getChild()==null)) { //one node heap ---> return empty heap
    		this.min = null;
    		this.exLeft = null;
    		return;
    	}
    	if ((this.min.getPrev()==this.min)) {
    		if (this.min.getChild()!=null) { //has children 
        		HeapNode traveler = this.min.getChild(); //unmark the children
        		int end = traveler.getKey();
        		if (traveler.mark) { //unmark and update field
    				traveler.mark = false;
    				traveler.setParent(null);//added last
    				this.marked -=1;
    			}
        		traveler.setParent(null);
        		traveler = traveler.getNext();
        		while (traveler.getKey()!= end) { //unmark all and update field
        			traveler.setParent(null);
        			if (traveler.mark) {
        				traveler.mark = false;
        				this.marked -=1;

        			}
        			traveler = traveler.getNext();
        		}// unmarking done
    		}
    		this.exLeft = this.min.getChild();
    		this.min.getChild().setParent(null);
    		this.min.setChild(null);
    		this.consolidate();
    		this.size-=1;
    		return;
    		
    	}
    	if (this.min.getChild()!=null) { //has children 
    		HeapNode traveler = this.min.getChild(); //unmark the children
    		int end = traveler.getKey();
    		if (traveler.mark) { //unmark and update field
				traveler.mark = false;
				traveler.setParent(null);//added last
				this.marked -=1;
			}
    		traveler = traveler.getNext();
    		while (traveler.getKey()!= end) { //unmark all and update field
    			if (traveler.mark) {
    				traveler.mark = false;
    				this.marked -=1;

    			}
    			traveler = traveler.getNext();
    		}// unmarking done
    		this.min.getChild().setPrev(this.min.getPrev());
    		HeapNode tmp = this.min.getChild();
    		if (this.min == this.exLeft) { // update extremes 
    			this.exLeft = this.min.getNext();
    		}
    		if (tmp==null) { //no child
    			this.min.getPrev().setNext(this.min.getNext());//connect brothers to each other
    			this.min.getNext().setPrev(this.min.getPrev());
    			this.min.setPrev(null); //disconnect from brothers ---> deleting
    			this.min.setNext(null);
    		}else {
    			tmp.setParent(null); //disconnect from deleted node
    			this.min.getPrev().setNext(tmp); //connect brother and first son
    			tmp.setPrev(this.min.getPrev());
    			while ((tmp!=null)&&(tmp.getNext()!=this.min.getChild())) { //go to last "son" *****added last tmp not null****
    				tmp.setParent(null);
    				tmp = tmp.getNext();
    			}
    			this.min.getNext().setPrev(tmp); //connect brother and last "son"
    			tmp.setNext(this.min.getNext());
    			tmp.setParent(null);
    			this.min.setNext(null);//disconnect from everyone ---> delete
    			this.min.setPrev(null);
    			this.min.setChild(null);
    		}
    	}else { //no children. just connect brothers
    		this.min.getNext().setPrev(this.min.getPrev());//connect brothers to each other
    		this.min.getPrev().setNext(this.min.getNext());
    		if (this.min.getKey()==this.exLeft.getKey()){
    			this.exLeft = this.min.getNext();
    		}
    		this.min.setNext(null);// disconnect from heap
    		this.min.setPrev(null);
    	}
    	for (int i=0; i<root_lst.size(); i++) {//update exLeft incase exLeft was deleted
    		if ((root_lst.get(i).getKey()!=Integer.MIN_VALUE)&&(root_lst.get(i).getKey()!=this.min.getKey())) { //connect exLeft with other roots and end
    			this.exLeft = root_lst.get(i);
    			break;
    		}
    	}
     	this.consolidate();
     	this.size -=1;
    }
    
     	
    
    
    private void consolidate() { //O(logn) amortized. O(n) worst case.
    	for (int i=0; i<root_lst.size(); i++) {//insert roots into auxiliary list
    		if (root_lst.get(i).getKey()!=Integer.MIN_VALUE) {
    			HeapNode dumby = new HeapNode(Integer.MIN_VALUE);
    			root_lst.set(i, dumby);
    		}
    	}
    	HeapNode tmp = this.exLeft;
    	int rank = this.min.getRank();
    	if ((tmp.getNext()==tmp) || (tmp.getPrev()==tmp)){ //one tree heap
     		this.min = tmp; //update min and finish
     		this.root_lst.set(rank, tmp);
     		this.trees = 1;
     		return;
     	}
    	HeapNode stopper = this.exLeft;
    	HeapNode stopNow = null;
    	while ((tmp != stopNow)||((tmp.getParent()==null)&&(rank!=0)&&(this.root_lst.get(rank)!=tmp)&&(this.root_lst.get(rank-1)!=tmp))) { //stop at last root
    		stopNow = stopper;
    		rank = tmp.getRank();
    		if (this.root_lst.get(rank).getKey()==Integer.MIN_VALUE) { //only tree from this size
    			this.root_lst.set(rank, tmp);
    			tmp = tmp.getNext();
    		}else{ // there is another tree of this size
    			tmp.join(this.root_lst.get(rank));
    			totalLinks+=1;
    			this.root_lst.set(rank, new HeapNode(Integer.MIN_VALUE));
    			if (tmp.getParent()!=null) { //update "root" for new tree
    				tmp = tmp.parent; //check if next spot in tree list is available in next iteration
    				stopNow = tmp.getChild();
    			}
    		}
    		if (tmp != null) {
    			rank = tmp.getRank();
    		}
    	}
    	for (int i=0; i<this.root_lst.size(); i++){
    		if ((this.root_lst.get(i).getKey()<this.min.getKey())&&((this.root_lst.get(i).getKey()!=Integer.MIN_VALUE))) {
    			this.min = this.root_lst.get(i); //update min pointer
    		}
    	}
    	int j=0;
    	while ((j<this.root_lst.size())&&(this.root_lst.get(j).getKey() == Integer.MIN_VALUE)) { //go over roots until first tree
    		j++;
    	}
    	this.exLeft = this.root_lst.get(j); //lowest ranked tree is exLeft
    	tmp = this.exLeft;
    	for(int k = j; k<this.root_lst.size(); k++) { //connecting roots
    		if (this.root_lst.get(k).getKey() != Integer.MIN_VALUE) {
    			tmp.setNext(this.root_lst.get(k));
    			this.root_lst.get(k).setPrev(tmp);
    			tmp = this.root_lst.get(k);
    		}
    	}
    	tmp.setNext(this.exLeft);//connect both ends of root list
    	this.exLeft.setPrev(tmp);
    	HeapNode stopLoop = this.exLeft;//finding the new minimum
    	this.min = stopLoop;
    	tmp = exLeft.getNext();
    	while (tmp != stopLoop) {
    		if (tmp.getKey()<this.min.getKey()) {
    			this.min = tmp;
    		}
    		tmp = tmp.getNext();
    	}
    	int cnt = 0;
    	for (int i=0; i<this.root_lst.size(); i++) {
    		if (this.root_lst.get(i).getKey()!=Integer.MIN_VALUE) {
    			cnt+=1;
    		}
    	}
    	this.trees = cnt;
    }
    
    

   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    */
    public HeapNode findMin() //O(1)
    {
    	if (this.isEmpty()) {
    		return null;
    	}
    	return this.min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
    public void meld (FibonacciHeap heap2) //O(1)
    {
    	 this.exLeft.getPrev().setNext(heap2.exLeft); // connect midleft to midright
    	 heap2.exLeft.getPrev().setNext(this.exLeft); // connect right end to left end
    	 HeapNode toSave = this.exLeft.getPrev(); //temporary
    	 this.exLeft.setPrev(heap2.exLeft.getPrev()); //connect left end to right end
    	 heap2.exLeft.setPrev(toSave); //connect midright to midleft
    	 this.size += heap2.size(); //update size, marks and trees
    	 this.marked += heap2.marked;
    	 this.trees += heap2.trees;
    	 if (this.min.getKey()>heap2.min.getKey()) {
    		 this.min = heap2.min; //update min
    	 }
    	 
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    *   
    */
    public int size() //O(1)
    {
    	if (this.isEmpty()) {
    		return 0;
    	}
    	return this.size; 
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
    * 
    */
    public int[] countersRep() //O(logn) amortized. O(n) worst case
    {
    	int[] arr = new int[this.size()];
    	int stopper = this.exLeft.getKey();
    	HeapNode tmp = this.exLeft;
    	int cntTrees = 0;
    	for (int i=0; i<arr.length; i++) { //create draft array
    		arr[tmp.getRank()]+=1;
    		cntTrees+=1;
    		tmp = tmp.getNext();
    		if (tmp.getKey()==stopper) {
    			break;
    		}
    	}
    	int highestIndex = 0;
    	for (int j=0; j<arr.length; j++) { // get final size
    		if (arr[j]!=0) {
    			highestIndex = j;
    		}
    	}
    	int[] result = new int[highestIndex+1];
    	for (int k=0; k<result.length; k++) { // create final array
    		result[k] = arr[k];
    	}
    	this.trees = cntTrees;//update number of trees
        return result; 
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    */
    public void delete(HeapNode x) //O(log(n)
    {   
    	int delta = Integer.MIN_VALUE;
    	this.decreaseKey(x, delta);
    	this.min = x;
    	if (x.getParent()!=null) {
    		this.cascadeCut(x);
    		totalCuts += 1;
    	}
    	this.deleteMin();
    	return; 
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta) // Worst case O(log(n). amortized O(1)
    {    
    	x.key -= delta;
    	if (x.getKey()<this.min.getKey()) { //update min
    		this.min = x;
    	}
    	if (x.getParent()==null) { //no parent no problem
    		return;
    	}
    	if (x.getKey()<x.getParent().getKey()) { //cut necessary
    		this.cascadeCut(x);
    		totalCuts+=1;
    	}
    	return; 
    }
    
    public void cascadeCut(HeapNode son) {// Worst case O(log(n). amortized O(1)
    	if (son.getKey()==son.getParent().getChild().getKey()) { //son is parent's actual child -----> switch the child
    		if (son.getNext().getKey() == son.getKey()) { //only child	
    			son.getParent().setChild(null);
    			son.getParent().setRank(0);
    		}else { // has brothers
    			son.getParent().setChild(son.getNext());
    			son.getNext().setPrev(son.getPrev());
    			son.getPrev().setNext(son.getNext());// connect brothers
    			son.getParent().setRank(son.getParent().getChild().getRank()+1);
    		}
    	}else {
    		son.getNext().setPrev(son.getPrev());
			son.getPrev().setNext(son.getNext());// connect brothers
    	}
    	HeapNode toSave = son.getParent(); // save parent for checking up the heap
    	son.setParent(null);
    	if (son.mark) {
    		son.mark = false;
    		this.marked -=1;
    	}
    	son.setNext(this.exLeft);
    	son.setPrev(this.exLeft.getPrev());
    	this.exLeft.getPrev().setNext(son);
    	this.exLeft.setPrev(son);
    	this.exLeft = son; //insert son as exLeft and adjust connections
    	if (toSave.mark) { //check father
    		this.cascadeCut(toSave);//cut father
    		totalCuts+=1;
    	}else if ((!toSave.mark)&&(toSave.getParent()==null)) { // root should stay unmarked
    		this.trees+=1;
    		return;
    	}else { // not marked and not a root ---->mark
    		toSave.mark(); // mark father
    		this.marked+=1;
    	}
    	this.trees +=1; // one tree added to heap
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap. 
    */
    public int potential() //O(1)
    {    
    	int result = 2*this.marked + this.trees;
    	return result; // should be replaced by student code
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    */
    public static int totalLinks()
    {   
    	return totalLinks;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return totalCuts; 
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H. 
    */
    public static int[] kMin(FibonacciHeap H, int k) //O(k*log(n)) - degH=log(n)
    {    
    	int[] arr = new int[k];
    	if (k==1) { //one node tree. protect from zero in index stopping condition
    		arr[0] = H.findMin().getKey();
    		return arr;
    	}
    	HashMap<Integer, HeapNode> originals = new HashMap<>(); //pointer dictionary to access new candidates in O(1)
    	arr[0] = H.findMin().getKey();
    	HeapNode tmp = H.findMin().getChild(); //pointer in original heap
    	FibonacciHeap auxHeap = new FibonacciHeap();
    	for (int j=1; j<k; j++) { //still need more keys
            int stopper = tmp.getKey();
            auxHeap.insert(stopper); //insert to auxHeap
            originals.put(tmp.getKey(), tmp); //add to pointer dictionary
            while (tmp.getNext().getKey()!=stopper) { // add sons to auxHeap and pointer dictionary
            	tmp = tmp.getNext();
            	auxHeap.insert(tmp.getKey());
            	originals.put(tmp.getKey(), tmp);
            }
            arr[j] = auxHeap.findMin().getKey();
            stopper = auxHeap.findMin().getKey();
            tmp = originals.get(stopper);
            auxHeap.deleteMin();
            while (tmp.getChild()==null) { //added node has children in original heap ----> next round will add his children to the aux heap
            	j+=1;
            	if (j>=k) {
            		break;
            	}
            	arr[j] = auxHeap.findMin().getKey();
            	tmp = originals.get(auxHeap.findMin().getKey());
            	auxHeap.deleteMin();
            }
            tmp = tmp.getChild();	
            } 	
        return arr; // should be replaced by student code
    }
    

    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{

    	private int key;
    	private String info;
    	private boolean mark;
    	private int rank;
    	private HeapNode child;
    	private HeapNode next;
    	private HeapNode prev;
    	private HeapNode parent;
    	
    	public HeapNode(int key) {
    		this.key = key;
    	}

    	public int getKey() {
    		return this.key;
    	}
    	
    	public void setChild(HeapNode child) {
    		this.child = child;
    	}
    	
    	public void setNext(HeapNode next) {
    		this.next = next;
    	}
    	
    	public void setPrev(HeapNode prev) {
    		this.prev = prev;
    	}
    	
    	public void setParent(HeapNode parent) {
    		this.parent = parent;
    	}
    	
    	public HeapNode getChild() {
    		return this.child;
    	}
    	
    	public HeapNode getNext() {
    		return this.next;
    	}
    	
    	public HeapNode getPrev() {
    		return this.prev;
    	}
    	
    	public HeapNode getParent() {
    		return this.parent;
    	}
    	
    	public void mark() {
    		this.mark = true;
    	}
    	
    	public void setRank(int k) {
    		this.rank = k;
    	}
    	
    	public int getRank() {
    		return this.rank;
    	}
    	
    	public void join(HeapNode toJoin) { //O(1) connect roots of trees and "meld" the min's sons with the max root
    		if (toJoin.getKey()>this.getKey()) { //case #1 - this remains root
    			HeapNode saveNextRoot = this.getNext();
    			this.setRank(this.getRank()+1); //rank one up
    			if (this.getChild()!=null) {
    				toJoin.setPrev(this.getChild().getPrev()); //connect new son to last "son"
    				this.getChild().getPrev().setNext(toJoin); //connect last "son" to new son
    				this.getChild().setPrev(toJoin); //connect first son to new son
    				toJoin.setNext(this.getChild()); //connect new son to old son
    				this.setChild(toJoin); //update son
    				toJoin.setParent(this);
    			}else { //both don't have children
    				this.setPrev(toJoin.getPrev()); //"steal" brothers
    				this.setNext(toJoin.getNext());
    				toJoin.setNext(toJoin); //disconnect brothers
    				toJoin.setPrev(toJoin);
    				toJoin.setParent(this);//connect new father and son
    				this.setChild(toJoin);
    			}
    			this.setNext(saveNextRoot);
    			
    		}else { //case #2 - next node becomes root ----->connect symmetrically
    			HeapNode saveNextRoot = this.getNext();
    			toJoin.setRank(toJoin.getRank()+1); //rank one up**********added last
    			if (toJoin.getChild()!=null) {
    				this.setPrev(toJoin.getChild().getPrev()); 
    				toJoin.getChild().getPrev().setNext(this);
    				toJoin.getChild().setPrev(this);
    				this.setNext(toJoin.getChild());
    				this.setParent(toJoin);//*******added last
    				toJoin.setChild(this);
    			}else { //both don't have children
    				toJoin.setPrev(this.getPrev());//"steal" brothers
    				toJoin.setNext(this.getNext());
    				this.setNext(this);//disconnect brothers
    				this.setPrev(this);
    				this.setParent(toJoin); //connect new father and son
    				toJoin.setChild(this);
    			}
    			toJoin.setNext(saveNextRoot);
    		}
    	}
    	
    	
    }
}
