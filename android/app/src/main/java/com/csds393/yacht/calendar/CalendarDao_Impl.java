package com.csds393.yacht.calendar;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.csds393.yacht.database.Converters;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import kotlin.ranges.ClosedRange;

@SuppressWarnings({"unchecked", "deprecation"})
public final class CalendarDao_Impl implements CalendarDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CalendarEvent> __insertionAdapterOfCalendarEvent;

  private final EntityInsertionAdapter<RecurringCalendarEvent> __insertionAdapterOfRecurringCalendarEvent;

  private final EntityInsertionAdapter<RecurringCalendarEvent.Exception> __insertionAdapterOfException;

  private final EntityDeletionOrUpdateAdapter<CalendarEvent> __deletionAdapterOfCalendarEvent;

  private final EntityDeletionOrUpdateAdapter<RecurringCalendarEvent> __deletionAdapterOfRecurringCalendarEvent;

  private final EntityDeletionOrUpdateAdapter<CalendarEvent> __updateAdapterOfCalendarEvent;

  private final EntityDeletionOrUpdateAdapter<RecurringCalendarEvent> __updateAdapterOfRecurringCalendarEvent;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllExceptionsForRecurringEvent;

  private final SharedSQLiteStatement __preparedStmtOf_clearAllNormalEvents;

  private final SharedSQLiteStatement __preparedStmtOf_clearAllRecurringEvents;

  private final SharedSQLiteStatement __preparedStmtOf_clearAllRecurrenceExceptions;

  public CalendarDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCalendarEvent = new EntityInsertionAdapter<CalendarEvent>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `normal_events` (`startDate`,`startTime`,`endDate`,`endTime`,`id`,`label`,`description`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CalendarEvent value) {
        final Integer _tmp;
        _tmp = Converters.localDateToInt(value.getStartDate());
        if (_tmp == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindLong(1, _tmp);
        }
        final Long _tmp_1;
        _tmp_1 = Converters.localTimeToLong(value.getStartTime());
        if (_tmp_1 == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindLong(2, _tmp_1);
        }
        final Integer _tmp_2;
        _tmp_2 = Converters.localDateToInt(value.getEndDate());
        if (_tmp_2 == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindLong(3, _tmp_2);
        }
        final Long _tmp_3;
        _tmp_3 = Converters.localTimeToLong(value.getEndTime());
        if (_tmp_3 == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindLong(4, _tmp_3);
        }
        if (value.getId() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindLong(5, value.getId());
        }
        final CalendarEvent.Details _tmpDetails = value.getDetails();
        if(_tmpDetails != null) {
          if (_tmpDetails.getLabel() == null) {
            stmt.bindNull(6);
          } else {
            stmt.bindString(6, _tmpDetails.getLabel());
          }
          if (_tmpDetails.getDescription() == null) {
            stmt.bindNull(7);
          } else {
            stmt.bindString(7, _tmpDetails.getDescription());
          }
        } else {
          stmt.bindNull(6);
          stmt.bindNull(7);
        }
      }
    };
    this.__insertionAdapterOfRecurringCalendarEvent = new EntityInsertionAdapter<RecurringCalendarEvent>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `recurring_events` (`activeWindow`,`datePattern`,`rec_id`,`startDate`,`startTime`,`endDate`,`endTime`,`id`,`label`,`description`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, RecurringCalendarEvent value) {
        final Long _tmp;
        _tmp = Converters.localDateRangeToLong(value.getActiveWindow());
        if (_tmp == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindLong(1, _tmp);
        }
        final long _tmp_1;
        _tmp_1 = DatePattern.datePatternToInt(value.getDatePattern());
        stmt.bindLong(2, _tmp_1);
        if (value.getRec_id() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindLong(3, value.getRec_id());
        }
        final CalendarEvent _tmpEventBase = value.getEventBase();
        if(_tmpEventBase != null) {
          final Integer _tmp_2;
          _tmp_2 = Converters.localDateToInt(_tmpEventBase.getStartDate());
          if (_tmp_2 == null) {
            stmt.bindNull(4);
          } else {
            stmt.bindLong(4, _tmp_2);
          }
          final Long _tmp_3;
          _tmp_3 = Converters.localTimeToLong(_tmpEventBase.getStartTime());
          if (_tmp_3 == null) {
            stmt.bindNull(5);
          } else {
            stmt.bindLong(5, _tmp_3);
          }
          final Integer _tmp_4;
          _tmp_4 = Converters.localDateToInt(_tmpEventBase.getEndDate());
          if (_tmp_4 == null) {
            stmt.bindNull(6);
          } else {
            stmt.bindLong(6, _tmp_4);
          }
          final Long _tmp_5;
          _tmp_5 = Converters.localTimeToLong(_tmpEventBase.getEndTime());
          if (_tmp_5 == null) {
            stmt.bindNull(7);
          } else {
            stmt.bindLong(7, _tmp_5);
          }
          if (_tmpEventBase.getId() == null) {
            stmt.bindNull(8);
          } else {
            stmt.bindLong(8, _tmpEventBase.getId());
          }
          final CalendarEvent.Details _tmpDetails = _tmpEventBase.getDetails();
          if(_tmpDetails != null) {
            if (_tmpDetails.getLabel() == null) {
              stmt.bindNull(9);
            } else {
              stmt.bindString(9, _tmpDetails.getLabel());
            }
            if (_tmpDetails.getDescription() == null) {
              stmt.bindNull(10);
            } else {
              stmt.bindString(10, _tmpDetails.getDescription());
            }
          } else {
            stmt.bindNull(9);
            stmt.bindNull(10);
          }
        } else {
          stmt.bindNull(4);
          stmt.bindNull(5);
          stmt.bindNull(6);
          stmt.bindNull(7);
          stmt.bindNull(8);
          stmt.bindNull(9);
          stmt.bindNull(10);
        }
      }
    };
    this.__insertionAdapterOfException = new EntityInsertionAdapter<RecurringCalendarEvent.Exception>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `recurrence_exceptions` (`date`,`event_id`) VALUES (?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, RecurringCalendarEvent.Exception value) {
        final Integer _tmp;
        _tmp = Converters.localDateToInt(value.getDate());
        if (_tmp == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindLong(1, _tmp);
        }
        stmt.bindLong(2, value.getEvent_id());
      }
    };
    this.__deletionAdapterOfCalendarEvent = new EntityDeletionOrUpdateAdapter<CalendarEvent>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `normal_events` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CalendarEvent value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindLong(1, value.getId());
        }
      }
    };
    this.__deletionAdapterOfRecurringCalendarEvent = new EntityDeletionOrUpdateAdapter<RecurringCalendarEvent>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `recurring_events` WHERE `rec_id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, RecurringCalendarEvent value) {
        if (value.getRec_id() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindLong(1, value.getRec_id());
        }
      }
    };
    this.__updateAdapterOfCalendarEvent = new EntityDeletionOrUpdateAdapter<CalendarEvent>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `normal_events` SET `startDate` = ?,`startTime` = ?,`endDate` = ?,`endTime` = ?,`id` = ?,`label` = ?,`description` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CalendarEvent value) {
        final Integer _tmp;
        _tmp = Converters.localDateToInt(value.getStartDate());
        if (_tmp == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindLong(1, _tmp);
        }
        final Long _tmp_1;
        _tmp_1 = Converters.localTimeToLong(value.getStartTime());
        if (_tmp_1 == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindLong(2, _tmp_1);
        }
        final Integer _tmp_2;
        _tmp_2 = Converters.localDateToInt(value.getEndDate());
        if (_tmp_2 == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindLong(3, _tmp_2);
        }
        final Long _tmp_3;
        _tmp_3 = Converters.localTimeToLong(value.getEndTime());
        if (_tmp_3 == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindLong(4, _tmp_3);
        }
        if (value.getId() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindLong(5, value.getId());
        }
        final CalendarEvent.Details _tmpDetails = value.getDetails();
        if(_tmpDetails != null) {
          if (_tmpDetails.getLabel() == null) {
            stmt.bindNull(6);
          } else {
            stmt.bindString(6, _tmpDetails.getLabel());
          }
          if (_tmpDetails.getDescription() == null) {
            stmt.bindNull(7);
          } else {
            stmt.bindString(7, _tmpDetails.getDescription());
          }
        } else {
          stmt.bindNull(6);
          stmt.bindNull(7);
        }
        if (value.getId() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindLong(8, value.getId());
        }
      }
    };
    this.__updateAdapterOfRecurringCalendarEvent = new EntityDeletionOrUpdateAdapter<RecurringCalendarEvent>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `recurring_events` SET `activeWindow` = ?,`datePattern` = ?,`rec_id` = ?,`startDate` = ?,`startTime` = ?,`endDate` = ?,`endTime` = ?,`id` = ?,`label` = ?,`description` = ? WHERE `rec_id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, RecurringCalendarEvent value) {
        final Long _tmp;
        _tmp = Converters.localDateRangeToLong(value.getActiveWindow());
        if (_tmp == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindLong(1, _tmp);
        }
        final long _tmp_1;
        _tmp_1 = DatePattern.datePatternToInt(value.getDatePattern());
        stmt.bindLong(2, _tmp_1);
        if (value.getRec_id() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindLong(3, value.getRec_id());
        }
        final CalendarEvent _tmpEventBase = value.getEventBase();
        if(_tmpEventBase != null) {
          final Integer _tmp_2;
          _tmp_2 = Converters.localDateToInt(_tmpEventBase.getStartDate());
          if (_tmp_2 == null) {
            stmt.bindNull(4);
          } else {
            stmt.bindLong(4, _tmp_2);
          }
          final Long _tmp_3;
          _tmp_3 = Converters.localTimeToLong(_tmpEventBase.getStartTime());
          if (_tmp_3 == null) {
            stmt.bindNull(5);
          } else {
            stmt.bindLong(5, _tmp_3);
          }
          final Integer _tmp_4;
          _tmp_4 = Converters.localDateToInt(_tmpEventBase.getEndDate());
          if (_tmp_4 == null) {
            stmt.bindNull(6);
          } else {
            stmt.bindLong(6, _tmp_4);
          }
          final Long _tmp_5;
          _tmp_5 = Converters.localTimeToLong(_tmpEventBase.getEndTime());
          if (_tmp_5 == null) {
            stmt.bindNull(7);
          } else {
            stmt.bindLong(7, _tmp_5);
          }
          if (_tmpEventBase.getId() == null) {
            stmt.bindNull(8);
          } else {
            stmt.bindLong(8, _tmpEventBase.getId());
          }
          final CalendarEvent.Details _tmpDetails = _tmpEventBase.getDetails();
          if(_tmpDetails != null) {
            if (_tmpDetails.getLabel() == null) {
              stmt.bindNull(9);
            } else {
              stmt.bindString(9, _tmpDetails.getLabel());
            }
            if (_tmpDetails.getDescription() == null) {
              stmt.bindNull(10);
            } else {
              stmt.bindString(10, _tmpDetails.getDescription());
            }
          } else {
            stmt.bindNull(9);
            stmt.bindNull(10);
          }
        } else {
          stmt.bindNull(4);
          stmt.bindNull(5);
          stmt.bindNull(6);
          stmt.bindNull(7);
          stmt.bindNull(8);
          stmt.bindNull(9);
          stmt.bindNull(10);
        }
        if (value.getRec_id() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindLong(11, value.getRec_id());
        }
      }
    };
    this.__preparedStmtOfDeleteAllExceptionsForRecurringEvent = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM recurrence_exceptions WHERE event_id == ?";
        return _query;
      }
    };
    this.__preparedStmtOf_clearAllNormalEvents = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM normal_events";
        return _query;
      }
    };
    this.__preparedStmtOf_clearAllRecurringEvents = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM recurring_events";
        return _query;
      }
    };
    this.__preparedStmtOf_clearAllRecurrenceExceptions = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM recurrence_exceptions";
        return _query;
      }
    };
  }

  @Override
  public void insertEvent(final CalendarEvent event) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfCalendarEvent.insert(event);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertEvent(final RecurringCalendarEvent event) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfRecurringCalendarEvent.insert(event);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void _addExceptionForEvent(final RecurringCalendarEvent.Exception exception) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfException.insert(exception);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void _deleteEvent(final CalendarEvent event) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfCalendarEvent.handle(event);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void _deleteEvent(final RecurringCalendarEvent event) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfRecurringCalendarEvent.handle(event);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateEvent(final CalendarEvent event) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfCalendarEvent.handle(event);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateEvent(final RecurringCalendarEvent event) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfRecurringCalendarEvent.handle(event);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public Map<RecurringCalendarEvent, List<LocalDate>> getRecurringEventsWithExceptions() {
    __db.beginTransaction();
    try {
      Map<RecurringCalendarEvent, List<LocalDate>> _result = CalendarDao.DefaultImpls.getRecurringEventsWithExceptions(CalendarDao_Impl.this);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteEvent(final CalendarEvent event) {
    __db.beginTransaction();
    try {
      CalendarDao.DefaultImpls.deleteEvent(CalendarDao_Impl.this, event);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteEvent(final RecurringCalendarEvent event) {
    __db.beginTransaction();
    try {
      CalendarDao.DefaultImpls.deleteEvent(CalendarDao_Impl.this, event);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void clearAllEvents() {
    __db.beginTransaction();
    try {
      CalendarDao.DefaultImpls.clearAllEvents(CalendarDao_Impl.this);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAllExceptionsForRecurringEvent(final int recurringCalendarEventID) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllExceptionsForRecurringEvent.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, recurringCalendarEventID);
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAllExceptionsForRecurringEvent.release(_stmt);
    }
  }

  @Override
  public void _clearAllNormalEvents() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOf_clearAllNormalEvents.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOf_clearAllNormalEvents.release(_stmt);
    }
  }

  @Override
  public void _clearAllRecurringEvents() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOf_clearAllRecurringEvents.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOf_clearAllRecurringEvents.release(_stmt);
    }
  }

  @Override
  public void _clearAllRecurrenceExceptions() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOf_clearAllRecurrenceExceptions.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOf_clearAllRecurrenceExceptions.release(_stmt);
    }
  }

  @Override
  public List<CalendarEvent> getAllWithLabel(final String label, final int limit) {
    final String _sql = "SELECT * FROM normal_events WHERE label = ? LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (label == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, label);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
      final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
      final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
      final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final List<CalendarEvent> _result = new ArrayList<CalendarEvent>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final CalendarEvent _item;
        final LocalDate _tmpStartDate;
        final Integer _tmp;
        if (_cursor.isNull(_cursorIndexOfStartDate)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getInt(_cursorIndexOfStartDate);
        }
        _tmpStartDate = Converters.intToLocalDate(_tmp);
        final LocalTime _tmpStartTime;
        final Long _tmp_1;
        if (_cursor.isNull(_cursorIndexOfStartTime)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getLong(_cursorIndexOfStartTime);
        }
        _tmpStartTime = Converters.longToLocalTime(_tmp_1);
        final LocalDate _tmpEndDate;
        final Integer _tmp_2;
        if (_cursor.isNull(_cursorIndexOfEndDate)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getInt(_cursorIndexOfEndDate);
        }
        _tmpEndDate = Converters.intToLocalDate(_tmp_2);
        final LocalTime _tmpEndTime;
        final Long _tmp_3;
        if (_cursor.isNull(_cursorIndexOfEndTime)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getLong(_cursorIndexOfEndTime);
        }
        _tmpEndTime = Converters.longToLocalTime(_tmp_3);
        final Integer _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getInt(_cursorIndexOfId);
        }
        final CalendarEvent.Details _tmpDetails;
        if (! (_cursor.isNull(_cursorIndexOfLabel) && _cursor.isNull(_cursorIndexOfDescription))) {
          final String _tmpLabel;
          if (_cursor.isNull(_cursorIndexOfLabel)) {
            _tmpLabel = null;
          } else {
            _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
          }
          final String _tmpDescription;
          if (_cursor.isNull(_cursorIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
          }
          _tmpDetails = new CalendarEvent.Details(_tmpLabel,_tmpDescription);
        }  else  {
          _tmpDetails = null;
        }
        _item = new CalendarEvent(_tmpStartDate,_tmpStartTime,_tmpEndDate,_tmpEndTime,_tmpDetails,_tmpId);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<String> getLabelsInDateRange(final LocalDate earliest, final LocalDate latest) {
    final String _sql = "SELECT label FROM normal_events WHERE ? <= startDate <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final Integer _tmp;
    _tmp = Converters.localDateToInt(earliest);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, _tmp);
    }
    _argIndex = 2;
    final Integer _tmp_1;
    _tmp_1 = Converters.localDateToInt(latest);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, _tmp_1);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final List<String> _result = new ArrayList<String>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final String _item;
        if (_cursor.isNull(0)) {
          _item = null;
        } else {
          _item = _cursor.getString(0);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<RecurringCalendarEvent> _getRecurringEvents() {
    final String _sql = "SELECT * FROM recurring_events";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfActiveWindow = CursorUtil.getColumnIndexOrThrow(_cursor, "activeWindow");
      final int _cursorIndexOfDatePattern = CursorUtil.getColumnIndexOrThrow(_cursor, "datePattern");
      final int _cursorIndexOfRecId = CursorUtil.getColumnIndexOrThrow(_cursor, "rec_id");
      final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
      final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
      final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
      final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final List<RecurringCalendarEvent> _result = new ArrayList<RecurringCalendarEvent>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final RecurringCalendarEvent _item;
        final ClosedRange<LocalDate> _tmpActiveWindow;
        final Long _tmp;
        if (_cursor.isNull(_cursorIndexOfActiveWindow)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getLong(_cursorIndexOfActiveWindow);
        }
        _tmpActiveWindow = Converters.longToLocalDateRange(_tmp);
        final DatePattern _tmpDatePattern;
        final long _tmp_1;
        _tmp_1 = _cursor.getLong(_cursorIndexOfDatePattern);
        _tmpDatePattern = DatePattern.intToDatePattern(_tmp_1);
        final Integer _tmpRec_id;
        if (_cursor.isNull(_cursorIndexOfRecId)) {
          _tmpRec_id = null;
        } else {
          _tmpRec_id = _cursor.getInt(_cursorIndexOfRecId);
        }
        final CalendarEvent _tmpEventBase;
        if (! (_cursor.isNull(_cursorIndexOfStartDate) && _cursor.isNull(_cursorIndexOfStartTime) && _cursor.isNull(_cursorIndexOfEndDate) && _cursor.isNull(_cursorIndexOfEndTime) && _cursor.isNull(_cursorIndexOfId) && _cursor.isNull(_cursorIndexOfLabel) && _cursor.isNull(_cursorIndexOfDescription))) {
          final LocalDate _tmpStartDate;
          final Integer _tmp_2;
          if (_cursor.isNull(_cursorIndexOfStartDate)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _cursor.getInt(_cursorIndexOfStartDate);
          }
          _tmpStartDate = Converters.intToLocalDate(_tmp_2);
          final LocalTime _tmpStartTime;
          final Long _tmp_3;
          if (_cursor.isNull(_cursorIndexOfStartTime)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _cursor.getLong(_cursorIndexOfStartTime);
          }
          _tmpStartTime = Converters.longToLocalTime(_tmp_3);
          final LocalDate _tmpEndDate;
          final Integer _tmp_4;
          if (_cursor.isNull(_cursorIndexOfEndDate)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _cursor.getInt(_cursorIndexOfEndDate);
          }
          _tmpEndDate = Converters.intToLocalDate(_tmp_4);
          final LocalTime _tmpEndTime;
          final Long _tmp_5;
          if (_cursor.isNull(_cursorIndexOfEndTime)) {
            _tmp_5 = null;
          } else {
            _tmp_5 = _cursor.getLong(_cursorIndexOfEndTime);
          }
          _tmpEndTime = Converters.longToLocalTime(_tmp_5);
          final Integer _tmpId;
          if (_cursor.isNull(_cursorIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _cursor.getInt(_cursorIndexOfId);
          }
          final CalendarEvent.Details _tmpDetails;
          if (! (_cursor.isNull(_cursorIndexOfLabel) && _cursor.isNull(_cursorIndexOfDescription))) {
            final String _tmpLabel;
            if (_cursor.isNull(_cursorIndexOfLabel)) {
              _tmpLabel = null;
            } else {
              _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            _tmpDetails = new CalendarEvent.Details(_tmpLabel,_tmpDescription);
          }  else  {
            _tmpDetails = null;
          }
          _tmpEventBase = new CalendarEvent(_tmpStartDate,_tmpStartTime,_tmpEndDate,_tmpEndTime,_tmpDetails,_tmpId);
        }  else  {
          _tmpEventBase = null;
        }
        _item = new RecurringCalendarEvent(_tmpEventBase,_tmpActiveWindow,_tmpDatePattern,_tmpRec_id);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<LocalDate> _getExceptionsForEvent(final int id) {
    final String _sql = "SELECT date FROM recurrence_exceptions WHERE event_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final List<LocalDate> _result = new ArrayList<LocalDate>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final LocalDate _item;
        final Integer _tmp;
        if (_cursor.isNull(0)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getInt(0);
        }
        _item = Converters.intToLocalDate(_tmp);
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
}
