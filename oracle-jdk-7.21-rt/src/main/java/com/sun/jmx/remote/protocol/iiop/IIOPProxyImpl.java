/*     */ package com.sun.jmx.remote.protocol.iiop;
/*     */ 
/*     */ import com.sun.jmx.remote.internal.IIOPProxy;
/*     */ import java.rmi.NoSuchObjectException;
/*     */ import java.rmi.Remote;
/*     */ import java.rmi.RemoteException;
/*     */ import java.util.Properties;
/*     */ import javax.rmi.CORBA.Stub;
/*     */ import javax.rmi.PortableRemoteObject;
/*     */ import org.omg.CORBA.BAD_OPERATION;
/*     */ import org.omg.CORBA.ORB;
/*     */ import org.omg.CORBA.portable.Delegate;
/*     */ 
/*     */ public class IIOPProxyImpl
/*     */   implements IIOPProxy
/*     */ {
/*     */   public boolean isStub(java.lang.Object paramObject)
/*     */   {
/*  50 */     return paramObject instanceof Stub;
/*     */   }
/*     */ 
/*     */   public java.lang.Object getDelegate(java.lang.Object paramObject)
/*     */   {
/*  55 */     return ((Stub)paramObject)._get_delegate();
/*     */   }
/*     */ 
/*     */   public void setDelegate(java.lang.Object paramObject1, java.lang.Object paramObject2)
/*     */   {
/*  60 */     ((Stub)paramObject1)._set_delegate((Delegate)paramObject2);
/*     */   }
/*     */ 
/*     */   public java.lang.Object getOrb(java.lang.Object paramObject)
/*     */   {
/*     */     try {
/*  66 */       return ((Stub)paramObject)._orb();
/*     */     } catch (BAD_OPERATION localBAD_OPERATION) {
/*  68 */       throw new UnsupportedOperationException(localBAD_OPERATION);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void connect(java.lang.Object paramObject1, java.lang.Object paramObject2)
/*     */     throws RemoteException
/*     */   {
/*  76 */     ((Stub)paramObject1).connect((ORB)paramObject2);
/*     */   }
/*     */ 
/*     */   public boolean isOrb(java.lang.Object paramObject)
/*     */   {
/*  81 */     return paramObject instanceof ORB;
/*     */   }
/*     */ 
/*     */   public java.lang.Object createOrb(String[] paramArrayOfString, Properties paramProperties)
/*     */   {
/*  86 */     return ORB.init(paramArrayOfString, paramProperties);
/*     */   }
/*     */ 
/*     */   public java.lang.Object stringToObject(java.lang.Object paramObject, String paramString)
/*     */   {
/*  91 */     return ((ORB)paramObject).string_to_object(paramString);
/*     */   }
/*     */ 
/*     */   public String objectToString(java.lang.Object paramObject1, java.lang.Object paramObject2)
/*     */   {
/*  96 */     return ((ORB)paramObject1).object_to_string((org.omg.CORBA.Object)paramObject2);
/*     */   }
/*     */ 
/*     */   public <T> T narrow(java.lang.Object paramObject, Class<T> paramClass)
/*     */   {
/* 102 */     return PortableRemoteObject.narrow(paramObject, paramClass);
/*     */   }
/*     */ 
/*     */   public void exportObject(Remote paramRemote) throws RemoteException
/*     */   {
/* 107 */     PortableRemoteObject.exportObject(paramRemote);
/*     */   }
/*     */ 
/*     */   public void unexportObject(Remote paramRemote) throws NoSuchObjectException
/*     */   {
/* 112 */     PortableRemoteObject.unexportObject(paramRemote);
/*     */   }
/*     */ 
/*     */   public Remote toStub(Remote paramRemote) throws NoSuchObjectException
/*     */   {
/* 117 */     return PortableRemoteObject.toStub(paramRemote);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.remote.protocol.iiop.IIOPProxyImpl
 * JD-Core Version:    0.6.2
 */