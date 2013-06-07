/*     */ package javax.activation;
/*     */ 
/*     */ import com.sun.activation.registries.LogSupport;
/*     */ import com.sun.activation.registries.MimeTypeFile;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class MimetypesFileTypeMap extends FileTypeMap
/*     */ {
/*  76 */   private static MimeTypeFile defDB = null;
/*     */   private MimeTypeFile[] DB;
/*     */   private static final int PROG = 0;
/*  80 */   private static String defaultType = "application/octet-stream";
/*     */ 
/*     */   public MimetypesFileTypeMap()
/*     */   {
/*  86 */     Vector dbv = new Vector(5);
/*  87 */     MimeTypeFile mf = null;
/*  88 */     dbv.addElement(null);
/*     */ 
/*  90 */     LogSupport.log("MimetypesFileTypeMap: load HOME");
/*     */     try {
/*  92 */       String user_home = System.getProperty("user.home");
/*     */ 
/*  94 */       if (user_home != null) {
/*  95 */         String path = user_home + File.separator + ".mime.types";
/*  96 */         mf = loadFile(path);
/*  97 */         if (mf != null)
/*  98 */           dbv.addElement(mf);
/*     */       }
/*     */     } catch (SecurityException ex) {
/*     */     }
/* 102 */     LogSupport.log("MimetypesFileTypeMap: load SYS");
/*     */     try
/*     */     {
/* 105 */       String system_mimetypes = System.getProperty("java.home") + File.separator + "lib" + File.separator + "mime.types";
/*     */ 
/* 107 */       mf = loadFile(system_mimetypes);
/* 108 */       if (mf != null)
/* 109 */         dbv.addElement(mf);
/*     */     } catch (SecurityException ex) {
/*     */     }
/* 112 */     LogSupport.log("MimetypesFileTypeMap: load JAR");
/*     */ 
/* 114 */     loadAllResources(dbv, "META-INF/mime.types");
/*     */ 
/* 116 */     LogSupport.log("MimetypesFileTypeMap: load DEF");
/* 117 */     synchronized (MimetypesFileTypeMap.class)
/*     */     {
/* 119 */       if (defDB == null) {
/* 120 */         defDB = loadResource("/META-INF/mimetypes.default");
/*     */       }
/*     */     }
/* 123 */     if (defDB != null) {
/* 124 */       dbv.addElement(defDB);
/*     */     }
/* 126 */     this.DB = new MimeTypeFile[dbv.size()];
/* 127 */     dbv.copyInto(this.DB);
/*     */   }
/*     */ 
/*     */   private MimeTypeFile loadResource(String name)
/*     */   {
/* 134 */     InputStream clis = null;
/*     */     try {
/* 136 */       clis = SecuritySupport.getResourceAsStream(getClass(), name);
/* 137 */       if (clis != null) {
/* 138 */         MimeTypeFile mf = new MimeTypeFile(clis);
/* 139 */         if (LogSupport.isLoggable()) {
/* 140 */           LogSupport.log("MimetypesFileTypeMap: successfully loaded mime types file: " + name);
/*     */         }
/* 142 */         return mf;
/*     */       }
/* 144 */       if (LogSupport.isLoggable())
/* 145 */         LogSupport.log("MimetypesFileTypeMap: not loading mime types file: " + name);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 149 */       if (LogSupport.isLoggable())
/* 150 */         LogSupport.log("MimetypesFileTypeMap: can't load " + name, e);
/*     */     } catch (SecurityException sex) {
/* 152 */       if (LogSupport.isLoggable())
/* 153 */         LogSupport.log("MimetypesFileTypeMap: can't load " + name, sex);
/*     */     } finally {
/*     */       try {
/* 156 */         if (clis != null)
/* 157 */           clis.close(); 
/*     */       } catch (IOException ex) {  }
/*     */ 
/*     */     }
/* 160 */     return null;
/*     */   }
/*     */ 
/*     */   private void loadAllResources(Vector v, String name)
/*     */   {
/* 167 */     boolean anyLoaded = false;
/*     */     try
/*     */     {
/* 170 */       ClassLoader cld = null;
/*     */ 
/* 172 */       cld = SecuritySupport.getContextClassLoader();
/* 173 */       if (cld == null)
/* 174 */         cld = getClass().getClassLoader();
/*     */       URL[] urls;
/*     */       URL[] urls;
/* 175 */       if (cld != null)
/* 176 */         urls = SecuritySupport.getResources(cld, name);
/*     */       else
/* 178 */         urls = SecuritySupport.getSystemResources(name);
/* 179 */       if (urls != null) {
/* 180 */         if (LogSupport.isLoggable())
/* 181 */           LogSupport.log("MimetypesFileTypeMap: getResources");
/* 182 */         for (int i = 0; i < urls.length; i++) {
/* 183 */           URL url = urls[i];
/* 184 */           InputStream clis = null;
/* 185 */           if (LogSupport.isLoggable())
/* 186 */             LogSupport.log("MimetypesFileTypeMap: URL " + url);
/*     */           try {
/* 188 */             clis = SecuritySupport.openStream(url);
/* 189 */             if (clis != null) {
/* 190 */               v.addElement(new MimeTypeFile(clis));
/* 191 */               anyLoaded = true;
/* 192 */               if (LogSupport.isLoggable()) {
/* 193 */                 LogSupport.log("MimetypesFileTypeMap: successfully loaded mime types from URL: " + url);
/*     */               }
/*     */ 
/*     */             }
/* 197 */             else if (LogSupport.isLoggable()) {
/* 198 */               LogSupport.log("MimetypesFileTypeMap: not loading mime types from URL: " + url);
/*     */             }
/*     */           }
/*     */           catch (IOException ioex)
/*     */           {
/* 203 */             if (LogSupport.isLoggable())
/* 204 */               LogSupport.log("MimetypesFileTypeMap: can't load " + url, ioex);
/*     */           }
/*     */           catch (SecurityException sex) {
/* 207 */             if (LogSupport.isLoggable())
/* 208 */               LogSupport.log("MimetypesFileTypeMap: can't load " + url, sex);
/*     */           }
/*     */           finally {
/*     */             try {
/* 212 */               if (clis != null)
/* 213 */                 clis.close(); 
/*     */             } catch (IOException cex) {
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     } catch (Exception ex) { if (LogSupport.isLoggable()) {
/* 220 */         LogSupport.log("MimetypesFileTypeMap: can't load " + name, ex);
/*     */       }
/*     */     }
/*     */ 
/* 224 */     if (!anyLoaded) {
/* 225 */       LogSupport.log("MimetypesFileTypeMap: !anyLoaded");
/* 226 */       MimeTypeFile mf = loadResource("/" + name);
/* 227 */       if (mf != null)
/* 228 */         v.addElement(mf);
/*     */     }
/*     */   }
/*     */ 
/*     */   private MimeTypeFile loadFile(String name)
/*     */   {
/* 236 */     MimeTypeFile mtf = null;
/*     */     try
/*     */     {
/* 239 */       mtf = new MimeTypeFile(name);
/*     */     }
/*     */     catch (IOException e) {
/*     */     }
/* 243 */     return mtf;
/*     */   }
/*     */ 
/*     */   public MimetypesFileTypeMap(String mimeTypeFileName)
/*     */     throws IOException
/*     */   {
/* 253 */     this();
/* 254 */     this.DB[0] = new MimeTypeFile(mimeTypeFileName);
/*     */   }
/*     */ 
/*     */   public MimetypesFileTypeMap(InputStream is)
/*     */   {
/* 264 */     this();
/*     */     try {
/* 266 */       this.DB[0] = new MimeTypeFile(is);
/*     */     }
/*     */     catch (IOException ex)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void addMimeTypes(String mime_types)
/*     */   {
/* 279 */     if (this.DB[0] == null) {
/* 280 */       this.DB[0] = new MimeTypeFile();
/*     */     }
/* 282 */     this.DB[0].appendToRegistry(mime_types);
/*     */   }
/*     */ 
/*     */   public String getContentType(File f)
/*     */   {
/* 294 */     return getContentType(f.getName());
/*     */   }
/*     */ 
/*     */   public synchronized String getContentType(String filename)
/*     */   {
/* 307 */     int dot_pos = filename.lastIndexOf(".");
/*     */ 
/* 309 */     if (dot_pos < 0) {
/* 310 */       return defaultType;
/*     */     }
/* 312 */     String file_ext = filename.substring(dot_pos + 1);
/* 313 */     if (file_ext.length() == 0) {
/* 314 */       return defaultType;
/*     */     }
/* 316 */     for (int i = 0; i < this.DB.length; i++)
/* 317 */       if (this.DB[i] != null)
/*     */       {
/* 319 */         String result = this.DB[i].getMIMETypeString(file_ext);
/* 320 */         if (result != null)
/* 321 */           return result;
/*     */       }
/* 323 */     return defaultType;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.activation.MimetypesFileTypeMap
 * JD-Core Version:    0.6.2
 */