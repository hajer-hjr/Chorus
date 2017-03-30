package chorus.chorus.com.chorus;

/**
 * Created by pc on 20/07/2016.
 */
public class OneMessage {
    public boolean left;
    public String comment;
    public String image_profil;
    public String date;
    public OneMessage(boolean left, String comment,String image,String _date) {
        super();
        this.left = left;
        this.comment = comment;
        this.image_profil=image;
        this.date=_date;
    }
}
