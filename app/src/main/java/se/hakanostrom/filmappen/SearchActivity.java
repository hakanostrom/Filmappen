package se.hakanostrom.filmappen;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.hakanostrom.filmappen.api.OmdbClient;
import se.hakanostrom.filmappen.api.OmdbInterface;
import se.hakanostrom.filmappen.model.SearchResult;

public class SearchActivity extends AppCompatActivity {

    OmdbInterface omdbInterface;
    EditText etSokord;
    TextView tvSokresultat;
    ListView lvSearchResults;
    ArrayAdapter<SearchResult.SingleResult> aaSearchResults;


    String[] tutorials
            = {"Algorithms", "Data Structures",
            "Languages", "Interview Corner",
            "GATE", "ISRO CS", "xxx", "yyy", "zzz",
            "UGC NET CS", "CS Subjects",
            "Web Technologies"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrofit client
        omdbInterface = OmdbClient.getClient(SearchActivity.this).create(OmdbInterface.class);

        // Global views
        etSokord = findViewById(R.id.etSokord);
        tvSokresultat = findViewById(R.id.tvSokresultat);
        lvSearchResults = findViewById(R.id.lvSearchResults);

        // searchbox
        etSokord.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_SEARCH)) {
                doSearch(String.valueOf(etSokord.getText()));
            }
            return false;
        });
        findViewById(R.id.btnSok).setOnClickListener(v -> {
            doSearch(String.valueOf(etSokord.getText()));
        });

        // resultlist
        aaSearchResults = new ArrayAdapter<SearchResult.SingleResult>(SearchActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>());
        lvSearchResults.setAdapter(aaSearchResults);

        lvSearchResults.setOnItemClickListener((parent, view, position, id) -> {
            Log.d(SearchActivity.class.getCanonicalName(), String.format("Är du säker på att du vill spara %s som en favorit? Ange även ditt betyg (1-10)", aaSearchResults.getItem(position).title));

            Log.d(SearchActivity.class.getCanonicalName(), aaSearchResults.getItem(position).imdbID);

            // Alerta

            AlertDialog.Builder alert = new AlertDialog.Builder(SearchActivity.this);
            final EditText edittext = new EditText(SearchActivity.this);
            edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
            alert.setView(edittext);
            alert.setTitle(aaSearchResults.getItem(position).title);
            alert.setMessage("Vill du lägga till denna film som en favorit? Ange även ditt betyg (1-10)");

            alert.setPositiveButton("Ja", (dialog, whichButton) -> {
                String YouEditTextValue = edittext.getText().toString();

                SearchActivity.this.finish();
            });

            alert.setNegativeButton("Avbryt", (dialog, whichButton) -> {
                // what ever you want to do with No option.
            });
            
            alert.show();

        });
    }

    private void doSearch(String sokord) {

        if (sokord.isEmpty())
            Toast.makeText(this, "Ange sökord", Toast.LENGTH_SHORT).show();
        else {
            Log.d(SearchActivity.this.getLocalClassName(), "Sök nu på " + sokord);

            Call<SearchResult> call = omdbInterface.searchMovies(getResources().getString(R.string.api_key), sokord);
            call.enqueue(new Callback<SearchResult>() {
                @Override
                public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {

                    SearchResult searchResult = response.body();
                    if (searchResult.error != null) {
                        tvSokresultat.setText(searchResult.error);
                    } else {
                        tvSokresultat.setText(String.format("Hittade %s, visar %s första", searchResult.totalResults, searchResult.singleResultList.size()));
                        Log.d("TAG", response.code() + "");
                        Log.i(this.getClass().toString(), "totalResults: " + searchResult.totalResults);
                        Log.i(this.getClass().toString(), "antal singleresults: " + searchResult.singleResultList.size());
                        Log.i(this.getClass().toString(), "Första titeln: " + searchResult.singleResultList.get(0).title);

                        aaSearchResults.clear();
                        searchResult.singleResultList.forEach(singleResult -> {
                            aaSearchResults.add(singleResult);
                        });

                    }
                }

                @Override
                public void onFailure(Call<SearchResult> call, Throwable throwable) {
                    Log.d("TAG", "Nu blev det fel " + throwable.getMessage());
                    Toast.makeText(SearchActivity.this, "Kommunikationsfel", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}