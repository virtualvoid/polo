package sk.virtualvoid.ingress.polo;

import android.content.Context;
import android.os.Bundle;
import android.renderscript.Double2;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.text.NumberFormat;
import java.util.Locale;

import sk.virtualvoid.ingress.polo.data.PortalLocation;
import sk.virtualvoid.ingress.polo.utils.OsHax;
import sk.virtualvoid.ingress.polo.utils.WellKnownFragment;

/**
 * Created by Juraj on 4/21/2016.
 */
public class PortalDetailFragment extends Fragment implements WellKnownFragment {
    public static final String NAME = "__polo_PortalDetail";
    public static final String KEY_ISNEW = "isNew";
    public static final String KEY_GOTGEO = "gotGeoLocation";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_PARCELABLE = "portalLocationParcelable";

    private PortalDetailFragmentHandler handler;

    private Long currentId = null;
    private AppCompatEditText editTextName;
    private AppCompatEditText editTextLatitude;
    private AppCompatEditText editTextLongitude;

    public PortalDetailFragment() {

    }

    public static PortalDetailFragment newInstance(Bundle args) {
        PortalDetailFragment fragment = new PortalDetailFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        handler = (PortalDetailFragmentHandler) getActivity();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.done: {
                PortalLocation newPortalLocation = new PortalLocation();
                newPortalLocation.setId(currentId);

                String name = editTextName.getText().toString();
                if (name.length() == 0) {
                    editTextName.setError(getContext().getString(R.string.please_enter_portal_name));
                    return false;
                } else {
                    editTextName.setError(null);
                    newPortalLocation.setName(editTextName.getText().toString());
                }

                String latitudeStr = editTextLatitude.getText().toString();
                if (latitudeStr.length() == 0) {
                    editTextLatitude.setError(getContext().getString(R.string.please_enter_correct_latitude));
                    return false;
                } else {
                    try {
                        double latitude = Double.parseDouble(latitudeStr);
                        newPortalLocation.setLatitude(latitude);
                        editTextLatitude.setError(null);
                    } catch (NumberFormatException e) {
                        editTextLatitude.setError(getContext().getString(R.string.pos_invalid_format));
                        return false;
                    }
                }

                String longitudeStr = editTextLongitude.getText().toString();
                if (longitudeStr.length() == 0) {
                    editTextLongitude.setError(getContext().getString(R.string.please_enter_correct_longitude));
                    return false;
                } else {
                    try {
                        double longitude = Double.parseDouble(longitudeStr);
                        newPortalLocation.setLongitude(longitude);
                        editTextLongitude.setError(null);
                    } catch (NumberFormatException e) {
                        editTextLongitude.setError(getContext().getString(R.string.pos_invalid_format));
                        return false;
                    }
                }

                OsHax.hideKeyboard(getActivity());

                handler.onPortalDetail(newPortalLocation);
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        editTextName = (AppCompatEditText)view.findViewById(R.id.portal_detail_name);
        editTextLatitude = (AppCompatEditText)view.findViewById(R.id.portal_detail_latitude);
        editTextLongitude = (AppCompatEditText)view.findViewById(R.id.portal_detail_longitude);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        boolean isNew = args.getBoolean(KEY_ISNEW);
        boolean gotGeo = args.getBoolean(KEY_GOTGEO);

        if (isNew && gotGeo) {
            double latitude = args.getDouble(KEY_LATITUDE);
            double longitude = args.getDouble(KEY_LONGITUDE);
            setPortalLocation(latitude, longitude);
        }

        if (!isNew && args.containsKey(KEY_PARCELABLE)) {
            PortalLocation portalLocation = args.getParcelable(KEY_PARCELABLE);
            if (portalLocation == null) {
                throw new RuntimeException("Got KEY_PARCELABLE, but nothing was in it.");
            }

            setPortalLocation(portalLocation.getLatitude(), portalLocation.getLongitude());

            editTextName.setText(portalLocation.getName());
            currentId = portalLocation.getId();
        }

    }

    private void setPortalLocation(double latitude, double longitude) {
        editTextLatitude.setText(String.format(Locale.getDefault(), "%f", latitude));
        editTextLongitude.setText(String.format(Locale.getDefault(), "%f", longitude));
    }
}
