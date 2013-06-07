package java.rmi.dgc;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ObjID;

public abstract interface DGC extends Remote
{
  public abstract Lease dirty(ObjID[] paramArrayOfObjID, long paramLong, Lease paramLease)
    throws RemoteException;

  public abstract void clean(ObjID[] paramArrayOfObjID, long paramLong, VMID paramVMID, boolean paramBoolean)
    throws RemoteException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.rmi.dgc.DGC
 * JD-Core Version:    0.6.2
 */