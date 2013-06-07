/*      */ package sun.tools.jar;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileDescriptor;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.OpenOption;
/*      */ import java.nio.file.Path;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.MissingResourceException;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.jar.Attributes;
/*      */ import java.util.jar.Attributes.Name;
/*      */ import java.util.jar.JarFile;
/*      */ import java.util.jar.JarOutputStream;
/*      */ import java.util.jar.Manifest;
/*      */ import java.util.zip.CRC32;
/*      */ import java.util.zip.ZipEntry;
/*      */ import java.util.zip.ZipFile;
/*      */ import java.util.zip.ZipInputStream;
/*      */ import java.util.zip.ZipOutputStream;
/*      */ import sun.misc.JarIndex;
/*      */ 
/*      */ public class Main
/*      */ {
/*      */   String program;
/*      */   PrintStream out;
/*      */   PrintStream err;
/*      */   String fname;
/*      */   String mname;
/*      */   String ename;
/*   51 */   String zname = "";
/*      */   String[] files;
/*   53 */   String rootjar = null;
/*      */ 
/*   58 */   Map<String, File> entryMap = new HashMap();
/*      */ 
/*   61 */   Set<File> entries = new LinkedHashSet();
/*      */ 
/*   64 */   Set<String> paths = new HashSet();
/*      */   boolean cflag;
/*      */   boolean uflag;
/*      */   boolean xflag;
/*      */   boolean tflag;
/*      */   boolean vflag;
/*      */   boolean flag0;
/*      */   boolean Mflag;
/*      */   boolean iflag;
/*      */   static final String MANIFEST_DIR = "META-INF/";
/*      */   static final String VERSION = "1.0";
/*      */   private static ResourceBundle rsrc;
/*      */   private static final boolean useExtractionTime;
/*      */   private boolean ok;
/*  766 */   private byte[] copyBuf = new byte[8192];
/*      */ 
/* 1035 */   private HashSet<String> jarPaths = new HashSet();
/*      */ 
/*      */   private String getMsg(String paramString)
/*      */   {
/*      */     try
/*      */     {
/*  104 */       return rsrc.getString(paramString); } catch (MissingResourceException localMissingResourceException) {
/*      */     }
/*  106 */     throw new Error("Error in message file");
/*      */   }
/*      */ 
/*      */   private String formatMsg(String paramString1, String paramString2)
/*      */   {
/*  111 */     String str = getMsg(paramString1);
/*  112 */     String[] arrayOfString = new String[1];
/*  113 */     arrayOfString[0] = paramString2;
/*  114 */     return MessageFormat.format(str, (Object[])arrayOfString);
/*      */   }
/*      */ 
/*      */   private String formatMsg2(String paramString1, String paramString2, String paramString3) {
/*  118 */     String str = getMsg(paramString1);
/*  119 */     String[] arrayOfString = new String[2];
/*  120 */     arrayOfString[0] = paramString2;
/*  121 */     arrayOfString[1] = paramString3;
/*  122 */     return MessageFormat.format(str, (Object[])arrayOfString);
/*      */   }
/*      */ 
/*      */   public Main(PrintStream paramPrintStream1, PrintStream paramPrintStream2, String paramString) {
/*  126 */     this.out = paramPrintStream1;
/*  127 */     this.err = paramPrintStream2;
/*  128 */     this.program = paramString;
/*      */   }
/*      */ 
/*      */   private static File createTempFileInSameDirectoryAs(File paramFile)
/*      */     throws IOException
/*      */   {
/*  137 */     File localFile = paramFile.getParentFile();
/*  138 */     if (localFile == null)
/*  139 */       localFile = new File(".");
/*  140 */     return File.createTempFile("jartmp", null, localFile);
/*      */   }
/*      */ 
/*      */   public synchronized boolean run(String[] paramArrayOfString)
/*      */   {
/*  149 */     this.ok = true;
/*  150 */     if (!parseArgs(paramArrayOfString))
/*  151 */       return false;
/*      */     try
/*      */     {
/*  154 */       if (((this.cflag) || (this.uflag)) && 
/*  155 */         (this.fname != null))
/*      */       {
/*  159 */         this.zname = this.fname.replace(File.separatorChar, '/');
/*  160 */         if (this.zname.startsWith("./"))
/*  161 */           this.zname = this.zname.substring(2);
/*      */       }
/*      */       Object localObject1;
/*      */       Object localObject2;
/*      */       Object localObject3;
/*  165 */       if (this.cflag) {
/*  166 */         localObject1 = null;
/*  167 */         localObject2 = null;
/*      */ 
/*  169 */         if (!this.Mflag) {
/*  170 */           if (this.mname != null) {
/*  171 */             localObject2 = new FileInputStream(this.mname);
/*  172 */             localObject1 = new Manifest(new BufferedInputStream((InputStream)localObject2));
/*      */           } else {
/*  174 */             localObject1 = new Manifest();
/*      */           }
/*  176 */           addVersion((Manifest)localObject1);
/*  177 */           addCreatedBy((Manifest)localObject1);
/*  178 */           if (isAmbiguousMainClass((Manifest)localObject1)) {
/*  179 */             if (localObject2 != null) {
/*  180 */               ((InputStream)localObject2).close();
/*      */             }
/*  182 */             return false;
/*      */           }
/*  184 */           if (this.ename != null) {
/*  185 */             addMainClass((Manifest)localObject1, this.ename);
/*      */           }
/*      */         }
/*      */ 
/*  189 */         if (this.fname != null) {
/*  190 */           localObject3 = new FileOutputStream(this.fname);
/*      */         } else {
/*  192 */           localObject3 = new FileOutputStream(FileDescriptor.out);
/*  193 */           if (this.vflag)
/*      */           {
/*  197 */             this.vflag = false;
/*      */           }
/*      */         }
/*  200 */         expand(null, this.files, false);
/*  201 */         create(new BufferedOutputStream((OutputStream)localObject3, 4096), (Manifest)localObject1);
/*  202 */         if (localObject2 != null) {
/*  203 */           ((InputStream)localObject2).close();
/*      */         }
/*  205 */         ((OutputStream)localObject3).close();
/*  206 */       } else if (this.uflag) {
/*  207 */         localObject1 = null; localObject2 = null;
/*      */         FileOutputStream localFileOutputStream;
/*  210 */         if (this.fname != null) {
/*  211 */           localObject1 = new File(this.fname);
/*  212 */           localObject2 = createTempFileInSameDirectoryAs((File)localObject1);
/*  213 */           localObject3 = new FileInputStream((File)localObject1);
/*  214 */           localFileOutputStream = new FileOutputStream((File)localObject2);
/*      */         } else {
/*  216 */           localObject3 = new FileInputStream(FileDescriptor.in);
/*  217 */           localFileOutputStream = new FileOutputStream(FileDescriptor.out);
/*  218 */           this.vflag = false;
/*      */         }
/*  220 */         InputStream localInputStream = (!this.Mflag) && (this.mname != null) ? new FileInputStream(this.mname) : null;
/*      */ 
/*  222 */         expand(null, this.files, true);
/*  223 */         boolean bool = update((InputStream)localObject3, new BufferedOutputStream(localFileOutputStream), localInputStream, null);
/*      */ 
/*  225 */         if (this.ok) {
/*  226 */           this.ok = bool;
/*      */         }
/*  228 */         ((FileInputStream)localObject3).close();
/*  229 */         localFileOutputStream.close();
/*  230 */         if (localInputStream != null) {
/*  231 */           localInputStream.close();
/*      */         }
/*  233 */         if (this.fname != null)
/*      */         {
/*  235 */           ((File)localObject1).delete();
/*  236 */           if (!((File)localObject2).renameTo((File)localObject1)) {
/*  237 */             ((File)localObject2).delete();
/*  238 */             throw new IOException(getMsg("error.write.file"));
/*      */           }
/*  240 */           ((File)localObject2).delete();
/*      */         }
/*  242 */       } else if (this.tflag) {
/*  243 */         replaceFSC(this.files);
/*  244 */         if (this.fname != null) {
/*  245 */           list(this.fname, this.files);
/*      */         } else {
/*  247 */           localObject1 = new FileInputStream(FileDescriptor.in);
/*      */           try {
/*  249 */             list(new BufferedInputStream((InputStream)localObject1), this.files);
/*      */           } finally {
/*  251 */             ((InputStream)localObject1).close();
/*      */           }
/*      */         }
/*  254 */       } else if (this.xflag) {
/*  255 */         replaceFSC(this.files);
/*  256 */         if ((this.fname != null) && (this.files != null)) {
/*  257 */           extract(this.fname, this.files);
/*      */         } else {
/*  259 */           localObject1 = this.fname == null ? new FileInputStream(FileDescriptor.in) : new FileInputStream(this.fname);
/*      */           try
/*      */           {
/*  263 */             extract(new BufferedInputStream((InputStream)localObject1), this.files);
/*      */           } finally {
/*  265 */             ((InputStream)localObject1).close();
/*      */           }
/*      */         }
/*  268 */       } else if (this.iflag) {
/*  269 */         genIndex(this.rootjar, this.files);
/*      */       }
/*      */     } catch (IOException localIOException) {
/*  272 */       fatalError(localIOException);
/*  273 */       this.ok = false;
/*      */     } catch (Error localError) {
/*  275 */       localError.printStackTrace();
/*  276 */       this.ok = false;
/*      */     } catch (Throwable localThrowable) {
/*  278 */       localThrowable.printStackTrace();
/*  279 */       this.ok = false;
/*      */     }
/*  281 */     this.out.flush();
/*  282 */     this.err.flush();
/*  283 */     return this.ok;
/*      */   }
/*      */ 
/*      */   boolean parseArgs(String[] paramArrayOfString)
/*      */   {
/*      */     try
/*      */     {
/*  292 */       paramArrayOfString = CommandLine.parse(paramArrayOfString);
/*      */     } catch (FileNotFoundException localFileNotFoundException) {
/*  294 */       fatalError(formatMsg("error.cant.open", localFileNotFoundException.getMessage()));
/*  295 */       return false;
/*      */     } catch (IOException localIOException) {
/*  297 */       fatalError(localIOException);
/*  298 */       return false;
/*  301 */     }
/*      */ int i = 1;
/*      */     int k;
/*      */     try { String str1 = paramArrayOfString[0];
/*  304 */       if (str1.startsWith("-")) {
/*  305 */         str1 = str1.substring(1);
/*      */       }
/*  307 */       for (k = 0; k < str1.length(); k++)
/*  308 */         switch (str1.charAt(k)) {
/*      */         case 'c':
/*  310 */           if ((this.xflag) || (this.tflag) || (this.uflag) || (this.iflag)) {
/*  311 */             usageError();
/*  312 */             return false;
/*      */           }
/*  314 */           this.cflag = true;
/*  315 */           break;
/*      */         case 'u':
/*  317 */           if ((this.cflag) || (this.xflag) || (this.tflag) || (this.iflag)) {
/*  318 */             usageError();
/*  319 */             return false;
/*      */           }
/*  321 */           this.uflag = true;
/*  322 */           break;
/*      */         case 'x':
/*  324 */           if ((this.cflag) || (this.uflag) || (this.tflag) || (this.iflag)) {
/*  325 */             usageError();
/*  326 */             return false;
/*      */           }
/*  328 */           this.xflag = true;
/*  329 */           break;
/*      */         case 't':
/*  331 */           if ((this.cflag) || (this.uflag) || (this.xflag) || (this.iflag)) {
/*  332 */             usageError();
/*  333 */             return false;
/*      */           }
/*  335 */           this.tflag = true;
/*  336 */           break;
/*      */         case 'M':
/*  338 */           this.Mflag = true;
/*  339 */           break;
/*      */         case 'v':
/*  341 */           this.vflag = true;
/*  342 */           break;
/*      */         case 'f':
/*  344 */           this.fname = paramArrayOfString[(i++)];
/*  345 */           break;
/*      */         case 'm':
/*  347 */           this.mname = paramArrayOfString[(i++)];
/*  348 */           break;
/*      */         case '0':
/*  350 */           this.flag0 = true;
/*  351 */           break;
/*      */         case 'i':
/*  353 */           if ((this.cflag) || (this.uflag) || (this.xflag) || (this.tflag)) {
/*  354 */             usageError();
/*  355 */             return false;
/*      */           }
/*      */ 
/*  358 */           this.rootjar = paramArrayOfString[(i++)];
/*  359 */           this.iflag = true;
/*  360 */           break;
/*      */         case 'e':
/*  362 */           this.ename = paramArrayOfString[(i++)];
/*  363 */           break;
/*      */         default:
/*  365 */           error(formatMsg("error.illegal.option", String.valueOf(str1.charAt(k))));
/*      */ 
/*  367 */           usageError();
/*  368 */           return false;
/*      */         }
/*      */     } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException1)
/*      */     {
/*  372 */       usageError();
/*  373 */       return false;
/*      */     }
/*  375 */     if ((!this.cflag) && (!this.tflag) && (!this.xflag) && (!this.uflag) && (!this.iflag)) {
/*  376 */       error(getMsg("error.bad.option"));
/*  377 */       usageError();
/*  378 */       return false;
/*      */     }
/*      */ 
/*  381 */     int j = paramArrayOfString.length - i;
/*  382 */     if (j > 0) {
/*  383 */       k = 0;
/*  384 */       String[] arrayOfString = new String[j];
/*      */       try {
/*  386 */         for (int m = i; m < paramArrayOfString.length; m++)
/*  387 */           if (paramArrayOfString[m].equals("-C"))
/*      */           {
/*  389 */             String str2 = paramArrayOfString[(++m)];
/*  390 */             str2 = str2 + File.separator;
/*      */ 
/*  392 */             str2 = str2.replace(File.separatorChar, '/');
/*  393 */             while (str2.indexOf("//") > -1) {
/*  394 */               str2 = str2.replace("//", "/");
/*      */             }
/*  396 */             this.paths.add(str2.replace(File.separatorChar, '/'));
/*  397 */             arrayOfString[(k++)] = (str2 + paramArrayOfString[(++m)]);
/*      */           } else {
/*  399 */             arrayOfString[(k++)] = paramArrayOfString[m];
/*      */           }
/*      */       }
/*      */       catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException2) {
/*  403 */         usageError();
/*  404 */         return false;
/*      */       }
/*  406 */       this.files = new String[k];
/*  407 */       System.arraycopy(arrayOfString, 0, this.files, 0, k); } else {
/*  408 */       if ((this.cflag) && (this.mname == null)) {
/*  409 */         error(getMsg("error.bad.cflag"));
/*  410 */         usageError();
/*  411 */         return false;
/*  412 */       }if (this.uflag) {
/*  413 */         if ((this.mname != null) || (this.ename != null))
/*      */         {
/*  415 */           return true;
/*      */         }
/*  417 */         error(getMsg("error.bad.uflag"));
/*  418 */         usageError();
/*  419 */         return false;
/*      */       }
/*      */     }
/*  422 */     return true;
/*      */   }
/*      */ 
/*      */   void expand(File paramFile, String[] paramArrayOfString, boolean paramBoolean)
/*      */   {
/*  430 */     if (paramArrayOfString == null) {
/*  431 */       return;
/*      */     }
/*  433 */     for (int i = 0; i < paramArrayOfString.length; i++)
/*      */     {
/*      */       File localFile;
/*  435 */       if (paramFile == null)
/*  436 */         localFile = new File(paramArrayOfString[i]);
/*      */       else {
/*  438 */         localFile = new File(paramFile, paramArrayOfString[i]);
/*      */       }
/*  440 */       if (localFile.isFile()) {
/*  441 */         if ((this.entries.add(localFile)) && 
/*  442 */           (paramBoolean))
/*  443 */           this.entryMap.put(entryName(localFile.getPath()), localFile);
/*      */       }
/*  445 */       else if (localFile.isDirectory()) {
/*  446 */         if (this.entries.add(localFile)) {
/*  447 */           if (paramBoolean) {
/*  448 */             String str = localFile.getPath();
/*  449 */             str = str + File.separator;
/*      */ 
/*  451 */             this.entryMap.put(entryName(str), localFile);
/*      */           }
/*  453 */           expand(localFile, localFile.list(), paramBoolean);
/*      */         }
/*      */       } else {
/*  456 */         error(formatMsg("error.nosuch.fileordir", String.valueOf(localFile)));
/*  457 */         this.ok = false;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void create(OutputStream paramOutputStream, Manifest paramManifest)
/*      */     throws IOException
/*      */   {
/*  468 */     JarOutputStream localJarOutputStream = new JarOutputStream(paramOutputStream);
/*  469 */     if (this.flag0) {
/*  470 */       localJarOutputStream.setMethod(0);
/*      */     }
/*  472 */     if (paramManifest != null) {
/*  473 */       if (this.vflag) {
/*  474 */         output(getMsg("out.added.manifest"));
/*      */       }
/*  476 */       localObject = new ZipEntry("META-INF/");
/*  477 */       ((ZipEntry)localObject).setTime(System.currentTimeMillis());
/*  478 */       ((ZipEntry)localObject).setSize(0L);
/*  479 */       ((ZipEntry)localObject).setCrc(0L);
/*  480 */       localJarOutputStream.putNextEntry((ZipEntry)localObject);
/*  481 */       localObject = new ZipEntry("META-INF/MANIFEST.MF");
/*  482 */       ((ZipEntry)localObject).setTime(System.currentTimeMillis());
/*  483 */       if (this.flag0) {
/*  484 */         crc32Manifest((ZipEntry)localObject, paramManifest);
/*      */       }
/*  486 */       localJarOutputStream.putNextEntry((ZipEntry)localObject);
/*  487 */       paramManifest.write(localJarOutputStream);
/*  488 */       localJarOutputStream.closeEntry();
/*      */     }
/*  490 */     for (Object localObject = this.entries.iterator(); ((Iterator)localObject).hasNext(); ) { File localFile = (File)((Iterator)localObject).next();
/*  491 */       addFile(localJarOutputStream, localFile);
/*      */     }
/*  493 */     localJarOutputStream.close();
/*      */   }
/*      */ 
/*      */   private char toUpperCaseASCII(char paramChar) {
/*  497 */     return (paramChar < 'a') || (paramChar > 'z') ? paramChar : (char)(paramChar + 'A' - 97);
/*      */   }
/*      */ 
/*      */   private boolean equalsIgnoreCase(String paramString1, String paramString2)
/*      */   {
/*  507 */     assert (paramString2.toUpperCase(Locale.ENGLISH).equals(paramString2));
/*      */     int i;
/*  509 */     if ((i = paramString1.length()) != paramString2.length())
/*  510 */       return false;
/*  511 */     for (int j = 0; j < i; j++) {
/*  512 */       char c1 = paramString1.charAt(j);
/*  513 */       char c2 = paramString2.charAt(j);
/*  514 */       if ((c1 != c2) && (toUpperCaseASCII(c1) != c2))
/*  515 */         return false;
/*      */     }
/*  517 */     return true;
/*      */   }
/*      */ 
/*      */   boolean update(InputStream paramInputStream1, OutputStream paramOutputStream, InputStream paramInputStream2, JarIndex paramJarIndex)
/*      */     throws IOException
/*      */   {
/*  527 */     ZipInputStream localZipInputStream = new ZipInputStream(paramInputStream1);
/*  528 */     JarOutputStream localJarOutputStream = new JarOutputStream(paramOutputStream);
/*  529 */     ZipEntry localZipEntry = null;
/*  530 */     int i = 0;
/*  531 */     boolean bool1 = true;
/*      */ 
/*  533 */     if (paramJarIndex != null) {
/*  534 */       addIndex(paramJarIndex, localJarOutputStream);
/*      */     }
/*      */ 
/*  538 */     while ((localZipEntry = localZipInputStream.getNextEntry()) != null) {
/*  539 */       localObject1 = localZipEntry.getName();
/*      */ 
/*  541 */       boolean bool2 = equalsIgnoreCase((String)localObject1, "META-INF/MANIFEST.MF");
/*      */ 
/*  543 */       if (((paramJarIndex == null) || (!equalsIgnoreCase((String)localObject1, "META-INF/INDEX.LIST"))) && ((!this.Mflag) || (!bool2)))
/*      */       {
/*      */         Object localObject2;
/*  546 */         if ((bool2) && ((paramInputStream2 != null) || (this.ename != null)))
/*      */         {
/*  548 */           i = 1;
/*  549 */           if (paramInputStream2 != null)
/*      */           {
/*  553 */             localObject2 = new FileInputStream(this.mname);
/*  554 */             boolean bool3 = isAmbiguousMainClass(new Manifest((InputStream)localObject2));
/*  555 */             ((FileInputStream)localObject2).close();
/*  556 */             if (bool3) {
/*  557 */               return false;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  562 */           localObject2 = new Manifest(localZipInputStream);
/*  563 */           if (paramInputStream2 != null) {
/*  564 */             ((Manifest)localObject2).read(paramInputStream2);
/*      */           }
/*  566 */           updateManifest((Manifest)localObject2, localJarOutputStream);
/*      */         }
/*  568 */         else if (!this.entryMap.containsKey(localObject1))
/*      */         {
/*  570 */           localObject2 = new ZipEntry((String)localObject1);
/*  571 */           ((ZipEntry)localObject2).setMethod(localZipEntry.getMethod());
/*  572 */           ((ZipEntry)localObject2).setTime(localZipEntry.getTime());
/*  573 */           ((ZipEntry)localObject2).setComment(localZipEntry.getComment());
/*  574 */           ((ZipEntry)localObject2).setExtra(localZipEntry.getExtra());
/*  575 */           if (localZipEntry.getMethod() == 0) {
/*  576 */             ((ZipEntry)localObject2).setSize(localZipEntry.getSize());
/*  577 */             ((ZipEntry)localObject2).setCrc(localZipEntry.getCrc());
/*      */           }
/*  579 */           localJarOutputStream.putNextEntry((ZipEntry)localObject2);
/*  580 */           copy(localZipInputStream, localJarOutputStream);
/*      */         } else {
/*  582 */           localObject2 = (File)this.entryMap.get(localObject1);
/*  583 */           addFile(localJarOutputStream, (File)localObject2);
/*  584 */           this.entryMap.remove(localObject1);
/*  585 */           this.entries.remove(localObject2);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  591 */     for (Object localObject1 = this.entries.iterator(); ((Iterator)localObject1).hasNext(); ) { File localFile = (File)((Iterator)localObject1).next();
/*  592 */       addFile(localJarOutputStream, localFile);
/*      */     }
/*  594 */     if (i == 0) {
/*  595 */       if (paramInputStream2 != null) {
/*  596 */         localObject1 = new Manifest(paramInputStream2);
/*  597 */         bool1 = !isAmbiguousMainClass((Manifest)localObject1);
/*  598 */         if (bool1)
/*  599 */           updateManifest((Manifest)localObject1, localJarOutputStream);
/*      */       }
/*  601 */       else if (this.ename != null) {
/*  602 */         updateManifest(new Manifest(), localJarOutputStream);
/*      */       }
/*      */     }
/*  605 */     localZipInputStream.close();
/*  606 */     localJarOutputStream.close();
/*  607 */     return bool1;
/*      */   }
/*      */ 
/*      */   private void addIndex(JarIndex paramJarIndex, ZipOutputStream paramZipOutputStream)
/*      */     throws IOException
/*      */   {
/*  614 */     ZipEntry localZipEntry = new ZipEntry("META-INF/INDEX.LIST");
/*  615 */     localZipEntry.setTime(System.currentTimeMillis());
/*  616 */     if (this.flag0) {
/*  617 */       CRC32OutputStream localCRC32OutputStream = new CRC32OutputStream();
/*  618 */       paramJarIndex.write(localCRC32OutputStream);
/*  619 */       localCRC32OutputStream.updateEntry(localZipEntry);
/*      */     }
/*  621 */     paramZipOutputStream.putNextEntry(localZipEntry);
/*  622 */     paramJarIndex.write(paramZipOutputStream);
/*  623 */     paramZipOutputStream.closeEntry();
/*      */   }
/*      */ 
/*      */   private void updateManifest(Manifest paramManifest, ZipOutputStream paramZipOutputStream)
/*      */     throws IOException
/*      */   {
/*  629 */     addVersion(paramManifest);
/*  630 */     addCreatedBy(paramManifest);
/*  631 */     if (this.ename != null) {
/*  632 */       addMainClass(paramManifest, this.ename);
/*      */     }
/*  634 */     ZipEntry localZipEntry = new ZipEntry("META-INF/MANIFEST.MF");
/*  635 */     localZipEntry.setTime(System.currentTimeMillis());
/*  636 */     if (this.flag0) {
/*  637 */       crc32Manifest(localZipEntry, paramManifest);
/*      */     }
/*  639 */     paramZipOutputStream.putNextEntry(localZipEntry);
/*  640 */     paramManifest.write(paramZipOutputStream);
/*  641 */     if (this.vflag)
/*  642 */       output(getMsg("out.update.manifest"));
/*      */   }
/*      */ 
/*      */   private String entryName(String paramString)
/*      */   {
/*  648 */     paramString = paramString.replace(File.separatorChar, '/');
/*  649 */     Object localObject = "";
/*  650 */     for (String str : this.paths) {
/*  651 */       if ((paramString.startsWith(str)) && (str.length() > ((String)localObject).length()))
/*      */       {
/*  653 */         localObject = str;
/*      */       }
/*      */     }
/*  656 */     paramString = paramString.substring(((String)localObject).length());
/*      */ 
/*  658 */     if (paramString.startsWith("/"))
/*  659 */       paramString = paramString.substring(1);
/*  660 */     else if (paramString.startsWith("./")) {
/*  661 */       paramString = paramString.substring(2);
/*      */     }
/*  663 */     return paramString;
/*      */   }
/*      */ 
/*      */   private void addVersion(Manifest paramManifest) {
/*  667 */     Attributes localAttributes = paramManifest.getMainAttributes();
/*  668 */     if (localAttributes.getValue(Attributes.Name.MANIFEST_VERSION) == null)
/*  669 */       localAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
/*      */   }
/*      */ 
/*      */   private void addCreatedBy(Manifest paramManifest)
/*      */   {
/*  674 */     Attributes localAttributes = paramManifest.getMainAttributes();
/*  675 */     if (localAttributes.getValue(new Attributes.Name("Created-By")) == null) {
/*  676 */       String str1 = System.getProperty("java.vendor");
/*  677 */       String str2 = System.getProperty("java.version");
/*  678 */       localAttributes.put(new Attributes.Name("Created-By"), str2 + " (" + str1 + ")");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addMainClass(Manifest paramManifest, String paramString)
/*      */   {
/*  684 */     Attributes localAttributes = paramManifest.getMainAttributes();
/*      */ 
/*  687 */     localAttributes.put(Attributes.Name.MAIN_CLASS, paramString);
/*      */   }
/*      */ 
/*      */   private boolean isAmbiguousMainClass(Manifest paramManifest) {
/*  691 */     if (this.ename != null) {
/*  692 */       Attributes localAttributes = paramManifest.getMainAttributes();
/*  693 */       if (localAttributes.get(Attributes.Name.MAIN_CLASS) != null) {
/*  694 */         error(getMsg("error.bad.eflag"));
/*  695 */         usageError();
/*  696 */         return true;
/*      */       }
/*      */     }
/*  699 */     return false;
/*      */   }
/*      */ 
/*      */   void addFile(ZipOutputStream paramZipOutputStream, File paramFile)
/*      */     throws IOException
/*      */   {
/*  706 */     String str = paramFile.getPath();
/*  707 */     boolean bool = paramFile.isDirectory();
/*  708 */     if (bool) {
/*  709 */       str = str + File.separator;
/*      */     }
/*      */ 
/*  712 */     str = entryName(str);
/*      */ 
/*  714 */     if ((str.equals("")) || (str.equals(".")) || (str.equals(this.zname)))
/*  715 */       return;
/*  716 */     if (((str.equals("META-INF/")) || (str.equals("META-INF/MANIFEST.MF"))) && (!this.Mflag))
/*      */     {
/*  718 */       if (this.vflag) {
/*  719 */         output(formatMsg("out.ignore.entry", str));
/*      */       }
/*  721 */       return;
/*      */     }
/*      */ 
/*  724 */     long l1 = bool ? 0L : paramFile.length();
/*      */ 
/*  726 */     if (this.vflag) {
/*  727 */       this.out.print(formatMsg("out.adding", str));
/*      */     }
/*  729 */     ZipEntry localZipEntry = new ZipEntry(str);
/*  730 */     localZipEntry.setTime(paramFile.lastModified());
/*  731 */     if (l1 == 0L) {
/*  732 */       localZipEntry.setMethod(0);
/*  733 */       localZipEntry.setSize(0L);
/*  734 */       localZipEntry.setCrc(0L);
/*  735 */     } else if (this.flag0) {
/*  736 */       crc32File(localZipEntry, paramFile);
/*      */     }
/*  738 */     paramZipOutputStream.putNextEntry(localZipEntry);
/*  739 */     if (!bool) {
/*  740 */       copy(paramFile, paramZipOutputStream);
/*      */     }
/*  742 */     paramZipOutputStream.closeEntry();
/*      */ 
/*  744 */     if (this.vflag) {
/*  745 */       l1 = localZipEntry.getSize();
/*  746 */       long l2 = localZipEntry.getCompressedSize();
/*  747 */       this.out.print(formatMsg2("out.size", String.valueOf(l1), String.valueOf(l2)));
/*      */ 
/*  749 */       if (localZipEntry.getMethod() == 8) {
/*  750 */         long l3 = 0L;
/*  751 */         if (l1 != 0L) {
/*  752 */           l3 = (l1 - l2) * 100L / l1;
/*      */         }
/*  754 */         output(formatMsg("out.deflated", String.valueOf(l3)));
/*      */       } else {
/*  756 */         output(getMsg("out.stored"));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void copy(InputStream paramInputStream, OutputStream paramOutputStream)
/*      */     throws IOException
/*      */   {
/*      */     int i;
/*  778 */     while ((i = paramInputStream.read(this.copyBuf)) != -1)
/*  779 */       paramOutputStream.write(this.copyBuf, 0, i);
/*      */   }
/*      */ 
/*      */   private void copy(File paramFile, OutputStream paramOutputStream)
/*      */     throws IOException
/*      */   {
/*  791 */     FileInputStream localFileInputStream = new FileInputStream(paramFile);
/*      */     try {
/*  793 */       copy(localFileInputStream, paramOutputStream);
/*      */     } finally {
/*  795 */       localFileInputStream.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void copy(InputStream paramInputStream, File paramFile)
/*      */     throws IOException
/*      */   {
/*  808 */     FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
/*      */     try {
/*  810 */       copy(paramInputStream, localFileOutputStream);
/*      */     } finally {
/*  812 */       localFileOutputStream.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void crc32Manifest(ZipEntry paramZipEntry, Manifest paramManifest)
/*      */     throws IOException
/*      */   {
/*  821 */     CRC32OutputStream localCRC32OutputStream = new CRC32OutputStream();
/*  822 */     paramManifest.write(localCRC32OutputStream);
/*  823 */     localCRC32OutputStream.updateEntry(paramZipEntry);
/*      */   }
/*      */ 
/*      */   private void crc32File(ZipEntry paramZipEntry, File paramFile)
/*      */     throws IOException
/*      */   {
/*  831 */     CRC32OutputStream localCRC32OutputStream = new CRC32OutputStream();
/*  832 */     copy(paramFile, localCRC32OutputStream);
/*  833 */     if (localCRC32OutputStream.n != paramFile.length()) {
/*  834 */       throw new JarException(formatMsg("error.incorrect.length", paramFile.getPath()));
/*      */     }
/*      */ 
/*  837 */     localCRC32OutputStream.updateEntry(paramZipEntry);
/*      */   }
/*      */ 
/*      */   void replaceFSC(String[] paramArrayOfString) {
/*  841 */     if (paramArrayOfString != null)
/*  842 */       for (String str : paramArrayOfString)
/*  843 */         str = str.replace(File.separatorChar, '/');
/*      */   }
/*      */ 
/*      */   Set<ZipEntry> newDirSet()
/*      */   {
/*  850 */     return new HashSet() {
/*      */       public boolean add(ZipEntry paramAnonymousZipEntry) {
/*  852 */         return (paramAnonymousZipEntry == null) || (Main.useExtractionTime) ? false : super.add(paramAnonymousZipEntry);
/*      */       } } ;
/*      */   }
/*      */ 
/*      */   void updateLastModifiedTime(Set<ZipEntry> paramSet) throws IOException {
/*  857 */     for (ZipEntry localZipEntry : paramSet) {
/*  858 */       long l = localZipEntry.getTime();
/*  859 */       if (l != -1L) {
/*  860 */         File localFile = new File(localZipEntry.getName().replace('/', File.separatorChar));
/*  861 */         localFile.setLastModified(l);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void extract(InputStream paramInputStream, String[] paramArrayOfString)
/*      */     throws IOException
/*      */   {
/*  870 */     ZipInputStream localZipInputStream = new ZipInputStream(paramInputStream);
/*      */ 
/*  874 */     Set localSet = newDirSet();
/*      */     ZipEntry localZipEntry;
/*  875 */     while ((localZipEntry = localZipInputStream.getNextEntry()) != null) {
/*  876 */       if (paramArrayOfString == null) {
/*  877 */         localSet.add(extractFile(localZipInputStream, localZipEntry));
/*      */       } else {
/*  879 */         String str1 = localZipEntry.getName();
/*  880 */         for (String str2 : paramArrayOfString) {
/*  881 */           if (str1.startsWith(str2)) {
/*  882 */             localSet.add(extractFile(localZipInputStream, localZipEntry));
/*  883 */             break;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  893 */     updateLastModifiedTime(localSet);
/*      */   }
/*      */ 
/*      */   void extract(String paramString, String[] paramArrayOfString)
/*      */     throws IOException
/*      */   {
/*  900 */     ZipFile localZipFile = new ZipFile(paramString);
/*  901 */     Set localSet = newDirSet();
/*  902 */     Enumeration localEnumeration = localZipFile.entries();
/*  903 */     while (localEnumeration.hasMoreElements()) {
/*  904 */       ZipEntry localZipEntry = (ZipEntry)localEnumeration.nextElement();
/*      */ 
/*  906 */       if (paramArrayOfString == null) {
/*  907 */         localSet.add(extractFile(localZipFile.getInputStream(localZipEntry), localZipEntry));
/*      */       } else {
/*  909 */         String str1 = localZipEntry.getName();
/*  910 */         for (String str2 : paramArrayOfString) {
/*  911 */           if (str1.startsWith(str2)) {
/*  912 */             localSet.add(extractFile(localZipFile.getInputStream(localZipEntry), localZipEntry));
/*  913 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  918 */     localZipFile.close();
/*  919 */     updateLastModifiedTime(localSet);
/*      */   }
/*      */ 
/*      */   ZipEntry extractFile(InputStream paramInputStream, ZipEntry paramZipEntry)
/*      */     throws IOException
/*      */   {
/*  928 */     ZipEntry localZipEntry = null;
/*  929 */     String str = paramZipEntry.getName();
/*  930 */     File localFile1 = new File(paramZipEntry.getName().replace('/', File.separatorChar));
/*  931 */     if (paramZipEntry.isDirectory()) {
/*  932 */       if (localFile1.exists()) {
/*  933 */         if (!localFile1.isDirectory())
/*  934 */           throw new IOException(formatMsg("error.create.dir", localFile1.getPath()));
/*      */       }
/*      */       else
/*      */       {
/*  938 */         if (!localFile1.mkdirs()) {
/*  939 */           throw new IOException(formatMsg("error.create.dir", localFile1.getPath()));
/*      */         }
/*      */ 
/*  942 */         localZipEntry = paramZipEntry;
/*      */       }
/*      */ 
/*  946 */       if (this.vflag)
/*  947 */         output(formatMsg("out.create", str));
/*      */     }
/*      */     else {
/*  950 */       if (localFile1.getParent() != null) {
/*  951 */         File localFile2 = new File(localFile1.getParent());
/*  952 */         if (((!localFile2.exists()) && (!localFile2.mkdirs())) || (!localFile2.isDirectory())) {
/*  953 */           throw new IOException(formatMsg("error.create.dir", localFile2.getPath()));
/*      */         }
/*      */       }
/*      */       try
/*      */       {
/*  958 */         copy(paramInputStream, localFile1);
/*      */       } finally {
/*  960 */         if ((paramInputStream instanceof ZipInputStream))
/*  961 */           ((ZipInputStream)paramInputStream).closeEntry();
/*      */         else
/*  963 */           paramInputStream.close();
/*      */       }
/*  965 */       if (this.vflag) {
/*  966 */         if (paramZipEntry.getMethod() == 8)
/*  967 */           output(formatMsg("out.inflated", str));
/*      */         else {
/*  969 */           output(formatMsg("out.extracted", str));
/*      */         }
/*      */       }
/*      */     }
/*  973 */     if (!useExtractionTime) {
/*  974 */       long l = paramZipEntry.getTime();
/*  975 */       if (l != -1L) {
/*  976 */         localFile1.setLastModified(l);
/*      */       }
/*      */     }
/*  979 */     return localZipEntry;
/*      */   }
/*      */ 
/*      */   void list(InputStream paramInputStream, String[] paramArrayOfString)
/*      */     throws IOException
/*      */   {
/*  986 */     ZipInputStream localZipInputStream = new ZipInputStream(paramInputStream);
/*      */     ZipEntry localZipEntry;
/*  988 */     while ((localZipEntry = localZipInputStream.getNextEntry()) != null)
/*      */     {
/*  995 */       localZipInputStream.closeEntry();
/*  996 */       printEntry(localZipEntry, paramArrayOfString);
/*      */     }
/*      */   }
/*      */ 
/*      */   void list(String paramString, String[] paramArrayOfString)
/*      */     throws IOException
/*      */   {
/* 1004 */     ZipFile localZipFile = new ZipFile(paramString);
/* 1005 */     Enumeration localEnumeration = localZipFile.entries();
/* 1006 */     while (localEnumeration.hasMoreElements()) {
/* 1007 */       printEntry((ZipEntry)localEnumeration.nextElement(), paramArrayOfString);
/*      */     }
/* 1009 */     localZipFile.close();
/*      */   }
/*      */ 
/*      */   void dumpIndex(String paramString, JarIndex paramJarIndex)
/*      */     throws IOException
/*      */   {
/* 1017 */     File localFile = new File(paramString);
/* 1018 */     Path localPath1 = localFile.toPath();
/* 1019 */     Path localPath2 = createTempFileInSameDirectoryAs(localFile).toPath();
/*      */     try {
/* 1021 */       if (update(Files.newInputStream(localPath1, new OpenOption[0]), Files.newOutputStream(localPath2, new OpenOption[0]), null, paramJarIndex))
/*      */         try
/*      */         {
/*      */         }
/*      */         catch (IOException localIOException)
/*      */         {
/* 1027 */           throw new IOException(getMsg("error.write.file"), localIOException);
/*      */         }
/*      */     }
/*      */     finally {
/* 1031 */       Files.deleteIfExists(localPath2);
/*      */     }
/*      */   }
/*      */ 
/*      */   List<String> getJarPath(String paramString)
/*      */     throws IOException
/*      */   {
/* 1042 */     ArrayList localArrayList = new ArrayList();
/* 1043 */     localArrayList.add(paramString);
/* 1044 */     this.jarPaths.add(paramString);
/*      */ 
/* 1047 */     String str1 = paramString.substring(0, Math.max(0, paramString.lastIndexOf('/') + 1));
/*      */ 
/* 1052 */     JarFile localJarFile = new JarFile(paramString.replace('/', File.separatorChar));
/*      */ 
/* 1054 */     if (localJarFile != null) {
/* 1055 */       Manifest localManifest = localJarFile.getManifest();
/* 1056 */       if (localManifest != null) {
/* 1057 */         Attributes localAttributes = localManifest.getMainAttributes();
/* 1058 */         if (localAttributes != null) {
/* 1059 */           String str2 = localAttributes.getValue(Attributes.Name.CLASS_PATH);
/* 1060 */           if (str2 != null) {
/* 1061 */             StringTokenizer localStringTokenizer = new StringTokenizer(str2);
/* 1062 */             while (localStringTokenizer.hasMoreTokens()) {
/* 1063 */               String str3 = localStringTokenizer.nextToken();
/* 1064 */               if (!str3.endsWith("/")) {
/* 1065 */                 str3 = str1.concat(str3);
/*      */ 
/* 1067 */                 if (!this.jarPaths.contains(str3)) {
/* 1068 */                   localArrayList.addAll(getJarPath(str3));
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1076 */     localJarFile.close();
/* 1077 */     return localArrayList;
/*      */   }
/*      */ 
/*      */   void genIndex(String paramString, String[] paramArrayOfString)
/*      */     throws IOException
/*      */   {
/* 1084 */     List localList = getJarPath(paramString);
/* 1085 */     int i = localList.size();
/*      */ 
/* 1088 */     if ((i == 1) && (paramArrayOfString != null))
/*      */     {
/* 1091 */       for (int j = 0; j < paramArrayOfString.length; j++) {
/* 1092 */         localList.addAll(getJarPath(paramArrayOfString[j]));
/*      */       }
/* 1094 */       i = localList.size();
/*      */     }
/* 1096 */     String[] arrayOfString = (String[])localList.toArray(new String[i]);
/* 1097 */     JarIndex localJarIndex = new JarIndex(arrayOfString);
/* 1098 */     dumpIndex(paramString, localJarIndex);
/*      */   }
/*      */ 
/*      */   void printEntry(ZipEntry paramZipEntry, String[] paramArrayOfString)
/*      */     throws IOException
/*      */   {
/* 1105 */     if (paramArrayOfString == null) {
/* 1106 */       printEntry(paramZipEntry);
/*      */     } else {
/* 1108 */       String str1 = paramZipEntry.getName();
/* 1109 */       for (String str2 : paramArrayOfString)
/* 1110 */         if (str1.startsWith(str2)) {
/* 1111 */           printEntry(paramZipEntry);
/* 1112 */           return;
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   void printEntry(ZipEntry paramZipEntry)
/*      */     throws IOException
/*      */   {
/* 1122 */     if (this.vflag) {
/* 1123 */       StringBuilder localStringBuilder = new StringBuilder();
/* 1124 */       String str = Long.toString(paramZipEntry.getSize());
/* 1125 */       for (int i = 6 - str.length(); i > 0; i--) {
/* 1126 */         localStringBuilder.append(' ');
/*      */       }
/* 1128 */       localStringBuilder.append(str).append(' ').append(new Date(paramZipEntry.getTime()).toString());
/* 1129 */       localStringBuilder.append(' ').append(paramZipEntry.getName());
/* 1130 */       output(localStringBuilder.toString());
/*      */     } else {
/* 1132 */       output(paramZipEntry.getName());
/*      */     }
/*      */   }
/*      */ 
/*      */   void usageError()
/*      */   {
/* 1140 */     error(getMsg("usage"));
/*      */   }
/*      */ 
/*      */   void fatalError(Exception paramException)
/*      */   {
/* 1147 */     paramException.printStackTrace();
/*      */   }
/*      */ 
/*      */   void fatalError(String paramString)
/*      */   {
/* 1155 */     error(this.program + ": " + paramString);
/*      */   }
/*      */ 
/*      */   protected void output(String paramString)
/*      */   {
/* 1162 */     this.out.println(paramString);
/*      */   }
/*      */ 
/*      */   protected void error(String paramString)
/*      */   {
/* 1169 */     this.err.println(paramString);
/*      */   }
/*      */ 
/*      */   public static void main(String[] paramArrayOfString)
/*      */   {
/* 1176 */     Main localMain = new Main(System.out, System.err, "jar");
/* 1177 */     System.exit(localMain.run(paramArrayOfString) ? 0 : 1);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   88 */     useExtractionTime = Boolean.getBoolean("sun.tools.jar.useExtractionTime");
/*      */     try
/*      */     {
/*   96 */       rsrc = ResourceBundle.getBundle("sun.tools.jar.resources.jar");
/*      */     } catch (MissingResourceException localMissingResourceException) {
/*   98 */       throw new Error("Fatal: Resource for jar is missing");
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class CRC32OutputStream extends OutputStream
/*      */   {
/* 1186 */     final CRC32 crc = new CRC32();
/* 1187 */     long n = 0L;
/*      */ 
/*      */     public void write(int paramInt)
/*      */       throws IOException
/*      */     {
/* 1192 */       this.crc.update(paramInt);
/* 1193 */       this.n += 1L;
/*      */     }
/*      */ 
/*      */     public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
/* 1197 */       this.crc.update(paramArrayOfByte, paramInt1, paramInt2);
/* 1198 */       this.n += paramInt2;
/*      */     }
/*      */ 
/*      */     public void updateEntry(ZipEntry paramZipEntry)
/*      */     {
/* 1206 */       paramZipEntry.setMethod(0);
/* 1207 */       paramZipEntry.setSize(this.n);
/* 1208 */       paramZipEntry.setCrc(this.crc.getValue());
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.tools.jar.Main
 * JD-Core Version:    0.6.2
 */