package chorus.chorus.com.chorus;

/**
 * Created by pc on 29/03/2017.
 */

public class Verset {

    public String id;
    public String member_id;
    public String verset_text;
    public String date_add;
    public String id_liker;
    public String date_like;
    public String getIsLiked;
    public String state="1";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVerset_text() {
        return verset_text;
    }

    public void setVerset_text(String verset_text) {
        this.verset_text = verset_text;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getDate_add() {
        return date_add;
    }

    public void setDate_add(String date_add) {
        this.date_add = date_add;
    }

    public String getId_liker() {
        return id_liker;
    }

    public void setId_liker(String id_liker) {
        this.id_liker = id_liker;
    }

    public String getDate_like() {
        return date_like;
    }

    public void setDate_like(String date_like) {
        this.date_like = date_like;
    }

    private Boolean getIsLiked()
    {
        return true;
    }
}
