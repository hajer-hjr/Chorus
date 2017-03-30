package chorus.chorus.com.chorus;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.BaseAdapter;
import android.widget.Toast;

/**
 * Created by pc on 30/05/2016.
 */
public abstract class ApiTaskNoDialog <Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    protected ProgressDialog dialog;
    protected Context context;
    protected String error;
    BaseAdapter adapter;
    protected SJBException exception;

    ApiTaskNoDialog(Context _context) {
        context = _context;
    }
    ApiTaskNoDialog(Context _context, BaseAdapter _adapter) {
        context = _context;
        adapter = _adapter;

    }
    protected void onPreExecute() {
    }

    protected void onPostExecute(Result result) {
    }

    protected void showError() {
       if (error.equals("NO_CONNECTION_ERROR")) error = context.getResources().getString(R.string.error_no_connection);
        else if (error.equals("SERVICE_ERROR")) error = context.getResources().getString(R.string.error_service);
        else if (error.equals("API_ERROR")) error = context.getResources().getString(R.string.error_api);
        else if (error.equals("NOT_VALID_EMAIL_FORMAT")) error = context.getResources().getString(R.string.field_error_invalid_email_format);
        else if (error.equals("EMPTY_VALUE_EMAIL")) error = context.getResources().getString(R.string.field_error_empty_value_email);
       else if (error.equals("EMPTY_VALUE_EMAIL_TEL")) error = context.getResources().getString(R.string.field_error_empty_value_email_tel);
        else if (error.equals("EMPTY_VALUE_PASSWORD")) error = context.getResources().getString(R.string.field_error_empty_value_password);
        else if (error.equals("EMPTY_VALUE_USERNAME")) error = context.getResources().getString(R.string.field_error_empty_value_username);
        else if (error.equals("EMPTY_VALUE_NAME")) error = context.getResources().getString(R.string.field_error_empty_value_name);
        else if (error.equals("EMPTY_ANNONCE")) error = context.getResources().getString(R.string.field_error_empty_value_annonce);
        else if (error.equals("EMPTY_VALUE_SURNAME")) error = context.getResources().getString(R.string.field_error_empty_value_surname);
        else if (error.equals("NOT_UNIQUE_VALUE_EMAIL")) error = context.getResources().getString(R.string.field_error_not_unique_email);
        else if (error.equals("NOT_UNIQUE_VALUE_USERNAME")) error = context.getResources().getString(R.string.field_error_not_unique_username);
        else if (error.equals("EMPTY_VALUE_CONFIRM_PASSWORD")) error = context.getResources().getString(R.string.field_error_empty_value_confirm_password);
        else if (error.equals("NOT_VALID_CONFIRM_PASSWORD")) error = context.getResources().getString(R.string.field_error_empty_value_surname);
        else if (error.equals("NOT_VALID_VALUE_AGE")) error = context.getResources().getString(R.string.field_error_invalid_age);
        else if (error.equals("NOT_VALID_VALUE_OLD")) error = context.getResources().getString(R.string.field_error_invalid_age_old);
        else if (error.equals("NOT_VALID_VALUE_CGU")) error = context.getResources().getString(R.string.field_error_invalid_cgu);
        else if (error.equals("NOT_VALID_VALUE_CGU")) error = context.getResources().getString(R.string.field_error_invalid_cgu);
        else if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
        else if (error.equals("EMPTY_VALUE_PAYS")) error = context.getResources().getString(R.string.field_error_empty_value_pays);
       else if (error.equals("EMPTY_VALUE_TEL")) error = context.getResources().getString(R.string.field_error_empty_value_tel);
       else if (error.equals("EMPTY_VALUE_PAROISSE")) error = context.getResources().getString(R.string.field_error_empty_value_paroisse);
       else if (error.equals("EMPTY_VALUE_ORIGINE")) error = context.getResources().getString(R.string.field_error_empty_value_origine);
       else if (error.equals("NOT_UNIQUE_VALUE_TEL")) error = context.getResources().getString(R.string.field_error_not_unique_tel);
        else if (error.equals("FORBIDDEN_ATTENTE")) error = context.getResources().getString(R.string.error_forbidden_attente);
        else if (error.equals("FORBIDDEN_ACCOUNT")) error = context.getResources().getString(R.string.error_forbidden_account);

        else {
            error = "Unknown field error";
        }
        if (error.equals("FIELD_ERROR")) {
            String errorCode = exception.getErrorCode();
            String fieldName = exception.getFieldName();

            if (errorCode.equals("EMPTY_VALUE")) {
                if (fieldName.toLowerCase().equals("email")) error = context.getResources().getString(R.string.field_error_empty_value_email);
                else if (fieldName.toLowerCase().equals("user name")) error = context.getResources().getString(R.string.field_error_empty_value_username);
                else if (fieldName.toLowerCase().equals("password")) error = context.getResources().getString(R.string.field_error_empty_value_password);
                else if (fieldName.toLowerCase().equals("confirm_password")) error = context.getResources().getString(R.string.field_error_empty_value_confirm_password);
                else if (fieldName.toLowerCase().equals("name")) error = context.getResources().getString(R.string.field_error_empty_value_name);
                else if (fieldName.toLowerCase().equals("surname")) error = context.getResources().getString(R.string.field_error_empty_value_surname);
            } else if (errorCode.equals("NOT_VALID_EMAIL_FORMAT")) {
                error = context.getResources().getString(R.string.field_error_invalid_email_format);
            } else if (errorCode.equals("NOT_UNIQUE_VALUE")) {
                if (fieldName.toLowerCase().equals("email")) error = context.getResources().getString(R.string.field_error_not_unique_email);
                else if (fieldName.toLowerCase().equals("user name")) error = context.getResources().getString(R.string.field_error_not_unique_username);
            } else {
                error = "Unknown field error";
            }
        }

        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }
}
