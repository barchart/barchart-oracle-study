package netscape.javascript;

public class JSException extends RuntimeException
{

  /** @deprecated */
  public static final int EXCEPTION_TYPE_EMPTY = -1;

  /** @deprecated */
  public static final int EXCEPTION_TYPE_VOID = 0;

  /** @deprecated */
  public static final int EXCEPTION_TYPE_OBJECT = 1;

  /** @deprecated */
  public static final int EXCEPTION_TYPE_FUNCTION = 2;

  /** @deprecated */
  public static final int EXCEPTION_TYPE_STRING = 3;

  /** @deprecated */
  public static final int EXCEPTION_TYPE_NUMBER = 4;

  /** @deprecated */
  public static final int EXCEPTION_TYPE_BOOLEAN = 5;

  /** @deprecated */
  public static final int EXCEPTION_TYPE_ERROR = 6;

  /** @deprecated */
  protected String message = null;

  /** @deprecated */
  protected String filename = null;

  /** @deprecated */
  protected int lineno = -1;

  /** @deprecated */
  protected String source = null;

  /** @deprecated */
  protected int tokenIndex = -1;

  /** @deprecated */
  private int wrappedExceptionType = -1;

  /** @deprecated */
  private Object wrappedException = null;

  public JSException()
  {
    this(null);
  }

  public JSException(String paramString)
  {
    this(paramString, null, -1, null, -1);
  }

  /** @deprecated */
  public JSException(String paramString1, String paramString2, int paramInt1, String paramString3, int paramInt2)
  {
    super(paramString1);
    this.message = paramString1;
    this.filename = paramString2;
    this.lineno = paramInt1;
    this.source = paramString3;
    this.tokenIndex = paramInt2;
    this.wrappedExceptionType = -1;
  }

  /** @deprecated */
  public JSException(int paramInt, Object paramObject)
  {
    this();
    this.wrappedExceptionType = paramInt;
    this.wrappedException = paramObject;
  }

  /** @deprecated */
  public int getWrappedExceptionType()
  {
    return this.wrappedExceptionType;
  }

  /** @deprecated */
  public Object getWrappedException()
  {
    return this.wrappedException;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     netscape.javascript.JSException
 * JD-Core Version:    0.6.2
 */