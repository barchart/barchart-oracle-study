/*     */ package javax.swing;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectOutputStream;
/*     */ import javax.accessibility.Accessible;
/*     */ import javax.accessibility.AccessibleContext;
/*     */ import javax.accessibility.AccessibleRole;
/*     */ import javax.swing.plaf.ComponentUI;
/*     */ import javax.swing.plaf.ToolTipUI;
/*     */ 
/*     */ public class JToolTip extends JComponent
/*     */   implements Accessible
/*     */ {
/*     */   private static final String uiClassID = "ToolTipUI";
/*     */   String tipText;
/*     */   JComponent component;
/*     */ 
/*     */   public JToolTip()
/*     */   {
/*  81 */     setOpaque(true);
/*  82 */     updateUI();
/*     */   }
/*     */ 
/*     */   public ToolTipUI getUI()
/*     */   {
/*  91 */     return (ToolTipUI)this.ui;
/*     */   }
/*     */ 
/*     */   public void updateUI()
/*     */   {
/* 100 */     setUI((ToolTipUI)UIManager.getUI(this));
/*     */   }
/*     */ 
/*     */   public String getUIClassID()
/*     */   {
/* 112 */     return "ToolTipUI";
/*     */   }
/*     */ 
/*     */   public void setTipText(String paramString)
/*     */   {
/* 127 */     String str = this.tipText;
/* 128 */     this.tipText = paramString;
/* 129 */     firePropertyChange("tiptext", str, paramString);
/*     */   }
/*     */ 
/*     */   public String getTipText()
/*     */   {
/* 139 */     return this.tipText;
/*     */   }
/*     */ 
/*     */   public void setComponent(JComponent paramJComponent)
/*     */   {
/* 156 */     JComponent localJComponent = this.component;
/*     */ 
/* 158 */     this.component = paramJComponent;
/* 159 */     firePropertyChange("component", localJComponent, paramJComponent);
/*     */   }
/*     */ 
/*     */   public JComponent getComponent()
/*     */   {
/* 171 */     return this.component;
/*     */   }
/*     */ 
/*     */   boolean alwaysOnTop()
/*     */   {
/* 180 */     return true;
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*     */     throws IOException
/*     */   {
/* 190 */     paramObjectOutputStream.defaultWriteObject();
/* 191 */     if (getUIClassID().equals("ToolTipUI")) {
/* 192 */       byte b = JComponent.getWriteObjCounter(this);
/* 193 */       b = (byte)(b - 1); JComponent.setWriteObjCounter(this, b);
/* 194 */       if ((b == 0) && (this.ui != null))
/* 195 */         this.ui.installUI(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String paramString()
/*     */   {
/* 212 */     String str = this.tipText != null ? this.tipText : "";
/*     */ 
/* 215 */     return super.paramString() + ",tipText=" + str;
/*     */   }
/*     */ 
/*     */   public AccessibleContext getAccessibleContext()
/*     */   {
/* 234 */     if (this.accessibleContext == null) {
/* 235 */       this.accessibleContext = new AccessibleJToolTip();
/*     */     }
/* 237 */     return this.accessibleContext;
/*     */   }
/*     */ 
/*     */   protected class AccessibleJToolTip extends JComponent.AccessibleJComponent
/*     */   {
/*     */     protected AccessibleJToolTip()
/*     */     {
/* 254 */       super();
/*     */     }
/*     */ 
/*     */     public String getAccessibleDescription()
/*     */     {
/* 262 */       String str = this.accessibleDescription;
/*     */ 
/* 265 */       if (str == null) {
/* 266 */         str = (String)JToolTip.this.getClientProperty("AccessibleDescription");
/*     */       }
/* 268 */       if (str == null) {
/* 269 */         str = JToolTip.this.getTipText();
/*     */       }
/* 271 */       return str;
/*     */     }
/*     */ 
/*     */     public AccessibleRole getAccessibleRole()
/*     */     {
/* 281 */       return AccessibleRole.TOOL_TIP;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.JToolTip
 * JD-Core Version:    0.6.2
 */