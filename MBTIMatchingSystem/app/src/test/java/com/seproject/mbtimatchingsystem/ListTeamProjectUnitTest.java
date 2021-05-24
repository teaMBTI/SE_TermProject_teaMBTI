package com.seproject.mbtimatchingsystem;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import junit.framework.TestResult;

import java.lang.reflect.Array;


public class ListTeamProjectUnitTest extends TestCase {

    private ListTeamProject listTeamProject;

    @Before
    public void setUp() {
        listTeamProject = new ListTeamProject();
    }

    @Test
    public void testTeamProject1() {
        String result = listTeamProject.isContained("Architectural design");
        assertEquals("Yes", result);
    }

    @Test
    public void testTeamProject2() {
        String result = listTeamProject.isContained("Individual Homework");
        assertEquals("No", result);
    }

}

