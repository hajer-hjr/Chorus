package chorus.chorus.com.chorus;

import android.text.TextUtils;

public class SJBLocation {
    public String Country;
    public String State;
    public String State_Code;

    public String getFormattedLocation() {
        String location = "";

        if (!TextUtils.isEmpty(State_Code)) {
            if (!location.isEmpty()) {
                location += ", ";
            }

            location += State_Code;
        }

        if (!TextUtils.isEmpty(Country)) {
            if (!location.isEmpty()) {
                location += ", ";
            }

            location += Country;
        }

        return location;
    }
}
