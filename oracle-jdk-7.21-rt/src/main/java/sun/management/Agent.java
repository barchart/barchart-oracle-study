/*     */ package sun.management;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.ThreadMXBean;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.Properties;
/*     */ import java.util.ResourceBundle;
/*     */ import javax.management.remote.JMXConnectorServer;
/*     */ import javax.management.remote.JMXServiceURL;
/*     */ import sun.management.jmxremote.ConnectorBootstrap;
/*     */ import sun.misc.VMSupport;
/*     */ 
/*     */ public class Agent
/*     */ {
/*     */   private static Properties mgmtProps;
/*     */   private static ResourceBundle messageRB;
/*     */   private static final String CONFIG_FILE = "com.sun.management.config.file";
/*     */   private static final String SNMP_PORT = "com.sun.management.snmp.port";
/*     */   private static final String JMXREMOTE = "com.sun.management.jmxremote";
/*     */   private static final String JMXREMOTE_PORT = "com.sun.management.jmxremote.port";
/*     */   private static final String ENABLE_THREAD_CONTENTION_MONITORING = "com.sun.management.enableThreadContentionMonitoring";
/*     */   private static final String LOCAL_CONNECTOR_ADDRESS_PROP = "com.sun.management.jmxremote.localConnectorAddress";
/*     */   private static final String SNMP_ADAPTOR_BOOTSTRAP_CLASS_NAME = "sun.management.snmp.AdaptorBootstrap";
/*  79 */   private static JMXConnectorServer jmxServer = null;
/*     */ 
/*     */   private static Properties parseString(String paramString)
/*     */   {
/*  85 */     Properties localProperties = new Properties();
/*  86 */     if (paramString != null) {
/*  87 */       for (String str1 : paramString.split(",")) {
/*  88 */         String[] arrayOfString2 = str1.split("=", 2);
/*  89 */         String str2 = arrayOfString2[0].trim();
/*  90 */         String str3 = arrayOfString2.length > 1 ? arrayOfString2[1].trim() : "";
/*     */ 
/*  92 */         if (!str2.startsWith("com.sun.management.")) {
/*  93 */           error("agent.err.invalid.option", str2);
/*     */         }
/*     */ 
/*  96 */         localProperties.setProperty(str2, str3);
/*     */       }
/*     */     }
/*     */ 
/* 100 */     return localProperties;
/*     */   }
/*     */ 
/*     */   public static void premain(String paramString)
/*     */     throws Exception
/*     */   {
/* 106 */     agentmain(paramString);
/*     */   }
/*     */ 
/*     */   public static void agentmain(String paramString) throws Exception
/*     */   {
/* 111 */     if ((paramString == null) || (paramString.length() == 0)) {
/* 112 */       paramString = "com.sun.management.jmxremote";
/*     */     }
/*     */ 
/* 115 */     Properties localProperties1 = parseString(paramString);
/*     */ 
/* 118 */     Properties localProperties2 = new Properties();
/* 119 */     String str = localProperties1.getProperty("com.sun.management.config.file");
/* 120 */     readConfiguration(str, localProperties2);
/*     */ 
/* 123 */     localProperties2.putAll(localProperties1);
/* 124 */     startAgent(localProperties2);
/*     */   }
/*     */ 
/*     */   private static synchronized void startLocalManagementAgent()
/*     */   {
/* 130 */     Properties localProperties = VMSupport.getAgentProperties();
/*     */ 
/* 133 */     if (localProperties.get("com.sun.management.jmxremote.localConnectorAddress") == null) {
/* 134 */       JMXConnectorServer localJMXConnectorServer = ConnectorBootstrap.startLocalConnectorServer();
/* 135 */       String str = localJMXConnectorServer.getAddress().toString();
/*     */ 
/* 137 */       localProperties.put("com.sun.management.jmxremote.localConnectorAddress", str);
/*     */       try
/*     */       {
/* 141 */         ConnectorAddressLink.export(str);
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 145 */         warning("agent.err.exportaddress.failed", localException.getMessage());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static synchronized void startRemoteManagementAgent(String paramString)
/*     */     throws Exception
/*     */   {
/* 155 */     if (jmxServer != null) {
/* 156 */       throw new RuntimeException(getText("agent.err.invalid.state", new String[] { "Agent already started" }));
/*     */     }
/*     */ 
/* 159 */     Properties localProperties1 = parseString(paramString);
/* 160 */     Properties localProperties2 = new Properties();
/*     */ 
/* 166 */     String str1 = System.getProperty("com.sun.management.config.file");
/* 167 */     readConfiguration(str1, localProperties2);
/*     */ 
/* 171 */     localProperties2.putAll(System.getProperties());
/*     */ 
/* 176 */     String str2 = localProperties1.getProperty("com.sun.management.config.file");
/* 177 */     if (str2 != null) {
/* 178 */       readConfiguration(str2, localProperties2);
/*     */     }
/*     */ 
/* 184 */     localProperties2.putAll(localProperties1);
/*     */ 
/* 189 */     String str3 = localProperties2.getProperty("com.sun.management.enableThreadContentionMonitoring");
/*     */ 
/* 192 */     if (str3 != null) {
/* 193 */       ManagementFactory.getThreadMXBean().setThreadContentionMonitoringEnabled(true);
/*     */     }
/*     */ 
/* 197 */     String str4 = localProperties2.getProperty("com.sun.management.jmxremote.port");
/* 198 */     if (str4 != null)
/* 199 */       jmxServer = ConnectorBootstrap.startRemoteConnectorServer(str4, localProperties2);
/*     */   }
/*     */ 
/*     */   private static synchronized void stopRemoteManagementAgent()
/*     */     throws Exception
/*     */   {
/* 205 */     if (jmxServer != null) {
/* 206 */       ConnectorBootstrap.unexportRegistry();
/*     */ 
/* 210 */       jmxServer.stop();
/* 211 */       jmxServer = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void startAgent(Properties paramProperties) throws Exception {
/* 216 */     String str1 = paramProperties.getProperty("com.sun.management.snmp.port");
/* 217 */     String str2 = paramProperties.getProperty("com.sun.management.jmxremote");
/* 218 */     String str3 = paramProperties.getProperty("com.sun.management.jmxremote.port");
/*     */ 
/* 221 */     String str4 = paramProperties.getProperty("com.sun.management.enableThreadContentionMonitoring");
/*     */ 
/* 223 */     if (str4 != null) {
/* 224 */       ManagementFactory.getThreadMXBean().setThreadContentionMonitoringEnabled(true);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 229 */       if (str1 != null) {
/* 230 */         loadSnmpAgent(str1, paramProperties);
/*     */       }
/*     */ 
/* 242 */       if ((str2 != null) || (str3 != null)) {
/* 243 */         if (str3 != null) {
/* 244 */           jmxServer = ConnectorBootstrap.startRemoteConnectorServer(str3, paramProperties);
/*     */         }
/*     */ 
/* 247 */         startLocalManagementAgent();
/*     */       }
/*     */     }
/*     */     catch (AgentConfigurationError localAgentConfigurationError) {
/* 251 */       error(localAgentConfigurationError.getError(), localAgentConfigurationError.getParams());
/*     */     } catch (Exception localException) {
/* 253 */       error(localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Properties loadManagementProperties() {
/* 258 */     Properties localProperties = new Properties();
/*     */ 
/* 262 */     String str = System.getProperty("com.sun.management.config.file");
/* 263 */     readConfiguration(str, localProperties);
/*     */ 
/* 267 */     localProperties.putAll(System.getProperties());
/*     */ 
/* 269 */     return localProperties;
/*     */   }
/*     */ 
/*     */   public static synchronized Properties getManagementProperties() {
/* 273 */     if (mgmtProps == null) {
/* 274 */       String str1 = System.getProperty("com.sun.management.config.file");
/* 275 */       String str2 = System.getProperty("com.sun.management.snmp.port");
/* 276 */       String str3 = System.getProperty("com.sun.management.jmxremote");
/* 277 */       String str4 = System.getProperty("com.sun.management.jmxremote.port");
/*     */ 
/* 279 */       if ((str1 == null) && (str2 == null) && (str3 == null) && (str4 == null))
/*     */       {
/* 282 */         return null;
/*     */       }
/* 284 */       mgmtProps = loadManagementProperties();
/*     */     }
/* 286 */     return mgmtProps;
/*     */   }
/*     */ 
/*     */   private static void loadSnmpAgent(String paramString, Properties paramProperties)
/*     */   {
/*     */     try
/*     */     {
/* 293 */       Class localClass = Class.forName("sun.management.snmp.AdaptorBootstrap", true, null);
/*     */ 
/* 295 */       localObject = localClass.getMethod("initialize", new Class[] { String.class, Properties.class });
/*     */ 
/* 298 */       ((Method)localObject).invoke(null, new Object[] { paramString, paramProperties });
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException) {
/* 301 */       throw new UnsupportedOperationException("Unsupported management property: com.sun.management.snmp.port", localClassNotFoundException);
/*     */     }
/*     */     catch (NoSuchMethodException localNoSuchMethodException) {
/* 304 */       throw new UnsupportedOperationException("Unsupported management property: com.sun.management.snmp.port", localNoSuchMethodException);
/*     */     } catch (InvocationTargetException localInvocationTargetException) {
/* 306 */       Object localObject = localInvocationTargetException.getCause();
/* 307 */       if ((localObject instanceof RuntimeException))
/* 308 */         throw ((RuntimeException)localObject);
/* 309 */       if ((localObject instanceof Error)) {
/* 310 */         throw ((Error)localObject);
/*     */       }
/* 312 */       throw new UnsupportedOperationException("Unsupported management property: com.sun.management.snmp.port", (Throwable)localObject);
/*     */     }
/*     */     catch (IllegalAccessException localIllegalAccessException) {
/* 315 */       throw new UnsupportedOperationException("Unsupported management property: com.sun.management.snmp.port", localIllegalAccessException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void readConfiguration(String paramString, Properties paramProperties)
/*     */   {
/* 321 */     if (paramString == null) {
/* 322 */       localObject1 = System.getProperty("java.home");
/* 323 */       if (localObject1 == null) {
/* 324 */         throw new Error("Can't find java.home ??");
/*     */       }
/* 326 */       localObject2 = new StringBuffer((String)localObject1);
/* 327 */       ((StringBuffer)localObject2).append(File.separator).append("lib");
/* 328 */       ((StringBuffer)localObject2).append(File.separator).append("management");
/* 329 */       ((StringBuffer)localObject2).append(File.separator).append("management.properties");
/*     */ 
/* 331 */       paramString = ((StringBuffer)localObject2).toString();
/*     */     }
/* 333 */     Object localObject1 = new File(paramString);
/* 334 */     if (!((File)localObject1).exists()) {
/* 335 */       error("agent.err.configfile.notfound", paramString);
/*     */     }
/*     */ 
/* 338 */     Object localObject2 = null;
/*     */     try {
/* 340 */       localObject2 = new FileInputStream((File)localObject1);
/* 341 */       BufferedInputStream localBufferedInputStream = new BufferedInputStream((InputStream)localObject2);
/* 342 */       paramProperties.load(localBufferedInputStream);
/*     */     } catch (FileNotFoundException localFileNotFoundException) {
/* 344 */       error("agent.err.configfile.failed", localFileNotFoundException.getMessage());
/*     */     } catch (IOException localIOException3) {
/* 346 */       error("agent.err.configfile.failed", localIOException3.getMessage());
/*     */     } catch (SecurityException localSecurityException) {
/* 348 */       error("agent.err.configfile.access.denied", paramString);
/*     */     } finally {
/* 350 */       if (localObject2 != null)
/*     */         try {
/* 352 */           ((InputStream)localObject2).close();
/*     */         } catch (IOException localIOException6) {
/* 354 */           error("agent.err.configfile.closed.failed", paramString);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void startAgent() throws Exception
/*     */   {
/* 361 */     String str1 = System.getProperty("com.sun.management.agent.class");
/*     */ 
/* 365 */     if (str1 == null)
/*     */     {
/* 367 */       localObject1 = getManagementProperties();
/* 368 */       if (localObject1 != null) {
/* 369 */         startAgent((Properties)localObject1);
/*     */       }
/* 371 */       return;
/*     */     }
/*     */ 
/* 375 */     Object localObject1 = str1.split(":");
/* 376 */     if ((localObject1.length < 1) || (localObject1.length > 2)) {
/* 377 */       error("agent.err.invalid.agentclass", "\"" + str1 + "\"");
/*     */     }
/* 379 */     String str2 = localObject1[0];
/* 380 */     Object localObject2 = localObject1.length == 2 ? localObject1[1] : null;
/*     */ 
/* 382 */     if ((str2 == null) || (str2.length() == 0)) {
/* 383 */       error("agent.err.invalid.agentclass", "\"" + str1 + "\"");
/*     */     }
/*     */ 
/* 386 */     if (str2 != null)
/*     */     {
/*     */       try
/*     */       {
/* 390 */         Class localClass = ClassLoader.getSystemClassLoader().loadClass(str2);
/* 391 */         localObject3 = localClass.getMethod("premain", new Class[] { String.class });
/*     */ 
/* 393 */         ((Method)localObject3).invoke(null, new Object[] { localObject2 });
/*     */       }
/*     */       catch (ClassNotFoundException localClassNotFoundException) {
/* 396 */         error("agent.err.agentclass.notfound", "\"" + str2 + "\"");
/*     */       } catch (NoSuchMethodException localNoSuchMethodException) {
/* 398 */         error("agent.err.premain.notfound", "\"" + str2 + "\"");
/*     */       } catch (SecurityException localSecurityException) {
/* 400 */         error("agent.err.agentclass.access.denied");
/*     */       } catch (Exception localException) {
/* 402 */         Object localObject3 = localException.getCause() == null ? localException.getMessage() : localException.getCause().getMessage();
/*     */ 
/* 405 */         error("agent.err.agentclass.failed", (String)localObject3);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void error(String paramString) {
/* 411 */     String str = getText(paramString);
/* 412 */     System.err.print(getText("agent.err.error") + ": " + str);
/* 413 */     throw new RuntimeException(str);
/*     */   }
/*     */ 
/*     */   public static void error(String paramString, String[] paramArrayOfString) {
/* 417 */     if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
/* 418 */       error(paramString);
/*     */     } else {
/* 420 */       StringBuffer localStringBuffer = new StringBuffer(paramArrayOfString[0]);
/* 421 */       for (int i = 1; i < paramArrayOfString.length; i++) {
/* 422 */         localStringBuffer.append(" " + paramArrayOfString[i]);
/*     */       }
/* 424 */       error(paramString, localStringBuffer.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void error(String paramString1, String paramString2)
/*     */   {
/* 430 */     String str = getText(paramString1);
/* 431 */     System.err.print(getText("agent.err.error") + ": " + str);
/* 432 */     System.err.println(": " + paramString2);
/* 433 */     throw new RuntimeException(str);
/*     */   }
/*     */ 
/*     */   public static void error(Exception paramException) {
/* 437 */     paramException.printStackTrace();
/* 438 */     System.err.println(getText("agent.err.exception") + ": " + paramException.toString());
/* 439 */     throw new RuntimeException(paramException);
/*     */   }
/*     */ 
/*     */   public static void warning(String paramString1, String paramString2) {
/* 443 */     System.err.print(getText("agent.err.warning") + ": " + getText(paramString1));
/* 444 */     System.err.println(": " + paramString2);
/*     */   }
/*     */ 
/*     */   private static void initResource() {
/*     */     try {
/* 449 */       messageRB = ResourceBundle.getBundle("sun.management.resources.agent");
/*     */     }
/*     */     catch (MissingResourceException localMissingResourceException) {
/* 452 */       throw new Error("Fatal: Resource for management agent is missing");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getText(String paramString) {
/* 457 */     if (messageRB == null)
/* 458 */       initResource();
/*     */     try
/*     */     {
/* 461 */       return messageRB.getString(paramString); } catch (MissingResourceException localMissingResourceException) {
/*     */     }
/* 463 */     return "Missing management agent resource bundle: key = \"" + paramString + "\"";
/*     */   }
/*     */ 
/*     */   public static String getText(String paramString, String[] paramArrayOfString)
/*     */   {
/* 468 */     if (messageRB == null) {
/* 469 */       initResource();
/*     */     }
/* 471 */     String str = messageRB.getString(paramString);
/* 472 */     if (str == null) {
/* 473 */       str = "missing resource key: key = \"" + paramString + "\", " + "arguments = \"{0}\", \"{1}\", \"{2}\"";
/*     */     }
/*     */ 
/* 476 */     return MessageFormat.format(str, (Object[])paramArrayOfString);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.management.Agent
 * JD-Core Version:    0.6.2
 */