/*      */ package sun.security.krb5;
/*      */ 
/*      */ import java.io.BufferedReader;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.PrintStream;
/*      */ import java.net.InetAddress;
/*      */ import java.net.UnknownHostException;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import sun.net.dns.ResolverConfiguration;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ import sun.security.krb5.internal.Krb5;
/*      */ import sun.security.krb5.internal.crypto.EType;
/*      */ 
/*      */ public class Config
/*      */ {
/*   61 */   private static Config singleton = null;
/*      */   private Hashtable<String, Object> stanzaTable;
/*   68 */   private static boolean DEBUG = Krb5.DEBUG;
/*      */   private static final int BASE16_0 = 1;
/*      */   private static final int BASE16_1 = 16;
/*      */   private static final int BASE16_2 = 256;
/*      */   private static final int BASE16_3 = 4096;
/*      */   private final String defaultRealm;
/*      */   private final String defaultKDC;
/*      */ 
/*      */   private static native String getWindowsDirectory(boolean paramBoolean);
/*      */ 
/*      */   public static synchronized Config getInstance()
/*      */     throws KrbException
/*      */   {
/*   95 */     if (singleton == null) {
/*   96 */       singleton = new Config();
/*      */     }
/*   98 */     return singleton;
/*      */   }
/*      */ 
/*      */   public static synchronized void refresh()
/*      */     throws KrbException
/*      */   {
/*  112 */     singleton = new Config();
/*  113 */     KdcComm.initStatic();
/*      */   }
/*      */ 
/*      */   private static boolean isMacosLionOrBetter()
/*      */   {
/*  119 */     String str = System.getProperty("os.version");
/*  120 */     String[] arrayOfString = str.split("\\.");
/*      */ 
/*  123 */     if (!arrayOfString[0].equals("10")) return false;
/*  124 */     if (arrayOfString.length < 2) return false;
/*      */ 
/*      */     try
/*      */     {
/*  128 */       int i = Integer.parseInt(arrayOfString[1]);
/*  129 */       if (i >= 7) return true;
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/*      */     }
/*  134 */     return false;
/*      */   }
/*      */ 
/*      */   private Config()
/*      */     throws KrbException
/*      */   {
/*  144 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.security.krb5.kdc"));
/*      */ 
/*  148 */     if (str != null)
/*      */     {
/*  150 */       this.defaultKDC = str.replace(':', ' ');
/*      */     }
/*  152 */     else this.defaultKDC = null;
/*      */ 
/*  154 */     this.defaultRealm = ((String)AccessController.doPrivileged(new GetPropertyAction("java.security.krb5.realm")));
/*      */ 
/*  158 */     if (((this.defaultKDC == null) && (this.defaultRealm != null)) || ((this.defaultRealm == null) && (this.defaultKDC != null)))
/*      */     {
/*  160 */       throw new KrbException("System property java.security.krb5.kdc and java.security.krb5.realm both must be set or neither must be set.");
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  169 */       Vector localVector = loadConfigFile();
/*  170 */       if ((localVector == null) && (isMacosLionOrBetter()))
/*  171 */         this.stanzaTable = SCDynamicStoreConfig.getConfig();
/*      */       else
/*  173 */         this.stanzaTable = parseStanzaTable(localVector);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getDefaultIntValue(String paramString)
/*      */   {
/*  188 */     String str = null;
/*  189 */     int i = -2147483648;
/*  190 */     str = getDefault(paramString);
/*  191 */     if (str != null) {
/*      */       try {
/*  193 */         i = parseIntValue(str);
/*      */       } catch (NumberFormatException localNumberFormatException) {
/*  195 */         if (DEBUG) {
/*  196 */           System.out.println("Exception in getting value of " + paramString + " " + localNumberFormatException.getMessage());
/*      */ 
/*  199 */           System.out.println("Setting " + paramString + " to minimum value");
/*      */         }
/*      */ 
/*  202 */         i = -2147483648;
/*      */       }
/*      */     }
/*  205 */     return i;
/*      */   }
/*      */ 
/*      */   public int getDefaultIntValue(String paramString1, String paramString2)
/*      */   {
/*  219 */     String str = null;
/*  220 */     int i = -2147483648;
/*  221 */     str = getDefault(paramString1, paramString2);
/*  222 */     if (str != null) {
/*      */       try {
/*  224 */         i = parseIntValue(str);
/*      */       } catch (NumberFormatException localNumberFormatException) {
/*  226 */         if (DEBUG) {
/*  227 */           System.out.println("Exception in getting value of " + paramString1 + " in section " + paramString2 + " " + localNumberFormatException.getMessage());
/*      */ 
/*  230 */           System.out.println("Setting " + paramString1 + " to minimum value");
/*      */         }
/*      */ 
/*  233 */         i = -2147483648;
/*      */       }
/*      */     }
/*  236 */     return i;
/*      */   }
/*      */ 
/*      */   public String getDefault(String paramString)
/*      */   {
/*  245 */     if (this.stanzaTable == null) {
/*  246 */       return null;
/*      */     }
/*  248 */     return getDefault(paramString, this.stanzaTable);
/*      */   }
/*      */ 
/*      */   private String getDefault(String paramString, Hashtable paramHashtable)
/*      */   {
/*  261 */     String str1 = null;
/*      */     Enumeration localEnumeration;
/*  263 */     if (this.stanzaTable != null) {
/*  264 */       for (localEnumeration = paramHashtable.keys(); localEnumeration.hasMoreElements(); ) {
/*  265 */         String str2 = (String)localEnumeration.nextElement();
/*  266 */         Object localObject = paramHashtable.get(str2);
/*  267 */         if ((localObject instanceof Hashtable)) {
/*  268 */           str1 = getDefault(paramString, (Hashtable)localObject);
/*  269 */           if (str1 != null)
/*  270 */             return str1;
/*      */         }
/*  272 */         else if (str2.equalsIgnoreCase(paramString)) {
/*  273 */           if ((localObject instanceof String))
/*  274 */             return (String)paramHashtable.get(str2);
/*  275 */           if ((localObject instanceof Vector)) {
/*  276 */             str1 = "";
/*  277 */             int i = ((Vector)localObject).size();
/*  278 */             for (int j = 0; j < i; j++) {
/*  279 */               if (j == i - 1) {
/*  280 */                 str1 = str1 + (String)((Vector)localObject).elementAt(j);
/*      */               }
/*      */               else {
/*  283 */                 str1 = str1 + (String)((Vector)localObject).elementAt(j) + " ";
/*      */               }
/*      */             }
/*      */ 
/*  287 */             return str1;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  292 */     return str1;
/*      */   }
/*      */ 
/*      */   public String getDefault(String paramString1, String paramString2)
/*      */   {
/*  305 */     String str2 = null;
/*      */     Enumeration localEnumeration;
/*  308 */     if (this.stanzaTable != null) {
/*  309 */       for (localEnumeration = this.stanzaTable.keys(); localEnumeration.hasMoreElements(); ) {
/*  310 */         String str1 = (String)localEnumeration.nextElement();
/*  311 */         Hashtable localHashtable1 = (Hashtable)this.stanzaTable.get(str1);
/*  312 */         if (str1.equalsIgnoreCase(paramString2)) {
/*  313 */           if (localHashtable1.containsKey(paramString1))
/*  314 */             return (String)localHashtable1.get(paramString1);
/*      */         }
/*  316 */         else if (localHashtable1.containsKey(paramString2)) {
/*  317 */           Object localObject1 = localHashtable1.get(paramString2);
/*  318 */           if ((localObject1 instanceof Hashtable)) {
/*  319 */             Hashtable localHashtable2 = (Hashtable)localObject1;
/*  320 */             if (localHashtable2.containsKey(paramString1)) {
/*  321 */               Object localObject2 = localHashtable2.get(paramString1);
/*  322 */               if ((localObject2 instanceof Vector)) {
/*  323 */                 str2 = "";
/*  324 */                 int i = ((Vector)localObject2).size();
/*  325 */                 for (int j = 0; j < i; j++) {
/*  326 */                   if (j == i - 1) {
/*  327 */                     str2 = str2 + (String)((Vector)localObject2).elementAt(j);
/*      */                   }
/*      */                   else {
/*  330 */                     str2 = str2 + (String)((Vector)localObject2).elementAt(j) + " ";
/*      */                   }
/*      */                 }
/*      */               }
/*      */               else
/*      */               {
/*  336 */                 str2 = (String)localObject2;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  343 */     return str2;
/*      */   }
/*      */ 
/*      */   public boolean getDefaultBooleanValue(String paramString)
/*      */   {
/*  353 */     String str = null;
/*  354 */     if (this.stanzaTable == null)
/*  355 */       str = null;
/*      */     else {
/*  357 */       str = getDefault(paramString, this.stanzaTable);
/*      */     }
/*  359 */     if ((str != null) && (str.equalsIgnoreCase("true"))) {
/*  360 */       return true;
/*      */     }
/*  362 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean getDefaultBooleanValue(String paramString1, String paramString2)
/*      */   {
/*  376 */     String str = getDefault(paramString1, paramString2);
/*  377 */     if ((str != null) && (str.equalsIgnoreCase("true"))) {
/*  378 */       return true;
/*      */     }
/*  380 */     return false;
/*      */   }
/*      */ 
/*      */   private int parseIntValue(String paramString)
/*      */     throws NumberFormatException
/*      */   {
/*  396 */     int i = 0;
/*      */     String str;
/*  397 */     if (paramString.startsWith("+")) {
/*  398 */       str = paramString.substring(1);
/*  399 */       return Integer.parseInt(str);
/*  400 */     }if (paramString.startsWith("0x")) {
/*  401 */       str = paramString.substring(2);
/*  402 */       char[] arrayOfChar = str.toCharArray();
/*  403 */       if (arrayOfChar.length > 8) {
/*  404 */         throw new NumberFormatException();
/*      */       }
/*  406 */       for (int j = 0; j < arrayOfChar.length; j++) {
/*  407 */         int k = arrayOfChar.length - j - 1;
/*  408 */         switch (arrayOfChar[j]) {
/*      */         case '0':
/*  410 */           i += 0;
/*  411 */           break;
/*      */         case '1':
/*  413 */           i += 1 * getBase(k);
/*  414 */           break;
/*      */         case '2':
/*  416 */           i += 2 * getBase(k);
/*  417 */           break;
/*      */         case '3':
/*  419 */           i += 3 * getBase(k);
/*  420 */           break;
/*      */         case '4':
/*  422 */           i += 4 * getBase(k);
/*  423 */           break;
/*      */         case '5':
/*  425 */           i += 5 * getBase(k);
/*  426 */           break;
/*      */         case '6':
/*  428 */           i += 6 * getBase(k);
/*  429 */           break;
/*      */         case '7':
/*  431 */           i += 7 * getBase(k);
/*  432 */           break;
/*      */         case '8':
/*  434 */           i += 8 * getBase(k);
/*  435 */           break;
/*      */         case '9':
/*  437 */           i += 9 * getBase(k);
/*  438 */           break;
/*      */         case 'A':
/*      */         case 'a':
/*  441 */           i += 10 * getBase(k);
/*  442 */           break;
/*      */         case 'B':
/*      */         case 'b':
/*  445 */           i += 11 * getBase(k);
/*  446 */           break;
/*      */         case 'C':
/*      */         case 'c':
/*  449 */           i += 12 * getBase(k);
/*  450 */           break;
/*      */         case 'D':
/*      */         case 'd':
/*  453 */           i += 13 * getBase(k);
/*  454 */           break;
/*      */         case 'E':
/*      */         case 'e':
/*  457 */           i += 14 * getBase(k);
/*  458 */           break;
/*      */         case 'F':
/*      */         case 'f':
/*  461 */           i += 15 * getBase(k);
/*  462 */           break;
/*      */         case ':':
/*      */         case ';':
/*      */         case '<':
/*      */         case '=':
/*      */         case '>':
/*      */         case '?':
/*      */         case '@':
/*      */         case 'G':
/*      */         case 'H':
/*      */         case 'I':
/*      */         case 'J':
/*      */         case 'K':
/*      */         case 'L':
/*      */         case 'M':
/*      */         case 'N':
/*      */         case 'O':
/*      */         case 'P':
/*      */         case 'Q':
/*      */         case 'R':
/*      */         case 'S':
/*      */         case 'T':
/*      */         case 'U':
/*      */         case 'V':
/*      */         case 'W':
/*      */         case 'X':
/*      */         case 'Y':
/*      */         case 'Z':
/*      */         case '[':
/*      */         case '\\':
/*      */         case ']':
/*      */         case '^':
/*      */         case '_':
/*      */         case '`':
/*      */         default:
/*  464 */           throw new NumberFormatException("Invalid numerical format");
/*      */         }
/*      */       }
/*      */ 
/*  468 */       if (i < 0)
/*  469 */         throw new NumberFormatException("Data overflow.");
/*      */     }
/*      */     else {
/*  472 */       i = Integer.parseInt(paramString);
/*      */     }
/*  474 */     return i;
/*      */   }
/*      */ 
/*      */   private int getBase(int paramInt) {
/*  478 */     int i = 16;
/*  479 */     switch (paramInt) {
/*      */     case 0:
/*  481 */       i = 1;
/*  482 */       break;
/*      */     case 1:
/*  484 */       i = 16;
/*  485 */       break;
/*      */     case 2:
/*  487 */       i = 256;
/*  488 */       break;
/*      */     case 3:
/*  490 */       i = 4096;
/*  491 */       break;
/*      */     default:
/*  493 */       for (int j = 1; j < paramInt; j++) {
/*  494 */         i *= 16;
/*      */       }
/*      */     }
/*  497 */     return i;
/*      */   }
/*      */ 
/*      */   private String find(String paramString1, String paramString2)
/*      */   {
/*      */     String str;
/*  505 */     if ((this.stanzaTable != null) && ((str = (String)((Hashtable)this.stanzaTable.get(paramString1)).get(paramString2)) != null))
/*      */     {
/*  508 */       return str;
/*      */     }
/*  510 */     return "";
/*      */   }
/*      */ 
/*      */   private Vector<String> loadConfigFile()
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/*  544 */       final String str1 = getFileName();
/*  545 */       if (!str1.equals("")) {
/*  546 */         BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader((InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */         {
/*      */           public FileInputStream run() throws IOException
/*      */           {
/*  550 */             return new FileInputStream(str1);
/*      */           }
/*      */         })));
/*  554 */         Vector localVector = new Vector();
/*  555 */         Object localObject = null;
/*      */         String str2;
/*  556 */         while ((str2 = localBufferedReader.readLine()) != null)
/*      */         {
/*  559 */           if ((!str2.startsWith("#")) && (!str2.trim().isEmpty())) {
/*  560 */             String str3 = str2.trim();
/*      */ 
/*  575 */             if (str3.equals("{")) {
/*  576 */               if (localObject == null) {
/*  577 */                 throw new IOException("Config file should not start with \"{\"");
/*      */               }
/*      */ 
/*  580 */               localObject = (String)localObject + " " + str3;
/*      */             } else {
/*  582 */               if (localObject != null) {
/*  583 */                 localVector.addElement(localObject);
/*      */               }
/*  585 */               localObject = str3;
/*      */             }
/*      */           }
/*      */         }
/*  589 */         if (localObject != null) {
/*  590 */           localVector.addElement(localObject);
/*      */         }
/*      */ 
/*  593 */         localBufferedReader.close();
/*  594 */         return localVector;
/*      */       }
/*  596 */       return null;
/*      */     } catch (PrivilegedActionException localPrivilegedActionException) {
/*  598 */       throw ((IOException)localPrivilegedActionException.getException());
/*      */     }
/*      */   }
/*      */ 
/*      */   private Hashtable<String, Object> parseStanzaTable(Vector<String> paramVector)
/*      */     throws KrbException
/*      */   {
/*  611 */     if (paramVector == null) {
/*  612 */       throw new KrbException("I/O error while reading configuration file.");
/*      */     }
/*      */ 
/*  615 */     Hashtable localHashtable1 = new Hashtable();
/*  616 */     for (int i = 0; i < paramVector.size(); i++) {
/*  617 */       String str1 = ((String)paramVector.elementAt(i)).trim();
/*      */       int j;
/*      */       Hashtable localHashtable2;
/*  618 */       if (str1.equalsIgnoreCase("[realms]")) {
/*  619 */         for (j = i + 1; j < paramVector.size() + 1; j++)
/*      */         {
/*  621 */           if ((j == paramVector.size()) || (((String)paramVector.elementAt(j)).startsWith("[")))
/*      */           {
/*  623 */             localHashtable2 = new Hashtable();
/*      */ 
/*  625 */             localHashtable2 = parseRealmField(paramVector, i + 1, j);
/*  626 */             localHashtable1.put("realms", localHashtable2);
/*  627 */             i = j - 1;
/*  628 */             break;
/*      */           }
/*      */         }
/*  631 */       } else if (str1.equalsIgnoreCase("[capaths]")) {
/*  632 */         for (j = i + 1; j < paramVector.size() + 1; j++)
/*      */         {
/*  634 */           if ((j == paramVector.size()) || (((String)paramVector.elementAt(j)).startsWith("[")))
/*      */           {
/*  636 */             localHashtable2 = new Hashtable();
/*      */ 
/*  638 */             localHashtable2 = parseRealmField(paramVector, i + 1, j);
/*  639 */             localHashtable1.put("capaths", localHashtable2);
/*  640 */             i = j - 1;
/*  641 */             break;
/*      */           }
/*      */         }
/*  644 */       } else if ((str1.startsWith("[")) && (str1.endsWith("]"))) {
/*  645 */         String str2 = str1.substring(1, str1.length() - 1);
/*  646 */         for (int k = i + 1; k < paramVector.size() + 1; k++)
/*      */         {
/*  648 */           if ((k == paramVector.size()) || (((String)paramVector.elementAt(k)).startsWith("[")))
/*      */           {
/*  650 */             Hashtable localHashtable3 = parseField(paramVector, i + 1, k);
/*      */ 
/*  652 */             localHashtable1.put(str2, localHashtable3);
/*  653 */             i = k - 1;
/*  654 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  659 */     return localHashtable1;
/*      */   }
/*      */ 
/*      */   private String getFileName()
/*      */   {
/*  682 */     Object localObject = (String)AccessController.doPrivileged(new GetPropertyAction("java.security.krb5.conf"));
/*      */ 
/*  686 */     if (localObject == null) {
/*  687 */       localObject = (String)AccessController.doPrivileged(new GetPropertyAction("java.home")) + File.separator + "lib" + File.separator + "security" + File.separator + "krb5.conf";
/*      */ 
/*  692 */       if (!fileExists((String)localObject)) {
/*  693 */         localObject = null;
/*  694 */         String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
/*      */ 
/*  697 */         if (str1.startsWith("Windows")) {
/*      */           try {
/*  699 */             Credentials.ensureLoaded();
/*      */           }
/*      */           catch (Exception localException) {
/*      */           }
/*  703 */           if (Credentials.alreadyLoaded) {
/*  704 */             String str2 = getWindowsDirectory(false);
/*  705 */             if (str2 != null) {
/*  706 */               if (str2.endsWith("\\"))
/*  707 */                 str2 = str2 + "krb5.ini";
/*      */               else {
/*  709 */                 str2 = str2 + "\\krb5.ini";
/*      */               }
/*  711 */               if (fileExists(str2)) {
/*  712 */                 localObject = str2;
/*      */               }
/*      */             }
/*  715 */             if (localObject == null) {
/*  716 */               str2 = getWindowsDirectory(true);
/*  717 */               if (str2 != null) {
/*  718 */                 if (str2.endsWith("\\"))
/*  719 */                   str2 = str2 + "krb5.ini";
/*      */                 else {
/*  721 */                   str2 = str2 + "\\krb5.ini";
/*      */                 }
/*  723 */                 localObject = str2;
/*      */               }
/*      */             }
/*      */           }
/*  727 */           if (localObject == null)
/*  728 */             localObject = "c:\\winnt\\krb5.ini";
/*      */         }
/*  730 */         else if (str1.startsWith("SunOS")) {
/*  731 */           localObject = "/etc/krb5/krb5.conf";
/*  732 */         } else if (str1.contains("OS X")) {
/*  733 */           if (isMacosLionOrBetter()) return "";
/*  734 */           localObject = findMacosConfigFile();
/*      */         } else {
/*  736 */           localObject = "/etc/krb5.conf";
/*      */         }
/*      */       }
/*      */     }
/*  740 */     if (DEBUG) {
/*  741 */       System.out.println("Config name: " + (String)localObject);
/*      */     }
/*  743 */     return localObject;
/*      */   }
/*      */ 
/*      */   private String getProperty(String paramString) {
/*  747 */     return (String)AccessController.doPrivileged(new GetPropertyAction(paramString));
/*      */   }
/*      */ 
/*      */   private String findMacosConfigFile() {
/*  751 */     String str1 = getProperty("user.home");
/*      */ 
/*  753 */     String str2 = str1 + "/Library/Preferences/edu.mit.Kerberos";
/*      */ 
/*  755 */     if (fileExists(str2)) {
/*  756 */       return str2;
/*      */     }
/*      */ 
/*  759 */     if (fileExists("/Library/Preferences/edu.mit.Kerberos")) {
/*  760 */       return "/Library/Preferences/edu.mit.Kerberos";
/*      */     }
/*      */ 
/*  763 */     if (fileExists("/etc/krb5.conf")) {
/*  764 */       return "/etc/krb5.conf";
/*      */     }
/*      */ 
/*  767 */     return "";
/*      */   }
/*      */ 
/*      */   private static String trimmed(String paramString) {
/*  771 */     paramString = paramString.trim();
/*  772 */     if (((paramString.charAt(0) == '"') && (paramString.charAt(paramString.length() - 1) == '"')) || ((paramString.charAt(0) == '\'') && (paramString.charAt(paramString.length() - 1) == '\'')))
/*      */     {
/*  774 */       paramString = paramString.substring(1, paramString.length() - 1).trim();
/*      */     }
/*  776 */     return paramString;
/*      */   }
/*      */ 
/*      */   private Hashtable<String, String> parseField(Vector<String> paramVector, int paramInt1, int paramInt2)
/*      */   {
/*  782 */     Hashtable localHashtable = new Hashtable();
/*      */ 
/*  784 */     for (int i = paramInt1; i < paramInt2; i++) {
/*  785 */       String str1 = (String)paramVector.elementAt(i);
/*  786 */       for (int j = 0; j < str1.length(); j++) {
/*  787 */         if (str1.charAt(j) == '=') {
/*  788 */           String str2 = str1.substring(0, j).trim();
/*  789 */           String str3 = trimmed(str1.substring(j + 1));
/*  790 */           localHashtable.put(str2, str3);
/*  791 */           break;
/*      */         }
/*      */       }
/*      */     }
/*  795 */     return localHashtable;
/*      */   }
/*      */ 
/*      */   private Hashtable<String, Hashtable<String, Vector<String>>> parseRealmField(Vector<String> paramVector, int paramInt1, int paramInt2)
/*      */   {
/*  804 */     Hashtable localHashtable1 = new Hashtable();
/*      */ 
/*  806 */     for (int i = paramInt1; i < paramInt2; i++) {
/*  807 */       String str1 = ((String)paramVector.elementAt(i)).trim();
/*  808 */       if (str1.endsWith("{")) {
/*  809 */         String str2 = "";
/*  810 */         for (int j = 0; j < str1.length(); j++) {
/*  811 */           if (str1.charAt(j) == '=') {
/*  812 */             str2 = str1.substring(0, j).trim();
/*      */ 
/*  814 */             break;
/*      */           }
/*      */         }
/*  817 */         for (j = i + 1; j < paramInt2; j++) {
/*  818 */           int k = 0;
/*  819 */           str1 = ((String)paramVector.elementAt(j)).trim();
/*  820 */           for (int m = 0; m < str1.length(); m++) {
/*  821 */             if (str1.charAt(m) == '}') {
/*  822 */               k = 1;
/*  823 */               break;
/*      */             }
/*      */           }
/*  826 */           if (k == 1) {
/*  827 */             Hashtable localHashtable2 = parseRealmFieldEx(paramVector, i + 1, j);
/*  828 */             localHashtable1.put(str2, localHashtable2);
/*  829 */             i = j;
/*  830 */             k = 0;
/*  831 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  837 */     return localHashtable1;
/*      */   }
/*      */ 
/*      */   private Hashtable<String, Vector<String>> parseRealmFieldEx(Vector<String> paramVector, int paramInt1, int paramInt2)
/*      */   {
/*  844 */     Hashtable localHashtable = new Hashtable();
/*  845 */     Vector localVector1 = new Vector();
/*  846 */     Vector localVector2 = new Vector();
/*  847 */     String str1 = "";
/*      */ 
/*  849 */     for (int i = paramInt1; i < paramInt2; i++) {
/*  850 */       str1 = (String)paramVector.elementAt(i);
/*  851 */       for (int j = 0; j < str1.length(); j++) {
/*  852 */         if (str1.charAt(j) == '=')
/*      */         {
/*  854 */           String str2 = str1.substring(0, j).trim();
/*  855 */           if (!exists(str2, localVector1)) {
/*  856 */             localVector1.addElement(str2);
/*  857 */             localVector2 = new Vector();
/*      */           } else {
/*  859 */             localVector2 = (Vector)localHashtable.get(str2);
/*      */           }
/*  861 */           localVector2.addElement(trimmed(str1.substring(j + 1)));
/*  862 */           localHashtable.put(str2, localVector2);
/*  863 */           break;
/*      */         }
/*      */       }
/*      */     }
/*  867 */     return localHashtable;
/*      */   }
/*      */ 
/*      */   private boolean exists(String paramString, Vector paramVector)
/*      */   {
/*  874 */     boolean bool = false;
/*  875 */     for (int i = 0; i < paramVector.size(); i++) {
/*  876 */       if (((String)paramVector.elementAt(i)).equals(paramString)) {
/*  877 */         bool = true;
/*      */       }
/*      */     }
/*  880 */     return bool;
/*      */   }
/*      */ 
/*      */   public void listTable()
/*      */   {
/*  888 */     listTable(this.stanzaTable);
/*      */   }
/*      */ 
/*      */   private void listTable(Hashtable paramHashtable) {
/*  892 */     Vector localVector = new Vector();
/*      */     Enumeration localEnumeration;
/*  894 */     if (this.stanzaTable != null) {
/*  895 */       for (localEnumeration = paramHashtable.keys(); localEnumeration.hasMoreElements(); ) {
/*  896 */         String str = (String)localEnumeration.nextElement();
/*  897 */         Object localObject = paramHashtable.get(str);
/*  898 */         if (paramHashtable == this.stanzaTable) {
/*  899 */           System.out.println("[" + str + "]");
/*      */         }
/*  901 */         if ((localObject instanceof Hashtable)) {
/*  902 */           if (paramHashtable != this.stanzaTable)
/*  903 */             System.out.println("\t" + str + " = {");
/*  904 */           listTable((Hashtable)localObject);
/*  905 */           if (paramHashtable != this.stanzaTable)
/*  906 */             System.out.println("\t}");
/*      */         }
/*  908 */         else if ((localObject instanceof String)) {
/*  909 */           System.out.println("\t" + str + " = " + (String)paramHashtable.get(str));
/*      */         }
/*  911 */         else if ((localObject instanceof Vector)) {
/*  912 */           localVector = (Vector)localObject;
/*  913 */           for (int i = 0; i < localVector.size(); i++) {
/*  914 */             System.out.println("\t" + str + " = " + (String)localVector.elementAt(i));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*  920 */       System.out.println("Configuration file not found.");
/*      */   }
/*      */ 
/*      */   public int[] defaultEtype(String paramString)
/*      */   {
/*  930 */     String str1 = getDefault(paramString, "libdefaults");
/*  931 */     String str2 = " ";
/*      */     int[] arrayOfInt;
/*      */     int i;
/*  934 */     if (str1 == null) {
/*  935 */       if (DEBUG) {
/*  936 */         System.out.println("Using builtin default etypes for " + paramString);
/*      */       }
/*      */ 
/*  939 */       arrayOfInt = EType.getBuiltInDefaults();
/*      */     } else {
/*  941 */       for (i = 0; i < str1.length(); i++) {
/*  942 */         if (str1.substring(i, i + 1).equals(","))
/*      */         {
/*  945 */           str2 = ",";
/*  946 */           break;
/*      */         }
/*      */       }
/*  949 */       StringTokenizer localStringTokenizer = new StringTokenizer(str1, str2);
/*  950 */       i = localStringTokenizer.countTokens();
/*  951 */       ArrayList localArrayList = new ArrayList(i);
/*      */ 
/*  953 */       for (int k = 0; k < i; k++) {
/*  954 */         int j = getType(localStringTokenizer.nextToken());
/*  955 */         if ((j != -1) && (EType.isSupported(j)))
/*      */         {
/*  957 */           localArrayList.add(Integer.valueOf(j));
/*      */         }
/*      */       }
/*  960 */       if (localArrayList.size() == 0) {
/*  961 */         if (DEBUG) {
/*  962 */           System.out.println("no supported default etypes for " + paramString);
/*      */         }
/*      */ 
/*  965 */         return null;
/*      */       }
/*  967 */       arrayOfInt = new int[localArrayList.size()];
/*  968 */       for (k = 0; k < arrayOfInt.length; k++) {
/*  969 */         arrayOfInt[k] = ((Integer)localArrayList.get(k)).intValue();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  974 */     if (DEBUG) {
/*  975 */       System.out.print("default etypes for " + paramString + ":");
/*  976 */       for (i = 0; i < arrayOfInt.length; i++) {
/*  977 */         System.out.print(" " + arrayOfInt[i]);
/*      */       }
/*  979 */       System.out.println(".");
/*      */     }
/*  981 */     return arrayOfInt;
/*      */   }
/*      */ 
/*      */   public int getType(String paramString)
/*      */   {
/*  996 */     int i = -1;
/*  997 */     if (paramString == null) {
/*  998 */       return i;
/*      */     }
/* 1000 */     if ((paramString.startsWith("d")) || (paramString.startsWith("D"))) {
/* 1001 */       if (paramString.equalsIgnoreCase("des-cbc-crc"))
/* 1002 */         i = 1;
/* 1003 */       else if (paramString.equalsIgnoreCase("des-cbc-md5"))
/* 1004 */         i = 3;
/* 1005 */       else if (paramString.equalsIgnoreCase("des-mac"))
/* 1006 */         i = 4;
/* 1007 */       else if (paramString.equalsIgnoreCase("des-mac-k"))
/* 1008 */         i = 5;
/* 1009 */       else if (paramString.equalsIgnoreCase("des-cbc-md4"))
/* 1010 */         i = 2;
/* 1011 */       else if ((paramString.equalsIgnoreCase("des3-cbc-sha1")) || (paramString.equalsIgnoreCase("des3-hmac-sha1")) || (paramString.equalsIgnoreCase("des3-cbc-sha1-kd")) || (paramString.equalsIgnoreCase("des3-cbc-hmac-sha1-kd")))
/*      */       {
/* 1015 */         i = 16;
/*      */       }
/* 1017 */     } else if ((paramString.startsWith("a")) || (paramString.startsWith("A")))
/*      */     {
/* 1019 */       if ((paramString.equalsIgnoreCase("aes128-cts")) || (paramString.equalsIgnoreCase("aes128-cts-hmac-sha1-96")))
/*      */       {
/* 1021 */         i = 17;
/* 1022 */       } else if ((paramString.equalsIgnoreCase("aes256-cts")) || (paramString.equalsIgnoreCase("aes256-cts-hmac-sha1-96")))
/*      */       {
/* 1024 */         i = 18;
/*      */       }
/* 1026 */       else if ((paramString.equalsIgnoreCase("arcfour-hmac")) || (paramString.equalsIgnoreCase("arcfour-hmac-md5")))
/*      */       {
/* 1028 */         i = 23;
/*      */       }
/*      */     }
/* 1031 */     else if (paramString.equalsIgnoreCase("rc4-hmac"))
/* 1032 */       i = 23;
/* 1033 */     else if (paramString.equalsIgnoreCase("CRC32"))
/* 1034 */       i = 1;
/* 1035 */     else if ((paramString.startsWith("r")) || (paramString.startsWith("R"))) {
/* 1036 */       if (paramString.equalsIgnoreCase("rsa-md5"))
/* 1037 */         i = 7;
/* 1038 */       else if (paramString.equalsIgnoreCase("rsa-md5-des"))
/* 1039 */         i = 8;
/*      */     }
/* 1041 */     else if (paramString.equalsIgnoreCase("hmac-sha1-des3-kd"))
/* 1042 */       i = 12;
/* 1043 */     else if (paramString.equalsIgnoreCase("hmac-sha1-96-aes128"))
/* 1044 */       i = 15;
/* 1045 */     else if (paramString.equalsIgnoreCase("hmac-sha1-96-aes256"))
/* 1046 */       i = 16;
/* 1047 */     else if ((paramString.equalsIgnoreCase("hmac-md5-rc4")) || (paramString.equalsIgnoreCase("hmac-md5-arcfour")) || (paramString.equalsIgnoreCase("hmac-md5-enc")))
/*      */     {
/* 1050 */       i = -138;
/* 1051 */     } else if (paramString.equalsIgnoreCase("NULL")) {
/* 1052 */       i = 0;
/*      */     }
/*      */ 
/* 1055 */     return i;
/*      */   }
/*      */ 
/*      */   public void resetDefaultRealm(String paramString)
/*      */   {
/* 1065 */     if (DEBUG)
/* 1066 */       System.out.println(">>> Config try resetting default kdc " + paramString);
/*      */   }
/*      */ 
/*      */   public boolean useAddresses()
/*      */   {
/* 1075 */     boolean bool = false;
/*      */ 
/* 1077 */     String str = getDefault("no_addresses", "libdefaults");
/* 1078 */     bool = (str != null) && (str.equalsIgnoreCase("false"));
/* 1079 */     if (!bool)
/*      */     {
/* 1081 */       str = getDefault("noaddresses", "libdefaults");
/* 1082 */       bool = (str != null) && (str.equalsIgnoreCase("false"));
/*      */     }
/* 1084 */     return bool;
/*      */   }
/*      */ 
/*      */   public boolean useDNS(String paramString)
/*      */   {
/* 1091 */     String str = getDefault(paramString, "libdefaults");
/* 1092 */     if (str == null) {
/* 1093 */       str = getDefault("dns_fallback", "libdefaults");
/* 1094 */       if ("false".equalsIgnoreCase(str)) {
/* 1095 */         return false;
/*      */       }
/* 1097 */       return true;
/*      */     }
/*      */ 
/* 1100 */     return str.equalsIgnoreCase("true");
/*      */   }
/*      */ 
/*      */   public boolean useDNS_KDC()
/*      */   {
/* 1108 */     return useDNS("dns_lookup_kdc");
/*      */   }
/*      */ 
/*      */   public boolean useDNS_Realm()
/*      */   {
/* 1115 */     return useDNS("dns_lookup_realm");
/*      */   }
/*      */ 
/*      */   public String getDefaultRealm()
/*      */     throws KrbException
/*      */   {
/* 1124 */     if (this.defaultRealm != null) {
/* 1125 */       return this.defaultRealm;
/*      */     }
/* 1127 */     Object localObject = null;
/* 1128 */     String str = getDefault("default_realm", "libdefaults");
/* 1129 */     if ((str == null) && (useDNS_Realm())) {
/*      */       try
/*      */       {
/* 1132 */         str = getRealmFromDNS();
/*      */       } catch (KrbException localKrbException1) {
/* 1134 */         localObject = localKrbException1;
/*      */       }
/*      */     }
/* 1137 */     if (str == null) {
/* 1138 */       str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public String run()
/*      */         {
/* 1142 */           String str = System.getProperty("os.name");
/* 1143 */           if (str.startsWith("Windows")) {
/* 1144 */             return System.getenv("USERDNSDOMAIN");
/*      */           }
/* 1146 */           return null;
/*      */         }
/*      */       });
/*      */     }
/* 1150 */     if (str == null) {
/* 1151 */       KrbException localKrbException2 = new KrbException("Cannot locate default realm");
/* 1152 */       if (localObject != null) {
/* 1153 */         localKrbException2.initCause(localObject);
/*      */       }
/* 1155 */       throw localKrbException2;
/*      */     }
/* 1157 */     return str;
/*      */   }
/*      */ 
/*      */   public String getKDCList(String paramString)
/*      */     throws KrbException
/*      */   {
/* 1168 */     if (paramString == null) {
/* 1169 */       paramString = getDefaultRealm();
/*      */     }
/* 1171 */     if (paramString.equalsIgnoreCase(this.defaultRealm)) {
/* 1172 */       return this.defaultKDC;
/*      */     }
/* 1174 */     Object localObject = null;
/* 1175 */     String str = getDefault("kdc", paramString);
/* 1176 */     if ((str == null) && (useDNS_KDC())) {
/*      */       try
/*      */       {
/* 1179 */         str = getKDCFromDNS(paramString);
/*      */       } catch (KrbException localKrbException1) {
/* 1181 */         localObject = localKrbException1;
/*      */       }
/*      */     }
/* 1184 */     if (str == null) {
/* 1185 */       str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public String run()
/*      */         {
/* 1189 */           String str1 = System.getProperty("os.name");
/* 1190 */           if (str1.startsWith("Windows")) {
/* 1191 */             String str2 = System.getenv("LOGONSERVER");
/* 1192 */             if ((str2 != null) && (str2.startsWith("\\\\")))
/*      */             {
/* 1194 */               str2 = str2.substring(2);
/*      */             }
/* 1196 */             return str2;
/*      */           }
/* 1198 */           return null;
/*      */         }
/*      */       });
/*      */     }
/* 1202 */     if (str == null) {
/* 1203 */       if (this.defaultKDC != null) {
/* 1204 */         return this.defaultKDC;
/*      */       }
/* 1206 */       KrbException localKrbException2 = new KrbException("Cannot locate KDC");
/* 1207 */       if (localObject != null) {
/* 1208 */         localKrbException2.initCause(localObject);
/*      */       }
/* 1210 */       throw localKrbException2;
/*      */     }
/* 1212 */     return str;
/*      */   }
/*      */ 
/*      */   private String getRealmFromDNS()
/*      */     throws KrbException
/*      */   {
/* 1222 */     String str1 = null;
/* 1223 */     String str2 = null;
/*      */     Object localObject;
/*      */     try
/*      */     {
/* 1225 */       str2 = InetAddress.getLocalHost().getCanonicalHostName();
/*      */     } catch (UnknownHostException localUnknownHostException) {
/* 1227 */       localObject = new KrbException(60, "Unable to locate Kerberos realm: " + localUnknownHostException.getMessage());
/*      */ 
/* 1229 */       ((KrbException)localObject).initCause(localUnknownHostException);
/* 1230 */       throw ((Throwable)localObject);
/*      */     }
/*      */ 
/* 1233 */     String str3 = PrincipalName.mapHostToRealm(str2);
/* 1234 */     if (str3 == null)
/*      */     {
/* 1236 */       localObject = ResolverConfiguration.open().searchlist();
/* 1237 */       for (String str4 : (List)localObject) {
/* 1238 */         str1 = checkRealm(str4);
/* 1239 */         if (str1 != null)
/*      */           break;
/*      */       }
/*      */     }
/*      */     else {
/* 1244 */       str1 = checkRealm(str3);
/*      */     }
/* 1246 */     if (str1 == null) {
/* 1247 */       throw new KrbException(60, "Unable to locate Kerberos realm");
/*      */     }
/*      */ 
/* 1250 */     return str1;
/*      */   }
/*      */ 
/*      */   private static String checkRealm(String paramString)
/*      */   {
/* 1258 */     if (DEBUG) {
/* 1259 */       System.out.println("getRealmFromDNS: trying " + paramString);
/*      */     }
/* 1261 */     String[] arrayOfString = null;
/* 1262 */     String str = paramString;
/* 1263 */     while ((arrayOfString == null) && (str != null))
/*      */     {
/* 1265 */       arrayOfString = KrbServiceLocator.getKerberosService(str);
/* 1266 */       str = Realm.parseRealmComponent(str);
/*      */     }
/*      */ 
/* 1269 */     if (arrayOfString != null) {
/* 1270 */       for (int i = 0; i < arrayOfString.length; i++) {
/* 1271 */         if (arrayOfString[i].equalsIgnoreCase(paramString)) {
/* 1272 */           return arrayOfString[i];
/*      */         }
/*      */       }
/*      */     }
/* 1276 */     return null;
/*      */   }
/*      */ 
/*      */   private String getKDCFromDNS(String paramString)
/*      */     throws KrbException
/*      */   {
/* 1287 */     String str1 = null;
/* 1288 */     String[] arrayOfString = null;
/*      */ 
/* 1290 */     if (DEBUG) {
/* 1291 */       System.out.println("getKDCFromDNS using UDP");
/*      */     }
/* 1293 */     arrayOfString = KrbServiceLocator.getKerberosService(paramString, "_udp");
/* 1294 */     if (arrayOfString == null)
/*      */     {
/* 1296 */       if (DEBUG) {
/* 1297 */         System.out.println("getKDCFromDNS using UDP");
/*      */       }
/* 1299 */       arrayOfString = KrbServiceLocator.getKerberosService(paramString, "_tcp");
/*      */     }
/* 1301 */     if (arrayOfString == null)
/*      */     {
/* 1303 */       throw new KrbException(60, "Unable to locate KDC for realm " + paramString);
/*      */     }
/*      */ 
/* 1306 */     for (int i = 0; i < arrayOfString.length; i++) {
/* 1307 */       String str2 = arrayOfString[i];
/* 1308 */       for (int j = 0; j < arrayOfString[i].length(); j++)
/*      */       {
/* 1310 */         if (str2.charAt(j) == ':') {
/* 1311 */           str1 = str2.substring(0, j).trim();
/*      */         }
/*      */       }
/*      */     }
/* 1315 */     return str1;
/*      */   }
/*      */ 
/*      */   private boolean fileExists(String paramString) {
/* 1319 */     return ((Boolean)AccessController.doPrivileged(new FileExistsAction(paramString))).booleanValue();
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1339 */     StringBuffer localStringBuffer = new StringBuffer();
/* 1340 */     toStringIndented("", this.stanzaTable, localStringBuffer);
/* 1341 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   private static void toStringIndented(String paramString, Object paramObject, StringBuffer paramStringBuffer) {
/* 1345 */     if ((paramObject instanceof String)) {
/* 1346 */       paramStringBuffer.append(paramString);
/* 1347 */       paramStringBuffer.append(paramObject);
/* 1348 */       paramStringBuffer.append('\n');
/*      */     }
/*      */     else
/*      */     {
/*      */       Object localObject1;
/*      */       Object localObject2;
/* 1349 */       if ((paramObject instanceof Hashtable)) {
/* 1350 */         localObject1 = (Hashtable)paramObject;
/* 1351 */         for (localObject2 = ((Hashtable)localObject1).keySet().iterator(); ((Iterator)localObject2).hasNext(); ) { Object localObject3 = ((Iterator)localObject2).next();
/* 1352 */           paramStringBuffer.append(paramString);
/* 1353 */           paramStringBuffer.append(localObject3);
/* 1354 */           paramStringBuffer.append(" = {\n");
/* 1355 */           toStringIndented(paramString + "    ", ((Hashtable)localObject1).get(localObject3), paramStringBuffer);
/* 1356 */           paramStringBuffer.append(paramString + "}\n"); }
/*      */       }
/* 1358 */       else if ((paramObject instanceof Vector)) {
/* 1359 */         localObject1 = (Vector)paramObject;
/* 1360 */         for (Object localObject4 : ((Vector)localObject1).toArray())
/* 1361 */           toStringIndented(paramString + "    ", localObject4, paramStringBuffer);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static class FileExistsAction
/*      */     implements PrivilegedAction<Boolean>
/*      */   {
/*      */     private String fileName;
/*      */ 
/*      */     public FileExistsAction(String paramString)
/*      */     {
/* 1329 */       this.fileName = paramString;
/*      */     }
/*      */ 
/*      */     public Boolean run() {
/* 1333 */       return Boolean.valueOf(new File(this.fileName).exists());
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.krb5.Config
 * JD-Core Version:    0.6.2
 */