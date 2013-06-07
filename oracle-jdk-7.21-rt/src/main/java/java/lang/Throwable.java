/*      */ package java.lang;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.Serializable;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ 
/*      */ public class Throwable
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = -3042686055658047285L;
/*      */   private transient Object backtrace;
/*      */   private String detailMessage;
/*  159 */   private static final StackTraceElement[] UNASSIGNED_STACK = new StackTraceElement[0];
/*      */ 
/*  197 */   private Throwable cause = this;
/*      */ 
/*  210 */   private StackTraceElement[] stackTrace = UNASSIGNED_STACK;
/*      */ 
/*  214 */   private static final List<Throwable> SUPPRESSED_SENTINEL = Collections.unmodifiableList(new ArrayList(0));
/*      */ 
/*  227 */   private List<Throwable> suppressedExceptions = SUPPRESSED_SENTINEL;
/*      */   private static final String NULL_CAUSE_MESSAGE = "Cannot suppress a null exception.";
/*      */   private static final String SELF_SUPPRESSION_MESSAGE = "Self-suppression not permitted";
/*      */   private static final String CAUSE_CAPTION = "Caused by: ";
/*      */   private static final String SUPPRESSED_CAPTION = "Suppressed: ";
/* 1056 */   private static final Throwable[] EMPTY_THROWABLE_ARRAY = new Throwable[0];
/*      */ 
/*      */   public Throwable()
/*      */   {
/*  250 */     fillInStackTrace();
/*      */   }
/*      */ 
/*      */   public Throwable(String paramString)
/*      */   {
/*  265 */     fillInStackTrace();
/*  266 */     this.detailMessage = paramString;
/*      */   }
/*      */ 
/*      */   public Throwable(String paramString, Throwable paramThrowable)
/*      */   {
/*  287 */     fillInStackTrace();
/*  288 */     this.detailMessage = paramString;
/*  289 */     this.cause = paramThrowable;
/*      */   }
/*      */ 
/*      */   public Throwable(Throwable paramThrowable)
/*      */   {
/*  310 */     fillInStackTrace();
/*  311 */     this.detailMessage = (paramThrowable == null ? null : paramThrowable.toString());
/*  312 */     this.cause = paramThrowable;
/*      */   }
/*      */ 
/*      */   protected Throwable(String paramString, Throwable paramThrowable, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  359 */     if (paramBoolean2)
/*  360 */       fillInStackTrace();
/*      */     else {
/*  362 */       this.stackTrace = null;
/*      */     }
/*  364 */     this.detailMessage = paramString;
/*  365 */     this.cause = paramThrowable;
/*  366 */     if (!paramBoolean1)
/*  367 */       this.suppressedExceptions = null;
/*      */   }
/*      */ 
/*      */   public String getMessage()
/*      */   {
/*  377 */     return this.detailMessage;
/*      */   }
/*      */ 
/*      */   public String getLocalizedMessage()
/*      */   {
/*  391 */     return getMessage();
/*      */   }
/*      */ 
/*      */   public synchronized Throwable getCause()
/*      */   {
/*  415 */     return this.cause == this ? null : this.cause;
/*      */   }
/*      */ 
/*      */   public synchronized Throwable initCause(Throwable paramThrowable)
/*      */   {
/*  455 */     if (this.cause != this)
/*  456 */       throw new IllegalStateException("Can't overwrite cause");
/*  457 */     if (paramThrowable == this)
/*  458 */       throw new IllegalArgumentException("Self-causation not permitted");
/*  459 */     this.cause = paramThrowable;
/*  460 */     return this;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  478 */     String str1 = getClass().getName();
/*  479 */     String str2 = getLocalizedMessage();
/*  480 */     return str2 != null ? str1 + ": " + str2 : str1;
/*      */   }
/*      */ 
/*      */   public void printStackTrace()
/*      */   {
/*  633 */     printStackTrace(System.err);
/*      */   }
/*      */ 
/*      */   public void printStackTrace(PrintStream paramPrintStream)
/*      */   {
/*  642 */     printStackTrace(new WrappedPrintStream(paramPrintStream));
/*      */   }
/*      */ 
/*      */   private void printStackTrace(PrintStreamOrWriter paramPrintStreamOrWriter)
/*      */   {
/*  648 */     Set localSet = Collections.newSetFromMap(new IdentityHashMap());
/*      */ 
/*  650 */     localSet.add(this);
/*      */ 
/*  652 */     synchronized (paramPrintStreamOrWriter.lock())
/*      */     {
/*  654 */       paramPrintStreamOrWriter.println(this);
/*  655 */       StackTraceElement[] arrayOfStackTraceElement = getOurStackTrace();
/*      */       Object localObject2;
/*  656 */       for (localObject2 : arrayOfStackTraceElement) {
/*  657 */         paramPrintStreamOrWriter.println("\tat " + localObject2);
/*      */       }
/*      */ 
/*  660 */       for (localObject2 : getSuppressed()) {
/*  661 */         localObject2.printEnclosedStackTrace(paramPrintStreamOrWriter, arrayOfStackTraceElement, "Suppressed: ", "\t", localSet);
/*      */       }
/*      */ 
/*  664 */       ??? = getCause();
/*  665 */       if (??? != null)
/*  666 */         ((Throwable)???).printEnclosedStackTrace(paramPrintStreamOrWriter, arrayOfStackTraceElement, "Caused by: ", "", localSet);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void printEnclosedStackTrace(PrintStreamOrWriter paramPrintStreamOrWriter, StackTraceElement[] paramArrayOfStackTraceElement, String paramString1, String paramString2, Set<Throwable> paramSet)
/*      */   {
/*  679 */     assert (Thread.holdsLock(paramPrintStreamOrWriter.lock()));
/*  680 */     if (paramSet.contains(this)) {
/*  681 */       paramPrintStreamOrWriter.println("\t[CIRCULAR REFERENCE:" + this + "]");
/*      */     } else {
/*  683 */       paramSet.add(this);
/*      */ 
/*  685 */       StackTraceElement[] arrayOfStackTraceElement = getOurStackTrace();
/*  686 */       int i = arrayOfStackTraceElement.length - 1;
/*  687 */       for (int j = paramArrayOfStackTraceElement.length - 1; 
/*  688 */         (i >= 0) && (j >= 0) && (arrayOfStackTraceElement[i].equals(paramArrayOfStackTraceElement[j])); 
/*  689 */         j--) i--;
/*      */ 
/*  691 */       int k = arrayOfStackTraceElement.length - 1 - i;
/*      */ 
/*  694 */       paramPrintStreamOrWriter.println(paramString2 + paramString1 + this);
/*  695 */       for (int m = 0; m <= i; m++)
/*  696 */         paramPrintStreamOrWriter.println(paramString2 + "\tat " + arrayOfStackTraceElement[m]);
/*  697 */       if (k != 0) {
/*  698 */         paramPrintStreamOrWriter.println(paramString2 + "\t... " + k + " more");
/*      */       }
/*      */ 
/*  701 */       for (Object localObject2 : getSuppressed()) {
/*  702 */         localObject2.printEnclosedStackTrace(paramPrintStreamOrWriter, arrayOfStackTraceElement, "Suppressed: ", paramString2 + "\t", paramSet);
/*      */       }
/*      */ 
/*  706 */       ??? = getCause();
/*  707 */       if (??? != null)
/*  708 */         ((Throwable)???).printEnclosedStackTrace(paramPrintStreamOrWriter, arrayOfStackTraceElement, "Caused by: ", paramString2, paramSet);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void printStackTrace(PrintWriter paramPrintWriter)
/*      */   {
/*  720 */     printStackTrace(new WrappedPrintWriter(paramPrintWriter));
/*      */   }
/*      */ 
/*      */   public synchronized Throwable fillInStackTrace()
/*      */   {
/*  780 */     if ((this.stackTrace != null) || (this.backtrace != null))
/*      */     {
/*  782 */       fillInStackTrace(0);
/*  783 */       this.stackTrace = UNASSIGNED_STACK;
/*      */     }
/*  785 */     return this;
/*      */   }
/*      */ 
/*      */   private native Throwable fillInStackTrace(int paramInt);
/*      */ 
/*      */   public StackTraceElement[] getStackTrace()
/*      */   {
/*  815 */     return (StackTraceElement[])getOurStackTrace().clone();
/*      */   }
/*      */ 
/*      */   private synchronized StackTraceElement[] getOurStackTrace()
/*      */   {
/*  821 */     if ((this.stackTrace == UNASSIGNED_STACK) || ((this.stackTrace == null) && (this.backtrace != null)))
/*      */     {
/*  823 */       int i = getStackTraceDepth();
/*  824 */       this.stackTrace = new StackTraceElement[i];
/*  825 */       for (int j = 0; j < i; j++)
/*  826 */         this.stackTrace[j] = getStackTraceElement(j);
/*  827 */     } else if (this.stackTrace == null) {
/*  828 */       return UNASSIGNED_STACK;
/*      */     }
/*  830 */     return this.stackTrace;
/*      */   }
/*      */ 
/*      */   public void setStackTrace(StackTraceElement[] paramArrayOfStackTraceElement)
/*      */   {
/*  863 */     StackTraceElement[] arrayOfStackTraceElement = (StackTraceElement[])paramArrayOfStackTraceElement.clone();
/*  864 */     for (int i = 0; i < arrayOfStackTraceElement.length; i++) {
/*  865 */       if (arrayOfStackTraceElement[i] == null) {
/*  866 */         throw new NullPointerException("stackTrace[" + i + "]");
/*      */       }
/*      */     }
/*  869 */     synchronized (this) {
/*  870 */       if ((this.stackTrace == null) && (this.backtrace == null))
/*      */       {
/*  872 */         return;
/*  873 */       }this.stackTrace = arrayOfStackTraceElement;
/*      */     }
/*      */   }
/*      */ 
/*      */   native int getStackTraceDepth();
/*      */ 
/*      */   native StackTraceElement getStackTraceElement(int paramInt);
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/*  913 */     paramObjectInputStream.defaultReadObject();
/*      */     Object localObject1;
/*  914 */     if (this.suppressedExceptions != null) {
/*  915 */       localObject1 = null;
/*  916 */       if (this.suppressedExceptions.isEmpty())
/*      */       {
/*  918 */         localObject1 = SUPPRESSED_SENTINEL;
/*      */       } else {
/*  920 */         localObject1 = new ArrayList(1);
/*  921 */         for (Throwable localThrowable : this.suppressedExceptions)
/*      */         {
/*  924 */           if (localThrowable == null)
/*  925 */             throw new NullPointerException("Cannot suppress a null exception.");
/*  926 */           if (localThrowable == this)
/*  927 */             throw new IllegalArgumentException("Self-suppression not permitted");
/*  928 */           ((List)localObject1).add(localThrowable);
/*      */         }
/*      */       }
/*  931 */       this.suppressedExceptions = ((List)localObject1);
/*      */     }
/*      */ 
/*  943 */     if (this.stackTrace != null) {
/*  944 */       if (this.stackTrace.length == 0)
/*  945 */         this.stackTrace = ((StackTraceElement[])UNASSIGNED_STACK.clone());
/*  946 */       else if ((this.stackTrace.length == 1) && (SentinelHolder.STACK_TRACE_ELEMENT_SENTINEL.equals(this.stackTrace[0])))
/*      */       {
/*  949 */         this.stackTrace = null;
/*      */       }
/*  951 */       else for (Object localObject2 : this.stackTrace) {
/*  952 */           if (localObject2 == null) {
/*  953 */             throw new NullPointerException("null StackTraceElement in serial stream. ");
/*      */           }
/*      */         }
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  961 */       this.stackTrace = ((StackTraceElement[])UNASSIGNED_STACK.clone());
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */     throws IOException
/*      */   {
/*  978 */     getOurStackTrace();
/*      */ 
/*  980 */     StackTraceElement[] arrayOfStackTraceElement = this.stackTrace;
/*      */     try {
/*  982 */       if (this.stackTrace == null)
/*  983 */         this.stackTrace = SentinelHolder.STACK_TRACE_SENTINEL;
/*  984 */       paramObjectOutputStream.defaultWriteObject();
/*      */     } finally {
/*  986 */       this.stackTrace = arrayOfStackTraceElement;
/*      */     }
/*      */   }
/*      */ 
/*      */   public final synchronized void addSuppressed(Throwable paramThrowable)
/*      */   {
/* 1041 */     if (paramThrowable == this) {
/* 1042 */       throw new IllegalArgumentException("Self-suppression not permitted");
/*      */     }
/* 1044 */     if (paramThrowable == null) {
/* 1045 */       throw new NullPointerException("Cannot suppress a null exception.");
/*      */     }
/* 1047 */     if (this.suppressedExceptions == null) {
/* 1048 */       return;
/*      */     }
/* 1050 */     if (this.suppressedExceptions == SUPPRESSED_SENTINEL) {
/* 1051 */       this.suppressedExceptions = new ArrayList(1);
/*      */     }
/* 1053 */     this.suppressedExceptions.add(paramThrowable);
/*      */   }
/*      */ 
/*      */   public final synchronized Throwable[] getSuppressed()
/*      */   {
/* 1074 */     if ((this.suppressedExceptions == SUPPRESSED_SENTINEL) || (this.suppressedExceptions == null))
/*      */     {
/* 1076 */       return EMPTY_THROWABLE_ARRAY;
/*      */     }
/* 1078 */     return (Throwable[])this.suppressedExceptions.toArray(EMPTY_THROWABLE_ARRAY);
/*      */   }
/*      */ 
/*      */   private static class WrappedPrintWriter extends Throwable.PrintStreamOrWriter
/*      */   {
/*      */     private final PrintWriter printWriter;
/*      */ 
/*      */     WrappedPrintWriter(PrintWriter paramPrintWriter)
/*      */     {
/*  754 */       super();
/*  755 */       this.printWriter = paramPrintWriter;
/*      */     }
/*      */ 
/*      */     Object lock() {
/*  759 */       return this.printWriter;
/*      */     }
/*      */ 
/*      */     void println(Object paramObject) {
/*  763 */       this.printWriter.println(paramObject);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class WrappedPrintStream extends Throwable.PrintStreamOrWriter
/*      */   {
/*      */     private final PrintStream printStream;
/*      */ 
/*      */     WrappedPrintStream(PrintStream paramPrintStream)
/*      */     {
/*  738 */       super();
/*  739 */       this.printStream = paramPrintStream;
/*      */     }
/*      */ 
/*      */     Object lock() {
/*  743 */       return this.printStream;
/*      */     }
/*      */ 
/*      */     void println(Object paramObject) {
/*  747 */       this.printStream.println(paramObject);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static abstract class PrintStreamOrWriter
/*      */   {
/*      */     abstract Object lock();
/*      */ 
/*      */     abstract void println(Object paramObject);
/*      */   }
/*      */ 
/*      */   private static class SentinelHolder
/*      */   {
/*  145 */     public static final StackTraceElement STACK_TRACE_ELEMENT_SENTINEL = new StackTraceElement("", "", null, -2147483648);
/*      */ 
/*  152 */     public static final StackTraceElement[] STACK_TRACE_SENTINEL = { STACK_TRACE_ELEMENT_SENTINEL };
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.Throwable
 * JD-Core Version:    0.6.2
 */