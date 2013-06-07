/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.net.URI;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ import java.nio.charset.CodingErrorAction;
/*     */ import java.nio.file.FileSystemException;
/*     */ import java.nio.file.InvalidPathException;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.ProviderMismatchException;
/*     */ import java.nio.file.WatchEvent.Kind;
/*     */ import java.nio.file.WatchEvent.Modifier;
/*     */ import java.nio.file.WatchKey;
/*     */ import java.nio.file.WatchService;
/*     */ import java.util.Arrays;
/*     */ import java.util.Objects;
/*     */ 
/*     */ class UnixPath extends AbstractPath
/*     */ {
/*  46 */   private static ThreadLocal<SoftReference<CharsetEncoder>> encoder = new ThreadLocal();
/*     */   private final UnixFileSystem fs;
/*     */   private final byte[] path;
/*     */   private volatile String stringValue;
/*     */   private int hash;
/*     */   private volatile int[] offsets;
/*     */ 
/*     */   UnixPath(UnixFileSystem paramUnixFileSystem, byte[] paramArrayOfByte)
/*     */   {
/*  65 */     this.fs = paramUnixFileSystem;
/*  66 */     this.path = paramArrayOfByte;
/*     */   }
/*     */ 
/*     */   UnixPath(UnixFileSystem paramUnixFileSystem, String paramString)
/*     */   {
/*  71 */     this(paramUnixFileSystem, encode(normalizeAndCheck(paramString)));
/*     */   }
/*     */ 
/*     */   static String normalizeAndCheck(String paramString)
/*     */   {
/*  77 */     int i = paramString.length();
/*  78 */     int j = 0;
/*  79 */     for (int k = 0; k < i; k++) {
/*  80 */       char c = paramString.charAt(k);
/*  81 */       if ((c == '/') && (j == 47))
/*  82 */         return normalize(paramString, i, k - 1);
/*  83 */       checkNotNul(paramString, c);
/*  84 */       j = c;
/*     */     }
/*  86 */     if (j == 47)
/*  87 */       return normalize(paramString, i, i - 1);
/*  88 */     return paramString;
/*     */   }
/*     */ 
/*     */   private static void checkNotNul(String paramString, char paramChar) {
/*  92 */     if (paramChar == 0)
/*  93 */       throw new InvalidPathException(paramString, "Nul character not allowed");
/*     */   }
/*     */ 
/*     */   private static String normalize(String paramString, int paramInt1, int paramInt2) {
/*  97 */     if (paramInt1 == 0)
/*  98 */       return paramString;
/*  99 */     int i = paramInt1;
/* 100 */     while ((i > 0) && (paramString.charAt(i - 1) == '/')) i--;
/* 101 */     if (i == 0)
/* 102 */       return "/";
/* 103 */     StringBuilder localStringBuilder = new StringBuilder(paramString.length());
/* 104 */     if (paramInt2 > 0)
/* 105 */       localStringBuilder.append(paramString.substring(0, paramInt2));
/* 106 */     int j = 0;
/* 107 */     for (int k = paramInt2; k < i; k++) {
/* 108 */       char c = paramString.charAt(k);
/* 109 */       if ((c != '/') || (j != 47))
/*     */       {
/* 111 */         checkNotNul(paramString, c);
/* 112 */         localStringBuilder.append(c);
/* 113 */         j = c;
/*     */       }
/*     */     }
/* 115 */     return localStringBuilder.toString();
/*     */   }
/*     */ 
/*     */   private static byte[] encode(String paramString)
/*     */   {
/* 120 */     SoftReference localSoftReference = (SoftReference)encoder.get();
/* 121 */     CharsetEncoder localCharsetEncoder = localSoftReference != null ? (CharsetEncoder)localSoftReference.get() : null;
/* 122 */     if (localCharsetEncoder == null) {
/* 123 */       localCharsetEncoder = Charset.defaultCharset().newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
/*     */ 
/* 126 */       encoder.set(new SoftReference(localCharsetEncoder));
/*     */     }
/*     */ 
/* 129 */     char[] arrayOfChar = paramString.toCharArray();
/*     */ 
/* 132 */     byte[] arrayOfByte = new byte[(int)(arrayOfChar.length * localCharsetEncoder.maxBytesPerChar())];
/*     */ 
/* 135 */     ByteBuffer localByteBuffer = ByteBuffer.wrap(arrayOfByte);
/* 136 */     CharBuffer localCharBuffer = CharBuffer.wrap(arrayOfChar);
/* 137 */     localCharsetEncoder.reset();
/* 138 */     CoderResult localCoderResult = localCharsetEncoder.encode(localCharBuffer, localByteBuffer, true);
/*     */     int i;
/* 140 */     if (!localCoderResult.isUnderflow()) {
/* 141 */       i = 1;
/*     */     } else {
/* 143 */       localCoderResult = localCharsetEncoder.flush(localByteBuffer);
/* 144 */       i = !localCoderResult.isUnderflow() ? 1 : 0;
/*     */     }
/* 146 */     if (i != 0) {
/* 147 */       throw new InvalidPathException(paramString, "Malformed input or input contains unmappable chacraters");
/*     */     }
/*     */ 
/* 152 */     int j = localByteBuffer.position();
/* 153 */     if (j != arrayOfByte.length) {
/* 154 */       arrayOfByte = Arrays.copyOf(arrayOfByte, j);
/*     */     }
/* 156 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   byte[] asByteArray()
/*     */   {
/* 161 */     return this.path;
/*     */   }
/*     */ 
/*     */   byte[] getByteArrayForSysCalls()
/*     */   {
/* 168 */     if (getFileSystem().needToResolveAgainstDefaultDirectory()) {
/* 169 */       return resolve(getFileSystem().defaultDirectory(), this.path);
/*     */     }
/* 171 */     if (!isEmpty()) {
/* 172 */       return this.path;
/*     */     }
/*     */ 
/* 175 */     byte[] arrayOfByte = { 46 };
/* 176 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   String getPathForExecptionMessage()
/*     */   {
/* 183 */     return toString();
/*     */   }
/*     */ 
/*     */   String getPathForPermissionCheck()
/*     */   {
/* 188 */     if (getFileSystem().needToResolveAgainstDefaultDirectory()) {
/* 189 */       return new String(getByteArrayForSysCalls());
/*     */     }
/* 191 */     return toString();
/*     */   }
/*     */ 
/*     */   static UnixPath toUnixPath(Path paramPath)
/*     */   {
/* 197 */     if (paramPath == null)
/* 198 */       throw new NullPointerException();
/* 199 */     if (!(paramPath instanceof UnixPath))
/* 200 */       throw new ProviderMismatchException();
/* 201 */     return (UnixPath)paramPath;
/*     */   }
/*     */ 
/*     */   private void initOffsets()
/*     */   {
/* 206 */     if (this.offsets == null)
/*     */     {
/* 210 */       int i = 0;
/* 211 */       int j = 0;
/* 212 */       if (isEmpty())
/*     */       {
/* 214 */         i = 1;
/*     */       }
/* 216 */       else while (j < this.path.length) {
/* 217 */           int k = this.path[(j++)];
/* 218 */           if (k != 47) {
/* 219 */             i++;
/* 220 */             while ((j < this.path.length) && (this.path[j] != 47)) {
/* 221 */               j++;
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/*     */ 
/* 227 */       int[] arrayOfInt = new int[i];
/* 228 */       i = 0;
/* 229 */       j = 0;
/* 230 */       while (j < this.path.length) {
/* 231 */         int m = this.path[j];
/* 232 */         if (m == 47) {
/* 233 */           j++;
/*     */         } else {
/* 235 */           arrayOfInt[(i++)] = (j++);
/* 236 */           while ((j < this.path.length) && (this.path[j] != 47))
/* 237 */             j++;
/*     */         }
/*     */       }
/* 240 */       synchronized (this) {
/* 241 */         if (this.offsets == null)
/* 242 */           this.offsets = arrayOfInt;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isEmpty()
/*     */   {
/* 249 */     return this.path.length == 0;
/*     */   }
/*     */ 
/*     */   private UnixPath emptyPath()
/*     */   {
/* 254 */     return new UnixPath(getFileSystem(), new byte[0]);
/*     */   }
/*     */ 
/*     */   public UnixFileSystem getFileSystem()
/*     */   {
/* 259 */     return this.fs;
/*     */   }
/*     */ 
/*     */   public UnixPath getRoot()
/*     */   {
/* 264 */     if ((this.path.length > 0) && (this.path[0] == 47)) {
/* 265 */       return getFileSystem().rootDirectory();
/*     */     }
/* 267 */     return null;
/*     */   }
/*     */ 
/*     */   public UnixPath getFileName()
/*     */   {
/* 273 */     initOffsets();
/*     */ 
/* 275 */     int i = this.offsets.length;
/*     */ 
/* 278 */     if (i == 0) {
/* 279 */       return null;
/*     */     }
/*     */ 
/* 282 */     if ((i == 1) && (this.path.length > 0) && (this.path[0] != 47)) {
/* 283 */       return this;
/*     */     }
/* 285 */     int j = this.offsets[(i - 1)];
/* 286 */     int k = this.path.length - j;
/* 287 */     byte[] arrayOfByte = new byte[k];
/* 288 */     System.arraycopy(this.path, j, arrayOfByte, 0, k);
/* 289 */     return new UnixPath(getFileSystem(), arrayOfByte);
/*     */   }
/*     */ 
/*     */   public UnixPath getParent()
/*     */   {
/* 294 */     initOffsets();
/*     */ 
/* 296 */     int i = this.offsets.length;
/* 297 */     if (i == 0)
/*     */     {
/* 299 */       return null;
/*     */     }
/* 301 */     int j = this.offsets[(i - 1)] - 1;
/* 302 */     if (j <= 0)
/*     */     {
/* 304 */       return getRoot();
/*     */     }
/* 306 */     byte[] arrayOfByte = new byte[j];
/* 307 */     System.arraycopy(this.path, 0, arrayOfByte, 0, j);
/* 308 */     return new UnixPath(getFileSystem(), arrayOfByte);
/*     */   }
/*     */ 
/*     */   public int getNameCount()
/*     */   {
/* 313 */     initOffsets();
/* 314 */     return this.offsets.length;
/*     */   }
/*     */ 
/*     */   public UnixPath getName(int paramInt)
/*     */   {
/* 319 */     initOffsets();
/* 320 */     if (paramInt < 0)
/* 321 */       throw new IllegalArgumentException();
/* 322 */     if (paramInt >= this.offsets.length) {
/* 323 */       throw new IllegalArgumentException();
/*     */     }
/* 325 */     int i = this.offsets[paramInt];
/*     */     int j;
/* 327 */     if (paramInt == this.offsets.length - 1)
/* 328 */       j = this.path.length - i;
/*     */     else {
/* 330 */       j = this.offsets[(paramInt + 1)] - i - 1;
/*     */     }
/*     */ 
/* 334 */     byte[] arrayOfByte = new byte[j];
/* 335 */     System.arraycopy(this.path, i, arrayOfByte, 0, j);
/* 336 */     return new UnixPath(getFileSystem(), arrayOfByte);
/*     */   }
/*     */ 
/*     */   public UnixPath subpath(int paramInt1, int paramInt2)
/*     */   {
/* 341 */     initOffsets();
/*     */ 
/* 343 */     if (paramInt1 < 0)
/* 344 */       throw new IllegalArgumentException();
/* 345 */     if (paramInt1 >= this.offsets.length)
/* 346 */       throw new IllegalArgumentException();
/* 347 */     if (paramInt2 > this.offsets.length)
/* 348 */       throw new IllegalArgumentException();
/* 349 */     if (paramInt1 >= paramInt2) {
/* 350 */       throw new IllegalArgumentException();
/*     */     }
/*     */ 
/* 354 */     int i = this.offsets[paramInt1];
/*     */     int j;
/* 356 */     if (paramInt2 == this.offsets.length)
/* 357 */       j = this.path.length - i;
/*     */     else {
/* 359 */       j = this.offsets[paramInt2] - i - 1;
/*     */     }
/*     */ 
/* 363 */     byte[] arrayOfByte = new byte[j];
/* 364 */     System.arraycopy(this.path, i, arrayOfByte, 0, j);
/* 365 */     return new UnixPath(getFileSystem(), arrayOfByte);
/*     */   }
/*     */ 
/*     */   public boolean isAbsolute()
/*     */   {
/* 370 */     return (this.path.length > 0) && (this.path[0] == 47);
/*     */   }
/*     */ 
/*     */   private static byte[] resolve(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
/*     */   {
/* 375 */     int i = paramArrayOfByte1.length;
/* 376 */     int j = paramArrayOfByte2.length;
/* 377 */     if (j == 0)
/* 378 */       return paramArrayOfByte1;
/* 379 */     if ((i == 0) || (paramArrayOfByte2[0] == 47))
/* 380 */       return paramArrayOfByte2;
/*     */     byte[] arrayOfByte;
/* 382 */     if ((i == 1) && (paramArrayOfByte1[0] == 47)) {
/* 383 */       arrayOfByte = new byte[j + 1];
/* 384 */       arrayOfByte[0] = 47;
/* 385 */       System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, 1, j);
/*     */     } else {
/* 387 */       arrayOfByte = new byte[i + 1 + j];
/* 388 */       System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, i);
/* 389 */       arrayOfByte[paramArrayOfByte1.length] = 47;
/* 390 */       System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, i + 1, j);
/*     */     }
/* 392 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public UnixPath resolve(Path paramPath)
/*     */   {
/* 397 */     byte[] arrayOfByte1 = toUnixPath(paramPath).path;
/* 398 */     if ((arrayOfByte1.length > 0) && (arrayOfByte1[0] == 47))
/* 399 */       return (UnixPath)paramPath;
/* 400 */     byte[] arrayOfByte2 = resolve(this.path, arrayOfByte1);
/* 401 */     return new UnixPath(getFileSystem(), arrayOfByte2);
/*     */   }
/*     */ 
/*     */   UnixPath resolve(byte[] paramArrayOfByte) {
/* 405 */     return resolve(new UnixPath(getFileSystem(), paramArrayOfByte));
/*     */   }
/*     */ 
/*     */   public UnixPath relativize(Path paramPath)
/*     */   {
/* 410 */     UnixPath localUnixPath = toUnixPath(paramPath);
/* 411 */     if (localUnixPath.equals(this)) {
/* 412 */       return emptyPath();
/*     */     }
/*     */ 
/* 415 */     if (isAbsolute() != localUnixPath.isAbsolute()) {
/* 416 */       throw new IllegalArgumentException("'other' is different type of Path");
/*     */     }
/*     */ 
/* 419 */     if (isEmpty()) {
/* 420 */       return localUnixPath;
/*     */     }
/* 422 */     int i = getNameCount();
/* 423 */     int j = localUnixPath.getNameCount();
/*     */ 
/* 426 */     int k = i > j ? j : i;
/* 427 */     int m = 0;
/* 428 */     while ((m < k) && 
/* 429 */       (getName(m).equals(localUnixPath.getName(m))))
/*     */     {
/* 431 */       m++;
/*     */     }
/*     */ 
/* 434 */     int n = i - m;
/* 435 */     if (m < j)
/*     */     {
/* 437 */       localObject = localUnixPath.subpath(m, j);
/* 438 */       if (n == 0) {
/* 439 */         return localObject;
/*     */       }
/*     */ 
/* 442 */       bool = localUnixPath.isEmpty();
/*     */ 
/* 447 */       int i1 = n * 3 + ((UnixPath)localObject).path.length;
/* 448 */       if (bool) {
/* 449 */         assert (((UnixPath)localObject).isEmpty());
/* 450 */         i1--;
/*     */       }
/* 452 */       byte[] arrayOfByte = new byte[i1];
/* 453 */       int i2 = 0;
/* 454 */       while (n > 0) {
/* 455 */         arrayOfByte[(i2++)] = 46;
/* 456 */         arrayOfByte[(i2++)] = 46;
/* 457 */         if (bool) {
/* 458 */           if (n > 1) arrayOfByte[(i2++)] = 47; 
/*     */         }
/*     */         else {
/* 460 */           arrayOfByte[(i2++)] = 47;
/*     */         }
/* 462 */         n--;
/*     */       }
/* 464 */       System.arraycopy(((UnixPath)localObject).path, 0, arrayOfByte, i2, ((UnixPath)localObject).path.length);
/* 465 */       return new UnixPath(getFileSystem(), arrayOfByte);
/*     */     }
/*     */ 
/* 468 */     Object localObject = new byte[n * 3 - 1];
/* 469 */     boolean bool = false;
/* 470 */     while (n > 0) {
/* 471 */       localObject[(bool++)] = 46;
/* 472 */       localObject[(bool++)] = 46;
/*     */ 
/* 474 */       if (n > 1)
/* 475 */         localObject[(bool++)] = 47;
/* 476 */       n--;
/*     */     }
/* 478 */     return new UnixPath(getFileSystem(), (byte[])localObject);
/*     */   }
/*     */ 
/*     */   public Path normalize()
/*     */   {
/* 484 */     int i = getNameCount();
/* 485 */     if (i == 0) {
/* 486 */       return this;
/*     */     }
/* 488 */     boolean[] arrayOfBoolean = new boolean[i];
/* 489 */     int[] arrayOfInt = new int[i];
/* 490 */     int j = i;
/* 491 */     int k = 0;
/* 492 */     boolean bool = isAbsolute();
/*     */ 
/* 498 */     for (int m = 0; m < i; m++) {
/* 499 */       n = this.offsets[m];
/*     */ 
/* 501 */       if (m == this.offsets.length - 1)
/* 502 */         i1 = this.path.length - n;
/*     */       else {
/* 504 */         i1 = this.offsets[(m + 1)] - n - 1;
/*     */       }
/* 506 */       arrayOfInt[m] = i1;
/*     */ 
/* 508 */       if (this.path[n] == 46) {
/* 509 */         if (i1 == 1) {
/* 510 */           arrayOfBoolean[m] = true;
/* 511 */           j--;
/*     */         }
/* 514 */         else if (this.path[(n + 1)] == 46) {
/* 515 */           k = 1;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 521 */     if (k != 0) {
/*     */       do
/*     */       {
/* 524 */         m = j;
/* 525 */         n = -1;
/* 526 */         for (i1 = 0; i1 < i; i1++)
/* 527 */           if (arrayOfBoolean[i1] == 0)
/*     */           {
/* 531 */             if (arrayOfInt[i1] != 2) {
/* 532 */               n = i1;
/*     */             }
/*     */             else
/*     */             {
/* 536 */               i2 = this.offsets[i1];
/* 537 */               if ((this.path[i2] != 46) || (this.path[(i2 + 1)] != 46)) {
/* 538 */                 n = i1;
/*     */               }
/* 543 */               else if (n >= 0)
/*     */               {
/* 546 */                 arrayOfBoolean[n] = true;
/* 547 */                 arrayOfBoolean[i1] = true;
/* 548 */                 j -= 2;
/* 549 */                 n = -1;
/*     */               }
/* 552 */               else if (bool) {
/* 553 */                 int i3 = 0;
/* 554 */                 for (int i4 = 0; i4 < i1; i4++) {
/* 555 */                   if (arrayOfBoolean[i4] == 0) {
/* 556 */                     i3 = 1;
/* 557 */                     break;
/*     */                   }
/*     */                 }
/* 560 */                 if (i3 == 0)
/*     */                 {
/* 562 */                   arrayOfBoolean[i1] = true;
/* 563 */                   j--;
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */       }
/* 568 */       while (m > j);
/*     */     }
/*     */ 
/* 572 */     if (j == i) {
/* 573 */       return this;
/*     */     }
/*     */ 
/* 576 */     if (j == 0) {
/* 577 */       return bool ? getFileSystem().rootDirectory() : emptyPath();
/*     */     }
/*     */ 
/* 581 */     m = j - 1;
/* 582 */     if (bool) {
/* 583 */       m++;
/*     */     }
/* 585 */     for (int n = 0; n < i; n++) {
/* 586 */       if (arrayOfBoolean[n] == 0)
/* 587 */         m += arrayOfInt[n];
/*     */     }
/* 589 */     byte[] arrayOfByte = new byte[m];
/*     */ 
/* 592 */     int i1 = 0;
/* 593 */     if (bool)
/* 594 */       arrayOfByte[(i1++)] = 47;
/* 595 */     for (int i2 = 0; i2 < i; i2++) {
/* 596 */       if (arrayOfBoolean[i2] == 0) {
/* 597 */         System.arraycopy(this.path, this.offsets[i2], arrayOfByte, i1, arrayOfInt[i2]);
/* 598 */         i1 += arrayOfInt[i2];
/* 599 */         j--; if (j > 0) {
/* 600 */           arrayOfByte[(i1++)] = 47;
/*     */         }
/*     */       }
/*     */     }
/* 604 */     return new UnixPath(getFileSystem(), arrayOfByte);
/*     */   }
/*     */ 
/*     */   public boolean startsWith(Path paramPath)
/*     */   {
/* 609 */     if (!(Objects.requireNonNull(paramPath) instanceof UnixPath))
/* 610 */       return false;
/* 611 */     UnixPath localUnixPath = (UnixPath)paramPath;
/*     */ 
/* 614 */     if (localUnixPath.path.length > this.path.length) {
/* 615 */       return false;
/*     */     }
/* 617 */     int i = getNameCount();
/* 618 */     int j = localUnixPath.getNameCount();
/*     */ 
/* 621 */     if ((j == 0) && (isAbsolute())) {
/* 622 */       return !localUnixPath.isEmpty();
/*     */     }
/*     */ 
/* 626 */     if (j > i) {
/* 627 */       return false;
/*     */     }
/*     */ 
/* 630 */     if ((j == i) && (this.path.length != localUnixPath.path.length))
/*     */     {
/* 632 */       return false;
/*     */     }
/*     */ 
/* 636 */     for (int k = 0; k < j; k++) {
/* 637 */       Integer localInteger1 = Integer.valueOf(this.offsets[k]);
/* 638 */       Integer localInteger2 = Integer.valueOf(localUnixPath.offsets[k]);
/* 639 */       if (!localInteger1.equals(localInteger2)) {
/* 640 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 644 */     k = 0;
/* 645 */     while (k < localUnixPath.path.length) {
/* 646 */       if (this.path[k] != localUnixPath.path[k])
/* 647 */         return false;
/* 648 */       k++;
/*     */     }
/*     */ 
/* 652 */     if ((k < this.path.length) && (this.path[k] != 47)) {
/* 653 */       return false;
/*     */     }
/* 655 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean endsWith(Path paramPath)
/*     */   {
/* 660 */     if (!(Objects.requireNonNull(paramPath) instanceof UnixPath))
/* 661 */       return false;
/* 662 */     UnixPath localUnixPath = (UnixPath)paramPath;
/*     */ 
/* 664 */     int i = this.path.length;
/* 665 */     int j = localUnixPath.path.length;
/*     */ 
/* 668 */     if (j > i) {
/* 669 */       return false;
/*     */     }
/*     */ 
/* 672 */     if ((i > 0) && (j == 0)) {
/* 673 */       return false;
/*     */     }
/*     */ 
/* 676 */     if ((localUnixPath.isAbsolute()) && (!isAbsolute())) {
/* 677 */       return false;
/*     */     }
/* 679 */     int k = getNameCount();
/* 680 */     int m = localUnixPath.getNameCount();
/*     */ 
/* 683 */     if (m > k) {
/* 684 */       return false;
/*     */     }
/*     */ 
/* 687 */     if (m == k) {
/* 688 */       if (k == 0)
/* 689 */         return true;
/* 690 */       n = i;
/* 691 */       if ((isAbsolute()) && (!localUnixPath.isAbsolute()))
/* 692 */         n--;
/* 693 */       if (j != n) {
/* 694 */         return false;
/*     */       }
/*     */     }
/* 697 */     else if (localUnixPath.isAbsolute()) {
/* 698 */       return false;
/*     */     }
/*     */ 
/* 703 */     int n = this.offsets[(k - m)];
/* 704 */     int i1 = localUnixPath.offsets[0];
/* 705 */     if (j - i1 != i - n)
/* 706 */       return false;
/* 707 */     while (i1 < j) {
/* 708 */       if (this.path[(n++)] != localUnixPath.path[(i1++)]) {
/* 709 */         return false;
/*     */       }
/*     */     }
/* 712 */     return true;
/*     */   }
/*     */ 
/*     */   public int compareTo(Path paramPath)
/*     */   {
/* 717 */     int i = this.path.length;
/* 718 */     int j = ((UnixPath)paramPath).path.length;
/*     */ 
/* 720 */     int k = Math.min(i, j);
/* 721 */     byte[] arrayOfByte1 = this.path;
/* 722 */     byte[] arrayOfByte2 = ((UnixPath)paramPath).path;
/*     */ 
/* 724 */     int m = 0;
/* 725 */     while (m < k) {
/* 726 */       int n = arrayOfByte1[m] & 0xFF;
/* 727 */       int i1 = arrayOfByte2[m] & 0xFF;
/* 728 */       if (n != i1) {
/* 729 */         return n - i1;
/*     */       }
/* 731 */       m++;
/*     */     }
/* 733 */     return i - j;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 738 */     if ((paramObject != null) && ((paramObject instanceof UnixPath))) {
/* 739 */       return compareTo((Path)paramObject) == 0;
/*     */     }
/* 741 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 747 */     int i = this.hash;
/* 748 */     if (i == 0) {
/* 749 */       for (int j = 0; j < this.path.length; j++) {
/* 750 */         i = 31 * i + (this.path[j] & 0xFF);
/*     */       }
/* 752 */       this.hash = i;
/*     */     }
/* 754 */     return i;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 760 */     if (this.stringValue == null)
/* 761 */       this.stringValue = new String(this.path);
/* 762 */     return this.stringValue;
/*     */   }
/*     */ 
/*     */   int openForAttributeAccess(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 769 */     int i = 0;
/* 770 */     if (!paramBoolean)
/* 771 */       i |= 131072;
/*     */     try {
/* 773 */       return UnixNativeDispatcher.open(this, i, 0);
/*     */     }
/*     */     catch (UnixException localUnixException) {
/* 776 */       if ((getFileSystem().isSolaris()) && (localUnixException.errno() == 22)) {
/* 777 */         localUnixException.setError(40);
/*     */       }
/* 779 */       if (localUnixException.errno() == 40) {
/* 780 */         throw new FileSystemException(getPathForExecptionMessage(), null, localUnixException.getMessage() + " or unable to access attributes of symbolic link");
/*     */       }
/*     */ 
/* 783 */       localUnixException.rethrowAsIOException(this);
/* 784 */     }return -1;
/*     */   }
/*     */ 
/*     */   void checkRead()
/*     */   {
/* 789 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 790 */     if (localSecurityManager != null)
/* 791 */       localSecurityManager.checkRead(getPathForPermissionCheck());
/*     */   }
/*     */ 
/*     */   void checkWrite() {
/* 795 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 796 */     if (localSecurityManager != null)
/* 797 */       localSecurityManager.checkWrite(getPathForPermissionCheck());
/*     */   }
/*     */ 
/*     */   void checkDelete() {
/* 801 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 802 */     if (localSecurityManager != null)
/* 803 */       localSecurityManager.checkDelete(getPathForPermissionCheck());
/*     */   }
/*     */ 
/*     */   public UnixPath toAbsolutePath()
/*     */   {
/* 808 */     if (isAbsolute()) {
/* 809 */       return this;
/*     */     }
/*     */ 
/* 813 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 814 */     if (localSecurityManager != null) {
/* 815 */       localSecurityManager.checkPropertyAccess("user.dir");
/*     */     }
/* 817 */     return new UnixPath(getFileSystem(), resolve(getFileSystem().defaultDirectory(), this.path));
/*     */   }
/*     */ 
/*     */   public Path toRealPath(LinkOption[] paramArrayOfLinkOption)
/*     */     throws IOException
/*     */   {
/* 823 */     checkRead();
/*     */ 
/* 825 */     UnixPath localUnixPath1 = toAbsolutePath();
/*     */ 
/* 828 */     if (Util.followLinks(paramArrayOfLinkOption)) {
/*     */       try {
/* 830 */         byte[] arrayOfByte = UnixNativeDispatcher.realpath(localUnixPath1);
/* 831 */         return new UnixPath(getFileSystem(), arrayOfByte);
/*     */       } catch (UnixException localUnixException1) {
/* 833 */         localUnixException1.rethrowAsIOException(this);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 839 */     UnixPath localUnixPath2 = this.fs.rootDirectory();
/* 840 */     for (int i = 0; i < localUnixPath1.getNameCount(); i++) {
/* 841 */       UnixPath localUnixPath3 = localUnixPath1.getName(i);
/*     */ 
/* 844 */       if ((localUnixPath3.asByteArray().length != 1) || (localUnixPath3.asByteArray()[0] != 46))
/*     */       {
/* 848 */         if ((localUnixPath3.asByteArray().length == 2) && (localUnixPath3.asByteArray()[0] == 46) && (localUnixPath3.asByteArray()[1] == 46))
/*     */         {
/* 851 */           UnixFileAttributes localUnixFileAttributes = null;
/*     */           try {
/* 853 */             localUnixFileAttributes = UnixFileAttributes.get(localUnixPath2, false);
/*     */           } catch (UnixException localUnixException3) {
/* 855 */             localUnixException3.rethrowAsIOException(localUnixPath2);
/*     */           }
/* 857 */           if (!localUnixFileAttributes.isSymbolicLink()) {
/* 858 */             localUnixPath2 = localUnixPath2.getParent();
/* 859 */             if (localUnixPath2 != null) continue;
/* 860 */             localUnixPath2 = this.fs.rootDirectory(); continue;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 865 */         localUnixPath2 = localUnixPath2.resolve(localUnixPath3);
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 870 */       UnixFileAttributes.get(localUnixPath2, false);
/*     */     } catch (UnixException localUnixException2) {
/* 872 */       localUnixException2.rethrowAsIOException(localUnixPath2);
/*     */     }
/* 874 */     return localUnixPath2;
/*     */   }
/*     */ 
/*     */   public URI toUri()
/*     */   {
/* 879 */     return UnixUriUtils.toUri(this);
/*     */   }
/*     */ 
/*     */   public WatchKey register(WatchService paramWatchService, WatchEvent.Kind<?>[] paramArrayOfKind, WatchEvent.Modifier[] paramArrayOfModifier)
/*     */     throws IOException
/*     */   {
/* 888 */     if (paramWatchService == null)
/* 889 */       throw new NullPointerException();
/* 890 */     if (!(paramWatchService instanceof AbstractWatchService))
/* 891 */       throw new ProviderMismatchException();
/* 892 */     checkRead();
/* 893 */     return ((AbstractWatchService)paramWatchService).register(this, paramArrayOfKind, paramArrayOfModifier);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.UnixPath
 * JD-Core Version:    0.6.2
 */