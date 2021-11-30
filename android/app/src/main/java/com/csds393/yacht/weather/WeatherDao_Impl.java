package com.csds393.yacht.weather;

import android.database.Cursor;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.csds393.yacht.database.Converters;
import java.lang.Class;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kotlin.ranges.IntRange;

@SuppressWarnings({"unchecked", "deprecation"})
public final class WeatherDao_Impl implements WeatherDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DayWeather> __insertionAdapterOfDayWeather;

  private final SharedSQLiteStatement __preparedStmtOfClearAllForecasts;

  public WeatherDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDayWeather = new EntityInsertionAdapter<DayWeather>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `daily_forecasts` (`date`,`latitude`,`longitude`,`dateRetrieved`,`daytemperature`,`daywindSpeed`,`daysky`,`dayshortForecast`,`daydetailedForecast`,`nighttemperature`,`nightwindSpeed`,`nightsky`,`nightshortForecast`,`nightdetailedForecast`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DayWeather value) {
        final Integer _tmp;
        _tmp = Converters.localDateToInt(value.getDate());
        if (_tmp == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindLong(1, _tmp);
        }
        stmt.bindDouble(2, value.getLatitude());
        stmt.bindDouble(3, value.getLongitude());
        final Long _tmp_1;
        _tmp_1 = Converters.zonedDateTimeToLong(value.getDateRetrieved());
        if (_tmp_1 == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindLong(4, _tmp_1);
        }
        final HalfDayWeather _tmpMorningWeather = value.getMorningWeather();
        if(_tmpMorningWeather != null) {
          stmt.bindLong(5, _tmpMorningWeather.getTemperature());
          final Long _tmp_2;
          _tmp_2 = Converters.intRangeToLong(_tmpMorningWeather.getWindSpeed());
          if (_tmp_2 == null) {
            stmt.bindNull(6);
          } else {
            stmt.bindLong(6, _tmp_2);
          }
          if (_tmpMorningWeather.getSky() == null) {
            stmt.bindNull(7);
          } else {
            stmt.bindString(7, __Sky_enumToString(_tmpMorningWeather.getSky()));
          }
          if (_tmpMorningWeather.getShortForecast() == null) {
            stmt.bindNull(8);
          } else {
            stmt.bindString(8, _tmpMorningWeather.getShortForecast());
          }
          if (_tmpMorningWeather.getDetailedForecast() == null) {
            stmt.bindNull(9);
          } else {
            stmt.bindString(9, _tmpMorningWeather.getDetailedForecast());
          }
        } else {
          stmt.bindNull(5);
          stmt.bindNull(6);
          stmt.bindNull(7);
          stmt.bindNull(8);
          stmt.bindNull(9);
        }
        final HalfDayWeather _tmpNightWeather = value.getNightWeather();
        if(_tmpNightWeather != null) {
          stmt.bindLong(10, _tmpNightWeather.getTemperature());
          final Long _tmp_3;
          _tmp_3 = Converters.intRangeToLong(_tmpNightWeather.getWindSpeed());
          if (_tmp_3 == null) {
            stmt.bindNull(11);
          } else {
            stmt.bindLong(11, _tmp_3);
          }
          if (_tmpNightWeather.getSky() == null) {
            stmt.bindNull(12);
          } else {
            stmt.bindString(12, __Sky_enumToString(_tmpNightWeather.getSky()));
          }
          if (_tmpNightWeather.getShortForecast() == null) {
            stmt.bindNull(13);
          } else {
            stmt.bindString(13, _tmpNightWeather.getShortForecast());
          }
          if (_tmpNightWeather.getDetailedForecast() == null) {
            stmt.bindNull(14);
          } else {
            stmt.bindString(14, _tmpNightWeather.getDetailedForecast());
          }
        } else {
          stmt.bindNull(10);
          stmt.bindNull(11);
          stmt.bindNull(12);
          stmt.bindNull(13);
          stmt.bindNull(14);
        }
      }
    };
    this.__preparedStmtOfClearAllForecasts = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM daily_forecasts";
        return _query;
      }
    };
  }

  @Override
  public void insertForecast(final DayWeather dayWeather) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfDayWeather.insert(dayWeather);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertForecasts(final List<DayWeather> dayWeathers) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfDayWeather.insert(dayWeathers);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void clearAllForecasts() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllForecasts.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfClearAllForecasts.release(_stmt);
    }
  }

  @Override
  public List<DayWeather> getWeather(final double latitude, final double longitude) {
    final String _sql = "SELECT * FROM daily_forecasts WHERE ? == latitude AND ? == longitude ORDER BY date";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindDouble(_argIndex, latitude);
    _argIndex = 2;
    _statement.bindDouble(_argIndex, longitude);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfDateRetrieved = CursorUtil.getColumnIndexOrThrow(_cursor, "dateRetrieved");
      final int _cursorIndexOfTemperature = CursorUtil.getColumnIndexOrThrow(_cursor, "daytemperature");
      final int _cursorIndexOfWindSpeed = CursorUtil.getColumnIndexOrThrow(_cursor, "daywindSpeed");
      final int _cursorIndexOfSky = CursorUtil.getColumnIndexOrThrow(_cursor, "daysky");
      final int _cursorIndexOfShortForecast = CursorUtil.getColumnIndexOrThrow(_cursor, "dayshortForecast");
      final int _cursorIndexOfDetailedForecast = CursorUtil.getColumnIndexOrThrow(_cursor, "daydetailedForecast");
      final int _cursorIndexOfTemperature_1 = CursorUtil.getColumnIndexOrThrow(_cursor, "nighttemperature");
      final int _cursorIndexOfWindSpeed_1 = CursorUtil.getColumnIndexOrThrow(_cursor, "nightwindSpeed");
      final int _cursorIndexOfSky_1 = CursorUtil.getColumnIndexOrThrow(_cursor, "nightsky");
      final int _cursorIndexOfShortForecast_1 = CursorUtil.getColumnIndexOrThrow(_cursor, "nightshortForecast");
      final int _cursorIndexOfDetailedForecast_1 = CursorUtil.getColumnIndexOrThrow(_cursor, "nightdetailedForecast");
      final List<DayWeather> _result = new ArrayList<DayWeather>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final DayWeather _item;
        final LocalDate _tmpDate;
        final Integer _tmp;
        if (_cursor.isNull(_cursorIndexOfDate)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getInt(_cursorIndexOfDate);
        }
        _tmpDate = Converters.intToLocalDate(_tmp);
        final double _tmpLatitude;
        _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
        final double _tmpLongitude;
        _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
        final ZonedDateTime _tmpDateRetrieved;
        final Long _tmp_1;
        if (_cursor.isNull(_cursorIndexOfDateRetrieved)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getLong(_cursorIndexOfDateRetrieved);
        }
        _tmpDateRetrieved = Converters.longToZonedDateTime(_tmp_1);
        final HalfDayWeather _tmpMorningWeather;
        if (! (_cursor.isNull(_cursorIndexOfTemperature) && _cursor.isNull(_cursorIndexOfWindSpeed) && _cursor.isNull(_cursorIndexOfSky) && _cursor.isNull(_cursorIndexOfShortForecast) && _cursor.isNull(_cursorIndexOfDetailedForecast))) {
          final int _tmpTemperature;
          _tmpTemperature = _cursor.getInt(_cursorIndexOfTemperature);
          final IntRange _tmpWindSpeed;
          final Long _tmp_2;
          if (_cursor.isNull(_cursorIndexOfWindSpeed)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _cursor.getLong(_cursorIndexOfWindSpeed);
          }
          _tmpWindSpeed = Converters.longToIntRange(_tmp_2);
          final Sky _tmpSky;
          _tmpSky = __Sky_stringToEnum(_cursor.getString(_cursorIndexOfSky));
          final String _tmpShortForecast;
          if (_cursor.isNull(_cursorIndexOfShortForecast)) {
            _tmpShortForecast = null;
          } else {
            _tmpShortForecast = _cursor.getString(_cursorIndexOfShortForecast);
          }
          final String _tmpDetailedForecast;
          if (_cursor.isNull(_cursorIndexOfDetailedForecast)) {
            _tmpDetailedForecast = null;
          } else {
            _tmpDetailedForecast = _cursor.getString(_cursorIndexOfDetailedForecast);
          }
          _tmpMorningWeather = new HalfDayWeather(_tmpTemperature,_tmpWindSpeed,_tmpSky,_tmpShortForecast,_tmpDetailedForecast);
        }  else  {
          _tmpMorningWeather = null;
        }
        final HalfDayWeather _tmpNightWeather;
        if (! (_cursor.isNull(_cursorIndexOfTemperature_1) && _cursor.isNull(_cursorIndexOfWindSpeed_1) && _cursor.isNull(_cursorIndexOfSky_1) && _cursor.isNull(_cursorIndexOfShortForecast_1) && _cursor.isNull(_cursorIndexOfDetailedForecast_1))) {
          final int _tmpTemperature_1;
          _tmpTemperature_1 = _cursor.getInt(_cursorIndexOfTemperature_1);
          final IntRange _tmpWindSpeed_1;
          final Long _tmp_3;
          if (_cursor.isNull(_cursorIndexOfWindSpeed_1)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _cursor.getLong(_cursorIndexOfWindSpeed_1);
          }
          _tmpWindSpeed_1 = Converters.longToIntRange(_tmp_3);
          final Sky _tmpSky_1;
          _tmpSky_1 = __Sky_stringToEnum(_cursor.getString(_cursorIndexOfSky_1));
          final String _tmpShortForecast_1;
          if (_cursor.isNull(_cursorIndexOfShortForecast_1)) {
            _tmpShortForecast_1 = null;
          } else {
            _tmpShortForecast_1 = _cursor.getString(_cursorIndexOfShortForecast_1);
          }
          final String _tmpDetailedForecast_1;
          if (_cursor.isNull(_cursorIndexOfDetailedForecast_1)) {
            _tmpDetailedForecast_1 = null;
          } else {
            _tmpDetailedForecast_1 = _cursor.getString(_cursorIndexOfDetailedForecast_1);
          }
          _tmpNightWeather = new HalfDayWeather(_tmpTemperature_1,_tmpWindSpeed_1,_tmpSky_1,_tmpShortForecast_1,_tmpDetailedForecast_1);
        }  else  {
          _tmpNightWeather = null;
        }
        _item = new DayWeather(_tmpDate,_tmpLatitude,_tmpLongitude,_tmpDateRetrieved,_tmpMorningWeather,_tmpNightWeather);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<DayWeather> getAllWeather() {
    final String _sql = "SELECT * FROM daily_forecasts ORDER BY date";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfDateRetrieved = CursorUtil.getColumnIndexOrThrow(_cursor, "dateRetrieved");
      final int _cursorIndexOfTemperature = CursorUtil.getColumnIndexOrThrow(_cursor, "daytemperature");
      final int _cursorIndexOfWindSpeed = CursorUtil.getColumnIndexOrThrow(_cursor, "daywindSpeed");
      final int _cursorIndexOfSky = CursorUtil.getColumnIndexOrThrow(_cursor, "daysky");
      final int _cursorIndexOfShortForecast = CursorUtil.getColumnIndexOrThrow(_cursor, "dayshortForecast");
      final int _cursorIndexOfDetailedForecast = CursorUtil.getColumnIndexOrThrow(_cursor, "daydetailedForecast");
      final int _cursorIndexOfTemperature_1 = CursorUtil.getColumnIndexOrThrow(_cursor, "nighttemperature");
      final int _cursorIndexOfWindSpeed_1 = CursorUtil.getColumnIndexOrThrow(_cursor, "nightwindSpeed");
      final int _cursorIndexOfSky_1 = CursorUtil.getColumnIndexOrThrow(_cursor, "nightsky");
      final int _cursorIndexOfShortForecast_1 = CursorUtil.getColumnIndexOrThrow(_cursor, "nightshortForecast");
      final int _cursorIndexOfDetailedForecast_1 = CursorUtil.getColumnIndexOrThrow(_cursor, "nightdetailedForecast");
      final List<DayWeather> _result = new ArrayList<DayWeather>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final DayWeather _item;
        final LocalDate _tmpDate;
        final Integer _tmp;
        if (_cursor.isNull(_cursorIndexOfDate)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getInt(_cursorIndexOfDate);
        }
        _tmpDate = Converters.intToLocalDate(_tmp);
        final double _tmpLatitude;
        _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
        final double _tmpLongitude;
        _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
        final ZonedDateTime _tmpDateRetrieved;
        final Long _tmp_1;
        if (_cursor.isNull(_cursorIndexOfDateRetrieved)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getLong(_cursorIndexOfDateRetrieved);
        }
        _tmpDateRetrieved = Converters.longToZonedDateTime(_tmp_1);
        final HalfDayWeather _tmpMorningWeather;
        if (! (_cursor.isNull(_cursorIndexOfTemperature) && _cursor.isNull(_cursorIndexOfWindSpeed) && _cursor.isNull(_cursorIndexOfSky) && _cursor.isNull(_cursorIndexOfShortForecast) && _cursor.isNull(_cursorIndexOfDetailedForecast))) {
          final int _tmpTemperature;
          _tmpTemperature = _cursor.getInt(_cursorIndexOfTemperature);
          final IntRange _tmpWindSpeed;
          final Long _tmp_2;
          if (_cursor.isNull(_cursorIndexOfWindSpeed)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _cursor.getLong(_cursorIndexOfWindSpeed);
          }
          _tmpWindSpeed = Converters.longToIntRange(_tmp_2);
          final Sky _tmpSky;
          _tmpSky = __Sky_stringToEnum(_cursor.getString(_cursorIndexOfSky));
          final String _tmpShortForecast;
          if (_cursor.isNull(_cursorIndexOfShortForecast)) {
            _tmpShortForecast = null;
          } else {
            _tmpShortForecast = _cursor.getString(_cursorIndexOfShortForecast);
          }
          final String _tmpDetailedForecast;
          if (_cursor.isNull(_cursorIndexOfDetailedForecast)) {
            _tmpDetailedForecast = null;
          } else {
            _tmpDetailedForecast = _cursor.getString(_cursorIndexOfDetailedForecast);
          }
          _tmpMorningWeather = new HalfDayWeather(_tmpTemperature,_tmpWindSpeed,_tmpSky,_tmpShortForecast,_tmpDetailedForecast);
        }  else  {
          _tmpMorningWeather = null;
        }
        final HalfDayWeather _tmpNightWeather;
        if (! (_cursor.isNull(_cursorIndexOfTemperature_1) && _cursor.isNull(_cursorIndexOfWindSpeed_1) && _cursor.isNull(_cursorIndexOfSky_1) && _cursor.isNull(_cursorIndexOfShortForecast_1) && _cursor.isNull(_cursorIndexOfDetailedForecast_1))) {
          final int _tmpTemperature_1;
          _tmpTemperature_1 = _cursor.getInt(_cursorIndexOfTemperature_1);
          final IntRange _tmpWindSpeed_1;
          final Long _tmp_3;
          if (_cursor.isNull(_cursorIndexOfWindSpeed_1)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _cursor.getLong(_cursorIndexOfWindSpeed_1);
          }
          _tmpWindSpeed_1 = Converters.longToIntRange(_tmp_3);
          final Sky _tmpSky_1;
          _tmpSky_1 = __Sky_stringToEnum(_cursor.getString(_cursorIndexOfSky_1));
          final String _tmpShortForecast_1;
          if (_cursor.isNull(_cursorIndexOfShortForecast_1)) {
            _tmpShortForecast_1 = null;
          } else {
            _tmpShortForecast_1 = _cursor.getString(_cursorIndexOfShortForecast_1);
          }
          final String _tmpDetailedForecast_1;
          if (_cursor.isNull(_cursorIndexOfDetailedForecast_1)) {
            _tmpDetailedForecast_1 = null;
          } else {
            _tmpDetailedForecast_1 = _cursor.getString(_cursorIndexOfDetailedForecast_1);
          }
          _tmpNightWeather = new HalfDayWeather(_tmpTemperature_1,_tmpWindSpeed_1,_tmpSky_1,_tmpShortForecast_1,_tmpDetailedForecast_1);
        }  else  {
          _tmpNightWeather = null;
        }
        _item = new DayWeather(_tmpDate,_tmpLatitude,_tmpLongitude,_tmpDateRetrieved,_tmpMorningWeather,_tmpNightWeather);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ZonedDateTime> _getDateRetrievedForForecast(final LocalDate date) {
    final String _sql = "SELECT dateRetrieved FROM daily_forecasts WHERE date == ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final Integer _tmp;
    _tmp = Converters.localDateToInt(date);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, _tmp);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final List<ZonedDateTime> _result = new ArrayList<ZonedDateTime>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ZonedDateTime _item;
        final Long _tmp_1;
        if (_cursor.isNull(0)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getLong(0);
        }
        _item = Converters.longToZonedDateTime(_tmp_1);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __Sky_enumToString(final Sky _value) {
    if (_value == null) {
      return null;
    } switch (_value) {
      case SNOW: return "SNOW";
      case SUNNY: return "SUNNY";
      case CLOUDY: return "CLOUDY";
      case RAIN: return "RAIN";
      case CLEAR: return "CLEAR";
      default: return "CLEAR"; // Protective Programming
    }
  }

  private Sky __Sky_stringToEnum(final String _value) {
    if (_value == null) {
      return null;
    } switch (_value) {
      case "SNOW": return Sky.SNOW;
      case "SUNNY": return Sky.SUNNY;
      case "CLOUDY": return Sky.CLOUDY;
      case "RAIN": return Sky.RAIN;
      case "CLEAR": return Sky.CLEAR;
      default: return Sky.CLEAR; // This is so that it doesn't throw weird things
    }
  }
}
