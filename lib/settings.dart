import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class SettingWidget extends StatefulWidget {
  const SettingWidget({Key? key}) : super(key: key);

  @override
  Settings createState() => Settings();
}

class Settings extends State<SettingWidget> {

  SavedSettings settings = new SavedSettings(true, true);

  @override
  Widget build(BuildContext context) =>
      ListView(
        children: <Widget>[
          Card(
              child: SwitchListTile(
                title: const Text('Lights'),
                value: settings._lights,
                onChanged: (bool value) {
                  setState(() {
                    settings._lights = value;
                  });
                },
                secondary: const Icon(Icons.lightbulb_outline),
              )
          ),
          Card(
              child: SwitchListTile(
                title: const Text('Theme'),
                value: settings._theme,
                onChanged: (bool value) {
                  setState(() {
                    settings._theme = value;
                  });
                },
                secondary: const Icon(Icons.color_lens),
              )
          ),
        ],
      );
}

class SavedSettings {
  SavedSettings(this._lights, this._theme) ;

  bool _lights;
  bool _theme;

}