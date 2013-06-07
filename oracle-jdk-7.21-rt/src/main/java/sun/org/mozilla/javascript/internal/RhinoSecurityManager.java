/*    */ package sun.org.mozilla.javascript.internal;
/*    */ 
/*    */ public class RhinoSecurityManager extends SecurityManager
/*    */ {
/*    */   protected Class getCurrentScriptClass()
/*    */   {
/* 19 */     Class[] arrayOfClass1 = getClassContext();
/* 20 */     for (Class localClass : arrayOfClass1) {
/* 21 */       if (((localClass != InterpretedFunction.class) && (NativeFunction.class.isAssignableFrom(localClass))) || (PolicySecurityController.SecureCaller.class.isAssignableFrom(localClass)))
/*    */       {
/* 23 */         return localClass;
/*    */       }
/*    */     }
/* 26 */     return null;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.RhinoSecurityManager
 * JD-Core Version:    0.6.2
 */