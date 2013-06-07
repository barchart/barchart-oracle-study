package java.rmi.activation;

import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

public abstract interface ActivationInstantiator extends Remote
{
  public abstract MarshalledObject<? extends Remote> newInstance(ActivationID paramActivationID, ActivationDesc paramActivationDesc)
    throws ActivationException, RemoteException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.rmi.activation.ActivationInstantiator
 * JD-Core Version:    0.6.2
 */