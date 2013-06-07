/*      */ package java.awt.datatransfer;
/*      */ 
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.CharArrayReader;
/*      */ import java.io.Externalizable;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.ObjectInput;
/*      */ import java.io.ObjectOutput;
/*      */ import java.io.OptionalDataException;
/*      */ import java.io.Reader;
/*      */ import java.io.Serializable;
/*      */ import java.io.StringReader;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.List;
/*      */ import sun.awt.datatransfer.DataTransferer;
/*      */ import sun.awt.datatransfer.DataTransferer.DataFlavorComparator;
/*      */ 
/*      */ public class DataFlavor
/*      */   implements Externalizable, Cloneable
/*      */ {
/*      */   private static final long serialVersionUID = 8367026044764648243L;
/*  105 */   private static final Class ioInputStreamClass = InputStream.class;
/*      */ 
/*  173 */   public static final DataFlavor stringFlavor = createConstant(String.class, "Unicode String");
/*      */ 
/*  183 */   public static final DataFlavor imageFlavor = createConstant("image/x-java-image; class=java.awt.Image", "Image");
/*      */ 
/*      */   @Deprecated
/*  202 */   public static final DataFlavor plainTextFlavor = createConstant("text/plain; charset=unicode; class=java.io.InputStream", "Plain Text");
/*      */   public static final String javaSerializedObjectMimeType = "application/x-java-serialized-object";
/*  221 */   public static final DataFlavor javaFileListFlavor = createConstant("application/x-java-file-list;class=java.util.List", null);
/*      */   public static final String javaJVMLocalObjectMimeType = "application/x-java-jvm-local-objectref";
/*      */   public static final String javaRemoteObjectMimeType = "application/x-java-remote-object";
/*      */   private static Comparator textFlavorComparator;
/*      */   transient int atom;
/*      */   MimeType mimeType;
/*      */   private String humanPresentableName;
/*      */   private Class representationClass;
/*      */ 
/*      */   protected static final Class<?> tryToLoadClass(String paramString, ClassLoader paramClassLoader)
/*      */     throws ClassNotFoundException
/*      */   {
/*  120 */     ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run()
/*      */       {
/*  124 */         ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
/*      */ 
/*  126 */         return localClassLoader != null ? localClassLoader : ClassLoader.getSystemClassLoader();
/*      */       }
/*      */ 
/*      */     });
/*      */     try
/*      */     {
/*  133 */       return Class.forName(paramString, true, localClassLoader);
/*      */     } catch (ClassNotFoundException localClassNotFoundException) {
/*  135 */       if (paramClassLoader != null)
/*  136 */         return Class.forName(paramString, true, paramClassLoader);
/*      */     }
/*  138 */     throw new ClassNotFoundException(paramString);
/*      */   }
/*      */ 
/*      */   private static DataFlavor createConstant(Class paramClass, String paramString)
/*      */   {
/*      */     try
/*      */     {
/*  148 */       return new DataFlavor(paramClass, paramString); } catch (Exception localException) {
/*      */     }
/*  150 */     return null;
/*      */   }
/*      */ 
/*      */   private static DataFlavor createConstant(String paramString1, String paramString2)
/*      */   {
/*      */     try
/*      */     {
/*  159 */       return new DataFlavor(paramString1, paramString2); } catch (Exception localException) {
/*      */     }
/*  161 */     return null;
/*      */   }
/*      */ 
/*      */   public DataFlavor()
/*      */   {
/*      */   }
/*      */ 
/*      */   private DataFlavor(String paramString1, String paramString2, MimeTypeParameterList paramMimeTypeParameterList, Class paramClass, String paramString3)
/*      */   {
/*  268 */     if (paramString1 == null) {
/*  269 */       throw new NullPointerException("primaryType");
/*      */     }
/*  271 */     if (paramString2 == null) {
/*  272 */       throw new NullPointerException("subType");
/*      */     }
/*  274 */     if (paramClass == null) {
/*  275 */       throw new NullPointerException("representationClass");
/*      */     }
/*      */ 
/*  278 */     if (paramMimeTypeParameterList == null) paramMimeTypeParameterList = new MimeTypeParameterList();
/*      */ 
/*  280 */     paramMimeTypeParameterList.set("class", paramClass.getName());
/*      */ 
/*  282 */     if (paramString3 == null) {
/*  283 */       paramString3 = paramMimeTypeParameterList.get("humanPresentableName");
/*      */ 
/*  285 */       if (paramString3 == null)
/*  286 */         paramString3 = paramString1 + "/" + paramString2;
/*      */     }
/*      */     try
/*      */     {
/*  290 */       this.mimeType = new MimeType(paramString1, paramString2, paramMimeTypeParameterList);
/*      */     } catch (MimeTypeParseException localMimeTypeParseException) {
/*  292 */       throw new IllegalArgumentException("MimeType Parse Exception: " + localMimeTypeParseException.getMessage());
/*      */     }
/*      */ 
/*  295 */     this.representationClass = paramClass;
/*  296 */     this.humanPresentableName = paramString3;
/*      */ 
/*  298 */     this.mimeType.removeParameter("humanPresentableName");
/*      */   }
/*      */ 
/*      */   public DataFlavor(Class<?> paramClass, String paramString)
/*      */   {
/*  317 */     this("application", "x-java-serialized-object", null, paramClass, paramString);
/*  318 */     if (paramClass == null)
/*  319 */       throw new NullPointerException("representationClass");
/*      */   }
/*      */ 
/*      */   public DataFlavor(String paramString1, String paramString2)
/*      */   {
/*  354 */     if (paramString1 == null)
/*  355 */       throw new NullPointerException("mimeType");
/*      */     try
/*      */     {
/*  358 */       initialize(paramString1, paramString2, getClass().getClassLoader());
/*      */     } catch (MimeTypeParseException localMimeTypeParseException) {
/*  360 */       throw new IllegalArgumentException("failed to parse:" + paramString1);
/*      */     } catch (ClassNotFoundException localClassNotFoundException) {
/*  362 */       throw new IllegalArgumentException("can't find specified class: " + localClassNotFoundException.getMessage());
/*      */     }
/*      */   }
/*      */ 
/*      */   public DataFlavor(String paramString1, String paramString2, ClassLoader paramClassLoader)
/*      */     throws ClassNotFoundException
/*      */   {
/*  394 */     if (paramString1 == null)
/*  395 */       throw new NullPointerException("mimeType");
/*      */     try
/*      */     {
/*  398 */       initialize(paramString1, paramString2, paramClassLoader);
/*      */     } catch (MimeTypeParseException localMimeTypeParseException) {
/*  400 */       throw new IllegalArgumentException("failed to parse:" + paramString1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public DataFlavor(String paramString)
/*      */     throws ClassNotFoundException
/*      */   {
/*  422 */     if (paramString == null)
/*  423 */       throw new NullPointerException("mimeType");
/*      */     try
/*      */     {
/*  426 */       initialize(paramString, null, getClass().getClassLoader());
/*      */     } catch (MimeTypeParseException localMimeTypeParseException) {
/*  428 */       throw new IllegalArgumentException("failed to parse:" + paramString);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initialize(String paramString1, String paramString2, ClassLoader paramClassLoader)
/*      */     throws MimeTypeParseException, ClassNotFoundException
/*      */   {
/*  447 */     if (paramString1 == null) {
/*  448 */       throw new NullPointerException("mimeType");
/*      */     }
/*      */ 
/*  451 */     this.mimeType = new MimeType(paramString1);
/*      */ 
/*  453 */     String str = getParameter("class");
/*      */ 
/*  455 */     if (str == null) {
/*  456 */       if ("application/x-java-serialized-object".equals(this.mimeType.getBaseType()))
/*      */       {
/*  458 */         throw new IllegalArgumentException("no representation class specified for:" + paramString1);
/*      */       }
/*  460 */       this.representationClass = InputStream.class;
/*      */     } else {
/*  462 */       this.representationClass = tryToLoadClass(str, paramClassLoader);
/*      */     }
/*      */ 
/*  465 */     this.mimeType.setParameter("class", this.representationClass.getName());
/*      */ 
/*  467 */     if (paramString2 == null) {
/*  468 */       paramString2 = this.mimeType.getParameter("humanPresentableName");
/*  469 */       if (paramString2 == null) {
/*  470 */         paramString2 = this.mimeType.getPrimaryType() + "/" + this.mimeType.getSubType();
/*      */       }
/*      */     }
/*  473 */     this.humanPresentableName = paramString2;
/*      */ 
/*  475 */     this.mimeType.removeParameter("humanPresentableName");
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  491 */     String str = getClass().getName();
/*  492 */     str = str + "[" + paramString() + "]";
/*  493 */     return str;
/*      */   }
/*      */ 
/*      */   private String paramString() {
/*  497 */     String str = "";
/*  498 */     str = str + "mimetype=";
/*  499 */     if (this.mimeType == null)
/*  500 */       str = str + "null";
/*      */     else {
/*  502 */       str = str + this.mimeType.getBaseType();
/*      */     }
/*  504 */     str = str + ";representationclass=";
/*  505 */     if (this.representationClass == null)
/*  506 */       str = str + "null";
/*      */     else {
/*  508 */       str = str + this.representationClass.getName();
/*      */     }
/*  510 */     if ((DataTransferer.isFlavorCharsetTextType(this)) && ((isRepresentationClassInputStream()) || (isRepresentationClassByteBuffer()) || (DataTransferer.byteArrayClass.equals(this.representationClass))))
/*      */     {
/*  515 */       str = str + ";charset=" + DataTransferer.getTextCharset(this);
/*      */     }
/*  517 */     return str;
/*      */   }
/*      */ 
/*      */   public static final DataFlavor getTextPlainUnicodeFlavor()
/*      */   {
/*  537 */     String str = null;
/*  538 */     DataTransferer localDataTransferer = DataTransferer.getInstance();
/*  539 */     if (localDataTransferer != null) {
/*  540 */       str = localDataTransferer.getDefaultUnicodeEncoding();
/*      */     }
/*  542 */     return new DataFlavor("text/plain;charset=" + str + ";class=java.io.InputStream", "Plain Text");
/*      */   }
/*      */ 
/*      */   public static final DataFlavor selectBestTextFlavor(DataFlavor[] paramArrayOfDataFlavor)
/*      */   {
/*  665 */     if ((paramArrayOfDataFlavor == null) || (paramArrayOfDataFlavor.length == 0)) {
/*  666 */       return null;
/*      */     }
/*      */ 
/*  669 */     if (textFlavorComparator == null) {
/*  670 */       textFlavorComparator = new TextFlavorComparator();
/*      */     }
/*      */ 
/*  673 */     DataFlavor localDataFlavor = (DataFlavor)Collections.max(Arrays.asList(paramArrayOfDataFlavor), textFlavorComparator);
/*      */ 
/*  677 */     if (!localDataFlavor.isFlavorTextType()) {
/*  678 */       return null;
/*      */     }
/*      */ 
/*  681 */     return localDataFlavor;
/*      */   }
/*      */ 
/*      */   public Reader getReaderForText(Transferable paramTransferable)
/*      */     throws UnsupportedFlavorException, IOException
/*      */   {
/*  770 */     Object localObject1 = paramTransferable.getTransferData(this);
/*  771 */     if (localObject1 == null) {
/*  772 */       throw new IllegalArgumentException("getTransferData() returned null");
/*      */     }
/*      */ 
/*  776 */     if ((localObject1 instanceof Reader))
/*  777 */       return (Reader)localObject1;
/*  778 */     if ((localObject1 instanceof String))
/*  779 */       return new StringReader((String)localObject1);
/*  780 */     if ((localObject1 instanceof CharBuffer)) {
/*  781 */       localObject2 = (CharBuffer)localObject1;
/*  782 */       int i = ((CharBuffer)localObject2).remaining();
/*  783 */       char[] arrayOfChar = new char[i];
/*  784 */       ((CharBuffer)localObject2).get(arrayOfChar, 0, i);
/*  785 */       return new CharArrayReader(arrayOfChar);
/*  786 */     }if ((localObject1 instanceof char[])) {
/*  787 */       return new CharArrayReader((char[])localObject1);
/*      */     }
/*      */ 
/*  790 */     Object localObject2 = null;
/*      */ 
/*  792 */     if ((localObject1 instanceof InputStream)) {
/*  793 */       localObject2 = (InputStream)localObject1;
/*  794 */     } else if ((localObject1 instanceof ByteBuffer)) {
/*  795 */       localObject3 = (ByteBuffer)localObject1;
/*  796 */       int j = ((ByteBuffer)localObject3).remaining();
/*  797 */       byte[] arrayOfByte = new byte[j];
/*  798 */       ((ByteBuffer)localObject3).get(arrayOfByte, 0, j);
/*  799 */       localObject2 = new ByteArrayInputStream(arrayOfByte);
/*  800 */     } else if ((localObject1 instanceof byte[])) {
/*  801 */       localObject2 = new ByteArrayInputStream((byte[])localObject1);
/*      */     }
/*      */ 
/*  804 */     if (localObject2 == null) {
/*  805 */       throw new IllegalArgumentException("transfer data is not Reader, String, CharBuffer, char array, InputStream, ByteBuffer, or byte array");
/*      */     }
/*      */ 
/*  808 */     Object localObject3 = getParameter("charset");
/*  809 */     return localObject3 == null ? new InputStreamReader((InputStream)localObject2) : new InputStreamReader((InputStream)localObject2, (String)localObject3);
/*      */   }
/*      */ 
/*      */   public String getMimeType()
/*      */   {
/*  819 */     return this.mimeType != null ? this.mimeType.toString() : null;
/*      */   }
/*      */ 
/*      */   public Class<?> getRepresentationClass()
/*      */   {
/*  831 */     return this.representationClass;
/*      */   }
/*      */ 
/*      */   public String getHumanPresentableName()
/*      */   {
/*  842 */     return this.humanPresentableName;
/*      */   }
/*      */ 
/*      */   public String getPrimaryType()
/*      */   {
/*  850 */     return this.mimeType != null ? this.mimeType.getPrimaryType() : null;
/*      */   }
/*      */ 
/*      */   public String getSubType()
/*      */   {
/*  858 */     return this.mimeType != null ? this.mimeType.getSubType() : null;
/*      */   }
/*      */ 
/*      */   public String getParameter(String paramString)
/*      */   {
/*  871 */     if (paramString.equals("humanPresentableName")) {
/*  872 */       return this.humanPresentableName;
/*      */     }
/*  874 */     return this.mimeType != null ? this.mimeType.getParameter(paramString) : null;
/*      */   }
/*      */ 
/*      */   public void setHumanPresentableName(String paramString)
/*      */   {
/*  886 */     this.humanPresentableName = paramString;
/*      */   }
/*      */ 
/*      */   public boolean equals(Object paramObject)
/*      */   {
/*  911 */     return ((paramObject instanceof DataFlavor)) && (equals((DataFlavor)paramObject));
/*      */   }
/*      */ 
/*      */   public boolean equals(DataFlavor paramDataFlavor)
/*      */   {
/*  926 */     if (paramDataFlavor == null) {
/*  927 */       return false;
/*      */     }
/*  929 */     if (this == paramDataFlavor) {
/*  930 */       return true;
/*      */     }
/*      */ 
/*  933 */     if (this.representationClass == null) {
/*  934 */       if (paramDataFlavor.getRepresentationClass() != null) {
/*  935 */         return false;
/*      */       }
/*      */     }
/*  938 */     else if (!this.representationClass.equals(paramDataFlavor.getRepresentationClass())) {
/*  939 */       return false;
/*      */     }
/*      */ 
/*  943 */     if (this.mimeType == null) {
/*  944 */       if (paramDataFlavor.mimeType != null)
/*  945 */         return false;
/*      */     }
/*      */     else {
/*  948 */       if (!this.mimeType.match(paramDataFlavor.mimeType)) {
/*  949 */         return false;
/*      */       }
/*      */ 
/*  952 */       if (("text".equals(getPrimaryType())) && (DataTransferer.doesSubtypeSupportCharset(this)) && (this.representationClass != null) && (!isRepresentationClassReader()) && (!String.class.equals(this.representationClass)) && (!isRepresentationClassCharBuffer()) && (!DataTransferer.charArrayClass.equals(this.representationClass)))
/*      */       {
/*  960 */         String str1 = DataTransferer.canonicalName(getParameter("charset"));
/*      */ 
/*  962 */         String str2 = DataTransferer.canonicalName(paramDataFlavor.getParameter("charset"));
/*      */ 
/*  964 */         if (str1 == null) {
/*  965 */           if (str2 != null) {
/*  966 */             return false;
/*      */           }
/*      */         }
/*  969 */         else if (!str1.equals(str2)) {
/*  970 */           return false;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  976 */     return true;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean equals(String paramString)
/*      */   {
/*  994 */     if ((paramString == null) || (this.mimeType == null))
/*  995 */       return false;
/*  996 */     return isMimeTypeEqual(paramString);
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/* 1010 */     int i = 0;
/*      */ 
/* 1012 */     if (this.representationClass != null) {
/* 1013 */       i += this.representationClass.hashCode();
/*      */     }
/*      */ 
/* 1016 */     if (this.mimeType != null) {
/* 1017 */       String str1 = this.mimeType.getPrimaryType();
/* 1018 */       if (str1 != null) {
/* 1019 */         i += str1.hashCode();
/*      */       }
/*      */ 
/* 1026 */       if (("text".equals(str1)) && (DataTransferer.doesSubtypeSupportCharset(this)) && (this.representationClass != null) && (!isRepresentationClassReader()) && (!String.class.equals(this.representationClass)) && (!isRepresentationClassCharBuffer()) && (!DataTransferer.charArrayClass.equals(this.representationClass)))
/*      */       {
/* 1035 */         String str2 = DataTransferer.canonicalName(getParameter("charset"));
/*      */ 
/* 1037 */         if (str2 != null) {
/* 1038 */           i += str2.hashCode();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1043 */     return i;
/*      */   }
/*      */ 
/*      */   public boolean match(DataFlavor paramDataFlavor)
/*      */   {
/* 1057 */     return equals(paramDataFlavor);
/*      */   }
/*      */ 
/*      */   public boolean isMimeTypeEqual(String paramString)
/*      */   {
/* 1073 */     if (paramString == null) {
/* 1074 */       throw new NullPointerException("mimeType");
/*      */     }
/* 1076 */     if (this.mimeType == null)
/* 1077 */       return false;
/*      */     try
/*      */     {
/* 1080 */       return this.mimeType.match(new MimeType(paramString)); } catch (MimeTypeParseException localMimeTypeParseException) {
/*      */     }
/* 1082 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean isMimeTypeEqual(DataFlavor paramDataFlavor)
/*      */   {
/* 1096 */     return isMimeTypeEqual(paramDataFlavor.mimeType);
/*      */   }
/*      */ 
/*      */   private boolean isMimeTypeEqual(MimeType paramMimeType)
/*      */   {
/* 1108 */     if (this.mimeType == null) {
/* 1109 */       return paramMimeType == null;
/*      */     }
/* 1111 */     return this.mimeType.match(paramMimeType);
/*      */   }
/*      */ 
/*      */   public boolean isMimeTypeSerializedObject()
/*      */   {
/* 1119 */     return isMimeTypeEqual("application/x-java-serialized-object");
/*      */   }
/*      */ 
/*      */   public final Class<?> getDefaultRepresentationClass() {
/* 1123 */     return ioInputStreamClass;
/*      */   }
/*      */ 
/*      */   public final String getDefaultRepresentationClassAsString() {
/* 1127 */     return getDefaultRepresentationClass().getName();
/*      */   }
/*      */ 
/*      */   public boolean isRepresentationClassInputStream()
/*      */   {
/* 1136 */     return ioInputStreamClass.isAssignableFrom(this.representationClass);
/*      */   }
/*      */ 
/*      */   public boolean isRepresentationClassReader()
/*      */   {
/* 1147 */     return Reader.class.isAssignableFrom(this.representationClass);
/*      */   }
/*      */ 
/*      */   public boolean isRepresentationClassCharBuffer()
/*      */   {
/* 1158 */     return CharBuffer.class.isAssignableFrom(this.representationClass);
/*      */   }
/*      */ 
/*      */   public boolean isRepresentationClassByteBuffer()
/*      */   {
/* 1169 */     return ByteBuffer.class.isAssignableFrom(this.representationClass);
/*      */   }
/*      */ 
/*      */   public boolean isRepresentationClassSerializable()
/*      */   {
/* 1178 */     return Serializable.class.isAssignableFrom(this.representationClass);
/*      */   }
/*      */ 
/*      */   public boolean isRepresentationClassRemote()
/*      */   {
/* 1187 */     return DataTransferer.isRemote(this.representationClass);
/*      */   }
/*      */ 
/*      */   public boolean isFlavorSerializedObjectType()
/*      */   {
/* 1198 */     return (isRepresentationClassSerializable()) && (isMimeTypeEqual("application/x-java-serialized-object"));
/*      */   }
/*      */ 
/*      */   public boolean isFlavorRemoteObjectType()
/*      */   {
/* 1209 */     return (isRepresentationClassRemote()) && (isRepresentationClassSerializable()) && (isMimeTypeEqual("application/x-java-remote-object"));
/*      */   }
/*      */ 
/*      */   public boolean isFlavorJavaFileListType()
/*      */   {
/* 1223 */     if ((this.mimeType == null) || (this.representationClass == null))
/* 1224 */       return false;
/* 1225 */     return (List.class.isAssignableFrom(this.representationClass)) && (this.mimeType.match(javaFileListFlavor.mimeType));
/*      */   }
/*      */ 
/*      */   public boolean isFlavorTextType()
/*      */   {
/* 1261 */     return (DataTransferer.isFlavorCharsetTextType(this)) || (DataTransferer.isFlavorNoncharsetTextType(this));
/*      */   }
/*      */ 
/*      */   public synchronized void writeExternal(ObjectOutput paramObjectOutput)
/*      */     throws IOException
/*      */   {
/* 1270 */     if (this.mimeType != null) {
/* 1271 */       this.mimeType.setParameter("humanPresentableName", this.humanPresentableName);
/* 1272 */       paramObjectOutput.writeObject(this.mimeType);
/* 1273 */       this.mimeType.removeParameter("humanPresentableName");
/*      */     } else {
/* 1275 */       paramObjectOutput.writeObject(null);
/*      */     }
/*      */ 
/* 1278 */     paramObjectOutput.writeObject(this.representationClass);
/*      */   }
/*      */ 
/*      */   public synchronized void readExternal(ObjectInput paramObjectInput)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/* 1286 */     String str = null;
/* 1287 */     this.mimeType = ((MimeType)paramObjectInput.readObject());
/*      */ 
/* 1289 */     if (this.mimeType != null) {
/* 1290 */       this.humanPresentableName = this.mimeType.getParameter("humanPresentableName");
/*      */ 
/* 1292 */       this.mimeType.removeParameter("humanPresentableName");
/* 1293 */       str = this.mimeType.getParameter("class");
/* 1294 */       if (str == null) {
/* 1295 */         throw new IOException("no class parameter specified in: " + this.mimeType);
/*      */       }
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1301 */       this.representationClass = ((Class)paramObjectInput.readObject());
/*      */     } catch (OptionalDataException localOptionalDataException) {
/* 1303 */       if ((!localOptionalDataException.eof) || (localOptionalDataException.length != 0)) {
/* 1304 */         throw localOptionalDataException;
/*      */       }
/*      */ 
/* 1308 */       if (str != null)
/* 1309 */         this.representationClass = tryToLoadClass(str, getClass().getClassLoader());
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object clone()
/*      */     throws CloneNotSupportedException
/*      */   {
/* 1321 */     Object localObject = super.clone();
/* 1322 */     if (this.mimeType != null) {
/* 1323 */       ((DataFlavor)localObject).mimeType = ((MimeType)this.mimeType.clone());
/*      */     }
/* 1325 */     return localObject;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   protected String normalizeMimeTypeParameter(String paramString1, String paramString2)
/*      */   {
/* 1344 */     return paramString2;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   protected String normalizeMimeType(String paramString)
/*      */   {
/* 1360 */     return paramString;
/*      */   }
/*      */ 
/*      */   static class TextFlavorComparator extends DataTransferer.DataFlavorComparator
/*      */   {
/*      */     public int compare(Object paramObject1, Object paramObject2)
/*      */     {
/*  710 */       DataFlavor localDataFlavor1 = (DataFlavor)paramObject1;
/*  711 */       DataFlavor localDataFlavor2 = (DataFlavor)paramObject2;
/*      */ 
/*  713 */       if (localDataFlavor1.isFlavorTextType()) {
/*  714 */         if (localDataFlavor2.isFlavorTextType()) {
/*  715 */           return super.compare(paramObject1, paramObject2);
/*      */         }
/*  717 */         return 1;
/*      */       }
/*  719 */       if (localDataFlavor2.isFlavorTextType()) {
/*  720 */         return -1;
/*      */       }
/*  722 */       return 0;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.datatransfer.DataFlavor
 * JD-Core Version:    0.6.2
 */