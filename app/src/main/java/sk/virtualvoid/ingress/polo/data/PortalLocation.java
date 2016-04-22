package sk.virtualvoid.ingress.polo.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Locale;

/**
 * Created by Juraj on 4/21/2016.
 */
@DatabaseTable(tableName = "locations")
public class PortalLocation implements Parcelable {
    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String name;

    @DatabaseField
    private Double latitude;

    @DatabaseField
    private Double longitude;

    // from previous in list (info character)
    private Double distance;

    public PortalLocation() {
    }

    public PortalLocation(Parcel source) {
        Long id = source.readLong();
        this.id = id != -1 ? id : null;

        this.name = source.readString();
        this.latitude = source.readDouble();
        this.longitude = source.readDouble();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%f, %f", getLatitude(), getLongitude());
    }

    public static final Parcelable.Creator<PortalLocation> CREATOR = new Parcelable.Creator<PortalLocation>() {
        @Override
        public PortalLocation createFromParcel(Parcel source) {
            return new PortalLocation(source);
        }

        @Override
        public PortalLocation[] newArray(int size) {
            return new PortalLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id != null ? this.id : -1);
        dest.writeString(this.name);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }
}
