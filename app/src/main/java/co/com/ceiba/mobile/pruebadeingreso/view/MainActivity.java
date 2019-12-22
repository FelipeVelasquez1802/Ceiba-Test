package co.com.ceiba.mobile.pruebadeingreso.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import co.com.ceiba.mobile.pruebadeingreso.R;
import co.com.ceiba.mobile.pruebadeingreso.adapters.UserAdapter;
import co.com.ceiba.mobile.pruebadeingreso.objects.Post;
import co.com.ceiba.mobile.pruebadeingreso.objects.User;
import co.com.ceiba.mobile.pruebadeingreso.rest.Endpoints;

public class MainActivity extends Activity implements Endpoints.ResponseInterface, TextWatcher, UserAdapter.OnItemClickListener {

    private SharedPreferences preferences;

    private RequestQueue requestQueue;
    private Endpoints endpoints;

    private View include;
    private ProgressBar progressBar;
    private EditText search;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<User> users;
    private List<User> users_all;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialObjects();
        initialViews();
    }

    private void initialObjects() {
        endpoints = new Endpoints(this);

        users = new ArrayList<>();
        users_all = new ArrayList<>();

        gson = new Gson();
    }

    private void initialViews() {
        progressBar = findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.VISIBLE);

        preferences = getSharedPreferences("test_ceiba", Context.MODE_PRIVATE);
        String usersString = preferences.getString("users", null);

        adapter = new UserAdapter(users, this);

        if (usersString == null) {
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(endpoints.getUsers(this));
        } else {
            updateList(endpoints.toListUser(usersString));
            progressBar.setVisibility(View.GONE);
        }

        include = findViewById(R.id.include);
        include.setVisibility(View.GONE);

        search = findViewById(R.id.editTextSearch);
        search.addTextChangedListener(this);

        recyclerView = findViewById(R.id.recyclerViewSearchResults);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void ResponseUsers(List<User> users) {
        progressBar.setVisibility(View.GONE);
        users_all.addAll(users);
        saveData(users);
        updateList(users);
    }

    private void saveData(List<User> users) {
        String usersString = gson.toJson(users);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("users", usersString);
        editor.apply();
    }

    @Override
    public void ResponsePosts(List<Post> posts) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        final String string = charSequence.toString();
        filterUser(string);
    }

    private void filterUser(String string) {
        List<User> users = new ArrayList<>();
        for (User user : users_all) {
            if (user.getName().toLowerCase().contains(string.toLowerCase())) {
                users.add(user);
            }
        }
        int count = users.size();
        include.setVisibility((count > 0) ? View.GONE : View.VISIBLE);
        updateList(users);
    }

    private void updateList(List<User> users) {
        this.users.clear();
        this.users.addAll(users);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onItemClick(User user) {
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra("user", gson.toJson(user));
        startActivity(intent);
    }
}