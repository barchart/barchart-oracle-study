/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.security.AccessController;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Date;
/*     */ import java.util.logging.Formatter;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogManager;
/*     */ import java.util.logging.LogRecord;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ public class XAWTFormatter extends Formatter
/*     */ {
/*  38 */   Date dat = new Date();
/*     */   private static final String format = "{0,date} {0,time}";
/*     */   private MessageFormat formatter;
/*  42 */   private Object[] args = new Object[1];
/*     */ 
/*  46 */   private String lineSeparator = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
/*     */ 
/*  49 */   boolean displayFullRecord = false;
/*  50 */   boolean useANSI = false;
/*  51 */   boolean showDate = true;
/*  52 */   boolean showLevel = true;
/*  53 */   boolean swapMethodClass = false;
/*     */ 
/*  55 */   public XAWTFormatter() { this.displayFullRecord = "true".equals(LogManager.getLogManager().getProperty("XAWTFormatter.displayFullRecord"));
/*  56 */     this.useANSI = "true".equals(LogManager.getLogManager().getProperty("XAWTFormatter.useANSI"));
/*  57 */     this.showDate = (!"false".equals(LogManager.getLogManager().getProperty("XAWTFormatter.showDate")));
/*  58 */     this.showLevel = (!"false".equals(LogManager.getLogManager().getProperty("XAWTFormatter.showLevel")));
/*  59 */     this.swapMethodClass = "true".equals(LogManager.getLogManager().getProperty("XAWTFormatter.swapMethodClass"));
/*     */   }
/*     */ 
/*     */   public synchronized String format(LogRecord paramLogRecord)
/*     */   {
/*  68 */     StringBuffer localStringBuffer = new StringBuffer();
/*  69 */     if (this.useANSI) {
/*  70 */       localObject = paramLogRecord.getLevel();
/*  71 */       if (Level.FINEST.equals(localObject))
/*  72 */         localStringBuffer.append("\033[36m");
/*  73 */       else if (Level.FINER.equals(localObject))
/*  74 */         localStringBuffer.append("\033[32m");
/*  75 */       else if (Level.FINE.equals(localObject)) {
/*  76 */         localStringBuffer.append("\033[34m");
/*     */       }
/*     */     }
/*  79 */     if (this.displayFullRecord) {
/*  80 */       if (this.showDate)
/*     */       {
/*  82 */         this.dat.setTime(paramLogRecord.getMillis());
/*  83 */         this.args[0] = this.dat;
/*  84 */         localObject = new StringBuffer();
/*  85 */         if (this.formatter == null) {
/*  86 */           this.formatter = new MessageFormat("{0,date} {0,time}");
/*     */         }
/*  88 */         this.formatter.format(this.args, (StringBuffer)localObject, null);
/*  89 */         localStringBuffer.append((StringBuffer)localObject);
/*  90 */         localStringBuffer.append(" ");
/*     */       } else {
/*  92 */         localStringBuffer.append("    ");
/*     */       }
/*  94 */       if (this.swapMethodClass) {
/*  95 */         if (paramLogRecord.getSourceMethodName() != null) {
/*  96 */           localStringBuffer.append(" \033[35m");
/*  97 */           localStringBuffer.append(paramLogRecord.getSourceMethodName());
/*  98 */           localStringBuffer.append("\033[30m ");
/*     */         }
/* 100 */         if (paramLogRecord.getSourceClassName() != null)
/* 101 */           localStringBuffer.append(paramLogRecord.getSourceClassName());
/*     */         else
/* 103 */           localStringBuffer.append(paramLogRecord.getLoggerName());
/*     */       }
/*     */       else {
/* 106 */         if (paramLogRecord.getSourceClassName() != null)
/* 107 */           localStringBuffer.append(paramLogRecord.getSourceClassName());
/*     */         else {
/* 109 */           localStringBuffer.append(paramLogRecord.getLoggerName());
/*     */         }
/* 111 */         if (paramLogRecord.getSourceMethodName() != null) {
/* 112 */           localStringBuffer.append(" \033[35m");
/* 113 */           localStringBuffer.append(paramLogRecord.getSourceMethodName());
/* 114 */           localStringBuffer.append("\033[30m");
/*     */         }
/*     */       }
/* 117 */       localStringBuffer.append(this.lineSeparator);
/*     */     }
/* 119 */     if (this.useANSI) {
/* 120 */       localObject = paramLogRecord.getLevel();
/* 121 */       if (Level.FINEST.equals(localObject))
/* 122 */         localStringBuffer.append("\033[36m");
/* 123 */       else if (Level.FINER.equals(localObject))
/* 124 */         localStringBuffer.append("\033[32m");
/* 125 */       else if (Level.FINE.equals(localObject)) {
/* 126 */         localStringBuffer.append("\033[34m");
/*     */       }
/*     */     }
/* 129 */     if (this.showLevel) {
/* 130 */       localStringBuffer.append(paramLogRecord.getLevel().getLocalizedName());
/* 131 */       localStringBuffer.append(": ");
/*     */     }
/* 133 */     Object localObject = formatMessage(paramLogRecord);
/* 134 */     localStringBuffer.append((String)localObject);
/* 135 */     localStringBuffer.append(this.lineSeparator);
/* 136 */     if (paramLogRecord.getThrown() != null)
/*     */       try {
/* 138 */         StringWriter localStringWriter = new StringWriter();
/* 139 */         PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
/* 140 */         paramLogRecord.getThrown().printStackTrace(localPrintWriter);
/* 141 */         localPrintWriter.close();
/* 142 */         localStringBuffer.append(localStringWriter.toString());
/*     */       }
/*     */       catch (Exception localException) {
/*     */       }
/* 146 */     if (this.useANSI) {
/* 147 */       localStringBuffer.append("\033[30m");
/*     */     }
/* 149 */     return localStringBuffer.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XAWTFormatter
 * JD-Core Version:    0.6.2
 */