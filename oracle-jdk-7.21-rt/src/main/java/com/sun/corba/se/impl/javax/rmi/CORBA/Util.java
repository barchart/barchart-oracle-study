/*     */ package com.sun.corba.se.impl.javax.rmi.CORBA;
/*     */ 
/*     */ import com.sun.corba.se.impl.corba.AnyImpl;
/*     */ import com.sun.corba.se.impl.io.ValueHandlerImpl;
/*     */ import com.sun.corba.se.impl.logging.UtilSystemException;
/*     */ import com.sun.corba.se.impl.orbutil.ORBClassLoader;
/*     */ import com.sun.corba.se.impl.orbutil.ORBUtility;
/*     */ import com.sun.corba.se.impl.util.IdentityHashtable;
/*     */ import com.sun.corba.se.impl.util.JDKBridge;
/*     */ import com.sun.corba.se.impl.util.Utility;
/*     */ import com.sun.corba.se.pept.transport.ContactInfoList;
/*     */ import com.sun.corba.se.spi.copyobject.CopierManager;
/*     */ import com.sun.corba.se.spi.copyobject.ObjectCopier;
/*     */ import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
/*     */ import com.sun.corba.se.spi.copyobject.ReflectiveCopyException;
/*     */ import com.sun.corba.se.spi.oa.OAInvocationInfo;
/*     */ import com.sun.corba.se.spi.orb.ORBVersion;
/*     */ import com.sun.corba.se.spi.orb.ORBVersionFactory;
/*     */ import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
/*     */ import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
/*     */ import com.sun.corba.se.spi.transport.CorbaContactInfoList;
/*     */ import java.io.NotSerializableException;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.rmi.AccessException;
/*     */ import java.rmi.MarshalException;
/*     */ import java.rmi.NoSuchObjectException;
/*     */ import java.rmi.Remote;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.ServerError;
/*     */ import java.rmi.ServerException;
/*     */ import java.rmi.UnexpectedException;
/*     */ import java.rmi.server.RMIClassLoader;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.EmptyStackException;
/*     */ import java.util.Enumeration;
/*     */ import javax.rmi.CORBA.Stub;
/*     */ import javax.rmi.CORBA.Tie;
/*     */ import javax.rmi.CORBA.UtilDelegate;
/*     */ import javax.rmi.CORBA.ValueHandler;
/*     */ import javax.transaction.InvalidTransactionException;
/*     */ import javax.transaction.TransactionRequiredException;
/*     */ import javax.transaction.TransactionRolledbackException;
/*     */ import org.omg.CORBA.ACTIVITY_COMPLETED;
/*     */ import org.omg.CORBA.ACTIVITY_REQUIRED;
/*     */ import org.omg.CORBA.Any;
/*     */ import org.omg.CORBA.BAD_OPERATION;
/*     */ import org.omg.CORBA.BAD_PARAM;
/*     */ import org.omg.CORBA.COMM_FAILURE;
/*     */ import org.omg.CORBA.CompletionStatus;
/*     */ import org.omg.CORBA.INVALID_ACTIVITY;
/*     */ import org.omg.CORBA.INVALID_TRANSACTION;
/*     */ import org.omg.CORBA.INV_OBJREF;
/*     */ import org.omg.CORBA.MARSHAL;
/*     */ import org.omg.CORBA.NO_PERMISSION;
/*     */ import org.omg.CORBA.OBJECT_NOT_EXIST;
/*     */ import org.omg.CORBA.OBJ_ADAPTER;
/*     */ import org.omg.CORBA.SystemException;
/*     */ import org.omg.CORBA.TCKind;
/*     */ import org.omg.CORBA.TRANSACTION_REQUIRED;
/*     */ import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
/*     */ import org.omg.CORBA.TypeCode;
/*     */ import org.omg.CORBA.portable.Delegate;
/*     */ import org.omg.CORBA.portable.UnknownException;
/*     */ import sun.corba.JavaCorbaAccess;
/*     */ import sun.corba.SharedSecrets;
/*     */ 
/*     */ public class Util
/*     */   implements UtilDelegate
/*     */ {
/* 126 */   private static KeepAlive keepAlive = null;
/*     */ 
/* 129 */   private static IdentityHashtable exportedServants = new IdentityHashtable();
/*     */ 
/* 131 */   private static final ValueHandlerImpl valueHandlerSingleton = SharedSecrets.getJavaCorbaAccess().newValueHandlerImpl();
/*     */ 
/* 134 */   private UtilSystemException utilWrapper = UtilSystemException.get("rpc.encoding");
/*     */ 
/* 137 */   private static Util instance = null;
/*     */ 
/*     */   public Util() {
/* 140 */     setInstance(this);
/*     */   }
/*     */ 
/*     */   private static void setInstance(Util paramUtil) {
/* 144 */     assert (instance == null) : "Instance already defined";
/* 145 */     instance = paramUtil;
/*     */   }
/*     */ 
/*     */   public static Util getInstance() {
/* 149 */     return instance;
/*     */   }
/*     */ 
/*     */   public static boolean isInstanceDefined() {
/* 153 */     return instance != null;
/*     */   }
/*     */ 
/*     */   public void unregisterTargetsForORB(org.omg.CORBA.ORB paramORB)
/*     */   {
/* 160 */     for (Enumeration localEnumeration = exportedServants.keys(); localEnumeration.hasMoreElements(); )
/*     */     {
/* 162 */       java.lang.Object localObject = localEnumeration.nextElement();
/* 163 */       Remote localRemote = (Remote)((localObject instanceof Tie) ? ((Tie)localObject).getTarget() : localObject);
/*     */       try
/*     */       {
/* 168 */         if (paramORB == getTie(localRemote).orb())
/*     */           try {
/* 170 */             unexportObject(localRemote);
/*     */           }
/*     */           catch (NoSuchObjectException localNoSuchObjectException)
/*     */           {
/*     */           }
/*     */       }
/*     */       catch (BAD_OPERATION localBAD_OPERATION)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public RemoteException mapSystemException(SystemException paramSystemException)
/*     */   {
/* 189 */     if ((paramSystemException instanceof UnknownException)) {
/* 190 */       localObject1 = ((UnknownException)paramSystemException).originalEx;
/* 191 */       if ((localObject1 instanceof Error))
/* 192 */         return new ServerError("Error occurred in server thread", (Error)localObject1);
/* 193 */       if ((localObject1 instanceof RemoteException)) {
/* 194 */         return new ServerException("RemoteException occurred in server thread", (Exception)localObject1);
/*     */       }
/* 196 */       if ((localObject1 instanceof RuntimeException)) {
/* 197 */         throw ((RuntimeException)localObject1);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 202 */     java.lang.Object localObject1 = paramSystemException.getClass().getName();
/* 203 */     String str1 = ((String)localObject1).substring(((String)localObject1).lastIndexOf('.') + 1);
/*     */     String str2;
/* 205 */     switch (paramSystemException.completed.value()) {
/*     */     case 0:
/* 207 */       str2 = "Yes";
/* 208 */       break;
/*     */     case 1:
/* 210 */       str2 = "No";
/* 211 */       break;
/*     */     case 2:
/*     */     default:
/* 214 */       str2 = "Maybe";
/*     */     }
/*     */ 
/* 218 */     String str3 = "CORBA " + str1 + " " + paramSystemException.minor + " " + str2;
/*     */ 
/* 221 */     if ((paramSystemException instanceof COMM_FAILURE))
/* 222 */       return new MarshalException(str3, paramSystemException);
/*     */     java.lang.Object localObject2;
/* 223 */     if ((paramSystemException instanceof INV_OBJREF)) {
/* 224 */       localObject2 = new NoSuchObjectException(str3);
/* 225 */       ((RemoteException)localObject2).detail = paramSystemException;
/* 226 */       return localObject2;
/* 227 */     }if ((paramSystemException instanceof NO_PERMISSION))
/* 228 */       return new AccessException(str3, paramSystemException);
/* 229 */     if ((paramSystemException instanceof MARSHAL))
/* 230 */       return new MarshalException(str3, paramSystemException);
/* 231 */     if ((paramSystemException instanceof OBJECT_NOT_EXIST)) {
/* 232 */       localObject2 = new NoSuchObjectException(str3);
/* 233 */       ((RemoteException)localObject2).detail = paramSystemException;
/* 234 */       return localObject2;
/* 235 */     }if ((paramSystemException instanceof TRANSACTION_REQUIRED)) {
/* 236 */       localObject2 = new TransactionRequiredException(str3);
/* 237 */       ((RemoteException)localObject2).detail = paramSystemException;
/* 238 */       return localObject2;
/* 239 */     }if ((paramSystemException instanceof TRANSACTION_ROLLEDBACK)) {
/* 240 */       localObject2 = new TransactionRolledbackException(str3);
/* 241 */       ((RemoteException)localObject2).detail = paramSystemException;
/* 242 */       return localObject2;
/* 243 */     }if ((paramSystemException instanceof INVALID_TRANSACTION)) {
/* 244 */       localObject2 = new InvalidTransactionException(str3);
/* 245 */       ((RemoteException)localObject2).detail = paramSystemException;
/* 246 */       return localObject2;
/* 247 */     }if ((paramSystemException instanceof BAD_PARAM)) {
/* 248 */       localObject2 = paramSystemException;
/*     */ 
/* 252 */       if ((paramSystemException.minor == 1398079489) || (paramSystemException.minor == 1330446342))
/*     */       {
/* 255 */         if (paramSystemException.getMessage() != null)
/* 256 */           localObject2 = new NotSerializableException(paramSystemException.getMessage());
/*     */         else {
/* 258 */           localObject2 = new NotSerializableException();
/*     */         }
/* 260 */         ((Exception)localObject2).initCause(paramSystemException);
/*     */       }
/*     */ 
/* 263 */       return new MarshalException(str3, (Exception)localObject2);
/*     */     }
/*     */     Class[] arrayOfClass;
/*     */     Constructor localConstructor;
/*     */     java.lang.Object[] arrayOfObject;
/* 264 */     if ((paramSystemException instanceof ACTIVITY_REQUIRED)) {
/*     */       try {
/* 266 */         localObject2 = ORBClassLoader.loadClass("javax.activity.ActivityRequiredException");
/*     */ 
/* 268 */         arrayOfClass = new Class[2];
/* 269 */         arrayOfClass[0] = String.class;
/* 270 */         arrayOfClass[1] = Throwable.class;
/* 271 */         localConstructor = ((Class)localObject2).getConstructor(arrayOfClass);
/* 272 */         arrayOfObject = new java.lang.Object[2];
/* 273 */         arrayOfObject[0] = str3;
/* 274 */         arrayOfObject[1] = paramSystemException;
/* 275 */         return (RemoteException)localConstructor.newInstance(arrayOfObject);
/*     */       } catch (Throwable localThrowable1) {
/* 277 */         this.utilWrapper.classNotFound(localThrowable1, "javax.activity.ActivityRequiredException");
/*     */       }
/*     */     }
/* 280 */     else if ((paramSystemException instanceof ACTIVITY_COMPLETED)) {
/*     */       try {
/* 282 */         Class localClass1 = ORBClassLoader.loadClass("javax.activity.ActivityCompletedException");
/*     */ 
/* 284 */         arrayOfClass = new Class[2];
/* 285 */         arrayOfClass[0] = String.class;
/* 286 */         arrayOfClass[1] = Throwable.class;
/* 287 */         localConstructor = localClass1.getConstructor(arrayOfClass);
/* 288 */         arrayOfObject = new java.lang.Object[2];
/* 289 */         arrayOfObject[0] = str3;
/* 290 */         arrayOfObject[1] = paramSystemException;
/* 291 */         return (RemoteException)localConstructor.newInstance(arrayOfObject);
/*     */       } catch (Throwable localThrowable2) {
/* 293 */         this.utilWrapper.classNotFound(localThrowable2, "javax.activity.ActivityCompletedException");
/*     */       }
/*     */     }
/* 296 */     else if ((paramSystemException instanceof INVALID_ACTIVITY)) {
/*     */       try {
/* 298 */         Class localClass2 = ORBClassLoader.loadClass("javax.activity.InvalidActivityException");
/*     */ 
/* 300 */         arrayOfClass = new Class[2];
/* 301 */         arrayOfClass[0] = String.class;
/* 302 */         arrayOfClass[1] = Throwable.class;
/* 303 */         localConstructor = localClass2.getConstructor(arrayOfClass);
/* 304 */         arrayOfObject = new java.lang.Object[2];
/* 305 */         arrayOfObject[0] = str3;
/* 306 */         arrayOfObject[1] = paramSystemException;
/* 307 */         return (RemoteException)localConstructor.newInstance(arrayOfObject);
/*     */       } catch (Throwable localThrowable3) {
/* 309 */         this.utilWrapper.classNotFound(localThrowable3, "javax.activity.InvalidActivityException");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 315 */     return new RemoteException(str3, paramSystemException);
/*     */   }
/*     */ 
/*     */   public void writeAny(org.omg.CORBA.portable.OutputStream paramOutputStream, java.lang.Object paramObject)
/*     */   {
/* 326 */     org.omg.CORBA.ORB localORB = paramOutputStream.orb();
/*     */ 
/* 329 */     Any localAny = localORB.create_any();
/*     */ 
/* 332 */     java.lang.Object localObject = Utility.autoConnect(paramObject, localORB, false);
/*     */ 
/* 334 */     if ((localObject instanceof org.omg.CORBA.Object)) {
/* 335 */       localAny.insert_Object((org.omg.CORBA.Object)localObject);
/*     */     }
/* 337 */     else if (localObject == null)
/*     */     {
/* 340 */       localAny.insert_Value(null, createTypeCodeForNull(localORB));
/*     */     }
/* 342 */     else if ((localObject instanceof Serializable))
/*     */     {
/* 345 */       TypeCode localTypeCode = createTypeCode((Serializable)localObject, localAny, localORB);
/* 346 */       if (localTypeCode == null)
/* 347 */         localAny.insert_Value((Serializable)localObject);
/*     */       else
/* 349 */         localAny.insert_Value((Serializable)localObject, localTypeCode);
/* 350 */     } else if ((localObject instanceof Remote)) {
/* 351 */       ORBUtility.throwNotSerializableForCorba(localObject.getClass().getName());
/*     */     } else {
/* 353 */       ORBUtility.throwNotSerializableForCorba(localObject.getClass().getName());
/*     */     }
/*     */ 
/* 358 */     paramOutputStream.write_any(localAny);
/*     */   }
/*     */ 
/*     */   private TypeCode createTypeCode(Serializable paramSerializable, Any paramAny, org.omg.CORBA.ORB paramORB)
/*     */   {
/* 380 */     if (((paramAny instanceof AnyImpl)) && ((paramORB instanceof com.sun.corba.se.spi.orb.ORB)))
/*     */     {
/* 383 */       AnyImpl localAnyImpl = (AnyImpl)paramAny;
/*     */ 
/* 386 */       com.sun.corba.se.spi.orb.ORB localORB = (com.sun.corba.se.spi.orb.ORB)paramORB;
/*     */ 
/* 388 */       return localAnyImpl.createTypeCodeForClass(paramSerializable.getClass(), localORB);
/*     */     }
/*     */ 
/* 391 */     return null;
/*     */   }
/*     */ 
/*     */   private TypeCode createTypeCodeForNull(org.omg.CORBA.ORB paramORB)
/*     */   {
/* 403 */     if ((paramORB instanceof com.sun.corba.se.spi.orb.ORB))
/*     */     {
/* 405 */       localObject = (com.sun.corba.se.spi.orb.ORB)paramORB;
/*     */ 
/* 412 */       if ((!ORBVersionFactory.getFOREIGN().equals(((com.sun.corba.se.spi.orb.ORB)localObject).getORBVersion())) && (ORBVersionFactory.getNEWER().compareTo(((com.sun.corba.se.spi.orb.ORB)localObject).getORBVersion()) > 0))
/*     */       {
/* 415 */         return paramORB.get_primitive_tc(TCKind.tk_value);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 422 */     java.lang.Object localObject = "IDL:omg.org/CORBA/AbstractBase:1.0";
/*     */ 
/* 424 */     return paramORB.create_abstract_interface_tc((String)localObject, "");
/*     */   }
/*     */ 
/*     */   public java.lang.Object readAny(org.omg.CORBA.portable.InputStream paramInputStream)
/*     */   {
/* 434 */     Any localAny = paramInputStream.read_any();
/* 435 */     if (localAny.type().kind().value() == 14) {
/* 436 */       return localAny.extract_Object();
/*     */     }
/* 438 */     return localAny.extract_Value();
/*     */   }
/*     */ 
/*     */   public void writeRemoteObject(org.omg.CORBA.portable.OutputStream paramOutputStream, java.lang.Object paramObject)
/*     */   {
/* 455 */     java.lang.Object localObject = Utility.autoConnect(paramObject, paramOutputStream.orb(), false);
/* 456 */     paramOutputStream.write_Object((org.omg.CORBA.Object)localObject);
/*     */   }
/*     */ 
/*     */   public void writeAbstractObject(org.omg.CORBA.portable.OutputStream paramOutputStream, java.lang.Object paramObject)
/*     */   {
/* 473 */     java.lang.Object localObject = Utility.autoConnect(paramObject, paramOutputStream.orb(), false);
/* 474 */     ((org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream).write_abstract_interface(localObject);
/*     */   }
/*     */ 
/*     */   public void registerTarget(Tie paramTie, Remote paramRemote)
/*     */   {
/* 485 */     synchronized (exportedServants)
/*     */     {
/* 487 */       if (lookupTie(paramRemote) == null)
/*     */       {
/* 489 */         exportedServants.put(paramRemote, paramTie);
/* 490 */         paramTie.setTarget(paramRemote);
/*     */ 
/* 493 */         if (keepAlive == null)
/*     */         {
/* 496 */           keepAlive = (KeepAlive)AccessController.doPrivileged(new PrivilegedAction() {
/*     */             public java.lang.Object run() {
/* 498 */               return new KeepAlive();
/*     */             }
/*     */           });
/* 501 */           keepAlive.start();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unexportObject(Remote paramRemote)
/*     */     throws NoSuchObjectException
/*     */   {
/* 515 */     synchronized (exportedServants) {
/* 516 */       Tie localTie = lookupTie(paramRemote);
/* 517 */       if (localTie != null) {
/* 518 */         exportedServants.remove(paramRemote);
/* 519 */         Utility.purgeStubForTie(localTie);
/* 520 */         Utility.purgeTieAndServant(localTie);
/*     */         try {
/* 522 */           cleanUpTie(localTie);
/*     */         }
/*     */         catch (BAD_OPERATION localBAD_OPERATION)
/*     */         {
/*     */         }
/*     */         catch (OBJ_ADAPTER localOBJ_ADAPTER)
/*     */         {
/*     */         }
/*     */ 
/* 531 */         if (exportedServants.isEmpty()) {
/* 532 */           keepAlive.quit();
/* 533 */           keepAlive = null;
/*     */         }
/*     */       } else {
/* 536 */         throw new NoSuchObjectException("Tie not found");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void cleanUpTie(Tie paramTie)
/*     */     throws NoSuchObjectException
/*     */   {
/* 544 */     paramTie.setTarget(null);
/* 545 */     paramTie.deactivate();
/*     */   }
/*     */ 
/*     */   public Tie getTie(Remote paramRemote)
/*     */   {
/* 554 */     synchronized (exportedServants) {
/* 555 */       return lookupTie(paramRemote);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Tie lookupTie(Remote paramRemote)
/*     */   {
/* 564 */     Tie localTie = (Tie)exportedServants.get(paramRemote);
/* 565 */     if ((localTie == null) && ((paramRemote instanceof Tie)) && 
/* 566 */       (exportedServants.contains(paramRemote))) {
/* 567 */       localTie = (Tie)paramRemote;
/*     */     }
/*     */ 
/* 570 */     return localTie;
/*     */   }
/*     */ 
/*     */   public ValueHandler createValueHandler()
/*     */   {
/* 580 */     return valueHandlerSingleton;
/*     */   }
/*     */ 
/*     */   public String getCodebase(Class paramClass)
/*     */   {
/* 589 */     return RMIClassLoader.getClassAnnotation(paramClass);
/*     */   }
/*     */ 
/*     */   public Class loadClass(String paramString1, String paramString2, ClassLoader paramClassLoader)
/*     */     throws ClassNotFoundException
/*     */   {
/* 605 */     return JDKBridge.loadClass(paramString1, paramString2, paramClassLoader);
/*     */   }
/*     */ 
/*     */   public boolean isLocal(Stub paramStub)
/*     */     throws RemoteException
/*     */   {
/* 630 */     boolean bool = false;
/*     */     try
/*     */     {
/* 633 */       Delegate localDelegate = paramStub._get_delegate();
/* 634 */       if ((localDelegate instanceof CorbaClientDelegate))
/*     */       {
/* 636 */         CorbaClientDelegate localCorbaClientDelegate = (CorbaClientDelegate)localDelegate;
/* 637 */         ContactInfoList localContactInfoList = localCorbaClientDelegate.getContactInfoList();
/* 638 */         if ((localContactInfoList instanceof CorbaContactInfoList)) {
/* 639 */           CorbaContactInfoList localCorbaContactInfoList = (CorbaContactInfoList)localContactInfoList;
/* 640 */           LocalClientRequestDispatcher localLocalClientRequestDispatcher = localCorbaContactInfoList.getLocalClientRequestDispatcher();
/* 641 */           bool = localLocalClientRequestDispatcher.useLocalInvocation(null);
/*     */         }
/*     */       }
/*     */       else {
/* 645 */         bool = localDelegate.is_local(paramStub);
/*     */       }
/*     */     } catch (SystemException localSystemException) {
/* 648 */       throw javax.rmi.CORBA.Util.mapSystemException(localSystemException);
/*     */     }
/*     */ 
/* 651 */     return bool;
/*     */   }
/*     */ 
/*     */   public RemoteException wrapException(Throwable paramThrowable)
/*     */   {
/* 662 */     if ((paramThrowable instanceof SystemException)) {
/* 663 */       return mapSystemException((SystemException)paramThrowable);
/*     */     }
/*     */ 
/* 666 */     if ((paramThrowable instanceof Error))
/* 667 */       return new ServerError("Error occurred in server thread", (Error)paramThrowable);
/* 668 */     if ((paramThrowable instanceof RemoteException)) {
/* 669 */       return new ServerException("RemoteException occurred in server thread", (Exception)paramThrowable);
/*     */     }
/* 671 */     if ((paramThrowable instanceof RuntimeException)) {
/* 672 */       throw ((RuntimeException)paramThrowable);
/*     */     }
/*     */ 
/* 675 */     if ((paramThrowable instanceof Exception)) {
/* 676 */       return new UnexpectedException(paramThrowable.toString(), (Exception)paramThrowable);
/*     */     }
/* 678 */     return new UnexpectedException(paramThrowable.toString());
/*     */   }
/*     */ 
/*     */   public java.lang.Object[] copyObjects(java.lang.Object[] paramArrayOfObject, org.omg.CORBA.ORB paramORB)
/*     */     throws RemoteException
/*     */   {
/* 693 */     if (paramArrayOfObject == null)
/*     */     {
/* 698 */       throw new NullPointerException();
/*     */     }
/* 700 */     Class localClass = paramArrayOfObject.getClass().getComponentType();
/* 701 */     if ((Remote.class.isAssignableFrom(localClass)) && (!localClass.isInterface()))
/*     */     {
/* 705 */       Remote[] arrayOfRemote = new Remote[paramArrayOfObject.length];
/* 706 */       System.arraycopy(paramArrayOfObject, 0, arrayOfRemote, 0, paramArrayOfObject.length);
/* 707 */       return (java.lang.Object[])copyObject(arrayOfRemote, paramORB);
/*     */     }
/* 709 */     return (java.lang.Object[])copyObject(paramArrayOfObject, paramORB);
/*     */   }
/*     */ 
/*     */   public java.lang.Object copyObject(java.lang.Object paramObject, org.omg.CORBA.ORB paramORB)
/*     */     throws RemoteException
/*     */   {
/* 723 */     if ((paramORB instanceof com.sun.corba.se.spi.orb.ORB)) {
/* 724 */       localObject1 = (com.sun.corba.se.spi.orb.ORB)paramORB;
/*     */       try
/*     */       {
/* 730 */         return ((com.sun.corba.se.spi.orb.ORB)localObject1).peekInvocationInfo().getCopierFactory().make().copy(paramObject);
/*     */       }
/*     */       catch (EmptyStackException localEmptyStackException)
/*     */       {
/* 736 */         localObject2 = ((com.sun.corba.se.spi.orb.ORB)localObject1).getCopierManager();
/* 737 */         ObjectCopier localObjectCopier = ((CopierManager)localObject2).getDefaultObjectCopierFactory().make();
/* 738 */         return localObjectCopier.copy(paramObject);
/*     */       }
/*     */       catch (ReflectiveCopyException localReflectiveCopyException) {
/* 741 */         java.lang.Object localObject2 = new RemoteException();
/* 742 */         ((RemoteException)localObject2).initCause(localReflectiveCopyException);
/* 743 */         throw ((Throwable)localObject2);
/*     */       }
/*     */     }
/* 746 */     java.lang.Object localObject1 = (org.omg.CORBA_2_3.portable.OutputStream)paramORB.create_output_stream();
/*     */ 
/* 748 */     ((org.omg.CORBA_2_3.portable.OutputStream)localObject1).write_value((Serializable)paramObject);
/* 749 */     org.omg.CORBA_2_3.portable.InputStream localInputStream = (org.omg.CORBA_2_3.portable.InputStream)((org.omg.CORBA_2_3.portable.OutputStream)localObject1).create_input_stream();
/*     */ 
/* 751 */     return localInputStream.read_value();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.javax.rmi.CORBA.Util
 * JD-Core Version:    0.6.2
 */