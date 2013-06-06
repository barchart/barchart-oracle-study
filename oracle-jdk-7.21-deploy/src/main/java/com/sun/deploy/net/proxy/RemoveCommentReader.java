package com.sun.deploy.net.proxy;

import java.io.BufferedReader;
import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public final class RemoveCommentReader extends FilterReader
{
  boolean inComment1 = false;
  boolean inComment2 = false;
  int inQuote = 0;
  boolean havePendingChar = false;
  int pendingChar = -1;

  public RemoveCommentReader(Reader paramReader)
  {
    super(new BufferedReader(paramReader));
  }

  private int nextChar()
    throws IOException
  {
    if (this.havePendingChar)
    {
      this.havePendingChar = false;
      return this.pendingChar;
    }
    return this.in.read();
  }

  private void setPendingChar(int paramInt)
  {
    this.pendingChar = paramInt;
    this.havePendingChar = true;
  }

  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = 0;
    int j = paramInt1;
    if (this.havePendingChar)
      paramArrayOfChar[(j++)] = ((char)nextChar());
    while ((j < paramInt1 + paramInt2) && ((i = nextChar()) != -1))
    {
      if ((!this.inComment1) && (!this.inComment2));
      int k;
      switch (i)
      {
      case 92:
        paramArrayOfChar[(j++)] = ((char)i);
        k = nextChar();
        if (j < paramInt1 + paramInt2)
        {
          paramArrayOfChar[(j++)] = ((char)k);
        }
        else
        {
          setPendingChar(k);
          return j - paramInt1;
        }
        break;
      case 34:
      case 39:
        if (this.inQuote == 0)
          this.inQuote = i;
        else if (this.inQuote == i)
          this.inQuote = 0;
        paramArrayOfChar[(j++)] = ((char)i);
        break;
      case 47:
        if (this.inQuote == 0)
        {
          k = nextChar();
          switch (k)
          {
          case -1:
            paramArrayOfChar[(j++)] = ((char)i);
            return j - paramInt1;
          case 47:
            this.inComment1 = true;
            break;
          case 42:
            this.inComment2 = true;
            break;
          default:
            paramArrayOfChar[(j++)] = ((char)i);
            if (j < paramInt1 + paramInt2)
            {
              if (Character.isWhitespace(k))
              {
                paramArrayOfChar[(j++)] = ' ';
                continue;
              }
              paramArrayOfChar[(j++)] = ((char)k);
              continue;
            }
            setPendingChar(k);
            return j - paramInt1;
          }
        }
        else
        {
          paramArrayOfChar[(j++)] = ((char)i);
        }
        break;
      default:
        if (Character.isWhitespace(i))
        {
          paramArrayOfChar[(j++)] = ' ';
        }
        else
        {
          paramArrayOfChar[(j++)] = ((char)i);
          continue;
          if (this.inComment1)
          {
            this.inComment1 = (i != 10);
          }
          else if (i == 42)
          {
            k = nextChar();
            if (k == 47)
              this.inComment2 = false;
            else
              setPendingChar(k);
          }
        }
        break;
      }
    }
    if ((j == paramInt1) && (i == -1))
      return -1;
    return j - paramInt1;
  }

  public int read()
    throws IOException
  {
    char[] arrayOfChar = new char[1];
    int i = read(arrayOfChar, 0, 1);
    if (i == -1)
      return -1;
    return arrayOfChar[0];
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.net.proxy.RemoveCommentReader
 * JD-Core Version:    0.6.2
 */