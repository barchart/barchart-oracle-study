/*    */ package java.lang;
/*    */ 
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.Map;
/*    */ import sun.misc.JavaIOFileDescriptorAccess;
/*    */ import sun.misc.SharedSecrets;
/*    */ 
/*    */ final class ProcessImpl
/*    */ {
/* 42 */   private static final JavaIOFileDescriptorAccess fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
/*    */ 
/*    */   private static byte[] toCString(String paramString)
/*    */   {
/* 48 */     if (paramString == null)
/* 49 */       return null;
/* 50 */     byte[] arrayOfByte1 = paramString.getBytes();
/* 51 */     byte[] arrayOfByte2 = new byte[arrayOfByte1.length + 1];
/* 52 */     System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, arrayOfByte1.length);
/*    */ 
/* 55 */     arrayOfByte2[(arrayOfByte2.length - 1)] = 0;
/* 56 */     return arrayOfByte2;
/*    */   }
/*    */ 
/*    */   static Process start(String[] paramArrayOfString, Map<String, String> paramMap, String paramString, ProcessBuilder.Redirect[] paramArrayOfRedirect, boolean paramBoolean)
/*    */     throws IOException
/*    */   {
/* 67 */     assert ((paramArrayOfString != null) && (paramArrayOfString.length > 0));
/*    */ 
/* 71 */     byte[][] arrayOfByte = new byte[paramArrayOfString.length - 1][];
/* 72 */     int i = arrayOfByte.length;
/* 73 */     for (int j = 0; j < arrayOfByte.length; j++) {
/* 74 */       arrayOfByte[j] = paramArrayOfString[(j + 1)].getBytes();
/* 75 */       i += arrayOfByte[j].length;
/*    */     }
/* 77 */     byte[] arrayOfByte1 = new byte[i];
/* 78 */     int k = 0;
/* 79 */     for (localObject2 : arrayOfByte) {
/* 80 */       System.arraycopy(localObject2, 0, arrayOfByte1, k, localObject2.length);
/* 81 */       k += localObject2.length + 1;
/*    */     }
/*    */ 
/* 85 */     ??? = new int[1];
/* 86 */     byte[] arrayOfByte2 = ProcessEnvironment.toEnvironmentBlock(paramMap, (int[])???);
/*    */ 
/* 90 */     Object localObject2 = null;
/* 91 */     FileOutputStream localFileOutputStream1 = null;
/* 92 */     FileOutputStream localFileOutputStream2 = null;
/*    */     try
/*    */     {
/*    */       int[] arrayOfInt;
/* 95 */       if (paramArrayOfRedirect == null) {
/* 96 */         arrayOfInt = new int[] { -1, -1, -1 };
/*    */       } else {
/* 98 */         arrayOfInt = new int[3];
/*    */ 
/* 100 */         if (paramArrayOfRedirect[0] == ProcessBuilder.Redirect.PIPE) {
/* 101 */           arrayOfInt[0] = -1;
/* 102 */         } else if (paramArrayOfRedirect[0] == ProcessBuilder.Redirect.INHERIT) {
/* 103 */           arrayOfInt[0] = 0;
/*    */         } else {
/* 105 */           localObject2 = new FileInputStream(paramArrayOfRedirect[0].file());
/* 106 */           arrayOfInt[0] = fdAccess.get(((FileInputStream)localObject2).getFD());
/*    */         }
/*    */ 
/* 109 */         if (paramArrayOfRedirect[1] == ProcessBuilder.Redirect.PIPE) {
/* 110 */           arrayOfInt[1] = -1;
/* 111 */         } else if (paramArrayOfRedirect[1] == ProcessBuilder.Redirect.INHERIT) {
/* 112 */           arrayOfInt[1] = 1;
/*    */         } else {
/* 114 */           localFileOutputStream1 = new FileOutputStream(paramArrayOfRedirect[1].file(), paramArrayOfRedirect[1].append());
/*    */ 
/* 116 */           arrayOfInt[1] = fdAccess.get(localFileOutputStream1.getFD());
/*    */         }
/*    */ 
/* 119 */         if (paramArrayOfRedirect[2] == ProcessBuilder.Redirect.PIPE) {
/* 120 */           arrayOfInt[2] = -1;
/* 121 */         } else if (paramArrayOfRedirect[2] == ProcessBuilder.Redirect.INHERIT) {
/* 122 */           arrayOfInt[2] = 2;
/*    */         } else {
/* 124 */           localFileOutputStream2 = new FileOutputStream(paramArrayOfRedirect[2].file(), paramArrayOfRedirect[2].append());
/*    */ 
/* 126 */           arrayOfInt[2] = fdAccess.get(localFileOutputStream2.getFD());
/*    */         }
/*    */       }
/*    */ 
/* 130 */       return new UNIXProcess(toCString(paramArrayOfString[0]), arrayOfByte1, arrayOfByte.length, arrayOfByte2, ???[0], toCString(paramString), arrayOfInt, paramBoolean);
/*    */     }
/*    */     finally
/*    */     {
/*    */       try
/*    */       {
/* 140 */         if (localObject2 != null) ((FileInputStream)localObject2).close(); 
/*    */       } finally {
/*    */         try { if (localFileOutputStream1 != null) localFileOutputStream1.close();  } finally {
/* 143 */           if (localFileOutputStream2 != null) localFileOutputStream2.close();
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.ProcessImpl
 * JD-Core Version:    0.6.2
 */