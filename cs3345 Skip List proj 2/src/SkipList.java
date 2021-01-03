// Starter code for Project 2: skip lists
// Do not rename the class, or change names/signatures of methods that are declared to be public.


// change to your netid
//package dhd170001;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Random;

public class SkipList<T extends Comparable<? super T>> {
    static final int PossibleLevels = 33;

    static class Entry<E> {
	E element;
	Entry[] next;
	Entry prev;
	int level;
	int[] width;

	public Entry(E x, int lev) {
	    element = x;
	    next = new Entry[lev];
	    // add more code as needed
	    width = new int[lev];
	    level = lev;
	}

	public E getElement() {
	    return element;
	}
    }
    
    public class SkipListStack {
    	
        private Entry entry;
        private int totalWidth;

        public SkipListStack(Entry entry, int totalWidth) {
            this.entry = entry;
            this.totalWidth = totalWidth;
        }


    }
    
    SkipListStack[] pred;
    int maxlvl;
    int size;
    Entry head;
    Entry tail;
    Entry last;
    Random random;

    // Constructor
    public SkipList() {
    	
    	head = new Entry<>(null, PossibleLevels + 2);
        tail = new Entry<>(null, PossibleLevels + 2);
        maxlvl = 1;
        size = 0;
        setPointers();
        random = new Random();
    	
    }
    
    public void setPointers() 
    {
    	
        for (int i = 0; i <= PossibleLevels; i++) {
            this.head.next[i] = tail;
            this.head.width[i] = 1;
            this.tail.next[i] = null;
        }
        
    }
    
    public int chooseLevel() {
        int level = 1 + Integer.numberOfTrailingZeros(random.nextInt());
        return Math.min(level, maxlvl + 1); 
    }

    // Add x to list. If x already exists, reject it. Returns true if new node is added to list
    public boolean add(T x) {
    	
    	if (x == null) {return false;}
        
        if (contains(x)) {return false;}
        
        int level = Math.min(chooseLevel(), PossibleLevels);
        
        maxlvl = Math.max(maxlvl, level);
      
        
		Entry newEntry = new Entry<>(x, level + 1);
		
		  int distance = 0;
		
		  for (int i = 0; i <= PossibleLevels; i++) 
		  {
		      Entry prev = i < pred.length ? pred[i].entry : head;
		      if (i <= level) 
		      {
		    	  newEntry.next[i] = prev.next[i];
		          prev.next[i] = newEntry;
		
		          newEntry.width[i] = Math.max(prev.width[i] - distance, 1);
		          prev.width[i] = distance + 1;
		
		          distance += pred[i].totalWidth;
		      } 
		      else 
		      {
		    	  prev.width[i]++;
		      }
		  }
		
		  if (newEntry.next[0].getElement() != null) 
		  {
			  newEntry.next[0].prev = newEntry;
		  }
		
		  if (newEntry.next[0].getElement() == null) 
		  {
		      last = newEntry;
		  }
		  size++;
    	
	return true;
    }

    // Find smallest element that is greater or equal to x
    public T ceiling(T x) 
    {
    	
    	if (contains(x)) {return x;}
    	
        Entry next = pred[0].entry;
        if (next == null || next.next[0] == null) {return null;}
        
        return (T) next.next[0].getElement();
	
    }

    // Does list contain x?
    public boolean contains(T x) {
    	
    	pred = (SkipListStack[]) Array.newInstance(SkipListStack.class, Math.min(maxlvl + 2, PossibleLevels+1));
    	
        Entry p = head;
        
        for (int i = Math.min(PossibleLevels, maxlvl + 1); i >= 0; i--) 
        {
            int counter = 0;
            while (p.next[i].getElement() != null && x.compareTo((T) p.next[i].getElement()) > 0) 
            {
            	counter += p.width[i];
                p = p.next[i];
            }
            pred[i] = new SkipListStack(p, counter);
        }
		 Entry wantedElement = pred[0].entry;
		 
		 return wantedElement.next[0].getElement() != null && x.compareTo((T) wantedElement.next[0].getElement()) == 0;
		 
    }

    // Return first element of list
    public T first() {
    	
        return (T) head.next[0].getElement();

    }

    // Find largest element that is less than or equal to x
    public T floor(T x) {
    	
    	 if (contains(x)) {return x;}
    	 
         Entry node = pred[0].entry;
         
         return (T) node.getElement();
    	
    }

    // Return element at index n of list.  First element is at index 0.
    public T get(int n) {
    	
    	if (n < 0 || n >= size()) {return null;}
    	
    	int distance = 0;
        Entry entry = head;
        for (int i = maxlvl; i >= 0; i--) 
        {
            while (entry.next[i] != null && distance + entry.width[i] <= n) 
            {
            	distance += entry.width[i];
            	entry = entry.next[i];
            }
        }
        return (T) entry.next[0].getElement();
        
    }

    // Is the list empty?
    public boolean isEmpty() {return size == 0;}

    // Iterate through the elements of list in sorted order
    public Iterator<T> iterator() {return new SkipListIterator<>(0);}

    // Return last element of list
    public T last() {return (T) this.last.getElement();}

    // Remove x from list.  Removed element is returned. Return null if x not in list
    public T remove(T x) {
    	
    	if (!contains(x)) {return null;}

        Entry entryT = pred[0].entry.next[0];
        if (entryT.next[0].getElement() == null) 
        {
            last = pred[0].entry;
        }
        
        for (int i = 0; i <= PossibleLevels; i++) 
        {
            Entry prev = i < pred.length ? pred[i].entry : head;
            if (prev.next[i].getElement() != null && prev.next[i].getElement().equals(x)) 
            {
            	prev.width[i] += entryT.width[i] - 1;
            	prev.next[i] = entryT.next[i];
            } else {
            	prev.width[i]--;
            }

        }
        size = --size;
        
        return (T) entryT.getElement();
    	
    }

    // Return the number of elements in the list
    public int size() {
	return size;
    }
    
    public class SkipListIterator<T> implements Iterator<T> {
    	 
        Entry iterator;
        int level;

        SkipListIterator(int level) 
        {
            this.level = level;
            iterator = head.next[level];
        }

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			
			if (iterator != null && iterator.getElement() != null) 
			{
                return true;
            }
			return false;
		}

		@Override
		public T next() {
			// TODO Auto-generated method stub
			
			Entry<T> entryT = null;
            if (hasNext()) 
            {
            	entryT = iterator;
                iterator = iterator.next[level];
            }
            return entryT == null ? null : entryT.getElement();
		}

    }
}
