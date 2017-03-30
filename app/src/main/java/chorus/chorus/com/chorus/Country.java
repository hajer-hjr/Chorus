package chorus.chorus.com.chorus;

import java.util.ArrayList;

/**
 * Created by pc on 19/10/2016.
 */
public class Country {
    public String id;
    public String caption;
    public String code;
    public String active;
    @Override
    public String toString() {
        return caption;
    }
    public String getId() {
        return id;
    }
}
