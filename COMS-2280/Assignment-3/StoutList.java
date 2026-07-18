
package edu.iastate.cs2280.hw3;

/**
 * @author Pulkit Saxena
 */

import java.util.AbstractSequentialList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Implementation of the list interface based on linked nodes
 * that store multiple items per node.  Rules for adding and removing
 * elements ensure that each node (except possibly the last one)
 * is at least half full.
 */
public class StoutList<E extends Comparable<? super E>> extends AbstractSequentialList<E>
{
  /**
   * Default number of elements that may be stored in each node.
   */
  private static final int DEFAULT_NODESIZE = 4;
  
  /**
   * Number of elements that can be stored in each node.
   */
  private final int nodeSize;
  
  /**
   * Dummy node for head.  It should be private but set to public here only  
   * for grading purpose.  In practice, you should always make the head of a 
   * linked list a private instance variable.  
   */
  public Node head;
  
  /**
   * Dummy node for tail.
   */
  private Node tail;
  
  /**
   * Number of elements in the list.
   */
  private int size;
  
  /**
   * Constructs an empty list with the default node size.
   */
  public StoutList()
  {
    this(DEFAULT_NODESIZE);
  }

  /**
   * Constructs an empty list with the given node size.
   * @param nodeSize number of elements that may be stored in each node, must be 
   *   an even number
   */
  public StoutList(int nodeSize)
  {
    if (nodeSize <= 0 || nodeSize % 2 != 0) throw new IllegalArgumentException();
    
    // dummy nodes
    head = new Node();
    tail = new Node();
    head.next = tail;
    tail.previous = head;
    this.nodeSize = nodeSize;
  }
  
  /**
   * Constructor for grading only.  Fully implemented. 
   * @param head
   * @param tail
   * @param nodeSize
   * @param size
   */
  public StoutList(Node head, Node tail, int nodeSize, int size)
  {
	  this.head = head; 
	  this.tail = tail; 
	  this.nodeSize = nodeSize; 
	  this.size = size; 
  }
 /*
  * @return: It returns the total number of elements.
  */
  @Override
  public int size()
  {
    return size;
  }
  
  /*
   * @param item: It takes an input from the user and checks whether it can be added or not. 
   * @return: if the result is true then the item gets added to the list otherwise it doesn't get added.
   */
  @Override
  public boolean add(E item)
  {
	 //It checks if the item is null or not 
	  if (item == null) {
			throw new NullPointerException("Cannot return a null element");
		}
	//It checks whether a node contains the item or not. If it does, then it does not add the item to the list.  
		if(contains(item))
			return false;
    
	//If the size is 0, then a new node is created, else just the item is added to the node. 	
		if (size == 0) {
			Node newNode = new Node();
			newNode.addItem(item);
			head.next = newNode;
			newNode.previous = head;
			newNode.next = tail;
			tail.previous = newNode;
		} 
		else {
			
			if (tail.previous.count < nodeSize) {
				tail.previous.addItem(item);
			}
			
			else {
				Node oldNode = new Node();
				oldNode.addItem(item);
				Node temp = tail.previous;
				temp.next = oldNode;
				oldNode.previous = temp;
				oldNode.next = tail;
				tail.previous = oldNode;
			}
		}
	  
		size++;
		return true;
	}

  
  /*
   * @param pos: It specifies the position at which the item is supposed to be added.
   * @param item: It takes item from the user
   */
  @Override
  public void add(int pos, E item)
  {
    if(pos > size || pos < 0)
    {
    	throw new IndexOutOfBoundsException();
    }
    
    // An item is added to the node if the next element after head is tail
    if(head.next == tail)
    {
    	add(item);
    }
    
    NodeInfo info = find(pos);
    Node tNode = info.node;
    int offset = info.offset;
    
    
    if(offset == 0)
    {
    	if(tNode.previous.count < nodeSize && tNode.previous != head)
    	{
    		tNode.previous.addItem(item);
    		
        }
    	else if(tNode == tail)
    	{
    		add(item);
    	}
    	size++;
    }
    
   
    if(tNode.count < nodeSize)
    {
    	tNode.addItem(offset, item);
    }
    
    else
    {
    	Node newNext = new Node();
    	int half = nodeSize / 2;
    	int nodecount = 0;
    	
    	while(nodecount < half)
    	{
    		newNext.addItem(tNode.data[half]);
    		tNode.removeItem(half);
    		nodecount+=1;
    	}
    	
    	Node oldNext = tNode.next;
    	tNode.next = newNext;
    	newNext.previous = tNode;
    	newNext.next = oldNext;
    	oldNext.previous = newNext;
    	
    	if(offset <= half) 
    	{
    		tNode.addItem(offset, item);
    	}
    	else if(offset > half)
    	{
    		newNext.addItem(offset - half, item);
    	}
    }
    
    size++;
  }
  /*
   * @param item: It takes the element and let us know whether that item is already there is not. 
   * @return: it returns true if we find the same element otherwise it returns false.
   * 
   * if the size is greater than 1 then it moves to the next element and subsequently iterates through all the 
   * elements in the list and checks if any element is the same as the given element or not. 
   */
  private boolean contains(E item)
  {
	  if (size>=1)
	  {
		  Node n = head.next;
		  while(n!=tail)
		  {
			  for(int i=0;i<n.count;i++)
			  {
					if(n.data[i].equals(item))
						return true;
					n = n.next;
			  }
		  }
	  }
	  else
	  {
		  return false;
	  }
				 
	  return false;
  }
  
 /*
  * @param pos: It gives us the position in the node which needs to be removed. 
  * @return: It reduces the size and returns the node after removing the element. 
  */
  @Override
  public E remove(int pos)
  {
    if(pos < 0 || pos > size)
    {
    	throw new IndexOutOfBoundsException();
    }
    
    NodeInfo nodeInfo = find(pos);
    Node info = nodeInfo.node;
    int offset = nodeInfo.offset;
    E nodeVal = info.data[offset];
    int half = nodeSize / 2;
    
    if(info.next == tail && info.count == 1)
    {
    	Node previous = info.previous;
    	previous.next = info.next;
    	info.next.previous = previous;
    	info = null;
    }
    else if(info.next == tail || info.count > half)
    {
    	info.removeItem(offset);
    }
    else
    {
    	info.removeItem(offset);
    	Node n = info.next;
    	
    	if(n.count > half)
    	{
    		info.addItem(n.data[0]);
    		n.removeItem(0);
    	}
    	else if(n.count <= half)
    	{
    		for(int i = 0; i < n.count; i++)
    		{
    			info.addItem(n.data[i]);
    		}
    		
    		info.next = n.next;
    		n.next.previous = info;
    		n = null;
    	}
    }
    
    size--;
    return nodeVal;
  }

  /**
   * Sort all elements in the stout list in the NON-DECREASING order. You may do the following. 
   * Traverse the list and copy its elements into an array, deleting every visited node along 
   * the way.  Then, sort the array by calling the insertionSort() method.  (Note that sorting 
   * efficiency is not a concern for this project.)  Finally, copy all elements from the array 
   * back to the stout list, creating new nodes for storage. After sorting, all nodes but 
   * (possibly) the last one must be full of elements.  
   *  
   * Comparator<E> must have been implemented for calling insertionSort().    
   */
  
  public void sort()
  {
	  E[] List = (E[]) new Comparable[size];
	  
	  int index = 0;
	  Node temp = head.next;
	  
	  while(temp != tail)
	  {
		  for(int i = 0; i < temp.count; i++)
		  {
			  List[index] = temp.data[i];
			  index++;
		  }
		  
		  temp = temp.next;
	  }
	  
	  head.next = tail;
	  tail.previous = head;
	  insertionSort(List, new ElementComparator());
	  size = 0;
	  
	  for(int i = 0; i < List.length; i++)
	  {
		  add(List[i]);
	  }
  }
  
  /**
   * Sort all elements in the stout list in the NON-INCREASING order. Call the bubbleSort()
   * method.  After sorting, all but (possibly) the last nodes must be filled with elements.  
   *  
   * Comparable<? super E> must be implemented for calling bubbleSort(). 
   */
  public void sortReverse() 
  {
	  E[] RevList = (E[]) new Comparable[size];
	  int Index = 0;
	  Node temp = head.next;
	  
	  while(temp != tail)
	  {
		  for(int i = 0; i < temp.count; i++)
		  {
			  RevList[Index] = temp.data[i];
			  Index++;
		  }
		  
		  temp = temp.next;
	  }
	  
	  head.next = tail;
	  tail.previous = head;
	  bubbleSort(RevList);
	  size = 0;
	  
	  for(int i = 0; i < RevList.length; i++) 
	  {
		  add(RevList[i]);
	  }
  }
  /*
   * @return: it returns the iterator.
   */
  @Override
  public Iterator<E> iterator()
  {
    return new StoutListIterator();
  }
 /*
  * @return: it returns the iterator. 
  */
  @Override
  public ListIterator<E> listIterator()
  {
    return new StoutListIterator();
  }
 /*
  * @param index: It takes the index value for the iterator.
  * @return: it returns the iterator.
  */
  @Override
  public ListIterator<E> listIterator(int index)
  {
    return new StoutListIterator(index);
  }
  
  /**
   * Returns a string representation of this list showing
   * the internal structure of the nodes.
   */
  public String toStringInternal()
  {
    return toStringInternal(null);
  }

  /**
   * Returns a string representation of this list showing the internal
   * structure of the nodes and the position of the iterator.
   *
   * @param iter
   *            an iterator for this list
   */
  public String toStringInternal(ListIterator<E> iter) 
  {
      int count = 0;
      int position = -1;
      if (iter != null) {
          position = iter.nextIndex();
      }

      StringBuilder sb = new StringBuilder();
      sb.append('[');
      Node current = head.next;
      while (current != tail) {
          sb.append('(');
          E data = current.data[0];
          if (data == null) {
              sb.append("-");
          } else {
              if (position == count) {
                  sb.append("| ");
                  position = -1;
              }
              sb.append(data.toString());
              ++count;
          }

          for (int i = 1; i < nodeSize; ++i) {
             sb.append(", ");
              data = current.data[i];
              if (data == null) {
                  sb.append("-");
              } else {
                  if (position == count) {
                      sb.append("| ");
                      position = -1;
                  }
                  sb.append(data.toString());
                  ++count;

                  // iterator at end
                  if (position == size && count == size) {
                      sb.append(" |");
                      position = -1;
                  }
             }
          }
          sb.append(')');
          current = current.next;
          if (current != tail)
              sb.append(", ");
      }
      sb.append("]");
      return sb.toString();
  }


  /**
   * Node type for this list.  Each node holds a maximum
   * of nodeSize elements in an array.  Empty slots
   * are null.
   */
  private class Node
  {
    /**
     * Array of actual data elements.
     */
    // Unchecked warning unavoidable.
    public E[] data = (E[]) new Comparable[nodeSize];
    
    /**
     * Link to next node.
     */
    public Node next;
    
    /**
     * Link to previous node;
     */
    public Node previous;
    
    /**
     * Index of the next available offset in this node, also 
     * equal to the number of elements in this node.
     */
    public int count;

    /**
     * Adds an item to this node at the first available offset.
     * Precondition: count < nodeSize
     * @param item element to be added
     */
    void addItem(E item)
    {
      if (count >= nodeSize)
      {
        return;
      }
      data[count++] = item;
    }
  
    /**
     * Adds an item to this node at the indicated offset, shifting
     * elements to the right as necessary.
     * 
     * Precondition: count < nodeSize
     * @param offset array index at which to put the new element
     * @param item element to be added
     */
    void addItem(int offset, E item)
    {
      if (count >= nodeSize)
      {
    	  return;
      }
      for (int i = count - 1; i >= offset; --i)
      {
        data[i + 1] = data[i];
      }
      ++count;
      data[offset] = item;
    }

    /**
     * Deletes an element from this node at the indicated offset, 
     * shifting elements left as necessary.
     * Precondition: 0 <= offset < count
     * @param offset
     */
    void removeItem(int offset)
    {
      E item = data[offset];
      for (int i = offset + 1; i < nodeSize; ++i)
      {
        data[i - 1] = data[i];
      }
      data[count - 1] = null;
      --count;
    }    
  }
  
  /**
   * 
   *  Used for gaining information of a node
   *
   */
  private class NodeInfo 
  {
	public Node node;
	public int offset;

	public NodeInfo(Node node, int offset) 
	{
		this.node = node;
		this.offset = offset;
	}
  }
 
  /**
   * 
   * @param pos: It tells us about the position of the particular object 
   * @return It returns the information of that particular node.
   */
  private NodeInfo find(int pos) {
	    if (pos < 0 || pos >= size) {
	        throw new IndexOutOfBoundsException("Position out of bounds: " + pos);
	    }
	    
	    Node temp = head.next; 
	    int curPos = 0; 

	    // Traverse nodes until we locate the correct node and offset
	    while (temp != tail) {
	    	if (curPos + temp.count <= pos) 
			{
				curPos += temp.count;
				temp = temp.next;
				continue;
			}

			NodeInfo nodeInfo = new NodeInfo(temp, pos - curPos);
			return nodeInfo;
	    }

	    return null;
	}

  
  private class StoutListIterator implements ListIterator<E>
  {
	/*
	 * It represents behind
	 */
	private static final int PREV_ACT  = -1;
	/*
	 * It represents ahead
	 */
	private static final int NEXT_ACT = 1;
	private static final int NONE = 0;
	/*
	 * It tells us about the index of the required element.
	 */
	private int CurrIndex;
	/*
	 * It creates a list 
	 */
	private E[] data;
	/*
	 * It tells us whether the direction is forward or backward  
	 */
	private int dir;
	/**
     * Default constructor 
     */
    public StoutListIterator()
    {
    	CurrIndex=0;
    	dir=NONE;
    	ReturnData();
    }

    /**
     * Constructor finds node at a given position.
     * @param pos: It tells about the position of the iterator.
     */
    public StoutListIterator(int pos)
    {
    	if(pos < 0 || pos > size)
    	{
    		throw new IndexOutOfBoundsException();
    	}
   
    	CurrIndex = pos;
    	dir = NONE;
    	ReturnData();
    }
    /*
     * It is a helper method used for this class
     */
    private void ReturnData()
    {
    	data = (E[]) new Comparable[size];
    	int index = 0;
    	Node temp = head.next;
    	
    	while(temp != tail)
    	{
    		for(int i = 0; i < temp.count; i++)
    		{
    			data[index] = temp.data[i];
    			index++;
    		}
    		
    		temp = temp.next;
    	}
    }
    /*
     * @return It checks whether there is a next element or not. 
     */
    @Override
    public boolean hasNext()
    {
    	if(CurrIndex >=0 && CurrIndex < size)
    	{
    		return true;
    	}
    	return false;
    }
    /*
     * @return It checks whether there is a previous element or not.
     */
    @Override
    public boolean hasPrevious()
    {
    	if(CurrIndex > 0 && CurrIndex <= size)
    	{
    		return true;
    	}
    	return false;
    }
    /*
     * @return It moves the iterator to the next element.
     */
    @Override
    public E next()
    {
    	if(!hasNext())
    	{
    		throw new NoSuchElementException();
    	}
    	
    	dir = PREV_ACT;
    	return data[CurrIndex++];
    }
    /*
     * @return: it moves the element to the previous element
     */
    @Override
    public E previous()
    {
    	if(!hasPrevious())
    	{
    		throw new NoSuchElementException();
    	}
    	
    	CurrIndex--;
    	dir = NEXT_ACT;
    	return data[CurrIndex];
    }
    /*
     * @return: It moves the next index
     */
    @Override
    public int nextIndex()
    {
    	return CurrIndex;
    }
    /*
     * @return: It returns the previous index
     */
    @Override
    public int previousIndex()
    {
    	return CurrIndex - 1;
    }
    /*
     * @return It adds an element.
     */
    @Override
    public void add(E item)
    {
    	if(item == null)
    	{
    		throw new NullPointerException();
    	}
    	
    	StoutList.this.add(CurrIndex, item);
    	CurrIndex++;
    	ReturnData();
    	dir = NONE;
    }
    /*
     * @return: It removes an element from the node 
     */
    @Override
    public void remove()
    {
    	if(dir == NEXT_ACT)
    	{
    		StoutList.this.remove(CurrIndex);
    		ReturnData();
    		CurrIndex++;
    		if(CurrIndex > 0)
    		{
    			CurrIndex--;
    		}
    		else
    		{
    			CurrIndex = 0;
    		}
    	}
    	else if(dir == PREV_ACT)
    	{
    		StoutList.this.remove(CurrIndex - 1);
    		ReturnData();
    		CurrIndex--;
    	}
    	else
    	{
    		throw new IllegalStateException();
        }
    	
    	dir = NONE;
    }
    /*
     * It sets an element in the node.
     */
    @Override
    public void set(E item)
    {

    	if(dir == NONE)
    	{
    		throw new IllegalStateException();
    	}
    	else if(dir == NEXT_ACT)
    	{
    		NodeInfo nodeInfo = find(CurrIndex-1);
    		nodeInfo.node.data[nodeInfo.offset] = item;
    		data[CurrIndex - 1] = item;
    	}
    	else
    	{
    		NodeInfo nodeInfo = find(CurrIndex);
    		nodeInfo.node.data[nodeInfo.offset] = item;
    		data[CurrIndex] = item;
    	}

    }
    
  
  }
  

  /**
   * Sort an array arr[] using the insertion sort algorithm in the NON-DECREASING order. 
   * @param arr   array storing elements from the list 
   * @param comp  comparator used in sorting 
   */
  private void insertionSort(E[] arr, Comparator<? super E> comp)
  {
	  for(int i = 1; i < arr.length; i++)
	  {
		  E key = arr[i];
		  int j = i - 1;
		  
		  while(j >= 0 && comp.compare(arr[j], key) > 0)
		  {
			  arr[j + 1] = arr[j];
			  j--;
		  }
		  
		  arr[j + 1] = key;
	  }
  }
  
  /**
   * Sort arr[] using the bubble sort algorithm in the NON-INCREASING order. For a 
   * description of bubble sort please refer to Section 6.1 in the project description. 
   * You must use the compareTo() method from an implementation of the Comparable 
   * interface by the class E or ? super E. 
   * @param arr  array holding elements from the list
   */
  private void bubbleSort(E[] arr)
  {
	  int len = arr.length;
	  
	  for(int i = 0; i < len - 1; i++)
	  {
		  for(int j = 0; j < (len - i - 1); j++)
		  {
			  if(arr[j].compareTo(arr[j + 1]) < 0)
			  {
				  E temp = arr[j];
				  arr[j] = arr[j + 1];
				  arr[j + 1] = temp;
			  }
		  }
	  }
  }
  
  private class ElementComparator<E extends Comparable<E>> implements Comparator<E>
  {
	  @Override
	  public int compare(E lhs, E rhs)
	  {
		  return lhs.compareTo(rhs);
	  }
  }
}
