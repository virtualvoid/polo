package sk.virtualvoid.ingress.polo.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Juraj on 4/22/2016.
 */
public class OsHax {
    public static void hideKeyboard(Activity activity) {
        if (activity == null) {
            // fak yo couch
            throw new RuntimeException("Got null instead of activity instance.");
        }

        View focusedView = activity.getCurrentFocus();
        if (focusedView == null) {
            focusedView = new View(activity);
        }

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
    }
}
