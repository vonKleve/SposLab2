package lab2try1;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class ILamportLock implements ImprovedBakeryLock{
    private class Pair implements Comparable{
        private Integer key, priority;      // 0 is max priority
        
        Pair(Integer ia, Integer ib){
            key = ia;
            priority = ib;
        }
        
        Pair(Pair obj){
            key = obj.key;
            priority = obj.priority;
        }
        
        public void setKey(Integer ikey){
            key = ikey;
        }
        
        public void setPrior(Integer iprior){
            priority = iprior;
        }
        
        @Override
        public int compareTo(Object ix){
            Pair x = (Pair)ix;
            if(this.key < x.key){
                return -1;
            }
            else if(Objects.equals(this.key, x.key)){
                return 0;
            }
            return 1;
        }
    };
    
    private final Integer threads = 100;
    
    private final ArrayList<Pair> tickets = new ArrayList<>(threads);
    private final ArrayList<Boolean> entering = new ArrayList<>(threads);
    
    private final TreeSet<Pair>freeUsedTickets = new TreeSet<>();
    
    private final Integer ticketBound = 3;
  //  private final AtomicInteger maxTicket = new AtomicInteger(0);
    
    private final Semaphore fillCount = new Semaphore(ticketBound);
    
    private Boolean isInitialized = Boolean.FALSE;
    ILamportLock(){
        synchronized(isInitialized){
            if(!isInitialized){
                for(int i = 0; i < threads; i++){
                    tickets.add(new Pair(-1, -1));
                    entering.add(false);
                    freeUsedTickets.add(new Pair(i,0));
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
    public Condition newCondition() throws UnsupportedOperationException{
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void unlock(Integer pid){
        Pair freeTicket = new Pair(tickets.get(pid));
        freeTicket.setPrior(freeTicket.priority + 1);
        tickets.get(pid).setKey(-1);
        tickets.get(pid).setPrior(-1);
        

        synchronized(freeUsedTickets){
            freeUsedTickets.add(freeTicket);
        }
        fillCount.release();
        
        System.out.println("Thread #" + pid + " exited....");
    }
    
    private void actualLock(Integer pid){
        Pair ticket;
        
        entering.set(pid, true);
        synchronized(freeUsedTickets){
            ticket = freeUsedTickets.pollFirst();
        }
        entering.set(pid, false);
        tickets.set(pid, ticket);
        
        System.out.println(pid + " got " + ticket.key + " with priority " + ticket.priority);
        
        for(int i = 0; i < threads; i++){
           if(i == pid){ continue; }
           while(entering.get(i)){ Thread.yield(); }
           while(tickets.get(pid).priority > tickets.get(i).priority && tickets.get(i).priority != -1){ Thread.yield(); }
           if(tickets.get(i).priority.equals(tickets.get(pid).priority)){
               while(tickets.get(i).key != -1 && tickets.get(pid).key >= tickets.get(i).key){ Thread.yield(); }}
//                if(tickets.get(i).equals(tickets.get(pid))){        // to prove that there is no duplicates
//                    System.out.println("WARNING!!! DUPLICATE FOR " + pid + " IN " + i);
//               }
        }
        
       // System.out.println(freeUsedTickets);
    }
}