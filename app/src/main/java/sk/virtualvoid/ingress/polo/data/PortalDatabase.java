package sk.virtualvoid.ingress.polo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Juraj on 4/21/2016.
 */
public class PortalDatabase extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "polo1.db";
    private static final Integer DATABASE_VERSION = 1;

    private Dao<PortalLocation, Long> portalLocationsDao = null;

    public PortalDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, PortalLocation.class);
        } catch (SQLException e) {
            Log.e(PortalDatabase.class.getCanonicalName(), String.format("onCreate: %s", e.getMessage()));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    public Dao<PortalLocation, Long> getPortalLocationsDao() {
        if (portalLocationsDao == null) {
            try {
                portalLocationsDao = DaoManager.createDao(getConnectionSource(), PortalLocation.class);
            } catch (SQLException e) {
                Log.e(PortalDatabase.class.getCanonicalName(), String.format("getPortalLocationsDao: %s", e.getMessage()));
            }
        }
        return portalLocationsDao;
    }
}

