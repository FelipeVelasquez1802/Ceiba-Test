package co.com.ceiba.mobile.pruebadeingreso.rest;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import co.com.ceiba.mobile.pruebadeingreso.objects.Post;
import co.com.ceiba.mobile.pruebadeingreso.objects.User;
import co.com.ceiba.mobile.pruebadeingreso.view.PostActivity;

public class Endpoints {
    public static final String URL_BASE = "https://jsonplaceholder.typicode.com";
    public static final String GET_USERS = "/users";
    public static final String GET_POST_USER = "/posts?";
    public static final Gson gson = new Gson();

    private ResponseInterface responseInterface;

    public Endpoints() {
    }

    public Endpoints(ResponseInterface responseInterface) {
        this.responseInterface = responseInterface;
    }

    public interface ResponseInterface {
        void ResponseUsers(List<User> users);

        void ResponsePosts(List<Post> posts);
    }

    public List<User> toListUser(String string) {
        return gson.fromJson(string, new TypeToken<List<User>>() {
        }.getType());
    }

    public List<Post> toListPost(String string) {
        return gson.fromJson(string, new TypeToken<List<Post>>() {
        }.getType());
    }

    public User toUser(String string) {
        return gson.fromJson(string, User.class);
    }

    public StringRequest getUsers(final Context context) {
        String get_user = URL_BASE + GET_USERS;
        return new StringRequest(
                Request.Method.GET,
                get_user,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        responseInterface.ResponseUsers(toListUser(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error: " + error.networkResponse, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public StringRequest getPosts(final Context context, int userId) {
        String get_post = URL_BASE + GET_POST_USER + "userId=" + userId;
        return new StringRequest(
                Request.Method.GET,
                get_post,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        responseInterface.ResponsePosts(toListPost(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error: " + error.networkResponse, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
