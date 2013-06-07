/*     */ package com.sun.corba.se.impl.orbutil;
/*     */ 
/*     */ import com.sun.corba.se.impl.corba.CORBAObjectImpl;
/*     */ import com.sun.corba.se.impl.ior.iiop.JavaSerializationComponent;
/*     */ import com.sun.corba.se.impl.logging.OMGSystemException;
/*     */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*     */ import com.sun.corba.se.pept.transport.ContactInfoList;
/*     */ import com.sun.corba.se.spi.ior.IOR;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import com.sun.corba.se.spi.orb.ORBData;
/*     */ import com.sun.corba.se.spi.orb.ORBVersionFactory;
/*     */ import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
/*     */ import com.sun.corba.se.spi.protocol.ClientDelegateFactory;
/*     */ import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
/*     */ import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
/*     */ import com.sun.corba.se.spi.transport.CorbaContactInfoList;
/*     */ import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
/*     */ import java.io.PrintStream;
/*     */ import java.rmi.RemoteException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PermissionCollection;
/*     */ import java.security.Policy;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.ProtectionDomain;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.rmi.CORBA.Util;
/*     */ import javax.rmi.CORBA.ValueHandler;
/*     */ import javax.rmi.CORBA.ValueHandlerMultiFormat;
/*     */ import org.omg.CORBA.Any;
/*     */ import org.omg.CORBA.BAD_OPERATION;
/*     */ import org.omg.CORBA.CompletionStatus;
/*     */ import org.omg.CORBA.INTERNAL;
/*     */ import org.omg.CORBA.StructMember;
/*     */ import org.omg.CORBA.SystemException;
/*     */ import org.omg.CORBA.TCKind;
/*     */ import org.omg.CORBA.TypeCode;
/*     */ import org.omg.CORBA.TypeCodePackage.BadKind;
/*     */ import org.omg.CORBA.TypeCodePackage.Bounds;
/*     */ import org.omg.CORBA.portable.Delegate;
/*     */ import org.omg.CORBA.portable.InputStream;
/*     */ import org.omg.CORBA.portable.OutputStream;
/*     */ 
/*     */ public final class ORBUtility
/*     */ {
/*  99 */   private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("util");
/*     */ 
/* 101 */   private static OMGSystemException omgWrapper = OMGSystemException.get("util");
/*     */ 
/* 104 */   private static StructMember[] members = null;
/*     */ 
/* 360 */   private static final Hashtable exceptionClassNames = new Hashtable();
/* 361 */   private static final Hashtable exceptionRepositoryIds = new Hashtable();
/*     */ 
/*     */   private static StructMember[] systemExceptionMembers(ORB paramORB)
/*     */   {
/* 107 */     if (members == null) {
/* 108 */       members = new StructMember[3];
/* 109 */       members[0] = new StructMember("id", paramORB.create_string_tc(0), null);
/* 110 */       members[1] = new StructMember("minor", paramORB.get_primitive_tc(TCKind.tk_long), null);
/* 111 */       members[2] = new StructMember("completed", paramORB.get_primitive_tc(TCKind.tk_long), null);
/*     */     }
/* 113 */     return members;
/*     */   }
/*     */ 
/*     */   private static TypeCode getSystemExceptionTypeCode(ORB paramORB, String paramString1, String paramString2) {
/* 117 */     synchronized (TypeCode.class) {
/* 118 */       return paramORB.create_exception_tc(paramString1, paramString2, systemExceptionMembers(paramORB));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean isSystemExceptionTypeCode(TypeCode paramTypeCode, ORB paramORB) {
/* 123 */     StructMember[] arrayOfStructMember = systemExceptionMembers(paramORB);
/*     */     try {
/* 125 */       return (paramTypeCode.kind().value() == 22) && (paramTypeCode.member_count() == 3) && (paramTypeCode.member_type(0).equal(arrayOfStructMember[0].type)) && (paramTypeCode.member_type(1).equal(arrayOfStructMember[1].type)) && (paramTypeCode.member_type(2).equal(arrayOfStructMember[2].type));
/*     */     }
/*     */     catch (BadKind localBadKind)
/*     */     {
/* 131 */       return false; } catch (Bounds localBounds) {
/*     */     }
/* 133 */     return false;
/*     */   }
/*     */ 
/*     */   public static void insertSystemException(SystemException paramSystemException, Any paramAny)
/*     */   {
/* 142 */     OutputStream localOutputStream = paramAny.create_output_stream();
/* 143 */     ORB localORB = (ORB)localOutputStream.orb();
/* 144 */     String str1 = paramSystemException.getClass().getName();
/* 145 */     String str2 = repositoryIdOf(str1);
/* 146 */     localOutputStream.write_string(str2);
/* 147 */     localOutputStream.write_long(paramSystemException.minor);
/* 148 */     localOutputStream.write_long(paramSystemException.completed.value());
/* 149 */     paramAny.read_value(localOutputStream.create_input_stream(), getSystemExceptionTypeCode(localORB, str2, str1));
/*     */   }
/*     */ 
/*     */   public static SystemException extractSystemException(Any paramAny)
/*     */   {
/* 154 */     InputStream localInputStream = paramAny.create_input_stream();
/* 155 */     ORB localORB = (ORB)localInputStream.orb();
/* 156 */     if (!isSystemExceptionTypeCode(paramAny.type(), localORB)) {
/* 157 */       throw wrapper.unknownDsiSysex(CompletionStatus.COMPLETED_MAYBE);
/*     */     }
/* 159 */     return readSystemException(localInputStream);
/*     */   }
/*     */ 
/*     */   public static ValueHandler createValueHandler()
/*     */   {
/* 166 */     return Util.createValueHandler();
/*     */   }
/*     */ 
/*     */   public static boolean isForeignORB(ORB paramORB)
/*     */   {
/* 176 */     if (paramORB == null)
/* 177 */       return false;
/*     */     try
/*     */     {
/* 180 */       return paramORB.getORBVersion().equals(ORBVersionFactory.getFOREIGN()); } catch (SecurityException localSecurityException) {
/*     */     }
/* 182 */     return false;
/*     */   }
/*     */ 
/*     */   public static int bytesToInt(byte[] paramArrayOfByte, int paramInt)
/*     */   {
/* 197 */     int i = paramArrayOfByte[(paramInt++)] << 24 & 0xFF000000;
/* 198 */     int j = paramArrayOfByte[(paramInt++)] << 16 & 0xFF0000;
/* 199 */     int k = paramArrayOfByte[(paramInt++)] << 8 & 0xFF00;
/* 200 */     int m = paramArrayOfByte[(paramInt++)] << 0 & 0xFF;
/*     */ 
/* 202 */     return i | j | k | m;
/*     */   }
/*     */ 
/*     */   public static void intToBytes(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
/*     */   {
/* 214 */     paramArrayOfByte[(paramInt2++)] = ((byte)(paramInt1 >>> 24 & 0xFF));
/* 215 */     paramArrayOfByte[(paramInt2++)] = ((byte)(paramInt1 >>> 16 & 0xFF));
/* 216 */     paramArrayOfByte[(paramInt2++)] = ((byte)(paramInt1 >>> 8 & 0xFF));
/* 217 */     paramArrayOfByte[(paramInt2++)] = ((byte)(paramInt1 >>> 0 & 0xFF));
/*     */   }
/*     */ 
/*     */   public static int hexOf(char paramChar)
/*     */   {
/* 226 */     int i = paramChar - '0';
/* 227 */     if ((i >= 0) && (i <= 9)) {
/* 228 */       return i;
/*     */     }
/* 230 */     i = paramChar - 'a' + 10;
/* 231 */     if ((i >= 10) && (i <= 15)) {
/* 232 */       return i;
/*     */     }
/* 234 */     i = paramChar - 'A' + 10;
/* 235 */     if ((i >= 10) && (i <= 15)) {
/* 236 */       return i;
/*     */     }
/* 238 */     throw wrapper.badHexDigit();
/*     */   }
/*     */ 
/*     */   public static void writeSystemException(SystemException paramSystemException, OutputStream paramOutputStream)
/*     */   {
/* 251 */     String str = repositoryIdOf(paramSystemException.getClass().getName());
/* 252 */     paramOutputStream.write_string(str);
/* 253 */     paramOutputStream.write_long(paramSystemException.minor);
/* 254 */     paramOutputStream.write_long(paramSystemException.completed.value());
/*     */   }
/*     */ 
/*     */   public static SystemException readSystemException(InputStream paramInputStream)
/*     */   {
/*     */     try
/*     */     {
/* 264 */       String str = classNameOf(paramInputStream.read_string());
/* 265 */       SystemException localSystemException = (SystemException)ORBClassLoader.loadClass(str).newInstance();
/*     */ 
/* 267 */       localSystemException.minor = paramInputStream.read_long();
/* 268 */       localSystemException.completed = CompletionStatus.from_int(paramInputStream.read_long());
/* 269 */       return localSystemException;
/*     */     } catch (Exception localException) {
/* 271 */       throw wrapper.unknownSysex(CompletionStatus.COMPLETED_MAYBE, localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String classNameOf(String paramString)
/*     */   {
/* 284 */     String str = null;
/*     */ 
/* 286 */     str = (String)exceptionClassNames.get(paramString);
/* 287 */     if (str == null) {
/* 288 */       str = "org.omg.CORBA.UNKNOWN";
/*     */     }
/* 290 */     return str;
/*     */   }
/*     */ 
/*     */   public static boolean isSystemException(String paramString)
/*     */   {
/* 299 */     String str = null;
/*     */ 
/* 301 */     str = (String)exceptionClassNames.get(paramString);
/* 302 */     if (str == null) {
/* 303 */       return false;
/*     */     }
/* 305 */     return true;
/*     */   }
/*     */ 
/*     */   public static byte getEncodingVersion(ORB paramORB, IOR paramIOR)
/*     */   {
/* 320 */     if (paramORB.getORBData().isJavaSerializationEnabled()) {
/* 321 */       IIOPProfile localIIOPProfile = paramIOR.getProfile();
/* 322 */       IIOPProfileTemplate localIIOPProfileTemplate = (IIOPProfileTemplate)localIIOPProfile.getTaggedProfileTemplate();
/*     */ 
/* 324 */       Iterator localIterator = localIIOPProfileTemplate.iteratorById(1398099458);
/*     */ 
/* 326 */       if (localIterator.hasNext()) {
/* 327 */         JavaSerializationComponent localJavaSerializationComponent = (JavaSerializationComponent)localIterator.next();
/*     */ 
/* 329 */         int i = localJavaSerializationComponent.javaSerializationVersion();
/* 330 */         if (i >= 1)
/* 331 */           return 1;
/* 332 */         if (i > 0) {
/* 333 */           return localJavaSerializationComponent.javaSerializationVersion();
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 340 */     return 0;
/*     */   }
/*     */ 
/*     */   public static String repositoryIdOf(String paramString)
/*     */   {
/* 353 */     String str = (String)exceptionRepositoryIds.get(paramString);
/* 354 */     if (str == null) {
/* 355 */       str = "IDL:omg.org/CORBA/UNKNOWN:1.0";
/*     */     }
/* 357 */     return str;
/*     */   }
/*     */ 
/*     */   public static int[] parseVersion(String paramString)
/*     */   {
/* 481 */     if (paramString == null)
/* 482 */       return new int[0];
/* 483 */     char[] arrayOfChar = paramString.toCharArray();
/*     */ 
/* 485 */     for (int i = 0; 
/* 486 */       (i < arrayOfChar.length) && ((arrayOfChar[i] < '0') || (arrayOfChar[i] > '9')); i++)
/* 487 */       if (i == arrayOfChar.length)
/* 488 */         return new int[0];
/* 489 */     int j = i + 1;
/* 490 */     int k = 1;
/* 491 */     for (; j < arrayOfChar.length; j++)
/* 492 */       if (arrayOfChar[j] == '.')
/* 493 */         k++;
/* 494 */       else if ((arrayOfChar[j] < '0') || (arrayOfChar[j] > '9'))
/*     */           break;
/* 496 */     int[] arrayOfInt = new int[k];
/* 497 */     for (int m = 0; m < k; m++) {
/* 498 */       int n = paramString.indexOf('.', i);
/* 499 */       if ((n == -1) || (n > j))
/* 500 */         n = j;
/* 501 */       if (i >= n)
/* 502 */         arrayOfInt[m] = 0;
/*     */       else
/* 504 */         arrayOfInt[m] = Integer.parseInt(paramString.substring(i, n));
/* 505 */       i = n + 1;
/*     */     }
/* 507 */     return arrayOfInt;
/*     */   }
/*     */ 
/*     */   public static int compareVersion(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
/*     */   {
/* 514 */     if (paramArrayOfInt1 == null)
/* 515 */       paramArrayOfInt1 = new int[0];
/* 516 */     if (paramArrayOfInt2 == null)
/* 517 */       paramArrayOfInt2 = new int[0];
/* 518 */     for (int i = 0; i < paramArrayOfInt1.length; i++) {
/* 519 */       if ((i >= paramArrayOfInt2.length) || (paramArrayOfInt1[i] > paramArrayOfInt2[i]))
/* 520 */         return 1;
/* 521 */       if (paramArrayOfInt1[i] < paramArrayOfInt2[i])
/* 522 */         return -1;
/*     */     }
/* 524 */     return paramArrayOfInt1.length == paramArrayOfInt2.length ? 0 : -1;
/*     */   }
/*     */ 
/*     */   public static synchronized int compareVersion(String paramString1, String paramString2)
/*     */   {
/* 531 */     return compareVersion(parseVersion(paramString1), parseVersion(paramString2));
/*     */   }
/*     */ 
/*     */   private static String compressClassName(String paramString)
/*     */   {
/* 537 */     String str = "com.sun.corba.se.";
/* 538 */     if (paramString.startsWith(str)) {
/* 539 */       return "(ORB)." + paramString.substring(str.length());
/*     */     }
/* 541 */     return paramString;
/*     */   }
/*     */ 
/*     */   public static String getThreadName(Thread paramThread)
/*     */   {
/* 549 */     if (paramThread == null) {
/* 550 */       return "null";
/*     */     }
/*     */ 
/* 556 */     String str = paramThread.getName();
/* 557 */     StringTokenizer localStringTokenizer = new StringTokenizer(str);
/* 558 */     int i = localStringTokenizer.countTokens();
/* 559 */     if (i != 5) {
/* 560 */       return str;
/*     */     }
/* 562 */     String[] arrayOfString = new String[i];
/* 563 */     for (int j = 0; j < i; j++) {
/* 564 */       arrayOfString[j] = localStringTokenizer.nextToken();
/*     */     }
/* 566 */     if (!arrayOfString[0].equals("SelectReaderThread")) {
/* 567 */       return str;
/*     */     }
/* 569 */     return "SelectReaderThread[" + arrayOfString[2] + ":" + arrayOfString[3] + "]";
/*     */   }
/*     */ 
/*     */   private static String formatStackTraceElement(StackTraceElement paramStackTraceElement)
/*     */   {
/* 574 */     return compressClassName(paramStackTraceElement.getClassName()) + "." + paramStackTraceElement.getMethodName() + (paramStackTraceElement.getFileName() != null ? "(" + paramStackTraceElement.getFileName() + ")" : (paramStackTraceElement.getFileName() != null) && (paramStackTraceElement.getLineNumber() >= 0) ? "(" + paramStackTraceElement.getFileName() + ":" + paramStackTraceElement.getLineNumber() + ")" : paramStackTraceElement.isNativeMethod() ? "(Native Method)" : "(Unknown Source)");
/*     */   }
/*     */ 
/*     */   private static void printStackTrace(StackTraceElement[] paramArrayOfStackTraceElement)
/*     */   {
/* 583 */     System.out.println("    Stack Trace:");
/*     */ 
/* 586 */     for (int i = 1; i < paramArrayOfStackTraceElement.length; i++) {
/* 587 */       System.out.print("        >");
/* 588 */       System.out.println(formatStackTraceElement(paramArrayOfStackTraceElement[i]));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static synchronized void dprint(java.lang.Object paramObject, String paramString)
/*     */   {
/* 596 */     System.out.println(compressClassName(paramObject.getClass().getName()) + "(" + getThreadName(Thread.currentThread()) + "): " + paramString);
/*     */   }
/*     */ 
/*     */   public static synchronized void dprint(String paramString1, String paramString2)
/*     */   {
/* 602 */     System.out.println(compressClassName(paramString1) + "(" + getThreadName(Thread.currentThread()) + "): " + paramString2);
/*     */   }
/*     */ 
/*     */   public synchronized void dprint(String paramString)
/*     */   {
/* 608 */     dprint(this, paramString);
/*     */   }
/*     */ 
/*     */   public static synchronized void dprintTrace(java.lang.Object paramObject, String paramString) {
/* 612 */     dprint(paramObject, paramString);
/*     */ 
/* 614 */     Throwable localThrowable = new Throwable();
/* 615 */     printStackTrace(localThrowable.getStackTrace());
/*     */   }
/*     */ 
/*     */   public static synchronized void dprint(java.lang.Object paramObject, String paramString, Throwable paramThrowable)
/*     */   {
/* 621 */     System.out.println(compressClassName(paramObject.getClass().getName()) + '(' + Thread.currentThread() + "): " + paramString);
/*     */ 
/* 625 */     if (paramThrowable != null)
/* 626 */       printStackTrace(paramThrowable.getStackTrace());
/*     */   }
/*     */ 
/*     */   public static String[] concatenateStringArrays(String[] paramArrayOfString1, String[] paramArrayOfString2)
/*     */   {
/* 631 */     String[] arrayOfString = new String[paramArrayOfString1.length + paramArrayOfString2.length];
/*     */ 
/* 634 */     for (int i = 0; i < paramArrayOfString1.length; i++) {
/* 635 */       arrayOfString[i] = paramArrayOfString1[i];
/*     */     }
/* 637 */     for (i = 0; i < paramArrayOfString2.length; i++) {
/* 638 */       arrayOfString[(i + paramArrayOfString1.length)] = paramArrayOfString2[i];
/*     */     }
/* 640 */     return arrayOfString;
/*     */   }
/*     */ 
/*     */   public static void throwNotSerializableForCorba(String paramString)
/*     */   {
/* 656 */     throw omgWrapper.notSerializable(CompletionStatus.COMPLETED_MAYBE, paramString);
/*     */   }
/*     */ 
/*     */   public static byte getMaxStreamFormatVersion()
/*     */   {
/* 665 */     ValueHandler localValueHandler = Util.createValueHandler();
/*     */ 
/* 667 */     if (!(localValueHandler instanceof ValueHandlerMultiFormat)) {
/* 668 */       return 1;
/*     */     }
/* 670 */     return ((ValueHandlerMultiFormat)localValueHandler).getMaximumStreamFormatVersion();
/*     */   }
/*     */ 
/*     */   public static CorbaClientDelegate makeClientDelegate(IOR paramIOR)
/*     */   {
/* 675 */     ORB localORB = paramIOR.getORB();
/* 676 */     CorbaContactInfoList localCorbaContactInfoList = localORB.getCorbaContactInfoListFactory().create(paramIOR);
/* 677 */     CorbaClientDelegate localCorbaClientDelegate = localORB.getClientDelegateFactory().create(localCorbaContactInfoList);
/* 678 */     return localCorbaClientDelegate;
/*     */   }
/*     */ 
/*     */   public static org.omg.CORBA.Object makeObjectReference(IOR paramIOR)
/*     */   {
/* 685 */     CorbaClientDelegate localCorbaClientDelegate = makeClientDelegate(paramIOR);
/* 686 */     CORBAObjectImpl localCORBAObjectImpl = new CORBAObjectImpl();
/* 687 */     StubAdapter.setDelegate(localCORBAObjectImpl, localCorbaClientDelegate);
/* 688 */     return localCORBAObjectImpl;
/*     */   }
/*     */ 
/*     */   public static IOR getIOR(org.omg.CORBA.Object paramObject)
/*     */   {
/* 704 */     if (paramObject == null) {
/* 705 */       throw wrapper.nullObjectReference();
/*     */     }
/* 707 */     IOR localIOR = null;
/* 708 */     if (StubAdapter.isStub(paramObject)) {
/* 709 */       Delegate localDelegate = StubAdapter.getDelegate(paramObject);
/*     */ 
/* 712 */       if ((localDelegate instanceof CorbaClientDelegate)) {
/* 713 */         CorbaClientDelegate localCorbaClientDelegate = (CorbaClientDelegate)localDelegate;
/* 714 */         ContactInfoList localContactInfoList = localCorbaClientDelegate.getContactInfoList();
/*     */ 
/* 716 */         if ((localContactInfoList instanceof CorbaContactInfoList)) {
/* 717 */           CorbaContactInfoList localCorbaContactInfoList = (CorbaContactInfoList)localContactInfoList;
/* 718 */           localIOR = localCorbaContactInfoList.getTargetIOR();
/* 719 */           if (localIOR == null) {
/* 720 */             throw wrapper.nullIor();
/*     */           }
/* 722 */           return localIOR;
/*     */         }
/*     */ 
/* 730 */         throw new INTERNAL();
/*     */       }
/*     */ 
/* 741 */       throw wrapper.objrefFromForeignOrb();
/*     */     }
/* 743 */     throw wrapper.localObjectNotAllowed();
/*     */   }
/*     */ 
/*     */   public static IOR connectAndGetIOR(ORB paramORB, org.omg.CORBA.Object paramObject)
/*     */   {
/*     */     IOR localIOR;
/*     */     try
/*     */     {
/* 759 */       localIOR = getIOR(paramObject);
/*     */     } catch (BAD_OPERATION localBAD_OPERATION) {
/* 761 */       if (StubAdapter.isStub(paramObject))
/*     */         try {
/* 763 */           StubAdapter.connect(paramObject, paramORB);
/*     */         } catch (RemoteException localRemoteException) {
/* 765 */           throw wrapper.connectingServant(localRemoteException);
/*     */         }
/*     */       else {
/* 768 */         paramORB.connect(paramObject);
/*     */       }
/*     */ 
/* 771 */       localIOR = getIOR(paramObject);
/*     */     }
/*     */ 
/* 774 */     return localIOR;
/*     */   }
/*     */ 
/*     */   public static String operationNameAndRequestId(CorbaMessageMediator paramCorbaMessageMediator)
/*     */   {
/* 779 */     return "op/" + paramCorbaMessageMediator.getOperationName() + " id/" + paramCorbaMessageMediator.getRequestId();
/*     */   }
/*     */ 
/*     */   public static boolean isPrintable(char paramChar)
/*     */   {
/* 784 */     if (Character.isJavaIdentifierStart(paramChar))
/*     */     {
/* 786 */       return true;
/*     */     }
/* 788 */     if (Character.isDigit(paramChar)) {
/* 789 */       return true;
/*     */     }
/* 791 */     switch (Character.getType(paramChar)) { case 27:
/* 792 */       return true;
/*     */     case 20:
/* 793 */       return true;
/*     */     case 25:
/* 794 */       return true;
/*     */     case 24:
/* 795 */       return true;
/*     */     case 21:
/* 796 */       return true;
/*     */     case 22:
/* 797 */       return true;
/*     */     case 23:
/* 799 */     case 26: } return false;
/*     */   }
/*     */ 
/*     */   public static String getClassSecurityInfo(Class paramClass)
/*     */   {
/* 814 */     String str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public java.lang.Object run() {
/* 817 */         StringBuffer localStringBuffer = new StringBuffer(500);
/* 818 */         ProtectionDomain localProtectionDomain = this.val$cl.getProtectionDomain();
/* 819 */         Policy localPolicy = Policy.getPolicy();
/* 820 */         PermissionCollection localPermissionCollection = localPolicy.getPermissions(localProtectionDomain);
/* 821 */         localStringBuffer.append("\nPermissionCollection ");
/* 822 */         localStringBuffer.append(localPermissionCollection.toString());
/*     */ 
/* 825 */         localStringBuffer.append(localProtectionDomain.toString());
/* 826 */         return localStringBuffer.toString();
/*     */       }
/*     */     });
/* 829 */     return str;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 368 */     exceptionClassNames.put("IDL:omg.org/CORBA/BAD_CONTEXT:1.0", "org.omg.CORBA.BAD_CONTEXT");
/*     */ 
/* 370 */     exceptionClassNames.put("IDL:omg.org/CORBA/BAD_INV_ORDER:1.0", "org.omg.CORBA.BAD_INV_ORDER");
/*     */ 
/* 372 */     exceptionClassNames.put("IDL:omg.org/CORBA/BAD_OPERATION:1.0", "org.omg.CORBA.BAD_OPERATION");
/*     */ 
/* 374 */     exceptionClassNames.put("IDL:omg.org/CORBA/BAD_PARAM:1.0", "org.omg.CORBA.BAD_PARAM");
/*     */ 
/* 376 */     exceptionClassNames.put("IDL:omg.org/CORBA/BAD_TYPECODE:1.0", "org.omg.CORBA.BAD_TYPECODE");
/*     */ 
/* 378 */     exceptionClassNames.put("IDL:omg.org/CORBA/COMM_FAILURE:1.0", "org.omg.CORBA.COMM_FAILURE");
/*     */ 
/* 380 */     exceptionClassNames.put("IDL:omg.org/CORBA/DATA_CONVERSION:1.0", "org.omg.CORBA.DATA_CONVERSION");
/*     */ 
/* 382 */     exceptionClassNames.put("IDL:omg.org/CORBA/IMP_LIMIT:1.0", "org.omg.CORBA.IMP_LIMIT");
/*     */ 
/* 384 */     exceptionClassNames.put("IDL:omg.org/CORBA/INTF_REPOS:1.0", "org.omg.CORBA.INTF_REPOS");
/*     */ 
/* 386 */     exceptionClassNames.put("IDL:omg.org/CORBA/INTERNAL:1.0", "org.omg.CORBA.INTERNAL");
/*     */ 
/* 388 */     exceptionClassNames.put("IDL:omg.org/CORBA/INV_FLAG:1.0", "org.omg.CORBA.INV_FLAG");
/*     */ 
/* 390 */     exceptionClassNames.put("IDL:omg.org/CORBA/INV_IDENT:1.0", "org.omg.CORBA.INV_IDENT");
/*     */ 
/* 392 */     exceptionClassNames.put("IDL:omg.org/CORBA/INV_OBJREF:1.0", "org.omg.CORBA.INV_OBJREF");
/*     */ 
/* 394 */     exceptionClassNames.put("IDL:omg.org/CORBA/MARSHAL:1.0", "org.omg.CORBA.MARSHAL");
/*     */ 
/* 396 */     exceptionClassNames.put("IDL:omg.org/CORBA/NO_MEMORY:1.0", "org.omg.CORBA.NO_MEMORY");
/*     */ 
/* 398 */     exceptionClassNames.put("IDL:omg.org/CORBA/FREE_MEM:1.0", "org.omg.CORBA.FREE_MEM");
/*     */ 
/* 400 */     exceptionClassNames.put("IDL:omg.org/CORBA/NO_IMPLEMENT:1.0", "org.omg.CORBA.NO_IMPLEMENT");
/*     */ 
/* 402 */     exceptionClassNames.put("IDL:omg.org/CORBA/NO_PERMISSION:1.0", "org.omg.CORBA.NO_PERMISSION");
/*     */ 
/* 404 */     exceptionClassNames.put("IDL:omg.org/CORBA/NO_RESOURCES:1.0", "org.omg.CORBA.NO_RESOURCES");
/*     */ 
/* 406 */     exceptionClassNames.put("IDL:omg.org/CORBA/NO_RESPONSE:1.0", "org.omg.CORBA.NO_RESPONSE");
/*     */ 
/* 408 */     exceptionClassNames.put("IDL:omg.org/CORBA/OBJ_ADAPTER:1.0", "org.omg.CORBA.OBJ_ADAPTER");
/*     */ 
/* 410 */     exceptionClassNames.put("IDL:omg.org/CORBA/INITIALIZE:1.0", "org.omg.CORBA.INITIALIZE");
/*     */ 
/* 412 */     exceptionClassNames.put("IDL:omg.org/CORBA/PERSIST_STORE:1.0", "org.omg.CORBA.PERSIST_STORE");
/*     */ 
/* 414 */     exceptionClassNames.put("IDL:omg.org/CORBA/TRANSIENT:1.0", "org.omg.CORBA.TRANSIENT");
/*     */ 
/* 416 */     exceptionClassNames.put("IDL:omg.org/CORBA/UNKNOWN:1.0", "org.omg.CORBA.UNKNOWN");
/*     */ 
/* 418 */     exceptionClassNames.put("IDL:omg.org/CORBA/OBJECT_NOT_EXIST:1.0", "org.omg.CORBA.OBJECT_NOT_EXIST");
/*     */ 
/* 422 */     exceptionClassNames.put("IDL:omg.org/CORBA/INVALID_TRANSACTION:1.0", "org.omg.CORBA.INVALID_TRANSACTION");
/*     */ 
/* 424 */     exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_REQUIRED:1.0", "org.omg.CORBA.TRANSACTION_REQUIRED");
/*     */ 
/* 426 */     exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_ROLLEDBACK:1.0", "org.omg.CORBA.TRANSACTION_ROLLEDBACK");
/*     */ 
/* 430 */     exceptionClassNames.put("IDL:omg.org/CORBA/INV_POLICY:1.0", "org.omg.CORBA.INV_POLICY");
/*     */ 
/* 434 */     exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_UNAVAILABLE:1.0", "org.omg.CORBA.TRANSACTION_UNAVAILABLE");
/*     */ 
/* 437 */     exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_MODE:1.0", "org.omg.CORBA.TRANSACTION_MODE");
/*     */ 
/* 441 */     exceptionClassNames.put("IDL:omg.org/CORBA/CODESET_INCOMPATIBLE:1.0", "org.omg.CORBA.CODESET_INCOMPATIBLE");
/*     */ 
/* 443 */     exceptionClassNames.put("IDL:omg.org/CORBA/REBIND:1.0", "org.omg.CORBA.REBIND");
/*     */ 
/* 445 */     exceptionClassNames.put("IDL:omg.org/CORBA/TIMEOUT:1.0", "org.omg.CORBA.TIMEOUT");
/*     */ 
/* 447 */     exceptionClassNames.put("IDL:omg.org/CORBA/BAD_QOS:1.0", "org.omg.CORBA.BAD_QOS");
/*     */ 
/* 451 */     exceptionClassNames.put("IDL:omg.org/CORBA/INVALID_ACTIVITY:1.0", "org.omg.CORBA.INVALID_ACTIVITY");
/*     */ 
/* 453 */     exceptionClassNames.put("IDL:omg.org/CORBA/ACTIVITY_COMPLETED:1.0", "org.omg.CORBA.ACTIVITY_COMPLETED");
/*     */ 
/* 455 */     exceptionClassNames.put("IDL:omg.org/CORBA/ACTIVITY_REQUIRED:1.0", "org.omg.CORBA.ACTIVITY_REQUIRED");
/*     */ 
/* 461 */     Enumeration localEnumeration = exceptionClassNames.keys();
/*     */     try
/*     */     {
/* 467 */       while (localEnumeration.hasMoreElements()) {
/* 468 */         java.lang.Object localObject = localEnumeration.nextElement();
/* 469 */         String str1 = (String)localObject;
/* 470 */         String str2 = (String)exceptionClassNames.get(str1);
/* 471 */         exceptionRepositoryIds.put(str2, str1);
/*     */       }
/*     */     }
/*     */     catch (NoSuchElementException localNoSuchElementException)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.orbutil.ORBUtility
 * JD-Core Version:    0.6.2
 */