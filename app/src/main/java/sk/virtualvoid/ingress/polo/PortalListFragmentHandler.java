package sk.virtualvoid.ingress.polo;

import java.util.List;

import rx.Observable;
import sk.virtualvoid.ingress.polo.data.PortalLocation;

/**
 * Created by Juraj on 4/21/2016.
 */
public interface PortalListFragmentHandler {
    Observable<List<PortalLocation>> onDataRequested();
}
