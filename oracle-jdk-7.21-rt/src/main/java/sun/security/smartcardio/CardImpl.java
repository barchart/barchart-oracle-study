/*     */ package sun.security.smartcardio;
/*     */ 
/*     */ import javax.smartcardio.ATR;
/*     */ import javax.smartcardio.Card;
/*     */ import javax.smartcardio.CardChannel;
/*     */ import javax.smartcardio.CardException;
/*     */ import javax.smartcardio.CardPermission;
/*     */ 
/*     */ final class CardImpl extends Card
/*     */ {
/*     */   private final TerminalImpl terminal;
/*     */   final long cardId;
/*     */   private final ATR atr;
/*     */   final int protocol;
/*     */   private final ChannelImpl basicChannel;
/*     */   private volatile State state;
/*     */   private volatile Thread exclusiveThread;
/* 159 */   private static byte[] commandOpenChannel = { 0, 112, 0, 0, 1 };
/*     */ 
/*     */   CardImpl(TerminalImpl paramTerminalImpl, String paramString)
/*     */     throws PCSCException
/*     */   {
/*  66 */     this.terminal = paramTerminalImpl;
/*  67 */     int i = 2;
/*     */     int j;
/*  69 */     if (paramString.equals("*")) {
/*  70 */       j = 3;
/*  71 */     } else if (paramString.equalsIgnoreCase("T=0")) {
/*  72 */       j = 1;
/*  73 */     } else if (paramString.equalsIgnoreCase("T=1")) {
/*  74 */       j = 2;
/*  75 */     } else if (paramString.equalsIgnoreCase("direct"))
/*     */     {
/*  77 */       j = 0;
/*  78 */       i = 3;
/*     */     } else {
/*  80 */       throw new IllegalArgumentException("Unsupported protocol " + paramString);
/*     */     }
/*  82 */     this.cardId = PCSC.SCardConnect(paramTerminalImpl.contextId, paramTerminalImpl.name, i, j);
/*     */ 
/*  84 */     byte[] arrayOfByte1 = new byte[2];
/*  85 */     byte[] arrayOfByte2 = PCSC.SCardStatus(this.cardId, arrayOfByte1);
/*  86 */     this.atr = new ATR(arrayOfByte2);
/*  87 */     this.protocol = (arrayOfByte1[1] & 0xFF);
/*  88 */     this.basicChannel = new ChannelImpl(this, 0);
/*  89 */     this.state = State.OK;
/*     */   }
/*     */ 
/*     */   void checkState() {
/*  93 */     State localState = this.state;
/*  94 */     if (localState == State.DISCONNECTED)
/*  95 */       throw new IllegalStateException("Card has been disconnected");
/*  96 */     if (localState == State.REMOVED)
/*  97 */       throw new IllegalStateException("Card has been removed");
/*     */   }
/*     */ 
/*     */   boolean isValid()
/*     */   {
/* 102 */     if (this.state != State.OK) {
/* 103 */       return false;
/*     */     }
/*     */     try
/*     */     {
/* 107 */       PCSC.SCardStatus(this.cardId, new byte[2]);
/* 108 */       return true;
/*     */     } catch (PCSCException localPCSCException) {
/* 110 */       this.state = State.REMOVED;
/* 111 */     }return false;
/*     */   }
/*     */ 
/*     */   private void checkSecurity(String paramString)
/*     */   {
/* 116 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 117 */     if (localSecurityManager != null)
/* 118 */       localSecurityManager.checkPermission(new CardPermission(this.terminal.name, paramString));
/*     */   }
/*     */ 
/*     */   void handleError(PCSCException paramPCSCException)
/*     */   {
/* 123 */     if (paramPCSCException.code == -2146434967)
/* 124 */       this.state = State.REMOVED;
/*     */   }
/*     */ 
/*     */   public ATR getATR()
/*     */   {
/* 129 */     return this.atr;
/*     */   }
/*     */ 
/*     */   public String getProtocol() {
/* 133 */     switch (this.protocol) {
/*     */     case 1:
/* 135 */       return "T=0";
/*     */     case 2:
/* 137 */       return "T=1";
/*     */     }
/*     */ 
/* 140 */     return "Unknown protocol " + this.protocol;
/*     */   }
/*     */ 
/*     */   public CardChannel getBasicChannel()
/*     */   {
/* 145 */     checkSecurity("getBasicChannel");
/* 146 */     checkState();
/* 147 */     return this.basicChannel;
/*     */   }
/*     */ 
/*     */   private static int getSW(byte[] paramArrayOfByte) {
/* 151 */     if (paramArrayOfByte.length < 2) {
/* 152 */       return -1;
/*     */     }
/* 154 */     int i = paramArrayOfByte[(paramArrayOfByte.length - 2)] & 0xFF;
/* 155 */     int j = paramArrayOfByte[(paramArrayOfByte.length - 1)] & 0xFF;
/* 156 */     return i << 8 | j;
/*     */   }
/*     */ 
/*     */   public CardChannel openLogicalChannel()
/*     */     throws CardException
/*     */   {
/* 162 */     checkSecurity("openLogicalChannel");
/* 163 */     checkState();
/* 164 */     checkExclusive();
/*     */     try {
/* 166 */       byte[] arrayOfByte = PCSC.SCardTransmit(this.cardId, this.protocol, commandOpenChannel, 0, commandOpenChannel.length);
/*     */ 
/* 168 */       if ((arrayOfByte.length != 3) || (getSW(arrayOfByte) != 36864)) {
/* 169 */         throw new CardException("openLogicalChannel() failed, card response: " + PCSC.toString(arrayOfByte));
/*     */       }
/*     */ 
/* 173 */       return new ChannelImpl(this, arrayOfByte[0]);
/*     */     } catch (PCSCException localPCSCException) {
/* 175 */       handleError(localPCSCException);
/* 176 */       throw new CardException("openLogicalChannel() failed", localPCSCException);
/*     */     }
/*     */   }
/*     */ 
/*     */   void checkExclusive() throws CardException {
/* 181 */     Thread localThread = this.exclusiveThread;
/* 182 */     if (localThread == null) {
/* 183 */       return;
/*     */     }
/* 185 */     if (localThread != Thread.currentThread())
/* 186 */       throw new CardException("Exclusive access established by another Thread");
/*     */   }
/*     */ 
/*     */   public synchronized void beginExclusive() throws CardException
/*     */   {
/* 191 */     checkSecurity("exclusive");
/* 192 */     checkState();
/* 193 */     if (this.exclusiveThread != null) {
/* 194 */       throw new CardException("Exclusive access has already been assigned to Thread " + this.exclusiveThread.getName());
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 199 */       PCSC.SCardBeginTransaction(this.cardId);
/*     */     } catch (PCSCException localPCSCException) {
/* 201 */       handleError(localPCSCException);
/* 202 */       throw new CardException("beginExclusive() failed", localPCSCException);
/*     */     }
/* 204 */     this.exclusiveThread = Thread.currentThread();
/*     */   }
/*     */ 
/*     */   public synchronized void endExclusive() throws CardException {
/* 208 */     checkState();
/* 209 */     if (this.exclusiveThread != Thread.currentThread()) {
/* 210 */       throw new IllegalStateException("Exclusive access not assigned to current Thread");
/*     */     }
/*     */     try
/*     */     {
/* 214 */       PCSC.SCardEndTransaction(this.cardId, 0);
/*     */     } catch (PCSCException localPCSCException) {
/* 216 */       handleError(localPCSCException);
/* 217 */       throw new CardException("endExclusive() failed", localPCSCException);
/*     */     } finally {
/* 219 */       this.exclusiveThread = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public byte[] transmitControlCommand(int paramInt, byte[] paramArrayOfByte) throws CardException
/*     */   {
/* 225 */     checkSecurity("transmitControl");
/* 226 */     checkState();
/* 227 */     checkExclusive();
/* 228 */     if (paramArrayOfByte == null)
/* 229 */       throw new NullPointerException();
/*     */     try
/*     */     {
/* 232 */       return PCSC.SCardControl(this.cardId, paramInt, paramArrayOfByte);
/*     */     }
/*     */     catch (PCSCException localPCSCException) {
/* 235 */       handleError(localPCSCException);
/* 236 */       throw new CardException("transmitControlCommand() failed", localPCSCException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void disconnect(boolean paramBoolean) throws CardException {
/* 241 */     if (paramBoolean) {
/* 242 */       checkSecurity("reset");
/*     */     }
/* 244 */     if (this.state != State.OK) {
/* 245 */       return;
/*     */     }
/* 247 */     checkExclusive();
/*     */     try {
/* 249 */       PCSC.SCardDisconnect(this.cardId, paramBoolean ? 0 : 1);
/*     */     } catch (PCSCException localPCSCException) {
/* 251 */       throw new CardException("disconnect() failed", localPCSCException);
/*     */     } finally {
/* 253 */       this.state = State.DISCONNECTED;
/* 254 */       this.exclusiveThread = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 259 */     return "PC/SC card in " + this.terminal.getName() + ", protocol " + getProtocol() + ", state " + this.state;
/*     */   }
/*     */ 
/*     */   protected void finalize() throws Throwable
/*     */   {
/*     */     try {
/* 265 */       if (this.state == State.OK)
/* 266 */         PCSC.SCardDisconnect(this.cardId, 0);
/*     */     }
/*     */     finally {
/* 269 */       super.finalize();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum State
/*     */   {
/*  42 */     OK, REMOVED, DISCONNECTED;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.smartcardio.CardImpl
 * JD-Core Version:    0.6.2
 */