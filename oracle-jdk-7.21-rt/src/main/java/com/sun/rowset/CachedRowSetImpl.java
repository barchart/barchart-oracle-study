/*       */ package com.sun.rowset;
/*       */ 
/*       */ import com.sun.rowset.internal.BaseRow;
/*       */ import com.sun.rowset.internal.CachedRowSetReader;
/*       */ import com.sun.rowset.internal.CachedRowSetWriter;
/*       */ import com.sun.rowset.internal.InsertRow;
/*       */ import com.sun.rowset.internal.Row;
/*       */ import com.sun.rowset.providers.RIOptimisticProvider;
/*       */ import java.io.ByteArrayInputStream;
/*       */ import java.io.ByteArrayOutputStream;
/*       */ import java.io.IOException;
/*       */ import java.io.InputStream;
/*       */ import java.io.InputStreamReader;
/*       */ import java.io.ObjectInputStream;
/*       */ import java.io.ObjectOutputStream;
/*       */ import java.io.OptionalDataException;
/*       */ import java.io.PrintStream;
/*       */ import java.io.Reader;
/*       */ import java.io.Serializable;
/*       */ import java.io.StreamCorruptedException;
/*       */ import java.io.StringBufferInputStream;
/*       */ import java.io.StringReader;
/*       */ import java.io.UnsupportedEncodingException;
/*       */ import java.math.BigDecimal;
/*       */ import java.net.URL;
/*       */ import java.sql.Array;
/*       */ import java.sql.Blob;
/*       */ import java.sql.Clob;
/*       */ import java.sql.Connection;
/*       */ import java.sql.DatabaseMetaData;
/*       */ import java.sql.NClob;
/*       */ import java.sql.Ref;
/*       */ import java.sql.ResultSet;
/*       */ import java.sql.ResultSetMetaData;
/*       */ import java.sql.RowId;
/*       */ import java.sql.SQLData;
/*       */ import java.sql.SQLException;
/*       */ import java.sql.SQLFeatureNotSupportedException;
/*       */ import java.sql.SQLWarning;
/*       */ import java.sql.SQLXML;
/*       */ import java.sql.Savepoint;
/*       */ import java.sql.Statement;
/*       */ import java.sql.Struct;
/*       */ import java.sql.Time;
/*       */ import java.sql.Timestamp;
/*       */ import java.text.DateFormat;
/*       */ import java.text.MessageFormat;
/*       */ import java.text.ParseException;
/*       */ import java.util.Calendar;
/*       */ import java.util.Collection;
/*       */ import java.util.Hashtable;
/*       */ import java.util.Iterator;
/*       */ import java.util.Map;
/*       */ import java.util.TreeMap;
/*       */ import java.util.Vector;
/*       */ import javax.sql.RowSet;
/*       */ import javax.sql.RowSetEvent;
/*       */ import javax.sql.RowSetInternal;
/*       */ import javax.sql.RowSetMetaData;
/*       */ import javax.sql.RowSetReader;
/*       */ import javax.sql.RowSetWriter;
/*       */ import javax.sql.rowset.BaseRowSet;
/*       */ import javax.sql.rowset.CachedRowSet;
/*       */ import javax.sql.rowset.RowSetMetaDataImpl;
/*       */ import javax.sql.rowset.RowSetWarning;
/*       */ import javax.sql.rowset.serial.SQLInputImpl;
/*       */ import javax.sql.rowset.serial.SerialArray;
/*       */ import javax.sql.rowset.serial.SerialBlob;
/*       */ import javax.sql.rowset.serial.SerialClob;
/*       */ import javax.sql.rowset.serial.SerialRef;
/*       */ import javax.sql.rowset.serial.SerialStruct;
/*       */ import javax.sql.rowset.spi.SyncFactory;
/*       */ import javax.sql.rowset.spi.SyncProvider;
/*       */ import javax.sql.rowset.spi.SyncProviderException;
/*       */ import javax.sql.rowset.spi.TransactionalWriter;
/*       */ 
/*       */ public class CachedRowSetImpl extends BaseRowSet
/*       */   implements RowSet, RowSetInternal, Serializable, Cloneable, CachedRowSet
/*       */ {
/*       */   private SyncProvider provider;
/*       */   private RowSetReader rowSetReader;
/*       */   private RowSetWriter rowSetWriter;
/*       */   private transient Connection conn;
/*       */   private transient ResultSetMetaData RSMD;
/*       */   private RowSetMetaDataImpl RowSetMD;
/*       */   private int[] keyCols;
/*       */   private String tableName;
/*       */   private Vector<Object> rvh;
/*       */   private int cursorPos;
/*       */   private int absolutePos;
/*       */   private int numDeleted;
/*       */   private int numRows;
/*       */   private InsertRow insertRow;
/*       */   private boolean onInsertRow;
/*       */   private int currentRow;
/*       */   private boolean lastValueNull;
/*       */   private SQLWarning sqlwarn;
/*   192 */   private String strMatchColumn = "";
/*       */ 
/*   197 */   private int iMatchColumn = -1;
/*       */   private RowSetWarning rowsetWarning;
/*   207 */   private String DEFAULT_SYNC_PROVIDER = "com.sun.rowset.providers.RIOptimisticProvider";
/*       */   private boolean dbmslocatorsUpdateCopy;
/*       */   private transient ResultSet resultSet;
/*       */   private int endPos;
/*       */   private int prevEndPos;
/*       */   private int startPos;
/*       */   private int startPrev;
/*       */   private int pageSize;
/*       */   private int maxRowsreached;
/*   258 */   private boolean pagenotend = true;
/*       */   private boolean onFirstPage;
/*       */   private boolean onLastPage;
/*       */   private int populatecallcount;
/*       */   private int totalRows;
/*       */   private boolean callWithCon;
/*       */   private CachedRowSetReader crsReader;
/*       */   private Vector<Integer> iMatchColumns;
/*       */   private Vector<String> strMatchColumns;
/*   307 */   private boolean tXWriter = false;
/*       */ 
/*   312 */   private TransactionalWriter tWriter = null;
/*       */   protected transient JdbcRowSetResourceBundle resBundle;
/*       */   private boolean updateOnInsert;
/*       */   static final long serialVersionUID = 1884577171200622428L;
/*       */ 
/*       */   public CachedRowSetImpl()
/*       */     throws SQLException
/*       */   {
/*       */     try
/*       */     {
/*   353 */       this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
/*       */     } catch (IOException localIOException) {
/*   355 */       throw new RuntimeException(localIOException);
/*       */     }
/*       */ 
/*   359 */     this.provider = SyncFactory.getInstance(this.DEFAULT_SYNC_PROVIDER);
/*       */ 
/*   362 */     if (!(this.provider instanceof RIOptimisticProvider)) {
/*   363 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidp").toString());
/*       */     }
/*       */ 
/*   366 */     this.rowSetReader = ((CachedRowSetReader)this.provider.getRowSetReader());
/*   367 */     this.rowSetWriter = ((CachedRowSetWriter)this.provider.getRowSetWriter());
/*       */ 
/*   370 */     initParams();
/*       */ 
/*   372 */     initContainer();
/*       */ 
/*   375 */     initProperties();
/*       */ 
/*   378 */     this.onInsertRow = false;
/*   379 */     this.insertRow = null;
/*       */ 
/*   382 */     this.sqlwarn = new SQLWarning();
/*   383 */     this.rowsetWarning = new RowSetWarning();
/*       */   }
/*       */ 
/*       */   public CachedRowSetImpl(Hashtable paramHashtable)
/*       */     throws SQLException
/*       */   {
/*       */     try
/*       */     {
/*   456 */       this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
/*       */     } catch (IOException localIOException) {
/*   458 */       throw new RuntimeException(localIOException);
/*       */     }
/*       */ 
/*   461 */     if (paramHashtable == null) {
/*   462 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.nullhash").toString());
/*       */     }
/*       */ 
/*   465 */     String str = (String)paramHashtable.get("rowset.provider.classname");
/*       */ 
/*   469 */     this.provider = SyncFactory.getInstance(str);
/*       */ 
/*   472 */     this.rowSetReader = this.provider.getRowSetReader();
/*   473 */     this.rowSetWriter = this.provider.getRowSetWriter();
/*       */ 
/*   475 */     initParams();
/*   476 */     initContainer();
/*   477 */     initProperties();
/*       */   }
/*       */ 
/*       */   private void initContainer()
/*       */   {
/*   487 */     this.rvh = new Vector(100);
/*   488 */     this.cursorPos = 0;
/*   489 */     this.absolutePos = 0;
/*   490 */     this.numRows = 0;
/*   491 */     this.numDeleted = 0;
/*       */   }
/*       */ 
/*       */   private void initProperties()
/*       */     throws SQLException
/*       */   {
/*   502 */     if (this.resBundle == null) {
/*       */       try {
/*   504 */         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
/*       */       } catch (IOException localIOException) {
/*   506 */         throw new RuntimeException(localIOException);
/*       */       }
/*       */     }
/*   509 */     setShowDeleted(false);
/*   510 */     setQueryTimeout(0);
/*   511 */     setMaxRows(0);
/*   512 */     setMaxFieldSize(0);
/*   513 */     setType(1004);
/*   514 */     setConcurrency(1008);
/*   515 */     if ((this.rvh.size() > 0) && (!isReadOnly()))
/*   516 */       setReadOnly(false);
/*       */     else
/*   518 */       setReadOnly(true);
/*   519 */     setTransactionIsolation(2);
/*   520 */     setEscapeProcessing(true);
/*       */ 
/*   522 */     checkTransactionalWriter();
/*       */ 
/*   526 */     this.iMatchColumns = new Vector(10);
/*   527 */     for (int i = 0; i < 10; i++) {
/*   528 */       this.iMatchColumns.add(i, Integer.valueOf(-1));
/*       */     }
/*       */ 
/*   531 */     this.strMatchColumns = new Vector(10);
/*   532 */     for (i = 0; i < 10; i++)
/*   533 */       this.strMatchColumns.add(i, null);
/*       */   }
/*       */ 
/*       */   private void checkTransactionalWriter()
/*       */   {
/*   542 */     if (this.rowSetWriter != null) {
/*   543 */       Class localClass = this.rowSetWriter.getClass();
/*   544 */       if (localClass != null) {
/*   545 */         Class[] arrayOfClass = localClass.getInterfaces();
/*   546 */         for (int i = 0; i < arrayOfClass.length; i++)
/*   547 */           if (arrayOfClass[i].getName().indexOf("TransactionalWriter") > 0) {
/*   548 */             this.tXWriter = true;
/*   549 */             establishTransactionalWriter();
/*       */           }
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   private void establishTransactionalWriter()
/*       */   {
/*   560 */     this.tWriter = ((TransactionalWriter)this.provider.getRowSetWriter());
/*       */   }
/*       */ 
/*       */   public void setCommand(String paramString)
/*       */     throws SQLException
/*       */   {
/*   583 */     super.setCommand(paramString);
/*       */ 
/*   585 */     if (!buildTableName(paramString).equals(""))
/*   586 */       setTableName(buildTableName(paramString));
/*       */   }
/*       */ 
/*       */   public void populate(ResultSet paramResultSet)
/*       */     throws SQLException
/*       */   {
/*   625 */     Map localMap = getTypeMap();
/*       */ 
/*   629 */     if (paramResultSet == null) {
/*   630 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.populate").toString());
/*       */     }
/*   632 */     this.resultSet = paramResultSet;
/*       */ 
/*   635 */     this.RSMD = paramResultSet.getMetaData();
/*       */ 
/*   638 */     this.RowSetMD = new RowSetMetaDataImpl();
/*   639 */     initMetaData(this.RowSetMD, this.RSMD);
/*       */ 
/*   642 */     this.RSMD = null;
/*   643 */     int j = this.RowSetMD.getColumnCount();
/*   644 */     int m = getMaxRows();
/*   645 */     int i = 0;
/*   646 */     Row localRow = null;
/*       */ 
/*   648 */     while (paramResultSet.next())
/*       */     {
/*   650 */       localRow = new Row(j);
/*       */ 
/*   652 */       if ((i > m) && (m > 0)) {
/*   653 */         this.rowsetWarning.setNextWarning(new RowSetWarning("Populating rows setting has exceeded max row setting"));
/*       */       }
/*       */ 
/*   656 */       for (int k = 1; k <= j; k++)
/*       */       {
/*       */         Object localObject;
/*   663 */         if (localMap == null)
/*   664 */           localObject = paramResultSet.getObject(k);
/*       */         else {
/*   666 */           localObject = paramResultSet.getObject(k, localMap);
/*       */         }
/*       */ 
/*   673 */         if ((localObject instanceof Struct))
/*   674 */           localObject = new SerialStruct((Struct)localObject, localMap);
/*   675 */         else if ((localObject instanceof SQLData))
/*   676 */           localObject = new SerialStruct((SQLData)localObject, localMap);
/*   677 */         else if ((localObject instanceof Blob))
/*   678 */           localObject = new SerialBlob((Blob)localObject);
/*   679 */         else if ((localObject instanceof Clob))
/*   680 */           localObject = new SerialClob((Clob)localObject);
/*   681 */         else if ((localObject instanceof Array)) {
/*   682 */           if (localMap != null)
/*   683 */             localObject = new SerialArray((Array)localObject, localMap);
/*       */           else {
/*   685 */             localObject = new SerialArray((Array)localObject);
/*       */           }
/*       */         }
/*   688 */         localRow.initColumnObject(k, localObject);
/*       */       }
/*   690 */       i++;
/*   691 */       this.rvh.add(localRow);
/*       */     }
/*       */ 
/*   694 */     this.numRows = i;
/*       */ 
/*   698 */     notifyRowSetChanged();
/*       */   }
/*       */ 
/*       */   private void initMetaData(RowSetMetaDataImpl paramRowSetMetaDataImpl, ResultSetMetaData paramResultSetMetaData)
/*       */     throws SQLException
/*       */   {
/*   715 */     int i = paramResultSetMetaData.getColumnCount();
/*       */ 
/*   717 */     paramRowSetMetaDataImpl.setColumnCount(i);
/*   718 */     for (int j = 1; j <= i; j++) {
/*   719 */       paramRowSetMetaDataImpl.setAutoIncrement(j, paramResultSetMetaData.isAutoIncrement(j));
/*   720 */       if (paramResultSetMetaData.isAutoIncrement(j))
/*   721 */         this.updateOnInsert = true;
/*   722 */       paramRowSetMetaDataImpl.setCaseSensitive(j, paramResultSetMetaData.isCaseSensitive(j));
/*   723 */       paramRowSetMetaDataImpl.setCurrency(j, paramResultSetMetaData.isCurrency(j));
/*   724 */       paramRowSetMetaDataImpl.setNullable(j, paramResultSetMetaData.isNullable(j));
/*   725 */       paramRowSetMetaDataImpl.setSigned(j, paramResultSetMetaData.isSigned(j));
/*   726 */       paramRowSetMetaDataImpl.setSearchable(j, paramResultSetMetaData.isSearchable(j));
/*       */ 
/*   731 */       int k = paramResultSetMetaData.getColumnDisplaySize(j);
/*   732 */       if (k < 0) {
/*   733 */         k = 0;
/*       */       }
/*   735 */       paramRowSetMetaDataImpl.setColumnDisplaySize(j, k);
/*   736 */       paramRowSetMetaDataImpl.setColumnLabel(j, paramResultSetMetaData.getColumnLabel(j));
/*   737 */       paramRowSetMetaDataImpl.setColumnName(j, paramResultSetMetaData.getColumnName(j));
/*   738 */       paramRowSetMetaDataImpl.setSchemaName(j, paramResultSetMetaData.getSchemaName(j));
/*       */ 
/*   743 */       int m = paramResultSetMetaData.getPrecision(j);
/*   744 */       if (m < 0) {
/*   745 */         m = 0;
/*       */       }
/*   747 */       paramRowSetMetaDataImpl.setPrecision(j, m);
/*       */ 
/*   754 */       int n = paramResultSetMetaData.getScale(j);
/*   755 */       if (n < 0) {
/*   756 */         n = 0;
/*       */       }
/*   758 */       paramRowSetMetaDataImpl.setScale(j, n);
/*   759 */       paramRowSetMetaDataImpl.setTableName(j, paramResultSetMetaData.getTableName(j));
/*   760 */       paramRowSetMetaDataImpl.setCatalogName(j, paramResultSetMetaData.getCatalogName(j));
/*   761 */       paramRowSetMetaDataImpl.setColumnType(j, paramResultSetMetaData.getColumnType(j));
/*   762 */       paramRowSetMetaDataImpl.setColumnTypeName(j, paramResultSetMetaData.getColumnTypeName(j));
/*       */     }
/*       */ 
/*   765 */     if (this.conn != null)
/*       */     {
/*   768 */       this.dbmslocatorsUpdateCopy = this.conn.getMetaData().locatorsUpdateCopy();
/*       */     }
/*       */   }
/*       */ 
/*       */   public void execute(Connection paramConnection)
/*       */     throws SQLException
/*       */   {
/*   792 */     setConnection(paramConnection);
/*       */ 
/*   794 */     if (getPageSize() != 0) {
/*   795 */       this.crsReader = ((CachedRowSetReader)this.provider.getRowSetReader());
/*   796 */       this.crsReader.setStartPosition(1);
/*   797 */       this.callWithCon = true;
/*   798 */       this.crsReader.readData(this);
/*       */     }
/*       */     else
/*       */     {
/*   803 */       this.rowSetReader.readData(this);
/*       */     }
/*   805 */     this.RowSetMD = ((RowSetMetaDataImpl)getMetaData());
/*       */ 
/*   807 */     if (paramConnection != null)
/*       */     {
/*   810 */       this.dbmslocatorsUpdateCopy = paramConnection.getMetaData().locatorsUpdateCopy();
/*       */     }
/*       */   }
/*       */ 
/*       */   private void setConnection(Connection paramConnection)
/*       */   {
/*   830 */     this.conn = paramConnection;
/*       */   }
/*       */ 
/*       */   public void acceptChanges()
/*       */     throws SyncProviderException
/*       */   {
/*   869 */     if (this.onInsertRow == true) {
/*   870 */       throw new SyncProviderException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
/*       */     }
/*       */ 
/*   873 */     int i = this.cursorPos;
/*   874 */     int j = 0;
/*   875 */     boolean bool = false;
/*       */     try
/*       */     {
/*   878 */       if (this.rowSetWriter != null) {
/*   879 */         i = this.cursorPos;
/*   880 */         bool = this.rowSetWriter.writeData(this);
/*   881 */         this.cursorPos = i;
/*       */       }
/*       */ 
/*   884 */       if (this.tXWriter)
/*       */       {
/*   886 */         if (!bool) {
/*   887 */           this.tWriter = ((TransactionalWriter)this.rowSetWriter);
/*   888 */           this.tWriter.rollback();
/*   889 */           j = 0;
/*       */         } else {
/*   891 */           this.tWriter = ((TransactionalWriter)this.rowSetWriter);
/*   892 */           if ((this.tWriter instanceof CachedRowSetWriter))
/*   893 */             ((CachedRowSetWriter)this.tWriter).commit(this, this.updateOnInsert);
/*       */           else {
/*   895 */             this.tWriter.commit();
/*       */           }
/*       */ 
/*   898 */           j = 1;
/*       */         }
/*       */       }
/*       */ 
/*   902 */       if (j == 1) {
/*   903 */         setOriginal();
/*       */       }
/*   904 */       else if (j != 0);
/*       */     }
/*       */     catch (SyncProviderException localSyncProviderException)
/*       */     {
/*   909 */       throw localSyncProviderException;
/*       */     } catch (SQLException localSQLException) {
/*   911 */       localSQLException.printStackTrace();
/*   912 */       throw new SyncProviderException(localSQLException.getMessage());
/*       */     } catch (SecurityException localSecurityException) {
/*   914 */       throw new SyncProviderException(localSecurityException.getMessage());
/*       */     }
/*       */   }
/*       */ 
/*       */   public void acceptChanges(Connection paramConnection)
/*       */     throws SyncProviderException
/*       */   {
/*   942 */     setConnection(paramConnection);
/*   943 */     acceptChanges();
/*       */   }
/*       */ 
/*       */   public void restoreOriginal()
/*       */     throws SQLException
/*       */   {
/*   960 */     for (Iterator localIterator = this.rvh.iterator(); localIterator.hasNext(); ) {
/*   961 */       Row localRow = (Row)localIterator.next();
/*   962 */       if (localRow.getInserted() == true) {
/*   963 */         localIterator.remove();
/*   964 */         this.numRows -= 1;
/*       */       } else {
/*   966 */         if (localRow.getDeleted() == true) {
/*   967 */           localRow.clearDeleted();
/*       */         }
/*   969 */         if (localRow.getUpdated() == true) {
/*   970 */           localRow.clearUpdated();
/*       */         }
/*       */       }
/*       */     }
/*       */ 
/*   975 */     this.cursorPos = 0;
/*       */ 
/*   978 */     notifyRowSetChanged();
/*       */   }
/*       */ 
/*       */   public void release()
/*       */     throws SQLException
/*       */   {
/*   991 */     initContainer();
/*   992 */     notifyRowSetChanged();
/*       */   }
/*       */ 
/*       */   public void undoDelete()
/*       */     throws SQLException
/*       */   {
/*  1005 */     if (!getShowDeleted()) {
/*  1006 */       return;
/*       */     }
/*       */ 
/*  1009 */     checkCursor();
/*       */ 
/*  1012 */     if (this.onInsertRow == true) {
/*  1013 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
/*       */     }
/*       */ 
/*  1016 */     Row localRow = (Row)getCurrentRow();
/*  1017 */     if (localRow.getDeleted() == true) {
/*  1018 */       localRow.clearDeleted();
/*  1019 */       this.numDeleted -= 1;
/*  1020 */       notifyRowChanged();
/*       */     }
/*       */   }
/*       */ 
/*       */   public void undoInsert()
/*       */     throws SQLException
/*       */   {
/*  1039 */     checkCursor();
/*       */ 
/*  1042 */     if (this.onInsertRow == true) {
/*  1043 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
/*       */     }
/*       */ 
/*  1046 */     Row localRow = (Row)getCurrentRow();
/*  1047 */     if (localRow.getInserted() == true) {
/*  1048 */       this.rvh.remove(this.cursorPos - 1);
/*  1049 */       this.numRows -= 1;
/*  1050 */       notifyRowChanged();
/*       */     } else {
/*  1052 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.illegalop").toString());
/*       */     }
/*       */   }
/*       */ 
/*       */   public void undoUpdate()
/*       */     throws SQLException
/*       */   {
/*  1078 */     moveToCurrentRow();
/*       */ 
/*  1082 */     undoDelete();
/*       */ 
/*  1084 */     undoInsert();
/*       */   }
/*       */ 
/*       */   public RowSet createShared()
/*       */     throws SQLException
/*       */   {
/*       */     RowSet localRowSet;
/*       */     try
/*       */     {
/*  1108 */       localRowSet = (CachedRowSet)clone();
/*       */     } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*  1110 */       throw new SQLException(localCloneNotSupportedException.getMessage());
/*       */     }
/*  1112 */     return localRowSet;
/*       */   }
/*       */ 
/*       */   protected Object clone()
/*       */     throws CloneNotSupportedException
/*       */   {
/*  1132 */     return super.clone(); } 
/*       */   public CachedRowSet createCopy() throws SQLException { // Byte code:
/*       */     //   0: new 675	java/io/ByteArrayOutputStream
/*       */     //   3: dup
/*       */     //   4: invokespecial 1500	java/io/ByteArrayOutputStream:<init>	()V
/*       */     //   7: astore_2
/*       */     //   8: new 680	java/io/ObjectOutputStream
/*       */     //   11: dup
/*       */     //   12: aload_2
/*       */     //   13: invokespecial 1508	java/io/ObjectOutputStream:<init>	(Ljava/io/OutputStream;)V
/*       */     //   16: astore_1
/*       */     //   17: aload_1
/*       */     //   18: aload_0
/*       */     //   19: invokevirtual 1509	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
/*       */     //   22: goto +38 -> 60
/*       */     //   25: astore_3
/*       */     //   26: new 721	java/sql/SQLException
/*       */     //   29: dup
/*       */     //   30: aload_0
/*       */     //   31: getfield 1322	com/sun/rowset/CachedRowSetImpl:resBundle	Lcom/sun/rowset/JdbcRowSetResourceBundle;
/*       */     //   34: ldc 4
/*       */     //   36: invokevirtual 1472	com/sun/rowset/JdbcRowSetResourceBundle:handleGetObject	(Ljava/lang/String;)Ljava/lang/Object;
/*       */     //   39: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   42: iconst_1
/*       */     //   43: anewarray 702	java/lang/Object
/*       */     //   46: dup
/*       */     //   47: iconst_0
/*       */     //   48: aload_3
/*       */     //   49: invokevirtual 1502	java/io/IOException:getMessage	()Ljava/lang/String;
/*       */     //   52: aastore
/*       */     //   53: invokestatic 1587	java/text/MessageFormat:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*       */     //   56: invokespecial 1577	java/sql/SQLException:<init>	(Ljava/lang/String;)V
/*       */     //   59: athrow
/*       */     //   60: new 674	java/io/ByteArrayInputStream
/*       */     //   63: dup
/*       */     //   64: aload_2
/*       */     //   65: invokevirtual 1501	java/io/ByteArrayOutputStream:toByteArray	()[B
/*       */     //   68: invokespecial 1499	java/io/ByteArrayInputStream:<init>	([B)V
/*       */     //   71: astore 4
/*       */     //   73: new 679	java/io/ObjectInputStream
/*       */     //   76: dup
/*       */     //   77: aload 4
/*       */     //   79: invokespecial 1506	java/io/ObjectInputStream:<init>	(Ljava/io/InputStream;)V
/*       */     //   82: astore_3
/*       */     //   83: goto +77 -> 160
/*       */     //   86: astore 4
/*       */     //   88: new 721	java/sql/SQLException
/*       */     //   91: dup
/*       */     //   92: aload_0
/*       */     //   93: getfield 1322	com/sun/rowset/CachedRowSetImpl:resBundle	Lcom/sun/rowset/JdbcRowSetResourceBundle;
/*       */     //   96: ldc 4
/*       */     //   98: invokevirtual 1472	com/sun/rowset/JdbcRowSetResourceBundle:handleGetObject	(Ljava/lang/String;)Ljava/lang/Object;
/*       */     //   101: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   104: iconst_1
/*       */     //   105: anewarray 702	java/lang/Object
/*       */     //   108: dup
/*       */     //   109: iconst_0
/*       */     //   110: aload 4
/*       */     //   112: invokevirtual 1513	java/io/StreamCorruptedException:getMessage	()Ljava/lang/String;
/*       */     //   115: aastore
/*       */     //   116: invokestatic 1587	java/text/MessageFormat:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*       */     //   119: invokespecial 1577	java/sql/SQLException:<init>	(Ljava/lang/String;)V
/*       */     //   122: athrow
/*       */     //   123: astore 4
/*       */     //   125: new 721	java/sql/SQLException
/*       */     //   128: dup
/*       */     //   129: aload_0
/*       */     //   130: getfield 1322	com/sun/rowset/CachedRowSetImpl:resBundle	Lcom/sun/rowset/JdbcRowSetResourceBundle;
/*       */     //   133: ldc 4
/*       */     //   135: invokevirtual 1472	com/sun/rowset/JdbcRowSetResourceBundle:handleGetObject	(Ljava/lang/String;)Ljava/lang/Object;
/*       */     //   138: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   141: iconst_1
/*       */     //   142: anewarray 702	java/lang/Object
/*       */     //   145: dup
/*       */     //   146: iconst_0
/*       */     //   147: aload 4
/*       */     //   149: invokevirtual 1502	java/io/IOException:getMessage	()Ljava/lang/String;
/*       */     //   152: aastore
/*       */     //   153: invokestatic 1587	java/text/MessageFormat:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*       */     //   156: invokespecial 1577	java/sql/SQLException:<init>	(Ljava/lang/String;)V
/*       */     //   159: athrow
/*       */     //   160: aload_3
/*       */     //   161: invokevirtual 1507	java/io/ObjectInputStream:readObject	()Ljava/lang/Object;
/*       */     //   164: checkcast 666	com/sun/rowset/CachedRowSetImpl
/*       */     //   167: astore 4
/*       */     //   169: aload 4
/*       */     //   171: aload_0
/*       */     //   172: getfield 1322	com/sun/rowset/CachedRowSetImpl:resBundle	Lcom/sun/rowset/JdbcRowSetResourceBundle;
/*       */     //   175: putfield 1322	com/sun/rowset/CachedRowSetImpl:resBundle	Lcom/sun/rowset/JdbcRowSetResourceBundle;
/*       */     //   178: aload 4
/*       */     //   180: areturn
/*       */     //   181: astore 4
/*       */     //   183: new 721	java/sql/SQLException
/*       */     //   186: dup
/*       */     //   187: aload_0
/*       */     //   188: getfield 1322	com/sun/rowset/CachedRowSetImpl:resBundle	Lcom/sun/rowset/JdbcRowSetResourceBundle;
/*       */     //   191: ldc 4
/*       */     //   193: invokevirtual 1472	com/sun/rowset/JdbcRowSetResourceBundle:handleGetObject	(Ljava/lang/String;)Ljava/lang/Object;
/*       */     //   196: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   199: iconst_1
/*       */     //   200: anewarray 702	java/lang/Object
/*       */     //   203: dup
/*       */     //   204: iconst_0
/*       */     //   205: aload 4
/*       */     //   207: invokevirtual 1526	java/lang/ClassNotFoundException:getMessage	()Ljava/lang/String;
/*       */     //   210: aastore
/*       */     //   211: invokestatic 1587	java/text/MessageFormat:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*       */     //   214: invokespecial 1577	java/sql/SQLException:<init>	(Ljava/lang/String;)V
/*       */     //   217: athrow
/*       */     //   218: astore 4
/*       */     //   220: new 721	java/sql/SQLException
/*       */     //   223: dup
/*       */     //   224: aload_0
/*       */     //   225: getfield 1322	com/sun/rowset/CachedRowSetImpl:resBundle	Lcom/sun/rowset/JdbcRowSetResourceBundle;
/*       */     //   228: ldc 4
/*       */     //   230: invokevirtual 1472	com/sun/rowset/JdbcRowSetResourceBundle:handleGetObject	(Ljava/lang/String;)Ljava/lang/Object;
/*       */     //   233: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   236: iconst_1
/*       */     //   237: anewarray 702	java/lang/Object
/*       */     //   240: dup
/*       */     //   241: iconst_0
/*       */     //   242: aload 4
/*       */     //   244: invokevirtual 1510	java/io/OptionalDataException:getMessage	()Ljava/lang/String;
/*       */     //   247: aastore
/*       */     //   248: invokestatic 1587	java/text/MessageFormat:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*       */     //   251: invokespecial 1577	java/sql/SQLException:<init>	(Ljava/lang/String;)V
/*       */     //   254: athrow
/*       */     //   255: astore 4
/*       */     //   257: new 721	java/sql/SQLException
/*       */     //   260: dup
/*       */     //   261: aload_0
/*       */     //   262: getfield 1322	com/sun/rowset/CachedRowSetImpl:resBundle	Lcom/sun/rowset/JdbcRowSetResourceBundle;
/*       */     //   265: ldc 4
/*       */     //   267: invokevirtual 1472	com/sun/rowset/JdbcRowSetResourceBundle:handleGetObject	(Ljava/lang/String;)Ljava/lang/Object;
/*       */     //   270: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   273: iconst_1
/*       */     //   274: anewarray 702	java/lang/Object
/*       */     //   277: dup
/*       */     //   278: iconst_0
/*       */     //   279: aload 4
/*       */     //   281: invokevirtual 1502	java/io/IOException:getMessage	()Ljava/lang/String;
/*       */     //   284: aastore
/*       */     //   285: invokestatic 1587	java/text/MessageFormat:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*       */     //   288: invokespecial 1577	java/sql/SQLException:<init>	(Ljava/lang/String;)V
/*       */     //   291: athrow
/*       */     //
/*       */     // Exception table:
/*       */     //   from	to	target	type
/*       */     //   8	22	25	java/io/IOException
/*       */     //   60	83	86	java/io/StreamCorruptedException
/*       */     //   60	83	123	java/io/IOException
/*       */     //   160	180	181	java/lang/ClassNotFoundException
/*       */     //   160	180	218	java/io/OptionalDataException
/*       */     //   160	180	255	java/io/IOException } 
/*  1220 */   public CachedRowSet createCopySchema() throws SQLException { int i = this.numRows;
/*  1221 */     this.numRows = 0;
/*       */ 
/*  1223 */     CachedRowSet localCachedRowSet = createCopy();
/*       */ 
/*  1226 */     this.numRows = i;
/*       */ 
/*  1228 */     return localCachedRowSet;
/*       */   }
/*       */ 
/*       */   public CachedRowSet createCopyNoConstraints()
/*       */     throws SQLException
/*       */   {
/*  1252 */     CachedRowSetImpl localCachedRowSetImpl = (CachedRowSetImpl)createCopy();
/*       */ 
/*  1254 */     localCachedRowSetImpl.initProperties();
/*       */     try {
/*  1256 */       localCachedRowSetImpl.unsetMatchColumn(localCachedRowSetImpl.getMatchColumnIndexes());
/*       */     }
/*       */     catch (SQLException localSQLException1)
/*       */     {
/*       */     }
/*       */     try {
/*  1262 */       localCachedRowSetImpl.unsetMatchColumn(localCachedRowSetImpl.getMatchColumnNames());
/*       */     }
/*       */     catch (SQLException localSQLException2)
/*       */     {
/*       */     }
/*  1267 */     return localCachedRowSetImpl;
/*       */   }
/*       */ 
/*       */   public Collection<?> toCollection()
/*       */     throws SQLException
/*       */   {
/*  1287 */     TreeMap localTreeMap = new TreeMap();
/*       */ 
/*  1289 */     for (int i = 0; i < this.numRows; i++) {
/*  1290 */       localTreeMap.put(Integer.valueOf(i), this.rvh.get(i));
/*       */     }
/*       */ 
/*  1293 */     return localTreeMap.values();
/*       */   }
/*       */ 
/*       */   public Collection<?> toCollection(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  1316 */     int i = this.numRows;
/*  1317 */     Vector localVector = new Vector(i);
/*       */ 
/*  1321 */     CachedRowSetImpl localCachedRowSetImpl = (CachedRowSetImpl)createCopy();
/*       */ 
/*  1323 */     while (i != 0) {
/*  1324 */       localCachedRowSetImpl.next();
/*  1325 */       localVector.add(localCachedRowSetImpl.getObject(paramInt));
/*  1326 */       i--;
/*       */     }
/*       */ 
/*  1329 */     return localVector;
/*       */   }
/*       */ 
/*       */   public Collection<?> toCollection(String paramString)
/*       */     throws SQLException
/*       */   {
/*  1351 */     return toCollection(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public SyncProvider getSyncProvider()
/*       */     throws SQLException
/*       */   {
/*  1370 */     return this.provider;
/*       */   }
/*       */ 
/*       */   public void setSyncProvider(String paramString)
/*       */     throws SQLException
/*       */   {
/*  1381 */     this.provider = SyncFactory.getInstance(paramString);
/*       */ 
/*  1384 */     this.rowSetReader = this.provider.getRowSetReader();
/*  1385 */     this.rowSetWriter = this.provider.getRowSetWriter();
/*       */   }
/*       */ 
/*       */   public void execute()
/*       */     throws SQLException
/*       */   {
/*  1426 */     execute(null);
/*       */   }
/*       */ 
/*       */   public boolean next()
/*       */     throws SQLException
/*       */   {
/*  1461 */     if ((this.cursorPos < 0) || (this.cursorPos >= this.numRows + 1)) {
/*  1462 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
/*       */     }
/*       */ 
/*  1465 */     boolean bool = internalNext();
/*  1466 */     notifyCursorMoved();
/*       */ 
/*  1468 */     return bool;
/*       */   }
/*       */ 
/*       */   protected boolean internalNext()
/*       */     throws SQLException
/*       */   {
/*  1495 */     boolean bool = false;
/*       */     do
/*       */     {
/*  1498 */       if (this.cursorPos < this.numRows) {
/*  1499 */         this.cursorPos += 1;
/*  1500 */         bool = true;
/*  1501 */       } else if (this.cursorPos == this.numRows)
/*       */       {
/*  1503 */         this.cursorPos += 1;
/*  1504 */         bool = false;
/*  1505 */         break;
/*       */       }
/*       */     }
/*  1507 */     while ((!getShowDeleted()) && (rowDeleted() == true));
/*       */ 
/*  1512 */     if (bool == true)
/*  1513 */       this.absolutePos += 1;
/*       */     else {
/*  1515 */       this.absolutePos = 0;
/*       */     }
/*  1517 */     return bool;
/*       */   }
/*       */ 
/*       */   public void close()
/*       */     throws SQLException
/*       */   {
/*  1532 */     this.cursorPos = 0;
/*  1533 */     this.absolutePos = 0;
/*  1534 */     this.numRows = 0;
/*  1535 */     this.numDeleted = 0;
/*       */ 
/*  1539 */     initProperties();
/*       */ 
/*  1542 */     this.rvh.clear();
/*       */   }
/*       */ 
/*       */   public boolean wasNull()
/*       */     throws SQLException
/*       */   {
/*  1560 */     return this.lastValueNull;
/*       */   }
/*       */ 
/*       */   private void setLastValueNull(boolean paramBoolean)
/*       */   {
/*  1572 */     this.lastValueNull = paramBoolean;
/*       */   }
/*       */ 
/*       */   private void checkIndex(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  1593 */     if ((paramInt < 1) || (paramInt > this.RowSetMD.getColumnCount()))
/*  1594 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcol").toString());
/*       */   }
/*       */ 
/*       */   private void checkCursor()
/*       */     throws SQLException
/*       */   {
/*  1611 */     if ((isAfterLast() == true) || (isBeforeFirst() == true))
/*  1612 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
/*       */   }
/*       */ 
/*       */   private int getColIdxByName(String paramString)
/*       */     throws SQLException
/*       */   {
/*  1628 */     this.RowSetMD = ((RowSetMetaDataImpl)getMetaData());
/*  1629 */     int i = this.RowSetMD.getColumnCount();
/*       */ 
/*  1631 */     for (int j = 1; j <= i; j++) {
/*  1632 */       String str = this.RowSetMD.getColumnName(j);
/*  1633 */       if ((str != null) && 
/*  1634 */         (paramString.equalsIgnoreCase(str))) {
/*  1635 */         return j;
/*       */       }
/*       */     }
/*       */ 
/*  1639 */     throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalcolnm").toString());
/*       */   }
/*       */ 
/*       */   protected BaseRow getCurrentRow()
/*       */   {
/*  1651 */     if (this.onInsertRow == true) {
/*  1652 */       return this.insertRow;
/*       */     }
/*  1654 */     return (BaseRow)this.rvh.get(this.cursorPos - 1);
/*       */   }
/*       */ 
/*       */   protected void removeCurrentRow()
/*       */   {
/*  1668 */     ((Row)getCurrentRow()).setDeleted();
/*  1669 */     this.rvh.remove(this.cursorPos - 1);
/*  1670 */     this.numRows -= 1;
/*       */   }
/*       */ 
/*       */   public String getString(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  1696 */     checkIndex(paramInt);
/*       */ 
/*  1698 */     checkCursor();
/*       */ 
/*  1700 */     setLastValueNull(false);
/*  1701 */     Object localObject = getCurrentRow().getColumnObject(paramInt);
/*       */ 
/*  1704 */     if (localObject == null) {
/*  1705 */       setLastValueNull(true);
/*  1706 */       return null;
/*       */     }
/*       */ 
/*  1709 */     return localObject.toString(); } 
/*       */   public boolean getBoolean(int paramInt) throws SQLException { // Byte code:
/*       */     //   0: aload_0
/*       */     //   1: iload_1
/*       */     //   2: invokespecial 1387	com/sun/rowset/CachedRowSetImpl:checkIndex	(I)V
/*       */     //   5: aload_0
/*       */     //   6: invokespecial 1356	com/sun/rowset/CachedRowSetImpl:checkCursor	()V
/*       */     //   9: aload_0
/*       */     //   10: iconst_0
/*       */     //   11: invokespecial 1412	com/sun/rowset/CachedRowSetImpl:setLastValueNull	(Z)V
/*       */     //   14: aload_0
/*       */     //   15: invokevirtual 1416	com/sun/rowset/CachedRowSetImpl:getCurrentRow	()Lcom/sun/rowset/internal/BaseRow;
/*       */     //   18: iload_1
/*       */     //   19: invokevirtual 1474	com/sun/rowset/internal/BaseRow:getColumnObject	(I)Ljava/lang/Object;
/*       */     //   22: astore_2
/*       */     //   23: aload_2
/*       */     //   24: ifnonnull +10 -> 34
/*       */     //   27: aload_0
/*       */     //   28: iconst_1
/*       */     //   29: invokespecial 1412	com/sun/rowset/CachedRowSetImpl:setLastValueNull	(Z)V
/*       */     //   32: iconst_0
/*       */     //   33: ireturn
/*       */     //   34: aload_2
/*       */     //   35: instanceof 689
/*       */     //   38: ifeq +11 -> 49
/*       */     //   41: aload_2
/*       */     //   42: checkcast 689	java/lang/Boolean
/*       */     //   45: invokevirtual 1517	java/lang/Boolean:booleanValue	()Z
/*       */     //   48: ireturn
/*       */     //   49: new 695	java/lang/Double
/*       */     //   52: dup
/*       */     //   53: aload_2
/*       */     //   54: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   57: invokespecial 1531	java/lang/Double:<init>	(Ljava/lang/String;)V
/*       */     //   60: astore_3
/*       */     //   61: aload_3
/*       */     //   62: new 695	java/lang/Double
/*       */     //   65: dup
/*       */     //   66: dconst_0
/*       */     //   67: invokespecial 1529	java/lang/Double:<init>	(D)V
/*       */     //   70: invokevirtual 1530	java/lang/Double:compareTo	(Ljava/lang/Double;)I
/*       */     //   73: ifne +5 -> 78
/*       */     //   76: iconst_0
/*       */     //   77: ireturn
/*       */     //   78: iconst_1
/*       */     //   79: ireturn
/*       */     //   80: astore_3
/*       */     //   81: new 721	java/sql/SQLException
/*       */     //   84: dup
/*       */     //   85: aload_0
/*       */     //   86: getfield 1322	com/sun/rowset/CachedRowSetImpl:resBundle	Lcom/sun/rowset/JdbcRowSetResourceBundle;
/*       */     //   89: ldc_w 615
/*       */     //   92: invokevirtual 1472	com/sun/rowset/JdbcRowSetResourceBundle:handleGetObject	(Ljava/lang/String;)Ljava/lang/Object;
/*       */     //   95: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   98: iconst_2
/*       */     //   99: anewarray 702	java/lang/Object
/*       */     //   102: dup
/*       */     //   103: iconst_0
/*       */     //   104: aload_2
/*       */     //   105: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   108: invokevirtual 1559	java/lang/String:trim	()Ljava/lang/String;
/*       */     //   111: aastore
/*       */     //   112: dup
/*       */     //   113: iconst_1
/*       */     //   114: iload_1
/*       */     //   115: invokestatic 1538	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*       */     //   118: aastore
/*       */     //   119: invokestatic 1587	java/text/MessageFormat:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*       */     //   122: invokespecial 1577	java/sql/SQLException:<init>	(Ljava/lang/String;)V
/*       */     //   125: athrow
/*       */     //
/*       */     // Exception table:
/*       */     //   from	to	target	type
/*       */     //   49	77	80	java/lang/NumberFormatException
/*       */     //   78	79	80	java/lang/NumberFormatException } 
/*       */   public byte getByte(int paramInt) throws SQLException { // Byte code:
/*       */     //   0: aload_0
/*       */     //   1: iload_1
/*       */     //   2: invokespecial 1387	com/sun/rowset/CachedRowSetImpl:checkIndex	(I)V
/*       */     //   5: aload_0
/*       */     //   6: invokespecial 1356	com/sun/rowset/CachedRowSetImpl:checkCursor	()V
/*       */     //   9: aload_0
/*       */     //   10: iconst_0
/*       */     //   11: invokespecial 1412	com/sun/rowset/CachedRowSetImpl:setLastValueNull	(Z)V
/*       */     //   14: aload_0
/*       */     //   15: invokevirtual 1416	com/sun/rowset/CachedRowSetImpl:getCurrentRow	()Lcom/sun/rowset/internal/BaseRow;
/*       */     //   18: iload_1
/*       */     //   19: invokevirtual 1474	com/sun/rowset/internal/BaseRow:getColumnObject	(I)Ljava/lang/Object;
/*       */     //   22: astore_2
/*       */     //   23: aload_2
/*       */     //   24: ifnonnull +10 -> 34
/*       */     //   27: aload_0
/*       */     //   28: iconst_1
/*       */     //   29: invokespecial 1412	com/sun/rowset/CachedRowSetImpl:setLastValueNull	(Z)V
/*       */     //   32: iconst_0
/*       */     //   33: ireturn
/*       */     //   34: aload_2
/*       */     //   35: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   38: invokestatic 1522	java/lang/Byte:valueOf	(Ljava/lang/String;)Ljava/lang/Byte;
/*       */     //   41: invokevirtual 1520	java/lang/Byte:byteValue	()B
/*       */     //   44: ireturn
/*       */     //   45: astore_3
/*       */     //   46: new 721	java/sql/SQLException
/*       */     //   49: dup
/*       */     //   50: aload_0
/*       */     //   51: getfield 1322	com/sun/rowset/CachedRowSetImpl:resBundle	Lcom/sun/rowset/JdbcRowSetResourceBundle;
/*       */     //   54: ldc_w 616
/*       */     //   57: invokevirtual 1472	com/sun/rowset/JdbcRowSetResourceBundle:handleGetObject	(Ljava/lang/String;)Ljava/lang/Object;
/*       */     //   60: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   63: iconst_2
/*       */     //   64: anewarray 702	java/lang/Object
/*       */     //   67: dup
/*       */     //   68: iconst_0
/*       */     //   69: aload_2
/*       */     //   70: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   73: invokevirtual 1559	java/lang/String:trim	()Ljava/lang/String;
/*       */     //   76: aastore
/*       */     //   77: dup
/*       */     //   78: iconst_1
/*       */     //   79: iload_1
/*       */     //   80: invokestatic 1538	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*       */     //   83: aastore
/*       */     //   84: invokestatic 1587	java/text/MessageFormat:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*       */     //   87: invokespecial 1577	java/sql/SQLException:<init>	(Ljava/lang/String;)V
/*       */     //   90: athrow
/*       */     //
/*       */     // Exception table:
/*       */     //   from	to	target	type
/*       */     //   34	44	45	java/lang/NumberFormatException } 
/*       */   public short getShort(int paramInt) throws SQLException { // Byte code:
/*       */     //   0: aload_0
/*       */     //   1: iload_1
/*       */     //   2: invokespecial 1387	com/sun/rowset/CachedRowSetImpl:checkIndex	(I)V
/*       */     //   5: aload_0
/*       */     //   6: invokespecial 1356	com/sun/rowset/CachedRowSetImpl:checkCursor	()V
/*       */     //   9: aload_0
/*       */     //   10: iconst_0
/*       */     //   11: invokespecial 1412	com/sun/rowset/CachedRowSetImpl:setLastValueNull	(Z)V
/*       */     //   14: aload_0
/*       */     //   15: invokevirtual 1416	com/sun/rowset/CachedRowSetImpl:getCurrentRow	()Lcom/sun/rowset/internal/BaseRow;
/*       */     //   18: iload_1
/*       */     //   19: invokevirtual 1474	com/sun/rowset/internal/BaseRow:getColumnObject	(I)Ljava/lang/Object;
/*       */     //   22: astore_2
/*       */     //   23: aload_2
/*       */     //   24: ifnonnull +10 -> 34
/*       */     //   27: aload_0
/*       */     //   28: iconst_1
/*       */     //   29: invokespecial 1412	com/sun/rowset/CachedRowSetImpl:setLastValueNull	(Z)V
/*       */     //   32: iconst_0
/*       */     //   33: ireturn
/*       */     //   34: aload_2
/*       */     //   35: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   38: invokevirtual 1559	java/lang/String:trim	()Ljava/lang/String;
/*       */     //   41: invokestatic 1553	java/lang/Short:valueOf	(Ljava/lang/String;)Ljava/lang/Short;
/*       */     //   44: invokevirtual 1551	java/lang/Short:shortValue	()S
/*       */     //   47: ireturn
/*       */     //   48: astore_3
/*       */     //   49: new 721	java/sql/SQLException
/*       */     //   52: dup
/*       */     //   53: aload_0
/*       */     //   54: getfield 1322	com/sun/rowset/CachedRowSetImpl:resBundle	Lcom/sun/rowset/JdbcRowSetResourceBundle;
/*       */     //   57: ldc_w 644
/*       */     //   60: invokevirtual 1472	com/sun/rowset/JdbcRowSetResourceBundle:handleGetObject	(Ljava/lang/String;)Ljava/lang/Object;
/*       */     //   63: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   66: iconst_2
/*       */     //   67: anewarray 702	java/lang/Object
/*       */     //   70: dup
/*       */     //   71: iconst_0
/*       */     //   72: aload_2
/*       */     //   73: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   76: invokevirtual 1559	java/lang/String:trim	()Ljava/lang/String;
/*       */     //   79: aastore
/*       */     //   80: dup
/*       */     //   81: iconst_1
/*       */     //   82: iload_1
/*       */     //   83: invokestatic 1538	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*       */     //   86: aastore
/*       */     //   87: invokestatic 1587	java/text/MessageFormat:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*       */     //   90: invokespecial 1577	java/sql/SQLException:<init>	(Ljava/lang/String;)V
/*       */     //   93: athrow
/*       */     //
/*       */     // Exception table:
/*       */     //   from	to	target	type
/*       */     //   34	47	48	java/lang/NumberFormatException } 
/*       */   public int getInt(int paramInt) throws SQLException { // Byte code:
/*       */     //   0: aload_0
/*       */     //   1: iload_1
/*       */     //   2: invokespecial 1387	com/sun/rowset/CachedRowSetImpl:checkIndex	(I)V
/*       */     //   5: aload_0
/*       */     //   6: invokespecial 1356	com/sun/rowset/CachedRowSetImpl:checkCursor	()V
/*       */     //   9: aload_0
/*       */     //   10: iconst_0
/*       */     //   11: invokespecial 1412	com/sun/rowset/CachedRowSetImpl:setLastValueNull	(Z)V
/*       */     //   14: aload_0
/*       */     //   15: invokevirtual 1416	com/sun/rowset/CachedRowSetImpl:getCurrentRow	()Lcom/sun/rowset/internal/BaseRow;
/*       */     //   18: iload_1
/*       */     //   19: invokevirtual 1474	com/sun/rowset/internal/BaseRow:getColumnObject	(I)Ljava/lang/Object;
/*       */     //   22: astore_2
/*       */     //   23: aload_2
/*       */     //   24: ifnonnull +10 -> 34
/*       */     //   27: aload_0
/*       */     //   28: iconst_1
/*       */     //   29: invokespecial 1412	com/sun/rowset/CachedRowSetImpl:setLastValueNull	(Z)V
/*       */     //   32: iconst_0
/*       */     //   33: ireturn
/*       */     //   34: aload_2
/*       */     //   35: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   38: invokevirtual 1559	java/lang/String:trim	()Ljava/lang/String;
/*       */     //   41: invokestatic 1542	java/lang/Integer:valueOf	(Ljava/lang/String;)Ljava/lang/Integer;
/*       */     //   44: invokevirtual 1537	java/lang/Integer:intValue	()I
/*       */     //   47: ireturn
/*       */     //   48: astore_3
/*       */     //   49: new 721	java/sql/SQLException
/*       */     //   52: dup
/*       */     //   53: aload_0
/*       */     //   54: getfield 1322	com/sun/rowset/CachedRowSetImpl:resBundle	Lcom/sun/rowset/JdbcRowSetResourceBundle;
/*       */     //   57: ldc_w 625
/*       */     //   60: invokevirtual 1472	com/sun/rowset/JdbcRowSetResourceBundle:handleGetObject	(Ljava/lang/String;)Ljava/lang/Object;
/*       */     //   63: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   66: iconst_2
/*       */     //   67: anewarray 702	java/lang/Object
/*       */     //   70: dup
/*       */     //   71: iconst_0
/*       */     //   72: aload_2
/*       */     //   73: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   76: invokevirtual 1559	java/lang/String:trim	()Ljava/lang/String;
/*       */     //   79: aastore
/*       */     //   80: dup
/*       */     //   81: iconst_1
/*       */     //   82: iload_1
/*       */     //   83: invokestatic 1538	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*       */     //   86: aastore
/*       */     //   87: invokestatic 1587	java/text/MessageFormat:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*       */     //   90: invokespecial 1577	java/sql/SQLException:<init>	(Ljava/lang/String;)V
/*       */     //   93: athrow
/*       */     //
/*       */     // Exception table:
/*       */     //   from	to	target	type
/*       */     //   34	47	48	java/lang/NumberFormatException } 
/*       */   public long getLong(int paramInt) throws SQLException { // Byte code:
/*       */     //   0: aload_0
/*       */     //   1: iload_1
/*       */     //   2: invokespecial 1387	com/sun/rowset/CachedRowSetImpl:checkIndex	(I)V
/*       */     //   5: aload_0
/*       */     //   6: invokespecial 1356	com/sun/rowset/CachedRowSetImpl:checkCursor	()V
/*       */     //   9: aload_0
/*       */     //   10: iconst_0
/*       */     //   11: invokespecial 1412	com/sun/rowset/CachedRowSetImpl:setLastValueNull	(Z)V
/*       */     //   14: aload_0
/*       */     //   15: invokevirtual 1416	com/sun/rowset/CachedRowSetImpl:getCurrentRow	()Lcom/sun/rowset/internal/BaseRow;
/*       */     //   18: iload_1
/*       */     //   19: invokevirtual 1474	com/sun/rowset/internal/BaseRow:getColumnObject	(I)Ljava/lang/Object;
/*       */     //   22: astore_2
/*       */     //   23: aload_2
/*       */     //   24: ifnonnull +10 -> 34
/*       */     //   27: aload_0
/*       */     //   28: iconst_1
/*       */     //   29: invokespecial 1412	com/sun/rowset/CachedRowSetImpl:setLastValueNull	(Z)V
/*       */     //   32: lconst_0
/*       */     //   33: lreturn
/*       */     //   34: aload_2
/*       */     //   35: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   38: invokevirtual 1559	java/lang/String:trim	()Ljava/lang/String;
/*       */     //   41: invokestatic 1545	java/lang/Long:valueOf	(Ljava/lang/String;)Ljava/lang/Long;
/*       */     //   44: invokevirtual 1543	java/lang/Long:longValue	()J
/*       */     //   47: lreturn
/*       */     //   48: astore_3
/*       */     //   49: new 721	java/sql/SQLException
/*       */     //   52: dup
/*       */     //   53: aload_0
/*       */     //   54: getfield 1322	com/sun/rowset/CachedRowSetImpl:resBundle	Lcom/sun/rowset/JdbcRowSetResourceBundle;
/*       */     //   57: ldc_w 628
/*       */     //   60: invokevirtual 1472	com/sun/rowset/JdbcRowSetResourceBundle:handleGetObject	(Ljava/lang/String;)Ljava/lang/Object;
/*       */     //   63: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   66: iconst_2
/*       */     //   67: anewarray 702	java/lang/Object
/*       */     //   70: dup
/*       */     //   71: iconst_0
/*       */     //   72: aload_2
/*       */     //   73: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   76: invokevirtual 1559	java/lang/String:trim	()Ljava/lang/String;
/*       */     //   79: aastore
/*       */     //   80: dup
/*       */     //   81: iconst_1
/*       */     //   82: iload_1
/*       */     //   83: invokestatic 1538	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*       */     //   86: aastore
/*       */     //   87: invokestatic 1587	java/text/MessageFormat:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*       */     //   90: invokespecial 1577	java/sql/SQLException:<init>	(Ljava/lang/String;)V
/*       */     //   93: athrow
/*       */     //
/*       */     // Exception table:
/*       */     //   from	to	target	type
/*       */     //   34	47	48	java/lang/NumberFormatException } 
/*       */   public float getFloat(int paramInt) throws SQLException { // Byte code:
/*       */     //   0: aload_0
/*       */     //   1: iload_1
/*       */     //   2: invokespecial 1387	com/sun/rowset/CachedRowSetImpl:checkIndex	(I)V
/*       */     //   5: aload_0
/*       */     //   6: invokespecial 1356	com/sun/rowset/CachedRowSetImpl:checkCursor	()V
/*       */     //   9: aload_0
/*       */     //   10: iconst_0
/*       */     //   11: invokespecial 1412	com/sun/rowset/CachedRowSetImpl:setLastValueNull	(Z)V
/*       */     //   14: aload_0
/*       */     //   15: invokevirtual 1416	com/sun/rowset/CachedRowSetImpl:getCurrentRow	()Lcom/sun/rowset/internal/BaseRow;
/*       */     //   18: iload_1
/*       */     //   19: invokevirtual 1474	com/sun/rowset/internal/BaseRow:getColumnObject	(I)Ljava/lang/Object;
/*       */     //   22: astore_2
/*       */     //   23: aload_2
/*       */     //   24: ifnonnull +10 -> 34
/*       */     //   27: aload_0
/*       */     //   28: iconst_1
/*       */     //   29: invokespecial 1412	com/sun/rowset/CachedRowSetImpl:setLastValueNull	(Z)V
/*       */     //   32: fconst_0
/*       */     //   33: freturn
/*       */     //   34: new 696	java/lang/Float
/*       */     //   37: dup
/*       */     //   38: aload_2
/*       */     //   39: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   42: invokespecial 1534	java/lang/Float:<init>	(Ljava/lang/String;)V
/*       */     //   45: invokevirtual 1532	java/lang/Float:floatValue	()F
/*       */     //   48: freturn
/*       */     //   49: astore_3
/*       */     //   50: new 721	java/sql/SQLException
/*       */     //   53: dup
/*       */     //   54: aload_0
/*       */     //   55: getfield 1322	com/sun/rowset/CachedRowSetImpl:resBundle	Lcom/sun/rowset/JdbcRowSetResourceBundle;
/*       */     //   58: ldc_w 623
/*       */     //   61: invokevirtual 1472	com/sun/rowset/JdbcRowSetResourceBundle:handleGetObject	(Ljava/lang/String;)Ljava/lang/Object;
/*       */     //   64: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   67: iconst_2
/*       */     //   68: anewarray 702	java/lang/Object
/*       */     //   71: dup
/*       */     //   72: iconst_0
/*       */     //   73: aload_2
/*       */     //   74: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   77: invokevirtual 1559	java/lang/String:trim	()Ljava/lang/String;
/*       */     //   80: aastore
/*       */     //   81: dup
/*       */     //   82: iconst_1
/*       */     //   83: iload_1
/*       */     //   84: invokestatic 1538	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*       */     //   87: aastore
/*       */     //   88: invokestatic 1587	java/text/MessageFormat:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*       */     //   91: invokespecial 1577	java/sql/SQLException:<init>	(Ljava/lang/String;)V
/*       */     //   94: athrow
/*       */     //
/*       */     // Exception table:
/*       */     //   from	to	target	type
/*       */     //   34	48	49	java/lang/NumberFormatException } 
/*       */   public double getDouble(int paramInt) throws SQLException { // Byte code:
/*       */     //   0: aload_0
/*       */     //   1: iload_1
/*       */     //   2: invokespecial 1387	com/sun/rowset/CachedRowSetImpl:checkIndex	(I)V
/*       */     //   5: aload_0
/*       */     //   6: invokespecial 1356	com/sun/rowset/CachedRowSetImpl:checkCursor	()V
/*       */     //   9: aload_0
/*       */     //   10: iconst_0
/*       */     //   11: invokespecial 1412	com/sun/rowset/CachedRowSetImpl:setLastValueNull	(Z)V
/*       */     //   14: aload_0
/*       */     //   15: invokevirtual 1416	com/sun/rowset/CachedRowSetImpl:getCurrentRow	()Lcom/sun/rowset/internal/BaseRow;
/*       */     //   18: iload_1
/*       */     //   19: invokevirtual 1474	com/sun/rowset/internal/BaseRow:getColumnObject	(I)Ljava/lang/Object;
/*       */     //   22: astore_2
/*       */     //   23: aload_2
/*       */     //   24: ifnonnull +10 -> 34
/*       */     //   27: aload_0
/*       */     //   28: iconst_1
/*       */     //   29: invokespecial 1412	com/sun/rowset/CachedRowSetImpl:setLastValueNull	(Z)V
/*       */     //   32: dconst_0
/*       */     //   33: dreturn
/*       */     //   34: new 695	java/lang/Double
/*       */     //   37: dup
/*       */     //   38: aload_2
/*       */     //   39: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   42: invokevirtual 1559	java/lang/String:trim	()Ljava/lang/String;
/*       */     //   45: invokespecial 1531	java/lang/Double:<init>	(Ljava/lang/String;)V
/*       */     //   48: invokevirtual 1528	java/lang/Double:doubleValue	()D
/*       */     //   51: dreturn
/*       */     //   52: astore_3
/*       */     //   53: new 721	java/sql/SQLException
/*       */     //   56: dup
/*       */     //   57: aload_0
/*       */     //   58: getfield 1322	com/sun/rowset/CachedRowSetImpl:resBundle	Lcom/sun/rowset/JdbcRowSetResourceBundle;
/*       */     //   61: ldc_w 618
/*       */     //   64: invokevirtual 1472	com/sun/rowset/JdbcRowSetResourceBundle:handleGetObject	(Ljava/lang/String;)Ljava/lang/Object;
/*       */     //   67: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   70: iconst_2
/*       */     //   71: anewarray 702	java/lang/Object
/*       */     //   74: dup
/*       */     //   75: iconst_0
/*       */     //   76: aload_2
/*       */     //   77: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   80: invokevirtual 1559	java/lang/String:trim	()Ljava/lang/String;
/*       */     //   83: aastore
/*       */     //   84: dup
/*       */     //   85: iconst_1
/*       */     //   86: iload_1
/*       */     //   87: invokestatic 1538	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*       */     //   90: aastore
/*       */     //   91: invokestatic 1587	java/text/MessageFormat:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*       */     //   94: invokespecial 1577	java/sql/SQLException:<init>	(Ljava/lang/String;)V
/*       */     //   97: athrow
/*       */     //
/*       */     // Exception table:
/*       */     //   from	to	target	type
/*       */     //   34	51	52	java/lang/NumberFormatException } 
/*  2050 */   /** @deprecated */
/*       */   public BigDecimal getBigDecimal(int paramInt1, int paramInt2) throws SQLException { checkIndex(paramInt1);
/*       */ 
/*  2052 */     checkCursor();
/*       */ 
/*  2054 */     setLastValueNull(false);
/*  2055 */     Object localObject = getCurrentRow().getColumnObject(paramInt1);
/*       */ 
/*  2058 */     if (localObject == null) {
/*  2059 */       setLastValueNull(true);
/*  2060 */       return new BigDecimal(0);
/*       */     }
/*       */ 
/*  2063 */     BigDecimal localBigDecimal1 = getBigDecimal(paramInt1);
/*       */ 
/*  2065 */     BigDecimal localBigDecimal2 = localBigDecimal1.setScale(paramInt2);
/*       */ 
/*  2067 */     return localBigDecimal2;
/*       */   }
/*       */ 
/*       */   public byte[] getBytes(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  2092 */     checkIndex(paramInt);
/*       */ 
/*  2094 */     checkCursor();
/*       */ 
/*  2096 */     if (!isBinary(this.RowSetMD.getColumnType(paramInt))) {
/*  2097 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */     }
/*       */ 
/*  2100 */     return (byte[])getCurrentRow().getColumnObject(paramInt);
/*       */   }
/*       */ 
/*       */   public java.sql.Date getDate(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  2121 */     checkIndex(paramInt);
/*       */ 
/*  2123 */     checkCursor();
/*       */ 
/*  2125 */     setLastValueNull(false);
/*  2126 */     Object localObject = getCurrentRow().getColumnObject(paramInt);
/*       */ 
/*  2129 */     if (localObject == null) {
/*  2130 */       setLastValueNull(true);
/*  2131 */       return null;
/*       */     }
/*       */     long l;
/*  2141 */     switch (this.RowSetMD.getColumnType(paramInt)) {
/*       */     case 91:
/*  2143 */       l = ((java.sql.Date)localObject).getTime();
/*  2144 */       return new java.sql.Date(l);
/*       */     case 93:
/*  2147 */       l = ((Timestamp)localObject).getTime();
/*  2148 */       return new java.sql.Date(l);
/*       */     case -1:
/*       */     case 1:
/*       */     case 12:
/*       */       try
/*       */       {
/*  2154 */         DateFormat localDateFormat = DateFormat.getDateInstance();
/*  2155 */         return (java.sql.Date)localDateFormat.parse(localObject.toString());
/*       */       } catch (ParseException localParseException) {
/*  2157 */         throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.datefail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
/*       */       }
/*       */ 
/*       */     }
/*       */ 
/*  2162 */     throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.datefail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
/*       */   }
/*       */ 
/*       */   public Time getTime(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  2185 */     checkIndex(paramInt);
/*       */ 
/*  2187 */     checkCursor();
/*       */ 
/*  2189 */     setLastValueNull(false);
/*  2190 */     Object localObject = getCurrentRow().getColumnObject(paramInt);
/*       */ 
/*  2193 */     if (localObject == null) {
/*  2194 */       setLastValueNull(true);
/*  2195 */       return null;
/*       */     }
/*       */ 
/*  2205 */     switch (this.RowSetMD.getColumnType(paramInt)) {
/*       */     case 92:
/*  2207 */       return (Time)localObject;
/*       */     case 93:
/*  2210 */       long l = ((Timestamp)localObject).getTime();
/*  2211 */       return new Time(l);
/*       */     case -1:
/*       */     case 1:
/*       */     case 12:
/*       */       try
/*       */       {
/*  2217 */         DateFormat localDateFormat = DateFormat.getTimeInstance();
/*  2218 */         return (Time)localDateFormat.parse(localObject.toString());
/*       */       } catch (ParseException localParseException) {
/*  2220 */         throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
/*       */       }
/*       */ 
/*       */     }
/*       */ 
/*  2225 */     throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
/*       */   }
/*       */ 
/*       */   public Timestamp getTimestamp(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  2248 */     checkIndex(paramInt);
/*       */ 
/*  2250 */     checkCursor();
/*       */ 
/*  2252 */     setLastValueNull(false);
/*  2253 */     Object localObject = getCurrentRow().getColumnObject(paramInt);
/*       */ 
/*  2256 */     if (localObject == null) {
/*  2257 */       setLastValueNull(true);
/*  2258 */       return null;
/*       */     }
/*       */     long l;
/*  2268 */     switch (this.RowSetMD.getColumnType(paramInt)) {
/*       */     case 93:
/*  2270 */       return (Timestamp)localObject;
/*       */     case 92:
/*  2273 */       l = ((Time)localObject).getTime();
/*  2274 */       return new Timestamp(l);
/*       */     case 91:
/*  2277 */       l = ((java.sql.Date)localObject).getTime();
/*  2278 */       return new Timestamp(l);
/*       */     case -1:
/*       */     case 1:
/*       */     case 12:
/*       */       try
/*       */       {
/*  2284 */         DateFormat localDateFormat = DateFormat.getTimeInstance();
/*  2285 */         return (Timestamp)localDateFormat.parse(localObject.toString());
/*       */       } catch (ParseException localParseException) {
/*  2287 */         throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
/*       */       }
/*       */ 
/*       */     }
/*       */ 
/*  2292 */     throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
/*       */   }
/*       */ 
/*       */   public InputStream getAsciiStream(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  2333 */     this.asciiStream = null;
/*       */ 
/*  2336 */     checkIndex(paramInt);
/*       */ 
/*  2338 */     checkCursor();
/*       */ 
/*  2340 */     Object localObject = getCurrentRow().getColumnObject(paramInt);
/*  2341 */     if (localObject == null) {
/*  2342 */       this.lastValueNull = true;
/*  2343 */       return null;
/*       */     }
/*       */     try
/*       */     {
/*  2347 */       if (isString(this.RowSetMD.getColumnType(paramInt)))
/*  2348 */         this.asciiStream = new ByteArrayInputStream(((String)localObject).getBytes("ASCII"));
/*       */       else
/*  2350 */         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */     }
/*       */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*  2353 */       throw new SQLException(localUnsupportedEncodingException.getMessage());
/*       */     }
/*       */ 
/*  2356 */     return this.asciiStream;
/*       */   }
/*       */ 
/*       */   /** @deprecated */
/*       */   public InputStream getUnicodeStream(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  2382 */     this.unicodeStream = null;
/*       */ 
/*  2385 */     checkIndex(paramInt);
/*       */ 
/*  2387 */     checkCursor();
/*       */ 
/*  2389 */     if ((!isBinary(this.RowSetMD.getColumnType(paramInt))) && (!isString(this.RowSetMD.getColumnType(paramInt))))
/*       */     {
/*  2391 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */     }
/*       */ 
/*  2394 */     Object localObject = getCurrentRow().getColumnObject(paramInt);
/*  2395 */     if (localObject == null) {
/*  2396 */       this.lastValueNull = true;
/*  2397 */       return null;
/*       */     }
/*       */ 
/*  2400 */     this.unicodeStream = new StringBufferInputStream(localObject.toString());
/*       */ 
/*  2402 */     return this.unicodeStream;
/*       */   }
/*       */ 
/*       */   public InputStream getBinaryStream(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  2438 */     this.binaryStream = null;
/*       */ 
/*  2441 */     checkIndex(paramInt);
/*       */ 
/*  2443 */     checkCursor();
/*       */ 
/*  2445 */     if (!isBinary(this.RowSetMD.getColumnType(paramInt))) {
/*  2446 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */     }
/*       */ 
/*  2449 */     Object localObject = getCurrentRow().getColumnObject(paramInt);
/*  2450 */     if (localObject == null) {
/*  2451 */       this.lastValueNull = true;
/*  2452 */       return null;
/*       */     }
/*       */ 
/*  2455 */     this.binaryStream = new ByteArrayInputStream((byte[])localObject);
/*       */ 
/*  2457 */     return this.binaryStream;
/*       */   }
/*       */ 
/*       */   public String getString(String paramString)
/*       */     throws SQLException
/*       */   {
/*  2481 */     return getString(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public boolean getBoolean(String paramString)
/*       */     throws SQLException
/*       */   {
/*  2500 */     return getBoolean(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public byte getByte(String paramString)
/*       */     throws SQLException
/*       */   {
/*  2520 */     return getByte(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public short getShort(String paramString)
/*       */     throws SQLException
/*       */   {
/*  2541 */     return getShort(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public int getInt(String paramString)
/*       */     throws SQLException
/*       */   {
/*  2562 */     return getInt(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public long getLong(String paramString)
/*       */     throws SQLException
/*       */   {
/*  2583 */     return getLong(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public float getFloat(String paramString)
/*       */     throws SQLException
/*       */   {
/*  2604 */     return getFloat(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public double getDouble(String paramString)
/*       */     throws SQLException
/*       */   {
/*  2626 */     return getDouble(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   /** @deprecated */
/*       */   public BigDecimal getBigDecimal(String paramString, int paramInt)
/*       */     throws SQLException
/*       */   {
/*  2650 */     return getBigDecimal(getColIdxByName(paramString), paramInt);
/*       */   }
/*       */ 
/*       */   public byte[] getBytes(String paramString)
/*       */     throws SQLException
/*       */   {
/*  2671 */     return getBytes(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public java.sql.Date getDate(String paramString)
/*       */     throws SQLException
/*       */   {
/*  2689 */     return getDate(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public Time getTime(String paramString)
/*       */     throws SQLException
/*       */   {
/*  2705 */     return getTime(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public Timestamp getTimestamp(String paramString)
/*       */     throws SQLException
/*       */   {
/*  2721 */     return getTimestamp(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public InputStream getAsciiStream(String paramString)
/*       */     throws SQLException
/*       */   {
/*  2755 */     return getAsciiStream(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   /** @deprecated */
/*       */   public InputStream getUnicodeStream(String paramString)
/*       */     throws SQLException
/*       */   {
/*  2781 */     return getUnicodeStream(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public InputStream getBinaryStream(String paramString)
/*       */     throws SQLException
/*       */   {
/*  2814 */     return getBinaryStream(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public SQLWarning getWarnings()
/*       */   {
/*  2836 */     return this.sqlwarn;
/*       */   }
/*       */ 
/*       */   public void clearWarnings()
/*       */   {
/*  2846 */     this.sqlwarn = null;
/*       */   }
/*       */ 
/*       */   public String getCursorName()
/*       */     throws SQLException
/*       */   {
/*  2874 */     throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.posupdate").toString());
/*       */   }
/*       */ 
/*       */   public ResultSetMetaData getMetaData()
/*       */     throws SQLException
/*       */   {
/*  2904 */     return this.RowSetMD;
/*       */   }
/*       */ 
/*       */   public Object getObject(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  2945 */     checkIndex(paramInt);
/*       */ 
/*  2947 */     checkCursor();
/*       */ 
/*  2949 */     setLastValueNull(false);
/*  2950 */     Object localObject = getCurrentRow().getColumnObject(paramInt);
/*       */ 
/*  2953 */     if (localObject == null) {
/*  2954 */       setLastValueNull(true);
/*  2955 */       return null;
/*       */     }
/*  2957 */     if ((localObject instanceof Struct)) {
/*  2958 */       Struct localStruct = (Struct)localObject;
/*  2959 */       Map localMap = getTypeMap();
/*       */ 
/*  2961 */       Class localClass = (Class)localMap.get(localStruct.getSQLTypeName());
/*  2962 */       if (localClass != null)
/*       */       {
/*  2964 */         SQLData localSQLData = null;
/*       */         try {
/*  2966 */           localSQLData = (SQLData)localClass.newInstance();
/*       */         } catch (InstantiationException localInstantiationException) {
/*  2968 */           throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.unableins").toString(), new Object[] { localInstantiationException.getMessage() }));
/*       */         }
/*       */         catch (IllegalAccessException localIllegalAccessException) {
/*  2971 */           throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.unableins").toString(), new Object[] { localIllegalAccessException.getMessage() }));
/*       */         }
/*       */ 
/*  2975 */         Object[] arrayOfObject = localStruct.getAttributes(localMap);
/*       */ 
/*  2977 */         SQLInputImpl localSQLInputImpl = new SQLInputImpl(arrayOfObject, localMap);
/*       */ 
/*  2979 */         localSQLData.readSQL(localSQLInputImpl, localStruct.getSQLTypeName());
/*  2980 */         return localSQLData;
/*       */       }
/*       */     }
/*  2983 */     return localObject;
/*       */   }
/*       */ 
/*       */   public Object getObject(String paramString)
/*       */     throws SQLException
/*       */   {
/*  3019 */     return getObject(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public int findColumn(String paramString)
/*       */     throws SQLException
/*       */   {
/*  3035 */     return getColIdxByName(paramString);
/*       */   }
/*       */ 
/*       */   public Reader getCharacterStream(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  3071 */     checkIndex(paramInt);
/*       */ 
/*  3073 */     checkCursor();
/*       */     Object localObject;
/*  3075 */     if (isBinary(this.RowSetMD.getColumnType(paramInt))) {
/*  3076 */       localObject = getCurrentRow().getColumnObject(paramInt);
/*  3077 */       if (localObject == null) {
/*  3078 */         this.lastValueNull = true;
/*  3079 */         return null;
/*       */       }
/*  3081 */       this.charStream = new InputStreamReader(new ByteArrayInputStream((byte[])localObject));
/*       */     }
/*  3083 */     else if (isString(this.RowSetMD.getColumnType(paramInt))) {
/*  3084 */       localObject = getCurrentRow().getColumnObject(paramInt);
/*  3085 */       if (localObject == null) {
/*  3086 */         this.lastValueNull = true;
/*  3087 */         return null;
/*       */       }
/*  3089 */       this.charStream = new StringReader(localObject.toString());
/*       */     } else {
/*  3091 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */     }
/*       */ 
/*  3094 */     return this.charStream;
/*       */   }
/*       */ 
/*       */   public Reader getCharacterStream(String paramString)
/*       */     throws SQLException
/*       */   {
/*  3118 */     return getCharacterStream(getColIdxByName(paramString)); } 
/*       */   public BigDecimal getBigDecimal(int paramInt) throws SQLException { // Byte code:
/*       */     //   0: aload_0
/*       */     //   1: iload_1
/*       */     //   2: invokespecial 1387	com/sun/rowset/CachedRowSetImpl:checkIndex	(I)V
/*       */     //   5: aload_0
/*       */     //   6: invokespecial 1356	com/sun/rowset/CachedRowSetImpl:checkCursor	()V
/*       */     //   9: aload_0
/*       */     //   10: iconst_0
/*       */     //   11: invokespecial 1412	com/sun/rowset/CachedRowSetImpl:setLastValueNull	(Z)V
/*       */     //   14: aload_0
/*       */     //   15: invokevirtual 1416	com/sun/rowset/CachedRowSetImpl:getCurrentRow	()Lcom/sun/rowset/internal/BaseRow;
/*       */     //   18: iload_1
/*       */     //   19: invokevirtual 1474	com/sun/rowset/internal/BaseRow:getColumnObject	(I)Ljava/lang/Object;
/*       */     //   22: astore_2
/*       */     //   23: aload_2
/*       */     //   24: ifnonnull +10 -> 34
/*       */     //   27: aload_0
/*       */     //   28: iconst_1
/*       */     //   29: invokespecial 1412	com/sun/rowset/CachedRowSetImpl:setLastValueNull	(Z)V
/*       */     //   32: aconst_null
/*       */     //   33: areturn
/*       */     //   34: new 709	java/math/BigDecimal
/*       */     //   37: dup
/*       */     //   38: aload_2
/*       */     //   39: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   42: invokevirtual 1559	java/lang/String:trim	()Ljava/lang/String;
/*       */     //   45: invokespecial 1571	java/math/BigDecimal:<init>	(Ljava/lang/String;)V
/*       */     //   48: areturn
/*       */     //   49: astore_3
/*       */     //   50: new 721	java/sql/SQLException
/*       */     //   53: dup
/*       */     //   54: aload_0
/*       */     //   55: getfield 1322	com/sun/rowset/CachedRowSetImpl:resBundle	Lcom/sun/rowset/JdbcRowSetResourceBundle;
/*       */     //   58: ldc_w 618
/*       */     //   61: invokevirtual 1472	com/sun/rowset/JdbcRowSetResourceBundle:handleGetObject	(Ljava/lang/String;)Ljava/lang/Object;
/*       */     //   64: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   67: iconst_2
/*       */     //   68: anewarray 702	java/lang/Object
/*       */     //   71: dup
/*       */     //   72: iconst_0
/*       */     //   73: aload_2
/*       */     //   74: invokevirtual 1548	java/lang/Object:toString	()Ljava/lang/String;
/*       */     //   77: invokevirtual 1559	java/lang/String:trim	()Ljava/lang/String;
/*       */     //   80: aastore
/*       */     //   81: dup
/*       */     //   82: iconst_1
/*       */     //   83: iload_1
/*       */     //   84: invokestatic 1538	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*       */     //   87: aastore
/*       */     //   88: invokestatic 1587	java/text/MessageFormat:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*       */     //   91: invokespecial 1577	java/sql/SQLException:<init>	(Ljava/lang/String;)V
/*       */     //   94: athrow
/*       */     //
/*       */     // Exception table:
/*       */     //   from	to	target	type
/*       */     //   34	48	49	java/lang/NumberFormatException } 
/*  3184 */   public BigDecimal getBigDecimal(String paramString) throws SQLException { return getBigDecimal(getColIdxByName(paramString)); }
/*       */ 
/*       */ 
/*       */   public int size()
/*       */   {
/*  3197 */     return this.numRows;
/*       */   }
/*       */ 
/*       */   public boolean isBeforeFirst()
/*       */     throws SQLException
/*       */   {
/*  3209 */     if ((this.cursorPos == 0) && (this.numRows > 0)) {
/*  3210 */       return true;
/*       */     }
/*  3212 */     return false;
/*       */   }
/*       */ 
/*       */   public boolean isAfterLast()
/*       */     throws SQLException
/*       */   {
/*  3225 */     if ((this.cursorPos == this.numRows + 1) && (this.numRows > 0)) {
/*  3226 */       return true;
/*       */     }
/*  3228 */     return false;
/*       */   }
/*       */ 
/*       */   public boolean isFirst()
/*       */     throws SQLException
/*       */   {
/*  3242 */     int i = this.cursorPos;
/*  3243 */     int j = this.absolutePos;
/*  3244 */     internalFirst();
/*  3245 */     if (this.cursorPos == i) {
/*  3246 */       return true;
/*       */     }
/*  3248 */     this.cursorPos = i;
/*  3249 */     this.absolutePos = j;
/*  3250 */     return false;
/*       */   }
/*       */ 
/*       */   public boolean isLast()
/*       */     throws SQLException
/*       */   {
/*  3267 */     int i = this.cursorPos;
/*  3268 */     int j = this.absolutePos;
/*  3269 */     boolean bool = getShowDeleted();
/*  3270 */     setShowDeleted(true);
/*  3271 */     internalLast();
/*  3272 */     if (this.cursorPos == i) {
/*  3273 */       setShowDeleted(bool);
/*  3274 */       return true;
/*       */     }
/*  3276 */     setShowDeleted(bool);
/*  3277 */     this.cursorPos = i;
/*  3278 */     this.absolutePos = j;
/*  3279 */     return false;
/*       */   }
/*       */ 
/*       */   public void beforeFirst()
/*       */     throws SQLException
/*       */   {
/*  3292 */     if (getType() == 1003) {
/*  3293 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.beforefirst").toString());
/*       */     }
/*  3295 */     this.cursorPos = 0;
/*  3296 */     this.absolutePos = 0;
/*  3297 */     notifyCursorMoved();
/*       */   }
/*       */ 
/*       */   public void afterLast()
/*       */     throws SQLException
/*       */   {
/*  3308 */     if (this.numRows > 0) {
/*  3309 */       this.cursorPos = (this.numRows + 1);
/*  3310 */       this.absolutePos = 0;
/*  3311 */       notifyCursorMoved();
/*       */     }
/*       */   }
/*       */ 
/*       */   public boolean first()
/*       */     throws SQLException
/*       */   {
/*  3327 */     if (getType() == 1003) {
/*  3328 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.first").toString());
/*       */     }
/*       */ 
/*  3332 */     boolean bool = internalFirst();
/*  3333 */     notifyCursorMoved();
/*       */ 
/*  3335 */     return bool;
/*       */   }
/*       */ 
/*       */   protected boolean internalFirst()
/*       */     throws SQLException
/*       */   {
/*  3355 */     boolean bool = false;
/*       */ 
/*  3357 */     if (this.numRows > 0) {
/*  3358 */       this.cursorPos = 1;
/*  3359 */       if ((!getShowDeleted()) && (rowDeleted() == true))
/*  3360 */         bool = internalNext();
/*       */       else {
/*  3362 */         bool = true;
/*       */       }
/*       */     }
/*       */ 
/*  3366 */     if (bool == true)
/*  3367 */       this.absolutePos = 1;
/*       */     else {
/*  3369 */       this.absolutePos = 0;
/*       */     }
/*  3371 */     return bool;
/*       */   }
/*       */ 
/*       */   public boolean last()
/*       */     throws SQLException
/*       */   {
/*  3386 */     if (getType() == 1003) {
/*  3387 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.last").toString());
/*       */     }
/*       */ 
/*  3391 */     boolean bool = internalLast();
/*  3392 */     notifyCursorMoved();
/*       */ 
/*  3394 */     return bool;
/*       */   }
/*       */ 
/*       */   protected boolean internalLast()
/*       */     throws SQLException
/*       */   {
/*  3415 */     boolean bool = false;
/*       */ 
/*  3417 */     if (this.numRows > 0) {
/*  3418 */       this.cursorPos = this.numRows;
/*  3419 */       if ((!getShowDeleted()) && (rowDeleted() == true))
/*  3420 */         bool = internalPrevious();
/*       */       else {
/*  3422 */         bool = true;
/*       */       }
/*       */     }
/*  3425 */     if (bool == true)
/*  3426 */       this.absolutePos = (this.numRows - this.numDeleted);
/*       */     else
/*  3428 */       this.absolutePos = 0;
/*  3429 */     return bool;
/*       */   }
/*       */ 
/*       */   public int getRow()
/*       */     throws SQLException
/*       */   {
/*  3443 */     if ((this.numRows > 0) && (this.cursorPos > 0) && (this.cursorPos < this.numRows + 1) && (!getShowDeleted()) && (!rowDeleted()))
/*       */     {
/*  3447 */       return this.absolutePos;
/*  3448 */     }if (getShowDeleted() == true) {
/*  3449 */       return this.cursorPos;
/*       */     }
/*  3451 */     return 0;
/*       */   }
/*       */ 
/*       */   public boolean absolute(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  3503 */     if ((paramInt == 0) || (getType() == 1003)) {
/*  3504 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.absolute").toString());
/*       */     }
/*       */ 
/*  3507 */     if (paramInt > 0) {
/*  3508 */       if (paramInt > this.numRows)
/*       */       {
/*  3510 */         afterLast();
/*  3511 */         return false;
/*       */       }
/*  3513 */       if (this.absolutePos <= 0)
/*  3514 */         internalFirst();
/*       */     }
/*       */     else {
/*  3517 */       if (this.cursorPos + paramInt < 0)
/*       */       {
/*  3519 */         beforeFirst();
/*  3520 */         return false;
/*       */       }
/*  3522 */       if (this.absolutePos >= 0) {
/*  3523 */         internalLast();
/*       */       }
/*       */ 
/*       */     }
/*       */ 
/*  3528 */     while (this.absolutePos != paramInt) {
/*  3529 */       if (this.absolutePos < paramInt) {
/*  3530 */         if (!internalNext()) {
/*  3531 */           break;
/*       */         }
/*       */       }
/*  3534 */       else if (!internalPrevious()) {
/*  3535 */         break;
/*       */       }
/*       */     }
/*       */ 
/*  3539 */     notifyCursorMoved();
/*       */ 
/*  3541 */     if ((isAfterLast()) || (isBeforeFirst())) {
/*  3542 */       return false;
/*       */     }
/*  3544 */     return true;
/*       */   }
/*       */ 
/*       */   public boolean relative(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  3604 */     if ((this.numRows == 0) || (isBeforeFirst()) || (isAfterLast()) || (getType() == 1003))
/*       */     {
/*  3606 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.relative").toString());
/*       */     }
/*       */ 
/*  3609 */     if (paramInt == 0)
/*  3610 */       return true;
/*       */     int i;
/*  3613 */     if (paramInt > 0) {
/*  3614 */       if (this.cursorPos + paramInt > this.numRows)
/*       */       {
/*  3616 */         afterLast();
/*       */       }
/*       */       else {
/*  3618 */         for (i = 0; (i < paramInt) && 
/*  3619 */           (internalNext()); i++);
/*       */       }
/*       */ 
/*       */     }
/*  3624 */     else if (this.cursorPos + paramInt < 0)
/*       */     {
/*  3626 */       beforeFirst();
/*       */     }
/*       */     else
/*       */     {
/*  3628 */       for (i = paramInt; (i < 0) && 
/*  3629 */         (internalPrevious()); i++);
/*       */     }
/*       */ 
/*  3634 */     notifyCursorMoved();
/*       */ 
/*  3636 */     if ((isAfterLast()) || (isBeforeFirst())) {
/*  3637 */       return false;
/*       */     }
/*  3639 */     return true;
/*       */   }
/*       */ 
/*       */   public boolean previous()
/*       */     throws SQLException
/*       */   {
/*  3686 */     if (getType() == 1003) {
/*  3687 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.last").toString());
/*       */     }
/*       */ 
/*  3694 */     if ((this.cursorPos < 0) || (this.cursorPos > this.numRows + 1)) {
/*  3695 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
/*       */     }
/*       */ 
/*  3698 */     boolean bool = internalPrevious();
/*  3699 */     notifyCursorMoved();
/*       */ 
/*  3701 */     return bool;
/*       */   }
/*       */ 
/*       */   protected boolean internalPrevious()
/*       */     throws SQLException
/*       */   {
/*  3721 */     boolean bool = false;
/*       */     do
/*       */     {
/*  3724 */       if (this.cursorPos > 1) {
/*  3725 */         this.cursorPos -= 1;
/*  3726 */         bool = true;
/*  3727 */       } else if (this.cursorPos == 1)
/*       */       {
/*  3729 */         this.cursorPos -= 1;
/*  3730 */         bool = false;
/*  3731 */         break;
/*       */       }
/*       */     }
/*  3733 */     while ((!getShowDeleted()) && (rowDeleted() == true));
/*       */ 
/*  3739 */     if (bool == true)
/*  3740 */       this.absolutePos -= 1;
/*       */     else {
/*  3742 */       this.absolutePos = 0;
/*       */     }
/*  3744 */     return bool;
/*       */   }
/*       */ 
/*       */   public boolean rowUpdated()
/*       */     throws SQLException
/*       */   {
/*  3768 */     checkCursor();
/*  3769 */     if (this.onInsertRow == true) {
/*  3770 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
/*       */     }
/*  3772 */     return ((Row)getCurrentRow()).getUpdated();
/*       */   }
/*       */ 
/*       */   public boolean columnUpdated(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  3791 */     checkCursor();
/*  3792 */     if (this.onInsertRow == true) {
/*  3793 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
/*       */     }
/*  3795 */     return ((Row)getCurrentRow()).getColUpdated(paramInt - 1);
/*       */   }
/*       */ 
/*       */   public boolean columnUpdated(String paramString)
/*       */     throws SQLException
/*       */   {
/*  3814 */     return columnUpdated(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public boolean rowInserted()
/*       */     throws SQLException
/*       */   {
/*  3830 */     checkCursor();
/*  3831 */     if (this.onInsertRow == true) {
/*  3832 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
/*       */     }
/*  3834 */     return ((Row)getCurrentRow()).getInserted();
/*       */   }
/*       */ 
/*       */   public boolean rowDeleted()
/*       */     throws SQLException
/*       */   {
/*  3853 */     if ((isAfterLast() == true) || (isBeforeFirst() == true) || (this.onInsertRow == true))
/*       */     {
/*  3857 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
/*       */     }
/*  3859 */     return ((Row)getCurrentRow()).getDeleted();
/*       */   }
/*       */ 
/*       */   private boolean isNumeric(int paramInt)
/*       */   {
/*  3873 */     switch (paramInt) {
/*       */     case -7:
/*       */     case -6:
/*       */     case -5:
/*       */     case 2:
/*       */     case 3:
/*       */     case 4:
/*       */     case 5:
/*       */     case 6:
/*       */     case 7:
/*       */     case 8:
/*  3884 */       return true;
/*       */     case -4:
/*       */     case -3:
/*       */     case -2:
/*       */     case -1:
/*       */     case 0:
/*  3886 */     case 1: } return false;
/*       */   }
/*       */ 
/*       */   private boolean isString(int paramInt)
/*       */   {
/*  3899 */     switch (paramInt) {
/*       */     case -1:
/*       */     case 1:
/*       */     case 12:
/*  3903 */       return true;
/*       */     }
/*  3905 */     return false;
/*       */   }
/*       */ 
/*       */   private boolean isBinary(int paramInt)
/*       */   {
/*  3918 */     switch (paramInt) {
/*       */     case -4:
/*       */     case -3:
/*       */     case -2:
/*  3922 */       return true;
/*       */     }
/*  3924 */     return false;
/*       */   }
/*       */ 
/*       */   private boolean isTemporal(int paramInt)
/*       */   {
/*  3939 */     switch (paramInt) {
/*       */     case 91:
/*       */     case 92:
/*       */     case 93:
/*  3943 */       return true;
/*       */     }
/*  3945 */     return false;
/*       */   }
/*       */ 
/*       */   private boolean isBoolean(int paramInt)
/*       */   {
/*  3960 */     switch (paramInt) {
/*       */     case -7:
/*       */     case 16:
/*  3963 */       return true;
/*       */     }
/*  3965 */     return false;
/*       */   }
/*       */ 
/*       */   private Object convertNumeric(Object paramObject, int paramInt1, int paramInt2)
/*       */     throws SQLException
/*       */   {
/*  3997 */     if (paramInt1 == paramInt2) {
/*  3998 */       return paramObject;
/*       */     }
/*       */ 
/*  4001 */     if ((!isNumeric(paramInt2)) && (!isString(paramInt2))) {
/*  4002 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + paramInt2);
/*       */     }
/*       */     try
/*       */     {
/*  4006 */       switch (paramInt2) {
/*       */       case -7:
/*  4008 */         Integer localInteger = Integer.valueOf(paramObject.toString().trim());
/*  4009 */         return localInteger.equals(Integer.valueOf(0)) ? Boolean.valueOf(false) : Boolean.valueOf(true);
/*       */       case -6:
/*  4013 */         return Byte.valueOf(paramObject.toString().trim());
/*       */       case 5:
/*  4015 */         return Short.valueOf(paramObject.toString().trim());
/*       */       case 4:
/*  4017 */         return Integer.valueOf(paramObject.toString().trim());
/*       */       case -5:
/*  4019 */         return Long.valueOf(paramObject.toString().trim());
/*       */       case 2:
/*       */       case 3:
/*  4022 */         return new BigDecimal(paramObject.toString().trim());
/*       */       case 6:
/*       */       case 7:
/*  4025 */         return new Float(paramObject.toString().trim());
/*       */       case 8:
/*  4027 */         return new Double(paramObject.toString().trim());
/*       */       case -1:
/*       */       case 1:
/*       */       case 12:
/*  4031 */         return paramObject.toString();
/*       */       case -4:
/*       */       case -3:
/*       */       case -2:
/*       */       case 0:
/*       */       case 9:
/*       */       case 10:
/*  4033 */       case 11: } throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + paramInt2);
/*       */     } catch (NumberFormatException localNumberFormatException) {
/*       */     }
/*  4036 */     throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + paramInt2);
/*       */   }
/*       */ 
/*       */   private Object convertTemporal(Object paramObject, int paramInt1, int paramInt2)
/*       */     throws SQLException
/*       */   {
/*  4096 */     if (paramInt1 == paramInt2) {
/*  4097 */       return paramObject;
/*       */     }
/*       */ 
/*  4100 */     if ((isNumeric(paramInt2) == true) || ((!isString(paramInt2)) && (!isTemporal(paramInt2))))
/*       */     {
/*  4102 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */     }
/*       */     try
/*       */     {
/*  4106 */       switch (paramInt2) {
/*       */       case 91:
/*  4108 */         if (paramInt1 == 93) {
/*  4109 */           return new java.sql.Date(((Timestamp)paramObject).getTime());
/*       */         }
/*  4111 */         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */       case 93:
/*  4114 */         if (paramInt1 == 92) {
/*  4115 */           return new Timestamp(((Time)paramObject).getTime());
/*       */         }
/*  4117 */         return new Timestamp(((java.sql.Date)paramObject).getTime());
/*       */       case 92:
/*  4120 */         if (paramInt1 == 93) {
/*  4121 */           return new Time(((Timestamp)paramObject).getTime());
/*       */         }
/*  4123 */         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */       case -1:
/*       */       case 1:
/*       */       case 12:
/*  4128 */         return paramObject.toString();
/*       */       }
/*  4130 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */     } catch (NumberFormatException localNumberFormatException) {
/*       */     }
/*  4133 */     throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */   }
/*       */ 
/*       */   private Object convertBoolean(Object paramObject, int paramInt1, int paramInt2)
/*       */     throws SQLException
/*       */   {
/*  4162 */     if (paramInt1 == paramInt2) {
/*  4163 */       return paramObject;
/*       */     }
/*       */ 
/*  4166 */     if ((isNumeric(paramInt2) == true) || ((!isString(paramInt2)) && (!isBoolean(paramInt2))))
/*       */     {
/*  4168 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */     }
/*       */ 
/*       */     try
/*       */     {
/*  4173 */       switch (paramInt2) {
/*       */       case -7:
/*  4175 */         Integer localInteger = Integer.valueOf(paramObject.toString().trim());
/*  4176 */         return localInteger.equals(Integer.valueOf(0)) ? Boolean.valueOf(false) : Boolean.valueOf(true);
/*       */       case 16:
/*  4180 */         return Boolean.valueOf(paramObject.toString().trim());
/*       */       }
/*  4182 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + paramInt2);
/*       */     } catch (NumberFormatException localNumberFormatException) {
/*       */     }
/*  4185 */     throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + paramInt2);
/*       */   }
/*       */ 
/*       */   public void updateNull(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  4217 */     checkIndex(paramInt);
/*       */ 
/*  4219 */     checkCursor();
/*       */ 
/*  4221 */     BaseRow localBaseRow = getCurrentRow();
/*  4222 */     localBaseRow.setColumnObject(paramInt, null);
/*       */   }
/*       */ 
/*       */   public void updateBoolean(int paramInt, boolean paramBoolean)
/*       */     throws SQLException
/*       */   {
/*  4251 */     checkIndex(paramInt);
/*       */ 
/*  4253 */     checkCursor();
/*  4254 */     Object localObject = convertBoolean(Boolean.valueOf(paramBoolean), -7, this.RowSetMD.getColumnType(paramInt));
/*       */ 
/*  4258 */     getCurrentRow().setColumnObject(paramInt, localObject);
/*       */   }
/*       */ 
/*       */   public void updateByte(int paramInt, byte paramByte)
/*       */     throws SQLException
/*       */   {
/*  4286 */     checkIndex(paramInt);
/*       */ 
/*  4288 */     checkCursor();
/*       */ 
/*  4290 */     Object localObject = convertNumeric(Byte.valueOf(paramByte), -6, this.RowSetMD.getColumnType(paramInt));
/*       */ 
/*  4294 */     getCurrentRow().setColumnObject(paramInt, localObject);
/*       */   }
/*       */ 
/*       */   public void updateShort(int paramInt, short paramShort)
/*       */     throws SQLException
/*       */   {
/*  4322 */     checkIndex(paramInt);
/*       */ 
/*  4324 */     checkCursor();
/*       */ 
/*  4326 */     Object localObject = convertNumeric(Short.valueOf(paramShort), 5, this.RowSetMD.getColumnType(paramInt));
/*       */ 
/*  4330 */     getCurrentRow().setColumnObject(paramInt, localObject);
/*       */   }
/*       */ 
/*       */   public void updateInt(int paramInt1, int paramInt2)
/*       */     throws SQLException
/*       */   {
/*  4358 */     checkIndex(paramInt1);
/*       */ 
/*  4360 */     checkCursor();
/*  4361 */     Object localObject = convertNumeric(Integer.valueOf(paramInt2), 4, this.RowSetMD.getColumnType(paramInt1));
/*       */ 
/*  4365 */     getCurrentRow().setColumnObject(paramInt1, localObject);
/*       */   }
/*       */ 
/*       */   public void updateLong(int paramInt, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  4393 */     checkIndex(paramInt);
/*       */ 
/*  4395 */     checkCursor();
/*       */ 
/*  4397 */     Object localObject = convertNumeric(Long.valueOf(paramLong), -5, this.RowSetMD.getColumnType(paramInt));
/*       */ 
/*  4401 */     getCurrentRow().setColumnObject(paramInt, localObject);
/*       */   }
/*       */ 
/*       */   public void updateFloat(int paramInt, float paramFloat)
/*       */     throws SQLException
/*       */   {
/*  4430 */     checkIndex(paramInt);
/*       */ 
/*  4432 */     checkCursor();
/*       */ 
/*  4434 */     Object localObject = convertNumeric(new Float(paramFloat), 7, this.RowSetMD.getColumnType(paramInt));
/*       */ 
/*  4438 */     getCurrentRow().setColumnObject(paramInt, localObject);
/*       */   }
/*       */ 
/*       */   public void updateDouble(int paramInt, double paramDouble)
/*       */     throws SQLException
/*       */   {
/*  4466 */     checkIndex(paramInt);
/*       */ 
/*  4468 */     checkCursor();
/*  4469 */     Object localObject = convertNumeric(new Double(paramDouble), 8, this.RowSetMD.getColumnType(paramInt));
/*       */ 
/*  4473 */     getCurrentRow().setColumnObject(paramInt, localObject);
/*       */   }
/*       */ 
/*       */   public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal)
/*       */     throws SQLException
/*       */   {
/*  4501 */     checkIndex(paramInt);
/*       */ 
/*  4503 */     checkCursor();
/*       */ 
/*  4505 */     Object localObject = convertNumeric(paramBigDecimal, 2, this.RowSetMD.getColumnType(paramInt));
/*       */ 
/*  4509 */     getCurrentRow().setColumnObject(paramInt, localObject);
/*       */   }
/*       */ 
/*       */   public void updateString(int paramInt, String paramString)
/*       */     throws SQLException
/*       */   {
/*  4540 */     checkIndex(paramInt);
/*       */ 
/*  4542 */     checkCursor();
/*       */ 
/*  4544 */     getCurrentRow().setColumnObject(paramInt, paramString);
/*       */   }
/*       */ 
/*       */   public void updateBytes(int paramInt, byte[] paramArrayOfByte)
/*       */     throws SQLException
/*       */   {
/*  4572 */     checkIndex(paramInt);
/*       */ 
/*  4574 */     checkCursor();
/*       */ 
/*  4576 */     if (!isBinary(this.RowSetMD.getColumnType(paramInt))) {
/*  4577 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */     }
/*       */ 
/*  4580 */     getCurrentRow().setColumnObject(paramInt, paramArrayOfByte);
/*       */   }
/*       */ 
/*       */   public void updateDate(int paramInt, java.sql.Date paramDate)
/*       */     throws SQLException
/*       */   {
/*  4609 */     checkIndex(paramInt);
/*       */ 
/*  4611 */     checkCursor();
/*       */ 
/*  4613 */     Object localObject = convertTemporal(paramDate, 91, this.RowSetMD.getColumnType(paramInt));
/*       */ 
/*  4617 */     getCurrentRow().setColumnObject(paramInt, localObject);
/*       */   }
/*       */ 
/*       */   public void updateTime(int paramInt, Time paramTime)
/*       */     throws SQLException
/*       */   {
/*  4646 */     checkIndex(paramInt);
/*       */ 
/*  4648 */     checkCursor();
/*       */ 
/*  4650 */     Object localObject = convertTemporal(paramTime, 92, this.RowSetMD.getColumnType(paramInt));
/*       */ 
/*  4654 */     getCurrentRow().setColumnObject(paramInt, localObject);
/*       */   }
/*       */ 
/*       */   public void updateTimestamp(int paramInt, Timestamp paramTimestamp)
/*       */     throws SQLException
/*       */   {
/*  4684 */     checkIndex(paramInt);
/*       */ 
/*  4686 */     checkCursor();
/*       */ 
/*  4688 */     Object localObject = convertTemporal(paramTimestamp, 93, this.RowSetMD.getColumnType(paramInt));
/*       */ 
/*  4692 */     getCurrentRow().setColumnObject(paramInt, localObject);
/*       */   }
/*       */ 
/*       */   public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
/*       */     throws SQLException
/*       */   {
/*  4718 */     checkIndex(paramInt1);
/*       */ 
/*  4720 */     checkCursor();
/*       */ 
/*  4723 */     if ((!isString(this.RowSetMD.getColumnType(paramInt1))) && (!isBinary(this.RowSetMD.getColumnType(paramInt1))))
/*       */     {
/*  4725 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */     }
/*       */ 
/*  4728 */     byte[] arrayOfByte = new byte[paramInt2];
/*       */     try {
/*  4730 */       int i = 0;
/*       */       do
/*  4732 */         i += paramInputStream.read(arrayOfByte, i, paramInt2 - i);
/*  4733 */       while (i != paramInt2);
/*       */     }
/*       */     catch (IOException localIOException) {
/*  4736 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.asciistream").toString());
/*       */     }
/*  4738 */     String str = new String(arrayOfByte);
/*       */ 
/*  4740 */     getCurrentRow().setColumnObject(paramInt1, str);
/*       */   }
/*       */ 
/*       */   public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
/*       */     throws SQLException
/*       */   {
/*  4772 */     checkIndex(paramInt1);
/*       */ 
/*  4774 */     checkCursor();
/*       */ 
/*  4776 */     if (!isBinary(this.RowSetMD.getColumnType(paramInt1))) {
/*  4777 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */     }
/*       */ 
/*  4780 */     byte[] arrayOfByte = new byte[paramInt2];
/*       */     try {
/*  4782 */       int i = 0;
/*       */       do
/*  4784 */         i += paramInputStream.read(arrayOfByte, i, paramInt2 - i);
/*  4785 */       while (i != -1);
/*       */     } catch (IOException localIOException) {
/*  4787 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.binstream").toString());
/*       */     }
/*       */ 
/*  4790 */     getCurrentRow().setColumnObject(paramInt1, arrayOfByte);
/*       */   }
/*       */ 
/*       */   public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
/*       */     throws SQLException
/*       */   {
/*  4823 */     checkIndex(paramInt1);
/*       */ 
/*  4825 */     checkCursor();
/*       */ 
/*  4827 */     if ((!isString(this.RowSetMD.getColumnType(paramInt1))) && (!isBinary(this.RowSetMD.getColumnType(paramInt1))))
/*       */     {
/*  4829 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */     }
/*       */ 
/*  4832 */     char[] arrayOfChar = new char[paramInt2];
/*       */     try {
/*  4834 */       int i = 0;
/*       */       do
/*  4836 */         i += paramReader.read(arrayOfChar, i, paramInt2 - i);
/*  4837 */       while (i != paramInt2);
/*       */     }
/*       */     catch (IOException localIOException) {
/*  4840 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.binstream").toString());
/*       */     }
/*  4842 */     String str = new String(arrayOfChar);
/*       */ 
/*  4844 */     getCurrentRow().setColumnObject(paramInt1, str);
/*       */   }
/*       */ 
/*       */   public void updateObject(int paramInt1, Object paramObject, int paramInt2)
/*       */     throws SQLException
/*       */   {
/*  4877 */     checkIndex(paramInt1);
/*       */ 
/*  4879 */     checkCursor();
/*       */ 
/*  4881 */     int i = this.RowSetMD.getColumnType(paramInt1);
/*  4882 */     if ((i == 3) || (i == 2)) {
/*  4883 */       ((BigDecimal)paramObject).setScale(paramInt2);
/*       */     }
/*  4885 */     getCurrentRow().setColumnObject(paramInt1, paramObject);
/*       */   }
/*       */ 
/*       */   public void updateObject(int paramInt, Object paramObject)
/*       */     throws SQLException
/*       */   {
/*  4913 */     checkIndex(paramInt);
/*       */ 
/*  4915 */     checkCursor();
/*       */ 
/*  4917 */     getCurrentRow().setColumnObject(paramInt, paramObject);
/*       */   }
/*       */ 
/*       */   public void updateNull(String paramString)
/*       */     throws SQLException
/*       */   {
/*  4941 */     updateNull(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public void updateBoolean(String paramString, boolean paramBoolean)
/*       */     throws SQLException
/*       */   {
/*  4967 */     updateBoolean(getColIdxByName(paramString), paramBoolean);
/*       */   }
/*       */ 
/*       */   public void updateByte(String paramString, byte paramByte)
/*       */     throws SQLException
/*       */   {
/*  4993 */     updateByte(getColIdxByName(paramString), paramByte);
/*       */   }
/*       */ 
/*       */   public void updateShort(String paramString, short paramShort)
/*       */     throws SQLException
/*       */   {
/*  5019 */     updateShort(getColIdxByName(paramString), paramShort);
/*       */   }
/*       */ 
/*       */   public void updateInt(String paramString, int paramInt)
/*       */     throws SQLException
/*       */   {
/*  5045 */     updateInt(getColIdxByName(paramString), paramInt);
/*       */   }
/*       */ 
/*       */   public void updateLong(String paramString, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  5071 */     updateLong(getColIdxByName(paramString), paramLong);
/*       */   }
/*       */ 
/*       */   public void updateFloat(String paramString, float paramFloat)
/*       */     throws SQLException
/*       */   {
/*  5097 */     updateFloat(getColIdxByName(paramString), paramFloat);
/*       */   }
/*       */ 
/*       */   public void updateDouble(String paramString, double paramDouble)
/*       */     throws SQLException
/*       */   {
/*  5123 */     updateDouble(getColIdxByName(paramString), paramDouble);
/*       */   }
/*       */ 
/*       */   public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal)
/*       */     throws SQLException
/*       */   {
/*  5149 */     updateBigDecimal(getColIdxByName(paramString), paramBigDecimal);
/*       */   }
/*       */ 
/*       */   public void updateString(String paramString1, String paramString2)
/*       */     throws SQLException
/*       */   {
/*  5175 */     updateString(getColIdxByName(paramString1), paramString2);
/*       */   }
/*       */ 
/*       */   public void updateBytes(String paramString, byte[] paramArrayOfByte)
/*       */     throws SQLException
/*       */   {
/*  5201 */     updateBytes(getColIdxByName(paramString), paramArrayOfByte);
/*       */   }
/*       */ 
/*       */   public void updateDate(String paramString, java.sql.Date paramDate)
/*       */     throws SQLException
/*       */   {
/*  5229 */     updateDate(getColIdxByName(paramString), paramDate);
/*       */   }
/*       */ 
/*       */   public void updateTime(String paramString, Time paramTime)
/*       */     throws SQLException
/*       */   {
/*  5257 */     updateTime(getColIdxByName(paramString), paramTime);
/*       */   }
/*       */ 
/*       */   public void updateTimestamp(String paramString, Timestamp paramTimestamp)
/*       */     throws SQLException
/*       */   {
/*  5288 */     updateTimestamp(getColIdxByName(paramString), paramTimestamp);
/*       */   }
/*       */ 
/*       */   public void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt)
/*       */     throws SQLException
/*       */   {
/*  5313 */     updateAsciiStream(getColIdxByName(paramString), paramInputStream, paramInt);
/*       */   }
/*       */ 
/*       */   public void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt)
/*       */     throws SQLException
/*       */   {
/*  5343 */     updateBinaryStream(getColIdxByName(paramString), paramInputStream, paramInt);
/*       */   }
/*       */ 
/*       */   public void updateCharacterStream(String paramString, Reader paramReader, int paramInt)
/*       */     throws SQLException
/*       */   {
/*  5376 */     updateCharacterStream(getColIdxByName(paramString), paramReader, paramInt);
/*       */   }
/*       */ 
/*       */   public void updateObject(String paramString, Object paramObject, int paramInt)
/*       */     throws SQLException
/*       */   {
/*  5407 */     updateObject(getColIdxByName(paramString), paramObject, paramInt);
/*       */   }
/*       */ 
/*       */   public void updateObject(String paramString, Object paramObject)
/*       */     throws SQLException
/*       */   {
/*  5433 */     updateObject(getColIdxByName(paramString), paramObject);
/*       */   }
/*       */ 
/*       */   public void insertRow()
/*       */     throws SQLException
/*       */   {
/*  5454 */     if ((!this.onInsertRow) || (!this.insertRow.isCompleteRow(this.RowSetMD)))
/*       */     {
/*  5456 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.failedins").toString());
/*       */     }
/*       */ 
/*  5461 */     Object[] arrayOfObject = getParams();
/*       */ 
/*  5463 */     for (int j = 0; j < arrayOfObject.length; j++) {
/*  5464 */       this.insertRow.setColumnObject(j + 1, arrayOfObject[j]);
/*       */     }
/*       */ 
/*  5467 */     Row localRow = new Row(this.RowSetMD.getColumnCount(), this.insertRow.getOrigRow());
/*       */ 
/*  5469 */     localRow.setInserted();
/*       */     int i;
/*  5477 */     if ((this.currentRow >= this.numRows) || (this.currentRow < 0))
/*  5478 */       i = this.numRows;
/*       */     else {
/*  5480 */       i = this.currentRow;
/*       */     }
/*       */ 
/*  5483 */     this.rvh.add(i, localRow);
/*  5484 */     this.numRows += 1;
/*       */ 
/*  5486 */     notifyRowChanged();
/*       */   }
/*       */ 
/*       */   public void updateRow()
/*       */     throws SQLException
/*       */   {
/*  5504 */     if (this.onInsertRow == true) {
/*  5505 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.updateins").toString());
/*       */     }
/*       */ 
/*  5508 */     ((Row)getCurrentRow()).setUpdated();
/*       */ 
/*  5511 */     notifyRowChanged();
/*       */   }
/*       */ 
/*       */   public void deleteRow()
/*       */     throws SQLException
/*       */   {
/*  5531 */     checkCursor();
/*       */ 
/*  5533 */     ((Row)getCurrentRow()).setDeleted();
/*  5534 */     this.numDeleted += 1;
/*       */ 
/*  5537 */     notifyRowChanged();
/*       */   }
/*       */ 
/*       */   public void refreshRow()
/*       */     throws SQLException
/*       */   {
/*  5552 */     checkCursor();
/*       */ 
/*  5555 */     if (this.onInsertRow == true) {
/*  5556 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
/*       */     }
/*       */ 
/*  5559 */     Row localRow = (Row)getCurrentRow();
/*       */ 
/*  5561 */     localRow.clearUpdated();
/*       */   }
/*       */ 
/*       */   public void cancelRowUpdates()
/*       */     throws SQLException
/*       */   {
/*  5579 */     checkCursor();
/*       */ 
/*  5582 */     if (this.onInsertRow == true) {
/*  5583 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
/*       */     }
/*       */ 
/*  5586 */     Row localRow = (Row)getCurrentRow();
/*  5587 */     if (localRow.getUpdated() == true) {
/*  5588 */       localRow.clearUpdated();
/*  5589 */       notifyRowChanged();
/*       */     }
/*       */   }
/*       */ 
/*       */   public void moveToInsertRow()
/*       */     throws SQLException
/*       */   {
/*  5620 */     if (getConcurrency() == 1007) {
/*  5621 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.movetoins").toString());
/*       */     }
/*  5623 */     if (this.insertRow == null) {
/*  5624 */       if (this.RowSetMD == null)
/*  5625 */         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.movetoins1").toString());
/*  5626 */       int i = this.RowSetMD.getColumnCount();
/*  5627 */       if (i > 0)
/*  5628 */         this.insertRow = new InsertRow(i);
/*       */       else {
/*  5630 */         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.movetoins2").toString());
/*       */       }
/*       */     }
/*  5633 */     this.onInsertRow = true;
/*       */ 
/*  5636 */     this.currentRow = this.cursorPos;
/*  5637 */     this.cursorPos = -1;
/*       */ 
/*  5639 */     this.insertRow.initInsertRow();
/*       */   }
/*       */ 
/*       */   public void moveToCurrentRow()
/*       */     throws SQLException
/*       */   {
/*  5653 */     if (!this.onInsertRow) {
/*  5654 */       return;
/*       */     }
/*  5656 */     this.cursorPos = this.currentRow;
/*  5657 */     this.onInsertRow = false;
/*       */   }
/*       */ 
/*       */   public Statement getStatement()
/*       */     throws SQLException
/*       */   {
/*  5668 */     return null;
/*       */   }
/*       */ 
/*       */   public Object getObject(int paramInt, Map<String, Class<?>> paramMap)
/*       */     throws SQLException
/*       */   {
/*  5696 */     checkIndex(paramInt);
/*       */ 
/*  5698 */     checkCursor();
/*       */ 
/*  5700 */     setLastValueNull(false);
/*  5701 */     Object localObject = getCurrentRow().getColumnObject(paramInt);
/*       */ 
/*  5704 */     if (localObject == null) {
/*  5705 */       setLastValueNull(true);
/*  5706 */       return null;
/*       */     }
/*  5708 */     if ((localObject instanceof Struct)) {
/*  5709 */       Struct localStruct = (Struct)localObject;
/*       */ 
/*  5712 */       Class localClass = (Class)paramMap.get(localStruct.getSQLTypeName());
/*  5713 */       if (localClass != null)
/*       */       {
/*  5715 */         SQLData localSQLData = null;
/*       */         try {
/*  5717 */           localSQLData = (SQLData)localClass.newInstance();
/*       */         } catch (InstantiationException localInstantiationException) {
/*  5719 */           throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.unableins").toString(), new Object[] { localInstantiationException.getMessage() }));
/*       */         }
/*       */         catch (IllegalAccessException localIllegalAccessException) {
/*  5722 */           throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.unableins").toString(), new Object[] { localIllegalAccessException.getMessage() }));
/*       */         }
/*       */ 
/*  5726 */         Object[] arrayOfObject = localStruct.getAttributes(paramMap);
/*       */ 
/*  5728 */         SQLInputImpl localSQLInputImpl = new SQLInputImpl(arrayOfObject, paramMap);
/*       */ 
/*  5730 */         localSQLData.readSQL(localSQLInputImpl, localStruct.getSQLTypeName());
/*  5731 */         return localSQLData;
/*       */       }
/*       */     }
/*  5734 */     return localObject;
/*       */   }
/*       */ 
/*       */   public Ref getRef(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  5756 */     checkIndex(paramInt);
/*       */ 
/*  5758 */     checkCursor();
/*       */ 
/*  5760 */     if (this.RowSetMD.getColumnType(paramInt) != 2006) {
/*  5761 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */     }
/*       */ 
/*  5764 */     setLastValueNull(false);
/*  5765 */     Ref localRef = (Ref)getCurrentRow().getColumnObject(paramInt);
/*       */ 
/*  5768 */     if (localRef == null) {
/*  5769 */       setLastValueNull(true);
/*  5770 */       return null;
/*       */     }
/*       */ 
/*  5773 */     return localRef;
/*       */   }
/*       */ 
/*       */   public Blob getBlob(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  5795 */     checkIndex(paramInt);
/*       */ 
/*  5797 */     checkCursor();
/*       */ 
/*  5799 */     if (this.RowSetMD.getColumnType(paramInt) != 2004) {
/*  5800 */       System.out.println(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.type").toString(), new Object[] { Integer.valueOf(this.RowSetMD.getColumnType(paramInt)) }));
/*  5801 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */     }
/*       */ 
/*  5804 */     setLastValueNull(false);
/*  5805 */     Blob localBlob = (Blob)getCurrentRow().getColumnObject(paramInt);
/*       */ 
/*  5808 */     if (localBlob == null) {
/*  5809 */       setLastValueNull(true);
/*  5810 */       return null;
/*       */     }
/*       */ 
/*  5813 */     return localBlob;
/*       */   }
/*       */ 
/*       */   public Clob getClob(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  5835 */     checkIndex(paramInt);
/*       */ 
/*  5837 */     checkCursor();
/*       */ 
/*  5839 */     if (this.RowSetMD.getColumnType(paramInt) != 2005) {
/*  5840 */       System.out.println(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.type").toString(), new Object[] { Integer.valueOf(this.RowSetMD.getColumnType(paramInt)) }));
/*  5841 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */     }
/*       */ 
/*  5844 */     setLastValueNull(false);
/*  5845 */     Clob localClob = (Clob)getCurrentRow().getColumnObject(paramInt);
/*       */ 
/*  5848 */     if (localClob == null) {
/*  5849 */       setLastValueNull(true);
/*  5850 */       return null;
/*       */     }
/*       */ 
/*  5853 */     return localClob;
/*       */   }
/*       */ 
/*       */   public Array getArray(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  5876 */     checkIndex(paramInt);
/*       */ 
/*  5878 */     checkCursor();
/*       */ 
/*  5880 */     if (this.RowSetMD.getColumnType(paramInt) != 2003) {
/*  5881 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */     }
/*       */ 
/*  5884 */     setLastValueNull(false);
/*  5885 */     Array localArray = (Array)getCurrentRow().getColumnObject(paramInt);
/*       */ 
/*  5888 */     if (localArray == null) {
/*  5889 */       setLastValueNull(true);
/*  5890 */       return null;
/*       */     }
/*       */ 
/*  5893 */     return localArray;
/*       */   }
/*       */ 
/*       */   public Object getObject(String paramString, Map<String, Class<?>> paramMap)
/*       */     throws SQLException
/*       */   {
/*  5916 */     return getObject(getColIdxByName(paramString), paramMap);
/*       */   }
/*       */ 
/*       */   public Ref getRef(String paramString)
/*       */     throws SQLException
/*       */   {
/*  5934 */     return getRef(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public Blob getBlob(String paramString)
/*       */     throws SQLException
/*       */   {
/*  5952 */     return getBlob(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public Clob getClob(String paramString)
/*       */     throws SQLException
/*       */   {
/*  5971 */     return getClob(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public Array getArray(String paramString)
/*       */     throws SQLException
/*       */   {
/*  5990 */     return getArray(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public java.sql.Date getDate(int paramInt, Calendar paramCalendar)
/*       */     throws SQLException
/*       */   {
/*  6016 */     checkIndex(paramInt);
/*       */ 
/*  6018 */     checkCursor();
/*       */ 
/*  6020 */     setLastValueNull(false);
/*  6021 */     Object localObject = getCurrentRow().getColumnObject(paramInt);
/*       */ 
/*  6024 */     if (localObject == null) {
/*  6025 */       setLastValueNull(true);
/*  6026 */       return null;
/*       */     }
/*       */ 
/*  6029 */     localObject = convertTemporal(localObject, this.RowSetMD.getColumnType(paramInt), 91);
/*       */ 
/*  6034 */     Calendar localCalendar = Calendar.getInstance();
/*       */ 
/*  6036 */     localCalendar.setTime((java.util.Date)localObject);
/*       */ 
/*  6043 */     paramCalendar.set(1, localCalendar.get(1));
/*  6044 */     paramCalendar.set(2, localCalendar.get(2));
/*  6045 */     paramCalendar.set(5, localCalendar.get(5));
/*       */ 
/*  6051 */     return new java.sql.Date(paramCalendar.getTime().getTime());
/*       */   }
/*       */ 
/*       */   public java.sql.Date getDate(String paramString, Calendar paramCalendar)
/*       */     throws SQLException
/*       */   {
/*  6073 */     return getDate(getColIdxByName(paramString), paramCalendar);
/*       */   }
/*       */ 
/*       */   public Time getTime(int paramInt, Calendar paramCalendar)
/*       */     throws SQLException
/*       */   {
/*  6099 */     checkIndex(paramInt);
/*       */ 
/*  6101 */     checkCursor();
/*       */ 
/*  6103 */     setLastValueNull(false);
/*  6104 */     Object localObject = getCurrentRow().getColumnObject(paramInt);
/*       */ 
/*  6107 */     if (localObject == null) {
/*  6108 */       setLastValueNull(true);
/*  6109 */       return null;
/*       */     }
/*       */ 
/*  6112 */     localObject = convertTemporal(localObject, this.RowSetMD.getColumnType(paramInt), 92);
/*       */ 
/*  6117 */     Calendar localCalendar = Calendar.getInstance();
/*       */ 
/*  6119 */     localCalendar.setTime((java.util.Date)localObject);
/*       */ 
/*  6126 */     paramCalendar.set(11, localCalendar.get(11));
/*  6127 */     paramCalendar.set(12, localCalendar.get(12));
/*  6128 */     paramCalendar.set(13, localCalendar.get(13));
/*       */ 
/*  6130 */     return new Time(paramCalendar.getTime().getTime());
/*       */   }
/*       */ 
/*       */   public Time getTime(String paramString, Calendar paramCalendar)
/*       */     throws SQLException
/*       */   {
/*  6152 */     return getTime(getColIdxByName(paramString), paramCalendar);
/*       */   }
/*       */ 
/*       */   public Timestamp getTimestamp(int paramInt, Calendar paramCalendar)
/*       */     throws SQLException
/*       */   {
/*  6178 */     checkIndex(paramInt);
/*       */ 
/*  6180 */     checkCursor();
/*       */ 
/*  6182 */     setLastValueNull(false);
/*  6183 */     Object localObject = getCurrentRow().getColumnObject(paramInt);
/*       */ 
/*  6186 */     if (localObject == null) {
/*  6187 */       setLastValueNull(true);
/*  6188 */       return null;
/*       */     }
/*       */ 
/*  6191 */     localObject = convertTemporal(localObject, this.RowSetMD.getColumnType(paramInt), 93);
/*       */ 
/*  6196 */     Calendar localCalendar = Calendar.getInstance();
/*       */ 
/*  6198 */     localCalendar.setTime((java.util.Date)localObject);
/*       */ 
/*  6205 */     paramCalendar.set(1, localCalendar.get(1));
/*  6206 */     paramCalendar.set(2, localCalendar.get(2));
/*  6207 */     paramCalendar.set(5, localCalendar.get(5));
/*  6208 */     paramCalendar.set(11, localCalendar.get(11));
/*  6209 */     paramCalendar.set(12, localCalendar.get(12));
/*  6210 */     paramCalendar.set(13, localCalendar.get(13));
/*       */ 
/*  6212 */     return new Timestamp(paramCalendar.getTime().getTime());
/*       */   }
/*       */ 
/*       */   public Timestamp getTimestamp(String paramString, Calendar paramCalendar)
/*       */     throws SQLException
/*       */   {
/*  6235 */     return getTimestamp(getColIdxByName(paramString), paramCalendar);
/*       */   }
/*       */ 
/*       */   public Connection getConnection()
/*       */     throws SQLException
/*       */   {
/*  6253 */     return this.conn;
/*       */   }
/*       */ 
/*       */   public void setMetaData(RowSetMetaData paramRowSetMetaData)
/*       */     throws SQLException
/*       */   {
/*  6266 */     this.RowSetMD = ((RowSetMetaDataImpl)paramRowSetMetaData);
/*       */   }
/*       */ 
/*       */   public ResultSet getOriginal()
/*       */     throws SQLException
/*       */   {
/*  6284 */     CachedRowSetImpl localCachedRowSetImpl = new CachedRowSetImpl();
/*  6285 */     localCachedRowSetImpl.RowSetMD = this.RowSetMD;
/*  6286 */     localCachedRowSetImpl.numRows = this.numRows;
/*  6287 */     localCachedRowSetImpl.cursorPos = 0;
/*       */ 
/*  6293 */     int i = this.RowSetMD.getColumnCount();
/*       */ 
/*  6296 */     for (Iterator localIterator = this.rvh.iterator(); localIterator.hasNext(); ) {
/*  6297 */       Row localRow = new Row(i, ((Row)localIterator.next()).getOrigRow());
/*  6298 */       localCachedRowSetImpl.rvh.add(localRow);
/*       */     }
/*  6300 */     return localCachedRowSetImpl;
/*       */   }
/*       */ 
/*       */   public ResultSet getOriginalRow()
/*       */     throws SQLException
/*       */   {
/*  6315 */     CachedRowSetImpl localCachedRowSetImpl = new CachedRowSetImpl();
/*  6316 */     localCachedRowSetImpl.RowSetMD = this.RowSetMD;
/*  6317 */     localCachedRowSetImpl.numRows = 1;
/*  6318 */     localCachedRowSetImpl.cursorPos = 0;
/*  6319 */     localCachedRowSetImpl.setTypeMap(getTypeMap());
/*       */ 
/*  6326 */     Row localRow = new Row(this.RowSetMD.getColumnCount(), getCurrentRow().getOrigRow());
/*       */ 
/*  6329 */     localCachedRowSetImpl.rvh.add(localRow);
/*       */ 
/*  6331 */     return localCachedRowSetImpl;
/*       */   }
/*       */ 
/*       */   public void setOriginalRow()
/*       */     throws SQLException
/*       */   {
/*  6342 */     if (this.onInsertRow == true) {
/*  6343 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
/*       */     }
/*       */ 
/*  6346 */     Row localRow = (Row)getCurrentRow();
/*  6347 */     makeRowOriginal(localRow);
/*       */ 
/*  6350 */     if (localRow.getDeleted() == true)
/*  6351 */       removeCurrentRow();
/*       */   }
/*       */ 
/*       */   private void makeRowOriginal(Row paramRow)
/*       */   {
/*  6365 */     if (paramRow.getInserted() == true) {
/*  6366 */       paramRow.clearInserted();
/*       */     }
/*       */ 
/*  6369 */     if (paramRow.getUpdated() == true)
/*  6370 */       paramRow.moveCurrentToOrig();
/*       */   }
/*       */ 
/*       */   public void setOriginal()
/*       */     throws SQLException
/*       */   {
/*  6382 */     for (Iterator localIterator = this.rvh.iterator(); localIterator.hasNext(); ) {
/*  6383 */       Row localRow = (Row)localIterator.next();
/*  6384 */       makeRowOriginal(localRow);
/*       */ 
/*  6386 */       if (localRow.getDeleted() == true) {
/*  6387 */         localIterator.remove();
/*  6388 */         this.numRows -= 1;
/*       */       }
/*       */     }
/*  6391 */     this.numDeleted = 0;
/*       */ 
/*  6394 */     notifyRowSetChanged();
/*       */   }
/*       */ 
/*       */   public String getTableName()
/*       */     throws SQLException
/*       */   {
/*  6406 */     return this.tableName;
/*       */   }
/*       */ 
/*       */   public void setTableName(String paramString)
/*       */     throws SQLException
/*       */   {
/*  6419 */     if (paramString == null) {
/*  6420 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.tablename").toString());
/*       */     }
/*  6422 */     this.tableName = paramString;
/*       */   }
/*       */ 
/*       */   public int[] getKeyColumns()
/*       */     throws SQLException
/*       */   {
/*  6437 */     return this.keyCols;
/*       */   }
/*       */ 
/*       */   public void setKeyColumns(int[] paramArrayOfInt)
/*       */     throws SQLException
/*       */   {
/*  6458 */     int i = 0;
/*  6459 */     if (this.RowSetMD != null) {
/*  6460 */       i = this.RowSetMD.getColumnCount();
/*  6461 */       if (paramArrayOfInt.length > i)
/*  6462 */         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.keycols").toString());
/*       */     }
/*  6464 */     this.keyCols = new int[paramArrayOfInt.length];
/*  6465 */     for (int j = 0; j < paramArrayOfInt.length; j++) {
/*  6466 */       if ((this.RowSetMD != null) && ((paramArrayOfInt[j] <= 0) || (paramArrayOfInt[j] > i)))
/*       */       {
/*  6468 */         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcol").toString() + paramArrayOfInt[j]);
/*       */       }
/*       */ 
/*  6471 */       this.keyCols[j] = paramArrayOfInt[j];
/*       */     }
/*       */   }
/*       */ 
/*       */   public void updateRef(int paramInt, Ref paramRef)
/*       */     throws SQLException
/*       */   {
/*  6500 */     checkIndex(paramInt);
/*       */ 
/*  6502 */     checkCursor();
/*       */ 
/*  6507 */     getCurrentRow().setColumnObject(paramInt, new SerialRef(paramRef));
/*       */   }
/*       */ 
/*       */   public void updateRef(String paramString, Ref paramRef)
/*       */     throws SQLException
/*       */   {
/*  6533 */     updateRef(getColIdxByName(paramString), paramRef);
/*       */   }
/*       */ 
/*       */   public void updateClob(int paramInt, Clob paramClob)
/*       */     throws SQLException
/*       */   {
/*  6561 */     checkIndex(paramInt);
/*       */ 
/*  6563 */     checkCursor();
/*       */ 
/*  6569 */     if (this.dbmslocatorsUpdateCopy) {
/*  6570 */       getCurrentRow().setColumnObject(paramInt, new SerialClob(paramClob));
/*       */     }
/*       */     else
/*  6573 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateClob(String paramString, Clob paramClob)
/*       */     throws SQLException
/*       */   {
/*  6600 */     updateClob(getColIdxByName(paramString), paramClob);
/*       */   }
/*       */ 
/*       */   public void updateBlob(int paramInt, Blob paramBlob)
/*       */     throws SQLException
/*       */   {
/*  6628 */     checkIndex(paramInt);
/*       */ 
/*  6630 */     checkCursor();
/*       */ 
/*  6636 */     if (this.dbmslocatorsUpdateCopy) {
/*  6637 */       getCurrentRow().setColumnObject(paramInt, new SerialBlob(paramBlob));
/*       */     }
/*       */     else
/*  6640 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateBlob(String paramString, Blob paramBlob)
/*       */     throws SQLException
/*       */   {
/*  6667 */     updateBlob(getColIdxByName(paramString), paramBlob);
/*       */   }
/*       */ 
/*       */   public void updateArray(int paramInt, Array paramArray)
/*       */     throws SQLException
/*       */   {
/*  6695 */     checkIndex(paramInt);
/*       */ 
/*  6697 */     checkCursor();
/*       */ 
/*  6702 */     getCurrentRow().setColumnObject(paramInt, new SerialArray(paramArray));
/*       */   }
/*       */ 
/*       */   public void updateArray(String paramString, Array paramArray)
/*       */     throws SQLException
/*       */   {
/*  6728 */     updateArray(getColIdxByName(paramString), paramArray);
/*       */   }
/*       */ 
/*       */   public URL getURL(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  6751 */     checkIndex(paramInt);
/*       */ 
/*  6753 */     checkCursor();
/*       */ 
/*  6755 */     if (this.RowSetMD.getColumnType(paramInt) != 70) {
/*  6756 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
/*       */     }
/*       */ 
/*  6759 */     setLastValueNull(false);
/*  6760 */     URL localURL = (URL)getCurrentRow().getColumnObject(paramInt);
/*       */ 
/*  6763 */     if (localURL == null) {
/*  6764 */       setLastValueNull(true);
/*  6765 */       return null;
/*       */     }
/*       */ 
/*  6768 */     return localURL;
/*       */   }
/*       */ 
/*       */   public URL getURL(String paramString)
/*       */     throws SQLException
/*       */   {
/*  6786 */     return getURL(getColIdxByName(paramString));
/*       */   }
/*       */ 
/*       */   public RowSetWarning getRowSetWarnings()
/*       */   {
/*       */     try
/*       */     {
/*  6811 */       notifyCursorMoved(); } catch (SQLException localSQLException) {
/*       */     }
/*  6813 */     return this.rowsetWarning;
/*       */   }
/*       */ 
/*       */   private String buildTableName(String paramString)
/*       */     throws SQLException
/*       */   {
/*  6833 */     Object localObject1 = "";
/*  6834 */     paramString = paramString.trim();
/*       */ 
/*  6838 */     if (paramString.toLowerCase().startsWith("select"))
/*       */     {
/*  6843 */       int i = paramString.toLowerCase().indexOf("from");
/*  6844 */       int j = paramString.indexOf(",", i);
/*       */ 
/*  6846 */       if (j == -1)
/*       */       {
/*  6848 */         localObject1 = paramString.substring(i + "from".length(), paramString.length()).trim();
/*       */ 
/*  6850 */         Object localObject2 = localObject1;
/*       */ 
/*  6852 */         int k = ((String)localObject2).toLowerCase().indexOf("where");
/*       */ 
/*  6859 */         if (k != -1)
/*       */         {
/*  6861 */           localObject2 = ((String)localObject2).substring(0, k).trim();
/*       */         }
/*       */ 
/*  6864 */         localObject1 = localObject2;
/*       */       }
/*       */ 
/*       */     }
/*  6870 */     else if (!paramString.toLowerCase().startsWith("insert"))
/*       */     {
/*  6872 */       if (!paramString.toLowerCase().startsWith("update"));
/*       */     }
/*       */ 
/*  6875 */     return localObject1;
/*       */   }
/*       */ 
/*       */   public void commit()
/*       */     throws SQLException
/*       */   {
/*  6885 */     this.conn.commit();
/*       */   }
/*       */ 
/*       */   public void rollback()
/*       */     throws SQLException
/*       */   {
/*  6895 */     this.conn.rollback();
/*       */   }
/*       */ 
/*       */   public void rollback(Savepoint paramSavepoint)
/*       */     throws SQLException
/*       */   {
/*  6905 */     this.conn.rollback(paramSavepoint);
/*       */   }
/*       */ 
/*       */   public void unsetMatchColumn(int[] paramArrayOfInt)
/*       */     throws SQLException
/*       */   {
/*  6925 */     for (int j = 0; j < paramArrayOfInt.length; j++) {
/*  6926 */       int i = Integer.parseInt(((Integer)this.iMatchColumns.get(j)).toString());
/*  6927 */       if (paramArrayOfInt[j] != i) {
/*  6928 */         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols").toString());
/*       */       }
/*       */     }
/*       */ 
/*  6932 */     for (j = 0; j < paramArrayOfInt.length; j++)
/*  6933 */       this.iMatchColumns.set(j, Integer.valueOf(-1));
/*       */   }
/*       */ 
/*       */   public void unsetMatchColumn(String[] paramArrayOfString)
/*       */     throws SQLException
/*       */   {
/*  6953 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/*  6954 */       if (!paramArrayOfString[i].equals(this.strMatchColumns.get(i))) {
/*  6955 */         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols").toString());
/*       */       }
/*       */     }
/*       */ 
/*  6959 */     for (i = 0; i < paramArrayOfString.length; i++)
/*  6960 */       this.strMatchColumns.set(i, null);
/*       */   }
/*       */ 
/*       */   public String[] getMatchColumnNames()
/*       */     throws SQLException
/*       */   {
/*  6976 */     String[] arrayOfString = new String[this.strMatchColumns.size()];
/*       */ 
/*  6978 */     if (this.strMatchColumns.get(0) == null) {
/*  6979 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.setmatchcols").toString());
/*       */     }
/*       */ 
/*  6982 */     this.strMatchColumns.copyInto(arrayOfString);
/*  6983 */     return arrayOfString;
/*       */   }
/*       */ 
/*       */   public int[] getMatchColumnIndexes()
/*       */     throws SQLException
/*       */   {
/*  6997 */     Integer[] arrayOfInteger = new Integer[this.iMatchColumns.size()];
/*  6998 */     int[] arrayOfInt = new int[this.iMatchColumns.size()];
/*       */ 
/*  7001 */     int i = ((Integer)this.iMatchColumns.get(0)).intValue();
/*       */ 
/*  7003 */     if (i == -1) {
/*  7004 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.setmatchcols").toString());
/*       */     }
/*       */ 
/*  7008 */     this.iMatchColumns.copyInto(arrayOfInteger);
/*       */ 
/*  7010 */     for (int j = 0; j < arrayOfInteger.length; j++) {
/*  7011 */       arrayOfInt[j] = arrayOfInteger[j].intValue();
/*       */     }
/*       */ 
/*  7014 */     return arrayOfInt;
/*       */   }
/*       */ 
/*       */   public void setMatchColumn(int[] paramArrayOfInt)
/*       */     throws SQLException
/*       */   {
/*  7036 */     for (int i = 0; i < paramArrayOfInt.length; i++) {
/*  7037 */       if (paramArrayOfInt[i] < 0) {
/*  7038 */         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols1").toString());
/*       */       }
/*       */     }
/*  7041 */     for (i = 0; i < paramArrayOfInt.length; i++)
/*  7042 */       this.iMatchColumns.add(i, Integer.valueOf(paramArrayOfInt[i]));
/*       */   }
/*       */ 
/*       */   public void setMatchColumn(String[] paramArrayOfString)
/*       */     throws SQLException
/*       */   {
/*  7063 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/*  7064 */       if ((paramArrayOfString[i] == null) || (paramArrayOfString[i].equals(""))) {
/*  7065 */         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols2").toString());
/*       */       }
/*       */     }
/*  7068 */     for (i = 0; i < paramArrayOfString.length; i++)
/*  7069 */       this.strMatchColumns.add(i, paramArrayOfString[i]);
/*       */   }
/*       */ 
/*       */   public void setMatchColumn(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  7093 */     if (paramInt < 0) {
/*  7094 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols1").toString());
/*       */     }
/*       */ 
/*  7097 */     this.iMatchColumns.set(0, Integer.valueOf(paramInt));
/*       */   }
/*       */ 
/*       */   public void setMatchColumn(String paramString)
/*       */     throws SQLException
/*       */   {
/*  7119 */     if ((paramString == null) || ((paramString = paramString.trim()).equals(""))) {
/*  7120 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols2").toString());
/*       */     }
/*       */ 
/*  7123 */     this.strMatchColumns.set(0, paramString);
/*       */   }
/*       */ 
/*       */   public void unsetMatchColumn(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  7144 */     if (!((Integer)this.iMatchColumns.get(0)).equals(Integer.valueOf(paramInt)))
/*  7145 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.unsetmatch").toString());
/*  7146 */     if (this.strMatchColumns.get(0) != null) {
/*  7147 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.unsetmatch1").toString());
/*       */     }
/*       */ 
/*  7150 */     this.iMatchColumns.set(0, Integer.valueOf(-1));
/*       */   }
/*       */ 
/*       */   public void unsetMatchColumn(String paramString)
/*       */     throws SQLException
/*       */   {
/*  7170 */     paramString = paramString.trim();
/*       */ 
/*  7172 */     if (!((String)this.strMatchColumns.get(0)).equals(paramString))
/*  7173 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.unsetmatch").toString());
/*  7174 */     if (((Integer)this.iMatchColumns.get(0)).intValue() > 0) {
/*  7175 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.unsetmatch2").toString());
/*       */     }
/*  7177 */     this.strMatchColumns.set(0, null);
/*       */   }
/*       */ 
/*       */   public void rowSetPopulated(RowSetEvent paramRowSetEvent, int paramInt)
/*       */     throws SQLException
/*       */   {
/*  7196 */     if ((paramInt < 0) || (paramInt < getFetchSize())) {
/*  7197 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.numrows").toString());
/*       */     }
/*       */ 
/*  7200 */     if (size() % paramInt == 0) {
/*  7201 */       RowSetEvent localRowSetEvent = new RowSetEvent(this);
/*  7202 */       paramRowSetEvent = localRowSetEvent;
/*  7203 */       notifyRowSetChanged();
/*       */     }
/*       */   }
/*       */ 
/*       */   public void populate(ResultSet paramResultSet, int paramInt)
/*       */     throws SQLException
/*       */   {
/*  7245 */     Map localMap = getTypeMap();
/*       */ 
/*  7249 */     this.cursorPos = 0;
/*  7250 */     if (this.populatecallcount == 0) {
/*  7251 */       if (paramInt < 0) {
/*  7252 */         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.startpos").toString());
/*       */       }
/*  7254 */       if (getMaxRows() == 0) {
/*  7255 */         paramResultSet.absolute(paramInt);
/*  7256 */         while (paramResultSet.next()) {
/*  7257 */           this.totalRows += 1;
/*       */         }
/*  7259 */         this.totalRows += 1;
/*       */       }
/*  7261 */       this.startPos = paramInt;
/*       */     }
/*  7263 */     this.populatecallcount += 1;
/*  7264 */     this.resultSet = paramResultSet;
/*  7265 */     if ((this.endPos - this.startPos >= getMaxRows()) && (getMaxRows() > 0)) {
/*  7266 */       this.endPos = this.prevEndPos;
/*  7267 */       this.pagenotend = false;
/*  7268 */       return;
/*       */     }
/*       */ 
/*  7271 */     if (((this.maxRowsreached != getMaxRows()) || (this.maxRowsreached != this.totalRows)) && (this.pagenotend)) {
/*  7272 */       this.startPrev = (paramInt - getPageSize());
/*       */     }
/*       */ 
/*  7275 */     if (this.pageSize == 0) {
/*  7276 */       this.prevEndPos = this.endPos;
/*  7277 */       this.endPos = (paramInt + getMaxRows());
/*       */     }
/*       */     else {
/*  7280 */       this.prevEndPos = this.endPos;
/*  7281 */       this.endPos = (paramInt + getPageSize());
/*       */     }
/*       */ 
/*  7285 */     if (paramInt == 1) {
/*  7286 */       this.resultSet.beforeFirst();
/*       */     }
/*       */     else {
/*  7289 */       this.resultSet.absolute(paramInt - 1);
/*       */     }
/*  7291 */     if (this.pageSize == 0) {
/*  7292 */       this.rvh = new Vector(getMaxRows());
/*       */     }
/*       */     else
/*       */     {
/*  7296 */       this.rvh = new Vector(getPageSize());
/*       */     }
/*       */ 
/*  7299 */     if (paramResultSet == null) {
/*  7300 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.populate").toString());
/*       */     }
/*       */ 
/*  7304 */     this.RSMD = paramResultSet.getMetaData();
/*       */ 
/*  7307 */     this.RowSetMD = new RowSetMetaDataImpl();
/*  7308 */     initMetaData(this.RowSetMD, this.RSMD);
/*       */ 
/*  7311 */     this.RSMD = null;
/*  7312 */     int j = this.RowSetMD.getColumnCount();
/*  7313 */     int m = getMaxRows();
/*  7314 */     int i = 0;
/*  7315 */     Row localRow = null;
/*       */ 
/*  7317 */     if ((!paramResultSet.next()) && (m == 0)) {
/*  7318 */       this.endPos = this.prevEndPos;
/*  7319 */       this.pagenotend = false;
/*  7320 */       return;
/*       */     }
/*       */ 
/*  7323 */     paramResultSet.previous();
/*       */ 
/*  7325 */     while (paramResultSet.next())
/*       */     {
/*  7327 */       localRow = new Row(j);
/*  7328 */       if (this.pageSize == 0) {
/*  7329 */         if ((i >= m) && (m > 0)) {
/*  7330 */           this.rowsetWarning.setNextException(new SQLException("Populating rows setting has exceeded max row setting"));
/*       */ 
/*  7332 */           break;
/*       */         }
/*       */ 
/*       */       }
/*  7336 */       else if ((i >= this.pageSize) || ((this.maxRowsreached >= m) && (m > 0))) {
/*  7337 */         this.rowsetWarning.setNextException(new SQLException("Populating rows setting has exceeded max row setting"));
/*       */ 
/*  7339 */         break;
/*       */       }
/*       */ 
/*  7343 */       for (int k = 1; k <= j; k++)
/*       */       {
/*       */         Object localObject;
/*  7350 */         if (localMap == null)
/*  7351 */           localObject = paramResultSet.getObject(k);
/*       */         else {
/*  7353 */           localObject = paramResultSet.getObject(k, localMap);
/*       */         }
/*       */ 
/*  7360 */         if ((localObject instanceof Struct))
/*  7361 */           localObject = new SerialStruct((Struct)localObject, localMap);
/*  7362 */         else if ((localObject instanceof SQLData))
/*  7363 */           localObject = new SerialStruct((SQLData)localObject, localMap);
/*  7364 */         else if ((localObject instanceof Blob))
/*  7365 */           localObject = new SerialBlob((Blob)localObject);
/*  7366 */         else if ((localObject instanceof Clob))
/*  7367 */           localObject = new SerialClob((Clob)localObject);
/*  7368 */         else if ((localObject instanceof Array)) {
/*  7369 */           localObject = new SerialArray((Array)localObject, localMap);
/*       */         }
/*       */ 
/*  7372 */         localRow.initColumnObject(k, localObject);
/*       */       }
/*  7374 */       i++;
/*  7375 */       this.maxRowsreached += 1;
/*  7376 */       this.rvh.add(localRow);
/*       */     }
/*  7378 */     this.numRows = i;
/*       */ 
/*  7381 */     notifyRowSetChanged();
/*       */   }
/*       */ 
/*       */   public boolean nextPage()
/*       */     throws SQLException
/*       */   {
/*  7394 */     if (this.populatecallcount == 0) {
/*  7395 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.nextpage").toString());
/*       */     }
/*       */ 
/*  7398 */     this.onFirstPage = false;
/*  7399 */     if (this.callWithCon) {
/*  7400 */       this.crsReader.setStartPosition(this.endPos);
/*  7401 */       this.crsReader.readData(this);
/*  7402 */       this.resultSet = null;
/*       */     }
/*       */     else {
/*  7405 */       populate(this.resultSet, this.endPos);
/*       */     }
/*  7407 */     return this.pagenotend;
/*       */   }
/*       */ 
/*       */   public void setPageSize(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  7418 */     if (paramInt < 0) {
/*  7419 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.pagesize").toString());
/*       */     }
/*  7421 */     if ((paramInt > getMaxRows()) && (getMaxRows() != 0)) {
/*  7422 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.pagesize1").toString());
/*       */     }
/*  7424 */     this.pageSize = paramInt;
/*       */   }
/*       */ 
/*       */   public int getPageSize()
/*       */   {
/*  7433 */     return this.pageSize;
/*       */   }
/*       */ 
/*       */   public boolean previousPage()
/*       */     throws SQLException
/*       */   {
/*  7451 */     int i = getPageSize();
/*  7452 */     int j = this.maxRowsreached;
/*       */ 
/*  7454 */     if (this.populatecallcount == 0) {
/*  7455 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.nextpage").toString());
/*       */     }
/*       */ 
/*  7458 */     if ((!this.callWithCon) && 
/*  7459 */       (this.resultSet.getType() == 1003)) {
/*  7460 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.fwdonly").toString());
/*       */     }
/*       */ 
/*  7464 */     this.pagenotend = true;
/*       */ 
/*  7466 */     if (this.startPrev < this.startPos) {
/*  7467 */       this.onFirstPage = true;
/*  7468 */       return false;
/*       */     }
/*       */ 
/*  7471 */     if (this.onFirstPage) {
/*  7472 */       return false;
/*       */     }
/*       */ 
/*  7475 */     int k = j % i;
/*       */ 
/*  7477 */     if (k == 0) {
/*  7478 */       this.maxRowsreached -= 2 * i;
/*  7479 */       if (this.callWithCon) {
/*  7480 */         this.crsReader.setStartPosition(this.startPrev);
/*  7481 */         this.crsReader.readData(this);
/*  7482 */         this.resultSet = null;
/*       */       }
/*       */       else {
/*  7485 */         populate(this.resultSet, this.startPrev);
/*       */       }
/*  7487 */       return true;
/*       */     }
/*       */ 
/*  7491 */     this.maxRowsreached -= i + k;
/*  7492 */     if (this.callWithCon) {
/*  7493 */       this.crsReader.setStartPosition(this.startPrev);
/*  7494 */       this.crsReader.readData(this);
/*  7495 */       this.resultSet = null;
/*       */     }
/*       */     else {
/*  7498 */       populate(this.resultSet, this.startPrev);
/*       */     }
/*  7500 */     return true;
/*       */   }
/*       */ 
/*       */   public void setRowInserted(boolean paramBoolean)
/*       */     throws SQLException
/*       */   {
/*  7692 */     checkCursor();
/*       */ 
/*  7694 */     if (this.onInsertRow == true) {
/*  7695 */       throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
/*       */     }
/*  7697 */     if (paramBoolean)
/*  7698 */       ((Row)getCurrentRow()).setInserted();
/*       */     else
/*  7700 */       ((Row)getCurrentRow()).clearInserted();
/*       */   }
/*       */ 
/*       */   public SQLXML getSQLXML(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  7713 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public SQLXML getSQLXML(String paramString)
/*       */     throws SQLException
/*       */   {
/*  7724 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public RowId getRowId(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  7739 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public RowId getRowId(String paramString)
/*       */     throws SQLException
/*       */   {
/*  7754 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateRowId(int paramInt, RowId paramRowId)
/*       */     throws SQLException
/*       */   {
/*  7770 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateRowId(String paramString, RowId paramRowId)
/*       */     throws SQLException
/*       */   {
/*  7786 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public int getHoldability()
/*       */     throws SQLException
/*       */   {
/*  7796 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public boolean isClosed()
/*       */     throws SQLException
/*       */   {
/*  7807 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateNString(int paramInt, String paramString)
/*       */     throws SQLException
/*       */   {
/*  7819 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateNString(String paramString1, String paramString2)
/*       */     throws SQLException
/*       */   {
/*  7831 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateNClob(int paramInt, NClob paramNClob)
/*       */     throws SQLException
/*       */   {
/*  7844 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateNClob(String paramString, NClob paramNClob)
/*       */     throws SQLException
/*       */   {
/*  7856 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public NClob getNClob(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  7871 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public NClob getNClob(String paramString)
/*       */     throws SQLException
/*       */   {
/*  7887 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public <T> T unwrap(Class<T> paramClass) throws SQLException {
/*  7891 */     return null;
/*       */   }
/*       */ 
/*       */   public boolean isWrapperFor(Class<?> paramClass) throws SQLException {
/*  7895 */     return false;
/*       */   }
/*       */ 
/*       */   public void setSQLXML(int paramInt, SQLXML paramSQLXML)
/*       */     throws SQLException
/*       */   {
/*  7908 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public void setSQLXML(String paramString, SQLXML paramSQLXML)
/*       */     throws SQLException
/*       */   {
/*  7920 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public void setRowId(int paramInt, RowId paramRowId)
/*       */     throws SQLException
/*       */   {
/*  7936 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public void setRowId(String paramString, RowId paramRowId)
/*       */     throws SQLException
/*       */   {
/*  7951 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public void setNCharacterStream(int paramInt, Reader paramReader)
/*       */     throws SQLException
/*       */   {
/*  7978 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setNClob(String paramString, NClob paramNClob)
/*       */     throws SQLException
/*       */   {
/*  7994 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public Reader getNCharacterStream(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  8014 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public Reader getNCharacterStream(String paramString)
/*       */     throws SQLException
/*       */   {
/*  8034 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateSQLXML(int paramInt, SQLXML paramSQLXML)
/*       */     throws SQLException
/*       */   {
/*  8051 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateSQLXML(String paramString, SQLXML paramSQLXML)
/*       */     throws SQLException
/*       */   {
/*  8068 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public String getNString(int paramInt)
/*       */     throws SQLException
/*       */   {
/*  8086 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public String getNString(String paramString)
/*       */     throws SQLException
/*       */   {
/*  8104 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateNCharacterStream(int paramInt, Reader paramReader, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  8126 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateNCharacterStream(String paramString, Reader paramReader, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  8148 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateNCharacterStream(int paramInt, Reader paramReader)
/*       */     throws SQLException
/*       */   {
/*  8178 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateNCharacterStream(String paramString, Reader paramReader)
/*       */     throws SQLException
/*       */   {
/*  8210 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateBlob(int paramInt, InputStream paramInputStream, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  8245 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateBlob(String paramString, InputStream paramInputStream, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  8278 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateBlob(int paramInt, InputStream paramInputStream)
/*       */     throws SQLException
/*       */   {
/*  8313 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateBlob(String paramString, InputStream paramInputStream)
/*       */     throws SQLException
/*       */   {
/*  8348 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateClob(int paramInt, Reader paramReader, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  8380 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateClob(String paramString, Reader paramReader, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  8412 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateClob(int paramInt, Reader paramReader)
/*       */     throws SQLException
/*       */   {
/*  8446 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateClob(String paramString, Reader paramReader)
/*       */     throws SQLException
/*       */   {
/*  8481 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateNClob(int paramInt, Reader paramReader, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  8515 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateNClob(String paramString, Reader paramReader, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  8549 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateNClob(int paramInt, Reader paramReader)
/*       */     throws SQLException
/*       */   {
/*  8585 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateNClob(String paramString, Reader paramReader)
/*       */     throws SQLException
/*       */   {
/*  8622 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateAsciiStream(int paramInt, InputStream paramInputStream, long paramLong)
/*       */     throws SQLException
/*       */   {
/*       */   }
/*       */ 
/*       */   public void updateBinaryStream(int paramInt, InputStream paramInputStream, long paramLong)
/*       */     throws SQLException
/*       */   {
/*       */   }
/*       */ 
/*       */   public void updateCharacterStream(int paramInt, Reader paramReader, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  8693 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateCharacterStream(String paramString, Reader paramReader, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  8719 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateAsciiStream(String paramString, InputStream paramInputStream, long paramLong)
/*       */     throws SQLException
/*       */   {
/*       */   }
/*       */ 
/*       */   public void updateBinaryStream(String paramString, InputStream paramInputStream, long paramLong)
/*       */     throws SQLException
/*       */   {
/*       */   }
/*       */ 
/*       */   public void updateBinaryStream(int paramInt, InputStream paramInputStream)
/*       */     throws SQLException
/*       */   {
/*  8789 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateBinaryStream(String paramString, InputStream paramInputStream)
/*       */     throws SQLException
/*       */   {
/*  8816 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateCharacterStream(int paramInt, Reader paramReader)
/*       */     throws SQLException
/*       */   {
/*  8841 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateCharacterStream(String paramString, Reader paramReader)
/*       */     throws SQLException
/*       */   {
/*  8868 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateAsciiStream(int paramInt, InputStream paramInputStream)
/*       */     throws SQLException
/*       */   {
/*  8893 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void updateAsciiStream(String paramString, InputStream paramInputStream)
/*       */     throws SQLException
/*       */   {
/*       */   }
/*       */ 
/*       */   public void setURL(int paramInt, URL paramURL)
/*       */     throws SQLException
/*       */   {
/*  8935 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setNClob(int paramInt, Reader paramReader)
/*       */     throws SQLException
/*       */   {
/*  8963 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setNClob(String paramString, Reader paramReader, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  8991 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setNClob(String paramString, Reader paramReader)
/*       */     throws SQLException
/*       */   {
/*  9018 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setNClob(int paramInt, Reader paramReader, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  9046 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setNClob(int paramInt, NClob paramNClob)
/*       */     throws SQLException
/*       */   {
/*  9062 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setNString(int paramInt, String paramString)
/*       */     throws SQLException
/*       */   {
/*  9082 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setNString(String paramString1, String paramString2)
/*       */     throws SQLException
/*       */   {
/*  9099 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setNCharacterStream(int paramInt, Reader paramReader, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  9117 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setNCharacterStream(String paramString, Reader paramReader, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  9136 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setNCharacterStream(String paramString, Reader paramReader)
/*       */     throws SQLException
/*       */   {
/*  9162 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setTimestamp(String paramString, Timestamp paramTimestamp, Calendar paramCalendar)
/*       */     throws SQLException
/*       */   {
/*  9188 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setClob(String paramString, Reader paramReader, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  9214 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setClob(String paramString, Clob paramClob)
/*       */     throws SQLException
/*       */   {
/*  9232 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setClob(String paramString, Reader paramReader)
/*       */     throws SQLException
/*       */   {
/*  9258 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setDate(String paramString, java.sql.Date paramDate)
/*       */     throws SQLException
/*       */   {
/*  9280 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setDate(String paramString, java.sql.Date paramDate, Calendar paramCalendar)
/*       */     throws SQLException
/*       */   {
/*  9307 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setTime(String paramString, Time paramTime)
/*       */     throws SQLException
/*       */   {
/*  9327 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setTime(String paramString, Time paramTime, Calendar paramCalendar)
/*       */     throws SQLException
/*       */   {
/*  9354 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setClob(int paramInt, Reader paramReader)
/*       */     throws SQLException
/*       */   {
/*  9380 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setClob(int paramInt, Reader paramReader, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  9404 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setBlob(int paramInt, InputStream paramInputStream, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  9434 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setBlob(int paramInt, InputStream paramInputStream)
/*       */     throws SQLException
/*       */   {
/*  9464 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setBlob(String paramString, InputStream paramInputStream, long paramLong)
/*       */     throws SQLException
/*       */   {
/*  9496 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setBlob(String paramString, Blob paramBlob)
/*       */     throws SQLException
/*       */   {
/*  9514 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setBlob(String paramString, InputStream paramInputStream)
/*       */     throws SQLException
/*       */   {
/*  9541 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setObject(String paramString, Object paramObject, int paramInt1, int paramInt2)
/*       */     throws SQLException
/*       */   {
/*  9587 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setObject(String paramString, Object paramObject, int paramInt)
/*       */     throws SQLException
/*       */   {
/*  9615 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setObject(String paramString, Object paramObject)
/*       */     throws SQLException
/*       */   {
/*  9656 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setAsciiStream(String paramString, InputStream paramInputStream, int paramInt)
/*       */     throws SQLException
/*       */   {
/*  9683 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setBinaryStream(String paramString, InputStream paramInputStream, int paramInt)
/*       */     throws SQLException
/*       */   {
/*  9710 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setCharacterStream(String paramString, Reader paramReader, int paramInt)
/*       */     throws SQLException
/*       */   {
/*  9740 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setAsciiStream(String paramString, InputStream paramInputStream)
/*       */     throws SQLException
/*       */   {
/*  9768 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setBinaryStream(String paramString, InputStream paramInputStream)
/*       */     throws SQLException
/*       */   {
/*  9795 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setCharacterStream(String paramString, Reader paramReader)
/*       */     throws SQLException
/*       */   {
/*  9826 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setBigDecimal(String paramString, BigDecimal paramBigDecimal)
/*       */     throws SQLException
/*       */   {
/*  9845 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setString(String paramString1, String paramString2)
/*       */     throws SQLException
/*       */   {
/*  9868 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setBytes(String paramString, byte[] paramArrayOfByte)
/*       */     throws SQLException
/*       */   {
/*  9890 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setTimestamp(String paramString, Timestamp paramTimestamp)
/*       */     throws SQLException
/*       */   {
/*  9912 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setNull(String paramString, int paramInt)
/*       */     throws SQLException
/*       */   {
/*  9929 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setNull(String paramString1, int paramInt, String paramString2)
/*       */     throws SQLException
/*       */   {
/*  9967 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setBoolean(String paramString, boolean paramBoolean)
/*       */     throws SQLException
/*       */   {
/*  9987 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setByte(String paramString, byte paramByte)
/*       */     throws SQLException
/*       */   {
/* 10007 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setShort(String paramString, short paramShort)
/*       */     throws SQLException
/*       */   {
/* 10027 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setInt(String paramString, int paramInt)
/*       */     throws SQLException
/*       */   {
/* 10046 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setLong(String paramString, long paramLong)
/*       */     throws SQLException
/*       */   {
/* 10065 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setFloat(String paramString, float paramFloat)
/*       */     throws SQLException
/*       */   {
/* 10084 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   public void setDouble(String paramString, double paramDouble)
/*       */     throws SQLException
/*       */   {
/* 10103 */     throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
/*       */   }
/*       */ 
/*       */   private void readObject(ObjectInputStream paramObjectInputStream)
/*       */     throws IOException, ClassNotFoundException
/*       */   {
/* 10113 */     paramObjectInputStream.defaultReadObject();
/*       */     try
/*       */     {
/* 10116 */       this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
/*       */     } catch (IOException localIOException) {
/* 10118 */       throw new RuntimeException(localIOException);
/*       */     }
/*       */   }
/*       */ 
/*       */   public <T> T getObject(int paramInt, Class<T> paramClass)
/*       */     throws SQLException
/*       */   {
/* 10125 */     throw new SQLFeatureNotSupportedException("Not supported yet.");
/*       */   }
/*       */ 
/*       */   public <T> T getObject(String paramString, Class<T> paramClass) throws SQLException {
/* 10129 */     throw new SQLFeatureNotSupportedException("Not supported yet.");
/*       */   }
/*       */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.rowset.CachedRowSetImpl
 * JD-Core Version:    0.6.2
 */