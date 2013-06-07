/*    */ package sun.nio.fs;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.file.LinkOption;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.attribute.BasicFileAttributes;
/*    */ import java.nio.file.attribute.DosFileAttributeView;
/*    */ import java.nio.file.attribute.DosFileAttributes;
/*    */ import java.nio.file.attribute.FileAttributeView;
/*    */ import java.nio.file.attribute.UserDefinedFileAttributeView;
/*    */ 
/*    */ public class LinuxFileSystemProvider extends UnixFileSystemProvider
/*    */ {
/*    */   LinuxFileSystem newFileSystem(String paramString)
/*    */   {
/* 43 */     return new LinuxFileSystem(this, paramString);
/*    */   }
/*    */ 
/*    */   LinuxFileStore getFileStore(UnixPath paramUnixPath) throws IOException
/*    */   {
/* 48 */     return new LinuxFileStore(paramUnixPath);
/*    */   }
/*    */ 
/*    */   public <V extends FileAttributeView> V getFileAttributeView(Path paramPath, Class<V> paramClass, LinkOption[] paramArrayOfLinkOption)
/*    */   {
/* 57 */     if (paramClass == DosFileAttributeView.class) {
/* 58 */       return new LinuxDosFileAttributeView(UnixPath.toUnixPath(paramPath), Util.followLinks(paramArrayOfLinkOption));
/*    */     }
/*    */ 
/* 61 */     if (paramClass == UserDefinedFileAttributeView.class) {
/* 62 */       return new LinuxUserDefinedFileAttributeView(UnixPath.toUnixPath(paramPath), Util.followLinks(paramArrayOfLinkOption));
/*    */     }
/*    */ 
/* 65 */     return super.getFileAttributeView(paramPath, paramClass, paramArrayOfLinkOption);
/*    */   }
/*    */ 
/*    */   public DynamicFileAttributeView getFileAttributeView(Path paramPath, String paramString, LinkOption[] paramArrayOfLinkOption)
/*    */   {
/* 73 */     if (paramString.equals("dos")) {
/* 74 */       return new LinuxDosFileAttributeView(UnixPath.toUnixPath(paramPath), Util.followLinks(paramArrayOfLinkOption));
/*    */     }
/*    */ 
/* 77 */     if (paramString.equals("user")) {
/* 78 */       return new LinuxUserDefinedFileAttributeView(UnixPath.toUnixPath(paramPath), Util.followLinks(paramArrayOfLinkOption));
/*    */     }
/*    */ 
/* 81 */     return super.getFileAttributeView(paramPath, paramString, paramArrayOfLinkOption);
/*    */   }
/*    */ 
/*    */   public <A extends BasicFileAttributes> A readAttributes(Path paramPath, Class<A> paramClass, LinkOption[] paramArrayOfLinkOption)
/*    */     throws IOException
/*    */   {
/* 91 */     if (paramClass == DosFileAttributes.class) {
/* 92 */       DosFileAttributeView localDosFileAttributeView = (DosFileAttributeView)getFileAttributeView(paramPath, DosFileAttributeView.class, paramArrayOfLinkOption);
/*    */ 
/* 94 */       return localDosFileAttributeView.readAttributes();
/*    */     }
/* 96 */     return super.readAttributes(paramPath, paramClass, paramArrayOfLinkOption);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.LinuxFileSystemProvider
 * JD-Core Version:    0.6.2
 */