import '../calendar.dart';

class EventRetriever {
  static final List<EventItem> _events = List.empty(growable: true);

  static DateTime today() => new DateTime(
      DateTime.now().year, DateTime.now().month, DateTime.now().day, 0, 0, 0);

  static void init() {}

  static List<EventItem> retrieveEvents(bool test(EventItem e)) {
    return List.from(_events.where(test));
  }

  static void addEvent(EventItem event) {
    _events.add(event);
  }

  static void removeEvent(EventItem event) {
    EventItem reEvent = _events.firstWhere((EventItem e) => (event.id == e.id));
    _events.remove(reEvent);
  }

  static List<EventItem> retrieveEventFromStartEnd(
      DateTime start, DateTime end) {
    return retrieveEvents(
        (e) => start.isBefore(e.startTime) && end.isAfter(e.startTime));
  }
}
