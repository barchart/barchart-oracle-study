/*     */ package javax.activation;
/*     */ 
/*     */ public abstract class CommandMap
/*     */ {
/*  40 */   private static CommandMap defaultCommandMap = null;
/*     */ 
/*     */   public static CommandMap getDefaultCommandMap()
/*     */   {
/*  60 */     if (defaultCommandMap == null) {
/*  61 */       defaultCommandMap = new MailcapCommandMap();
/*     */     }
/*  63 */     return defaultCommandMap;
/*     */   }
/*     */ 
/*     */   public static void setDefaultCommandMap(CommandMap commandMap)
/*     */   {
/*  75 */     SecurityManager security = System.getSecurityManager();
/*  76 */     if (security != null)
/*     */       try
/*     */       {
/*  79 */         security.checkSetFactory();
/*     */       }
/*     */       catch (SecurityException ex)
/*     */       {
/*  84 */         if (CommandMap.class.getClassLoader() != commandMap.getClass().getClassLoader())
/*     */         {
/*  86 */           throw ex;
/*     */         }
/*     */       }
/*  89 */     defaultCommandMap = commandMap;
/*     */   }
/*     */ 
/*     */   public abstract CommandInfo[] getPreferredCommands(String paramString);
/*     */ 
/*     */   public CommandInfo[] getPreferredCommands(String mimeType, DataSource ds)
/*     */   {
/* 117 */     return getPreferredCommands(mimeType);
/*     */   }
/*     */ 
/*     */   public abstract CommandInfo[] getAllCommands(String paramString);
/*     */ 
/*     */   public CommandInfo[] getAllCommands(String mimeType, DataSource ds)
/*     */   {
/* 145 */     return getAllCommands(mimeType);
/*     */   }
/*     */ 
/*     */   public abstract CommandInfo getCommand(String paramString1, String paramString2);
/*     */ 
/*     */   public CommandInfo getCommand(String mimeType, String cmdName, DataSource ds)
/*     */   {
/* 174 */     return getCommand(mimeType, cmdName);
/*     */   }
/*     */ 
/*     */   public abstract DataContentHandler createDataContentHandler(String paramString);
/*     */ 
/*     */   public DataContentHandler createDataContentHandler(String mimeType, DataSource ds)
/*     */   {
/* 206 */     return createDataContentHandler(mimeType);
/*     */   }
/*     */ 
/*     */   public String[] getMimeTypes()
/*     */   {
/* 218 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.activation.CommandMap
 * JD-Core Version:    0.6.2
 */