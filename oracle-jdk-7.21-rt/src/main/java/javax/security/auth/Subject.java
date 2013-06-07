/*      */ package javax.security.auth;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectInputStream.GetField;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.ObjectOutputStream.PutField;
/*      */ import java.io.ObjectStreamField;
/*      */ import java.io.Serializable;
/*      */ import java.security.AccessControlContext;
/*      */ import java.security.AccessController;
/*      */ import java.security.DomainCombiner;
/*      */ import java.security.Principal;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.security.ProtectionDomain;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.AbstractSet;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.ListIterator;
/*      */ import java.util.Set;
/*      */ import sun.security.util.ResourcesMgr;
/*      */ 
/*      */ public final class Subject
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = -8308522755600156056L;
/*      */   Set<Principal> principals;
/*      */   transient Set<Object> pubCredentials;
/*      */   transient Set<Object> privCredentials;
/*  128 */   private volatile boolean readOnly = false;
/*      */   private static final int PRINCIPAL_SET = 1;
/*      */   private static final int PUB_CREDENTIAL_SET = 2;
/*      */   private static final int PRIV_CREDENTIAL_SET = 3;
/*  134 */   private static final ProtectionDomain[] NULL_PD_ARRAY = new ProtectionDomain[0];
/*      */ 
/*      */   public Subject()
/*      */   {
/*  156 */     this.principals = Collections.synchronizedSet(new SecureSet(this, 1));
/*      */ 
/*  158 */     this.pubCredentials = Collections.synchronizedSet(new SecureSet(this, 2));
/*      */ 
/*  160 */     this.privCredentials = Collections.synchronizedSet(new SecureSet(this, 3));
/*      */   }
/*      */ 
/*      */   public Subject(boolean paramBoolean, Set<? extends Principal> paramSet, Set<?> paramSet1, Set<?> paramSet2)
/*      */   {
/*  203 */     if ((paramSet == null) || (paramSet1 == null) || (paramSet2 == null))
/*      */     {
/*  206 */       throw new NullPointerException(ResourcesMgr.getString("invalid.null.input.s."));
/*      */     }
/*      */ 
/*  209 */     this.principals = Collections.synchronizedSet(new SecureSet(this, 1, paramSet));
/*      */ 
/*  211 */     this.pubCredentials = Collections.synchronizedSet(new SecureSet(this, 2, paramSet1));
/*      */ 
/*  213 */     this.privCredentials = Collections.synchronizedSet(new SecureSet(this, 3, paramSet2));
/*      */ 
/*  215 */     this.readOnly = paramBoolean;
/*      */   }
/*      */ 
/*      */   public void setReadOnly()
/*      */   {
/*  239 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  240 */     if (localSecurityManager != null) {
/*  241 */       localSecurityManager.checkPermission(AuthPermissionHolder.SET_READ_ONLY_PERMISSION);
/*      */     }
/*      */ 
/*  244 */     this.readOnly = true;
/*      */   }
/*      */ 
/*      */   public boolean isReadOnly()
/*      */   {
/*  255 */     return this.readOnly;
/*      */   }
/*      */ 
/*      */   public static Subject getSubject(AccessControlContext paramAccessControlContext)
/*      */   {
/*  285 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  286 */     if (localSecurityManager != null) {
/*  287 */       localSecurityManager.checkPermission(AuthPermissionHolder.GET_SUBJECT_PERMISSION);
/*      */     }
/*      */ 
/*  290 */     if (paramAccessControlContext == null) {
/*  291 */       throw new NullPointerException(ResourcesMgr.getString("invalid.null.AccessControlContext.provided"));
/*      */     }
/*      */ 
/*  296 */     return (Subject)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Subject run() {
/*  299 */         DomainCombiner localDomainCombiner = this.val$acc.getDomainCombiner();
/*  300 */         if (!(localDomainCombiner instanceof SubjectDomainCombiner))
/*  301 */           return null;
/*  302 */         SubjectDomainCombiner localSubjectDomainCombiner = (SubjectDomainCombiner)localDomainCombiner;
/*  303 */         return localSubjectDomainCombiner.getSubject();
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public static <T> T doAs(Subject paramSubject, PrivilegedAction<T> paramPrivilegedAction)
/*      */   {
/*  343 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  344 */     if (localSecurityManager != null) {
/*  345 */       localSecurityManager.checkPermission(AuthPermissionHolder.DO_AS_PERMISSION);
/*      */     }
/*  347 */     if (paramPrivilegedAction == null) {
/*  348 */       throw new NullPointerException(ResourcesMgr.getString("invalid.null.action.provided"));
/*      */     }
/*      */ 
/*  353 */     AccessControlContext localAccessControlContext = AccessController.getContext();
/*      */ 
/*  356 */     return AccessController.doPrivileged(paramPrivilegedAction, createContext(paramSubject, localAccessControlContext));
/*      */   }
/*      */ 
/*      */   public static <T> T doAs(Subject paramSubject, PrivilegedExceptionAction<T> paramPrivilegedExceptionAction)
/*      */     throws PrivilegedActionException
/*      */   {
/*  402 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  403 */     if (localSecurityManager != null) {
/*  404 */       localSecurityManager.checkPermission(AuthPermissionHolder.DO_AS_PERMISSION);
/*      */     }
/*      */ 
/*  407 */     if (paramPrivilegedExceptionAction == null) {
/*  408 */       throw new NullPointerException(ResourcesMgr.getString("invalid.null.action.provided"));
/*      */     }
/*      */ 
/*  412 */     AccessControlContext localAccessControlContext = AccessController.getContext();
/*      */ 
/*  415 */     return AccessController.doPrivileged(paramPrivilegedExceptionAction, createContext(paramSubject, localAccessControlContext));
/*      */   }
/*      */ 
/*      */   public static <T> T doAsPrivileged(Subject paramSubject, PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext)
/*      */   {
/*  456 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  457 */     if (localSecurityManager != null) {
/*  458 */       localSecurityManager.checkPermission(AuthPermissionHolder.DO_AS_PRIVILEGED_PERMISSION);
/*      */     }
/*      */ 
/*  461 */     if (paramPrivilegedAction == null) {
/*  462 */       throw new NullPointerException(ResourcesMgr.getString("invalid.null.action.provided"));
/*      */     }
/*      */ 
/*  467 */     AccessControlContext localAccessControlContext = paramAccessControlContext == null ? new AccessControlContext(NULL_PD_ARRAY) : paramAccessControlContext;
/*      */ 
/*  473 */     return AccessController.doPrivileged(paramPrivilegedAction, createContext(paramSubject, localAccessControlContext));
/*      */   }
/*      */ 
/*      */   public static <T> T doAsPrivileged(Subject paramSubject, PrivilegedExceptionAction<T> paramPrivilegedExceptionAction, AccessControlContext paramAccessControlContext)
/*      */     throws PrivilegedActionException
/*      */   {
/*  520 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  521 */     if (localSecurityManager != null) {
/*  522 */       localSecurityManager.checkPermission(AuthPermissionHolder.DO_AS_PRIVILEGED_PERMISSION);
/*      */     }
/*      */ 
/*  525 */     if (paramPrivilegedExceptionAction == null) {
/*  526 */       throw new NullPointerException(ResourcesMgr.getString("invalid.null.action.provided"));
/*      */     }
/*      */ 
/*  530 */     AccessControlContext localAccessControlContext = paramAccessControlContext == null ? new AccessControlContext(NULL_PD_ARRAY) : paramAccessControlContext;
/*      */ 
/*  536 */     return AccessController.doPrivileged(paramPrivilegedExceptionAction, createContext(paramSubject, localAccessControlContext));
/*      */   }
/*      */ 
/*      */   private static AccessControlContext createContext(Subject paramSubject, final AccessControlContext paramAccessControlContext)
/*      */   {
/*  545 */     return (AccessControlContext)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public AccessControlContext run() {
/*  548 */         if (this.val$subject == null) {
/*  549 */           return new AccessControlContext(paramAccessControlContext, null);
/*      */         }
/*  551 */         return new AccessControlContext(paramAccessControlContext, new SubjectDomainCombiner(this.val$subject));
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public Set<Principal> getPrincipals()
/*      */   {
/*  577 */     return this.principals;
/*      */   }
/*      */ 
/*      */   public <T extends Principal> Set<T> getPrincipals(Class<T> paramClass)
/*      */   {
/*  604 */     if (paramClass == null) {
/*  605 */       throw new NullPointerException(ResourcesMgr.getString("invalid.null.Class.provided"));
/*      */     }
/*      */ 
/*  610 */     return new ClassSet(1, paramClass);
/*      */   }
/*      */ 
/*      */   public Set<Object> getPublicCredentials()
/*      */   {
/*  631 */     return this.pubCredentials;
/*      */   }
/*      */ 
/*      */   public Set<Object> getPrivateCredentials()
/*      */   {
/*  671 */     return this.privCredentials;
/*      */   }
/*      */ 
/*      */   public <T> Set<T> getPublicCredentials(Class<T> paramClass)
/*      */   {
/*  698 */     if (paramClass == null) {
/*  699 */       throw new NullPointerException(ResourcesMgr.getString("invalid.null.Class.provided"));
/*      */     }
/*      */ 
/*  704 */     return new ClassSet(2, paramClass);
/*      */   }
/*      */ 
/*      */   public <T> Set<T> getPrivateCredentials(Class<T> paramClass)
/*      */   {
/*  743 */     if (paramClass == null) {
/*  744 */       throw new NullPointerException(ResourcesMgr.getString("invalid.null.Class.provided"));
/*      */     }
/*      */ 
/*  749 */     return new ClassSet(3, paramClass);
/*      */   }
/*      */ 
/*      */   public boolean equals(Object paramObject)
/*      */   {
/*  775 */     if (paramObject == null) {
/*  776 */       return false;
/*      */     }
/*  778 */     if (this == paramObject) {
/*  779 */       return true;
/*      */     }
/*  781 */     if ((paramObject instanceof Subject))
/*      */     {
/*  783 */       Subject localSubject = (Subject)paramObject;
/*      */       HashSet localHashSet;
/*  787 */       synchronized (localSubject.principals)
/*      */       {
/*  789 */         localHashSet = new HashSet(localSubject.principals);
/*      */       }
/*  791 */       if (!this.principals.equals(localHashSet)) {
/*  792 */         return false;
/*      */       }
/*      */ 
/*  796 */       synchronized (localSubject.pubCredentials)
/*      */       {
/*  798 */         ??? = new HashSet(localSubject.pubCredentials);
/*      */       }
/*  800 */       if (!this.pubCredentials.equals(???)) {
/*  801 */         return false;
/*      */       }
/*      */ 
/*  805 */       synchronized (localSubject.privCredentials)
/*      */       {
/*  807 */         ??? = new HashSet(localSubject.privCredentials);
/*      */       }
/*  809 */       if (!this.privCredentials.equals(???)) {
/*  810 */         return false;
/*      */       }
/*  812 */       return true;
/*      */     }
/*  814 */     return false;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  825 */     return toString(true);
/*      */   }
/*      */ 
/*      */   String toString(boolean paramBoolean)
/*      */   {
/*  835 */     String str1 = ResourcesMgr.getString("Subject.");
/*  836 */     String str2 = "";
/*      */     Iterator localIterator;
/*      */     Object localObject1;
/*  838 */     synchronized (this.principals) {
/*  839 */       localIterator = this.principals.iterator();
/*  840 */       while (localIterator.hasNext()) {
/*  841 */         localObject1 = (Principal)localIterator.next();
/*  842 */         str2 = str2 + ResourcesMgr.getString(".Principal.") + ((Principal)localObject1).toString() + ResourcesMgr.getString("NEWLINE");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  847 */     synchronized (this.pubCredentials) {
/*  848 */       localIterator = this.pubCredentials.iterator();
/*  849 */       while (localIterator.hasNext()) {
/*  850 */         localObject1 = localIterator.next();
/*  851 */         str2 = str2 + ResourcesMgr.getString(".Public.Credential.") + localObject1.toString() + ResourcesMgr.getString("NEWLINE");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  857 */     if (paramBoolean) {
/*  858 */       synchronized (this.privCredentials) {
/*  859 */         localIterator = this.privCredentials.iterator();
/*      */         while (true) if (localIterator.hasNext()) {
/*      */             try {
/*  862 */               localObject1 = localIterator.next();
/*  863 */               str2 = str2 + ResourcesMgr.getString(".Private.Credential.") + localObject1.toString() + ResourcesMgr.getString("NEWLINE");
/*      */             }
/*      */             catch (SecurityException localSecurityException)
/*      */             {
/*  868 */               str2 = str2 + ResourcesMgr.getString(".Private.Credential.inaccessible.");
/*      */             }
/*      */           }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  875 */     return str1 + str2;
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/*  901 */     int i = 0;
/*      */     Iterator localIterator;
/*  903 */     synchronized (this.principals) {
/*  904 */       localIterator = this.principals.iterator();
/*  905 */       while (localIterator.hasNext()) {
/*  906 */         Principal localPrincipal = (Principal)localIterator.next();
/*  907 */         i ^= localPrincipal.hashCode();
/*      */       }
/*      */     }
/*      */ 
/*  911 */     synchronized (this.pubCredentials) {
/*  912 */       localIterator = this.pubCredentials.iterator();
/*  913 */       while (localIterator.hasNext()) {
/*  914 */         i ^= getCredHashCode(localIterator.next());
/*      */       }
/*      */     }
/*  917 */     return i;
/*      */   }
/*      */ 
/*      */   private int getCredHashCode(Object paramObject)
/*      */   {
/*      */     try
/*      */     {
/*  925 */       return paramObject.hashCode(); } catch (IllegalStateException localIllegalStateException) {
/*      */     }
/*  927 */     return paramObject.getClass().toString().hashCode();
/*      */   }
/*      */ 
/*      */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */     throws IOException
/*      */   {
/*  936 */     synchronized (this.principals) {
/*  937 */       paramObjectOutputStream.defaultWriteObject();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/*  947 */     paramObjectInputStream.defaultReadObject();
/*      */ 
/*  951 */     this.pubCredentials = Collections.synchronizedSet(new SecureSet(this, 2));
/*      */ 
/*  953 */     this.privCredentials = Collections.synchronizedSet(new SecureSet(this, 3));
/*      */   }
/*      */ 
/*      */   static class AuthPermissionHolder
/*      */   {
/* 1400 */     static final AuthPermission DO_AS_PERMISSION = new AuthPermission("doAs");
/*      */ 
/* 1403 */     static final AuthPermission DO_AS_PRIVILEGED_PERMISSION = new AuthPermission("doAsPrivileged");
/*      */ 
/* 1406 */     static final AuthPermission SET_READ_ONLY_PERMISSION = new AuthPermission("setReadOnly");
/*      */ 
/* 1409 */     static final AuthPermission GET_SUBJECT_PERMISSION = new AuthPermission("getSubject");
/*      */ 
/* 1412 */     static final AuthPermission MODIFY_PRINCIPALS_PERMISSION = new AuthPermission("modifyPrincipals");
/*      */ 
/* 1415 */     static final AuthPermission MODIFY_PUBLIC_CREDENTIALS_PERMISSION = new AuthPermission("modifyPublicCredentials");
/*      */ 
/* 1418 */     static final AuthPermission MODIFY_PRIVATE_CREDENTIALS_PERMISSION = new AuthPermission("modifyPrivateCredentials");
/*      */   }
/*      */ 
/*      */   private class ClassSet<T> extends AbstractSet<T>
/*      */   {
/*      */     private int which;
/*      */     private Class<T> c;
/*      */     private Set<T> set;
/*      */ 
/*      */     ClassSet(Class<T> arg2)
/*      */     {
/*      */       int i;
/* 1315 */       this.which = i;
/*      */       Object localObject1;
/* 1316 */       this.c = localObject1;
/* 1317 */       this.set = new HashSet();
/*      */ 
/* 1319 */       switch (i) {
/*      */       case 1:
/* 1321 */         synchronized (Subject.this.principals) { populateSet(); }
/* 1322 */         break;
/*      */       case 2:
/* 1324 */         synchronized (Subject.this.pubCredentials) { populateSet(); }
/* 1325 */         break;
/*      */       default:
/* 1327 */         synchronized (Subject.this.privCredentials) { populateSet(); }
/*      */ 
/*      */       }
/*      */     }
/*      */ 
/*      */     private void populateSet()
/*      */     {
/*      */       final Iterator localIterator;
/* 1334 */       switch (this.which) {
/*      */       case 1:
/* 1336 */         localIterator = Subject.this.principals.iterator();
/* 1337 */         break;
/*      */       case 2:
/* 1339 */         localIterator = Subject.this.pubCredentials.iterator();
/* 1340 */         break;
/*      */       default:
/* 1342 */         localIterator = Subject.this.privCredentials.iterator();
/*      */       }
/*      */ 
/* 1349 */       while (localIterator.hasNext())
/*      */       {
/*      */         Object localObject;
/* 1351 */         if (this.which == 3)
/* 1352 */           localObject = AccessController.doPrivileged(new PrivilegedAction()
/*      */           {
/*      */             public Object run() {
/* 1355 */               return localIterator.next();
/*      */             }
/*      */           });
/*      */         else {
/* 1359 */           localObject = localIterator.next();
/*      */         }
/* 1361 */         if (this.c.isAssignableFrom(localObject.getClass()))
/* 1362 */           if (this.which != 3) {
/* 1363 */             this.set.add(localObject);
/*      */           }
/*      */           else {
/* 1366 */             SecurityManager localSecurityManager = System.getSecurityManager();
/* 1367 */             if (localSecurityManager != null) {
/* 1368 */               localSecurityManager.checkPermission(new PrivateCredentialPermission(localObject.getClass().getName(), Subject.this.getPrincipals()));
/*      */             }
/*      */ 
/* 1372 */             this.set.add(localObject);
/*      */           }
/*      */       }
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 1379 */       return this.set.size();
/*      */     }
/*      */ 
/*      */     public Iterator<T> iterator() {
/* 1383 */       return this.set.iterator();
/*      */     }
/*      */ 
/*      */     public boolean add(T paramT)
/*      */     {
/* 1388 */       if (!paramT.getClass().isAssignableFrom(this.c)) {
/* 1389 */         MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("attempting.to.add.an.object.which.is.not.an.instance.of.class"));
/*      */ 
/* 1391 */         Object[] arrayOfObject = { this.c.toString() };
/* 1392 */         throw new SecurityException(localMessageFormat.format(arrayOfObject));
/*      */       }
/*      */ 
/* 1395 */       return this.set.add(paramT);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class SecureSet<E> extends AbstractSet<E>
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 7911754171111800359L;
/*  972 */     private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("this$0", Subject.class), new ObjectStreamField("elements", LinkedList.class), new ObjectStreamField("which", Integer.TYPE) };
/*      */     Subject subject;
/*      */     LinkedList<E> elements;
/*      */     private int which;
/*      */ 
/*      */     SecureSet(Subject paramSubject, int paramInt)
/*      */     {
/*  994 */       this.subject = paramSubject;
/*  995 */       this.which = paramInt;
/*  996 */       this.elements = new LinkedList();
/*      */     }
/*      */ 
/*      */     SecureSet(Subject paramSubject, int paramInt, Set<? extends E> paramSet) {
/* 1000 */       this.subject = paramSubject;
/* 1001 */       this.which = paramInt;
/* 1002 */       this.elements = new LinkedList(paramSet);
/*      */     }
/*      */ 
/*      */     public int size() {
/* 1006 */       return this.elements.size();
/*      */     }
/*      */ 
/*      */     public Iterator<E> iterator() {
/* 1010 */       final LinkedList localLinkedList = this.elements;
/* 1011 */       return new Iterator() {
/* 1012 */         ListIterator<E> i = localLinkedList.listIterator(0);
/*      */ 
/* 1014 */         public boolean hasNext() { return this.i.hasNext(); }
/*      */ 
/*      */         public E next() {
/* 1017 */           if (Subject.SecureSet.this.which != 3) {
/* 1018 */             return this.i.next();
/*      */           }
/*      */ 
/* 1021 */           SecurityManager localSecurityManager = System.getSecurityManager();
/* 1022 */           if (localSecurityManager != null) {
/*      */             try {
/* 1024 */               localSecurityManager.checkPermission(new PrivateCredentialPermission(localLinkedList.get(this.i.nextIndex()).getClass().getName(), Subject.SecureSet.this.subject.getPrincipals()));
/*      */             }
/*      */             catch (SecurityException localSecurityException)
/*      */             {
/* 1028 */               this.i.next();
/* 1029 */               throw localSecurityException;
/*      */             }
/*      */           }
/* 1032 */           return this.i.next();
/*      */         }
/*      */ 
/*      */         public void remove()
/*      */         {
/* 1037 */           if (Subject.SecureSet.this.subject.isReadOnly()) {
/* 1038 */             throw new IllegalStateException(ResourcesMgr.getString("Subject.is.read.only"));
/*      */           }
/*      */ 
/* 1042 */           SecurityManager localSecurityManager = System.getSecurityManager();
/* 1043 */           if (localSecurityManager != null) {
/* 1044 */             switch (Subject.SecureSet.this.which) {
/*      */             case 1:
/* 1046 */               localSecurityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PRINCIPALS_PERMISSION);
/* 1047 */               break;
/*      */             case 2:
/* 1049 */               localSecurityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PUBLIC_CREDENTIALS_PERMISSION);
/* 1050 */               break;
/*      */             default:
/* 1052 */               localSecurityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PRIVATE_CREDENTIALS_PERMISSION);
/*      */             }
/*      */           }
/*      */ 
/* 1056 */           this.i.remove();
/*      */         }
/*      */       };
/*      */     }
/*      */ 
/*      */     public boolean add(E paramE)
/*      */     {
/* 1063 */       if (this.subject.isReadOnly()) {
/* 1064 */         throw new IllegalStateException(ResourcesMgr.getString("Subject.is.read.only"));
/*      */       }
/*      */ 
/* 1068 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 1069 */       if (localSecurityManager != null) {
/* 1070 */         switch (this.which) {
/*      */         case 1:
/* 1072 */           localSecurityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PRINCIPALS_PERMISSION);
/* 1073 */           break;
/*      */         case 2:
/* 1075 */           localSecurityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PUBLIC_CREDENTIALS_PERMISSION);
/* 1076 */           break;
/*      */         default:
/* 1078 */           localSecurityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PRIVATE_CREDENTIALS_PERMISSION);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1083 */       switch (this.which) {
/*      */       case 1:
/* 1085 */         if (!(paramE instanceof Principal)) {
/* 1086 */           throw new SecurityException(ResourcesMgr.getString("attempting.to.add.an.object.which.is.not.an.instance.of.java.security.Principal.to.a.Subject.s.Principal.Set"));
/*      */         }
/*      */ 
/*      */         break;
/*      */       }
/*      */ 
/* 1096 */       if (!this.elements.contains(paramE)) {
/* 1097 */         return this.elements.add(paramE);
/*      */       }
/* 1099 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean remove(Object paramObject)
/*      */     {
/* 1104 */       final Iterator localIterator = iterator();
/* 1105 */       while (localIterator.hasNext())
/*      */       {
/*      */         Object localObject;
/* 1107 */         if (this.which != 3)
/* 1108 */           localObject = localIterator.next();
/*      */         else {
/* 1110 */           localObject = AccessController.doPrivileged(new PrivilegedAction()
/*      */           {
/*      */             public E run() {
/* 1113 */               return localIterator.next();
/*      */             }
/*      */           });
/*      */         }
/*      */ 
/* 1118 */         if (localObject == null) {
/* 1119 */           if (paramObject == null) {
/* 1120 */             localIterator.remove();
/* 1121 */             return true;
/*      */           }
/* 1123 */         } else if (localObject.equals(paramObject)) {
/* 1124 */           localIterator.remove();
/* 1125 */           return true;
/*      */         }
/*      */       }
/* 1128 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean contains(Object paramObject) {
/* 1132 */       final Iterator localIterator = iterator();
/* 1133 */       while (localIterator.hasNext())
/*      */       {
/*      */         Object localObject;
/* 1135 */         if (this.which != 3) {
/* 1136 */           localObject = localIterator.next();
/*      */         }
/*      */         else
/*      */         {
/* 1145 */           SecurityManager localSecurityManager = System.getSecurityManager();
/* 1146 */           if (localSecurityManager != null) {
/* 1147 */             localSecurityManager.checkPermission(new PrivateCredentialPermission(paramObject.getClass().getName(), this.subject.getPrincipals()));
/*      */           }
/*      */ 
/* 1151 */           localObject = AccessController.doPrivileged(new PrivilegedAction()
/*      */           {
/*      */             public E run() {
/* 1154 */               return localIterator.next();
/*      */             }
/*      */           });
/*      */         }
/*      */ 
/* 1159 */         if (localObject == null) {
/* 1160 */           if (paramObject == null)
/* 1161 */             return true;
/*      */         }
/* 1163 */         else if (localObject.equals(paramObject)) {
/* 1164 */           return true;
/*      */         }
/*      */       }
/* 1167 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean removeAll(Collection<?> paramCollection)
/*      */     {
/* 1172 */       boolean bool = false;
/* 1173 */       final Iterator localIterator1 = iterator();
/* 1174 */       while (localIterator1.hasNext())
/*      */       {
/*      */         Object localObject1;
/* 1176 */         if (this.which != 3)
/* 1177 */           localObject1 = localIterator1.next();
/*      */         else {
/* 1179 */           localObject1 = AccessController.doPrivileged(new PrivilegedAction()
/*      */           {
/*      */             public E run() {
/* 1182 */               return localIterator1.next();
/*      */             }
/*      */           });
/*      */         }
/*      */ 
/* 1187 */         Iterator localIterator2 = paramCollection.iterator();
/* 1188 */         while (localIterator2.hasNext()) {
/* 1189 */           Object localObject2 = localIterator2.next();
/* 1190 */           if (localObject1 == null) {
/* 1191 */             if (localObject2 == null) {
/* 1192 */               localIterator1.remove();
/* 1193 */               bool = true;
/* 1194 */               break;
/*      */             }
/* 1196 */           } else if (localObject1.equals(localObject2)) {
/* 1197 */             localIterator1.remove();
/* 1198 */             bool = true;
/* 1199 */             break;
/*      */           }
/*      */         }
/*      */       }
/* 1203 */       return bool;
/*      */     }
/*      */ 
/*      */     public boolean retainAll(Collection<?> paramCollection)
/*      */     {
/* 1208 */       boolean bool = false;
/* 1209 */       int i = 0;
/* 1210 */       final Iterator localIterator1 = iterator();
/* 1211 */       while (localIterator1.hasNext()) {
/* 1212 */         i = 0;
/*      */         Object localObject1;
/* 1214 */         if (this.which != 3)
/* 1215 */           localObject1 = localIterator1.next();
/*      */         else {
/* 1217 */           localObject1 = AccessController.doPrivileged(new PrivilegedAction()
/*      */           {
/*      */             public E run() {
/* 1220 */               return localIterator1.next();
/*      */             }
/*      */           });
/*      */         }
/*      */ 
/* 1225 */         Iterator localIterator2 = paramCollection.iterator();
/* 1226 */         while (localIterator2.hasNext()) {
/* 1227 */           Object localObject2 = localIterator2.next();
/* 1228 */           if (localObject1 == null) {
/* 1229 */             if (localObject2 == null) {
/* 1230 */               i = 1;
/* 1231 */               break;
/*      */             }
/* 1233 */           } else if (localObject1.equals(localObject2)) {
/* 1234 */             i = 1;
/* 1235 */             break;
/*      */           }
/*      */         }
/*      */ 
/* 1239 */         if (i == 0) {
/* 1240 */           localIterator1.remove();
/* 1241 */           i = 0;
/* 1242 */           bool = true;
/*      */         }
/*      */       }
/* 1245 */       return bool;
/*      */     }
/*      */ 
/*      */     public void clear() {
/* 1249 */       final Iterator localIterator = iterator();
/* 1250 */       while (localIterator.hasNext())
/*      */       {
/*      */         Object localObject;
/* 1252 */         if (this.which != 3)
/* 1253 */           localObject = localIterator.next();
/*      */         else {
/* 1255 */           localObject = AccessController.doPrivileged(new PrivilegedAction()
/*      */           {
/*      */             public E run() {
/* 1258 */               return localIterator.next();
/*      */             }
/*      */           });
/*      */         }
/* 1262 */         localIterator.remove();
/*      */       }
/*      */     }
/*      */ 
/*      */     private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */       throws IOException
/*      */     {
/* 1280 */       if (this.which == 3)
/*      */       {
/* 1282 */         localObject = iterator();
/* 1283 */         while (((Iterator)localObject).hasNext()) {
/* 1284 */           ((Iterator)localObject).next();
/*      */         }
/*      */       }
/* 1287 */       Object localObject = paramObjectOutputStream.putFields();
/* 1288 */       ((ObjectOutputStream.PutField)localObject).put("this$0", this.subject);
/* 1289 */       ((ObjectOutputStream.PutField)localObject).put("elements", this.elements);
/* 1290 */       ((ObjectOutputStream.PutField)localObject).put("which", this.which);
/* 1291 */       paramObjectOutputStream.writeFields();
/*      */     }
/*      */ 
/*      */     private void readObject(ObjectInputStream paramObjectInputStream)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/* 1297 */       ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
/* 1298 */       this.subject = ((Subject)localGetField.get("this$0", null));
/* 1299 */       this.elements = ((LinkedList)localGetField.get("elements", null));
/* 1300 */       this.which = localGetField.get("which", 0);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.security.auth.Subject
 * JD-Core Version:    0.6.2
 */