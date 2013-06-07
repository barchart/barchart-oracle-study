/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Enumeration;
/*     */ import sun.security.util.BitArray;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ 
/*     */ public class ReasonFlags
/*     */ {
/*     */   public static final String UNUSED = "unused";
/*     */   public static final String KEY_COMPROMISE = "key_compromise";
/*     */   public static final String CA_COMPROMISE = "ca_compromise";
/*     */   public static final String AFFILIATION_CHANGED = "affiliation_changed";
/*     */   public static final String SUPERSEDED = "superseded";
/*     */   public static final String CESSATION_OF_OPERATION = "cessation_of_operation";
/*     */   public static final String CERTIFICATE_HOLD = "certificate_hold";
/*     */   public static final String PRIVILEGE_WITHDRAWN = "privilege_withdrawn";
/*     */   public static final String AA_COMPROMISE = "aa_compromise";
/*  72 */   private static final String[] NAMES = { "unused", "key_compromise", "ca_compromise", "affiliation_changed", "superseded", "cessation_of_operation", "certificate_hold", "privilege_withdrawn", "aa_compromise" };
/*     */   private boolean[] bitString;
/*     */ 
/*     */   private static int name2Index(String paramString)
/*     */     throws IOException
/*     */   {
/*  85 */     for (int i = 0; i < NAMES.length; i++) {
/*  86 */       if (NAMES[i].equalsIgnoreCase(paramString)) {
/*  87 */         return i;
/*     */       }
/*     */     }
/*  90 */     throw new IOException("Name not recognized by ReasonFlags");
/*     */   }
/*     */ 
/*     */   private boolean isSet(int paramInt)
/*     */   {
/* 102 */     return this.bitString[paramInt];
/*     */   }
/*     */ 
/*     */   private void set(int paramInt, boolean paramBoolean)
/*     */   {
/* 110 */     if (paramInt >= this.bitString.length) {
/* 111 */       boolean[] arrayOfBoolean = new boolean[paramInt + 1];
/* 112 */       System.arraycopy(this.bitString, 0, arrayOfBoolean, 0, this.bitString.length);
/* 113 */       this.bitString = arrayOfBoolean;
/*     */     }
/* 115 */     this.bitString[paramInt] = paramBoolean;
/*     */   }
/*     */ 
/*     */   public ReasonFlags(byte[] paramArrayOfByte)
/*     */   {
/* 124 */     this.bitString = new BitArray(paramArrayOfByte.length * 8, paramArrayOfByte).toBooleanArray();
/*     */   }
/*     */ 
/*     */   public ReasonFlags(boolean[] paramArrayOfBoolean)
/*     */   {
/* 133 */     this.bitString = paramArrayOfBoolean;
/*     */   }
/*     */ 
/*     */   public ReasonFlags(BitArray paramBitArray)
/*     */   {
/* 142 */     this.bitString = paramBitArray.toBooleanArray();
/*     */   }
/*     */ 
/*     */   public ReasonFlags(DerInputStream paramDerInputStream)
/*     */     throws IOException
/*     */   {
/* 152 */     DerValue localDerValue = paramDerInputStream.getDerValue();
/* 153 */     this.bitString = localDerValue.getUnalignedBitString(true).toBooleanArray();
/*     */   }
/*     */ 
/*     */   public ReasonFlags(DerValue paramDerValue)
/*     */     throws IOException
/*     */   {
/* 163 */     this.bitString = paramDerValue.getUnalignedBitString(true).toBooleanArray();
/*     */   }
/*     */ 
/*     */   public boolean[] getFlags()
/*     */   {
/* 170 */     return this.bitString;
/*     */   }
/*     */ 
/*     */   public void set(String paramString, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 177 */     if (!(paramObject instanceof Boolean)) {
/* 178 */       throw new IOException("Attribute must be of type Boolean.");
/*     */     }
/* 180 */     boolean bool = ((Boolean)paramObject).booleanValue();
/* 181 */     set(name2Index(paramString), bool);
/*     */   }
/*     */ 
/*     */   public Object get(String paramString)
/*     */     throws IOException
/*     */   {
/* 188 */     return Boolean.valueOf(isSet(name2Index(paramString)));
/*     */   }
/*     */ 
/*     */   public void delete(String paramString)
/*     */     throws IOException
/*     */   {
/* 195 */     set(paramString, Boolean.FALSE);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 202 */     String str = "Reason Flags [\n";
/*     */     try
/*     */     {
/* 205 */       if (isSet(0)) str = str + "  Unused\n";
/* 206 */       if (isSet(1)) str = str + "  Key Compromise\n";
/* 207 */       if (isSet(2)) str = str + "  CA Compromise\n";
/* 208 */       if (isSet(3)) str = str + "  Affiliation_Changed\n";
/* 209 */       if (isSet(4)) str = str + "  Superseded\n";
/* 210 */       if (isSet(5)) str = str + "  Cessation Of Operation\n";
/* 211 */       if (isSet(6)) str = str + "  Certificate Hold\n";
/* 212 */       if (isSet(7)) str = str + "  Privilege Withdrawn\n";
/* 213 */       if (isSet(8)) str = str + "  AA Compromise\n"; 
/*     */     }
/*     */     catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {  }
/*     */ 
/* 216 */     str = str + "]\n";
/*     */ 
/* 218 */     return str;
/*     */   }
/*     */ 
/*     */   public void encode(DerOutputStream paramDerOutputStream)
/*     */     throws IOException
/*     */   {
/* 228 */     paramDerOutputStream.putTruncatedUnalignedBitString(new BitArray(this.bitString));
/*     */   }
/*     */ 
/*     */   public Enumeration<String> getElements()
/*     */   {
/* 236 */     AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
/* 237 */     for (int i = 0; i < NAMES.length; i++) {
/* 238 */       localAttributeNameEnumeration.addElement(NAMES[i]);
/*     */     }
/* 240 */     return localAttributeNameEnumeration.elements();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.x509.ReasonFlags
 * JD-Core Version:    0.6.2
 */