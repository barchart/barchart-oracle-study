/*    */ package javax.xml.bind.annotation.adapters;
/*    */ 
/*    */ import javax.xml.bind.DatatypeConverter;
/*    */ 
/*    */ public final class HexBinaryAdapter extends XmlAdapter<String, byte[]>
/*    */ {
/*    */   public byte[] unmarshal(String s)
/*    */   {
/* 41 */     if (s == null) return null;
/* 42 */     return DatatypeConverter.parseHexBinary(s);
/*    */   }
/*    */ 
/*    */   public String marshal(byte[] bytes) {
/* 46 */     if (bytes == null) return null;
/* 47 */     return DatatypeConverter.printHexBinary(bytes);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.bind.annotation.adapters.HexBinaryAdapter
 * JD-Core Version:    0.6.2
 */