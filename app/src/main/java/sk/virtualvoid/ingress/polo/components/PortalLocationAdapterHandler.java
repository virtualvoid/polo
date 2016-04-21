package sk.virtualvoid.ingress.polo.components;

import android.content.ClipboardManager;

import rx.Observable;
import sk.virtualvoid.ingress.polo.data.PortalLocation;

/**
 * Created by Juraj on 4/21/2016.
 */
public interface PortalLocationAdapterHandler {
    ClipboardManager onClipboardManagerRequested();

    Observable<Boolean> onPortalLocationDelete(PortalLocation portalLocation);
}
