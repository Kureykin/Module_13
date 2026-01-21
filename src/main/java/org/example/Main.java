package org.example;


import java.nio.file.Path;

public class Main {
    static void main() {

        String testData = "src/main/resources/testPost.json";

       try {
           new MyHttpClient().getLastPostComments(2);
       } catch (Exception e) {
           System.out.println("Exception: \n" + e.getMessage());
       }
    }
}
