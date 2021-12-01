// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility that Flutter provides. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'package:flutter_test/flutter_test.dart';
import 'package:intl/intl.dart';
import 'package:yacht_demo/day-display.dart';

import 'package:yacht_demo/main.dart';
import 'package:yacht_demo/services/event-retriever.dart';

void main() {
  testWidgets('Find today on the list', (WidgetTester tester) async {
    // Build our app and trigger a frame.
    await tester.pumpWidget(MyApp());
    final cardFinder =
        find.text(DateFormat('M-d').format(EventRetriever.today()));
    expect(cardFinder, findsWidgets);
    final item = find.text("testEvent");

    expect(item, findsOneWidget);
  });
}
