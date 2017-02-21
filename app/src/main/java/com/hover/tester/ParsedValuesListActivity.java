package com.hover.tester;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ParsedValuesListActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parsed_values_list);

        String operatorName = Utils.getOperator(getApplicationContext());
        String actionName = getIntent().getStringExtra(Utils.ACTION);
        String prefix = operatorName + "_" + actionName + "_";

        ArrayList<ParsedValue> parsedValues = new ArrayList<ParsedValue>();
        ParsedValuesAdapter adapter = new ParsedValuesAdapter(this, parsedValues);
        setListAdapter(adapter);

        for (int i = 0; i < Utils.parsableValues.length; i++) {
            SharedPreferences prefs = Utils.getSharedPrefs(getApplicationContext());
            String key = prefix + Utils.parsableValues[i];
            if (prefs.contains(key)) {
                String val = prefs.getString(key, "");
                ParsedValue newParsedValue = new ParsedValue(key, val);
                adapter.add(newParsedValue);
            }
        }
    }

    public class ParsedValue {
        public String label;
        public String content;

        public ParsedValue(String label, String content) {
            this.label = label;
            this.content = content;
        }
    }

    public class ParsedValuesAdapter extends ArrayAdapter<ParsedValue> {
        public ParsedValuesAdapter(Context context, ArrayList<ParsedValue> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ParsedValue parsedValue = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_parsed_value, parent, false);
            }

            TextView parsedValueLabel = (TextView) convertView.findViewById(R.id.parsedValueLabel);
            TextView parsedValueContent = (TextView) convertView.findViewById(R.id.parsedValueContent);

            parsedValueLabel.setText(parsedValue.label);
            parsedValueContent.setText(parsedValue.content);
            return convertView;
        }
    }
}
