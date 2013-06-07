package java.rmi.activation;

import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

public abstract interface Activator extends Remote
{
  public abstract MarshalledObject<? extends Remote> activate(ActivationID paramActivationID, boolean paramBoolean)
    throws ActivationException, UnknownObjectException, RemoteException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.rmi.activation.Activator
 * JD-Core Version:    0.6.2
 */