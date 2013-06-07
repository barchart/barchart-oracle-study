/*    */ package sun.nio.fs;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.file.Path;
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
/*    */ 
/*    */ public class GnomeFileTypeDetector extends AbstractFileTypeDetector
/*    */ {
/*    */   private static final String GNOME_VFS_MIME_TYPE_UNKNOWN = "application/octet-stream";
/*    */   private final boolean gioAvailable;
/*    */   private final boolean gnomeVfsAvailable;
/*    */ 
/*    */   public GnomeFileTypeDetector()
/*    */   {
/* 51 */     this.gioAvailable = initializeGio();
/* 52 */     if (this.gioAvailable)
/* 53 */       this.gnomeVfsAvailable = false;
/*    */     else
/* 55 */       this.gnomeVfsAvailable = initializeGnomeVfs();
/*    */   }
/*    */ 
/*    */   public String implProbeContentType(Path paramPath)
/*    */     throws IOException
/*    */   {
/* 61 */     if ((!this.gioAvailable) && (!this.gnomeVfsAvailable))
/* 62 */       return null;
/* 63 */     if (!(paramPath instanceof UnixPath)) {
/* 64 */       return null;
/*    */     }
/* 66 */     UnixPath localUnixPath = (UnixPath)paramPath;
/* 67 */     NativeBuffer localNativeBuffer = NativeBuffers.asNativeBuffer(localUnixPath.getByteArrayForSysCalls());
/*    */     try {
/* 69 */       if (this.gioAvailable) {
/* 70 */         arrayOfByte = probeUsingGio(localNativeBuffer.address());
/* 71 */         return arrayOfByte == null ? null : new String(arrayOfByte);
/*    */       }
/* 73 */       byte[] arrayOfByte = probeUsingGnomeVfs(localNativeBuffer.address());
/* 74 */       if (arrayOfByte == null)
/* 75 */         return null;
/* 76 */       String str1 = new String(arrayOfByte);
/* 77 */       return str1.equals("application/octet-stream") ? null : str1;
/*    */     }
/*    */     finally
/*    */     {
/* 81 */       localNativeBuffer.release();
/*    */     }
/*    */   }
/*    */ 
/*    */   private static native boolean initializeGio();
/*    */ 
/*    */   private static native byte[] probeUsingGio(long paramLong);
/*    */ 
/*    */   private static native boolean initializeGnomeVfs();
/*    */ 
/*    */   private static native byte[] probeUsingGnomeVfs(long paramLong);
/*    */ 
/*    */   static
/*    */   {
/* 95 */     AccessController.doPrivileged(new PrivilegedAction() {
/*    */       public Void run() {
/* 97 */         System.loadLibrary("nio");
/* 98 */         return null;
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.GnomeFileTypeDetector
 * JD-Core Version:    0.6.2
 */