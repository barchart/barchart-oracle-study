package sun.plugin.util;

import java.lang.reflect.Array;
import java.util.EventListener;
import sun.net.ProgressEvent;
import sun.net.ProgressListener;

public class EventMulticaster
  implements ProgressListener
{
  protected final EventListener a;
  protected final EventListener b;

  protected EventMulticaster(EventListener paramEventListener1, EventListener paramEventListener2)
  {
    this.a = paramEventListener1;
    this.b = paramEventListener2;
  }

  protected EventListener remove(EventListener paramEventListener)
  {
    if (paramEventListener == this.a)
      return this.b;
    if (paramEventListener == this.b)
      return this.a;
    EventListener localEventListener1 = removeInternal(this.a, paramEventListener);
    EventListener localEventListener2 = removeInternal(this.b, paramEventListener);
    if ((localEventListener1 == this.a) && (localEventListener2 == this.b))
      return this;
    return addInternal(localEventListener1, localEventListener2);
  }

  public void progressStart(ProgressEvent paramProgressEvent)
  {
    ((ProgressListener)this.a).progressStart(paramProgressEvent);
    ((ProgressListener)this.b).progressStart(paramProgressEvent);
  }

  public void progressUpdate(ProgressEvent paramProgressEvent)
  {
    ((ProgressListener)this.a).progressUpdate(paramProgressEvent);
    ((ProgressListener)this.b).progressUpdate(paramProgressEvent);
  }

  public void progressFinish(ProgressEvent paramProgressEvent)
  {
    ((ProgressListener)this.a).progressFinish(paramProgressEvent);
    ((ProgressListener)this.b).progressFinish(paramProgressEvent);
  }

  public static ProgressListener add(ProgressListener paramProgressListener1, ProgressListener paramProgressListener2)
  {
    return (ProgressListener)addInternal(paramProgressListener1, paramProgressListener2);
  }

  public static ProgressListener remove(ProgressListener paramProgressListener1, ProgressListener paramProgressListener2)
  {
    return (ProgressListener)removeInternal(paramProgressListener1, paramProgressListener2);
  }

  protected static EventListener addInternal(EventListener paramEventListener1, EventListener paramEventListener2)
  {
    if (paramEventListener1 == null)
      return paramEventListener2;
    if (paramEventListener2 == null)
      return paramEventListener1;
    return new EventMulticaster(paramEventListener1, paramEventListener2);
  }

  protected static EventListener removeInternal(EventListener paramEventListener1, EventListener paramEventListener2)
  {
    if ((paramEventListener1 == paramEventListener2) || (paramEventListener1 == null))
      return null;
    if ((paramEventListener1 instanceof EventMulticaster))
      return ((EventMulticaster)paramEventListener1).remove(paramEventListener2);
    return paramEventListener1;
  }

  private static int getListenerCount(EventListener paramEventListener)
  {
    if ((paramEventListener instanceof EventMulticaster))
    {
      EventMulticaster localEventMulticaster = (EventMulticaster)paramEventListener;
      return getListenerCount(localEventMulticaster.a) + getListenerCount(localEventMulticaster.b);
    }
    return paramEventListener == null ? 0 : 1;
  }

  private static int populateListenerArray(EventListener[] paramArrayOfEventListener, EventListener paramEventListener, int paramInt)
  {
    if ((paramEventListener instanceof EventMulticaster))
    {
      EventMulticaster localEventMulticaster = (EventMulticaster)paramEventListener;
      int i = populateListenerArray(paramArrayOfEventListener, localEventMulticaster.a, paramInt);
      return populateListenerArray(paramArrayOfEventListener, localEventMulticaster.b, i);
    }
    if (paramEventListener != null)
    {
      paramArrayOfEventListener[paramInt] = paramEventListener;
      return paramInt + 1;
    }
    return paramInt;
  }

  public static EventListener[] getListeners(EventListener paramEventListener, Class paramClass)
  {
    int i = getListenerCount(paramEventListener);
    EventListener[] arrayOfEventListener = (EventListener[])Array.newInstance(paramClass, i);
    populateListenerArray(arrayOfEventListener, paramEventListener, 0);
    return arrayOfEventListener;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.util.EventMulticaster
 * JD-Core Version:    0.6.2
 */