/*     */ package sun.management;
/*     */ 
/*     */ import com.sun.management.HotSpotDiagnosticMXBean;
/*     */ import com.sun.management.VMOption;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.management.ObjectName;
/*     */ 
/*     */ public class HotSpotDiagnostic
/*     */   implements HotSpotDiagnosticMXBean
/*     */ {
/*     */   public native void dumpHeap(String paramString, boolean paramBoolean)
/*     */     throws IOException;
/*     */ 
/*     */   public List<VMOption> getDiagnosticOptions()
/*     */   {
/*  46 */     List localList = Flag.getAllFlags();
/*  47 */     ArrayList localArrayList = new ArrayList();
/*  48 */     for (Flag localFlag : localList) {
/*  49 */       if ((localFlag.isWriteable()) && (localFlag.isExternal())) {
/*  50 */         localArrayList.add(localFlag.getVMOption());
/*     */       }
/*     */     }
/*  53 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   public VMOption getVMOption(String paramString) {
/*  57 */     if (paramString == null) {
/*  58 */       throw new NullPointerException("name cannot be null");
/*     */     }
/*     */ 
/*  61 */     Flag localFlag = Flag.getFlag(paramString);
/*  62 */     if (localFlag == null) {
/*  63 */       throw new IllegalArgumentException("VM option \"" + paramString + "\" does not exist");
/*     */     }
/*     */ 
/*  66 */     return localFlag.getVMOption();
/*     */   }
/*     */ 
/*     */   public void setVMOption(String paramString1, String paramString2) {
/*  70 */     if (paramString1 == null) {
/*  71 */       throw new NullPointerException("name cannot be null");
/*     */     }
/*  73 */     if (paramString2 == null) {
/*  74 */       throw new NullPointerException("value cannot be null");
/*     */     }
/*     */ 
/*  77 */     Util.checkControlAccess();
/*  78 */     Flag localFlag = Flag.getFlag(paramString1);
/*  79 */     if (localFlag == null) {
/*  80 */       throw new IllegalArgumentException("VM option \"" + paramString1 + "\" does not exist");
/*     */     }
/*     */ 
/*  83 */     if (!localFlag.isWriteable()) {
/*  84 */       throw new IllegalArgumentException("VM Option \"" + paramString1 + "\" is not writeable");
/*     */     }
/*     */ 
/*  89 */     Object localObject = localFlag.getValue();
/*  90 */     if ((localObject instanceof Long)) {
/*     */       try {
/*  92 */         long l = Long.parseLong(paramString2);
/*  93 */         Flag.setLongValue(paramString1, l);
/*     */       } catch (NumberFormatException localNumberFormatException) {
/*  95 */         IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException("Invalid value: VM Option \"" + paramString1 + "\"" + " expects numeric value");
/*     */ 
/*  99 */         localIllegalArgumentException.initCause(localNumberFormatException);
/* 100 */         throw localIllegalArgumentException;
/*     */       }
/* 102 */     } else if ((localObject instanceof Boolean)) {
/* 103 */       if ((!paramString2.equalsIgnoreCase("true")) && (!paramString2.equalsIgnoreCase("false")))
/*     */       {
/* 105 */         throw new IllegalArgumentException("Invalid value: VM Option \"" + paramString1 + "\"" + " expects \"true\" or \"false\".");
/*     */       }
/*     */ 
/* 109 */       Flag.setBooleanValue(paramString1, Boolean.parseBoolean(paramString2));
/* 110 */     } else if ((localObject instanceof String)) {
/* 111 */       Flag.setStringValue(paramString1, paramString2);
/*     */     } else {
/* 113 */       throw new IllegalArgumentException("VM Option \"" + paramString1 + "\" is of an unsupported type: " + localObject.getClass().getName());
/*     */     }
/*     */   }
/*     */ 
/*     */   public ObjectName getObjectName()
/*     */   {
/* 120 */     return Util.newObjectName("com.sun.management:type=HotSpotDiagnostic");
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.management.HotSpotDiagnostic
 * JD-Core Version:    0.6.2
 */