package lab2try1;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public interface ImprovedBakeryLock{
    public void lock(Integer pid);
    public void lockInterruptibly(Integer pid) throws InterruptedException;
    public boolean tryLock(Integer pid);
    public boolean tryLock(Integer pid, long time, TimeUnit unit ) throws InterruptedException;
    public void unlock(Integer pid);
    Condition newCondition() throws UnsupportedOperationException;
}
