package sun.plugin2.message;

import sun.plugin2.message.transport.SerializingTransport;

public class PluginMessages
{
  static final int SET_JVM_ID = 1;
  static final int JVM_STARTED_ID = 2;
  static final int START_APPLET = 3;
  static final int START_APPLET_ACK = 4;
  static final int SET_APPLET_SIZE = 5;
  static final int SET_CHILD_WINDOW_HANDLE = 6;
  static final int SYNTHESIZE_WINDOW_ACTIVATION = 7;
  static final int PRINT_APPLET = 8;
  static final int PRINT_APPLET_REPLY = 9;
  static final int PRINT_BAND = 10;
  static final int PRINT_BAND_REPLY = 11;
  static final int STOP_APPLET = 12;
  static final int STOP_APPLET_ACK = 13;
  static final int SHUTDOWN_JVM = 14;
  static final int HEARTBEAT = 15;
  static final int MARK_JVM_TAINTED = 16;
  static final int BEST_JRE_AVAILABLE = 17;
  static final int LAUNCH_JVM_ID = 18;
  static final int JAVASCRIPT_GET_WINDOW = 21;
  static final int JAVASCRIPT_CALL = 22;
  static final int JAVASCRIPT_EVAL = 23;
  static final int JAVASCRIPT_MEMBER_OP = 24;
  static final int JAVASCRIPT_SLOT_OP = 25;
  static final int JAVASCRIPT_TO_STRING = 26;
  static final int JAVASCRIPT_REPLY = 27;
  static final int JAVASCRIPT_RELEASE_OBJECT = 28;
  static final int GET_APPLET = 31;
  static final int GET_NAMESPACE = 32;
  static final int JAVA_OBJECT_OP = 33;
  static final int JAVA_REPLY = 34;
  static final int RELEASE_REMOTE_OBJECT = 35;
  static final int GET_PROXY = 41;
  static final int PROXY_REPLY = 42;
  static final int GET_AUTHENTICATION = 43;
  static final int GET_AUTHENTICATION_REPLY = 44;
  static final int COOKIE_OP = 45;
  static final int COOKIE_REPLY = 46;
  static final int SHOW_DOCUMENT = 51;
  static final int SHOW_STATUS = 52;
  static final int MODALITY_CHANGE = 61;
  static final int MOVE_OVERLAY_WINDOW = 80;
  static final int CACONTEXT_ID = 81;
  static final int FOCUS_EVENT = 82;
  static final int KEY_EVENT = 83;
  static final int MOUSE_EVENT = 84;
  static final int SCROLL_EVENT = 85;
  static final int TEXT_EVENT = 86;
  static final int CUSTOM_SECURITY_MANAGER_REQUEST = 90;
  static final int CUSTOM_SECURITY_MANAGER_ACK = 91;

  public static void register(SerializingTransport paramSerializingTransport)
  {
    paramSerializingTransport.registerMessageID(1, SetJVMIDMessage.class);
    paramSerializingTransport.registerMessageID(18, LaunchJVMAppletMessage.class);
    paramSerializingTransport.registerMessageID(2, JVMStartedMessage.class);
    paramSerializingTransport.registerMessageID(3, StartAppletMessage.class);
    paramSerializingTransport.registerMessageID(4, StartAppletAckMessage.class);
    paramSerializingTransport.registerMessageID(5, SetAppletSizeMessage.class);
    paramSerializingTransport.registerMessageID(7, WindowActivationEventMessage.class);
    paramSerializingTransport.registerMessageID(8, PrintAppletMessage.class);
    paramSerializingTransport.registerMessageID(9, PrintAppletReplyMessage.class);
    paramSerializingTransport.registerMessageID(10, PrintBandMessage.class);
    paramSerializingTransport.registerMessageID(11, PrintBandReplyMessage.class);
    paramSerializingTransport.registerMessageID(12, StopAppletMessage.class);
    paramSerializingTransport.registerMessageID(13, StopAppletAckMessage.class);
    paramSerializingTransport.registerMessageID(14, ShutdownJVMMessage.class);
    paramSerializingTransport.registerMessageID(15, HeartbeatMessage.class);
    paramSerializingTransport.registerMessageID(16, MarkTaintedMessage.class);
    paramSerializingTransport.registerMessageID(17, BestJREAvailableMessage.class);
    paramSerializingTransport.registerMessageID(21, JavaScriptGetWindowMessage.class);
    paramSerializingTransport.registerMessageID(22, JavaScriptCallMessage.class);
    paramSerializingTransport.registerMessageID(23, JavaScriptEvalMessage.class);
    paramSerializingTransport.registerMessageID(24, JavaScriptMemberOpMessage.class);
    paramSerializingTransport.registerMessageID(25, JavaScriptSlotOpMessage.class);
    paramSerializingTransport.registerMessageID(26, JavaScriptToStringMessage.class);
    paramSerializingTransport.registerMessageID(27, JavaScriptReplyMessage.class);
    paramSerializingTransport.registerMessageID(28, JavaScriptReleaseObjectMessage.class);
    paramSerializingTransport.registerMessageID(31, GetAppletMessage.class);
    paramSerializingTransport.registerMessageID(32, GetNameSpaceMessage.class);
    paramSerializingTransport.registerMessageID(33, JavaObjectOpMessage.class);
    paramSerializingTransport.registerMessageID(34, JavaReplyMessage.class);
    paramSerializingTransport.registerMessageID(35, ReleaseRemoteObjectMessage.class);
    paramSerializingTransport.registerMessageID(41, GetProxyMessage.class);
    paramSerializingTransport.registerMessageID(42, ProxyReplyMessage.class);
    paramSerializingTransport.registerMessageID(43, GetAuthenticationMessage.class);
    paramSerializingTransport.registerMessageID(44, GetAuthenticationReplyMessage.class);
    paramSerializingTransport.registerMessageID(45, CookieOpMessage.class);
    paramSerializingTransport.registerMessageID(46, CookieReplyMessage.class);
    paramSerializingTransport.registerMessageID(51, ShowDocumentMessage.class);
    paramSerializingTransport.registerMessageID(52, ShowStatusMessage.class);
    paramSerializingTransport.registerMessageID(61, ModalityChangeMessage.class);
    paramSerializingTransport.registerMessageID(83, KeyEventMessage.class);
    paramSerializingTransport.registerMessageID(84, MouseEventMessage.class);
    paramSerializingTransport.registerMessageID(85, ScrollEventMessage.class);
    paramSerializingTransport.registerMessageID(86, TextEventMessage.class);
    paramSerializingTransport.registerMessageID(82, FocusTransitionEventMessage.class);
    paramSerializingTransport.registerMessageID(80, OverlayWindowMoveMessage.class);
    paramSerializingTransport.registerMessageID(81, RemoteCAContextIdMessage.class);
    paramSerializingTransport.registerMessageID(90, CustomSecurityManagerRequestMessage.class);
    paramSerializingTransport.registerMessageID(91, CustomSecurityManagerAckMessage.class);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.PluginMessages
 * JD-Core Version:    0.6.2
 */