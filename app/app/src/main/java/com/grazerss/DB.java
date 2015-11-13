package com.grazerss;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import com.grazerss.BackendProvider.StateChange;
import com.grazerss.search.SearchProvider;
import com.grazerss.util.SQLiteOpenHelper;
import com.grazerss.util.Timing;
import com.grazerss.util.U;

public class DB extends SQLiteOpenHelper
{

  public static class Entries
  {

    private static final String[]   FIELD_NAMES;

    static final String             TABLE_NAME            = "entries";
    static final String             __ID                  = "_id";

    private static final String     ALTERNATE_URL         = "ALTERNATE_URL";
    public static final String      ATOM_ID               = "ATOM_ID";
    public static final String      CONTENT               = "CONTENT";
    public static final String      SNIPPET               = "SNIPPET";
    private static final String     CONTENT_TYPE          = "CONTENT_TYPE";
    private static final String     CONTENT_URL           = "CONTENT_URL";
    public static final String      TITLE                 = "TITLE";
    static final String             FEED_TITLE            = "FEED_TITLE";

    private static final String     FEED_ID               = "FEED_ID";
    public static final String      READ_STATE            = "READ_STATE";
    public static final String      READ_STATE_PENDING    = "READ_STATE_PENDING";

    public static final String      STARRED_STATE         = "STARRED_STATE";
    public static final String      STARRED_STATE_PENDING = "STARRED_STATE_PENDING";

    public static final String      LIKED_STATE           = "LIKED_STATE";
    public static final String      LIKED_STATE_PENDING   = "LIKED_STATE_PENDING";
    public static final String      PINNED_STATE_PENDING  = "PINNED_STATE_PENDING";

    public static final String      SHARED_STATE          = "SHARED_STATE";
    public static final String      SHARED_STATE_PENDING  = "SHARED_STATE_PENDING";

    public static final String      FRIENDS_SHARED_STATE  = "FRIENDS_SHARED_STATE";
    public static final String      SHARED_BY_FRIEND      = "SHARED_BY_FRIEND";

    private static final String     UPDATED_UTC           = "UPDATED_UTC";
    private static final String     INSERTED_AT           = "INSERTED_AT";

    public static final String      DOWNLOADED            = "DOWNLOADED";
    public static final String      AUTHOR                = "AUTHOR";
    public static final String      ENTRY_HASH            = "ENTRY_HASH";
    public static final String      STORAGE_LOCATION      = "STORAGE_LOCATION";

    static final String             ERROR                 = "ERROR";

    static final String             TYPE                  = "TYPE";
    public static final String      NOTE_SUBMITTED_STATE  = "NOTE_SUBMITTED_STATE";
    public static final String      NOTE                  = "NOTE";
    static final String             NOTE_SHOULD_BE_SHARED = "NOTE_SHOULD_BE_SHARED";

    private static final String[][] FIELDS                = { { __ID, "INTEGER PRIMARY KEY" }, { ATOM_ID, "TEXT" }, { ALTERNATE_URL, "TEXT" },
                                                              { CONTENT, "TEXT" }, { CONTENT_TYPE, "TEXT" }, { CONTENT_URL, "TEXT" },
                                                              { TITLE, "TEXT" }, { ENTRY_HASH, "TEXT" }, { STORAGE_LOCATION, "TEXT" },
                                                              { FEED_TITLE, "TEXT" }, { FEED_ID, "INTEGER" }, { READ_STATE, "INTEGER" },
                                                              { READ_STATE_PENDING, "INTEGER" }, { STARRED_STATE, "INTEGER" },
                                                              { STARRED_STATE_PENDING, "INTEGER" }, { SHARED_STATE, "INTEGER" },
                                                              { LIKED_STATE, "INTEGER" }, { SHARED_STATE_PENDING, "INTEGER" },
                                                              { PINNED_STATE_PENDING, "INTEGER" }, { LIKED_STATE_PENDING, "INTEGER" },
                                                              { FRIENDS_SHARED_STATE, "INTEGER" }, { SHARED_BY_FRIEND, "TEXT" },
                                                              { UPDATED_UTC, "INTEGER" }, { DOWNLOADED, "INTEGER" }, { ERROR, "TEXT" },
                                                              { AUTHOR, "TEXT" }, { INSERTED_AT, "INTEGER" }, { TYPE, "TEXT" },
                                                              { NOTE_SUBMITTED_STATE, "INTEGER" }, { NOTE, "TEXT" },
                                                              { NOTE_SHOULD_BE_SHARED, "INTEGER" }, { SNIPPET, "TEXT" } };

    static
    {
      FIELD_NAMES = new String[Entries.FIELDS.length];
      for (int i = 0; i < Entries.FIELD_NAMES.length; i++)
      {
        Entries.FIELD_NAMES[i] = Entries.FIELDS[i][0];
      }
    }

  }

  static class EntryLabelAssociations
  {

    static final String             TABLE_NAME = "entry_label_associations";
    private static final String[]   FIELD_NAMES;
    private static final String     __ID       = "_id";
    private static final String     LABEL_ID   = "LABEL_ID";
    static final String             ENTRY_ID   = "ENTRY_ID";
    private static final String[][] FIELDS     = { { __ID, "INTEGER PRIMARY KEY" }, { ENTRY_ID, "INTEGER" }, { LABEL_ID, "INTEGER" } };

    static
    {
      FIELD_NAMES = new String[EntryLabelAssociations.FIELDS.length];
      for (int i = 0; i < EntryLabelAssociations.FIELD_NAMES.length; i++)
      {
        EntryLabelAssociations.FIELD_NAMES[i] = EntryLabelAssociations.FIELDS[i][0];
      }
    }
  }

  private static class Feeds
  {

    private static final String     TABLE_NAME           = "feeds";
    private static final String[]   FIELD_NAMES;
    private static final String     __ID                 = "_id";
    private static final String     ATOM_ID              = "ATOM_ID";
    private static final String     TITLE                = "TITLE";
    private static final String     ALTERNATE_URL        = "ALT_URL";
    private static final String     DOWNLOAD_PREF        = "DOWNLOAD_PREF";
    private static final String     DISPLAY_PREF         = "DISPLAY_PREF";
    private static final String     NOTIFICATION_ENABLED = "NOTIFICATION";
    private static final String     WEB_SCALE            = "WEB_SCALE";
    private static final String     FEED_SCALE           = "FEED_SCALE";
    private static final String     JAVASCRIPT_ENABLED   = "JS_ENABLED";
    private static final String     FIT_TO_WIDTH_ENABLED = "FTW_ENABLED";

    private static final String[][] FIELDS               = { { __ID, "INTEGER PRIMARY KEY" }, { ATOM_ID, "TEXT" }, { TITLE, "TEXT" },
                                                             { DOWNLOAD_PREF, "INTEGER" }, { DISPLAY_PREF, "INTEGER" },
                                                             { NOTIFICATION_ENABLED, "INTEGER" }, { WEB_SCALE, "REAL" }, { FEED_SCALE, "REAL" },
                                                             { JAVASCRIPT_ENABLED, "INTEGER" }, { FIT_TO_WIDTH_ENABLED, "INTEGER" },
                                                             { ALTERNATE_URL, "TEXT" } };

    static
    {
      FIELD_NAMES = new String[Feeds.FIELDS.length];
      for (int i = 0; i < Feeds.FIELD_NAMES.length; i++)
      {
        Feeds.FIELD_NAMES[i] = Feeds.FIELDS[i][0];
      }
    }
  }

  private static class Labels
  {

    private static final String     TABLE_NAME = "labels";
    private static final String[]   FIELD_NAMES;
    private static final String     __ID       = "_id";
    private static final String     NAME       = "NAME";
    private static final String     ORD        = "ORD";                                                                    // LATER REMOVE ME
    private static final String[][] FIELDS     = { { __ID, "INTEGER PRIMARY KEY" }, { NAME, "TEXT" }, { ORD, "INTEGER" } };

    static
    {
      FIELD_NAMES = new String[Labels.FIELDS.length];
      for (int i = 0; i < Labels.FIELD_NAMES.length; i++)
      {
        Labels.FIELD_NAMES[i] = Labels.FIELDS[i][0];
      }
    }

  }

  public enum TempTable
  {
    READ, STARRED, PINNED, NOTES, READ_HASHES;
  }

  private static class UnsubscribeFeeds
  {
    private static final String     TABLE_NAME   = "unsubscribe_feeds";
    private static final String[]   FIELD_NAMES;
    private static final String     __ID         = "_id";

    private static final String     FEED_ATOM_ID = "FEED_ATOM_ID";

    private static final String[][] FIELDS       = { { __ID, "INTEGER PRIMARY KEY" }, { FEED_ATOM_ID, "TEXT" } };

    static
    {
      FIELD_NAMES = new String[UnsubscribeFeeds.FIELDS.length];
      for (int i = 0; i < UnsubscribeFeeds.FIELD_NAMES.length; i++)
      {
        UnsubscribeFeeds.FIELD_NAMES[i] = UnsubscribeFeeds.FIELDS[i][0];
      }
    }

  }

  private static final String TAG                       = DB.class.getName();

  private static final String ENTRIES_VIEW              = "entries_view";

  private static final String DATABASE_NAME             = "newsrob.db";

  private static final int    DATABASE_VERSION          = 34;

  private static final String CREATE_TABLE_TEMP_IDS_SQL = "CREATE TABLE IF NOT EXISTS temp_ids (atom_id TEXT PRIMARY KEY, timestamp INTEGER);";

  private static final String CLEAR_TEMP_TABLE_SQL      = "DELETE FROM temp_ids;";

  static String               sqlQueryFindLabelsByEntry;
  static String               sqlQueryLabelsSummary;
  static String               sqlQueryLabelsSummaryReadItemsHidden;
  static String               sqlQueryContent;
  static String               sqlQueryContentReadItemsHidden;
  static String               sqlQueryContentWithLabels;
  static String               sqlQueryContentWithLabelsReadItemsHidden;

  static final String[]       CREATE_INDICES            = new String[] { "CREATE INDEX e1 ON entries (read_state, updated_utc desc);",
      "CREATE INDEX e2 ON entries (updated_utc desc, read_state, starred_state);", "CREATE INDEX e3 ON entries (atom_id);",
      "CREATE INDEX e4 ON entries (read_state, starred_state);", "CREATE INDEX e5 ON entries (read_state, friends_shared_state);",
      "CREATE INDEX e6 ON entries (type, read_state asc, updated_utc desc);", "CREATE INDEX e7 ON entries (entry_hash);",
      "CREATE INDEX l1 ON labels (name);", "CREATE INDEX f2 ON feeds (_id);", "CREATE INDEX ela3 ON entry_label_associations (entry_id);" };

  final public static boolean getBooleanValueFromCursor(final Cursor cursor, final int columnIndex)
  {
    return cursor.getInt(columnIndex) != 0;
  }

  final public static boolean getBooleanValueFromCursor(final Cursor cursor, String fieldName)
  {
    int columnIndex = cursor.getColumnIndex(fieldName);
    return getBooleanValueFromCursor(cursor, columnIndex);
  }

  final static Date getDateFromCursor(Cursor cursor, String fieldName)
  {
    return new Date(cursor.getLong(cursor.getColumnIndex(fieldName)));
  }

  private static float getFloatFromCursor(Cursor c, int columnIndex)
  {
    return c.getFloat(columnIndex);
  }

  private static float getFloatFromCursor(Cursor c, String fieldName)
  {
    return getFloatFromCursor(c, c.getColumnIndex(fieldName));
  }

  final public static int getIntegerFromCursor(final Cursor cursor, final int columnIndex)
  {
    return cursor.getInt(columnIndex);
  }

  final public static int getIntegerFromCursor(final Cursor cursor, final String fieldName)
  {
    return getIntegerFromCursor(cursor, cursor.getColumnIndex(fieldName));
  }

  final static long getLongFromCursor(Cursor cursor, String fieldName)
  {
    return cursor.getLong(cursor.getColumnIndex(fieldName));
  }

  final public static String getStringValueFromCursor(final Cursor cursor, final int columnIndex)
  {
    return cursor.getString(columnIndex);
  }

  final public static String getStringValueFromCursor(final Cursor cursor, final String fieldName)
  {
    return getStringValueFromCursor(cursor, cursor.getColumnIndex(fieldName));
  }

  private Context        context;

  private SQLiteDatabase readOnlyDB;

  DB(Context context, String path)
  {
    super(context, path, DATABASE_NAME, null, DATABASE_VERSION);

    this.context = context.getApplicationContext();

    getDb();

  }

  private List<String> addFakeLabelsToFilter(DBQuery query)
  {
    boolean showMyRecentlyStarredOnly = false;
    boolean showRecentlySharedByFriendsOnly = false;

    if (NewsRob.CONSTANT_FRIENDS_RECENTLY_SHARED.equals(query.getFilterLabel()))
    {
      showRecentlySharedByFriendsOnly = true;
      query.setFilterLabel(null);
    }
    else if (NewsRob.CONSTANT_MY_RECENTLY_STARRED.equals(query.getFilterLabel()))
    {
      showMyRecentlyStarredOnly = true;
      query.setFilterLabel(null);
    }
    List<String> sArgs = new ArrayList<String>(2);
    sArgs.add(showMyRecentlyStarredOnly ? "0" : "-1");
    sArgs.add(showRecentlySharedByFriendsOnly ? "0" : "-1");

    return sArgs;
  }

  public void addFeed2Unsubscribe(String feedAtomId)
  {
    ContentValues cv = new ContentValues();
    cv.put(UnsubscribeFeeds.FEED_ATOM_ID, feedAtomId);
    getDb().insert(UnsubscribeFeeds.TABLE_NAME, null, cv);
  }

  private void associateLabelToEntry(long entryId, Label label, SQLiteDatabase dbase)
  {

    long labelId = label.getId();
    if (labelId == -1)
    {

      // try to find an existing label first
      Label l = findLabelByName(label.getName());
      if (l != null)
      {
        labelId = l.getId();
      }
      else
      {
        // Otherwise write a new one
        labelId = createLabel(label).getId();
      }
    }

    dbase.delete(DB.EntryLabelAssociations.TABLE_NAME, EntryLabelAssociations.ENTRY_ID + " = ? AND " + EntryLabelAssociations.LABEL_ID + " = ?",
        new String[] { Long.toString(entryId), Long.toString(label.getId()) });

    ContentValues cv = new ContentValues();
    cv.put(EntryLabelAssociations.ENTRY_ID, entryId);
    cv.put(EntryLabelAssociations.LABEL_ID, labelId);
    dbase.insert(EntryLabelAssociations.TABLE_NAME, null, cv);
  }

  public void clearNotesSubmissionStateForAllSubmittedNotes()
  {
    ContentValues cv = new ContentValues();
    String note = null;
    cv.put(Entries.NOTE, note);
    cv.put(Entries.NOTE_SUBMITTED_STATE, 0);
    cv.put(Entries.NOTE_SHOULD_BE_SHARED, 0);

    getDb().update(Entries.TABLE_NAME, cv, Entries.NOTE + " IS NOT NULL AND " + Entries.NOTE_SUBMITTED_STATE + " = 1", null);

  }

  public void clearTempTable(TempTable tempTableType)
  {
    Timing t = new Timing("DB.clearTempTable " + tempTableType, context);
    SQLiteDatabase dbase = null;

    try
    {
      dbase = getDb();

      dbase.beginTransaction();

      final String sql = expandTempTableName(CLEAR_TEMP_TABLE_SQL, tempTableType);

      dbase.execSQL(sql);
    }
    catch (Exception e)
    {
      PL.log("clearTempTable", e, context);
    }
    finally
    {
      dbase.setTransactionSuccessful();
      dbase.endTransaction();
      t.stop();
    }
  }

  private Entry createEntryFromCursor(Cursor cursor)
  {

    Entry entry = new Entry(cursor.getLong(cursor.getColumnIndex(Entries.__ID)));
    entry.setAtomId(getStringValueFromCursor(cursor, Entries.ATOM_ID));
    entry.setHash(getStringValueFromCursor(cursor, Entries.ENTRY_HASH));
    entry.setStorageLocation(getStringValueFromCursor(cursor, Entries.STORAGE_LOCATION));

    entry.setAlternateHRef(getStringValueFromCursor(cursor, Entries.ALTERNATE_URL));

    entry.setContent(getStringValueFromCursor(cursor, Entries.CONTENT));
    entry.setContentType(getStringValueFromCursor(cursor, Entries.CONTENT_TYPE));
    entry.setContentURL(getStringValueFromCursor(cursor, Entries.CONTENT_URL));

    entry.setTitle(getStringValueFromCursor(cursor, Entries.TITLE));

    entry.setFeedTitle(getStringValueFromCursor(cursor, Entries.FEED_TITLE));

    entry.setReadState(ReadState.fromInt(getIntegerFromCursor(cursor, Entries.READ_STATE)));

    entry.setReadStatePending(getBooleanValueFromCursor(cursor, Entries.READ_STATE_PENDING));

    entry.setStarred(getBooleanValueFromCursor(cursor, Entries.STARRED_STATE));
    entry.setStarredStatePending(getBooleanValueFromCursor(cursor, Entries.STARRED_STATE_PENDING));
    entry.setPinnedStatePending(getBooleanValueFromCursor(cursor, Entries.PINNED_STATE_PENDING));

    entry.setUpdated(getLongFromCursor(cursor, Entries.UPDATED_UTC));

    entry.setDownloaded(getIntegerFromCursor(cursor, Entries.DOWNLOADED));
    entry.setError(getStringValueFromCursor(cursor, Entries.ERROR));

    entry.setFeedId(DB.getLongFromCursor(cursor, Entries.FEED_ID));
    entry.setFeedAtomId(DB.getStringValueFromCursor(cursor, "FEED_ATOM_ID"));

    entry.setAuthor(DB.getStringValueFromCursor(cursor, Entries.AUTHOR));

    entry.setDownloadPref(DB.getIntegerFromCursor(cursor, Feeds.DOWNLOAD_PREF));
    entry.setDisplayPref(DB.getIntegerFromCursor(cursor, Feeds.DISPLAY_PREF));

    entry.setJavaScriptEnabled(DB.getBooleanValueFromCursor(cursor, Feeds.JAVASCRIPT_ENABLED));
    entry.setFitToWidthEnabled(DB.getBooleanValueFromCursor(cursor, Feeds.FIT_TO_WIDTH_ENABLED));

    entry.setFeedAlternateUrl(DB.getStringValueFromCursor(cursor, "FEED_ALTERNATE_URL"));
    entry.setSnippet(DB.getStringValueFromCursor(cursor, Entries.SNIPPET));

    return entry;
  }

  private Feed createFeedFromCursor(Cursor c)
  {

    Feed feed = new Feed();

    feed.setId(DB.getLongFromCursor(c, Feeds.__ID));
    feed.setTitle(DB.getStringValueFromCursor(c, Feeds.TITLE));
    feed.setDownloadPref(DB.getIntegerFromCursor(c, Feeds.DOWNLOAD_PREF));
    feed.setDisplayPref(DB.getIntegerFromCursor(c, Feeds.DISPLAY_PREF));
    feed.setNotificationEnabled(DB.getBooleanValueFromCursor(c, Feeds.NOTIFICATION_ENABLED));
    feed.setWebScale(DB.getFloatFromCursor(c, Feeds.WEB_SCALE));
    feed.setFeedScale(DB.getFloatFromCursor(c, Feeds.FEED_SCALE));
    feed.setJavaScriptEnabled(DB.getBooleanValueFromCursor(c, Feeds.JAVASCRIPT_ENABLED));
    feed.setFitToWidthEnabled(DB.getBooleanValueFromCursor(c, Feeds.FIT_TO_WIDTH_ENABLED));
    feed.setAtomId(DB.getStringValueFromCursor(c, Feeds.ATOM_ID));

    return feed;
  }

  private Label createLabel(Label label)
  {

    if (label.getId() != -1)
    {
      return label;
    }

    ContentValues cv = new ContentValues();
    cv.put(Labels.NAME, label.getName());
    cv.put(Labels.ORD, label.getOrder());

    label.setId(getDb().insert(Labels.TABLE_NAME, null, cv));

    return label;
  }

  private String createMarkAllReadSQLStatementAndParametersAndAddSelectionArgs(DBQuery query, List<String> selectionArgs)
  {
    query = new DBQuery(query);

    boolean feedsNeeded = false;

    String sql = context.getString(R.string.sql_mark_all_read_label);

    // startDate
    selectionArgs.add(String.valueOf(query.getStartDate()));

    selectionArgs.addAll(addFakeLabelsToFilter(query));

    // date limit
    if (query.getDateLimit() != 0l)
    {
      final String dateLimitClause = "\n  updated_utc " + (query.isSortOrderAscending() ? " <= " : " >= ") + query.getDateLimit() + " AND\n";
      sql = Pattern.compile("-- D_L --", Pattern.DOTALL).matcher(sql).replaceAll(dateLimitClause);
    }

    // feed
    if ((query.getFilterFeedId() == null) || "all articles".equals(query.getFilterFeedId()))
    {
      sql = Pattern.compile("-- feeds-mark.*?-- feeds-mark-end", Pattern.DOTALL).matcher(sql).replaceAll("");
    }
    else
    {
      selectionArgs.add(String.valueOf(query.getFilterFeedId()));
      feedsNeeded = true;
    }

    // label
    if ((query.getFilterLabel() == null) || "all articles".equals(query.getFilterLabel()))
    {
      sql = Pattern.compile("-- labels-mark.*?-- labels-mark-end", Pattern.DOTALL).matcher(sql).replaceAll("");
    }
    else
    {
      selectionArgs.add(query.getFilterLabel());
    }

    // notifications
    if (query.getStartDate() <= 0)
    {
      sql = Pattern.compile("-- notification-feeds-mark.*?-- notification-feeds-mark-end", Pattern.DOTALL).matcher(sql).replaceAll("");
    }
    else
    {
      feedsNeeded = true;
    }

    if (!feedsNeeded)
    {
      sql = Pattern.compile("-- feeds-needed.*?-- feeds-needed-end", Pattern.DOTALL).matcher(sql).replaceAll("");
    }

    // only add LIMIT if a limit is specified in the DBQuery and not already
    // in the SQL
    if ((query.getLimit() > 0) && (sql.indexOf(" LIMIT") == -1))
    {
      sql = Pattern.compile("--LIMIT--", Pattern.DOTALL).matcher(sql).replaceAll("\n  LIMIT " + query.getLimit() + "--END-LIMIT\n");
      // sql += "\n  LIMIT " + query.getLimit() + "--END-LIMIT\n";

    }
    return sql;
  }

  int deleteAll()
  {
    // LATER Really delete Labels, in particular later on when they hold
    // configurations?
    SQLiteDatabase dbase = getDb();
    dbase.beginTransaction();
    try
    {
      dbase.delete(Labels.TABLE_NAME, "1", null);
      dbase.delete(EntryLabelAssociations.TABLE_NAME, "1", null);
      dbase.delete(UnsubscribeFeeds.TABLE_NAME, "1", null);
      return dbase.delete(Entries.TABLE_NAME, "1", null);
    }
    finally
    {
      dbase.setTransactionSuccessful();
      dbase.endTransaction();
    }
  }

  int deleteEntry(Entry entry)
  {
    return deleteEntry(entry, getDb());
  }

  private int deleteEntry(Entry entry, SQLiteDatabase dbase)
  {
    final String[] whereArgs = new String[] { String.valueOf(entry.getId()) };
    dbase.delete(EntryLabelAssociations.TABLE_NAME, EntryLabelAssociations.ENTRY_ID + "=?", whereArgs);
    return dbase.delete(Entries.TABLE_NAME, Entries.__ID + "=?", whereArgs);

  }

  boolean doesFeedExist(String feedAtomId)
  {
    Cursor c = null;
    try
    {
      c = getReadOnlyDb().query(Feeds.TABLE_NAME, new String[] { Feeds.__ID }, Feeds.ATOM_ID + " = ?", new String[] { feedAtomId }, null, null,
          null);
      return c.moveToFirst();
    }
    finally
    {
      if (c != null)
      {
        c.close();
      }
    }

  }

  private String expandTempTableName(final String sql, TempTable tempTableType)
  {
    return sql.replaceAll("temp_ids", "temp_ids_" + tempTableType.name());
  }

  List<Long> findAllArticleIdsToDownload()
  {
    Timing t = new Timing("findAllArticleIdsToDownload", context);
    List<Long> rv = null;
    String sql = context.getString(R.string.sql_articles_to_download);
    Cursor c = getReadOnlyDb().rawQuery(sql, null);
    try
    {
      rv = new ArrayList<Long>(c.getCount());
      while (c.moveToNext())
      {
        rv.add(c.getLong(0));
      }
      return rv;
    }
    finally
    {
      c.close();
      t.stop();
    }
  }

  List<Entry> findAllByPendingState(String column, String desiredState)
  {
    Timing t = new Timing("findAllByPendingState: " + column, context);

    List<Entry> result = null;
    if (Entries.PINNED_STATE_PENDING.equals(column))
    {
      String readState = "1".equals(desiredState) ? " = -1" : " > -1";
      result = findAllByQueryString(column + "='1' AND " + Entries.READ_STATE + readState);
    }
    else
    {
      String valueColumn = column.substring(0, column.lastIndexOf('_'));
      result = findAllByQueryString(column + "='1' AND " + valueColumn + "='" + desiredState + "'");
    }
    t.stop();
    return result;
  }

  List<Entry> findAllByQueryString(String queryString)
  {
    Timing t = new Timing("findAllByQueryString " + queryString, context);
    List<Entry> entries = new ArrayList<Entry>();

    Cursor cursor = findCursorByQueryString(queryString);

    if (cursor.moveToFirst())
    {
      do
      {
        entries.add(createEntryFromCursor(cursor));
      } while (cursor.moveToNext());
    }

    cursor.close();
    t.stop();
    return entries;

  }

  List<Feed> findAllFeeds()
  {
    Cursor c = null;
    List<Feed> feeds = new ArrayList<Feed>(500);
    try
    {
      c = getReadOnlyDb().query(Feeds.TABLE_NAME, Feeds.FIELD_NAMES, "1=1", null, null, null, null);
      while (c.moveToNext())
      {
        feeds.add(createFeedFromCursor(c));
      }
    }
    finally
    {
      if (c != null)
      {
        c.close();
      }
    }
    return feeds;
  }

  Entry findArticleById(Long id)
  {
    Cursor c = getReadOnlyDb().rawQuery("SELECT * FROM " + ENTRIES_VIEW + " where _id=?", new String[] { String.valueOf(id) });
    Entry entry = null;
    if (c.moveToFirst())
    {
      entry = createEntryFromCursor(c);
    }
    c.close();
    return entry;
  }

  public List<Entry> findArticlesForFeedId(long feedId)
  {
    return findAllByQueryString(Entries.FEED_ID + " = " + feedId);
  }

  public Cursor findByFullText(String query)
  {
    Timing t = new Timing("DB.findByFullText " + query + ".", context);

    // 1.6 version
    /*
     * StringBuilder sb = new StringBuilder("SELECT _id, title AS " + SearchManager.SUGGEST_COLUMN_TEXT_1 + ", feed_title AS " +
     * SearchManager.SUGGEST_COLUMN_TEXT_2 + ", atom_id AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA + ", \"com.grazerss.VIEW\" AS " +
     * SearchManager.SUGGEST_COLUMN_INTENT_ACTION + ", \"" + SearchManager.SUGGEST_NEVER_MAKE_SHORTCUT + "\" AS " + SearchManager.SUGGEST_COLUMN_SHORTCUT_ID +
     * " FROM entries_view WHERE ");
     */

    StringBuilder sb = new StringBuilder("SELECT _id, title AS " + SearchManager.SUGGEST_COLUMN_TEXT_1 + ", feed_title AS "
        + SearchManager.SUGGEST_COLUMN_TEXT_2 + ", atom_id AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA + ", \"com.grazerss.VIEW\" AS "
        + SearchManager.SUGGEST_COLUMN_INTENT_ACTION + " FROM entries_view WHERE ");
    sb.append(processFullTextQueryString(query) + " LIMIT 10");
    // if (readOnlyDB == null)
    // readOnlyDB = getReadableDatabase();
    Cursor cursor = readOnlyDB.rawQuery(sb.toString(), null);

    MatrixCursor mc = new MatrixCursor(SearchProvider.COLUMNS);
    while (cursor.moveToNext())
    {
      String[] values = new String[SearchProvider.COLUMNS.length];
      for (int i = 0; i < values.length; i++)
      {
        values[i] = cursor.getString(i);
      }
      mc.addRow(values);
    }
    cursor.close();
    // readOnlyDB.close();

    t.stop();

    return mc;
  }

  Cursor findCursorByQueryString(String queryString)
  {
    String sql = "SELECT * FROM entries_view WHERE " + queryString;
    return getReadOnlyDb().rawQuery(sql, null);

  }

  List<Entry> findEntriesByAtomId(String entryAtomId)
  {
    List<Entry> entries = new ArrayList<Entry>();

    Cursor c = getReadOnlyDb().rawQuery("SELECT * FROM " + ENTRIES_VIEW + " where ATOM_ID=?", new String[] { entryAtomId });

    while (c.moveToNext())
    {
      Entry entry = createEntryFromCursor(c);

      if (entry != null)
      {
        entries.add(entry);
      }
    }

    c.close();
    return entries;
  }

  public List<Entry> findEntriesWithNotesToBeSubmitted()
  {
    Timing t = new Timing("findEntriesWithNotesToBeSubmitted", context);
    List<Entry> entries = findAllByQueryString(Entries.NOTE + " IS NOT NULL AND " + Entries.NOTE_SUBMITTED_STATE + " = 0");
    t.stop();
    return entries;
  }

  Entry findEntryByAtomId(String entryAtomId)
  {
    Cursor c = getReadOnlyDb().rawQuery("SELECT * FROM " + ENTRIES_VIEW + " where ATOM_ID=?", new String[] { entryAtomId });
    Entry entry = null;
    if (c.moveToFirst())
    {
      entry = createEntryFromCursor(c);
    }
    c.close();
    return entry;
  }

  Entry findEntryByHash(String hash)
  {
    Cursor c = getReadOnlyDb().rawQuery("SELECT * FROM " + ENTRIES_VIEW + " where ENTRY_HASH=?", new String[] { hash });
    Entry entry = null;
    if (c.moveToFirst())
    {
      entry = createEntryFromCursor(c);
    }
    c.close();
    return entry;
  }

  Feed findFeedByAtomId(String feedId)
  {
    Cursor c = null;
    try
    {
      c = getReadOnlyDb().query(Feeds.TABLE_NAME, Feeds.FIELD_NAMES, Feeds.ATOM_ID + " = ?", new String[] { String.valueOf(feedId) }, null,
          null, null);
      if (c.moveToFirst())
      {
        return createFeedFromCursor(c);
      }
      else
      {
        return null;
      }
    }
    finally
    {
      if (c != null)
      {
        c.close();
      }
    }
  }

  Feed findFeedById(long feedId)
  {
    Cursor c = null;
    try
    {
      c = getReadOnlyDb().query(Feeds.TABLE_NAME, Feeds.FIELD_NAMES, Feeds.__ID + " = ?", new String[] { String.valueOf(feedId) }, null, null,
          null);
      if (c.moveToFirst())
      {
        return createFeedFromCursor(c);
      }
      else
      {
        return null;
      }
    }
    finally
    {
      if (c != null)
      {
        c.close();
      }
    }
  }

  long findFeedIdByFeedAtomId(String feedAtomId)
  {
    Cursor c = null;
    try
    {
      c = getReadOnlyDb().query(Feeds.TABLE_NAME, new String[] { Feeds.__ID }, Feeds.ATOM_ID + " = ?", new String[] { feedAtomId }, null, null,
          null);
      if (c.moveToFirst())
      {
        return c.getLong(0);
      }
      else
      {
        return -1;
      }
    }
    finally
    {
      if (c != null)
      {
        c.close();
      }
    }

  }

  public long findFeedIdByFeedUrl(String feedUrl)
  {
    Cursor c = null;
    try
    {
      c = getReadOnlyDb().query(Feeds.TABLE_NAME, new String[] { Feeds.__ID }, Feeds.ATOM_ID + " LIKE ?", new String[] { "%" + feedUrl }, null,
          null, null);
      if (c.moveToFirst())
      {
        return c.getLong(0);
      }
      else
      {
        return -1;
      }
    }
    finally
    {
      if (c != null)
      {
        c.close();
      }
    }
  }

  private Label findLabelByName(String name)
  {
    String queryString = Labels.NAME + " = ?";
    Cursor cursor = getReadOnlyDb().query(true, Labels.TABLE_NAME, Labels.FIELD_NAMES, queryString, new String[] { name }, null, null, null,
        null);
    Label label = null;
    if (cursor.moveToFirst())
    {
      label = new Label(cursor.getLong(0));
      label.setName(cursor.getString(1));
    }
    cursor.close();
    return label;
  }

  public Long findNotesFeedId(String googleUserId)
  {
    Cursor c = null;
    try
    {
      c = getReadOnlyDb().query(
          Feeds.TABLE_NAME,
          Feeds.FIELD_NAMES,
          Feeds.TITLE + " = \"Your Notes\" AND " + Feeds.ATOM_ID + " = \"tag:google.com,2005:reader/user/" + googleUserId
              + "/source/com.google/link\"", null, null, null, null);
      if (c.moveToFirst())
      {
        return c.getLong(0);
      }
      else
      {
        return null;
      }
    }
    finally
    {
      if (c != null)
      {
        c.close();
      }
    }

  }

  Cursor getAllFeedsCursor()
  {
    return getReadOnlyDb().query(Feeds.TABLE_NAME, new String[] { "_id", "lower(" + Feeds.TITLE + ") AS TITLE" }, "1=1", null, null, null,
        Feeds.TITLE);
  }

  Cursor getAllLabelsCursor()
  {
    return getReadOnlyDb().query(Labels.TABLE_NAME, new String[] { "_id", "lower(" + Labels.NAME + ") AS NAME" }, "1=1", null, null, null,
        Labels.NAME);
  }

  public int getArticleCount()
  {
    return getRowCount(Entries.TABLE_NAME);
  }

  public int getChangedArticleCount()
  {
    Timing t = new Timing("getChangedArticleCount", context);
    try
    {
      return getRowCount(
          Entries.TABLE_NAME,
          "read_state_pending <> 0 OR starred_state_pending <> 0 OR shared_state_pending <> 0 OR pinned_state_pending <> 0 OR liked_state_pending <> 0 OR (note_submitted_state = 0 AND note IS NOT null)",
          null);
    }
    finally
    {
      t.stop();
    }
  }

  public int getContentCount(DBQuery query)
  {
    Timing t = new Timing("getContentCount", context);

    query = new DBQuery(query);

    List<String> selectionArgs = new ArrayList<String>(4);
    selectionArgs.add(query.shouldHideReadItems() ? "1" : "2");

    // startDate
    selectionArgs.add(String.valueOf(query.getStartDate()));

    selectionArgs.addAll(addFakeLabelsToFilter(query));

    String sql = getContentCursorSQL(query, selectionArgs);
    sql = Pattern.compile("SELECT(.*)FROM", Pattern.DOTALL).matcher(sql).replaceFirst("SELECT COUNT(*) FROM");
    sql = Pattern.compile("(ORDER.*)", Pattern.DOTALL).matcher(sql).replaceAll("");
    String[] sArgs = selectionArgs.toArray(new String[selectionArgs.size()]);

    Cursor c = getReadOnlyDb().rawQuery(sql, sArgs);
    try
    {
      if (c.moveToFirst())
      {
        return c.getInt(0);
      }
      else
      {
        return -1;
      }
    }
    finally
    {
      c.close();
      t.stop();
    }

  }

  public Cursor getContentCursor(DBQuery query)
  {
    Timing t = new Timing("getContentCursor", context);

    query = new DBQuery(query);

    List<String> selectionArgs = new ArrayList<String>(4);
    selectionArgs.add(query.shouldHideReadItems() ? "1" : "2");

    // startDate
    selectionArgs.add(String.valueOf(query.getStartDate()));

    selectionArgs.addAll(addFakeLabelsToFilter(query));

    String sql = getContentCursorSQL(query, selectionArgs);
    String[] sArgs = selectionArgs.toArray(new String[selectionArgs.size()]);

    Timing t2 = new Timing("rawQuery", context);

    Cursor c = getReadOnlyDb().rawQuery(sql, sArgs);
    t2.stop();
    t.stop();

    return c;

  }

  private String getContentCursorSQL(DBQuery query, List<String> selectionArgs)
  {
    // feed
    String sql = context.getString(R.string.sql_content_cursor_query);
    if ((query.getFilterFeedId() == null) || "all articles".equals(query.getFilterFeedId()))
    {
      sql = Pattern.compile("-- feeds-mark.*?-- feeds-mark-end", Pattern.DOTALL).matcher(sql).replaceAll("");
    }
    else
    {
      selectionArgs.add(String.valueOf(query.getFilterFeedId()));
    }

    // label
    if ((query.getFilterLabel() == null) || "all articles".equals(query.getFilterLabel()))
    {
      sql = Pattern.compile("-- labels-mark.*?-- labels-mark-end", Pattern.DOTALL).matcher(sql).replaceAll("");
    }
    else
    {
      selectionArgs.add(query.getFilterLabel());
    }

    // notifications
    if (query.getStartDate() <= 0)
    {
      sql = Pattern.compile("-- notification-feeds-mark.*?-- notification-feeds-mark-end", Pattern.DOTALL).matcher(sql).replaceAll("");
    }

    // sort order
    sql += (query.isSortOrderAscending() ? " ASC" : " DESC") + ",\n  entries._id\n";

    if (query.getLimit() > 0)
    {
      sql += "\n  LIMIT " + query.getLimit();
    }
    return sql;
  }

  Cursor getDashboardContentCursor(DBQuery dbq)
  {
    boolean hideReadItems = dbq.shouldHideReadItems();
    String sql = context.getString(hideReadItems ? R.string.sql_query_dashboard_unread_only : R.string.sql_query_dashboard_all);
    return getReadOnlyDb().rawQuery(sql, null);
  }

  SQLiteDatabase getDb()
  {
    // Timing t = new Timing("DB.getDB()", context);
    try
    {
      return getWritableDatabase();
    }
    finally
    {
      // t.stop();
    }
  }

  public int getFeedCount()
  {
    return getRowCount(Feeds.TABLE_NAME);
  }

  public Cursor getFeedListContentCursor(DBQuery query)
  {
    Timing t = new Timing("DB.getFeedListContentCursor()", context);

    query = new DBQuery(query);

    List<String> selectionArgs = new ArrayList<String>(5);
    selectionArgs.add(query.shouldHideReadItems() ? "1" : "2");

    selectionArgs.addAll(addFakeLabelsToFilter(query));

    String sql = context.getString(R.string.sql_feeds_query);

    if (query.shouldHideReadItems())
    {
      sql = Pattern.compile("-- read-all-mark.*?-- read-all-mark-end", Pattern.DOTALL).matcher(sql).replaceFirst("");
    }
    else
    {
      sql = Pattern.compile("-- read-unread-only-mark.*?-- read-unread-only-mark-end", Pattern.DOTALL).matcher(sql).replaceFirst("");
    }

    // label
    if ((query.getFilterLabel() == null) || "all articles".equals(query.getFilterLabel()))
    {
      sql = Pattern.compile("-- labels-mark.*?-- labels-mark-end", Pattern.DOTALL).matcher(sql).replaceAll("");
    }
    else
    {
      selectionArgs.add(query.getFilterLabel());
    }

    String[] sArgs = selectionArgs.toArray(new String[selectionArgs.size()]);

    if (false && NewsRob.isDebuggingEnabled(context))
    {
      StringBuilder sb = new StringBuilder("\nselectionArgs=\n");
      for (String s : sArgs)
      {
        sb.append(s + "\n");
      }
      PL.log("getFeedListContentCursor:\ndbq=\n" + query + sb + "\nsql=\n" + sql, context);
    }

    Cursor c = getReadOnlyDb().rawQuery(sql, sArgs);
    t.stop();

    return c;
  }

  public int getFeeds2UnsubscribeCount()
  {
    return getRowCount(UnsubscribeFeeds.TABLE_NAME);
  }

  Cursor getFeeds2UnsubscribeCursor()
  {
    Cursor c = getReadOnlyDb().rawQuery("SELECT _ID, FEED_ATOM_ID FROM " + UnsubscribeFeeds.TABLE_NAME, null);
    return c;
  }

  Cursor getLabelsSummaryCursor(boolean hideReadItems)
  {
    String queryString = hideReadItems ? sqlQueryLabelsSummaryReadItemsHidden : sqlQueryLabelsSummary;
    // Timing t = new Timing("getLabelsSummaryCursor "+queryString);
    Cursor c = getReadOnlyDb().rawQuery(queryString, null);
    // t.stop();
    return c;
  }

  long getMarkAllReadCount(DBQuery query)
  {
    Timing t = new Timing("markAllReadCount - total", context);

    List<String> selectionArgs = new ArrayList<String>(2);
    String sql = createMarkAllReadSQLStatementAndParametersAndAddSelectionArgs(query, selectionArgs);
    sql = Pattern.compile("UPDATE.*?-- END-OF-UPDATE", Pattern.DOTALL).matcher(sql).replaceAll("SELECT COUNT(*)\nFROM entries\n");

    String[] sArgs = selectionArgs.toArray(new String[selectionArgs.size()]);
    Cursor c = getReadOnlyDb().rawQuery(sql, sArgs);
    long count = -1l;
    if (c.moveToFirst())
    {
      count = c.getInt(0);
    }
    c.close();

    t.stop();

    return count;

  }

  public List<String> getNewArticleAtomIdsToFetch(int noOfArticles2Fetch)
  {
    final String sql = context.getString(R.string.sql_select_next_articles_to_fetch) + " " + noOfArticles2Fetch;

    List<String> rv = new ArrayList<String>();

    Cursor c = getReadOnlyDb().rawQuery(sql, null);

    if (c.moveToFirst())
    {
      do
      {
        rv.add(c.getString(0));
      } while (c.moveToNext());
    }

    c.close();

    return rv;
  }

  public List<String> getNewHashesToFetch(int noOfArticles2Fetch, boolean newArticlesFirst)
  {
    final String sort = newArticlesFirst ? " DESC " : " ASC ";
    final String sql = context.getString(R.string.sql_select_next_hashes_to_fetch) + sort + " LIMIT " + noOfArticles2Fetch;

    List<String> rv = new ArrayList<String>();

    Cursor c = getReadOnlyDb().rawQuery(sql, null);

    if (c.moveToFirst())
    {
      do
      {
        rv.add(c.getString(0));
      } while (c.moveToNext());
    }

    c.close();

    return rv;
  }

  public int getNotesCount()
  {
    return getRowCount(Entries.TABLE_NAME, "type = ?", new String[] { "N" });
  }

  Cursor getOverCapacityIds(final int capacity, final int keepStarred, final int keepShared, final int keepNotes)
  {

    String sql = context.getString(R.string.sql_get_ids_to_delete);
    sql = Pattern.compile("-- MARK BEGIN.*?-- MARK END", Pattern.DOTALL).matcher(sql).replaceAll("");

    Cursor cursor = getReadOnlyDb().rawQuery(sql,
        new String[] { Integer.toString(keepStarred), Integer.toString(keepShared), Integer.toString(keepNotes), Integer.toString(capacity) });

    return cursor;
  }

  public int getPendingReadStateArticleCount()
  {
    return getRowCount(Entries.TABLE_NAME, "read_state_pending = ?", new String[] { "1" });
  }

  public int getPinnedArticleCount()
  {
    return getRowCount(Entries.TABLE_NAME, "read_state = ?", new String[] { "-1" });
  }

  public int getReadArticleCount()
  {
    return getRowCount(Entries.TABLE_NAME, "read_state = ?", new String[] { "1" });
  }

  Cursor getReadArticlesIdsForDeletion(int numberOfStarredArticlesToKeep, int numberOfSharedArticlesToKeep, int numberOfNotesToKeep)
  {

    String sql = context.getString(R.string.sql_get_ids_to_delete);
    Cursor cursor = getReadOnlyDb().rawQuery(
        sql,
        new String[] { Integer.toString(numberOfStarredArticlesToKeep), Integer.toString(numberOfSharedArticlesToKeep),
            Integer.toString(numberOfNotesToKeep), Integer.toString(0) });

    return cursor;
  }

  SQLiteDatabase getReadOnlyDb()
  {
    // return getReadOnlyDatabase();
    return getDb();
  }

  private int getRowCount(SQLiteDatabase db, String tableName, String whereClause, String[] parameters)
  {
    int count = -1;
    Cursor c = db.query(tableName, new String[] { "COUNT(*)" }, whereClause, parameters, null, null, null);
    if (c.moveToFirst())
    {
      count = c.getInt(0);
    }
    c.close();
    return count;
  }

  private int getRowCount(String tableName)
  {
    return getRowCount(tableName, null, null);
  }

  private int getRowCount(String tableName, String whereClause, String[] parameters)
  {
    return getRowCount(getReadOnlyDb(), tableName, whereClause, parameters);
  }

  public int getSharedArticleCount()
  {
    return getRowCount(Entries.TABLE_NAME, "shared_state > ?", new String[] { "0" });
  }

  public int getStarredArticleCount()
  {
    return getRowCount(Entries.TABLE_NAME, "starred_state > ?", new String[] { "0" });
  }

  public int getTempIdsCount(TempTable tempTableType)
  {
    int result = -1;
    try
    {
      result = getRowCount(expandTempTableName("temp_ids", tempTableType));
    }
    catch (Exception e)
    {
      // table doesn't exists yet, use -1
    }
    finally
    {
      return result;
    }
  }

  public int getUnreadArticleCount()
  {
    return getRowCount(Entries.TABLE_NAME, "read_state <= ?", new String[] { "0" });
  }

  public int getUnreadArticleCountExcludingPinned()
  {
    return getRowCount(Entries.TABLE_NAME, "read_state = ?", new String[] { "0" });
  }

  public Map<String, Integer> getUnreadCounts()
  {
    Map<String, Integer> data = new HashMap<String, Integer>();

    Cursor c = getReadOnlyDb()
        .rawQuery(
            "select feeds.atom_id, count(feeds.atom_id) from entries, feeds where entries.read_state = 0 and entries.feed_id = feeds._id group by feeds.atom_id",
            null);

    if (c.moveToFirst())
    {
      while (c.isAfterLast() == false)
      {
        data.put(c.getString(0), c.getInt(1));
        c.moveToNext();
      }
    }
    c.close();

    return data;
  }

  public void insert(Entry entry)
  {
    ArrayList<Entry> oneEntryList = new ArrayList<Entry>(1);
    oneEntryList.add(entry);
    insert(oneEntryList);
  }

  public long insert(Feed feed)
  {
    ContentValues cv = mapFeedToContentValues(feed);
    return getDb().insert(Feeds.TABLE_NAME, null, cv);
  }

  void insert(List<Entry> entries)
  {
    Timing t = new Timing("DB.insert for " + entries.size() + " records.", context);

    SQLiteDatabase dbase = getDb();
    dbase.beginTransaction();
    try
    {
      for (Entry entry : entries)
      {
        if (entry.getFeedAtomId() == null)
        {
          PL.log("Feed atom id cannot be null at this point. Title of article = " + entry.getTitle(), context);
          continue;
        }

        Cursor feedCursorAtomId = dbase.rawQuery("SELECT " + Feeds.__ID + ", " + Feeds.ATOM_ID + ", " + Feeds.ALTERNATE_URL + ", "
            + DB.Feeds.TITLE + " FROM " + Feeds.TABLE_NAME + " WHERE atom_id = ?", new String[] { entry.getFeedAtomId() });

        // does the feed already exists with an atom_id?
        if (feedCursorAtomId.moveToFirst())
        {
          entry.setFeedId(feedCursorAtomId.getLong(0));
          if (feedCursorAtomId.getString(2) == null)
          {
            // set alternate url when not there yet.
            ContentValues cv = new ContentValues();
            cv.put(Feeds.ALTERNATE_URL, entry.getFeedAlternateUrl());
            dbase.update(Feeds.TABLE_NAME, cv, Feeds.__ID + "=" + entry.getFeedId(), null);
          }
          // fix for Henrik's issue with badp
          if (false && entry.getFeedAtomId().endsWith("/source/com.google/link") && !feedCursorAtomId.getString(3).equals(entry.getFeedTitle()))
          {
            PL.log("DB: Fixing feed title" + " oldTitle=" + feedCursorAtomId.getString(3) + " newTitle=" + entry.getFeedTitle(), context);
            ContentValues cv = new ContentValues();
            cv.put(Feeds.TITLE, entry.getFeedTitle());
            dbase.update(Feeds.TABLE_NAME, cv, Feeds.__ID + "=?", new String[] { String.valueOf(entry.getFeedId()) });
          }
        }
        else
        {
          ContentValues cv = new ContentValues();
          cv.put(Feeds.ATOM_ID, entry.getFeedAtomId());
          cv.put(Feeds.TITLE, entry.getFeedTitle());
          cv.put(Feeds.ALTERNATE_URL, entry.getFeedAlternateUrl());
          cv.put(Feeds.WEB_SCALE, -1f);
          cv.put(Feeds.FEED_SCALE, -1f);
          cv.put(Feeds.FIT_TO_WIDTH_ENABLED, 1);

          entry.setFeedId(dbase.insert(Feeds.TABLE_NAME, null, cv));
        }

        feedCursorAtomId.close();

        // make sure the updated timestamp is unique
        long proposedUpdated = entry.getUpdatedInHighResolution();

        while (!isTimestampeUnique(dbase, proposedUpdated))
        {
          proposedUpdated++;
        }

        entry.setUpdated(proposedUpdated);

        long newEntryId = dbase.insert(Entries.TABLE_NAME, null, mapEntryToContentValues(entry));

        for (Label label : entry.getLabels())
        {
          associateLabelToEntry(newEntryId, label, dbase);
        }
      }
    }
    finally
    {
      dbase.setTransactionSuccessful();
      dbase.endTransaction();
    }

    t.stop();
  }

  public boolean isFeedMarkedToBeUnsubscribed(String feedAtomId)
  {
    return getRowCount(UnsubscribeFeeds.TABLE_NAME, UnsubscribeFeeds.FEED_ATOM_ID + " = ?", new String[] { feedAtomId }) > 0;
  }

  Boolean isMarkAllReadPossible(DBQuery query)
  {
    Timing t = new Timing("isMarkAllReadPossible - total", context);

    List<String> selectionArgs = new ArrayList<String>(2);
    String sql = createMarkAllReadSQLStatementAndParametersAndAddSelectionArgs(query, selectionArgs);
    sql = Pattern.compile("LIMIT .*--END-LIMIT", Pattern.DOTALL).matcher(sql).replaceAll(" ");
    sql = Pattern.compile("UPDATE.*?-- END-OF-UPDATE", Pattern.DOTALL).matcher(sql).replaceAll("SELECT _id\nFROM entries\n")
        + "\n LIMIT 1 --END-LIMIT\n";
    String[] sArgs = selectionArgs.toArray(new String[selectionArgs.size()]);
    Cursor c = null;
    try
    {
      c = getReadOnlyDb().rawQuery(sql, sArgs);
      return Boolean.valueOf(c.moveToFirst());
    }
    finally
    {
      if (c != null)
      {
        c.close();
      }
      t.stop();
    }
  }

  private final boolean isTimestampeUnique(SQLiteDatabase dbase, long proposedUpdated)
  {
    Cursor c = null;
    try
    {
      c = dbase.rawQuery("SELECT _id FROM " + Entries.TABLE_NAME + " WHERE " + Entries.UPDATED_UTC + " = ? LIMIT 1",
          new String[] { String.valueOf(proposedUpdated) });
      return c.getCount() == 0;
    }
    finally
    {
      if (c != null)
      {
        c.close();
      }
    }
  }

  private ContentValues mapEntryToContentValues(Entry entry)
  {

    ContentValues cv = new ContentValues();
    if (entry.getId() >= 0l)
    {
      cv.put(Entries.__ID, entry.getId());
    }

    cv.put(Entries.ATOM_ID, entry.getAtomId());

    cv.put(Entries.ENTRY_HASH, entry.getHash());
    cv.put(Entries.STORAGE_LOCATION, entry.getStorageLocation());

    cv.put(Entries.ALTERNATE_URL, entry.getAlternateHRef());

    cv.put(Entries.CONTENT, entry.getContent());
    cv.put(Entries.CONTENT_TYPE, entry.getContentType());
    cv.put(Entries.CONTENT_URL, entry.getContentURL());

    cv.put(Entries.TITLE, entry.getTitle());

    cv.put(Entries.FEED_TITLE, entry.getFeedTitle());
    cv.put(Entries.FEED_ID, entry.getFeedId());

    cv.put(Entries.READ_STATE, ReadState.toInt(entry.getReadState()));
    cv.put(Entries.READ_STATE_PENDING, entry.isReadStatePending() ? 1 : 0);

    cv.put(Entries.STARRED_STATE, entry.isStarred() ? 1 : 0);
    cv.put(Entries.STARRED_STATE_PENDING, entry.isStarredStatePending() ? 1 : 0);

    cv.put(Entries.PINNED_STATE_PENDING, entry.getReadState() == ReadState.PINNED ? 1 : 0);

    cv.put(Entries.UPDATED_UTC, entry.getUpdatedInHighResolution());

    cv.put(Entries.DOWNLOADED, entry.getDownloaded());

    cv.put(Entries.AUTHOR, entry.getAuthor());

    cv.put(Entries.ERROR, entry.getError());

    cv.put(Entries.INSERTED_AT, System.currentTimeMillis());

    cv.put(Entries.SNIPPET, entry.getSnippet());

    cv.put(Entries.LIKED_STATE, 0);
    cv.put(Entries.LIKED_STATE_PENDING, 0);

    cv.put(Entries.SHARED_STATE, 0);
    cv.put(Entries.SHARED_STATE_PENDING, 0);
    cv.put(Entries.FRIENDS_SHARED_STATE, 0);
    cv.put(Entries.SHARED_BY_FRIEND, "");

    return cv;
  }

  private ContentValues mapFeedToContentValues(Feed feed)
  {
    ContentValues cv = new ContentValues();

    cv.put(Feeds.ATOM_ID, feed.getAtomId());
    cv.put(Feeds.TITLE, feed.getTitle());

    cv.put(Feeds.DOWNLOAD_PREF, feed.getDownloadPref());
    cv.put(Feeds.DISPLAY_PREF, feed.getDisplayPref());

    cv.put(Feeds.NOTIFICATION_ENABLED, feed.isNotificationEnabled() ? "1" : "0");

    cv.put(Feeds.WEB_SCALE, feed.getWebScale());
    cv.put(Feeds.FEED_SCALE, feed.getFeedScale());

    cv.put(Feeds.JAVASCRIPT_ENABLED, feed.isJavaScriptEnabled() ? "1" : "0");
    cv.put(Feeds.FIT_TO_WIDTH_ENABLED, feed.isFitToWidthEnabled() ? "1" : "0");

    cv.put(Feeds.ALTERNATE_URL, feed.getAlternateUrl());
    return cv;
  }

  void markAllRead(DBQuery query)
  {
    Timing t = new Timing("markAllRead - total", context);

    List<String> selectionArgs = new ArrayList<String>(2);
    if (query.getLimit() > 0)
    {
      // also a date limit is set?
      // then remove the limit
      if (query.getDateLimit() != 0l)
      {
        query = new DBQuery(query);
        query.setLimit(0);
      }
      else
      {
        query = prepareDBQueryForUpdateWithLimit(query);
      }
    }

    // problem? can't get limit?
    if (query == null)
    {
      return;
    }

    String sql = createMarkAllReadSQLStatementAndParametersAndAddSelectionArgs(query, selectionArgs);
    // sql =
    // Pattern.compile("LIMIT.*--END-LIMIT").matcher(sql).replaceFirst("");

    String[] sArgs = selectionArgs.toArray(new String[selectionArgs.size()]);
    getDb().execSQL(sql, sArgs);
    t.stop();
  }

  @Override
  public void onCreate(SQLiteDatabase db)
  {
    // NewsRob.installNewsRobDefaultExceptionHandler(context);

    // tables
    db.execSQL(prepareCreateTableSQL(Entries.TABLE_NAME, Entries.FIELDS));
    db.execSQL(prepareCreateTableSQL(Labels.TABLE_NAME, Labels.FIELDS));
    db.execSQL(prepareCreateTableSQL(EntryLabelAssociations.TABLE_NAME, EntryLabelAssociations.FIELDS));
    db.execSQL(prepareCreateTableSQL(Feeds.TABLE_NAME, Feeds.FIELDS));
    db.execSQL(prepareCreateTableSQL(UnsubscribeFeeds.TABLE_NAME, UnsubscribeFeeds.FIELDS));
    // db.execSQL(CREATE_TABLE_TEMP_IDS_SQL); Removed on the 21st of October
    // 12

    // indices
    for (String sql : CREATE_INDICES)
    {
      db.execSQL(sql);
    }

    // views
    db.execSQL(context.getString(R.string.sql_create_view));
    db.execSQL(context.getString(R.string.sql_create_dashboard_view));

    Log.d(TAG, "Database initialized from scratch!");

  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    // NewsRob.installNewsRobDefaultExceptionHandler(context);
    Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

    if (oldVersion <= 1)
    {
      db.execSQL(prepareCreateTableSQL(Labels.TABLE_NAME, Labels.FIELDS));
      db.execSQL(prepareCreateTableSQL(EntryLabelAssociations.TABLE_NAME, EntryLabelAssociations.FIELDS));
    }
    if (oldVersion <= 2)
    {
      String sqlBegin = "ALTER TABLE " + Entries.TABLE_NAME + " ADD COLUMN ";
      String sqlEnd = " " + "INTEGER;";
      db.execSQL(sqlBegin + Entries.SHARED_STATE + sqlEnd);
      db.execSQL(sqlBegin + Entries.SHARED_STATE_PENDING + sqlEnd);
    }
    if (oldVersion <= 3)
    {
      for (String sql : CREATE_INDICES)
      {
        db.execSQL(sql);
      }
    }
    if (oldVersion <= 4)
    {
      String sql = "ALTER TABLE " + Entries.TABLE_NAME + " ADD COLUMN " + Entries.ERROR + " TEXT;";
      db.execSQL(sql);
    }
    if (oldVersion < 6)
    {

      // structure
      db.execSQL(prepareCreateTableSQL(Feeds.TABLE_NAME, Feeds.FIELDS));
      db.execSQL("drop index e1;");
      db.execSQL("create index e1 on entries (read_state, updated_utc desc); ");
      db.execSQL("create index e2 on entries (updated_utc desc);");
      db.execSQL("drop index if exists ela1;");
      db.execSQL("drop index if exists ela2;");
      db.execSQL("CREATE INDEX ela3 on entry_label_associations (entry_id);");

      db.execSQL("create index f1 on feeds (atom_id);");
      db.execSQL("ALTER TABLE entries ADD COLUMN feed_id INTEGER;");

      // data
      Log.d(TAG, "Starting data migration (creating feeds) done.");
      Cursor allEntriesCursor = db.rawQuery("select _id, feed_title from entries", null);

      if (allEntriesCursor.moveToFirst())
      {
        do
        {
          long feedId = -1l;
          Cursor c = db.rawQuery("select _id from feeds where title = ?", new String[] { allEntriesCursor.getString(1) });

          // Feed with title already there?
          if (c.moveToFirst())
          {
            feedId = c.getLong(0);
          }
          c.close();

          // Create one otherwise
          if (feedId == -1l)
          {
            ContentValues cv = new ContentValues();
            cv.put(Feeds.TITLE, allEntriesCursor.getString(1));
            feedId = db.insert(Feeds.TABLE_NAME, null, cv);
          }

          if (feedId == -1l)
          {
            throw new IllegalStateException("Failed to create feed.");
          }

          // Assign the feed to the entry
          ContentValues cv = new ContentValues();
          cv.put(Entries.FEED_ID, feedId);
          db.update(Entries.TABLE_NAME, cv, Entries.__ID + "=" + allEntriesCursor.getLong(0), null);

        } while (allEntriesCursor.moveToNext());
      }
      allEntriesCursor.close();

      Log.d(TAG, "Data migration (creating feeds) done.");

      Log.d(TAG, "Now migrating download status.");
      ContentValues cv = new ContentValues();
      cv.put(Entries.DOWNLOADED, Entry.STATE_DOWNLOAD_ERROR);
      db.update(Entries.TABLE_NAME, cv, Entries.DOWNLOADED + "=?", new String[] { "2" });

      cv.put(Entries.DOWNLOADED, Entry.STATE_DOWNLOADED_FULL_PAGE);
      db.update(Entries.TABLE_NAME, cv, Entries.DOWNLOADED + "=?", new String[] { "1" });

      Log.d(TAG, "Migration of download status done.");

      // --
      Log.d(TAG, "Creating view.");
      db.execSQL(context.getString(R.string.sql_create_view));
      Log.d(TAG, "Creating view done.");
      // structure
    }
    if (oldVersion < 7)
    {
      db.execSQL("drop index f1;");
      db.execSQL("create index f2 on feeds (_id);");
    }
    if (oldVersion < 8)
    {
      db.execSQL("ALTER TABLE entries ADD COLUMN AUTHOR TEXT;");
      /*
       * db.execSQL("DROP VIEW IF EXISTS entries_view;"); Log.d(TAG, "Re-Creating view."); db.execSQL(context.getString(R.string.sql_create_view));
       */
    }
    if (oldVersion < 9)
    {
      db.execSQL("ALTER TABLE feeds ADD COLUMN NOTIFICATION INTEGER;");
    }
    if (oldVersion < 10)
    { // 5th of July 2009 developed
      db.execSQL("ALTER TABLE entries ADD COLUMN INSERTED_AT INTEGER;");
    }
    if (oldVersion < 11)
    { // 5th of July 2009 developed
      db.execSQL("UPDATE entries SET INSERTED_AT = 1 WHERE INSERTED_AT IS NULL;");
    }
    if (oldVersion < 12)
    { // 5th of September 2009 developed
      db.execSQL("ALTER TABLE labels ADD COLUMN " + Labels.ORD + " INTEGER;");
      db.execSQL("UPDATE labels SET ord = 0 WHERE ord IS NULL;");
    }
    if (oldVersion < 13)
    { // 11th of September 2009 developed
      db.execSQL("ALTER TABLE feeds ADD COLUMN " + Feeds.WEB_SCALE + " DEFAULT 1.0;");
      db.execSQL("ALTER TABLE feeds ADD COLUMN " + Feeds.FEED_SCALE + " DEFAULT 1.0;");
    }
    if (oldVersion < 14)
    { // 18th of September 2009 developed
      /*
       * db.execSQL("DROP VIEW IF EXISTS dashboard_view;"); Log.d(TAG, "Re-creating dashboard_view."); db.execSQL(context.getString(R.string
       * .sql_create_dashboard_view));
       */

    }
    if (oldVersion < 15)
    { // 21th of September 2009 developed
      db.execSQL(CREATE_TABLE_TEMP_IDS_SQL);
    }
    if (oldVersion < 16)
    { // 24th of September 2009 developed
      db.execSQL("ALTER TABLE " + Entries.TABLE_NAME + " ADD COLUMN " + Entries.FRIENDS_SHARED_STATE + " INTEGER;");

      db.execSQL("UPDATE entries SET friends_shared_state = 0;");
      db.execSQL("UPDATE entries SET friends_shared_state = 1 " + "WHERE EXISTS(SELECT 1 FROM entry_label_associations AS elas, labels "
          + "WHERE elas.entry_id = entries._id AND elas.label_id = labels._id " + "AND labels.name = \"friends' recently shared\");");

      String deleteElas = "DELETE FROM entry_label_associations " + "WHERE EXISTS(SELECT 1 FROM labels "
          + "WHERE (labels._id = entry_label_associations.label_id "
          + "AND labels.name IN(\"my recently starred\", \"friends' recently shared\")));";
      String deleteLabels = "DELETE FROM labels " + "WHERE labels.name IN(\"my recently starred\", \"friends' recently shared\");";
      db.execSQL(deleteElas);
      db.execSQL(deleteLabels);

      /*
       * db.execSQL("DROP VIEW IF EXISTS dashboard_view;"); Log.d(TAG, "Re-Creating dashboard view."); db.execSQL(context.getString(R.string
       * .sql_create_dashboard_view));
       * 
       * db.execSQL("DROP VIEW IF EXISTS entries_view;"); Log.d(TAG, "Re-Creating entries view."); db.execSQL(context.getString(R.string.sql_create_view));
       */

    }

    if (oldVersion < 17)
    { // 2th of October 2009 developed

      db.execSQL("CREATE INDEX e4 on entries (read_state, starred_state);");
      db.execSQL("CREATE INDEX e5 on entries (read_state, friends_shared_state);");
      db.execSQL("DROP INDEX e2;");
      db.execSQL("CREATE INDEX e2 ON entries (updated_utc desc, read_state, starred_state);");

      // db.execSQL("DROP VIEW IF EXISTS dashboard_view;");
      // Log.d(TAG, "Re-Creating dashboard view.");
      // db.execSQL(context.getString(R.string.sql_create_dashboard_view));

    }
    if (oldVersion < 19)
    { // 25th of November 2009 developed

      String sql = "ALTER TABLE " + Feeds.TABLE_NAME + " ADD COLUMN " + Feeds.JAVASCRIPT_ENABLED + " INTEGER;";
      db.execSQL(sql);

      // db.execSQL("DROP VIEW IF EXISTS entries_view;");
      // Log.d(TAG, "Re-Creating entries view.");
      // db.execSQL(context.getString(R.string.sql_create_view));

    }

    if (oldVersion < 20)
    { // 3 dec 09
      String sql = "ALTER TABLE " + Entries.TABLE_NAME + " ADD COLUMN " + Entries.SHARED_BY_FRIEND + " TEXT;";
      db.execSQL(sql);

      // db.execSQL("DROP VIEW IF EXISTS entries_view;");
      // Log.d(TAG, "Re-Creating entries view.");
      // db.execSQL(context.getString(R.string.sql_create_view));

    }

    if (oldVersion < 21)
    { // 19 jan 10

      db.execSQL("ALTER TABLE " + Feeds.TABLE_NAME + " ADD COLUMN " + Feeds.ALTERNATE_URL + " TEXT;");

      db.execSQL("ALTER TABLE " + Entries.TABLE_NAME + " ADD COLUMN " + Entries.TYPE + " TEXT;");
      db.execSQL("ALTER TABLE " + Entries.TABLE_NAME + " ADD COLUMN " + Entries.NOTE_SUBMITTED_STATE + " INTEGER;");
      db.execSQL("ALTER TABLE " + Entries.TABLE_NAME + " ADD COLUMN " + Entries.NOTE + " TEXT;");
      db.execSQL("ALTER TABLE " + Entries.TABLE_NAME + " ADD COLUMN " + Entries.NOTE_SHOULD_BE_SHARED + " INTEGER;");

      // db.execSQL("DROP VIEW IF EXISTS dashboard_view;");
      // Log.d(TAG, "Re-Creating dashboard view.");
      // db.execSQL(context.getString(R.string.sql_create_dashboard_view));

      db.execSQL("CREATE INDEX e6 on entries (type, read_state asc, updated_utc desc);");

      db.execSQL("UPDATE entries SET " + Entries.DOWNLOADED + " = 0;");

      Log.d(TAG, "Done migrating.");

    }
    if (oldVersion < 22)
    { // 26th of May 2010 developed

      db.execSQL("ALTER TABLE entries ADD COLUMN " + Entries.LIKED_STATE + " INTEGER;");
      db.execSQL("ALTER TABLE entries ADD COLUMN " + Entries.LIKED_STATE_PENDING + " INTEGER;");

      String sql = "ALTER TABLE " + Feeds.TABLE_NAME + " ADD COLUMN " + Feeds.FIT_TO_WIDTH_ENABLED + " INTEGER;";
      db.execSQL(sql);

      // db.execSQL("DROP VIEW IF EXISTS entries_view;");
      // Log.d(TAG, "Re-Creating entries view.");
      // db.execSQL(context.getString(R.string.sql_create_view));
      db.execSQL("UPDATE " + Feeds.TABLE_NAME + " SET " + Feeds.FIT_TO_WIDTH_ENABLED + " = 1");

    }
    if (oldVersion < 23)
    { // 23th of July 2010 developed
      db.execSQL("DROP VIEW IF EXISTS dashboard_view;");
      Log.d(TAG, "Re-Creating dashboard view.");
      db.execSQL(context.getString(R.string.sql_create_dashboard_view));
    }
    if (oldVersion < 24)
    {// 27th of July 2010 developed
      db.execSQL(prepareCreateTableSQL(UnsubscribeFeeds.TABLE_NAME, UnsubscribeFeeds.FIELDS));
    }
    if (oldVersion < 25)
    { // 16th of September 2010 developed
      // Now using a higher resolution updated date
      // db.execSQL("UPDATE " + Entries.TABLE_NAME + " SET " +
      // Entries.UPDATED_UTC + " = " + Entries.UPDATED_UTC
      // + " * 1000;");
    }
    if (oldVersion < 26)
    {
      String whereClause = Entries.UPDATED_UTC + " > ?";
      String value = String.valueOf(new Date(2015, 1, 1).getTime());

      int affectedRows = getRowCount(db, Entries.TABLE_NAME, whereClause, new String[] { value });
      Log.d("DB Migration", affectedRows + " rows affected.");

      db.execSQL("UPDATE " + Entries.TABLE_NAME + " SET " + Entries.UPDATED_UTC + " = " + Entries.UPDATED_UTC + " / 1000 WHERE " + whereClause,
          new String[] { value });
    }
    if (oldVersion < 27)
    { // 9th of October 2010 developed
    }
    if (oldVersion < 28)
    { // 13th of October 2010 developed
      db.execSQL("ALTER TABLE entries ADD COLUMN " + Entries.SNIPPET + " TEXT;");

      // db.execSQL("DROP VIEW IF EXISTS entries_view;");
      // Log.d(TAG, "Re-Creating entries view.");
      // db.execSQL(context.getString(R.string.sql_create_view));
    }
    if (oldVersion < 29)
    { // 13th of July 2012 developed
      db.execSQL("ALTER TABLE entries ADD COLUMN " + Entries.PINNED_STATE_PENDING + " INTEGER DEFAULT 0;");
      db.execSQL("DROP VIEW IF EXISTS entries_view;");
      Log.d(TAG, "Re-Creating entries view.");
      db.execSQL(context.getString(R.string.sql_create_view));
    }
    if (oldVersion < 30)
    { // 24th of July 2012 developed

      db.execSQL("UPDATE entries SET " + Entries.PINNED_STATE_PENDING + " = 1 WHERE " + Entries.READ_STATE + " = -1;");
    }
    if (oldVersion < 31)
    { // Developed on October 21th 2013
      db.execSQL("DROP TABLE IF EXISTS temp_ids;");
      db.execSQL("UPDATE entries SET " + Entries.PINNED_STATE_PENDING + " = 0 WHERE " + Entries.PINNED_STATE_PENDING + " IS NULL;");
    }
    if (oldVersion < 32)
    {
      db.execSQL("DROP TABLE IF EXISTS temp_ids_READ_HASHES;");
    }
    if (oldVersion < 33)
    {
      db.execSQL("ALTER TABLE entries ADD COLUMN " + Entries.STORAGE_LOCATION + " TEXT;");
      db.execSQL("DROP VIEW IF EXISTS entries_view;");
      Log.d(TAG, "Re-Creating entries view.");
      db.execSQL(context.getString(R.string.sql_create_view));
    }
    if (oldVersion < 34)
    {
      db.execSQL("DROP VIEW IF EXISTS dashboard_view;");
      Log.d(TAG, "Re-Creating dashboard view.");
      db.execSQL(context.getString(R.string.sql_create_dashboard_view));
    }

  }

  public void populateTempHashes(TempTable tempTableType, Map<String, Long> flatHashList)
  {
    SQLiteDatabase dbase = getDb();
    final String createTableDDL = expandTempTableName(CREATE_TABLE_TEMP_IDS_SQL, tempTableType);
    PL.log("Executing sql=" + createTableDDL, context);
    dbase.execSQL(createTableDDL);

    clearTempTable(tempTableType);

    Timing t = new Timing("DB.populateTempIds " + tempTableType + " count=" + flatHashList.size(), context);

    PL.log("DB.populateTempIds(" + tempTableType + "): number of article ids=" + flatHashList.size(), context);

    String[] keys = flatHashList.keySet().toArray(new String[flatHashList.size()]);

    // offset points at the current element, 0 meaning the first element
    int offset = 0;
    while (offset < flatHashList.size())
    {

      int nextPackSize = Math.min(flatHashList.size() - offset, 30);
      // if (nextPackSize == 0)
      // break;

      SQLiteStatement stmt = null;
      try
      {

        dbase.beginTransaction();
        stmt = dbase.compileStatement(expandTempTableName("INSERT INTO temp_ids values(?, ?);", tempTableType));

        for (int j = offset; (j < (offset + nextPackSize)) && (j < flatHashList.size()); j++)
        {

          String id = keys[j];
          Long timeStamp = flatHashList.get(id);

          stmt.bindString(1, id);
          stmt.bindLong(2, timeStamp);
          stmt.execute();
        }
        offset += nextPackSize;

      }
      finally
      {
        if (stmt != null)
        {
          stmt.close();
        }
        dbase.setTransactionSuccessful();
        dbase.endTransaction();
      }
      Thread.yield();
    }
    t.stop();
  }

  public void populateTempIds(TempTable tempTableType, List<String> articleIds)
  {
    SQLiteDatabase dbase = getDb();
    final String createTableDDL = expandTempTableName(CREATE_TABLE_TEMP_IDS_SQL, tempTableType);
    PL.log("Executing sql=" + createTableDDL, context);
    dbase.execSQL(createTableDDL);

    clearTempTable(tempTableType);

    Timing t = new Timing("DB.populateTempIds " + tempTableType + " count=" + articleIds.size(), context);

    PL.log("DB.populateTempIds(" + tempTableType + "): number of article ids=" + articleIds.size(), context);

    // offset points at the current element, 0 meaning the first element
    int offset = 0;
    while (offset < articleIds.size())
    {
      int nextPackSize = Math.min(articleIds.size() - offset, 30);
      // if (nextPackSize == 0)
      // break;

      SQLiteStatement stmt = null;
      try
      {
        dbase.beginTransaction();
        stmt = dbase.compileStatement(expandTempTableName("INSERT INTO temp_ids values(?, ?);", tempTableType));

        for (int j = offset; (j < (offset + nextPackSize)) && (j < articleIds.size()); j++)
        {
          stmt.bindString(1, articleIds.get(j));
          stmt.bindString(2, articleIds.get(j));
          stmt.execute();
        }
        offset += nextPackSize;
      }
      finally
      {
        if (stmt != null)
        {
          stmt.close();
        }
        dbase.setTransactionSuccessful();
        dbase.endTransaction();
      }
      Thread.yield();
    }
    t.stop();
  }

  public void populateTempIds(TempTable tempTableType, long[] articleIds)
  {
    SQLiteDatabase dbase = getDb();
    final String createTableDDL = expandTempTableName(CREATE_TABLE_TEMP_IDS_SQL, tempTableType);
    PL.log("Executing sql=" + createTableDDL, context);
    dbase.execSQL(createTableDDL);

    clearTempTable(tempTableType);

    Timing t = new Timing("DB.populateTempIds " + tempTableType + " count=" + articleIds.length, context);

    PL.log("DB.populateTempIds(" + tempTableType + "): number of article ids=" + articleIds.length, context);

    // offset points at the current element, 0 meaning the first element
    int offset = 0;
    while (offset < articleIds.length)
    {

      int nextPackSize = Math.min(articleIds.length - offset, 30);
      // if (nextPackSize == 0)
      // break;

      SQLiteStatement stmt = null;
      try
      {
        dbase.beginTransaction();
        stmt = dbase.compileStatement(expandTempTableName("INSERT INTO temp_ids values(?);", tempTableType));

        for (int j = offset; (j < (offset + nextPackSize)) && (j < articleIds.length); j++)
        {

          long l = articleIds[j];

          // "tag:google.com,2005:reader/item/"
          String id = EntriesRetriever.TAG_GR_ITEM + U.longToHex(l);
          stmt.bindString(1, id);
          stmt.execute();
        }
        offset += nextPackSize;

      }
      finally
      {
        if (stmt != null)
        {
          stmt.close();
        }
        dbase.setTransactionSuccessful();
        dbase.endTransaction();
      }
      Thread.yield();
    }
    t.stop();
  }

  private String prepareCreateTableSQL(String tableName, String[][] fields)
  {

    List<String> fieldExpressions = new ArrayList<String>(fields.length);
    for (String[] field : fields)
    {
      String fieldName = field[0];
      String fieldType = field[1];
      fieldExpressions.add(fieldName + " " + fieldType);
    }

    return "CREATE TABLE " + tableName + " (" + U.join(fieldExpressions, ", ") + ");";

  }

  // This is a workaround for Android's limitation to update with LIMIT
  // the nth article's date is used as the new boundary
  private DBQuery prepareDBQueryForUpdateWithLimit(DBQuery query)
  {
    Timing t = new Timing("prepareDBQueryForUpdateWithLimit", context);
    // more than limit records, otherwise return?
    if (getMarkAllReadCount(query) <= query.getLimit())
    {
      return query;
    }

    // find LIMITth article
    List<String> sArgs = new ArrayList<String>(0);
    String sql = createMarkAllReadSQLStatementAndParametersAndAddSelectionArgs(query, sArgs);
    sql = Pattern.compile("UPDATE.*?-- END-OF-UPDATE", Pattern.DOTALL).matcher(sql).replaceAll("SELECT atom_id\nFROM entries\n");
    sql = Pattern
        .compile("LIMIT.*--END-LIMIT")
        .matcher(sql)
        .replaceFirst(
            "\n " + ("\nORDER BY entries.updated_utc " + (query.isSortOrderAscending() ? "ASC" : "DESC")) + "\nLIMIT " + query.getLimit()
                + " OFFSET " + (query.getLimit() - 1));

    Cursor c = getDb().rawQuery(sql, sArgs.toArray(new String[sArgs.size()]));

    // if this fails another update has been done concurrently. Return null
    // then.
    try
    {
      if (!c.moveToNext())
      {
        return null;
      }
      else
      {
        Entry entry = findEntryByAtomId(c.getString(0));
        if (entry == null)
        {
          return null;
        }

        query = new DBQuery(query);
        // remove the now no longer needed limit
        query.setLimit(0);
        // and replace it with an attribute based limit
        query.setDateLimit(entry.getUpdatedInHighResolution());
        return query;
      }
    }
    finally
    {
      c.close();
      t.stop();
    }
  }

  private String processFullTextQueryString(String query)
  {
    if (query == null)
    {
      query = "";
    }

    StringBuilder processedQuery = new StringBuilder("(");

    String[] columns = { "content", "title", "feed_title" };

    for (String columnName : columns)
    {
      StringTokenizer st = new StringTokenizer(query);
      List<String> expressions = new ArrayList<String>(6);
      while (st.hasMoreTokens())
      {
        String keyWord = st.nextToken();
        expressions.add(columnName + " LIKE \'%" + keyWord + "%\'");
        if (expressions.size() >= 2)
        {
          break;
        }
      }

      processedQuery.append(TextUtils.join(" AND ", expressions));
      processedQuery.append(") OR (");
    }
    processedQuery.append(")");
    return processedQuery.toString().replace("OR ()", "").trim();
  }

  public void removeDeletedNotes()
  {
    Timing t = new Timing("DB.removeDeletedNotes", context);
    SQLiteDatabase dbase = getDb();
    dbase.beginTransaction();

    try
    {
      dbase.execSQL(context.getString(R.string.sql_remove_deleted_notes));
    }
    finally
    {
      dbase.setTransactionSuccessful();
      dbase.endTransaction();
    }
    t.stop();
  }

  public void removeFeedFromFeeds2Unsubscribe(String feedAtomId)
  {
    getDb().delete(UnsubscribeFeeds.TABLE_NAME, UnsubscribeFeeds.FEED_ATOM_ID + " = ?", new String[] { feedAtomId });
  }

  public void removeLocallyExistingArticlesFromTempTable()
  {
    SQLiteDatabase dbase = getDb();
    dbase.beginTransaction();

    try
    {
      dbase.execSQL(expandTempTableName(context.getString(R.string.sql_delete_existing_from_temp_table), TempTable.READ));
    }
    finally
    {
      dbase.setTransactionSuccessful();
      dbase.endTransaction();
    }
  }

  public void removeLocallyExistingHashesFromTempTable()
  {
    SQLiteDatabase dbase = getDb();
    dbase.beginTransaction();

    try
    {
      dbase.execSQL(expandTempTableName(context.getString(R.string.sql_delete_existing_hashes_from_temp_table), TempTable.READ_HASHES));
    }
    finally
    {
      dbase.setTransactionSuccessful();
      dbase.endTransaction();
    }
  }

  void removePendingStateMarkers(Collection<String> atomIds, String column)
  {
    ContentValues cv = new ContentValues();
    if (Entries.READ_STATE_PENDING.equals(column))
    {
      cv.put(Entries.READ_STATE_PENDING, false);
    }
    else if (Entries.STARRED_STATE_PENDING.equals(column))
    {
      cv.put(Entries.STARRED_STATE_PENDING, false);
    }
    else if (Entries.LIKED_STATE_PENDING.equals(column))
    {
      cv.put(Entries.LIKED_STATE_PENDING, false);
    }
    else if (Entries.SHARED_STATE_PENDING.equals(column))
    {
      cv.put(Entries.SHARED_STATE_PENDING, false);
    }
    else if (Entries.PINNED_STATE_PENDING.equals(column))
    {
      cv.put(Entries.PINNED_STATE_PENDING, false);
    }

    for (String atomId : atomIds)
    {
      getDb().update(Entries.TABLE_NAME, cv, Entries.ATOM_ID + "=?", new String[] { atomId });
    }
  }

  void update(Entry entry)
  {
    ContentValues cv = mapEntryToContentValues(entry);
    getDb().update(Entries.TABLE_NAME, cv, Entries.__ID + " = ?", new String[] { String.valueOf(entry.getId()) });
  }

  boolean updateDownloaded(Entry entry)
  {
    ContentValues cv = new ContentValues();
    cv.put(Entries.DOWNLOADED, entry.getDownloaded());
    cv.put(Entries.ERROR, entry.getError());
    int result = getDb().update(Entries.TABLE_NAME, cv, Entries.ATOM_ID + "='" + entry.getAtomId() + "'", null);
    return result == 1;
  }

  boolean updateFeed(Feed feed)
  {

    ContentValues cv = mapFeedToContentValues(feed);

    return getDb().update(Feeds.TABLE_NAME, cv, Feeds.__ID + " = ?", new String[] { String.valueOf(feed.getId()) }) == 1;
  }

  public void updateFeedNames(Map<String, String> remoteFeedAtomIdsAndFeedTitles)
  {
    Timing t = new Timing("Updating feed names in DB", context);

    int updateCount = 0;
    Cursor c = null;
    try
    {
      SQLiteDatabase dbase = getDb();
      c = dbase.query(Feeds.TABLE_NAME, new String[] { Feeds.__ID, Feeds.ATOM_ID, Feeds.TITLE }, "1=1", null, null, null, null);
      while (c.moveToNext())
      {
        final long localId = c.getLong(0);
        final String localeAtomId = c.getString(1);
        final String localTitle = c.getString(2);

        if (remoteFeedAtomIdsAndFeedTitles.containsKey(localeAtomId))
        {
          final String remoteTitle = remoteFeedAtomIdsAndFeedTitles.get(localeAtomId);
          if ((localTitle == null) || !localTitle.equals(remoteTitle))
          {

            ContentValues cv = new ContentValues();
            cv.put(Feeds.TITLE, remoteTitle);

            dbase.update(Feeds.TABLE_NAME, cv, Feeds.__ID + " = ?", new String[] { String.valueOf(localId) });

            updateCount++;
          }

        }
      }

    }
    finally
    {
      if (c != null)
      {
        c.close();
      }
      t.stop();
      if (NewsRob.isDebuggingEnabled(context))
      {
        PL.log("DB::updateFeedNames updated " + updateCount + " feeds.", context);
      }
    }
  }

  void updateReadState(Entry entry)
  {

    ContentValues cv = new ContentValues();
    cv.put(Entries.READ_STATE, ReadState.toInt(entry.getReadState()));
    cv.put(Entries.READ_STATE_PENDING, entry.isReadStatePending() ? 1 : 0);
    cv.put(Entries.PINNED_STATE_PENDING, entry.isPinnedStatePending() ? 1 : 0);

    SQLiteDatabase db = getDb();
    Timing t = new Timing("DB.updateReadState execution", context);
    db.update(Entries.TABLE_NAME, cv, Entries.__ID + "= ?", new String[] { String.valueOf(entry.getId()) });
    t.stop();
  }

  void updateStarredState(Entry entry)
  {
    ContentValues cv = new ContentValues();
    cv.put(Entries.STARRED_STATE, entry.isStarred() ? 1 : 0);
    cv.put(Entries.STARRED_STATE_PENDING, entry.isStarredStatePending() ? 1 : 0);
    getDb().update(Entries.TABLE_NAME, cv, Entries.__ID + "=?", new String[] { String.valueOf(entry.getId()) });
  }

  public boolean updateStates(Collection<StateChange> stateChanges)
  {
    boolean updated = false;
    for (StateChange stateChange : stateChanges)
    {
      Cursor c = null;
      String valueColumnName = null;
      String stateColumnName = null;

      switch (stateChange.getState())
      {
        case BackendProvider.StateChange.STATE_READ:
          valueColumnName = Entries.READ_STATE;
          stateColumnName = Entries.READ_STATE_PENDING;
          break;
        case BackendProvider.StateChange.STATE_STARRED:
          valueColumnName = Entries.STARRED_STATE;
          stateColumnName = Entries.STARRED_STATE_PENDING;
          break;
        default:
          throw new IllegalArgumentException("state invalid: " + stateChange.getState());
      }
      try
      {
        String newValue = stateChange.getOperation() == BackendProvider.StateChange.OPERATION_REMOVE ? "0" : "1";
        c = getDb().query(
            Entries.TABLE_NAME,
            Entries.FIELD_NAMES,
            "atom_id = ? " + "AND ((" + valueColumnName + " != " + newValue + " AND " + stateColumnName + " = 0) OR " + "(" + valueColumnName
                + " = " + newValue + " AND " + stateColumnName + " = 1))", new String[] { stateChange.getAtomId() }, null, null, null);

        if (c.moveToFirst())
        {
          ContentValues cv = new ContentValues();
          cv.put(valueColumnName, newValue);
          if (!stateColumnName.equals("0"))
          {
            cv.put(stateColumnName, "0");
          }
          int rowsAffected = getDb().update(Entries.TABLE_NAME, cv, Entries.ATOM_ID + " = ?", new String[] { stateChange.getAtomId() });
          if (rowsAffected > 0)
          {
            updated = true;
          }
        } // else nothing to do.
      }
      finally
      {
        if (c != null)
        {
          c.close();
        }
      }
    }
    return updated;
  }

  public void updateStatesFromTempTable(TempTable tempTableType, ArticleDbState state)
  {

    Timing t = new Timing("DB.updateStatesFromTempTable state=" + state, context);
    PL.log("DB.updateStatesFromTempTable state=" + state, context);
    String sql;
    String stateColumn = null;
    String statePendingColumn = null;
    int targetValueOn = 1;
    int targetValueOff = 0;
    boolean mergePinned = false;

    if (state == ArticleDbState.READ)
    {
      stateColumn = Entries.READ_STATE;
      statePendingColumn = Entries.READ_STATE_PENDING;
      targetValueOn = 0;
      targetValueOff = 1;
      mergePinned = false;
    }
    else if (state == ArticleDbState.STARRED)
    {
      stateColumn = Entries.STARRED_STATE;
      statePendingColumn = Entries.STARRED_STATE_PENDING;
    }
    else if (state == ArticleDbState.PINNED)
    {
      stateColumn = Entries.READ_STATE;
      statePendingColumn = Entries.PINNED_STATE_PENDING;
      mergePinned = true;
    }

    if (stateColumn == null)
    {
      throw new IllegalStateException("stateColumn must not be null here.");
    }

    SQLiteDatabase dbase = getDb();
    try
    {
      dbase.beginTransaction();

      if (state != ArticleDbState.PINNED)
      {
        // Mark all articles as read where the read_state is not pending
        // and they were not read before
        Timing t3 = new Timing("DB.updateStatesFromTempTable - mark existing read", context);
        final String markExistingSQL = "UPDATE " + Entries.TABLE_NAME + " SET " + stateColumn + " = " + targetValueOff + " WHERE " + stateColumn
            + " = " + targetValueOn + " AND " + statePendingColumn + " = 0;";
        dbase.execSQL(markExistingSQL);
        t3.stop();

        // Mark all articles unread that exists in the temp table and are
        // not read state pending

        Timing t4 = new Timing("DB.updateStatesFromTempTable - mark as x", context);

        sql = context.getString(R.string.sql_mark_as_x);
        sql = sql.replaceAll("-STATE-", stateColumn);
        sql = sql.replaceAll("-STATE_PENDING-", statePendingColumn);
        sql = sql.replaceAll("-SET-", targetValueOn + "");
        sql = sql.replaceAll("-CLEAR-", targetValueOff + "");

        dbase.execSQL(expandTempTableName(sql, tempTableType));
        t4.stop();

        if (state == ArticleDbState.READ)
        {
          Timing t5 = new Timing("DB.updateReadStates - mark as read even when pinned", context);

          dbase.execSQL(expandTempTableName(context.getString(R.string.sql_mark_as_read_even_when_pinned), tempTableType));
          t5.stop();
        }
      }

      if (mergePinned)
      {
        Timing t6 = new Timing("DB.updateReadStates - mark pinned as pinned", context);

        sql = expandTempTableName(context.getString(R.string.sql_mark_as_x), TempTable.PINNED);
        sql = sql.replaceAll("-STATE-", stateColumn); // READ STATE
        sql = sql.replaceAll("-STATE_PENDING-", Entries.READ_STATE_PENDING + " = 0 AND " + Entries.PINNED_STATE_PENDING);
        sql = sql.replaceAll("-SET-", "-1");
        sql = sql.replaceAll("-CLEAR-", "0");
        PL.log("DB.updateStatesFromTempTable Executing sql=" + sql, context);
        // -- Setting Pinned state for all articles that are unread
        // and for which in temp_ids_pinned a record exists
        // and for which neither a read_state_pending nor a
        // pinned_state_pending is set.

        dbase.execSQL(sql);
        sql = sql.replaceAll("EXISTS", "NOT EXISTS").replaceAll("-1", "0").replaceAll("entries.READ_STATE = 0", "entries.READ_STATE = -1");
        PL.log("DB.updateStatesFromTempTable Executing sql2=" + sql, context);

        // -- Setting Unread state for all articles that are pinned
        // and for which in temp_ids_pinned no record exists
        // and for which neither a read_state_pending nor a
        // pinned_state_pending is set.
        dbase.execSQL(sql);

        t6.stop();
      }
    }
    finally
    {
      dbase.setTransactionSuccessful();
      dbase.endTransaction();
      t.stop();
    }

  }

  public void updateStatesFromTempTableHash(TempTable tempTableType, ArticleDbState state)
  {

    Timing t = new Timing("DB.updateStatesFromTempTable state=" + state, context);
    PL.log("DB.updateStatesFromTempTable state=" + state, context);
    String stateColumn = null;
    String statePendingColumn = null;
    int targetValueOn = 1;
    int targetValueOff = 0;
    boolean mergePinned = false;

    if (state == ArticleDbState.READ)
    {
      stateColumn = Entries.READ_STATE;
      statePendingColumn = Entries.READ_STATE_PENDING;
      targetValueOn = 0;
      targetValueOff = 1;
      mergePinned = false;

    }
    else if (state == ArticleDbState.STARRED)
    {
      stateColumn = Entries.STARRED_STATE;
      statePendingColumn = Entries.STARRED_STATE_PENDING;

    }
    // PINNED STATE ISSUE
    else if (state == ArticleDbState.PINNED)
    {
      throw new RuntimeException("Boy, this was unexpected!");
      /*
       * stateColumn = Entries.READ_STATE; statePendingColumn = Entries.PINNED_STATE_PENDING;
       */
    }

    if (stateColumn == null)
    {
      throw new IllegalStateException("stateColumn must not be null here.");
    }

    SQLiteDatabase dbase = getDb();
    try
    {
      dbase.beginTransaction();

      // Mark all articles as read where the read_state is not pending
      // and they were not read before
      Timing t3 = new Timing("DB.updateStatesFromTempTable - mark existing read", context);
      final String markExistingSQL = "UPDATE " + Entries.TABLE_NAME + " SET " + stateColumn + " = " + targetValueOff + " WHERE " + stateColumn
          + " = " + targetValueOn + " AND " + statePendingColumn + " = 0;";
      dbase.execSQL(markExistingSQL);
      t3.stop();

      // Mark all articles unread that exists in the temp table and are
      // not
      // read state pending

      Timing t4 = new Timing("DB.updateStatesFromTempTable - mark as x", context);

      String sql = context.getString(R.string.sql_mark_as_x_hash);
      sql = sql.replaceAll("-STATE-", stateColumn);
      sql = sql.replaceAll("-STATE_PENDING-", statePendingColumn);
      sql = sql.replaceAll("-SET-", targetValueOn + "");
      sql = sql.replaceAll("-CLEAR-", targetValueOff + "");

      dbase.execSQL(expandTempTableName(sql, tempTableType));
      t4.stop();

      if (state == ArticleDbState.READ)
      {
        Timing t5 = new Timing("DB.updateReadStates - mark as read even when pinned", context);

        dbase.execSQL(expandTempTableName(context.getString(R.string.sql_mark_as_read_even_when_pinned_hash), tempTableType));
        t5.stop();
      }

      // I think this throws exceptions due to the table not existing, but
      // since we aren't supporting pinned states on Newsblur yet and
      // that's where this is being called from, disable it for now.
      /*
       * if (false && mergePinned) { Timing t6 = new Timing("DB.updateReadStates - mark pinned as pinned", context);
       * 
       * sql = expandTempTableName(context.getString(R.string.sql_mark_as_x_hash ), TempTable.PINNED); sql = sql.replaceAll("-STATE-", stateColumn); // READ
       * STATE sql = sql.replaceAll("-STATE_PENDING-", Entries.READ_STATE_PENDING + " = 0 AND " + Entries.PINNED_STATE_PENDING); sql = sql.replaceAll("-SET-",
       * "-1"); sql = sql.replaceAll("-CLEAR-", "0"); PL.log("DB.updateStatesFromTempTable Executing sql=" + sql, context); // -- Setting Pinned state for all
       * articles that are unread // and for which in temp_ids_pinned a record exists // and for which neither a read_state_pending nor a //
       * pinned_state_pending is set.
       * 
       * dbase.execSQL(sql); sql = sql.replaceAll("EXISTS", "NOT EXISTS").replaceAll("-1", "0") .replaceAll("entries.READ_STATE = 0",
       * "entries.READ_STATE = -1"); PL.log("DB.updateStatesFromTempTable Executing sql2=" + sql, context);
       * 
       * // -- Setting Unread state for all articles that are pinned // and for which in temp_ids_pinned no record exists // and for which neither a
       * read_state_pending nor a // pinned_state_pending is set. dbase.execSQL(sql);
       * 
       * t6.stop(); }
       */
    }
    finally
    {
      dbase.setTransactionSuccessful();
      dbase.endTransaction();
      t.stop();
    }
  }

  public void vacuum(Context context)
  {
    if ("1".equals(NewsRob.getDebugProperties(context).getProperty("dbVacuumEnabled", "0")))
    {
      if (U.isScreenOn(context))
      {
        PL.log("Vacuuming skipped, because the screen was on.", context);
        return;
      }
      Timing t = new Timing("Vacuuming ...", context);
      try
      {
        PL.log("Vacuuming ...", context);
        getDb().execSQL("VACUUM");
        PL.log("Vacuuming done.", context);
      }
      catch (Throwable thr)
      {
        PL.log("Vacuuming done with error.", thr, context);
      }
      finally
      {
        t.stop();
      }
    }
  }

}