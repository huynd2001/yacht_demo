package com.csds393.yacht.database;

import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import com.csds393.yacht.calendar.CalendarDao;
import com.csds393.yacht.calendar.CalendarDao_Impl;
import com.csds393.yacht.weather.WeatherDao;
import com.csds393.yacht.weather.WeatherDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class DB_Impl extends DB {
  private volatile CalendarDao _calendarDao;

  private volatile WeatherDao _weatherDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(5) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `normal_events` (`startDate` INTEGER NOT NULL, `startTime` INTEGER, `endDate` INTEGER NOT NULL, `endTime` INTEGER, `id` INTEGER, `label` TEXT NOT NULL, `description` TEXT NOT NULL, PRIMARY KEY(`id`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `recurring_events` (`activeWindow` INTEGER NOT NULL, `datePattern` INTEGER NOT NULL, `rec_id` INTEGER PRIMARY KEY AUTOINCREMENT, `startDate` INTEGER NOT NULL, `startTime` INTEGER, `endDate` INTEGER NOT NULL, `endTime` INTEGER, `id` INTEGER, `label` TEXT NOT NULL, `description` TEXT NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `recurrence_exceptions` (`date` INTEGER NOT NULL, `event_id` INTEGER NOT NULL, PRIMARY KEY(`date`, `event_id`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `daily_forecasts` (`date` INTEGER NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `dateRetrieved` INTEGER NOT NULL, `daytemperature` INTEGER NOT NULL, `daywindSpeed` INTEGER NOT NULL, `daysky` TEXT NOT NULL, `dayshortForecast` TEXT NOT NULL, `daydetailedForecast` TEXT NOT NULL, `nighttemperature` INTEGER NOT NULL, `nightwindSpeed` INTEGER NOT NULL, `nightsky` TEXT NOT NULL, `nightshortForecast` TEXT NOT NULL, `nightdetailedForecast` TEXT NOT NULL, PRIMARY KEY(`date`, `latitude`, `longitude`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'cbc5dc7df198ececf9ea1428215c1dd5')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `normal_events`");
        _db.execSQL("DROP TABLE IF EXISTS `recurring_events`");
        _db.execSQL("DROP TABLE IF EXISTS `recurrence_exceptions`");
        _db.execSQL("DROP TABLE IF EXISTS `daily_forecasts`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      protected RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsNormalEvents = new HashMap<String, TableInfo.Column>(7);
        _columnsNormalEvents.put("startDate", new TableInfo.Column("startDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNormalEvents.put("startTime", new TableInfo.Column("startTime", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNormalEvents.put("endDate", new TableInfo.Column("endDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNormalEvents.put("endTime", new TableInfo.Column("endTime", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNormalEvents.put("id", new TableInfo.Column("id", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNormalEvents.put("label", new TableInfo.Column("label", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNormalEvents.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNormalEvents = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNormalEvents = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNormalEvents = new TableInfo("normal_events", _columnsNormalEvents, _foreignKeysNormalEvents, _indicesNormalEvents);
        final TableInfo _existingNormalEvents = TableInfo.read(_db, "normal_events");
        if (! _infoNormalEvents.equals(_existingNormalEvents)) {
          return new RoomOpenHelper.ValidationResult(false, "normal_events(com.csds393.yacht.calendar.CalendarEvent).\n"
                  + " Expected:\n" + _infoNormalEvents + "\n"
                  + " Found:\n" + _existingNormalEvents);
        }
        final HashMap<String, TableInfo.Column> _columnsRecurringEvents = new HashMap<String, TableInfo.Column>(10);
        _columnsRecurringEvents.put("activeWindow", new TableInfo.Column("activeWindow", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringEvents.put("datePattern", new TableInfo.Column("datePattern", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringEvents.put("rec_id", new TableInfo.Column("rec_id", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringEvents.put("startDate", new TableInfo.Column("startDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringEvents.put("startTime", new TableInfo.Column("startTime", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringEvents.put("endDate", new TableInfo.Column("endDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringEvents.put("endTime", new TableInfo.Column("endTime", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringEvents.put("id", new TableInfo.Column("id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringEvents.put("label", new TableInfo.Column("label", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringEvents.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecurringEvents = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRecurringEvents = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRecurringEvents = new TableInfo("recurring_events", _columnsRecurringEvents, _foreignKeysRecurringEvents, _indicesRecurringEvents);
        final TableInfo _existingRecurringEvents = TableInfo.read(_db, "recurring_events");
        if (! _infoRecurringEvents.equals(_existingRecurringEvents)) {
          return new RoomOpenHelper.ValidationResult(false, "recurring_events(com.csds393.yacht.calendar.RecurringCalendarEvent).\n"
                  + " Expected:\n" + _infoRecurringEvents + "\n"
                  + " Found:\n" + _existingRecurringEvents);
        }
        final HashMap<String, TableInfo.Column> _columnsRecurrenceExceptions = new HashMap<String, TableInfo.Column>(2);
        _columnsRecurrenceExceptions.put("date", new TableInfo.Column("date", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurrenceExceptions.put("event_id", new TableInfo.Column("event_id", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecurrenceExceptions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRecurrenceExceptions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRecurrenceExceptions = new TableInfo("recurrence_exceptions", _columnsRecurrenceExceptions, _foreignKeysRecurrenceExceptions, _indicesRecurrenceExceptions);
        final TableInfo _existingRecurrenceExceptions = TableInfo.read(_db, "recurrence_exceptions");
        if (! _infoRecurrenceExceptions.equals(_existingRecurrenceExceptions)) {
          return new RoomOpenHelper.ValidationResult(false, "recurrence_exceptions(com.csds393.yacht.calendar.RecurringCalendarEvent.Exception).\n"
                  + " Expected:\n" + _infoRecurrenceExceptions + "\n"
                  + " Found:\n" + _existingRecurrenceExceptions);
        }
        final HashMap<String, TableInfo.Column> _columnsDailyForecasts = new HashMap<String, TableInfo.Column>(14);
        _columnsDailyForecasts.put("date", new TableInfo.Column("date", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyForecasts.put("latitude", new TableInfo.Column("latitude", "REAL", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyForecasts.put("longitude", new TableInfo.Column("longitude", "REAL", true, 3, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyForecasts.put("dateRetrieved", new TableInfo.Column("dateRetrieved", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyForecasts.put("daytemperature", new TableInfo.Column("daytemperature", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyForecasts.put("daywindSpeed", new TableInfo.Column("daywindSpeed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyForecasts.put("daysky", new TableInfo.Column("daysky", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyForecasts.put("dayshortForecast", new TableInfo.Column("dayshortForecast", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyForecasts.put("daydetailedForecast", new TableInfo.Column("daydetailedForecast", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyForecasts.put("nighttemperature", new TableInfo.Column("nighttemperature", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyForecasts.put("nightwindSpeed", new TableInfo.Column("nightwindSpeed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyForecasts.put("nightsky", new TableInfo.Column("nightsky", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyForecasts.put("nightshortForecast", new TableInfo.Column("nightshortForecast", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyForecasts.put("nightdetailedForecast", new TableInfo.Column("nightdetailedForecast", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyForecasts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyForecasts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDailyForecasts = new TableInfo("daily_forecasts", _columnsDailyForecasts, _foreignKeysDailyForecasts, _indicesDailyForecasts);
        final TableInfo _existingDailyForecasts = TableInfo.read(_db, "daily_forecasts");
        if (! _infoDailyForecasts.equals(_existingDailyForecasts)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_forecasts(com.csds393.yacht.weather.DayWeather).\n"
                  + " Expected:\n" + _infoDailyForecasts + "\n"
                  + " Found:\n" + _existingDailyForecasts);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "cbc5dc7df198ececf9ea1428215c1dd5", "4910657fc6c8143fff8cff318589e34c");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
            .name(configuration.name)
            .callback(_openCallback)
            .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "normal_events","recurring_events","recurrence_exceptions","daily_forecasts");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `normal_events`");
      _db.execSQL("DELETE FROM `recurring_events`");
      _db.execSQL("DELETE FROM `recurrence_exceptions`");
      _db.execSQL("DELETE FROM `daily_forecasts`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(CalendarDao.class, CalendarDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WeatherDao.class, WeatherDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  public CalendarDao getCalendarDao() {
    if (_calendarDao != null) {
      return _calendarDao;
    } else {
      synchronized(this) {
        if(_calendarDao == null) {
          _calendarDao = new CalendarDao_Impl(this);
        }
        return _calendarDao;
      }
    }
  }

  @Override
  public WeatherDao getWeatherDao() {
    if (_weatherDao != null) {
      return _weatherDao;
    } else {
      synchronized(this) {
        if(_weatherDao == null) {
          _weatherDao = new WeatherDao_Impl(this);
        }
        return _weatherDao;
      }
    }
  }
}
