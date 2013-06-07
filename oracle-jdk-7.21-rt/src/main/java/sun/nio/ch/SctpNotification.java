package sun.nio.ch;

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.Notification;

abstract interface SctpNotification extends Notification
{
  public abstract int assocId();

  public abstract void setAssociation(Association paramAssociation);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SctpNotification
 * JD-Core Version:    0.6.2
 */