/*     */ package java.lang;
/*     */ 
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ final class ProcessEnvironment
/*     */ {
/*     */   private static final HashMap<Variable, Value> theEnvironment;
/*  78 */   private static final Map<String, String> theUnmodifiableEnvironment = Collections.unmodifiableMap(new StringEnvironment(theEnvironment));
/*     */   static final int MIN_NAME_LENGTH = 0;
/*     */ 
/*     */   static String getenv(String paramString)
/*     */   {
/*  85 */     return (String)theUnmodifiableEnvironment.get(paramString);
/*     */   }
/*     */ 
/*     */   static Map<String, String> getenv()
/*     */   {
/*  90 */     return theUnmodifiableEnvironment;
/*     */   }
/*     */ 
/*     */   static Map<String, String> environment()
/*     */   {
/*  95 */     return new StringEnvironment((Map)theEnvironment.clone());
/*     */   }
/*     */ 
/*     */   static Map<String, String> emptyEnvironment(int paramInt)
/*     */   {
/* 101 */     return new StringEnvironment(new HashMap(paramInt));
/*     */   }
/*     */ 
/*     */   private static native byte[][] environ();
/*     */ 
/*     */   private static void validateVariable(String paramString)
/*     */   {
/* 111 */     if ((paramString.indexOf('=') != -1) || (paramString.indexOf(0) != -1))
/*     */     {
/* 113 */       throw new IllegalArgumentException("Invalid environment variable name: \"" + paramString + "\"");
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void validateValue(String paramString)
/*     */   {
/* 119 */     if (paramString.indexOf(0) != -1)
/* 120 */       throw new IllegalArgumentException("Invalid environment variable value: \"" + paramString + "\"");
/*     */   }
/*     */ 
/*     */   static byte[] toEnvironmentBlock(Map<String, String> paramMap, int[] paramArrayOfInt)
/*     */   {
/* 296 */     return paramMap == null ? null : ((StringEnvironment)paramMap).toEnvironmentBlock(paramArrayOfInt);
/*     */   }
/*     */ 
/*     */   private static int arrayCompare(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
/*     */   {
/* 414 */     int i = paramArrayOfByte1.length < paramArrayOfByte2.length ? paramArrayOfByte1.length : paramArrayOfByte2.length;
/* 415 */     for (int j = 0; j < i; j++)
/* 416 */       if (paramArrayOfByte1[j] != paramArrayOfByte2[j])
/* 417 */         return paramArrayOfByte1[j] - paramArrayOfByte2[j];
/* 418 */     return paramArrayOfByte1.length - paramArrayOfByte2.length;
/*     */   }
/*     */ 
/*     */   private static boolean arrayEquals(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
/*     */   {
/* 423 */     if (paramArrayOfByte1.length != paramArrayOfByte2.length)
/* 424 */       return false;
/* 425 */     for (int i = 0; i < paramArrayOfByte1.length; i++)
/* 426 */       if (paramArrayOfByte1[i] != paramArrayOfByte2[i])
/* 427 */         return false;
/* 428 */     return true;
/*     */   }
/*     */ 
/*     */   private static int arrayHash(byte[] paramArrayOfByte)
/*     */   {
/* 433 */     int i = 0;
/* 434 */     for (int j = 0; j < paramArrayOfByte.length; j++)
/* 435 */       i = 31 * i + paramArrayOfByte[j];
/* 436 */     return i;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  70 */     byte[][] arrayOfByte = environ();
/*  71 */     theEnvironment = new HashMap(arrayOfByte.length / 2 + 3);
/*     */ 
/*  74 */     for (int i = arrayOfByte.length - 1; i > 0; i -= 2)
/*  75 */       theEnvironment.put(Variable.valueOf(arrayOfByte[(i - 1)]), Value.valueOf(arrayOfByte[i]));
/*     */   }
/*     */ 
/*     */   private static abstract class ExternalData
/*     */   {
/*     */     protected final String str;
/*     */     protected final byte[] bytes;
/*     */ 
/*     */     protected ExternalData(String paramString, byte[] paramArrayOfByte)
/*     */     {
/* 131 */       this.str = paramString;
/* 132 */       this.bytes = paramArrayOfByte;
/*     */     }
/*     */ 
/*     */     public byte[] getBytes() {
/* 136 */       return this.bytes;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 140 */       return this.str;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object paramObject) {
/* 144 */       return ((paramObject instanceof ExternalData)) && (ProcessEnvironment.arrayEquals(getBytes(), ((ExternalData)paramObject).getBytes()));
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 149 */       return ProcessEnvironment.arrayHash(getBytes());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class StringEntry
/*     */     implements Map.Entry<String, String>
/*     */   {
/*     */     private final Map.Entry<ProcessEnvironment.Variable, ProcessEnvironment.Value> e;
/*     */ 
/*     */     public StringEntry(Map.Entry<ProcessEnvironment.Variable, ProcessEnvironment.Value> paramEntry)
/*     */     {
/* 305 */       this.e = paramEntry; } 
/* 306 */     public String getKey() { return ((ProcessEnvironment.Variable)this.e.getKey()).toString(); } 
/* 307 */     public String getValue() { return ((ProcessEnvironment.Value)this.e.getValue()).toString(); } 
/*     */     public String setValue(String paramString) {
/* 309 */       return ((ProcessEnvironment.Value)this.e.setValue(ProcessEnvironment.Value.valueOf(paramString))).toString();
/*     */     }
/* 311 */     public String toString() { return getKey() + "=" + getValue(); } 
/*     */     public boolean equals(Object paramObject) {
/* 313 */       return ((paramObject instanceof StringEntry)) && (this.e.equals(((StringEntry)paramObject).e));
/*     */     }
/*     */     public int hashCode() {
/* 316 */       return this.e.hashCode();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class StringEntrySet extends AbstractSet<Map.Entry<String, String>> {
/*     */     private final Set<Map.Entry<ProcessEnvironment.Variable, ProcessEnvironment.Value>> s;
/*     */ 
/* 323 */     public StringEntrySet(Set<Map.Entry<ProcessEnvironment.Variable, ProcessEnvironment.Value>> paramSet) { this.s = paramSet; } 
/* 324 */     public int size() { return this.s.size(); } 
/* 325 */     public boolean isEmpty() { return this.s.isEmpty(); } 
/* 326 */     public void clear() { this.s.clear(); } 
/*     */     public Iterator<Map.Entry<String, String>> iterator() {
/* 328 */       return new Iterator() {
/* 329 */         Iterator<Map.Entry<ProcessEnvironment.Variable, ProcessEnvironment.Value>> i = ProcessEnvironment.StringEntrySet.this.s.iterator();
/*     */ 
/* 330 */         public boolean hasNext() { return this.i.hasNext(); } 
/*     */         public Map.Entry<String, String> next() {
/* 332 */           return new ProcessEnvironment.StringEntry((Map.Entry)this.i.next());
/*     */         }
/* 334 */         public void remove() { this.i.remove(); } } ;
/*     */     }
/*     */ 
/*     */     private static Map.Entry<ProcessEnvironment.Variable, ProcessEnvironment.Value> vvEntry(Object paramObject) {
/* 338 */       if ((paramObject instanceof ProcessEnvironment.StringEntry))
/* 339 */         return ((ProcessEnvironment.StringEntry)paramObject).e;
/* 340 */       return new Map.Entry() {
/*     */         public ProcessEnvironment.Variable getKey() {
/* 342 */           return ProcessEnvironment.Variable.valueOfQueryOnly(((Map.Entry)this.val$o).getKey());
/*     */         }
/*     */         public ProcessEnvironment.Value getValue() {
/* 345 */           return ProcessEnvironment.Value.valueOfQueryOnly(((Map.Entry)this.val$o).getValue());
/*     */         }
/*     */         public ProcessEnvironment.Value setValue(ProcessEnvironment.Value paramAnonymousValue) {
/* 348 */           throw new UnsupportedOperationException();
/*     */         } } ;
/*     */     }
/*     */ 
/* 352 */     public boolean contains(Object paramObject) { return this.s.contains(vvEntry(paramObject)); } 
/* 353 */     public boolean remove(Object paramObject) { return this.s.remove(vvEntry(paramObject)); } 
/*     */     public boolean equals(Object paramObject) {
/* 355 */       return ((paramObject instanceof StringEntrySet)) && (this.s.equals(((StringEntrySet)paramObject).s));
/*     */     }
/*     */     public int hashCode() {
/* 358 */       return this.s.hashCode();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class StringEnvironment extends AbstractMap<String, String>
/*     */   {
/*     */     private Map<ProcessEnvironment.Variable, ProcessEnvironment.Value> m;
/*     */ 
/*     */     private static String toString(ProcessEnvironment.Value paramValue)
/*     */     {
/* 225 */       return paramValue == null ? null : paramValue.toString();
/*     */     }
/* 227 */     public StringEnvironment(Map<ProcessEnvironment.Variable, ProcessEnvironment.Value> paramMap) { this.m = paramMap; } 
/* 228 */     public int size() { return this.m.size(); } 
/* 229 */     public boolean isEmpty() { return this.m.isEmpty(); } 
/* 230 */     public void clear() { this.m.clear(); } 
/*     */     public boolean containsKey(Object paramObject) {
/* 232 */       return this.m.containsKey(ProcessEnvironment.Variable.valueOfQueryOnly(paramObject));
/*     */     }
/*     */     public boolean containsValue(Object paramObject) {
/* 235 */       return this.m.containsValue(ProcessEnvironment.Value.valueOfQueryOnly(paramObject));
/*     */     }
/*     */     public String get(Object paramObject) {
/* 238 */       return toString((ProcessEnvironment.Value)this.m.get(ProcessEnvironment.Variable.valueOfQueryOnly(paramObject)));
/*     */     }
/*     */     public String put(String paramString1, String paramString2) {
/* 241 */       return toString((ProcessEnvironment.Value)this.m.put(ProcessEnvironment.Variable.valueOf(paramString1), ProcessEnvironment.Value.valueOf(paramString2)));
/*     */     }
/*     */ 
/*     */     public String remove(Object paramObject) {
/* 245 */       return toString((ProcessEnvironment.Value)this.m.remove(ProcessEnvironment.Variable.valueOfQueryOnly(paramObject)));
/*     */     }
/*     */     public Set<String> keySet() {
/* 248 */       return new ProcessEnvironment.StringKeySet(this.m.keySet());
/*     */     }
/*     */     public Set<Map.Entry<String, String>> entrySet() {
/* 251 */       return new ProcessEnvironment.StringEntrySet(this.m.entrySet());
/*     */     }
/*     */     public Collection<String> values() {
/* 254 */       return new ProcessEnvironment.StringValues(this.m.values());
/*     */     }
/*     */ 
/*     */     public byte[] toEnvironmentBlock(int[] paramArrayOfInt)
/*     */     {
/* 270 */       int i = this.m.size() * 2;
/* 271 */       for (Object localObject = this.m.entrySet().iterator(); ((Iterator)localObject).hasNext(); ) { Map.Entry localEntry1 = (Map.Entry)((Iterator)localObject).next();
/* 272 */         i += ((ProcessEnvironment.Variable)localEntry1.getKey()).getBytes().length;
/* 273 */         i += ((ProcessEnvironment.Value)localEntry1.getValue()).getBytes().length;
/*     */       }
/*     */ 
/* 276 */       localObject = new byte[i];
/*     */ 
/* 278 */       int j = 0;
/* 279 */       for (Map.Entry localEntry2 : this.m.entrySet()) {
/* 280 */         byte[] arrayOfByte1 = ((ProcessEnvironment.Variable)localEntry2.getKey()).getBytes();
/* 281 */         byte[] arrayOfByte2 = ((ProcessEnvironment.Value)localEntry2.getValue()).getBytes();
/* 282 */         System.arraycopy(arrayOfByte1, 0, localObject, j, arrayOfByte1.length);
/* 283 */         j += arrayOfByte1.length;
/* 284 */         localObject[(j++)] = 61;
/* 285 */         System.arraycopy(arrayOfByte2, 0, localObject, j, arrayOfByte2.length);
/* 286 */         j += arrayOfByte2.length + 1;
/*     */       }
/*     */ 
/* 290 */       paramArrayOfInt[0] = this.m.size();
/* 291 */       return localObject;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class StringKeySet extends AbstractSet<String>
/*     */   {
/*     */     private final Set<ProcessEnvironment.Variable> s;
/*     */ 
/*     */     public StringKeySet(Set<ProcessEnvironment.Variable> paramSet)
/*     */     {
/* 392 */       this.s = paramSet; } 
/* 393 */     public int size() { return this.s.size(); } 
/* 394 */     public boolean isEmpty() { return this.s.isEmpty(); } 
/* 395 */     public void clear() { this.s.clear(); } 
/*     */     public Iterator<String> iterator() {
/* 397 */       return new Iterator() {
/* 398 */         Iterator<ProcessEnvironment.Variable> i = ProcessEnvironment.StringKeySet.this.s.iterator();
/*     */ 
/* 399 */         public boolean hasNext() { return this.i.hasNext(); } 
/* 400 */         public String next() { return ((ProcessEnvironment.Variable)this.i.next()).toString(); } 
/* 401 */         public void remove() { this.i.remove(); } } ;
/*     */     }
/*     */ 
/*     */     public boolean contains(Object paramObject) {
/* 405 */       return this.s.contains(ProcessEnvironment.Variable.valueOfQueryOnly(paramObject));
/*     */     }
/*     */     public boolean remove(Object paramObject) {
/* 408 */       return this.s.remove(ProcessEnvironment.Variable.valueOfQueryOnly(paramObject));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class StringValues extends AbstractCollection<String>
/*     */   {
/*     */     private final Collection<ProcessEnvironment.Value> c;
/*     */ 
/*     */     public StringValues(Collection<ProcessEnvironment.Value> paramCollection)
/*     */     {
/* 365 */       this.c = paramCollection; } 
/* 366 */     public int size() { return this.c.size(); } 
/* 367 */     public boolean isEmpty() { return this.c.isEmpty(); } 
/* 368 */     public void clear() { this.c.clear(); } 
/*     */     public Iterator<String> iterator() {
/* 370 */       return new Iterator() {
/* 371 */         Iterator<ProcessEnvironment.Value> i = ProcessEnvironment.StringValues.this.c.iterator();
/*     */ 
/* 372 */         public boolean hasNext() { return this.i.hasNext(); } 
/* 373 */         public String next() { return ((ProcessEnvironment.Value)this.i.next()).toString(); } 
/* 374 */         public void remove() { this.i.remove(); } } ;
/*     */     }
/*     */ 
/*     */     public boolean contains(Object paramObject) {
/* 378 */       return this.c.contains(ProcessEnvironment.Value.valueOfQueryOnly(paramObject));
/*     */     }
/*     */     public boolean remove(Object paramObject) {
/* 381 */       return this.c.remove(ProcessEnvironment.Value.valueOfQueryOnly(paramObject));
/*     */     }
/*     */     public boolean equals(Object paramObject) {
/* 384 */       return ((paramObject instanceof StringValues)) && (this.c.equals(((StringValues)paramObject).c));
/*     */     }
/*     */     public int hashCode() {
/* 387 */       return this.c.hashCode();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class Value extends ProcessEnvironment.ExternalData
/*     */     implements Comparable<Value>
/*     */   {
/*     */     protected Value(String paramString, byte[] paramArrayOfByte)
/*     */     {
/* 190 */       super(paramArrayOfByte);
/*     */     }
/*     */ 
/*     */     public static Value valueOfQueryOnly(Object paramObject) {
/* 194 */       return valueOfQueryOnly((String)paramObject);
/*     */     }
/*     */ 
/*     */     public static Value valueOfQueryOnly(String paramString) {
/* 198 */       return new Value(paramString, paramString.getBytes());
/*     */     }
/*     */ 
/*     */     public static Value valueOf(String paramString) {
/* 202 */       ProcessEnvironment.validateValue(paramString);
/* 203 */       return valueOfQueryOnly(paramString);
/*     */     }
/*     */ 
/*     */     public static Value valueOf(byte[] paramArrayOfByte) {
/* 207 */       return new Value(new String(paramArrayOfByte), paramArrayOfByte);
/*     */     }
/*     */ 
/*     */     public int compareTo(Value paramValue) {
/* 211 */       return ProcessEnvironment.arrayCompare(getBytes(), paramValue.getBytes());
/*     */     }
/*     */ 
/*     */     public boolean equals(Object paramObject) {
/* 215 */       return ((paramObject instanceof Value)) && (super.equals(paramObject));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class Variable extends ProcessEnvironment.ExternalData
/*     */     implements Comparable<Variable>
/*     */   {
/*     */     protected Variable(String paramString, byte[] paramArrayOfByte)
/*     */     {
/* 157 */       super(paramArrayOfByte);
/*     */     }
/*     */ 
/*     */     public static Variable valueOfQueryOnly(Object paramObject) {
/* 161 */       return valueOfQueryOnly((String)paramObject);
/*     */     }
/*     */ 
/*     */     public static Variable valueOfQueryOnly(String paramString) {
/* 165 */       return new Variable(paramString, paramString.getBytes());
/*     */     }
/*     */ 
/*     */     public static Variable valueOf(String paramString) {
/* 169 */       ProcessEnvironment.validateVariable(paramString);
/* 170 */       return valueOfQueryOnly(paramString);
/*     */     }
/*     */ 
/*     */     public static Variable valueOf(byte[] paramArrayOfByte) {
/* 174 */       return new Variable(new String(paramArrayOfByte), paramArrayOfByte);
/*     */     }
/*     */ 
/*     */     public int compareTo(Variable paramVariable) {
/* 178 */       return ProcessEnvironment.arrayCompare(getBytes(), paramVariable.getBytes());
/*     */     }
/*     */ 
/*     */     public boolean equals(Object paramObject) {
/* 182 */       return ((paramObject instanceof Variable)) && (super.equals(paramObject));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.ProcessEnvironment
 * JD-Core Version:    0.6.2
 */