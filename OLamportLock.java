package lab2try1;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class OLamportLock implements ImprovedBakeryLock{
    private final Integer threads = 10;
    
    private final ArrayList<Integer> tickets = new ArrayList<>(threads);
    private final ArrayList<Boolean> entering = new ArrayList<>(threads);
    
    private final Integer ticketBound = 10;
    
    private final TreeSet<Integer> freeUsedTickets = new TreeSet<>();
    
    private final Semaphore fillCount = new Semaphore(ticketBound);
    
    private Boolean isInitialized = false;
    
    OLamportLock(){
        synchronized(isInitialized){
            if(!isInitialized){
                for(int i = 0; i < threads; i++){
                    tickets.add(-1);
                    entering.add(false);
                    freeUsedTickets.add(i + 1);
                }
                isInitialized = true;
            }
        }
    }
    
    @Override
    public void lock(Integer pid){
        try{
            fillCount.acquire();
        }
        catch(InterruptedException me){
            
        }
        actualLock(pid);
    }
    
    @Override
    public void lockInterruptibly(Integer pid) throws InterruptedException{
        fillCount.acquire();
        actualLock(pid);
    }
    
    @Override
    public boolean tryLock(Integer pid){
        Boolean result = fillCount.tryAcquire();
        if(!result){
            return false;
        }
        actualLock(pid);
        return true;
    }
    
    @Override
    public boolean tryLock(Integer pid, long time, TimeUnit unit) throws InterruptedException{
        Boolean result = fillCount.tryAcquire(time, unit);
        if(!result){
            return false;
        }
        actualLock(pid);
        return true;
    }
    
    @Override
    public void unlock(Integer pid){
        Integer freeTicket = tickets.get(pid);
        tickets.set(pid, -1);
        

        synchronized(freeUsedTickets){
            freeUsedTickets.add(freeTicket);
        }
        fillCount.release();
        
        System.out.println("Thread #" + pid + " exited....");
      //  System.out.println("UNLOCK: " + freeUsedTickets);
    }
    
    @Override
    public Condition newCondition() throws UnsupportedOperationException{
        throw new UnsupportedOperationException();
    }
    
    private void actualLock(Integer pid){
        Integer curTicket;
        entering.set(pid, true);
        System.out.println(pid + " entered");
        synchronized(freeUsedTickets){     
            curTicket = freeUsedTickets.pollFirst();
            tickets.set(pid, curTicket);       
        }
        entering.set(pid, false);
        
        System.out.println("Thread #" + pid + " got ticket: " + tickets.get(pid));
        
        for(int i = 0; i < threads; i++){
            if(i != pid){
                while(entering.get(i)) { Thread.yield();}
                while(tickets.get(i) < tickets.get(pid) && tickets.get(i) != -1){ Thread.yield(); }
//                if(tickets.get(i).equals(tickets.get(pid))){        // just to prove that there is no duplicates
//                    System.out.println("WARNING!!! DUPLICATE FOR " + pid + " IN " + i);
//                }
            }
        }
    }
}