package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.List;

public class MyHttpClient {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String uri = "https://jsonplaceholder.typicode.com/users";
    private HttpResponse<String> response;

    private void methods(String uri ,String method, HttpRequest.BodyPublisher body) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(uri))
                .header("Content-Type", "application/json")
                .method(method, body)
                .timeout(Duration.ofSeconds(20))
                .build();

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("" + response.statusCode());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //Post methods
    public void post(String path) throws FileNotFoundException {
        post(Path.of(path));
    }
    public void post(Path path) throws FileNotFoundException {
        String method = "POST";
        methods(uri, method, HttpRequest.BodyPublishers.ofFile(path));
        }

    //Update methods
    public void update(Path newData, int id) throws FileNotFoundException {
        String method = "PUT";
        methods(uri + "/" + id, method, HttpRequest.BodyPublishers.ofFile(newData));
    }

    //Delete methods
    public void delete(int id) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(uri + "/" + id))
                .DELETE()
                .timeout(Duration.ofSeconds(20))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("" + response.statusCode());
        System.out.println(response.body());
    }

    //Get methods
    private void get(String uri) {
        String method = "GET";
        methods(uri, method, HttpRequest.BodyPublishers.noBody());
    }
    public void getAllUsers() {
        get(uri);
    }
    public void getUserWithID(int id) {
        get( uri + "/" + id);
    }
    public void getUserWithUsername(String username) {
        get(uri + "?username=" + username);
    }
    public void getUncompleted(int id) {
        get(uri + "/" + id + "/todos?completed=false");
    }
    public void getLastPostComments(int id) {
        getUsersPosts(id);

        int lastPostId = getLastPostId();
        String uri = "https://jsonplaceholder.typicode.com/posts/";

        get(uri + lastPostId + "/comments");

        writeInJson("src/main/resources/user-"+ id +"-post-" + lastPostId + "-comments.json");
    }

    private void getUsersPosts(int id) {
        get(uri + "/" + id + "/posts");

    }
    private List<UserPost> parseToObject() {
        Gson gson =new GsonBuilder().setPrettyPrinting().create();

        String tmp = response.body();

        Root root = gson.fromJson("{\"postList\": " + tmp + "}", Root.class);
        return root.getPostList();
    }
    private void writeInJson(String fileName) {
        try {
            Files.write(Path.of(fileName), response.body().getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private int getLastPostId() {
        List<UserPost> postList = parseToObject();

        int tmp = postList.getFirst().getId();
        for (UserPost userPost: postList) {
            tmp = Math.max(tmp, userPost.getId());
        }

        return tmp;
    }


}
