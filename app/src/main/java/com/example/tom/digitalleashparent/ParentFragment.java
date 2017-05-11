package com.example.tom.digitalleashparent;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class ParentFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private EditText userNameEditText;
    private EditText radiusEditText;

    private EditText latitudeEditText;
    private EditText longitudeEditText;

    private Button createButton;
    private Button updateButton;
    private Button statusButton;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private final int LOCATION_INTERVAL = 1000;
    private Location currentLocation;

    private boolean childInBounds;
    protected String url = "https://turntotech.firebaseio.com/digitalleash/";
    private JSONObject userData;

    private String requestType;

    public ParentFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent, container, false);

        createButton = (Button) view.findViewById(R.id.createButton);
        updateButton = (Button) view.findViewById(R.id.updateButton);
        statusButton = (Button) view.findViewById(R.id.statusButton);

        userNameEditText = (EditText) view.findViewById(R.id.userNameEditText);
        radiusEditText = (EditText) view.findViewById(R.id.radiusEditText);

        latitudeEditText = (EditText) view.findViewById(R.id.latitudeEditText);
        longitudeEditText = (EditText) view.findViewById(R.id.longitudeEditText);

        view.setBackgroundColor(Color.parseColor("#0099C9"));
        setOnClicks();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(LOCATION_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_INTERVAL);

        googleApiClient = new GoogleApiClient.Builder(this.getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();

        double radius = 1000;
        radiusEditText.setText(String.valueOf(radius));

        return view;
    }


    private class CreateUserData extends AsyncTask<JSONObject, Void, String> {
        @Override
        protected String doInBackground(JSONObject... params) {
            URL urlObj;
            try {
                String urlString = url + params[0].get("user_name") + ".json";
                urlObj = new URL(urlString);
                HttpURLConnection httpCon = (HttpURLConnection)urlObj.openConnection();
                httpCon.setDoOutput(true);
                httpCon.setInstanceFollowRedirects(false);
                httpCon.setRequestMethod("PUT");
                httpCon.setRequestProperty("Accept", "application/json");
                httpCon.setRequestProperty("Content-Type", "application/json");
                OutputStreamWriter wr = new OutputStreamWriter(httpCon.getOutputStream());
                wr.write(params[0].toString());
                wr.flush();
                wr.close();
                httpCon.getInputStream();
                if(httpCon.getResponseCode() < 200 || httpCon.getResponseCode() > 299) {
                    int responseCode = httpCon.getResponseCode();
                    Log.v("responsecode", String.valueOf(responseCode));
                } else {
                    int responseCode = httpCon.getResponseCode();
                    Log.v("responsecode", String.valueOf(responseCode));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class UpdateUserData extends AsyncTask<JSONObject, Void, String> {
        @Override
        protected String doInBackground(JSONObject... params) {
            URL urlObj;
            try {
                String urlString = url + params[0].get("user_name") + ".json";
                urlObj = new URL(urlString);
                HttpURLConnection httpCon = (HttpURLConnection)urlObj.openConnection();
                httpCon.setDoOutput(true);
                httpCon.setInstanceFollowRedirects(false);
                httpCon.setRequestMethod("PATCH");
                httpCon.setRequestProperty("Accept", "application/json");
                httpCon.setRequestProperty("Content-Type", "application/json");
                OutputStreamWriter wr = new OutputStreamWriter(httpCon.getOutputStream());
                wr.write(params[0].toString());
                wr.flush();
                wr.close();
                httpCon.getInputStream();
                if(httpCon.getResponseCode() < 200 || httpCon.getResponseCode() > 299) {
                    int responseCode = httpCon.getResponseCode();
                    Log.v("responsecode", String.valueOf(responseCode));
                } else {
                    int responseCode = httpCon.getResponseCode();
                    Log.v("responsecode", String.valueOf(responseCode));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class GetUserData extends AsyncTask<String, Void, JSONObject> {
        private boolean isChildInBounds(JSONObject jsonObject) {
            Location loc1 = null;
            Location loc2 = null;
            try {
                if(jsonObject.get("child_latitude") == null || jsonObject.get("child_longitude") == null) {
                    return false;
                } else {
                    loc1 = new Location("Parent Location");
                    loc1.setLatitude(Float.valueOf((String)jsonObject.get("latitude")));
                    loc1.setLongitude(Float.valueOf((String)jsonObject.get("longitude")));
                    loc2 = new Location("Child Location");
                    loc2.setLatitude((Double) jsonObject.get("child_latitude"));
                    loc2.setLongitude((Double) jsonObject.get("child_longitude"));
                    double distance = loc1.distanceTo(loc2);
                    double radius = Float.valueOf((String)jsonObject.get("radius"));
                    Log.v("distance: " , String.valueOf(distance));
                    Log.v("radius: " , String.valueOf(radius));
                    if(Float.valueOf(loc1.distanceTo(loc2)) < Float.valueOf((String)jsonObject.get("radius"))) {
                        return true;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            userData = jsonObject;
            if(userData != null) {
                if(requestType == "UPDATE") {
                    updateUser();
                }
                else if(requestType == "CREATE") {
                    Toast.makeText(getContext(), "User Already Exists", Toast.LENGTH_SHORT).show();
                }
                else if(requestType == "STATUS") {
                   try {
                       if (jsonObject.get("child_latitude") != null) {
                           if (isChildInBounds(userData)) {
                               childInBounds = true;
                           } else {
                               childInBounds = false;
                           }
                           Bundle args = new Bundle();
                           args.putBoolean("childInBounds", childInBounds);
                           StatusFragment newStatus = new StatusFragment();
                           newStatus.setArguments(args);
                           getFragmentManager().beginTransaction().replace(R.id.mainContainer, newStatus).commit();
                       } else {
                           Toast.makeText(getContext().getApplicationContext(), "Child Has Not Checked In", Toast.LENGTH_SHORT).show();
                       }
                   } catch (JSONException e) {
                       Toast.makeText(getContext().getApplicationContext(), "No Child Data Found", Toast.LENGTH_SHORT).show();
                       e.printStackTrace();
                   }
                }
                else {
                    Log.e("GetUserData", "Invalid Request Type");
                }

            }
            else {
                Toast.makeText(getContext().getApplicationContext(), "User Data Not Found", Toast.LENGTH_SHORT).show();
                childInBounds = false;
                if (requestType == "CREATE") {
                    createUser();
                    Toast.makeText(getContext().getApplicationContext(), "User Created", Toast.LENGTH_SHORT).show();
                }
                if (requestType == "UPDATE") {
                    Toast.makeText(getContext().getApplicationContext(), "User Data Not Found", Toast.LENGTH_SHORT).show();
                }
                if(requestType  == "STATUS") {
                    Toast.makeText(getContext().getApplicationContext(), "User Data Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            String urlString = url + params[0] + ".json";

            URL url = null;
            try {
                url = new URL(urlString);
                HttpURLConnection httpCon = (HttpURLConnection)url.openConnection();
                InputStream is = httpCon.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder result = new StringBuilder();
                while((line = reader.readLine()) != null) {
                    result.append(line);
                }

                if (result.equals("null")){
                    Toast.makeText(getContext(), "User Not Found", Toast.LENGTH_LONG).show();
                    return null;
                }

                JSONObject responseFromJson = new JSONObject(result.toString());

                return responseFromJson;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public boolean checkConnection() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

    protected void createUser() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_name", userNameEditText.getText().toString());
            jsonObject.put("latitude", latitudeEditText.getText().toString());
            jsonObject.put("longitude", longitudeEditText.getText().toString());
            jsonObject.put("radius", radiusEditText.getText().toString());
            new CreateUserData().execute(jsonObject);
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Failed to Created User", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    protected void updateUser() {
        if(userData != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_name", userNameEditText.getText().toString());
                jsonObject.put("latitude", latitudeEditText.getText().toString());
                jsonObject.put("longitude", longitudeEditText.getText().toString());
                jsonObject.put("radius", radiusEditText.getText().toString());
                new UpdateUserData().execute(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(getContext(), "Updated User", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getContext().getApplicationContext(), "Can't Update NonExistent User", Toast.LENGTH_SHORT);
        }
    }

    public void makeFirebaseRESTRequest(String requestOperation) {
        requestType = requestOperation;
        //do stuff for get user data
        String[] params = new String[1];
        params[0] = userNameEditText.getText().toString();

        //execute asynctask
        new GetUserData().execute(params);
    }

    public void setOnClicks() {
        //CREATE
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnection()) {
                    makeFirebaseRESTRequest("CREATE");
                } else {
                    Toast.makeText(getContext(), "No Network Found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //UPDATE
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnection()) {
                    makeFirebaseRESTRequest("UPDATE");
                } else {
                    Toast.makeText(getContext(), "No Network Found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //STATUS
        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnection()) {
                    makeFirebaseRESTRequest("STATUS");
                } else {
                    Toast.makeText(getContext(), "No Network Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        latitudeEditText.setText(String.valueOf(currentLocation.getLatitude()));
        longitudeEditText.setText(String.valueOf(currentLocation.getLongitude()));
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
