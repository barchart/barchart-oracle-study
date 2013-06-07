/*     */ package javax.swing.event;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.EventListener;
/*     */ 
/*     */ public class EventListenerList
/*     */   implements Serializable
/*     */ {
/* 101 */   private static final Object[] NULL_ARRAY = new Object[0];
/*     */ 
/* 103 */   protected transient Object[] listenerList = NULL_ARRAY;
/*     */ 
/*     */   public Object[] getListenerList()
/*     */   {
/* 123 */     return this.listenerList;
/*     */   }
/*     */ 
/*     */   public <T extends EventListener> T[] getListeners(Class<T> paramClass)
/*     */   {
/* 135 */     Object[] arrayOfObject = this.listenerList;
/* 136 */     int i = getListenerCount(arrayOfObject, paramClass);
/* 137 */     EventListener[] arrayOfEventListener = (EventListener[])Array.newInstance(paramClass, i);
/* 138 */     int j = 0;
/* 139 */     for (int k = arrayOfObject.length - 2; k >= 0; k -= 2) {
/* 140 */       if (arrayOfObject[k] == paramClass) {
/* 141 */         arrayOfEventListener[(j++)] = ((EventListener)arrayOfObject[(k + 1)]);
/*     */       }
/*     */     }
/* 144 */     return arrayOfEventListener;
/*     */   }
/*     */ 
/*     */   public int getListenerCount()
/*     */   {
/* 151 */     return this.listenerList.length / 2;
/*     */   }
/*     */ 
/*     */   public int getListenerCount(Class<?> paramClass)
/*     */   {
/* 159 */     Object[] arrayOfObject = this.listenerList;
/* 160 */     return getListenerCount(arrayOfObject, paramClass);
/*     */   }
/*     */ 
/*     */   private int getListenerCount(Object[] paramArrayOfObject, Class paramClass) {
/* 164 */     int i = 0;
/* 165 */     for (int j = 0; j < paramArrayOfObject.length; j += 2) {
/* 166 */       if (paramClass == (Class)paramArrayOfObject[j])
/* 167 */         i++;
/*     */     }
/* 169 */     return i;
/*     */   }
/*     */ 
/*     */   public synchronized <T extends EventListener> void add(Class<T> paramClass, T paramT)
/*     */   {
/* 178 */     if (paramT == null)
/*     */     {
/* 182 */       return;
/*     */     }
/* 184 */     if (!paramClass.isInstance(paramT)) {
/* 185 */       throw new IllegalArgumentException("Listener " + paramT + " is not of type " + paramClass);
/*     */     }
/*     */ 
/* 188 */     if (this.listenerList == NULL_ARRAY)
/*     */     {
/* 191 */       this.listenerList = new Object[] { paramClass, paramT };
/*     */     }
/*     */     else {
/* 194 */       int i = this.listenerList.length;
/* 195 */       Object[] arrayOfObject = new Object[i + 2];
/* 196 */       System.arraycopy(this.listenerList, 0, arrayOfObject, 0, i);
/*     */ 
/* 198 */       arrayOfObject[i] = paramClass;
/* 199 */       arrayOfObject[(i + 1)] = paramT;
/*     */ 
/* 201 */       this.listenerList = arrayOfObject;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized <T extends EventListener> void remove(Class<T> paramClass, T paramT)
/*     */   {
/* 211 */     if (paramT == null)
/*     */     {
/* 215 */       return;
/*     */     }
/* 217 */     if (!paramClass.isInstance(paramT)) {
/* 218 */       throw new IllegalArgumentException("Listener " + paramT + " is not of type " + paramClass);
/*     */     }
/*     */ 
/* 222 */     int i = -1;
/* 223 */     for (int j = this.listenerList.length - 2; j >= 0; j -= 2) {
/* 224 */       if ((this.listenerList[j] == paramClass) && (this.listenerList[(j + 1)].equals(paramT) == true)) {
/* 225 */         i = j;
/* 226 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 231 */     if (i != -1) {
/* 232 */       Object[] arrayOfObject = new Object[this.listenerList.length - 2];
/*     */ 
/* 234 */       System.arraycopy(this.listenerList, 0, arrayOfObject, 0, i);
/*     */ 
/* 238 */       if (i < arrayOfObject.length) {
/* 239 */         System.arraycopy(this.listenerList, i + 2, arrayOfObject, i, arrayOfObject.length - i);
/*     */       }
/*     */ 
/* 242 */       this.listenerList = (arrayOfObject.length == 0 ? NULL_ARRAY : arrayOfObject);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException
/*     */   {
/* 248 */     Object[] arrayOfObject = this.listenerList;
/* 249 */     paramObjectOutputStream.defaultWriteObject();
/*     */ 
/* 252 */     for (int i = 0; i < arrayOfObject.length; i += 2) {
/* 253 */       Class localClass = (Class)arrayOfObject[i];
/* 254 */       EventListener localEventListener = (EventListener)arrayOfObject[(i + 1)];
/* 255 */       if ((localEventListener != null) && ((localEventListener instanceof Serializable))) {
/* 256 */         paramObjectOutputStream.writeObject(localClass.getName());
/* 257 */         paramObjectOutputStream.writeObject(localEventListener);
/*     */       }
/*     */     }
/*     */ 
/* 261 */     paramObjectOutputStream.writeObject(null);
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException
/*     */   {
/* 266 */     this.listenerList = NULL_ARRAY;
/* 267 */     paramObjectInputStream.defaultReadObject();
/*     */     Object localObject;
/* 270 */     while (null != (localObject = paramObjectInputStream.readObject())) {
/* 271 */       ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
/* 272 */       EventListener localEventListener = (EventListener)paramObjectInputStream.readObject();
/* 273 */       add(Class.forName((String)localObject, true, localClassLoader), localEventListener);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 281 */     Object[] arrayOfObject = this.listenerList;
/* 282 */     String str = "EventListenerList: ";
/* 283 */     str = str + arrayOfObject.length / 2 + " listeners: ";
/* 284 */     for (int i = 0; i <= arrayOfObject.length - 2; i += 2) {
/* 285 */       str = str + " type " + ((Class)arrayOfObject[i]).getName();
/* 286 */       str = str + " listener " + arrayOfObject[(i + 1)];
/*     */     }
/* 288 */     return str;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.event.EventListenerList
 * JD-Core Version:    0.6.2
 */