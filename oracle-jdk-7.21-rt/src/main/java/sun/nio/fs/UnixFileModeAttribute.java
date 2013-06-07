/*    */ package sun.nio.fs;
/*    */ 
/*    */ import java.nio.file.attribute.FileAttribute;
/*    */ import java.nio.file.attribute.PosixFilePermission;
/*    */ import java.util.Set;
/*    */ 
/*    */ class UnixFileModeAttribute
/*    */ {
/*    */   static final int ALL_PERMISSIONS = 511;
/*    */   static final int ALL_READWRITE = 438;
/*    */   static final int TEMPFILE_PERMISSIONS = 448;
/*    */ 
/*    */   static int toUnixMode(Set<PosixFilePermission> paramSet)
/*    */   {
/* 49 */     int i = 0;
/* 50 */     for (PosixFilePermission localPosixFilePermission : paramSet) {
/* 51 */       if (localPosixFilePermission == null)
/* 52 */         throw new NullPointerException();
/* 53 */       switch (1.$SwitchMap$java$nio$file$attribute$PosixFilePermission[localPosixFilePermission.ordinal()]) { case 1:
/* 54 */         i |= 256; break;
/*    */       case 2:
/* 55 */         i |= 128; break;
/*    */       case 3:
/* 56 */         i |= 64; break;
/*    */       case 4:
/* 57 */         i |= 32; break;
/*    */       case 5:
/* 58 */         i |= 16; break;
/*    */       case 6:
/* 59 */         i |= 8; break;
/*    */       case 7:
/* 60 */         i |= 4; break;
/*    */       case 8:
/* 61 */         i |= 2; break;
/*    */       case 9:
/* 62 */         i |= 1;
/*    */       }
/*    */     }
/* 65 */     return i;
/*    */   }
/*    */ 
/*    */   static int toUnixMode(int paramInt, FileAttribute<?>[] paramArrayOfFileAttribute)
/*    */   {
/* 70 */     int i = paramInt;
/* 71 */     for (FileAttribute<?> localFileAttribute : paramArrayOfFileAttribute) {
/* 72 */       String str = localFileAttribute.name();
/* 73 */       if ((!str.equals("posix:permissions")) && (!str.equals("unix:permissions"))) {
/* 74 */         throw new UnsupportedOperationException("'" + localFileAttribute.name() + "' not supported as initial attribute");
/*    */       }
/*    */ 
/* 77 */       i = toUnixMode((Set)localFileAttribute.value());
/*    */     }
/* 79 */     return i;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.UnixFileModeAttribute
 * JD-Core Version:    0.6.2
 */