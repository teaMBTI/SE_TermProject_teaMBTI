package com.seproject.mbtimatchingsystem;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import junit.framework.TestResult;

import java.lang.reflect.Array;


public class ListCourseRoomUnitTest extends TestCase {

    private ListCourseRoom listCourseRoom;

    @Before
    public void setUp() {
        listCourseRoom = new ListCourseRoom();
    }

    @Test
    public void testStatus() {
        String status = listCourseRoom.getStatus("bere@google.com");
        assertEquals("Student", status);
    }

    @Test
    public void testEnterCourse(){
        String enterCourse = listCourseRoom.enterCourse("10177002");
        assertEquals("소프트웨어공학", enterCourse);
    }
    @Test
    public void testAddCourse(){
        String addCourseInfo = listCourseRoom.getAddCourseInfo("10177002", "소프트웨어공학", "20090000","정옥란");
        assertEquals("Yes", addCourseInfo);
    }

}