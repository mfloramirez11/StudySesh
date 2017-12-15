package team8.studysesh;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ListGroups extends ListActivity {

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    public static final ArrayList<StudyGroupModel> listItems=new ArrayList<StudyGroupModel>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    public static ArrayAdapter<StudyGroupModel> adapter;

    private String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_groups);

        adapter=new ArrayAdapter<StudyGroupModel>(this,
                android.R.layout.simple_list_item_1,
                listItems) {
        @Override
            public void notifyDataSetChanged() {
                updateList();
                super.notifyDataSetChanged();
            }
        @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                String[] pieces = listItems.get(position).toString().split("<br>", 2);
                text.setText(Html.fromHtml("<b>" + pieces[0] + "<br>" + "</b>" + "<small>" + pieces[1] + "</small>"));
                if (position%2 == 0)
                    view.setBackgroundColor(Color.LTGRAY);
                else
                    view.setBackgroundColor(Color.TRANSPARENT);

            return view;
            }
        };
        setListAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEnterGroupInfo(); // using new intent
                //addNewStudyGroup(view); // using dialog box
            }
        });

        adapter.notifyDataSetChanged();

    }






    @Override
    protected void onListItemClick (ListView l, View v, final int position, long id) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Description:");
//        builder.setMessage(listItems.get(position).description + "\nOwner: " + listItems.get(position).owner);
//
//        // Set up the buttons
//        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                deleteGroup(position);
//                dialog.cancel();
//            }
//        });
//
//
//        builder.show();
        DisplayGroupInfo.selectedGroup = position;
        goToDisplayGroupInfo();
    }

// old impl using dialog box
//    private void addNewStudyGroup(View view) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Class Name");
//
//        // Set up the input
//        final EditText input = new EditText(this);
//        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        builder.setView(input);
//
//        // Set up the buttons
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                goToEnterGroupInfo();
//                m_Text = input.getText().toString();
//                listItems.add(0, new StudyGroupModel(LoginActivity.DUMMY_CREDENTIALS.get(LoginActivity.userIndex)));
//                adapter.notifyDataSetChanged();
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();
//    }

    public void goToEnterGroupInfo() {
        Intent intent = new Intent(this, EnterGroupInfo.class);
        startActivity(intent);
    }

    public void goToDisplayGroupInfo() {
        Intent intent = new Intent(this, DisplayGroupInfo.class);
        startActivity(intent);
    }

    private void deleteGroup(int position) {
        if (listItems.get(position).owner.equals(LoginActivity.DUMMY_CREDENTIALS.get(LoginActivity.userIndex).split("@")[0])) {
            listItems.remove(position);
            ListGroups.adapter.notifyDataSetChanged();
        }
    }

    // MUST TAKE JSON ARRAY
    public List<SingleEventData> getDataForListView(JsonObject json_array){
        List<SingleEventData> list_events = new ArrayList<SingleEventData>();
        String json_str =  json_array.getAsJsonArray("events").toString();

        try {
            // Turning our JsonObject to a JSONArray
            JSONArray jsonArray = new JSONArray(json_str);

            // Parsing the JSONArray
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj1 = jsonArray.getJSONObject(i);

                //Instantiate event object
                SingleEventData new_event = new SingleEventData(obj1);

                list_events.add(new_event);
            }

        } catch (JSONException je) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            je.printStackTrace(printWriter);
            printWriter.flush();
            //debugMessage(writer.toString());
        }

        return list_events;
    }

    public class SingleEventData{
        public final static String TITLE = "title";
        public final static String START_TIME = "start_time";
        public final static String LOCATION = "location";
        public final static String CAPACITY = "capacity";
        public final static String DESCRIPTION = "description";
        public final static String ID = "id";
        public final static String OWNER = "owner";
        public final static String MEMBERS = "members";

        public SingleEventData(JSONObject json_event){
            try {
                course_title = json_event.get(TITLE).toString();
                start_time = json_event.get(START_TIME).toString();
                location = json_event.get(LOCATION).toString();
                capacity = json_event.get(CAPACITY).toString();
                description = json_event.get(DESCRIPTION).toString();
                id = json_event.get(ID).toString();
                owner = json_event.get(OWNER).toString();
                members = json_event.get(MEMBERS).toString();
            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        String course_title;
        String start_time;
        String location;
        String capacity;
        String description;
        String id;
        String owner;
        String members;
    }
    public void updateList() {
        Ion.with(this)
                .load("http://198.199.98.53/scripts/get_event_data.php")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    // PERFORM MAIN OPERATIONS HERE
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        //Create a List of events
                        List<SingleEventData> list_events = getDataForListView(result);
                        listItems.clear();

                        for (int i = list_events.size() - 1; i >= 0; --i) {
                            SingleEventData data = list_events.get(i);
                            StudyGroupModel element = new StudyGroupModel(data.owner,
                                    data.course_title, data.location,
                                    Integer.parseInt(data.capacity),
                                    data.description,
                                    data.start_time);
                            listItems.add(element);
                        }
                        adapter.notifyDataSetChanged();
                        /*
                        ArrayList<String> desc_list_events = new ArrayList<String>();

                        // Grabbing only descriptions
                        for (int i = list_events.size() - 1; i >= 0; i--) {
                            desc_list_events.add(list_events.get(i).description);
                        }

                        String[] str_desc_list_events = desc_list_events.toArray(new String[list_events.size()]);
                        //Build Adapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.event_item, str_desc_list_events);

                        //Configure List View
                        ListView list = (ListView) findViewById(R.id.list_events);
                        list.setAdapter(adapter);*/
                    }
                });
    }

}
