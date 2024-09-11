package com.bytmasoft;

public class Main {

    public static void main(String[] args) {
      String path =  "'/home/abakar/programming/git_repos/dss/storage/student/certificate/b163b837-21b0-4118-b2b5-072990b54efatasnim.png'";

         System.out.println("Path : " + path);
        System.out.println("Path2 : " + path.substring(0, path.lastIndexOf("/")));
    }
}
