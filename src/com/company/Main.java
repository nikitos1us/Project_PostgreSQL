package com.company;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Main {
    public static ArrayList<Student> Students = new ArrayList<Student>();

    public static void main(String[] args) {
        TestStudent(10);
    }

    public static void TestStudent(int kol) {
        Connection c;
        Statement stmt;
        Integer ch = 0;

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/iate", "postgres", "1111");
            c.setAutoCommit(false);
            System.out.println("-- Connected successfully --");


            //--------------- SELECT DATA ------------------
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT stid,discid,semnum,sum(ktmark) FROM studmark WHERE studmark.stid IN (SELECT stid FROM student WHERE student.gid IN (SELECT gid FROM sgroup WHERE sgroup.shname ILIKE 'МЕН%' AND sgroup.enty IN (2006,2007,2008,2009))) AND studmark.semnum IN (4,5,6) GROUP BY studmark.stid, studmark.discid, studmark.semnum ORDER BY studmark.stid ASC , studmark.semnum ASC;");

            while (rs.next()) {
                Student student = new Student();
                String id = rs.getString(1);
                String gid = rs.getString(2);
                String semnum = rs.getString(3);
                String mark = rs.getString(4);

                student.stid = id;
                student.discid = gid;
                student.semnum = semnum;
                student.mark = mark;

                ch += 1;
                Students.add(student);
            }

            rs.close();
            stmt.close();
            c.commit();
            c.close();

            FileWriter writer = new FileWriter("C:/Users/Nik1/IdeaProjects/Java/Project_PostgreSQL/file.txt", false);
            writer.write("Группы 'МЕН%', годы поступления = (2006,2007,2008,2009), семестры = (4,5,6)" + "\r\n");
            writer.write("Первые 10 элементов" + "\r\n");
            //--------------- PRINT 10 FIRST AND 10 LAST ------------------
            for (int i = 0; i < kol; i++) {
                String str = Students.get(i).stid + " " + Students.get(i).discid + " " +Students.get(i).semnum + " " + Students.get(i).mark;
                System.out.println( str );
                writer.write(str + "\r\n");

            }
            writer.write("Последние 10 элементов" + "\r\n");
            int size = Students.size();
            for (int i = kol; i > 0; i--) {
                String str1 = Students.get(size - i).stid + " " + Students.get(size - i).discid + " " +Students.get(size - i).semnum + " " + Students.get(size - i).mark;
                System.out.println(str1);
                writer.write(str1 + "\r\n");

            }
            writer.close();


            System.out.println("-- Operation SELECT done successfully --");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("-- All Operations done successfully --");
        System.out.println("Количество записей по запросу: " + ch);

    }

    //end_Main
}

class Student {
    String stid;
    String discid;
    String semnum;
    String mark;

}
