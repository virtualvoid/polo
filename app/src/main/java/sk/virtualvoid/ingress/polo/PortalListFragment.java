package sk.virtualvoid.ingress.polo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import sk.virtualvoid.ingress.polo.components.PortalLocationAdapter;
import sk.virtualvoid.ingress.polo.components.PortalLocationAdapterHandler;
import sk.virtualvoid.ingress.polo.data.PortalLocation;
import sk.virtualvoid.ingress.polo.utils.PortalListFragmentHandler;
import sk.virtualvoid.ingress.polo.utils.WellKnownFragment;

/**
 * Created by Juraj on 4/21/2016.
 */
public class PortalListFragment extends Fragment implements WellKnownFragment {
    public static final String NAME = "__polo_PortalList";

    private PortalListFragmentHandler handler;
    private PortalLocationAdapterHandler adapterHandler;

    private SwipeRefreshLayout swipeRefreshLayout;

    private PortalLocationAdapter adapter;

    public PortalListFragment() {

    }

    public static PortalListFragment newInstance() {
        Bundle args = new Bundle();

        PortalListFragment fragment = new PortalListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        handler = (PortalListFragmentHandler) getActivity();
        adapterHandler = (PortalLocationAdapterHandler) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new PortalLocationAdapter(adapterHandler);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.list_srl);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_rv);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        load();
    }

    private void load() {
        swipeRefreshLayout.setRefreshing(true);

        handler.onDataRequested()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<PortalLocation>>() {
                    @Override
                    public void call(List<PortalLocation> portalLocations) {
                        swipeRefreshLayout.setRefreshing(false);
                        adapter.addPortalLocations(portalLocations);
                    }
                });
    }
}
