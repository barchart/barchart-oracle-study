/*      */ package sun.misc;
/*      */ 
/*      */ import java.io.Closeable;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FilePermission;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.net.HttpURLConnection;
/*      */ import java.net.JarURLConnection;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.SocketPermission;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.net.URLStreamHandler;
/*      */ import java.net.URLStreamHandlerFactory;
/*      */ import java.security.AccessControlException;
/*      */ import java.security.AccessController;
/*      */ import java.security.CodeSigner;
/*      */ import java.security.Permission;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.security.cert.Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Set;
/*      */ import java.util.Stack;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.jar.Attributes;
/*      */ import java.util.jar.Attributes.Name;
/*      */ import java.util.jar.JarEntry;
/*      */ import java.util.jar.JarFile;
/*      */ import java.util.jar.Manifest;
/*      */ import java.util.zip.ZipEntry;
/*      */ import sun.net.util.URLUtil;
/*      */ import sun.net.www.ParseUtil;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ 
/*      */ public class URLClassPath
/*      */ {
/*      */   static final String USER_AGENT_JAVA_VERSION = "UA-Java-Version";
/*   68 */   static final String JAVA_VERSION = (String)AccessController.doPrivileged(new GetPropertyAction("java.version"));
/*      */ 
/*   70 */   private static final boolean DEBUG = AccessController.doPrivileged(new GetPropertyAction("sun.misc.URLClassPath.debug")) != null;
/*      */ 
/*   75 */   private ArrayList<URL> path = new ArrayList();
/*      */ 
/*   78 */   Stack<URL> urls = new Stack();
/*      */ 
/*   81 */   ArrayList<Loader> loaders = new ArrayList();
/*      */ 
/*   84 */   HashMap<String, Loader> lmap = new HashMap();
/*      */   private URLStreamHandler jarHandler;
/*   90 */   private boolean closed = false;
/*      */ 
/*      */   public URLClassPath(URL[] paramArrayOfURL, URLStreamHandlerFactory paramURLStreamHandlerFactory)
/*      */   {
/*  103 */     for (int i = 0; i < paramArrayOfURL.length; i++) {
/*  104 */       this.path.add(paramArrayOfURL[i]);
/*      */     }
/*  106 */     push(paramArrayOfURL);
/*  107 */     if (paramURLStreamHandlerFactory != null)
/*  108 */       this.jarHandler = paramURLStreamHandlerFactory.createURLStreamHandler("jar");
/*      */   }
/*      */ 
/*      */   public URLClassPath(URL[] paramArrayOfURL)
/*      */   {
/*  113 */     this(paramArrayOfURL, null);
/*      */   }
/*      */ 
/*      */   public synchronized List<IOException> closeLoaders() {
/*  117 */     if (this.closed) {
/*  118 */       return Collections.emptyList();
/*      */     }
/*  120 */     LinkedList localLinkedList = new LinkedList();
/*  121 */     for (Loader localLoader : this.loaders) {
/*      */       try {
/*  123 */         localLoader.close();
/*      */       } catch (IOException localIOException) {
/*  125 */         localLinkedList.add(localIOException);
/*      */       }
/*      */     }
/*  128 */     this.closed = true;
/*  129 */     return localLinkedList;
/*      */   }
/*      */ 
/*      */   public synchronized void addURL(URL paramURL)
/*      */   {
/*  140 */     if (this.closed)
/*  141 */       return;
/*  142 */     synchronized (this.urls) {
/*  143 */       if ((paramURL == null) || (this.path.contains(paramURL))) {
/*  144 */         return;
/*      */       }
/*  146 */       this.urls.add(0, paramURL);
/*  147 */       this.path.add(paramURL);
/*      */     }
/*      */   }
/*      */ 
/*      */   public URL[] getURLs()
/*      */   {
/*  155 */     synchronized (this.urls) {
/*  156 */       return (URL[])this.path.toArray(new URL[this.path.size()]);
/*      */     }
/*      */   }
/*      */ 
/*      */   public URL findResource(String paramString, boolean paramBoolean)
/*      */   {
/*      */     Loader localLoader;
/*  171 */     for (int i = 0; (localLoader = getLoader(i)) != null; i++) {
/*  172 */       URL localURL = localLoader.findResource(paramString, paramBoolean);
/*  173 */       if (localURL != null) {
/*  174 */         return localURL;
/*      */       }
/*      */     }
/*  177 */     return null;
/*      */   }
/*      */ 
/*      */   public Resource getResource(String paramString, boolean paramBoolean)
/*      */   {
/*  189 */     if (DEBUG)
/*  190 */       System.err.println("URLClassPath.getResource(\"" + paramString + "\")");
/*      */     Loader localLoader;
/*  194 */     for (int i = 0; (localLoader = getLoader(i)) != null; i++) {
/*  195 */       Resource localResource = localLoader.getResource(paramString, paramBoolean);
/*  196 */       if (localResource != null) {
/*  197 */         return localResource;
/*      */       }
/*      */     }
/*  200 */     return null;
/*      */   }
/*      */ 
/*      */   public Enumeration<URL> findResources(final String paramString, final boolean paramBoolean)
/*      */   {
/*  212 */     return new Enumeration() {
/*  213 */       private int index = 0;
/*  214 */       private URL url = null;
/*      */ 
/*      */       private boolean next() {
/*  217 */         if (this.url != null)
/*  218 */           return true;
/*      */         URLClassPath.Loader localLoader;
/*  221 */         while ((localLoader = URLClassPath.this.getLoader(this.index++)) != null) {
/*  222 */           this.url = localLoader.findResource(paramString, paramBoolean);
/*  223 */           if (this.url != null) {
/*  224 */             return true;
/*      */           }
/*      */         }
/*  227 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean hasMoreElements()
/*      */       {
/*  232 */         return next();
/*      */       }
/*      */ 
/*      */       public URL nextElement() {
/*  236 */         if (!next()) {
/*  237 */           throw new NoSuchElementException();
/*      */         }
/*  239 */         URL localURL = this.url;
/*  240 */         this.url = null;
/*  241 */         return localURL;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public Resource getResource(String paramString) {
/*  247 */     return getResource(paramString, true);
/*      */   }
/*      */ 
/*      */   public Enumeration<Resource> getResources(final String paramString, final boolean paramBoolean)
/*      */   {
/*  259 */     return new Enumeration() {
/*  260 */       private int index = 0;
/*  261 */       private Resource res = null;
/*      */ 
/*      */       private boolean next() {
/*  264 */         if (this.res != null)
/*  265 */           return true;
/*      */         URLClassPath.Loader localLoader;
/*  268 */         while ((localLoader = URLClassPath.this.getLoader(this.index++)) != null) {
/*  269 */           this.res = localLoader.getResource(paramString, paramBoolean);
/*  270 */           if (this.res != null) {
/*  271 */             return true;
/*      */           }
/*      */         }
/*  274 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean hasMoreElements()
/*      */       {
/*  279 */         return next();
/*      */       }
/*      */ 
/*      */       public Resource nextElement() {
/*  283 */         if (!next()) {
/*  284 */           throw new NoSuchElementException();
/*      */         }
/*  286 */         Resource localResource = this.res;
/*  287 */         this.res = null;
/*  288 */         return localResource;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public Enumeration<Resource> getResources(String paramString) {
/*  294 */     return getResources(paramString, true);
/*      */   }
/*      */ 
/*      */   private synchronized Loader getLoader(int paramInt)
/*      */   {
/*  303 */     if (this.closed) {
/*  304 */       return null;
/*      */     }
/*      */ 
/*  308 */     while (this.loaders.size() < paramInt + 1)
/*      */     {
/*      */       URL localURL;
/*  311 */       synchronized (this.urls) {
/*  312 */         if (this.urls.empty()) {
/*  313 */           return null;
/*      */         }
/*  315 */         localURL = (URL)this.urls.pop();
/*      */       }
/*      */ 
/*  321 */       ??? = URLUtil.urlNoFragString(localURL);
/*  322 */       if (!this.lmap.containsKey(???))
/*      */       {
/*      */         Loader localLoader;
/*      */         try
/*      */         {
/*  328 */           localLoader = getLoader(localURL);
/*      */ 
/*  331 */           URL[] arrayOfURL = localLoader.getClassPath();
/*  332 */           if (arrayOfURL != null)
/*  333 */             push(arrayOfURL);
/*      */         }
/*      */         catch (IOException localIOException) {
/*      */         }
/*  337 */         continue;
/*      */ 
/*  340 */         this.loaders.add(localLoader);
/*  341 */         this.lmap.put(???, localLoader);
/*      */       }
/*      */     }
/*  343 */     return (Loader)this.loaders.get(paramInt);
/*      */   }
/*      */ 
/*      */   private Loader getLoader(final URL paramURL)
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/*  351 */       return (Loader)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public URLClassPath.Loader run() throws IOException {
/*  354 */           String str = paramURL.getFile();
/*  355 */           if ((str != null) && (str.endsWith("/"))) {
/*  356 */             if ("file".equals(paramURL.getProtocol())) {
/*  357 */               return new URLClassPath.FileLoader(paramURL);
/*      */             }
/*  359 */             return new URLClassPath.Loader(paramURL);
/*      */           }
/*      */ 
/*  362 */           return new URLClassPath.JarLoader(paramURL, URLClassPath.this.jarHandler, URLClassPath.this.lmap);
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException) {
/*  367 */       throw ((IOException)localPrivilegedActionException.getException());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void push(URL[] paramArrayOfURL)
/*      */   {
/*  375 */     synchronized (this.urls) {
/*  376 */       for (int i = paramArrayOfURL.length - 1; i >= 0; i--)
/*  377 */         this.urls.push(paramArrayOfURL[i]);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static URL[] pathToURLs(String paramString)
/*      */   {
/*  389 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString, File.pathSeparator);
/*  390 */     Object localObject1 = new URL[localStringTokenizer.countTokens()];
/*  391 */     int i = 0;
/*      */     Object localObject2;
/*  392 */     while (localStringTokenizer.hasMoreTokens()) {
/*  393 */       localObject2 = new File(localStringTokenizer.nextToken());
/*      */       try {
/*  395 */         localObject2 = new File(((File)localObject2).getCanonicalPath());
/*      */       }
/*      */       catch (IOException localIOException1) {
/*      */       }
/*      */       try {
/*  400 */         localObject1[(i++)] = ParseUtil.fileToEncodedURL((File)localObject2);
/*      */       } catch (IOException localIOException2) {
/*      */       }
/*      */     }
/*  404 */     if (localObject1.length != i) {
/*  405 */       localObject2 = new URL[i];
/*  406 */       System.arraycopy(localObject1, 0, localObject2, 0, i);
/*  407 */       localObject1 = localObject2;
/*      */     }
/*  409 */     return localObject1;
/*      */   }
/*      */ 
/*      */   public URL checkURL(URL paramURL)
/*      */   {
/*      */     try
/*      */     {
/*  419 */       check(paramURL);
/*      */     } catch (Exception localException) {
/*  421 */       return null;
/*      */     }
/*      */ 
/*  424 */     return paramURL;
/*      */   }
/*      */ 
/*      */   static void check(URL paramURL)
/*      */     throws IOException
/*      */   {
/*  433 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  434 */     if (localSecurityManager != null) {
/*  435 */       URLConnection localURLConnection = paramURL.openConnection();
/*  436 */       Permission localPermission = localURLConnection.getPermission();
/*  437 */       if (localPermission != null)
/*      */         try {
/*  439 */           localSecurityManager.checkPermission(localPermission);
/*      */         }
/*      */         catch (SecurityException localSecurityException)
/*      */         {
/*  443 */           if (((localPermission instanceof FilePermission)) && (localPermission.getActions().indexOf("read") != -1))
/*      */           {
/*  445 */             localSecurityManager.checkRead(localPermission.getName());
/*  446 */           } else if (((localPermission instanceof SocketPermission)) && (localPermission.getActions().indexOf("connect") != -1))
/*      */           {
/*  449 */             URL localURL = paramURL;
/*  450 */             if ((localURLConnection instanceof JarURLConnection)) {
/*  451 */               localURL = ((JarURLConnection)localURLConnection).getJarFileURL();
/*      */             }
/*  453 */             localSecurityManager.checkConnect(localURL.getHost(), localURL.getPort());
/*      */           }
/*      */           else {
/*  456 */             throw localSecurityException;
/*      */           }
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class FileLoader extends URLClassPath.Loader
/*      */   {
/*      */     private File dir;
/*      */ 
/*      */     FileLoader(URL paramURL)
/*      */       throws IOException
/*      */     {
/* 1011 */       super();
/* 1012 */       if (!"file".equals(paramURL.getProtocol())) {
/* 1013 */         throw new IllegalArgumentException("url");
/*      */       }
/* 1015 */       String str = paramURL.getFile().replace('/', File.separatorChar);
/* 1016 */       str = ParseUtil.decode(str);
/* 1017 */       this.dir = new File(str).getCanonicalFile();
/*      */     }
/*      */ 
/*      */     URL findResource(String paramString, boolean paramBoolean)
/*      */     {
/* 1024 */       Resource localResource = getResource(paramString, paramBoolean);
/* 1025 */       if (localResource != null) {
/* 1026 */         return localResource.getURL();
/*      */       }
/* 1028 */       return null;
/*      */     }
/*      */ 
/*      */     Resource getResource(final String paramString, boolean paramBoolean)
/*      */     {
/*      */       try {
/* 1034 */         URL localURL2 = new URL(getBaseURL(), ".");
/* 1035 */         final URL localURL1 = new URL(getBaseURL(), ParseUtil.encodePath(paramString, false));
/*      */ 
/* 1037 */         if (!localURL1.getFile().startsWith(localURL2.getFile()))
/*      */         {
/* 1039 */           return null;
/*      */         }
/*      */ 
/* 1042 */         if (paramBoolean)
/* 1043 */           URLClassPath.check(localURL1);
/*      */         final File localFile;
/* 1046 */         if (paramString.indexOf("..") != -1) {
/* 1047 */           localFile = new File(this.dir, paramString.replace('/', File.separatorChar)).getCanonicalFile();
/*      */ 
/* 1049 */           if (!localFile.getPath().startsWith(this.dir.getPath()))
/*      */           {
/* 1051 */             return null;
/*      */           }
/*      */         } else {
/* 1054 */           localFile = new File(this.dir, paramString.replace('/', File.separatorChar));
/*      */         }
/*      */ 
/* 1057 */         if (localFile.exists())
/* 1058 */           return new Resource() {
/* 1059 */             public String getName() { return paramString; } 
/* 1060 */             public URL getURL() { return localURL1; } 
/* 1061 */             public URL getCodeSourceURL() { return URLClassPath.FileLoader.this.getBaseURL(); } 
/*      */             public InputStream getInputStream() throws IOException {
/* 1063 */               return new FileInputStream(localFile);
/*      */             }
/* 1065 */             public int getContentLength() throws IOException { return (int)localFile.length(); }
/*      */           };
/*      */       }
/*      */       catch (Exception localException) {
/* 1069 */         return null;
/*      */       }
/* 1071 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class JarLoader extends URLClassPath.Loader
/*      */   {
/*      */     private JarFile jar;
/*      */     private URL csu;
/*      */     private JarIndex index;
/*      */     private MetaIndex metaIndex;
/*      */     private URLStreamHandler handler;
/*      */     private HashMap<String, URLClassPath.Loader> lmap;
/*  594 */     private boolean closed = false;
/*      */ 
/*      */     JarLoader(URL paramURL, URLStreamHandler paramURLStreamHandler, HashMap<String, URLClassPath.Loader> paramHashMap)
/*      */       throws IOException
/*      */     {
/*  604 */       super();
/*  605 */       this.csu = paramURL;
/*  606 */       this.handler = paramURLStreamHandler;
/*  607 */       this.lmap = paramHashMap;
/*      */ 
/*  609 */       if (!isOptimizable(paramURL)) {
/*  610 */         ensureOpen();
/*      */       } else {
/*  612 */         String str = paramURL.getFile();
/*  613 */         if (str != null) {
/*  614 */           str = ParseUtil.decode(str);
/*  615 */           File localFile = new File(str);
/*  616 */           this.metaIndex = MetaIndex.forJar(localFile);
/*      */ 
/*  623 */           if ((this.metaIndex != null) && (!localFile.exists())) {
/*  624 */             this.metaIndex = null;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  631 */         if (this.metaIndex == null)
/*  632 */           ensureOpen();
/*      */       }
/*      */     }
/*      */ 
/*      */     public void close()
/*      */       throws IOException
/*      */     {
/*  640 */       if (!this.closed) {
/*  641 */         this.closed = true;
/*      */ 
/*  643 */         ensureOpen();
/*  644 */         this.jar.close();
/*      */       }
/*      */     }
/*      */ 
/*      */     JarFile getJarFile() {
/*  649 */       return this.jar;
/*      */     }
/*      */ 
/*      */     private boolean isOptimizable(URL paramURL) {
/*  653 */       return "file".equals(paramURL.getProtocol());
/*      */     }
/*      */ 
/*      */     private void ensureOpen() throws IOException {
/*  657 */       if (this.jar == null)
/*      */         try {
/*  659 */           AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */           {
/*      */             public Void run() throws IOException {
/*  662 */               if (URLClassPath.DEBUG) {
/*  663 */                 System.err.println("Opening " + URLClassPath.JarLoader.this.csu);
/*  664 */                 Thread.dumpStack();
/*      */               }
/*      */ 
/*  667 */               URLClassPath.JarLoader.this.jar = URLClassPath.JarLoader.this.getJarFile(URLClassPath.JarLoader.this.csu);
/*  668 */               URLClassPath.JarLoader.this.index = JarIndex.getJarIndex(URLClassPath.JarLoader.this.jar, URLClassPath.JarLoader.this.metaIndex);
/*  669 */               if (URLClassPath.JarLoader.this.index != null) {
/*  670 */                 String[] arrayOfString = URLClassPath.JarLoader.this.index.getJarFiles();
/*      */ 
/*  676 */                 for (int i = 0; i < arrayOfString.length; i++) {
/*      */                   try {
/*  678 */                     URL localURL = new URL(URLClassPath.JarLoader.this.csu, arrayOfString[i]);
/*      */ 
/*  680 */                     String str = URLUtil.urlNoFragString(localURL);
/*  681 */                     if (!URLClassPath.JarLoader.this.lmap.containsKey(str))
/*  682 */                       URLClassPath.JarLoader.this.lmap.put(str, null);
/*      */                   }
/*      */                   catch (MalformedURLException localMalformedURLException)
/*      */                   {
/*      */                   }
/*      */                 }
/*      */               }
/*  689 */               return null;
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (PrivilegedActionException localPrivilegedActionException) {
/*  694 */           throw ((IOException)localPrivilegedActionException.getException());
/*      */         }
/*      */     }
/*      */ 
/*      */     private JarFile getJarFile(URL paramURL)
/*      */       throws IOException
/*      */     {
/*  701 */       if (isOptimizable(paramURL)) {
/*  702 */         localObject = new FileURLMapper(paramURL);
/*  703 */         if (!((FileURLMapper)localObject).exists()) {
/*  704 */           throw new FileNotFoundException(((FileURLMapper)localObject).getPath());
/*      */         }
/*  706 */         return new JarFile(((FileURLMapper)localObject).getPath());
/*      */       }
/*  708 */       Object localObject = getBaseURL().openConnection();
/*  709 */       ((URLConnection)localObject).setRequestProperty("UA-Java-Version", URLClassPath.JAVA_VERSION);
/*  710 */       return ((JarURLConnection)localObject).getJarFile();
/*      */     }
/*      */ 
/*      */     JarIndex getIndex()
/*      */     {
/*      */       try
/*      */       {
/*  718 */         ensureOpen();
/*      */       } catch (IOException localIOException) {
/*  720 */         throw ((InternalError)new InternalError().initCause(localIOException));
/*      */       }
/*  722 */       return this.index;
/*      */     }
/*      */ 
/*      */     Resource checkResource(final String paramString, boolean paramBoolean, final JarEntry paramJarEntry)
/*      */     {
/*      */       final URL localURL;
/*      */       try
/*      */       {
/*  734 */         localURL = new URL(getBaseURL(), ParseUtil.encodePath(paramString, false));
/*  735 */         if (paramBoolean)
/*  736 */           URLClassPath.check(localURL);
/*      */       }
/*      */       catch (MalformedURLException localMalformedURLException) {
/*  739 */         return null;
/*      */       }
/*      */       catch (IOException localIOException) {
/*  742 */         return null;
/*      */       } catch (AccessControlException localAccessControlException) {
/*  744 */         return null;
/*      */       }
/*      */ 
/*  747 */       return new Resource() {
/*  748 */         public String getName() { return paramString; } 
/*  749 */         public URL getURL() { return localURL; } 
/*  750 */         public URL getCodeSourceURL() { return URLClassPath.JarLoader.this.csu; } 
/*      */         public InputStream getInputStream() throws IOException {
/*  752 */           return URLClassPath.JarLoader.this.jar.getInputStream(paramJarEntry);
/*      */         }
/*  754 */         public int getContentLength() { return (int)paramJarEntry.getSize(); } 
/*      */         public Manifest getManifest() throws IOException {
/*  756 */           return URLClassPath.JarLoader.this.jar.getManifest();
/*      */         }
/*  758 */         public Certificate[] getCertificates() { return paramJarEntry.getCertificates(); } 
/*      */         public CodeSigner[] getCodeSigners() {
/*  760 */           return paramJarEntry.getCodeSigners();
/*      */         }
/*      */       };
/*      */     }
/*      */ 
/*      */     boolean validIndex(String paramString)
/*      */     {
/*  770 */       String str1 = paramString;
/*      */       int i;
/*  772 */       if ((i = paramString.lastIndexOf("/")) != -1) {
/*  773 */         str1 = paramString.substring(0, i);
/*      */       }
/*      */ 
/*  778 */       Enumeration localEnumeration = this.jar.entries();
/*  779 */       while (localEnumeration.hasMoreElements()) {
/*  780 */         ZipEntry localZipEntry = (ZipEntry)localEnumeration.nextElement();
/*  781 */         String str2 = localZipEntry.getName();
/*  782 */         if ((i = str2.lastIndexOf("/")) != -1)
/*  783 */           str2 = str2.substring(0, i);
/*  784 */         if (str2.equals(str1)) {
/*  785 */           return true;
/*      */         }
/*      */       }
/*  788 */       return false;
/*      */     }
/*      */ 
/*      */     URL findResource(String paramString, boolean paramBoolean)
/*      */     {
/*  795 */       Resource localResource = getResource(paramString, paramBoolean);
/*  796 */       if (localResource != null) {
/*  797 */         return localResource.getURL();
/*      */       }
/*  799 */       return null;
/*      */     }
/*      */ 
/*      */     Resource getResource(String paramString, boolean paramBoolean)
/*      */     {
/*  806 */       if ((this.metaIndex != null) && 
/*  807 */         (!this.metaIndex.mayContain(paramString))) {
/*  808 */         return null;
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  813 */         ensureOpen();
/*      */       } catch (IOException localIOException) {
/*  815 */         throw ((InternalError)new InternalError().initCause(localIOException));
/*      */       }
/*  817 */       JarEntry localJarEntry = this.jar.getJarEntry(paramString);
/*  818 */       if (localJarEntry != null) {
/*  819 */         return checkResource(paramString, paramBoolean, localJarEntry);
/*      */       }
/*  821 */       if (this.index == null) {
/*  822 */         return null;
/*      */       }
/*  824 */       HashSet localHashSet = new HashSet();
/*  825 */       return getResource(paramString, paramBoolean, localHashSet);
/*      */     }
/*      */ 
/*      */     Resource getResource(String paramString, boolean paramBoolean, Set<String> paramSet)
/*      */     {
/*  840 */       int i = 0;
/*  841 */       int j = 0;
/*  842 */       LinkedList localLinkedList = null;
/*      */ 
/*  847 */       if ((localLinkedList = this.index.get(paramString)) == null)
/*  848 */         return null;
/*      */       do
/*      */       {
/*  851 */         Object[] arrayOfObject = localLinkedList.toArray();
/*  852 */         int k = localLinkedList.size();
/*      */ 
/*  854 */         while (j < k) { String str1 = (String)arrayOfObject[(j++)];
/*      */           final URL localURL;
/*      */           JarLoader localJarLoader;
/*      */           try {
/*  860 */             localURL = new URL(this.csu, str1);
/*  861 */             String str2 = URLUtil.urlNoFragString(localURL);
/*  862 */             if ((localJarLoader = (JarLoader)this.lmap.get(str2)) == null)
/*      */             {
/*  866 */               localJarLoader = (JarLoader)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */               {
/*      */                 public URLClassPath.JarLoader run() throws IOException {
/*  869 */                   return new URLClassPath.JarLoader(localURL, URLClassPath.JarLoader.this.handler, URLClassPath.JarLoader.this.lmap);
/*      */                 }
/*      */               });
/*  878 */               JarIndex localJarIndex = localJarLoader.getIndex();
/*  879 */               if (localJarIndex != null) {
/*  880 */                 int n = str1.lastIndexOf("/");
/*  881 */                 localJarIndex.merge(this.index, n == -1 ? null : str1.substring(0, n + 1));
/*      */               }
/*      */ 
/*  886 */               this.lmap.put(str2, localJarLoader);
/*      */             }
/*      */           } catch (PrivilegedActionException localPrivilegedActionException) {
/*  889 */             continue; } catch (MalformedURLException localMalformedURLException) {
/*      */           }
/*  891 */           continue;
/*      */ 
/*  898 */           int m = !paramSet.add(URLUtil.urlNoFragString(localURL)) ? 1 : 0;
/*  899 */           if (m == 0) {
/*      */             try {
/*  901 */               localJarLoader.ensureOpen();
/*      */             } catch (IOException localIOException) {
/*  903 */               throw ((InternalError)new InternalError().initCause(localIOException));
/*      */             }
/*  905 */             JarEntry localJarEntry = localJarLoader.jar.getJarEntry(paramString);
/*  906 */             if (localJarEntry != null) {
/*  907 */               return localJarLoader.checkResource(paramString, paramBoolean, localJarEntry);
/*      */             }
/*      */ 
/*  914 */             if (!localJarLoader.validIndex(paramString))
/*      */             {
/*  916 */               throw new InvalidJarIndexException("Invalid index");
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  925 */           if ((m == 0) && (localJarLoader != this) && (localJarLoader.getIndex() != null))
/*      */           {
/*      */             Resource localResource;
/*  932 */             if ((localResource = localJarLoader.getResource(paramString, paramBoolean, paramSet)) != null)
/*      */             {
/*  934 */               return localResource;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  939 */         localLinkedList = this.index.get(paramString);
/*      */       }
/*      */ 
/*  942 */       while (j < localLinkedList.size());
/*  943 */       return null;
/*      */     }
/*      */ 
/*      */     URL[] getClassPath()
/*      */       throws IOException
/*      */     {
/*  951 */       if (this.index != null) {
/*  952 */         return null;
/*      */       }
/*      */ 
/*  955 */       if (this.metaIndex != null) {
/*  956 */         return null;
/*      */       }
/*      */ 
/*  959 */       ensureOpen();
/*  960 */       parseExtensionsDependencies();
/*  961 */       if (SharedSecrets.javaUtilJarAccess().jarFileHasClassPathAttribute(this.jar)) {
/*  962 */         Manifest localManifest = this.jar.getManifest();
/*  963 */         if (localManifest != null) {
/*  964 */           Attributes localAttributes = localManifest.getMainAttributes();
/*  965 */           if (localAttributes != null) {
/*  966 */             String str = localAttributes.getValue(Attributes.Name.CLASS_PATH);
/*  967 */             if (str != null) {
/*  968 */               return parseClassPath(this.csu, str);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  973 */       return null;
/*      */     }
/*      */ 
/*      */     private void parseExtensionsDependencies()
/*      */       throws IOException
/*      */     {
/*  980 */       ExtensionDependency.checkExtensionsDependencies(this.jar);
/*      */     }
/*      */ 
/*      */     private URL[] parseClassPath(URL paramURL, String paramString)
/*      */       throws MalformedURLException
/*      */     {
/*  990 */       StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
/*  991 */       URL[] arrayOfURL = new URL[localStringTokenizer.countTokens()];
/*  992 */       int i = 0;
/*  993 */       while (localStringTokenizer.hasMoreTokens()) {
/*  994 */         String str = localStringTokenizer.nextToken();
/*  995 */         arrayOfURL[i] = new URL(paramURL, str);
/*  996 */         i++;
/*      */       }
/*  998 */       return arrayOfURL;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class Loader
/*      */     implements Closeable
/*      */   {
/*      */     private final URL base;
/*      */     private JarFile jarfile;
/*      */ 
/*      */     Loader(URL paramURL)
/*      */     {
/*  475 */       this.base = paramURL;
/*      */     }
/*      */ 
/*      */     URL getBaseURL()
/*      */     {
/*  482 */       return this.base;
/*      */     }
/*      */ 
/*      */     URL findResource(String paramString, boolean paramBoolean) {
/*      */       URL localURL;
/*      */       try {
/*  488 */         localURL = new URL(this.base, ParseUtil.encodePath(paramString, false));
/*      */       } catch (MalformedURLException localMalformedURLException) {
/*  490 */         throw new IllegalArgumentException("name");
/*      */       }
/*      */       try
/*      */       {
/*  494 */         if (paramBoolean) {
/*  495 */           URLClassPath.check(localURL);
/*      */         }
/*      */ 
/*  502 */         URLConnection localURLConnection = localURL.openConnection();
/*      */         Object localObject;
/*  503 */         if ((localURLConnection instanceof HttpURLConnection)) {
/*  504 */           localObject = (HttpURLConnection)localURLConnection;
/*  505 */           ((HttpURLConnection)localObject).setRequestMethod("HEAD");
/*  506 */           if (((HttpURLConnection)localObject).getResponseCode() >= 400)
/*  507 */             return null;
/*      */         }
/*      */         else
/*      */         {
/*  511 */           localObject = localURL.openStream();
/*  512 */           ((InputStream)localObject).close();
/*      */         }
/*  514 */         return localURL; } catch (Exception localException) {
/*      */       }
/*  516 */       return null;
/*      */     }
/*      */ 
/*      */     Resource getResource(final String paramString, boolean paramBoolean)
/*      */     {
/*      */       final URL localURL;
/*      */       try {
/*  523 */         localURL = new URL(this.base, ParseUtil.encodePath(paramString, false));
/*      */       } catch (MalformedURLException localMalformedURLException) {
/*  525 */         throw new IllegalArgumentException("name");
/*      */       }
/*      */       final URLConnection localURLConnection;
/*      */       try {
/*  529 */         if (paramBoolean) {
/*  530 */           URLClassPath.check(localURL);
/*      */         }
/*  532 */         localURLConnection = localURL.openConnection();
/*  533 */         InputStream localInputStream = localURLConnection.getInputStream();
/*  534 */         if ((localURLConnection instanceof JarURLConnection))
/*      */         {
/*  538 */           JarURLConnection localJarURLConnection = (JarURLConnection)localURLConnection;
/*  539 */           this.jarfile = localJarURLConnection.getJarFile();
/*      */         }
/*      */       } catch (Exception localException) {
/*  542 */         return null;
/*      */       }
/*  544 */       return new Resource() {
/*  545 */         public String getName() { return paramString; } 
/*  546 */         public URL getURL() { return localURL; } 
/*  547 */         public URL getCodeSourceURL() { return URLClassPath.Loader.this.base; } 
/*      */         public InputStream getInputStream() throws IOException {
/*  549 */           return localURLConnection.getInputStream();
/*      */         }
/*      */         public int getContentLength() throws IOException {
/*  552 */           return localURLConnection.getContentLength();
/*      */         }
/*      */       };
/*      */     }
/*      */ 
/*      */     Resource getResource(String paramString)
/*      */     {
/*  563 */       return getResource(paramString, true);
/*      */     }
/*      */ 
/*      */     public void close()
/*      */       throws IOException
/*      */     {
/*  571 */       if (this.jarfile != null)
/*  572 */         this.jarfile.close();
/*      */     }
/*      */ 
/*      */     URL[] getClassPath()
/*      */       throws IOException
/*      */     {
/*  580 */       return null;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.misc.URLClassPath
 * JD-Core Version:    0.6.2
 */