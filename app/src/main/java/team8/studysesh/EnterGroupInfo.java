package team8.studysesh;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.koushikdutta.ion.Ion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EnterGroupInfo extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    // Defining key values for data
    public final static String TITLE = "title";
    public final static String START_TIME = "start_time";
    public final static String LOCATION = "location";
    public final static String CAPACITY = "capacity";
    public final static String DESCRIPTION = "description";
    public final static String ID = "id";
    public final static String OWNER = "owner";
    public final static String MEMBERS = "members";

    //googlemaps api
    protected GoogleApiClient mGoogleApiClient;

    private GooglePlacesAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));


    // ASyncTask TAG log
    private static final String TAG = "post_event";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        setContentView(R.layout.activity_enter_group_info);

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);


        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new GooglePlacesAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);
        mAutocompleteView.setAdapter(mAdapter);

        // Set up the 'clear text' button that clears the text in the autocomplete view
        Button clearButton = (Button) findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteView.setText("");
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postEventData(view);
            }
        });


    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    public void postEventData(View view) {
        EditText theClass = (EditText) findViewById(R.id.theClass);
        if (theClass.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), R.string.retry_enter_group_info, Toast.LENGTH_SHORT).show();
            return;
        }
        EditText location = (EditText) findViewById(R.id.autocomplete_places);
        EditText capacity = (EditText) findViewById(R.id.capacity);
        EditText description = (EditText) findViewById(R.id.description);

        String str_owner = LoginActivity.DUMMY_CREDENTIALS.get(LoginActivity.userIndex).split("@")[0];
        String str_courseTitle = theClass.getText().toString();
        String str_location = mAutocompleteView.getText().toString();
        int inputCapacity;
        if (capacity.getText().toString().length() > 0)
            inputCapacity = Integer.parseInt(capacity.getText().toString());
        else
            inputCapacity = 5;
        String str_capacity = Integer.toString(inputCapacity);
        String str_description;
        if (description.getText().toString().length() > 0)
            str_description = description.getText().toString();
        else
            str_description = "Lets study ";// + str_courseTitle;

        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        Date theDate = new Date();
        String str_startTime = dateFormat.format(theDate).toString();

        System.out.println( str_owner );

        Ion.with(this)
                .load("http://198.199.98.53/scripts/post_event_data.php")
                .setBodyParameter(TITLE, str_courseTitle)
                .setBodyParameter(START_TIME, str_startTime)
                .setBodyParameter(LOCATION, str_location)
                .setBodyParameter(CAPACITY, str_capacity)
                .setBodyParameter(DESCRIPTION, str_description)
                .setBodyParameter(OWNER, str_owner)
                .asString();

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Success.");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();

        ListGroups.adapter.notifyDataSetChanged();
        //finish();
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

}

