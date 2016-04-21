package sk.virtualvoid.ingress.polo;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import sk.virtualvoid.ingress.polo.components.PortalLocationAdapterHandler;
import sk.virtualvoid.ingress.polo.data.PortalDatabase;
import sk.virtualvoid.ingress.polo.data.PortalLocation;
import sk.virtualvoid.ingress.polo.utils.PortalListFragmentHandler;
import sk.virtualvoid.ingress.polo.utils.WellKnownFragment;

/**
 * Created by Juraj on 4/21/2016.
 */
public class PortalLocationsActivity extends AppCompatActivity implements PortalListFragmentHandler, PortalLocationAdapterHandler, PortalDetailFragmentHandler {
    private Toolbar toolbar;
    private FloatingActionButton actionButton;

    private Fragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        // ui
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionButton = (FloatingActionButton) findViewById(R.id.fab);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putBoolean(PortalDetailFragment.KEY_ISNEW, true);
                args.putBoolean(PortalDetailFragment.KEY_GOTGEO, false);
                replaceFragment(PortalDetailFragment.newInstance(args), true);
            }
        });

        // logic
        Intent intent = getIntent();
        String action = intent.getAction();

        double latitude = 0.0;
        double longitude = 0.0;
        boolean displayPortalDetail = false;

        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uriData = intent.getData();
            String uriStr = uriData.toString();

            Pattern pattern = Pattern.compile("(\\d+.\\d+),(\\d+.\\d+)");
            Matcher matcher = pattern.matcher(uriStr);
            if (matcher.find()) {
                String latitudeStr = matcher.group(1);
                latitude = Double.parseDouble(latitudeStr);

                String longitudeStr = matcher.group(2);
                longitude = Double.parseDouble(longitudeStr);

                displayPortalDetail = true;
            } else {
                Log.w(PortalLocationsActivity.class.getCanonicalName(), String.format("Unknown geo location: %s", uriStr));
            }
        }

        if (savedInstanceState == null) {
            if (displayPortalDetail) {
                Bundle args = new Bundle();
                args.putBoolean(PortalDetailFragment.KEY_ISNEW, true);
                args.putBoolean(PortalDetailFragment.KEY_GOTGEO, true);
                args.putDouble(PortalDetailFragment.KEY_LATITUDE, latitude);
                args.putDouble(PortalDetailFragment.KEY_LONGITUDE, longitude);
                replaceFragment(PortalDetailFragment.newInstance(args), true);
            } else {
                replaceFragment(PortalListFragment.newInstance(), false);
            }
        }
    }

    @Override
    public ClipboardManager onClipboardManagerRequested() {
        return (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public Observable<Boolean> onPortalLocationDelete(final PortalLocation portalLocation) {
        final PortalDatabase database = new PortalDatabase(getApplicationContext());

        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                Dao<PortalLocation, Long> dao = database.getPortalLocationsDao();
                try {
                    boolean succeeded = false;
                    if (portalLocation.getId() != null) {
                        succeeded = dao.deleteById(portalLocation.getId()) != 0;
                    }
                    subscriber.onNext(succeeded);
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<List<PortalLocation>> onDataRequested() {
        final PortalDatabase database = new PortalDatabase(getApplicationContext());

        return Observable.create(new Observable.OnSubscribe<List<PortalLocation>>() {
            @Override
            public void call(Subscriber<? super List<PortalLocation>> subscriber) {
                Dao<PortalLocation, Long> dao = database.getPortalLocationsDao();
                try {
                    List<PortalLocation> list = dao.queryForAll();
                    subscriber.onNext(list);
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public void onPortalDetail(final PortalLocation portalLocation) {
        final PortalDatabase database = new PortalDatabase(getApplicationContext());

        Observable<Dao.CreateOrUpdateStatus> observable = Observable.create(new Observable.OnSubscribe<Dao.CreateOrUpdateStatus>() {
            @Override
            public void call(Subscriber<? super Dao.CreateOrUpdateStatus> subscriber) {
                Dao<PortalLocation, Long> dao = database.getPortalLocationsDao();
                try {
                    Dao.CreateOrUpdateStatus status = dao.createOrUpdate(portalLocation);

                    subscriber.onNext(status);
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Dao.CreateOrUpdateStatus>() {
                    @Override
                    public void call(Dao.CreateOrUpdateStatus createOrUpdateStatus) {
                        View rootView = findViewById(R.id.rootView);
                        if (rootView != null) {
                            Snackbar.make(rootView, R.string.saved, Snackbar.LENGTH_SHORT).show();
                        }

                        replaceFragment(new PortalListFragment(), false);
                    }
                });

    }

    private void replaceFragment(Fragment fragment, boolean isFabHidden) {
        if (!(fragment instanceof WellKnownFragment)) {
            throw new RuntimeException("Only wellKnownFragments can be placed there.");
        }

        if (isFabHidden) {
            actionButton.hide();
        } else {
            actionButton.show();
        }

        currentFragment = fragment;

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment, ((WellKnownFragment) fragment).getName())
                .commit();
    }
}
