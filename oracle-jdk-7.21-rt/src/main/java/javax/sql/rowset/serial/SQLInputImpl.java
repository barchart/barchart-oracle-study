/*     */ package javax.sql.rowset.serial;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.math.BigDecimal;
/*     */ import java.net.URL;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Date;
/*     */ import java.sql.NClob;
/*     */ import java.sql.Ref;
/*     */ import java.sql.RowId;
/*     */ import java.sql.SQLData;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLInput;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Struct;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class SQLInputImpl
/*     */   implements SQLInput
/*     */ {
/*     */   private boolean lastValueWasNull;
/*     */   private int idx;
/*     */   private Object[] attrib;
/*     */   private Map map;
/*     */ 
/*     */   public SQLInputImpl(Object[] paramArrayOfObject, Map<String, Class<?>> paramMap)
/*     */     throws SQLException
/*     */   {
/* 121 */     if ((paramArrayOfObject == null) || (paramMap == null)) {
/* 122 */       throw new SQLException("Cannot instantiate a SQLInputImpl object with null parameters");
/*     */     }
/*     */ 
/* 126 */     this.attrib = paramArrayOfObject;
/*     */ 
/* 128 */     this.idx = -1;
/*     */ 
/* 130 */     this.map = paramMap;
/*     */   }
/*     */ 
/*     */   private Object getNextAttribute()
/*     */     throws SQLException
/*     */   {
/* 144 */     if (++this.idx >= this.attrib.length) {
/* 145 */       throw new SQLException("SQLInputImpl exception: Invalid read position");
/*     */     }
/*     */ 
/* 148 */     return this.attrib[this.idx];
/*     */   }
/*     */ 
/*     */   public String readString()
/*     */     throws SQLException
/*     */   {
/* 175 */     String str = (String)getNextAttribute();
/*     */ 
/* 177 */     if (str == null) {
/* 178 */       this.lastValueWasNull = true;
/* 179 */       return null;
/*     */     }
/* 181 */     this.lastValueWasNull = false;
/* 182 */     return str;
/*     */   }
/*     */ 
/*     */   public boolean readBoolean()
/*     */     throws SQLException
/*     */   {
/* 202 */     Boolean localBoolean = (Boolean)getNextAttribute();
/*     */ 
/* 204 */     if (localBoolean == null) {
/* 205 */       this.lastValueWasNull = true;
/* 206 */       return false;
/*     */     }
/* 208 */     this.lastValueWasNull = false;
/* 209 */     return localBoolean.booleanValue();
/*     */   }
/*     */ 
/*     */   public byte readByte()
/*     */     throws SQLException
/*     */   {
/* 228 */     Byte localByte = (Byte)getNextAttribute();
/*     */ 
/* 230 */     if (localByte == null) {
/* 231 */       this.lastValueWasNull = true;
/* 232 */       return 0;
/*     */     }
/* 234 */     this.lastValueWasNull = false;
/* 235 */     return localByte.byteValue();
/*     */   }
/*     */ 
/*     */   public short readShort()
/*     */     throws SQLException
/*     */   {
/* 253 */     Short localShort = (Short)getNextAttribute();
/*     */ 
/* 255 */     if (localShort == null) {
/* 256 */       this.lastValueWasNull = true;
/* 257 */       return 0;
/*     */     }
/* 259 */     this.lastValueWasNull = false;
/* 260 */     return localShort.shortValue();
/*     */   }
/*     */ 
/*     */   public int readInt()
/*     */     throws SQLException
/*     */   {
/* 278 */     Integer localInteger = (Integer)getNextAttribute();
/*     */ 
/* 280 */     if (localInteger == null) {
/* 281 */       this.lastValueWasNull = true;
/* 282 */       return 0;
/*     */     }
/* 284 */     this.lastValueWasNull = false;
/* 285 */     return localInteger.intValue();
/*     */   }
/*     */ 
/*     */   public long readLong()
/*     */     throws SQLException
/*     */   {
/* 303 */     Long localLong = (Long)getNextAttribute();
/*     */ 
/* 305 */     if (localLong == null) {
/* 306 */       this.lastValueWasNull = true;
/* 307 */       return 0L;
/*     */     }
/* 309 */     this.lastValueWasNull = false;
/* 310 */     return localLong.longValue();
/*     */   }
/*     */ 
/*     */   public float readFloat()
/*     */     throws SQLException
/*     */   {
/* 328 */     Float localFloat = (Float)getNextAttribute();
/*     */ 
/* 330 */     if (localFloat == null) {
/* 331 */       this.lastValueWasNull = true;
/* 332 */       return 0.0F;
/*     */     }
/* 334 */     this.lastValueWasNull = false;
/* 335 */     return localFloat.floatValue();
/*     */   }
/*     */ 
/*     */   public double readDouble()
/*     */     throws SQLException
/*     */   {
/* 353 */     Double localDouble = (Double)getNextAttribute();
/*     */ 
/* 355 */     if (localDouble == null) {
/* 356 */       this.lastValueWasNull = true;
/* 357 */       return 0.0D;
/*     */     }
/* 359 */     this.lastValueWasNull = false;
/* 360 */     return localDouble.doubleValue();
/*     */   }
/*     */ 
/*     */   public BigDecimal readBigDecimal()
/*     */     throws SQLException
/*     */   {
/* 378 */     BigDecimal localBigDecimal = (BigDecimal)getNextAttribute();
/*     */ 
/* 380 */     if (localBigDecimal == null) {
/* 381 */       this.lastValueWasNull = true;
/* 382 */       return null;
/*     */     }
/* 384 */     this.lastValueWasNull = false;
/* 385 */     return localBigDecimal;
/*     */   }
/*     */ 
/*     */   public byte[] readBytes()
/*     */     throws SQLException
/*     */   {
/* 403 */     byte[] arrayOfByte = (byte[])getNextAttribute();
/*     */ 
/* 405 */     if (arrayOfByte == null) {
/* 406 */       this.lastValueWasNull = true;
/* 407 */       return null;
/*     */     }
/* 409 */     this.lastValueWasNull = false;
/* 410 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public Date readDate()
/*     */     throws SQLException
/*     */   {
/* 428 */     Date localDate = (Date)getNextAttribute();
/*     */ 
/* 430 */     if (localDate == null) {
/* 431 */       this.lastValueWasNull = true;
/* 432 */       return null;
/*     */     }
/* 434 */     this.lastValueWasNull = false;
/* 435 */     return localDate;
/*     */   }
/*     */ 
/*     */   public Time readTime()
/*     */     throws SQLException
/*     */   {
/* 454 */     Time localTime = (Time)getNextAttribute();
/*     */ 
/* 456 */     if (localTime == null) {
/* 457 */       this.lastValueWasNull = true;
/* 458 */       return null;
/*     */     }
/* 460 */     this.lastValueWasNull = false;
/* 461 */     return localTime;
/*     */   }
/*     */ 
/*     */   public Timestamp readTimestamp()
/*     */     throws SQLException
/*     */   {
/* 475 */     Timestamp localTimestamp = (Timestamp)getNextAttribute();
/*     */ 
/* 477 */     if (localTimestamp == null) {
/* 478 */       this.lastValueWasNull = true;
/* 479 */       return null;
/*     */     }
/* 481 */     this.lastValueWasNull = false;
/* 482 */     return localTimestamp;
/*     */   }
/*     */ 
/*     */   public Reader readCharacterStream()
/*     */     throws SQLException
/*     */   {
/* 500 */     Reader localReader = (Reader)getNextAttribute();
/*     */ 
/* 502 */     if (localReader == null) {
/* 503 */       this.lastValueWasNull = true;
/* 504 */       return null;
/*     */     }
/* 506 */     this.lastValueWasNull = false;
/* 507 */     return localReader;
/*     */   }
/*     */ 
/*     */   public InputStream readAsciiStream()
/*     */     throws SQLException
/*     */   {
/* 526 */     InputStream localInputStream = (InputStream)getNextAttribute();
/*     */ 
/* 528 */     if (localInputStream == null) {
/* 529 */       this.lastValueWasNull = true;
/* 530 */       return null;
/*     */     }
/* 532 */     this.lastValueWasNull = false;
/* 533 */     return localInputStream;
/*     */   }
/*     */ 
/*     */   public InputStream readBinaryStream()
/*     */     throws SQLException
/*     */   {
/* 552 */     InputStream localInputStream = (InputStream)getNextAttribute();
/*     */ 
/* 554 */     if (localInputStream == null) {
/* 555 */       this.lastValueWasNull = true;
/* 556 */       return null;
/*     */     }
/* 558 */     this.lastValueWasNull = false;
/* 559 */     return localInputStream;
/*     */   }
/*     */ 
/*     */   public Object readObject()
/*     */     throws SQLException
/*     */   {
/* 594 */     Object localObject = getNextAttribute();
/*     */ 
/* 596 */     if (localObject == null) {
/* 597 */       this.lastValueWasNull = true;
/* 598 */       return null;
/*     */     }
/* 600 */     this.lastValueWasNull = false;
/* 601 */     if ((localObject instanceof Struct)) {
/* 602 */       Struct localStruct = (Struct)localObject;
/*     */ 
/* 604 */       Class localClass = (Class)this.map.get(localStruct.getSQLTypeName());
/* 605 */       if (localClass != null)
/*     */       {
/* 607 */         SQLData localSQLData = null;
/*     */         try {
/* 609 */           localSQLData = (SQLData)localClass.newInstance();
/*     */         } catch (InstantiationException localInstantiationException) {
/* 611 */           throw new SQLException("Unable to instantiate: " + localInstantiationException.getMessage());
/*     */         }
/*     */         catch (IllegalAccessException localIllegalAccessException) {
/* 614 */           throw new SQLException("Unable to instantiate: " + localIllegalAccessException.getMessage());
/*     */         }
/*     */ 
/* 618 */         Object[] arrayOfObject = localStruct.getAttributes(this.map);
/*     */ 
/* 620 */         SQLInputImpl localSQLInputImpl = new SQLInputImpl(arrayOfObject, this.map);
/*     */ 
/* 622 */         localSQLData.readSQL(localSQLInputImpl, localStruct.getSQLTypeName());
/* 623 */         return localSQLData;
/*     */       }
/*     */     }
/* 626 */     return localObject;
/*     */   }
/*     */ 
/*     */   public Ref readRef()
/*     */     throws SQLException
/*     */   {
/* 641 */     Ref localRef = (Ref)getNextAttribute();
/*     */ 
/* 643 */     if (localRef == null) {
/* 644 */       this.lastValueWasNull = true;
/* 645 */       return null;
/*     */     }
/* 647 */     this.lastValueWasNull = false;
/* 648 */     return localRef;
/*     */   }
/*     */ 
/*     */   public Blob readBlob()
/*     */     throws SQLException
/*     */   {
/* 670 */     Blob localBlob = (Blob)getNextAttribute();
/*     */ 
/* 672 */     if (localBlob == null) {
/* 673 */       this.lastValueWasNull = true;
/* 674 */       return null;
/*     */     }
/* 676 */     this.lastValueWasNull = false;
/* 677 */     return localBlob;
/*     */   }
/*     */ 
/*     */   public Clob readClob()
/*     */     throws SQLException
/*     */   {
/* 700 */     Clob localClob = (Clob)getNextAttribute();
/* 701 */     if (localClob == null) {
/* 702 */       this.lastValueWasNull = true;
/* 703 */       return null;
/*     */     }
/* 705 */     this.lastValueWasNull = false;
/* 706 */     return localClob;
/*     */   }
/*     */ 
/*     */   public Array readArray()
/*     */     throws SQLException
/*     */   {
/* 729 */     Array localArray = (Array)getNextAttribute();
/*     */ 
/* 731 */     if (localArray == null) {
/* 732 */       this.lastValueWasNull = true;
/* 733 */       return null;
/*     */     }
/* 735 */     this.lastValueWasNull = false;
/* 736 */     return localArray;
/*     */   }
/*     */ 
/*     */   public boolean wasNull()
/*     */     throws SQLException
/*     */   {
/* 751 */     return this.lastValueWasNull;
/*     */   }
/*     */ 
/*     */   public URL readURL()
/*     */     throws SQLException
/*     */   {
/* 772 */     throw new SQLException("Operation not supported");
/*     */   }
/*     */ 
/*     */   public NClob readNClob()
/*     */     throws SQLException
/*     */   {
/* 787 */     throw new UnsupportedOperationException("Operation not supported");
/*     */   }
/*     */ 
/*     */   public String readNString()
/*     */     throws SQLException
/*     */   {
/* 800 */     throw new UnsupportedOperationException("Operation not supported");
/*     */   }
/*     */ 
/*     */   public SQLXML readSQLXML()
/*     */     throws SQLException
/*     */   {
/* 813 */     throw new UnsupportedOperationException("Operation not supported");
/*     */   }
/*     */ 
/*     */   public RowId readRowId()
/*     */     throws SQLException
/*     */   {
/* 826 */     throw new UnsupportedOperationException("Operation not supported");
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.sql.rowset.serial.SQLInputImpl
 * JD-Core Version:    0.6.2
 */