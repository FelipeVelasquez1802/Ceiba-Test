package co.com.ceiba.mobile.pruebadeingreso.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import co.com.ceiba.mobile.pruebadeingreso.R;
import co.com.ceiba.mobile.pruebadeingreso.adapters.PostAdapter;
import co.com.ceiba.mobile.pruebadeingreso.objects.Post;
import co.com.ceiba.mobile.pruebadeingreso.objects.User;
import co.com.ceiba.mobile.pruebadeingreso.rest.Endpoints;

public class PostActivity extends Activity implements Endpoints.ResponseInterface {

    private SharedPreferences preferences;

    private RequestQueue requestQueue;

    private User user;
    private Endpoints endpoints;
    private Gson gson;

    private View include;
    private ProgressBar progressBar;
    private TextView name;
    private TextView phone;
    private TextView email;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        initialObjects();
        initialViews();
    }

    private void initialObjects() {
        endpoints = new Endpoints(this);

        posts = new ArrayList<>();
        gson = new Gson();
    }

    private void initialViews() {
        user = endpoints.toUser(getIntent().getExtras().getString("user"));

        include = findViewById(R.id.include);
        progressBar = findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.VISIBLE);

        adapter = new PostAdapter(posts);

        preferences = getSharedPreferences("test_ceiba", Context.MODE_PRIVATE);
        String postString = preferences.getString("posts_" + user.getId(), null);

        if (postString == null) {
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(endpoints.getPosts(this, user.getId()));
        } else {
            updateList(endpoints.toListPost(postString));
            progressBar.setVisibility(View.GONE);
        }

        name = findViewById(R.id.name);
        name.setText(user.getName());

        phone = findViewById(R.id.phone);
        phone.setText(user.getPhone());

        email = findViewById(R.id.email);
        email.setText(user.getEmail());

        recyclerView = findViewById(R.id.recyclerViewPostsResults);
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
    }

    @Override
    public void ResponsePosts(List<Post> posts) {
        progressBar.setVisibility(View.GONE);
        int count = posts.size();
        include.setVisibility((count > 0) ? View.GONE : View.VISIBLE);
        saveData(posts);
        updateList(posts);
    }

    private void saveData(List<Post> posts) {
        String postsString = gson.toJson(posts);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("posts_" + user.getId(), postsString);
        editor.apply();
    }

    private void updateList(List<Post> posts) {
        this.posts.clear();
        this.posts.addAll(posts);
        adapter.notifyDataSetChanged();
    }
}
