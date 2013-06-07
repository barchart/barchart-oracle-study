/*      */ package sun.net.ftp.impl;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.Closeable;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.net.Inet6Address;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.Proxy;
/*      */ import java.net.Proxy.Type;
/*      */ import java.net.ServerSocket;
/*      */ import java.net.Socket;
/*      */ import java.net.SocketAddress;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.text.DateFormat;
/*      */ import java.text.ParseException;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.TimeZone;
/*      */ import java.util.Vector;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import javax.net.ssl.SSLException;
/*      */ import javax.net.ssl.SSLSession;
/*      */ import javax.net.ssl.SSLSocket;
/*      */ import javax.net.ssl.SSLSocketFactory;
/*      */ import sun.misc.BASE64Decoder;
/*      */ import sun.misc.BASE64Encoder;
/*      */ import sun.net.TelnetInputStream;
/*      */ import sun.net.TelnetOutputStream;
/*      */ import sun.net.ftp.FtpClient.TransferType;
/*      */ import sun.net.ftp.FtpDirEntry;
/*      */ import sun.net.ftp.FtpDirEntry.Type;
/*      */ import sun.net.ftp.FtpDirParser;
/*      */ import sun.net.ftp.FtpProtocolException;
/*      */ import sun.net.ftp.FtpReplyCode;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ public class FtpClient extends sun.net.ftp.FtpClient
/*      */ {
/*      */   private static int defaultSoTimeout;
/*      */   private static int defaultConnectTimeout;
/*   55 */   private static final PlatformLogger logger = PlatformLogger.getLogger("sun.net.ftp.FtpClient");
/*      */   private Proxy proxy;
/*      */   private Socket server;
/*      */   private PrintStream out;
/*      */   private InputStream in;
/*   61 */   private int readTimeout = -1;
/*   62 */   private int connectTimeout = -1;
/*      */ 
/*   65 */   private static String encoding = "ISO8859_1";
/*      */   private InetSocketAddress serverAddr;
/*   68 */   private boolean replyPending = false;
/*   69 */   private boolean loggedIn = false;
/*   70 */   private boolean useCrypto = false;
/*      */   private SSLSocketFactory sslFact;
/*      */   private Socket oldSocket;
/*   74 */   private Vector<String> serverResponse = new Vector(1);
/*      */ 
/*   76 */   private FtpReplyCode lastReplyCode = null;
/*      */   private String welcomeMsg;
/*   79 */   private boolean passiveMode = true;
/*   80 */   private FtpClient.TransferType type = FtpClient.TransferType.BINARY;
/*   81 */   private long restartOffset = 0L;
/*   82 */   private long lastTransSize = -1L;
/*      */   private String lastFileName;
/*   87 */   private static String[] patStrings = { "([\\-ld](?:[r\\-][w\\-][x\\-]){3})\\s*\\d+ (\\w+)\\s*(\\w+)\\s*(\\d+)\\s*([A-Z][a-z][a-z]\\s*\\d+)\\s*(\\d\\d:\\d\\d)\\s*(\\p{Print}*)", "([\\-ld](?:[r\\-][w\\-][x\\-]){3})\\s*\\d+ (\\w+)\\s*(\\w+)\\s*(\\d+)\\s*([A-Z][a-z][a-z]\\s*\\d+)\\s*(\\d{4})\\s*(\\p{Print}*)", "(\\d{2}/\\d{2}/\\d{4})\\s*(\\d{2}:\\d{2}[ap])\\s*((?:[0-9,]+)|(?:<DIR>))\\s*(\\p{Graph}*)", "(\\d{2}-\\d{2}-\\d{2})\\s*(\\d{2}:\\d{2}[AP]M)\\s*((?:[0-9,]+)|(?:<DIR>))\\s*(\\p{Graph}*)" };
/*      */ 
/*   97 */   private static int[][] patternGroups = { { 7, 4, 5, 6, 0, 1, 2, 3 }, { 7, 4, 5, 0, 6, 1, 2, 3 }, { 4, 3, 1, 2, 0, 0, 0, 0 }, { 4, 3, 1, 2, 0, 0, 0, 0 } };
/*      */   private static Pattern[] patterns;
/*  105 */   private static Pattern linkp = Pattern.compile("(\\p{Print}+) \\-\\> (\\p{Print}+)$");
/*  106 */   private DateFormat df = DateFormat.getDateInstance(2, Locale.US);
/*      */ 
/*  365 */   private FtpDirParser parser = new DefaultParser(null);
/*  366 */   private FtpDirParser mlsxParser = new MLSxParser(null);
/*      */   private static Pattern transPat;
/*      */   private static Pattern epsvPat;
/*      */   private static Pattern pasvPat;
/*      */   private static String[] MDTMformats;
/*      */   private static SimpleDateFormat[] dateFormats;
/*      */ 
/*      */   private static boolean isASCIISuperset(String paramString)
/*      */     throws Exception
/*      */   {
/*  168 */     String str = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'();/?:@&=+$,";
/*      */ 
/*  172 */     byte[] arrayOfByte1 = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 45, 95, 46, 33, 126, 42, 39, 40, 41, 59, 47, 63, 58, 64, 38, 61, 43, 36, 44 };
/*      */ 
/*  178 */     byte[] arrayOfByte2 = str.getBytes(paramString);
/*  179 */     return Arrays.equals(arrayOfByte2, arrayOfByte1);
/*      */   }
/*      */ 
/*      */   private void getTransferSize()
/*      */   {
/*  370 */     this.lastTransSize = -1L;
/*      */ 
/*  377 */     String str1 = getLastResponseString();
/*  378 */     if (transPat == null) {
/*  379 */       transPat = Pattern.compile("150 Opening .*\\((\\d+) bytes\\).");
/*      */     }
/*  381 */     Matcher localMatcher = transPat.matcher(str1);
/*  382 */     if (localMatcher.find()) {
/*  383 */       String str2 = localMatcher.group(1);
/*  384 */       this.lastTransSize = Long.parseLong(str2);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void getTransferName()
/*      */   {
/*  394 */     this.lastFileName = null;
/*  395 */     String str = getLastResponseString();
/*  396 */     int i = str.indexOf("unique file name:");
/*  397 */     int j = str.lastIndexOf(')');
/*  398 */     if (i >= 0) {
/*  399 */       i += 17;
/*  400 */       this.lastFileName = str.substring(i, j);
/*      */     }
/*      */   }
/*      */ 
/*      */   private int readServerResponse()
/*      */     throws IOException
/*      */   {
/*  409 */     StringBuffer localStringBuffer = new StringBuffer(32);
/*      */ 
/*  411 */     int j = -1;
/*      */ 
/*  415 */     this.serverResponse.setSize(0);
/*      */     label76: int k;
/*      */     while (true)
/*      */     {
/*      */       int i;
/*  417 */       if ((i = this.in.read()) != -1) {
/*  418 */         if ((i == 13) && 
/*  419 */           ((i = this.in.read()) != 10)) {
/*  420 */           localStringBuffer.append('\r');
/*      */         }
/*      */ 
/*  423 */         localStringBuffer.append((char)i);
/*  424 */         if (i == 10)
/*  425 */           break label76;
/*      */       }
/*      */       else {
/*  428 */         String str = localStringBuffer.toString();
/*  429 */         localStringBuffer.setLength(0);
/*  430 */         if (logger.isLoggable(300)) {
/*  431 */           logger.finest("Server [" + this.serverAddr + "] --> " + str);
/*      */         }
/*      */ 
/*  434 */         if (str.length() == 0) {
/*  435 */           k = -1;
/*      */         } else {
/*      */           try {
/*  438 */             k = Integer.parseInt(str.substring(0, 3));
/*      */           } catch (NumberFormatException localNumberFormatException) {
/*  440 */             k = -1;
/*      */           }
/*      */           catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {
/*      */           }
/*  444 */           continue;
/*      */         }
/*      */ 
/*  447 */         this.serverResponse.addElement(str);
/*  448 */         if (j != -1)
/*      */         {
/*  450 */           if ((k == j) && ((str.length() < 4) || (str.charAt(3) != '-')))
/*      */           {
/*  455 */             j = -1;
/*  456 */             break;
/*      */           }
/*      */         } else { if ((str.length() < 4) || (str.charAt(3) != '-')) break;
/*  459 */           j = k;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  466 */     return k;
/*      */   }
/*      */ 
/*      */   private void sendServer(String paramString)
/*      */   {
/*  471 */     this.out.print(paramString);
/*  472 */     if (logger.isLoggable(300))
/*  473 */       logger.finest("Server [" + this.serverAddr + "] <-- " + paramString);
/*      */   }
/*      */ 
/*      */   private String getResponseString()
/*      */   {
/*  479 */     return (String)this.serverResponse.elementAt(0);
/*      */   }
/*      */ 
/*      */   private Vector<String> getResponseStrings()
/*      */   {
/*  484 */     return this.serverResponse;
/*      */   }
/*      */ 
/*      */   private boolean readReply()
/*      */     throws IOException
/*      */   {
/*  494 */     this.lastReplyCode = FtpReplyCode.find(readServerResponse());
/*      */ 
/*  496 */     if (this.lastReplyCode.isPositivePreliminary()) {
/*  497 */       this.replyPending = true;
/*  498 */       return true;
/*      */     }
/*  500 */     if ((this.lastReplyCode.isPositiveCompletion()) || (this.lastReplyCode.isPositiveIntermediate())) {
/*  501 */       if (this.lastReplyCode == FtpReplyCode.CLOSING_DATA_CONNECTION) {
/*  502 */         getTransferName();
/*      */       }
/*  504 */       return true;
/*      */     }
/*  506 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean issueCommand(String paramString)
/*      */     throws IOException
/*      */   {
/*  518 */     if (!isConnected()) {
/*  519 */       throw new IllegalStateException("Not connected");
/*      */     }
/*  521 */     if (this.replyPending)
/*      */       try {
/*  523 */         completePending();
/*      */       }
/*      */       catch (FtpProtocolException localFtpProtocolException)
/*      */       {
/*      */       }
/*  528 */     sendServer(paramString + "\r\n");
/*  529 */     return readReply();
/*      */   }
/*      */ 
/*      */   private void issueCommandCheck(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/*  540 */     if (!issueCommand(paramString))
/*  541 */       throw new FtpProtocolException(paramString + ":" + getResponseString(), getLastReplyCode());
/*      */   }
/*      */ 
/*      */   private Socket openPassiveDataConnection(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/*  557 */     InetSocketAddress localInetSocketAddress = null;
/*      */     String str1;
/*      */     Object localObject;
/*      */     String str2;
/*      */     int i;
/*  570 */     if (issueCommand("EPSV ALL"))
/*      */     {
/*  572 */       issueCommandCheck("EPSV");
/*  573 */       str1 = getResponseString();
/*      */ 
/*  581 */       if (epsvPat == null) {
/*  582 */         epsvPat = Pattern.compile("^229 .* \\(\\|\\|\\|(\\d+)\\|\\)");
/*      */       }
/*  584 */       localObject = epsvPat.matcher(str1);
/*  585 */       if (!((Matcher)localObject).find()) {
/*  586 */         throw new FtpProtocolException("EPSV failed : " + str1);
/*      */       }
/*      */ 
/*  589 */       str2 = ((Matcher)localObject).group(1);
/*  590 */       i = Integer.parseInt(str2);
/*  591 */       InetAddress localInetAddress = this.server.getInetAddress();
/*  592 */       if (localInetAddress != null) {
/*  593 */         localInetSocketAddress = new InetSocketAddress(localInetAddress, i);
/*      */       }
/*      */       else
/*      */       {
/*  599 */         localInetSocketAddress = InetSocketAddress.createUnresolved(this.serverAddr.getHostName(), i);
/*      */       }
/*      */     }
/*      */     else {
/*  603 */       issueCommandCheck("PASV");
/*  604 */       str1 = getResponseString();
/*      */ 
/*  618 */       if (pasvPat == null) {
/*  619 */         pasvPat = Pattern.compile("227 .* \\(?(\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,3}),(\\d{1,3}),(\\d{1,3})\\)?");
/*      */       }
/*  621 */       localObject = pasvPat.matcher(str1);
/*  622 */       if (!((Matcher)localObject).find()) {
/*  623 */         throw new FtpProtocolException("PASV failed : " + str1);
/*      */       }
/*      */ 
/*  626 */       i = Integer.parseInt(((Matcher)localObject).group(3)) + (Integer.parseInt(((Matcher)localObject).group(2)) << 8);
/*      */ 
/*  628 */       str2 = ((Matcher)localObject).group(1).replace(',', '.');
/*  629 */       localInetSocketAddress = new InetSocketAddress(str2, i);
/*      */     }
/*      */ 
/*  633 */     if (this.proxy != null) {
/*  634 */       if (this.proxy.type() == Proxy.Type.SOCKS)
/*  635 */         localObject = (Socket)AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Socket run()
/*      */           {
/*  639 */             return new Socket(FtpClient.this.proxy);
/*      */           }
/*      */         });
/*      */       else
/*  643 */         localObject = new Socket(Proxy.NO_PROXY);
/*      */     }
/*      */     else {
/*  646 */       localObject = new Socket();
/*      */     }
/*      */ 
/*  650 */     ((Socket)localObject).bind(new InetSocketAddress(this.server.getLocalAddress(), 0));
/*  651 */     if (this.connectTimeout >= 0) {
/*  652 */       ((Socket)localObject).connect(localInetSocketAddress, this.connectTimeout);
/*      */     }
/*  654 */     else if (defaultConnectTimeout > 0)
/*  655 */       ((Socket)localObject).connect(localInetSocketAddress, defaultConnectTimeout);
/*      */     else {
/*  657 */       ((Socket)localObject).connect(localInetSocketAddress);
/*      */     }
/*      */ 
/*  660 */     if (this.readTimeout >= 0)
/*  661 */       ((Socket)localObject).setSoTimeout(this.readTimeout);
/*  662 */     else if (defaultSoTimeout > 0) {
/*  663 */       ((Socket)localObject).setSoTimeout(defaultSoTimeout);
/*      */     }
/*  665 */     if (this.useCrypto) {
/*      */       try {
/*  667 */         localObject = this.sslFact.createSocket((Socket)localObject, localInetSocketAddress.getHostName(), localInetSocketAddress.getPort(), true);
/*      */       } catch (Exception localException) {
/*  669 */         throw new FtpProtocolException("Can't open secure data channel: " + localException);
/*      */       }
/*      */     }
/*  672 */     if (!issueCommand(paramString)) {
/*  673 */       ((Socket)localObject).close();
/*  674 */       if (getLastReplyCode() == FtpReplyCode.FILE_UNAVAILABLE)
/*      */       {
/*  676 */         throw new FileNotFoundException(paramString);
/*      */       }
/*  678 */       throw new FtpProtocolException(paramString + ":" + getResponseString(), getLastReplyCode());
/*      */     }
/*  680 */     return localObject;
/*      */   }
/*      */ 
/*      */   private Socket openDataConnection(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/*      */     Object localObject1;
/*  694 */     if (this.passiveMode) {
/*      */       try {
/*  696 */         return openPassiveDataConnection(paramString);
/*      */       }
/*      */       catch (FtpProtocolException localFtpProtocolException)
/*      */       {
/*  700 */         localObject1 = localFtpProtocolException.getMessage();
/*  701 */         if ((!((String)localObject1).startsWith("PASV")) && (!((String)localObject1).startsWith("EPSV"))) {
/*  702 */           throw localFtpProtocolException;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  710 */     if ((this.proxy != null) && (this.proxy.type() == Proxy.Type.SOCKS))
/*      */     {
/*  714 */       throw new FtpProtocolException("Passive mode failed");
/*  718 */     }
/*      */ ServerSocket localServerSocket = new ServerSocket(0, 1, this.server.getLocalAddress());
/*      */     Socket localSocket;
/*      */     try {
/*  720 */       localObject1 = localServerSocket.getInetAddress();
/*  721 */       if (((InetAddress)localObject1).isAnyLocalAddress()) {
/*  722 */         localObject1 = this.server.getLocalAddress();
/*      */       }
/*      */ 
/*  731 */       String str = "EPRT |" + ((localObject1 instanceof Inet6Address) ? "2" : "1") + "|" + ((InetAddress)localObject1).getHostAddress() + "|" + localServerSocket.getLocalPort() + "|";
/*      */ 
/*  733 */       if ((!issueCommand(str)) || (!issueCommand(paramString)))
/*      */       {
/*  735 */         str = "PORT ";
/*  736 */         byte[] arrayOfByte = ((InetAddress)localObject1).getAddress();
/*      */ 
/*  739 */         for (int i = 0; i < arrayOfByte.length; i++) {
/*  740 */           str = str + (arrayOfByte[i] & 0xFF) + ",";
/*      */         }
/*      */ 
/*  744 */         str = str + (localServerSocket.getLocalPort() >>> 8 & 0xFF) + "," + (localServerSocket.getLocalPort() & 0xFF);
/*  745 */         issueCommandCheck(str);
/*  746 */         issueCommandCheck(paramString);
/*      */       }
/*      */ 
/*  750 */       if (this.connectTimeout >= 0) {
/*  751 */         localServerSocket.setSoTimeout(this.connectTimeout);
/*      */       }
/*  753 */       else if (defaultConnectTimeout > 0) {
/*  754 */         localServerSocket.setSoTimeout(defaultConnectTimeout);
/*      */       }
/*      */ 
/*  757 */       localSocket = localServerSocket.accept();
/*  758 */       if (this.readTimeout >= 0) {
/*  759 */         localSocket.setSoTimeout(this.readTimeout);
/*      */       }
/*  761 */       else if (defaultSoTimeout > 0)
/*  762 */         localSocket.setSoTimeout(defaultSoTimeout);
/*      */     }
/*      */     finally
/*      */     {
/*  766 */       localServerSocket.close();
/*      */     }
/*  768 */     if (this.useCrypto) {
/*      */       try {
/*  770 */         localSocket = this.sslFact.createSocket(localSocket, this.serverAddr.getHostName(), this.serverAddr.getPort(), true);
/*      */       } catch (Exception localException) {
/*  772 */         throw new IOException(localException.getLocalizedMessage());
/*      */       }
/*      */     }
/*  775 */     return localSocket;
/*      */   }
/*      */ 
/*      */   private InputStream createInputStream(InputStream paramInputStream) {
/*  779 */     if (this.type == FtpClient.TransferType.ASCII) {
/*  780 */       return new TelnetInputStream(paramInputStream, false);
/*      */     }
/*  782 */     return paramInputStream;
/*      */   }
/*      */ 
/*      */   private OutputStream createOutputStream(OutputStream paramOutputStream) {
/*  786 */     if (this.type == FtpClient.TransferType.ASCII) {
/*  787 */       return new TelnetOutputStream(paramOutputStream, false);
/*      */     }
/*  789 */     return paramOutputStream;
/*      */   }
/*      */ 
/*      */   public static sun.net.ftp.FtpClient create()
/*      */   {
/*  806 */     return new FtpClient();
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient enablePassiveMode(boolean paramBoolean)
/*      */   {
/*  819 */     this.passiveMode = paramBoolean;
/*  820 */     return this;
/*      */   }
/*      */ 
/*      */   public boolean isPassiveModeEnabled()
/*      */   {
/*  829 */     return this.passiveMode;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient setConnectTimeout(int paramInt)
/*      */   {
/*  841 */     this.connectTimeout = paramInt;
/*  842 */     return this;
/*      */   }
/*      */ 
/*      */   public int getConnectTimeout()
/*      */   {
/*  852 */     return this.connectTimeout;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient setReadTimeout(int paramInt)
/*      */   {
/*  863 */     this.readTimeout = paramInt;
/*  864 */     return this;
/*      */   }
/*      */ 
/*      */   public int getReadTimeout()
/*      */   {
/*  874 */     return this.readTimeout;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient setProxy(Proxy paramProxy) {
/*  878 */     this.proxy = paramProxy;
/*  879 */     return this;
/*      */   }
/*      */ 
/*      */   public Proxy getProxy()
/*      */   {
/*  890 */     return this.proxy;
/*      */   }
/*      */ 
/*      */   private void tryConnect(InetSocketAddress paramInetSocketAddress, int paramInt)
/*      */     throws IOException
/*      */   {
/*  900 */     if (isConnected()) {
/*  901 */       disconnect();
/*      */     }
/*  903 */     this.server = doConnect(paramInetSocketAddress, paramInt);
/*      */     try {
/*  905 */       this.out = new PrintStream(new BufferedOutputStream(this.server.getOutputStream()), true, encoding);
/*      */     }
/*      */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*  908 */       throw new InternalError(encoding + "encoding not found");
/*      */     }
/*  910 */     this.in = new BufferedInputStream(this.server.getInputStream());
/*      */   }
/*      */ 
/*      */   private Socket doConnect(InetSocketAddress paramInetSocketAddress, int paramInt)
/*      */     throws IOException
/*      */   {
/*      */     Socket localSocket;
/*  915 */     if (this.proxy != null) {
/*  916 */       if (this.proxy.type() == Proxy.Type.SOCKS)
/*  917 */         localSocket = (Socket)AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Socket run()
/*      */           {
/*  921 */             return new Socket(FtpClient.this.proxy);
/*      */           }
/*      */         });
/*      */       else
/*  925 */         localSocket = new Socket(Proxy.NO_PROXY);
/*      */     }
/*      */     else {
/*  928 */       localSocket = new Socket();
/*      */     }
/*      */ 
/*  934 */     if (paramInt >= 0) {
/*  935 */       localSocket.connect(paramInetSocketAddress, paramInt);
/*      */     }
/*  937 */     else if (this.connectTimeout >= 0) {
/*  938 */       localSocket.connect(paramInetSocketAddress, this.connectTimeout);
/*      */     }
/*  940 */     else if (defaultConnectTimeout > 0)
/*  941 */       localSocket.connect(paramInetSocketAddress, defaultConnectTimeout);
/*      */     else {
/*  943 */       localSocket.connect(paramInetSocketAddress);
/*      */     }
/*      */ 
/*  947 */     if (this.readTimeout >= 0)
/*  948 */       localSocket.setSoTimeout(this.readTimeout);
/*  949 */     else if (defaultSoTimeout > 0) {
/*  950 */       localSocket.setSoTimeout(defaultSoTimeout);
/*      */     }
/*  952 */     return localSocket;
/*      */   }
/*      */ 
/*      */   private void disconnect() throws IOException {
/*  956 */     if (isConnected()) {
/*  957 */       this.server.close();
/*      */     }
/*  959 */     this.server = null;
/*  960 */     this.in = null;
/*  961 */     this.out = null;
/*  962 */     this.lastTransSize = -1L;
/*  963 */     this.lastFileName = null;
/*  964 */     this.restartOffset = 0L;
/*  965 */     this.welcomeMsg = null;
/*  966 */     this.lastReplyCode = null;
/*  967 */     this.serverResponse.setSize(0);
/*      */   }
/*      */ 
/*      */   public boolean isConnected()
/*      */   {
/*  976 */     return this.server != null;
/*      */   }
/*      */ 
/*      */   public SocketAddress getServerAddress() {
/*  980 */     return this.server == null ? null : this.server.getRemoteSocketAddress();
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient connect(SocketAddress paramSocketAddress) throws FtpProtocolException, IOException {
/*  984 */     return connect(paramSocketAddress, -1);
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient connect(SocketAddress paramSocketAddress, int paramInt)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/*  994 */     if (!(paramSocketAddress instanceof InetSocketAddress)) {
/*  995 */       throw new IllegalArgumentException("Wrong address type");
/*      */     }
/*  997 */     this.serverAddr = ((InetSocketAddress)paramSocketAddress);
/*  998 */     tryConnect(this.serverAddr, paramInt);
/*  999 */     if (!readReply()) {
/* 1000 */       throw new FtpProtocolException("Welcome message: " + getResponseString(), this.lastReplyCode);
/*      */     }
/*      */ 
/* 1003 */     this.welcomeMsg = getResponseString().substring(4);
/* 1004 */     return this;
/*      */   }
/*      */ 
/*      */   private void tryLogin(String paramString, char[] paramArrayOfChar) throws FtpProtocolException, IOException {
/* 1008 */     issueCommandCheck("USER " + paramString);
/*      */ 
/* 1013 */     if ((this.lastReplyCode == FtpReplyCode.NEED_PASSWORD) && 
/* 1014 */       (paramArrayOfChar != null) && (paramArrayOfChar.length > 0))
/* 1015 */       issueCommandCheck("PASS " + String.valueOf(paramArrayOfChar));
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient login(String paramString, char[] paramArrayOfChar)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1029 */     if (!isConnected()) {
/* 1030 */       throw new FtpProtocolException("Not connected yet", FtpReplyCode.BAD_SEQUENCE);
/*      */     }
/* 1032 */     if ((paramString == null) || (paramString.length() == 0)) {
/* 1033 */       throw new IllegalArgumentException("User name can't be null or empty");
/*      */     }
/* 1035 */     tryLogin(paramString, paramArrayOfChar);
/*      */ 
/* 1040 */     StringBuffer localStringBuffer = new StringBuffer();
/* 1041 */     for (int i = 0; i < this.serverResponse.size(); i++) {
/* 1042 */       String str = (String)this.serverResponse.elementAt(i);
/* 1043 */       if (str != null) {
/* 1044 */         if ((str.length() >= 4) && (str.startsWith("230")))
/*      */         {
/* 1046 */           str = str.substring(4);
/*      */         }
/* 1048 */         localStringBuffer.append(str);
/*      */       }
/*      */     }
/* 1051 */     this.welcomeMsg = localStringBuffer.toString();
/* 1052 */     this.loggedIn = true;
/* 1053 */     return this;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient login(String paramString1, char[] paramArrayOfChar, String paramString2)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1068 */     if (!isConnected()) {
/* 1069 */       throw new FtpProtocolException("Not connected yet", FtpReplyCode.BAD_SEQUENCE);
/*      */     }
/* 1071 */     if ((paramString1 == null) || (paramString1.length() == 0)) {
/* 1072 */       throw new IllegalArgumentException("User name can't be null or empty");
/*      */     }
/* 1074 */     tryLogin(paramString1, paramArrayOfChar);
/*      */ 
/* 1079 */     if (this.lastReplyCode == FtpReplyCode.NEED_ACCOUNT) {
/* 1080 */       issueCommandCheck("ACCT " + paramString2);
/*      */     }
/*      */ 
/* 1085 */     StringBuffer localStringBuffer = new StringBuffer();
/* 1086 */     if (this.serverResponse != null) {
/* 1087 */       for (String str : this.serverResponse) {
/* 1088 */         if (str != null) {
/* 1089 */           if ((str.length() >= 4) && (str.startsWith("230")))
/*      */           {
/* 1091 */             str = str.substring(4);
/*      */           }
/* 1093 */           localStringBuffer.append(str);
/*      */         }
/*      */       }
/*      */     }
/* 1097 */     this.welcomeMsg = localStringBuffer.toString();
/* 1098 */     this.loggedIn = true;
/* 1099 */     return this;
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/* 1108 */     if (isConnected()) {
/* 1109 */       issueCommand("QUIT");
/* 1110 */       this.loggedIn = false;
/*      */     }
/* 1112 */     disconnect();
/*      */   }
/*      */ 
/*      */   public boolean isLoggedIn()
/*      */   {
/* 1121 */     return this.loggedIn;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient changeDirectory(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1132 */     if ((paramString == null) || ("".equals(paramString))) {
/* 1133 */       throw new IllegalArgumentException("directory can't be null or empty");
/*      */     }
/*      */ 
/* 1136 */     issueCommandCheck("CWD " + paramString);
/* 1137 */     return this;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient changeToParentDirectory()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1147 */     issueCommandCheck("CDUP");
/* 1148 */     return this;
/*      */   }
/*      */ 
/*      */   public String getWorkingDirectory()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1160 */     issueCommandCheck("PWD");
/*      */ 
/* 1166 */     String str = getResponseString();
/* 1167 */     if (!str.startsWith("257")) {
/* 1168 */       return null;
/*      */     }
/* 1170 */     return str.substring(5, str.lastIndexOf('"'));
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient setRestartOffset(long paramLong)
/*      */   {
/* 1185 */     if (paramLong < 0L) {
/* 1186 */       throw new IllegalArgumentException("offset can't be negative");
/*      */     }
/* 1188 */     this.restartOffset = paramLong;
/* 1189 */     return this;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient getFile(String paramString, OutputStream paramOutputStream)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1207 */     int i = 1500;
/*      */     Socket localSocket;
/*      */     InputStream localInputStream;
/*      */     byte[] arrayOfByte;
/*      */     int j;
/* 1208 */     if (this.restartOffset > 0L)
/*      */     {
/*      */       try {
/* 1211 */         localSocket = openDataConnection("REST " + this.restartOffset);
/*      */       } finally {
/* 1213 */         this.restartOffset = 0L;
/*      */       }
/* 1215 */       issueCommandCheck("RETR " + paramString);
/* 1216 */       getTransferSize();
/* 1217 */       localInputStream = createInputStream(localSocket.getInputStream());
/* 1218 */       arrayOfByte = new byte[i * 10];
/*      */ 
/* 1220 */       while ((j = localInputStream.read(arrayOfByte)) >= 0) {
/* 1221 */         if (j > 0) {
/* 1222 */           paramOutputStream.write(arrayOfByte, 0, j);
/*      */         }
/*      */       }
/* 1225 */       localInputStream.close();
/*      */     } else {
/* 1227 */       localSocket = openDataConnection("RETR " + paramString);
/* 1228 */       getTransferSize();
/* 1229 */       localInputStream = createInputStream(localSocket.getInputStream());
/* 1230 */       arrayOfByte = new byte[i * 10];
/*      */ 
/* 1232 */       while ((j = localInputStream.read(arrayOfByte)) >= 0) {
/* 1233 */         if (j > 0) {
/* 1234 */           paramOutputStream.write(arrayOfByte, 0, j);
/*      */         }
/*      */       }
/* 1237 */       localInputStream.close();
/*      */     }
/* 1239 */     return completePending();
/*      */   }
/*      */ 
/*      */   public InputStream getFileStream(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1255 */     if (this.restartOffset > 0L) {
/*      */       try {
/* 1257 */         localSocket = openDataConnection("REST " + this.restartOffset);
/*      */       } finally {
/* 1259 */         this.restartOffset = 0L;
/*      */       }
/* 1261 */       if (localSocket == null) {
/* 1262 */         return null;
/*      */       }
/* 1264 */       issueCommandCheck("RETR " + paramString);
/* 1265 */       getTransferSize();
/* 1266 */       return createInputStream(localSocket.getInputStream());
/*      */     }
/*      */ 
/* 1269 */     Socket localSocket = openDataConnection("RETR " + paramString);
/* 1270 */     if (localSocket == null) {
/* 1271 */       return null;
/*      */     }
/* 1273 */     getTransferSize();
/* 1274 */     return createInputStream(localSocket.getInputStream());
/*      */   }
/*      */ 
/*      */   public OutputStream putFileStream(String paramString, boolean paramBoolean)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1303 */     String str = paramBoolean ? "STOU " : "STOR ";
/* 1304 */     Socket localSocket = openDataConnection(str + paramString);
/* 1305 */     if (localSocket == null) {
/* 1306 */       return null;
/*      */     }
/* 1308 */     if (this.type == FtpClient.TransferType.BINARY) {
/* 1309 */       return localSocket.getOutputStream();
/*      */     }
/* 1311 */     return new TelnetOutputStream(localSocket.getOutputStream(), false);
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient putFile(String paramString, InputStream paramInputStream, boolean paramBoolean)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1333 */     String str = paramBoolean ? "STOU " : "STOR ";
/* 1334 */     int i = 1500;
/* 1335 */     if (this.type == FtpClient.TransferType.BINARY) {
/* 1336 */       Socket localSocket = openDataConnection(str + paramString);
/* 1337 */       OutputStream localOutputStream = createOutputStream(localSocket.getOutputStream());
/* 1338 */       byte[] arrayOfByte = new byte[i * 10];
/*      */       int j;
/* 1340 */       while ((j = paramInputStream.read(arrayOfByte)) >= 0) {
/* 1341 */         if (j > 0) {
/* 1342 */           localOutputStream.write(arrayOfByte, 0, j);
/*      */         }
/*      */       }
/* 1345 */       localOutputStream.close();
/*      */     }
/* 1347 */     return completePending();
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient appendFile(String paramString, InputStream paramInputStream)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1363 */     int i = 1500;
/* 1364 */     Socket localSocket = openDataConnection("APPE " + paramString);
/* 1365 */     OutputStream localOutputStream = createOutputStream(localSocket.getOutputStream());
/* 1366 */     byte[] arrayOfByte = new byte[i * 10];
/*      */     int j;
/* 1368 */     while ((j = paramInputStream.read(arrayOfByte)) >= 0) {
/* 1369 */       if (j > 0) {
/* 1370 */         localOutputStream.write(arrayOfByte, 0, j);
/*      */       }
/*      */     }
/* 1373 */     localOutputStream.close();
/* 1374 */     return completePending();
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient rename(String paramString1, String paramString2)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1385 */     issueCommandCheck("RNFR " + paramString1);
/* 1386 */     issueCommandCheck("RNTO " + paramString2);
/* 1387 */     return this;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient deleteFile(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1399 */     issueCommandCheck("DELE " + paramString);
/* 1400 */     return this;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient makeDirectory(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1412 */     issueCommandCheck("MKD " + paramString);
/* 1413 */     return this;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient removeDirectory(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1426 */     issueCommandCheck("RMD " + paramString);
/* 1427 */     return this;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient noop()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1437 */     issueCommandCheck("NOOP");
/* 1438 */     return this;
/*      */   }
/*      */ 
/*      */   public String getStatus(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1457 */     issueCommandCheck("STAT " + paramString);
/*      */ 
/* 1482 */     Vector localVector = getResponseStrings();
/* 1483 */     StringBuffer localStringBuffer = new StringBuffer();
/* 1484 */     for (int i = 1; i < localVector.size() - 1; i++) {
/* 1485 */       localStringBuffer.append((String)localVector.get(i));
/*      */     }
/* 1487 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   public List<String> getFeatures()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1519 */     ArrayList localArrayList = new ArrayList();
/* 1520 */     issueCommandCheck("FEAT");
/* 1521 */     Vector localVector = getResponseStrings();
/*      */ 
/* 1524 */     for (int i = 1; i < localVector.size() - 1; i++) {
/* 1525 */       String str = (String)localVector.get(i);
/*      */ 
/* 1527 */       localArrayList.add(str.substring(1, str.length() - 1));
/*      */     }
/* 1529 */     return localArrayList;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient abort()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1540 */     issueCommandCheck("ABOR");
/*      */ 
/* 1559 */     return this;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient completePending()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1596 */     while (this.replyPending) {
/* 1597 */       this.replyPending = false;
/* 1598 */       if (!readReply()) {
/* 1599 */         throw new FtpProtocolException(getLastResponseString(), this.lastReplyCode);
/*      */       }
/*      */     }
/* 1602 */     return this;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient reInit()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1611 */     issueCommandCheck("REIN");
/* 1612 */     this.loggedIn = false;
/* 1613 */     if ((this.useCrypto) && 
/* 1614 */       ((this.server instanceof SSLSocket))) {
/* 1615 */       SSLSession localSSLSession = ((SSLSocket)this.server).getSession();
/* 1616 */       localSSLSession.invalidate();
/*      */ 
/* 1618 */       this.server = this.oldSocket;
/* 1619 */       this.oldSocket = null;
/*      */       try {
/* 1621 */         this.out = new PrintStream(new BufferedOutputStream(this.server.getOutputStream()), true, encoding);
/*      */       }
/*      */       catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 1624 */         throw new InternalError(encoding + "encoding not found");
/*      */       }
/* 1626 */       this.in = new BufferedInputStream(this.server.getInputStream());
/*      */     }
/*      */ 
/* 1629 */     this.useCrypto = false;
/* 1630 */     return this;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient setType(FtpClient.TransferType paramTransferType)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1642 */     String str = "NOOP";
/*      */ 
/* 1644 */     this.type = paramTransferType;
/* 1645 */     if (paramTransferType == FtpClient.TransferType.ASCII) {
/* 1646 */       str = "TYPE A";
/*      */     }
/* 1648 */     if (paramTransferType == FtpClient.TransferType.BINARY) {
/* 1649 */       str = "TYPE I";
/*      */     }
/* 1651 */     if (paramTransferType == FtpClient.TransferType.EBCDIC) {
/* 1652 */       str = "TYPE E";
/*      */     }
/* 1654 */     issueCommandCheck(str);
/* 1655 */     return this;
/*      */   }
/*      */ 
/*      */   public InputStream list(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1673 */     Socket localSocket = openDataConnection("LIST " + paramString);
/* 1674 */     if (localSocket != null) {
/* 1675 */       return createInputStream(localSocket.getInputStream());
/*      */     }
/* 1677 */     return null;
/*      */   }
/*      */ 
/*      */   public InputStream nameList(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1697 */     Socket localSocket = openDataConnection("NLST " + paramString);
/* 1698 */     if (localSocket != null) {
/* 1699 */       return createInputStream(localSocket.getInputStream());
/*      */     }
/* 1701 */     return null;
/*      */   }
/*      */ 
/*      */   public long getSize(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1718 */     if ((paramString == null) || (paramString.length() == 0)) {
/* 1719 */       throw new IllegalArgumentException("path can't be null or empty");
/*      */     }
/* 1721 */     issueCommandCheck("SIZE " + paramString);
/* 1722 */     if (this.lastReplyCode == FtpReplyCode.FILE_STATUS) {
/* 1723 */       String str = getResponseString();
/* 1724 */       str = str.substring(4, str.length() - 1);
/* 1725 */       return Long.parseLong(str);
/*      */     }
/* 1727 */     return -1L;
/*      */   }
/*      */ 
/*      */   public Date getLastModified(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1755 */     issueCommandCheck("MDTM " + paramString);
/* 1756 */     if (this.lastReplyCode == FtpReplyCode.FILE_STATUS) {
/* 1757 */       String str = getResponseString().substring(4);
/* 1758 */       Date localDate = null;
/* 1759 */       for (SimpleDateFormat localSimpleDateFormat : dateFormats) {
/*      */         try {
/* 1761 */           localDate = localSimpleDateFormat.parse(str);
/*      */         } catch (ParseException localParseException) {
/*      */         }
/* 1764 */         if (localDate != null) {
/* 1765 */           return localDate;
/*      */         }
/*      */       }
/*      */     }
/* 1769 */     return null;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient setDirParser(FtpDirParser paramFtpDirParser)
/*      */   {
/* 1783 */     this.parser = paramFtpDirParser;
/* 1784 */     return this;
/*      */   }
/*      */ 
/*      */   public Iterator<FtpDirEntry> listFiles(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1865 */     Socket localSocket = null;
/* 1866 */     BufferedReader localBufferedReader = null;
/*      */     try {
/* 1868 */       localSocket = openDataConnection("MLSD " + paramString);
/*      */     }
/*      */     catch (FtpProtocolException localFtpProtocolException)
/*      */     {
/*      */     }
/*      */ 
/* 1874 */     if (localSocket != null) {
/* 1875 */       localBufferedReader = new BufferedReader(new InputStreamReader(localSocket.getInputStream()));
/* 1876 */       return new FtpFileIterator(this.mlsxParser, localBufferedReader);
/*      */     }
/* 1878 */     localSocket = openDataConnection("LIST " + paramString);
/* 1879 */     if (localSocket != null) {
/* 1880 */       localBufferedReader = new BufferedReader(new InputStreamReader(localSocket.getInputStream()));
/* 1881 */       return new FtpFileIterator(this.parser, localBufferedReader);
/*      */     }
/*      */ 
/* 1884 */     return null;
/*      */   }
/*      */ 
/*      */   private boolean sendSecurityData(byte[] paramArrayOfByte) throws IOException {
/* 1888 */     BASE64Encoder localBASE64Encoder = new BASE64Encoder();
/* 1889 */     String str = localBASE64Encoder.encode(paramArrayOfByte);
/* 1890 */     return issueCommand("ADAT " + str);
/*      */   }
/*      */ 
/*      */   private byte[] getSecurityData() {
/* 1894 */     String str = getLastResponseString();
/* 1895 */     if (str.substring(4, 9).equalsIgnoreCase("ADAT=")) {
/* 1896 */       BASE64Decoder localBASE64Decoder = new BASE64Decoder();
/*      */       try
/*      */       {
/* 1900 */         return localBASE64Decoder.decodeBuffer(str.substring(9, str.length() - 1));
/*      */       }
/*      */       catch (IOException localIOException) {
/*      */       }
/*      */     }
/* 1905 */     return null;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient useKerberos()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1952 */     return this;
/*      */   }
/*      */ 
/*      */   public String getWelcomeMsg()
/*      */   {
/* 1962 */     return this.welcomeMsg;
/*      */   }
/*      */ 
/*      */   public FtpReplyCode getLastReplyCode()
/*      */   {
/* 1971 */     return this.lastReplyCode;
/*      */   }
/*      */ 
/*      */   public String getLastResponseString()
/*      */   {
/* 1981 */     StringBuffer localStringBuffer = new StringBuffer();
/* 1982 */     if (this.serverResponse != null) {
/* 1983 */       for (String str : this.serverResponse) {
/* 1984 */         if (str != null) {
/* 1985 */           localStringBuffer.append(str);
/*      */         }
/*      */       }
/*      */     }
/* 1989 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   public long getLastTransferSize()
/*      */   {
/* 2001 */     return this.lastTransSize;
/*      */   }
/*      */ 
/*      */   public String getLastFileName()
/*      */   {
/* 2014 */     return this.lastFileName;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient startSecureSession()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 2031 */     if (!isConnected()) {
/* 2032 */       throw new FtpProtocolException("Not connected yet", FtpReplyCode.BAD_SEQUENCE);
/*      */     }
/* 2034 */     if (this.sslFact == null) {
/*      */       try {
/* 2036 */         this.sslFact = ((SSLSocketFactory)SSLSocketFactory.getDefault());
/*      */       } catch (Exception localException1) {
/* 2038 */         throw new IOException(localException1.getLocalizedMessage());
/*      */       }
/*      */     }
/* 2041 */     issueCommandCheck("AUTH TLS");
/* 2042 */     Socket localSocket = null;
/*      */     try {
/* 2044 */       localSocket = this.sslFact.createSocket(this.server, this.serverAddr.getHostName(), this.serverAddr.getPort(), true);
/*      */     } catch (SSLException localSSLException) {
/*      */       try {
/* 2047 */         disconnect();
/*      */       } catch (Exception localException2) {
/*      */       }
/* 2050 */       throw localSSLException;
/*      */     }
/*      */ 
/* 2053 */     this.oldSocket = this.server;
/* 2054 */     this.server = localSocket;
/*      */     try {
/* 2056 */       this.out = new PrintStream(new BufferedOutputStream(this.server.getOutputStream()), true, encoding);
/*      */     }
/*      */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 2059 */       throw new InternalError(encoding + "encoding not found");
/*      */     }
/* 2061 */     this.in = new BufferedInputStream(this.server.getInputStream());
/*      */ 
/* 2063 */     issueCommandCheck("PBSZ 0");
/* 2064 */     issueCommandCheck("PROT P");
/* 2065 */     this.useCrypto = true;
/* 2066 */     return this;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient endSecureSession()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 2079 */     if (!this.useCrypto) {
/* 2080 */       return this;
/*      */     }
/*      */ 
/* 2083 */     issueCommandCheck("CCC");
/* 2084 */     issueCommandCheck("PROT C");
/* 2085 */     this.useCrypto = false;
/*      */ 
/* 2087 */     this.server = this.oldSocket;
/* 2088 */     this.oldSocket = null;
/*      */     try {
/* 2090 */       this.out = new PrintStream(new BufferedOutputStream(this.server.getOutputStream()), true, encoding);
/*      */     }
/*      */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 2093 */       throw new InternalError(encoding + "encoding not found");
/*      */     }
/* 2095 */     this.in = new BufferedInputStream(this.server.getInputStream());
/*      */ 
/* 2097 */     return this;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient allocate(long paramLong)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 2109 */     issueCommandCheck("ALLO " + paramLong);
/* 2110 */     return this;
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient structureMount(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 2124 */     issueCommandCheck("SMNT " + paramString);
/* 2125 */     return this;
/*      */   }
/*      */ 
/*      */   public String getSystem()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 2138 */     issueCommandCheck("SYST");
/*      */ 
/* 2142 */     String str = getResponseString();
/*      */ 
/* 2144 */     return str.substring(4);
/*      */   }
/*      */ 
/*      */   public String getHelp(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 2158 */     issueCommandCheck("HELP " + paramString);
/*      */ 
/* 2177 */     Vector localVector = getResponseStrings();
/* 2178 */     if (localVector.size() == 1)
/*      */     {
/* 2180 */       return ((String)localVector.get(0)).substring(4);
/*      */     }
/*      */ 
/* 2184 */     StringBuffer localStringBuffer = new StringBuffer();
/* 2185 */     for (int i = 1; i < localVector.size() - 1; i++) {
/* 2186 */       localStringBuffer.append(((String)localVector.get(i)).substring(3));
/*      */     }
/* 2188 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   public sun.net.ftp.FtpClient siteCmd(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 2201 */     issueCommandCheck("SITE " + paramString);
/* 2202 */     return this;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  109 */     int[] arrayOfInt = { 0, 0 };
/*  110 */     final String[] arrayOfString = { null };
/*      */ 
/*  112 */     AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run()
/*      */       {
/*  116 */         this.val$vals[0] = Integer.getInteger("sun.net.client.defaultReadTimeout", 0).intValue();
/*  117 */         this.val$vals[1] = Integer.getInteger("sun.net.client.defaultConnectTimeout", 0).intValue();
/*  118 */         arrayOfString[0] = System.getProperty("file.encoding", "ISO8859_1");
/*  119 */         return null;
/*      */       }
/*      */     });
/*  122 */     if (arrayOfInt[0] == 0)
/*  123 */       defaultSoTimeout = -1;
/*      */     else {
/*  125 */       defaultSoTimeout = arrayOfInt[0];
/*      */     }
/*      */ 
/*  128 */     if (arrayOfInt[1] == 0)
/*  129 */       defaultConnectTimeout = -1;
/*      */     else {
/*  131 */       defaultConnectTimeout = arrayOfInt[1];
/*      */     }
/*      */ 
/*  134 */     encoding = arrayOfString[0];
/*      */     try {
/*  136 */       if (!isASCIISuperset(encoding))
/*  137 */         encoding = "ISO8859_1";
/*      */     }
/*      */     catch (Exception localException) {
/*  140 */       encoding = "ISO8859_1";
/*      */     }
/*      */ 
/*  143 */     patterns = new Pattern[patStrings.length];
/*  144 */     for (int j = 0; j < patStrings.length; j++) {
/*  145 */       patterns[j] = Pattern.compile(patStrings[j]);
/*      */     }
/*      */ 
/*  367 */     transPat = null;
/*      */ 
/*  544 */     epsvPat = null;
/*  545 */     pasvPat = null;
/*      */ 
/* 1729 */     MDTMformats = new String[] { "yyyyMMddHHmmss.SSS", "yyyyMMddHHmmss" };
/*      */ 
/* 1733 */     dateFormats = new SimpleDateFormat[MDTMformats.length];
/*      */ 
/* 1736 */     for (int i = 0; i < MDTMformats.length; i++) {
/* 1737 */       dateFormats[i] = new SimpleDateFormat(MDTMformats[i]);
/* 1738 */       dateFormats[i].setTimeZone(TimeZone.getTimeZone("GMT"));
/*      */     }
/*      */   }
/*      */ 
/*      */   private class DefaultParser
/*      */     implements FtpDirParser
/*      */   {
/*      */     private DefaultParser()
/*      */     {
/*      */     }
/*      */ 
/*      */     public FtpDirEntry parseLine(String paramString)
/*      */     {
/*  205 */       String str1 = null;
/*  206 */       String str2 = null;
/*  207 */       String str3 = null;
/*  208 */       String str4 = null;
/*  209 */       String str5 = null;
/*  210 */       String str6 = null;
/*  211 */       String str7 = null;
/*  212 */       boolean bool = false;
/*  213 */       Calendar localCalendar = Calendar.getInstance();
/*  214 */       int i = localCalendar.get(1);
/*      */ 
/*  216 */       Matcher localMatcher1 = null;
/*  217 */       for (int j = 0; j < FtpClient.patterns.length; j++) {
/*  218 */         localMatcher1 = FtpClient.patterns[j].matcher(paramString);
/*  219 */         if (localMatcher1.find())
/*      */         {
/*  222 */           str4 = localMatcher1.group(FtpClient.patternGroups[j][0]);
/*  223 */           str2 = localMatcher1.group(FtpClient.patternGroups[j][1]);
/*  224 */           str1 = localMatcher1.group(FtpClient.patternGroups[j][2]);
/*  225 */           if (FtpClient.patternGroups[j][4] > 0)
/*  226 */             str1 = str1 + ", " + localMatcher1.group(FtpClient.patternGroups[j][4]);
/*  227 */           else if (FtpClient.patternGroups[j][3] > 0) {
/*  228 */             str1 = str1 + ", " + String.valueOf(i);
/*      */           }
/*  230 */           if (FtpClient.patternGroups[j][3] > 0) {
/*  231 */             str3 = localMatcher1.group(FtpClient.patternGroups[j][3]);
/*      */           }
/*  233 */           if (FtpClient.patternGroups[j][5] > 0) {
/*  234 */             str5 = localMatcher1.group(FtpClient.patternGroups[j][5]);
/*  235 */             bool = str5.startsWith("d");
/*      */           }
/*  237 */           if (FtpClient.patternGroups[j][6] > 0) {
/*  238 */             str6 = localMatcher1.group(FtpClient.patternGroups[j][6]);
/*      */           }
/*  240 */           if (FtpClient.patternGroups[j][7] > 0) {
/*  241 */             str7 = localMatcher1.group(FtpClient.patternGroups[j][7]);
/*      */           }
/*      */ 
/*  244 */           if ("<DIR>".equals(str2)) {
/*  245 */             bool = true;
/*  246 */             str2 = null;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  251 */       if (str4 != null) {
/*      */         Date localDate;
/*      */         try {
/*  254 */           localDate = FtpClient.this.df.parse(str1);
/*      */         } catch (Exception localException) {
/*  256 */           localDate = null;
/*      */         }
/*  258 */         if ((localDate != null) && (str3 != null)) {
/*  259 */           int k = str3.indexOf(":");
/*  260 */           localCalendar.setTime(localDate);
/*  261 */           localCalendar.set(10, Integer.parseInt(str3.substring(0, k)));
/*  262 */           localCalendar.set(12, Integer.parseInt(str3.substring(k + 1)));
/*  263 */           localDate = localCalendar.getTime();
/*      */         }
/*      */ 
/*  267 */         Matcher localMatcher2 = FtpClient.linkp.matcher(str4);
/*  268 */         if (localMatcher2.find())
/*      */         {
/*  270 */           str4 = localMatcher2.group(1);
/*      */         }
/*  272 */         boolean[][] arrayOfBoolean = new boolean[3][3];
/*  273 */         for (int m = 0; m < 3; m++) {
/*  274 */           for (int n = 0; n < 3; n++) {
/*  275 */             arrayOfBoolean[m][n] = (str5.charAt(m * 3 + n) != '-' ? 1 : 0);
/*      */           }
/*      */         }
/*  278 */         FtpDirEntry localFtpDirEntry = new FtpDirEntry(str4);
/*  279 */         localFtpDirEntry.setUser(str6).setGroup(str7);
/*  280 */         localFtpDirEntry.setSize(Long.parseLong(str2)).setLastModified(localDate);
/*  281 */         localFtpDirEntry.setPermissions(arrayOfBoolean);
/*  282 */         localFtpDirEntry.setType(paramString.charAt(0) == 'l' ? FtpDirEntry.Type.LINK : bool ? FtpDirEntry.Type.DIR : FtpDirEntry.Type.FILE);
/*  283 */         return localFtpDirEntry;
/*      */       }
/*  285 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class FtpFileIterator
/*      */     implements Iterator<FtpDirEntry>, Closeable
/*      */   {
/* 1789 */     private BufferedReader in = null;
/* 1790 */     private FtpDirEntry nextFile = null;
/* 1791 */     private FtpDirParser fparser = null;
/* 1792 */     private boolean eof = false;
/*      */ 
/*      */     public FtpFileIterator(FtpDirParser paramBufferedReader, BufferedReader arg3)
/*      */     {
/*      */       Object localObject;
/* 1795 */       this.in = localObject;
/* 1796 */       this.fparser = paramBufferedReader;
/* 1797 */       readNext();
/*      */     }
/*      */ 
/*      */     private void readNext() {
/* 1801 */       this.nextFile = null;
/* 1802 */       if (this.eof) {
/* 1803 */         return;
/*      */       }
/* 1805 */       String str = null;
/*      */       try {
/*      */         do {
/* 1808 */           str = this.in.readLine();
/* 1809 */           if (str != null) {
/* 1810 */             this.nextFile = this.fparser.parseLine(str);
/* 1811 */             if (this.nextFile != null)
/* 1812 */               return;
/*      */           }
/*      */         }
/* 1815 */         while (str != null);
/* 1816 */         this.in.close();
/*      */       } catch (IOException localIOException) {
/*      */       }
/* 1819 */       this.eof = true;
/*      */     }
/*      */ 
/*      */     public boolean hasNext() {
/* 1823 */       return this.nextFile != null;
/*      */     }
/*      */ 
/*      */     public FtpDirEntry next() {
/* 1827 */       FtpDirEntry localFtpDirEntry = this.nextFile;
/* 1828 */       readNext();
/* 1829 */       return localFtpDirEntry;
/*      */     }
/*      */ 
/*      */     public void remove() {
/* 1833 */       throw new UnsupportedOperationException("Not supported yet.");
/*      */     }
/*      */ 
/*      */     public void close() throws IOException {
/* 1837 */       if ((this.in != null) && (!this.eof)) {
/* 1838 */         this.in.close();
/*      */       }
/* 1840 */       this.eof = true;
/* 1841 */       this.nextFile = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class MLSxParser
/*      */     implements FtpDirParser
/*      */   {
/*  291 */     private SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
/*      */ 
/*      */     private MLSxParser() {  } 
/*  294 */     public FtpDirEntry parseLine(String paramString) { String str1 = null;
/*  295 */       int i = paramString.lastIndexOf(";");
/*  296 */       if (i > 0) {
/*  297 */         str1 = paramString.substring(i + 1).trim();
/*  298 */         paramString = paramString.substring(0, i);
/*      */       } else {
/*  300 */         str1 = paramString.trim();
/*  301 */         paramString = "";
/*      */       }
/*  303 */       FtpDirEntry localFtpDirEntry = new FtpDirEntry(str1);
/*      */       Object localObject;
/*  304 */       while (!paramString.isEmpty())
/*      */       {
/*  306 */         i = paramString.indexOf(";");
/*  307 */         if (i > 0) {
/*  308 */           str2 = paramString.substring(0, i);
/*  309 */           paramString = paramString.substring(i + 1);
/*      */         } else {
/*  311 */           str2 = paramString;
/*  312 */           paramString = "";
/*      */         }
/*  314 */         i = str2.indexOf("=");
/*  315 */         if (i > 0) {
/*  316 */           localObject = str2.substring(0, i);
/*  317 */           String str3 = str2.substring(i + 1);
/*  318 */           localFtpDirEntry.addFact((String)localObject, str3);
/*      */         }
/*      */       }
/*  321 */       String str2 = localFtpDirEntry.getFact("Size");
/*  322 */       if (str2 != null) {
/*  323 */         localFtpDirEntry.setSize(Long.parseLong(str2));
/*      */       }
/*  325 */       str2 = localFtpDirEntry.getFact("Modify");
/*  326 */       if (str2 != null) {
/*  327 */         localObject = null;
/*      */         try {
/*  329 */           localObject = this.df.parse(str2);
/*      */         } catch (ParseException localParseException1) {
/*      */         }
/*  332 */         if (localObject != null) {
/*  333 */           localFtpDirEntry.setLastModified((Date)localObject);
/*      */         }
/*      */       }
/*  336 */       str2 = localFtpDirEntry.getFact("Create");
/*  337 */       if (str2 != null) {
/*  338 */         localObject = null;
/*      */         try {
/*  340 */           localObject = this.df.parse(str2);
/*      */         } catch (ParseException localParseException2) {
/*      */         }
/*  343 */         if (localObject != null) {
/*  344 */           localFtpDirEntry.setCreated((Date)localObject);
/*      */         }
/*      */       }
/*  347 */       str2 = localFtpDirEntry.getFact("Type");
/*  348 */       if (str2 != null) {
/*  349 */         if (str2.equalsIgnoreCase("file")) {
/*  350 */           localFtpDirEntry.setType(FtpDirEntry.Type.FILE);
/*      */         }
/*  352 */         if (str2.equalsIgnoreCase("dir")) {
/*  353 */           localFtpDirEntry.setType(FtpDirEntry.Type.DIR);
/*      */         }
/*  355 */         if (str2.equalsIgnoreCase("cdir")) {
/*  356 */           localFtpDirEntry.setType(FtpDirEntry.Type.CDIR);
/*      */         }
/*  358 */         if (str2.equalsIgnoreCase("pdir")) {
/*  359 */           localFtpDirEntry.setType(FtpDirEntry.Type.PDIR);
/*      */         }
/*      */       }
/*  362 */       return localFtpDirEntry;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.ftp.impl.FtpClient
 * JD-Core Version:    0.6.2
 */