package sk.virtualvoid.ingress.polo.components;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import sk.virtualvoid.ingress.polo.R;
import sk.virtualvoid.ingress.polo.data.PortalLocation;

/**
 * Created by Juraj on 4/21/2016.
 */
public class PortalLocationAdapter extends RecyclerView.Adapter<PortalLocationAdapter.PortalLocationViewHolder> {

    private PortalLocationAdapterHandler handler;
    private List<PortalLocation> portalLocationList;

    public PortalLocationAdapter(PortalLocationAdapterHandler handler) {
        this.handler = handler;
        this.portalLocationList = new ArrayList<>();
    }

    public void addPortalLocation(PortalLocation portalLocation) {
        portalLocationList.add(portalLocation);
        notifyDataSetChanged();
    }

    public void addPortalLocations(Collection<PortalLocation> collection) {
        portalLocationList.addAll(collection);
        notifyDataSetChanged();
    }

    public void clearPortalLocations() {
        portalLocationList.clear();
        notifyDataSetChanged();
    }

    @Override
    public PortalLocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.portal_location_list_item, parent, false);
        return new PortalLocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PortalLocationViewHolder holder, final int position) {
        final PortalLocation portalLocation = portalLocationList.get(position);

        holder.name.setText(portalLocation.getName());
        holder.location.setText(portalLocation.toString());

        Double distance = null;
        if ( (distance = portalLocation.getDistance()) != null) {
            holder.distance.setText(String.format(Locale.getDefault(), "From previous: %.2f km", distance));
        } else {
            holder.distance.setText("");
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = handler.onClipboardManagerRequested();
                ClipData clipData = ClipData.newPlainText(portalLocation.getName(), portalLocation.toString());
                clipboardManager.setPrimaryClip(clipData);

                Snackbar.make(holder.cardView, R.string.copied, Snackbar.LENGTH_SHORT).show();
            }
        });

        holder.intelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.onLaunchIntel(portalLocation.toIntelString());
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.onPortalLocationDelete(portalLocation)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean succeeded) {
                                portalLocationList.remove(position);
                                notifyItemRemoved(position);
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return portalLocationList.size();
    }

    public static class PortalLocationViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView name;
        private TextView location;
        private TextView distance;
        private ImageButton intelButton;
        private ImageButton deleteButton;

        public PortalLocationViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.portal_location_cv);
            name = (TextView) itemView.findViewById(R.id.portal_name);
            location = (TextView) itemView.findViewById(R.id.portal_location);
            distance = (TextView) itemView.findViewById(R.id.portal_distance);
            intelButton = (ImageButton) itemView.findViewById(R.id.portal_location_intel);
            deleteButton = (ImageButton) itemView.findViewById(R.id.portal_location_delete);
        }
    }
}
