/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.file.ProviderMismatchException;
/*     */ import java.nio.file.attribute.BasicFileAttributes;
/*     */ import java.nio.file.attribute.FileTime;
/*     */ import java.nio.file.attribute.GroupPrincipal;
/*     */ import java.nio.file.attribute.PosixFileAttributeView;
/*     */ import java.nio.file.attribute.PosixFileAttributes;
/*     */ import java.nio.file.attribute.PosixFilePermission;
/*     */ import java.nio.file.attribute.UserPrincipal;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ class UnixFileAttributeViews
/*     */ {
/*     */   static Basic createBasicView(UnixPath paramUnixPath, boolean paramBoolean)
/*     */   {
/* 358 */     return new Basic(paramUnixPath, paramBoolean);
/*     */   }
/*     */ 
/*     */   static Posix createPosixView(UnixPath paramUnixPath, boolean paramBoolean) {
/* 362 */     return new Posix(paramUnixPath, paramBoolean);
/*     */   }
/*     */ 
/*     */   static Unix createUnixView(UnixPath paramUnixPath, boolean paramBoolean) {
/* 366 */     return new Unix(paramUnixPath, paramBoolean);
/*     */   }
/*     */ 
/*     */   static FileOwnerAttributeViewImpl createOwnerView(UnixPath paramUnixPath, boolean paramBoolean) {
/* 370 */     return new FileOwnerAttributeViewImpl(createPosixView(paramUnixPath, paramBoolean));
/*     */   }
/*     */ 
/*     */   static class Basic extends AbstractBasicFileAttributeView
/*     */   {
/*     */     protected final UnixPath file;
/*     */     protected final boolean followLinks;
/*     */ 
/*     */     Basic(UnixPath paramUnixPath, boolean paramBoolean)
/*     */     {
/*  43 */       this.file = paramUnixPath;
/*  44 */       this.followLinks = paramBoolean;
/*     */     }
/*     */ 
/*     */     public BasicFileAttributes readAttributes() throws IOException
/*     */     {
/*  49 */       this.file.checkRead();
/*     */       try {
/*  51 */         UnixFileAttributes localUnixFileAttributes = UnixFileAttributes.get(this.file, this.followLinks);
/*     */ 
/*  53 */         return localUnixFileAttributes.asBasicFileAttributes();
/*     */       } catch (UnixException localUnixException) {
/*  55 */         localUnixException.rethrowAsIOException(this.file);
/*  56 */       }return null;
/*     */     }
/*     */ 
/*     */     public void setTimes(FileTime paramFileTime1, FileTime paramFileTime2, FileTime paramFileTime3)
/*     */       throws IOException
/*     */     {
/*  66 */       if ((paramFileTime1 == null) && (paramFileTime2 == null))
/*     */       {
/*  68 */         return;
/*     */       }
/*     */ 
/*  72 */       this.file.checkWrite();
/*     */ 
/*  74 */       int i = this.file.openForAttributeAccess(this.followLinks);
/*     */       try
/*     */       {
/*  77 */         if ((paramFileTime1 == null) || (paramFileTime2 == null)) {
/*     */           try {
/*  79 */             UnixFileAttributes localUnixFileAttributes = UnixFileAttributes.get(i);
/*  80 */             if (paramFileTime1 == null)
/*  81 */               paramFileTime1 = localUnixFileAttributes.lastModifiedTime();
/*  82 */             if (paramFileTime2 == null)
/*  83 */               paramFileTime2 = localUnixFileAttributes.lastAccessTime();
/*     */           } catch (UnixException localUnixException1) {
/*  85 */             localUnixException1.rethrowAsIOException(this.file);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*  90 */         long l1 = paramFileTime1.to(TimeUnit.MICROSECONDS);
/*  91 */         long l2 = paramFileTime2.to(TimeUnit.MICROSECONDS);
/*     */ 
/*  93 */         int j = 0;
/*     */         try {
/*  95 */           UnixNativeDispatcher.futimes(i, l2, l1);
/*     */         }
/*     */         catch (UnixException localUnixException2)
/*     */         {
/*  99 */           if ((localUnixException2.errno() == 22) && ((l1 < 0L) || (l2 < 0L)))
/*     */           {
/* 101 */             j = 1;
/*     */           }
/* 103 */           else localUnixException2.rethrowAsIOException(this.file);
/*     */         }
/*     */ 
/* 106 */         if (j != 0) {
/* 107 */           if (l1 < 0L) l1 = 0L;
/* 108 */           if (l2 < 0L) l2 = 0L; try
/*     */           {
/* 110 */             UnixNativeDispatcher.futimes(i, l2, l1);
/*     */           } catch (UnixException localUnixException3) {
/* 112 */             localUnixException3.rethrowAsIOException(this.file);
/*     */           }
/*     */         }
/*     */       } finally {
/* 116 */         UnixNativeDispatcher.close(i);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class Posix extends UnixFileAttributeViews.Basic
/*     */     implements PosixFileAttributeView
/*     */   {
/*     */     private static final String PERMISSIONS_NAME = "permissions";
/*     */     private static final String OWNER_NAME = "owner";
/*     */     private static final String GROUP_NAME = "group";
/* 127 */     static final Set<String> posixAttributeNames = Util.newSet(basicAttributeNames, new String[] { "permissions", "owner", "group" });
/*     */ 
/*     */     Posix(UnixPath paramUnixPath, boolean paramBoolean)
/*     */     {
/* 131 */       super(paramBoolean);
/*     */     }
/*     */ 
/*     */     final void checkReadExtended() {
/* 135 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 136 */       if (localSecurityManager != null) {
/* 137 */         this.file.checkRead();
/* 138 */         localSecurityManager.checkPermission(new RuntimePermission("accessUserInformation"));
/*     */       }
/*     */     }
/*     */ 
/*     */     final void checkWriteExtended() {
/* 143 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 144 */       if (localSecurityManager != null) {
/* 145 */         this.file.checkWrite();
/* 146 */         localSecurityManager.checkPermission(new RuntimePermission("accessUserInformation"));
/*     */       }
/*     */     }
/*     */ 
/*     */     public String name()
/*     */     {
/* 152 */       return "posix";
/*     */     }
/*     */ 
/*     */     public void setAttribute(String paramString, Object paramObject)
/*     */       throws IOException
/*     */     {
/* 160 */       if (paramString.equals("permissions")) {
/* 161 */         setPermissions((Set)paramObject);
/* 162 */         return;
/*     */       }
/* 164 */       if (paramString.equals("owner")) {
/* 165 */         setOwner((UserPrincipal)paramObject);
/* 166 */         return;
/*     */       }
/* 168 */       if (paramString.equals("group")) {
/* 169 */         setGroup((GroupPrincipal)paramObject);
/* 170 */         return;
/*     */       }
/* 172 */       super.setAttribute(paramString, paramObject);
/*     */     }
/*     */ 
/*     */     final void addRequestedPosixAttributes(PosixFileAttributes paramPosixFileAttributes, AbstractBasicFileAttributeView.AttributesBuilder paramAttributesBuilder)
/*     */     {
/* 182 */       addRequestedBasicAttributes(paramPosixFileAttributes, paramAttributesBuilder);
/* 183 */       if (paramAttributesBuilder.match("permissions"))
/* 184 */         paramAttributesBuilder.add("permissions", paramPosixFileAttributes.permissions());
/* 185 */       if (paramAttributesBuilder.match("owner"))
/* 186 */         paramAttributesBuilder.add("owner", paramPosixFileAttributes.owner());
/* 187 */       if (paramAttributesBuilder.match("group"))
/* 188 */         paramAttributesBuilder.add("group", paramPosixFileAttributes.group());
/*     */     }
/*     */ 
/*     */     public Map<String, Object> readAttributes(String[] paramArrayOfString)
/*     */       throws IOException
/*     */     {
/* 195 */       AbstractBasicFileAttributeView.AttributesBuilder localAttributesBuilder = AbstractBasicFileAttributeView.AttributesBuilder.create(posixAttributeNames, paramArrayOfString);
/*     */ 
/* 197 */       UnixFileAttributes localUnixFileAttributes = readAttributes();
/* 198 */       addRequestedPosixAttributes(localUnixFileAttributes, localAttributesBuilder);
/* 199 */       return localAttributesBuilder.unmodifiableMap();
/*     */     }
/*     */ 
/*     */     public UnixFileAttributes readAttributes() throws IOException
/*     */     {
/* 204 */       checkReadExtended();
/*     */       try {
/* 206 */         return UnixFileAttributes.get(this.file, this.followLinks);
/*     */       } catch (UnixException localUnixException) {
/* 208 */         localUnixException.rethrowAsIOException(this.file);
/* 209 */       }return null;
/*     */     }
/*     */ 
/*     */     final void setMode(int paramInt)
/*     */       throws IOException
/*     */     {
/* 215 */       checkWriteExtended();
/*     */       try {
/* 217 */         if (this.followLinks) {
/* 218 */           UnixNativeDispatcher.chmod(this.file, paramInt);
/*     */         } else {
/* 220 */           int i = this.file.openForAttributeAccess(false);
/*     */           try {
/* 222 */             UnixNativeDispatcher.fchmod(i, paramInt);
/*     */           } finally {
/* 224 */             UnixNativeDispatcher.close(i);
/*     */           }
/*     */         }
/*     */       } catch (UnixException localUnixException) {
/* 228 */         localUnixException.rethrowAsIOException(this.file);
/*     */       }
/*     */     }
/*     */ 
/*     */     final void setOwners(int paramInt1, int paramInt2) throws IOException
/*     */     {
/* 234 */       checkWriteExtended();
/*     */       try {
/* 236 */         if (this.followLinks)
/* 237 */           UnixNativeDispatcher.chown(this.file, paramInt1, paramInt2);
/*     */         else
/* 239 */           UnixNativeDispatcher.lchown(this.file, paramInt1, paramInt2);
/*     */       }
/*     */       catch (UnixException localUnixException) {
/* 242 */         localUnixException.rethrowAsIOException(this.file);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setPermissions(Set<PosixFilePermission> paramSet)
/*     */       throws IOException
/*     */     {
/* 250 */       setMode(UnixFileModeAttribute.toUnixMode(paramSet));
/*     */     }
/*     */ 
/*     */     public void setOwner(UserPrincipal paramUserPrincipal)
/*     */       throws IOException
/*     */     {
/* 257 */       if (paramUserPrincipal == null)
/* 258 */         throw new NullPointerException("'owner' is null");
/* 259 */       if (!(paramUserPrincipal instanceof UnixUserPrincipals.User))
/* 260 */         throw new ProviderMismatchException();
/* 261 */       if ((paramUserPrincipal instanceof UnixUserPrincipals.Group))
/* 262 */         throw new IOException("'owner' parameter can't be a group");
/* 263 */       int i = ((UnixUserPrincipals.User)paramUserPrincipal).uid();
/* 264 */       setOwners(i, -1);
/*     */     }
/*     */ 
/*     */     public UserPrincipal getOwner() throws IOException
/*     */     {
/* 269 */       return readAttributes().owner();
/*     */     }
/*     */ 
/*     */     public void setGroup(GroupPrincipal paramGroupPrincipal)
/*     */       throws IOException
/*     */     {
/* 276 */       if (paramGroupPrincipal == null)
/* 277 */         throw new NullPointerException("'owner' is null");
/* 278 */       if (!(paramGroupPrincipal instanceof UnixUserPrincipals.Group))
/* 279 */         throw new ProviderMismatchException();
/* 280 */       int i = ((UnixUserPrincipals.Group)paramGroupPrincipal).gid();
/* 281 */       setOwners(-1, i);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class Unix extends UnixFileAttributeViews.Posix
/*     */   {
/*     */     private static final String MODE_NAME = "mode";
/*     */     private static final String INO_NAME = "ino";
/*     */     private static final String DEV_NAME = "dev";
/*     */     private static final String RDEV_NAME = "rdev";
/*     */     private static final String NLINK_NAME = "nlink";
/*     */     private static final String UID_NAME = "uid";
/*     */     private static final String GID_NAME = "gid";
/*     */     private static final String CTIME_NAME = "ctime";
/* 296 */     static final Set<String> unixAttributeNames = Util.newSet(posixAttributeNames, new String[] { "mode", "ino", "dev", "rdev", "nlink", "uid", "gid", "ctime" });
/*     */ 
/*     */     Unix(UnixPath paramUnixPath, boolean paramBoolean)
/*     */     {
/* 302 */       super(paramBoolean);
/*     */     }
/*     */ 
/*     */     public String name()
/*     */     {
/* 307 */       return "unix";
/*     */     }
/*     */ 
/*     */     public void setAttribute(String paramString, Object paramObject)
/*     */       throws IOException
/*     */     {
/* 314 */       if (paramString.equals("mode")) {
/* 315 */         setMode(((Integer)paramObject).intValue());
/* 316 */         return;
/*     */       }
/* 318 */       if (paramString.equals("uid")) {
/* 319 */         setOwners(((Integer)paramObject).intValue(), -1);
/* 320 */         return;
/*     */       }
/* 322 */       if (paramString.equals("gid")) {
/* 323 */         setOwners(-1, ((Integer)paramObject).intValue());
/* 324 */         return;
/*     */       }
/* 326 */       super.setAttribute(paramString, paramObject);
/*     */     }
/*     */ 
/*     */     public Map<String, Object> readAttributes(String[] paramArrayOfString)
/*     */       throws IOException
/*     */     {
/* 333 */       AbstractBasicFileAttributeView.AttributesBuilder localAttributesBuilder = AbstractBasicFileAttributeView.AttributesBuilder.create(unixAttributeNames, paramArrayOfString);
/*     */ 
/* 335 */       UnixFileAttributes localUnixFileAttributes = readAttributes();
/* 336 */       addRequestedPosixAttributes(localUnixFileAttributes, localAttributesBuilder);
/* 337 */       if (localAttributesBuilder.match("mode"))
/* 338 */         localAttributesBuilder.add("mode", Integer.valueOf(localUnixFileAttributes.mode()));
/* 339 */       if (localAttributesBuilder.match("ino"))
/* 340 */         localAttributesBuilder.add("ino", Long.valueOf(localUnixFileAttributes.ino()));
/* 341 */       if (localAttributesBuilder.match("dev"))
/* 342 */         localAttributesBuilder.add("dev", Long.valueOf(localUnixFileAttributes.dev()));
/* 343 */       if (localAttributesBuilder.match("rdev"))
/* 344 */         localAttributesBuilder.add("rdev", Long.valueOf(localUnixFileAttributes.rdev()));
/* 345 */       if (localAttributesBuilder.match("nlink"))
/* 346 */         localAttributesBuilder.add("nlink", Integer.valueOf(localUnixFileAttributes.nlink()));
/* 347 */       if (localAttributesBuilder.match("uid"))
/* 348 */         localAttributesBuilder.add("uid", Integer.valueOf(localUnixFileAttributes.uid()));
/* 349 */       if (localAttributesBuilder.match("gid"))
/* 350 */         localAttributesBuilder.add("gid", Integer.valueOf(localUnixFileAttributes.gid()));
/* 351 */       if (localAttributesBuilder.match("ctime"))
/* 352 */         localAttributesBuilder.add("ctime", localUnixFileAttributes.ctime());
/* 353 */       return localAttributesBuilder.unmodifiableMap();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.UnixFileAttributeViews
 * JD-Core Version:    0.6.2
 */