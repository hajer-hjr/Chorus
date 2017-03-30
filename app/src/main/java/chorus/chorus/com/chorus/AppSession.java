package chorus.chorus.com.chorus;

import android.content.Context;
import android.support.annotation.NonNull;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AppSession {
    static String PHPSESSID;
    static Membre currentMember = null;
    static Integer currentInvitPage = 1;
    static Integer currentfavPage = 1;
    static Integer  currentContactPage=1;
    static User currentUser = null;
    static Integer currentPageMessage= 1;
    static Integer currentPage = 1;
    static ArrayList<NewsCategorie> newsCategories=new ArrayList<NewsCategorie>();
    static ArrayList<Country> countries = new ArrayList<Country>();
    static ArrayList<Membre> members = new ArrayList<Membre>();
    static Integer currentMembersNumber = 0;
    static Integer currentinvitationNumber = 0;
    static Integer currentfavorisNumber = 0;
    static Integer currentcontactsNumber = 0;
    static ArrayList<ContactObjet> contacts = new ArrayList<ContactObjet>();
    static ArrayList<FavorisObject> favoris = new ArrayList<FavorisObject>();
    static ArrayList<InvitObject> invitations = new ArrayList<InvitObject>();
    static SearchCriteria currentSearchCriteria = new SearchCriteria();

    static Integer currentUserMessagesNumber = 0;
    static ArrayList<Message> messages = new ArrayList<Message>();
    static ArrayList<Message> usermessages = new ArrayList<Message>();
    static Message currentMessage = null;
    static Integer currentMessagesNumber = 0;
    public static API getApi(Context context) {

        return new API(context);
    }
    public static Config getConfig(Context context) {

        return new Config(context);
    }
}
